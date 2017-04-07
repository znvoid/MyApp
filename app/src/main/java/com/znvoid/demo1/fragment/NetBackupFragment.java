package com.znvoid.demo1.fragment;


import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.znvoid.demo1.R;
import com.znvoid.demo1.view.WareView;

public class NetBackupFragment extends Fragment implements OnClickListener ,SensorEventListener{


	private WareView mWareView;
	private SensorManager mSensorManager;
	private Sensor gyroSensor;
	private float mDegree=0;
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
	}

	@Override
	public void onResume() {
//		mSensorManager.registerListener(this, gyroSensor, SensorManager.SENSOR_DELAY_NORMAL);  //为传感器注册监听器
		super.onResume();
	}

	@Override
	public void onPause() {
//		mSensorManager.unregisterListener(this);
		super.onPause();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.netbackup, container,false);
//		mSensorManager = (SensorManager) getActivity().getSystemService(getActivity().SENSOR_SERVICE);
//		gyroSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
//
//		mWareView = (WareView) view.findViewById(R.id.wareView1);
//		mWareView.setmWaveShiftRatio(0.5f);
//		mWareView.startAnimt();

		return view;

	}



	@Override
	public void onSensorChanged(SensorEvent event) {

		float n= event.values[2]-mDegree;
		if (Math.abs(n)>=1) {
			mDegree=event.values[2];
			mWareView.setmRotateDegrees(-mDegree);
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(View v) {

	}
}
