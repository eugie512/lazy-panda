package apps.sports.lazypanda;

import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.YahooApi;
import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

/**
 * Class dedicated to Logging in with Scribe.
 * 
 */
public class YahooLogin extends Activity {

	String reportTag = "YahooLogin";
	
	SharedPreferences settings;
	Token requestToken;
	Button home;
	String authURL;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.yahoologin);

		initControls();

		// set up service and get request token as seen on scribe website
		// https://github.com/fernandezpablo85/scribe-java/wiki/Getting-Started
		final OAuthService service = new ServiceBuilder()
				.provider(YahooApi.class).apiKey(getString(R.string.api_key))
				.apiSecret(getString(R.string.api_secret))
				.callback(getString(R.string.callback)).build();

		requestToken = service.getRequestToken();
		authURL = service.getAuthorizationUrl(requestToken);

		settings = getSharedPreferences("oAuthSaves", 0);

		final WebView webView = (WebView) findViewById(R.id.webview);

		// attach WebViewClient to intercept the callback url
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {

				// check for our custom callback protocol otherwise use default
				// behavior
				if (url.startsWith("oauth")) {
					
					// authorization complete hide webview for now.
					webView.setVisibility(View.GONE);

					Uri uri = Uri.parse(url);
					String verifier = uri.getQueryParameter("oauth_verifier");
					Verifier v = new Verifier(verifier);

					// save token and also extract session handle
					Token accessToken = service.getAccessToken(requestToken, v);
					String fullResponse = accessToken.getRawResponse();
					Log.d(reportTag, "Raw Response is " + fullResponse);
					int startId = fullResponse
							.indexOf("&oauth_session_handle=");
					int endId = fullResponse.indexOf(
							"&oauth_authorization_expires_in", startId);
					String oauthSessionHandle = fullResponse.substring(
							startId + 22, endId);
					Log.d(reportTag, "OAuth Session Handle is "
							+ oauthSessionHandle);

					YahooLoginHelper loginHelper = new YahooLoginHelper();
					loginHelper.saveOAuthSessionHandle(settings, oauthSessionHandle);
					loginHelper.saveAccessToken(settings, accessToken.getToken(),
							accessToken.getSecret());
					loginHelper.saveRequestToken(settings, requestToken.getToken(),
							requestToken.getSecret());

					return true;
				}

				return super.shouldOverrideUrlLoading(view, url);
			}
		});

		// send user to authorization page
		webView.loadUrl(authURL);
	}
	
	/**
	 * Initialize the button to go home
	 */
	private void initControls() {
		home = (Button) findViewById(R.id.home_button);
		final Intent intent = new Intent(this, LazyPanda.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

		home.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(intent);
			}

		});
	}
}