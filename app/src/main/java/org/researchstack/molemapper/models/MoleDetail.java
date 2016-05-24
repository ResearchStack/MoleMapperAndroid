package org.researchstack.molemapper.models;
import android.content.Context;

import org.researchstack.molemapper.R;

import java.util.Calendar;
import java.util.Collections;
import java.util.Locale;

/**
 * Created by bradleymcdermott on 2/22/16.
 */
public class MoleDetail
{
    public static final Calendar currentDate = Calendar.getInstance();

    public final Calendar lastMeasuredDate;
    public final float    lastMeasuredSize;
    public final float    increasePercent;
    public final int      zoneId;
    public final String   moleName;
    public final int      id;
    public final State    state;
    public final boolean  removed;

    /**
     * Ordinals are used to order list items in MoleMapperFragment. The order of these are crucial
     * to how they are ordered in the list. Please do not change.
     */
    public enum State
    {
        NEEDS_UPDATE,
        UNMEASURED,
        UP_TO_DATE
    }

    public MoleDetail(Mole mole)
    {
        moleName = mole.moleName;
        id = mole.id;
        zoneId = mole.zone.id;
        removed = mole.removed;
        Collections.sort(mole.measurements, (m1, m2) -> m2.date.compareTo(m1.date));

        if(mole.measurements.size() == 0)
        {
            state = State.UNMEASURED;
            lastMeasuredDate = null;
            lastMeasuredSize = 0f;
            increasePercent = 0f;
        }
        else
        {
            Measurement latest = mole.measurements.get(0);
            lastMeasuredDate = Calendar.getInstance();
            lastMeasuredDate.setTime(latest.date);
            lastMeasuredSize = latest.absoluteMoleDiameter;

            if(mole.measurements.size() > 1)
            {
                Measurement nextLatest = mole.measurements.get(1);
                increasePercent = (lastMeasuredSize - nextLatest.absoluteMoleDiameter) /
                        nextLatest.absoluteMoleDiameter;
            }
            else
            {
                increasePercent = 0f;
            }

            // If same years, compare the months
            if(lastMeasuredDate.get(Calendar.YEAR) == currentDate.get(Calendar.YEAR))
            {
                // if measurement is of this month, or future (because you're an alien/future-human)
                // then up to date
                if(lastMeasuredDate.get(Calendar.MONTH) >= currentDate.get(Calendar.MONTH))
                {
                    state = State.UP_TO_DATE;
                }
                else
                {
                    state = State.NEEDS_UPDATE;
                }
            }

            // If lastMeasured is from last year, update
            else if(lastMeasuredDate.get(Calendar.YEAR) < currentDate.get(Calendar.YEAR))
            {
                state = State.NEEDS_UPDATE;
            }

            // If you are from the future, then up to date
            else
            {
                state = State.UP_TO_DATE;
            }
        }

    }

    public String getLastCheckedText(Context context)
    {
        switch(state)
        {
            case UP_TO_DATE:
                String month = lastMeasuredDate.getDisplayName(Calendar.MONTH,
                        Calendar.LONG,
                        Locale.getDefault());
                String day = Integer.toString(lastMeasuredDate.get(Calendar.DAY_OF_MONTH));
                return context.getString(R.string.checked_in, month, day);

            case NEEDS_UPDATE:
                return context.getString(R.string.last_checked,
                        lastMeasuredDate.getDisplayName(Calendar.MONTH,
                                Calendar.LONG,
                                Locale.getDefault()));

            case UNMEASURED:
            default:
                return context.getString(R.string.never_measured);
        }
    }

    public boolean increased()
    {
        return increasePercent > 0f && state == MoleDetail.State.UP_TO_DATE;
    }

    public String getMeasurementText(Context context)
    {
        if(removed)
        {
            return context.getString(R.string.removed);
        }
        else if(increased())
        {
            // only show increase for recently updated
            return context.getString(R.string.size_increase,
                    increasePercent * 100,
                    lastMeasuredSize);
        }
        else if(lastMeasuredSize > 0f)
        {
            return context.getString(R.string.measurement_text, lastMeasuredSize);
        }
        else
        {
            return context.getString(R.string.not_applicable);
        }
    }
}
