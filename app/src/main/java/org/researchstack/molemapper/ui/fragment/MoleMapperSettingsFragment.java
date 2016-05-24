package org.researchstack.molemapper.ui.fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceScreen;

import org.researchstack.backbone.StorageAccess;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.storage.file.StorageAccessListener;
import org.researchstack.backbone.utils.ObservableUtils;
import org.researchstack.backbone.utils.TextUtils;
import org.researchstack.molemapper.R;
import org.researchstack.molemapper.models.MoleNameHelper;
import org.researchstack.molemapper.task.MoleMapperInitialTask;
import org.researchstack.skin.ui.fragment.SettingsFragment;

import rx.Observable;

public class MoleMapperSettingsFragment extends SettingsFragment implements StorageAccessListener
{
    public static final String KEY_PROFILE_PROFESSION = "MoleMapper.KEY_PROFILE_PROFESSION";
    public static final String KEY_APP                = "MoleMapper.KEY_APP";
    public static final String KEY_APP_MOLE_NAMING    = "MoleMapper.KEY_APP_MOLE_NAMING";

    @Override
    public void onCreatePreferences(Bundle bundle, String s)
    {
        super.onCreatePreferences(bundle, s);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                getContext());

        // Get our screen which is created in Skin SettingsFragment
        PreferenceScreen screen = getPreferenceScreen();

        // Get profile preference
        PreferenceCategory category = (PreferenceCategory) screen.findPreference(KEY_PROFILE);

        // If category exists, we should add mole mapper specific things. If not, that means we
        // are not consented so we have no data to set.
        if(category != null)
        {
            // Occupation Preference
            Preference checkBoxPref = new Preference(screen.getContext());
            checkBoxPref.setKey(KEY_PROFILE_PROFESSION);
            checkBoxPref.setTitle(R.string.profession);
            checkBoxPref.setSummary(R.string.profession_unknown); // Set to prevent a "jump" when first entering the screen
            category.addPreference(checkBoxPref);
        }

        // Create the app category to place out two rpefs
        PreferenceCategory appCategory = new PreferenceCategory(screen.getContext());
        appCategory.setKey(KEY_APP);
        appCategory.setTitle(R.string.settings_app);
        appCategory.setOrder(1);
        screen.addPreference(appCategory);

        // Generate values for the mole naming convention pref
        MoleNameHelper.Gender[] values = MoleNameHelper.Gender.values();
        String[] entries = new String[values.length];
        String[] entryValues = new String[values.length];
        for(int i = 0; i < values.length; i++)
        {
            MoleNameHelper.Gender gender = values[i];
            entries[i] = getString(gender.getNameResourceId());
            entryValues[i] = Integer.toString(i);
        }

        // Create ListPreference and add to "App" category
        String defaultNamingConvention = Integer.toString(MoleNameHelper.Gender.RANDOM.ordinal());
        ListPreference namingConventionPref = new ListPreference(screen.getContext());
        namingConventionPref.setTitle(R.string.settings_app_mole_name_scheme);
        namingConventionPref.setKey(KEY_APP_MOLE_NAMING);
        namingConventionPref.setDefaultValue(defaultNamingConvention);
        namingConventionPref.setEntries(entries);
        namingConventionPref.setEntryValues(entryValues);
        namingConventionPref.setDialogTitle(R.string.settings_app_mole_name_scheme);
        String currentNamingConvInt = sharedPreferences.getString(KEY_APP_MOLE_NAMING,
                defaultNamingConvention);
        int currentNamingOrdinal = Integer.parseInt(currentNamingConvInt);
        namingConventionPref.setSummary(values[currentNamingOrdinal].getNameResourceId());
        appCategory.addPreference(namingConventionPref);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
    {
        switch(key)
        {
            case KEY_APP_MOLE_NAMING:
                String defaultNamingConvention = Integer.toString(MoleNameHelper.Gender.RANDOM.ordinal());
                String currentNamingConvInt = sharedPreferences.getString(key,
                        defaultNamingConvention);
                int currentNamingOrdinal = Integer.parseInt(currentNamingConvInt);

                // Get App Category
                PreferenceCategory categoryNaming = (PreferenceCategory) getPreferenceScreen().findPreference(
                        KEY_APP);

                // Get naming pref and change summary string
                Preference namingConventionPref = categoryNaming.findPreference(KEY_APP_MOLE_NAMING);
                namingConventionPref.setSummary(MoleNameHelper.Gender.values()[currentNamingOrdinal]
                        .getNameResourceId());
                break;
            default:
                super.onSharedPreferenceChanged(sharedPreferences, key);
                break;
        }
    }


    @Override
    public void onDataReady()
    {
        super.onDataReady();

        // Get profile category
        PreferenceCategory profileCat = (PreferenceCategory) getPreferenceScreen().findPreference(
                KEY_PROFILE);

        // If exists, load users occupation
        if(profileCat != null)
        {
            Preference professionPref = profileCat.findPreference(KEY_PROFILE_PROFESSION);

            Observable.defer(() -> Observable.just(StorageAccess.getInstance()
                    .getAppDatabase()
                    .loadStepResults(MoleMapperInitialTask.PROFESSION)))
                    .compose(ObservableUtils.applyDefault())
                    .filter(list -> list != null && ! list.isEmpty())
                    .subscribe(steps -> {
                        StepResult<String> occupationResult = steps.get(0);
                        if(occupationResult != null &&
                                ! TextUtils.isEmpty(occupationResult.getResult()))
                        {
                            StringBuilder occupation = new StringBuilder(occupationResult.getResult());
                            occupation.setCharAt(0, Character.toUpperCase(occupation.charAt(0)));
                            professionPref.setSummary(occupation.toString());
                        }
                    });
        }
    }

    @Override
    public void onDataFailed()
    {
        super.onDataFailed();
        // Ignore
    }

    @Override
    public void onDataAuth()
    {
        super.onDataAuth();
        // Ignore
    }
}
