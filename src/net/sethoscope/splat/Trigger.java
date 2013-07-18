package net.sethoscope.splat;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.MenuItem;
import android.media.AudioManager;
import android.view.View;
import android.util.Log;


public class Trigger extends Activity implements View.OnClickListener {

	AudioQueue queue;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_trigger);
		
		// Set the hardware buttons to control the music
        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
	}

	String getPath() {
		Log.d("Trigger", "getting path");
		SharedPreferences sharedPref = 
				getSharedPreferences(getString(R.string.settings_key),
						Context.MODE_PRIVATE);
		final String path = sharedPref.getString(getString(R.string.path_settings_key),
				Settings.defaultPath);
		Log.d("Trigger", "got path: " + path);
		return path;
	}	
	
	@Override
	protected void onStart() {
		super.onStart();
		queue = new AudioQueue(getPath());
		Log.d("Trigger", "started");
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		queue.releaseResources();
		queue = null;
		Log.d("Trigger", "stopped");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.trigger, menu);
		
		final MenuItem about_item = menu.findItem(R.id.action_about);
        final Intent about_intent = new Intent(this, About.class);
        about_item.setIntent(about_intent);        		
		
        final MenuItem settings_item = menu.findItem(R.id.action_settings);
        final Intent settings_intent = new Intent(this, Settings.class);
        settings_item.setIntent(settings_intent);        		
		
        return true;
	}
	
	public void onClick (View view) {
		queue.play();
	}
}
