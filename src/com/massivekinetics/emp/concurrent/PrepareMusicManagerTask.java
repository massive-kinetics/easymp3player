package com.massivekinetics.emp.concurrent;

import android.os.AsyncTask;

import com.massivekinetics.emp.EMPApplication;
import com.massivekinetics.emp.data.EMPMusicManager;

public class PrepareMusicManagerTask extends AsyncTask<Void, Void, Void> {
	OnMusicManagerReadyListener mListener;

	public PrepareMusicManagerTask(OnMusicManagerReadyListener listener) {
		mListener = listener;
	}

	@Override
	protected Void doInBackground(Void... params) {
		EMPApplication.context.getMusicManager().init();
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		mListener.onMusicManagerReady();
	}

	public interface OnMusicManagerReadyListener {
		public void onMusicManagerReady();
	}

}
