package com.massivekinetics.emp.gui.fragments;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockListFragment;
import com.massivekinetics.emp.EMPApplication;
import com.massivekinetics.emp.adapters.AlbumsListAdapter;
import com.massivekinetics.emp.adapters.indexed.AlbumAdapter;
import com.massivekinetics.emp.concurrent.PrepareMusicManagerTask;
import com.massivekinetics.emp.concurrent.PrepareMusicManagerTask.OnMusicManagerReadyListener;
import com.massivekinetics.emp.data.entities.AlbumDO;
import com.massivekinetics.emp.interfaces.MusicManager;
import com.massivekinetics.emp.utils.GeneralUtils;

public class AlbumsFragment extends SherlockListFragment implements OnMusicManagerReadyListener {
	List<AlbumDO> albumList;
	MusicManager musicManager;
	Cursor cursor = GeneralUtils.emptyCursor;

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
			//albumList = musicManager.getAllAlbumsInfo();
			cursor = musicManager.getAlbums();
		}
		else{
			new PrepareMusicManagerTask(this).execute();
			//albumList = new ArrayList<AlbumDO>();
		}
		
		setListAdapter(new AlbumAdapter(cursor));
		//setListAdapter(new AlbumsListAdapter(albumList));
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Toast.makeText(getActivity(), "click", Toast.LENGTH_SHORT).show();
	}
}
