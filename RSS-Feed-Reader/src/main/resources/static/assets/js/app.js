"use strict";

/* ===== Enable Bootstrap Popover (on element  ====== */

var popoverTriggerList = [].slice.call(
  document.querySelectorAll('[data-toggle="popover"]')
);
var popoverList = popoverTriggerList.map(function (popoverTriggerEl) {
  return new bootstrap.Popover(popoverTriggerEl);
});

/* ==== Enable Bootstrap Alert ====== */
var alertList = document.querySelectorAll(".alert");
alertList.forEach(function (alert) {
  new bootstrap.Alert(alert);
});

/* ===== bootstrap tooltips ====== */
var tooltipTriggerList = [].slice.call(
  document.querySelectorAll('[data-bs-toggle="tooltip"]')
);
var tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
  return new bootstrap.Tooltip(tooltipTriggerEl);
});

/* ===== checkbox animation ====== */
// var checkboxes = document.querySelectorAll("input[type=checkbox]");
// for (let i = 0; i < checkboxes.length; i++) {
//   checkboxes[i].addEventListener("change", (event) => {
//     if (!event.currentTarget.checked === false) {
//       checkboxes[i].classList.add("animate-icon");
//       checkboxes[i].labels[0].classList.add("animate-icon");
//     } else {
//       checkboxes[i].classList.remove("animate-icon");
//       checkboxes[i].labels[0].classList.remove("animate-icon");
//     }
//   });
// }
