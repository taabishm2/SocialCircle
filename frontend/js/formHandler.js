// JS STUFF

console.log("AAA");

$('#connection-form').submit(function (event) {
	// event.preventDefault();
    console.log("Connection form");
	console.log($(this).serializeArray());
});

$('#connect-form').on('submit', function (event) {
	event.preventDefault();
    console.log("Connect form");
	console.log($(this).serializeArray());
});


function doStuff() {
    console.log("Connectionform");
    var formData = new FormData(document.querySelector('#connection-form'))

    console.log(formData);
}