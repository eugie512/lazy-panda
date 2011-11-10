package apps.sports.lazypanda;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TextView;

public class LazyBase extends TabActivity implements OnClickListener {

	Button backButton;
	TextView leagueName;
	TextView currentWeek;
	TabHost tabHost;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lazybase);

		// get extras from previous activity
		Intent oldIntent = getIntent();
		String league_id = oldIntent.getStringExtra("league_id");
		String league_name = oldIntent.getStringExtra("league_name");
		String current_week = oldIntent.getStringExtra("current_week");
		int team_index = oldIntent.getIntExtra("team_index", 100);

		// set the league name and current week info from the intent. also set
		// the back button with onClick to return to main
		backButton = (Button) findViewById(R.id.lazybase_back_button);
		leagueName = (TextView) findViewById(R.id.lazybase_league_title);
		currentWeek = (TextView) findViewById(R.id.lazybase_league_current_title);
		leagueName.setText(league_name);
		currentWeek.setText("Week " + current_week);
		backButton.setOnClickListener(this);

		// reusables for generating tabWidget
		tabHost = getTabHost();
		View tabView;
		Intent intent;
		TabHost.TabSpec spec;

		// Create all tabs and add to tab host
		tabView = createTabView(tabHost.getContext(), "Alerts");
		intent = new Intent().setClass(this, Alerts.class);
		spec = tabHost.newTabSpec("alerts").setIndicator(tabView)
				.setContent(intent);
		tabHost.addTab(spec);
		
		tabView = createTabView(tabHost.getContext(), "League");
		intent = new Intent().setClass(this, MyLeague.class);
		intent.putExtra("league_id", league_id);
		intent.putExtra("team_index", team_index);
		spec = tabHost.newTabSpec("my_league").setIndicator(tabView)
				.setContent(intent);
		tabHost.addTab(spec);

		tabView = createTabView(tabHost.getContext(), "My Team");
		intent = new Intent().setClass(this, MyTeam.class);
		intent.putExtra("league_id", league_id);
		intent.putExtra("team_index", team_index);
		spec = tabHost.newTabSpec("my_team").setIndicator(tabView)
				.setContent(intent);
		tabHost.addTab(spec);
		
		tabView = createTabView(tabHost.getContext(), "Matchup");
		intent = new Intent().setClass(this, Matchup.class);
		spec = tabHost.newTabSpec("matchup").setIndicator(tabView)
				.setContent(intent);
		tabHost.addTab(spec);

		tabView = createTabView(tabHost.getContext(), "Watch List");
		intent = new Intent().setClass(this, WatchList.class);
		intent.putExtra("league_id", league_id);
		spec = tabHost.newTabSpec("watch_list").setIndicator(tabView)
				.setContent(intent);
		tabHost.addTab(spec);

		tabView = createTabView(tabHost.getContext(), "Fantasy News");
		intent = new Intent().setClass(this, FantasyNews.class);
		intent.putExtra("league_id", league_id);
		spec = tabHost.newTabSpec("news").setIndicator(tabView)
				.setContent(intent);
		tabHost.addTab(spec);

		// Enable tab strip and set the color. Note that the setLeft and Right
		// are not work properly right now. Will revisit at a later time
		// 10/6/2011
		// tabHost.getTabWidget().setLeftStripDrawable(getResources().getDrawable(R.drawable.custom_tab_strip_selector));
		// tabHost.getTabWidget().setRightStripDrawable(getResources().getDrawable(R.drawable.custom_tab_strip_selector));
		tabHost.getTabWidget().setStripEnabled(true);
		tabHost.getTabWidget().setCurrentTab(0);
	}

	/**
	 * Create the tab view that will be inflated within the tab widget
	 * 
	 * @param context
	 *            - Tabhost context
	 * @param text
	 *            - Label for the tab
	 * @return The view of the tab with label set
	 */
	private static View createTabView(final Context context, final String text) {
		View view = LayoutInflater.from(context).inflate(R.layout.custom_tab,
				null);
		TextView tv = (TextView) view.findViewById(R.id.tabText);
		tv.setText(text);
		return view;
	}

	/**
	 * onClick
	 * 
	 * @param v
	 */
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.lazybase_back_button:
			finish();
			overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
		}
	}
}