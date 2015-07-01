package com.ibm.iot.android.iotstarter;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class SensorSelectorActivity extends Activity implements SensorEventListener{

    private TextView mTextView;
    private final static int SENS_ACCELEROMETER = Sensor.TYPE_ACCELEROMETER;
    private final static int SENS_STEP_COUNTER = Sensor.TYPE_STEP_COUNTER;

    SensorManager mSensorManager;
    Sensor accelerometerSensor;
    Sensor stepCounterSensor;
    private static final String TAG = "SensorDashboard/SensorService";
    private DeviceClient client;
    CheckBox accelerometer_checkbox;
    CheckBox step_checkbox;
    SharedPreferences sharedPreferences;
    boolean accel_isChecked;
    boolean step_isChecked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //sharedPreferences = PreferenceManager.getDefaultSharedPreferences(SensorSelectorActivity.this);
        sharedPreferences = this.getPreferences(MODE_PRIVATE);
        client = DeviceClient.getInstance(this);
        setContentView(R.layout.activity_select_sensor_activity);

        accelerometer_checkbox = (CheckBox) findViewById(R.id.checkBox_accelerometer);
        step_checkbox = (CheckBox) findViewById(R.id.checkBox_step_counter);

        accel_isChecked = getBooleanFromPreferences("accel_isChecked");
        step_isChecked = getBooleanFromPreferences("step_isChecked");
        //accel_isChecked = sharedPreferences.getBoolean("accel_isChecked", true);
        //step_isChecked = sharedPreferences.getBoolean("step_isChecked", true);
        accelerometer_checkbox.setChecked(accel_isChecked);
        step_checkbox.setChecked(step_isChecked);

        SensorSelectorActivity.this.putBooleanInPreferences(accel_isChecked, "step_isChecked");
        SensorSelectorActivity.this.putBooleanInPreferences(accel_isChecked, "accel_isChecked");

        step_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton view, boolean isChecked) {
                Log.i("boolean", "" + isChecked);
                SensorSelectorActivity.this.putBooleanInPreferences(isChecked, "step_isChecked");
            }
        });
        accelerometer_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton view, boolean isChecked) {
                Log.i("boolean", "" + isChecked);
                SensorSelectorActivity.this.putBooleanInPreferences(isChecked, "accel_isChecked");
            }
        });

    }
    @Override
    protected void onPause() {
        super.onPause();
        sharedPreferences = this.getPreferences(MODE_PRIVATE);

        accel_isChecked = getBooleanFromPreferences("accel_isChecked");
        step_isChecked = getBooleanFromPreferences("step_isChecked");
        SensorSelectorActivity.this.putBooleanInPreferences(accel_isChecked, "accel_isChecked");
        SensorSelectorActivity.this.putBooleanInPreferences(step_isChecked, "step_isChecked");
    }


    /*@Override
    protected void onResume() {
        super.onResume();
        sharedPreferences = this.getPreferences(MODE_PRIVATE);

        accel_isChecked = getBooleanFromPreferences("accel_isChecked");
        step_isChecked = getBooleanFromPreferences("step_isChecked");
        SensorSelectorActivity.this.putBooleanInPreferences(accel_isChecked, "accel_isChecked");
        SensorSelectorActivity.this.putBooleanInPreferences(step_isChecked, "step_isChecked");

        accelerometer_checkbox.setChecked(accel_isChecked);
        step_checkbox.setChecked(step_isChecked);
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        sharedPreferences = this.getPreferences(MODE_PRIVATE);

        accel_isChecked = getBooleanFromPreferences("accel_isChecked");
        step_isChecked = getBooleanFromPreferences("step_isChecked");

        accelerometer_checkbox.setChecked(accel_isChecked);
        step_checkbox.setChecked(step_isChecked);
    }*/

    public void putBooleanInPreferences(boolean isChecked,String key){
        sharedPreferences = this.getPreferences(Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, isChecked);
        editor.commit();
    }
    public boolean getBooleanFromPreferences(String key){
        sharedPreferences = this.getPreferences(Activity.MODE_PRIVATE);
        Boolean isChecked = sharedPreferences.getBoolean(key, false);
        return isChecked;
    }
    public void onCheckboxClicked(View view) {
        boolean checked = ((CheckBox) view).isChecked();
        mSensorManager = ((SensorManager) getSystemService(SENSOR_SERVICE));
        accelerometerSensor = mSensorManager.getDefaultSensor(SENS_ACCELEROMETER);
        stepCounterSensor = mSensorManager.getDefaultSensor(SENS_STEP_COUNTER);
        switch(view.getId()) {
            case R.id.checkBox_accelerometer:
                if (checked){
                    if (accelerometerSensor != null) {
                        mSensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
                        SensorSelectorActivity.this.putBooleanInPreferences(true, "accel_isChecked");
                    } else {
                        Log.w(TAG, "No Accelerometer found");
                    }
                }
                else if (!checked){
                    stopMeasurement(accelerometerSensor);
                    SensorSelectorActivity.this.putBooleanInPreferences(false, "accel_isChecked");
                }
                break;
            case R.id.checkBox_step_counter:
                if (checked){
                    if (stepCounterSensor != null) {
                        mSensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_NORMAL);
                        SensorSelectorActivity.this.putBooleanInPreferences(true, "step_isChecked");
                    } else {
                        Log.d(TAG, "No Step Counter Sensor found");
                    }
                }
                else if(!checked) {
                    stopMeasurement(stepCounterSensor);
                    SensorSelectorActivity.this.putBooleanInPreferences(false, "step_isChecked");
                }
                break;
            }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopMeasurement(accelerometerSensor);
        stopMeasurement(stepCounterSensor);
    }

    /*protected void startMeasurement() {
        mSensorManager = ((SensorManager) getSystemService(SENSOR_SERVICE));

        Sensor accelerometerSensor = mSensorManager.getDefaultSensor(SENS_ACCELEROMETER);
        Sensor stepCounterSensor = mSensorManager.getDefaultSensor(SENS_STEP_COUNTER);
        //Sensor stepDetectorSensor = mSensorManager.getDefaultSensor(SENS_STEP_DETECTOR);
        //mHeartrateSensor = mSensorManager.getDefaultSensor(SENS_HEARTRATE);

        /*
        Sensor ambientTemperatureSensor = mSensorManager.getDefaultSensor(SENS_AMBIENT_TEMPERATURE);
        Sensor gameRotationVectorSensor = mSensorManager.getDefaultSensor(SENS_GAME_ROTATION_VECTOR);
        Sensor geomagneticSensor = mSensorManager.getDefaultSensor(SENS_GEOMAGNETIC);
        Sensor gravitySensor = mSensorManager.getDefaultSensor(SENS_GRAVITY);
        Sensor gyroscopeSensor = mSensorManager.getDefaultSensor(SENS_GYROSCOPE);
        Sensor gyroscopeUncalibratedSensor = mSensorManager.getDefaultSensor(SENS_GYROSCOPE_UNCALIBRATED);
        mHeartrateSensor = mSensorManager.getDefaultSensor(SENS_HEARTRATE);
        Sensor heartrateSamsungSensor = mSensorManager.getDefaultSensor(65562);
        Sensor lightSensor = mSensorManager.getDefaultSensor(SENS_LIGHT);
        Sensor linearAccelerationSensor = mSensorManager.getDefaultSensor(SENS_LINEAR_ACCELERATION);
        Sensor magneticFieldSensor = mSensorManager.getDefaultSensor(SENS_MAGNETIC_FIELD);
        Sensor magneticFieldUncalibratedSensor = mSensorManager.getDefaultSensor(SENS_MAGNETIC_FIELD_UNCALIBRATED);
        Sensor pressureSensor = mSensorManager.getDefaultSensor(SENS_PRESSURE);
        Sensor proximitySensor = mSensorManager.getDefaultSensor(SENS_PROXIMITY);
        Sensor humiditySensor = mSensorManager.getDefaultSensor(SENS_HUMIDITY);
        Sensor rotationVectorSensor = mSensorManager.getDefaultSensor(SENS_ROTATION_VECTOR);
        Sensor significantMotionSensor = mSensorManager.getDefaultSensor(SENS_SIGNIFICANT_MOTION);
        Sensor stepCounterSensor = mSensorManager.getDefaultSensor(SENS_STEP_COUNTER);
        Sensor stepDetectorSensor = mSensorManager.getDefaultSensor(SENS_STEP_DETECTOR);



        // Register the listener
        /*if (mSensorManager != null) {
            if (accelerometerSensor != null) {
                mSensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
            } else {
                Log.w(TAG, "No Accelerometer found");
            }
            /*if (stepCounterSensor != null) {
                mSensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_NORMAL);
                //builder.setContentText(stepDetectorSensor.getName());
            } else {
                Log.d(TAG, "No Step Counter Sensor found");
            }*/

            /*
            if (ambientTemperatureSensor != null) {
                mSensorManager.registerListener(this, ambientTemperatureSensor, SensorManager.SENSOR_DELAY_NORMAL);
            } else {
                Log.w(TAG, "Ambient Temperature Sensor not found");
            }

            if (gameRotationVectorSensor != null) {
                mSensorManager.registerListener(this, gameRotationVectorSensor, SensorManager.SENSOR_DELAY_NORMAL);
            } else {
                Log.w(TAG, "Gaming Rotation Vector Sensor not found");
            }

            if (geomagneticSensor != null) {
                mSensorManager.registerListener(this, geomagneticSensor, SensorManager.SENSOR_DELAY_NORMAL);
            } else {
                Log.w(TAG, "No Geomagnetic Sensor found");
            }

            if (gravitySensor != null) {
                mSensorManager.registerListener(this, gravitySensor, SensorManager.SENSOR_DELAY_NORMAL);
            } else {
                Log.w(TAG, "No Gravity Sensor");
            }

            if (gyroscopeSensor != null) {
                mSensorManager.registerListener(this, gyroscopeSensor, SensorManager.SENSOR_DELAY_NORMAL);
            } else {
                Log.w(TAG, "No Gyroscope Sensor found");
            }

            if (gyroscopeUncalibratedSensor != null) {
                mSensorManager.registerListener(this, gyroscopeUncalibratedSensor, SensorManager.SENSOR_DELAY_NORMAL);
            } else {
                Log.w(TAG, "No Uncalibrated Gyroscope Sensor found");
            }

            if (mHeartrateSensor != null) {
                final int measurementDuration   = 10;   // Seconds
                final int measurementBreak      = 5;    // Seconds

                ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
                scheduler.scheduleAtFixedRate(
                        new Runnable() {
                            @Override
                            public void run() {
                                Log.d(TAG, "register Heartrate Sensor");
                                mSensorManager.registerListener(SensorService.this, mHeartrateSensor, SensorManager.SENSOR_DELAY_NORMAL);

                                try {
                                    Thread.sleep(measurementDuration * 1000);
                                } catch (InterruptedException e) {
                                    Log.e(TAG, "Interrupted while waitting to unregister Heartrate Sensor");
                                }

                                Log.d(TAG, "unregister Heartrate Sensor");
                                mSensorManager.unregisterListener(SensorService.this, mHeartrateSensor);
                            }
                        }, 3, measurementDuration + measurementBreak, TimeUnit.SECONDS);
            } else {
                Log.d(TAG, "No Heartrate Sensor found");
            }

            if (heartrateSamsungSensor != null) {
                mSensorManager.registerListener(this, heartrateSamsungSensor, SensorManager.SENSOR_DELAY_FASTEST);
            } else {
                Log.d(TAG, "Samsungs Heartrate Sensor not found");
            }

            if (lightSensor != null) {
                mSensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
            } else {
                Log.d(TAG, "No Light Sensor found");
            }

            if (linearAccelerationSensor != null) {
                mSensorManager.registerListener(this, linearAccelerationSensor, SensorManager.SENSOR_DELAY_NORMAL);
            } else {
                Log.d(TAG, "No Linear Acceleration Sensor found");
            }

            if (magneticFieldSensor != null) {
                mSensorManager.registerListener(this, magneticFieldSensor, SensorManager.SENSOR_DELAY_NORMAL);
            } else {
                Log.d(TAG, "No Magnetic Field Sensor found");
            }

            if (magneticFieldUncalibratedSensor != null) {
                mSensorManager.registerListener(this, magneticFieldUncalibratedSensor, SensorManager.SENSOR_DELAY_NORMAL);
            } else {
                Log.d(TAG, "No uncalibrated Magnetic Field Sensor found");
            }

            if (pressureSensor != null) {
                mSensorManager.registerListener(this, pressureSensor, SensorManager.SENSOR_DELAY_NORMAL);
            } else {
                Log.d(TAG, "No Pressure Sensor found");
            }

            if (proximitySensor != null) {
                mSensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
            } else {
                Log.d(TAG, "No Proximity Sensor found");
            }

            if (humiditySensor != null) {
                mSensorManager.registerListener(this, humiditySensor, SensorManager.SENSOR_DELAY_NORMAL);
            } else {
                Log.d(TAG, "No Humidity Sensor found");
            }

            if (rotationVectorSensor != null) {
                mSensorManager.registerListener(this, rotationVectorSensor, SensorManager.SENSOR_DELAY_NORMAL);
            } else {
                Log.d(TAG, "No Rotation Vector Sensor found");
            }

            if (significantMotionSensor != null) {
                mSensorManager.registerListener(this, significantMotionSensor, SensorManager.SENSOR_DELAY_NORMAL);
            } else {
                Log.d(TAG, "No Significant Motion Sensor found");
            }

            if (stepCounterSensor != null) {
                mSensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_NORMAL);
            } else {
                Log.d(TAG, "No Step Counter Sensor found");
            }

            if (stepDetectorSensor != null) {
                mSensorManager.registerListener(this, stepDetectorSensor, SensorManager.SENSOR_DELAY_NORMAL);
            } else {
                Log.d(TAG, "No Step Detector Sensor found");
            }

        }
    }*/

    private void stopMeasurement(Sensor sensor) {
        if (mSensorManager != null)
            mSensorManager.unregisterListener(this, sensor);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        client.sendSensorData(event.sensor.getType(), event.accuracy, event.timestamp, event.values);
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

}
