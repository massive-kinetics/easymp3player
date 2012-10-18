package com.massivekinetics.emp.interfaces;

import com.massivekinetics.emp.data.entities.Playlist;

public interface DataManager {

	public void reset();
	public Playlist getPlayList(int playlistID);
	
}
