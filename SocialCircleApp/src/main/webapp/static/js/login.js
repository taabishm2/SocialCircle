function login(event) {
    var json = JSON.parse(toJson($("#login-form")));
    $.ajax({
        url: getBaseUrl() + "/api/login",
        type: "POST",
        data: {
            "email": json['email'],
            "password": json['password']
        },
        headers: {
            "Content-Type": "application/x-www-form-urlencoded"
        },
        success: function(response) {
            getSuccessSnackbar("Logged in. Redirecting to home...");
            //await delay(3000);
            window.location.replace(getBaseUrl() + "/feed");
        },
        error: handleAjaxError
    });
    return false;
}

function init() {
    $('#login-form').submit(login);
}

$(document).ready(function() {
    init();
});