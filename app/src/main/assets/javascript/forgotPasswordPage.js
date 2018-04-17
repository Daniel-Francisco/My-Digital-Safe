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
$(document).ready(function () {
    var securityQuestionJson = Android.fetchQuestions();
    console.log(securityQuestionJson);
    try{
        var securityQuestions = JSON.parse(securityQuestionJson);

        for(var i = 0; i<securityQuestions.length; i++){
            console.log("here");
            if(i == 0){
                document.getElementById("questionOne").innerText = securityQuestions[i].question;
            }else if(i == 1){
                document.getElementById("questionTwo").innerText = securityQuestions[i].question;
                document.getElementById("questionTwo").classList.remove("hide");
                document.getElementById("responseTwo").classList.remove("hide");
                document.getElementById("questionTwoDivide").classList.remove("hide");
            }else if(i == 2){
                document.getElementById("questionThree").innerText = securityQuestions[i].question;
                document.getElementById("questionThree").classList.remove("hide");
                document.getElementById("responseThree").classList.remove("hide");
                document.getElementById("questionThreeDivide").classList.remove("hide");
            }else if(i == 3){
                document.getElementById("questionFour").innerText = securityQuestions[i].question;
                document.getElementById("questionFour").classList.remove("hide");
                document.getElementById("responseFour").classList.remove("hide");
                document.getElementById("questionFourDivide").classList.remove("hide");
            }else if(i == 4){
                document.getElementById("questionFive").innerText = securityQuestions[i].question;
                document.getElementById("questionFive").classList.remove("hide");
                document.getElementById("responseFive").classList.remove("hide");
                document.getElementById("questionFiveDivide").classList.remove("hide");
            }
        }
    }catch(exception){
        document.getElementById("forgotPasswordPage").classList.add("hide");
        document.getElementById("noQuestionPage").classList.remove("hide");
        console.error(exception);
    }

    var checkForLockout = Android.getLockoutString();
    if(checkForLockout != ""){
        document.getElementById("forgotPasswordPage").classList.add("hide");
        document.getElementById("lockoutSection").classList.remove("hide");
        document.getElementById("lockoutSection").innerText = checkForLockout;
    }

    $("#submitResponse").on("click", function (event) {
        var responseOne = document.getElementById("responseOne").value;
        var responseTwo = document.getElementById("responseTwo").value;
        var responseThree = document.getElementById("responseThree").value;
        var responseFour = document.getElementById("responseFour").value;
        var responseFive = document.getElementById("responseFive").value;

        document.getElementById("forgotPasswordPage").classList.add("hide");
        document.getElementById("loader").classList.remove("hide");
        Android.checkSecurityQuestion(responseOne, responseTwo, responseThree, responseFour, responseFive);
    });

    $("#responseOne")[0].addEventListener("keyup", function (event) {
        event.preventDefault();
        if (event.keyCode === 13) {
            $("#submitResponse").click();
        }
    });
    $("#responseTwo")[0].addEventListener("keyup", function (event) {
        event.preventDefault();
        if (event.keyCode === 13) {
            $("#submitResponse").click();
        }
    });
    $("#responseThree")[0].addEventListener("keyup", function (event) {
        event.preventDefault();
        if (event.keyCode === 13) {
            $("#submitResponse").click();
        }
    });
    $("#responseFour")[0].addEventListener("keyup", function (event) {
        event.preventDefault();
        if (event.keyCode === 13) {
            $("#submitResponse").click();
        }
    });
    $("#responseFive")[0].addEventListener("keyup", function (event) {
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
    document.getElementById("responseOne").value = "";
    document.getElementById("responseTwo").value = "";
    document.getElementById("responseThree").value = "";
    document.getElementById("responseFour").value = "";
    document.getElementById("responseFive").value = "";
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

function clearFields() {
    $("#passwordOne").val("");
    $("#passwordTwo").val("");

    document.getElementById("loader").classList.add("hide");
    document.getElementById("createPasswordPage").classList.remove("hide");

    clearList();

    document.getElementById("password-strength-meter").value = 0;
}

function clearList(){
    document.getElementById("badLength").classList.remove("hide");
    document.getElementById("badUpperCase").classList.remove("hide");
    document.getElementById("badLowerCase").classList.remove("hide");
    document.getElementById("badNumber").classList.remove("hide");
    document.getElementById("badSymbol").classList.remove("hide");
}

