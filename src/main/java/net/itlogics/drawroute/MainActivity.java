package net.itlogics.drawroute;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import net.itlogics.drawroute.remote.RemoteRequest;
import net.itlogics.drawroute.utils.directionhelpers.FetchURL;
import net.itlogics.drawroute.utils.directionhelpers.TaskLoadedCallback;

import org.json.JSONException;
import org.json.JSONObject;

import static net.itlogics.drawroute.remote.RemoteRequest.makeRequest;
import static net.itlogics.drawroute.remote.RemoteRequest.responseError;
import static net.itlogics.drawroute.utils.Common.AlertTitles.mError;
import static net.itlogics.drawroute.utils.Common.HandlerTimeout;
import static net.itlogics.drawroute.utils.Common.Messages.SomeThingWentWrong;
import static net.itlogics.drawroute.utils.Common.ShowErrorDialog;
import static net.itlogics.drawroute.utils.Common.countDownTimer;
import static net.itlogics.drawroute.utils.Common.countDownTimerFinished;
import static net.itlogics.drawroute.utils.Common.handler;
import static net.itlogics.drawroute.utils.Common.mContext;
import static net.itlogics.drawroute.utils.Common.pd;
import static net.itlogics.drawroute.utils.Common.runnable;
import static net.itlogics.drawroute.utils.Common.startCountDown;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        OnMapReadyCallback, TaskLoadedCallback {

    private Button butnGet, butnSend;
    private TextView tvResponse;
    private double i = 0;


    private GoogleMap mMap;
    private MarkerOptions place1, place2;
    Button getDirection;
    private Polyline currentPolyline;
    private MapFragment fragMap;
    private String mapUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        butnGet = (Button) ((Activity) mContext).findViewById(R.id.butnGet);
        butnGet.setOnClickListener(this);
        butnSend = (Button) ((Activity) mContext).findViewById(R.id.butnSend);
        butnSend.setOnClickListener(this);
        tvResponse = (TextView) ((Activity) mContext).findViewById(R.id.tvResponse);

        fragMap = (MapFragment) ((Activity) mContext).getFragmentManager()
                .findFragmentById(R.id.fragMap);
        fragMap.getMapAsync((OnMapReadyCallback) mContext);

//        SupportMapFragment mapFragment =
//                (SupportMapFragment) ((FragmentActivity) mContext).getSupportFragmentManager()
//                        .findFragmentById(R.id.fragMap);
//        mapFragment.getMapAsync((OnMapReadyCallback) mContext);

        place1 = new MarkerOptions().position(new LatLng(24.875027, 67.028863))
                .title("Soldier Bazar Police Station");

        place1 = new MarkerOptions().position(new LatLng(24.877967, 67.034128))
                .title("Fatimiyah Hospital");
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.butnGet:
                CallService();
                break;
            case R.id.butnSend:
                JSONObject jsonBody = new JSONObject();
                try {
                    jsonBody.put("cor", i + 1);
                    CallService(jsonBody);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }

    private void CallService() {
        try {
            RemoteRequest.result = "";
            pd = new ProgressDialog(mContext);
            pd.setTitle("Loading...");
            pd.setMessage("Please wait...");
            pd.setCancelable(false);
            pd.show();
            RemoteRequest.result = makeRequest(mContext);
            ServerResponse();
        } catch (Exception e) {

        }
    }

    private void CallService(JSONObject jsonBody) {
        try {
            RemoteRequest.result = "";
            pd = new ProgressDialog(mContext);
            pd.setTitle("Loading...");
            pd.setMessage("Please wait...");
            pd.setCancelable(false);
            pd.show();
            RemoteRequest.result = makeRequest(mContext, jsonBody);
            ServerResponse();
        } catch (Exception e) {

        }
    }

    private void ServerResponse() {
        startCountDown();
        handler = new Handler();
        runnable = new Runnable() {
            public void run() {
//                Log.d("Runnable","Handler is working");
                if (!RemoteRequest.result.equals("") || countDownTimerFinished) { // just remove call backs
                    handler.removeCallbacks(this);
                    countDownTimer.cancel();
                    countDownTimerFinished = true;
                    GetResponse(RemoteRequest.result);
                } else { // post again
                    handler.postDelayed(this, HandlerTimeout);
                }
            }
        };
        handler.removeCallbacks(runnable);
        handler.postDelayed(runnable, HandlerTimeout);
    }

    private void GetResponse(String result) {
        Log.d("result >>> ", "\" " + result + " \"");
        if (result != null) {
            try {
                JSONObject jsonObject = new JSONObject(result.trim());

                if (jsonObject.has("Success")) {
                    if (pd != null) {
                        pd.dismiss();
                        if (jsonObject.optString("Cor").length() > 0) {
                            tvResponse.setText(jsonObject.optString("Cor"));
                            new FetchURL(mContext).execute(getUrl(place1.getPosition(),
                                    place2.getPosition(), "driving"), "driving");
                        } else {
                            tvResponse.setText(jsonObject.optString("Success"));
                        }
                    }
                } else {
                    responseError = jsonObject.getJSONObject("GetLoginResult").opt("ErrorMessage").toString();
                    Log.d("Data", responseError);

                    if (pd != null) {
                        pd.dismiss();
                    }

                    ShowErrorDialog(mError, responseError, "OK", ((Activity) mContext));
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("JSONException 1:", e.toString());

                if (pd != null) {
                    pd.dismiss();
                }

                ShowErrorDialog(mError, SomeThingWentWrong, "OK", ((Activity) mContext));
            }

        }
        pd.dismiss();
    }

    private String getUrl(LatLng origin, LatLng dest, String directionMode) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Mode
        String mode = "mode=" + directionMode;
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?"
                + parameters + "&key=" + mContext.getString(R.string.google_maps_key);
        return url;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Log.d("mylog", "Added Markers");
        mMap.addMarker(place1);
        mMap.addMarker(place2);
    }

    @Override
    public void onTaskDone(Object... values) {
        if (currentPolyline != null)
            currentPolyline.remove();
        currentPolyline = mMap.addPolyline((PolylineOptions) values[0]);
    }
}




//package net.itlogics.drawroute;
//
//import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
//
//import net.itlogics.drawroute.interfaces.MainActivityResultCallbacks;
//
//public class MainActivity extends AppCompatActivity implements MainActivityResultCallbacks {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
////        Common.mContext = this;
////        ActivityMainBinding activityMainBinding = DataBindingUtil
////                .setContentView(this, R.layout.activity_main);
////        activityMainBinding.setViewModel(ViewModelProviders.of(
////                this,
////                new MainActivityViewModelFactory(this))
////                .get(MainActivityViewModel.class));
//    }
//
//    @Override
//    public void onSuccess(String message) {
//
//    }
//
//    @Override
//    public void onError(String message) {
//
//    }
//}
