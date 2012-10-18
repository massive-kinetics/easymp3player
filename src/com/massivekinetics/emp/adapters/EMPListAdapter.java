package com.massivekinetics.emp.adapters;

import com.massivekinetics.emp.EMPApplication;
import com.massivekinetics.emp.R;
import com.massivekinetics.emp.data.EMPMusicManager;
import com.massivekinetics.emp.data.entities.Playlist;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class EMPListAdapter extends BaseAdapter {
	
	protected Playlist playlist;
	protected EMPMusicManager musicManager;
	
	public EMPListAdapter(Playlist playlist) {
		this.playlist = playlist;
		this.musicManager = EMPApplication.context.getMusicManager();
		
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		return null;
	}

}
