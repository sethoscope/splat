package net.sethoscope.splat;

import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Random;
import java.io.File;
import android.media.SoundPool;
import android.media.AudioManager;
import android.os.Environment;

public class AudioQueue{
	SoundPool pool;
	final int numSimultaneousSounds = 5;
	final int poolBufferSize = 8;  // pre-load this many sounds 
	ArrayList<String> filenames = new ArrayList<String>();
	PriorityQueue<Integer> queue = new PriorityQueue<Integer>();
	Random rand = new Random();
	
	public AudioQueue() {
		
		pool = new SoundPool(numSimultaneousSounds, AudioManager.STREAM_NOTIFICATION, 0);

		String root = Environment.getExternalStorageDirectory().getPath() + "/notifications/";
		//FileFinder finder = new FileFinder(root);
		File f = new File(root);
		String []filenameList = f.list();
		for ( int i=0; i < filenameList.length; ++i ) {
			filenames.add(root + filenameList[i]);
		}

		refillQueue();
	}
	
	void refillQueue() {
		while ( queue.size() < poolBufferSize ) {
			enqueueRandom();
		}
	}
	
	void enqueueRandom() {
		final int index = rand.nextInt(filenames.size());
		final int soundId = pool.load(filenames.get(index), 1);
		queue.add(soundId);
	}
	
	public void play() {
		final Integer id_obj = queue.poll();
		if ( id_obj == null ) {
			return;  // the queue was empty
		}
		play(id_obj.intValue());
		refillQueue();
	}
	
	private void play(int soundId) {
		final float volume = (float) 1.0;
		final int priority = 1;
		final int loop = 0;
		final float rate = (float) 1.0;
		pool.play(soundId, volume, volume, priority, loop, rate);
	}
}
