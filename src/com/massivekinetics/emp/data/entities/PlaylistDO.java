package com.massivekinetics.emp.data.entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.massivekinetics.emp.player.EMPMusicController;

public class PlaylistDO implements BaseDO {

	private final static String NULL = "null";

	public final static int nullId = -100;

	public final static PlaylistDO Null = new PlaylistDO(nullId, NULL,
			new ArrayList<TrackDO>());

	private List<TrackDO> items;

	private String title;

	private long id;
	private long createdTime;
	private long modifiedTime;

	public PlaylistDO(long id, String title) {
		if (title == null)
			throwConstructorException();

		this.id = id;
		this.title = title;
		items = new ArrayList<TrackDO>();

		createdTime = modifiedTime = System.currentTimeMillis();
	}

	public PlaylistDO(long id, String title, long createdTime, long modifiedTime) {
		if (title == null || id <= 0)
			throwConstructorException();

		this.id = id;
		this.title = title;
		items = new ArrayList<TrackDO>();

		this.createdTime = createdTime;
		this.modifiedTime = modifiedTime;
	}

	public PlaylistDO(long id, String title, List<TrackDO> trackList) {
		if (title == null || trackList == null)
			throwConstructorException();

		this.title = title;
		this.id = id;
		items = new ArrayList<TrackDO>(trackList);
		Collections.copy(items, trackList);

		createdTime = modifiedTime = System.currentTimeMillis();
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
		modified();
	}

	public long getId() {
		return id;
	}

	public long getCreatedTime() {
		return createdTime;
	}

	public long getModifiedTime() {
		return modifiedTime;
	}

	public TrackDO get(int position) {
		return items.get(position);
	}

	public TrackDO getById(long id) {
		for (TrackDO track : items) {
			if (track.getId() == id)
				return track;
		}
		return TrackDO.Null;
	}

	public void setTracks(List<TrackDO> trackList) {
		items = new ArrayList<TrackDO>(trackList);
	}

	public List<TrackDO> getTracks() {
		return items;
	}

	public int indexOf(TrackDO track) {
		return items.indexOf(track);
	}

	public TrackDO getSong(TrackDO track, int direction) {
		int index = indexOf(track);
		if ((index == 0 && direction == EMPMusicController.DIRECTION_BACKWARD)  || (index == (size() - 1) && direction == EMPMusicController.DIRECTION_FORWARD))
			return track;
		// TODO: test this. Seems like black magic
		index = (direction == EMPMusicController.DIRECTION_FORWARD) ? ++index
				: --index;
		return get(index);
	}

	public void add(TrackDO item) {
		if (!items.contains(item)) {
			items.add(item);
			modified();
		}
	}

	public void remove(TrackDO item) {
		if (items.contains(item)) {
			items.remove(item);
			modified();
		}
	}

	public int size() {
		return items.size();
	}

	private void modified() {
		modifiedTime = System.currentTimeMillis();
	}

	private void throwConstructorException() {
		throw new NullPointerException(
				"Playlist constructor: input argument cant be null!");
	}
}
