package cn.perfectgames.jewels.activities;

import android.app.ListActivity;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;
import cn.perfectgames.jewels.R;
import cn.perfectgames.jewels.cfg.Configuration;
import cn.perfectgames.jewels.util.ScoreUtil;

public class LocalRankActivity extends ListActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Configuration.config()
				.setSharedPreferences(
						PreferenceManager
								.getDefaultSharedPreferences(getBaseContext()));

		ListView lv = getListView();

		SimpleAdapter listItemAdapter = new SimpleAdapter(this, ScoreUtil
				.loadLocalScores().getLocalRankForList(0, 20),

		R.layout.list_item, new String[] { "rank", "player", "score" },
				new int[] { R.id.list_item_rank, R.id.list_item_player,
						R.id.list_item_score });

		setListAdapter(listItemAdapter);

		lv.setTextFilterEnabled(false);
		lv.setClickable(false);

		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO
			}
		});
	}
}