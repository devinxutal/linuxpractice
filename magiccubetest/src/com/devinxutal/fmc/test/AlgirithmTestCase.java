package com.devinxutal.fmc.test;

import android.content.res.AssetManager;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

import com.devinxutal.fc.activities.CubeSolverActivity;
import com.devinxutal.fc.algorithm.model.BasicCubeModel;
import com.devinxutal.fc.algorithm.pattern.ColorPattern;
import com.devinxutal.fc.algorithm.patternalgorithm.PatternAlgorithm;
import com.devinxutal.fc.control.MoveSequence;
import com.devinxutal.fc.model.MagicCube;
import com.devinxutal.fc.solver.CfopSolver;
import com.devinxutal.fc.util.AlgorithmUtils;
import com.devinxutal.fc.util.SymbolMoveUtil;

public class AlgirithmTestCase extends
		ActivityInstrumentationTestCase2<CubeSolverActivity> {
	String[] desc = new String[] {//
	"Hello World", // title
			"2 2 4 1",// red center
			"2 0 2 2",// white center
			"4 2 2 3",// green center
			"3 3 4 2",//
			"3 4 3 1",//
			"4 3 3 3", //
			"U R U' R'" };
	String descStr = "Hello World, (2241, 2022, 4223),(3342,3431,4333),URU'R'";

	public AlgirithmTestCase() {
		super("com.devinxutal.fc", CubeSolverActivity.class);
	}

	public void testGenPatternAlgorithm() {
		Log.v("unitest", "in testGenPatternAlgorithm");
		PatternAlgorithm a = AlgorithmUtils.parsePatternAlgorithm(desc, 3);
		assertNotNull(a);
		assertEquals("Hello World", a.getName());
		assertEquals(desc.length - 2, ((ColorPattern) a.getPattern())
				.getConstraints().size());
		ColorPattern p = (ColorPattern) a.getPattern();
		assertEquals(2, p.getConstraints().get(0).getX());
		assertEquals(2, p.getConstraints().get(0).getY());
		assertEquals(4, p.getConstraints().get(0).getZ());
		assertEquals(1, p.getConstraints().get(0).getColor());
		assertEquals(4, a.getMoves().totalMoves());
	}

	public void testGenPatternAlgorithm2() {
		Log.v("unitest", "in testGenPatternAlgorithm 2");
		PatternAlgorithm a = AlgorithmUtils.parsePatternAlgorithm(descStr, 3);
		assertNotNull(a);
		assertEquals("Hello World", a.getName());
		assertEquals(desc.length - 2, ((ColorPattern) a.getPattern())
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
		model.applyTurn(SymbolMoveUtil
				.parseMoveFromSymbol("y", cube.getOrder()));
		// model.applyRotate(MagicCube.DIM_Y, 1, false);
		assertTrue(a.getPattern().match(model));

	}

	public void testSolver() {
		assertNotNull(getActivity());
		AssetManager manager = getActivity().getAssets();
		CfopSolver solver = CfopSolver.getSolver(getActivity());
		assertEquals(41, solver.F.size());
		assertEquals("01", solver.F.get(0).getName());

		MagicCube cube = new MagicCube();
		cube.turnBySymbol("R");
		cube.turnBySymbol("U");
		cube.turnBySymbol("R'");
		cube.turnBySymbol("U'");

		solver.nextMoves(cube);
		assertNotNull(manager);
	}

	public void testSymbolMoveUtil() {
		String moves = "FRUBLDF'R'U'B'L'D'F2R2U2B2L2D2F'2R'2U'2B'2L'2D'2";
		MoveSequence seq = new MoveSequence(moves, 3);
		String movesNew = SymbolMoveUtil.parseSymbolsFromMoveSequence(seq, 3);
		assertEquals(moves, movesNew);

		moves = "frubldf'r'u'b'l'd'f2r2u2b2l2d2f'2r'2u'2b'2l'2d'2";
		seq = new MoveSequence(moves, 3);
		movesNew = SymbolMoveUtil.parseSymbolsFromMoveSequence(seq, 3);
		assertEquals(moves, movesNew);

		moves = "xyzx'y'z'x2y2z2x'2y'2z'2";
		seq = new MoveSequence(moves, 3);
		movesNew = SymbolMoveUtil.parseSymbolsFromMoveSequence(seq, 3);
		assertEquals(moves, movesNew);
	}

	public void testSequenceOptimize() {
		String moves = "xyzz'y'x'FRU";
		MoveSequence seq = new MoveSequence(moves, 3);
		MoveSequence seqNew = SymbolMoveUtil.optimizeMoveSequence(seq);
		String movesNew = SymbolMoveUtil
				.parseSymbolsFromMoveSequence(seqNew, 3);
		assertEquals("FRU", movesNew);

		moves = "FF'2";
		seq = new MoveSequence(moves, 3);
		seqNew = SymbolMoveUtil.optimizeMoveSequence(seq);
		movesNew = SymbolMoveUtil.parseSymbolsFromMoveSequence(seqNew, 3);
		assertEquals("F'", movesNew);

		moves = "F'F'";
		seq = new MoveSequence(moves, 3);
		seqNew = SymbolMoveUtil.optimizeMoveSequence(seq);
		movesNew = SymbolMoveUtil.parseSymbolsFromMoveSequence(seqNew, 3);
		assertEquals("F'2", movesNew);

		moves = "U2U'R'FRyU'";
		seq = new MoveSequence(moves, 3);
		seqNew = SymbolMoveUtil.optimizeMoveSequence(seq);
		movesNew = SymbolMoveUtil.parseSymbolsFromMoveSequence(seqNew, 3);
		assertEquals("UR'FRyU'", movesNew);
	}

}
