package net.sethoscope.splat;

import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Random;

import android.media.SoundPool;
import android.media.AudioManager;

public class AudioQueue{
	SoundPool pool;
	final int numSimultaneousSounds = 5;
	final int poolBufferSize = 8;  // pre-load this many sounds 
	ArrayList<String> filenames = new ArrayList<String>();
	PriorityQueue<Integer> queue = new PriorityQueue<Integer>();
	Random rand = new Random();
	
	public AudioQueue() {
		
		pool = new SoundPool(numSimultaneousSounds, AudioManager.STREAM_NOTIFICATION, 0);
		
		filenames.add("/mnt/sdcard/notifications/flintstone-paper-delivery.mp3");
		filenames.add("/mnt/sdcard/notifications/fred-drops-the-ball.mp3");
		filenames.add("/mnt/sdcard/notifications/hangout_dingtone.m4a");
		filenames.add("/mnt/sdcard/notifications/hangout_ringtone.m4a");
		filenames.add("/mnt/sdcard/notifications/horn.mp3");
		filenames.add("/mnt/sdcard/notifications/Jetson's Entrance Tube Arrive.mp3");
		filenames.add("/mnt/sdcard/notifications/pixie_and_dixie.mp3");
		filenames.add("/mnt/sdcard/notifications/sad trombone-sadder.mp3");
		filenames.add("/mnt/sdcard/notifications/screaming-goat.ogg");
		filenames.add("/mnt/sdcard/notifications/squeeze chicken.mp3");
		filenames.add("/mnt/sdcard/notifications/Squish Pop.mp3");
		filenames.add("/mnt/sdcard/notifications/success-achievement unlocked.mp3");
		filenames.add("/mnt/sdcard/notifications/success-choir.mp3");
		filenames.add("/mnt/sdcard/notifications/success-level up.mp3");
		filenames.add("/mnt/sdcard/notifications/thunk-twang.mp3");
		filenames.add("/mnt/sdcard/notifications/Waheef and Poof.mp3");
		filenames.add("/mnt/sdcard/notifications/Whisker_Pluck.mp3");
		filenames.add("/mnt/sdcard/notifications/Wilhelm_tk1.wav");
		filenames.add("/mnt/sdcard/notifications/Wilhelm_tk3.wav");
		filenames.add("/mnt/sdcard/notifications/Wilhelm_tk4.wav");
		
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
