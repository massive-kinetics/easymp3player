package com.massivekinetics.emp.gui.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockListFragment;
import com.massivekinetics.emp.EMPApplication;
import com.massivekinetics.emp.adapters.indexed.TrackAdapter;
import com.massivekinetics.emp.concurrent.PrepareMusicManagerTask;
import com.massivekinetics.emp.concurrent.PrepareMusicManagerTask.OnMusicManagerReadyListener;
import com.massivekinetics.emp.interfaces.MusicController;
import com.massivekinetics.emp.interfaces.MusicManager;
import com.massivekinetics.emp.player.EMPMusicController;
import com.massivekinetics.emp.utils.GeneralUtils;

public class SongsFragment extends SherlockListFragment implements
		OnMusicManagerReadyListener {

	MusicManager musicManager;
	MusicController musicController;

	Cursor cursor;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		musicManager = EMPApplication.context.getMusicManager();
		musicController = EMPMusicController.getInstance();
		fillListAdapter();

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onMusicManagerReady() {
		// setListAdapter(null);
		fillListAdapter();
	}

	private void fillListAdapter() {
		if (musicManager.isInitialized()) {

			cursor = musicManager.getTracks();
			setListAdapter(new TrackAdapter(cursor));

		} else {
			new PrepareMusicManagerTask(this).execute();

			cursor = GeneralUtils.emptyCursor;
			setListAdapter(new TrackAdapter(cursor));
		}
		// setListAdapter(new TracksListAdapter<TrackDO>(trackList));
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		musicController.setCurrentPlaying(MusicManager.ALL_TRACKS, id);
		musicController.play();
	}

}
