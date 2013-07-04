package net.sethoscope.splat;

import java.util.HashSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Random;
import java.io.File;
import android.media.SoundPool;
import android.media.AudioManager;
import android.os.Environment;

public class AudioQueue{
	SoundPool pool;
	HashSet<Integer> loadedIds = new HashSet<Integer>();
	final int numSimultaneousSounds = 5;
	final int poolBufferSize = 8;  // pre-load this many sounds 
	ArrayList<String> filenames = new ArrayList<String>();
	PriorityQueue<Integer> queue = new PriorityQueue<Integer>();
	Random rand = new Random();
	
	public AudioQueue() {
		pool = new SoundPool(numSimultaneousSounds, AudioManager.STREAM_NOTIFICATION, 0);
		String directory = Environment.getExternalStorageDirectory().getPath() + "/notifications/";
		File f = new File(directory);
		String []filenameList = f.list();
		for ( int i=0; i < filenameList.length; ++i ) {
			filenames.add(directory + filenameList[i]);
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
		loadedIds.add(soundId);
		queue.add(soundId);
	}
	
	/*
	 * Unloads sounds from the pool if they're no longer in the queue.
	 */
	public void unloadSounds() {
		// Modifying a set invalidates its iterator, so we use a copy.
		final HashSet<Integer> loadedIdsCopy = new HashSet<Integer>(loadedIds);
		Iterator<Integer> iter = loadedIdsCopy.iterator();
		while ( iter.hasNext() ) {
			Integer id = iter.next();
			if ( ! queue.contains(id) ) {
				loadedIds.remove(id);
			}
		}
	}
	
	public void play() {
		final Integer id_obj = queue.poll();
		if ( id_obj == null ) {
			return;  // the queue was empty
		}
		play(id_obj.intValue());
		refillQueue();
		unloadSounds();  // TODO: move this to a background thread
	}
	
	private void play(int soundId) {
		final float volume = (float) 1.0;
		final int priority = 1;
		final int loop = 0;
		final float rate = (float) 1.0;
		pool.play(soundId, volume, volume, priority, loop, rate);
	}
}
