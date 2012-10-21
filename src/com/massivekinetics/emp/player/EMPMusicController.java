package com.massivekinetics.emp.player;

import android.content.Intent;

import com.massivekinetics.emp.EMPApplication;
import com.massivekinetics.emp.MusicService;
import com.massivekinetics.emp.data.EMPMusicManager;
import com.massivekinetics.emp.data.entities.PlaylistDO;
import com.massivekinetics.emp.data.entities.TrackDO;
import com.massivekinetics.emp.interfaces.MusicController;

public class EMPMusicController implements MusicController {
	private EMPApplication context = EMPApplication.context;
	private static EMPMusicController instance;
	private EMPMusicManager musicManager;
	private PlaylistDO currentPlaylist;
	private TrackDO currentTrack;
	
	
	private EMPMusicController(){
		musicManager = context.getMusicManager();
	}
	
	public static EMPMusicController getInstance(){
		if(instance==null)
			instance = new EMPMusicController();
		return instance;
	}
	
	@Override
	public void setCurrentPlaying(long playlistId, int position) {
		currentPlaylist = musicManager.getPlaylist(playlistId);
		currentTrack = currentPlaylist.get(position);
	}

	@Override
	public PlaylistDO getCurrentPlaylist() {
		return currentPlaylist;
	}

	@Override
	public void switchSong(int direction) {
		TrackDO track = currentPlaylist.getSong(currentTrack, direction);
		if(track == currentTrack)
			return;
		else{
			currentTrack = track;
			context.startService(new Intent(MusicService.ACTION_SKIP));
		}
	}

	@Override
	public void play() {
		context.startService(new Intent(MusicService.ACTION_PLAY));
	}

	@Override
	public void pause() {
		context.startService(new Intent(MusicService.ACTION_PAUSE));
	}

	@Override
	public void rewind() {
		// TODO Auto-generated method stub

	}

	@Override
	public void stop() {
		context.startService(new Intent(MusicService.ACTION_STOP));
	}

	@Override
	public TrackDO getTrackToPlay() {
		return currentTrack;
	}

}
