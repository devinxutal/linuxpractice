package com.devinxutal.fmc.activities;

import java.util.LinkedList;
import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.devinxutal.fmc.R;
import com.devinxutal.fmc.algorithm.pattern.ColorPattern;
import com.devinxutal.fmc.algorithm.patternalgorithm.PatternAlgorithm;
import com.devinxutal.fmc.cfg.Configuration;
import com.devinxutal.fmc.solver.CfopSolver;
import com.devinxutal.fmc.util.AlgorithmUtils;

public class CfopViewerActivity extends ListActivity {
	static List<String> formulas = new LinkedList<String>();
	static {
		for (int i = 0; i < 41; i++) {
			String num = (i + 1) + "";
			if (i < 9) {
				num = "0" + num;
			}
			formulas.add("F2L " + num);
		}
		for (int i = 0; i < 57; i++) {
			String num = (i + 1) + "";
			if (i < 9) {
				num = "0" + num;
			}
			formulas.add("OLL " + num);
		}
		for (int i = 0; i < 21; i++) {
			String num = (i + 1) + "";
			if (i < 9) {
				num = "0" + num;
			}
			formulas.add("PLL " + num);
		}
	}

	private void startDemostrator(String text) {
		String parts[] = text.split(" ");
		if (parts.length != 2) {
			return;
		}
		PatternAlgorithm pa = solver
				.getAlgorithm(parts[0].charAt(0) + parts[1]);
		if (pa != null) {
			Intent i = new Intent(this, CubeDemostratorActivity.class);
			i.putExtra("formula", pa.getFormula());
			i.putExtra("model", AlgorithmUtils.PatternToCubeState(
					(ColorPattern) pa.getPattern(), 3));
			startActivity(i);
		}
	}

	private CfopSolver solver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Configuration.config()
				.setSharedPreferences(
						PreferenceManager
								.getDefaultSharedPreferences(getBaseContext()));
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		ListView lv = getListView();

		setListAdapter(new ArrayAdapter<String>(this, R.layout.list_item,
				formulas));

		lv.setTextFilterEnabled(true);

		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				startDemostrator(((TextView) view).getText().toString());
			}
		});

		solver = CfopSolver.getSolver(this);
	}
}