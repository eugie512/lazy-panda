package apps.sports.lazypanda;

import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.YahooApi;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class MyTeam extends ListActivity {

	String reportTag = "MyTeam";

	Context ctx;
	YahooQuery querier;
	private ArrayList<Player> playersArrayList;
	private PlayersAdapter adapter;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent oldIntent = getIntent();
		final String league_id = oldIntent.getStringExtra("league_id");
		final int team_index = oldIntent.getIntExtra("team_index", 100);
		if (team_index == 100) {
			Log.d(reportTag, "Team Index is OOB");
		} else {
			Log.d(reportTag, "Team Index is " + team_index);
		}

		ctx = this;
		querier = new YahooQuery(ctx);
		playersArrayList = new ArrayList<Player>();

		SharedPreferences settings = getSharedPreferences("oAuthSaves", 0);

		Token accessToken = new Token(settings.getString("accessToken", null),
				settings.getString("accessSecret", null));

		OAuthService service = new ServiceBuilder().provider(YahooApi.class)
				.apiKey(getString(R.string.api_key))
				.apiSecret(getString(R.string.api_secret))
				.callback(getString(R.string.callback)).build();

		String RESTquery = "http://query.yahooapis.com/v1/yql/eugie/getTeamsRoster?game_key="
				+ getString(R.string.game_key) + "&format=json&callback=";
		Response response;
		OAuthRequest request = new OAuthRequest(Verb.GET, RESTquery);
		service.signRequest(accessToken, request);
		response = request.send();
		String JSONresp = response.getBody();

		// get number of players in the team's roster
		int playerCnt = 0;
		try {
			JSONObject top = new JSONObject(JSONresp);
			JSONObject query = top.getJSONObject("query");
			JSONObject results = query.getJSONObject("results");
			JSONArray teams = results.getJSONArray("team");
			JSONObject team = teams.getJSONObject(team_index);
			JSONObject roster = team.getJSONObject("roster");
			JSONObject players = roster.getJSONObject("players");
			playerCnt = players.getInt("count");
			JSONArray playersList = players.getJSONArray("player");

			for (int i = 0; i < playerCnt; i++) {
				playersArrayList.add(new Player());
			}

			for (int i = 0; i < playerCnt; i++) {
				JSONObject player = playersList.getJSONObject(i);
				new backgroundLoadListView(i).execute(player);
			}
		} catch (Exception e) {
			Log.d(reportTag, "playerTemp produce Exception:" + e.getMessage());
		}

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
				overridePendingTransition(R.anim.push_left_in,
						R.anim.push_left_out);
			}
		});
	}

	/**
	 * 
	 * @author Panda
	 * 
	 */
	public class backgroundLoadListView extends
			AsyncTask<JSONObject, Void, Player> {

		int posInList;

		public backgroundLoadListView(int i) {
			posInList = i;
		}

		@Override
		protected Player doInBackground(JSONObject... player) {
			Player playerTemp = new Player();
			try {
				playerTemp.setID(player[0].getString("player_id"));

				String img_url = player[0].getString("image_url");
				Bitmap img = BitmapFactory.decodeStream(new URL(img_url)
						.openConnection().getInputStream());
				playerTemp.setPicture(img);

				playerTemp.setPosition(player[0].getString("display_position"));

				JSONObject name = player[0].getJSONObject("name");
				playerTemp.setName(name.getString("full"));
			} catch (Exception e) {
				Log.d(reportTag,
						"backgroundGrab produce Exception:" + e.getMessage());
			}
			return playerTemp;
		}

		@Override
		protected void onPostExecute(Player player) {
			playersArrayList.set(posInList, player);
			adapter = new PlayersAdapter(getParent(), R.layout.player_item,
					playersArrayList);
			setListAdapter(adapter);
		}
	}
}