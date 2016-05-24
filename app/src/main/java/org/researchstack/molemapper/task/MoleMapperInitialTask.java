package org.researchstack.molemapper.task;
import android.content.Context;

import org.researchstack.backbone.answerformat.AnswerFormat;
import org.researchstack.backbone.answerformat.BirthDateAnswerFormat;
import org.researchstack.backbone.answerformat.BooleanAnswerFormat;
import org.researchstack.backbone.answerformat.ChoiceAnswerFormat;
import org.researchstack.backbone.answerformat.DateAnswerFormat;
import org.researchstack.backbone.answerformat.IntegerAnswerFormat;
import org.researchstack.backbone.model.Choice;
import org.researchstack.backbone.step.FormStep;
import org.researchstack.backbone.step.InstructionStep;
import org.researchstack.backbone.step.QuestionStep;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.task.OrderedTask;
import org.researchstack.backbone.task.Task;
import org.researchstack.backbone.ui.step.body.BodyAnswer;
import org.researchstack.molemapper.R;

import java.util.ArrayList;
import java.util.List;

public class MoleMapperInitialTask
{

    public static final String BASIC_INFO        = "basicInfo";
    public static final String GENDER            = "gender";
    public static final String ZIP_CODE          = "zipCode";
    public static final String HAIR_EYES_INFO    = "hairEyesInfo";
    public static final String HAIR_COLOR        = "hairColor";
    public static final String EYE_COLOR         = "eyeColor";
    public static final String PROFESSION        = "profession";
    public static final String MEDICAL_INFO      = "medicalInfo";
    public static final String HISTORY_MELANOMA  = "historyMelanoma";
    public static final String FAMILY_HISTORY    = "familyHistory";
    public static final String MOLE_REMOVED      = "moleRemoved";
    public static final String AUTO_IMMUNE       = "autoImmune";
    public static final String IMMUNOCOMPROMISED = "immunocompromised";
    public static final String THANK_YOU         = "thankYou";
    public static final String INTRO             = "intro";
    public static final String BIRTH_DATE        = "birthDate";

    private MoleMapperInitialTask()
    {
    }

    public static Task create(Context context, String identifier)
    {
        List<Step> steps = new ArrayList<>();

        //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
        // Intro step
        //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*

        InstructionStep step = new InstructionStep(INTRO,
                context.getString(R.string.task_initial_title),
                context.getString(R.string.task_initial_text));

        step.setStepTitle(R.string.task_initial_step_title);

        // Add to Task
        steps.add(step);

        //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
        // Basic Info Form step
        //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*

        FormStep basicInfoForm = new FormStep(BASIC_INFO,
                context.getString(R.string.task_initial_title),
                "");

        // Gender
        AnswerFormat genderFormat = new ChoiceAnswerFormat(AnswerFormat.ChoiceAnswerStyle.SingleChoice,
                new Choice<>(context.getString(R.string.task_initial_gender_female), "Female"),
                new Choice<>(context.getString(R.string.task_initial_gender_male), "Male"),
                new Choice<>(context.getString(R.string.task_initial_gender_other), "Other"));
        QuestionStep genderStep = new QuestionStep(GENDER,
                context.getString(R.string.task_initial_gender_title),
                genderFormat);

        // Zip Code
        IntegerAnswerFormat zipCodeFormat = new ZipAnswerFormat();
        QuestionStep zipCodeStep = new QuestionStep(ZIP_CODE,
                context.getString(R.string.task_initial_zip_code_title),
                zipCodeFormat);

        DateAnswerFormat dobFormat = new BirthDateAnswerFormat(null, 18, 0);

        QuestionStep birthDateStep = new QuestionStep(BIRTH_DATE,
                context.getString(R.string.task_initial_birth_date_title),
                dobFormat);

        // Set items on FormStep
        basicInfoForm.setOptional(true);
        basicInfoForm.setFormSteps(genderStep, zipCodeStep, birthDateStep);

        // Add to Task
        steps.add(basicInfoForm);

        //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
        // Eye and Hair color form step
        //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*

        FormStep hairEyesForm = new FormStep(HAIR_EYES_INFO,
                context.getString(R.string.task_initial_hair_eyes_title),
                "");

        // Hair Color
        AnswerFormat hairColorFormat = new ChoiceAnswerFormat(AnswerFormat.ChoiceAnswerStyle.SingleChoice,
                new Choice<>(context.getString(R.string.task_initial_hair_eyes_red_hair),
                        "redHair"),
                // American English defines adjective-form as Blond where Noun type is based on sex
                // of person (Male -> Blond, Female -> Blonde). I looked this up. We will keep the
                // ID as blondeHair as thats the ID used in mole-mapper-ios.
                new Choice<>(context.getString(R.string.task_initial_hair_eyes_blond_hair),
                        "blondeHair"),
                new Choice<>(context.getString(R.string.task_initial_hair_eyes_brown_hair),
                        "brownHair"),
                new Choice<>(context.getString(R.string.task_initial_hair_eyes_black_hair),
                        "blackHair"));
        QuestionStep hairColorStep = new QuestionStep(HAIR_COLOR,
                context.getString(R.string.task_initial_hair_title),
                hairColorFormat);

        AnswerFormat eyeColorFormat = new ChoiceAnswerFormat(AnswerFormat.ChoiceAnswerStyle.SingleChoice,
                new Choice<>(context.getString(R.string.task_initial_eyes_blue), "blueEyes"),
                new Choice<>(context.getString(R.string.task_initial_eyes_green), "greenEyes"),
                new Choice<>(context.getString(R.string.task_initial_eyes_brown), "brownEyes"));
        QuestionStep eyeColorStep = new QuestionStep(EYE_COLOR,
                context.getString(R.string.task_initial_eyes_title),
                eyeColorFormat);

        // Set items on FormStep
        hairEyesForm.setOptional(true);
        hairEyesForm.setFormSteps(hairColorStep, eyeColorStep);

        // Add to Task
        steps.add(hairEyesForm);

        //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
        // Profession Step
        //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*

        // iOS defines this as a single choice, should be MultiChoice
        AnswerFormat professionFormat = new ChoiceAnswerFormat(AnswerFormat.ChoiceAnswerStyle.SingleChoice,
                new Choice<>(context.getString(R.string.task_initial_profession_pilot), "pilot"),
                new Choice<>(context.getString(R.string.task_initial_profession_dental), "dental"),
                new Choice<>(context.getString(R.string.task_initial_profession_construction),
                        "construction"),
                new Choice<>(context.getString(R.string.task_initial_profession_radiology),
                        "radiology"),
                new Choice<>(context.getString(R.string.task_initial_profession_farming),
                        "farming"),
                new Choice<>(context.getString(R.string.task_initial_profession_tsa), "tsaAgent"),
                new Choice<>(context.getString(R.string.task_initial_profession_natural_res),
                        "coalOilGas"),
                new Choice<>(context.getString(R.string.task_initial_profession_mil), "veteran"),
                new Choice<>(context.getString(R.string.task_initial_profession_doc), "doctor"),
                new Choice<>(context.getString(R.string.task_initial_profession_weld), "welding"),
                new Choice<>(context.getString(R.string.task_initial_profession_elect),
                        "electrician"),
                new Choice<>(context.getString(R.string.task_initial_profession_bio_research),
                        "researcher"),
                new Choice<>(context.getString(R.string.task_initial_profession_none), "none"));
        QuestionStep professionStep = new QuestionStep(PROFESSION,
                context.getString(R.string.task_initial_profession_title),
                professionFormat);
        professionStep.setOptional(true);

        // Add to Task
        steps.add(professionStep);

        //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
        // Medical Info Form Step
        //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*

        FormStep medicalInfoForm = new FormStep(MEDICAL_INFO,
                context.getString(R.string.task_initial_medical_info_title),
                "");

        BooleanAnswerFormat booleanAnswerFormat = new BooleanAnswerFormat(context.getString(R.string.rsb_yes),
                context.getString(R.string.rsb_no));

        QuestionStep melanomaStep = new QuestionStep(HISTORY_MELANOMA,
                context.getString(R.string.task_initial_medical_info_history),
                booleanAnswerFormat);
        QuestionStep familyHistoryStep = new QuestionStep(FAMILY_HISTORY,
                context.getString(R.string.task_initial_medical_info_history_fam),
                booleanAnswerFormat);
        QuestionStep moleRemovedStep = new QuestionStep(MOLE_REMOVED,
                context.getString(R.string.task_initial_medical_info_removed_mole),
                booleanAnswerFormat);
        QuestionStep autoImmuneStep = new QuestionStep(AUTO_IMMUNE,
                context.getString(R.string.task_initial_medical_info_auto_immune),
                booleanAnswerFormat);
        QuestionStep immunocompromisedStep = new QuestionStep(IMMUNOCOMPROMISED,
                context.getString(R.string.task_initial_medical_info_immunocomp),
                booleanAnswerFormat);

        // Set items on FormStep
        medicalInfoForm.setOptional(true);
        medicalInfoForm.setFormSteps(melanomaStep,
                familyHistoryStep,
                moleRemovedStep,
                autoImmuneStep,
                immunocompromisedStep);

        // Add to Task
        steps.add(medicalInfoForm);

        //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
        // Thank You Step
        //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*

        InstructionStep thankYouStep = new InstructionStep(THANK_YOU,
                context.getString(R.string.task_initial_thankyou_title),
                context.getString(R.string.task_initial_thankyou_text));

        // Add to Task
        steps.add(thankYouStep);

        return new OrderedTask(identifier, steps);
    }

    public static class ZipAnswerFormat extends IntegerAnswerFormat
    {
        /**
         * Creates a zip code answer format.
         */
        public ZipAnswerFormat()
        {
            super(0, 99999);
        }

        @Override
        public BodyAnswer validateAnswer(String inputString)
        {
            if(inputString != null && inputString.length() == 5)
            {
                return super.validateAnswer(inputString);
            }
            else
            {
                return new BodyAnswer(false, R.string.zip_invalid);

            }
        }
    }
}
