// JS STUFF

// console.log("Javascript was loaded...");

function getBaseUrl() {
    // TODO
    return "";
}

$(function() {
    $('#connection-form').submit(function(event) {
      event.preventDefault();
      console.log($(this).serializeArray());
    });
});

$(function() {
    $('#connect-form').submit(function(event) {
      event.preventDefault();
      console.log($(this).serializeArray());
    });
});


function addConnection(data) {

    data.reverse()

    let formdata = {};
    if(data.length == 4) {
        formdata["known"] = (data[3].value == "on") ? true : false;
    }
    formdata["known"] = false;

    formdata["start"] = int(data[2].value);
    formdata["stop"] = int(data[1].value);
    formdata["time"] = int(data[0].value)

    $.ajax({
        url: getBaseUrl() + "/api/addconnection", // TODO change call
        type: "POST",
        data: {
            formdata
        }
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
