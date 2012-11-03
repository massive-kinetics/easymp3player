package com.massivekinetics.emp.adapters;

import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.massivekinetics.emp.EMPApplication;
import com.massivekinetics.emp.R;
import com.massivekinetics.emp.data.entities.ArtistDO;
import com.massivekinetics.emp.data.entities.BaseDO;
import com.massivekinetics.emp.data.listeners.OnPlaylistChangedListener;

public abstract class EMPListAdapter<T extends BaseDO> extends BaseAdapter
		implements OnPlaylistChangedListener {

	protected List<T> items;
	protected int layoutID;

	public EMPListAdapter(List<T> items, int layoutID) {
		this.items = items;
		this.layoutID = layoutID;
	}

	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public Object getItem(int position) {
		return items.get(position);
	}

	@Override
	public long getItemId(int position) {
		return ((T) getItem(position)).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(EMPApplication.context);
		View view = getView(inflater, convertView, position);
		return view;
	}

	protected ViewGroup getView(LayoutInflater inflater, View convertView,
			int position) {
		ViewGroup view;
		if (convertView != null && convertView instanceof ViewGroup) {
			view = (ViewGroup) convertView;
		} else {
			view = (ViewGroup) inflater.inflate(layoutID, null);
		}

		updateView(view, position);

		return view;
	}

	protected abstract void updateView(ViewGroup view, int position);

}
