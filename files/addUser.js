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

		var data = $('.editable').editable('getValue');

		// TODO validate data

		$.ajax({
			url: '/rest/user/new',
			type: 'POST',
			data: JSON.stringify(data),
			contentType: 'application/json; charset=utf-8',
			dataType: 'json',
			async: false,
			success: function(data) {
				window.location.replace("/backend/users/edit/" + data.id);
			}
		});

	});
});