package com.massivekinetics.emp.interfaces;

import java.util.List;

import com.massivekinetics.emp.data.entities.Playlist;
import com.massivekinetics.emp.data.entities.Track;
import com.massivekinetics.emp.data.listeners.OnPlaylistChangedListener;
import com.massivekinetics.emp.data.listeners.OnPlaylistsInfoChangedListener;

public interface MusicManager {
	public static final int ALL_TRACKS = -1;
	public static final int OPERATION_ERROR = -1110001;

	void init();

	Playlist getPlaylist(int playlistId);

	List<Playlist> getPlaylists();

	void updatePlaylist(Playlist playlist);

	long createPlaylist(String title, List<Track> tracks);

	long deletePlaylist(int playlistId);

	void deleteTrackFromPlaylist(int trackId, int playlistId);

	void addOnPlaylistsInfoChangedListener(
			OnPlaylistsInfoChangedListener listener);

	void addOnPlaylistChangedListener(OnPlaylistChangedListener listener,
			int playlistId);
	
	boolean isInitialized();

}
