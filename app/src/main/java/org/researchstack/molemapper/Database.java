package org.researchstack.molemapper;
import android.content.Context;
import android.database.Cursor;
import android.util.Pair;

import net.sqlcipher.database.SQLiteDatabase;

import org.researchstack.backbone.StorageAccess;
import org.researchstack.backbone.utils.LogExt;
import org.researchstack.backbone.storage.database.sqlite.UpdatablePassphraseProvider;
import org.researchstack.molemapper.bridge.BridgeEncryptedDatabase;
import org.researchstack.molemapper.models.Measurement;
import org.researchstack.molemapper.models.Mole;
import org.researchstack.molemapper.models.MoleNameHelper;
import org.researchstack.molemapper.models.Zone;
import org.researchstack.molemapper.ui.view.BodyMapView;
import org.researchstack.molemapper.ui.view.BodyZoneHelper;

import java.sql.SQLException;
import java.util.List;
import java.util.Random;
import java.util.Set;

import co.touchlab.squeaky.dao.Dao;
import co.touchlab.squeaky.db.sqlcipher.SQLiteDatabaseImpl;
import co.touchlab.squeaky.stmt.Where;
import co.touchlab.squeaky.stmt.query.ManyClause;
import co.touchlab.squeaky.table.TableUtils;
import rx.Observable;

/**
 * Created by kgalligan on 11/27/15.
 */
public class Database extends BridgeEncryptedDatabase
{
    public static final String DB_NAME    = "appdb";
    public static final int    DB_VERSION = 1;

    public Database(Context context, UpdatablePassphraseProvider passphraseProvider)
    {
        super(context, DB_NAME, null, DB_VERSION, passphraseProvider);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase)
    {
        super.onCreate(sqLiteDatabase);
        try
        {
            TableUtils.createTables(new SQLiteDatabaseImpl(sqLiteDatabase),
                    Zone.class,
                    Mole.class,
                    Measurement.class);
        }
        catch(SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion)
    {
        super.onUpgrade(sqLiteDatabase, oldVersion, newVersion);
        throw new RuntimeException("Database onUpgrade not implemented");
    }

    public void saveZone(Zone zone)
    {
        LogExt.d(this.getClass(), "saveZone() id: " + zone.id);

        try
        {
            this.getDao(Zone.class).createOrUpdate(zone);
        }
        catch(SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    public Zone loadZone(int id)
    {
        LogExt.d(this.getClass(), "loadZone() id: " + id);
        try
        {
            return this.getDao(Zone.class).queryForId(id);
        }
        catch(SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    public List<Zone> loadZones(int side)
    {
        LogExt.d(this.getClass(), "loadZones() side" + side);
        try
        {
            int[] bodyLimit = BodyZoneHelper.getStartEndIdLimits(side);
            int[] headLimit = BodyZoneHelper.getStartEndIdLimits(BodyMapView.STATE_BODY_HEAD);
            Dao<Zone> zoneDao = this.getDao(Zone.class);
            ManyClause rangeQuery = new Where(zoneDao).or()
                    .between("id", bodyLimit[0], bodyLimit[1])
                    .between("id", headLimit[0], headLimit[1]);
            return rangeQuery.query().list();
        }
        catch(SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    public Mole createMole(Context context, Mole emptyMole, Zone zone)
    {
        emptyMole.zone = zone;
        try
        {
            Set<String> names = MoleNameHelper.loadNames(context);
            List<Mole> list = this.getDao(Mole.class).queryForAll().list();
            for(Mole mole : list)
            {
                names.remove(mole.moleName);
            }

            if(names.isEmpty())
            {
                // what do we do when all the names have been used? Unnamed Mole 103 for now
                emptyMole.moleName = context.getString(R.string.unnamed_mole, list.size());
            }
            else
            {
                String[] unusedNames = names.toArray(new String[names.size()]);
                emptyMole.moleName = unusedNames[new Random().nextInt(unusedNames.length)];
            }
        }
        catch(SQLException e)
        {
            throw new RuntimeException("Error loading moles to check available names", e);
        }
        saveMole(emptyMole);

        return emptyMole;
    }


    public void saveMole(Mole mole)
    {
        LogExt.d(this.getClass(), "saveMole() id: " + mole.id);

        try
        {
            this.getDao(Mole.class).createOrUpdate(mole);
        }
        catch(SQLException e)
        {
            throw new RuntimeException(e);
        }
    }


    public void deleteMole(Mole mole)
    {
        LogExt.d(this.getClass(), "deleteMole() id: " + mole.id);

        try
        {
            this.getDao(Mole.class).delete(mole);
        }
        catch(SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    public Mole loadMole(int id)
    {
        LogExt.d(this.getClass(), "loadMole() id: " + id);
        try
        {
            return this.getDao(Mole.class).queryForId(id);
        }
        catch(SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    public void saveMeasurement(Measurement measurement)
    {
        LogExt.d(this.getClass(), "saveMeasurement() id: " + measurement.id);

        try
        {
            this.getDao(Measurement.class).createOrUpdate(measurement);
        }
        catch(SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    public void deleteMeasurement(Context context, Measurement measurement)
    {
        LogExt.d(this.getClass(), "deleteMeasurement() id: " + measurement.id);

        try
        {
            StorageAccess.getInstance()
                    .getFileAccess()
                    .clearData(context, measurement.measurementPhoto);

            this.getDao(Measurement.class).delete(measurement);
        }
        catch(SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    public List<Mole> loadMoles()
    {
        try
        {
            return this.getDao(Mole.class).queryForAll().list();
        }
        catch(SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    public Observable<Pair<String, Float>> getLargestMoleNameAndDiameter()
    {
        return Observable.create(subscriber -> {
            try
            {
                getWrappedDatabase().beginTransaction();

                // Create a join where the max-diameter measurement mole_id is then used to select
                // the correct mole from the mole table. We then select the max-diameter and moleName
                Cursor maxCursor = getWrappedDatabase().rawQuery(
                        "SELECT M.moleName, MT.maxDiameter " +
                                "FROM mole " +
                                "AS M " +
                                "JOIN (SELECT MAX(absoluteMoleDiameter) AS maxDiameter, mole_id FROM measurement) AS MT " +
                                "ON M.id = MT.mole_id",
                        null);

                Pair<String, Float> value = null;

                if(maxCursor.getCount() != 0)
                {
                    // Get our values
                    maxCursor.moveToFirst();
                    String largestMoleName = maxCursor.getString(0);
                    float largestMoleSize = maxCursor.getFloat(1);

                    value = new Pair<>(largestMoleName, largestMoleSize);
                }

                // Cleanup
                maxCursor.close();
                getWrappedDatabase().endTransaction();

                // Return value
                subscriber.onNext(value);
                subscriber.onCompleted();
            }
            catch(Exception e)
            {
                throw new RuntimeException(e);
            }
        });
    }

    public Observable<Float> getAverageMoleDiameter()
    {
        return Observable.create(subscriber -> {
            try
            {
                getWrappedDatabase().beginTransaction();

                // We get the average mole by initially grouping each measurement by it's
                // mole-id. We then take the max of each date for each group, this gives us the
                // latest mole measurement per mole-id. We then wrap that as a sub-query and get
                // the average for absoluteMoleDiameter
                Cursor maxCursor = getWrappedDatabase().rawQuery(
                        "SELECT AVG(absoluteMoleDiameter) " +
                                "FROM ( " +
                                "SELECT msnt.mole_id, msnt.absoluteMoleDiameter, msnt.date " +
                                "FROM measurement msnt " +
                                "JOIN " +
                                "(SELECT mole_id, MAX(date) AS MaxDate " +
                                "FROM measurement " +
                                "GROUP BY mole_id) groupedmsnt " +
                                "ON msnt.mole_id = groupedmsnt.mole_id " +
                                "AND msnt.date = groupedmsnt.MaxDate " +
                                ")", null);

                Float value = null;

                // If cursor has row entry, get result
                if(maxCursor.getCount() != 0)
                {
                    // Get Value
                    maxCursor.moveToNext();
                    value = maxCursor.getFloat(0);
                }

                // Clean up
                maxCursor.close();
                getWrappedDatabase().endTransaction();

                subscriber.onNext(value);
                subscriber.onCompleted();
            }
            catch(Exception e)
            {
                throw new RuntimeException(e);
            }
        });
    }

    public Observable<List<Mole>> loadMolesObservable()
    {
        return Observable.create(subscriber -> {
            try
            {
                subscriber.onNext(this.getDao(Mole.class).queryForAll().list());
                subscriber.onCompleted();
            }
            catch(SQLException e)
            {
                throw new RuntimeException(e);
            }
        });
    }

    // Wrap the following in a transaction just in-case a delete fails
    public void deleteZone(Context context, int zoneId)
    {
        Context appContext = context.getApplicationContext();

        try
        {
            Dao<Mole> moleDao = this.getDao(Mole.class);
            Dao<Measurement> measurementDao = this.getDao(Measurement.class);

            // Load zone
            Zone zone = loadZone(zoneId);

            for(Mole mole : zone.moles)
            {
                // Iterate through all measurements, delete the photos for each measurement
                for(Measurement measurement : mole.measurements)
                {
                    StorageAccess.getInstance()
                            .getFileAccess()
                            .clearData(appContext, measurement.measurementPhoto);
                }

                // Now delete all zone measurement objects
                measurementDao.delete(mole.measurements);
            }

            // Then delete the zones
            moleDao.delete(zone.moles);

            // Finally, Delete zone-photo and photo
            Dao<Zone> zoneDao = this.getDao(Zone.class);
            StorageAccess.getInstance().getFileAccess().clearData(appContext, zone.photo);
            zoneDao.delete(zone);
        }
        catch(SQLException e)
        {
            throw new RuntimeException(e);
        }
    }
}

