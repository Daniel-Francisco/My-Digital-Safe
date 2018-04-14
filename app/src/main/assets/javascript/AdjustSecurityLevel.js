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