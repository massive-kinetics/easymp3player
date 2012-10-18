package com.massivekinetics.handlers;

import java.util.Random;

public class DownloadTask extends Thread {

	int lengthSec;
	private static final Random random = new Random();

	public DownloadTask() {
		lengthSec = random.nextInt(3) + 1;
	}

	@Override
	public void run() {
		try {
			Thread.sleep(lengthSec * 1000);
		} catch (InterruptedException e) {
		}
	}

}
