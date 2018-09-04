package graziano.g.accessmonitoring.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import graziano.g.accessmonitoring.data.DataUtils;
import graziano.g.accessmonitoring.fragment.AlertConfigurationDialogFragment;
import graziano.g.accessmonitoring.fragment.ChildMapViewFragment;
import graziano.g.accessmonitoring.fragment.FamilyFragment;
import graziano.g.accessmonitoring.broadcast.receiver.UserPresentBroadcastReceiver;
import graziano.g.accessmonitoring.network.HttpClient;
import graziano.g.accessmonitoring.R;
import graziano.g.accessmonitoring.model.Child;
import graziano.g.accessmonitoring.model.Family;
import graziano.g.accessmonitoring.service.ChildService;

public class MainActivity extends AppCompatActivity implements Response.Listener, Response.ErrorListener, FamilyFragment.OnListFragmentInteractionListener {

    private UserPresentBroadcastReceiver userPresentBroadcastReceiver =  new UserPresentBroadcastReceiver();
    private  Intent childServiceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //initialize http client and settings
        HttpClient.initialize(getApplicationContext());
        SettingsActivity.initialize(getApplicationContext());

        IntentFilter userPresentIntentFilter = new IntentFilter(Intent.ACTION_USER_PRESENT);
       // IntentFilter mTime = new IntentFilter(Intent.ACTION_TIME_TICK);
        registerReceiver(userPresentBroadcastReceiver, userPresentIntentFilter);

        //Getting family service
        Family family = SettingsActivity.family;
        HttpClient.getFamilyByName(family.getName(), family.getPassword(), this, this);

        //action button for child view
        FloatingActionButton actionButtonChildMode = findViewById(R.id.actiln_button_child_mode);
        actionButtonChildMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Child chid = SettingsActivity.child;
                HttpClient.getChild(chid.getFamilyName(), chid.getName(), chid.getPassword(), MainActivity.this, MainActivity.this);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            intent.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT, SettingsActivity.GeneralPreferenceFragment.class.getName());
            intent.putExtra(PreferenceActivity.EXTRA_NO_HEADERS, true);
            this.startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(userPresentBroadcastReceiver);
        stopService(childServiceIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (SettingsActivity.child.isActive() && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            DataUtils.displayPromptForEnablingGPS(this);
        }
        else {

            if(SettingsActivity.child.isActive() && childServiceIntent == null) {
                //child location update service
                childServiceIntent = new Intent(this, ChildService.class);
                startService(childServiceIntent);
            }

        }
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        if(fragment instanceof ChildMapViewFragment){
            FloatingActionButton actionButtonChildMode = findViewById(R.id.actiln_button_child_mode);
            actionButtonChildMode.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        FloatingActionButton actionButtonChildMode = findViewById(R.id.actiln_button_child_mode);
        actionButtonChildMode.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onPause() {
        super.onPause();

        FloatingActionButton actionButtonChildMode = findViewById(R.id.actiln_button_child_mode);
        actionButtonChildMode.setVisibility(View.GONE);
    }

    @Override
    public void onResponse(Object response) {

        if(response instanceof Child){
            Child child = (Child) response;
            this.onListFragmentInteraction(child);
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        DialogFragment alertConfigurationDialogFragment = new AlertConfigurationDialogFragment();
        alertConfigurationDialogFragment.show(getSupportFragmentManager(), "");
    }

    @Override
    public void onListFragmentInteraction(Child child) {

        if(child!= null && child.getSessions() != null && !child.getSessions().isEmpty()) {

            FragmentManager fragmentManager = getSupportFragmentManager();
            if(fragmentManager.getFragments().size() == 1) {
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Fragment childMapViewFragment = ChildMapViewFragment.newInstance(child);
                fragmentTransaction.add(R.id.activity_main, childMapViewFragment);
                fragmentTransaction.addToBackStack(childMapViewFragment.toString());
                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                fragmentTransaction.commit();
            }
        }
        else {
            final AlertDialog.Builder builder =  new AlertDialog.Builder(this);
            builder.setMessage(getString(R.string.no_session_available) + " " + child.getName())
                    .setPositiveButton(R.string.ok_button, null);
            builder.create().show();
        }
    }
}
