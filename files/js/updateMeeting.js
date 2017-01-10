$.fn.editable.defaults.mode = 'inline';

$(document).ready(function() {
    $('.editable').not('#location').not('#host').not('#datetime').editable({
        ajaxOptions: {
            contentType: 'application/json',
            dataType: 'json', 
            method: 'PUT'
        },
        params: function(params) {
            var ret = {};
            ret['id'] = params.pk;
            ret[params.name] = params.value;

            console.log(params);
            console.log(ret);
            return JSON.stringify(ret);
        }
    });

    $('#location').editable({
        ajaxOptions: {
            contentType: 'application/json',
            dataType: 'json',
            method: 'PUT'
        },
        params: function(params) {
            var ret = {};
            ret['id'] = params.pk;
            ret[params.name] = params.value;

            console.log(params);
            console.log(ret);
            return JSON.stringify(ret);
        },
        source: function(){
            var list = [];
            $.ajax({
                url: "/rest/location/list",
                async: false,
                dataType: 'json',
                success: function( data ) {
                    data.forEach(function(elem){
                        var elemObj = {
                            value: elem.id,
                            text: "".concat(elem.id, " – ", elem.street, " ", elem.number, ", ", elem.country)
                        };
                        list.push(elemObj);
                    });
                }
            });

            var ret = JSON.stringify(list);
            console.log(ret);
            return ret;
        }
    });

    $('#host').editable({
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
    });

    $('#datetime').editable({
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
        format: 'DD.MM.YYYY HH:mm:ss',    
        viewformat: 'DD.MM.YYYY HH:mm',    
        template: 'DD  MMMM  YYYY   –   HH  mm',    
        combodate: {
            minYear: 2008,
            maxYear: 2025,
            minuteStep: 5
        }
    });

});
