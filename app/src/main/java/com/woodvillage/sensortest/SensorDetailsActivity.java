package com.woodvillage.sensortest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.gson.Gson;

public class SensorDetailsActivity extends Activity implements SensorEventListener {

    private Sensor selectedSensor;
    private SensorManager sensorManager;

    private float[] values;

    private TextView sensorValueTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_details);

        this.sensorValueTextView = (TextView)findViewById(R.id.sensor_value);

        this.sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);

        Gson gson = new Gson();
        String json = getIntent().getStringExtra(Globals.id.SENSOR_DESCRIPTION.name());
        this.selectedSensor = gson.fromJson(json, Sensor.class);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        this.values = event.values;
        this.sensorValueTextView.setText(Float.toString(this.values[0]));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        this.sensorManager.registerListener(this,
                this.selectedSensor,
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.sensorManager.unregisterListener(this);
    }
}
