
function getSuggestionCard(id, name, email, location) {
    let html = `
        <div class="card">
        <div class="card-body p-5 pb-1 pt-1">
            <h5 class="card-title">${name}</h5>
            <hr class="mt-0 mb-0" />
            <div class="row">
                <div class="col p-2">
                    <p class="m-1">${email}</p>
                    <p class="m-1">${location}</p>
                </div>
                <div class="col d-flex justify-content-center align-items-center p-2">
                    <button
                        type="button"
                        class="btn btn-outline-dark h-75"
                        data-bs-toggle="modal"
                        data-bs-target="#modal-${id}"
                    >
                        Add Connection
                    </button>
                    <div class="modal fade" id="modal-${id}" tabindex="-1">
                        <div class="modal-dialog modal-dialog-centered">
                            <div class="modal-content">
                                <div class="modal-header">
                                    <h5 class="modal-title">Add Connection</h5>
                                    <button
                                        type="button"
                                        class="btn-close"
                                        data-bs-dismiss="modal"
                                        aria-label="Close"
                                    ></button>
                                </div>
                                <div class="modal-body">
                                    <form class="row g-3" id="connection-form">
                                        <input 
                                            type="hidden" 
                                            id="connection-form-userid" 
                                            name="connection-form-userid" 
                                            value="${id}"
                                        >
                                        <div class="row-md-4">
                                            <label for="connection-form-known" class="form-label"
                                                >Already Known</label
                                            >
                                            <input
                                                type="checkbox"
                                                id="connection-form-known"
                                                name="connection-form-known"
                                            />
                                        </div>
                                        <div class="row-md-4">
                                            <label for="connection-form-start" class="form-label"
                                                >Current Connection:</label
                                            >
                                            <br />
                                            <label
                                                for="connection-form-start"
                                                class="form-label d-inline-block"
                                                >I see them every
                                            </label>
                                            <input
                                                type="number"
                                                class="form-control w-25 d-inline-block"
                                                id="connection-form-start"
                                                name="connection-form-start"
                                                value="1"
                                                required
                                                min="0"
                                            />
                                            <label
                                                for="connection-form-start"
                                                class="form-label d-inline-block"
                                                >days</label
                                            >
                                        </div>
                                        <div class="row-md-4">
                                            <label for="connection-form-stop" class="form-label"
                                                >Desired Connection:
                                            </label>
                                            <br />
                                            <label
                                                for="connection-form-stop"
                                                class="form-label d-inline-block"
                                                >I want to see them every
                                            </label>
                                            <input
                                                type="number"
                                                class="form-control w-25 d-inline-block"
                                                id="connection-form-stop"
                                                name="connection-form-stop"
                                                value="10"
                                                required
                                                min="0"
                                            />
                                            <label
                                                for="connection-form-stop"
                                                class="form-label d-inline-block"
                                                >days</label
                                            >
                                        </div>
                                        <div class="row-md-4">
                                            <label for="connection-form-time" class="form-label"
                                                >Timeframe:
                                            </label>
                                            <input
                                                type="number"
                                                class="form-control"
                                                id="connection-form-time"
                                                name="connection-form-time"
                                                value="100"
                                                required
                                                min="0"
                                            />
                                        </div>
                                        <div class="col-12">
                                            <button
                                                id="connection-form-btn"
                                                class="btn btn-outline-dark"
                                                type="submit"
                                                data-bs-dismiss="modal"
                                            >
                                                Submit
                                            </button>
                                        </div>
                                    </form>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        </div>
    `
    return html;
}


$(function() {
    $('#connections-main-section').ready(function(event) {
        cards = [];
        //suggestions = getSuggestions();
        suggestions = [{"userId": "1", "name": "test", "email": "test_email", "city": "madison"},
                        {"userId": "2", "name": "test2", "email": "test2_email", "city": "madison"}];
        for(let i = 0; i < suggestions.length; i++) {
            cards.push(getSuggestionCard(suggestions[i]["userId"], suggestions[i]["name"], 
                                        suggestions[i]["email"], suggestions[i]["city"]))
        }
        let html = cards.join("\n");
        console.log(html)
        $('#connections-main-section').html(html)
    });
});


