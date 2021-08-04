"use strict";

/* ===== disply pre-loader ====== */
function showPreloader() {
  return new Promise((resolve) => {
    const preloader = document.getElementById("loader");
    preloader.classList.remove("d-none");
    setTimeout(resolve, 1000);
  });
}
