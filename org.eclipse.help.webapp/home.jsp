<%@ page import="org.eclipse.help.servlet.*,org.w3c.dom.*" errorPage="err.jsp" contentType="text/html; charset=UTF-8"%>

<% 
	// calls the utility class to initialize the application
	application.getRequestDispatcher("/servlet/org.eclipse.help.servlet.InitServlet").include(request,response);
	
%>

<%
	String query=request.getQueryString();
	query=UrlUtil.changeParameterEncoding(query,"titleJS13", "title");
	String title=UrlUtil.getRequestParameter(query, "title");
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<!--
 (c) Copyright IBM Corp. 2000, 2002.
 All Rights Reserved.
-->
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
 
 <style type="text/css">
 
 BODY {
	background-color: Window;
	font: icon;
	margin:0;
	padding:0;
	cursor:default;	
}  
</style>

</head>

<body>

<div id="bannerTitle" style="background:ButtonFace; width:100%; position:absolute; left:10px; top:20; font: 14pt icon">
	<%=title != null ?title : WebappResources.getString("Bookshelf", request)%>
</div>

</body>
</html>
