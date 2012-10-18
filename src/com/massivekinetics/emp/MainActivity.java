package com.massivekinetics.emp;

import static com.massivekinetics.emp.utils.Constants.SONG_POSITION;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.massivekinetics.emp.data.EMPMusicManager;
import com.massivekinetics.emp.data.ProviderMusicManager;
import com.massivekinetics.emp.data.entities.Playlist;
import com.massivekinetics.emp.player.EMPMusicController;
import com.massivekinetics.emp.concurrent.PrepareMusicManagerTask.OnMusicManagerReadyListener;

public class MainActivity extends ListActivity implements OnItemClickListener, OnMusicManagerReadyListener {

	private final static String ROOT_FOLDER = "/sdcard/";
	private final static String[] EXTENSIONS = { "mp3" };
	List<String> musicFiles = new ArrayList<String>();
	Handler handler = new Handler();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		getListView().setOnItemClickListener(this);
		updateMusicFiles();

	}

	private void updateMusicFiles() {
		ProviderMusicManager musicManager = new ProviderMusicManager();
		musicManager.prepare();
		
		Playlist pl = musicManager.getPlayList(1);
		for(int i=0; i<pl.size();i++){
			musicFiles.add(pl.get(i).getArtist() + " - " + pl.get(i).getTitle());
		}
		
		//musicFiles = MusicManager.getInstance().getCurrentPlaylistNames();
		
		handler.post(new Runnable() {
			@Override
			public void run() {
				ListView list = MainActivity.this.getListView();
				
				
				String[] items = musicFiles.toArray(new String[musicFiles
						.size()]);
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(
						MainActivity.this, android.R.layout.simple_list_item_1,
						android.R.id.text1, items);
				list.setAdapter(null);
				list.setAdapter(adapter);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View clicked, int position,
			long id) {
		Intent intent = new Intent(MainActivity.this, OneFilePlayerActivity.class);
		intent.putExtra(SONG_POSITION, position);
		startActivity(intent);
	}

	@Override
	public void onMusicManagerReady() {
		EMPMusicController.getInstance().setCurrentPlaying(EMPMusicManager.ALL_TRACKS, 0);
	}

}
