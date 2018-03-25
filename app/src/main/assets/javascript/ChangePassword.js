$(document).ready(function () {

    $("#changePassword").on("click", function (event) {
        event.preventDefault();

        document.getElementById("loader").classList.remove("displayNone");
        document.getElementById("changePassword").classList.add("displayNone");


        var currentPassword = $("#currentPassword").val().trim();
        var passwordOne = $("#passwordOne").val().trim();
        var passwordTwo = $("#passwordTwo").val().trim();

        Android.changePassword(currentPassword, passwordOne, passwordTwo);
    });


    $('#passwordOne').on('input', function () {
        var passwordOne = $("#passwordOne").val().trim();

        clearList();

        var strength = Android.checkPasswordStrength(passwordOne);

        if(strength < 1){
            strength = 1;
        }

        document.getElementById("password-strength-meter").value = strength;


        if(strength < 8){
            //display how to improve
            document.getElementById("password-strength-text").classList.remove("hide");
        }else{
            document.getElementById("password-strength-text").classList.add("hide");
        }
    });

    $("#currentPassword")[0].addEventListener("keyup", function (event) {
        event.preventDefault();
        if (event.keyCode === 13) {
            $("#changePassword").click();
        }
    });
    $("#passwordOne")[0].addEventListener("keyup", function (event) {
        event.preventDefault();
        if (event.keyCode === 13) {
            $("#changePassword").click();
        }
    });
    $("#passwordTwo")[0].addEventListener("keyup", function (event) {
        event.preventDefault();
        if (event.keyCode === 13) {
            $("#changePassword").click();
        }
    });


});

function clearLength(){
    document.getElementById("badLength").classList.add("hide");
}
function clearUpperCase(){
    document.getElementById("badUpperCase").classList.add("hide");
}
function clearLowerCase(){
    document.getElementById("badLowerCase").classList.add("hide");
}
function clearBadNumber(){
    document.getElementById("badNumber").classList.add("hide");
}
function clearBadSymbol(){
    document.getElementById("badSymbol").classList.add("hide");
}

function clearList(){
    document.getElementById("badLength").classList.remove("hide");
    document.getElementById("badUpperCase").classList.remove("hide");
    document.getElementById("badLowerCase").classList.remove("hide");
    document.getElementById("badNumber").classList.remove("hide");
    document.getElementById("badSymbol").classList.remove("hide");
}

function clearFields() {
    $("#currentPassword").val("");
    $("#passwordOne").val("");
    $("#passwordTwo").val("");

    document.getElementById("loader").classList.add("displayNone");
    document.getElementById("changePassword").classList.remove("displayNone");

    document.getElementById("badLength").classList.add("hide");
    document.getElementById("badUpperCase").classList.add("hide");
    document.getElementById("badLowerCase").classList.add("hide");
    document.getElementById("badNumber").classList.add("hide");
    document.getElementById("badSymbol").classList.add("hide");

    document.getElementById("password-strength-meter").value = 0;
}