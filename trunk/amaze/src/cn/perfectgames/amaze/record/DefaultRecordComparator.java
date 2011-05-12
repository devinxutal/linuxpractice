package cn.perfectgames.amaze.record;

public class DefaultRecordComparator extends AbstractRecordComparator{
	
	public int compare(Record record1, Record record2){
		return compare(record1.getResult() , record2.getResult());
	}
}
