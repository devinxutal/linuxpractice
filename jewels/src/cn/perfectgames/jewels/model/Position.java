package cn.perfectgames.jewels.model;

public class Position {
	public int row;
	public int col;

	public Position(int row, int col) {
		this.row = row;
		this.col = col;
	}
	
	public boolean conjuncted(Position other){
		return (row == other.row && Math.abs(col - other.col) == 1) ||(col == other.col && Math.abs(row - other.row) == 1) ;
	}
	
	public String toString(){
		return "["+row+","+col+"]";
	}
}
