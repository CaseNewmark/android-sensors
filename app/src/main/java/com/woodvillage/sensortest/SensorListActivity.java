package com.woodvillage.sensortest;

import android.app.ActivityManager;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.woodvillage.sensortest.services.SensorService;

import java.util.List;

public class SensorListActivity extends AppCompatActivity {

    private ListView sensorListView;
    private SensorService sensorService;
    private Intent serviceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_list);

        this.sensorListView = (ListView)findViewById(R.id.listview_sensor_list);
        SensorManager sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        List<Sensor> sensorList = sensorManager.getSensorList(Sensor.TYPE_ALL);
        SensorListAdapter<Sensor> adapter = new SensorListAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                sensorList);
        this.sensorListView.setAdapter(adapter);
        this.sensorListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Gson gson = new Gson();
                Intent intent = new Intent(SensorListActivity.this, SensorDetailsActivity.class);
                Sensor selectedSensor = (Sensor) parent.getItemAtPosition(position);
                intent.putExtra(Globals.id.SENSOR_DESCRIPTION.name(), gson.toJson(selectedSensor));
                startActivity(intent);
            }
        });

        this.sensorService = new SensorService(this);
        this.serviceIntent = new Intent(this, this.sensorService.getClass());
        if (!isServiceRunning(this.sensorService.getClass())) {
            startService(this.serviceIntent);
        }
    }

    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("isMyServiceRunning?", true+"");
                return true;
            }
        }
        Log.i ("isMyServiceRunning?", false+"");
        return false;
    }

    private class SensorListAdapter<S> extends ArrayAdapter<Sensor> {

        private List<Sensor> sensors;

        public SensorListAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List sensors) {
            super(context, resource, sensors);

            this.sensors = sensors;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater li = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
            View sensorListViewItem = li.inflate(R.layout.view_sensor_info, null);

            Sensor sensor = this.sensors.get(position);
            if (sensor != null) {
                ((TextView)sensorListViewItem
                        .findViewById(R.id.textview_sensor_name))
                        .setText(sensor.getName());
                ((TextView)sensorListViewItem
                        .findViewById(R.id.textview_sensor_type))
                        .setText(sensor.getStringType()
                                + " - "
                                + Integer.toString(sensor.getType()));
                ((TextView)sensorListViewItem
                        .findViewById(R.id.textview_sensor_version))
                        .setText(Integer.toString(sensor.getVersion()));
                ((TextView)sensorListViewItem
                        .findViewById(R.id.textview_sensor_vendor))
                        .setText(sensor.getVendor());
                ((TextView)sensorListViewItem
                        .findViewById(R.id.textview_sensor_max_range))
                        .setText(Float.toString(sensor.getMaximumRange()));
                ((TextView)sensorListViewItem
                        .findViewById(R.id.textview_sensor_resolution))
                        .setText(Float.toString(sensor.getResolution()));
                ((TextView)sensorListViewItem
                        .findViewById(R.id.textview_sensor_power))
                        .setText(Float.toString(sensor.getPower()));
                ((TextView)sensorListViewItem
                        .findViewById(R.id.textview_sensor_min_delay))
                        .setText(Integer.toString(sensor.getMinDelay()));
            }

            return sensorListViewItem;
        }
    }

}
