package org.researchstack.molemapper;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.researchstack.backbone.StorageAccess;
import org.researchstack.backbone.ui.PinCodeActivity;
import org.researchstack.backbone.utils.ObservableUtils;
import org.researchstack.molemapper.ui.view.BodyMapView;

import rx.Observable;

public class BodyMapActivity extends PinCodeActivity
{
    public static final String TAG = BodyMapActivity.class.getSimpleName();

    private BodyMapView bodyMapView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_body_map);

        bodyMapView = (BodyMapView) findViewById(R.id.body_map);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(bodyMapView.getCurrentSide() == BodyMapView.STATE_BODY_FRONT
                ? R.string.body_map_front
                : R.string.body_map_back);
    }

    private void loadZoneSection()
    {
        final int currentSize = bodyMapView.getCurrentSide();
        Observable.from(((Database) StorageAccess.getInstance().getAppDatabase()).loadZones(
                currentSize))
                .filter(zone -> zone != null && zone.moles != null && ! zone.moles.isEmpty())
                .toList()
                .compose(ObservableUtils.applyDefault())
                .subscribe(bodyMapView:: setMoleCount);
    }

    @Override
    public void onDataReady()
    {
        super.onDataReady();

        loadZoneSection();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_body_map, menu);

        MenuItem toggle = menu.findItem(R.id.menu_toggle_body);
        MenuItemCompat.setActionView(toggle, R.layout.view_toggle);
        MenuItemCompat.expandActionView(toggle);
        toggle.getActionView().setOnClickListener(v -> onOptionsItemSelected(toggle));

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(item.getItemId() == android.R.id.home)
        {
            onBackPressed();
            return true;
        }
        else if(item.getItemId() == R.id.menu_toggle_body)
        {
            // Start a cool rotate animation
            View actionView = item.getActionView();
            if(actionView.getRotation() == 360)
            {
                actionView.setRotation(0);
            }
            actionView.animate().rotation(actionView.getRotation() + 180).start();

            // Switch our side
            bodyMapView.toggleSide();

            // Fetch out data
            loadZoneSection();

            // Update title
            getSupportActionBar().setTitle(
                    bodyMapView.getCurrentSide() == BodyMapView.STATE_BODY_FRONT
                            ? R.string.body_map_front
                            : R.string.body_map_back);
        }

        return super.onOptionsItemSelected(item);
    }

}
