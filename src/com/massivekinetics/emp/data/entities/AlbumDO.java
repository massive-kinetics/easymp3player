package com.massivekinetics.emp.data.entities;

import java.nio.charset.Charset;

import com.massivekinetics.emp.logger.Logger;

public class AlbumDO {

	private int id;
	private String title = "No title";
	private String artistName = "NO ARTIST";
	private int songCount;

	public AlbumDO(int albumId, String albumTitle, String albumArtistName,
			int albumSongCount) {
		this.id = albumId;
		try {
			if (!isNullOrEmpty(albumTitle)) {
				this.title = new String(albumTitle.getBytes(), "UTF8");
			}
			if (!isNullOrEmpty(albumArtistName))

				this.artistName = new String(albumArtistName.getBytes(), "UTF8");
		} catch (Exception e) {
			Logger.e("BLA", e.getMessage());
		}
		this.songCount = albumSongCount;
	}

	public int getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public String getArtistName() {
		return artistName;
	}

	public int getSongCount() {
		return songCount;
	}

	private boolean isNullOrEmpty(String value) {
		return (value == null || value.equals(""));
	}

}
