{
    "title": {
        "text": "Force",
        "subtext": "Force-directed tree",
        "x": "right",
        "y": "bottom"
    },
    "tooltip": {
        "trigger": "item"
    },
    "toolbox": {
        "show": true,
        "feature": {
            "restore": {
                "show": true
            },
            "magicType": {
                "show": true,
                "type": [
                    "force",
                    "chord"
                ]
            },
            "saveAsImage": {
                "show": true
            }
        }
    },
    "legend": {
        "x": "left",
        "data": [
                 '外部集合','机房'
        ]
    },
    "series": [
        {
            "type": "force",
            "name": "Force tree",
            "ribbonType": false,
            "categories": #categories#,
            "nodes": #nodes#,
            "links": #links#
        }
    ]
}