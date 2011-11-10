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

public class MyLeague extends ListActivity {

	String reportTag = "MyLeague";

	Context ctx;
	YahooQuery querier;
	private ArrayList<Team> teamsArrayList;
	private TeamsAdapter adapter;
	int teamCnt = 0;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent oldIntent = getIntent();
		final int team_index = oldIntent.getIntExtra("team_index", 100);
		if (team_index == 100) {
			Log.d(reportTag, "Team Index is OOB");
		} else {
			Log.d(reportTag, "Team Index is " + team_index);
		}

		ctx = this;
		querier = new YahooQuery(ctx);
		teamsArrayList = new ArrayList<Team>();

		SharedPreferences settings = getSharedPreferences("oAuthSaves", 0);

		Token accessToken = new Token(settings.getString("accessToken", null),
				settings.getString("accessSecret", null));

		OAuthService service = new ServiceBuilder().provider(YahooApi.class)
				.apiKey(getString(R.string.api_key))
				.apiSecret(getString(R.string.api_secret))
				.callback(getString(R.string.callback)).build();

		String RESTquery = "http://query.yahooapis.com/v1/yql/eugie/getLeaguesStandings?game_key="
				+ getString(R.string.game_key) + "&format=json&callback=";
		Response response;
		OAuthRequest request = new OAuthRequest(Verb.GET, RESTquery);
		service.signRequest(accessToken, request);
		response = request.send();
		String JSONresp = response.getBody();

		// get number of players in the team's roster
		try {
			JSONObject top = new JSONObject(JSONresp);
			JSONObject query = top.getJSONObject("query");
			JSONObject results = query.getJSONObject("results");
			JSONArray leagues = results.getJSONArray("league");
			JSONObject league = leagues.getJSONObject(team_index);
			JSONObject standings = league.getJSONObject("standings");
			JSONObject teams = standings.getJSONObject("teams");
			teamCnt = teams.getInt("count");
			JSONArray teamList = teams.getJSONArray("team");

			for (int i = 0; i < teamCnt; i++) {
				teamsArrayList.add(new Team());
			}

			for (int i = 0; i < teamCnt; i++) {
				JSONObject team = teamList.getJSONObject(i);
				new backgroundLoadListView().execute(team);
			}

		} catch (Exception e) {
			Log.d(reportTag,
					"league standings count produce Exception:"
							+ e.getMessage());
		}

		// create the adapter that we will attach the listener below to
		// adapter = new TeamsAdapter(this, R.layout.team_item, teamsArrayList);
		// setListAdapter(adapter);

		// process action for listView. Specifically, open analysis activity
		// when player name is clicked
		ListView lv = getListView();
		lv.setTextFilterEnabled(true);
		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				// launch activity

			}
		});
	}

	/**
	 * 
	 * @author Panda
	 * 
	 */
	public class backgroundLoadListView extends
			AsyncTask<JSONObject, Void, Team> {

		@Override
		protected Team doInBackground(JSONObject... team) {
			Team teamTemp = new Team();
			try {
				teamTemp.setName(team[0].getString("name"));

				JSONObject team_logos = team[0].getJSONObject("team_logos");
				JSONObject team_logo = team_logos.getJSONObject("team_logo");
				String img_url = team_logo.getString("url");

				Bitmap img = BitmapFactory.decodeStream(new URL(img_url)
						.openConnection().getInputStream());
				teamTemp.setTeamLogo(img);

				JSONObject team_points = team[0].getJSONObject("team_points");
				teamTemp.setPts(Float.valueOf(team_points.getString("total"))
						.floatValue());

				JSONObject team_standings = team[0]
						.getJSONObject("team_standings");
				teamTemp.setRank(team_standings.getInt("rank"));

				JSONObject outcome_totals = team_standings
						.getJSONObject("outcome_totals");
				teamTemp.setWins(outcome_totals.getInt("wins"));
				teamTemp.setLosses(outcome_totals.getInt("losses"));
				teamTemp.setTies(outcome_totals.getInt("ties"));

				JSONObject streak = team_standings.getJSONObject("streak");
				teamTemp.setStreakType(streak.getString("type"));
				teamTemp.setStreakValue(streak.getInt("value"));
			} catch (Exception e) {
				Log.d(reportTag,
						"backgroundGrab produce Exception:" + e.getMessage());
			}

			return teamTemp;
		}

		@Override
		protected void onPostExecute(Team team) {
			teamsArrayList.set((team.getRank() - 1), team);
			adapter = new TeamsAdapter(getParent(), R.layout.team_item,
					teamsArrayList);
			setListAdapter(adapter);
		}
	}
}