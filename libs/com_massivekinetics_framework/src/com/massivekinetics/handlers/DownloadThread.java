package com.massivekinetics.handlers;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

public class DownloadThread extends Thread {

	private static final String TAG = "DownloadThread";
	private DownloadThreadListener mListener;
	private Handler mHandler;

	private int totalQueued, totalCompleted;

	public DownloadThread(DownloadThreadListener listener) {
		mListener = listener;
	}

	@Override
	public void run() {
		try {
			Looper.prepare();

			mHandler = new Handler();

			Looper.loop();
		} catch (Exception e) {
			Log.e(TAG, "Error " + e.getMessage());
		}
	}

	public void addTask(final DownloadTask task) {
		mHandler.post(new Runnable() {

			@Override
			public void run() {
				try {
					task.run();
				} finally {
					synchronized (DownloadThread.this) {
						totalCompleted++;
					}
					signalUpdate();
				}
			}
		});

		totalQueued++;
		signalUpdate();
	}

	public void requestStop() {
		Runnable stopRunnable = new Runnable() {
			@Override
			public void run() {
				Looper.myLooper().quit();
			}
		};

		mHandler.post(stopRunnable);
	}

	private void signalUpdate() {
		if (mListener != null)
			mListener.handleDownloadThreadUpdate();
	}

	public int getTotalQueued() {
		return totalQueued;
	}

	public int getTotalCompleted() {
		return totalCompleted;
	}

}
