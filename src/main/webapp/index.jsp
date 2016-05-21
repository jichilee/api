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
            <textarea class="form-control" rows="5" id="sql" placeholder="select NAME as genre,TASKSTOTAL,WORKERSTOTAL,EXECUTORSTOTAL from monitor.topology where collecttime>20160519174000 and collecttime<20160519174520"></textarea>
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
    <div class="row">

        <div class="col-md-9" id="c1"></div>
    </div>
    <script>

        function summary(sql, quorum){
            $("#c1").empty();
//            var url = "http://localhost:8080/api/getData?num=600024&sql=select%20NAME%20as%20genre,TASKSTOTAL,WORKERSTOTAL,EXECUTORSTOTAL%20from%20monitor.topology%20where%20collecttime%3E20160519174000%20and%20collecttime%3C20160519174520";
            var url = "http://localhost:8080/api/getData?num=600024&sql=" + sql + "&quorum=" + quorum;
            $.get(url,
                    {Action:"get"},
                    function (data, textStatus){
                        //返回的 data 可以是 xmlDoc, jsonObj, html, text, 等等.
                        this; // 在这里this指向的是Ajax请求的选项配置信息，请参考下图
                        //alert(textStatus);//请求状态：success，error等等。
                        //alert(this);
                        var datas = data.result.data;
//                        $("#sqlRet").val(JSON.stringify(datas));
                        $("#sqlRet").val(JsonUti.convertToString(datas));
                        console.log(datas)
//                   var  datas = [
//                        {genre: 'Sports', sold: 27500, satisfied: 25000, dissatisfied: 2000},
//                        {genre: 'Strategy', sold: 11500, satisfied: 10000, dissatisfied: 1500},
//                        {genre: 'Action', sold: 6000, satisfied: 5500, dissatisfied: 500},
//                        {genre: 'Shooter', sold: 3500, satisfied: 3000, dissatisfied: 500},
//                        {genre: 'Other', sold: 1500, satisfied: 1000, dissatisfied: 500},
//                    ];
//                    datas = [
//                        {
//                            genre: 'Sports',
//                            taskstotal: 131,
//                            workerstotal: 15,
//                            executorstotal: 131
//                        },
//                        {
//                            genre: 'Strategy',
//                            taskstotal: 131,
//                            workerstotal: 15,
//                            executorstotal: 131
//                        }
//                    ];
//                    console.log(datas)

                        var Frame = G2.Frame;
                        var Stat = G2.Stat;
                        var frame = new Frame(datas);
                        frame = Frame.combinColumns(frame, ['taskstotal', 'workerstotal', 'executorstotal'], 'count', 'type', 'genre'); // 将'satisfied','dissatisfied'合并成'count'列各自的类型 type = 'satisfied' 或者 type = 'dissatisfied'
                        var chart = new G2.Chart({
                            id : 'c1',
                            width : 1200,
                            height : 300,
                            plotCfg: {
                                margin: [50, 80, 50, 60]
                            }
                        });
                        chart.source(frame); // 载入数据源
                        chart.coord('theta'); // 设置坐标系
                        chart.facet(['genre']); // 设置分面的切割维度
                        chart.intervalStack().position(Stat.summary.percent('count')).color('type'); // 声明图形语法
                        chart.render();
                    });
        }


    </script>
</div>
</div>
<script type="text/javascript">
    var JsonUti = {
        //定义换行符
        n: "\n",
        //定义制表符
        t: "\t",
        //转换String
        convertToString: function(obj) {
            return JsonUti.__writeObj(obj, 1);
        },
        //写对象
        __writeObj: function(obj //对象
                , level //层次（基数为1）
                , isInArray) { //此对象是否在一个集合内
            //如果为空，直接输出null
            if (obj == null) {
                return "null";
            }
            //为普通类型，直接输出值
            if (obj.constructor == Number || obj.constructor == Date || obj.constructor == String || obj.constructor == Boolean) {
                var v = obj.toString();
                var tab = isInArray ? JsonUti.__repeatStr(JsonUti.t, level - 1) : "";
                if (obj.constructor == String || obj.constructor == Date) {
                    //时间格式化只是单纯输出字符串，而不是Date对象
                    return tab + ("\"" + v + "\"");
                }
                else if (obj.constructor == Boolean) {
                    return tab + v.toLowerCase();
                }
                else {
                    return tab + (v);
                }
            }
            //写Json对象，缓存字符串
            var currentObjStrings = [];
            //遍历属性
            for (var name in obj) {
                var temp = [];
                //格式化Tab
                var paddingTab = JsonUti.__repeatStr(JsonUti.t, level);
                temp.push(paddingTab);
                //写出属性名
                temp.push("\"" + name + "\" : ");
                var val = obj[name];
                if (val == null) {
                    temp.push("null");
                }
                else {
                    var c = val.constructor;
                    if (c == Array) { //如果为集合，循环内部对象
                        temp.push(JsonUti.n + paddingTab + "[" + JsonUti.n);
                        var levelUp = level + 2; //层级+2
                        var tempArrValue = []; //集合元素相关字符串缓存片段
                        for (var i = 0; i < val.length; i++) {
                            //递归写对象
                            tempArrValue.push(JsonUti.__writeObj(val[i], levelUp, true));
                        }
                        temp.push(tempArrValue.join("," + JsonUti.n));
                        temp.push(JsonUti.n + paddingTab + "]");
                    }
                    else if (c == Function) {
                        temp.push("[Function]");
                    }
                    else {
                        //递归写对象
                        temp.push(JsonUti.__writeObj(val, level + 1));
                    }
                }
                //加入当前对象“属性”字符串
                currentObjStrings.push(temp.join(""));
            }
            return (level > 1 && !isInArray ? JsonUti.n: "") //如果Json对象是内部，就要换行格式化
                    + JsonUti.__repeatStr(JsonUti.t, level - 1) + "{" + JsonUti.n //加层次Tab格式化
                    + currentObjStrings.join("," + JsonUti.n) //串联所有属性值
                    + JsonUti.n + JsonUti.__repeatStr(JsonUti.t, level - 1) + "}"; //封闭对象
        },
        __isArray: function(obj) {
            if (obj) {
                return obj.constructor == Array;
            }
            return false;
        },
        __repeatStr: function(str, times) {
            var newStr = [];
            if (times > 0) {
                for (var i = 0; i < times; i++) {
                    newStr.push(str);
                }
            }
            return newStr.join("");
        }
    };

    $(document).ready(function() {
        function jump(count) {
            window.setTimeout(function(){
                count--;
                if(count > 0) {
                    $('#num').attr('innerHTML', count);
                    jump(count);
                } else {
                    summary();
                }
            }, 1000);
        }
        var sql = "select NAME as genre,TASKSTOTAL,WORKERSTOTAL,EXECUTORSTOTAL from monitor.topology where collecttime>20160519174000 and collecttime<20160519174520";
        var quorum = "cdn240,cdn241,cdn242:2181/hbase";
        summary(sql, quorum);

        $(".sql-submit").click(function(){
            var sql = $("#sql").val();
            var quorum = $("#datasource").val();
            if(sql){
                sql = sql.replace(/\"/g, '\'')
                console.log(sql, quorum);
                summary(sql, quorum);
                console.log("submit sql with ajax.")
            }
        })
    });
</script>
<%--<%@ include file="/WEB-INF/shares/footer.jsp"%>--%>
</body>
</html>