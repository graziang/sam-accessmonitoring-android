package graziano.g.accessmonitoring.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import graziano.g.accessmonitoring.R;
import graziano.g.accessmonitoring.activity.SettingsActivity;

public class AlertConfigurationDialogFragment extends DialogFragment {

    public AlertConfigurationDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_alert_configuration_dialog, container, false);

        Button buttonSettings = (Button)v.findViewById(R.id.button_settings);
        buttonSettings.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getDialog().dismiss();
                Intent intent = new Intent(getContext(), SettingsActivity.class);
                intent.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT, SettingsActivity.GeneralPreferenceFragment.class.getName());
                intent.putExtra(PreferenceActivity.EXTRA_NO_HEADERS, true);
                startActivity(intent);

            }
        });

        return v;
    }
}
