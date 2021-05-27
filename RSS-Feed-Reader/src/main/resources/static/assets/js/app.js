'use strict';

/* ===== Enable Bootstrap Popover (on element  ====== */

var popoverTriggerList = [].slice.call(document.querySelectorAll('[data-toggle="popover"]'))
var popoverList = popoverTriggerList.map(function (popoverTriggerEl) {
	return new bootstrap.Popover(popoverTriggerEl)
})

/* ==== Enable Bootstrap Alert ====== */
var alertList = document.querySelectorAll('.alert')
alertList.forEach(function (alert) {
	new bootstrap.Alert(alert)
});


/* ===== Responsive Sidepanel ====== */
const sidePanelToggler = document.getElementById('sidepanel-toggler');
const sidePanel = document.getElementById('app-sidepanel');
const sidePanelDrop = document.getElementById('sidepanel-drop');
const sidePanelClose = document.getElementById('sidepanel-close');

window.addEventListener('load', function () {
	responsiveSidePanel();
});

window.addEventListener('resize', function () {
	responsiveSidePanel();
});


function responsiveSidePanel() {
	let w = window.innerWidth;
	if (w >= 1200) {
		// if larger 
		//console.log('larger');
		sidePanel.classList.remove('sidepanel-hidden');
		sidePanel.classList.add('sidepanel-visible');

	} else {
		// if smaller
		//console.log('smaller');
		sidePanel.classList.remove('sidepanel-visible');
		sidePanel.classList.add('sidepanel-hidden');
	}
};

sidePanelToggler.addEventListener('click', () => {
	if (sidePanel.classList.contains('sidepanel-visible')) {
		console.log('visible');
		sidePanel.classList.remove('sidepanel-visible');
		sidePanel.classList.add('sidepanel-hidden');

	} else {
		console.log('hidden');
		sidePanel.classList.remove('sidepanel-hidden');
		sidePanel.classList.add('sidepanel-visible');
	}
});



sidePanelClose.addEventListener('click', (e) => {
	e.preventDefault();
	sidePanelToggler.click();
});

sidePanelDrop.addEventListener('click', (e) => {
	sidePanelToggler.click();
});


/* ===== bootstrap tooltips ====== */
var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'))
var tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
	return new bootstrap.Tooltip(tooltipTriggerEl)
})


/* ===== Ajax ====== */
function get(url, container) {
	var request = new XMLHttpRequest();
	request.open('GET', url, true);

	request.onload = function () {
		if (this.status >= 200 && this.status < 400) {
			// Success!
			var resp = this.response;
			container.innerHTML = request.responseText
		} else {
			// We reached our target server, but it returned an error

		}
	};

	request.onerror = function () {
		// There was a connection error of some sort
	};

	request.send();
}

/* ===== Select order and period ====== */
const viewSelect = document.getElementById('view-select');
const orederSelect = document.getElementById('order-select');
const periodSelect = document.getElementById('period-select');

function handleViewAndPerieodAndOrderSelect(selectedView, selectedPeriod, selectedOrder, feedsContainer) {
	if (window.location.href.indexOf('/all-feeds') > -1)
		handleViewAndPeriodAndOrder('/all-feeds', selectedView, selectedPeriod, selectedOrder, feedsContainer);
	else if (window.location.href.indexOf('/read-later') > -1)
		handleViewAndPeriodAndOrder('/read-later', selectedView, selectedPeriod, selectedOrder, feedsContainer);
	else if (window.location.href.indexOf('/liked-feeds') > -1)
		handleViewAndPeriodAndOrder('/liked-feeds', selectedView, selectedPeriod, selectedOrder, feedsContainer);
	else if (window.location.href.indexOf('/favorite-channels') > -1)
		handleViewAndPeriodAndOrder('/favorite-channels', selectedView, selectedPeriod, selectedOrder, feedsContainer);
	else if (window.location.href.indexOf('/recently-read') > -1)
		handleViewAndPeriodAndOrder('/recently-read', selectedView, selectedPeriod, selectedOrder, feedsContainer);
	else if (window.location.href.indexOf('/category/') > -1) {
		const categoryPathVariable = window.location.pathname.split("/").pop();
		handleViewAndPeriodAndOrder('/category/' + categoryPathVariable, selectedView, selectedPeriod, selectedOrder, feedsContainer);
	} else if (window.location.href.indexOf('/channel/') > -1) {
		const categoryPathVariable = window.location.pathname.split("/").pop();
		handleViewAndPeriodAndOrder('/channel/' + categoryPathVariable, selectedView, selectedPeriod, selectedOrderm, feedsContainer);
	}
}

function handleViewAndPeriodAndOrder(url, selectedView, selectedPeriod, slectedOrder, feedsContainer) {
	if (selectedPeriod == 'today')
		handleOrderAndView(url + '?period=today', slectedOrder, selectedView, feedsContainer);
	else if (selectedPeriod == 'this-week')
		handleOrderAndView(url + '?period=this-week', slectedOrder, selectedView, feedsContainer);
	else if (selectedPeriod == 'this-month')
		handleOrderAndView(url + '?period=this-month', slectedOrder, selectedView, feedsContainer);
	else if (selectedPeriod == 'all')
		handleOrderAndView(url + '?period=all', slectedOrder, selectedView, feedsContainer);
}



function handleOrderAndView(url, slectedOrder, selectedView, feedsContainer) {
	if (slectedOrder == 'latest')
		handleView(url + '&orderBy=latest', selectedView, feedsContainer);
	else if (slectedOrder == 'most-relevant')
		handleView(url + '&orderBy=most-relevant', selectedView, feedsContainer);
	else if (slectedOrder == 'oldest')
		handleView(url + '&orderBy=oldest', selectedView, feedsContainer);
	else if (slectedOrder == 'unread')
		handleView(url + '&orderBy=unread', selectedView, feedsContainer);
	else if (slectedOrder == 'channel')
		handleView(url + '&orderBy=channel', selectedView, feedsContainer);
	else if (slectedOrder == 'category')
		handleView(url + '&orderBy=category', selectedView, feedsContainer);
}

function handleView(url, selectedView, feedsContainer) {
	if (selectedView == 'view-cards')
		get(url + '&view=cards', feedsContainer);
	else if (selectedView == 'view-title-only')
		get(url + '&view=title-only', feedsContainer);
}

async function handleSelect(selectedView, selectedPeriod, selectedOrder) {
	// delete list contianer, if it exists
	const listItemsContainer = document.getElementById('list-items-container');
	const cardsContainer = document.getElementById('cards-container');


	if (document.body.contains(listItemsContainer))
		listItemsContainer.remove();

	if (document.body.contains(cardsContainer))
		cardsContainer.remove();

	await showPreloader();

	if (selectedView == 'view-cards') {

		const mainContainer = document.getElementById('main-container');
		const cardsContainer = document.createElement("div");
		cardsContainer.id = 'cards-container';
		mainContainer.appendChild(cardsContainer);
		handleViewAndPerieodAndOrderSelect(selectedView, selectedPeriod, selectedOrder, cardsContainer);

	} else if (selectedView == 'view-title-only') {

		const mainContainer = document.getElementById('main-container');
		const listItemsContainer = document.createElement("div");
		listItemsContainer.id = 'list-items-container';
		mainContainer.appendChild(listItemsContainer);
		handleViewAndPerieodAndOrderSelect(selectedView, selectedPeriod, selectedOrder, listItemsContainer);

	} else {
		console.log('Feeds container was not found !')
	}
}

// handle view select
viewSelect.addEventListener('change', function (e) {
	var selectedView = e.target.value;
	var selectedPeriod = document.getElementById('period-select').value;
	var selectedOrder = document.getElementById('order-select').value;

	console.log('Selected view =' + selectedView + ', period = ' + selectedPeriod, ' and order = ' + selectedOrder);
	handleSelect(selectedView, selectedPeriod, selectedOrder);

});


// handle period select
periodSelect.addEventListener('change', function (e) {
	var selectedView = document.getElementById('view-select').value;
	var selectedPeriod = e.target.value;
	var selectedOrder = document.getElementById('order-select').value;

	console.log('Selected view =' + selectedView + ', period = ' + selectedPeriod, ' and order = ' + selectedOrder);
	handleSelect(selectedView, selectedPeriod, selectedOrder);

});


// handle order select
orederSelect.addEventListener('change', function (e) {
	var selectedView = document.getElementById('view-select').value;
	var selectedPeriod = document.getElementById('period-select').value;
	var selectedOrder = e.target.value;

	console.log('Selected view =' + selectedView + ', period = ' + selectedPeriod, ' and order = ' + selectedOrder);
	handleSelect(selectedView, selectedPeriod, selectedOrder);
});


// disply pre-loader
function showPreloader() {
	return new Promise((resolve) => {
		
		const preloader = document.getElementById('loader');
		preloader.style.cssText = 'display:block !important';
		setTimeout(() => {
			preloader.style.cssText = 'display:none !important';
		}, 1000);

		setTimeout(resolve, 1000);
	});
}