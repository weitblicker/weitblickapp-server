$.fn.editable.defaults.mode = 'inline';

$(document).ready(function() {
	$('.msg').hide();

    $('.editable').not('#location').not('#host').not('#datetime').editable({
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

    $('#host').editable({
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
            console.log(ret);
            return ret;
        },

        validate: function(value) {
            if($.trim(value) == '') {
                return 'This field is required!';
            }
        }
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



	$('#save-btn').click(function() {
		$('.msg').hide();
		
		var data = $('.editable').editable('getValue');

		// TODO validate data

		$.ajax({
			url: '/rest/meeting/new',
			type: 'POST',
			data: JSON.stringify(data),
			contentType: 'application/json; charset=utf-8',
			dataType: 'json',
			async: false,
			success: function(data) {
                // TODO Guide to right language page or even to an overview where
                // one can choose between keeping or editing the meeting.
                var lang = data.language;
                console.log(lang);
				//window.location.replace("/backend/meetings/en/edit/" + data.id);
                window.location.replace("/backend/meetings/"+lang+"/edit/" + data.id);
			}
		});

	});
});
