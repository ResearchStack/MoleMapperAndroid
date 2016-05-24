package org.researchstack.molemapper;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

import org.researchstack.skin.PermissionRequestManager;

public class MoleMappePermissionRequestManager extends PermissionRequestManager
{
    @Override
    public boolean hasPermission(Context context, String permissionId)
    {
        return ContextCompat.checkSelfPermission(context, permissionId) ==
                PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Used to tell if the permission-id should be handled by the system (using {@link
     * Activity#requestPermissions(String[], int)}) or through our own custom implementation in
     * {@link #onRequestNonSystemPermission}
     *
     * @param permissionId
     * @return
     */
    @Override
    public boolean isNonSystemPermission(String permissionId)
    {
        return false;
    }

    /**
     * This method is called when {@link #isNonSystemPermission} returns true. For example, if using
     * Google+ Sign In, you would create your signIn-Intent and start that activity. Any result will
     * then be passed through to {#link onNonSystemPermissionResult}
     *
     * @param permissionId
     */
    @Override
    public void onRequestNonSystemPermission(Activity activity, String permissionId)
    {
        // Do Nothing, we don't use PermissionRequestManager
    }

    /**
     * Method is called when your Activity called in {@link #onRequestNonSystemPermission} has
     * returned with a result
     *
     * @param requestCode
     * @param resultCode
     * @param data
     * @return
     */
    @Override
    public boolean onNonSystemPermissionResult(Activity activity, int requestCode, int resultCode, Intent data)
    {
        // Do Nothing, we don't use PermissionRequestManager
        return false;
    }
}
