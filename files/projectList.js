
$(document).ready(function() {
    $('a.delete').click(function(event){
        event.preventDefault();
        $.ajax({
            url: $(this).attr('href'),
            method: 'DELETE',
            success: function(){
                console.log("Removed project!");
            },
            error: function(){
                console.log("An error occurred!")
            }

        });
    });
});
