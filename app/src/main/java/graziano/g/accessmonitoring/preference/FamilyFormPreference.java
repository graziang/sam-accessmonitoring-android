package graziano.g.accessmonitoring.preference;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;

import graziano.g.accessmonitoring.network.HttpClient;
import graziano.g.accessmonitoring.R;
import graziano.g.accessmonitoring.model.Family;

public class FamilyFormPreference extends DialogPreference implements View.OnClickListener {

    private Gson gson = new Gson();
    private EditText usernameEditText;
    private EditText passwordEditText;
    private EditText childrenPasswordEditText;

    public FamilyFormPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setDialogLayoutResource(R.layout.dialog_preference_family);
    }

    public FamilyFormPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDialogLayoutResource(R.layout.dialog_preference_family);
    }

    @Override
    protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {
        super.onPrepareDialogBuilder(builder);
        builder.setNeutralButton(R.string.create_button, null);

    }
    @Override
    public void onClick(View v) {

        final Family family = getFamily();
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        HttpClient.createFamily(family, new Response.Listener<Family>() {
                    @Override
                    public void onResponse(Family response) {
                        builder.setTitle(R.string.done_message);
                        String message = builder.getContext().getString(R.string.family) + " " + family.getName() + " " +builder.getContext().getString(R.string.success_create);

                        builder.setMessage(message);
                        builder.setPositiveButton(R.string.ok_button, null);
                        builder.create().show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        builder.setTitle(R.string.error_title);
                        builder.setMessage(HttpClient.getErrorMessage(error));
                        builder.setPositiveButton(R.string.ok_button, null);
                        builder.create().show();
                    }
                }
        );
    }

    @Override
    public void onBindDialogView(View view) {

        usernameEditText = (EditText) view.findViewById(R.id.preference_family_username);
        passwordEditText = (EditText) view.findViewById(R.id.preference_family_password);
        childrenPasswordEditText = (EditText) view.findViewById(R.id.preference_family_children_password);
        String persistenceJson = getPersistedString("{}");

        Family family = gson.fromJson(persistenceJson, Family.class);

        if(family.getName() != null){
            usernameEditText.setText(family.getName());
        }
        if(family.getPassword() != null){
            passwordEditText.setText(family.getPassword());
        }
        if(family.getChildrenPassword() != null){
            childrenPasswordEditText.setText(family.getChildrenPassword());
        }

        super.onBindDialogView(view);
    }

    @Override
    protected void showDialog(Bundle state) {
        super.showDialog(state);
        View neutralButton = ((AlertDialog) getDialog()).getButton(DialogInterface.BUTTON_NEUTRAL);
        neutralButton.setOnClickListener(this);
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return "";
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        return;
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (positiveResult) {
            Family family = this.getFamily();
            persistString(gson.toJson(family));
        }
    }

    private Family getFamily(){
        Family family = new Family();
        family.setName(this.usernameEditText.getText().toString());
        family.setPassword(this.passwordEditText.getText().toString());
        family.setChildrenPassword(this.childrenPasswordEditText.getText().toString());
        return family;

    }
}