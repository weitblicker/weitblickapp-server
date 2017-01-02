$.fn.editable.defaults.mode = 'inline';

$(document).ready(function() {
	$('.msg').hide();

	$('.editable').editable({
		ajaxOptions: {
			contentType: 'application/json',
			dataType: 'json' 
		},
		escape: false,
		params: function(params) {
			var ret = {};
			ret['id'] = params.pk;
			ret[params.name] = params.value;
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

		var data = {};

		$('.editable').each(function(index, elem){
			var key = $(elem).attr('id');
			data[key] = $(elem).html();
		});

		// TODO validate data

		$.ajax({
			url: '/rest/host/new',
			type: 'POST',
			data: JSON.stringify(data),
			contentType: 'application/json; charset=utf-8',
			dataType: 'json',
			async: false,
			success: function(data) {
				window.location.replace("/backend/hosts/edit/" + data.id);
			}
		});

	});
});