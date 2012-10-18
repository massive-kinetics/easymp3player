package com.massivekinetics.emp.data.entities;

public class ArtistDO {

	private int id;
	private String name;
	private int albumCount;
	private int trackCount;

	public ArtistDO(int artistId, String artistName, int artistAlbumCount,
			int artistTrackCount) {
		id = artistId;
		name = artistName;
		albumCount = artistAlbumCount;
		trackCount = artistTrackCount;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public int getAlbumCount() {
		return albumCount;
	}

	public int getTrackCount() {
		return trackCount;
	}

}
