/**
 * Created by nel on 16/5/20.
 */

/**
 * 容器id.
 * 会在内部构建pie的id,组合域,分类key 等元素
 *
 * @param id
 */
var meta = {
    pieNum: 0
}
var initPieDashbord = function(id){
    console.info(meta.pieNum)
    if(meta.pieNum > 0){
        return;
    }
    var groupId = id + "_pie_div" + meta.pieNum ;
    $("#" + id).before($('<form class="form-inline" role="form"><div id="' + groupId + '" class="form-group">aaaa</div></form>'));

    //$("#" + groupId).append($('<span>pie</span>'));
    $("#" + groupId).append($('<label class="form-label">fields</label><input id="' + id + "_pie_fields_" + meta.pieNum +'" type="text" class="form-control">'));
    $("#" + groupId).append($('<label class="form-label">key</label><input id="' + id + "_pie_key_" + meta.pieNum +'" type="text" class="form-control">'));
    meta = {
        containerId: id,
        componentFieldsId: id + "_pie_fields_" + meta.pieNum,
        compenentKey: id + "_pie_key_" + meta.pieNum,
        pieNum: meta.pieNum + 1

    }

}

var pie = function(id, data, arr, genre){
    if(meta.pieNum > 0){
        arr = eval($("#" + meta.componentFieldsId).val());// 'a','b','c'
        genre = eval($("#" + meta.compenentKey).val());
        console.info(eval(arr), eval(genre));
    } else {
        initPieDashbord("chart");
    }
    console.info(arr, genre);
    var Frame = G2.Frame;
    var Stat = G2.Stat;
    var frame = new Frame(data);
    frame = Frame.combinColumns(frame, arr, 'count', 'type', genre); // 将'satisfied','dissatisfied'合并成'count'列各自的类型 type = 'satisfied' 或者 type = 'dissatisfied'
    var chart = new G2.Chart({
        id : id,
        width : 1200,
        height : 300,
        plotCfg: {
            margin: [50, 80, 50, 60]
        }
    });
    chart.source(frame); // 载入数据源
    chart.coord('theta'); // 设置坐标系
    chart.facet([genre]); // 设置分面的切割维度
    chart.intervalStack().position(Stat.summary.percent('count')).color('type'); // 声明图形语法
    chart.render();
}