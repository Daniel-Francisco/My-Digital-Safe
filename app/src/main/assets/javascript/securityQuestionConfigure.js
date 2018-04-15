$(document).ready(function () {
    var numberOfSecurityQuestions = 1;

    $("#newSecurityQuestion").on("click", function (event) {
       numberOfSecurityQuestions++;
       if(numberOfSecurityQuestions == 2){
           document.getElementById("securityQuestionWrapperTwo").classList.remove("hide");
           document.getElementById("removeSecurityQuestion").classList.remove("hide");
       }else if(numberOfSecurityQuestions == 3){
           document.getElementById("securityQuestionWrapperThree").classList.remove("hide");
       }else if(numberOfSecurityQuestions == 4){
           document.getElementById("securityQuestionWrapperFour").classList.remove("hide");
       }else if(numberOfSecurityQuestions == 5){
           document.getElementById("newSecurityQuestion").classList.add("hide");
           document.getElementById("securityQuestionWrapperFive").classList.remove("hide");
       }
    });

    $("#removeSecurityQuestion").on("click", function (event) {
        numberOfSecurityQuestions--;
        if(numberOfSecurityQuestions == 1){
            document.getElementById("securityQuestionWrapperTwo").classList.add("hide");
            document.getElementById("removeSecurityQuestion").classList.add("hide");
        }else if(numberOfSecurityQuestions == 2){
            document.getElementById("securityQuestionWrapperThree").classList.add("hide");
        }else if(numberOfSecurityQuestions == 3){
            document.getElementById("securityQuestionWrapperFour").classList.add("hide");
        }else if(numberOfSecurityQuestions == 4){
            document.getElementById("securityQuestionWrapperFive").classList.add("hide");
            document.getElementById("newSecurityQuestion").classList.remove("hide");
        }
    });

    $("#submitSecurityQuestions").on("click", function (event) {
        var passwordField = document.getElementById("password");

        var password = passwordField.value;



        // var passwordField = document.getElementById("password");
        // var responseField = document.getElementById("securityQuestionAnswerOne");
        // var questionField = document.getElementById("securityQuestionOne");
        //
        // var question = questionField.options[questionField.selectedIndex].text;
        // var password = passwordField.value;
        // var response = responseField.value;


        var responseFieldOne = document.getElementById("securityQuestionAnswerOne");
        var questionFieldOne = document.getElementById("securityQuestionOne");
        var questionOne = questionFieldOne.options[questionFieldOne.selectedIndex].text;
        var responseOne = responseFieldOne.value;

        var responseFieldTwo = document.getElementById("securityQuestionAnswerTwo");
        var questionFieldTwo = document.getElementById("securityQuestionTwo");
        var questionTwo = questionFieldTwo.options[questionFieldTwo.selectedIndex].text;
        var responseTwo = responseFieldTwo.value;

        var responseFieldThree = document.getElementById("securityQuestionAnswerThree");
        var questionFieldThree = document.getElementById("securityQuestionThree");
        var questionThree = questionFieldThree.options[questionFieldThree.selectedIndex].text;
        var responseThree = responseFieldThree.value;

        var responseFieldFour = document.getElementById("securityQuestionAnswerFour");
        var questionFieldFour = document.getElementById("securityQuestionFour");
        var questionFour = questionFieldFour.options[questionFieldFour.selectedIndex].text;
        var responseFour = responseFieldFour.value;

        var responseFieldFive = document.getElementById("securityQuestionAnswerFive");
        var questionFieldFive = document.getElementById("securityQuestionFive");
        var questionFive = questionFieldFive.options[questionFieldFive.selectedIndex].text;
        var responseFive = responseFieldFive.value;

        document.getElementById("loader").classList.remove("hide");
        document.getElementById("inputWrapper").classList.add("hide");

        Android.setSecurityQuestion(password, numberOfSecurityQuestions, questionOne, responseOne, questionTwo, responseTwo, questionThree, responseThree, questionFour, responseFour, questionFive, responseFive);
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
        Android.keyPressOccured();
    });

    $("#securityQuestionAnswerTwo")[0].addEventListener("keyup", function (event) {
        event.preventDefault();
        if (event.keyCode === 13) {
            $("#submitSecurityQuestions").click();
        }
        Android.keyPressOccured();
    });

    $("#securityQuestionAnswerThree")[0].addEventListener("keyup", function (event) {
        event.preventDefault();
        if (event.keyCode === 13) {
            $("#submitSecurityQuestions").click();
        }
        Android.keyPressOccured();
    });

    $("#securityQuestionAnswerFour")[0].addEventListener("keyup", function (event) {
        event.preventDefault();
        if (event.keyCode === 13) {
            $("#submitSecurityQuestions").click();
        }
        Android.keyPressOccured();
    });

    $("#securityQuestionAnswerFive")[0].addEventListener("keyup", function (event) {
        event.preventDefault();
        if (event.keyCode === 13) {
            $("#submitSecurityQuestions").click();
        }
        Android.keyPressOccured();
    });

    $("#password")[0].addEventListener("keyup", function (event) {
        event.preventDefault();
        if (event.keyCode === 13) {
            $("#submitSecurityQuestions").click();
        }
        Android.keyPressOccured();
    });

});

function clearInputs(){
    document.getElementById("loader").classList.add("hide");
    document.getElementById("inputWrapper").classList.remove("hide");

    var passwordField = document.getElementById("password");

    passwordField.value = "";
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
