package com.devinxutal.fmc.util;

import java.util.LinkedList;
import java.util.List;

import android.util.Log;

import com.devinxutal.fmc.control.Move;
import com.devinxutal.fmc.model.MagicCube;

public class SymbolMoveUtil {
	public static List<Move> parseMovesFromSymbolSequence(String sequence,
			int cubeOrder) {
		LinkedList<Move> list = new LinkedList<Move>();
		if (sequence.length() == 0) {
			return list;
		}
		int start = 0;
		for (int i = 1; i < sequence.length(); i++) {
			if (isLetter(sequence.charAt(i))) {
				list.addAll(parseMovesFromSymbol(getSymbol(sequence, start, i),
						cubeOrder));
				start = i;
			}
		}
		list.addAll(parseMovesFromSymbol(getSymbol(sequence, start, sequence
				.length()), cubeOrder));
		return list;
	}

	private static String getSymbol(String seq, int start, int end) {
		Log.v("motiontest", seq.substring(start, end));
		return seq.substring(start, end);
	}

	public static boolean isLetter(char c) {
		if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
			return true;
		}
		return false;
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
}
