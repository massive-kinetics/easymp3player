package com.massivekinetics.emp.gui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.massivekinetics.emp.R;

public class FirstFragment extends SherlockFragment {

	static int c = 1;

	public FirstFragment() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater
				.inflate(R.layout.fragment_layout, container, false);
		
		((TextView)view.findViewById(R.id.textView1)).setText("Sherlock Fragment" + c++);
		
		return view;
	}

}
