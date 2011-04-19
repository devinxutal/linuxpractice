package com.devinxutal.fmc.test;

import java.io.IOException;

import android.content.res.AssetManager;
import android.test.ActivityUnitTestCase;

import com.devinxutal.fc.activities.CubeSolverActivity;

public class SolverActivityTestCase extends
		ActivityUnitTestCase<CubeSolverActivity> {

	public SolverActivityTestCase(Class<CubeSolverActivity> activityClass) {
		super(activityClass);
		// TODO Auto-generated constructor stub
	}

	public void testSolver() {
		AssetManager manager = getActivity().getAssets();
		try {
			manager.open("cfop");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertNotNull(manager);
	}
}
