/*
 * (c) Copyright IBM Corp. 2000, 2001.
 * All Rights Reserved.
 */
package org.eclipse.help.servlet;
import java.io.*;

import javax.servlet.ServletContext;
import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
/**
 * Helper class for search jsp initialization
 */
public class Search {

	private ServletContext context;
	private EclipseConnector connector;

	/**
	 * Constructor
	 */
	public Search(ServletContext context) {
		this.context = context;
		this.connector = new EclipseConnector(context);
	}
	
	/**
	 * Generates the html for the search results based on input xml data
	 */
	public void generateResults(String query, Writer out) {
		try {
			if (query == null || query.trim().length() == 0)
				return;

			String urlString = "search:/";
			if (query != null && query.length() >= 0)
				urlString += "?" + query;
				
			//System.out.println("search:"+query);
			InputSource xmlSource = new InputSource(connector.openStream(urlString));
			DOMParser parser = new DOMParser();
			parser.parse(xmlSource);
			Element elem = parser.getDocument().getDocumentElement();
			if (elem.getTagName().equals("toc"))
				genToc(elem, out);
			else
				displayProgressMonitor(out, elem.getAttribute("indexed"));
		} catch (Exception e) {
		}
	}
	
	private void genToc(Element toc, Writer out) throws IOException 
	{
		NodeList topics = toc.getChildNodes();
		if (topics.getLength() == 0)
		{
			out.write("Nothing found");
			return;
		}
		out.write("<ul class='expanded'>");
		for (int i = 0; i < topics.getLength(); i++) {
			Node n = topics.item(i);
			if (n.getNodeType() == Node.ELEMENT_NODE)
				genTopic((Element) n, out);
		}
		out.write("</ul>");
	}
	private void genTopic(Element topic, Writer out) throws IOException {
		out.write("<li class=");
		out.write(topic.hasChildNodes() ? "'node'>" : "'leaf'>");
		out.write("<a href=");
		String href = topic.getAttribute("href");
		if (href != null && href.length() > 0) {
			// external href
			if (href.charAt(0) == '/')
				href = "content/help:" + href;
		} else
			href = "javascript:void 0";
		out.write("'" + href + "'>");
		// do this for IE5.0 only. Mozilla and IE5.5 work fine with nowrap css
		out.write("<nobr>");
		out.write(topic.getAttribute("label") + "</nobr></a>");
		if (topic.hasChildNodes()) {
			out.write("<ul class='collapsed'>");
			NodeList topics = topic.getChildNodes();
			for (int i = 0; i < topics.getLength(); i++) {
				Node n = topics.item(i);
				if (n.getNodeType() == Node.ELEMENT_NODE)
					genTopic((Element) n, out);
			}
			out.write("</ul>");
		}
		out.write("</li>");
	}


	private void displayProgressMonitor(Writer out, String indexed) throws IOException {
		//out.write("<script>window.open('progress.jsp?indexed="+indexed+"', null, 'height=200,width=400,status=no,toolbar=no,menubar=no,location=no'); </script>");
		//out.flush();
		int percentage = 0;
		try
		{
			percentage = Integer.parseInt(indexed);
		}
		catch(Exception e)
		{}
		
		StringBuffer sb = new StringBuffer();
		sb
		.append("<CENTER>")
		.append("<TABLE BORDER='0'>")
		.append("    <TR><TD>Indexing...</TD></TR>")
        .append("    <TR>")
        .append("    	<TD ALIGN='LEFT'>")
  		.append("			<DIV STYLE='width:100px;height:16px;border-width:1px;border-style:solid;border-color:black'>")
  		.append("				<DIV ID='divProgress' STYLE='width:"+percentage+"px;height:15px;background-color:Highlight'>")
  		.append("				</DIV>")
  		.append("			</DIV>")
  		.append("		</TD>")
  		.append("	</TR>")
  		.append("	<TR>")
  		.append("		<TD>"+indexed+"% complete</TD>")
  		.append("	</TR>")
  		.append("</TABLE>")
  		.append("</CENTER>")
  		.append("<script language='JavaScript'>")
  		.append("setTimeout('refresh()', 2000);")
  		.append("</script>");

		out.write(sb.toString());
		out.flush();
	}
}