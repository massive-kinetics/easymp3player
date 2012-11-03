package com.massivekinetics.emp.gui.fragments;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockListFragment;
import com.massivekinetics.emp.EMPApplication;
import com.massivekinetics.emp.adapters.PlaylistListAdapter;
import com.massivekinetics.emp.concurrent.PrepareMusicManagerTask;
import com.massivekinetics.emp.concurrent.PrepareMusicManagerTask.OnMusicManagerReadyListener;
import com.massivekinetics.emp.data.entities.PlaylistDO;
import com.massivekinetics.emp.interfaces.MusicManager;

public class PlaylistsFragment extends SherlockListFragment implements OnMusicManagerReadyListener {
	List<PlaylistDO> playlists;
	MusicManager musicManager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		musicManager = EMPApplication.context.getMusicManager();
		fillListAdapter();		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onMusicManagerReady() {
		fillListAdapter();
	}

	private void fillListAdapter() {
		if (musicManager.isInitialized()) {
			playlists = EMPApplication.context.getMusicManager().getPlaylists();
		}
		else{
			new PrepareMusicManagerTask(this).execute();
			playlists = new ArrayList<PlaylistDO>();
		}
		setListAdapter(new PlaylistListAdapter(playlists));
	}

}
