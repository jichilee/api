/**
 * Created by nel on 16/5/20.
 */
var jsonTransferTime = function(arr, key, format){
    var year, month, day, hour, m, s;
    var i;
    for(i in arr){
        year = arr[i][key].toString().substring(0, 4);
        month = arr[i][key].toString().substring(4, 6);
        day = arr[i][key].toString().substring(6, 8);
        hour = arr[i][key].toString().substring(8, 10);
        m = arr[i][key].toString().substring(10, 12);
        s = arr[i][key].toString().substring(12, 14);
        arr[i][key] = year + "/" + month + "/" + day + " " + hour + ":" + m + ":" + s;
    }
    return arr;
}