package apps.sports.lazypanda;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class TeamsAdapter extends ArrayAdapter<Team> {

	private ArrayList<Team> teams;
	private static LayoutInflater inflater = null;

	public TeamsAdapter(Context c, int textViewResourceId, ArrayList<Team> teams) {
		super(c, textViewResourceId, teams);
		inflater = (LayoutInflater) c
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.teams = teams;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;

		Team team = teams.get(position);

		if (team != null) {
			if (convertView == null) {
				v = inflater.inflate(R.layout.team_item, null);
			}

			// set team ranking
			TextView rank = (TextView) v.findViewById(R.id.team_rank);
			if (rank != null) {
				rank.setText(Integer.toString(position + 1));
			}

			// set team image
			ImageView logo = (ImageView) v.findViewById(R.id.team_logo);
			if (logo != null) {
				logo.setImageBitmap(team.getTeamLogo());
			}

			// set team name
			TextView name = (TextView) v.findViewById(R.id.team_name);
			if (name != null) {
				name.setText(team.getName());
			}

			// set win, loss, tie
			TextView wins = (TextView) v.findViewById(R.id.team_win_to_loss);
			if (wins != null) {
				wins.setText(Integer.toString(team.getWins()) + "-"
						+ Integer.toString(team.getLosses()) + "-"
						+ Integer.toString(team.getTies()));
			}

			// set team points
			TextView pts = (TextView) v.findViewById(R.id.team_pts);
			if (pts != null) {
				pts.setText(Float.toString(team.getPts()));
			}

			// set team streak
			TextView streak = (TextView) v.findViewById(R.id.team_streak);
			if (streak != null) {
				if (team.getStreakType().equals("win")) {
					streak.setText("W-"
							+ Integer.toString(team.getStreakValue()));
				} else {
					streak.setText("L-"
							+ Integer.toString(team.getStreakValue()));
				}

			}
		}

		return v;
	}
}