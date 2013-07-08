package net.sethoscope.splat;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.logging.Logger;
import java.io.File;
import android.os.Environment;
import android.media.SoundPool;
import android.media.AudioManager;

class Sound implements Comparable<Sound> {

	/*
	 * The Sound class keeps track of the filename we loaded the sound from, the
	 * id it has in the SoundPool, how long it is (except not really), and when
	 * the most recently started playing of it is expected to finish.
	 * 
	 * This is all so we can load up sounds in advance (in the SoundPool) and
	 * unload them when we are truly done with them, but not a moment before.
	 * SoundPool is not very helpful in this regard, as it won't tell us what
	 * it's up to. So instead of just asking it what it's playing, we keep track
	 * of when we started things and when they will finish.
	 * 
	 * That would probably work beautifully if we had any idea how long each
	 * sound was. In theory, we store that in the duration field, but in reality
	 * I haven't found anywhere to get that information, so we just go with
	 * "all sounds are 20 seconds long" and hope for the best. It's not clear
	 * what happens when a sound is unloaded while it's playing. The audio is
	 * not interrupted, but I haven't dug deep enough to see if the memory ever
	 * gets freed.
	 */

	String filename;
	int id;
	long duration;
	Long timeFinished; // time in milliseconds this sound will be done playing
	final static long SAFETY_MARGIN = 1000; // ms extra to wait
	final static long DEFAULT_SOUND_DURATION = 20 * 1000;

	Sound(String filename, int id) {
		this.filename = filename;
		this.id = id;
		duration = DEFAULT_SOUND_DURATION;
	}

	public void updateFinishTime() {
		timeFinished = Long.valueOf(System.currentTimeMillis() + duration);
	}

	@Override
	public int compareTo(Sound other) {
		return timeFinished.compareTo(other.timeFinished);
	}

	public boolean isDone() {
		return (System.currentTimeMillis() + SAFETY_MARGIN) > timeFinished;
	}
}

public class AudioQueue {
	private final static Logger LOGGER = Logger.getLogger(AudioQueue.class
			.getName());
	SoundPool pool;
	final int numSimultaneousSounds = 5;
	final int poolBufferSize = 8; // pre-load this many sounds
	ArrayList<String> filenames = new ArrayList<String>();
	HashMap<String, Sound> loadedSounds = new HashMap<String, Sound>();
	PriorityQueue<Sound> unloadQueue = new PriorityQueue<Sound>();
	ArrayDeque<Sound> playQueue = new ArrayDeque<Sound>(poolBufferSize);
	Random rand = new Random();

	public AudioQueue() {
		pool = new SoundPool(numSimultaneousSounds,
				AudioManager.STREAM_NOTIFICATION, 0);
		String directory = Environment.getExternalStorageDirectory().getPath()
				+ "/notifications/";
		File f = new File(directory);
		String[] filenameList = f.list();
		if (filenameList != null) {
			for (int i = 0; i < filenameList.length; ++i) {
				filenames.add(directory + filenameList[i]);
			}
		}
		refillPlayQueue();
	}

	void refillPlayQueue() {
		if (filenames.size() > 0) {
			while (playQueue.size() < poolBufferSize) {
				enqueueRandom();
			}
		}
	}

	void enqueueRandom() {
		final int index = rand.nextInt(filenames.size());
		final String filename = filenames.get(index);
		Sound sound = loadedSounds.get(filename);
		if (sound == null) {
			final int soundId = pool.load(filename, 1);
			sound = new Sound(filename, soundId);
			loadedSounds.put(filename, sound);
		}
		LOGGER.info("queueing " + filename);
		playQueue.add(sound);
		unloadQueue.remove(sound);
	}

	/*
	 * Unload sounds from the pool if we're done with them.
	 */
	public void unloadSounds() {
		while (!unloadQueue.isEmpty() && unloadQueue.peek().isDone()) {
			Sound sound = unloadQueue.remove();
			if (!playQueue.contains(unloadQueue.peek())) {
				loadedSounds.remove(sound.filename);
				pool.unload(sound.id);
				LOGGER.info("unloaded " + sound.filename);
			} else {
				LOGGER.warning("tried to unload queued sound: "
						+ sound.filename);
			}
		}
	}

	/*
	 * Play the next sound.
	 */
	public void play() {
		final Sound sound = playQueue.poll();
		if (sound == null) {
			return; // the queue was empty
		}
		play(sound.id);
		LOGGER.info("playing " + sound.filename);		
		unloadQueue.remove(sound);
		sound.updateFinishTime();
		if (!playQueue.contains(sound)) {
			unloadQueue.add(sound);
		}
		
		// These could happen in another thread, but it seems fast enough.
		refillPlayQueue();
		unloadSounds();
	}

	private void play(int soundId) {
		final float volume = (float) 1.0;
		final int priority = 1;
		final int loop = 0;
		final float rate = (float) 1.0;
		pool.play(soundId, volume, volume, priority, loop, rate);
	}
}
