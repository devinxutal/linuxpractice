package com.appspot.perfectgames.servlets.fancycube;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.appspot.perfectgames.dao.CubeRecordDao;
import com.appspot.perfectgames.model.CubeRecord;

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
		String orderStr = request.getParameter("order");
		String shufflesStr = request.getParameter("shuffles");
		String stepsStr = request.getParameter("steps");
		String description = request.getParameter("description");

		long time = Long.valueOf(timeStr);
		int order = Integer.valueOf(orderStr);
		int shuffles = Integer.valueOf(shufflesStr);
		int steps = Integer.valueOf(stepsStr);

		if (time <= 0 || steps <= 0 || order <= 0) {
			return;
		}
		if(shuffles <20){
			return;
		}

		CubeRecord record = new CubeRecord();
		record.setPlayer(player);
		record.setTime(Long.valueOf(time));
		record.setOrder(Integer.valueOf(order));
		record.setShuffleSteps(Integer.valueOf(shuffles));
		record.setSteps(Integer.valueOf(steps));
		record.setDescription(description);
		CubeRecordDao.commitRecord(record);
		CubeRecordDao.closePersistenceManager();
	}
}
