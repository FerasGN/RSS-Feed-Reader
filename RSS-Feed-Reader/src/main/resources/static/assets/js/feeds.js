"use strict";
/* ===== Variables ====== */
var HOST = "http://localhost:8080/";
var ALL_FEEDS_URL = "/all-feeds";
var READ_LATER_URL = "/read-later";
var LIKED_FEEDS_URL = "/liked-feeds";
var FAVORITE_CHANNELS_URL = "/favorite-channels";
var RECENTLY_READ_URL = "/recently-read";
var CATEGORY_URL = "/category/";
var CHANNEL_URL = "/channel/";
var LIKE_URL = "/like";
var MARK_AS_READ_LATER = "/mark-as-read-later";
var MARK_AS_READ = "/mark-as-read";
var LAST_READING_TIME = "/last-reading-date";
var FEEDS_PAGE_URL = HOST + "feeds-page";
var SEARCH_URL = HOST + "search";
var SSE_NOTIFICATIONS_URL = HOST + "sse-notifications";
var currentFeedsUrl =
  "/" + window.location.pathname.replace(/^\/([^\/]*).*$/, "$1");

var pageNumber = 1;

/* ===== Ajax ====== */
async function getAllFeeds(url, container) {
  let request = new XMLHttpRequest();
  request.open("GET", url, true);

  request.onload = function () {
    if (this.status >= 200 && this.status < 400) {
      // Success!
      const preloader = document.getElementById("loader");
      preloader.classList.add("d-none");

      let resp = this.response;
      container.innerHTML = request.responseText;
      initInteractionsButtons();
    } else {
      // We reached our target server, but it returned an error
    }
  };

  request.onerror = function () {
    // There was a connection error of some sort
  };

  await showPreloader();
  request.send();
}

function getFeedsPage(url, container) {
  let request = new XMLHttpRequest();
  request.open("GET", url, true);

  request.onload = function () {
    if (this.status >= 200 && this.status < 400) {
      // Success!
      let resp = this.response;
      if (resp !== "") {
        container.innerHTML += request.responseText;
        initInteractionsButtons();
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

async function getSearchResults(url, container) {
  let request = new XMLHttpRequest();
  request.open("GET", url, true);

  request.onload = function () {
    if (this.status >= 200 && this.status < 400) {
      // Success!
      const preloader = document.getElementById("loader");
      preloader.classList.add("d-none");
      let resp = this.response;
      if (resp !== "") {
        container.innerHTML = request.responseText;
        initInteractionsButtons();
      }
    } else {
      // We reached our target server, but it returned an error
    }
  };
  request.onerror = function () {
    // There was a connection error of some sort
  };

  await showPreloader();
  request.send();
}

function postInteraction(url, payload) {
  let request = new XMLHttpRequest();
  request.open("POST", url, true);
  request.setRequestHeader("X-CSRF-TOKEN", csrfToken);
  request.setRequestHeader("Content-Type", "application/json");

  request.onload = function () {
    if (this.status >= 200 && this.status < 400) {
      console.log("click Successed!");
    } else {
      // We reached our target server, but it returned an error
    }
  };
  request.onerror = function () {
    // There was a connection error of some sort
  };

  // await showPreloader();
  request.send(JSON.stringify(payload));
}

/* ===== Select order and period ====== */
let viewSelect = document.getElementById("view-select");
let orederSelect = document.getElementById("order-select");
let periodSelect = document.getElementById("period-select");

function handleViewAndPerieodAndOrderSelect(
  selectedView,
  selectedPeriod,
  selectedOrder,
  feedsContainer
) {
  if (window.location.href.indexOf(ALL_FEEDS_URL) > -1)
    handleViewAndPeriodAndOrder(
      ALL_FEEDS_URL,
      selectedView,
      selectedPeriod,
      selectedOrder,
      feedsContainer
    );
  else if (window.location.href.indexOf(READ_LATER_URL) > -1)
    handleViewAndPeriodAndOrder(
      READ_LATER_URL,
      selectedView,
      selectedPeriod,
      selectedOrder,
      feedsContainer
    );
  else if (window.location.href.indexOf(LIKED_FEEDS_URL) > -1)
    handleViewAndPeriodAndOrder(
      LIKED_FEEDS_URL,
      selectedView,
      selectedPeriod,
      selectedOrder,
      feedsContainer
    );
  else if (window.location.href.indexOf(FAVORITE_CHANNELS_URL) > -1)
    handleViewAndPeriodAndOrder(
      FAVORITE_CHANNELS_URL,
      selectedView,
      selectedPeriod,
      selectedOrder,
      feedsContainer
    );
  else if (window.location.href.indexOf(RECENTLY_READ_URL) > -1)
    handleViewAndPeriodAndOrder(
      RECENTLY_READ_URL,
      selectedView,
      selectedPeriod,
      selectedOrder,
      feedsContainer
    );
  else if (
    window.location.href.indexOf(CATEGORY_URL) > -1 &&
    window.location.href.indexOf(CHANNEL_URL) == -1
  ) {
    const categoryPathVariable = window.location.pathname.split("/").pop();
    handleViewAndPeriodAndOrder(
      CATEGORY_URL + categoryPathVariable,
      selectedView,
      selectedPeriod,
      selectedOrder,
      feedsContainer
    );
  } else if (window.location.href.indexOf(CHANNEL_URL) > -1) {
    const categoryPathVariable = window.location.pathname.split("/")[2];
    const channelPathVariable = window.location.pathname.split("/").pop();
    handleViewAndPeriodAndOrder(
      CATEGORY_URL + categoryPathVariable + CHANNEL_URL + channelPathVariable,
      selectedView,
      selectedPeriod,
      selectedOrder,
      feedsContainer
    );
  }
}

function handleViewAndPeriodAndOrder(
  url,
  selectedView,
  selectedPeriod,
  slectedOrder,
  feedsContainer
) {
  if (selectedPeriod == "today")
    handleOrderAndView(
      url + "?period=today",
      slectedOrder,
      selectedView,
      feedsContainer
    );
  else if (selectedPeriod == "last-seven-days")
    handleOrderAndView(
      url + "?period=last-seven-days",
      slectedOrder,
      selectedView,
      feedsContainer
    );
  else if (selectedPeriod == "last-thirty-days")
    handleOrderAndView(
      url + "?period=last-thirty-days",
      slectedOrder,
      selectedView,
      feedsContainer
    );
  else if (selectedPeriod == "all")
    handleOrderAndView(
      url + "?period=all",
      slectedOrder,
      selectedView,
      feedsContainer
    );
}

function handleOrderAndView(url, slectedOrder, selectedView, feedsContainer) {
  if (slectedOrder == "latest")
    handleView(url + "&orderBy=latest", selectedView, feedsContainer);
  else if (slectedOrder == "most-relevant")
    handleView(url + "&orderBy=most-relevant", selectedView, feedsContainer);
  else if (slectedOrder == "oldest")
    handleView(url + "&orderBy=oldest", selectedView, feedsContainer);
  else if (slectedOrder == "unread")
    handleView(url + "&orderBy=unread", selectedView, feedsContainer);
  else if (slectedOrder == "channel")
    handleView(url + "&orderBy=channel", selectedView, feedsContainer);
  else if (slectedOrder == "all-categories")
    handleView(url + "&orderBy=all-categories", selectedView, feedsContainer);
}

function handleView(url, selectedView, feedsContainer) {
  if (selectedView == "cards") getAllFeeds(url + "&view=cards", feedsContainer);
  else if (selectedView == "title-only")
    getAllFeeds(url + "&view=title-only", feedsContainer);
}

function handleSelect(selectedView, selectedPeriod, selectedOrder) {
  pageNumber = 1;

  let listItemsContainer = document.getElementById("list-items-container");
  let cardsContainer = document.getElementById("cards-container");

  if (document.body.contains(listItemsContainer)) listItemsContainer.remove();

  if (document.body.contains(cardsContainer)) cardsContainer.remove();

  if (selectedView == "cards") {
    let mainContainer = document.getElementById("main-container");
    let cardsContainer = document.createElement("div");
    cardsContainer.id = "cards-container";
    mainContainer.appendChild(cardsContainer);
    handleViewAndPerieodAndOrderSelect(
      selectedView,
      selectedPeriod,
      selectedOrder,
      cardsContainer
    );
  } else if (selectedView == "title-only") {
    let mainContainer = document.getElementById("main-container");
    let listItemsContainer = document.createElement("div");
    listItemsContainer.id = "list-items-container";
    mainContainer.appendChild(listItemsContainer);
    handleViewAndPerieodAndOrderSelect(
      selectedView,
      selectedPeriod,
      selectedOrder,
      listItemsContainer
    );
  } else {
    console.log("Feeds container was not found !");
  }
}

// handle view select
viewSelect.addEventListener("change", function (e) {
  let selectedView = e.target.value;
  let selectedPeriod = document.getElementById("period-select").value.trim();
  let selectedOrder = document.getElementById("order-select").value.trim();
  let searchTermValue = document.getElementById("q").value.trim();

  console.log(
    "Selected view= " + selectedView + ", period= " + selectedPeriod,
    " and order= " + selectedOrder
  );

  if (searchTermValue !== "") search();
  else handleSelect(selectedView, selectedPeriod, selectedOrder);
});

// handle period select
periodSelect.addEventListener("change", function (e) {
  let selectedView = document.getElementById("view-select").value.trim();
  let selectedPeriod = e.target.value.trim();
  let selectedOrder = document.getElementById("order-select").value.trim();
  let searchTermValue = document.getElementById("q").value.trim();

  console.log(
    "Selected view= " + selectedView + ", period= " + selectedPeriod,
    " and order= " + selectedOrder
  );
  if (searchTermValue !== "") search();
  else handleSelect(selectedView, selectedPeriod, selectedOrder);
});

// handle order select
orederSelect.addEventListener("change", function (e) {
  let selectedView = document.getElementById("view-select").value.trim();
  let selectedPeriod = document.getElementById("period-select").value.trim();
  let selectedOrder = e.target.value.trim();
  let searchTermValue = document.getElementById("q").value.trim();

  console.log(
    "Selected view =" + selectedView + ", period = " + selectedPeriod,
    " and order = " + selectedOrder
  );
  if (searchTermValue !== "") search();
  else handleSelect(selectedView, selectedPeriod, selectedOrder);
});

/* ===== Load feeds on scroll ====== */

function loadFeeds() {
  let searchTerm = document.getElementById("q").value.trim();
  if (searchTerm !== "") search();
  else loadNormalFeeds();
}

function loadNormalFeeds() {
  let selectedView = document.getElementById("view-select").value;
  let selectedPeriod = document.getElementById("period-select").value;
  let selectedOrder = document.getElementById("order-select").value;

  const listItemsContainer = document.getElementById("list-items-container");
  const cardsContainer = document.getElementById("cards-container");
  if (document.body.contains(cardsContainer)) {
    if (
      window.location.href.indexOf(CATEGORY_URL) > -1 &&
      window.location.href.indexOf(CHANNEL_URL) == -1
    ) {
      let category = window.location.pathname.split("/").pop();
      getFeedsPage(
        FEEDS_PAGE_URL +
          "?currentFeedsUrl=" +
          currentFeedsUrl +
          "&category=" +
          category +
          "&view=" +
          selectedView +
          "&period=" +
          selectedPeriod +
          "&orderBy=" +
          selectedOrder +
          "&pageNumber=" +
          pageNumber,
        cardsContainer
      );
    } else if (window.location.href.indexOf(CHANNEL_URL) > -1) {
      let channelTitle = decodeURI(window.location.pathname.split("/").pop());
      getFeedsPage(
        FEEDS_PAGE_URL +
          "?currentFeedsUrl=" +
          "/channel" +
          "&channelTitle=" +
          channelTitle +
          "&view=" +
          selectedView +
          "&period=" +
          selectedPeriod +
          "&orderBy=" +
          selectedOrder +
          "&pageNumber=" +
          pageNumber,
        cardsContainer
      );
    } else {
      getFeedsPage(
        FEEDS_PAGE_URL +
          "?currentFeedsUrl=" +
          currentFeedsUrl +
          "&view=" +
          selectedView +
          "&period=" +
          selectedPeriod +
          "&orderBy=" +
          selectedOrder +
          "&pageNumber=" +
          pageNumber,
        cardsContainer
      );
    }
  } else if (document.body.contains(listItemsContainer)) {
    if (
      window.location.href.indexOf(CATEGORY_URL) > -1 &&
      window.location.href.indexOf(CHANNEL_URL) == -1
    ) {
      let category = window.location.pathname.split("/").pop();
      getFeedsPage(
        FEEDS_PAGE_URL +
          "?currentFeedsUrl=" +
          currentFeedsUrl +
          "&category=" +
          category +
          "&view=" +
          selectedView +
          "&period=" +
          selectedPeriod +
          "&orderBy=" +
          selectedOrder +
          "&pageNumber=" +
          pageNumber,
        listItemsContainer
      );
    } else if (window.location.href.indexOf(CHANNEL_URL) > -1) {
      let channelTitle = decodeURI(window.location.pathname.split("/").pop());
      getFeedsPage(
        FEEDS_PAGE_URL +
          "?currentFeedsUrl=" +
          "/channel" +
          "&channelTitle=" +
          channelTitle +
          "&view=" +
          selectedView +
          "&period=" +
          selectedPeriod +
          "&orderBy=" +
          selectedOrder +
          "&pageNumber=" +
          pageNumber,
        listItemsContainer
      );
    } else {
      getFeedsPage(
        FEEDS_PAGE_URL +
          "?currentFeedsUrl=" +
          currentFeedsUrl +
          "&view=" +
          selectedView +
          "&period=" +
          selectedPeriod +
          "&orderBy=" +
          selectedOrder +
          "&pageNumber=" +
          pageNumber,
        listItemsContainer
      );
    }
  }

  pageNumber++;
}

// listen for scroll event and load more feeds if we reach the bottom of window
window.addEventListener("scroll", () => {
  if (
    window.scrollY + 0.5 + window.innerHeight >=
    document.documentElement.scrollHeight - 0.4
  ) {
    loadFeeds();
  }
});

/* ===== search ====== */
function search() {
  let selectedView = document.getElementById("view-select").value;
  let selectedPeriod = document.getElementById("period-select").value;
  let selectedOrder = document.getElementById("order-select").value;
  let searchTerm = document.getElementById("q").value.trim();

  const listItemsContainer = document.getElementById("list-items-container");
  const cardsContainer = document.getElementById("cards-container");
  if (document.body.contains(cardsContainer)) {
    if (
      window.location.href.indexOf(CATEGORY_URL) > -1 &&
      window.location.href.indexOf(CHANNEL_URL) == -1
    ) {
      let category = window.location.pathname.split("/").pop();
      getSearchResults(
        SEARCH_URL +
          "?q=" +
          searchTerm +
          "&currentFeedsUrl=" +
          currentFeedsUrl +
          "&category=" +
          category +
          "&view=" +
          selectedView +
          "&period=" +
          selectedPeriod +
          "&orderBy=" +
          selectedOrder +
          "&pageNumber=" +
          pageNumber,
        cardsContainer
      );
    } else if (window.location.href.indexOf(CHANNEL_URL) > -1) {
      let channelTitle = decodeURI(window.location.pathname.split("/").pop());
      getSearchResults(
        SEARCH_URL +
          "?q=" +
          searchTerm +
          "&currentFeedsUrl=" +
          "/channel" +
          "&channelTitle=" +
          channelTitle +
          "&view=" +
          selectedView +
          "&period=" +
          selectedPeriod +
          "&orderBy=" +
          selectedOrder +
          "&pageNumber=" +
          pageNumber,
        cardsContainer
      );
    } else {
      getSearchResults(
        SEARCH_URL +
          "?q=" +
          searchTerm +
          "&currentFeedsUrl=" +
          currentFeedsUrl +
          "&view=" +
          selectedView +
          "&period=" +
          selectedPeriod +
          "&orderBy=" +
          selectedOrder +
          "&pageNumber=" +
          pageNumber,
        cardsContainer
      );
    }
  } else if (document.body.contains(listItemsContainer)) {
    if (
      window.location.href.indexOf(CATEGORY_URL) > -1 &&
      window.location.href.indexOf(CHANNEL_URL) == -1
    ) {
      let category = window.location.pathname.split("/").pop();
      getSearchResults(
        SEARCH_URL +
          "?q=" +
          searchTerm +
          "&currentFeedsUrl=" +
          currentFeedsUrl +
          "&category=" +
          category +
          "&view=" +
          selectedView +
          "&period=" +
          selectedPeriod +
          "&orderBy=" +
          selectedOrder +
          "&pageNumber=" +
          pageNumber,
        listItemsContainer
      );
    } else if (window.location.href.indexOf(CHANNEL_URL) > -1) {
      let channelTitle = decodeURI(window.location.pathname.split("/").pop());
      getSearchResults(
        SEARCH_URL +
          "?q=" +
          searchTerm +
          "&currentFeedsUrl=" +
          "/channel" +
          "&channelTitle=" +
          channelTitle +
          "&view=" +
          selectedView +
          "&period=" +
          selectedPeriod +
          "&orderBy=" +
          selectedOrder +
          "&pageNumber=" +
          pageNumber,
        listItemsContainer
      );
    } else {
      getSearchResults(
        SEARCH_URL +
          "?q=" +
          searchTerm +
          "&currentFeedsUrl=" +
          currentFeedsUrl +
          "&view=" +
          selectedView +
          "&period=" +
          selectedPeriod +
          "&orderBy=" +
          selectedOrder +
          "&pageNumber=" +
          pageNumber,
        listItemsContainer
      );
    }
  }

  pageNumber++;
}

let searchTerm = document.getElementById("q");
searchTerm.addEventListener("input", (e) => {
  pageNumber = 1;
  // document.getElementById("order-select").value = "most-relevant";
  let searchTermValue = document.getElementById("q").value.trim();
  if (searchTermValue === "") {
    pageNumber = 0;
    document.getElementById("order-select").value = "latest";
  }
  loadFeeds();
});

/* ===== sse-notifications ====== */
const eventSource = new EventSource(SSE_NOTIFICATIONS_URL);
eventSource.onmessage = function (e) {
  let notification = JSON.parse(e.data);

  // refresh the feed items list
  if (notification.message === "New Feed") {
    let selectedView = document.getElementById("view-select").value.trim();
    let selectedPeriod = document.getElementById("period-select").value.trim();
    let selectedOrder = document.getElementById("order-select").value.trim();
    handleSelect(selectedView, selectedPeriod, selectedOrder);
  }
};

/* ===== feeds buttons ====== */
initInteractionsButtons();

function initLikeButton() {
  var heartCheckboxes = document.querySelectorAll("[id^=heart-]");
  for (let i = 0; i < heartCheckboxes.length; i++) {
    heartCheckboxes[i].addEventListener("change", (event) => {
      let id = heartCheckboxes[i].id.split("-").pop();

      if (!event.currentTarget.checked === false) {
        let payload = { liked: true, feedId: id };
        postInteraction(LIKE_URL, payload);
      } else {
        let payload = { liked: false, feedId: id };
        postInteraction(LIKE_URL, payload);
      }
    });
  }
}

function initReadLaterButton() {
  var readLaterCheckboxes = document.querySelectorAll("[id^=bookmark-]");
  for (let i = 0; i < readLaterCheckboxes.length; i++) {
    readLaterCheckboxes[i].addEventListener("change", (event) => {
      let id = readLaterCheckboxes[i].id.split("-").pop();

      if (!event.currentTarget.checked === false) {
        let payload = { readLater: true, feedId: id };
        postInteraction(MARK_AS_READ_LATER, payload);
      } else {
        let payload = { readLater: false, feedId: id };
        postInteraction(MARK_AS_READ_LATER, payload);
      }
    });
  }
}

function initReadButton() {
  var readCheckboxes = document.querySelectorAll("[id^=read-]");
  for (let i = 0; i < readCheckboxes.length; i++) {
    readCheckboxes[i].addEventListener("change", (event) => {
      let id = readCheckboxes[i].id.split("-").pop();

      if (!event.currentTarget.checked === false) {
        let payload = { read: true, feedId: id };
        postInteraction(MARK_AS_READ, payload);
        let headerContainer = document.getElementById("header-container");
        refreshHeader(REFRESH_HEADER_URL, headerContainer);
      } else {
        let payload = { read: false, feedId: id };
        postInteraction(MARK_AS_READ, payload);
        let headerContainer = document.getElementById("header-container");
        refreshHeader(REFRESH_HEADER_URL, headerContainer);
      }
    });
  }
}

function initLastReadingDate() {
  var lastReadingDateLinks = document.querySelectorAll("[id^=read-more]");
  for (let i = 0; i < lastReadingDateLinks.length; i++) {
    lastReadingDateLinks[i].addEventListener("click", (event) => {
      let id = lastReadingDateLinks[i].id.split("-").pop();
      postInteraction(LAST_READING_TIME, id);
    });
  }
}

function initInteractionsButtons() {
  initLikeButton();
  initReadLaterButton();
  initReadButton();
  initLastReadingDate();
}
