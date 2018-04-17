/*
 * My Digital Safe, the secure notepad Android app.
 * Copyright (C) 2018 Security First Designs
 *
 * My Digital Safe is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <a href="www.gnu.org/licenses/">here</a>.
 */
var currentLevel = "";
$(document).ready(function () {
    console.log("test");
    $("#updateSecurityLevel").on("click", function (event) {
        event.preventDefault();

        document.getElementById("loader").classList.remove("hide");
        document.getElementById("mainArea").classList.add("hide");


        var currentPassword = $("#currentPassword").val().trim();

        Android.updatePasswordStrength(currentPassword, currentLevel);
    });


});


function highSecurityCheckboxClick() {
    document.getElementById("mediumSecurityCheckbox").checked = false;
    document.getElementById("lowSecurityCheckbox").checked = false;
    currentLevel = "high";
}

function mediumSecurityCheckboxClick() {
    document.getElementById("highSecurityCheckbox").checked = false;
    document.getElementById("lowSecurityCheckbox").checked = false;
    currentLevel = "medium";
}

function lowSecurityCheckboxClick() {
    document.getElementById("mediumSecurityCheckbox").checked = false;
    document.getElementById("highSecurityCheckbox").checked = false;
    currentLevel = "low";
}

function badPassword(){
    document.getElementById("loader").classList.add("hide");
    document.getElementById("mainArea").classList.remove("hide");
}