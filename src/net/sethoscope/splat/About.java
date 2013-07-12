package net.sethoscope.splat;

import android.os.Bundle;
import android.webkit.WebView;
import android.app.Activity;

public class About extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		
		WebView webView = (WebView)findViewById(R.id.aboutContent);
		String content = String.valueOf(getResources().getString(R.string.about_content));
		webView.loadData(content, "html/text", "utf-8");
	}
}
