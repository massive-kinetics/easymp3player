package com.massivekinetics.emp.adapters;

import java.util.List;

import android.view.ViewGroup;
import android.widget.TextView;

import com.massivekinetics.emp.R;
import com.massivekinetics.emp.data.entities.PlaylistDO;
import com.massivekinetics.emp.utils.StringUtils;

public class PlaylistListAdapter<T extends PlaylistDO> extends
		EMPListAdapter<T> {

	public PlaylistListAdapter(List<T> items) {
		super(items, R.layout.listitem_playlist);
	}

	@Override
	protected void updateView(ViewGroup view, int position) {
		Object tag = view.getTag();
		PlaylistViewHolder viewHolder;
		if (tag != null)
			viewHolder = (PlaylistViewHolder) tag;
		else {
			viewHolder = new PlaylistViewHolder(
					(TextView) view.findViewById(R.id.playlistName),
					(TextView) view.findViewById(R.id.created),
					(TextView) view.findViewById(R.id.trackCount));
			view.setTag(viewHolder);
		}

		int backgroundID = (position % 2 == 0) ? R.drawable.row_light
				: R.drawable.row_dark;

		view.setBackgroundResource(backgroundID);
		
		view.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
		
		PlaylistDO playlist = items.get(position);
		viewHolder.name.setText(playlist.getTitle().toUpperCase());
		viewHolder.created.setText("Created: " + StringUtils.getDateAsMediumString(playlist.getCreatedTime()));
		viewHolder.trackCount.setText(""+playlist.size() + " tracks");
		
	}

	@Override
	public void onPlaylistChanged() {

	}
	
	private class PlaylistViewHolder {
		final TextView name;
		final TextView created;
		final TextView trackCount;

		public PlaylistViewHolder(TextView name, TextView created,
				TextView trackCount) {
			this.name = name;
			this.created = created;
			this.trackCount = trackCount;
		}
	}
}
