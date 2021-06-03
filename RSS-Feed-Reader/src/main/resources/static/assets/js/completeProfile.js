/* ===== complete Interestes ====== */
function postInterests(url, interests) {
    var request = new XMLHttpRequest();
    request.open('POST', url, true);
    request.setRequestHeader('X-CSRF-TOKEN', csrfToken);
    request.setRequestHeader('Content-Type', 'application/json');
    request.onload = async function () {
        if (this.status >= 200 && this.status < 400) {
            // Success!
            await showPreloader();

            var resp = this.response;
            history.pushState({}, null, "/all-feeds");
            document.write(request.responseText);
        } else {
            // We reached our target server, but it returned an error

        }
    };
    request.send(JSON.stringify(interests));
}

var saveInterestsBtn = document.getElementById("save-interests");
saveInterestsBtn.addEventListener('click', function (e) {
    e.preventDefault
    var allTags = document.getElementsByClassName("tag");
    var tags = new Array();
    for (var i = 0; i < allTags.length; i++)
        tags[i] = allTags[i].firstChild.data;
    postInterests("/save-interests", tags);
});