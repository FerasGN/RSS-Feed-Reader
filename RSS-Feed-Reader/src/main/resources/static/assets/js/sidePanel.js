/* ===== Responsive Sidepanel ====== */
var sidePanelToggler = document.getElementById('sidepanel-toggler');
var sidePanel = document.getElementById('app-sidepanel');
var sidePanelDrop = document.getElementById('sidepanel-drop');
var sidePanelClose = document.getElementById('sidepanel-close');

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

/* ===== Search for channel and save channel ====== */
function getSubscribeModal(url, container) {
	var request = new XMLHttpRequest();
	request.open('GET', url, true);

	request.onload = function () {
		if (this.status >= 200 && this.status < 400) {
			// Success!
			var resp = this.response;
			container.innerHTML = request.responseText;

			// if the channel was found
			channelInfo = document.getElementById("channel-info").innerText;
			if (channelInfo != 'URL was not found' && channelInfo != 'Channel already exists')
				addSubscribeButton();

		} else {
			// We reached our target server, but it returned an error

		}
	};

	request.onerror = function () {
		// There was a connection error of some sort
	};

	request.send();
}

function searchChannel() {
	var searchChannelButton = document.getElementById("search-channel-button");
	searchChannel.disabled = true
	searchChannel.innerHTML = '<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span> ' +
		'Searching...';
	var subscribeModal = document.getElementById('subscribe-modal');
	var channelUrl = document.getElementById('channel-url').value;
	getSubscribeModal("/search-channel?url=" + channelUrl, subscribeModal);

	return false;

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
	warpChannelImage.classList.add("warp-channel-image-no-after")

	// bind save channel button to the channel url input
	var channelUrl = document.getElementById('channel-url');
	channelUrl.addEventListener('input', (e) => {
		const saveChannelButton = document.getElementById('save-channel-button');
		if (typeof (saveChannelButton) != 'undefined' && saveChannelButton != null) {
			saveChannelButton.parentNode.removeChild(saveChannelButton);
		}
	});
}



/* ===== reset bootstrap modal on hide ====== */
var subscribeModal = document.getElementById('subscribe-modal')
subscribeModal.addEventListener('hidden.bs.modal', function (event) {
	const channelInfo = document.getElementById("channel-info");
	const channelUrl = document.getElementById("channel-url");
	const searchChannelButton = document.getElementById("search-channel-button");
	const saveChannelButton = document.getElementById("save-channel-button");


	channelInfo.innerText = "";
	channelUrl.value = "";
	searchChannelButton.disabled = false;
	searchChannelButton.innerHTML = "Search";
	if (typeof (saveChannelButton) != 'undefined' && saveChannelButton != null)
		saveChannelButton.parentNode.removeChild(saveChannelButton);

})