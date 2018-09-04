package graziano.g.accessmonitoring.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.SwitchPreference;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.MenuItem;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;

import graziano.g.accessmonitoring.network.HttpClient;
import graziano.g.accessmonitoring.R;
import graziano.g.accessmonitoring.model.Child;
import graziano.g.accessmonitoring.model.Family;

public class SettingsActivity extends AppCompatPreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener, Response.Listener, Response.ErrorListener {

    private GeneralPreferenceFragment generalPreferenceFragment;

    public final static String PREFERENCE_KEY_FAMILY_SWITCH = "family_switch";
    public final static String PREFERENCE_KEY_CHILD_SWITCH = "child_switch";
    public final static String PREFERENCE_KEY_FAMILY_FORM = "family_form";
    public final static String PREFERENCE_KEY_CHILD_FORM = "child_form";

    public static Family family = new Family();
    public static  Child child = new Child();

    public static void initialize(Context context){

        Gson googleJson = new Gson();
        SharedPreferences pm = PreferenceManager.getDefaultSharedPreferences(context);
        final Family savedFamily = googleJson.fromJson(pm.getString(PREFERENCE_KEY_FAMILY_FORM, ""), Family.class);
        final Child savedChild = googleJson.fromJson(pm.getString(PREFERENCE_KEY_CHILD_FORM, ""), Child.class);

        if(savedFamily!= null) {
            family = savedFamily;
        }

        if(savedChild != null) {
            child = savedChild;
        }

        boolean famiycsActive  = pm.getBoolean(PREFERENCE_KEY_FAMILY_SWITCH, false);
        boolean childIsActive =  pm.getBoolean(PREFERENCE_KEY_CHILD_SWITCH, false);

        family.setActive(famiycsActive);
        child.setActive(childIsActive);

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        Gson googleJson = new Gson();
        String preferenceValue;

        final Family savedFamily = googleJson.fromJson(sharedPreferences.getString(PREFERENCE_KEY_FAMILY_FORM, ""), Family.class);
        final Child savedChild = googleJson.fromJson(sharedPreferences.getString(PREFERENCE_KEY_CHILD_FORM, ""), Child.class);

        Preference preference = this.generalPreferenceFragment.findPreference(key);

        if (savedFamily != null && !family.equals(savedFamily) && preference.getKey().equals(PREFERENCE_KEY_FAMILY_FORM)) {

            if(!family.getChildrenPassword().equals(savedFamily.getChildrenPassword())){
                HttpClient.setChildrenPassword(family.getName(), family.getPassword(), family.getChildrenPassword(), this, null);
            }

            family = savedFamily;
            preferenceValue = family.getName();
            HttpClient.getFamilyByName(family.getName(), family.getPassword(), this, this);
            preference.setSummary(preferenceValue);
        }
        else if (preference.getKey().equals(PREFERENCE_KEY_FAMILY_SWITCH)) {
            SwitchPreference switchFamily = (SwitchPreference) preference;

            if(family.isActive() != switchFamily.isChecked()) {
                HttpClient.setFamilyStatus(family.getName(), family.getPassword(), switchFamily.isChecked(), this, this);
            }
        }
        else if (savedChild != null && !child.equals(savedChild) && preference.getKey().equals(PREFERENCE_KEY_CHILD_FORM)) {
            child = savedChild;
            preferenceValue = child.getName();
            HttpClient.getChild(child.getFamilyName(), child.getName(), child.getPassword(), this, this);
            preference.setSummary(preferenceValue);
        }
        else if (preference.getKey().equals(PREFERENCE_KEY_CHILD_SWITCH)) {
            SwitchPreference switchChild = (SwitchPreference) preference;
            if(child.isActive() != switchChild.isChecked()) {
                HttpClient.setChildStatus(child.getFamilyName(), child.getName(), child.getPassword(), switchChild.isChecked(), this, this);
            }
        }
    }

    @Override
    public void onResponse(Object response) {

        SwitchPreference switchFamily = (SwitchPreference) this.generalPreferenceFragment.findPreference(PREFERENCE_KEY_FAMILY_SWITCH);
        family.setActive(switchFamily.isChecked());
        if(response instanceof Family){

            if(!family.getName().isEmpty()) {
                if (family.isActive()) {
                    FirebaseMessaging.getInstance().subscribeToTopic(family.getName());
                } else {
                    FirebaseMessaging.getInstance().unsubscribeFromTopic(family.getName());
                }
            }
        }

        SwitchPreference switchChild = (SwitchPreference) this.generalPreferenceFragment.findPreference(PREFERENCE_KEY_CHILD_SWITCH);
        child.setActive(switchChild.isChecked());
    }


    @Override
    public void onErrorResponse(VolleyError error) {

        if(error.networkResponse != null){
            for (com.android.volley.Header header: error.networkResponse.allHeaders) {
                if(header.getName().equals("Classname")) {

                    if(header.getValue().equals(Family.class.getSimpleName())){
                        SwitchPreference switchFamily = (SwitchPreference) this.generalPreferenceFragment.findPreference(PREFERENCE_KEY_FAMILY_SWITCH);
                        switchFamily.setChecked(false);
                        if(!family.getName().isEmpty()) {
                            FirebaseMessaging.getInstance().unsubscribeFromTopic(family.getName());
                        }
                    }
                    else if(header.getValue().equals(Child.class.getSimpleName())){
                        SwitchPreference switchChild = (SwitchPreference) this.generalPreferenceFragment.findPreference(PREFERENCE_KEY_CHILD_SWITCH);
                        switchChild.setChecked(false);
                    }

                }
            }
        }

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.error_title);
        builder.setPositiveButton(R.string.ok_button, null);
        builder.setMessage(HttpClient.getErrorMessage(error));
        builder.create().show();
    }

    private void initializeComponents(){

        Preference preferenceFamily = this.generalPreferenceFragment.findPreference(PREFERENCE_KEY_FAMILY_FORM);
        preferenceFamily.setSummary(family.getName());

        Preference preferenceChild = this.generalPreferenceFragment.findPreference(PREFERENCE_KEY_CHILD_FORM);
        preferenceChild.setSummary(child.getName());


        SwitchPreference switchFamily = (SwitchPreference) this.generalPreferenceFragment.findPreference(PREFERENCE_KEY_FAMILY_SWITCH);
        switchFamily.setChecked(family.isActive());

        SwitchPreference switchChild = (SwitchPreference) this.generalPreferenceFragment.findPreference(PREFERENCE_KEY_CHILD_SWITCH);
        switchChild.setChecked(child.isActive());

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(this.generalPreferenceFragment == null){
            return;
        }

        initializeComponents();

        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
        onSharedPreferenceChanged(PreferenceManager.getDefaultSharedPreferences(this), PREFERENCE_KEY_FAMILY_FORM);
        onSharedPreferenceChanged(PreferenceManager.getDefaultSharedPreferences(this), PREFERENCE_KEY_FAMILY_SWITCH);
        onSharedPreferenceChanged(PreferenceManager.getDefaultSharedPreferences(this), PREFERENCE_KEY_CHILD_FORM);
        onSharedPreferenceChanged(PreferenceManager.getDefaultSharedPreferences(this), PREFERENCE_KEY_CHILD_SWITCH);
    }

    @Override
    protected void onPause() {
        super.onPause();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || GeneralPreferenceFragment.class.getName().equals(fragmentName);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);
            setHasOptionsMenu(true);
        }

        @Override
        public void onAttach(Context context) {
            super.onAttach(context);
            if (context instanceof SettingsActivity)
                ((SettingsActivity) context).setGeneralPreferenceFragment(this);
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            if (activity instanceof SettingsActivity)
                ((SettingsActivity) activity).setGeneralPreferenceFragment(this);
        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                NavUtils.navigateUpFromSameTask(getActivity());
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    private void setGeneralPreferenceFragment(GeneralPreferenceFragment generalPreferenceFragment) {
        this.generalPreferenceFragment = generalPreferenceFragment;
    }

}
