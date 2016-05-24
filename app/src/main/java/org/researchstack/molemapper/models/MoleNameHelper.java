package org.researchstack.molemapper.models;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.StringRes;
import android.support.v7.preference.PreferenceManager;

import org.researchstack.molemapper.R;
import org.researchstack.molemapper.ui.fragment.MoleMapperSettingsFragment;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by bradleymcdermott on 4/8/16.
 */
public class MoleNameHelper
{
    public enum Gender
    {
        RANDOM(R.string.naming_convention_random),
        MALE(R.string.naming_convention_male),
        FEMALE(R.string.naming_convention_female);

        private final int nameResourceId;

        Gender(@StringRes int nameResourceId)
        {
            this.nameResourceId = nameResourceId;
        }

        @StringRes
        public int getNameResourceId()
        {
            return nameResourceId;
        }
    }

    public static Set<String> loadNames(Context context)
    {
        Gender gender = loadGenderSetting(context);

        Set<String> names = new HashSet<>();

        if(gender == Gender.RANDOM || gender == Gender.MALE)
        {
            names.addAll(Arrays.asList(context.getResources().getStringArray(R.array.male_names)));
        }

        if(gender == Gender.RANDOM || gender == Gender.FEMALE)
        {
            names.addAll(Arrays.asList(context.getResources()
                    .getStringArray(R.array.female_names)));
        }

        return names;
    }

    private static Gender loadGenderSetting(Context context)
    {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String defaultNamingConvention = Integer.toString(MoleNameHelper.Gender.RANDOM.ordinal());
        String currentNamingConvInt = sharedPreferences.getString(MoleMapperSettingsFragment.KEY_APP_MOLE_NAMING,
                defaultNamingConvention);
        int currentNamingOrdinal = Integer.parseInt(currentNamingConvInt);
        return MoleNameHelper.Gender.values()[currentNamingOrdinal];
    }
}
