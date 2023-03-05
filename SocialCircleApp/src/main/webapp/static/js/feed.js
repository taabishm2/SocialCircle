function getFeed(event) {
    $.ajax({
        url: getBaseUrl() + "/api/user/feed",
        type: "GET",
        success: function(response) {
            populateFeed(response)
        },
        error: function(response) {
            handleAjaxError(response);
            window.location.replace(getBaseUrl());
        }
    });
     spinner = document.getElementById("search-results-container-spinner");
     spinner.style.display = "none";
    return false;
}

function populateFeed(data) {
    $('#feed-container').empty();
    var feedRows = [];

    for (let i = 0; i < data.length; i++) {
        if (data[i].isReview == true) {
            feedRows.push(getFeedEntryForReview(data[i]));
        } else {
            feedRows.push(getFeedEntryForLike(data[i]));
        }
    }

    $('#feed-container').html(feedRows.join(''));
     feed = document.getElementById("feed-container");
     feed.style.display = "block";
}

function getFeedEntryForReview(data) {
    rating_full_stars = data.review.rating;
    rating_less_stars = 5 - rating_full_stars;

    let ratingStars = ""
    for (let i=0; i<rating_full_stars; i++) {
        ratingStars += "<i class=\"bi bi-star-fill\"></i>"
    }
    for (let i=0; i<rating_less_stars; i++) {
        ratingStars += "<i class=\"bi bi-star\"></i>"
    }

    difficult_full_stars = data.review.difficulty;
    difficult_less_stars = 5 - difficult_full_stars;

    let difficultStars = ""
    for (let i=0; i<difficult_full_stars; i++) {
        difficultStars += "<i class=\"bi bi-star-fill\"></i>"
    }
    for (let i=0; i<difficult_less_stars; i++) {
        difficultStars += "<i class=\"bi bi-star\"></i>"
    }

    const ReviewTemplate = ({ userName, courseName, timestamp, headline, review, ratingStars, difficultStars, url }) => `
        <div class="card">
            <div class="card-body">
                <div class="alert alert-primary alert-dismissible fade show"
                     role="alert" style="margin-top:16px">
                    <div class="icon">
                        <i class="bi bi-file-person"></i>
                        <b>${userName} added a review</b>
                        <div style="font-size:0.9em">
                            <i class="bi bi-pencil-square"></i>
                            &nbspCourse: <span style="font-weight:bold"><a href="${url}">${courseName}</a></span>
                        </div>
                        <div style="font-size:0.9em">
                            <i class="bi bi-clock"></i>
                            &nbspAdded at ${timestamp}
                        </div>
                    </div>
                </div>
                <div class="col-12"
                     style="padding:16px; border:1px solid #ddd; border-radius:10px">
                    <div style="color:#084298; font-size:1.1em; font-weight:bold;">
                        ${headline}
                    </div>
                    <hr/>
                    <div>
                        ${review}
                    </div>
                    <hr>
                    <div class="row">
                        <div class="col-md-6">
                            <div style="font-weight:bold">Rating</div>
                                ${ratingStars}
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div style="font-weight:bold">Difficulty</div>
                                ${difficultStars}
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    `;

    var loader = [{
        userName: data.user.name,
        courseName: data.courseData.name,
        timestamp: data.timestamp,
        headline: data.review.headline,
        review: data.review.review,
        ratingStars: ratingStars,
        difficultStars: difficultStars,
        url: getBaseUrl() + "/course?" + data.courseData.courseId,
    }].map(ReviewTemplate);

    return loader;
}

function getFeedEntryForLike(data) {

    const ReviewTemplate = ({ userName, courseName, timestamp, url }) => `
        <div class="card">
            <div class="card-body">
                <div class="alert alert-primary alert-dismissible fade show"
                     role="alert" style="margin-top:16px">
                    <div class="icon">
                        <i class="bi bi-heart-fill"></i>
                        <b>${userName} liked a Course</b>
                        <div style="font-size:0.9em">
                            <i class="bi bi-pencil-square"></i>
                            &nbspCourse: <span style="font-weight:bold"><a href="${url}">${courseName}</a></span>
                        </div>
                        <div style="font-size:0.9em">
                            <i class="bi bi-clock"></i>
                            &nbspLiked at ${timestamp}
                        </div>
                    </div>
                </div>
            </div>
        </div>
    `
    var loader = [{
        userName: data.user.name,
        courseName: data.courseData.name,
        timestamp: data.timestamp,
        url: getBaseUrl() + "/course?" + data.courseData.courseId,
    }].map(ReviewTemplate);

    return loader;
}

$(document).ready(function() {
    //getFeed();
});