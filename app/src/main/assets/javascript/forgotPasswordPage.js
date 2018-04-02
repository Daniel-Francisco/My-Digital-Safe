$(document).ready(function () {
    var securityQuestionJson = Android.fetchQuestions();
    var securityQuestion = JSON.parse(securityQuestionJson);

    console.log("marker");
    console.log(securityQuestionJson);
    console.log(securityQuestion);

    document.getElementById("securityQuestionText").innerText = securityQuestion.question;
    $("#submitResponse").on("click", function (event) {
        var responseValue = document.getElementById("securityQuestionResponse").value;
        document.getElementById("forgotPasswordPage").classList.add("hide");
        document.getElementById("loader").classList.remove("hide");
        Android.checkSecurityQuestion(responseValue);
    });

    $("#securityQuestionResponse")[0].addEventListener("keyup", function (event) {
        event.preventDefault();
        if (event.keyCode === 13) {
            $("#submitResponse").click();
        }
    });

    //resetPassword


    $("#resetPassword").on("click", function (event) {
        var password1 = document.getElementById("passwordOne").value;
        var password2 = document.getElementById("passwordTwo").value;

        document.getElementById("createPasswordPage").classList.add("hide");
        document.getElementById("loader").classList.remove("hide");

        Android.resetPassword(password1, password2);
    });

    $("#passwordOne")[0].addEventListener("keyup", function (event) {
        event.preventDefault();
        if (event.keyCode === 13) {
            $("#resetPassword").click();
        }
    });
    $("#passwordTwo")[0].addEventListener("keyup", function (event) {
        event.preventDefault();
        if (event.keyCode === 13) {
            $("#resetPassword").click();
        }
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
function failedResponse(){
    document.getElementById("securityQuestionResponse").value = "";
    document.getElementById("forgotPasswordPage").classList.remove("hide");
    document.getElementById("loader").classList.add("hide");
}
function successfulResponse(){
    document.getElementById("forgotPasswordPage").classList.add("hide");
    document.getElementById("createPasswordPage").classList.remove("hide");
    document.getElementById("loader").classList.add("hide");
}

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