package org.researchstack.molemapper.bridge;
import org.researchstack.backbone.result.TaskResult;

/**
 * Created by bradleymcdermott on 2/25/16.
 */
public class Feedback
{
    public static final String FILENAME = "userFeedback.json";
    public static final String ITEM     = "userFeedback";
    public static final int    REVISION = 2;

    public String feedback;

    public Feedback(TaskResult result)
    {
        this.feedback = (String) result.getStepResult("feedback").getResult();
    }
}
