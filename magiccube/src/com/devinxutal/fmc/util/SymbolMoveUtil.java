package com.devinxutal.fmc.util;

import java.util.LinkedList;
import java.util.List;

import com.devinxutal.fmc.control.Move;
import com.devinxutal.fmc.model.MagicCube;

public class SymbolMoveUtil {
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
			list.addAll(parseMovesFromSymbol(token, cubeOrder));
		}
		return list;
	}

	public static List<Move> parseMovesFromSymbol(String symbol, int cubeOrder) {
		LinkedList<Move> list = new LinkedList<Move>();
		boolean dbl = false;
		if (symbol.length() == 0) {
			return list;
		}
		if (symbol.length() == 3) {
			if (symbol.charAt(1) != '2') {
				return list;
			} else {
				dbl = true;
			}
		}
		int dir = 1;
		if (symbol.length() > 1 && symbol.charAt(symbol.length() - 1) != '\'') {
			return list;
		} else if (symbol.charAt(symbol.length() - 1) != '\'') {
			dir = -1;
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
		default:
			return list;
		}
		Move mv = new Move();
		mv.dimension = dim;
		mv.direction = dir;
		if (symbol.toLowerCase().charAt(0) == 'u'
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
		list.addLast(mv);
		if (dbl) {
			list.addLast(mv.cloneMove());
		}
		return list;
	}

	public static boolean isValidSymbol(String symbol) {
		if (SymbolTokenizer.isLetter(symbol.charAt(0))) {
			return true;
		}
		return false;
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
