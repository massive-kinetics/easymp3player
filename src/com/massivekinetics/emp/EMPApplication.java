package com.massivekinetics.emp;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.Toast;

import com.massivekinetics.emp.concurrent.PrepareMusicManagerTask.OnMusicManagerReadyListener;
import com.massivekinetics.emp.data.EMPMusicManager;
import com.massivekinetics.emp.player.EMPMusicController;

public class EMPApplication extends Application implements OnMusicManagerReadyListener {

	public static EMPApplication context;
	
	private EMPMusicController musicController;
	private EMPMusicManager musicManager;

	@Override
	public void onCreate() {
		super.onCreate();
		initEMP();
	}

	// Initialization of application resources. Firstly sets link to static
	// variable 'context', thus allowing to have application context from any
	// place in the application. There is some chances for link to be null - but
	// assumes that application itself firstly creates and fill this link before
	// any components are created
	private void initEMP() {
		context = this;
		musicManager = new EMPMusicManager();
		musicController = EMPMusicController.getInstance();
		//new PrepareMusicManagerTask(this).execute();
		
		registerMediaContentObserver();
		
	}

	private void registerMediaContentObserver() {
		registerReceiver(new BroadcastReceiver() {
		    @Override
		    public void onReceive(Context context, Intent intent) {
		      String newFileURL = intent.getDataString();
		      Log.e("DELETED", newFileURL);
		    }    
		  }, new IntentFilter(Intent.ACTION_MEDIA_REMOVED/*ACTION_MEDIA_SCANNER_SCAN_FILE*/));
	}

	public EMPMusicManager getMusicManager() {
		return musicManager;
	}
	
	public EMPMusicController getMusicController() {
		return musicController;
	}

	@Override
	public void onMusicManagerReady() {
		// TODO Auto-generated method stub
		
	}

}
