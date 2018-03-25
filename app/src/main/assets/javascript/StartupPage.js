var tipShown = false;
var tips = [
    "Password tip #1: Have you tried including at least one number in your password?",
    "Password tip #2: Have you included a mixture or capitals an lowercase letters in your password?",
    "Password tip #3: Longer passwords are stronger passwords!",
    "Password tip #4: Try to avoid common password, including 'password', 'qwerty' etc.",
    "Password tip #5: It is best to not use obvious words in your passwords, such as names of pets, family and friends."
];
$(document).ready(function () {

    $("#createPassword").on("click", function (event) {
        event.preventDefault();

        document.getElementById("loader").classList.remove("displayNone");
        document.getElementById("createPassword").classList.add("displayNone");

        var passwordOne = $("#passwordOne").val().trim();
        var passwordTwo = $("#passwordTwo").val().trim();
        // var dropDownValue = $('#dropDown').val();

        Android.createPassword(passwordOne, passwordTwo);
    });


    $("#passwordOne")[0].addEventListener("keyup", function (event) {
        event.preventDefault();
        if (event.keyCode === 13) {
            $("#createPassword").click();
            $("#passwordOne").blur();
        }
    });
    $("#passwordOne").submit(function (event) {
        event.preventDefault();
        $("#createPassword").click();
        $("#passwordOne").blur();
    });

    $("#passwordTwo")[0].addEventListener("keyup", function (event) {
        event.preventDefault();
        if (event.keyCode === 13) {
            $("#createPassword").click();
            $("#passwordTwo").blur();
        }
    });
    $("#passwordTwo").submit(function (event) {
        event.preventDefault();
        $("#createPassword").click();
        $("#passwordTwo").blur();
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
});
// <li id="badLength">Your password is too short! It should be a minimum of 6 characters!</li>
// <li id="badUpperCase">Add a upper-case letter to improve your password strength!</li>
// <li id="badLowerCase">Add a lower-case letter to improve your password strength!</li>
// <li id="badNumber">Add a number letter to improve your password strength!</li>
// <li id="badSymbol">Add a symbol letter to improve your password strength!</li>
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
    $("#passwordOne").val("");
    $("#passwordTwo").val("");

    document.getElementById("loader").classList.add("displayNone");
    document.getElementById("createPassword").classList.remove("displayNone");

    document.getElementById("badLength").classList.add("hide");
    document.getElementById("badUpperCase").classList.add("hide");
    document.getElementById("badLowerCase").classList.add("hide");
    document.getElementById("badNumber").classList.add("hide");
    document.getElementById("badSymbol").classList.add("hide");

    document.getElementById("password-strength-meter").value = 0;
}