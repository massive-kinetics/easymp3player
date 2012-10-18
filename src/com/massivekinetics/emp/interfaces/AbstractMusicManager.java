package com.massivekinetics.emp.interfaces;

import java.util.List;

import com.massivekinetics.emp.data.entities.Playlist;
import com.massivekinetics.emp.data.entities.Track;
import com.massivekinetics.emp.data.listeners.OnPlaylistChangedListener;
import com.massivekinetics.emp.data.listeners.OnPlaylistsInfoChangedListener;

public abstract class AbstractMusicManager {
	public static final int ALL_TRACKS = -1;
	public static final int OPERATION_ERROR = -1110001;

	public abstract void init();

	public abstract Playlist getPlaylist(int playlistId);

	public abstract List<Playlist> getPlaylists();

	public abstract void updatePlaylist(Playlist playlist);

	public abstract long createPlaylist(String title, List<Track> tracks);

	public abstract long deletePlaylist(int playlistId);

	public abstract void deleteTrackFromPlaylist(int trackId, int playlistId);

	public abstract void addOnPlaylistsInfoChangedListener(
			OnPlaylistsInfoChangedListener listener);

	public abstract void addOnPlaylistChangedListener(
			OnPlaylistChangedListener listener, int playlistId);

}
