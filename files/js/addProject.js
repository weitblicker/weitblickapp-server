$.fn.editable.defaults.mode = 'inline';

$(document).ready(function() {
	$('.msg').hide();

    $('.editable').not('#location').editable({
    	//url: '/rest/project/update/en',
    	ajaxOptions: {
    		contentType: 'application/json',
    		dataType: 'json' 
    	},
    	params: function(params) {
    		var ret = {};
    		ret['id'] = params.pk;
    		ret[params.name] = params.value;

    		console.log(params);
    		console.log(ret);
			return JSON.stringify(ret);
		},
		validate: function(value) {
			if($.trim(value) == '') {
    			return 'This field is required!';
			}
		}
	});


	$('#location').editable({
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
        },

        validate: function(value) {
			if($.trim(value) == '') {
    			return 'This field is required!';
			}
		}
    });


	$('#save-btn').click(function() {
		$('.msg').hide();
		
		var data = $('.editable').editable('getValue');

		// TODO validate data

		$.ajax({
			url: '/rest/project/new',
			type: 'POST',
			data: JSON.stringify(data),
			contentType: 'application/json; charset=utf-8',
			dataType: 'json',
			async: false,
			success: function(data) {
				window.location.replace("/backend/projects/de/edit/" + data.id);
			}
		});

	});
});