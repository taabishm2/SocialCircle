console.log("formHandler.js was loaded...");

$(function() {
    $(document).on("submit", "#connection-form", function(event) {
      event.preventDefault();
      console.log("here");
      console.log($(this).serializeArray());
    });
});

$(function() {
    $('#connect-form').submit(function(event) {
      event.preventDefault();
      //console.log($(this).serializeArray());
    });
});


function addConnection(data) {

    data.reverse()

    let formdata = {};
    if(data.length == 5) {
        formdata["known"] = (data[3].value == "on") ? true : false;
    }
    formdata["known"] = false;

    formdata["initialRate"] = int(data[2].value);
    formdata["targetRate"] = int(data[1].value);
    formdata["timeframe"] = int(data[0].value)

    console.log("adding connection with: ", formdata);

    $.ajax({
        url: getBaseUrl() + "/api/contact/add-contact/" + userid,
        type: "POST",
        data: formdata,
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

    return false;
}

// $('#connection-form').submit(function (event) {
// 	event.preventDefault();
//     console.log("Connection form");
// 	console.log($(this).serializeArray());
// });

// $('#connect-form').on('submit', function (event) {
// 	event.preventDefault();
//     console.log("Connect form");
// 	console.log($(this).serializeArray());
// });
