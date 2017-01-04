
$(document).ready(function() {
    $('a.delete').click(function(event){
        event.preventDefault();
        $.ajax({
            url: $(this).attr('href'),
            method: 'DELETE',
            success: function(){
                location.reload();
                console.log("Removed host!");
            },
            error: function(){
                console.log("An error occurred!")
            }
        });
    });
});
