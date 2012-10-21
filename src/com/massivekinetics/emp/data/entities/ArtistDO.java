package com.massivekinetics.emp.data.entities;

public class ArtistDO implements BaseDO {

	private long id;
	private String name;
	private int albumCount;
	private int trackCount;

	public ArtistDO(long artistId, String artistName, int artistAlbumCount,
			int artistTrackCount) {
		id = artistId;
		name = artistName;
		albumCount = artistAlbumCount;
		trackCount = artistTrackCount;
	}

	public long getId() {
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
