package com.appspot.perfectgames.dao;

import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.appspot.perfectgames.db.PMF;
import com.appspot.perfectgames.model.Report;

public class ReportDao {
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

	public static boolean saveOrUpdateReport(Report record) {
		try {
			getPersistenceManager().makePersistent(record);
			return true;
		} finally {
		}
	}

	public static boolean deleteReport(Report record) {
		try {
			getPersistenceManager().deletePersistent(record);
			return true;
		} finally {
		}
	}

	public static Report findReport(Long id) {
		try {
			return (Report) getPersistenceManager().getObjectById(id);
		} finally {
		}
	}

	public static List<Report> findByFilter(String filter) {
		if (filter.indexOf("=") > -1
				&& filter.indexOf("=") != filter.indexOf("==")) { // 对=做一些处理
			filter = filter.replace("=", "==");
		}
		filter = filter.replace(",", " && ");
		try {
			System.out.println("filter: " + filter);
			Query query = getPersistenceManager()
					.newQuery(Report.class, filter);
			List<Report> records = (List<Report>) query.execute();
			records.size(); // 这个步骤用于解决延迟加载问题
			return records;
		} finally {
		}
	}

	public static List<Report> findAll() {
		try {
			Query query = getPersistenceManager().newQuery(
					"select from " + Report.class.getName());
			query.setOrdering("commitTime desc");
			List<Report> records = (List<Report>) query.execute();
			records.size();
			return records;
		} finally {
		}
	}

	public static void closePersistenceManager() {
		pm.close();
	}

	public static void commitRecord(Report record) {
		saveOrUpdateReport(record);
	}

	public static void updateRecords() {

	}
}
