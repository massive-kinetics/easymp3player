package com.massivekinetics.emp;

import com.massivekinetics.emp.data.EMPMusicManager;
import com.massivekinetics.emp.interfaces.AbstractMusicManager;
import com.massivekinetics.emp.player.EMPMusicController;

import android.app.Application;

public class EMPApplication extends Application {

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
	}

	public EMPMusicManager getMusicManager() {
		return musicManager;
	}
	
	public EMPMusicController getMusicController() {
		return musicController;
	}

}
