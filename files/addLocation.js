$.fn.editable.defaults.mode = 'inline';

$(document).ready(function() {
	$('.msg').hide();

    $('.editable').editable({
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

	$('#save-btn').click(function() {
		$('.msg').hide();
		console.log("clicked...");
		$('.editable').editable('submit', { 
			url: '/rest/location/new',

			params: function(params) {
        		var ret = {};
        		ret[params.name] = params.value;
				return JSON.stringify(ret);
			},

			ajaxOptions: {
				dataType: 'json',
				contentType: 'application/json',
				beforeSend: function(jqXHR,options){
						console.log(options);
					if ( options.contentType == "application/json") {
						console.log("convert...");
						var opject = JSON.parse('{"' + decodeURI(options.data).replace(/"/g, '\\"').replace(/&/g, '","').replace(/=/g,'":"') + '"}');
						options.data = JSON.stringify(opject);
						console.log(options.data);
					}
				},
				success: function(data){
					console.log(data);
					window.location.replace("/backend/locations/en/edit/" + data.id);
					console.log("success...!");
				}
			},

			success: function(data, textStatus, jqXHR) {
				console.log("success!");
				
				/*if(data && data.id) {  //record created, response like 
					$(this).editable('option', 'pk', data.id);
					//remove unsaved class
					$(this).removeClass('editable-unsaved');
					//show messages
					var msg = 'New project created! Now editables submit individually.';

					$('#msg').addClass('alert-success').removeClass('alert-error').html(msg).show();
					$('#save-btn').hide(); 
					$(this).off('save.newuser');
				} else if(data && data.errors){ 
					//server-side validation error, response like {"errors": {"username": "username already exist"} }
					//config.error.call(this, data.errors);
				} 
				*/              
       		},
       		error: function(errors) {
       			console.log("errors :", errors);
				var msg = '';
				if(errors && errors.responseText) {
					//ajax error,errors = xhr object
					msg = errors.responseText;
				} else { //validation error (client-side or server-side)
					console.log(errors);
					$.each(errors, function(k, v) { 
						$('.msg'+'.'+k).html(v).show();
						console.log(k, v);
						msg = 'You need to fill the required fields!';
					});
				} 
				$('#msg').removeClass('alert-success').addClass('alert-warning').html(msg).show();
			}
		});
	});
});