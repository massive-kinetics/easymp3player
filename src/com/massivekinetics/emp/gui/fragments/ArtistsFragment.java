package com.massivekinetics.emp.gui.fragments;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.database.CharArrayBuffer;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockListFragment;
import com.massivekinetics.emp.EMPApplication;
import com.massivekinetics.emp.adapters.ArtistListAdapter;
import com.massivekinetics.emp.adapters.indexed.ArtistAdapter;
import com.massivekinetics.emp.concurrent.PrepareMusicManagerTask;
import com.massivekinetics.emp.concurrent.PrepareMusicManagerTask.OnMusicManagerReadyListener;
import com.massivekinetics.emp.data.entities.ArtistDO;
import com.massivekinetics.emp.interfaces.MusicManager;
import com.massivekinetics.emp.utils.GeneralUtils;

public class ArtistsFragment extends SherlockListFragment implements OnMusicManagerReadyListener {
	List<ArtistDO> artistList;
	MusicManager musicManager;
	
	Cursor cursor;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		musicManager = EMPApplication.context.getMusicManager();
		//getListView().setSelector(resID);
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
			/*artistList = EMPApplication.context.getMusicManager()
					.getArtistsInfo();*/
			
			cursor = EMPApplication.context.getMusicManager()
					.getArtists();
		}
		else{
			new PrepareMusicManagerTask(this).execute();
			//artistList = new ArrayList<ArtistDO>();
			cursor = GeneralUtils.emptyCursor;
		}
		
		setListAdapter(new ArtistAdapter(cursor));
		//setListAdapter(new ArtistListAdapter(artistList));
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Toast.makeText(getActivity(), "click", Toast.LENGTH_SHORT).show();
	}
}
