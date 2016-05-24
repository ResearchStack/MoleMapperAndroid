package org.researchstack.molemapper;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.davemorrissey.labs.subscaleview.ImageSource;

import org.researchstack.backbone.StorageAccess;
import org.researchstack.backbone.ui.PinCodeActivity;
import org.researchstack.backbone.ui.views.IconTabLayout;
import org.researchstack.backbone.utils.ObservableUtils;
import org.researchstack.molemapper.models.Measurement;
import org.researchstack.molemapper.models.Mole;
import org.researchstack.molemapper.ui.StatusBarUtils;
import org.researchstack.molemapper.ui.view.MeasurementView;

import java.text.SimpleDateFormat;
import java.util.Collections;

import rx.Observable;

public class MoleHistoryActivity extends PinCodeActivity
{
    public static final String TAG      = MoleHistoryActivity.class.getSimpleName();
    public static final String KEY_MOLE = TAG + ".MOLE";

    private TabLayout       tabLayout;
    private MeasurementView measurementView;
    private View            progress;

    private SparseArray<Measurement> data;

    public static Intent newIntent(Context context, Mole mole)
    {
        Intent intent = new Intent(context, MoleHistoryActivity.class);
        intent.putExtra(KEY_MOLE, mole);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_mole_history);

        Mole mole = (Mole) getIntent().getSerializableExtra(KEY_MOLE);

        //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
        // init Toolbar
        //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.rsb_black_40));
        toolbar.setNavigationIcon(R.drawable.abc_ic_clear_mtrl_alpha);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(getString(R.string.mole_history, mole.moleName));

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
        {
            StatusBarUtils.adjustForTransparentStatusBar(toolbar);
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            toolbar.setOutlineProvider(null);
        }

        //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
        // init measurementView
        //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
        measurementView = (MeasurementView) findViewById(R.id.mole_history_image);

        //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
        // init tablayout
        //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*

        tabLayout = (TabLayout) findViewById(R.id.mole_history_tabs);
        SimpleDateFormat format = new SimpleDateFormat("MMM d, yyyy");

        // Sort through each measurement by date
        Collections.sort(mole.measurements, (m1, m2) -> m1.date.compareTo(m2.date));

        // Add tabs and store measurements in sparse array for future reference
        data = new SparseArray<>();

        for(int i = 0; i < mole.measurements.size(); i++)
        {
            Measurement measurement = mole.measurements.get(i);

            // Store in sparse array w/ original index pos, in case user deletes one
            data.put(i, measurement);

            // Create the tab w/ formatted text
            TabLayout.Tab tab = tabLayout.newTab();
            tab.setTag(i);
            tab.setText(Html.fromHtml(format.format(measurement.date)));
            tabLayout.addTab(tab, false);
        }

        progress = findViewById(R.id.progress);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_measure_history, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case android.R.id.home:
                finish();
                return true;
            case R.id.menu_delete_measurement:
                int pos = tabLayout.getSelectedTabPosition();
                Measurement measurement = data.get(pos);

                ConfirmationDialog dialog = new ConfirmationDialog(measurement);
                dialog.setListener(() -> {
                    data.remove(pos);
                    tabLayout.removeTabAt(pos);
                });
                dialog.show(getSupportFragmentManager(), "dialog");

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDataReady()
    {
        super.onDataReady();

        showProgress(false);

        if(tabLayout.getTabCount() > 0)
        {
            tabLayout.setOnTabSelectedListener(new IconTabLayout.OnTabSelectedListenerAdapter()
            {
                @Override
                public void onTabSelected(TabLayout.Tab tab)
                {
                    int index = (int) tab.getTag();
                    Measurement measurement = data.get(index);

                    showProgress(true);

                    // Load mole image
                    Observable.fromCallable(() -> StorageAccess.getInstance()
                            .getFileAccess()
                            .readData(MoleHistoryActivity.this, measurement.measurementPhoto))
                            .map(bytes -> BitmapFactory.decodeByteArray(bytes, 0, bytes.length))
                            .compose(ObservableUtils.applyDefault())
                            .subscribe(bitmap -> {
                                measurementView.setImage(ImageSource.bitmap(bitmap));
                                measurementView.setMeasurement(measurement);

                                showProgress(false);
                            });
                }
            });

            if(tabLayout.getSelectedTabPosition() == - 1)
            {
                tabLayout.post(() -> {
                    int lastIndex = tabLayout.getTabCount() - 1;
                    int right = ((ViewGroup) tabLayout.getChildAt(0)).getChildAt(lastIndex)
                            .getRight();
                    tabLayout.scrollTo(right, 0);
                    tabLayout.getTabAt(lastIndex).select();
                });
            }
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        tabLayout.setOnTabSelectedListener(null);
    }

    private void showProgress(boolean show)
    {
        progress.animate().setDuration(100).alpha(show ? 1 : 0).withStartAction(() -> {
            if(show)
            {
                progress.setVisibility(View.VISIBLE);
            }
        }).withEndAction(() -> {
            if(! show)
            {
                progress.setVisibility(View.GONE);
            }
        });
    }

    @SuppressLint("ValidFragment")
    public static class ConfirmationDialog extends DialogFragment
    {
        private final Measurement          measurement;
        private       onMeasurementDeleted listener;

        public ConfirmationDialog(Measurement measurement)
        {
            super();
            this.measurement = measurement;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState)
        {
            return new AlertDialog.Builder(getActivity()).setTitle(R.string.delete_measurement)
                    .setMessage(R.string.delete_measurement_text)
                    .setPositiveButton(R.string.rsb_yes, (dialog, which) -> {

                        Observable.fromCallable(() -> {
                            Database db = (Database) StorageAccess.getInstance().getAppDatabase();
                            db.deleteMeasurement(getContext(), measurement);
                            return null;
                        }).compose(ObservableUtils.applyDefault()).subscribe(o -> {
                            if(listener != null)
                            {
                                listener.onMeasurementDeleted();
                            }
                        });
                    })
                    .setNegativeButton(android.R.string.cancel, (dialog, which) -> {
                        // Ignore
                    })
                    .create();
        }

        public void setListener(onMeasurementDeleted listener)
        {
            this.listener = listener;
        }

        public interface onMeasurementDeleted
        {
            public void onMeasurementDeleted();
        }
    }

}
