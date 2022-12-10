package com.example.navapp;

import android.graphics.Camera;
import android.graphics.Canvas;
import android.hardware.Sensor;
import android.hardware.SensorManager;
//import android.support.v7.app.NavApp;
import android.view.SurfaceView;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.hardware.SensorEvent;
import android.widget.ImageView;
import android.widget.TextView;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import java.lang.Math;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    final double pi = 3.1415926535;
    final double degToRad = pi / 180.0;

    private Camera mCamera;

    // device sensor manager
    private SensorManager SensorManage;

    // define the compass picture that will be use
    private ImageView compassimage;

    // define the pin image that will be used
    private ImageView pinImage;

    // record the angle turned of the compass picture
    private float DegreeStart = 0.0F;
    private float xStart = 0.0F, yStart = 0.0F;

    TextView DegreeTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //
        compassimage = (ImageView) findViewById(R.id.compass_image);

        pinImage = (ImageView) findViewById(R.id.pin_image);

        // TextView that will display the degree
        DegreeTV = (TextView) findViewById(R.id.DegreeTV);

        // initialize your android device sensor capabilities
        SensorManage = (SensorManager) getSystemService(SENSOR_SERVICE);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // to stop the listener and save battery
        SensorManage.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // code for system's orientation sensor registered listeners
        SensorManage.registerListener(this, SensorManage.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_GAME);
    }

    private float normalizePoint(float event_value, double point)
    {
        return event_value > point + 180 ? event_value - 360 : event_value;
    }


    @Override
    public void onSensorChanged(SensorEvent event) {

        double my_x = 0, my_y = 90;

        double theta = 0.0174533 * event.values[2];
        double roatated_x = (my_x * Math.cos(theta)) - (my_y * Math.sin(theta));
        double roatated_y = (my_x * Math.sin(theta)) + (my_y * Math.cos(theta));


        float normalized_event_x = normalizePoint(event.values[0], roatated_x);
        float normalized_event_y = normalizePoint(event.values[1], roatated_y);

        // get angle around the z-axis rotated
        float degree = Math.round(event.values[0]);
        float y = (float) (-15 * (roatated_y + normalized_event_y));
        float x = (float) (15 *  (roatated_x - normalized_event_x));

        // Setting text
        DegreeTV.setText("0: " + Float.toString(degree) + " degrees\n" +
                         "1: " + Math.round(event.values[1]) +"\n" +
                         "2: " + Math.round(event.values[2]));

        // rotation animation - reverse turn degree degrees
        RotateAnimation ra = new RotateAnimation(
                DegreeStart,
                -degree,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);

        // set the compass animation after the end of the reservation status
        ra.setFillAfter(true);

        // set how long the animation for the compass image will take place
        ra.setDuration(210);
        DegreeStart = -degree;

        // Start animation of compass image
        compassimage.startAnimation(ra);

        // Translation animation
        TranslateAnimation ta = new TranslateAnimation(xStart, x, yStart, y);

        // Start animation of pin image
        pinImage.startAnimation(ta);

        // set the compass animation after the end of the reservation status
        ta.setFillAfter(true);

        // set how long the animation for the compass image will take place
        ta.setDuration(210);

        xStart = x;
        yStart = y;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // not in use
    }
}