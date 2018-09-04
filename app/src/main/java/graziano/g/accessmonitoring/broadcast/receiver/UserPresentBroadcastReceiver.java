package graziano.g.accessmonitoring.broadcast.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import graziano.g.accessmonitoring.activity.SettingsActivity;
import graziano.g.accessmonitoring.model.Child;
import graziano.g.accessmonitoring.model.Family;
import graziano.g.accessmonitoring.model.Session;
import graziano.g.accessmonitoring.network.HttpClient;
import graziano.g.accessmonitoring.service.ChildService;

//update child position
public class UserPresentBroadcastReceiver extends BroadcastReceiver implements Response.Listener, Response.ErrorListener {

    @Override
    public void onReceive(Context context, Intent intent) {

        Location location = ChildService.lastLocation;
        String addressString = ChildService.lastLocationAddressString;
        Session session = new Session();

        if(location != null) {
            Log.d(UserPresentBroadcastReceiver.class.getName(), location.toString());
            session.setLatitude(location.getLatitude());
            session.setLongitude(location.getLongitude());
            session.setAddressString(addressString);
        }

        Child child = SettingsActivity.child;
        if(child.isActive()) {

            HttpClient.createSesison(child.getFamilyName(), child.getName(), session, this, this);
        }
    }

    @Override
    public void onResponse(Object response) {
        //Log.d("RECIVER","");
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        //Log.e("RECIVERR","ERROR");
    }
}