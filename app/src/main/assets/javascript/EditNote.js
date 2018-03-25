var initialText;// = Android.fetchContents();
var file;
var editText;// = document.getElementById("noteArea");
var title;
var titleText;


//
// function checkTitle(div){
//     alert("check");
//     if(div.innerText == "My Secure Note"){
//         alert("detected");
//         div.innerText = "";
// 	}
// }

function updateFile() {
    file.data = editText.value;
    file.file_name = title.value;
    var fileString = JSON.stringify(file);
    Android.updateFile(fileString);
}

$(document).ready(function () {
    // {"_id":4,"access_date":"2018-45-17 10:45:54","data":"","file_name":"My Secure Note"}

    var documentHeight = $(document).height();
    var height = $("#editNoteTitle").height();
    document.getElementById('page').style.height = (documentHeight - height) + 'px';

    editText = document.getElementById("noteArea");
    title = document.getElementById("editNoteTitle");

    var fileString = Android.getFile();
    file = JSON.parse(fileString);
    initialText = file.data;


    if (file.file_name == "My Secure Note") {
        titleText = "";
        title.placeholder = "My Secure Note";
    } else {
        titleText = file.file_name;
        title.value = titleText;
    }

    editText.value = initialText;

    $("#editNoteTitle").on("click touchstart", function (e) {
        if (e.type == "click") {
            alert("Mouse");
        }
        else if (e.type == "touchend") {
            alert("Touch");
            e.preventDefault();
            e.stopPropagation();
        }
    });

});