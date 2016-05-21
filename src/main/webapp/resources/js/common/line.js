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
var initDashbord = function(id){
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

var line = function(id, data, ptime, value, name){
    //if(meta.pieNum > 0){
    //    arr = eval($("#" + meta.componentFieldsId).val());// 'a','b','c'
    //    genre = eval($("#" + meta.compenentKey).val());
    //    console.info(eval(arr), eval(genre));
    //} else {
    //    initDashbord("chart");
    //}

    data = jsonTransferTime(data, "ptime");

    var chart = new G2.Chart({
        id: 'c1',
        width: 1000,
        height: 500
    });
    chart.source(data, {
        ptime : {
            type: 'timeCat',
            mask: 'yyyy/mm/dd HH:MM:ss',
            alias: 'Year/Month/Day'
        },
        value: {
            alias: '降雪量'
        }
    });
    chart.legend('bottom');
    chart.line().position(ptime + '*' + value).color(name).shape('smooth').size(2);
    chart.point().position(ptime + '*' + value).color(name).shape(name, ['circle', 'rect', 'diamond']).size(4);
    chart.render();
}