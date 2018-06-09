package com.vinogorova.newsfeedapp;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;


public class SettingsActivity extends AppCompatActivity {

    private static final String LOG_TAG = SettingsActivity.class.getName();
    //Strings for last entered values for EditText Views
    private final static String LAST_VALUE_FROM = "dateFrom";
    private final static String LAST_VALUE_TO = "dateTo";
    public static EditText dateFromEditText;
    public static EditText dateToEditText;
    //Strings for values of EditText Views
    private static String dateFrom;
    private static String dateTo;

    //Returns value of dateFrom string
    public static String getDateFrom() {
        return dateFrom;
    }

    //Returns value of dateTo string
    public static String getDateTo() {
        return dateTo;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(LOG_TAG, "SettingsActivity created");
        setContentView(R.layout.settings_activity);
    }

    public static class NewsfeedPreferenceFragment extends PreferenceFragment
            implements Preference.OnPreferenceChangeListener {

        Preference pref;

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {

            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_main);

            //Find section settings by its key
            Preference section = findPreference(getString(R.string.settings_section_key));
            bindPreferenceSummaryValueTo(section);

            //Find Date Period settings by its key
            SwitchPreference switchPreference = (SwitchPreference) findPreference(getString(R.string.settings_date_period_key));
            //Set the start state for Date Period SwitchPreference
            switchPreference.setChecked(false);
            //Set listener to SwitchPreference
            switchPreference.setOnPreferenceChangeListener(this);

            //Find preference for data picker fields
            pref = findPreference(getString(R.string.settings_choose_date_period_key));

        }

        @Override
        public boolean onPreferenceChange(final Preference preference, Object value) {
            String stringValue = value.toString();

            //Check if the preference is instance of ListPreference
            //If it is, set summary value from SharedPreferences
            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                int prefIndex = listPreference.findIndexOfValue(stringValue);
                if (prefIndex >= 0) {
                    CharSequence[] labels = listPreference.getEntries();
                    preference.setSummary(labels[prefIndex]);
                }
            }
            //Check if the preference is instance of SwitchPreference
            if (preference instanceof SwitchPreference) {

                Log.e(LOG_TAG, "Preferense is instance of SwitchPreference");

                //Find EditText view with id date_from_edit_text
                dateFromEditText = getView().findViewById(R.id.date_from_edit_text);
                //Find EditText view with id date_to_edit_text
                dateToEditText = getView().findViewById(R.id.date_to_edit_text);

                //Get SharedPreferences for filling EditText views with the last data
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(preference.getContext());
                String prefFromDate = preferences.getString(LAST_VALUE_FROM, "");
                dateFromEditText.setText(prefFromDate);

                String prefToDate = preferences.getString(LAST_VALUE_TO, "");
                dateToEditText.setText(prefToDate);

                //Disable EditText views if SwitchPreference is turned off
                if (((SwitchPreference) preference).isChecked()) {
                    Log.e(LOG_TAG, "SwitchPreference is checked");
                    if (pref.isEnabled()) {
                        Log.e(LOG_TAG, "Date picker will be disabled");
                        pref.setEnabled(false);
                    }
                    //Enable EditText views if SwitchPreference is turned on
                } else {
                    pref.setEnabled(true);
                    Log.e(LOG_TAG, "Date picker is enabled");

                    //Add TextChangedListener to EditText dateFromEditText
                    dateFromEditText.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                        }

                        //Get received date value and save it
                        @Override
                        public void afterTextChanged(Editable s) {
                            dateFrom = dateFromEditText.getText().toString();
                            saveLastValue(preference, LAST_VALUE_FROM, dateFrom);
                        }
                    });

                    //Add TextChangedListener to EditText dateToEditText
                    dateToEditText.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                        }

                        //Get received date value and save it
                        //Refresh summary
                        @Override
                        public void afterTextChanged(Editable s) {
                            dateTo = dateToEditText.getText().toString();
                            if (dateTo.length() == 10) {
                                preference.setSummary(dateFrom + " - " + dateTo);
                                saveLastValue(preference, LAST_VALUE_TO, dateTo);
                            }
                        }
                    });
                }
                return true;
            }
            preference.setSummary(stringValue);
            return true;


        }

        private void bindPreferenceSummaryValueTo(Preference preference) {
            preference.setOnPreferenceChangeListener(this);
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(preference.getContext());
            String prefString = preferences.getString(preference.getKey(), "");
            onPreferenceChange(preference, prefString);
        }

        /**
         * Save the last date value in SharedPreferences
         *
         * @param preference
         * @param key
         * @param value
         */
        public void saveLastValue(Preference preference, String key, String value) {
            SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(preference.getContext());
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString(key, value);
            editor.commit();
        }

    }

}
