package cn.perfectgames.jewels.model;

import java.io.Serializable;

public class SavablePlayground implements Serializable {
	public ScoreAndLevel scoreLevel;
	
	public boolean finished = false;
	
	public Jewel[][] playground;
}
