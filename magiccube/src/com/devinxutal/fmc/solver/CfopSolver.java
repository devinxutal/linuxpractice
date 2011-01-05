package com.devinxutal.fmc.solver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

import android.util.Log;

import com.devinxutal.fmc.algorithm.model.BasicCubeModel;
import com.devinxutal.fmc.algorithm.pattern.Pattern;
import com.devinxutal.fmc.algorithm.patternalgorithm.PatternAlgorithm;
import com.devinxutal.fmc.control.MoveSequence;
import com.devinxutal.fmc.model.MagicCube;
import com.devinxutal.fmc.util.AlgorithmUtils;

public class CfopSolver extends AbstractSolver {
	public List<PatternAlgorithm> C;
	public List<PatternAlgorithm> F;
	public List<PatternAlgorithm> O;
	public List<PatternAlgorithm> P;

	public Pattern cPattern;
	public Pattern fPattern;
	public Pattern oPattern;
	public Pattern pPattern;

	public String msg;

	public CfopSolver() {
		init();
	}

	public MoveSequence nextMoves(MagicCube cube) {
		BasicCubeModel model = new BasicCubeModel(cube);
		if (!cPattern.match(model)) { // do C
			Log.v("CfogSolver", "Cross not formed");
			return doC(model);
		} else if (!fPattern.match(model)) { // do F
			Log.v("CfogSolver", "F2L not formed");
			return doF(model);
		} else if (!oPattern.match(model)) { // do O

			Log.v("CfogSolver", "OLL not formed");
			return doO(model);
		} else { // do P
			Log.v("CfogSolver", "PLL not formed");
			return doP(model);
		}
	}

	public void init(InputStream in) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String line = null;
		List<PatternAlgorithm> list = C;
		try {
			while ((line = reader.readLine()) != null) {
				// Log.v("CfopSolver", "readline: " + line);
				line = line.trim();
				if (line.length() == 0 || line.startsWith("#")) {
					continue;
				} else if (line.length() == 1) {
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
					PatternAlgorithm pa = AlgorithmUtils.parsePatternAlgorithm(
							line, 3);
					list.add(pa);
				}

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
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

		oPattern = AlgorithmUtils.parseColorPattern(
				"(1411,1421,1431),"//
				+"(2411,2421,2431),"//
				+"(3411,3421,3431)"//
				);
		pPattern = AlgorithmUtils.parseColorPattern(
				"(0311,0321,0331),"//
				+"(4311,4321,4331),"//
				+"(1301,2301,3301),"//
				+"(1341,2341,3341)"//
				);
	}

	private MoveSequence doC(BasicCubeModel model) {
		return null;
	}

	private MoveSequence doF(BasicCubeModel model) {
		for (PatternAlgorithm pa : F) {
			if (pa.getPattern().match(model)) {
				Log.v("CfopSolver", "find matching fomula:" + pa.getName()
						+ ": " + pa.getFormula());
				return pa.getMoves();
			}
		}
		Log.v("CfopSolver", "cannot find matching fomula");
		return null;
	}

	private MoveSequence doO(BasicCubeModel model) {
		return null;
	}

	private MoveSequence doP(BasicCubeModel model) {
		return null;
	}

}
