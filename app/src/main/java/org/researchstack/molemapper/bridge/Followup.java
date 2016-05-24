package org.researchstack.molemapper.bridge;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.utils.FormatHelper;
import org.researchstack.molemapper.task.FollowupTask;

import java.util.Collection;

/**
 * Created by bradleymcdermott on 2/11/16.
 */
public class Followup
{
    public static final String FILENAME = "followup.json";
    public static final String ITEM     = "followup";
    public static final int    REVISION = 1;

    public Integer sunburn;
    public Integer moleRemoved;
    public Integer sunscreen;
    public Integer sick;
    public Integer tan;
    public String  date;

    public Followup(TaskResult taskResult)
    {
        Collection<StepResult> stepResults = taskResult.getResults().values();

        date = FormatHelper.DEFAULT_FORMAT.format(taskResult.getEndDate());
        for(StepResult stepResult : stepResults)
        {
            if(stepResult == null)
            {
                continue;
            }

            if(stepResult.getIdentifier().equals(FollowupTask.FOLLOWUP_FORM))
            {
                sunburn = getIntBoolean(stepResult, FollowupTask.SUNBURN);
                moleRemoved = getIntBoolean(stepResult, FollowupTask.MOLE_REMOVED);
                sick = getIntBoolean(stepResult, FollowupTask.SICK);
                tan = getIntBoolean(stepResult, FollowupTask.TAN);
                sunscreen = getIntBoolean(stepResult, FollowupTask.SUNSCREEN);

            }
        }
    }

    private Integer getIntBoolean(StepResult stepResult, String identifier)
    {
        Boolean result = ((StepResult<Boolean>) stepResult.getResultForIdentifier(identifier)).getResult();

        // unanswered
        if(result == null)
        {
            return null;
        }

        return Boolean.TRUE.equals(result) ? 1 : 0;
    }
}
