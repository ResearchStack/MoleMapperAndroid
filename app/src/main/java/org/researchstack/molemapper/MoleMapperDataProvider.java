package org.researchstack.molemapper;

import android.content.Context;

import org.researchstack.backbone.ResourcePathManager;
import org.researchstack.backbone.StorageAccess;
import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.utils.FormatHelper;
import org.researchstack.molemapper.bridge.BridgeDataInput;
import org.researchstack.molemapper.bridge.BridgeDataProvider;
import org.researchstack.molemapper.bridge.Feedback;
import org.researchstack.molemapper.bridge.Followup;
import org.researchstack.molemapper.bridge.Info;
import org.researchstack.molemapper.bridge.InitialData;
import org.researchstack.molemapper.bridge.MeasurementData;
import org.researchstack.molemapper.bridge.MoleRemoved;
import org.researchstack.molemapper.models.Measurement;
import org.researchstack.molemapper.models.Mole;
import org.researchstack.molemapper.models.Zone;
import org.researchstack.skin.ResourceManager;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class MoleMapperDataProvider extends BridgeDataProvider
{
    @Override
    protected ResourceManager.Resource getPublicKey()
    {
        return new MoleMapperResourceManager.PemResource(BuildConfig.STUDY_PEM);
    }

    @Override
    protected ResourcePathManager.Resource getTasksAndSchedulesResId()
    {
        throw new RuntimeException("Mole Mapper doesn't have a schedule");
    }

    @Override
    protected String getBaseUrl()
    {
        return BuildConfig.STUDY_BASE_URL;
    }

    @Override
    protected String getStudyId()
    {
        return BuildConfig.STUDY_ID;
    }

    @Override
    protected String getStudyName() {
        return BuildConfig.STUDY_NAME;
    }

    @Override
    protected String getAppVersion() {
        return Integer.toString(BuildConfig.VERSION_CODE);
    }

    @Override
    public void processInitialTaskResult(Context context, TaskResult taskResult)
    {
        BridgeDataInput dataFile = new BridgeDataInput(new InitialData(taskResult),
                InitialData.class, InitialData.FILENAME,
                FormatHelper.DEFAULT_FORMAT.format(taskResult.getEndDate()));

        uploadBridgeData(context, new Info(context, InitialData.ITEM, InitialData.REVISION),
                dataFile);
    }

    public void uploadMeasurement(Context context, Measurement measurement)
    {
        Database db = (Database) StorageAccess.getInstance().getAppDatabase();
        Mole mole = measurement.mole;
        try
        {
            db.getDao(Mole.class).refresh(mole);
        }
        catch(SQLException e)
        {
            throw new RuntimeException("Error refreshing mole from DB");
        }

        Zone zone = mole.zone;
        MeasurementData measurementData = new MeasurementData(zone, mole, measurement);
        BridgeDataInput measurementFile = new BridgeDataInput(measurementData,
                measurementData.getClass(), MeasurementData.FILENAME,
                measurementData.dateMeasured);

        BridgeDataInput measurementPhoto = new BridgeDataInput(measurement.measurementPhoto,
                MeasurementData.PHOTO_FILENAME,
                measurementData.dateMeasured);

        uploadBridgeData(context, new Info(context, MeasurementData.ITEM, MeasurementData.REVISION),
                measurementFile,
                measurementPhoto);
    }

    public void uploadFeedback(Context context, TaskResult result)
    {
        BridgeDataInput feedbackFile = new BridgeDataInput(new Feedback(result),
                Feedback.class, Feedback.FILENAME,
                FormatHelper.DEFAULT_FORMAT.format(result.getEndDate()));

        uploadBridgeData(context, new Info(context, Feedback.ITEM, Feedback.REVISION),
                feedbackFile);
    }

    public void uploadFollowup(Context context, TaskResult result)
    {
        BridgeDataInput followupFile = new BridgeDataInput(new Followup(result),
                Followup.class, Followup.FILENAME,
                FormatHelper.DEFAULT_FORMAT.format(result.getEndDate()));

        uploadBridgeData(context, new Info(context, Followup.ITEM, Followup.REVISION),
                followupFile);
    }

    public void uploadMoleRemoved(Context context, String moleId, List<String> diagnoses, Date endDate)
    {
        BridgeDataInput removedFile = new BridgeDataInput(new MoleRemoved(moleId, diagnoses),
                MoleRemoved.class, MoleRemoved.FILENAME,
                FormatHelper.DEFAULT_FORMAT.format(endDate));

        uploadBridgeData(context, new Info(context, MoleRemoved.ITEM, MoleRemoved.REVISION),
                removedFile);
    }
}
