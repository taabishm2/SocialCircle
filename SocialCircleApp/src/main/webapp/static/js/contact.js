function getData(query) {
    $.ajax({
        url: getBaseUrl() + "/api/user/search?query=" + query,
        type: "GET",
        success: function(response) {
            populateContactData(response)
        },
        error: handleAjaxError
    });
    return false;
}

function populateContactData(data) {
    console.log(data)
    const container = document.getElementById('cards-list');
    $('#cards-list').empty();
    var feedRows = [];

    var text = document.getElementById('search-input').value.toUpperCase();

    for (let index = 0; index < data.length; index++) {

        feedRows.push(getContactTile(data[index]));
        continue;

        if (data[index]['name'].toUpperCase().indexOf(text) > -1) {

            const link = document.createElement('a');
            link.href = getBaseUrl() + "/contact?" + data[index]['contactId']

            const card = document.createElement('div');
            card.className = "contact-card";

            const title = document.createElement('div');
            title.className = "title";
            title.innerHTML = data[index]['name'];

            const body = document.createElement('div');
            body.className = "body";
            body.innerHTML = data[index]['description'];

            card.append(title);
            card.append(body);

            link.append(card);
            container.append(link);

        }
    }
    $('#cards-list').html(feedRows.join(''));
    return false;
}

function connectWith(id) {
    var json = JSON.parse(toJson($('#connectform' + id)));
    console.log(json);
    $.ajax({
        url: getBaseUrl() + "/api/contacts/add-contact/" + id + "?"
        + "initialRate=" + json['currentFreq'] + "&targetRate=" + json['targetFreq'] + "&timeframe=" + json['timeframe'],
        type: "POST",
        success: function(response) {
            $("#connect" + id).prop("disabled", true);
            $("#connectform" + id)[0].reset();
            $("#basicModal" + id).modal("hide");
            getSuccessSnackbar("Success!");
        },
        error: handleAjaxError
    });
    return false;
}

function meetWith(id) {
    var json = JSON.parse(toJson($('#meetform' + id)));
    console.log(json);
    $.ajax({
        url: getBaseUrl() + "/api/connects/?connectedWithUserId=" + id + "&score=" + json['connectScore'] + "&notes=" + json['connectNotes'],
        type: "POST",
        success: function(response) {
            $("#meetform" + id)[0].reset();
            $("#basicMeetModal" + id).modal("hide");
            getSuccessSnackbar("Success!");
        },
        error: handleAjaxError
    });
    return false;
}

function getContactTile(data) {
    const ReviewTemplate = ({ otherUserId, name, email, interests, isContact, disabled }) => `
        <div class="card">
            <div class="card-body">
              <h5 class="card-title">${name}</h5>
              <h6 class="card-subtitle mb-2 text-muted">${email}</h6>
              <p class="card-text">${interests}</p>
              <p class="card-text">
                <button class="btn btn-primary" ${disabled} id="connect${otherUserId}" data-bs-toggle='modal' data-bs-target='#basicModal${otherUserId}'>Add as Contact</button>
                <button class="btn btn-primary" id="meet${otherUserId}" data-bs-toggle='modal' data-bs-target='#basicMeetModal${otherUserId}'>Add Connect</button>
              </p>
            </div>
        </div>
        <div class="modal fade" id="basicModal${otherUserId}" tabindex="-1">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title">Add ${name} as Contact</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                    <form id="connectform${otherUserId}">
                        <fieldset class="row mb-3">
                          <legend class="col-form-label col-sm-3 pt-0">Do you know ${name}?</legend>
                          <div class="col-sm-9">
                            <div class="form-check">
                              <input class="form-check-input" type="radio" name="gridRadios" id="gridRadios1" value="false" checked="">
                              <label class="form-check-label" for="gridRadios1">
                                Yes, I know ${name}
                              </label>
                            </div>
                            <div class="form-check">
                              <input class="form-check-input" type="radio" name="gridRadios" id="gridRadios2" value="true">
                              <label class="form-check-label" for="gridRadios2">
                                No, I don't know ${name}
                              </label>
                            </div>
                          </div>
                        </fieldset>

                        <div class="form-floating mb-3">
                          <input name="currentFreq" type="number" class="form-control" id="currentFreq" placeholder="name@example.com">
                          <label for="floatingInput">Current connecting frequency (days)</label>
                        </div>

                        <div class="form-floating mb-3">
                          <input name="targetFreq" type="number" class="form-control" id="targetFreq" placeholder="name@example.com">
                          <label for="floatingInput">Connecting frequency goal (days)</label>
                        </div>

                        <div class="form-floating mb-3">
                          <input name="timeframe" type="number" class="form-control" id="timeframe" placeholder="name@example.com">
                          <label for="floatingInput">Timeframe to reach goal (days)</label>
                        </div>

                      </form>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                        <button type="button" class="btn btn-primary" onclick="connectWith(${otherUserId})">Connect!</button>
                    </div>
                </div>
            </div>
        </div>

        <div class="modal fade" id="basicMeetModal${otherUserId}" tabindex="-1">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title">Add Connect with ${name}</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                    <form id="meetform${otherUserId}">

                        <div class="form-floating mb-3">
                          <input name="connectScore" type="number" class="form-control" id="connectScore" placeholder="name@example.com">
                          <label for="floatingInput">Score of connect (1-10)</label>
                        </div>

                        <div class="form-floating mb-3">
                          <input name="connectNotes" type="text" class="form-control" id="connectNotes" placeholder="name@example.com">
                          <label for="floatingInput">Connection notes</label>
                        </div>

                      </form>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                        <button type="button" class="btn btn-primary" onclick="meetWith(${otherUserId})">Connect!</button>
                    </div>
                </div>
            </div>
        </div>
        `

    var disable=''
    if (data.isContact) { disable = 'disabled' }

    var loader = [{
        otherUserId: data.user.userId,
        name: data.user.name,
        email: data.user.email,
        interests: data.user.attribute1 + ', ' + data.user.attribute2 + ', ' + data.user.attribute3 + ', ' +
            data.user.attribute4 + ', ' + data.user.attribute5,
        disabled: disable
    }].map(ReviewTemplate);

    return loader;
}

function search() {
     spinner = document.getElementById("search-results-container-spinner");
     spinner.style.display = "block";
     resultContainer = document.getElementById("search-results-container");
     resultContainer.style.display = "none";
     var json = JSON.parse(toJson($("#search-form")));
     console.log(json['query'])
     getData(json['query']);
     spinner = document.getElementById("search-results-container-spinner");
     spinner.style.display = "none";
     resultContainer = document.getElementById("search-results-container");
     resultContainer.style.display = "block";
     return false;
 }

function loadMostLikedContacts() {
    $.ajax({
        url: getBaseUrl() + "/api/contacts/recommendations",
        type: "GET",
        success: function(response) {
            populateLikedContacts(response)
        },
        error: handleAjaxError
    });
}

function populateLikedContacts(response) {
    populateContactData(response);
    spinner = document.getElementById("search-results-container-spinner");
    spinner.style.display = "none";
    resultContainer = document.getElementById("search-results-container");
    resultContainer.style.display = "block";
}

$(document).ready(function() {
    loadMostLikedContacts();
    $('#search-form').submit(search);
});