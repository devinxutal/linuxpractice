package com.devinxutal.fc.control;

import com.devinxutal.fc.control.MoveController.State;

public interface MoveControllerListener {
	void statusChanged(State from, State to);

	void moveSequenceStepped(int index);
}
