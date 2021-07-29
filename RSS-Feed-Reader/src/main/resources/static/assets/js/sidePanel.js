"use strict";
/* ===== Variables ====== */
var SEARCH_CHANNEL_URL = "/search-channel";
var REFRESH_HEADER_URL = "/refresh-header";
var FIND_CATEGORIES_URL = "/find-categories";
var SAVE_CHANNEL_URL = "/save-channel";
var EDIT_CATEGORY_URL = "/edit-category";
var DELETE_CATEGORY_URL = "/delete-category";

/* ===== Responsive Sidepanel ====== */
initSidePanel();
initShowSubMenus();

function initSidePanel() {
  var sidePanelToggler = document.getElementById("sidepanel-toggler");
  var sidePanel = document.getElementById("app-sidepanel");
  var sidePanelDrop = document.getElementById("sidepanel-drop");
  var sidePanelClose = document.getElementById("sidepanel-close");

  window.addEventListener("load", function () {
    responsiveSidePanel(sidePanel);
  });

  window.addEventListener("resize", function () {
    responsiveSidePanel(sidePanel);
  });

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
}

function responsiveSidePanel(sidePanel) {
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

function initShowSubMenus() {
  let subSubmenulinks = document.getElementsByClassName("sub-submenu-link");
  for (let i = 0; i < subSubmenulinks.length; i++) {
    if (subSubmenulinks[i].classList.contains("active")) {
      let subSubmenu = subSubmenulinks[i].closest("div");
      let submenuLink = subSubmenu.closest("li").getElementsByTagName("div")[0];
      let textCountArrow = submenuLink.getElementsByTagName("div")[1];
      let submenuToggle = textCountArrow.getElementsByTagName("span")[1];
      submenuToggle.setAttribute("aria-expanded", "true");
      subSubmenu.classList.add("show");
      subSubmenu.classList.remove("collapse");
      break;
    }
  }
}

/* ===== Search for channel and save channel ====== */
function searchChannel() {
  var searchChannelButton = document.getElementById("search-channel-button");
  searchChannelButton.disabled = true;
  searchChannelButton.innerHTML =
    '<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span> ' +
    "Searching...";
  var subscribeModal = document.getElementById("subscribe-modal");
  var channelUrl = document.getElementById("channel-url").value;
  searchForChannel(SEARCH_CHANNEL_URL + "?url=" + channelUrl, subscribeModal);
}

/* ===== Search for channel AJAX ====== */
function searchForChannel(url, container) {
  var request = new XMLHttpRequest();
  request.open("GET", url, true);

  request.onload = function () {
    if (this.status >= 200 && this.status < 400) {
      // Success!
      var resp = this.response;
      container.innerHTML = request.responseText;

      // if the channel was found
      let channelInfo = document.getElementById("channel-info").innerText;
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

function refreshHeader(url, headerContainer) {
  var request = new XMLHttpRequest();
  request.open("GET", url, true);

  request.onload = function () {
    if (this.status >= 200 && this.status < 400) {
      // Success!
      var resp = this.response;
      headerContainer.innerHTML = request.responseText;
      initSidePanel();
      initShowSubMenus();
    } else {
      // We reached our target server, but it returned an error
    }
  };

  request.onerror = function () {
    // There was a connection error of some sort
  };

  request.send();
}

/* =====  post AJAX====== */
function post(url, data, container) {
  let request = new XMLHttpRequest();
  request.open("POST", url, true);
  request.setRequestHeader("X-CSRF-TOKEN", csrfToken);
  request.setRequestHeader("Accept", "application/json");
  request.setRequestHeader("Content-Type", "application/json");
  request.onload = function () {
    if (this.status >= 200 && this.status < 400) {
      //Success
      let resp = this.response;
      if (resp !== "") {
        container.innerHTML = request.responseText;

        var headerContainer = document.getElementById("header-container");
        refreshHeader(REFRESH_HEADER_URL, headerContainer);
      }
      hideModals();
      pageNumber = 1;
    } else {
      // We reached our target server, but it returned an error
    }
  };
 
  request.send(JSON.stringify(data));
}

/* ===== init channel categories ====== */
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
  input.placeholder = "Add the channel to a category...";
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

  request.open("GET", FIND_CATEGORIES_URL, true);
  request.send();
}

// if channel url was valid and not found in the DB, show the save channel button
function addSubscribeButton() {
  var buttonContainer = document.getElementById("button-container");
  var saveChannelButton = document.createElement("button");
  saveChannelButton.id = "save-channel-button";
  saveChannelButton.className += "btn app-btn-primary mb-2";
  saveChannelButton.innerText = "Subscribe to channel";
  buttonContainer.appendChild(saveChannelButton);

  // prevent user from doing any actions while submitting
  saveChannelButton.onclick = (e) => {
    e.preventDefault;
    saveChannelButton.disabled = true;
    saveChannelButton.innerHTML =
      '<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span> ' +
      "Loading channel...";

    let channelUrl = document.getElementById("channel-url");
    let category = document.getElementById("category");
    let selectedView = document.getElementById("view-select").value;
    let selectedPeriod = document.getElementById("period-select").value;
    let selectedOrder = document.getElementById("order-select").value;
    let channelInfos = [channelUrl.value, category.value];
    let currentFeedsUrl =
      "/" + window.location.pathname.replace(/^\/([^\/]*).*$/, "$1");
    let categoryName = undefined;
    let channelTitle = undefined;

    if (
      window.location.href.indexOf(CATEGORY_URL) > -1 &&
      window.location.href.indexOf(CHANNEL_URL) == -1
    ) {
      categoryName = window.location.pathname.split("/").pop();
      currentFeedsUrl = "/category";
    } else if (window.location.href.indexOf(CHANNEL_URL) > -1) {
      channelTitle = decodeURI(window.location.pathname.split("/").pop());
      currentFeedsUrl = "/channel";
    }


    let postChannelCommand = {
      channelUrl: channelUrl.value,
      category: category.value,
      currentPageCommand:{
       currentFeedsUrl,
        categoryName,
        channelTitle,
        selectedView,
        selectedPeriod,
        selectedOrder,
      }
    };

    const listItemsContainer = document.getElementById("list-items-container");
    const cardsContainer = document.getElementById("cards-container");

    if (document.body.contains(cardsContainer)) {
      post(SAVE_CHANNEL_URL, postChannelCommand, cardsContainer);
    } else {
      post(SAVE_CHANNEL_URL, postChannelCommand, listItemsContainer);
    }
    let searchChannelButton = document.getElementById("search-channel-button");
    let dismissModal = document.getElementById("dismiss-modal");

    searchChannelButton.disabled = true;
    channelUrl.disabled = true;
    category.disabled = true;
    dismissModal.removeAttribute("data-bs-dismiss");
    dismissModal.href = "javascript:void(0)";

    window.onkeydown = function (e) {
      if (e.keyCode === 27) {
        // Key code for ESC key
        e.preventDefault();
      }
    };
  };

  // clean up the default image for the channel image
  var warpChannelImage = document.getElementById("warp-channel-image");
  warpChannelImage.classList.add("warp-channel-image-no-after");

  // bind save channel button and channel image to the channel url input
  var channelUrl = document.getElementById("channel-url");
  channelUrl.addEventListener("input", (e) => {
    const channelInfo = document.getElementById("channel-info");
    const categoryContainer = document.getElementById("category-container");
    const saveChannelButton = document.getElementById("save-channel-button");
    const warpChannelImage = document.getElementById("warp-channel-image");
    const faviconLink = document.getElementById("favicon-link");

    channelInfo.innerText = "";

    if (typeof saveChannelButton != "undefined" && saveChannelButton != null) {
      saveChannelButton.parentNode.removeChild(saveChannelButton);
    }

    if (typeof categoryContainer != "undefined" && categoryContainer != null) {
      categoryContainer.parentNode.removeChild(categoryContainer);
    }

    warpChannelImage.classList.remove("warp-channel-image-no-after");
    faviconLink.src = "//:0";
  });
}

/* ===== hide bootstrap modal ====== */
function hideModals(){
  hideSubscribeModal();
  hideRenameModal();
  hideDeleteModal();
}
function hideSubscribeModal() {
  let subscribeModal = document.getElementById("subscribe-modal");
  if(typeof subscribeModal != "undefined" && subscribeModal != null && subscribeModal.classList.contains("show")){
    let modal = bootstrap.Modal.getInstance(subscribeModal);
    modal.hide();

    const warpChannelImage = document.getElementById("warp-channel-image");
    const faviconLink = document.getElementById("favicon-link");
    warpChannelImage.classList.remove("warp-channel-image-no-after");
    faviconLink.src = "//:0";

    const dismissModal = document.getElementById("dismiss-modal");
    dismissModal.setAttribute("data-bs-dismiss", "modal");
    dismissModal.href = "#";

    const channelInfo = document.getElementById("channel-info");
    const channelUrl = document.getElementById("channel-url");
    const categoryContainer = document.getElementById("category-container");
    const searchChannelButton = document.getElementById("search-channel-button");
    const saveChannelButton = document.getElementById("save-channel-button");

    channelInfo.innerText = "";
    channelUrl.value = "";
    channelUrl.disabled = false;

    if (typeof categoryContainer != "undefined" && categoryContainer != null)
      categoryContainer.parentNode.removeChild(categoryContainer);

    searchChannelButton.disabled = false;
    searchChannelButton.innerHTML = "Search";
    if (typeof saveChannelButton != "undefined" && saveChannelButton != null)
      saveChannelButton.parentNode.removeChild(saveChannelButton);

  }
}

function hideRenameModal() {
  let renameModal = document.getElementById("rename-modal");
  if(typeof renameModal != "undefined" && renameModal != null && renameModal.classList.contains("show")){
    let modal = bootstrap.Modal.getInstance(renameModal);
    modal.hide();

    const currentCategoryName = document.querySelector("#rename-modal #current-category-name");
    const newCategoryName = document.querySelector("#rename-modal #new-category-name");
    currentCategoryName.value = "";
    newCategoryName.value = "";

    const dismissModal = document.getElementById("dismiss-modal");
    dismissModal.setAttribute("data-bs-dismiss", "modal");
    dismissModal.href = "#";

  }
}

function hideDeleteModal() {
  let deleteModal = document.getElementById("delete-modal");
  if(typeof deleteModal != "undefined" && deleteModal != null && deleteModal.classList.contains("show")){
    let modal = bootstrap.Modal.getInstance(deleteModal);
    modal.hide();

    const currentCategoryName = document.querySelector("#rename-modal #current-category-name");
    const newCategoryName = document.querySelector("#rename-modal #new-category-name");
    currentCategoryName.value = "";
    newCategoryName.value = "";

    const dismissModal = document.getElementById("dismiss-modal");
    dismissModal.setAttribute("data-bs-dismiss", "modal");
    dismissModal.href = "#";

  }
}


/* ===== reset bootstrap modal on hide ====== */
let subscribeModal = document.getElementById("subscribe-modal");
subscribeModal.addEventListener("hidden.bs.modal", hideSubscribeModal);

let renameModal = document.getElementById("rename-modal");
renameModal.addEventListener("hidden.bs.modal", hideRenameModal);

let deleteModal = document.getElementById("delete-modal");
deleteModal.addEventListener("hidden.bs.modal", hideDeleteModal);

renameModal.addEventListener('show.bs.modal', function (event) {
  let currentCategoryName = document.querySelector("#current-category-name");
  currentCategoryName.value =  event.relatedTarget.getAttribute('data-old-category-name');
})

deleteModal.addEventListener('show.bs.modal', function (event) {
  let categoryNameToDelete = document.querySelector("#category-name-to-delete");
  categoryNameToDelete.value =  event.relatedTarget.getAttribute('data-category-name');
})
/* ===== Edit category name ====== */
let renameCategoryBtn = document.querySelector("#rename-category-btn");
renameCategoryBtn.addEventListener("click", (event) => {

  let oldCategoryName = document.querySelector("#current-category-name").value;
  let newCategoryName = document.querySelector("#new-category-name").value;

  let channelUrl = document.getElementById("channel-url");
  let category = document.getElementById("category");
  let selectedView = document.getElementById("view-select").value;
  let selectedPeriod = document.getElementById("period-select").value;
  let selectedOrder = document.getElementById("order-select").value;
  let currentFeedsUrl =
    "/" + window.location.pathname.replace(/^\/([^\/]*).*$/, "$1");
  let categoryName = undefined;
  let channelTitle = undefined;

  if (
    window.location.href.indexOf(CATEGORY_URL) > -1 &&
    window.location.href.indexOf(CHANNEL_URL) == -1
  ) {
    categoryName = window.location.pathname.split("/").pop();
    currentFeedsUrl = "/category";
  } else if (window.location.href.indexOf(CHANNEL_URL) > -1) {
    channelTitle = decodeURI(window.location.pathname.split("/").pop());
    currentFeedsUrl = "/channel";
  }
  let postCategoryCommand = {
    categoryCommand:{
      name:oldCategoryName
    },
    newCategoryName,
    currentPageCommand:{
     currentFeedsUrl,
      categoryName,
      channelTitle,
      selectedView,
      selectedPeriod,
      selectedOrder,
    }
  };

  const listItemsContainer = document.getElementById("list-items-container");
  const cardsContainer = document.getElementById("cards-container");

  if (document.body.contains(cardsContainer)) {
    post(EDIT_CATEGORY_URL, postCategoryCommand, cardsContainer);
  } else {
    post(EDIT_CATEGORY_URL, postCategoryCommand, listItemsContainer);
  }
});


let deleteCategoryBtn = document.querySelector("#delete-category-btn");
deleteCategoryBtn.addEventListener("click", (event) => {

  let name = document.querySelector("#category-name-to-delete").value;

  let channelUrl = document.getElementById("channel-url");
  let category = document.getElementById("category");
  let selectedView = document.getElementById("view-select").value;
  let selectedPeriod = document.getElementById("period-select").value;
  let selectedOrder = document.getElementById("order-select").value;
  let currentFeedsUrl =
    "/" + window.location.pathname.replace(/^\/([^\/]*).*$/, "$1");
  let categoryName = undefined;
  let channelTitle = undefined;

  if (
    window.location.href.indexOf(CATEGORY_URL) > -1 &&
    window.location.href.indexOf(CHANNEL_URL) == -1
  ) {
    categoryName = window.location.pathname.split("/").pop();
    currentFeedsUrl = "/category";
  } else if (window.location.href.indexOf(CHANNEL_URL) > -1) {
    channelTitle = decodeURI(window.location.pathname.split("/").pop());
    currentFeedsUrl = "/channel";
  }
  let postCategoryCommand = {
    categoryCommand:{
      name
    },
    currentPageCommand:{
     currentFeedsUrl,
      categoryName,
      channelTitle,
      selectedView,
      selectedPeriod,
      selectedOrder,
    }
  };

  const listItemsContainer = document.getElementById("list-items-container");
  const cardsContainer = document.getElementById("cards-container");

  if (document.body.contains(cardsContainer)) {
    post(DELETE_CATEGORY_URL, postCategoryCommand, cardsContainer);
  } else {
    post(DELETE_CATEGORY_URL, postCategoryCommand, listItemsContainer);
  }

});
