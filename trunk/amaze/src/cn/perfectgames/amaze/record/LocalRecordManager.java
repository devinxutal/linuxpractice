package cn.perfectgames.amaze.record;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.os.Environment;

public class LocalRecordManager {
	public static final int MAX_RECORDS = 100;

	private Context context;
	private int gameMode = -1;
	private RecordComparator recordComparator;

	private Map<Integer, LinkedList<Record>> recordsMap;
	private int first;
	private int rangeLength = 10;

	private Record submittedRecord;

	public LocalRecordManager(Context context) {
		this(context, new DefaultRecordComparator());
	}

	public LocalRecordManager(Context context, RecordComparator comparator) {
		this.context = context;
		if (comparator == null) {
			comparator = new DefaultRecordComparator();
		}
		this.recordComparator = comparator;

		recordsMap = new HashMap<Integer, LinkedList<Record>>();
	}

	public void setGameMode(int mode) {
		if (mode < 0) {
			return;
		}
		if (this.gameMode != mode) {
			gameMode = mode;
			if (!recordsMap.containsKey(gameMode)) {
				loadRecords(gameMode);
			}
		}
	}

	public void submitRecord(Record record) {
		this.setGameMode(record.getMode());
		LinkedList<Record> records = this.getAllRecordsForCurrentMode();
		this.submittedRecord = record;

		int i = 0;
		for (i = 0; i < records.size(); i++) {
			if (recordComparator.compare(record, records.get(i)) > 0) {
				break;
			}
		}
		records.add(i, record);
		saveRecords(gameMode);
	}

	public int getRangeLength() {
		return rangeLength;
	}

	public void setRangeLength(int rangeLength) {
		this.rangeLength = rangeLength;
	}

	public int getGameMode() {
		return gameMode;
	}

	public Record getSubmittedRecord() {
		return submittedRecord;
	}

	public List<Record> getRecords() {
		if (gameMode < 0 || first < 0 || rangeLength <= 0) {
			return null;
		}
		return null;
	}

	public boolean hasNextRange() {
		if (gameMode < 0 || first < 0 || rangeLength <= 0) {
			return false;
		}
		LinkedList<Record> records = getAllRecordsForCurrentMode();
		int last = first + rangeLength;
		if (last >= records.size()) {
			return false;
		} else {
			return true;
		}
	}

	public boolean hasPreviousRange() {
		if (gameMode < 0 || first < 0 || rangeLength <= 0) {
			return false;
		}
		if (first > 1) {
			return true;
		} else {
			return false;
		}
	}

	public List<Record> loadNextPage() {
		return loadPageAtRank(first+rangeLength);
	}

	public List<Record> loadPreviousPage(){
		int rank = 1;
		if(first>rangeLength){
			rank = first - rangeLength;
		}
		return loadPageAtRank(rank);
	}
	
	public List<Record> loadPageAtRank(int rank){
		List<Record> list = new LinkedList<Record>();
		LinkedList<Record> records = getAllRecordsForCurrentMode();
		if(records!= null && rank <=records.size()){
			first = rank;
			for(int i = first; i<= records.size() && i< first+rangeLength; i++){
				Record r = records.get(i-1);
				r.setRank(i);
				list.add(r);
			}
		}
		
		return list;
	}
	
	private LinkedList<Record> getAllRecordsForCurrentMode() {
		if (gameMode < 0) {
			return null;
		}
		if (!recordsMap.containsKey(gameMode)) {
			loadRecords(gameMode);
		}
		return recordsMap.get(gameMode);
	}

	private void saveRecords(int mode) {
		if (recordsMap.containsKey(mode)) {
			LinkedList<Record> records = recordsMap.get(mode);
			File scoreFile = getScoreFile(mode);
			try {
				PrintWriter writer = new PrintWriter(new FileWriter(scoreFile));
				int recordCount = 0;
				for (Record record : records) {
					writer.println(record.serialize());
					if (++recordCount > MAX_RECORDS) {
						break;
					}
				}
				writer.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void loadRecords(int mode) {
		LinkedList<Record> records = null;
		if (recordsMap.containsKey(mode)) {
			records = recordsMap.get(mode);
			records.clear();
		} else {
			records = new LinkedList<Record>();
			recordsMap.put(mode, records);
		}
		File scoreFile = getScoreFile(mode);
		try {
			BufferedReader reader = new BufferedReader(
					new FileReader(scoreFile));
			String line;
			Record rcd = null;
			while ((line = reader.readLine()) != null) {
				if ((rcd = Record.deserializeRecord(line)) != null) {
					int i = 0;
					for (i = 0; i < records.size(); i++) {
						if (recordComparator.compare(rcd, records.get(i)) > 0) {
							break;
						}
					}
					records.add(i, rcd);
				}
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private File getScoreFile(int mode) {
		File file = Environment.getExternalStorageDirectory();
		String packageName = context.getPackageName();
		try {
			File dataFileFolder = new File(file, "data/" + packageName
					+ "/scores");
			if (!dataFileFolder.exists()) {
				dataFileFolder.mkdirs();
			}
			return new File(dataFileFolder, "score_mode_" + mode);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
