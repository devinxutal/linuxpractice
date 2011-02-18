package com.appspot.perfectgames.servlets.twentyseconds;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.appspot.perfectgames.dao.TwentySecondsRecordDao;
import com.appspot.perfectgames.model.TwentySecondsRecord;
import com.appspot.perfectgames.util.ParamUtil;

public class QueryRecordsServlet extends HttpServlet {

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

		int from = ParamUtil.getInt(request, "from", 1);
		int count = ParamUtil.getInt(request, "count", 10);

		List<TwentySecondsRecord> records = TwentySecondsRecordDao.getRank(
				from - 1, from - 1 + count);
		PrintWriter writer = new PrintWriter(response.getOutputStream());
		int rank = from;
		for (TwentySecondsRecord record : records) {
			writer.println((rank++) + "\t" + record.toTabbedString());
		}
		writer.flush();
	}
}
