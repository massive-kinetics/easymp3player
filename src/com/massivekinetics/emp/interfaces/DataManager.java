package com.massivekinetics.emp.interfaces;

import com.massivekinetics.emp.data.entities.PlaylistDO;

public interface DataManager {

	public void reset();
	public PlaylistDO getPlayList(int playlistID);
	
}
