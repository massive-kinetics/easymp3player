package com.massivekinetics;

import java.util.Random;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.massivekinetics.handlers.DownloadTask;
import com.massivekinetics.handlers.DownloadThread;
import com.massivekinetics.handlers.DownloadThreadListener;

public class DownloadQueueActivity extends Activity implements
		DownloadThreadListener, OnClickListener {

	private DownloadThread downloadThread;

	private Handler handler;

	private ProgressBar progressBar;

	private TextView statusText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.progress);

		downloadThread = new DownloadThread(this);
		downloadThread.start();

		// Create the Handler. It will implicitly bind to the Looper
		// that is internally created for this thread (since it is the UI
		// thread)
		handler = new Handler();

		progressBar = (ProgressBar) findViewById(R.id.progress_bar);
		statusText = (TextView) findViewById(R.id.status_text);

		Button scheduleButton = (Button) findViewById(R.id.schedule_button);
		scheduleButton.setOnClickListener(this);

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		// request the thread to stop
		downloadThread.requestStop();
	}

	@Override
	public void onClick(View v) {
		int totalTask = new Random().nextInt(3) + 1;
		for (int i = 0; i < totalTask; i++)
			downloadThread.addTask(new DownloadTask());
	}

	@Override
	public void handleDownloadThreadUpdate() {
		handler.post(new Runnable() {
			@Override
			public void run() {
				int completed = downloadThread.getTotalCompleted();
				int queued = downloadThread.getTotalQueued();

				progressBar.setMax(queued);
				progressBar.setProgress(0);
				progressBar.setProgress(completed);

				statusText.setText(String.format("Downloaded %d from %d",
						completed, queued));

				if (completed == queued)
					((Vibrator) getSystemService(VIBRATOR_SERVICE))
							.vibrate(100);

			}
		});
	}

}
