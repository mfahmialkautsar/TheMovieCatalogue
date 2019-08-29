package ga.softogi.themoviecatalogue.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.widget.Toast;

import java.util.Objects;

import ga.softogi.themoviecatalogue.R;
import ga.softogi.themoviecatalogue.reminder.AlarmReceiver;

public class ThePreferenceFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {
    private String DAILY_REMINDER;
    private String RELEASE_REMINDER;
    private String LOCALE;

    private AlarmReceiver alarmReceiver = new AlarmReceiver();

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.preferences);
        init();
    }

    private void init() {
        RELEASE_REMINDER = getResources().getString(R.string.key_release_reminder);
        DAILY_REMINDER = getResources().getString(R.string.key_daily_reminder);
        LOCALE = getResources().getString(R.string.key_locale);

        findPreference(RELEASE_REMINDER).setOnPreferenceChangeListener(this);
        findPreference(DAILY_REMINDER).setOnPreferenceChangeListener(this);
        findPreference(LOCALE).setOnPreferenceClickListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object o) {
        String key = preference.getKey();
        boolean isOn = (boolean) o;
        String releasedTime = "08:00";
        String dailyTime = "07:00";

        if (key.equals(RELEASE_REMINDER)) {
            if (isOn) {
                alarmReceiver.setRepeatingAlarm(Objects.requireNonNull(getActivity()), AlarmReceiver.TYPE_RELEASED, releasedTime, null);
            } else {
                alarmReceiver.cancelAlarm(Objects.requireNonNull(getActivity()), AlarmReceiver.TYPE_RELEASED);
            }

            Toast.makeText(getContext(), getString(R.string.daily_release_reminder) + " " + (isOn ? getString(R.string.activated) : getString(R.string.deactivated)), Toast.LENGTH_SHORT).show();
            return true;
        }
        if (key.equals(DAILY_REMINDER)) {
            if (isOn) {
                alarmReceiver.setRepeatingAlarm(Objects.requireNonNull(getActivity()), AlarmReceiver.TYPE_REPEATING, dailyTime, getString(R.string.daily_reminder_message));
            } else {
                alarmReceiver.cancelAlarm(Objects.requireNonNull(getActivity()), AlarmReceiver.TYPE_REPEATING);
            }

            Toast.makeText(getContext(), getString(R.string.daily_reminder_label) + " " + (isOn ? getString(R.string.activated) : getString(R.string.deactivated)), Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        String key = preference.getKey();

        if (key.equals(LOCALE)) {
            Intent intent = new Intent(Settings.ACTION_LOCALE_SETTINGS);
            startActivity(intent);
            return true;
        }
        return false;
    }
}
