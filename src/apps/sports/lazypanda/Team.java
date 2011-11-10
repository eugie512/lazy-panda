package apps.sports.lazypanda;

import android.graphics.Bitmap;

public class Team {

	private String team_key;
	private String team_id;
	private String name;
	private Bitmap team_logo;
	private String waiver_priority;
	private String number_of_moves;
	private float total_pts;
	private int rank;
	private int wins;
	private int losses;
	private int ties;
	private float percentage;
	private String streak_type;
	private int streak_value;

	public Team() {
		team_key = "257.l.172775.t.1";
		team_id = "1";
		streak_type = "win";
	}

	public void setTeamKey(String t) {
		team_key = t;
	}

	public String getTeamKey() {
		return team_key;
	}

	public void setTeamLogo(Bitmap pic) {
		team_logo = pic;
	}

	public Bitmap getTeamLogo() {
		return team_logo;
	}

	public void setName(String n) {
		name = n;
	}

	public String getName() {
		return name;
	}

	public void setPts(float p) {
		total_pts = p;
	}

	public float getPts() {
		return total_pts;
	}

	public void setRank(int r) {
		rank = r;
	}

	public int getRank() {
		return rank;
	}

	public void setWins(int w) {
		wins = w;
	}

	public int getWins() {
		return wins;
	}

	public void setLosses(int l) {
		losses = l;
	}

	public int getLosses() {
		return losses;
	}

	public void setTies(int t) {
		ties = t;
	}

	public int getTies() {
		return ties;
	}

	public void setStreakType(String t) {
		streak_type = t;
	}

	public String getStreakType() {
		return streak_type;
	}

	public void setStreakValue(int v) {
		streak_value = v;
	}

	public int getStreakValue() {
		return streak_value;
	}
}