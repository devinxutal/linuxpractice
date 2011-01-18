package com.devinxutal.fmc.util;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import android.util.Log;

import com.devinxutal.fmc.control.Move;
import com.devinxutal.fmc.control.MoveSequence;
import com.devinxutal.fmc.model.MagicCube;

public class SymbolMoveUtil {
	private static final String TAG = "SymbolMoveUtil";

	public static List<String> parseSymbolSequenceAsList(String sequence) {
		LinkedList<String> list = new LinkedList<String>();
		if (sequence == null || sequence.length() == 0) {
			return list;
		}
		SymbolTokenizer t = new SymbolTokenizer(sequence);
		String token = null;
		while ((token = t.nextToken()) != null) {
			list.add(token);
		}
		return list;
	}

	public static String[] parseSymbolSequenceAsArray(String sequence) {
		List<String> list = parseSymbolSequenceAsList(sequence);
		return list.toArray(new String[0]);
	}

	public static List<Move> parseMovesFromSymbolSequence(String sequence,
			int cubeOrder) {
		LinkedList<Move> list = new LinkedList<Move>();
		if (sequence == null || sequence.length() == 0) {
			return list;
		}
		SymbolTokenizer t = new SymbolTokenizer(sequence);
		String token = null;
		while ((token = t.nextToken()) != null) {
			Move mv = parseMoveFromSymbol(token, cubeOrder);
			if (mv != null) {
				list.add(mv);
			}
		}
		return list;
	}

	public static Move parseMoveFromSymbol(String symbol, int cubeOrder) {
		boolean dbl = false;

		int dir = 1;

		if (symbol.length() == 0 || symbol.length() > 3) {
			return null;
		}

		if (symbol.length() == 3) {
			if (symbol.charAt(2) == '\'' && symbol.charAt(1) == '2') {
				symbol = symbol.replace("2'", "'2");
			}
			if (symbol.charAt(2) != '2' || symbol.charAt(1) != '\'') {
				return null;
			} else {
				dbl = true;
				dir = -dir;
			}
		} else if (symbol.length() == 2) {
			if (symbol.charAt(1) == '2') {
				dbl = true;
			} else if (symbol.charAt(1) == '\'') {
				dir = -dir;
			}
		}

		symbol = symbol.substring(0, 1);
		boolean upper = symbol.charAt(0) >= 'A' && symbol.charAt(0) <= 'Z';
		int dim = 0;
		switch (symbol.toLowerCase().charAt(0)) {
		case 'u':
			dim = MagicCube.DIM_Y;
			break;
		case 'd':
			dim = MagicCube.DIM_Y;
			dir = -dir;
			break;
		case 'f':
			dim = MagicCube.DIM_Z;
			break;
		case 'b':
			dim = MagicCube.DIM_Z;
			dir = -dir;
			break;
		case 'r':
			dim = MagicCube.DIM_X;
			break;
		case 'l':
			dim = MagicCube.DIM_X;
			dir = -dir;
			break;
		case 'x':
			dim = MagicCube.DIM_X;
			break;
		case 'y':
			dim = MagicCube.DIM_Y;
			break;
		case 'z':
			dim = MagicCube.DIM_Z;
			break;
		default:
			return null;
		}
		Move mv = new Move();
		mv.dimension = dim;
		mv.direction = dir;
		if (symbol.charAt(0) == 'x' || symbol.charAt(0) == 'y'
				|| symbol.charAt(0) == 'z') {
			for (int i = 1; i <= cubeOrder; i++) {
				mv.layers.add(i);
			}
		} else if (symbol.toLowerCase().charAt(0) == 'u'
				|| symbol.toLowerCase().charAt(0) == 'f'
				|| symbol.toLowerCase().charAt(0) == 'r') {
			mv.layers.add(cubeOrder);
			if (!upper) {
				mv.layers.add(cubeOrder - 1);
			}
		} else {
			mv.layers.add(1);
			if (!upper) {
				mv.layers.add(2);
			}
		}
		if (dbl) {
			mv.doubleTurn = true;
		}
		return mv;
	}

	public static String parseSymbolsFromMoveSequence(MoveSequence seq,
			int cubeOrder) {
		StringBuilder builder = new StringBuilder();
		seq.reset();
		Move m = null;
		while ((m = seq.step()) != null) {
			builder.append(parseSymbolFromMove(m, cubeOrder));
		}
		seq.reset();
		return builder.toString();
	}

	public static String parseSymbolFromMove(Move move, int cubeOrder) {
		String symbol = "";
		if (move.layers.size() == cubeOrder) {
			switch (move.dimension) {
			case MagicCube.DIM_X:
				symbol += "x";
				break;
			case MagicCube.DIM_Y:
				symbol += "y";
				break;
			case MagicCube.DIM_Z:
				symbol += "z";
				break;
			}
			if (move.direction < 0) {
				symbol += "'";
			}
		} else if (move.layers.size() == 1) {
			int layer = move.layers.get(0);
			if (layer == 1) {
				switch (move.dimension) {
				case MagicCube.DIM_X:
					symbol += "L";
					break;
				case MagicCube.DIM_Y:
					symbol += "D";
					break;
				case MagicCube.DIM_Z:
					symbol += "B";
					break;
				}
				if (move.direction > 0) {
					symbol += "'";
				}
			} else if (layer == cubeOrder) {
				switch (move.dimension) {
				case MagicCube.DIM_X:
					symbol += "R";
					break;
				case MagicCube.DIM_Y:
					symbol += "U";
					break;
				case MagicCube.DIM_Z:
					symbol += "F";
					break;
				}
				if (move.direction < 0) {
					symbol += "'";
				}
			}
		} else if (move.layers.size() == 2) {
			int layer1 = move.layers.get(0);
			int layer2 = move.layers.get(1);
			if ((layer1 == 1 && layer2 == 2) || (layer1 == 2 && layer2 == 1)) {
				switch (move.dimension) {
				case MagicCube.DIM_X:
					symbol += "l";
					break;
				case MagicCube.DIM_Y:
					symbol += "d";
					break;
				case MagicCube.DIM_Z:
					symbol += "b";
					break;
				}
				if (move.direction > 0) {
					symbol += "'";
				}
			} else if ((layer1 == cubeOrder && layer2 == cubeOrder - 1)
					|| (layer1 == cubeOrder - 1 && layer2 == cubeOrder)) {
				switch (move.dimension) {
				case MagicCube.DIM_X:
					symbol += "r";
					break;
				case MagicCube.DIM_Y:
					symbol += "u";
					break;
				case MagicCube.DIM_Z:
					symbol += "f";
					break;
				}
				if (move.direction < 0) {
					symbol += "'";
				}
			}
		}

		if (move.doubleTurn) {
			symbol += "2";
		}
		return symbol;
	}

	public static boolean isValidSymbol(String symbol) {
		if (SymbolTokenizer.isLetter(symbol.charAt(0))) {
			return true;
		}
		return false;
	}

	public static Move randomMove(int cubeOrder) {
		Move move = new Move();
		Random r = new Random();
		move.direction = r.nextDouble() >= 0.5 ? 1 : -1;
		int rd = r.nextInt(3);
		if (rd == 0) {
			move.dimension = MagicCube.DIM_X;
		} else if (rd == 1) {
			move.dimension = MagicCube.DIM_Y;
		} else {
			move.dimension = MagicCube.DIM_Z;
		}
		move.layers.add(r.nextInt(cubeOrder) + 1);

		return move;
	}

	public static MoveSequence optimizeMoveSequence(MoveSequence sequence) {
		LinkedList<Move> l1 = new LinkedList<Move>();
		LinkedList<Move> l2 = new LinkedList<Move>();
		Move m = null;
		while ((m = sequence.step()) != null) {
			l1.add(m);
		}
		boolean needOptimize = true;
		while (needOptimize && l1.size() > 1) {
			needOptimize = false;
			for (int i = 0; i < l1.size() - 1; i++) {
				Move curr = l1.get(i);
				Move next = l1.get(i + 1);
				Log.v("TEST", "current move: " + parseSymbolFromMove(curr, 3));
				Log.v("TEST", "next move: " + parseSymbolFromMove(next, 3));

				Move combine = combineMove(curr, next);
				if (combine == null) {
					Log.v("TEST", "parsed move is null, add current");

					l2.addLast(curr);
					if (i == l1.size() - 2) {
						l2.addLast(next);
					}
				} else if (combine.direction != 0) {
					Log.v("TEST", "parsed move is "
							+ parseSymbolFromMove(combine, 3));
					needOptimize = true;
					l2.addLast(combine);
					i++;
				} else if (combine.direction == 0) {

					needOptimize = true;
					i++;
				}
			}
			l1.clear();
			LinkedList<Move> tmp = l2;
			l2 = l1;
			l1 = tmp;
		}

		MoveSequence seq = new MoveSequence();
		for (Move mm : l1) {
			seq.addMove(mm);
		}
		return seq;
	}

	private static Move combineMove(Move m1, Move m2) {
		
		if (m1.dimension != m2.dimension) {
			return null;
		}
		if (m1.layers.size() != m2.layers.size()) {
			return null;
		}
		for (Integer i : m1.layers) {
			if (!m2.layers.contains(i)) {
				return null;
			}
		}
		m1.direction = m1.direction > 0 ? 1 : -1;
		m2.direction = m2.direction > 0 ? 1 : -1;
		int direction = m1.direction * (m1.doubleTurn ? 2 : 1) + m2.direction
				* (m2.doubleTurn ? 2 : 1);

		boolean doubleTurn = false;
		direction = direction % 4;
		if (direction == 0) {
			Move m = new Move();
			m.direction = 0;
			return m;
		}
		if (direction == 3) {
			direction = -1;
		} else if (direction == -3) {
			direction = 1;
		}
		if (direction == 2) {
			direction = 1;
			doubleTurn = true;
		} else if (direction == -2) {
			direction = -1;
			doubleTurn = true;
		}
		Move m = new Move();
		m.layers.addAll(m1.layers);
		m.dimension = m1.dimension;
		m.direction = direction;
		m.doubleTurn = doubleTurn;
		return m;
	}
}

class SymbolTokenizer {
	private String string;
	private int i = 0;

	SymbolTokenizer(String string) {
		this.string = string.replace(" ", "");
	}

	public String nextToken() {
		if (i >= string.length()) {
			return null;
		}
		int start = i;
		if (isLetter(string.charAt(i))) {
			while ((++i) != string.length()) {
				char c = string.charAt(i);
				if (c == '(' || c == ')' || isLetter(c)) {
					break;
				}
			}
		} else if (string.charAt(i) == '(' || string.charAt(i) == ')') {
			while ((++i) != string.length()) {
				char c = string.charAt(i);
				if (c != '(' && c != '(') {
					break;
				}
			}
		} else {
			i++;
		}

		return string.substring(start, i);
	}

	public static boolean isLetter(char c) {
		if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
			return true;
		}
		return false;
	}

}
