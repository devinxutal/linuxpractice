package com.devinxutal.fmc.solver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.util.Log;

import com.devinxutal.fmc.algorithm.model.BasicCubeModel;
import com.devinxutal.fmc.algorithm.pattern.Pattern;
import com.devinxutal.fmc.algorithm.patternalgorithm.PatternAlgorithm;
import com.devinxutal.fmc.control.Move;
import com.devinxutal.fmc.control.MoveSequence;
import com.devinxutal.fmc.model.MagicCube;
import com.devinxutal.fmc.util.AlgorithmUtils;
import com.devinxutal.fmc.util.SymbolMoveUtil;

public class CfopSolver extends AbstractSolver {

	private static CfopSolver solver = new CfopSolver();

	public List<PatternAlgorithm> C;
	public List<PatternAlgorithm> F;
	public List<PatternAlgorithm> O;
	public List<PatternAlgorithm> P;

	public Pattern cPattern;
	public Pattern fPattern;
	public Pattern oPattern;
	public Pattern pPattern;

	public Pattern cPartPattern;
	public Pattern fPartPattern;

	public static Move[] rotates;
	public static Move[] upturns;

	public static int order = 3;

	private boolean initialized = false;

	static {
		String[] array_rotates = new String[] { "y", "y'", "y2" };
		String[] array_upturns = new String[] { "U", "U'", "U2" };
		rotates = new Move[3];
		upturns = new Move[3];
		for (int i = 0; i < 3; i++) {
			rotates[i] = SymbolMoveUtil.parseMoveFromSymbol(array_rotates[i],
					order);
			upturns[i] = SymbolMoveUtil.parseMoveFromSymbol(array_upturns[i],
					order);
		}
	}

	private CfopSolver() {
		init();
	}

	public static CfopSolver getSolver(Activity activity) {
		if (!solver.isInitialized()) {
			Log.v("CfopSolver", "initializing solver");
			solver.initialize(activity);
		}
		return solver;
	}

	public boolean isInitialized() {
		return initialized;
	}

	public void initialize(Activity activity) {
		if (this.initialized) {
			return;
		}
		try {
			this.init(activity.getAssets().open("algorithm/cfop_pattern"),
					activity.getAssets().open("algorithm/cfop_algorithm"));
			initialized = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public PatternAlgorithm getAlgorithm(String name) {
		// Log.v("CfopSolver", "get algorithm by name " + name);
		if (name == null || name.length() != 3) {
			return null;
		}
		List<PatternAlgorithm> list = C;
		char c = name.charAt(0);
		String n = name.substring(1);
		switch (c) {
		case 'C':
			list = C;
			break;
		case 'F':
			list = F;
			break;
		case 'O':
			list = O;
			break;
		case 'P':
			list = P;
			break;
		}
		for (PatternAlgorithm pa : list) {
			if (pa.getName().equals(n)) {
				return pa;
			}
		}
		return null;
	}

	public MoveSequence nextMoves(MagicCube cube) {
		BasicCubeModel model = new BasicCubeModel(cube);
		if (!cPattern.match(model)) { // do C
			// Log.v("CfogSolver", "Cross not formed");
			return doC(model);
		} else if (!fPattern.match(model)) { // do F
			// Log.v("CfogSolver", "F2L not formed");
			return doF(model);
		} else if (!oPattern.match(model)) { // do O
			// Log.v("CfogSolver", "OLL not formed");
			return doO(model);
		} else if (!pPattern.match(model)) { // do P
			// Log.v("CfogSolver", "PLL not formed");
			return doP(model);
		} else if (pPattern.match(model)) {
			// Log.v("CfogSolver", "PLL formed, ajust upside");
			return ajustUp(model);
		} else {
			return null;
		}
	}

	public void init(InputStream patternIn, InputStream algorithmIn) {
		Map<String, Pattern> patternMap = initPatternMap(patternIn);
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				algorithmIn));
		String line = null;
		List<PatternAlgorithm> list = C;
		String prefix = "C";
		try {
			while ((line = reader.readLine()) != null) {
				line = line.trim();
				if (line.length() == 0 || line.startsWith("#")) {
					continue;
				} else if (line.length() == 1) {
					prefix = line;
					switch (line.charAt(0)) {
					case 'C':
						list = C;
						break;
					case 'F':
						list = F;
						break;
					case 'O':
						list = O;
						break;
					case 'P':
						list = P;
						break;
					}
				} else {
					String parts[] = line.split("\t");
					if (parts.length != 2) {
						continue;
					}
					String name = parts[0];
					String formula = parts[1];
					MoveSequence seq = new MoveSequence(formula, 3);
					Pattern p = patternMap.get(prefix + name);
					if (p != null) {
						// Log.v("CfopSolver", "find a formula: " + name + ": "
						// + formula);
						PatternAlgorithm pa = new PatternAlgorithm(parts[0], p,
								seq, formula);

						list.add(pa);
					}

				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private Map<String, Pattern> initPatternMap(InputStream in) {
		Map<String, Pattern> patternMap = new HashMap<String, Pattern>();
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String line = null;
		String prefix = "C";
		try {
			while ((line = reader.readLine()) != null) {
				line = line.trim();
				if (line.length() == 0 || line.startsWith("#")) {
					continue;
				} else if (line.length() == 1) {
					prefix = line.substring(0, 1);
				} else {
					String parts[] = line.split("\t");
					PatternAlgorithm pa = AlgorithmUtils.parsePatternAlgorithm(
							line, 3);
					// Log.v("CfopSolver", "find a pattern: " + parts[0]);
					patternMap.put(prefix + parts[0], AlgorithmUtils
							.parsePattern(parts[1], 3));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return patternMap;
	}

	private void init() {
		C = new LinkedList<PatternAlgorithm>();
		F = new LinkedList<PatternAlgorithm>();
		O = new LinkedList<PatternAlgorithm>();
		P = new LinkedList<PatternAlgorithm>();

		cPattern = AlgorithmUtils.parseColorPattern(//
				"(2021),"//
						+ "(2202,0223,2244,4225)," //
						+ "(2011,1021,2031,3021),"//
						+ "(2102,0123,2144,4125)");

		fPattern = AlgorithmUtils.parseColorPattern("(2021),"//
				+ "(2202,0223,2244,4225)," //
				+ "(1011,1031,3031,3011)," //
				+ "(1102,3102,0113,0133,1144,3144,4115,4135)," //
				+ "(1202,3202,0213,0233,1244,3244,4215,4235)");

		oPattern = AlgorithmUtils.parseColorPattern("(1411,1421,1431),"//
				+ "(2411,2421,2431),"//
				+ "(3411,3421,3431)"//
		);
		pPattern = AlgorithmUtils.parseColorPattern("(0311,0321,0331),"//
				+ "(4312,4322,4332),"//
				+ "(1303,2303,3303),"//
				+ "(1344,2344,3344)"//
		);

		cPartPattern = AlgorithmUtils.parseColorPattern(//
				"(2021,2242),"//
						+ "(2031,2142)" //
				);
		fPartPattern = AlgorithmUtils.parseColorPattern(//
				"(2021,2243,4222),(3031,3143,4132),(4232,3243)");
	}

	private MoveSequence doC(BasicCubeModel model) {
		for (int i = -1; i < 3; i++) {
			for (int j = -1; j < 3; j++) {
				model.reset();
				MoveSequence seq = new MoveSequence();
				if (i >= 0) {
					model.applyTurn(rotates[i]);
					seq.addMove(rotates[i]);
				}
				if (j >= 0) {
					model.applyTurn(upturns[j]);
					seq.addMove(upturns[j]);
				}
				if (cPartPattern.match(model)) {
					break;
				}
				for (PatternAlgorithm pa : C) {
					if (pa.getPattern().match(model)) {
						setMessage("find matching CROSS fomula:\n"
								+ pa.getName() + ": \n" + pa.getFormula());
						Log.v("CfopSolver", "find matching CROSS fomula:"
								+ pa.getName() + ": " + pa.getFormula());
						MoveSequence moves = pa.getMoves();
						moves.reset();
						Move move;
						while ((move = moves.step()) != null) {
							seq.addMove(move);
						}
						return seq;
					}
				}
			}
		}

		// Log.v("CfopSolver", "cannot find matching F2L fomula");
		return null;
	}

	private MoveSequence doF(BasicCubeModel model) {

		for (int i = -1; i < 3; i++) {
			for (int j = -1; j < 3; j++) {
				model.reset();
				MoveSequence seq = new MoveSequence();
				if (i >= 0) {
					model.applyTurn(rotates[i]);
					seq.addMove(rotates[i]);
				}
				if (j >= 0) {
					model.applyTurn(upturns[j]);
					seq.addMove(upturns[j]);
				}
				for (PatternAlgorithm pa : F) {
					if (pa.getPattern().match(model)) {
						setMessage("find matching F2L fomula:\n" + pa.getName()
								+ ": \n" + pa.getFormula());
						// Log.v("CfopSolver", "find matching F2L fomula:"
						// + pa.getName() + ": " + pa.getFormula());
						MoveSequence moves = pa.getMoves();
						moves.reset();
						Move move;
						while ((move = moves.step()) != null) {
							seq.addMove(move);
						}
						return seq;
					}
				}
			}
		}

		// Log.v("CfopSolver",
		// "cannot find matching F2L fomula, solve DeadLock");
		return solveF2lDeadLock(model);
	}

	private MoveSequence doO(BasicCubeModel model) {
		for (int i = -1; i < 3; i++) {
			model.reset();
			MoveSequence seq = new MoveSequence();
			if (i >= 0) {
				model.applyTurn(rotates[i]);
				seq.addMove(rotates[i]);
			}
			for (PatternAlgorithm pa : O) {
				if (pa.getPattern().match(model)) {
					setMessage("find matching OLL fomula:\n" + pa.getName()
							+ ": \n" + pa.getFormula());
					// Log.v("CfopSolver", "find matching OLL fomula:"
					// + pa.getName() + ": " + pa.getFormula());
					MoveSequence moves = pa.getMoves();
					moves.reset();
					Move move;
					while ((move = moves.step()) != null) {
						seq.addMove(move);
					}
					return seq;
				}
			}
		}

		// Log.v("CfopSolver", "cannot find matching OLL fomula");
		return null;
	}

	private MoveSequence doP(BasicCubeModel model) {
		for (int i = -1; i < 3; i++) {
			model.reset();
			MoveSequence seq = new MoveSequence();
			if (i >= 0) {
				model.applyTurn(rotates[i]);
				seq.addMove(rotates[i]);
			}
			for (PatternAlgorithm pa : P) {
				if (pa.getPattern().match(model)) {
					setMessage("find matching PLL fomula:\n" + pa.getName()
							+ ": \n" + pa.getFormula());
					// Log.v("CfopSolver", "find matching PLL fomula:"
					// + pa.getName() + ": " + pa.getFormula());
					MoveSequence moves = pa.getMoves();
					moves.reset();
					Move move;
					while ((move = moves.step()) != null) {
						seq.addMove(move);
					}
					return seq;
				}
			}
		}

		// Log.v("CfopSolver", "cannot find matching PLL fomula");
		return null;
	}

	private MoveSequence ajustUp(BasicCubeModel model) {

		MoveSequence seq = new MoveSequence();
		for (int i = 0; i < 3; i++) {
			model.reset();
			if (i >= 0) {
				model.applyTurn(upturns[i]);
				if (this.solved(model)) {
					seq.addMove(upturns[i]);
					setMessage("Ajust Up side");
					return seq;
				}
			}

		}
		return null;
	}

	private MoveSequence solveF2lDeadLock(BasicCubeModel model) {

		for (int i = 0; i < 3; i++) {
			model.reset();
			MoveSequence seq = new MoveSequence();
			model.applyTurn(rotates[i]);
			seq.addMove(rotates[i]);
			if (!fPartPattern.match(model)) {
				seq.addMove(SymbolMoveUtil.parseMoveFromSymbol("R", order));
				seq.addMove(SymbolMoveUtil.parseMoveFromSymbol("U", order));
				seq.addMove(SymbolMoveUtil.parseMoveFromSymbol("R'", order));
				return seq;
			}
		}
		setMessage("No F2L available, solve deadlock");
		return null;
	}

	public boolean solved(BasicCubeModel model) {
		Integer[][][] cube = model.get();
		Integer u = cube[order / 2 + 1][order + 1][order / 2 + 1];
		Integer d = cube[order / 2 + 1][0][order / 2 + 1];
		Integer r = cube[order + 1][order / 2 + 1][order / 2 + 1];
		Integer l = cube[0][order / 2 + 1][order / 2 + 1];
		Integer f = cube[order / 2 + 1][order / 2 + 1][order + 1];
		Integer b = cube[order / 2 + 1][order / 2 + 1][0];
		for (int i = 1; i <= order; i++) {
			for (int j = 1; j <= order; j++) {
				if (cube[i][order + 1][j] != u || cube[i][0][j] != d
						|| cube[order + 1][i][j] != r || cube[0][i][j] != l
						|| cube[i][j][order + 1] != f || cube[i][j][0] != b) {
					return false;
				}
			}
		}
		return true;
	}
}
