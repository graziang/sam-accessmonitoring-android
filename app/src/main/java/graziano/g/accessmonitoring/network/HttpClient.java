package graziano.g.accessmonitoring.network;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.List;

import graziano.g.accessmonitoring.R;
import graziano.g.accessmonitoring.model.Child;
import graziano.g.accessmonitoring.model.Family;
import graziano.g.accessmonitoring.model.Session;
import graziano.g.accessmonitoring.network.request.ChildRequest;
import graziano.g.accessmonitoring.network.request.FamilyRequest;
import graziano.g.accessmonitoring.network.request.SessionRequest;

public class HttpClient {

    protected static final String PROTOCOL_CHARSET = "utf-8";

    private static RequestQueue queue;
    private static Gson gson;

    private static String URL_SERVER;
    private static String USERNAME;
    private static String PASSWORD;

    private static  String PLACEHOLDER_CHILD_NAME = "${child_name}";
    private static  String PLACEHOLDER_PASSWORD = "${password}";
    private static String PLACEHOLDER_FAMILY_NAME = "${family_name}";
    private static String PLACEHOLDER_CHILD_ID = "${child_id}";
    private static String PLACEHOLDER_ACTIVE = "${active}";
    private static String PLACEHOLDER_CHILDREN_PASSORD = "${new_children_password}";
    private static String PLACEHOLDER_ERROR = "${error}";

    private static String ERROR_PARSING_MESSAGE;
    private static String ERROR_BAD_RESPONSE;

    private static String ENDPOINT_GET_FAMILY = "/family?family_name=${family_name}&password=${password}";
    private static String ENDPOINT_POST_FAMILY = "/family";

    private static String ENDPOINT_GET_CHILD = "/child?family_name=${family_name}&child_name=${child_name}&password=${password}";
    private static String ENDPOINT_POST_CHILD = "/child?family_name=${family_name}&password=${password}";

    private static String ENDPOINT_PUT_FAMILY_STATUS = "/active_family?family_name=${family_name}&password=${password}&active=${active}";
    private static String ENDPOINT_PUT_CHILD_STATUS = "/active_child?family_name=${family_name}&child_name=${child_name}&password=${password}&active=${active}";


    private static String ENDPOINT_PUT_CHILDREN_PASSWORD = "/update_children_password?family_name=${family_name}&password=${password}&new_children_password=${new_children_password}";

    private static String ENDPOINT_GET_SESSIONS = "/session?child_id=${child_id}";
    private static String ENDPOINT_POST_SESSION = "/session?family_name=${family_name}&child_name=${child_name}";

    public static void initialize(Context context){
        queue = Volley.newRequestQueue(context);
        gson = new Gson();
        USERNAME = context.getString(R.string.server_username);
        PASSWORD = context.getString(R.string.server_password);
        URL_SERVER = context.getString(R.string.server_url);
        ERROR_PARSING_MESSAGE = context.getString(R.string.http_error_parse);
        ERROR_BAD_RESPONSE = context.getString(R.string.http_error_bad_response);
    }

    public static void getFamilyByName(String family_name, String password, Response.Listener<Family> listener, Response.ErrorListener errorListener){

        String url = URL_SERVER + ENDPOINT_GET_FAMILY;
        url = url.replace(PLACEHOLDER_FAMILY_NAME, family_name);
        url = url.replace(PLACEHOLDER_PASSWORD, password);

        FamilyRequest familyRequest = new FamilyRequest(Request.Method.GET, url, null, listener, errorListener ,USERNAME, PASSWORD);
        queue.add(familyRequest);
    }

    public static void createFamily(Family family, Response.Listener<Family> listener, Response.ErrorListener errorListener){

        String url = URL_SERVER + ENDPOINT_POST_FAMILY;
        FamilyRequest familyRequest = new FamilyRequest(Request.Method.POST, url, gson.toJson(family), listener, errorListener ,USERNAME, PASSWORD);
        queue.add(familyRequest);
    }

    public static void getChild(String family_name, String child_name, String password, Response.Listener<Child> listener, Response.ErrorListener errorListener){

        String url = URL_SERVER + ENDPOINT_GET_CHILD;
        url = url.replace(PLACEHOLDER_FAMILY_NAME, family_name);
        url = url.replace(PLACEHOLDER_CHILD_NAME, child_name);
        url = url.replace(PLACEHOLDER_PASSWORD, password);

        ChildRequest childRequest = new ChildRequest(Request.Method.GET, url, null, listener, errorListener ,USERNAME, PASSWORD);
        queue.add(childRequest);
    }

    public static void createChild(String family_name, String password, Child child, Response.Listener<Child> listener, Response.ErrorListener errorListener){

        String url = URL_SERVER + ENDPOINT_POST_CHILD;
        url = url.replace(PLACEHOLDER_FAMILY_NAME, family_name);
        url = url.replace(PLACEHOLDER_PASSWORD, password);

        ChildRequest childRequest = new ChildRequest(Request.Method.POST, url,  gson.toJson(child), listener, errorListener ,USERNAME, PASSWORD);
        queue.add(childRequest);
    }

    public static void getSessions(String child_id, Response.Listener<List<SessionRequest>> listener, Response.ErrorListener errorListener){

        String url = URL_SERVER + ENDPOINT_GET_SESSIONS;
        url = url.replace(PLACEHOLDER_CHILD_ID, child_id);

        SessionRequest sessionRequest = new SessionRequest(Request.Method.GET, url, null, listener, errorListener ,USERNAME, PASSWORD);
        queue.add(sessionRequest);
    }

    public static void createSesison(String family_name, String child_name, Session session, Response.Listener<List<SessionRequest>> listener, Response.ErrorListener errorListener){

        String url = URL_SERVER + ENDPOINT_POST_SESSION;
        url = url.replace(PLACEHOLDER_FAMILY_NAME, family_name);
        url = url.replace(PLACEHOLDER_CHILD_NAME, child_name);

        SessionRequest sessionRequest = new SessionRequest(Request.Method.POST, url, gson.toJson(session), listener, errorListener ,USERNAME, PASSWORD);
        queue.add(sessionRequest);
    }

    public static void setFamilyStatus(String family_name, String password, boolean active, Response.Listener<Child> listener, Response.ErrorListener errorListener){

        String url = URL_SERVER + ENDPOINT_PUT_FAMILY_STATUS;
        url = url.replace(PLACEHOLDER_FAMILY_NAME, family_name);
        url = url.replace(PLACEHOLDER_PASSWORD, password);
        url = url.replace(PLACEHOLDER_ACTIVE, String.valueOf(active));

        FamilyRequest familyRequest = new FamilyRequest(Request.Method.PUT, url, null, listener, errorListener ,USERNAME, PASSWORD);
        queue.add(familyRequest);
    }

    public static void setChildStatus(String family_name, String child_name, String password, boolean active, Response.Listener<Child> listener, Response.ErrorListener errorListener){

        String url = URL_SERVER + ENDPOINT_PUT_CHILD_STATUS;
        url = url.replace(PLACEHOLDER_FAMILY_NAME, family_name);
        url = url.replace(PLACEHOLDER_CHILD_NAME, child_name);
        url = url.replace(PLACEHOLDER_PASSWORD, password);
        url = url.replace(PLACEHOLDER_ACTIVE, String.valueOf(active));

        ChildRequest childRequest = new ChildRequest(Request.Method.PUT, url,  null, listener, errorListener ,USERNAME, PASSWORD);
        queue.add(childRequest);
    }

    public static void setChildrenPassword(String family_name, String password, String newPassword, Response.Listener<Child> listener, Response.ErrorListener errorListener){

        String url = URL_SERVER + ENDPOINT_PUT_CHILDREN_PASSWORD;
        url = url.replace(PLACEHOLDER_FAMILY_NAME, family_name);
        url = url.replace(PLACEHOLDER_PASSWORD, password);
        url = url.replace(PLACEHOLDER_CHILDREN_PASSORD, newPassword);

        FamilyRequest familyRequest = new FamilyRequest(Request.Method.PUT, url, null, listener, errorListener ,USERNAME, PASSWORD);
        queue.add(familyRequest);
    }

    public static String getErrorMessage(VolleyError error){

        if(error != null && error.networkResponse != null && error.networkResponse.statusCode == 400) {

            String jsonString = null;
            try {
                jsonString = new String(error.networkResponse.data, HttpHeaderParser.parseCharset(error.networkResponse.headers, PROTOCOL_CHARSET));
                JSONObject jsonObject = new JSONObject(jsonString);

                if(!jsonObject.isNull("error")){
                    return jsonObject.getString("error");
                }

            } catch (Exception e) {
                return ERROR_PARSING_MESSAGE;
            }
        }
        return   ERROR_BAD_RESPONSE.replace(PLACEHOLDER_ERROR, error.getMessage());
    }

}
