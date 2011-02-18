package com.appspot.perfectgames.dao;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.appspot.perfectgames.cfg.Constants;
import com.appspot.perfectgames.db.PMF;
import com.appspot.perfectgames.model.TwentySecondsRecord;

public class TwentySecondsRecordDao {
	private static PersistenceManager pm;
	private static Object lockObject = new Object();

	public static PersistenceManager getPersistenceManager() {
		if (pm == null || pm.isClosed()) {
			synchronized (lockObject) {
				if (pm == null || pm.isClosed()) {
					pm = PMF.get().getPersistenceManager();
				}
			}
		}
		return pm;
	}

	public static boolean saveOrUpdateTwentySecondsRecord(
			TwentySecondsRecord record) {
		try {
			getPersistenceManager().makePersistent(record);
			return true;
		} finally {
		}
	}

	public static boolean deleteTwentySecondsRecord(TwentySecondsRecord record) {
		try {
			getPersistenceManager().deletePersistent(record);
			return true;
		} finally {
		}
	}

	public static TwentySecondsRecord findTwentySecondsRecord(Long id) {
		try {
			return (TwentySecondsRecord) getPersistenceManager().getObjectById(
					id);
		} finally {
		}
	}

	public static List<TwentySecondsRecord> findByFilter(String filter) {
		if (filter.indexOf("=") > -1
				&& filter.indexOf("=") != filter.indexOf("==")) {
			filter = filter.replace("=", "==");
		}
		filter = filter.replace(",", " && ");
		try {
			System.out.println("filter: " + filter);
			Query query = getPersistenceManager().newQuery(
					TwentySecondsRecord.class, filter);
			List<TwentySecondsRecord> records = (List<TwentySecondsRecord>) query
					.execute();
			records.size(); // 这个步骤用于解决延迟加载问题
			return records;
		} finally {
		}
	}

	public static List<TwentySecondsRecord> getRank(int from, int count) {
		Query query = getPersistenceManager().newQuery(
				TwentySecondsRecord.class);
		query.setOrdering("time desc");
		query.setRange(from, from + count);
		List<TwentySecondsRecord> records = (List<TwentySecondsRecord>) query
				.execute();
		records.size();
		return records;

	}

	public static List<TwentySecondsRecord> findAll() {
		try {
			Query query = getPersistenceManager().newQuery(
					"select from " + TwentySecondsRecord.class.getName());
			List<TwentySecondsRecord> records = (List<TwentySecondsRecord>) query
					.execute();
			records.size();
			return records;
		} finally {
		}
	}

	public static void closePersistenceManager() {
		pm.close();
	}

	public static void commitRecord(TwentySecondsRecord record) {
		record.setCommitTime(new Date());
		saveOrUpdateTwentySecondsRecord(record);
	}

	public static void updateRecords() {
		long time = 0;
		Date commitTime = null;
		Query query = getPersistenceManager().newQuery(
				TwentySecondsRecord.class);
		query.setOrdering("time desc");
		query.setRange(0, Constants.TOP_NUM);
		List<TwentySecondsRecord> records = (List<TwentySecondsRecord>) query
				.execute();
		if (records.size() != 0) {
			time = records.get(records.size() - 1).getTime();
		}
		query = getPersistenceManager().newQuery(TwentySecondsRecord.class);
		query.setOrdering("commitTime desc");
		query.setRange(0, Constants.TOP_NUM);
		records = (List<TwentySecondsRecord>) query.execute();
		if (records.size() != 0) {
			commitTime = records.get(records.size() - 1).getCommitTime();
		} else {
			return;
		}

		// start delete
		query = getPersistenceManager().newQuery(TwentySecondsRecord.class,
				"time < " + time);
		List<TwentySecondsRecord> recordsToDelete = new LinkedList<TwentySecondsRecord>();
		records = (List<TwentySecondsRecord>) query.execute();
		for (TwentySecondsRecord record : records) {
			if (record.getCommitTime().before(commitTime)) {
				recordsToDelete.add(record);
			}
		}
		getPersistenceManager().deletePersistentAll(recordsToDelete);

	}
}
