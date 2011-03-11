package com.appspot.perfectgames.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.appspot.perfectgames.dao.ReportDao;
import com.appspot.perfectgames.model.Report;

public class CommitReportServlet extends HttpServlet {

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
		String info = request.getParameter("info").trim();
		String rpt = request.getParameter("report").trim();
		String app = request.getParameter("app").trim();
		if (info.length() > 0 && rpt.length() > 0 && app.length() > 0) {
			Report report = new Report();
			report.setInfo(info);
			report.setApp(app);
			report.setReport(rpt);
			ReportDao.commitRecord(report);
			ReportDao.closePersistenceManager();
		}
	}
}
