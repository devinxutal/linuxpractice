package com.devinxutal.fmc.test;

import java.io.IOException;

import android.content.res.AssetManager;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

import com.devinxutal.fmc.activities.CubeSolverActivity;
import com.devinxutal.fmc.algorithm.model.BasicCubeModel;
import com.devinxutal.fmc.algorithm.pattern.ColorPattern;
import com.devinxutal.fmc.algorithm.patternalgorithm.PatternAlgorithm;
import com.devinxutal.fmc.model.MagicCube;
import com.devinxutal.fmc.util.AlgorithmUtils;

public class AlgirithmTestCase extends
		ActivityInstrumentationTestCase2<CubeSolverActivity> {
	String[] desc = new String[] {//
	"2 2 4 1",// red center
			"2 0 2 2",// white center
			"4 2 2 3",// green center
			"3 3 4 2",//
			"3 4 3 1",//
			"4 3 3 3", //
			"U R U' R'" };

	public AlgirithmTestCase() {
		super("com.devinxutal.fmc", CubeSolverActivity.class);
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
		Log.v("motiontest", "now apply rotate");
		model.applyRotate(MagicCube.DIM_Y, 1, false);
		assertTrue(a.getPattern().match(model));

	}

	public void testSolver() {
		assertNotNull(getActivity());
		AssetManager manager = getActivity().getAssets();
		try {
			manager.open("algorithm/cfop");
		} catch (IOException e) {
			e.printStackTrace();
			fail("file not opened");
		}
		assertNotNull(manager);
	}
}
