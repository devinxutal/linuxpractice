package com.devinxutal.fmc.test;

import junit.framework.TestCase;
import android.util.Log;

import com.devinxutal.fmc.algorithm.model.BasicCubeModel;
import com.devinxutal.fmc.algorithm.pattern.ColorPattern;
import com.devinxutal.fmc.algorithm.patternalgorithm.PatternAlgorithm;
import com.devinxutal.fmc.model.MagicCube;
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
		assertNotNull(a);
		assertEquals(desc.length - 1, ((ColorPattern) a.getPattern())
				.getConstraints().size());
		ColorPattern p = (ColorPattern) a.getPattern();
		assertEquals(2, p.getConstraints().get(0).getX());
		assertEquals(2, p.getConstraints().get(0).getY());
		assertEquals(4, p.getConstraints().get(0).getZ());
		assertEquals(1, p.getConstraints().get(0).getColor());
		assertEquals(4, a.getMoves().totalMoves());
	}

	public void testPatternMatch() {
		PatternAlgorithm a = AlgorithmUtils.parsePatternAlgorithm(desc, 3);

		MagicCube cube = new MagicCube(3);
		cube.turnBySymbol("R");
		cube.turnBySymbol("U");
		cube.turnBySymbol("R'");
		cube.turnBySymbol("U'");
		BasicCubeModel model = new BasicCubeModel(cube);
		assertTrue(a.getPattern().match(model));

		cube = new MagicCube(3);
		cube.turnBySymbol("B");
		cube.turnBySymbol("U");
		cube.turnBySymbol("B'");
		cube.turnBySymbol("U'");
		model = new BasicCubeModel(cube);
		assertFalse(a.getPattern().match(model));
		model.applyRotate(MagicCube.DIM_Y, -1, false);
		assertTrue(a.getPattern().match(model));

	}
}
