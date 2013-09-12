package net.sethoscope.splat;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.app.Activity;
import android.content.Intent;

public class About extends Activity implements View.OnClickListener {

	private CharSequence removePrefix(CharSequence chars, String prefix) {
		if  ( chars.toString().startsWith(prefix) ) {
			return chars.subSequence(prefix.length(), chars.length());
		}
		return chars;
	}
	
	private CharSequence removeSuffix(CharSequence chars, String suffix) {
		if  ( chars.toString().endsWith(suffix) ) {
			return chars.subSequence(0, chars.length() - suffix.length());
		}
		return chars;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		
		findViewById(R.id.aboutText).setOnClickListener(this);
		
		TextView urlView = (TextView) findViewById(R.id.aboutUrl);
		urlView.setOnClickListener(this);

		// make the URL prettier
		CharSequence url_text = urlView.getText();
		url_text = removePrefix(url_text, "http://");
		url_text = removePrefix(url_text, "https://");
		url_text = removeSuffix(url_text, "/");
		urlView.setText(url_text);	
	}

	@Override
	public void onClick(View v) {
		final String url = getResources().getString(R.string.url);
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse(url));
		startActivity(intent);
	}
}
