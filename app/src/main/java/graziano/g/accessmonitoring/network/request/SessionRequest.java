package graziano.g.accessmonitoring.network.request;

import android.support.annotation.NonNull;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import graziano.g.accessmonitoring.model.Session;

public class SessionRequest extends BasicAuthRequest<List<Session>> {

    private Gson gson = new Gson();

    public SessionRequest(int method, String url, String requestBody, Listener listener, ErrorListener errorListener, String username, String password) {
        super(method, url, requestBody, listener, errorListener, username, password);
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map headers = super.getHeaders();
        headers.put("className", Session.class.getName());
        return headers;
    }

    @Override
    protected Response<List<Session>> parseNetworkResponse(NetworkResponse response) {

        try {
            String jsonString = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));

            List<Session> session = gson.fromJson(jsonString, List.class);

            return Response.success(session,
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        }
    }

    @Override
    public int compareTo(@NonNull Request<List<Session>> o) {
        return super.compareTo((Request) o);
    }
}