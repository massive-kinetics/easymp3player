package com.massivekinetics.emp;

import static com.massivekinetics.emp.utils.Constants.NEXT_SONG;
import static com.massivekinetics.emp.utils.Constants.PREVIOUS_SONG;
import static com.massivekinetics.emp.utils.Constants.SONG_POSITION;

import java.io.IOException;

import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.util.Log;

import com.massivekinetics.emp.adapters.MusicAdapter;
import com.massivekinetics.emp.interfaces.OnSwypeCompletedListener;
import com.massivekinetics.emp.views.Swyper;

public class OneFilePlayerActivity extends Activity implements
		OnSwypeCompletedListener {
	Swyper swyper;
	private MediaPlayer mp = new MediaPlayer();

	int position;
	String songTitle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		swyper = new Swyper(getApplicationContext());
		swyper.setAdapter(new MusicAdapter(this));
		swyper.addOnSwypeCompletedListener(this);

		setContentView(swyper);
		swyper.snapToScreen(position);

		getExtraData();
		mp.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {
				switchSong(NEXT_SONG);
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		// playSong();
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (mp.isPlaying())
			mp.stop();
	}

	private void getExtraData() {
		position = getIntent().getIntExtra(SONG_POSITION, 0);
		
	}

	private void switchSong(int direction) {
		if (direction == NEXT_SONG) {
			/*int max = musicManager.getCurrentMaxPosition() - 1;
			position += (position == max) ? 0 : 1;*/
		} else {
			position -= (position == 0) ? 0 : 1;
		}
		playSong();
	}

	private void playSong() {

		try {
			if (mp.isPlaying())
				mp.stop();
			mp.reset();
			/*mp.setDataSource(MusicManager.getInstance()
					.getSongFromPosition(position).getAbsolutePath());*/
			mp.prepare();
			mp.start();
		} catch (IOException e) {
			Log.v(getString(R.string.app_name), e.getMessage());
		}

	}

	@Override
	public void onSwypeCompleted(int newScreenIndex) {
		int newPos = newScreenIndex - position;
		if (newPos != 0) {
			if (newPos > 0)
				switchSong(NEXT_SONG);
			else
				switchSong(PREVIOUS_SONG);
		}
	}

}
