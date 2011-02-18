package com.devinxutal.man20.util;

import com.devinxutal.man20.R;

public class MilitaryRank {

	private int stringID;
	private int drawableID;

	private static MilitaryRank[] RANKS = new MilitaryRank[] {
			new MilitaryRank(R.string.rank_a_recruit, R.drawable.rank_a_recruit),
			new MilitaryRank(R.string.rank_b_private, R.drawable.rank_b_private),
			new MilitaryRank(R.string.rank_c_corporal,
					R.drawable.rank_c_corporal),
			new MilitaryRank(R.string.rank_d_sergeant,
					R.drawable.rank_d_sergeant),
			new MilitaryRank(R.string.rank_e_lieutenant,
					R.drawable.rank_e_lieutenant),
			new MilitaryRank(R.string.rank_f_captain, R.drawable.rank_f_captain),
			new MilitaryRank(R.string.rank_g_major, R.drawable.rank_g_major),
			new MilitaryRank(R.string.rank_h_commander,
					R.drawable.rank_h_commander),
			new MilitaryRank(R.string.rank_i_lt_colonel,
					R.drawable.rank_i_lt_colonel),
			new MilitaryRank(R.string.rank_j_colonel, R.drawable.rank_j_colonel),
			new MilitaryRank(R.string.rank_k_general, R.drawable.rank_k_general),
			new MilitaryRank(R.string.rank_l_field_marshal,
					R.drawable.rank_l_field_marshal) };

	public static MilitaryRank getRank(long time, int difficulty) {
		// TODO take difficulty to consideration;
		int interval = 5000;
		int index = (int) (time / interval);
		if (index > RANKS.length) {
			index = RANKS.length - 1;
		}
		if (index < 0) {
			index = 0;
		}
		return RANKS[index];

	}

	private MilitaryRank(int stringID, int drawableID) {
		this.stringID = stringID;
		this.drawableID = drawableID;
	}

	public int getStringID() {
		return stringID;
	}

	public int getDrawableID() {
		return drawableID;
	}

}
