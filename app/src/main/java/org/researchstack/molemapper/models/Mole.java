package org.researchstack.molemapper.models;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import co.touchlab.squeaky.field.DatabaseField;
import co.touchlab.squeaky.field.ForeignCollectionField;
import co.touchlab.squeaky.table.DatabaseTable;

/**
 * Created by bradleymcdermott on 2/4/16.
 */
@DatabaseTable
public class Mole implements Serializable
{
    public static final int STATE_UNTRACKED        = 0;
    public static final int STATE_TRACKED          = 1;
    public static final int STATE_TRACKED_OUTDATED = 2;
    public static final int STATE_REMOVED          = 3;

    @DatabaseField(generatedId = true)
    public Integer           id;
    @DatabaseField
    public String            moleName;
    @DatabaseField
    public int               moleX;
    @DatabaseField
    public int               moleY;
    @DatabaseField
    public boolean           removed;
    @DatabaseField(foreign = true)
    public Zone              zone;
    @ForeignCollectionField(eager = true, foreignFieldName = "mole")
    public List<Measurement> measurements;

    public Mole()
    {
    }

    public int getState()
    {
        if(removed)
        {
            return STATE_REMOVED;
        }

        if(measurements == null || measurements.isEmpty())
        {
            return STATE_UNTRACKED;
        }

        // Get a Calendar object which represents the next time they should schedule their mole
        Date lastDate = measurements.get(measurements.size() - 1).date;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(lastDate);
        calendar.add(Calendar.DAY_OF_MONTH, 30);

        // If the current time is before the next measure time, the measurement is "current"
        if(System.currentTimeMillis() < calendar.getTimeInMillis())
        {
            return STATE_TRACKED;
        }
        else
        {
            return STATE_TRACKED_OUTDATED;
        }
    }
}
