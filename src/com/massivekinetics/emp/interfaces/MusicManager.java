package com.massivekinetics.emp.interfaces;

import java.util.List;

import com.massivekinetics.emp.data.entities.PlaylistDO;
import com.massivekinetics.emp.data.entities.TrackDO;
import com.massivekinetics.emp.data.listeners.OnPlaylistChangedListener;
import com.massivekinetics.emp.data.listeners.OnPlaylistsInfoChangedListener;

public interface MusicManager {
	public static final long ALL_TRACKS = -1;
	public static final int OPERATION_ERROR = -1110001;

	void init();

	PlaylistDO getPlaylist(long playlistId);

	List<PlaylistDO> getPlaylists();

	void updatePlaylist(PlaylistDO playlist);

	long createPlaylist(String title, List<TrackDO> tracks);

	long deletePlaylist(long playlistId);

	void deleteTrackFromPlaylist(long playlistId, long[] trackId);

	void addOnPlaylistsInfoChangedListener(
			OnPlaylistsInfoChangedListener listener);

	void addOnPlaylistChangedListener(OnPlaylistChangedListener listener,
			int playlistId);
	
	boolean isInitialized();

}
