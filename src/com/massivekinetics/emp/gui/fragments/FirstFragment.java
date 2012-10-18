package com.massivekinetics.emp.gui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.massivekinetics.emp.R;

public class FirstFragment extends Fragment {

	static int c = 1;

	public FirstFragment() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater
				.inflate(R.layout.fragment_layout, container, false);
		
		((TextView)view.findViewById(R.id.textView1)).setText("Fragment" + c++);
		
		return view;
	}

}
