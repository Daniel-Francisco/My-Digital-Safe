$(document).ready(function () {
    $("#submitSecurityQuestions").on("click", function (event) {
        var passwordField = document.getElementById("password");
        var responseField = document.getElementById("securityQuestionAnswerOne");
        var questionField = document.getElementById("securityQuestionOne");

        var question = questionField.options[questionField.selectedIndex].text;
        var password = passwordField.value;
        var response = responseField.value;

        document.getElementById("loader").classList.remove("hide");
        document.getElementById("inputWrapper").classList.add("hide");

        Android.setSecurityQuestion(password, question, response);
    });

    $("#returnToHome").on("click", function (event) {
        Android.goHome();
    });

    $("#goHome").on("click", function (event) {
        Android.goHome();
    });

    $("#replaceQuestions").on("click", function (event) {
        document.getElementById("confirmReplacingSecurityQuestionPage").classList.add("hide");
        document.getElementById("page").classList.remove("hide");
    });

    var questionsExistFlag = Android.checkQuestions();
    if(questionsExistFlag){
        questionsExist();
    }

    $("#securityQuestionAnswerOne")[0].addEventListener("keyup", function (event) {
        event.preventDefault();
        if (event.keyCode === 13) {
            $("#submitSecurityQuestions").click();
        }
    });

    $("#password")[0].addEventListener("keyup", function (event) {
        event.preventDefault();
        if (event.keyCode === 13) {
            $("#submitSecurityQuestions").click();
        }
    });

});

function clearInputs(){
    document.getElementById("loader").classList.add("hide");
    document.getElementById("inputWrapper").classList.remove("hide");

    var passwordField = document.getElementById("password");
    var responseField = document.getElementById("securityQuestionAnswerOne");

    passwordField.value = "";
    responseField.value = "";
}

function wrapUp(){
    document.getElementById("finishedPage").classList.remove("hide");
    document.getElementById("page").classList.add("hide");
}

function questionsExist(){
    console.log("thisCoolThing");
    document.getElementById("confirmReplacingSecurityQuestionPage").classList.remove("hide");
    document.getElementById("page").classList.add("hide");
}
