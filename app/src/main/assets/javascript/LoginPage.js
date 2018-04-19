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
var attemptingLogin = false;
var loginTime = -1;
var percentProgress = 0;
var currentTimer = null;

$(document).ready(function () {

    // $("#passwordStrengthBar").val(80);
    var recommendation = Android.getRecommendation();
    document.getElementById("recommendationArea").innerText = recommendation;

    $("#signIn").on("click", function (event) {
        loginTime = Android.getloginTime();
        event.preventDefault();
        document.getElementById("lockoutSection").classList.add("hide");
        if (!attemptingLogin) {
            var password = $("#password").val().trim();
            if(password != ""){
                attemptingLogin = true;
                if (loginTime != -1) {
                    $("#loginInfoArea").addClass("hideLoginInfoArea");
                    $("#forgotPasswordLink").addClass("hide");
                    $("#loginProgressArea").addClass("showProgressBar");
                    setTimeout(updateProgressBar, (loginTime / 100));
                }
                Android.authenticateUser(password);
            }
        }

    });
    $("#forgotPasswordLink").on("click", function (event) {
        Android.forgotPassword();
    });

    $("#privacyPolicyLink").on("click", function (event) {
        Android.goToPrivacyPolicy();
    });

    $("#password")[0].addEventListener("keyup", function (event) {
        event.preventDefault();
        document.getElementById("password").scrollIntoView();
        if (event.keyCode === 13) {
            $("#signIn").click();
        }
    });
    $("#password").submit(function (event) {
        event.preventDefault();
        $("#signIn").click();
    });

});

function updateProgressBar() {
    var foregedPercentProgress = percentProgress;
    if(foregedPercentProgress > 99){
        foregedPercentProgress = 99;
    }
    $("#loginProgressBar").val(foregedPercentProgress);
    document.getElementById("percentageText").innerText = (foregedPercentProgress.toString() + " %");

    percentProgress++;
    if (percentProgress != 101) {
        currentTimer = setTimeout(updateProgressBar, (loginTime / 100));
    } else {
        currentTimer = null;
    }
}

function failedLogin() {
    attemptingLogin = false;
    if (currentTimer != null) {
        clearTimeout(currentTimer);
    }
    $("#loginProgressBar").val(0);
    $("#loginInfoArea").removeClass("hideLoginInfoArea");
    $("#forgotPasswordLink").removeClass("hide");
    $("#loginProgressArea").removeClass("showProgressBar");
    $("#password").val("");
    percentProgress = 0;
}

function lockedOutLogin(){
    failedLogin();

    var lockoutMessage = Android.getLockoutString();

    var lockoutArea = document.getElementById("lockoutSection");
    lockoutArea.classList.remove("hide");
    lockoutArea.innerText = lockoutMessage;
}

function clearLockout(){
    var lockoutArea = document.getElementById("lockoutSection");
    lockoutArea.classList.add("hide");
    lockoutArea.innerText = "";
}

function userLockedOut(){
    var lockoutMessage = Android.getLockoutString();
    if(lockoutMessage != ""){
        var lockoutArea = document.getElementById("lockoutSection");
        lockoutArea.classList.remove("hide");
        lockoutArea.innerText = lockoutMessage;
    }
}