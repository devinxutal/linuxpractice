package com.devinxutal.fmc.activities;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.devinxutal.fmc.R;

public class CfopViewerActivity extends ListActivity {
	static String[] COUNTRIES;
	static {
		COUNTRIES = new String[21 + 57 + 41];
		for (int i = 0; i < 41; i++) {
			String num = i + "";
			if (i < 10) {
				num = "0" + num;
			}
			COUNTRIES[i] = "F" + num;
		}
		for (int i = 0; i < 57; i++) {
			String num = i + "";
			if (i < 10) {
				num = "0" + num;
			}
			COUNTRIES[41 + i] = "O" + num;
		}
		for (int i = 0; i < 21; i++) {
			String num = i + "";
			if (i < 10) {
				num = "0" + num;
			}
			COUNTRIES[57 + 41 + i] = "P" + num;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ListView lv = getListView();

		setListAdapter(new ArrayAdapter<String>(this, R.layout.list_item,
				COUNTRIES));

		lv.setTextFilterEnabled(true);

		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				// When clicked, show a toast with the TextView text
				Toast.makeText(getApplicationContext(),
						((TextView) view).getText(), Toast.LENGTH_SHORT).show();
			}
		});
	}
}