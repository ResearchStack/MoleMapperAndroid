package org.researchstack.molemapper.ui.fragment;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.text.Html;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.google.android.gms.location.LocationRequest;
import com.jakewharton.rxbinding.view.RxView;

import org.researchstack.backbone.StorageAccess;
import org.researchstack.backbone.storage.file.StorageAccessListener;
import org.researchstack.backbone.ui.graph.LineChartCard;
import org.researchstack.backbone.ui.graph.ProgressChartCard;
import org.researchstack.backbone.utils.LogExt;
import org.researchstack.backbone.utils.ObservableUtils;
import org.researchstack.molemapper.BodyMapActivity;
import org.researchstack.molemapper.Database;
import org.researchstack.molemapper.R;
import org.researchstack.molemapper.models.Measurement;
import org.researchstack.molemapper.models.Mole;
import org.researchstack.molemapper.network.body.UvHourly;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import pl.charmas.android.reactivelocation.ReactiveLocationProvider;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;
import retrofit2.RxJavaCallAdapterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;
import rx.Scheduler;
import rx.Subscription;
import rx.schedulers.Schedulers;

/**
 * Created by bradleymcdermott on 2/16/16.
 */
public class MoleDashboardFragment extends Fragment implements StorageAccessListener
{
    private static final int    LOCATION_PERMISSION_REQUEST_CODE = 123;
    private static final String URL_EPA                          = "http://iaspub.epa.gov";

    private View              emptyView;
    private ProgressChartCard progressCard;
    private View              generalCard;
    private LineChartCard     lineCard;
    private View              permissionCard;
    private Subscription      locationSub;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_mole_dashboard, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        emptyView = view.findViewById(R.id.dashboard_empty);
        progressCard = (ProgressChartCard) view.findViewById(R.id.dashboard_chart_progress);
        generalCard = view.findViewById(R.id.dashboard_chart_general);
        lineCard = (LineChartCard) view.findViewById(R.id.dashboard_chart_line);
        permissionCard = view.findViewById(R.id.permission_card);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode == LOCATION_PERMISSION_REQUEST_CODE)
        {
            // this will keep the permission card if denied, show uv chart if allowed
            initUvChart();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * @param moles
     */
    private void initProgressChart(List<Mole> moles)
    {
        progressCard.setTitle(R.string.dashboard_progress_chart_title);
        progressCard.setFinishAction(o -> {
            startActivity(new Intent(getContext(), BodyMapActivity.class));
        });

        SimpleDateFormat format = new SimpleDateFormat("MMM '&apos;'yy");
        SparseArray<Set<Mole>> monthMoleSets = new SparseArray<>();
        Calendar today = Calendar.getInstance();

        // Iterate through all our measurements
        for(Mole mole : moles)
        {
            for(Measurement measurement : mole.measurements)
            {
                Calendar measurementTime = Calendar.getInstance();
                measurementTime.setTime(measurement.date);

                // the key is the difference in months from the current month, the value is a set
                // of unique moles measured during that month
                int key = getDiffInMonths(measurementTime, today);
                Set<Mole> moleSet = monthMoleSets.get(key, new HashSet<>());
                moleSet.add(mole);
                monthMoleSets.put(key, moleSet);
            }
        }

        List<PieData> items = new ArrayList<>();
        int[] colors = new int[] {
                ContextCompat.getColor(getContext(), R.color.mm_colorPrimary),
                ContextCompat.getColor(getContext(), R.color.mm_med_gray)
        };
        String[] labels = getResources().getStringArray(R.array.chart_progress_center_labels);
        Calendar month = Calendar.getInstance();

        // Start from 13 months from now
        for(int i = 13; i >= 0; i--)
        {
            // Create our data set, first entry is of mole completed in the month, second is of
            // moles incomplete
            List<Entry> entries = new ArrayList<>();
            Set<Mole> moleSet = monthMoleSets.get(i);
            int complete = moleSet == null ? 0 : moleSet.size();
            entries.add(new Entry(complete, 0));
            entries.add(new Entry(moles.size() - complete, 1));

            // Format the date
            String formatMonth = Html.fromHtml(format.format(month.getTime())).toString();
            PieDataSet data = new PieDataSet(entries, formatMonth);
            data.setDrawValues(false);
            data.setColors(colors);
            items.add(new PieData(labels, data));

            month.add(Calendar.MONTH, - 1);
        }
        progressCard.setData(items);
    }

    private void initGeneralStats()
    {
        RxView.clicks(generalCard.findViewById(R.id.chat_general_track)).subscribe(v -> {
            startActivity(new Intent(getContext(), BodyMapActivity.class));
        });

        NumberFormat numberFormat = NumberFormat.getNumberInstance();
        numberFormat.setMaximumFractionDigits(2);
        numberFormat.setMinimumFractionDigits(2);

        Database db = (Database) StorageAccess.getInstance().getAppDatabase();

        TextView statsSubtitle1 = (TextView) generalCard.findViewById(R.id.stats_subtitle1);
        TextView statsData1 = (TextView) generalCard.findViewById(R.id.stats_data1);
        db.getLargestMoleNameAndDiameter().compose(ObservableUtils.applyDefault()).subscribe(p -> {
            if(p != null)
            {
                statsSubtitle1.setText(p.first);
                statsData1.setText(numberFormat.format(p.second) + "mm");
            }
        });

        TextView statsData2 = (TextView) generalCard.findViewById(R.id.stats_data2);
        db.getAverageMoleDiameter().compose(ObservableUtils.applyDefault()).subscribe(average -> {
            if(average != null)
            {
                statsData2.setText(numberFormat.format(average) + "mm");
            }
        });
    }

    private void initUvChart()
    {
        int permissionCheck = ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION);

        if(permissionCheck == PackageManager.PERMISSION_DENIED)
        {
            LogExt.d(getClass(), "Permission Denied, showing allow button");
            lineCard.setVisibility(View.GONE);
            initPermissionCard();
            return;
        }

        LogExt.d(getClass(), "Permission Granted");
        permissionCard.setVisibility(View.GONE);
        lineCard.setTitle(R.string.dashboard_uv_chart_title);

        LocationRequest request = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setNumUpdates(1);

        // get location, convert to address, use zip code to get uv data from epa website
        ReactiveLocationProvider locationProvider = new ReactiveLocationProvider(getActivity());
        Scheduler newScheduler = Schedulers.newThread();
        locationProvider.getUpdatedLocation(request).observeOn(newScheduler).flatMap(location -> {
            LogExt.d(MoleDashboardFragment.class, "getReverseGeocodeObservable");
            return locationProvider.getReverseGeocodeObservable(location.getLatitude(),
                    location.getLongitude(),
                    1);
        })
                .flatMap(Observable:: from)
                .map(Address:: getPostalCode).flatMap(zip -> {
            LogExt.d(MoleDashboardFragment.class, "getHourlyUvData");
            return new Retrofit.Builder().addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create()).baseUrl(URL_EPA)
                    .build()
                    .create(UvService.class)
                    .getHourlyUvData(zip);
        })
                // back to observing on main thread
                .compose(ObservableUtils.applyDefault())
                .subscribe(uvHourlies -> {
                    LogExt.d(MoleDashboardFragment.class, "onNext");

                    if(uvHourlies == null || uvHourlies.isEmpty())
                    {
                        lineCard.setVisibility(View.GONE);
                        checkErrorView();
                        return;
                    }

                    lineCard.setVisibility(View.VISIBLE);
                    List<String> xValues = new ArrayList<>();
                    xValues.add("Time");
                    List<Entry> entries = new ArrayList<>();
                    int max = 0;
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM/dd/yyyy hh aa");
                    SimpleDateFormat displayTimeFormat = new SimpleDateFormat("ha");
                    Date start = new Date(System.currentTimeMillis() - (1000l * 60 * 60 * 2));
                    for(UvHourly uvHourly : uvHourlies)
                    {
                        Date date = null;
                        try
                        {
                            date = simpleDateFormat.parse(uvHourly.DATE_TIME);
                        }
                        catch(ParseException e)
                        {
                            throw new RuntimeException(e);
                        }

                        if(date.before(start))
                        {
                            continue;
                        }
                        int pos = xValues.size();
                        String formattedXLabel = displayTimeFormat.format(date);
                        // it might be better to do this with DatFormatSymbols
                        // There's an issue with the chart library where "p" will get bumped down, use caps
                        formattedXLabel = formattedXLabel.replace("AM", "A").replace("PM", "P");

                        xValues.add(formattedXLabel);
                        entries.add(new Entry(uvHourly.UV_VALUE, pos));
                        max = uvHourly.UV_VALUE > max ? uvHourly.UV_VALUE : max;
                    }

                    int color = ContextCompat.getColor(getContext(), R.color.mm_colorPrimary);
                    LineDataSet set = new LineDataSet(entries, "");
                    set.setCircleColor(color);
                    set.setCircleRadius(4f);
                    set.setDrawCircleHole(false);
                    set.setColor(color);
                    set.setLineWidth(2f);
                    set.setDrawValues(false);

                    lineCard.setData(new LineData(xValues, set), 0, 7);

                    LineChart chart = lineCard.getChart();
                    YAxis yAxis = chart.getAxisLeft();
                    yAxis.setAxisMinValue(0f);
                    yAxis.setAxisMaxValue(max);
                    chart.notifyDataSetChanged();
                }, error -> {
                    LogExt.e(MoleDashboardFragment.class, "onError", error);
                    lineCard.setVisibility(View.GONE);
                    checkErrorView();
                });
    }

    private void initPermissionCard()
    {
        permissionCard.setVisibility(View.VISIBLE);

        ImageView icon = (ImageView) permissionCard.findViewById(R.id.permission_icon);
        Drawable drawable = ContextCompat.getDrawable(getActivity(),
                R.drawable.ic_place_black_24dp);
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable,
                ContextCompat.getColor(getActivity(), R.color.mm_colorPrimary));
        icon.setImageDrawable(drawable);

        AppCompatTextView title = (AppCompatTextView) permissionCard.findViewById(R.id.permission_title);
        title.setText(R.string.uv_levels);
        title.setTextColor(ContextCompat.getColor(getActivity(), R.color.mm_colorPrimary));

        AppCompatTextView details = (AppCompatTextView) permissionCard.findViewById(R.id.permission_details);
        details.setText(R.string.uv_permission_details);


        AppCompatButton permissionButton = (AppCompatButton) permissionCard.findViewById(R.id.permission_button);
        permissionButton.setEnabled(true);
        permissionButton.setTextColor(ContextCompat.getColor(getActivity(),
                R.color.mm_colorPrimary));

        RxView.clicks(permissionButton).subscribe(view -> {
            requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
            permissionButton.setEnabled(false);
        });
    }

    private void checkErrorView()
    {
        if(lineCard.getVisibility() == View.GONE && progressCard.getVisibility() == View.GONE &&
                generalCard.getVisibility() == View.GONE &&
                permissionCard.getVisibility() == View.GONE)
        {
            emptyView.setVisibility(View.VISIBLE);
        }
        else
        {
            emptyView.setVisibility(View.GONE);
        }
    }

    private int getDiffInMonths(Calendar start, Calendar end)
    {
        int diffYear = end.get(Calendar.YEAR) - start.get(Calendar.YEAR);
        return diffYear * 12 + end.get(Calendar.MONTH) - start.get(Calendar.MONTH);
    }

    @Override
    public void onDataReady()
    {
        Observable.defer(() -> Observable.just(((Database) StorageAccess.getInstance()
                .getAppDatabase()).loadMoles()))
                .compose(ObservableUtils.applyDefault())
                .subscribe(moles -> {
                    if(moles == null || moles.size() == 0)
                    {
                        generalCard.setVisibility(View.GONE);
                        progressCard.setVisibility(View.GONE);
                    }
                    else
                    {
                        initProgressChart(new ArrayList<>(moles));
                    }

                    initGeneralStats();
                    initUvChart();
                });
    }

    @Override
    public void onDataFailed()
    {
        // Ignore
    }

    @Override
    public void onDataAuth()
    {
        // Ignore
    }

    public interface UvService
    {

        @GET("enviro/efservice/getEnvirofactsUVHOURLY/ZIP/{zip}/JSON")
        Observable<List<UvHourly>> getHourlyUvData(@Path("zip") String zip);
    }
}
