package com.massivekinetics;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.massivekinetics.views.AllNewList;
import com.massivekinetics.views.Swyper;

public class MainActivity extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		/*Swyper swyperView = new Swyper(getApplicationContext());	
		
		for(int i=1;i<=5;i++){
			TextView text = new TextView(getApplicationContext());
			text.setText(""+i);
			text.setTextSize(100);
			
			text.setBackgroundColor(Color.BLUE);
			text.setGravity(Gravity.CENTER);
			swyperView.addView(text);
			
		}
		
		setContentView(swyperView);
		
		AllNewList list = (AllNewList) findViewById(R.id.volumeList);

		List<String> items = new ArrayList<String>();
		for (int i = 0; i < 20; i++)
			items.add("Item " + i);

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, items);

		list.setAdapter(adapter);*/
	}
}