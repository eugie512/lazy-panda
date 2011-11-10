package apps.sports.lazypanda;

import java.util.ArrayList;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class TopProspects extends ListActivity {

	String reportTag = "TopProspects";

	Context ctx;
	YahooQuery querier;
	private ArrayList<Player> topPlayers;
	private PlayersAdapter adapter;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent oldIntent = getIntent();
		final String league_id = oldIntent.getStringExtra("league_id");

		ctx = this;
		querier = new YahooQuery(ctx);
		topPlayers = new ArrayList<Player>();

		String response = querier
				.formPlayersQuery(getString(R.string.game_key));

		// set members of Player to send to list adapter
		for (int i = 0; i < 15; i++) {
			Player playerTemp = new Player();
			try {
				playerTemp = querier.procPlayersQuery(response, i);
			} catch (Exception e) {
				Log.d(reportTag,
						"playerTemp produce Exception:" + e.getMessage());
			}
			topPlayers.add(playerTemp);
		}

		// create the adapter that we will attach the listener below to
		adapter = new PlayersAdapter(this, R.layout.player_item, topPlayers);
		setListAdapter(adapter);

		// process action for listView. Specifically, open analysis activity
		// when player name is clicked
		ListView lv = getListView();
		lv.setTextFilterEnabled(true);
		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				Intent i = new Intent(getApplicationContext(), PlayerData.class);

				// add extras to pass to PlayerData view
				i.putExtra("player_id", adapter.getItem(position).getID());
				i.putExtra("player_pic", adapter.getItem(position).getPicture());
				i.putExtra("player_name", adapter.getItem(position).getName());
				i.putExtra("player_pos", adapter.getItem(position)
						.getPosition());
				i.putExtra("league_id", league_id);

				// add scores
				startActivity(i);
			}
		});
	}
}