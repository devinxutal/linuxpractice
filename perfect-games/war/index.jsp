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
  	<div>Fancy Cube Records</div><br>
    <table  cellpadding="5px">
    	<tr>
    		<td>Rank</td>
    		<td>Player</td>
    		<td>Time</td>
    		<td>Steps</td>
    		<td>Cube Size</td>
    		<td>Descripton</td>
    	</tr>
    	<% 
    		List<CubeRecord> records = CubeRecordDao.findAll();
    		for(int i = 0; i<records.size(); i++){
    			CubeRecord record = records.get(i);
    			String player = record.getPlayer();
    	%>
    		<tr>
    			<td><%=i+1%></td>
    			<td><%=record.getPlayer()%></td>
    			<td><%=record.getTime()/1000 +"."+record.getTime()%1000+" seconds" %></td>
    			<td><%=record.getSteps() %>
    			<td><%=record.getOrder()%></td>
    			<td><%=record.getDescription() %></td>
    		</tr>
    	<% 	} %>
    
    </table>
    
     <br><br>
  	<div>Twenty Seconds Records</div>
  	<br>
    <table cellpadding="5px">
    	<tr>
    		<td>Rank</td>
    		<td>Player</td>
    		<td>Time</td>
    		<td>Descripton</td>
    	</tr>
    	<% 
    		List<TwentySecondsRecord> txRecords = TwentySecondsRecordDao.findAll();
    		for(int i = 0; i<txRecords.size(); i++){
    			TwentySecondsRecord record = txRecords.get(i);
    			String player = record.getPlayer();
    	%>
    		<tr>
    			<td><%=i+1%></td>
    			<td><%=record.getPlayer()%></td>
    			<td><%=record.getTime()/1000 +"."+record.getTime()%1000+" seconds" %></td>
    			<td><%=record.getDescription() %></td>
    		</tr>
    	<% 	} %>
    
    </table>
  </body>
</html>
