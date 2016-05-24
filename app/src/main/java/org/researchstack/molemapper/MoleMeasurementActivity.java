package org.researchstack.molemapper;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.jakewharton.rxbinding.view.RxView;

import org.researchstack.backbone.StorageAccess;
import org.researchstack.backbone.ui.PinCodeActivity;
import org.researchstack.backbone.utils.LogExt;
import org.researchstack.backbone.utils.ObservableUtils;
import org.researchstack.molemapper.models.Measurement;
import org.researchstack.molemapper.ui.StatusBarUtils;
import org.researchstack.molemapper.ui.view.MoleView;

import java.text.NumberFormat;
import java.util.Date;

import rx.Observable;
import rx.functions.Action1;

public class MoleMeasurementActivity extends PinCodeActivity
{
    public static final int    REQUEST_CODE           = 9014;
    public static final String TAG                    = MoleMeasurementActivity.class.getSimpleName();
    public static final String KEY_MOLE_ID            = TAG + ".MOLE_ID";
    public static final String KEY_MOLE_PHOTO         = TAG + ".MOLE_PHOTO";
    public static final String KEY_MEASUREMENT        = TAG + ".MEASUREMENT";
    public static final String KEY_REFERENCE          = TAG + ".REFERENCE";
    public static final String KEY_RESULT_MEASUREMENT = TAG + ".RESULT_MEASUREMENT";
    public static final String KEY_RESULT_TEMP_PHOTO  = TAG + ".RESULT_TEMP_PHOTO";

    private static final int CONTROL_MOVE_X = 0;
    private static final int CONTROL_MOVE_Y = 1;
    private static final int CONTROL_SCALE  = 2;

    private volatile Measurement      measurement;
    private          MoleView         moleView;
    private          AppCompatSpinner referenceSpinner;
    private          String           moleLabel;
    private          String           tempMolePhoto;

    private NumberFormat moleDiameterFormat;
    private int          moleId;

    public static Intent newIntent(Context context, int moleId, String molePhoto)
    {
        Intent intent = new Intent(context, MoleMeasurementActivity.class);
        intent.putExtra(KEY_MOLE_ID, moleId);
        intent.putExtra(KEY_MOLE_PHOTO, molePhoto);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mole_measurement);

        //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
        // Init new measurement object
        //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
        if(savedInstanceState == null || ! savedInstanceState.containsKey(KEY_MEASUREMENT))
        {
            measurement = new Measurement();
            measurement.date = new Date();
        }
        else
        {
            measurement = (Measurement) savedInstanceState.getSerializable(KEY_MEASUREMENT);
        }

        moleDiameterFormat = NumberFormat.getInstance();
        moleDiameterFormat.setMinimumFractionDigits(2);
        moleDiameterFormat.setMaximumFractionDigits(2);

        moleId = getIntent().getIntExtra(KEY_MOLE_ID, 0);
        moleLabel = getString(R.string.mole);
        tempMolePhoto = getIntent().getStringExtra(KEY_MOLE_PHOTO);

        //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
        // Init Toolbar
        //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.rsb_black_40));
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            toolbar.setOutlineProvider(null);
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
        {
            StatusBarUtils.adjustForTransparentStatusBar(toolbar);
        }

        //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
        // Setup mole view
        //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
        moleView = (MoleView) findViewById(R.id.mole_measure_image);

        RxView.clicks(findViewById(R.id.mole_measure_finish)).subscribe(v -> {

            // We can ignore setting referenceObject and absoluteReferenceDiameter as that
            // is set within the Spinner.OnItemSelectedListener.onItemSelected

            measurement.referenceDiameter = moleView.getReferenceDiameter();
            measurement.referenceX = moleView.getReferenceX();
            measurement.referenceY = moleView.getReferenceY();

            measurement.measurementDiameter = moleView.getMeasurementDiameter();
            measurement.measurementX = moleView.getMeasurementX();
            measurement.measurementY = moleView.getMeasurementY();

            measurement.absoluteMoleDiameter = getMoleDiameter();

            Intent measurementIntent = new Intent();
            measurementIntent.putExtra(KEY_RESULT_MEASUREMENT, measurement);
            measurementIntent.putExtra(KEY_RESULT_TEMP_PHOTO, tempMolePhoto);
            setResult(RESULT_OK, measurementIntent);
            finish();
        });

        //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
        // Measurement Controls
        //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*

        float changeAmount = (getResources().getDisplayMetrics().density * 1) / 2;

        ImageView moveLeft = (ImageView) findViewById(R.id.mole_measure_move_left);
        moveLeft.setTag(- changeAmount);
        initControlView(moveLeft, CONTROL_MOVE_X);

        ImageView moveUp = (ImageView) findViewById(R.id.mole_measure_move_up);
        moveUp.setTag(- changeAmount);
        initControlView(moveUp, CONTROL_MOVE_Y);

        ImageView moveRight = (ImageView) findViewById(R.id.mole_measure_move_right);
        moveRight.setTag(changeAmount);
        initControlView(moveRight, CONTROL_MOVE_X);

        ImageView moveDown = (ImageView) findViewById(R.id.mole_measure_move_down);
        moveDown.setTag(changeAmount);
        initControlView(moveDown, CONTROL_MOVE_Y);

        ImageView scaleUp = (ImageView) findViewById(R.id.mole_measure_scale_up);
        scaleUp.setTag(changeAmount);
        initControlView(scaleUp, CONTROL_SCALE);

        ImageView scaleDown = (ImageView) findViewById(R.id.mole_measure_scale_down);
        scaleDown.setTag(- changeAmount);
        initControlView(scaleDown, CONTROL_SCALE);

        //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
        // Set color tint
        //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*

        View controlsContainer = findViewById(R.id.controls_container);

        moleView.setMeasurementFocusListener(focusedItem -> {

            //Start animation w/ a simple fade animation
            int startColor = ContextCompat.getColor(this,
                    focusedItem == MoleView.REFERENCE
                            ? R.color.mm_color_measure_mole
                            : R.color.mm_color_measure_reference);
            int endColor = ContextCompat.getColor(this,
                    focusedItem == MoleView.REFERENCE
                            ? R.color.mm_color_measure_reference
                            : R.color.mm_color_measure_mole);

            animateBackgroundTint(controlsContainer, startColor, endColor);
            animateImageViewTint(moveLeft, startColor, endColor);
            animateImageViewTint(moveUp, startColor, endColor);
            animateImageViewTint(moveRight, startColor, endColor);
            animateImageViewTint(moveDown, startColor, endColor);
            animateImageViewTint(scaleUp, startColor, endColor);
            animateImageViewTint(scaleDown, startColor, endColor);
        });

        moleView.post(() -> {
            int selectedColor = ContextCompat.getColor(this,
                    moleView.getCurrentSelected() == MoleView.MOLE
                            ? R.color.mm_color_measure_mole
                            : R.color.mm_color_measure_reference);

            controlsContainer.setBackgroundColor(selectedColor);
            moveLeft.setColorFilter(selectedColor);
            moveUp.setColorFilter(selectedColor);
            moveRight.setColorFilter(selectedColor);
            moveDown.setColorFilter(selectedColor);
            scaleUp.setColorFilter(selectedColor);
            scaleDown.setColorFilter(selectedColor);
        });

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            setElevationLollipop(moveLeft, moveUp, moveRight, moveDown, scaleUp, scaleDown);
        }

        //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
        // Reference Option
        //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
        String[] names = getResources().getStringArray(R.array.mole_reference_names);
        int[] values = getResources().getIntArray(R.array.mole_reference_value);

        ReferenceAdapter adapter = new ReferenceAdapter(this, names);

        referenceSpinner = (AppCompatSpinner) findViewById(R.id.mole_measure_reference);
        referenceSpinner.setAdapter(adapter);
        referenceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                LogExt.i(AppCompatSpinner.class, "onItemSelected: " + position);
                // There is no float-array in RES so we must use int values. Divide by 100 to ge the
                // proper millimeter value

                String name = (String) parent.getAdapter().getItem(position);
                measurement.referenceObject = name;
                measurement.absoluteReferenceDiameter = values[position] / 100f;
                moleView.setReferenceLabel(name);
                moleView.setMoleLabel(moleLabel + " - " + getFormattedMoleDiameter());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
                LogExt.i(AppCompatSpinner.class, "onNothingSelected");
            }
        });

        referenceSpinner.setSelection(
                savedInstanceState == null || ! savedInstanceState.containsKey(KEY_REFERENCE)
                        ? 0
                        : savedInstanceState.getInt(KEY_REFERENCE));
    }

    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    // Fancy API 21 stuff setting elevation
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    @TargetApi(21)
    private void setElevationLollipop(ImageView moveLeft, ImageView moveUp, ImageView moveRight, ImageView moveDown, ImageView scaleUp, ImageView scaleDown)
    {
        int elevation = (int) (getResources().getDisplayMetrics().density * 2 + .5f);

        // Set elevation and shadow outlines for movement views (rectangular d-pad)
        ViewOutlineProvider roundRecOutline = new ViewOutlineProvider()
        {
            @Override
            public void getOutline(View view, Outline outline)
            {
                int radius = (int) (view.getResources().getDisplayMetrics().density * 2 + .5f);
                outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), radius);
            }
        };
        setElevation(elevation, roundRecOutline, moveLeft, moveUp, moveRight, moveDown);

        // Set elevation and shadow outlines for circular views (scaling viws)
        ViewOutlineProvider ovalOutline = new ViewOutlineProvider()
        {
            @Override
            public void getOutline(View view, Outline outline)
            {
                outline.setOval(0, 0, view.getWidth(), view.getHeight());
            }
        };
        setElevation(elevation, ovalOutline, scaleUp, scaleDown);
    }

    @Override
    public void onDataReady()
    {
        super.onDataReady();

        //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
        // Load mole image
        //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
        Observable.fromCallable(() -> StorageAccess.getInstance()
                .getFileAccess()
                .readData(this, tempMolePhoto))
                .map(bytes -> BitmapFactory.decodeByteArray(bytes, 0, bytes.length))
                .compose(ObservableUtils.applyDefault())
                .subscribe(bitmap -> {
                    moleView.setImage(ImageSource.bitmap(bitmap));
                });

        //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
        // Load mole and set to measurement object iff needed
        //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
        if(measurement.mole == null || TextUtils.isEmpty(getSupportActionBar().getTitle()))
        {
            Observable.fromCallable(() -> ((Database) StorageAccess.getInstance()
                    .getAppDatabase()).loadMole(moleId))
                    .compose(ObservableUtils.applyDefault())
                    .subscribe(mole -> {
                        measurement.mole = mole;
                        getSupportActionBar().setTitle(mole.moleName);
                    });
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putSerializable(KEY_MEASUREMENT, measurement);
        outState.putInt(KEY_REFERENCE, referenceSpinner.getSelectedItemPosition());
    }

    private String getFormattedMoleDiameter()
    {
        return moleDiameterFormat.format(getMoleDiameter()) + "mm";
    }

    private float getMoleDiameter()
    {
        return measurement.absoluteReferenceDiameter * moleView.getMeasurementDiameter() /
                moleView.getReferenceDiameter();
    }

    private void initControlView(View view, int type)
    {
        Action1 action = new Action1<View>()
        {
            @Override
            public void call(View v)
            {
                float changeAmount = (float) v.getTag();
                if(type == CONTROL_MOVE_X)
                {
                    moleView.changeXPosition(changeAmount);
                }
                else if(type == CONTROL_MOVE_Y)
                {
                    moleView.changeYPosition(changeAmount);
                }
                else
                {
                    moleView.changeRadius(changeAmount);
                    moleView.setMoleLabel(moleLabel + " - " + getFormattedMoleDiameter());
                }
            }
        };

        view.setOnClickListener(v -> action.call(v));
        view.setOnLongClickListener(v -> {
            action.call(v);
            return false;
        });
    }

    private void animateBackgroundTint(View view, int startColor, int endColor)
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            ObjectAnimator colorAnimator = ObjectAnimator.ofObject(view.getBackground().mutate(),
                    "tint",
                    new ArgbEvaluator(),
                    startColor,
                    endColor);
            colorAnimator.setDuration(150);
            colorAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
            colorAnimator.start();
        }
        else
        {
            ValueAnimator colorAnimator = ValueAnimator.ofObject(new ArgbEvaluator(),
                    startColor,
                    endColor);
            colorAnimator.addUpdateListener(animator -> view.setBackgroundColor((int) animator.getAnimatedValue()));
            colorAnimator.setDuration(150);
            colorAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
            colorAnimator.start();
        }
    }

    private void animateImageViewTint(ImageView view, int startColor, int endColor)
    {
        ValueAnimator colorAnimator = ValueAnimator.ofObject(new ArgbEvaluator(),
                startColor,
                endColor);
        colorAnimator.addUpdateListener(animation -> view.setColorFilter((int) animation.getAnimatedValue()));
        colorAnimator.setDuration(150);
        colorAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        colorAnimator.start();
    }

    @TargetApi(21)
    private void setElevation(int elevation, ViewOutlineProvider outlineProvider, View... views)
    {
        for(View v : views)
        {
            v.setElevation(elevation);
            v.setOutlineProvider(outlineProvider);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(item.getItemId() == android.R.id.home)
        {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed()
    {
        Intent cancelIntent = new Intent();
        cancelIntent.putExtra(KEY_RESULT_TEMP_PHOTO, tempMolePhoto);
        setResult(RESULT_CANCELED, cancelIntent);
        super.onBackPressed();
    }

    static class ReferenceAdapter extends ArrayAdapter<String>
    {
        private LayoutInflater inflater;

        public ReferenceAdapter(Context context, String[] names)
        {
            super(context, 0, names);
            inflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            TextView tv = (TextView) (convertView == null
                    ? inflater.inflate(R.layout.item_spinner_view, parent, false)
                    : convertView);

            if(convertView == null)
            {
                Drawable drawable = ContextCompat.getDrawable(getContext(),
                        R.drawable.ic_spinner_down_16dp);
                drawable = DrawableCompat.wrap(drawable);
                DrawableCompat.setTint(drawable, Color.WHITE);
                tv.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
            }

            tv.setText(getItem(position));
            return tv;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent)
        {
            TextView tv = (TextView) (convertView == null
                    ? inflater.inflate(R.layout.item_spinner_drop_down, parent, false)
                    : convertView);
            tv.setText(getItem(position));
            return tv;
        }
    }

}
