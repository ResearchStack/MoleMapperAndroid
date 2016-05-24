package org.researchstack.molemapper;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.researchstack.backbone.StorageAccess;
import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.step.InstructionStep;
import org.researchstack.backbone.task.OrderedTask;
import org.researchstack.backbone.ui.ViewTaskActivity;
import org.researchstack.backbone.utils.LogExt;
import org.researchstack.backbone.utils.ObservableUtils;
import org.researchstack.molemapper.models.Measurement;
import org.researchstack.molemapper.models.Mole;
import org.researchstack.molemapper.models.Zone;
import org.researchstack.molemapper.ui.MoleEditDialog;
import org.researchstack.molemapper.ui.MoleGrowthDialog;
import org.researchstack.molemapper.ui.view.BodyZoneHelper;
import org.researchstack.molemapper.ui.view.ZoneView;
import org.researchstack.skin.DataProvider;
import org.researchstack.skin.ui.BaseActivity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import rx.Observable;
import rx.exceptions.OnErrorNotImplementedException;

public class BodyZoneActivity extends BaseActivity
{
    public static final  String TAG                  = BodyMapActivity.class.getSimpleName();
    public static final  String KEY_BODY_ZONE        = TAG + ".TITLE";
    private static final int    REQUEST_MOLE_REMOVAL = 0;

    private Toolbar              toolbar;
    private Zone                 zone;
    private ZoneView             zoneImage;
    private FloatingActionButton captureZone;
    private FloatingActionButton addMole;
    private TextView             empty;

    private LinearLayout addMoleContainer;
    private TextView     addMoleTitle;
    private TextView     addMoleAction;
    private View         addMoleCancel;

    private int bodyZone;

    public static Intent newIntent(Context context, int zone)
    {
        Intent intent = new Intent(context, BodyZoneActivity.class);
        intent.putExtra(KEY_BODY_ZONE, zone);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_body_zone);

        bodyZone = getIntent().getIntExtra(KEY_BODY_ZONE, 0);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(BodyZoneHelper.getTitle(this, bodyZone) + " " +
                BodyZoneHelper.getSectionString(this, bodyZone));

        zoneImage = (ZoneView) findViewById(R.id.zone_image);
        zoneImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
        zoneImage.setOnMoleClickListener(new ZoneView.MoleClickListener()
        {
            @Override
            public void onMoleClick(Mole mole)
            {
                showMoleDialog(mole);
            }

            @Override
            public void onMoleLongClick(Mole mole)
            {
                // If we long click, that means a mole has moved and new position needs to be saved
                Observable.create(subscriber -> {
                    ((Database) StorageAccess.getInstance().getAppDatabase()).saveMole(mole);
                }).compose(ObservableUtils.applyDefault()).subscribe();
            }
        });

        captureZone = (FloatingActionButton) findViewById(R.id.fab_action_capture_zone);
        captureZone.setOnClickListener(v -> {
            Intent intent = PhotoCaptureActivity.newIntent(this,
                    PhotoCaptureActivity.ZONE,
                    bodyZone);
            startActivityForResult(intent, PhotoCaptureActivity.REQUEST_CODE_ZONE);
        });

        addMole = (FloatingActionButton) findViewById(R.id.fab_action_add_mole);
        addMole.setOnClickListener(v -> {
            startMoleCreateState();
        });

        empty = (TextView) findViewById(R.id.empty);

        //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
        // Add Mole Views
        //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*

        addMoleContainer = (LinearLayout) findViewById(R.id.zone_add_mole_container);
        addMoleTitle = (TextView) findViewById(R.id.zone_add_mole_title);
        addMoleAction = (TextView) findViewById(R.id.zone_add_mole_action);
        addMoleCancel = findViewById(R.id.zone_add_mole_cancel);
        addMoleCancel.setOnClickListener(v -> endMoleCreateState());
    }

    @Override
    public void onDataReady()
    {
        super.onDataReady();
        fetchZone();
    }

    private void showMoleDialog(Mole mole)
    {
        MoleGrowthDialog.showGrowthDialog(BodyZoneActivity.this,
                mole,
                new MoleGrowthDialog.MoleGrowthCallbacks()
                {
                    @Override
                    public void onEditMole()
                    {
                        showEditDialog(mole);
                    }

                    @Override
                    public void onUpdateMole()
                    {
                        Intent intent = PhotoCaptureActivity.newIntent(BodyZoneActivity.this,
                                PhotoCaptureActivity.MOLE,
                                mole.id);
                        intent.putExtra(PhotoCaptureActivity.KEY_INSTRUCTION,
                                getString(R.string.photo_cap_inst_mole));
                        startActivityForResult(intent, PhotoCaptureActivity.REQUEST_CODE_MOLE);
                    }
                }).show();
    }

    private void deleteMole(Mole mole)
    {
        Observable.create(subscriber -> {
            ((Database) StorageAccess.getInstance().getAppDatabase()).deleteMole(mole);
            subscriber.onNext(null);
        }).compose(ObservableUtils.applyDefault()).subscribe(o -> {
            Toast.makeText(BodyZoneActivity.this, R.string.mole_deleted, Toast.LENGTH_SHORT).show();
            fetchZone();
        });
    }

    private void showEditDialog(Mole mole)
    {
        MoleEditDialog.newInstance(this, mole, true, new MoleEditDialog.MoleEditCallbacks()
        {
            @Override
            public void onCancel()
            {
                showMoleDialog(mole);
            }

            @Override
            public void onDeleteMole()
            {
                if(mole.measurements.size() > 0)
                {
                    showRemovedDialog(mole);
                }
                else
                {
                    deleteMole(mole);
                }
            }

            @Override
            public void onUpdateMoleName(String newTitle)
            {
                Observable.create(subscriber -> {
                    mole.moleName = newTitle;
                    ((Database) StorageAccess.getInstance().getAppDatabase()).saveMole(mole);
                    LogExt.i(MoleEditDialog.class, "New title saved: " + mole.moleName);
                }).compose(ObservableUtils.applyDefault()).subscribe();
            }
        }).show();
    }

    private void showRemovedDialog(Mole mole)
    {
        LayoutInflater inflater = LayoutInflater.from(BodyZoneActivity.this);
        View layout = inflater.inflate(R.layout.dialog_remove, null);

        Dialog dialog = new AlertDialog.Builder(BodyZoneActivity.this, R.style.Dialog_Body).show();
        dialog.setCancelable(true);

        ((TextView) layout.findViewById(R.id.dialog_remove_title)).setText(getString(R.string.dialog_remove_title,
                mole.moleName));

        layout.findViewById(R.id.dialog_remove_cancel).setOnClickListener(v -> {
            dialog.dismiss();
            showEditDialog(mole);
        });
        layout.findViewById(R.id.dialog_remove_removed).setOnClickListener(v -> {
            dialog.dismiss();
            startMoleRemovalSurvey(mole);
        });
        layout.findViewById(R.id.dialog_remove_delete).setOnClickListener(v -> {
            dialog.dismiss();
            deleteMole(mole);
        });


        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.copyFrom(dialog.getWindow().getAttributes());
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.CENTER;
        dialog.setContentView(layout);
        dialog.getWindow().setAttributes(params);
    }

    private void startMoleRemovalSurvey(Mole mole)
    {
        InstructionStep firstStep = new InstructionStep("sitePhoto",
                getString(R.string.biopsy_step_title),
                getString(R.string.biopsy_step_text));

        InstructionStep thankYouStep = new InstructionStep("thankYou",
                getString(R.string.rss_thank_you),
                getString(R.string.biopsy_thank_you_text));
        // not great, but we need to pass the mole id into the task, using it as the identifier
        OrderedTask task = new OrderedTask(String.valueOf(mole.id), firstStep, thankYouStep);
        startActivityForResult(ViewTaskActivity.newIntent(BodyZoneActivity.this, task),
                REQUEST_MOLE_REMOVAL);
    }

    private void fetchZone()
    {
        Observable.fromCallable(() -> ((Database) StorageAccess.getInstance()
                .getAppDatabase()).loadZone(bodyZone))
                .compose(ObservableUtils.applyDefault())
                .subscribe(loadedZone -> {
                    if(loadedZone == null)
                    {
                        this.zone = new Zone();
                        this.zone.id = bodyZone;
                    }
                    else
                    {
                        this.zone = loadedZone;
                    }

                    zoneImage.setMoles(zone.moles);

                    if(TextUtils.isEmpty(zone.photo))
                    {
                        // Hide Zone Image
                        zoneImage.setVisibility(View.GONE);

                        // Show Empty View
                        empty = (TextView) findViewById(R.id.empty);
                        Drawable top = empty.getCompoundDrawables()[1];
                        DrawableCompat.wrap(top);
                        DrawableCompat.setTint(top,
                                ContextCompat.getColor(this, R.color.text_color_mole_inactive));
                        empty.setVisibility(View.VISIBLE);

                        // Handle FABs
                        addMole.setVisibility(View.GONE);
                        captureZone.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        // Set Toolbar color
                        toolbar.setBackgroundColor(getResources().getColor(R.color.rsb_black_40));

                        byte[] imageBytes = StorageAccess.getInstance()
                                .getFileAccess()
                                .readData(this, zone.photo);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes,
                                0,
                                imageBytes.length);
                        zoneImage.setImageBitmap(bitmap);
                    }

                    supportInvalidateOptionsMenu();
                });
    }

    private void startMoleCreateState()
    {
        LogExt.i(getClass(), "startMoleCreateState");

        addMole.animate()
                .scaleX(0)
                .scaleY(0)
                .setDuration(150)
                .setInterpolator(new FastOutLinearInInterpolator())
                .withEndAction(() -> {
                    addMole.setVisibility(View.GONE);
                    addMoleContainer.animate().setDuration(150).translationY(0);
                });

        zoneImage.setCurrentState(ZoneView.STATE_MOLE_CREATE);
        addMoleTitle.setText(R.string.zone_add_mole_hint_highlight);
        addMoleAction.setText(R.string.save);
        addMoleAction.setOnClickListener(v -> {

            Observable.from(zoneImage.getCreated())
                    .map(emptyMole -> ((Database) StorageAccess.getInstance()
                            .getAppDatabase()).createMole(BodyZoneActivity.this, emptyMole, zone))
                    .compose(ObservableUtils.applyDefault())
                    .subscribe(mole -> {
                        LogExt.d(BodyZoneActivity.class, "Saved new mole: " + mole.moleName);
                    }, error -> {
                        throw new OnErrorNotImplementedException(error);
                    }, this :: fetchZone);

            startMoleSelectionState();
        });
        addMoleCancel.setVisibility(View.VISIBLE);
    }

    private void startMoleSelectionState()
    {
        LogExt.i(getClass(), "startMoleSelectionState");
        zoneImage.setCurrentState(ZoneView.STATE_MOLE_SELECT);
        addMoleTitle.setText(R.string.zone_add_mole_hint_select);
        addMoleAction.setText(R.string.rsb_step_skip);
        addMoleAction.setOnClickListener(v -> endMoleCreateState());
        addMoleCancel.setVisibility(View.GONE);
    }

    private void endMoleCreateState()
    {
        LogExt.i(getClass(), "endMoleCreateState");

        addMoleContainer.animate()
                .setDuration(150)
                .translationY(addMoleContainer.getHeight())
                .withEndAction(() -> {
                    addMole.setVisibility(View.VISIBLE);
                    addMole.animate().setDuration(150).scaleX(1).scaleY(1);
                });

        zoneImage.setCurrentState(ZoneView.STATE_MOLE_SELECT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode == PhotoCaptureActivity.REQUEST_CODE_ZONE && resultCode == RESULT_OK)
        {
            zone.photo = data.getStringExtra(PhotoCaptureActivity.KEY_PATH);

            byte[] imageBytes = StorageAccess.getInstance()
                    .getFileAccess()
                    .readData(this, zone.photo);
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

            // Handle zone image
            zoneImage.setVisibility(View.VISIBLE);
            zoneImage.setImageBitmap(bitmap);

            // Change toolbar BG
            toolbar.setBackgroundColor(getResources().getColor(R.color.rsb_black_40));

            // Handle FABs
            addMole.setVisibility(View.VISIBLE);
            captureZone.setVisibility(View.GONE);

            // Hide empty state
            empty.setVisibility(View.GONE);

            Observable.defer(() -> {
                ((Database) StorageAccess.getInstance().getAppDatabase()).saveZone(zone);
                return Observable.empty();
            }).compose(ObservableUtils.applyDefault()).subscribe(o -> {
                supportInvalidateOptionsMenu();
            });

        }
        else if(requestCode == PhotoCaptureActivity.REQUEST_CODE_MOLE && resultCode == RESULT_OK)
        {
            int moleId = data.getIntExtra(PhotoCaptureActivity.KEY_ID, 0);

            Intent intent = MoleMeasurementActivity.newIntent(this,
                    moleId,
                    data.getStringExtra(PhotoCaptureActivity.KEY_PATH));
            startActivityForResult(intent, MoleMeasurementActivity.REQUEST_CODE);
        }
        else if(requestCode == MoleMeasurementActivity.REQUEST_CODE)
        {
            String tempMeasurementPhoto = data.getStringExtra(MoleMeasurementActivity.KEY_RESULT_TEMP_PHOTO);

            if(resultCode == RESULT_OK)
            {
                Measurement measurement = (Measurement) data.getSerializableExtra(
                        MoleMeasurementActivity.KEY_RESULT_MEASUREMENT);

                Observable.fromCallable(() -> {
                    // Create the measurement in the database
                    ((Database) StorageAccess.getInstance().getAppDatabase()).saveMeasurement(
                            measurement);

                    // Rename temp image to official image
                    String imagePath = "/mole_measurements/measurement_" + measurement.id;
                    StorageAccess.getInstance()
                            .getFileAccess()
                            .moveData(this, tempMeasurementPhoto, imagePath);

                    // Save the measurement again with the new photo url
                    measurement.measurementPhoto = imagePath;
                    ((Database) StorageAccess.getInstance().getAppDatabase()).saveMeasurement(
                            measurement);

                    return measurement;
                }).compose(ObservableUtils.applyDefault()).subscribe(modified -> {
                    // Update zone
                    fetchZone();

                    // Upload result
                    Observable.fromCallable(() -> {
                        ((MoleMapperDataProvider) DataProvider.getInstance()).uploadMeasurement(
                                BodyZoneActivity.this,
                                modified);
                        return null;
                    }).compose(ObservableUtils.applyDefault()).subscribe();
                });
            }
            else
            {
                // Remove the measurement photo since it wont be used
                Observable.fromCallable(() -> {
                    StorageAccess.getInstance()
                            .getFileAccess()
                            .clearData(this, tempMeasurementPhoto);
                    return Observable.empty();
                }).compose(ObservableUtils.applyDefault()).subscribe();
            }
        }
        else if(requestCode == REQUEST_MOLE_REMOVAL && resultCode == RESULT_OK)
        {
            // mole id was stored as the task identifier
            String moleId = ((TaskResult) data.getSerializableExtra(ViewTaskActivity.EXTRA_TASK_RESULT))
                    .getIdentifier();

            // get diagnoses from survey if that is ever implemented (not currently on ios)
            List<String> diagnoses = new ArrayList<>();
            diagnoses.add("removed");

            Observable.create(subscriber -> {
                ((MoleMapperDataProvider) DataProvider.getInstance()).uploadMoleRemoved(this,
                        moleId,
                        diagnoses,
                        new Date());

                Database db = (Database) StorageAccess.getInstance().getAppDatabase();
                Mole mole = db.loadMole(Integer.valueOf(moleId));
                mole.removed = true;
                db.saveMole(mole);
                subscriber.onNext(true);
            }).compose(ObservableUtils.applyDefault()).subscribe(success -> fetchZone());
        }
        else
        {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_body_zone, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        MenuItem updatePhoto = menu.findItem(R.id.menu_update_photo);
        updatePhoto.setVisible(zone != null && ! TextUtils.isEmpty(zone.photo));

        MenuItem deleteZone = menu.findItem(R.id.menu_delete_zone);
        deleteZone.setVisible(zone != null && ! TextUtils.isEmpty(zone.photo));

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(item.getItemId() == android.R.id.home)
        {
            onBackPressed();
            return true;
        }
        else if(item.getItemId() == R.id.menu_update_photo)
        {
            Intent intent = PhotoCaptureActivity.newIntent(this,
                    PhotoCaptureActivity.ZONE,
                    zone.id);
            startActivityForResult(intent, PhotoCaptureActivity.REQUEST_CODE_ZONE);
            return true;
        }
        else if(item.getItemId() == R.id.menu_delete_zone)
        {
            new AlertDialog.Builder(this).setTitle(R.string.dialog_delete_zone_photo_title)
                    .setMessage(R.string.dialog_delete_zone_photo_text)
                    .setPositiveButton(R.string.delete, (dialog, which) -> {
                        Observable.create(subscriber -> {
                            ((Database) StorageAccess.getInstance().getAppDatabase()).deleteZone(
                                    this,
                                    zone.id);
                            subscriber.onNext(null);
                            subscriber.onCompleted();
                        }).compose(ObservableUtils.applyDefault()).subscribe(o -> {
                                    Toast.makeText(this,
                                            R.string.dialog_delete_zone_confirmed,
                                            Toast.LENGTH_SHORT).show();
                                    BodyZoneActivity.this.finish();
                                },
                                error -> Toast.makeText(this,
                                        R.string.dialog_delete_zone_failed,
                                        Toast.LENGTH_SHORT).show());
                    }).setNegativeButton(android.R.string.cancel, null)
                    .show();
            return true;
        }
        else
        {
            return super.onOptionsItemSelected(item);
        }
    }

}
