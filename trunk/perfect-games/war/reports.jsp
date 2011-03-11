<%@ page language="java" pageEncoding="GBK" contentType="text/html; charset=UTF-8"%>
<%@ page import="com.appspot.perfectgames.model.*" %>
<%@ page import="com.appspot.perfectgames.dao.*" %>
<%@ page import="java.util.List" %>
<%@ page isELIgnored="false" %>

<html>
  <head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
    <title>Perfect Games</title>
  </head>

  <body>
  	<div>Reports</div><br>
    <table  cellpadding="5px">
    	<tr>
    		<td>App</td>
    		<td>Info</td>
    		<td>Report</td>
    	</tr>
    	<% 
    		List<Report> records = ReportDao.findAll();
    		for(int i = 0; i<records.size(); i++){
    			Report record = records.get(i);
    	%>
    		<tr>
    			<td><%=record.getApp()%></td>
    			<td><%=record.getInfo()%></td>
    			<td><%=record.getReport() %></td>
    		</tr>
    	<% 	} %>
    
    </table>
    
     </body>
</html>
