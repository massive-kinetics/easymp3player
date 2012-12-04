package com.massivekinetics.emp.gui.fragments;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockListFragment;
import com.massivekinetics.emp.EMPApplication;
import com.massivekinetics.emp.adapters.indexed.TrackAdapter;
import com.massivekinetics.emp.concurrent.PrepareMusicManagerTask;
import com.massivekinetics.emp.concurrent.PrepareMusicManagerTask.OnMusicManagerReadyListener;
import com.massivekinetics.emp.data.entities.TrackDO;
import com.massivekinetics.emp.interfaces.MusicManager;
import com.massivekinetics.emp.utils.GeneralUtils;

public class SongsFragment extends SherlockListFragment implements
		OnMusicManagerReadyListener {
	List<TrackDO> trackList;
	MusicManager musicManager;

	Cursor cursor;

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
		// setListAdapter(null);
		fillListAdapter();
	}

	private void fillListAdapter() {
		if (musicManager.isInitialized()) {
			trackList = EMPApplication.context.getMusicManager()
					.getPlaylist(MusicManager.ALL_TRACKS).getTracks();
			cursor = musicManager.getTracks();
			setListAdapter(new TrackAdapter(cursor));
			
		} else {
			new PrepareMusicManagerTask(this).execute();
			trackList = new ArrayList<TrackDO>();
			cursor = GeneralUtils.emptyCursor;
			setListAdapter(new TrackAdapter(cursor));
		}
		// setListAdapter(new TracksListAdapter<TrackDO>(trackList));
	}
	

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Cursor cur = EMPApplication.context.getMusicManager().getTrack(id);
		cur.moveToFirst();
		
		int artistColumn = cur.getColumnIndex(MediaStore.Audio.Media.ARTIST);
		int titleColumn = cur.getColumnIndex(MediaStore.Audio.Media.TITLE);

		int idColumn = cur.getColumnIndex(MediaStore.Audio.Media._ID);
		
		StringBuilder str = new StringBuilder();
		
		str.append("Artist: " + cur.getString(artistColumn));
		str.append("Title: " + cur.getString(titleColumn));
		str.append("Id: " + cur.getString(idColumn));
		
		Toast.makeText(getActivity(), str.toString(), Toast.LENGTH_SHORT).show();
	}

	

}
