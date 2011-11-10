package apps.sports.lazypanda;

import org.json.JSONException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphView.GraphViewSeries;
import com.jjoe64.graphview.GraphView.LegendAlign;
import com.jjoe64.graphview.LineGraphView;

public class PlayerData extends Activity implements OnClickListener {

	String reportTag = "PlayerData";

	Button backButton;
	ImageView playerPic;
	TextView playerName;
	TextView playerPos;
	GraphViewSeries actualScoreSeries;
	GraphViewSeries projScoreSeries;
	GraphView graphView;
	Context ctx;
	YahooQuery querier;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.playerdata);

		Intent oldIntent = getIntent();
		String league_id = oldIntent.getStringExtra("league_id");
		
		backButton = (Button) findViewById(R.id.playerdata_back_button);
		backButton.setOnClickListener(this);

		String response;
		double[] points = new double[5];

		ctx = this;
		querier = new YahooQuery(ctx);

		Intent i = getIntent();
		String player_id = i.getStringExtra("player_id");
		Bitmap player_pic = i.getParcelableExtra("player_pic");
		String player_text = i.getStringExtra("player_name");
		String player_pos = i.getStringExtra("player_pos");

		playerPic = (ImageView) findViewById(R.id.player_data_pic);
		playerPic.setImageBitmap(player_pic);
		playerName = (TextView) findViewById(R.id.player_data_name);
		playerName.setText(player_text);
		playerPos = (TextView) findViewById(R.id.player_data_pos);
		playerPos.setText(player_pos);

		for (int week = 1; week < 6; week++) {
			response = querier.formPlayersStatsQuery(
					getString(R.string.game_key), league_id, player_id, week);
			try {
				points[week - 1] = querier.procPlayerStatsQueryForPts(response);
			} catch (JSONException e) {
				Log.d(reportTag,
						"[PlayerData] process playerStats produce Exception:"
								+ e.getMessage());
			}
		}

		// Add graph using jjoe's graph view
		GraphViewData[] graphData = new GraphViewData[5];
		String[] hLabels = new String[5];

		for (int x = 0; x < 5; x++) {
			graphData[x] = new GraphViewData((x + 1), points[x]);
			hLabels[x] = "Week " + (x + 1);
		}

		graphView = new LineGraphView(this, "");
		actualScoreSeries = new GraphViewSeries("Actual Points", Color.WHITE,
				graphData);
		graphView.addSeries(actualScoreSeries);
		graphView.setHorizontalLabels(hLabels);
		graphView.setShowLegend(true);
		graphView.setLegendAlign(LegendAlign.BOTTOM);
		graphView.setLegendWidth(150);

		LinearLayout layout = (LinearLayout) findViewById(R.id.player_data_graph);
		layout.setPadding(5, 0, 5, 0);
		layout.addView(graphView);
	}

	/**
	 * onClick
	 * 
	 * @param v
	 */
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.playerdata_back_button:
			finish();
			overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
		}
	}
};