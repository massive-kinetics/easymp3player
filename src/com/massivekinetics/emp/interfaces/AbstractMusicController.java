package com.massivekinetics.emp.interfaces;

import com.massivekinetics.emp.data.entities.Playlist;
import com.massivekinetics.emp.data.entities.Track;

public abstract class AbstractMusicController {
	public static final int DIRECTION_FORWARD = 10000001;
	public static final int DIRECTION_BACKWARD = 10000002;

	public abstract void setCurrentPlaying(int playlistId, int position);

	public abstract Playlist getCurrentPlaylist();
	
	public abstract Track getTrackToPlay();
	
	//public abstract Uri getNextTrackUri(int direction);
	//public abstract Track getTrack(int position);
	
	public abstract void switchSong(int direction);
	public abstract void play();
	public abstract void pause();
	public abstract void rewind();
	public abstract void stop();
	
}
