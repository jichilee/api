{
    title : {
        text: '节点关系：机房',
        subtext: '数据来自cdn',
        x:'right',
        y:'bottom'
    },
    tooltip : {
        trigger: 'item',
        showDelay: 100,
        enterable:true,
        textStyle : {
            color: 'yellow',
            decoration: 'none',
            fontFamily: 'Verdana, sans-serif',
            fontSize: 15,
            fontStyle: 'italic',
            fontWeight: 'bold'
        },
        formatter: function (params,ticket,callback) {
            var res = '详情:';
            for (var i = 0, l = params.name.split('__'); i < l.length; i++) {
                res += '<br/>' + l[i];
            }
            setTimeout(function (){
                callback(ticket, res);
            }, 1000);
            return 'loading';
        }
        
    },
    toolbox: {
        show : true,
        feature : {
            restore : {show: true},
            magicType: {show: true, type: ['force', 'chord']},
            saveAsImage : {show: true}
        }
    },
    legend: {
        x: 'left',
        data:['外部集合','机房']
    },
    series : [
        {
            type:'force',
            name : "机房情况",
            ribbonType: false,
            categories:#categories#,
            itemStyle: {
                normal: {
                    label: {
                        show: true,
                        textStyle: {
                            color: '#000000'
                        }
                    },
                    nodeStyle : {
                        brushType : 'both',
                        borderColor : 'rgba(255,215,0,0.4)',
                        borderWidth : 6
                    },
                    linkStyle: {
                        type: 'curve',
                        color: '#00FF00'
                    }
                },
                emphasis: {
                    label: {
                        show: true
                        /** textStyle: null 默认使用全局文本样式，详见TEXTSTYLE */
                    },
                    nodeStyle : {
                        /**r: 30 */
                    },
                    linkStyle : {}
                }
            },
            linkSymbol:['arrow'],
            useWorker: false,
            minRadius : 15,
            maxRadius : 25,
            gravity: 0.8,
            scaling: 1,
            /**roam:'move',*/
            nodes:#nodes#,
            links:#links#
        }
    ]
}