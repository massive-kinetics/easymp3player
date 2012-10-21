package com.massivekinetics.emp;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.massivekinetics.emp.concurrent.PrepareMusicManagerTask;
import com.massivekinetics.emp.concurrent.PrepareMusicManagerTask.OnMusicManagerReadyListener;
import com.massivekinetics.emp.data.entities.PlaylistDO;
import com.massivekinetics.emp.data.entities.TrackDO;
import com.massivekinetics.emp.interfaces.MusicController;
import com.massivekinetics.emp.interfaces.MusicManager;
import com.massivekinetics.emp.player.EMPMusicController;

public class DBTestActivity extends Activity implements
		OnMusicManagerReadyListener {

	boolean isPlaying = false;

	ListView list;
	MusicManager musicManager;
	MusicController musicController = EMPMusicController.getInstance();
	private Button btnPrev, btnPlay, btnStop, btnNext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_layout);
		// list = (ListView)findViewById(R.id.listView);
		btnPrev = (Button) findViewById(R.id.btnPrevious);
		btnPlay = (Button) findViewById(R.id.btnPlay);
		btnStop = (Button) findViewById(R.id.btnStop);
		btnNext = (Button) findViewById(R.id.btnNext);
		new PrepareMusicManagerTask(this).execute();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	public void onMusicManagerReady() {
		musicManager = EMPApplication.context.getMusicManager();
		doDbWork();
	}

	private void doDbWork() {

		PlaylistDO playlist = musicManager.getPlaylist(MusicManager.ALL_TRACKS);

		List<TrackDO> tracks = new ArrayList<TrackDO>();
		for (int i = 2; i < 10; i++) {
			tracks.add(playlist.get(i));
		}

		long playlistId = 1;// musicManager.createPlaylist("Playlist2", tracks);

		List p = musicManager.getPlaylists();
		
		long[] ids = new long[3];
		for(int i=0;i<3;i++){
			ids[i] = tracks.get(i).getId();
		}
		musicManager.deleteTrackFromPlaylist(playlistId, ids);
		
		p = musicManager.getPlaylists();

		musicController.setCurrentPlaying(musicManager.getPlaylist(playlistId)
				.getId(), 3);

		// startService(new Intent(MusicService.ACTION_PLAY));

		btnPrev.setOnClickListener(new View.OnClickListener() {
			int i = 0;

			@Override
			public void onClick(View v) {
				musicController
						.switchSong(EMPMusicController.DIRECTION_BACKWARD);
			}
		});

		btnNext.setOnClickListener(new View.OnClickListener() {
			int i = 0;

			@Override
			public void onClick(View v) {
				musicController
						.switchSong(EMPMusicController.DIRECTION_FORWARD);
			}
		});

		btnPlay.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!isPlaying)
					musicController.play();
				else
					musicController.pause();

				isPlaying = !isPlaying;
			}
		});

		btnStop.setOnClickListener(new View.OnClickListener() {
			int i = 0;

			@Override
			public void onClick(View v) {
				isPlaying = false;
				musicController.stop();
			}
		});

	}

}
