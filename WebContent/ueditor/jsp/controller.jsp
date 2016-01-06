<%@ page language="java" contentType="text/html; charset=utf-8" 
	import="com.baidu.ueditor.ActionEnter" pageEncoding="utf-8"%>

<!-- 这里注掉导入PropertyReader类和MgtPropsConstant类的标签，因为本工程没有依赖管理平台 -->	
<%--@ page language="java" contentType="text/html; charset=utf-8" 
	import="ngves.asiainfo.util.PropertyReader"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
	import="ngves.asiainfo.mgt.MgtPropsConstant" --%>
	
<%@ page trimDirectiveWhitespaces="true" %>
<%

    request.setCharacterEncoding( "utf-8" );
	response.setHeader("Content-Type" , "text/html");
	
	String rootPath = application.getRealPath( "/" );
	
	//以下返回的是富文本图片存储在图片服务器上的根路径和访问根路径。
	//这里注掉是因为本工程没有依赖管理平台，没有引用PropertyReader类和MgtPropsConstant类。
	//String rootPathExternal = PropertyReader.getProperty(MgtPropsConstant.PORP_AI_PIC_RICH_PATH);
	//String rootUrlExternal = PropertyReader.getProperty(MgtPropsConstant.PORP_AI_PIC_RICH_URL);
	
	String rootPathExternal = "D:/pic/rich/";
	String rootUrlExternal = "/pic/rich/";
	
	request.setAttribute("rootPathExternal",rootPathExternal);
	request.setAttribute("rootUrlExternal",rootUrlExternal);
	
	out.write( new ActionEnter( request, rootPath ).exec() );
	
	
	
%>