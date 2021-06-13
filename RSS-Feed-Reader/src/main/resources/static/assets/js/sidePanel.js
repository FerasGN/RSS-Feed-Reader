"use strict";

/* ===== Responsive Sidepanel ====== */
var sidePanelToggler = document.getElementById("sidepanel-toggler");
var sidePanel = document.getElementById("app-sidepanel");
var sidePanelDrop = document.getElementById("sidepanel-drop");
var sidePanelClose = document.getElementById("sidepanel-close");

window.addEventListener("load", function () {
  responsiveSidePanel();
});

window.addEventListener("resize", function () {
  responsiveSidePanel();
});

function responsiveSidePanel() {
  let w = window.innerWidth;
  if (w >= 1200) {
    // if larger
    //console.log('larger');
    sidePanel.classList.remove("sidepanel-hidden");
    sidePanel.classList.add("sidepanel-visible");
  } else {
    // if smaller
    //console.log('smaller');
    sidePanel.classList.remove("sidepanel-visible");
    sidePanel.classList.add("sidepanel-hidden");
  }
}

sidePanelToggler.addEventListener("click", () => {
  if (sidePanel.classList.contains("sidepanel-visible")) {
    console.log("visible");
    sidePanel.classList.remove("sidepanel-visible");
    sidePanel.classList.add("sidepanel-hidden");
  } else {
    console.log("hidden");
    sidePanel.classList.remove("sidepanel-hidden");
    sidePanel.classList.add("sidepanel-visible");
  }
});

sidePanelClose.addEventListener("click", (e) => {
  e.preventDefault();
  sidePanelToggler.click();
});

sidePanelDrop.addEventListener("click", (e) => {
  sidePanelToggler.click();
});

/* ===== Search for channel and save channel ====== */
function getSubscribeModal(url, container) {
  var request = new XMLHttpRequest();
  request.open("GET", url, true);

  request.onload = function () {
    if (this.status >= 200 && this.status < 400) {
      // Success!
      var resp = this.response;
      container.innerHTML = request.responseText;

      // if the channel was found
      channelInfo = document.getElementById("channel-info").innerText;
      if (
        channelInfo != "URL was not found" &&
        channelInfo != "Channel already exists"
      ) {
        initModalCategories();
        addSubscribeButton();
      }
    } else {
      // We reached our target server, but it returned an error
    }
  };

  request.onerror = function () {
    // There was a connection error of some sort
  };

  request.send();
}

function initModalCategories() {
  var div = document.createElement("div");
  div.id = "category-container";
  div.className += "form-group mb-4";

  var label = document.createElement("label");
  label.setAttribute("for", "category");
  label.className += "form-label";
  label.innerText = "Category";

  div.appendChild(label);

  var input = document.createElement("input");
  input.id = "category";
  input.className += "form-control";
  input.setAttribute("type", "text");
  input.setAttribute("list", "categories");
  input.placeholder = "Add the channel to a a Category...";
  input.autocomplete = "off";
  input.required = true;
  input.name = "categoryCommand.name";

  div.appendChild(input);

  var dataList = document.createElement("datalist");
  dataList.id = "categories";

  div.appendChild(dataList);

  var urlContainer = document.getElementById("url-container");
  urlContainer.parentNode.insertBefore(div, urlContainer.nextSibling);

  var request = new XMLHttpRequest();

  // Handle state changes for the request.
  request.onreadystatechange = function (response) {
    if (request.readyState === 4) {
      if (request.status >= 200 && request.status < 400) {
        // Parse the JSON
        var jsonOptions = JSON.parse(request.responseText);

        // Loop over the JSON array.
        jsonOptions.forEach(function (item) {
          // Create a new <option> element.
          var option = document.createElement("option");
          // Set the value using the item in the JSON array.
          option.value = item;
          // Add the <option> element to the <datalist>.
          dataList.appendChild(option);
        });
      } else {
        // An error occured
        input.placeholder = "Couldn't load datalist options";
      }
    }
  };

  request.open("GET", "/findCategories", true);
  request.send();
}

function searchChannel() {
  var searchChannelButton = document.getElementById("search-channel-button");
  searchChannelButton.disabled = true;
  searchChannelButton.innerHTML =
    '<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span> ' +
    "Searching...";
  var subscribeModal = document.getElementById("subscribe-modal");
  var channelUrl = document.getElementById("channel-url").value;
  getSubscribeModal("/search-channel?url=" + channelUrl, subscribeModal);
}

// if channel url was valid and not found in the DB, show the save channel button
function addSubscribeButton() {
  var buttonContainer = document.getElementById("button-container");
  var saveChannelButton = document.createElement("button");
  saveChannelButton.id = "save-channel-button";
  saveChannelButton.className += "btn app-btn-primary";
  saveChannelButton.innerText = "Subscribe to channel";
  buttonContainer.appendChild(saveChannelButton);

  // clean up the default image for the channel image
  var warpChannelImage = document.getElementById("warp-channel-image");
  warpChannelImage.classList.add("warp-channel-image-no-after");

  // bind save channel button to the channel url input
  var channelUrl = document.getElementById("channel-url");
  channelUrl.addEventListener("input", (e) => {
    const categoryContainer = document.getElementById("category-container");
    const saveChannelButton = document.getElementById("save-channel-button");
    if (typeof saveChannelButton != "undefined" && saveChannelButton != null) {
      saveChannelButton.parentNode.removeChild(saveChannelButton);
    }

    if (typeof categoryContainer != "undefined" && categoryContainer != null) {
      categoryContainer.parentNode.removeChild(categoryContainer);
    }
  });
}

/* ===== reset bootstrap modal on hide ====== */
var subscribeModal = document.getElementById("subscribe-modal");
subscribeModal.addEventListener("hidden.bs.modal", function (event) {
  const channelInfo = document.getElementById("channel-info");
  const channelUrl = document.getElementById("channel-url");
  const categoryContainer = document.getElementById("category-container");
  const searchChannelButton = document.getElementById("search-channel-button");
  const saveChannelButton = document.getElementById("save-channel-button");

  channelInfo.innerText = "";
  channelUrl.value = "";

  if (typeof categoryContainer != "undefined" && categoryContainer != null)
    categoryContainer.parentNode.removeChild(categoryContainer);

  searchChannelButton.disabled = false;
  searchChannelButton.innerHTML = "Search";
  if (typeof saveChannelButton != "undefined" && saveChannelButton != null)
    saveChannelButton.parentNode.removeChild(saveChannelButton);
});
