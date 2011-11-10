package apps.sports.lazypanda;

import org.json.JSONArray;
import org.json.JSONObject;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.YahooApi;
import org.scribe.oauth.OAuthService;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.Button;
import android.widget.LinearLayout;
import apps.sports.lazypanda.R.layout;

public class LazyPanda extends Activity implements OnClickListener {

	String reportTag = "LazyPanda";

	LinearLayout teamButtonsLayout;
	Button logButton;
	Button optionsButton;
	Button aboutButton;

	Context ctx;
	YahooQuery querier;
	SharedPreferences oAuthSaves;
	public String mToken;
	public String mSecret;

	static final int DIALOG_KILL = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		Log.d(reportTag, "onCreate hit");

		ctx = this;
		querier = new YahooQuery(ctx);

		oAuthSaves = getSharedPreferences("oAuthSaves", 0);
		mToken = oAuthSaves.getString("accessToken", null);
		mSecret = oAuthSaves.getString("accessSecret", null);

		logButton = (Button) findViewById(R.id.log_button);
		optionsButton = (Button) findViewById(R.id.options_button);
		aboutButton = (Button) findViewById(R.id.about_button);

		if ((mToken == null) || (mSecret == null)) {
			teamButtonsLayout = (LinearLayout) findViewById(R.id.main_team_lists);
			teamButtonsLayout.removeAllViews();
			logButton.setText(R.string.login_label);
		} else {
			logButton.setText(R.string.logout_label);
		}

		logButton.setOnClickListener(this);
		optionsButton.setOnClickListener(this);
		aboutButton.setOnClickListener(this);
	}

	@Override
	public void onStart() {
		super.onStart();

		Log.d(reportTag, "onStart hit");

		ctx = this;
		querier = new YahooQuery(ctx);

		YahooLoginHelper loginHelper = new YahooLoginHelper();
		oAuthSaves = getSharedPreferences("oAuthSaves", 0);
		final OAuthService service = new ServiceBuilder()
				.provider(YahooApi.class).apiKey(getString(R.string.api_key))
				.apiSecret(getString(R.string.api_secret))
				.callback(getString(R.string.callback)).build();
		loginHelper.refreshAccessToken(oAuthSaves, service);

		if ((mToken == null) || (mSecret == null)) {
			Log.d(reportTag, "mtoken is null for setting buttons");
			LinearLayout layout = (LinearLayout) findViewById(R.id.main_team_lists);
			layout.removeAllViews();
		} else {
			Log.d(reportTag, "mtoken has value for setting buttons");
			LinearLayout layout = (LinearLayout) findViewById(R.id.main_team_lists);
			if (layout.getChildCount() > 0) {
				layout.removeAllViews();
			}
			generateTeamButtons();
		}
	}

	/**
	 * onClick
	 * 
	 * @param v
	 */
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.log_button:
			Intent login;

			// If the button is login mode
			if ((mToken == null) || (mSecret == null)) {
				login = new Intent(this, YahooLogin.class);
				startActivity(login);
			}
			// If the button is in logout mode
			else {
				CookieSyncManager.createInstance(this);
				showDialog(DIALOG_KILL);
			}
			break;

		case R.id.about_button:
			Intent about = new Intent(this, About.class);
			startActivity(about);
			break;
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		AlertDialog alert;
		switch (id) {
		case DIALOG_KILL:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Are you sure you want to logout?")
					.setCancelable(false)
					.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									CookieManager cookieman = CookieManager
											.getInstance();
									cookieman.removeAllCookie();
									CookieSyncManager.getInstance().sync();

									YahooLoginHelper loginHelper = new YahooLoginHelper();
									oAuthSaves = getSharedPreferences(
											"oauthSaves", 0);
									loginHelper.killTokens(oAuthSaves);

									mToken = oAuthSaves.getString(
											"accessToken", null);
									mSecret = oAuthSaves.getString(
											"accessSecret", null);

									if ((mToken == null) || (mSecret == null)) {
										logButton.setText(R.string.login_label);
										LinearLayout layout = (LinearLayout) findViewById(R.id.main_team_lists);
										layout.removeAllViews();
									} else {
										logButton
												.setText(R.string.logout_label);
									}
								}
							})
					.setNegativeButton("Cancel",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
								}
							});
			alert = builder.create();
			break;
		default:
			alert = null;
		}
		return alert;
	}

	/**
	 * Uses login ID to populate main layout with "my_teams." This is run at
	 * onCreate, onStart, and onResume. This is probably not a good
	 * implementation, but it's the only way to guarantee that the buttons get
	 * populated during any phase of application startup.
	 */
	public void generateTeamButtons() {

		Log.d(reportTag, "Generating buttons");

		// use the querier to get info on all leagues user is logged into.
		// add
		// buttons and set listeners to launch LazyBase with league context
		int count = 0;
		String[] league_names = null;
		String[] league_ids = null;
		String[] current_weeks = null;

		String response = querier.formLeagueQuery(getString(R.string.game_key));

		try {
			JSONObject top = new JSONObject(response);
			JSONObject query = top.getJSONObject("query");
			count = query.getInt("count"); // grab number of leagues

			league_names = new String[count];
			league_ids = new String[count];
			current_weeks = new String[count];

			JSONObject results = query.getJSONObject("results");
			JSONArray leagues = results.getJSONArray("league");
			for (int i = 0; i < count; i++) {
				JSONObject league = leagues.getJSONObject(i);
				league_names[i] = league.getString("name");
				league_ids[i] = league.getString("league_id");
				current_weeks[i] = league.getString("current_week");
			}
		} catch (Exception e) {
			Log.d(reportTag, "leagueGrab produce Exception:" + e.getMessage());
		}

		// repopulate the team lists layour. First remove all existing
		// buttons, then assign new buttons for each league that signed in
		// user is a part of. Finally, set league name to the buttons.
		LinearLayout layout = (LinearLayout) findViewById(R.id.main_team_lists);
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		layoutParams.setMargins(5, 5, 5, 5);

		for (int i = 0; i < count; i++) {
			Button testTeam = new Button(this);
			testTeam.setOnClickListener(returnClickListener(testTeam,
					league_ids[i], league_names[i], current_weeks[i], i));
			testTeam.setBackgroundResource(R.drawable.button_grey);
			testTeam.setText(league_names[i]);
			testTeam.setTextAppearance(this, R.style.ButtonText);
			layout.addView(testTeam, layoutParams);
		}
	}

	/**
	 * Creates and returns click listeners for dynamic use
	 * 
	 * @param button
	 *            - target button that you click listener is assigned to
	 * @param league_id
	 *            league ID value to use in YQL
	 * @return an OnClickListener for button to use
	 */
	View.OnClickListener returnClickListener(final Button button,
			final String league_id, final String league_name,
			final String current_week, final int index) {

		return new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i;

				i = new Intent(getApplicationContext(), LazyBase.class);
				i.putExtra("league_id", league_id);
				i.putExtra("league_name", league_name);
				i.putExtra("current_week", current_week);
				i.putExtra("team_index", index);
				startActivity(i);
				overridePendingTransition(R.anim.push_left_in,
						R.anim.push_left_out);
			}
		};
	}
}