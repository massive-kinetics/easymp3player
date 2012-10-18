package com.massivekinetics.emp;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.massivekinetics.emp.adapters.ArtistListAdapter;
import com.massivekinetics.emp.concurrent.PrepareMusicManagerTask;
import com.massivekinetics.emp.concurrent.PrepareMusicManagerTask.OnMusicManagerReadyListener;
import com.massivekinetics.emp.data.EMPMusicManager;
import com.massivekinetics.emp.data.entities.ArtistDO;
import com.massivekinetics.emp.data.entities.Playlist;
import com.massivekinetics.emp.data.entities.Track;
import com.massivekinetics.emp.player.EMPMusicController;

public class DBTestActivity extends Activity implements
		OnMusicManagerReadyListener {

	boolean isPlaying = false;
	
	ListView list;
	EMPMusicManager musicManager;
	EMPMusicController musicController = EMPMusicController.getInstance();
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

		/*
		 * List<ArtistDO> albums = musicManager.getArtistsInfo();
		 * ArtistListAdapter adapter = new ArtistListAdapter(albums);
		 * list.setAdapter(adapter);
		 */

		musicController
				.setCurrentPlaying(
						musicManager.getPlaylist(EMPMusicManager.ALL_TRACKS)
								.getId(), 0);

		//startService(new Intent(MusicService.ACTION_PLAY));

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
