
$.fn.editable.defaults.mode = 'inline';

$(document).ready(function() {
    $('.editable').editable({
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
		}
	});
});

var geocoder;
var map;

function initialize() {
    geocoder = new google.maps.Geocoder();
    var latlng = new google.maps.LatLng(-34.397, 150.644);
    var mapOptions = {
        zoom: 8,
        center: latlng
    }
    map = new google.maps.Map(document.getElementById("map"), mapOptions);
}

function codeAddress() {
    var address = document.getElementById("address").value;
    geocoder.geocode( { 'address': address}, function(results, status) {
        if (status == google.maps.GeocoderStatus.OK) {
            console.log(results);
            map.setCenter(results[0].geometry.location);
            var marker = new google.maps.Marker({
                map: map,
                position: results[0].geometry.location
            });
        } else {
            alert("Geocode was not successful for the following reason: " + status);
        }
    });
}



