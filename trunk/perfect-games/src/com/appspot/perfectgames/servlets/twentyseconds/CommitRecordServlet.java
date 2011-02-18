package com.appspot.perfectgames.servlets.twentyseconds;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.appspot.perfectgames.dao.TwentySecondsRecordDao;
import com.appspot.perfectgames.model.TwentySecondsRecord;
import com.appspot.perfectgames.util.StringUtility;

public class CommitRecordServlet extends HttpServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		process(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		process(request, response);
	}

	private void process(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String player = request.getParameter("player");
		String timeStr = request.getParameter("time");
		String description = request.getParameter("description");
		if (player == null || player.length() == 0) {
			player = "unknown";
		}
		player = StringUtility.trim(player);
		description = StringUtility.trim(description);
		long time = Long.valueOf(timeStr);

		if (time <= 0) {
			return;
		}

		TwentySecondsRecord record = new TwentySecondsRecord();
		record.setPlayer(player);
		record.setTime(Long.valueOf(time));
		record.setDescription(description);
		TwentySecondsRecordDao.commitRecord(record);
		TwentySecondsRecordDao.closePersistenceManager();
	}
}
