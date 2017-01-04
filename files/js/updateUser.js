$.fn.editable.defaults.mode = 'inline';

$(document).ready(function() {

    var showUpdatedMsg = function(response, newValue) {
        console.log("success.");
        if(response.status == 'error')
            return response.msg;
        $('#msg').html("user has been updated.");
        $('#msg').show().delay(2000).fadeOut();
    } 


    $('.editable').not('#hostIds').editable({
        ajaxOptions: {
            contentType: 'application/json',
            dataType: 'json', 
            method: 'PUT'
        },
        params: function(params) {
            var ret = {};
            ret['id'] = params.pk;
            ret[params.name] = params.value;
            return JSON.stringify(ret);
        },
        success: showUpdatedMsg,
    });

    $('#hostIds').editable({
        ajaxOptions: {
            contentType: 'application/json',
            dataType: 'json',
            method: 'PUT'
        },
        params: function(params) {
            var ret = {};
            ret['id'] = params.pk;
            ret[params.name] = params.value;
            return JSON.stringify(ret);
        },
        source: function(){
            var list = [];
            $.ajax({
                url: "/rest/host/list",
                async: false,
                dataType: 'json',
                success: function( data ) {
                    data.forEach(function(elem){
                        var elemObj = {
                            value: elem.id,
                            text: "".concat(elem.id, " – ", elem.name)
                        };
                        list.push(elemObj);
                    });
                }
            });

            var ret = JSON.stringify(list);
            return ret;
        },
        success: showUpdatedMsg,
    });

});
