package apps.sports.lazypanda;

import org.scribe.builder.api.YahooApi;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;

public class YahooLoginHelper extends Activity {

	String reportTag = "YahooLoginHelper";

	OAuthService service;

	/**
	 * Saves the request token and secret. passing in null clears old values
	 * 
	 * @param settings
	 *            - SharedPreferences
	 * @param token
	 *            - request token
	 * @param secret
	 *            - request secret
	 */
	public void saveRequestToken(SharedPreferences settings, String token,
			String secret) {
		SharedPreferences.Editor editor = settings.edit();
		if (token == null) {
			editor.remove("requestToken");
		} else {
			editor.putString("requestToken", token);
		}
		if (secret == null) {
			editor.remove("requestSecret");
		} else {
			editor.putString("requestSecret", secret);
		}
		editor.commit();
	}

	/**
	 * Saves the access token and secret. passing in null clears old values
	 * 
	 * @param settings
	 *            - SharedPreferences
	 * @param token
	 *            - access token
	 * @param secret
	 *            - access secret
	 */
	public void saveAccessToken(SharedPreferences settings, String token,
			String secret) {
		SharedPreferences.Editor editor = settings.edit();
		if (token == null) {
			editor.remove("accessToken");
		} else {
			editor.putString("accessToken", token);
		}
		if (secret == null) {
			editor.remove("accessSecret");
		} else {
			editor.putString("accessSecret", secret);
		}
		editor.commit();
	}

	/**
	 * Kill all credentials. Delete request and access tokens
	 * 
	 * @param prefs
	 *            - targets oauthSaves preferences
	 */
	public void killTokens(SharedPreferences prefs) {
		saveRequestToken(prefs, null, null);
		saveAccessToken(prefs, null, null);
	}

	/**
	 * Saves the OAuth session handle for refresh purposes
	 * 
	 * @param settings
	 *            - Shared OAuth saves
	 * @param session
	 *            - The session handle
	 */
	public void saveOAuthSessionHandle(SharedPreferences settings,
			String session) {
		SharedPreferences.Editor editor = settings.edit();
		if (session == null) {
			editor.remove("sessionHandle");
		} else {
			editor.putString("sessionHandle", session);
		}
		editor.commit();
	}

	/**
	 * Refresh the Yahoo access tokens that expire after 1 hour limit. Uses the
	 * OAuth session handle and tokens saved in the OAuth SharedPrefs
	 * 
	 * @param settings
	 *            - Shared OAuth saves
	 */
	public void refreshAccessToken(SharedPreferences settings,
			OAuthService service) {
		Token accessToken = new Token(settings.getString("accessToken", null),
				settings.getString("accessSecret", null));

		OAuthRequest request = new OAuthRequest(Verb.GET,
				"https://api.login.yahoo.com/oauth/v2/get_token");

		Log.d(reportTag,
				"sessionHandle is " + settings.getString("sessionHandle", null));

		request.addOAuthParameter("oauth_session_handle",
				settings.getString("sessionHandle", null));
		service.signRequest(accessToken, request);
		Response response = request.send();

		try {
			accessToken = YahooApi.class.newInstance()
					.getAccessTokenExtractor().extract(response.getBody());
			saveAccessToken(settings, accessToken.getToken(),
					accessToken.getSecret());
		} catch (IllegalAccessException e) {
			Log.d(reportTag,
					"refreshAccessToken IllegalAccessException "
							+ e.getMessage());
		} catch (InstantiationException e) {
			Log.d(reportTag,
					"refreshAccessToken InstantiationException "
							+ e.getMessage());
		}
	}
}