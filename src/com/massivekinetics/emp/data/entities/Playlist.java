package com.massivekinetics.emp.data.entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.massivekinetics.emp.player.EMPMusicController;

public class Playlist {

	private final static String NULL = "null";

	public final static int nullId = -100;

	public final static Playlist Null = new Playlist(NULL, nullId,
			new ArrayList<Track>());

	private List<Track> items;

	private String title;

	private int id;
	private long createdTime;
	private long modifiedTime;

	public Playlist(String title, int id) {
		if (title == null)
			throwConstructorException();

		this.id = id;
		this.title = title;
		items = new ArrayList<Track>();

		createdTime = modifiedTime = System.currentTimeMillis();
	}

	public Playlist(String title, int id, long createdTime, long modifiedTime) {
		if (title == null || id <= 0)
			throwConstructorException();

		this.id = id;
		this.title = title;
		items = new ArrayList<Track>();

		this.createdTime = createdTime;
		this.modifiedTime = modifiedTime;
	}

	public Playlist(String title, int id, List<Track> trackList) {
		if (title == null || trackList == null)
			throwConstructorException();

		this.title = title;
		this.id = id;
		items = new ArrayList<Track>(trackList);
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

	public int getId() {
		return id;
	}

	public long getCreatedTime() {
		return createdTime;
	}

	public long getModifiedTime() {
		return modifiedTime;
	}

	public Track get(int position) {
		return items.get(position);
	}

	public Track getById(long id) {
		for (Track track : items) {
			if (track.getId() == id)
				return track;
		}
		return Track.Null;
	}

	public void setTracks(List<Track> trackList) {
		items = new ArrayList<Track>(trackList);
	}

	public List<Track> getTracks() {
		return items;
	}

	public int indexOf(Track track) {
		return items.indexOf(track);
	}

	public Track getSong(Track track, int direction) {
		int index = indexOf(track);
		if ((index == 0 && direction == EMPMusicController.DIRECTION_BACKWARD)  || (index == (size() - 1) && direction == EMPMusicController.DIRECTION_FORWARD))
			return track;
		// TODO: test this. Seems like black magic
		index = (direction == EMPMusicController.DIRECTION_FORWARD) ? ++index
				: --index;
		return get(index);
	}

	public void add(Track item) {
		if (!items.contains(item)) {
			items.add(item);
			modified();
		}
	}

	public void remove(Track item) {
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
