"use strict";

/* ===== complete Interestes ====== */
function postInterests(url, interests) {
  let request = new XMLHttpRequest();
  request.open("POST", url, true);
  request.setRequestHeader("X-CSRF-TOKEN", csrfToken);
  request.setRequestHeader("Content-Type", "application/json");
  request.onload = async function () {
    if (this.status >= 200 && this.status < 400) {
      // Success!
      await showPreloader();

      let resp = this.response;
      history.pushState({}, null, "/all-feeds");
      document.write(request.responseText);
    } else {
      // We reached our target server, but it returned an error
    }
  };
  request.send(JSON.stringify(interests));
}

let saveInterestsBtn = document.getElementById("save-interests");
saveInterestsBtn.addEventListener("click", function (e) {
  e.preventDefault;
  let allTags = document.getElementsByClassName("tag");
  let tags = new Array();
  for (let i = 0; i < allTags.length; i++) tags[i] = allTags[i].firstChild.data;
  postInterests("/save-interests", tags);
});
