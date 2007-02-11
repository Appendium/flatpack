
<%@ page language="java" import="java.util.*,net.sf.pzfilereader.*,net.sf.pzfilereader.ordering.*,java.io.File" %>


<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>PZ File Reader JSP Sample</title>
    
  </head>
  <%
  	OrderBy order = null;
  	try{  
    
  	   	File mappingFile = null;
  	   	File txtFile = null;
  	   	String appDirectory = null;
  	   	
  	   	//find out where this application is installed
  	   	appDirectory = getServletContext().getRealPath("");
  	   	
  	   	mappingFile  = new File (appDirectory + "/PEOPLE.pzmap.xml");
  	   	txtFile = new File (appDirectory + "/PEOPLE.txt");
  	   	
  	  	//read in the file
  	  	final PZParser pzparser = DefaultPZParserFactory.getInstance().newDelimitedParser(mappingFile, 
  	  			txtFile, ',', 0, false)	;
  	  	final DataSet ds = pzparser.parse();
  	  			
  	  			
  	  //check to see if there is a paramter in the request that is telling us what column to sort by
  	  if (request.getParameter("orderby") != null && 
  	  		request.getParameter("orderby").trim().length() > 0){	
  	  		
  		//sort the file by what was passed in the request	  		
  	  	order = new OrderBy();
  	  	order.addOrderColumn(new OrderColumn(request.getParameter("orderby"),false)); //set boolean to true for DESC sort
  	  	ds.orderRows(order);
  	  		
  	  }
  	  			
  	  }catch(Exception ex){
  		out.println("Error: " + ex);	  
  	  }
  %>
  <body>
   	<table border="1">
   		<tr>
   			<th>
   				<a href="index.jsp?orderby=FIRSTNAME">
	   				First Name
	   			</a>
   			</th>
   			<th>
   				<a href="index.jsp?orderby=LASTNAME">
	   				Last Name
	   			</a>
   			</th>   
   			<th>
   				<a href="index.jsp?orderby=ADDRESS">
	   				Address
	   			</a>
   			</th>   	
   			<th>
   				<a href="index.jsp?orderby=CITY">
	   				City
	   			</a>
   			</th>	
   			<th>
   				<a href="index.jsp?orderby=STATE">
	   				State
	   			</a>
   			</th>   
   			<th>
   				<a href="index.jsp?orderby=ZIP">
	   				Zip
	   			</a>
   			</th>   										
   		</tr>
   		
   		<%
   			while (ds.next()){
   		%>
   			
   				<tr>
   					<td>
   						<%=ds.getString("FIRSTNAME")%>
   					</td>
   					<td>
   						<%=ds.getString("LASTNAME")%>
   					</td>
    				<td>
   						<%=ds.getString("ADDRESS")%>
   					</td>
    				<td>
   						<%=ds.getString("CITY")%>
   					</td>   					
    				<td>
   						<%=ds.getString("STATE")%>
   					</td>   
     				<td>
   						<%=ds.getString("ZIP")%>
   					</td>    					  					
   				</tr>
   			
   			
   		<%
   			}
   		%>
   		
   	</table>
  </body>
</html>
