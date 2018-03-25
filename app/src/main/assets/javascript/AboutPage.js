$(document).ready(function () {
    // var stringThing = Android.getLogs();
    //
    // $("#location").innerText = stringThing;

    var questions = document.getElementsByClassName("faqQuestion");


    document.querySelectorAll('.faqQuestion').forEach(function (question) {
        // Now do something with my button
        question.addEventListener("click", function () {
            var wrapper = question.parentElement;
            var answer = wrapper.getElementsByClassName("faqAnswer")[0];
            var arrowImage = wrapper.getElementsByClassName("arrow")[0];

            if (answer.classList.contains("showAnswer")) {
                answer.classList.remove("showAnswer");
                arrowImage.src = "images/arrow_down.png";
            } else {
                answer.classList.add("showAnswer");
                arrowImage.src = "images/arrow_up.png";
            }
        });
    });


});