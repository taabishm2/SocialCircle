function getAuth() {
    $.ajax({
        url: getBaseUrl() + "/api/user/auth",
        type: "GET",
        success: function(response) {
            checkAuth(response)
        },
        error: handleAjaxError
    });
}

function checkAuth(data) {
    if (data.isAuthenticated) {
        console.log("AUTHED")
        $('#loginBtn').html('Logged in!');
        $('#loginBtn').prop('disabled', true);
        //window.location.replace(getBaseUrl() + "/feed");
    }
    $('#load-screen').hide();
}

getAuth();