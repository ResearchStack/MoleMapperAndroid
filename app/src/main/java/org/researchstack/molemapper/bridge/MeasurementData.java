package org.researchstack.molemapper.bridge;
import org.researchstack.backbone.utils.FormatHelper;
import org.researchstack.molemapper.models.Measurement;
import org.researchstack.molemapper.models.Mole;
import org.researchstack.molemapper.models.Zone;

/**
 * Created by bradleymcdermott on 2/8/16.
 */
public class MeasurementData
{
    public static final String FILENAME       = "measurementData.json";
    public static final String PHOTO_FILENAME = "measurementPhoto.jpg";
    public static final String ITEM           = "moleMeasurement";
    public static final int    REVISION       = 2;

    public String measurementID;
    public String zoneID;
    public String moleID;
    public float  xCoordinate;
    public float  yCoordinate;
    public float  diameter;
    public String dateMeasured;

    public MeasurementData(Zone zone, Mole mole, Measurement measurement)
    {
        this.measurementID = String.valueOf(measurement.id);
        this.moleID = String.valueOf(mole.id);
        this.zoneID = String.valueOf(zone.id);
        this.xCoordinate = measurement.measurementX;
        this.yCoordinate = measurement.measurementY;
        this.diameter = measurement.absoluteMoleDiameter;
        this.dateMeasured = FormatHelper.DEFAULT_FORMAT.format(measurement.date);
    }
}
