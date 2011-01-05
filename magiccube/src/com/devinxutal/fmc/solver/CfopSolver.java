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
			return doC(model);
		} else if (!fPattern.match(model)) { // do F
			return doF(model);
		} else if (!oPattern.match(model)) { // do O
			return doO(model);
		} else { // do P
			return doP(model);
		}
	}

	public void init(InputStream in) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String line = null;
		List<PatternAlgorithm> list = C;
		try {
			while ((line = reader.readLine()) != null) {
				Log.v("CfopSolver", "readline: " + line);
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
	}

	private MoveSequence doC(BasicCubeModel model) {
		return null;
	}

	private MoveSequence doF(BasicCubeModel model) {
		return null;
	}

	private MoveSequence doO(BasicCubeModel model) {
		return null;
	}

	private MoveSequence doP(BasicCubeModel model) {
		return null;
	}

}
