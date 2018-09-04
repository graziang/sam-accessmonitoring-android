package graziano.g.accessmonitoring.data;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;

import java.util.Date;

import graziano.g.accessmonitoring.R;

public class DataUtils {

    //Get string date miss
    public static String printDifference(Date startDate, Date endDate) {
        //milliseconds
        long different = endDate.getTime() - startDate.getTime();

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        //different = different % minutesInMilli;

        //for seconds
        // long elapsedSeconds = different / secondsInMilli;

        String dateDifferenceString = "";

        if(elapsedDays > 0){
            dateDifferenceString += elapsedDays + "d, ";
        }
        if(elapsedHours > 0){
            dateDifferenceString += elapsedHours + "h, ";
        }
        if(elapsedMinutes >= 0){
            dateDifferenceString += elapsedMinutes + "m ago";
        }
        return dateDifferenceString;
    }

    public static void displayPromptForEnablingGPS(final Activity activity)
    {

        final AlertDialog.Builder builder =  new AlertDialog.Builder(activity);
        final String action = Settings.ACTION_LOCATION_SOURCE_SETTINGS;
        final String message = activity.getString(R.string.location_request_message);

        builder.setMessage(message)
                .setPositiveButton(activity.getString(R.string.ok_button),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                                activity.startActivity(new Intent(action));
                                d.dismiss();
                            }
                        })
                .setNegativeButton(activity.getString(R.string.cancel_button),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                                d.cancel();
                            }
                        });
        builder.create().show();
    }
}
