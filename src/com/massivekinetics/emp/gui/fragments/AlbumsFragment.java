package com.massivekinetics.emp.gui.fragments;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;

import com.actionbarsherlock.app.SherlockListFragment;
import com.massivekinetics.emp.EMPApplication;
import com.massivekinetics.emp.adapters.AlbumsListAdapter;
import com.massivekinetics.emp.adapters.ArtistListAdapter;
import com.massivekinetics.emp.concurrent.PrepareMusicManagerTask;
import com.massivekinetics.emp.concurrent.PrepareMusicManagerTask.OnMusicManagerReadyListener;
import com.massivekinetics.emp.data.entities.AlbumDO;
import com.massivekinetics.emp.data.entities.ArtistDO;
import com.massivekinetics.emp.interfaces.MusicManager;

public class AlbumsFragment extends SherlockListFragment implements OnMusicManagerReadyListener {
	List<AlbumDO> albumList;
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
		//setListAdapter(null);
		fillListAdapter();
	}

	private void fillListAdapter() {
		if (musicManager.isInitialized()) {
			albumList = EMPApplication.context.getMusicManager()
					.getAllAlbumsInfo();
		}
		else{
			new PrepareMusicManagerTask(this).execute();
			albumList = new ArrayList<AlbumDO>();
		}
		
		setListAdapter(new AlbumsListAdapter(albumList
				));
	}

}
