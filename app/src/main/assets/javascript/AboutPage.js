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