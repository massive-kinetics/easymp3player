package com.massivekinetics.emp.player;

import android.content.Intent;

import com.massivekinetics.emp.EMPApplication;
import com.massivekinetics.emp.MusicService;
import com.massivekinetics.emp.data.EMPMusicManager;
import com.massivekinetics.emp.data.entities.Playlist;
import com.massivekinetics.emp.data.entities.Track;
import com.massivekinetics.emp.interfaces.AbstractMusicController;

public class EMPMusicController extends AbstractMusicController {
	private EMPApplication context = EMPApplication.context;
	private static EMPMusicController instance;
	private EMPMusicManager musicManager;
	private Playlist currentPlaylist;
	private Track currentTrack;
	
	
	private EMPMusicController(){
		musicManager = context.getMusicManager();
	}
	
	public static EMPMusicController getInstance(){
		if(instance==null)
			instance = new EMPMusicController();
		return instance;
	}
	
	@Override
	public void setCurrentPlaying(int playlistId, int position) {
		currentPlaylist = musicManager.getPlaylist(playlistId);
		currentTrack = currentPlaylist.get(position);
	}

	@Override
	public Playlist getCurrentPlaylist() {
		return currentPlaylist;
	}

	@Override
	public void switchSong(int direction) {
		Track track = currentPlaylist.getSong(currentTrack, direction);
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
	public Track getTrackToPlay() {
		return currentTrack;
	}

}
