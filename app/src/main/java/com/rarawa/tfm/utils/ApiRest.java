package com.rarawa.tfm.utils;

import android.content.Context;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.codesnippets4all.json.parsers.JSONParser;
import com.codesnippets4all.json.parsers.JsonParserFactory;
import com.rarawa.tfm.MainActivity;
import com.rarawa.tfm.sqlite.SqliteHandler;
import com.rarawa.tfm.sqlite.models.UserInfo;

import org.bson.Document;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.rarawa.tfm.utils.Constants.API_BASE_URL;
import static com.rarawa.tfm.utils.Constants.LOG_TAG;
import static com.rarawa.tfm.utils.Constants.UPDATE_DISPLAY_PATTERNS;
import static com.rarawa.tfm.utils.Constants.UPDATE_PATTERNS_PATIENT_URL;

public class ApiRest {

    final String contentType = "application/json; charset=utf-8";
    String url;
    Context context;
    RequestQueue requestQueue;
    String jsonresponse;

    private Map<String, String> header;

    public ApiRest(Context context, String path) {
        this.context = context;
        this.url = Constants.API_BASE_URL.concat(path);
        requestQueue = Volley.newRequestQueue(context);
        header = new HashMap<>();

    }

    public void addHeader(String key, String value) {
        header.put(key, value);
    }

    public void executeRequest(int method, final VolleyCallback callback) {

        StringRequest stringRequest = new StringRequest(method, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                jsonresponse = response;
                Log.e(Constants.LOG_TAG, " res::\n" + jsonresponse);

                JSONParser parser = new JSONParser();
                Map jsonMap = parser.parseJson(response);

                Document document= Document.parse(response);

                callback.getResponse(document);


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.e(Constants.LOG_TAG, error.getMessage());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                return header;
            }
        };
        requestQueue.add(stringRequest);

    }

    //Get pattern's updates from the server
    public static int registerPatient(final Context context, String registerToken){
        final int[] result = {1};

        SqliteHandler db = new SqliteHandler(context);

        //communicationToken
        final ApiRest jsonParserVolley = new ApiRest(context, Constants.SYNC_DEVICE_URL.concat(registerToken));
        jsonParserVolley.executeRequest(Request.Method.GET, new ApiRest.VolleyCallback() {
            @Override
            public void getResponse(Document response) {

                //Error
                if(response.containsKey("code") && Integer.parseInt(response.get("code").toString()) < 0){
                    result[0] = -1;
                } else {

                    db.insertUserInfo(response.get("name").toString(),
                            response.get("surname1").toString(),
                            response.get("surname2").toString(),
                            Integer.parseInt(response.get("age").toString()),
                            response.get("gender").toString(),
                            response.get("communicationToken").toString());

                    MainActivity.setDrawableRegister();

                    result[0] = 1;
                }
            }
        });

        return result[0];

    }

    //Get pattern's updates from the server
    public static int callUpdatePatternsPatient(final Context context){

        final int[] result = {1};

        SqliteHandler db = new SqliteHandler(context);
        UserInfo userInfo = db.getUserInfo();
        String communicationToken = userInfo.getCommunicationToken();

        //communicationToken
        final ApiRest jsonParserVolley = new ApiRest(context, UPDATE_PATTERNS_PATIENT_URL.concat(communicationToken));
        jsonParserVolley.executeRequest(Request.Method.GET, new ApiRest.VolleyCallback() {
            @Override
            public void getResponse(Document response) {
                //Error
                if(response.containsKey("code") && Integer.parseInt(response.get("code").toString()) < 0){
                    result[0] = -1;
                    Log.d(Constants.LOG_TAG, "callUpdatePatternsPatient: -1");
                } else {

                    SqliteHandler db = new SqliteHandler(context);

                    ArrayList patternsArrayList;

                    /* ADD and UPDATE elements */

                    String addUpdateKeys [] = {"add", "update"};

                    for(String key: addUpdateKeys){
                        if(response.containsKey(key)) {

                            patternsArrayList = (ArrayList)response.get(key);

                            for (int i=0; i < patternsArrayList.size(); i++){
                                int idPattern =
                                        Integer.parseInt((((Document)((ArrayList)response.get(key))
                                                .get(i)).get("idPattern")).toString());

                                //If the pattern already exists, delete it to overwrite it
                                if(db.patternExists(idPattern)) {
                                    db.deletePattern(idPattern);
                                }

                                //INSERT pattern

                                String patternName = (((Document)((ArrayList)response.get(key))
                                        .get(i)).get("name")).toString();
                                String patternIntensities = (((Document)((ArrayList)response.get(key))
                                        .get(i)).get("intensities")).toString();

                                db.insertPattern(idPattern, patternName, patternIntensities);
                            }
                        }
                    }

                    /* DELETE elements */

                    if(response.containsKey("delete")) {

                        patternsArrayList = (ArrayList)response.get("delete");

                        for (int i=0; i < patternsArrayList.size(); i++){
                            int idPattern =
                                    Integer.parseInt((((Document)((ArrayList)response.get("delete"))
                                            .get(i)).get("idPattern")).toString());

                            db.deletePattern(idPattern);
                        }
                    }
                }
            }
        });

        return result[0];

    }



    public static void callUpdateDisplayPatterns(Context context) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        SqliteHandler db = new SqliteHandler(context);
        String communicationToken = db.getUserInfo().getCommunicationToken();
        //String communicationToken = "LJ95273L2Y"; //DEBUG
        String url = API_BASE_URL.concat(UPDATE_DISPLAY_PATTERNS).concat(communicationToken);
        JSONObject episodes = db.getUnsyncedPatterns();
        JSONObject reasonAnger = db.getUnsyncedReasonAnger();
        JSONObject data = new JSONObject();

        try {
            data.put("displayPatterns", episodes);
            data.put("episodes", reasonAnger);
        } catch (JSONException e){
            Log.e(LOG_TAG, e.getStackTrace().toString());
        }


        JsonObjectRequest jsonobj = new JsonObjectRequest(Request.Method.POST, url,data,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(LOG_TAG,"callUpdateDisplayPatterns.onResponse()");
                        //TODO: read response
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //TODO: read response
                        Log.d(LOG_TAG,"callUpdateDisplayPatterns.onErrorResponse()");
                    }
                }
        );

        requestQueue.add(jsonobj);

    }


    public interface VolleyCallback {
        public void getResponse(Document response);
    }
}
