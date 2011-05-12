package cn.perfectgames.amaze.record;

public abstract class AbstractRecordComparator implements RecordComparator {
	private static final double DELTA = 0.000001;

	protected int compare(double value1, double value2) {
		double result = value1 - value2;
		if (Math.abs(result) < DELTA) {
			return 0;
		} else if (result < 0) {
			return -1;
		} else {
			return 1;
		}
	}

	protected int compare(int value1, int value2) {
		int result = value1 - value2;
		if (result == 0) {
			return 0;
		} else if (result < 0) {
			return -1;
		} else {
			return 1;
		}
	}
}
