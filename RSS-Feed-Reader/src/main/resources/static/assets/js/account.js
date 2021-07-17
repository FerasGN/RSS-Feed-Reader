"use strict";

let SAVE_PROFILE_URL = "/save-profile";
let CHANGE_PASSWORD_URL = "/change-password";

/* =====  save profile AJAX====== */
function postProfile(url, data) {
  let request = new XMLHttpRequest();
  request.open("POST", url, true);
  request.setRequestHeader("X-CSRF-TOKEN", csrfToken);
  request.setRequestHeader("Content-Type", "application/json");
  request.onload = function () {
    if (this.status >= 200 && this.status < 400) {
      //Success
      console.log("Profile was updated!");
    } else {
      // We reached our target server, but it returned an error
    }
  };
  request.send(JSON.stringify(data));
}

function enableInputFields() {
  let profileInputFiels = document.querySelectorAll(["[id^=profile-]"]);
  profileInputFiels.forEach((el) => (el.disabled = false));
  saveProfile();
}

function enablePasswordField() {
  let passwordInputField = document.querySelector(["[id=password]"]);
  passwordInputField.disabled = false;
  passwordInputField.value = "";
  savePassword();
}

function saveProfile() {
  let manageProfileBtn = document.querySelector(["[id=manage-profile]"]);
  manageProfileBtn.removeAttribute("onclick");
  manageProfileBtn.innerHTML = "Save Changes";
  manageProfileBtn.addEventListener("click", () => {
    let profileInputFields = document.querySelectorAll(["[id^=profile-]"]);
    profileInputFields.forEach((el) => (el.disabled = true));
    manageProfileBtn.setAttribute("onclick", "enableInputFields();");
    manageProfileBtn.innerHTML = "Manage Profile";

    let firstName = document.querySelector(["[id=profile-firstname]"]).value;
    let lastName = document.querySelector(["[id=profile-lastname]"]).value;
    let email = document.querySelector(["[id=profile-email]"]).value;
    let country = document.querySelector(["[id=profile-country]"]).value;
    let userProfileUpdateCommand = {
      firstName: firstName,
      lastName: lastName,
      email: email,
      country: country,
    };

    postProfile(SAVE_PROFILE_URL, userProfileUpdateCommand);
  });
}

function savePassword() {
  let changePasswordBtn = document.querySelector(["[id=change-password]"]);
  changePasswordBtn.removeAttribute("onclick");
  changePasswordBtn.innerHTML = "Save Changes";
  changePasswordBtn.addEventListener("click", () => {
    let passwordInputField = document.querySelector(["[id=password]"]);
    passwordInputField.disabled = true;
    changePasswordBtn.setAttribute("onclick", "enablePasswordField();");
    changePasswordBtn.innerHTML = "Change";
    let password = document.querySelector(["[id=password]"]).value;
    let userProfileUpdateCommand = {
      password: password,
    };
    passwordInputField.value = "••••••••";

    postProfile(CHANGE_PASSWORD_URL, userProfileUpdateCommand);
  });
}
