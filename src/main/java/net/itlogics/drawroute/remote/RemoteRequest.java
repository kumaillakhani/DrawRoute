package net.itlogics.drawroute.remote;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class RemoteRequest {

    public static String
            result = "",
            responseError = "";

    public static class ConfigValues {

        private static String
                // DEV LOCAL IP
                URL = "http://192.168.8.101/testservice/index.php";

        public static Integer
                RequestTimeOut = 60000;

    }

    public static String makeRequest(Context mContext) {
        result = "";
        Log.i("makeRequest1", "");
        RequestQueue queue = Volley.newRequestQueue(mContext);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, ConfigValues.URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // your response
                        result = response.replace("\\\"", "\"");
                        result = result.replace("\"{", "{");
                        result = result.replace("}\"", "}");
                        Log.i("LOG_VOLLEY_SUCCESS", result);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error
                result = error.toString();
                Log.i("LOG_VOLLEY_ERROR", result);
            }
        });

        RetryPolicy policy = new DefaultRetryPolicy(ConfigValues.RequestTimeOut, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);

        // Add the request to the RequestQueue.
        queue.add(stringRequest);

        return result;
    }

    public static String makeRequest(Context mContext, JSONObject mRequestBody) {

        Log.i("makeRequest1", mRequestBody.toString());
//        try {
//            JSONObject objLoginProperties = (JSONObject) mRequestBody;
//
//            JSONObject objServiceCredentialProperties = new JSONObject();
//            objServiceCredentialProperties.put("ServiceUserName", ConfigValues.ServiceUserName);
//            objServiceCredentialProperties.put("ServiceUserPassword", ConfigValues.ServiceUserPassword);
//            objServiceCredentialProperties.put("Token", Common.ConfigValues.Token);
//
//            mRequestBody = new JSONObject();
//            mRequestBody.put("objLoginProperties", objLoginProperties);
//            mRequestBody.put("objServiceCredentialProperties",objServiceCredentialProperties);
//            Log.i("makeRequest4 >>> ",mRequestBody.toString());
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(mContext);

        // Request a string response from the provided URL.
        final JSONObject finalMRequestBody = mRequestBody;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ConfigValues.URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // your response
//                        Log.i("LOG_VOLLEY", response);
                        result = response.replace("\\\"", "\"");
                        result = result.replace("\"{", "{");
                        result = result.replace("}\"", "}");
                        Log.i("LOG_VOLLEY_SUCCESS", result);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error
                result = error.toString();
                Log.i("LOG_VOLLEY_ERROR", result);
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                String your_string_json = String.valueOf(finalMRequestBody); // put your json
                return your_string_json.getBytes();
            }
        };

        RetryPolicy policy = new DefaultRetryPolicy(ConfigValues.RequestTimeOut, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);

        // Add the request to the RequestQueue.
        queue.add(stringRequest);

        return result;
    }
}
