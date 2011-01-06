package com.devinxutal.fmc.activities;

import java.io.IOException;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.devinxutal.fmc.R;
import com.devinxutal.fmc.algorithm.pattern.ColorPattern;
import com.devinxutal.fmc.algorithm.patternalgorithm.PatternAlgorithm;
import com.devinxutal.fmc.solver.CfopSolver;
import com.devinxutal.fmc.util.AlgorithmUtils;

public class CfopViewerActivity extends ListActivity {
	static String[] COUNTRIES;
	static {
		COUNTRIES = new String[21 + 57 + 41];
		for (int i = 0; i < 41; i++) {
			String num = (i + 1) + "";
			if (i < 9) {
				num = "0" + num;
			}
			COUNTRIES[i] = "F" + num;
		}
		for (int i = 0; i < 57; i++) {
			String num = (i + 1) + "";
			if (i < 9) {
				num = "0" + num;
			}
			COUNTRIES[41 + i] = "O" + num;
		}
		for (int i = 0; i < 21; i++) {
			String num = (i + 1) + "";
			if (i < 9) {
				num = "0" + num;
			}
			COUNTRIES[57 + 41 + i] = "P" + num;
		}
	}

	private void startDemostrator(String text) {
		PatternAlgorithm pa = solver.getAlgorithm(text);
		if (pa != null) {
			Intent i = new Intent(this, CubeDemostratorActivity.class);
			i.putExtra("formula", pa.getFormula());
			i.putExtra("model", AlgorithmUtils.PatternToCubeState(
					(ColorPattern) pa.getPattern(), 3));
			startActivity(i);
		}
	}

	private CfopSolver solver = new CfopSolver();

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
				startDemostrator(((TextView) view).getText().toString());
			}
		});

		try {
			solver.init(this.getAssets().open("algorithm/cfop_pattern"), this
					.getAssets().open("algorithm/cfop_algorithm"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}