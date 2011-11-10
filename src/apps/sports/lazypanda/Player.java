package apps.sports.lazypanda;

import android.graphics.Bitmap;

public class Player {

	private Bitmap picture;
	private String name;
	private String position;
	private String id;

	public void setPicture(Bitmap pic) {
		picture = pic;
	}
	
	public Bitmap getPicture() {
		return picture;
	}

	public void setName(String n) {
		name = n;
	}
	
	public String getName() {
		return name;
	}

	public void setPosition(String p) {
		position = p;
	}	

	public String getPosition() {
		return position;
	}

	public void setID(String x) {
		id = x;
	}
	
	public String getID() {
		return id;
	}
}