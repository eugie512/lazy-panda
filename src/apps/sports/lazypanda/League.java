package apps.sports.lazypanda;

public class League {

	private String league_id;
	private String league_name;
	private String current_week;
	private Team[] teams; // ordered by standings

	public void setCurrentWeek(String s) {
		current_week = s;
	}

	public String getCurrentWeek() {
		return current_week;
	}
}