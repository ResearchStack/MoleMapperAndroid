package org.researchstack.molemapper.bridge;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.result.TaskResult;
import org.researchstack.molemapper.task.MoleMapperInitialTask;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

/**
 * Created by bradleymcdermott on 2/11/16.
 */
public class InitialData
{
    public static final String FILENAME = "initialData.json";
    public static final String ITEM     = "initialData";
    public static final int    REVISION = 1;

    //Shortened zip code here for de-identified data going to Synapse
    private static final int SHORTENED_ZIP_LENGTH = 3;

    public String  birthyear;
    public String  gender;
    public String  shortenedZip;
    public String  hairColor;
    public String  eyeColor;
    public String  profession;
    public Integer melanomaDiagnosis;
    public Integer familyHistory;
    public Integer moleRemoved;
    public Integer autoImmune;
    public Integer immunocompromised;

    public InitialData(TaskResult taskResult)
    {
        Collection<StepResult> stepResults = taskResult.getResults().values();

        for(StepResult stepResult : stepResults)
        {
            if(stepResult == null)
            {
                continue;
            }

            if(stepResult.getIdentifier().equals(MoleMapperInitialTask.BASIC_INFO))
            {
                StepResult<String> genderResult = (StepResult<String>) stepResult.getResultForIdentifier(
                        MoleMapperInitialTask.GENDER);
                gender = genderResult.getResult();

                StepResult<Integer> zipResult = (StepResult<Integer>) stepResult.getResultForIdentifier(
                        MoleMapperInitialTask.ZIP_CODE);
                // pad left with 0s
                shortenedZip = String.format("%05d", zipResult.getResult())
                        .substring(0, SHORTENED_ZIP_LENGTH);

                StepResult<Long> birthDate = (StepResult<Long>) stepResult.getResultForIdentifier(
                        MoleMapperInitialTask.BIRTH_DATE);

                Long birthDateResult = birthDate.getResult();
                birthyear = birthDateResult == null
                        ? null
                        : new SimpleDateFormat("yyyy").format(new Date(birthDateResult));
            }
            else if(stepResult.getIdentifier().equals(MoleMapperInitialTask.HAIR_EYES_INFO))
            {
                StepResult<String> eyeResult = (StepResult<String>) stepResult.getResultForIdentifier(
                        MoleMapperInitialTask.EYE_COLOR);
                eyeColor = eyeResult.getResult();

                StepResult<String> hairResult = (StepResult<String>) stepResult.getResultForIdentifier(
                        MoleMapperInitialTask.HAIR_COLOR);
                hairColor = hairResult.getResult();

            }
            else if(stepResult.getIdentifier().equals(MoleMapperInitialTask.PROFESSION))
            {
                profession = ((String) stepResult.getResult());
            }
            else if(stepResult.getIdentifier().equals(MoleMapperInitialTask.MEDICAL_INFO))
            {
                melanomaDiagnosis = getIntegerFromBoolean(stepResult,
                        MoleMapperInitialTask.HISTORY_MELANOMA);
                familyHistory = getIntegerFromBoolean(stepResult,
                        MoleMapperInitialTask.FAMILY_HISTORY);
                moleRemoved = getIntegerFromBoolean(stepResult, MoleMapperInitialTask.MOLE_REMOVED);
                autoImmune = getIntegerFromBoolean(stepResult, MoleMapperInitialTask.AUTO_IMMUNE);
                immunocompromised = getIntegerFromBoolean(stepResult,
                        MoleMapperInitialTask.IMMUNOCOMPROMISED);
            }
        }
    }

    private Integer getIntegerFromBoolean(StepResult stepResult, String identifier)
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
