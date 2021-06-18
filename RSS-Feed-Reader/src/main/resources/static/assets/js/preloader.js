"use strict";

/* ===== disply pre-loader ====== */
function showPreloader() {
  return new Promise((resolve) => {
    const preloader = document.getElementById("loader");
    preloader.style.cssText = "display:block !important";
    // setTimeout(() => {
    //   preloader.style.cssText = "display:none !important";
    // }, 1000);

    setTimeout(resolve, 1000);
  });
}
