package com.massivekinetics.emp.data.entities;

import android.content.ContentUris;
import android.graphics.Bitmap;
import android.net.Uri;

import com.massivekinetics.emp.utils.Constants;
import com.massivekinetics.emp.utils.FileUtils;

public class TrackDO implements BaseDO {
	long id;
	String artist;
	String title;
	String album;
	long duration;
	long albumId;
	Bitmap cover;

	public static final TrackDO Null = new TrackDO(0, "", "", "", Constants.NO_COVER_ID, 0) {
		@Override
		public Uri getURI() {
			throw new IllegalAccessError(
					"Object Track.Null dont assumed to handle in application");
		}
	};

	public Bitmap getCover() {
		return cover;
	}

	public void setCover(Bitmap cover) {
		this.cover = cover;
	}

	public TrackDO(long id, String artist, String title, String album,
			long albumId, long duration) {
		this.id = id;
		this.artist = artist;
		this.title = title;
		this.album = album;
		this.duration = duration;
		this.albumId = albumId;
		this.cover = FileUtils.getArtworkQuick(albumId, 100, 100);
	}

	public long getId() {
		return id;
	}

	public String getArtist() {
		return artist;
	}

	public String getTitle() {
		return title;
	}

	public String getAlbum() {
		return album;
	}

	public long getDuration() {
		return duration;
	}

	public Uri getURI() {
		return ContentUris.withAppendedId(
				android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
				id);
	}
}
