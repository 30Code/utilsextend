package cn.linhome.lib.utils.extend;


import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * 摇一摇监听
 */
public class FShakeListener implements SensorEventListener
{
    private static final int ACC_SHAKE = 19;
    /**
     * 触发计算的间隔
     */
    private static final int DURATION_CALCULATE = 100;
    /**
     * 触发通知的间隔
     */
    private static final int DURATION_NOTIFY = 500;

    private SensorManager mSensorManager;
    private Sensor mSensor;

    private float mLastX;
    private float mLastY;
    private float mLastZ;
    private long mLastSensorChangedTime;

    private long mLastNotifyTime;

    private Callback mCallback;

    public FShakeListener(Context context)
    {
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    public void setCallback(Callback callback)
    {
        this.mCallback = callback;
    }

    public void start()
    {
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void stop()
    {
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event)
    {
        long currentTime = System.currentTimeMillis();
        long deltaTime = currentTime - mLastSensorChangedTime;
        if (deltaTime < DURATION_CALCULATE)
        {
            return;
        }
        mLastSensorChangedTime = currentTime;

        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        float deltaX = x - mLastX;
        float deltaY = y - mLastY;
        float deltaZ = z - mLastZ;

        mLastX = x;
        mLastY = y;
        mLastZ = z;

        if (Math.abs(deltaX) >= ACC_SHAKE || Math.abs(deltaY) >= ACC_SHAKE || Math.abs(deltaZ) >= ACC_SHAKE)
        {
            notifyCallback();
        }
    }

    private void notifyCallback()
    {
        long current = System.currentTimeMillis();
        if (current - mLastNotifyTime < DURATION_NOTIFY)
        {
            return;
        }
        mLastNotifyTime = current;

        if (mCallback != null)
        {
            mCallback.onShake();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {
    }

    public interface Callback
    {
        void onShake();
    }
}