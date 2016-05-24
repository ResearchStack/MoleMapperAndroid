package org.researchstack.molemapper;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import org.researchstack.backbone.ui.PinCodeActivity;
import org.researchstack.molemapper.ui.fragment.CameraFragment;
import org.researchstack.molemapper.ui.fragment.PhotoCaptureCallbacks;

;

public class PhotoCaptureActivity extends PinCodeActivity implements PhotoCaptureCallbacks
{
    public static final String TAG             = PhotoCaptureActivity.class.getSimpleName();
    public static final String KEY_TYPE        = TAG + ".TYPE";
    public static final String KEY_ID          = TAG + ".ID";
    public static final String KEY_INSTRUCTION = TAG + ".INSTRUCTION";
    public static final String KEY_PATH        = TAG + ".PATH";

    public static final int REQUEST_CODE_ZONE = 9012;
    public static final int REQUEST_CODE_MOLE = 9013;
    public static final int ZONE              = 0;
    public static final int MOLE              = 1;

    public static Intent newIntent(Context context, int type, int id)
    {
        Intent intent = new Intent(context, PhotoCaptureActivity.class);
        intent.putExtra(KEY_ID, id);
        intent.putExtra(KEY_TYPE, type);
        intent.putExtra(KEY_PATH, getTypeImagePath(type, id));
        return intent;
    }

    public static String getTypeImagePath(int type, int id)
    {
        if (type == ZONE)
        {
            return "/zones/zone_" + id;
        }

        // If we are taking a picture of a mole then its for MoleMeasurementActivity. Place it in a
        // temp dir.
        else if (type == MOLE)
        {
            return "/temp/temp_zone_" + id + ".temp";
        }
        else
        {
            throw new IllegalArgumentException("Invalid type " + type);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_photo);

        if(null == savedInstanceState)
        {
            int id = getIntent().getIntExtra(KEY_ID, 0);
            String desc = getIntent().getStringExtra(KEY_INSTRUCTION);
            String path = getIntent().getStringExtra(KEY_PATH);
            int type = getIntent().getIntExtra(KEY_TYPE, MOLE);
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, CameraFragment.newInstance(id, type, desc, path))
                    .commit();
        }
    }

    @Override
    public void captureCanceled()
    {
        setResult(Activity.RESULT_CANCELED);
        finish();
    }

    @Override
    public void captureCompleted()
    {
        setResult(Activity.RESULT_OK, getIntent());
        finish();
    }

}
