package apps.sports.lazypanda;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.YahooApi;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class YahooQuery {

	private String reportTag = "YahooQuery";
	private String PREFIX = "http://query.yahooapis.com/v1/yql/eugie/";
	private String SUFFIX = "?format=json&callback=";
	private String LEAGUES = "getLeagues";

	OAuthService service;
	SharedPreferences settings;
	Token accessToken;

	/**
	 * build service using the API keys passed in with context ctx. Access
	 * tokens found in the save preferences object oAuthSaves. Set access token
	 * that will be used to sign requests for JSON responses.
	 */
	public YahooQuery(Context ctx) {
		service = new ServiceBuilder().provider(YahooApi.class)
				.apiKey(ctx.getString(R.string.api_key))
				.apiSecret(ctx.getString(R.string.api_secret))
				.callback(ctx.getString(R.string.callback)).build();

		settings = ctx.getSharedPreferences("oAuthSaves", 0);

		accessToken = new Token(settings.getString("accessToken", null),
				settings.getString("accessSecret", null));
	}

	/**
	 * REST query (URL) is used to make a request. The JSON response is returned
	 * to be unpacked in application activities
	 * 
	 * @param restQuery
	 *            - the REST query using query aliases
	 * @return JSON response
	 */
	public String getJsonResp(String restQuery) {
		Response response;
		OAuthRequest request = new OAuthRequest(Verb.GET, restQuery);
		service.signRequest(accessToken, request);
		response = request.send();
		return response.getBody();
	}

	/**
	 * Return service
	 */
	public OAuthService getService() {
		return service;
	}

	/**
	 * Return the access token
	 */
	public Token getAccessToken() {
		return accessToken;
	}

	/**
	 * Get JSON response for ALL leagues that current user is signed into. The
	 * use_login flag is set to "1"
	 * 
	 * @param game_key
	 *            Yahoo game id (ex. 2011 Football is 257)
	 * @return JSON response
	 */
	public String formLeagueQuery(String game_id) {
		String query = "http://query.yahooapis.com/v1/yql?q=%20SELECT%20*%20FROM%20fantasysports.leagues%20WHERE%20game_key%3D%22"
				+ game_id
				+ "%22%20and%20use_login%3D%221%22&format=json&callback=";
		return getJsonResp(query);
	}

	/**
	 * Get JSON response for league with league_key. League key consists of a
	 * game ID and a league ID and follows format [game_id].l.[league_id]
	 * 
	 * @param game_id
	 * @param league_id
	 * @return JSON response
	 */
	public String formLeagueQuery(String game_id, String league_id) {
		String query = "http://query.yahooapis.com/v1/yql?q=select%20*%20from%20fantasysports.leagues%20where%20league_key%3D%22"
				+ game_id + ".l." + league_id + "%22&format=json&callback=";
		return getJsonResp(query);
	}

	/**
	 * Get JSON response for ALL league standings that current user is signed
	 * into. The use_login flag is set to "1"
	 * 
	 * @param game_id
	 *            Yahoo game id (ex. 2011 Football is 257)
	 * @return JSON response
	 */
	public String formLeagueStandingsQuery(String game_id) {
		String query = "http://query.yahooapis.com/v1/yql?q=select%20*%20from%20fantasysports.leagues.standings%20where%20game_key%3D%22"
				+ game_id
				+ "%22%20and%20use_login%3D%221%22&format=json&callback=";
		return getJsonResp(query);
	}

	/**
	 * Get JSON response for a "players" REST query
	 * 
	 * @param game_key
	 *            Yahoo game key (ex. 2011 Football is 257)
	 * @param player_key
	 *            Target player
	 * @return JSON response
	 */
	public String formPlayersQuery(String game_id, String player_id) {
		String query;
		query = "http://query.yahooapis.com/v1/yql?q=%20SELECT%20*%20FROM%20fantasysports.players%20WHERE%20player_key%3D%22"
				+ game_id + ".p." + player_id + "%22&format=json&callback=";
		Log.d(reportTag, "players query is: " + query);
		return getJsonResp(query);
	}

	/**
	 * Get JSON response for a "players" REST query
	 * 
	 * @param game_key
	 *            - Yahoo game key (ex. 2011 Football is 257)
	 * @return JSON response
	 */
	public String formPlayersQuery(String game_key) {
		String query;
		query = "http://query.yahooapis.com/v1/yql?q=%20SELECT%20*%20FROM%20fantasysports.players%20WHERE%20game_key%3D%22"
				+ game_key + "%22&format=json&callback=";
		Log.d(reportTag, "players query is: " + query);
		return getJsonResp(query);
	}

	/**
	 * Get JSON response for a "players_stats" REST query
	 * 
	 * @param league_id
	 * 
	 * @param player_id
	 * 
	 * @param stats_week
	 * @return JSON response
	 */
	public String formPlayersStatsQuery(String game_id, String league_id,
			String player_id, int stats_week) {
		String query;
		query = "http://query.yahooapis.com/v1/yql?q=%20SELECT%20*%20FROM%20fantasysports.players.stats%20WHERE%20league_key%3D%22"
				+ game_id
				+ ".l."
				+ league_id
				+ "%22%20and%20player_key%3D%22"
				+ game_id
				+ ".p."
				+ player_id
				+ "%22%20and%20stats_type%3D%22week%22%20and%20stats_week%3D%22"
				+ stats_week + "%22&format=json&callback=";
		return getJsonResp(query);
	}

	/**
	 * Get JSON response for a "teams_roster" query
	 * 
	 * @param game_id
	 * @return
	 */
	public String formTeamsRosterQuery(String game_id) {
		String query;
		query = "http://query.yahooapis.com/v1/yql?q=%20SELECT%20*%20FROM%20fantasysports.teams.roster%20WHERE%20game_key%3D%22"
				+ game_id
				+ "%22%20and%20use_login%3D%221%22&format=json&callback=";
		return getJsonResp(query);
	}

	/**
	 * Process a league query that targets a specific league user is logged into
	 * 
	 * @param resp
	 *            - JSON response
	 * @return Populated League object
	 * @throws JSONException
	 */
	public League procLeagueQuery(String resp) throws JSONException {
		League leagueTemp = new League();

		JSONObject top = new JSONObject(resp);
		JSONObject query = top.getJSONObject("query");
		JSONObject results = query.getJSONObject("results");
		JSONObject league = results.getJSONObject("league");
		leagueTemp.setCurrentWeek(league.getString("current_week"));
		return leagueTemp;
	}

	/**
	 * Process a player query using JSON
	 * 
	 * @param resp
	 *            - Response string
	 * @param id
	 *            - [Optional] If set, function player response with game_key
	 *            and uses "id" as index
	 * @throws JSONException
	 * @throws IOException
	 * @throws MalformedURLException
	 * @return Populated Player object
	 */
	public Player procPlayersQuery(String resp, int id) throws JSONException,
			MalformedURLException, IOException {
		Player playerTemp = new Player();
		String img_url;
		Bitmap img;

		JSONObject top = new JSONObject(resp);
		JSONObject query = top.getJSONObject("query");
		JSONObject results = query.getJSONObject("results");
		JSONArray players = results.getJSONArray("player");
		JSONObject player = players.getJSONObject(id);

		playerTemp.setID(player.getString("player_id"));

		img_url = player.getString("image_url");
		img = BitmapFactory.decodeStream(new URL(img_url).openConnection()
				.getInputStream());
		playerTemp.setPicture(img);

		playerTemp.setPosition(player.getString("display_position"));

		JSONObject name = player.getJSONObject("name");
		playerTemp.setName(name.getString("full"));

		return playerTemp;
	}

	/**
	 * Process a players_stats query for points. Used with formPlayerStatsQuery
	 * 
	 * @param resp
	 * @return points
	 * @throws JSONException
	 */
	public Double procPlayerStatsQueryForPts(String resp) throws JSONException {
		Double pts;
		JSONObject top = new JSONObject(resp);
		JSONObject query = top.getJSONObject("query");
		JSONObject results = query.getJSONObject("results");
		JSONObject player = results.getJSONObject("player");
		JSONObject player_points = player.getJSONObject("player_points");
		pts = player_points.getDouble("total");
		return pts;
	}

	/**
	 * Unpack the JSON response for a Player object. The player is indexed using
	 * the team index as well as the player index
	 * 
	 * @param resp
	 *            - JSON response
	 * @param team_index
	 * @param player_index
	 * @return Player object
	 * @throws JSONException
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public Player procTeamsRosterQuery(String resp, int team_index,
			int player_index) throws JSONException, MalformedURLException,
			IOException {

		Player playerTemp = new Player();
		String img_url;
		Bitmap img;

		JSONObject top = new JSONObject(resp);
		JSONObject query = top.getJSONObject("query");
		JSONObject results = query.getJSONObject("results");
		JSONArray teams = results.getJSONArray("team");
		JSONObject team = teams.getJSONObject(team_index);
		JSONObject roster = team.getJSONObject("roster");
		JSONObject playersList = roster.getJSONObject("players");
		JSONArray playerArr = playersList.getJSONArray("player");
		JSONObject player = playerArr.getJSONObject(player_index);

		playerTemp.setID(player.getString("player_id"));

		img_url = player.getString("image_url");
		img = BitmapFactory.decodeStream(new URL(img_url).openConnection()
				.getInputStream());
		playerTemp.setPicture(img);

		playerTemp.setPosition(player.getString("display_position"));

		JSONObject name = player.getJSONObject("name");
		playerTemp.setName(name.getString("full"));

		return playerTemp;
	}

	/**
	 * Unpack the JSON response for a Team object. The team is indexed using the
	 * team count
	 * 
	 * @param resp
	 * @param team_index
	 * @param team_count
	 * @return Team object
	 * @throws JSONException
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public Team procLeagueStandingsQuery(String resp, int team_index,
			int team_count) throws JSONException, MalformedURLException,
			IOException {

		Team teamTemp = new Team();
		String img_url;
		Bitmap img;

		JSONObject top = new JSONObject(resp);
		JSONObject query = top.getJSONObject("query");
		JSONObject results = query.getJSONObject("results");
		JSONArray leagues = results.getJSONArray("league");
		JSONObject league = leagues.getJSONObject(team_index);
		JSONObject standings = league.getJSONObject("standings");
		JSONObject teams = standings.getJSONObject("teams");
		JSONArray teamList = teams.getJSONArray("team");
		JSONObject team = teamList.getJSONObject(team_count);

		teamTemp.setName(team.getString("name"));

		JSONObject team_logos = team.getJSONObject("team_logos");
		JSONObject team_logo = team_logos.getJSONObject("team_logo");
		img_url = team_logo.getString("url");
		img = BitmapFactory.decodeStream(new URL(img_url).openConnection()
				.getInputStream());
		teamTemp.setTeamLogo(img);

		JSONObject team_points = team.getJSONObject("team_points");
		teamTemp.setPts(Float.valueOf(team_points.getString("total"))
				.floatValue());

		JSONObject team_standings = team.getJSONObject("team_standings");
		JSONObject outcome_totals = team_standings
				.getJSONObject("outcome_totals");
		teamTemp.setWins(outcome_totals.getInt("wins"));
		teamTemp.setLosses(outcome_totals.getInt("losses"));
		teamTemp.setTies(outcome_totals.getInt("ties"));

		JSONObject streak = team_standings.getJSONObject("streak");
		teamTemp.setStreakType(streak.getString("type"));
		teamTemp.setStreakValue(streak.getInt("value"));

		return teamTemp;
	}
}