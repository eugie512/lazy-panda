package apps.sports.lazypanda;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

class PlayersAdapter extends ArrayAdapter<Player> {

	private ArrayList<Player> players;
	private static LayoutInflater inflater = null;

	public PlayersAdapter(Context c, int textViewResourceId,
			ArrayList<Player> players) {
		super(c, textViewResourceId, players);
		inflater = (LayoutInflater) c
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.players = players;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;

		Player player = players.get(position);
		if (player != null) {
			if (convertView == null) {
				v = inflater.inflate(R.layout.player_item, null);
			}

			// set player image
			ImageView pic = (ImageView) v.findViewById(R.id.player_pic);
			if (pic != null) {
				pic.setImageBitmap(player.getPicture());
			}

			// set player name
			TextView name = (TextView) v.findViewById(R.id.player_name);
			if (name != null) {
				name.setText(player.getName());
			}

			// set player position
			TextView pos = (TextView) v.findViewById(R.id.player_pos);
			if (name != null) {
				pos.setText(player.getPosition());
			}
		}
		return v;
	}
}