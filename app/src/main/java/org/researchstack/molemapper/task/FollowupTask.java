package org.researchstack.molemapper.task;
import android.content.Context;

import org.researchstack.backbone.answerformat.AnswerFormat;
import org.researchstack.backbone.answerformat.BooleanAnswerFormat;
import org.researchstack.backbone.step.FormStep;
import org.researchstack.backbone.step.InstructionStep;
import org.researchstack.backbone.step.QuestionStep;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.task.OrderedTask;
import org.researchstack.backbone.task.Task;
import org.researchstack.molemapper.R;

import java.util.ArrayList;
import java.util.List;

public class FollowupTask
{
    public static final String TASK_FOLLOW_UP = "followup";

    public static final String TAN           = "tan";
    public static final String SUNBURN       = "sunburn";
    public static final String SUNSCREEN     = "sunscreen";
    public static final String SICK          = "sick";
    public static final String MOLE_REMOVED  = "moleRemoved";
    public static final String FOLLOWUP_FORM = "followupInfo";

    private FollowupTask()
    {
    }

    public static Task create(Context context)
    {
        List<Step> steps = new ArrayList<>();

        InstructionStep intro = new InstructionStep("intro",
                context.getString(R.string.task_followup_title),
                context.getString(R.string.task_followup_text));
        intro.setStepTitle(R.string.mole_follow_up);

        FormStep followupInfo = new FormStep(FOLLOWUP_FORM, null, null);
        followupInfo.setStepTitle(R.string.mole_follow_up);
        followupInfo.setOptional(false);

        ArrayList<QuestionStep> followupItems = new ArrayList<>();
        AnswerFormat booleanAnswerFormat = new BooleanAnswerFormat(context.getString(R.string.rsb_yes),
                context.getString(R.string.rsb_no));

        QuestionStep tan = new QuestionStep(TAN,
                context.getString(R.string.task_followup_tan_title),
                booleanAnswerFormat);
        tan.setOptional(false);
        followupItems.add(tan);

        QuestionStep sunburn = new QuestionStep(SUNBURN,
                context.getString(R.string.task_followup_sunburn_title), booleanAnswerFormat);
        sunburn.setOptional(false);
        followupItems.add(sunburn);

        QuestionStep sunscreen = new QuestionStep(SUNSCREEN,
                context.getString(R.string.task_followup_sunscreen_title), booleanAnswerFormat);
        sunscreen.setOptional(false);
        followupItems.add(sunscreen);

        QuestionStep sick = new QuestionStep(SICK,
                context.getString(R.string.task_followup_sick_title), booleanAnswerFormat);
        sick.setOptional(false);
        followupItems.add(sick);

        QuestionStep removed = new QuestionStep(MOLE_REMOVED,
                context.getString(R.string.task_followup_removed_title), booleanAnswerFormat);
        removed.setOptional(false);
        followupItems.add(removed);

        followupInfo.setFormSteps(followupItems);

        InstructionStep thankYouStep = new InstructionStep("thankYou",
                context.getString(R.string.task_followup_thankyou_title),
                context.getString(R.string.task_followup_thankyou_text));
        thankYouStep.setStepTitle(R.string.mole_follow_up);

        steps.add(intro);
        steps.add(followupInfo);
        steps.add(thankYouStep);

        return new OrderedTask(TASK_FOLLOW_UP, steps);
    }
}
