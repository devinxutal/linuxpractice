package com.devinxutal.fmc.test;

import junit.framework.TestCase;
import android.util.Log;

import com.devinxutal.fmc.algorithm.pattern.ColorPattern;
import com.devinxutal.fmc.algorithm.patternalgorithm.PatternAlgorithm;
import com.devinxutal.fmc.util.AlgorithmUtils;

public class AlgirithmTestCase extends TestCase {
	String[] desc = new String[] {//
	"2 2 4 1",// red center
			"2 0 2 2",// white center
			"4 2 2 3",// green center
			"3 3 4 2",//
			"3 4 3 1",//
			"4 3 3 3", //
			"U R U' R'" };

	@Override
	protected void setUp() throws Exception {
	}

	@Override
	protected void tearDown() throws Exception {
	}

	public void testGenPatternAlgorithm() {
		Log.v("unitest", "in testGenPatternAlgorithm");
		PatternAlgorithm a = AlgorithmUtils.parsePatternAlgorithm(desc, 3);
		this.assertNotNull(a);
		this.assertEquals(desc.length - 1, ((ColorPattern) a.getPattern())
				.getConstraints().size());
		ColorPattern p = (ColorPattern) a.getPattern();
		this.assertEquals(2, p.getConstraints().get(0).getX());
		this.assertEquals(2, p.getConstraints().get(0).getY());
		this.assertEquals(4, p.getConstraints().get(0).getZ());
		this.assertEquals(1, p.getConstraints().get(0).getColor());
		this.assertEquals(4, a.getMoves().totalMoves());
	}
}
