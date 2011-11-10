package apps.sports.lazypanda;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class About extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);

		TextView about = (TextView) findViewById(R.id.about_info);
		
		about.setOnClickListener(new TextView.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
}