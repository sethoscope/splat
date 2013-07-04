package net.sethoscope.splat;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.media.AudioManager;
import android.view.View;


public class Trigger extends Activity implements View.OnClickListener {

	AudioQueue queue;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_trigger);
		// Set the hardware buttons to control the music
        this.setVolumeControlStream(AudioManager.STREAM_NOTIFICATION);
        queue = new AudioQueue();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.trigger, menu);
		return true;
	}
	
	public void onClick (View view) {
		queue.play();
	}
}
