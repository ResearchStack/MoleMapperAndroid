package org.researchstack.molemapper.models;
import java.io.Serializable;
import java.util.Date;

import co.touchlab.squeaky.field.DatabaseField;
import co.touchlab.squeaky.table.DatabaseTable;

/**
 * Created by bradleymcdermott on 2/4/16.
 */
@DatabaseTable
public class Measurement implements Serializable
{
    @DatabaseField(generatedId = true)
    public int id;

    /**
     * Image Path for the current measurement. Path is initially stored with a temp name of
     * mole_{MOLE_ID}. After the object is initially saved and an ID is provided by the ORM, the
     * this temp file is moved and renamed to /mole_measurement_{MEASUREMENT_ID} and updated.
     */
    @DatabaseField
    public String measurementPhoto;

    /**
     * Value in DP
     */
    @DatabaseField
    public float measurementX;

    /**
     * Value in DP
     */
    @DatabaseField
    public float measurementY;

    /**
     * Value in DP
     */
    @DatabaseField
    public float measurementDiameter;

    /**
     * Value in Millimeters
     */
    @DatabaseField
    public float absoluteMoleDiameter;

    /**
     * Value in DP
     */
    @DatabaseField
    public float referenceX;

    /**
     * Value in DP
     */
    @DatabaseField
    public float referenceY;

    /**
     * Value in DP
     */
    @DatabaseField
    public float referenceDiameter;

    /**
     * Value in Millimeters
     */
    @DatabaseField
    public float absoluteReferenceDiameter;

    /**
     * int id that should be position of the object within your reference-object-array
     */
    @DatabaseField
    public String referenceObject;

    /**
     * Date object is set when entering the MoleMeasurementActivity as thats closer to the time the
     * picture was taken.
     */
    @DatabaseField
    public Date date;

    @DatabaseField(foreign = true)
    public Mole mole;

    public Measurement()
    {
    }

    public Measurement(String measurementPhoto, float measurementDiameter, float measurementX, float measurementY, float absoluteMoleDiameter, float referenceX, float referenceY, float referenceDiameter, String referenceObject, float absoluteReferenceDiameter, Date date, Mole mole)
    {
        this.measurementPhoto = measurementPhoto;
        this.measurementDiameter = measurementDiameter;
        this.measurementX = measurementX;
        this.measurementY = measurementY;
        this.absoluteMoleDiameter = absoluteMoleDiameter;
        this.referenceX = referenceX;
        this.referenceY = referenceY;
        this.referenceDiameter = referenceDiameter;
        this.referenceObject = referenceObject;
        this.absoluteReferenceDiameter = absoluteReferenceDiameter;
        this.date = date;
        this.mole = mole;
    }
}
