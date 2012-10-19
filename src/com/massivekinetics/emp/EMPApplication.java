package com.massivekinetics.emp;

import android.app.Application;

import com.massivekinetics.emp.concurrent.PrepareMusicManagerTask;
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
