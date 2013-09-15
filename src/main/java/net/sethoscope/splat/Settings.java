package net.sethoscope.splat;

import net.sethoscope.splat.R;
import android.os.Bundle;
import android.os.Environment;
import android.widget.TextView;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class Settings extends Activity {
	static SharedPreferences sharedPref;
	final static String defaultPath = Environment.getExternalStorageDirectory().getPath()
			+ "/notifications/";

	private void loadSettings() {
		TextView field = (TextView) findViewById(R.id.pathField);
		String path = sharedPref.getString(getString(R.string.path_settings_key),
				                                defaultPath);
		field.setText(path);
	}
	
	private void saveSettings() {
		final TextView field = (TextView) findViewById(R.id.pathField);
		String path = String.valueOf(field.getText());
		if ( path.length() == 0 ) {
			path = defaultPath;
		}
		
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putString(getString(R.string.path_settings_key), path);
		editor.apply();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		sharedPref = getSharedPreferences(getString(R.string.settings_key),
				Context.MODE_PRIVATE);		
		loadSettings();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		saveSettings();
	}
}
