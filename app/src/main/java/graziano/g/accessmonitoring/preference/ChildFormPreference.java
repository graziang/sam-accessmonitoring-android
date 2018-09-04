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
import graziano.g.accessmonitoring.model.Child;

public class ChildFormPreference extends DialogPreference implements View.OnClickListener {

    private Gson gson = new Gson();
    private EditText familyNameEditText;
    private EditText childNameEditText;
    private EditText passwordNameEditText;

    public ChildFormPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setDialogLayoutResource(R.layout.dialog_preference_child);
    }

    public ChildFormPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDialogLayoutResource(R.layout.dialog_preference_child);
    }

    @Override
    protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {
        super.onPrepareDialogBuilder(builder);
        builder.setNeutralButton(R.string.create_button, null);
    }

    @Override
    public void onClick(View v) {

        final Child child = getChild();
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        HttpClient.createChild(child.getFamilyName(), child.getPassword(), child, new Response.Listener<Child>() {
                    @Override
                    public void onResponse(Child response) {
                        builder.setTitle(R.string.done_message);
                        String message = builder.getContext().getString(R.string.child) + " " + child.getName() + " " + builder.getContext().getString(R.string.success_create);
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

        familyNameEditText = (EditText) view.findViewById(R.id.preference_family_name);
        childNameEditText = (EditText) view.findViewById(R.id.preference_child_name);
        passwordNameEditText = (EditText) view.findViewById(R.id.preference_children_password);
        String persistenceJson = getPersistedString("{}");

        Child child = gson.fromJson(persistenceJson, Child.class);

        if(child.getFamilyName() != null){
            familyNameEditText.setText(child.getFamilyName());
        }
        if(child.getName() != null){
            childNameEditText.setText(child.getName());
        }
        if(child.getPassword() != null){
            passwordNameEditText.setText(child.getPassword());
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

            Child child = this.getChild();
            child.setFamilyName(this.familyNameEditText.getText().toString());
            child.setName(this.childNameEditText.getText().toString());
            child.setPassword(this.passwordNameEditText.getText().toString());
            persistString(gson.toJson(child));
        }
    }
    private Child getChild(){

        Child child = new Child();
        child.setFamilyName(this.familyNameEditText.getText().toString());
        child.setName(this.childNameEditText.getText().toString());
        child.setPassword(this.passwordNameEditText.getText().toString());
        return child;
    }
}