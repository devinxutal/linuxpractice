package cn.perfectgames.amaze.record;

public class Record {
	private int rank;
	private String player;
	private int mode;
	private int level;
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}

	private double result;
	private double minorResult1;
	private double minorResult2;
	private double minorResult3;
	public int getRank() {
		return rank;
	}
	public void setRank(int rank) {
		this.rank = rank;
	}
	public String getPlayer() {
		return player;
	}
	public void setPlayer(String player) {
		this.player = player.replace("\t"," ");
	}
	public int getMode() {
		return mode;
	}
	public void setMode(int mode) {
		this.mode = mode;
	}
	public double getResult() {
		return result;
	}
	public void setResult(double result) {
		this.result = result;
	}
	public double getMinorResult1() {
		return minorResult1;
	}
	public void setMinorResult1(double minorResult1) {
		this.minorResult1 = minorResult1;
	}
	public double getMinorResult2() {
		return minorResult2;
	}
	public void setMinorResult2(double minorResult2) {
		this.minorResult2 = minorResult2;
	}
	public double getMinorResult3() {
		return minorResult3;
	}
	public void setMinorResult3(double minorResult3) {
		this.minorResult3 = minorResult3;
	}
	
	public Record clone(){
		Record r = new Record();
		r.setMode(this.getMode());
		r.setLevel(this.getLevel());
		r.setPlayer(this.getPlayer());
		r.setResult(this.getResult());
		r.setMinorResult1(this.getMinorResult1());
		r.setMinorResult2(this.getMinorResult2());
		r.setMinorResult3(this.getMinorResult3());
		return r;
	}
	public String serialize(){
		String TAB = "\t";
		return mode + TAB
		+player+TAB
		+level+TAB
		+result+TAB
		+minorResult1+TAB
		+minorResult2+TAB
		+minorResult3+TAB;
	}
	
	public boolean deserialize(String value){
		try{
			String values[] = value.split("\t");
			this.mode = Integer.valueOf(values[0]);
			this.player = values[1];
			this.level = Integer.valueOf(values[2]);
			this.result = Double.valueOf(values[3]);
			this.minorResult1 = Double.valueOf(values[4]);
			this.minorResult2 = Double.valueOf(values[5]);
			this.minorResult3 = Double.valueOf(values[6]);
		}catch(Exception e){
			return false;
		}
		return true;
	}
	
	public static String serializeRecord(Record record){
		if(record == null){
			return null;
		}else{
			return record.serialize();
		}
	}
	
	public static Record deserializeRecord(String value){
		if(value == null){
			return null;
		}else{
			Record record = new Record();
			if(record.deserialize(value)){
				return record;
			}else{
				return null;
			}
		}
	}
}
