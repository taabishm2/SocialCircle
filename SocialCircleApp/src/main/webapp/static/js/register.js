function login(event) {
    var json = JSON.parse(toJson($("#register-form")));
    $.ajax({
        url: getBaseUrl() + "/api/user/register",
        type: "POST",
        data: {
            "email": json['email'],
            "password": json['password'],
            "name": json['name'],
            "city": json['city'],
            "phone": json['phone'],
            "attribute1": json['attribute1'],
            "attribute2": json['attribute2'],
            "attribute3": json['attribute3'],
            "attribute4": json['attribute4'],
            "attribute5": json['attribute5'],
        },
        headers: {
            "Content-Type": "application/x-www-form-urlencoded"
        },
        success: function(response) {
            getSuccessSnackbar("Success! Redirecting to Log In...");
            //await delay(3000);
            window.location.replace(getBaseUrl() + "/login");
        },
        error: handleAjaxError
    });
    return false;
}

function init() {
    $('#register-form').submit(login);
}

$(document).ready(function() {
    init();
});