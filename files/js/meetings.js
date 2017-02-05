
$(document).ready(function() {
    $('a.delete').click(function(event){
        event.preventDefault();
        $.ajax({
            url: $(this).attr('href'),
            method: 'DELETE',
            success: function(){
                location.reload();
                console.log("Removed meeting!");
            },
            error: function(){
                console.log("An error occurred!")
            }

        });
    });


    $('a.clone').click(function(event){
        event.preventDefault();
        var data = $('.editable').editable('getValue');

        // TODO validate data

        $.ajax({
            url: $(this).attr('href'),
            type: 'POST',
            data: JSON.stringify(data),
            contentType: 'application/json; charset=utf-8',
            dataType: 'json',
            async: false,
            success: function(data) {
                console.log("Meeting cloned");
                window.location.replace("/backend/meetings/en/edit/" + data.id);
            }
        });

        /*
        event.preventDefault();
        $.ajax({
            url: $(this).attr('href'),
            method: 'POST',
            success: function(){
                //location.reload();
                window.location.replace("/backend/meetings/en/edit/" + data.id);
                console.log("Cloned meeting!");
            },
            error: function(){
                console.log("An error occurred!")
            }

        });*/
    });

});
