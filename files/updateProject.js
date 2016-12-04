	$.fn.editable.defaults.mode = 'inline';

$(document).ready(function() {
    $('.editable').not('#location').editable({
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

    $("#input-24").fileinput({
    	//uploadUrl: '/backend/upload/test2.jpg',
    	uploadAsync: true,
    	maxFileCount: 5,
        initialPreview: [
            'http://upload.wikimedia.org/wikipedia/commons/thumb/e/e1/FullMoon2010.jpg/631px-FullMoon2010.jpg',
            'http://upload.wikimedia.org/wikipedia/commons/thumb/6/6f/Earth_Eastern_Hemisphere.jpg/600px-Earth_Eastern_Hemisphere.jpg'
        ],
        initialPreviewAsData: true,
        initialPreviewConfig: [
            {caption: "Moon.jpg", size: 930321, width: "120px", key: 1},
            {caption: "Earth.jpg", size: 1218822, width: "120px", key: 2}
        ],
        deleteUrl: "/site/file-delete",
        overwriteInitial: false,
        maxFileSize: 1000,
        initialCaption: "The Moon and the Earth"
    });

});
