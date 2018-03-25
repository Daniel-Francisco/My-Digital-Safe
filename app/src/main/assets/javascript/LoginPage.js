var attemptingLogin = false;
var loginTime = -1;
var percentProgress = 0;
var currentTimer = null;

$(document).ready(function () {

    // $("#passwordStrengthBar").val(80);


    $("#signIn").on("click", function (event) {
        loginTime = Android.getloginTime();
        event.preventDefault();
        if (!attemptingLogin) {
            var password = $("#password").val().trim();
            attemptingLogin = true;
            if (loginTime != -1) {
                $("#loginInfoArea").addClass("hideLoginInfoArea");
                $("#loginProgressArea").addClass("showProgressBar");
                setTimeout(updateProgressBar, (loginTime / 100));
            }
            Android.authenticateUser(password);
        }

        function danielTestPleaseDontJudge() {
        }
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
    $("#loginProgressArea").removeClass("showProgressBar");
    $("#password").val("");
    percentProgress = 0;
}