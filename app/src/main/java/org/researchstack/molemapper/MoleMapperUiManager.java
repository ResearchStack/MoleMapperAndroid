package org.researchstack.molemapper;
import android.content.Context;

import org.researchstack.backbone.answerformat.BooleanAnswerFormat;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.QuestionStep;
import org.researchstack.backbone.step.Step;
import org.researchstack.molemapper.ui.MoleLearnActivity;
import org.researchstack.molemapper.ui.fragment.MoleDashboardFragment;
import org.researchstack.molemapper.ui.fragment.MoleMapperFragment;
import org.researchstack.skin.ActionItem;
import org.researchstack.skin.UiManager;
import org.researchstack.skin.task.OnboardingTask;

import java.util.ArrayList;
import java.util.List;

public class MoleMapperUiManager extends UiManager
{
    /**
     * @return List of NavigationItems
     */
    @Override
    public List<ActionItem> getMainTabBarItems()
    {
        List<ActionItem> navItems = new ArrayList<>();

        navItems.add(new ActionItem.ActionItemBuilder().setId(R.id.nav_body_map)
                .setTitle(R.string.body_map)
                .setIcon(R.drawable.ic_tabbar_moles)
                .setClass(MoleMapperFragment.class)
                .build());

        navItems.add(new ActionItem.ActionItemBuilder().setId(R.id.nav_dashboard)
                .setTitle(R.string.rss_dashboard)
                .setIcon(R.drawable.ic_tabbar_dashboard)
                .setClass(MoleDashboardFragment.class)
                .build());

        return navItems;
    }

    @Override
    public List<ActionItem> getMainActionBarItems()
    {
        List<ActionItem> navItems = new ArrayList<>();

        navItems.add(new ActionItem.ActionItemBuilder().setId(R.id.nav_learn)
                .setTitle(R.string.learn_more)
                .setIcon(R.drawable.ic_action_info)
                .setClass(MoleLearnActivity.class)
                .build());

        navItems.add(new ActionItem.ActionItemBuilder().setId(R.id.nav_settings)
                .setTitle(R.string.rss_settings)
                .setIcon(R.drawable.ic_action_settings)
                .setClass(MoleMapperSettingsActivity.class)
                .build());

        return navItems;
    }

    @Override
    public Step getInclusionCriteriaStep(Context context)
    {
        QuestionStep step = new QuestionStep(OnboardingTask.SignUpInclusionCriteriaStepIdentifier);
        step.setStepTitle(R.string.rss_eligibility);
        step.setTitle(context.getString(R.string.eligibility_title));
        step.setText(context.getString(R.string.eligibility_text));
        step.setAnswerFormat(new BooleanAnswerFormat(context.getString(R.string.rsb_yes),
                context.getString(R.string.rsb_no)));
        step.setOptional(false);
        return step;
    }

    @Override
    public boolean isInclusionCriteriaValid(StepResult stepResult)
    {
        return stepResult != null ? ((StepResult<Boolean>) stepResult).getResult() : false;
    }

    @Override
    public boolean isConsentSkippable()
    {
        return true;
    }
}
