

function getBaseUrl() {
    return "";
}

function getSuggestions() {

    let suggestions = [];

    $.ajax({
        url: getBaseUrl() + "/api/contact/suggested",
        type: "GET",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded"
        },
        success: function(response) {
            // getSuccessSnackbar("Logged in. Redirecting to home...");
            // await delay(3000);
            // window.location.replace(getBaseUrl() + "/feed");
        },
        error: handleAjaxError
    });

    return suggestions;


}

function getSuggestions() {

    let suggestions = [];

    $.ajax({
        url: getBaseUrl() + "/api/contact/suggested",
        type: "GET",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded"
        },
        success: function(response) {
            // getSuccessSnackbar("Logged in. Redirecting to home...");
            // await delay(3000);
            // window.location.replace(getBaseUrl() + "/feed");
        },
        error: handleAjaxError
    });

    return suggestions;


}