package com.example.harpreet.visitorguide;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.example.harpreet.visitorguide.UtilsFolder.BitmapUtils;
import com.example.harpreet.visitorguide.UtilsFolder.GPStracker;
import com.example.harpreet.visitorguide.UtilsFolder.PointsData;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import eu.livotov.labs.android.camview.CameraLiveView;
import eu.livotov.labs.android.camview.camera.PictureProcessingCallback;

public class Camera extends AppCompatActivity implements SensorEventListener {

    private ImageView image;
    GPStracker gps;
    Location l;
    ProgressDialog dialog;
    private float currentDegree = 0f;
    private SensorManager mSensorManager;
    CameraLiveView cameraLiveView;
    PictureProcessingCallback callback;
    private ObjectMapper mapper;
    private ListView li;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        cameraLiveView = findViewById(R.id.cam_camera);
        cameraLiveView.startCamera();
        li=findViewById(R.id.placeList);
        //For compass setting
        image =  findViewById(R.id.compass_fix);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        //For taking picture callback function
        callback = new PictureProcessingCallback() {
            @Override
            public void onShutterTriggered() {
//                Toast.makeText(MainActivity.this,"A",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onRawPictureTaken(byte[] rawData) {
//                Toast.makeText(MainActivity.this,"B",Toast.LENGTH_LONG).show();cameraLiveView.resumeDisplay();
            }

            @Override
            public void onPictureTaken(byte[] jpegData) {
                Bitmap img = BitmapUtils.convertCompressedByteArrayToBitmap(jpegData);

                cameraLiveView.startCamera();
            }
        };

    }



    public void SearchPlace(View view) {
        if(cameraLiveView.getController()!=null)
        cameraLiveView.getController().takePicture(callback);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        float degree = Math.round(event.values[0]);
        RotateAnimation ra = new RotateAnimation(

                currentDegree,degree,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f);
        ra.setDuration(210);
        ra.setFillAfter(true);
        image.startAnimation(ra);
        currentDegree = -degree;
        getList();
    }

    private void getList() {

        final JSONArray sortedJsonArray = new JSONArray();

        final List<JSONObject> jsonValues = new ArrayList<JSONObject>();
        AndroidNetworking.get("https://us-central1-monuments-5eabc.cloudfunctions.net/app/dummy")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {

                        Log.d("response", String.valueOf(response));

                        for (int i = 0; i < response.length(); i++) {
                            try {
                                jsonValues.add(response.getJSONObject(i));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        Collections.sort( jsonValues, new Comparator<JSONObject>() {
                            @Override
                            public int compare(JSONObject a, JSONObject b) {
                                Double valA = new Double(0);
                                Double valB = new Double(0);
                                try {
                                    valA =  a.getDouble("distance");
                                    valB =  b.getDouble("distance");
                                }
                                catch (JSONException e) {
                                    //do something
                                }

                                return valA.compareTo(valB);
                                //if you want to change the sort order, simply use the following:
                                //return -valA.compareTo(valB);
                            }
                        });

                        for (int i = 0; i < response.length(); i++) {
                            sortedJsonArray.put(jsonValues.get(i));
                        }
                        Log.d("list1", jsonValues.toString());
                        int maxItemsToDisplay;
                        if(sortedJsonArray.length() > 3)
                            maxItemsToDisplay = 3;
                        else
                            maxItemsToDisplay =sortedJsonArray.length() ;
                        final ArrayList<String> list = new ArrayList<String>();
                        JSONObject jo=new JSONObject();
                        for(int i=0;i<=maxItemsToDisplay;i++)
                        {
                            try {
                                jo = sortedJsonArray.getJSONObject(i);
                                Log.d("nitj", String.valueOf(jo));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            try {
                                Log.d("nitj", String.valueOf(jo.getDouble("degree")));
                                if (jo.getDouble("degree")>=currentDegree-20 && jo.getDouble("degree")<=currentDegree+20)
                                {
                                    list.add(jo.getString("name"));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        Log.d("list", list.toString());
                        display(list);
                    }
                    @Override
                    public void onError(ANError error) {
                        Toast.makeText(Camera.this, "Dinesh:"+error.toString(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void display(ArrayList<String> list) {
        ArrayAdapter adapter = new ArrayAdapter<String>(this,
                R.layout.place_list_items,
                list);
        li.setAdapter(adapter);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraLiveView.startCamera();
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
        SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    void addSpots(PointsData data[]){

        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.direction);
        Canvas canvas = new Canvas();
        Bitmap copy = bm.copy(bm.getConfig(),true);
        int a = copy.getWidth();
        int b = copy.getHeight();
        canvas.setBitmap(copy);
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.YELLOW);
        for(int i=0;i<data.length;i++){
            float x = (float) (a/2 + (data[i].getDistance())*Math.sin(Math.toRadians(data[i].getDegree()))*((0.303*a)/(double)1500));
            float y = (float) (b/2 - (data[i].getDistance())*Math.cos(Math.toRadians(data[i].getDegree()))*((0.303*a)/(double)1500));
            canvas.drawCircle(x,y,25,paint);
        }
        image.setImageBitmap(copy);
    }

    @Override
    protected void onStart() {
        super.onStart();
        gps = new GPStracker(this);
        l = gps.getLocation();
        final ObjectMapper mapper = new ObjectMapper();

        if(gps!=null) {
            dialog = ProgressDialog.show(this, "Hi User",
                    "Loading. Please wait...", true);
            l = gps.getLocation();
            String key = getIntent().getStringExtra("key");
            AndroidNetworking.get("https://us-central1-monuments-5eabc.cloudfunctions.net/app/dummy")
                    .setPriority(Priority.MEDIUM)
                    .addQueryParameter("key",key)
                    .addQueryParameter("lat",l.getLatitude()+"")
                    .addQueryParameter("long",l.getLongitude()+"")
                    .build()
                    .getAsJSONArray(new JSONArrayRequestListener() {

                        @Override
                        public void onResponse(JSONArray response) {

                            String json = response.toString();
                            try {

                                // 1. convert JSON array to Array objects
                                PointsData[] data = mapper.readValue(json, PointsData[].class);
                                addSpots(data);

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            dialog.dismiss();
                        }

                        @Override
                        public void onError(ANError anError) {
                        Toast.makeText(Camera.this, "Sorry some error occurred", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    });
        }
        else
        {
            Toast.makeText(this, "Unable to fetch location", Toast.LENGTH_SHORT).show();
            finish();
        }
    }


}
