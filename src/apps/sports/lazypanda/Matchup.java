package apps.sports.lazypanda;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class Matchup extends Activity {
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		TextView textview = new TextView(this);
		textview.setText("This is the Matchup tab");
		setContentView(textview);
	}
}