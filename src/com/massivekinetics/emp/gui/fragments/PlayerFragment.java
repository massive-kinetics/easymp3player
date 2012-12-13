package com.massivekinetics.emp.gui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.SeekBar;

import com.actionbarsherlock.app.SherlockFragment;
import com.massivekinetics.emp.EMPApplication;
import com.massivekinetics.emp.R;

public class PlayerFragment extends SherlockFragment {
	SeekBar bar;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public void onResume() {
		super.onResume();
		new Thread() {
			@Override
			public void run() {
				for (int i = 0; i < 100; i++) {
					bar.setProgress(i);
					synchronized (this) {
						try {

							wait(200);

						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		}.start();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View layout = inflater.inflate(R.layout.player_page, null);
		bar = (SeekBar) layout.findViewById(R.id.seekBar);
		return layout;
	}
}
