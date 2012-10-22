package com.massivekinetics.emp.interfaces;

import com.massivekinetics.emp.data.entities.PlaylistDO;
import com.massivekinetics.emp.data.entities.TrackDO;

public interface MusicController {
	int DIRECTION_FORWARD = 10000001;
	int DIRECTION_BACKWARD = 10000002;

	void setCurrentPlaying(long playlistId, int position);

	PlaylistDO getCurrentPlaylist();
	
	TrackDO getTrackToPlay();
	
	//Uri getNextTrackUri(int direction);
	//Track getTrack(int position);
	
	void switchSong(int direction);
	void play();
	void pause();
	void rewind();
	void stop();
	
}
