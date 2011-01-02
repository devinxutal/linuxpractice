package com.devinxutal.fmc.control;

import com.devinxutal.fmc.control.MoveController.State;

public interface MoveControllerListener {
	void statusChanged(State from, State to);

	void moveSequenceStepped(int index);
}
