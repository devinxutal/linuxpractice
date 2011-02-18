package com.appspot.perfectgames.dao;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.appspot.perfectgames.cfg.Constants;
import com.appspot.perfectgames.db.PMF;
import com.appspot.perfectgames.model.CubeRecord;

public class CubeRecordDao {
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

	public static boolean saveOrUpdateCubeRecord(CubeRecord record) {
		try {
			getPersistenceManager().makePersistent(record);
			return true;
		} finally {
		}
	}

	public static boolean deleteCubeRecord(CubeRecord record) {
		try {
			getPersistenceManager().deletePersistent(record);
			return true;
		} finally {
		}
	}

	public static CubeRecord findCubeRecord(Long id) {
		try {
			return (CubeRecord) getPersistenceManager().getObjectById(id);
		} finally {
		}
	}

	public static List<CubeRecord> findByFilter(String filter) {
		if (filter.indexOf("=") > -1
				&& filter.indexOf("=") != filter.indexOf("==")) { // 对=做一些处理
			filter = filter.replace("=", "==");
		}
		filter = filter.replace(",", " && ");
		try {
			System.out.println("filter: " + filter);
			Query query = getPersistenceManager().newQuery(CubeRecord.class,
					filter);
			List<CubeRecord> records = (List<CubeRecord>) query.execute();
			records.size(); // 这个步骤用于解决延迟加载问题
			return records;
		} finally {
		}
	}

	public static List<CubeRecord> getRank(int cubeOrder, int from, int count) {
		Query query = getPersistenceManager().newQuery(CubeRecord.class,
				"order == " + cubeOrder);
		query.setOrdering("time asc");
		query.setRange(from, from + count);
		List<CubeRecord> records = (List<CubeRecord>) query.execute();
		records.size();
		return records;

	}

	public static List<CubeRecord> findAll() {
		try {
			Query query = getPersistenceManager().newQuery(
					"select from " + CubeRecord.class.getName());
			List<CubeRecord> records = (List<CubeRecord>) query.execute();
			records.size();
			return records;
		} finally {
		}
	}

	public static void closePersistenceManager() {
		pm.close();
	}

	public static void commitRecord(CubeRecord record) {
		record.setCommitTime(new Date());
		saveOrUpdateCubeRecord(record);
	}

	public static void updateRecords() {
		for (int order = 2; order < 7; order++) {
			long time = 0;
			int steps = 0;
			Query query = getPersistenceManager().newQuery(CubeRecord.class,
					"order == " + order);
			query.setOrdering("time asc");
			query.setRange(0, Constants.TOP_NUM);
			List<CubeRecord> records = (List<CubeRecord>) query.execute();
			if (records.size() != 0) {
				time = records.get(records.size() - 1).getTime();
			}
			query = getPersistenceManager().newQuery(CubeRecord.class,
					"order == " + order);
			query.setOrdering("steps asc");
			query.setRange(0, Constants.TOP_NUM);
			records = (List<CubeRecord>) query.execute();
			if (records.size() != 0) {
				steps = records.get(records.size() - 1).getSteps();
			}

			// start delete
			query = getPersistenceManager().newQuery(CubeRecord.class,
					"order == " + order + " && time > " + time);
			List<CubeRecord> recordsToDelete = new LinkedList<CubeRecord>();
			records = (List<CubeRecord>) query.execute();
			for (CubeRecord record : records) {
				if (record.getSteps() > steps) {
					recordsToDelete.add(record);
				}
			}
			getPersistenceManager().deletePersistentAll(recordsToDelete);
		}

	}
}
