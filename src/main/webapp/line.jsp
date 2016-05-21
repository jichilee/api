<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page import="org.apache.commons.io.FileUtils"%>
<%@page import="java.io.File"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%
    FileUtils.writeStringToFile(new File("d:/bkb.txt"), "asdasdadasdasdasd", "utf-8");
%>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>欢迎</title>
    <script src="https://as.alipayobjects.com/g/datavis/g2/1.2.1/index.js"></script>
    <%@ include file="/WEB-INF/shares/global.jsp"%>
</head>

<body>
<%@ include file="/WEB-INF/shares/nav.jsp"%>
<div class="container bs-docs-container">
    <div class="row">
        <div class="col-md-5 form-group workspace">
            <label for="sql" >workbench:</label>
            <span class="glyphicon glyphicon-info-sign" aria-hidden="true" title="select NAME as genre,TASKSTOTAL,WORKERSTOTAL,EXECUTORSTOTAL from monitor.topology where collecttime>20160519174000 and collecttime<20160519174520"></span>
            <textarea class="form-control" rows="5" id="sql" placeholder="select collecttime as ptime, id, executorId as name , emitted as yaxis from monitor.spout_executorStats where collecttime>20160520175000 and collecttime<20160520184520
and componentid='vod-log'"></textarea>
            <button type="button" class="btn btn-success sql-submit">submit</button>
        </div>

        <div class="col-md-4 form-group workspace">
            <label for="sqlRet" >result:</label>
            <span class="glyphicon glyphicon-info-sign" aria-hidden="true" title="select NAME as genre,TASKSTOTAL,WORKERSTOTAL,EXECUTORSTOTAL from monitor.topology where collecttime>20160519174000 and collecttime<20160519174520"></span>
            <textarea class="form-control" rows="5" id="sqlRet" placeholder="return your data." readonly></textarea>
            <%--<button type="button" class="btn btn-success sql-submit">submit</button>--%>
        </div>
        <div class="col-md-2 form-group workspace">
            <label for="datasource" >phoenix-quorum:</label>
            <span class="glyphicon glyphicon-info-sign" aria-hidden="true" title="cdn240,cdn241,cdn242:2181/hbase"></span>
            <textarea class="form-control" rows="5" id="datasource" placeholder="select datasource."></textarea>
            <%--<button type="button" class="btn btn-success sql-submit">submit</button>--%>
        </div>
    </div>

    <%--<div class="row" id="chart-type" >--%>
        <%--<input type="text" class="form-control" id="chart_type">--%>
    <%--</div>--%>

    <div class="row" id="chart">
        <div class="col-md-9" id="c1"></div>
    </div>

</div>
</div>
<script type="text/javascript">

    $(document).ready(function() {

        var sql = "select collecttime as ptime, id, executorId as name , emitted as yaxis from monitor.spout_executorStats where collecttime>20160520175000 and collecttime<20160520184520 and componentid='vod-log'";
        var quorum = "cdn240,cdn241,cdn242:2181/hbase";
        query(sql, quorum, "line");

        $(".sql-submit").click(function(){
            var sql = $("#sql").val();
            var quorum = $("#datasource").val();
//            var chart_type = $("#chart_type").val();
            var chart_type = "line";
            if(sql){
                sql = sql.replace(/\"/g, '\'')
                console.log(sql, quorum);
                query(sql, quorum, chart_type);
                console.log("submit sql with ajax.")
            }
        })
    });

</script>
<%--<%@ include file="/WEB-INF/shares/footer.jsp"%>--%>
</body>
</html>