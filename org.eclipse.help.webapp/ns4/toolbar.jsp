<%@ page import="org.eclipse.help.servlet.*" errorPage="err.jsp" contentType="text/html; charset=UTF-8"%>

<% 
	// calls the utility class to initialize the application
	application.getRequestDispatcher("/servlet/org.eclipse.help.servlet.InitServlet").include(request,response);
%>


<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<!--
 (c) Copyright IBM Corp. 2000, 2002.
 All Rights Reserved.
-->
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Toolbar </title>
 
 
<script language="JavaScript">

// workaround for netscape resize bug
window.onresize = function (evt) { location.reload(); };

function bookmarkPage(button)
{
	// Currently we pick up the url from the content page.
	// If the page is from outside the help domain, a script
	// exception is thrown. We need to catch it and ignore it.

	parent.switchTab("bookmarks");
		
	// use the url from plugin id only
	var url = parent.MainFrame.location.href;
	var i = url.indexOf("content/help:/");
	if (i >=0 )
		url = url.substring(i+13);
	// remove any query string
	i = url.indexOf("?");
	if (i >= 0)
		url = url.substring(0, i);
		
	var title = parent.MainFrame.document.title;
	if (title == null || title == "")
		title = url;
		
	parent.NavFrame.location = "bookmarks.jsp?add="+url+"&title="+escape(title);

}

function printContent(button)
{
	parent.MainFrame.focus();
	parent.MainFrame.print();
}

function setTitle(label)
{
	if( label == null) label = "";
	var toolbarTitleLayerDoc = document.toolbarTitle.document;

    toolbarTitleLayerDoc.write('<body style="background:#D4D0C8; font-weight:bold; margin:3px; text-indent:4px; padding-left:3px;">');
    toolbarTitleLayerDoc.write(" "+label);
    toolbarTitleLayerDoc.write('</body>');
    toolbarTitleLayerDoc.close();
}


</script>

<style type="text/css">

BODY {
	font: 8pt Tahoma;
	background:black;
	margin:0px;
	padding-bottom:1px;
	padding-right:1px;
}

DIV {
	background:#D4D0C8;
}

TABLE {
	background:#D4D0C8;
	font:8pt Tahoma;
	font-weight:bold;
}

 
</style>

</head>

<body leftmargin="1" topmargin="1" bottommargin="1" marginheight="0" marginwidth="0">

	<table id="toolbarTable"  cellpading=0 cellspacing=0 border=0 width="100%" height="100%" nowrap>
	<tr border=1>
	<td align=left valign=center ><div id="toolbarTitle" style="position:relative; text-indent:4px; font-weight:bold;"> &nbsp;<%=WebappResources.getString("Content", request)%> </div></td>
	<td align=right >
		<a href="#" onclick="bookmarkPage(this)" onmouseover="window.status='<%=WebappResources.getString("BookmarkPage", request)%>';return true;" onmouseout="window.status='';"><img src="../images/bookmark_obj.gif" alt='<%=WebappResources.getString("BookmarkPage", request)%>' border="0" name="bookmark"></a>&nbsp;&nbsp;
		<a  href="#" onclick="printContent(this);" onmouseover="window.status='<%=WebappResources.getString("Print", request)%>';return true;" onmouseout="window.status=''"><img src="../images/print_edit.gif" alt='<%=WebappResources.getString("Print", request)%>' border="0" ></a>&nbsp;
	</td>
	</tr>
	</table>	

      <layer name="liveHelpFrame" style="visibility:hidden;width:0;height:0;" frameborder="no" width="0" height="0" scrolling="no">
      </layer>

<DIV style="width : 3000px; height : 1px; top : 0px; left : 0px;
  position : absolute;
  z-index : 2;
  visibility : visible;
" id="topBorder"><IMG src="../images/blackdot.gif" height="1" width="3000"></DIV>
<DIV style="width : 3000px; height : 1px; top : 25px; left : 0px;
  position : absolute;
  z-index : 2;
  visibility : visible;
" id="bottomBorder"><IMG src="../images/blackdot.gif" height="1" width="3000"></DIV>
</body>
</html>

