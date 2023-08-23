"use strict";
(function () {
    var isWindows = navigator.platform.indexOf('Win') > -1 ? true : false

    if (isWindows) {

        if (document.getElementsByClassName('scroller-wrapper')[0]) {
            var sidebar = document.querySelector('.scroller-wrapper')
            var ps2 = new PerfectScrollbar(sidebar)
        };

        if (document.getElementsByClassName('sidenav')[0]) {
            var sidebar = document.querySelector('.sidenav')
            var ps2 = new PerfectScrollbar(sidebar)
        }
    }
})()

function closeLoader(){
    $(".loader").hide()
    $(".main-content").show()
    $(".ps__rail-x").show()
    $(".ps__rail-y").show()
    $(".scroller-wrapper").css("overflow-y", "auto")
}

function openLoader(){
    $(".loader").show()
    $(".main-content").hide()
    $(".ps__rail-x").hide()
    $(".ps__rail-y").hide()
    $(".scroller-wrapper").css("overflow-y", "hidden")
}
