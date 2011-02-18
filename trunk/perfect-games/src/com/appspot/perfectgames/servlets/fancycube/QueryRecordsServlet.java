package com.appspot.perfectgames.servlets.fancycube;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.appspot.perfectgames.dao.CubeRecordDao;
import com.appspot.perfectgames.model.CubeRecord;
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

		int order = ParamUtil.getInt(request, "order", 3);
		int from = ParamUtil.getInt(request, "from", 1);
		int count = ParamUtil.getInt(request, "count", 10);

		List<CubeRecord> records = CubeRecordDao.getRank(order, from - 1, from
				- 1 + count);
		PrintWriter writer = new PrintWriter(response.getOutputStream());
		int rank = from;
		for (CubeRecord record : records) {
			writer.println((rank++) + "\t" + record.toTabbedString());
		}
		writer.flush();
	}
}
