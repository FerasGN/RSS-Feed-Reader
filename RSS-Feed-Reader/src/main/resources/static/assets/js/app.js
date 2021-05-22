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
const orederSelect = document.getElementById('order-select');
const periodSelect = document.getElementById('period-select');
const cardsContainer = document.getElementById('cards-container');

function handlePerieodAndOrderSelect(selectedPeriod, selectedOrder) {
	if (window.location.href.indexOf('/all-feeds') > -1)
		handlePeriodAndOrder('/all-feeds', selectedPeriod, selectedOrder);
	else if (window.location.href.indexOf('/read-later') > -1)
		handlePeriodAndOrder('/read-later', selectedPeriod, selectedOrder);
	else if (window.location.href.indexOf('/liked-feeds') > -1)
		handlePeriodAndOrder('/liked-feeds', selectedPeriod, selectedOrder);
	else if (window.location.href.indexOf('/favorite-channels') > -1)
		handlePeriodAndOrder('/favorite-channels', selectedPeriod, selectedOrder);
	else if (window.location.href.indexOf('/recently-read') > -1)
		handlePeriodAndOrder('/recently-read', selectedPeriod, selectedOrder);
	else if (window.location.href.indexOf('/category/') > -1) {
		const categoryPathVariable = window.location.pathname.split("/").pop();
		handlePeriodAndOrder('/category/' + categoryPathVariable, selectedPeriod, selectedOrder);
	} else if (window.location.href.indexOf('/channel/') > -1) {
		const categoryPathVariable = window.location.pathname.split("/").pop();
		handlePeriodAndOrder('/channel/' + categoryPathVariable, selectedPeriod, selectedOrder);
	}
}

function handlePeriodAndOrder(url, selectedPeriod, slectedOrder) {
	if (selectedPeriod == 'today')
		handleOrder(url + '?period=today', slectedOrder);
	else if (selectedPeriod == 'this-week')
		handleOrder(url + '?period=this-week', slectedOrder);
	else if (selectedPeriod == 'this-month')
		handleOrder(url + '?period=this-month', slectedOrder);
	else if (selectedPeriod == 'all')
		handleOrder(url + '?period=all', slectedOrder);
}

function handleOrder(url, slectedOrder) {
	if (slectedOrder == 'latest')
		get(url + '&orderBy=latest', cardsContainer);
	else if (slectedOrder == 'most-relevant')
		get(url + '&orderBy=most-relevant', cardsContainer);
	else if (slectedOrder == 'oldest')
		get(url + '&orderBy=oldest', cardsContainer);
	else if (slectedOrder == 'unread')
		get(url + '&orderBy=unread', cardsContainer);
	else if (slectedOrder == 'channel')
		get(url + '&orderBy=channel', cardsContainer);
	else if (slectedOrder == 'category')
		get(url + '&orderBy=category', cardsContainer);
}

periodSelect.addEventListener('change', function (e) {
	var selectedPeriod = e.target.value;
	var selectedOrder = document.getElementById('order-select').value;
	console.log('Selected period = ' + selectedPeriod, 'and order =  ' + selectedOrder);
	handlePerieodAndOrderSelect(selectedPeriod, selectedOrder);
});

orederSelect.addEventListener('change', function (e) {
	var selectedPeriod = document.getElementById('period-select').value;
	var selectedOrder = e.target.value;
	console.log('Selected period = ' + selectedPeriod, 'and order = ' + selectedOrder);
	handlePerieodAndOrderSelect(selectedPeriod, selectedOrder);
});



