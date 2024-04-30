/**
 * Copyright 2015-2018 NuoCanvas.
 *
 *
 * Created by Pulkit on 17MAR2018
 *
 * Content of this file is proprietary and confidential.
 * It shall not be reused or disclosed without prior consent
 * of distributor
 **/
"use strict";
const COMMUNICATION_TYPE_ERROR = -1;
const COMMUNICATION_TYPE_RELATIONSHIPS = 2;
const COMMUNICATION_TYPE_DEFAULT = 3;
const COMMUNICATION_TYPE_EXECUTION_STARTED = 5;
const COMMUNICATION_TYPE_RESULT_AVAILABLE = 7;
const COMMUNICATION_TYPE_EXECUTION_RUNNING = 11;
const COMMUNICATION_TYPE_EXECUTION_FAILED = 13;
const COMMUNICATION_TYPE_MAPPING = 17;
const systemFileLabel = "System File";
var activeCustomerFiles = [];
var nuoTables = [];

var activeProgressMonitors = [];
var scatter3dPlotCount = 0;

var historyAnalyses = {};

var tablePreviews = {};

var currRelAnalysisId = "";
var currRelMesssageFieldCount = 0;
var currNuoRelationshipInput = {};
var currNuoCommonFields = [];
var isHistoryLoading = false;
var isHistorySaving = false;
var shiftKeyDown = false;
var ctrlKeyDown = false;
var lastClickedColIndex = -1;
var lastClickedRowIndex = -1;
var audioContext;
var audioBuffer;
var activeNarrativeId;

function initUI() {

  // google.charts.load('current', {
  // 'packages':['geochart'],
  // 'mapsApiKey': ''
  // });
  // google.charts.setOnLoadCallback(function(){
  // refreshNuoTables(loadRestOfUI);
  // loadRestOfUI();
  // });
  $("#ajaxLoader").addClass("is-active");
  refreshNuoTables(loadRestOfUI);

  // loadRestOfUI();
}

// function loadChartLib(callback){

////Load the Visualization API and the corechart package.
// google.charts.load("current", {"packages":["corechart","gantt","sankey","calendar","line","bar","map","scatter"]});

////Set a callback to run when the Google Visualization API is loaded.
// google.charts.setOnLoadCallback(callback);
// }

function loadRestOfUI() {

  if (!window.AudioContext) {
    if (!window.webkitAudioContext) {
      console.log("Your browser does not support any AudioContext and cannot play back this audio.");
      return;
    }
    window.AudioContext = window.webkitAudioContext;
  }

  $("#moveToTopButton").on("click", function (e) {
    $("html, body").animate({ scrollTop: 0 }, "slow");
  });

  //Files Handler START
  $("#evaFilesButton").on("click", evaFilesButtonHandler);
  $("#fileInput").on("change", uploadFileSeletionHandler);
  $("#customerFileDownloadButton").on("click", downloadFileSeletionHandler);
  $("#customerFileDeleteButton").on("click", deleteFileSelectionHandler);
  $("#customerFileNewFolderButton").on("click", newFolderButtonHandler);
  $("#customerFileLoadTableButton").on(
    "click",
    loadIntoTableFileSelectionHandler
  );
  $("#customerFileAnalyzeImageButton").on(
    "click",
    analyzeImageFileSelectionHandler
  );
  //Files Handler END

  //Storage Explorer Handler START
  $("#evaTablesButton").on("click", evaTablesButtonHandler);
  $("#storageGridDeleteButton").on("click", handleStoreGridDeleteButtonEvent);
  $("#storageGridDuplicateButton").on("click", duplicateTableSelectionHandler);
  //Storage Explorer Handler END

  //Progress Monitor Handler START
  $("#evaProgressButton").on("click", evaProgressButtonHandler);
  addProgressMonitorEventHandlers();
  //Progress Monitor Handler END

  // addProfileMenu();

  $("#activeUsername").text("Welcome " + activeUsername + "!");

  if (!profileImageUrl || profileImageUrl.trim().length == 0) {
    profileImageUrl = "./public/userpic.svg";
  }
  var imageTag = "<label for='fileInputImage' class='fileInputImage'>";
  imageTag +=
    "<img id='profileImage' class='profileImage' width='40px' height='40px' src='" +
    profileImageUrl +
    "'/>";
  imageTag += "</label>";
  $("#profileMenuUserImageDiv img.profileMenuUserImage").replaceWith(imageTag);

  // var editIconUrl = "./public/edit_image_24x24.png";
  // var editIconTag = "<div class='editImage'><i class='fa fa-pencil fa-lg'></i></div>"
  // $("#profileImage").append(editIconTag);

  $("#fileInputImage").on("change", profileImageSelectionHandler);

  $("#profileUserName").text(activeUsername);
  $("#profileMenuUserName").text(activeUsername);

  // initMapperWindow();

  readFromHistory();
  addNewAnalysisHandler();

  // addHistoryScrollEvent();
  // $(document).ready(function () {
  // refreshMapperWindow();
  // })
  addUserInputEventHandlers();
  addGlobalDropdownHandler();

  $("html, body").animate({ scrollTop: 0 }, "fast");
}

// function for resizing chat blocks
function mresizeChart() {
  // $(".historyAnalysisChartContainerIn").on("mresize", function() {
  //   var data = JSON.parse($(this).attr("data-info")),
  //     layout = JSON.parse($(this).attr("data-info2"));
  //   Plotly.newPlot($(this)[0], data, layout);
  // });

  $(".historyAnalysisChartContainerIn")
    .draggable({
      cancel: ".historyAnalysisChart",
      start: (e, ui) => {
        $('.indexMax').removeClass('indexMax');
        $(e.target).addClass('indexMax');
      }
    })
    .resizable({
      // helper: "ui-resizable-helper",
      stop: (e, ui) => {
        // console.log(e, ui, ($(e.target).find('.historyAnalysisChart')));
        var elem = $(e.target).find('.historyAnalysisChart');
        var data = JSON.parse(elem.attr("data-info")),
          layout = JSON.parse(elem.attr("data-info2"));
        Plotly.newPlot(elem[0], data, layout, { responsive: true });
      }
    });
}

function addGlobalDropdownHandler() {
  window.onclick = function (e) {
    if (!e.target.matches(".dropbtn")) {
      var dropdowns = document.getElementsByClassName("dropdown-content");
      var i;
      for (i = 0; i < dropdowns.length; i++) {
        var openDropdown = dropdowns[i];
        if (openDropdown.classList.contains("show")) {
          openDropdown.classList.remove("show");
        }
      }
    }
  };
}

function addUserInputEventHandlers() {
  // $('#userInputWindow').fadeIn();
  $(".closeUserInputWindow").off("click");
  $(".closeUserInputWindow").on("click", function (e) {
    e.preventDefault();
    $(".userInputWindow").fadeOut();
  });
}
function addNewAnalysisHandler() {
  $("#newAnalysisButton").on("click", function (e) {
    e.preventDefault();
    var currentTimestamp = Date.now() + 0;
    var historyAnalysis = {
      AnalysisId: "" + currentTimestamp,
      CreatedAt: currentTimestamp,
      LastModifiedAt: currentTimestamp,
      Author: activeUsername
    };
    addRowToHistoryFeed(historyAnalysis);
  });
}

function addAnalysisEventHandlers(analysisId) {
  $("#historyAnalysisResetButton" + analysisId).hide();
  addAnalysisSearchHandler();
  addAnalysisSaveHandler();
  addAnalysisResultTableHandler();
  addAnalysisNarrativeHandler();
  addAnalysisAutoCompleteHandler(analysisId);
  addAnalysisResetHandler();
  addAnalysisMinimizeHandler();
  addAnalysisDeleteHandler();
  addDashboardDataHandler();
}

function addAnalysisResetHandler() {
  $(".historyAnalysisResetButton").off("click");
  $(".historyAnalysisResetButton").on("click", function (e) {
    e.preventDefault();
    var analysisId = e.target.id.substring(
      e.target.className.split(" ")[0].length
    );
    var historyAnalysis = historyAnalyses[analysisId];
    if (historyAnalysis) {
      historyAnalysis.ChartFilters = [];
      drawDashboard(historyAnalysis);
      $("#historyAnalysisResetButton" + analysisId).hide();
    }
  });
}

function addAnalysisMinimizeHandler() {
  $(".historyAnalysisMinimizeButton").off("click");
  $(".historyAnalysisMinimizeButton").on("click", function (e) {
    e.preventDefault();
    var analysisId = e.target.id.substring(
      e.target.className.split(" ")[0].length
    );
    var historyAnalysis = historyAnalyses[analysisId];
    if (historyAnalysis) {
      var buttonText = $("#historyAnalysisMinimizeImage" + analysisId).attr(
        "title"
      );
      // console.log(buttonText, $("#historyAnalysisMinimizeImage" + analysisId), e);
      if (buttonText == "Minimize") {
        // $("#historyAnalysisDashboard" + analysisId).hide();
        $("#historyAnalysisMinimizeImage" + analysisId).attr(
          "src",
          "./public/maximize_128x128.png"
        );
        $("#historyAnalysisMinimizeImage" + analysisId).attr("title", "Maximize");
        $("#historyAnalysisMinimizeImage" + analysisId).removeClass("_minimize");

        for (var property in historyAnalysis) {
          if (historyAnalysis.hasOwnProperty(property) && property.startsWith(analysisId + "_")) {

            delete historyAnalysis[property];
          }
        }
        historyAnalysis.ChartFilters = [];
        Plotly.purge("historyAnalysisDashboard" + analysisId);
        $("#historyAnalysisResetButton" + analysisId).hide();
        $("#historyAnalysisDashboard" + analysisId).empty();

      } else {
        drawDashboard(historyAnalysis);
        // $("#historyAnalysisDashboard" + analysisId).show();
        $("#historyAnalysisMinimizeImage" + analysisId).attr(
          "src",
          "./public/minimize_128x128.png"
        );
        $("#historyAnalysisMinimizeImage" + analysisId).attr("title", "Minimize");
        $("#historyAnalysisMinimizeImage" + analysisId).addClass("_minimize");
      }
    }
  });
}

function addAnalysisDeleteHandler() {
  $(".historyAnalysisHeaderDelete").off("click");
  $(".historyAnalysisHeaderDelete").on("click", function (e) {
    e.preventDefault();
    e.stopPropagation();
    var analysisId = e.currentTarget.id.substring(
      e.currentTarget.className.split(" ")[0].length
    );
    if (historyAnalyses[analysisId] && historyAnalyses[analysisId].AnalysisId) {

      $("#ajaxLoader").addClass("is-active");
      directCall("rt227", "NuoUserMessage", {
        AnalysisId: analysisId
      })
        .done(function (data) {
          handleResponse(data);
        })
        .fail(function () {
          if (sessionId === "X") {
            var data = { StatusCode: 200, Status: "OK", Content: "OK" };
            handleResponse(data);
          }
        });
      var handleResponse = function (data) {
        if (data.StatusCode && data.StatusCode === 200) {
          console.log("Analysis with id" + analysisId + " have been successfully deleted.")
          $("#historyRow" + analysisId).remove();
        } else {
          //window.location.reload();
        }
        $("#ajaxLoader").removeClass("is-active");
      };
    }


  });
}

function addAnalysisSearchHandler() {
  $(".historyAnalysisSearchButton").off("click");
  $(".historyAnalysisSearchButton").on("click", function (e) {
    e.preventDefault();
    var analysisId = e.target.id.substring(
      e.target.className.split(" ")[0].length
    );

    var selection = $("#historyAnalysisSelectionInput" + analysisId).text().replace(/\s+/gi, ' ');
    var filter = $("#historyAnalysisFilterInput" + analysisId).text().replace(/\s+/gi, ' ');


    if (historyAnalyses[analysisId]) {
      historyAnalyses[analysisId].SelectionRaw = selection;
      historyAnalyses[analysisId].FilterRaw = filter;
      var resultTableName = $(
        "#historyAnalysisResultTableInput" + analysisId
      )[0].value;
      if (
        resultTableName !== "No Result Table" &&
        resultTableName.trim().length > 0
      ) {
        historyAnalyses[analysisId].ResultTableName = resultTableName;
      }
      for (var alias in historyAnalyses[analysisId].AliasMapping) {
        var qualifiedName = historyAnalyses[analysisId].AliasMapping[alias];
        if (qualifiedName && qualifiedName.trim().length > 0) {
          historyAnalyses[analysisId].SelectionRaw = historyAnalyses[
            analysisId
          ].SelectionRaw.replace(
            new RegExp("\\b" + alias + "\\b", "gi"),
            qualifiedName
          );
          historyAnalyses[analysisId].FilterRaw = historyAnalyses[
            analysisId
          ].FilterRaw.replace(
            new RegExp("\\b" + alias + "\\b", "gi"),
            qualifiedName
          );
        }
      }
      // console.log(300, historyAnalyses[analysisId].SelectionRaw);
      sendQueryToEva(
        analysisId,
        historyAnalyses[analysisId].SelectionRaw,
        historyAnalyses[analysisId].FilterRaw,
        historyAnalyses[analysisId].ResultTableName
      );
    } else {
      alert("ERROR: I could not relate results to matching analysis.");
      return null;
    }
  });
}

function addAnalysisSaveHandler() {
  $(".historyAnalysisSaveButton").off("click");
  $(".historyAnalysisSaveButton").on("click", function (e) {
    e.preventDefault();
    var analysisId = e.target.id.substring(
      e.target.className.split(" ")[0].length
    );

    if (historyAnalyses[analysisId]) {
      var resultTableName = $(
        "#historyAnalysisResultTableInput" + analysisId
      )[0].value;
      if (
        resultTableName !== "No Result Table" &&
        resultTableName.trim().length > 0
      ) {
        historyAnalyses[analysisId].ResultTableName = resultTableName;
      }

      historyAnalyses[analysisId].Title = $(
        "#historyAnalysisTitleInput" + analysisId
      ).val();

      var selection = $("#historyAnalysisSelectionInput" + analysisId).text().replace(/\s+/gi, ' ');
      var filter = $("#historyAnalysisFilterInput" + analysisId).text().replace(/\s+/gi, ' ');

      historyAnalyses[analysisId].Selection = selection;
      historyAnalyses[analysisId].SelectionRaw = selection;
      historyAnalyses[analysisId].Filter = filter;
      historyAnalyses[analysisId].FilterRaw = filter;
      for (var alias in historyAnalyses[analysisId].AliasMapping) {
        var qualifiedName = historyAnalyses[analysisId].AliasMapping[alias];
        if (qualifiedName && qualifiedName.trim().length > 0) {
          historyAnalyses[analysisId].SelectionRaw = historyAnalyses[
            analysisId
          ].SelectionRaw.replace(
            new RegExp("\\b" + alias + "\\b", "gi"),
            qualifiedName
          );
          historyAnalyses[analysisId].FilterRaw = historyAnalyses[
            analysisId
          ].FilterRaw.replace(
            new RegExp("\\b" + alias + "\\b", "gi"),
            qualifiedName
          );
        }
      }

      saveToHistory(historyAnalyses[analysisId]);
    } else {
      alert("ERROR: I could not relate results to matching analysis.");
      return null;
    }
  });
}

function addAnalysisResultTableHandler() {
  $(".historyAnalysisResultTableButton").off("click");
  $(".historyAnalysisResultTableButton").on("click", function (e) {
    e.preventDefault();
    var analysisId = e.target.id.substring(
      e.target.className.split(" ")[0].length
    );

    var inputBox = $("#historyAnalysisResultTableInput" + analysisId);
    if (inputBox.is(":visible")) {
      $("#historyAnalysisResultTableButton" + analysisId)[0].innerText =
        "Result Table";
      inputBox.hide();
    } else {
      $("#historyAnalysisResultTableButton" + analysisId)[0].innerText =
        "Confirm";
      inputBox.show();
    }
  });
}

function addAnalysisNarrativeHandler() {
  $(".historyAnalysisNarrativeButton").off("click");
  $(".historyAnalysisNarrativeButton").on("click", function (e) {
    // $(e.currentTarget).children()[0].classList.toggle("show");
    // })
    // $(".historyAnalysisNarrativeOption").off("click");
    // $(".historyAnalysisNarrativeOption").on("click", function (e) {

    e.preventDefault();
    e.stopPropagation();
    var analysisId = e.currentTarget.id.substring(e.currentTarget.className.split(" ")[0].length);


    if (activeNarrativeId && activeNarrativeId === analysisId && audioContext && audioContext.state === 'suspended') {
      audioContext.resume();
    } else if (audioContext && audioContext.state === 'running') {

      audioContext.suspend();
      audioContext = new AudioContext();
      activeNarrativeId = null;
    }

    if (!activeNarrativeId || activeNarrativeId !== analysisId) {

      var languageCode = "en";
      // if (e.currentTarget.innerText === "Nederlands") {
      //   languageCode = "nl"
      // }

      var historyAnalysis = historyAnalyses[analysisId];
      if (historyAnalysis && historyAnalysis.Result) {

        var chartData = getChartDataFromResult(historyAnalysis.Result, historyAnalysis.ChartFilters);
        if (chartData.length > 0 && chartData[0].data.length > 0) {

          var stringSeriesGroup = getStringSeries(chartData).slice(0, 5);
          var dateSeriesGroup = getDateSeries(chartData).slice(0, 5);
          var numberSeriesGroup = getNumberSeries(chartData).slice(0, 5);

          var dashboardNarrativeText = narrateDashboard(historyAnalysis, stringSeriesGroup, dateSeriesGroup, numberSeriesGroup, languageCode);

          if (dashboardNarrativeText && dashboardNarrativeText.trim().length > 0) {
            $("#ajaxLoader").addClass("is-active");
            directCall(
              "rt223",
              "NuoSpeechOptions",
              {
                AnalysisId: analysisId,
                Ssml: dashboardNarrativeText,
                LanguageCode: languageCode
              }
            )
              .done(function (data) {
                handleResponse(data);
              })
              .fail(function () {
                if (sessionId === "X") {
                  var data = { "StatusCode": 200, "Status": "OK", "Content": { "NuoEvaMessage": { "AnalysisId": "TableExplorerRequest", "RuleText": "", "CommunicationType": 7, "Message": "" }, "Result": { "Metadata": [{ "DatasetName": "", "EntityName": "Stores", "FieldName": "store_id", "DataType": "INTEGER" }, { "DatasetName": "", "EntityName": "Stores", "FieldName": "store_type", "DataType": "STRING" }, { "DatasetName": "", "EntityName": "Stores", "FieldName": "assortment_type", "DataType": "STRING" }, { "DatasetName": "", "EntityName": "Stores", "FieldName": "competition_distance", "DataType": "FLOAT" }, { "DatasetName": "", "EntityName": "Stores", "FieldName": "promo2", "DataType": "INTEGER" }, { "DatasetName": "", "EntityName": "Stores", "FieldName": "promo2_since_week", "DataType": "FLOAT" }, { "DatasetName": "", "EntityName": "Stores", "FieldName": "Promo2_since_year", "DataType": "FLOAT" }, { "DatasetName": "", "EntityName": "Stores", "FieldName": "competition_open_since_month", "DataType": "INTEGER" }, { "DatasetName": "", "EntityName": "Stores", "FieldName": "competition_open_since_year", "DataType": "INTEGER" }], "Data": [["23", "d", "a", "4060.0", "0", "null", "null", "8", "2005"], ["34", "c", "a", "2240.0", "0", "null", "null", "9", "2009"], ["8", "a", "a", "7520.0", "0", "null", "null", "10", "2014"], ["6", "a", "a", "310.0", "0", "null", "null", "12", "2013"], ["48", "a", "a", "1060.0", "0", "null", "null", "5", "2012"], ["37", "c", "a", "4230.0", "0", "null", "null", "12", "2014"], ["10", "a", "a", "3160.0", "0", "null", "null", "9", "2009"], ["38", "d", "a", "1090.0", "0", "null", "null", "4", "2007"], ["5", "a", "a", "29910.0", "0", "null", "null", "4", "2015"], ["26", "d", "a", "2300.0", "0", "null", "null", "null", "null"], ["45", "d", "a", "9710.0", "0", "null", "null", "2", "2014"], ["1", "c", "a", "1270.0", "0", "null", "null", "9", "2008"], ["25", "c", "a", "430.0", "0", "null", "null", "4", "2003"], ["44", "a", "a", "540.0", "0", "null", "null", "6", "2011"], ["27", "a", "a", "60.0", "1", "5.0", "2011.0", "1", "2005"], ["28", "a", "a", "1200.0", "1", "6.0", "2015.0", "10", "2014"], ["30", "a", "a", "40.0", "1", "10.0", "2014.0", "2", "2014"], ["2", "a", "a", "570.0", "1", "13.0", "2010.0", "11", "2007"], ["46", "c", "a", "1200.0", "1", "14.0", "2011.0", "9", "2005"], ["3", "a", "a", "14130.0", "1", "14.0", "2011.0", "12", "2006"], ["22", "a", "a", "1040.0", "1", "22.0", "2012.0", "null", "null"], ["17", "a", "a", "50.0", "1", "26.0", "2010.0", "12", "2005"], ["39", "a", "a", "260.0", "1", "31.0", "2013.0", "10", "2006"], ["20", "d", "a", "2340.0", "1", "40.0", "2014.0", "5", "2009"], ["14", "a", "a", "1300.0", "1", "40.0", "2011.0", "3", "2014"], ["43", "d", "a", "4880.0", "1", "37.0", "2009.0", "null", "null"], ["40", "a", "a", "180.0", "1", "45.0", "2009.0", "null", "null"], ["13", "d", "a", "310.0", "1", "45.0", "2009.0", "null", "null"], ["32", "a", "a", "2910.0", "1", "45.0", "2009.0", "null", "null"], ["33", "a", "c", "1320.0", "0", "null", "null", "5", "2013"], ["16", "a", "c", "3270.0", "0", "null", "null", "null", "null"], ["31", "d", "c", "9800.0", "0", "null", "null", "7", "2012"], ["29", "d", "c", "2170.0", "0", "null", "null", "null", "null"], ["4", "c", "c", "620.0", "0", "null", "null", "9", "2009"], ["7", "a", "c", "24000.0", "0", "null", "null", "4", "2013"], ["9", "a", "c", "2030.0", "0", "null", "null", "8", "2000"], ["49", "d", "c", "18010.0", "0", "null", "null", "9", "2007"], ["12", "a", "c", "1070.0", "1", "13.0", "2010.0", "null", "null"], ["47", "a", "c", "270.0", "1", "14.0", "2013.0", "4", "2013"], ["15", "d", "c", "4110.0", "1", "14.0", "2011.0", "3", "2010"], ["18", "d", "c", "13840.0", "1", "14.0", "2012.0", "6", "2010"], ["19", "a", "c", "3240.0", "1", "22.0", "2011.0", "null", "null"], ["41", "d", "c", "1180.0", "1", "31.0", "2013.0", "null", "null"], ["24", "a", "c", "4590.0", "1", "40.0", "2011.0", "3", "2000"], ["42", "a", "c", "290.0", "1", "40.0", "2011.0", "null", "null"], ["36", "a", "c", "540.0", "1", "40.0", "2014.0", "6", "2003"], ["11", "a", "c", "960.0", "1", "1.0", "2012.0", "11", "2011"], ["35", "d", "c", "7660.0", "1", "1.0", "2012.0", "10", "2000"], ["21", "c", "c", "550.0", "1", "45.0", "2009.0", "10", "1999"]] }, "ProfilingResult": { "Metadata": [{ "DatasetName": "", "EntityName": "Stores_profiling", "FieldName": "Column_Name", "DataType": "STRING" }, { "DatasetName": "", "EntityName": "Stores_profiling", "FieldName": "Column_Type", "DataType": "STRING" }, { "DatasetName": "", "EntityName": "Stores_profiling", "FieldName": "Total_Count", "DataType": "INTEGER" }, { "DatasetName": "", "EntityName": "Stores_profiling", "FieldName": "Unique_Count", "DataType": "INTEGER" }, { "DatasetName": "", "EntityName": "Stores_profiling", "FieldName": "Empty_Count", "DataType": "INTEGER" }, { "DatasetName": "", "EntityName": "Stores_profiling", "FieldName": "Min", "DataType": "FLOAT" }, { "DatasetName": "", "EntityName": "Stores_profiling", "FieldName": "Max", "DataType": "FLOAT" }, { "DatasetName": "", "EntityName": "Stores_profiling", "FieldName": "Average", "DataType": "FLOAT" }, { "DatasetName": "", "EntityName": "Stores_profiling", "FieldName": "Median", "DataType": "FLOAT" }, { "DatasetName": "", "EntityName": "Stores_profiling", "FieldName": "Standard_Deviation", "DataType": "FLOAT" }], "Data": [["promo2_since_week", "FLOAT", "27", "12", "22", "1.0", "45.0", "24.74074074074074", "22.0", "14.893678978861375"], ["Promo2_since_year", "FLOAT", "27", "7", "22", "2009.0", "2015.0", "2011.3703703703702", "2011.0", "1.7028983603811314"], ["competition_open_since_year", "INTEGER", "37", "14", "12", "1999.0", "2015.0", "2008.6216216216219", "2009.0", "4.5993615970124955"], ["competition_open_since_month", "INTEGER", "37", "12", "12", "1.0", "12.0", "7.1351351351351351", "8.0", "3.2728379466422926"], ["promo2", "INTEGER", "49", "2", "0", "0.0", "1.0", "0.55102040816326525", "1.0", "0.49739010640628328"], ["competition_distance", "FLOAT", "49", "46", "0", "40.0", "29910.0", "4037.1428571428564", "1300.0", "6178.4943017481764"], ["store_id", "INTEGER", "49", "49", "0", "1.0", "49.0", "25.000000000000007", "25.0", "14.142135623730951"], ["store_type", "STRING", "49", "3", "0", "1.0", "1.0", "1.0", "1.0", "null"], ["assortment_type", "STRING", "49", "2", "0", "1.0", "1.0", "1.0", "1.0", "null"]] }, "Pattern": { "Metadata": [{ "DatasetName": "", "EntityName": "Stores_pattern", "FieldName": "Column_Name", "DataType": "STRING" }, { "DatasetName": "", "EntityName": "Stores_pattern", "FieldName": "Column_Type", "DataType": "STRING" }, { "DatasetName": "", "EntityName": "Stores_pattern", "FieldName": "Pattern", "DataType": "STRING" }, { "DatasetName": "", "EntityName": "Stores_pattern", "FieldName": "Total_Count", "DataType": "INTEGER" }], "Data": [["assortment_type", "STRING", "a", "49"], ["store_type", "STRING", "a", "49"]] } } };
                  handleResponse(data);
                } else {

                  $("#ajaxLoader").removeClass("is-active");
                }
              });
            var handleResponse = function (data) {
              if (data.StatusCode && data.StatusCode === 200) {

                var narrativeAudioContent = data.Content;
                activeNarrativeId = analysisId;
                audioContext = new AudioContext();
                // $("#historyAnalysisNarrativeButton"+analysisId).css("background-color","green");
                // $("#historyAnalysisNarrativeButton"+analysisId).css("color","white");
                playByteArray(hexToBytes(narrativeAudioContent));
              } else {
                //window.location.reload();
              }
              $("#ajaxLoader").removeClass("is-active");
            }
          }
        }
      }
    }
  })
}

function addDashboardDataHandler() {
  $(".historyAnalysisDataButton").off("click");
  $(".historyAnalysisDataButton").on("click", function (e) {
    e.preventDefault();
    var analysisId = e.target.id.substring(
      e.target.className.split(" ")[0].length
    );
    $(`#staticDataGridWindow${analysisId}`).show();
    $("#historyAnalysisDataButton" + analysisId).hide();
  });
}

function addChartMinimizeHandler() {

  $(".historyChartMinimizeButton").off("click");
  $(".historyChartMinimizeButton").on("click", function (e) {

    e.preventDefault();

    //change z-index of focused chart
    $('.indexMax').removeClass('indexMax');
    $(e.target).addClass('indexMax');

    var chartContainer = $(e.target).parents('.historyAnalysisChartContainer');
    var buttonEle = $(e.target).find('.historyAnalysisMinimizeImage');

    var buttonText = buttonEle.attr("title");
    if (buttonText == "Minimize") {
      buttonEle.attr(
        "src",
        "./public/maximize_128x128.png"
      );
      buttonEle.attr("title", "Maximize");
      buttonEle.removeClass("_minimize");

      chartContainer.css('width', '50%').css('width', '-=4px');
      chartContainer.css('height', '581px');

    } else {
      buttonEle.attr("src", "./public/minimize_128x128.png");
      buttonEle.attr("title", "Minimize");
      buttonEle.addClass("_minimize");

      chartContainer.css('width', '100%').css('width', '-=4px');
      chartContainer.css('height', '800px');

    }

    //redraw chart with new dimensions
    var elem = $(e.target).parents('.historyAnalysisChartContainer').find('.historyAnalysisChart');
    var data = JSON.parse(elem.attr("data-info")),
      layout = JSON.parse(elem.attr("data-info2"));
    Plotly.newPlot(elem[0], data, layout, { responsive: true });
  });
}

function addChartDeleteHandler() {
  $(".historyChartDeleteButton").off("click");
  $(".historyChartDeleteButton").on("click", function (e) {
    e.preventDefault();
    var analysisIdWithCounter = e.target.id.substring(
      e.target.className.baseVal.split(" ")[0].length
    );

    var elem = document.getElementById(
      "historyAnalysisChartContainer" + analysisIdWithCounter
    );
    // console.log(elem);
    elem.parentNode.removeChild(elem);
  });
}

function addChartDataHandler() {
  $(".historyChartDataButton").off("click");
  $(".historyChartDataButton").on("click", function (e) {
    e.preventDefault();
    var analysisIdWithCounter = e.target.id.substring(
      e.target.className.split(" ")[0].length
    );
    var analysisId = analysisIdWithCounter.substring(
      0,
      analysisIdWithCounter.indexOf("_")
    );

    var historyAnalysis = historyAnalyses[analysisId];
    if (historyAnalysis) {
      var chartDataGrid = historyAnalysis[analysisIdWithCounter].chartDataGrid;

      var seriesGroup = Object.getOwnPropertyNames(chartDataGrid).map(function (
        key
      ) {
        var nuoField = historyAnalysis.Result.Metadata.find(function (ele) {
          return ele.FieldName.toLowerCase() === key.toLowerCase();
        });

        var series = {
          nuoField: nuoField,
          data: chartDataGrid[key]
        };

        return series;
      });
      hideDataGridMetadataButtons();
      drawDataGrid(analysisId, seriesGroup);
    }
  });
}

function hideDataGridMetadataButtons() {
  $("#dataGridRenameButton")
    .parent()
    .hide();
  $("#dataGridDeleteButton")
    .parent()
    .hide();
  $("#dataGridRefreshButton")
    .parent()
    .hide();
  $("#dataGridReplaceButton")
    .parent()
    .hide();
  $("#dataGridMergeButton")
    .parent()
    .hide();
  $("#dataGridSplitButton")
    .parent()
    .hide();
  $("#dataGridColumnToRowsButton")
    .parent()
    .hide();
  $("#dataGridRowsToColumnButton")
    .parent()
    .hide();
  $("#dataGridMoreButton")
    .parent()
    .hide();
}

function showDataGridMetadataButtons() {
  $("#dataGridRenameButton")
    .parent()
    .show();
  $("#dataGridDeleteButton")
    .parent()
    .show();
  $("#dataGridRefreshButton")
    .parent()
    .show();
  $("#dataGridReplaceButton")
    .parent()
    .show();
  $("#dataGridMergeButton")
    .parent()
    .show();
  $("#dataGridSplitButton")
    .parent()
    .show();
  $("#dataGridColumnToRowsButton")
    .parent()
    .show();
  $("#dataGridRowsToColumnButton")
    .parent()
    .show();
  $("#dataGridMoreButton")
    .parent()
    .show();
}

function createDataGridDivContent(analysisId, parentNodeId) {
  var divContent = `<div class='staticDataGridWindow' style='display: inline-block;' id='staticDataGridWindow${analysisId}'>
				<div class='staticDataGridHeader'>
					<h3>Data Preview</h3>
					<ul>
						<li><a href='#' id='staticDataGridDetailsButtonDiv${analysisId}' class='staticDataGridDetailsButtonDiv'>
							<input id='staticDataGridDetailsButton${analysisId}' class='staticDataGridDetailsButton button button-white' type='button' value='Column Details'/>
						</a></li>
					</ul>
					<a href='#' class='close' aria-label='Close' id='closeDataGridWindow${analysisId}'>
						<svg class='minimizeDataGridWindow' id='minimizeDataGridWindow${analysisId}'  width='32px' height='32px' viewBox='0 0 32 32' version='1.1' xmlns='http://www.w3.org/2000/svg'><path d='M16,0.5 C7.43958638,0.5 0.5,7.43958638 0.5,16 C0.5,24.5604136 7.43958638,31.5 16,31.5 C24.5604136,31.5 31.5,24.5604136 31.5,16 C31.5,7.43958638 24.5604136,0.5 16,0.5 Z' class='round_path' stroke='#CCD7E2' fill='#ffffff'></path><g class='plus_path' transform='translate(11.000000, 11.000000)' stroke='#637E9C' stroke-linecap='round' stroke-linejoin='round' stroke-width='2'><path d='M0,0 L10,10'></path><path d='M1.10134124e-13,10 L10,0'></path></g></svg>
					</a>
				</div>
				<div id='staticDataGrid${analysisId}' class='staticDataGrid'>
				</div>
		</div>`;

  $("#" + parentNodeId).append(divContent);
  // $("body").append(divContent);
  $("#historyAnalysisDataButton" + analysisId).hide();
  $(`#minimizeDataGridWindow${analysisId}`).off("click");
  $(`#minimizeDataGridWindow${analysisId}`).on("click", function (e) {
    e.preventDefault();
    var analysisId = e.target.id.substring(
      e.target.className.baseVal.split(" ")[0].length
    );
    var buttonText = $("#historyAnalysisMinimizeImage" + analysisId).attr(
      "title"
    );

    $(`#staticDataGridWindow${analysisId}`).hide();
    $("#historyAnalysisDataButton" + analysisId).show();
    // $("#minimizeDataGridWindow" + analysisId).attr("src", "./public/maximize_white_128x128.png")
    $("#minimizeDataGridWindow" + analysisId).attr("title", "Maximize");
  });
}

function drawDataGrid(analysisId, seriesGroup, isStatic) {

  var jexcelConfig = {
    colHeaders: [],
    colWidths: [],
    colAlignments: [],
    data: [],
    tableWidth: "1100px",
    tableHeight: "300px",
    tableOverflow: true,
    onresize: function (e, colIndex, oldWidth) {
      console.log(colIndex);
      // drawHistograms(seriesGroup,colIndex);
    }
  };


  var seriesValues = seriesGroup.map(function (series) {
    return series.data;
  });

  //Add rest of the data to grid
  for (var i = 0; i < seriesValues[0].length; i++) {
    var row = [];
    seriesValues.forEach(function (ele) {
      row.push(ele[i]);
    });
    jexcelConfig.data.push(row);
  }

  var totalColWidth = 0;
  //Adjust the column width
  seriesGroup.forEach(function (series, i) {
    // var maxLength = ele.reduce(function(acc,r){return Math.max(acc,(r + "").length);},jexcelConfig.colHeaders[i].length);
    // jexcelConfig.colWidths.push(maxLength * 14);
    var colWidth = Math.min(
      260,
      Math.max(180, series.nuoField.FieldName.length * 14 + 100)
    );
    totalColWidth += colWidth;
    jexcelConfig.colWidths.push(colWidth);

    var colHeaderDetailsDivTag =
      "<div class='dataGridColHeaderDetails' title='" +
      series.nuoField.FieldName +
      "' alt='" +
      series.nuoField.FieldName +
      "'>";
    colHeaderDetailsDivTag += getDataTypeDivTag(
      series.nuoField.DataType,
      "dataGridColDataType"
    );
    // console.log(series.nuoField.DataType);
    colHeaderDetailsDivTag +=
      "<div class='dataGridColName'>" + series.nuoField.FieldName + "</div>";
    colHeaderDetailsDivTag +=
      "<div class='dataGridColUniqueCount'>" +
      new Set(series.data).size +
      "</div>";
    colHeaderDetailsDivTag += "</div>";

    var colHeaderDivTag =
      "<div class='dataGridColHeader' title='" +
      series.nuoField.FieldName +
      "' alt='" +
      series.nuoField.FieldName +
      "'>";
    colHeaderDivTag += "<div class='dataGridColSelection'>";
    colHeaderDivTag += " <div class='allcheckbox-td custom-checkbox'>" +
      "<input id='dataGridColCheckbox" +
      i +
      "' class='dataGridColCheckbox' onclick='clickedDataGridColCheckbox(this)' type='checkbox' value='" +
      escape(JSON.stringify(series.nuoField)) +
      "'/><span></span></div></div>";
    colHeaderDivTag += colHeaderDetailsDivTag;

    //Add div tag for histograms
    if (isStatic && isStatic === true) {
      colHeaderDivTag +=
        "<div id='staticDataGridTableColumn" +
        analysisId +
        i +
        "' class='dataGridTableColumn'></div>";
    } else {
      colHeaderDivTag +=
        "<div id='dataGridTableColumn" +
        i +
        "' class='dataGridTableColumn'></div>";
    }

    colHeaderDivTag += "</div>";

    jexcelConfig.colHeaders.push(colHeaderDivTag);

    if (
      ["int64", "int", "float64", "float"].indexOf(
        series.nuoField.DataType.toLowerCase()
      ) >= 0
    ) {
      jexcelConfig.colAlignments.push("right");
    } else {
      jexcelConfig.colAlignments.push("left");
    }
  });

  jexcelConfig.columns = jexcelConfig.colHeaders.map(function () {
    return { type: "text" };
  });
  if (isStatic && isStatic === true) {
    $("#staticDataGrid" + analysisId).jexcel(jexcelConfig);
    addDataGridCheckboxEvents();

    $("#staticDataGridWindow" + analysisId).attr("analysisId", analysisId);
    // $("#staticDataGrid" + analysisId).width(seriesGroup.length * 220);
  } else {
    // $(".dataGrid")[0].style.width = totalColWidth + "px";
    $("#dataGrid").jexcel(jexcelConfig);
    addDataGridCheckboxEvents();

    $(`#dataGridWindow`).fadeIn();
    $(`#closeDataGridWindow`).off("click");
    $(`#closeDataGridWindow`).on("click", function (e) {
      e.preventDefault();
      $(`#dataGridWindow`).fadeOut();
      lastClickedColIndex = -1;
    });
    if (analysisId === "TablePreview") {
      $("#dataGridWindow .modal-header h3")[0].innerText =
        "Preview: " + seriesGroup[0].nuoField.EntityName;
      showDataGridMetadataButtons();
    } else {
      $("#dataGridWindow .modal-header h3")[0].innerText = "Data Preview";
    }

    $("#dataGridWindow").attr("analysisId", analysisId);
  }
  addDataGridButtonHandler();

  //Draw a histogram for each column
  seriesGroup.forEach(function (series, i) {
    if (isStatic && isStatic === true) {
      drawHistogram("staticDataGridTableColumn" + analysisId, series, i);
    } else {
      drawHistogram("dataGridTableColumn", series, i);
    }
  });

  var selector = $(".dataGridWindow");
  if (isStatic && isStatic === true) {
    selector = $(".staticDataGridWindow");
  }
  selector
    .draggable({
      start: (e, ui) => {
        $('.indexMax').removeClass('indexMax');
        $(e.target).addClass('indexMax');
      }
    })
    .resizable({
      // helper: "ui-resizable-helper",
      // handles: 's',
      stop: (e, ui) => {
        $(ui.element).width($(ui.originalElement)[0].clientWidth + 2);
        var heightTableHeader = $(ui.element)
          .find("thead")
          .height();
        var heightBox = $(ui.element)[0].offsetHeight;
        $(ui.element)
          .find("tbody")
          .attr(
            "style",
            "display: block; overflow: auto; height: auto; max-height:" +
            (heightBox - heightTableHeader - 92 + "px")
          );
      }
    });
}
$(document).keydown(function (e) {
  if (e.which == "16") shiftKeyDown = true;
  if (e.which == "17") ctrlKeyDown = true;
});

$(document).keyup(function () {
  shiftKeyDown = false;
  ctrlKeyDown = false;
});

function clickedDataGridColCheckbox(currEle) {
  var currClickedColIndex = parseInt(
    currEle.id.substring(currEle.className.length)
  );

  if (shiftKeyDown === false && ctrlKeyDown === false) {
    $(".dataGrid .dataGridColCheckbox").each(function (index, ele) {
      if (currEle.id !== ele.id) ele.checked = false;
    });
  } else if (shiftKeyDown === true && lastClickedColIndex >= 0) {
    if (lastClickedColIndex < currClickedColIndex) {
      for (var i = lastClickedColIndex; i < currClickedColIndex; i++) {
        $(".dataGrid #dataGridColCheckbox" + i)[0].checked = currEle.checked;
      }
    } else {
      for (var i = currClickedColIndex; i < lastClickedColIndex; i++) {
        $(".dataGrid #dataGridColCheckbox" + i)[0].checked = currEle.checked;
      }
    }
  }

  lastClickedColIndex = currClickedColIndex;
  shiftKeyDown = false;
  ctrlKeyDown = false;
}

function loadColumnSummaryWindow(analysisId, selector) {
  var selectedColumns = Object.values($(selector))
    .filter(function (ele) {
      return ele.checked;
    })
    .map(function (ele) {
      return JSON.parse(unescape(ele.value));
    });

  if (selectedColumns && selectedColumns.length > 0) {
    var selectedColName = selectedColumns[0].FieldName;
    if (selectedColName && selectedColName.length > 0) {
      if (analysisId === "TablePreview") {
        var tableName = selectedColumns[0].EntityName;
        var tablePreview = tablePreviews[tableName];
        if (tablePreview.ProfilingResult) {
          drawColumnSummary(
            selectedColName,
            tablePreview.Result,
            tablePreview.ProfilingResult,
            tablePreview.ChartFilters,
            tablePreview.Pattern
          );
        } else {
          $("#ajaxLoader").addClass("is-active");
          directCall("rt179", "NuoEntities", [
            {
              DatasetName: "",
              EntityName: tableName,
              Fields: []
            }
          ])
            .done(function (data) {
              handleResponse(data);
            })
            .fail(function () {
              if (sessionId === "X") {
                var data = {
                  StatusCode: 200,
                  Status: "OK",
                  Content: {
                    NuoEvaMessage: {
                      AnalysisId: "TableExplorerRequest",
                      RuleText: "",
                      CommunicationType: 7,
                      Message: ""
                    },
                    Result: {
                      Metadata: [
                        {
                          DatasetName: "",
                          EntityName: "Stores",
                          FieldName: "store_id",
                          DataType: "INTEGER"
                        },
                        {
                          DatasetName: "",
                          EntityName: "Stores",
                          FieldName: "store_type",
                          DataType: "STRING"
                        },
                        {
                          DatasetName: "",
                          EntityName: "Stores",
                          FieldName: "assortment_type",
                          DataType: "STRING"
                        },
                        {
                          DatasetName: "",
                          EntityName: "Stores",
                          FieldName: "competition_distance",
                          DataType: "FLOAT"
                        },
                        {
                          DatasetName: "",
                          EntityName: "Stores",
                          FieldName: "promo2",
                          DataType: "INTEGER"
                        },
                        {
                          DatasetName: "",
                          EntityName: "Stores",
                          FieldName: "promo2_since_week",
                          DataType: "FLOAT"
                        },
                        {
                          DatasetName: "",
                          EntityName: "Stores",
                          FieldName: "Promo2_since_year",
                          DataType: "FLOAT"
                        },
                        {
                          DatasetName: "",
                          EntityName: "Stores",
                          FieldName: "competition_open_since_month",
                          DataType: "INTEGER"
                        },
                        {
                          DatasetName: "",
                          EntityName: "Stores",
                          FieldName: "competition_open_since_year",
                          DataType: "INTEGER"
                        }
                      ],
                      Data: [
                        [
                          "23",
                          "d",
                          "a",
                          "4060.0",
                          "0",
                          "null",
                          "null",
                          "8",
                          "2005"
                        ],
                        [
                          "34",
                          "c",
                          "a",
                          "2240.0",
                          "0",
                          "null",
                          "null",
                          "9",
                          "2009"
                        ],
                        [
                          "8",
                          "a",
                          "a",
                          "7520.0",
                          "0",
                          "null",
                          "null",
                          "10",
                          "2014"
                        ],
                        [
                          "6",
                          "a",
                          "a",
                          "310.0",
                          "0",
                          "null",
                          "null",
                          "12",
                          "2013"
                        ],
                        [
                          "48",
                          "a",
                          "a",
                          "1060.0",
                          "0",
                          "null",
                          "null",
                          "5",
                          "2012"
                        ],
                        [
                          "37",
                          "c",
                          "a",
                          "4230.0",
                          "0",
                          "null",
                          "null",
                          "12",
                          "2014"
                        ],
                        [
                          "10",
                          "a",
                          "a",
                          "3160.0",
                          "0",
                          "null",
                          "null",
                          "9",
                          "2009"
                        ],
                        [
                          "38",
                          "d",
                          "a",
                          "1090.0",
                          "0",
                          "null",
                          "null",
                          "4",
                          "2007"
                        ],
                        [
                          "5",
                          "a",
                          "a",
                          "29910.0",
                          "0",
                          "null",
                          "null",
                          "4",
                          "2015"
                        ],
                        [
                          "26",
                          "d",
                          "a",
                          "2300.0",
                          "0",
                          "null",
                          "null",
                          "null",
                          "null"
                        ],
                        [
                          "45",
                          "d",
                          "a",
                          "9710.0",
                          "0",
                          "null",
                          "null",
                          "2",
                          "2014"
                        ],
                        [
                          "1",
                          "c",
                          "a",
                          "1270.0",
                          "0",
                          "null",
                          "null",
                          "9",
                          "2008"
                        ],
                        [
                          "25",
                          "c",
                          "a",
                          "430.0",
                          "0",
                          "null",
                          "null",
                          "4",
                          "2003"
                        ],
                        [
                          "44",
                          "a",
                          "a",
                          "540.0",
                          "0",
                          "null",
                          "null",
                          "6",
                          "2011"
                        ],
                        [
                          "27",
                          "a",
                          "a",
                          "60.0",
                          "1",
                          "5.0",
                          "2011.0",
                          "1",
                          "2005"
                        ],
                        [
                          "28",
                          "a",
                          "a",
                          "1200.0",
                          "1",
                          "6.0",
                          "2015.0",
                          "10",
                          "2014"
                        ],
                        [
                          "30",
                          "a",
                          "a",
                          "40.0",
                          "1",
                          "10.0",
                          "2014.0",
                          "2",
                          "2014"
                        ],
                        [
                          "2",
                          "a",
                          "a",
                          "570.0",
                          "1",
                          "13.0",
                          "2010.0",
                          "11",
                          "2007"
                        ],
                        [
                          "46",
                          "c",
                          "a",
                          "1200.0",
                          "1",
                          "14.0",
                          "2011.0",
                          "9",
                          "2005"
                        ],
                        [
                          "3",
                          "a",
                          "a",
                          "14130.0",
                          "1",
                          "14.0",
                          "2011.0",
                          "12",
                          "2006"
                        ],
                        [
                          "22",
                          "a",
                          "a",
                          "1040.0",
                          "1",
                          "22.0",
                          "2012.0",
                          "null",
                          "null"
                        ],
                        [
                          "17",
                          "a",
                          "a",
                          "50.0",
                          "1",
                          "26.0",
                          "2010.0",
                          "12",
                          "2005"
                        ],
                        [
                          "39",
                          "a",
                          "a",
                          "260.0",
                          "1",
                          "31.0",
                          "2013.0",
                          "10",
                          "2006"
                        ],
                        [
                          "20",
                          "d",
                          "a",
                          "2340.0",
                          "1",
                          "40.0",
                          "2014.0",
                          "5",
                          "2009"
                        ],
                        [
                          "14",
                          "a",
                          "a",
                          "1300.0",
                          "1",
                          "40.0",
                          "2011.0",
                          "3",
                          "2014"
                        ],
                        [
                          "43",
                          "d",
                          "a",
                          "4880.0",
                          "1",
                          "37.0",
                          "2009.0",
                          "null",
                          "null"
                        ],
                        [
                          "40",
                          "a",
                          "a",
                          "180.0",
                          "1",
                          "45.0",
                          "2009.0",
                          "null",
                          "null"
                        ],
                        [
                          "13",
                          "d",
                          "a",
                          "310.0",
                          "1",
                          "45.0",
                          "2009.0",
                          "null",
                          "null"
                        ],
                        [
                          "32",
                          "a",
                          "a",
                          "2910.0",
                          "1",
                          "45.0",
                          "2009.0",
                          "null",
                          "null"
                        ],
                        [
                          "33",
                          "a",
                          "c",
                          "1320.0",
                          "0",
                          "null",
                          "null",
                          "5",
                          "2013"
                        ],
                        [
                          "16",
                          "a",
                          "c",
                          "3270.0",
                          "0",
                          "null",
                          "null",
                          "null",
                          "null"
                        ],
                        [
                          "31",
                          "d",
                          "c",
                          "9800.0",
                          "0",
                          "null",
                          "null",
                          "7",
                          "2012"
                        ],
                        [
                          "29",
                          "d",
                          "c",
                          "2170.0",
                          "0",
                          "null",
                          "null",
                          "null",
                          "null"
                        ],
                        [
                          "4",
                          "c",
                          "c",
                          "620.0",
                          "0",
                          "null",
                          "null",
                          "9",
                          "2009"
                        ],
                        [
                          "7",
                          "a",
                          "c",
                          "24000.0",
                          "0",
                          "null",
                          "null",
                          "4",
                          "2013"
                        ],
                        [
                          "9",
                          "a",
                          "c",
                          "2030.0",
                          "0",
                          "null",
                          "null",
                          "8",
                          "2000"
                        ],
                        [
                          "49",
                          "d",
                          "c",
                          "18010.0",
                          "0",
                          "null",
                          "null",
                          "9",
                          "2007"
                        ],
                        [
                          "12",
                          "a",
                          "c",
                          "1070.0",
                          "1",
                          "13.0",
                          "2010.0",
                          "null",
                          "null"
                        ],
                        [
                          "47",
                          "a",
                          "c",
                          "270.0",
                          "1",
                          "14.0",
                          "2013.0",
                          "4",
                          "2013"
                        ],
                        [
                          "15",
                          "d",
                          "c",
                          "4110.0",
                          "1",
                          "14.0",
                          "2011.0",
                          "3",
                          "2010"
                        ],
                        [
                          "18",
                          "d",
                          "c",
                          "13840.0",
                          "1",
                          "14.0",
                          "2012.0",
                          "6",
                          "2010"
                        ],
                        [
                          "19",
                          "a",
                          "c",
                          "3240.0",
                          "1",
                          "22.0",
                          "2011.0",
                          "null",
                          "null"
                        ],
                        [
                          "41",
                          "d",
                          "c",
                          "1180.0",
                          "1",
                          "31.0",
                          "2013.0",
                          "null",
                          "null"
                        ],
                        [
                          "24",
                          "a",
                          "c",
                          "4590.0",
                          "1",
                          "40.0",
                          "2011.0",
                          "3",
                          "2000"
                        ],
                        [
                          "42",
                          "a",
                          "c",
                          "290.0",
                          "1",
                          "40.0",
                          "2011.0",
                          "null",
                          "null"
                        ],
                        [
                          "36",
                          "a",
                          "c",
                          "540.0",
                          "1",
                          "40.0",
                          "2014.0",
                          "6",
                          "2003"
                        ],
                        [
                          "11",
                          "a",
                          "c",
                          "960.0",
                          "1",
                          "1.0",
                          "2012.0",
                          "11",
                          "2011"
                        ],
                        [
                          "35",
                          "d",
                          "c",
                          "7660.0",
                          "1",
                          "1.0",
                          "2012.0",
                          "10",
                          "2000"
                        ],
                        [
                          "21",
                          "c",
                          "c",
                          "550.0",
                          "1",
                          "45.0",
                          "2009.0",
                          "10",
                          "1999"
                        ]
                      ]
                    },
                    ProfilingResult: {
                      Metadata: [
                        {
                          DatasetName: "",
                          EntityName: "Stores_profiling",
                          FieldName: "Column_Name",
                          DataType: "STRING"
                        },
                        {
                          DatasetName: "",
                          EntityName: "Stores_profiling",
                          FieldName: "Column_Type",
                          DataType: "STRING"
                        },
                        {
                          DatasetName: "",
                          EntityName: "Stores_profiling",
                          FieldName: "Total_Count",
                          DataType: "INTEGER"
                        },
                        {
                          DatasetName: "",
                          EntityName: "Stores_profiling",
                          FieldName: "Unique_Count",
                          DataType: "INTEGER"
                        },
                        {
                          DatasetName: "",
                          EntityName: "Stores_profiling",
                          FieldName: "Empty_Count",
                          DataType: "INTEGER"
                        },
                        {
                          DatasetName: "",
                          EntityName: "Stores_profiling",
                          FieldName: "Min",
                          DataType: "FLOAT"
                        },
                        {
                          DatasetName: "",
                          EntityName: "Stores_profiling",
                          FieldName: "Max",
                          DataType: "FLOAT"
                        },
                        {
                          DatasetName: "",
                          EntityName: "Stores_profiling",
                          FieldName: "Average",
                          DataType: "FLOAT"
                        },
                        {
                          DatasetName: "",
                          EntityName: "Stores_profiling",
                          FieldName: "Median",
                          DataType: "FLOAT"
                        },
                        {
                          DatasetName: "",
                          EntityName: "Stores_profiling",
                          FieldName: "Standard_Deviation",
                          DataType: "FLOAT"
                        }
                      ],
                      Data: [
                        [
                          "promo2_since_week",
                          "FLOAT",
                          "27",
                          "12",
                          "22",
                          "1.0",
                          "45.0",
                          "24.74074074074074",
                          "22.0",
                          "14.893678978861375"
                        ],
                        [
                          "Promo2_since_year",
                          "FLOAT",
                          "27",
                          "7",
                          "22",
                          "2009.0",
                          "2015.0",
                          "2011.3703703703702",
                          "2011.0",
                          "1.7028983603811314"
                        ],
                        [
                          "competition_open_since_year",
                          "INTEGER",
                          "37",
                          "14",
                          "12",
                          "1999.0",
                          "2015.0",
                          "2008.6216216216219",
                          "2009.0",
                          "4.5993615970124955"
                        ],
                        [
                          "competition_open_since_month",
                          "INTEGER",
                          "37",
                          "12",
                          "12",
                          "1.0",
                          "12.0",
                          "7.1351351351351351",
                          "8.0",
                          "3.2728379466422926"
                        ],
                        [
                          "promo2",
                          "INTEGER",
                          "49",
                          "2",
                          "0",
                          "0.0",
                          "1.0",
                          "0.55102040816326525",
                          "1.0",
                          "0.49739010640628328"
                        ],
                        [
                          "competition_distance",
                          "FLOAT",
                          "49",
                          "46",
                          "0",
                          "40.0",
                          "29910.0",
                          "4037.1428571428564",
                          "1300.0",
                          "6178.4943017481764"
                        ],
                        [
                          "store_id",
                          "INTEGER",
                          "49",
                          "49",
                          "0",
                          "1.0",
                          "49.0",
                          "25.000000000000007",
                          "25.0",
                          "14.142135623730951"
                        ],
                        [
                          "store_type",
                          "STRING",
                          "49",
                          "3",
                          "0",
                          "1.0",
                          "1.0",
                          "1.0",
                          "1.0",
                          "null"
                        ],
                        [
                          "assortment_type",
                          "STRING",
                          "49",
                          "2",
                          "0",
                          "1.0",
                          "1.0",
                          "1.0",
                          "1.0",
                          "null"
                        ]
                      ]
                    },
                    Pattern: {
                      Metadata: [
                        {
                          DatasetName: "",
                          EntityName: "Stores_pattern",
                          FieldName: "Column_Name",
                          DataType: "STRING"
                        },
                        {
                          DatasetName: "",
                          EntityName: "Stores_pattern",
                          FieldName: "Column_Type",
                          DataType: "STRING"
                        },
                        {
                          DatasetName: "",
                          EntityName: "Stores_pattern",
                          FieldName: "Pattern",
                          DataType: "STRING"
                        },
                        {
                          DatasetName: "",
                          EntityName: "Stores_pattern",
                          FieldName: "Total_Count",
                          DataType: "INTEGER"
                        }
                      ],
                      Data: [
                        ["assortment_type", "STRING", "a", "49"],
                        ["store_type", "STRING", "a", "49"]
                      ]
                    }
                  }
                };
                handleResponse(data);
              }
            });
          var handleResponse = function (data) {
            if (data.StatusCode && data.StatusCode === 200) {
              var nuoQueryResponse = data.Content;

              var result = nuoQueryResponse.Result;
              var profilingResult = nuoQueryResponse.ProfilingResult;
              var pattern = nuoQueryResponse.Pattern;
              tablePreviews[tableName] = {
                Result: result,
                ProfilingResult: profilingResult,
                Pattern: pattern
              };
              var tablePreview = tablePreviews[tableName];
              drawColumnSummary(
                selectedColName,
                tablePreview.Result,
                tablePreview.ProfilingResult,
                tablePreview.ChartFilters,
                tablePreview.Pattern
              );
            } else {
              //window.location.reload();
            }
            $("#ajaxLoader").removeClass("is-active");
          };
        }
      } else {
        var historyAnalysis = historyAnalyses[analysisId];

        if (historyAnalysis) {
          if (historyAnalysis.ProfilingResult) {
            drawColumnSummary(
              selectedColName,
              historyAnalysis.Result,
              historyAnalysis.ProfilingResult,
              historyAnalysis.ChartFilters,
              historyAnalysis.Pattern
            );
          } else {
            $("#ajaxLoader").addClass("is-active");
            directCall("rt131", "NuoUserMessage", {
              AnalysisId: historyAnalysis.AnalysisId,
              Selection: historyAnalysis.SelectionRaw,
              Filter: historyAnalysis.FilterRaw
            })
              .done(function (data) {
                handleResponse(data);
              })
              .fail(function () {
                if (sessionId === "X") {
                  var data = {
                    StatusCode: 200,
                    Status: "OK",
                    Content: {
                      NuoEvaMessage: {
                        AnalysisId: "1541396428965",
                        RuleText: "",
                        CommunicationType: 7,
                        Message: ""
                      },
                      Result: {
                        Metadata: [
                          {
                            DatasetName: "",
                            EntityName: "1a932a0a78daa084bbc0b9a07e66b629",
                            FieldName: "Country",
                            DataType: "STRING"
                          },
                          {
                            DatasetName: "",
                            EntityName: "1a932a0a78daa084bbc0b9a07e66b629",
                            FieldName: "GL_2016_Value",
                            DataType: "FLOAT"
                          },
                          {
                            DatasetName: "",
                            EntityName: "1a932a0a78daa084bbc0b9a07e66b629",
                            FieldName: "GL_2016_Value_1",
                            DataType: "FLOAT"
                          },
                          {
                            DatasetName: "",
                            EntityName: "1a932a0a78daa084bbc0b9a07e66b629",
                            FieldName: "FX_Value",
                            DataType: "FLOAT"
                          }
                        ],
                        Data: [
                          ["FI", "-0.812", "-0.406", "1.0"],
                          ["FI", "-1.44292", "-0.72146", "1.0"],
                          ["FI", "-62.02975", "-31.014875", "1.0"],
                          ["FI", "-0.35291", "-0.176455", "1.0"],
                          ["FI", "-2.3256", "-1.1628", "1.0"],
                          ["FI", "0.41381", "0.206905", "1.0"],
                          ["FI", "-0.0306", "-0.0153", "1.0"],
                          ["FI", "-0.18621", "-0.093105", "1.0"],
                          ["FI", "-0.00503", "-0.002515", "1.0"],
                          ["FI", "2171.08858", "1085.54429", "1.0"],
                          ["FI", "127.364486", "63.682243", "1.0"],
                          ["FI", "69.470764", "34.735382", "1.0"],
                          ["FI", "-0.2886", "-0.1443", "1.0"],
                          ["FI", "220.181538", "110.090769", "1.0"],
                          ["FI", "-0.03435", "-0.017175", "1.0"],
                          ["FI", "12.91878", "6.45939", "1.0"],
                          ["FI", "224.432978", "112.216489", "1.0"],
                          ["FI", "-22.938", "-11.469", "1.0"],
                          ["FI", "39.287934", "19.643967", "1.0"],
                          ["FI", "-21.84192", "-10.92096", "1.0"],
                          ["FI", "0.110276", "0.055138", "1.0"],
                          ["FI", "-2.38492", "-1.19246", "1.0"],
                          ["FI", "1.942446", "0.971223", "1.0"],
                          ["FI", "7.22", "3.61", "1.0"],
                          ["FI", "149.244202", "74.622101", "1.0"],
                          ["FI", "8.198044", "4.099022", "1.0"],
                          ["FI", "-0.7448", "-0.3724", "1.0"],
                          ["FI", "2.502186", "1.251093", "1.0"],
                          ["FI", "0.45445", "0.227225", "1.0"],
                          ["FI", "27.186644", "13.593322", "1.0"],
                          ["FI", "-2.82961", "-1.414805", "1.0"],
                          ["FI", "-3.3425", "-1.67125", "1.0"],
                          ["FI", "1.84844", "0.92422", "1.0"],
                          ["FI", "68.978702", "34.489351", "1.0"],
                          ["FI", "-0.23464", "-0.11732", "1.0"],
                          ["FI", "-0.18979", "-0.094895", "1.0"],
                          ["FI", "-1.3034", "-0.6517", "1.0"],
                          ["FI", "37.25882", "18.62941", "1.0"],
                          ["FI", "9.4649", "4.73245", "1.0"],
                          ["FI", "3.63484", "1.81742", "1.0"],
                          ["FI", "-0.67148", "-0.33574", "1.0"],
                          ["FI", "-1.65256", "-0.82628", "1.0"],
                          ["FI", "-2.97983", "-1.489915", "1.0"],
                          ["FI", "21.78545", "10.892725", "1.0"],
                          ["FI", "-2.36573", "-1.182865", "1.0"],
                          ["FI", "15.281434", "7.640717", "1.0"],
                          ["FI", "2.20039", "1.100195", "1.0"],
                          ["FI", "-93.76091", "-46.880455", "1.0"],
                          ["FI", "547.880846", "273.940423", "1.0"],
                          ["FI", "16.625456", "8.312728", "1.0"],
                          ["FI", "-0.35297", "-0.176485", "1.0"],
                          ["FI", "-0.6734", "-0.3367", "1.0"],
                          ["FI", "-2.91304", "-1.45652", "1.0"],
                          ["FI", "-6.335", "-3.1675", "1.0"],
                          ["FI", "-60.35096", "-30.17548", "1.0"],
                          ["FI", "-0.07186", "-0.03593", "1.0"],
                          ["FI", "17.01678", "8.50839", "1.0"],
                          ["FI", "-10.865", "-5.4325", "1.0"],
                          ["FI", "-1.04504", "-0.52252", "1.0"],
                          ["FI", "-1.7195", "-0.85975", "1.0"],
                          ["FI", "0.92931", "0.464655", "1.0"],
                          ["FI", "-36.62213", "-18.311065", "1.0"],
                          ["FI", "-38.06805", "-19.034025", "1.0"],
                          ["FI", "-0.021", "-0.0105", "1.0"],
                          ["FI", "2.844072", "1.422036", "1.0"],
                          ["FI", "-45.0", "-22.5", "1.0"],
                          ["FI", "-1.31872", "-0.65936", "1.0"],
                          ["FI", "6.29805", "3.149025", "1.0"],
                          ["FI", "-1.01644", "-0.50822", "1.0"],
                          ["FI", "-0.08795", "-0.043975", "1.0"],
                          ["FI", "-0.19509", "-0.097545", "1.0"],
                          ["FI", "44.709318", "22.354659", "1.0"],
                          ["FI", "-1.36809", "-0.684045", "1.0"],
                          ["FI", "-0.31795", "-0.158975", "1.0"],
                          ["FI", "-3.12777", "-1.563885", "1.0"],
                          ["FI", "-24.26155", "-12.130775", "1.0"],
                          ["FI", "-1.4325", "-0.71625", "1.0"],
                          ["FI", "-7.61402", "-3.80701", "1.0"],
                          ["FI", "186.828254", "93.414127", "1.0"],
                          ["FI", "55.911072", "27.955536", "1.0"],
                          ["FI", "-12.44573", "-6.222865", "1.0"],
                          ["FI", "-1.25897", "-0.629485", "1.0"],
                          ["FI", "-1.146", "-0.573", "1.0"],
                          ["FI", "-103.0", "-51.5", "1.0"],
                          ["FI", "2.317126", "1.158563", "1.0"],
                          ["FI", "6.81723", "3.408615", "1.0"],
                          ["FI", "-0.4061", "-0.20305", "1.0"],
                          ["FI", "1.121", "0.5605", "1.0"],
                          ["FI", "287.347906", "143.673953", "1.0"],
                          ["FI", "14.281388", "7.140694", "1.0"],
                          ["FI", "-0.7188", "-0.3594", "1.0"],
                          ["FI", "0.47038", "0.23519", "1.0"],
                          ["FI", "24.234158", "12.117079", "1.0"],
                          ["FI", "28.030244", "14.015122", "1.0"],
                          ["FI", "1.53582", "0.76791", "1.0"],
                          ["FI", "-0.12184", "-0.06092", "1.0"],
                          ["FI", "-32.94589", "-16.472945", "1.0"],
                          ["FI", "10.923898", "5.461949", "1.0"],
                          ["FI", "256.928108", "128.464054", "1.0"],
                          ["FI", "22.32808", "11.16404", "1.0"]
                        ]
                      },
                      ProfilingResult: {
                        Metadata: [
                          {
                            DatasetName: "",
                            EntityName:
                              "1a932a0a78daa084bbc0b9a07e66b629_profiling",
                            FieldName: "Column_Name",
                            DataType: "STRING"
                          },
                          {
                            DatasetName: "",
                            EntityName:
                              "1a932a0a78daa084bbc0b9a07e66b629_profiling",
                            FieldName: "Column_Type",
                            DataType: "STRING"
                          },
                          {
                            DatasetName: "",
                            EntityName:
                              "1a932a0a78daa084bbc0b9a07e66b629_profiling",
                            FieldName: "Total_Count",
                            DataType: "INTEGER"
                          },
                          {
                            DatasetName: "",
                            EntityName:
                              "1a932a0a78daa084bbc0b9a07e66b629_profiling",
                            FieldName: "Unique_Count",
                            DataType: "INTEGER"
                          },
                          {
                            DatasetName: "",
                            EntityName:
                              "1a932a0a78daa084bbc0b9a07e66b629_profiling",
                            FieldName: "Empty_Count",
                            DataType: "INTEGER"
                          },
                          {
                            DatasetName: "",
                            EntityName:
                              "1a932a0a78daa084bbc0b9a07e66b629_profiling",
                            FieldName: "Min",
                            DataType: "FLOAT"
                          },
                          {
                            DatasetName: "",
                            EntityName:
                              "1a932a0a78daa084bbc0b9a07e66b629_profiling",
                            FieldName: "Max",
                            DataType: "FLOAT"
                          },
                          {
                            DatasetName: "",
                            EntityName:
                              "1a932a0a78daa084bbc0b9a07e66b629_profiling",
                            FieldName: "Average",
                            DataType: "FLOAT"
                          },
                          {
                            DatasetName: "",
                            EntityName:
                              "1a932a0a78daa084bbc0b9a07e66b629_profiling",
                            FieldName: "Median",
                            DataType: "FLOAT"
                          },
                          {
                            DatasetName: "",
                            EntityName:
                              "1a932a0a78daa084bbc0b9a07e66b629_profiling",
                            FieldName: "Standard_Deviation",
                            DataType: "FLOAT"
                          }
                        ],
                        Data: [
                          [
                            "Country",
                            "STRING",
                            "100",
                            "1",
                            "0",
                            "2.0",
                            "2.0",
                            "2.0",
                            "2.0",
                            "null"
                          ],
                          [
                            "FX_Value",
                            "FLOAT",
                            "100",
                            "1",
                            "0",
                            "1.0",
                            "1.0",
                            "1.0",
                            "1.0",
                            "0.0"
                          ],
                          [
                            "GL_2016_Value_1",
                            "FLOAT",
                            "100",
                            "100",
                            "0",
                            "-51.5",
                            "1085.54429",
                            "20.681395849999994",
                            "-0.043975",
                            "114.24473318537817"
                          ],
                          [
                            "GL_2016_Value",
                            "FLOAT",
                            "100",
                            "100",
                            "0",
                            "-103.0",
                            "2171.08858",
                            "41.362791699999988",
                            "-0.08795",
                            "228.48946637075633"
                          ]
                        ]
                      },
                      Pattern: {
                        Metadata: [
                          {
                            DatasetName: "",
                            EntityName:
                              "1a932a0a78daa084bbc0b9a07e66b629_pattern",
                            FieldName: "Column_Name",
                            DataType: "STRING"
                          },
                          {
                            DatasetName: "",
                            EntityName:
                              "1a932a0a78daa084bbc0b9a07e66b629_pattern",
                            FieldName: "Column_Type",
                            DataType: "STRING"
                          },
                          {
                            DatasetName: "",
                            EntityName:
                              "1a932a0a78daa084bbc0b9a07e66b629_pattern",
                            FieldName: "Pattern",
                            DataType: "STRING"
                          },
                          {
                            DatasetName: "",
                            EntityName:
                              "1a932a0a78daa084bbc0b9a07e66b629_pattern",
                            FieldName: "Total_Count",
                            DataType: "INTEGER"
                          }
                        ],
                        Data: [["Country", "STRING", "A", "100"]]
                      }
                    }
                  };
                  handleResponse(data);
                }
              });
            var handleResponse = function (data) {
              if (data.StatusCode && data.StatusCode === 200) {
                var nuoQueryResponse = data.Content;
                if (
                  nuoQueryResponse.NuoEvaMessage &&
                  nuoQueryResponse.NuoEvaMessage.CommunicationType
                ) {
                  handleResponseFromEva(nuoQueryResponse, false);
                  drawColumnSummary(
                    selectedColName,
                    historyAnalysis.Result,
                    historyAnalysis.ProfilingResult,
                    historyAnalysis.ChartFilters,
                    historyAnalysis.Pattern
                  );
                } else {
                  console.log("Something went wrong.");
                  console.log(JSON.stringify(nuoQueryResponse));
                }
                $("#ajaxLoader").removeClass("is-active");
              } else {
                $("#ajaxLoader").removeClass("is-active");
                //window.location.reload();
              }
            };
          }
        }
      }
    }
  }
}

function drawColumnSummary(
  selectedColName,
  result,
  profilingResult,
  chartFilters,
  pattern
) {
  var filter = {
    nuoField: {
      FieldName: "Column_Name",
      DataType: "String"
    },
    data: [selectedColName]
  };

  var filteredProfilingMetrics = getChartDataFromResult(profilingResult, [
    filter
  ]);
  if (filteredProfilingMetrics.length > 0) {
    var selectedDataType = filteredProfilingMetrics.find(function (series) {
      return series.nuoField.FieldName.toLowerCase() === "column_type";
    }).data[0];
    var totalCount = filteredProfilingMetrics.find(function (series) {
      return series.nuoField.FieldName.toLowerCase() === "total_count";
    }).data[0];
    var emptyCount = filteredProfilingMetrics.find(function (series) {
      return series.nuoField.FieldName.toLowerCase() === "empty_count";
    }).data[0];
    var uniqueCount = filteredProfilingMetrics.find(function (series) {
      return series.nuoField.FieldName.toLowerCase() === "unique_count";
    }).data[0];
    if (selectedDataType && totalCount && emptyCount && uniqueCount) {
      var colProfilingMetrics =
        "<div id='dataGridColProfilingMetrics' class='dataGridColProfilingMetrics'>";
      colProfilingMetrics +=
        "<div id='dataGridSummaryColHeader' class='dataGridSummaryColHeader'>";
      colProfilingMetrics += getDataTypeDivTag(
        selectedDataType,
        "dataGridSummaryColDataType"
      );
      colProfilingMetrics +=
        "<div class='dataGridSummaryColName'>" + selectedColName + "</div>";
      colProfilingMetrics += "</div>";

      colProfilingMetrics += "<div class='dataGridColMetric'>";
      colProfilingMetrics +=
        "<div class='dataGridColMetricName'>Total Count</div>";
      colProfilingMetrics += "<div class='dataGridColMetricValue2'></div>";
      colProfilingMetrics +=
        "<div class='dataGridColMetricValue'>" + totalCount + "</div>";
      colProfilingMetrics += "</div>";

      //Empty Count
      colProfilingMetrics += "<div class='dataGridColMetric'>";
      colProfilingMetrics +=
        "<div class='dataGridColMetricName'>Empty Count</div>";
      colProfilingMetrics +=
        "<div class='dataGridColMetricValue2'>" +
        ((emptyCount / totalCount) * 100.0).toFixed(2) +
        "%</div>";
      colProfilingMetrics +=
        "<div class='dataGridColMetricValue'>" + emptyCount + "</div>";
      colProfilingMetrics += "</div>";

      //Unique Count
      colProfilingMetrics += "<div class='dataGridColMetric'>";
      colProfilingMetrics +=
        "<div class='dataGridColMetricName'>Unique Count</div>";
      colProfilingMetrics +=
        "<div class='dataGridColMetricValue2'>" +
        ((uniqueCount / totalCount) * 100.0).toFixed(2) +
        "%</div>";
      colProfilingMetrics +=
        "<div class='dataGridColMetricValue'>" + uniqueCount + "</div>";
      colProfilingMetrics += "</div>";

      filteredProfilingMetrics
        .filter(function (series) {
          return (
            [
              "column_name",
              "column_type",
              "total_count",
              "empty_count",
              "unique_count"
            ].indexOf(series.nuoField.FieldName.toLowerCase()) < 0
          );
        })
        .forEach(function (series) {
          var metricName = series.nuoField.FieldName;
          var metricValue = series.data[0];
          if (
            metricValue &&
            (metricValue + "").trim().length > 0 &&
            metricValue.toLowerCase() !== "null"
          ) {
            colProfilingMetrics += "<div class='dataGridColMetric'>";
            colProfilingMetrics +=
              "<div class='dataGridColMetricName'>" +
              metricName.replace("_", " ") +
              "</div>";
            colProfilingMetrics +=
              "<div class='dataGridColMetricValue2'></div>";
            colProfilingMetrics +=
              "<div class='dataGridColMetricValue'>" +
              parseFloat(metricValue).toFixed(2) +
              "</div>";
            colProfilingMetrics += "</div>";
          }
        });
      colProfilingMetrics += "</div>";

      var histogramSeries = getChartDataFromResult(result, chartFilters).find(
        function (ele) {
          return (
            ele.nuoField.FieldName.toLowerCase() ===
            selectedColName.toLowerCase()
          );
        }
      );

      if (
        histogramSeries &&
        histogramSeries.data &&
        histogramSeries.data.length > 0
      ) {
        var colSummaryDivTag =
          "<div id='dataGridColSummary' class='dataGridColSummary'>";
        colSummaryDivTag +=
          "<div id='dataGridColSummaryTop' class='dataGridColSummaryTop'>";
        colSummaryDivTag += colProfilingMetrics;
        colSummaryDivTag +=
          "<div id='dataGridBoxPlotContainer' class='dataGridBoxPlotContainer'>";
        colSummaryDivTag +=
          "<div id='dataGridBoxPlotHeader' class='dataGridBoxPlotHeader'>Distribution</div>";
        colSummaryDivTag +=
          "<div id='dataGridBoxPlot' class='dataGridBoxPlot'></div>";
        colSummaryDivTag += "</div>";
        if (histogramSeries.nuoField.DataType.toLowerCase() === "string") {
          var patternDivTag =
            "<div id='dataGridColSummaryPatternContainer' class='dataGridColSummaryPatternContainer'> Pattern";
          patternDivTag +=
            "<div id='dataGridColSummaryPattern' class='dataGridColSummaryPattern'>";
          if (pattern) {
            var filteredPatterns = pattern.Data.filter(function (row) {
              return row[0].toLowerCase() === selectedColName.toLowerCase();
            });
            if (filteredPatterns && filteredPatterns.length > 0) {
              filteredPatterns.forEach(function (row) {
                var pattern = row[2];
                var count = parseFloat(row[3]);
                if (pattern && pattern.length > 0 && count && !isNaN(count)) {
                  patternDivTag += "<div class='w3-progress-container'>";

                  ////
                  patternDivTag +=
                    "<div class='w3-progressbar w3-light-blue' style='width:";
                  patternDivTag +=
                    getReadableNumber((count / totalCount) * 100.0) + "%;'>";
                  //
                  patternDivTag +=
                    "<div class='dataGridColSummaryPatternName'>";
                  patternDivTag += pattern.replace(/\s+/g, "S");
                  patternDivTag += "</div>";
                  //
                  patternDivTag += "</div>";
                  ////
                  patternDivTag += "</div>";
                }
              });
            }
          }
          //
          //

          patternDivTag += "</div>";
          colSummaryDivTag += patternDivTag;
          colSummaryDivTag += "</div>";
          colSummaryDivTag += "</div>";
        } else {
          colSummaryDivTag += "</div>";
          colSummaryDivTag +=
            "<div id='dataGridTableColumn_1' class='dataGridColHistogram'></div>";
        }
        colSummaryDivTag += "</div>";

        if ($("#dataGridColSummary")) {
          $("#dataGridColSummary").remove();
        }
        $("#columnSummaryWindow>div").append(colSummaryDivTag);

        updatePatternWidth();
        drawBoxPlot(histogramSeries, "dataGridBoxPlot");

        if (histogramSeries.nuoField.DataType.toLowerCase() !== "string") {
          drawHistogram("dataGridTableColumn_", histogramSeries, 1);
          $("#dataGridColSummary").height(480);
        } else {
          $("#dataGridColSummary").height(420);
        }
      }
      $("#columnSummaryWindow").fadeIn();
      $("#closeColumnSummaryWindow").off("click");
      $("#closeColumnSummaryWindow").on("click", function (e) {
        e.preventDefault();
        $("#columnSummaryWindow").fadeOut();
      });
    }
  }
}

function updatePatternWidth() {
  $(".dataGridColSummaryPattern>div>div").each(function (i, ele) {
    if (ele.attributes["fill-percent"]) {
      ele.style.width = ele.attributes["fill-percent"].value + "%";
    }
  });
}
function getDataTypeDivTag(dataType, className) {
  if (
    ["int64", "integer", "float64", "float"].indexOf(dataType.toLowerCase()) >=
    0
  ) {
    return "<div class='" + className + " floatImg'>#</div>";
  } else if (dataType.toLowerCase() === "string") {
    return "<div class='" + className + " stringImg'>Abc</div>";
  } else if (dataType.toLowerCase() === "boolean") {
    return "<div class='" + className + " booleanImg'>Y/N</div>";
  } else if (
    dataType.toLowerCase() === "time" ||
    dataType.toLowerCase() === "timestamp"
  ) {
    return "<div class='" + className + " timeImg'>&#xf017;</div>";
  } else if (dataType.toLowerCase() === "date") {
    return "<div class='" + className + " calendarImg'>&#xf073;</div>";
  }
}

function drawHistogram(divIdPrefix, series, i) {
  // if(typeof colIndex === 'undefined' || colIndex == i){
  // $('#dataGridTableColumn_'+i).attr("width",$('#col-'+colIndex).attr("width"));
  // $('#dataGridTableColumn_'+i).attr("height",$('#col-'+colIndex).attr("width"));
  var trace1 = {
    x: series.data,
    name: series.nuoField.FieldName,
    autobinx: false,
    histnorm: "count",
    marker: {
      color: "#3cb043",
      line: {
        color: "#3cb043",
        width: 1
      }
    },
    opacity: 0.85,
    type: "histogram"
  };

  var data = [trace1];
  var layout = {
    bargap: 0.05,
    bargroupgap: 0.2,
    showlegend: false,
    xaxis: {
      showticklabels: false
    },
    margin: {
      l: 20,
      r: 5,
      b: 25,
      t: 5,
      pad: 0
    }
  };
  Plotly.newPlot(divIdPrefix + i, data, layout, {
    showLink: false,
    displaylogo: false,
    displayModeBar: false,
    responsive: true
  });
  // }
}

function drawBoxPlot(series, divId) {
  // if(typeof colIndex === 'undefined' || colIndex == i){
  // $('#dataGridTableColumn_'+i).attr("width",$('#col-'+colIndex).attr("width"));
  // $('#dataGridTableColumn_'+i).attr("height",$('#col-'+colIndex).attr("width"));
  var trace1 = {
    x: series.nuoField.FieldName,
    y: series.data,
    boxpoints: "all",
    jitter: 0.3,
    pointpos: -1.8,
    type: "box",
    reversescale: true,
    marker: {
      color: "#3D9970"
    },
    opacity: 1
  };

  var data = [trace1];
  var layout = {
    // bargap: 0.05,
    // bargroupgap: 0.2,
    showlegend: false,
    xaxis: {
      showticklabels: false
    },
    margin: {
      l: 100,
      r: 5,
      b: 25,
      t: 5,
      pad: 0
    }
  };
  Plotly.newPlot(divId, data, layout, {
    showLink: false,
    displaylogo: false,
    displayModeBar: false,
    responsive: true
  });
  // }
}

function addDataGridButtonHandler() {
  $(".dataGridMoreButton").off("click");
  $(".dataGridMoreButton").on("click", function (e) {
    e.preventDefault();
    $(e.target)
      .parent()
      .next()[0]
      .classList.toggle("show");
  });

  $(".dataGridDetailsButton").off("click");
  $(".dataGridDetailsButton").on("click", function (e) {
    e.preventDefault();
    handleDataGridDetailsButtonEvent("#dataGrid", e);
  });

  $(".staticDataGridDetailsButton").off("click");
  $(".staticDataGridDetailsButton").on("click", function (e) {
    e.preventDefault();
    handleDataGridDetailsButtonEvent("#staticDataGrid", e);
  });

  $(".dataGridRenameButton").off("click");
  $(".dataGridRenameButton").on("click", function (e) {
    e.preventDefault();
    handleDataGridRenameButtonEvent("#dataGrid", e);
  });

  $(".dataGridDeleteButton").off("click");
  $(".dataGridDeleteButton").on("click", function (e) {
    e.preventDefault();
    handleDataGridDeleteButtonEvent("#dataGrid", e);
  });

  $(".dataGridReplaceButton").off("click");
  $(".dataGridReplaceButton").on("click", function (e) {
    e.preventDefault();
    handleDataGridReplaceButtonEvent("#dataGrid", e);
  });

  $(".dataGridMergeButton").off("click");
  $(".dataGridMergeButton").on("click", function (e) {
    e.preventDefault();
    handleDataGridMergeButtonEvent("#dataGrid", e);
  });

  $(".dataGridSplitButton").off("click");
  $(".dataGridSplitButton").on("click", function (e) {
    e.preventDefault();
    handleDataGridSplitButtonEvent("#dataGrid", e);
  });

  $(".dataGridColumnToRowsButton").off("click");
  $(".dataGridColumnToRowsButton").on("click", function (e) {
    e.preventDefault();
    handleDataGridColumnToRowsButtonEvent("#dataGrid", e);
  });

  $(".dataGridRowsToColumnsButton").off("click");
  $(".dataGridRowsToColumnsButton").on("click", function (e) {
    e.preventDefault();
    handleDataGridRowsToColumnsButtonEvent("#dataGrid", e);
  });

  $(".dataGridSubstringButton").off("click");
  $(".dataGridSubstringButton").on("click", function (e) {
    e.preventDefault();
    handleDataGridSubstringButtonEvent("#dataGrid", e);
  });

  $(".dataGridTrimButton").off("click");
  $(".dataGridTrimButton").on("click", function (e) {
    e.preventDefault();
    handleDataGridTrimButtonEvent("#dataGrid", e);
  });

  $(".dataGridToStringButton").off("click");
  $(".dataGridToStringButton").on("click", function (e) {
    e.preventDefault();
    handleDataGridToStringButtonEvent("#dataGrid", e);
  });

  $(".dataGridToIntegerButton").off("click");
  $(".dataGridToIntegerButton").on("click", function (e) {
    e.preventDefault();
    handleDataGridToIntegerButtonEvent("#dataGrid", e);
  });

  $(".dataGridToDateButton").off("click");
  $(".dataGridToDateButton").on("click", function (e) {
    e.preventDefault();
    handleDataGridToDateButtonEvent("#dataGrid", e);
  });

  $(".dataGridDuplicateColButton").off("click");
  $(".dataGridDuplicateColButton").on("click", function (e) {
    e.preventDefault();
    handleDataGridDuplicateButtonEvent("#dataGrid", e);
  });
}

function handleDataGridDetailsButtonEvent(selectorPrefix, e) {
  var analysisId = $(e.target)
    .closest(".dataGridWindow,.staticDataGridWindow")
    .attr("analysisId");

  if (analysisId) {
    var selector = "";
    if ($("#dataGridWindow")[0].style.display === "none") {
      selector = selectorPrefix + analysisId + " .dataGridColCheckbox";
    } else {
      selector = "#dataGridWindow .dataGridColCheckbox";
    }
    loadColumnSummaryWindow(analysisId, selector);
  }
}

function handleDataGridRenameButtonEvent(selectorPrefix, e) {
  var analysisId = $(e.target)
    .closest(".dataGridWindow,.staticDataGridWindow")
    .attr("analysisId");

  if (analysisId) {
    var selector = "";
    if ($("#dataGridWindow")[0].style.display === "none") {
      selector = selectorPrefix + analysisId + " .dataGridColCheckbox";
    } else {
      selector = "#dataGridWindow .dataGridColCheckbox";
    }
    $("#userInputWindow > div > div > h3").text(
      "How do you want to rename the column?"
    );
    $("#userInputMessage").text("New column name");
    $("#userInputTextbox").attr('placeholder', "New column name");

    $("#userInputMessage2").hide();
    $("#userInputTextboxContainer2").hide();

    $("#userInputWindow").fadeIn();
    $("#userInputSubmitButton").off("click");
    $("#userInputSubmitButton").on("click", function (e) {
      e.preventDefault();
      var newColumnName = $("#userInputTextbox").val();
      $("#userInputWindow").fadeOut();
      renameColumn(analysisId, selector, newColumnName);
    });
  }
}

function handleDataGridDeleteButtonEvent(selectorPrefix, e) {
  var analysisId = $(e.target)
    .closest(".dataGridWindow,.staticDataGridWindow")
    .attr("analysisId");

  if (analysisId) {
    var selector = "";
    if ($("#dataGridWindow")[0].style.display === "none") {
      selector = selectorPrefix + analysisId + " .dataGridColCheckbox";
    } else {
      selector = "#dataGridWindow .dataGridColCheckbox";
    }
    $("#userInputWindow > div > div > h3").text(
      "Are you sure that you want to delete the selected columns?"
    );
    // $("#userInputMessage").text("Search for");
    // $("#userInputTextbox").attr('placeholder', "Search for");

    $("#userInputMessage").hide();
    $("#userInputTextboxContainer").hide();

    $("#userInputMessage2").hide();
    $("#userInputTextboxContainer2").hide();

    $("#userInputWindow").fadeIn();
    $("#userInputSubmitButton").off("click");
    $("#userInputSubmitButton").on("click", function (e) {
      e.preventDefault();
      $("#userInputWindow").fadeOut();
      deleteColumns(analysisId, selector);
    });
  }
}

function handleStoreGridDeleteButtonEvent() {
  var selectedRows = getGridSelectedRows("storageExplorerGrid");
  $("#userInputWindow1 > div > div > h3").text(
    "You have selected " + selectedRows.length + " tables. Are you sure you want to delete them all?"
  );

  $("#userInputMessage1").hide();
  $("#userInputTextboxContainer1").hide();

  $("#userInputMessage21").hide();
  $("#userInputTextboxContainer21").hide();

  $("#userInputWindow1").fadeIn();
  $("#userInputSubmitButton1").off("click");
  $("#userInputSubmitButton1").on("click", function (e) {
    e.preventDefault();
    $("#userInputWindow1").fadeOut();
    deleteTableSelectionHandler();
  });
}

function handleDataGridDuplicateButtonEvent(selectorPrefix, e) {
  var analysisId = $(e.target)
    .closest(".dataGridWindow,.staticDataGridWindow")
    .attr("analysisId");

  if (analysisId) {
    var selector = "";
    if ($("#dataGridWindow")[0].style.display === "none") {
      selector = selectorPrefix + analysisId + " .dataGridColCheckbox";
    } else {
      selector = "#dataGridWindow .dataGridColCheckbox";
    }
    $("#userInputWindow > div > div > h3").text(
      "Are you sure that you want to duplicate the selected columns?"
    );
    // $("#userInputMessage").text("Search for");
    // $("#userInputTextbox").attr('placeholder', "Search for");

    $("#userInputMessage").hide();
    $("#userInputTextboxContainer").hide();

    $("#userInputMessage2").hide();
    $("#userInputTextboxContainer2").hide();

    $("#userInputWindow").fadeIn();
    $("#userInputSubmitButton").off("click");
    $("#userInputSubmitButton").on("click", function (e) {
      e.preventDefault();
      $("#userInputWindow").fadeOut();
      duplicateColumns(analysisId, selector);
    });
  }
}

function handleDataGridTrimButtonEvent(selectorPrefix, e) {
  var analysisId = $(e.target)
    .closest(".dataGridWindow,.staticDataGridWindow")
    .attr("analysisId");

  if (analysisId) {
    var selector = "";
    if ($("#dataGridWindow")[0].style.display === "none") {
      selector = selectorPrefix + analysisId + " .dataGridColCheckbox";
    } else {
      selector = "#dataGridWindow .dataGridColCheckbox";
    }
    $("#userInputWindow > div > div > h3").text(
      "Are you sure that you want to trim the selected columns?"
    );
    // $("#userInputMessage").text("Search for");
    // $("#userInputTextbox").attr('placeholder', "Search for");

    $("#userInputMessage").hide();
    $("#userInputTextboxContainer").hide();

    $("#userInputMessage2").hide();
    $("#userInputTextboxContainer2").hide();

    $("#userInputWindow").fadeIn();
    $("#userInputSubmitButton").off("click");
    $("#userInputSubmitButton").on("click", function (e) {
      e.preventDefault();
      $("#userInputWindow").fadeOut();
      trimColumns(analysisId, selector);
    });
  }
}

function handleDataGridToDateButtonEvent(selectorPrefix, e) {
  var analysisId = $(e.target)
    .closest(".dataGridWindow,.staticDataGridWindow")
    .attr("analysisId");

  if (analysisId) {
    var selector = "";
    if ($("#dataGridWindow")[0].style.display === "none") {
      selector = selectorPrefix + analysisId + " .dataGridColCheckbox";
    } else {
      selector = "#dataGridWindow .dataGridColCheckbox";
    }
    $("#userInputWindow > div > div > h3").text(
      "Are you sure that you want to convert the selected columns to date?"
    );
    // $("#userInputMessage").text("Search for");
    // $("#userInputTextbox").attr('placeholder', "Search for");

    $("#userInputMessage").hide();
    $("#userInputTextboxContainer").hide();

    $("#userInputMessage2").hide();
    $("#userInputTextboxContainer2").hide();

    $("#userInputWindow").fadeIn();
    $("#userInputSubmitButton").off("click");
    $("#userInputSubmitButton").on("click", function (e) {
      e.preventDefault();
      $("#userInputWindow").fadeOut();
      toDateColumns(analysisId, selector);
    });
  }
}

function handleDataGridToStringButtonEvent(selectorPrefix, e) {
  var analysisId = $(e.target)
    .closest(".dataGridWindow,.staticDataGridWindow")
    .attr("analysisId");

  if (analysisId) {
    var selector = "";
    if ($("#dataGridWindow")[0].style.display === "none") {
      selector = selectorPrefix + analysisId + " .dataGridColCheckbox";
    } else {
      selector = "#dataGridWindow .dataGridColCheckbox";
    }
    $("#userInputWindow > div > div > h3").text(
      "Are you sure that you want to convert the selected columns to string?"
    );
    // $("#userInputMessage").text("Search for");
    // $("#userInputTextbox").attr('placeholder', "Search for");

    $("#userInputMessage").hide();
    $("#userInputTextboxContainer").hide();

    $("#userInputMessage2").hide();
    $("#userInputTextboxContainer2").hide();

    $("#userInputWindow").fadeIn();
    $("#userInputSubmitButton").off("click");
    $("#userInputSubmitButton").on("click", function (e) {
      e.preventDefault();
      $("#userInputWindow").fadeOut();
      toStringColumns(analysisId, selector);
    });
  }
}

function handleDataGridToIntegerButtonEvent(selectorPrefix, e) {
  var analysisId = $(e.target)
    .closest(".dataGridWindow,.staticDataGridWindow")
    .attr("analysisId");

  if (analysisId) {
    var selector = "";
    if ($("#dataGridWindow")[0].style.display === "none") {
      selector = selectorPrefix + analysisId + " .dataGridColCheckbox";
    } else {
      selector = "#dataGridWindow .dataGridColCheckbox";
    }
    $("#userInputWindow > div > div > h3").text(
      "Are you sure that you want to convert the selected columns to integer?"
    );
    // $("#userInputMessage").text("Search for");
    // $("#userInputTextbox").attr('placeholder', "Search for");

    $("#userInputMessage").hide();
    $("#userInputTextboxContainer").hide();

    $("#userInputMessage2").hide();
    $("#userInputTextboxContainer2").hide();

    $("#userInputWindow").fadeIn();
    $("#userInputSubmitButton").off("click");
    $("#userInputSubmitButton").on("click", function (e) {
      e.preventDefault();
      $("#userInputWindow").fadeOut();
      toIntegerColumns(analysisId, selector);
    });
  }
}

function handleDataGridReplaceButtonEvent(selectorPrefix, e) {
  var analysisId = $(e.target)
    .closest(".dataGridWindow,.staticDataGridWindow")
    .attr("analysisId");

  if (analysisId) {
    var selector = "";
    if ($("#dataGridWindow")[0].style.display === "none") {
      selector = selectorPrefix + analysisId + " .dataGridColCheckbox";
    } else {
      selector = "#dataGridWindow .dataGridColCheckbox";
    }
    $("#userInputWindow > div > div > h3").text("What do you want to replace?");

    $("#userInputMessage").show();
    $("#userInputTextboxContainer").show();
    $("#userInputMessage").text("Search for");
    $("#userInputTextbox").attr('placeholder', "Search for");
    $("#userInputMessage2").text("Replace with");
    $("#userInputTextbox2").attr('placeholder', "Replace with");

    $("#userInputMessage2").show();
    $("#userInputTextboxContainer2").show();

    $("#userInputWindow").fadeIn();
    $("#userInputSubmitButton").off("click");
    $("#userInputSubmitButton").on("click", function (e) {
      e.preventDefault();
      var saerchTerm = $("#userInputTextbox").val();
      var replaceTerm = $("#userInputTextbox2").val();
      $("#userInputWindow").fadeOut();
      replaceColumn(analysisId, selector, saerchTerm, replaceTerm);
    });
  }
}

function handleDataGridMergeButtonEvent(selectorPrefix, e) {
  var analysisId = $(e.target)
    .closest(".dataGridWindow,.staticDataGridWindow")
    .attr("analysisId");

  if (analysisId) {
    var selector = "";
    if ($("#dataGridWindow")[0].style.display === "none") {
      selector = selectorPrefix + analysisId + " .dataGridColCheckbox";
    } else {
      selector = "#dataGridWindow .dataGridColCheckbox";
    }
    $("#userInputWindow > div > div > h3").text(
      "What should be the result column name?"
    );

    $("#userInputMessage").show();
    $("#userInputTextboxContainer").show();
    $("#userInputMessage").text("Result column name");
    $("#userInputTextbox").attr('placeholder', "Result column name");

    $("#userInputMessage2").text("Delimiter");
    $("#userInputTextbox2").attr('placeholder', "Delimiter");
    $("#userInputMessage2").show();
    $("#userInputTextboxContainer2").show();

    $("#userInputWindow").fadeIn();
    $("#userInputSubmitButton").off("click");
    $("#userInputSubmitButton").on("click", function (e) {
      e.preventDefault();
      var targetColumnName = $("#userInputTextbox").val();
      var delimiter = $("#userInputTextbox2").val();
      $("#userInputWindow").fadeOut();
      mergeColumns(analysisId, selector, targetColumnName, delimiter);
    });
  }
}

function handleDataGridSplitButtonEvent(selectorPrefix, e) {
  var analysisId = $(e.target)
    .closest(".dataGridWindow,.staticDataGridWindow")
    .attr("analysisId");

  if (analysisId) {
    var selector = "";
    if ($("#dataGridWindow")[0].style.display === "none") {
      selector = selectorPrefix + analysisId + " .dataGridColCheckbox";
    } else {
      selector = "#dataGridWindow .dataGridColCheckbox";
    }
    $("#userInputWindow > div > div > h3").text(
      "What should be the delimiter to split the column?"
    );

    $("#userInputMessage").text("Delimiter");
    $("#userInputTextbox").attr('placeholder', "Delimiter");
    $("#userInputMessage").show();
    $("#userInputTextboxContainer").show();

    $("#userInputMessage2").hide();
    $("#userInputTextboxContainer2").hide();

    $("#userInputWindow").fadeIn();
    $("#userInputSubmitButton").off("click");
    $("#userInputSubmitButton").on("click", function (e) {
      e.preventDefault();
      var delimiter = $("#userInputTextbox").val();
      $("#userInputWindow").fadeOut();
      splitColumn(analysisId, selector, delimiter);
    });
  }
}

function handleDataGridColumnToRowsButtonEvent(selectorPrefix, e) {
  var analysisId = $(e.target)
    .closest(".dataGridWindow,.staticDataGridWindow")
    .attr("analysisId");

  if (analysisId) {
    var selector = "";
    if ($("#dataGridWindow")[0].style.display === "none") {
      selector = selectorPrefix + analysisId + " .dataGridColCheckbox";
    } else {
      selector = "#dataGridWindow .dataGridColCheckbox";
    }
    $("#userInputWindow > div > div > h3").text(
      "How do you want to convert column to rows?"
    );

    $("#userInputMessage").text("Result column name");
    $("#userInputTextbox").attr('placeholder', "Result column name");
    $("#userInputMessage").show();
    $("#userInputTextboxContainer").show();

    $("#userInputMessage2").text("Column that contains values");
    $("#userInputTextbox").attr('placeholder', "Column that contains values");
    $("#userInputMessage2").show();
    $("#userInputTextboxContainer2").show();

    $("#userInputWindow").fadeIn();
    $("#userInputSubmitButton").off("click");
    $("#userInputSubmitButton").on("click", function (e) {
      e.preventDefault();
      var targetColumnName = $("#userInputTextbox").val();
      var valueColumnName = $("#userInputTextbox2").val();
      $("#userInputWindow").fadeOut();
      columnToRows(analysisId, selector, targetColumnName, valueColumnName);
    });
  }
}

function handleDataGridRowsToColumnsButtonEvent(selectorPrefix, e) {
  var analysisId = $(e.target)
    .closest(".dataGridWindow,.staticDataGridWindow")
    .attr("analysisId");

  if (analysisId) {
    var selector = "";
    if ($("#dataGridWindow")[0].style.display === "none") {
      selector = selectorPrefix + analysisId + " .dataGridColCheckbox";
    } else {
      selector = "#dataGridWindow .dataGridColCheckbox";
    }
    $("#userInputWindow > div > div > h3").text(
      "How do you want to convert rows to columns?"
    );

    $("#userInputMessage").text("Column that contains values");
    $("#userInputTextbox").attr('placeholder', "Column that contains values");
    $("#userInputMessage").show();
    $("#userInputTextboxContainer").show();

    $("#userInputMessage2").hide();
    $("#userInputTextboxContainer2").hide();

    $("#userInputWindow").fadeIn();
    $("#userInputSubmitButton").off("click");
    $("#userInputSubmitButton").on("click", function (e) {
      e.preventDefault();
      // var targetColumnName = $("#userInputTextbox").val();
      var valueColumnName = $("#userInputTextbox2").val();
      $("#userInputWindow").fadeOut();
      rowsToColumns(analysisId, selector, valueColumnName);
    });
  }
}

function handlePreviewOperationPromise(nuoQueryResponse, tableName) {
  tablePreviews[tableName] = null;
  previewTable(tableName);
  // var result = nuoQueryResponse.Result;
  // var profilingResult = nuoQueryResponse.ProfilingResult;
  // var pattern = nuoQueryResponse.Pattern;
  // var seriesGroup =
  // 	$(result.Metadata).map(function (index, nuoField) {
  // 		var series = {
  // 			nuoField: nuoField,
  // 			data: result.Data.map(function (row) { return row[index]; })
  // 		}
  // 		return series
  // 	});
  // tablePreviews[tableName] = {
  // 	Result: result,
  // 	ProfilingResult: profilingResult,
  // 	Pattern: pattern
  // };
  // drawDataGrid(analysisId, seriesGroup);
}

function deleteColumns(analysisId, selector) {
  var selectedColumns = Object.values($(selector))
    .filter(function (ele) {
      return ele.checked;
    })
    .map(function (ele) {
      return JSON.parse(unescape(ele.value));
    });
  if (selectedColumns && selectedColumns.length > 0) {
    var fieldCombinations = selectedColumns.map(function (nuoField) {
      var combination = {
        SourceField: nuoField
      };

      return combination;
    });
    $("#ajaxLoader").addClass("is-active");
    directCall("rt167", "NuoGridFeatureOptions", {
      NuoFieldCombinations: fieldCombinations
    }).done(function (data) {
      if (data.StatusCode && data.StatusCode === 200) {
        var nuoQueryResponse = data.Content;
        refreshNuoTables(function () {
          handlePreviewOperationPromise(
            nuoQueryResponse,
            selectedColumns[0].EntityName
          );
          $("#ajaxLoader").removeClass("is-active");
        });
      } else {
        $("#ajaxLoader").removeClass("is-active");
        //window.location.reload();
      }
    });
  }
}

function duplicateColumns(analysisId, selector) {
  var selectedColumns = Object.values($(selector))
    .filter(function (ele) {
      return ele.checked;
    })
    .map(function (ele) {
      return JSON.parse(unescape(ele.value));
    });
  if (selectedColumns && selectedColumns.length > 0) {
    var fieldCombinations = selectedColumns.map(function (nuoField) {
      var combination = {
        SourceField: nuoField
      };
      return combination;
    });

    $("#ajaxLoader").addClass("is-active");
    directCall("rt199", "NuoGridFeatureOptions", {
      NuoFieldCombinations: fieldCombinations
    })
      .done(function (data) {
        handleResponse(data);
      })
      .fail(function () {
        if (sessionId === "X") {
          var data = {
            StatusCode: 200,
            Status: "OK",
            Content: "Successfully deleted columns."
          };
          handleResponse(data);
        }
      });
    var handleResponse = function (data) {
      if (data.StatusCode && data.StatusCode === 200) {
        var nuoQueryResponse = data.Content;
        refreshNuoTables(function () {
          handlePreviewOperationPromise(
            nuoQueryResponse,
            selectedColumns[0].EntityName
          );
          $("#ajaxLoader").removeClass("is-active");
        });
      } else {
        $("#ajaxLoader").removeClass("is-active");
        //window.location.reload();
      }
    };
  }
}

function trimColumns(analysisId, selector) {
  var selectedColumns = Object.values($(selector))
    .filter(function (ele) {
      return ele.checked;
    })
    .map(function (ele) {
      return JSON.parse(unescape(ele.value));
    });
  if (selectedColumns && selectedColumns.length > 0) {
    var fieldCombinations = selectedColumns.map(function (nuoField) {
      var combination = {
        SourceField: nuoField
      };

      return combination;
    });
    $("#ajaxLoader").addClass("is-active");
    directCall("rt181", "NuoGridFeatureOptions", {
      NuoFieldCombinations: fieldCombinations
    })
      .done(function (data) {
        handleResponse(data);
      })
      .fail(function () {
        if (sessionId === "X") {
          var data = {
            StatusCode: 200,
            Status: "OK",
            Content: "Successfully deleted columns."
          };
          handleResponse(data);
        }
      });
    var handleResponse = function (data) {
      if (data.StatusCode && data.StatusCode === 200) {
        var nuoQueryResponse = data.Content;
        refreshNuoTables(function () {
          handlePreviewOperationPromise(
            nuoQueryResponse,
            selectedColumns[0].EntityName
          );
          $("#ajaxLoader").removeClass("is-active");
        });
      } else {
        $("#ajaxLoader").removeClass("is-active");
        //window.location.reload();
      }
    };
  }
}

function toStringColumns(analysisId, selector) {
  var selectedColumns = Object.values($(selector))
    .filter(function (ele) {
      return ele.checked;
    })
    .map(function (ele) {
      return JSON.parse(unescape(ele.value));
    });
  if (selectedColumns && selectedColumns.length > 0) {
    var fieldCombinations = selectedColumns.map(function (nuoField) {
      var combination = {
        SourceField: nuoField
      };

      return combination;
    });
    $("#ajaxLoader").addClass("is-active");
    directCall("rt193", "NuoGridFeatureOptions", {
      NuoFieldCombinations: fieldCombinations
    })
      .done(function (data) {
        handleResponse(data);
      })
      .fail(function () {
        if (sessionId === "X") {
          var data = {
            StatusCode: 200,
            Status: "OK",
            Content: "Successfully deleted columns."
          };
          handleResponse(data);
        }
      });
    var handleResponse = function (data) {
      if (data.StatusCode && data.StatusCode === 200) {
        var nuoQueryResponse = data.Content;
        refreshNuoTables(function () {
          handlePreviewOperationPromise(
            nuoQueryResponse,
            selectedColumns[0].EntityName
          );
          $("#ajaxLoader").removeClass("is-active");
        });
      } else {
        $("#ajaxLoader").removeClass("is-active");
        //window.location.reload();
      }
    };
  }
}

function toDateColumns(analysisId, selector) {
  var selectedColumns = Object.values($(selector))
    .filter(function (ele) {
      return ele.checked;
    })
    .map(function (ele) {
      return JSON.parse(unescape(ele.value));
    });
  if (selectedColumns && selectedColumns.length > 0) {
    var fieldCombinations = selectedColumns.map(function (nuoField) {
      var combination = {
        SourceField: nuoField
      };

      return combination;
    });
    $("#ajaxLoader").addClass("is-active");
    directCall("rt191", "NuoGridFeatureOptions", {
      NuoFieldCombinations: fieldCombinations
    })
      .done(function (data) {
        handleResponse(data);
      })
      .fail(function () {
        if (sessionId === "X") {
          var data = {
            StatusCode: 200,
            Status: "OK",
            Content: "Successfully deleted columns."
          };
          handleResponse(data);
        }
      });
    var handleResponse = function (data) {
      if (data.StatusCode && data.StatusCode === 200) {
        var nuoQueryResponse = data.Content;
        refreshNuoTables(function () {
          handlePreviewOperationPromise(
            nuoQueryResponse,
            selectedColumns[0].EntityName
          );
          $("#ajaxLoader").removeClass("is-active");
        });
      } else {
        $("#ajaxLoader").removeClass("is-active");
        //window.location.reload();
      }
    };
  }
}

function toIntegerColumns(analysisId, selector) {
  var selectedColumns = Object.values($(selector))
    .filter(function (ele) {
      return ele.checked;
    })
    .map(function (ele) {
      return JSON.parse(unescape(ele.value));
    });
  if (selectedColumns && selectedColumns.length > 0) {
    var fieldCombinations = selectedColumns.map(function (nuoField) {
      var combination = {
        SourceField: nuoField
      };

      return combination;
    });
    $("#ajaxLoader").addClass("is-active");
    directCall("rt197", "NuoGridFeatureOptions", {
      NuoFieldCombinations: fieldCombinations
    })
      .done(function (data) {
        handleResponse(data);
      })
      .fail(function () {
        if (sessionId === "X") {
          var data = {
            StatusCode: 200,
            Status: "OK",
            Content: "Successfully deleted columns."
          };
          handleResponse(data);
        }
      });
    var handleResponse = function (data) {
      if (data.StatusCode && data.StatusCode === 200) {
        var nuoQueryResponse = data.Content;
        refreshNuoTables(function () {
          handlePreviewOperationPromise(
            nuoQueryResponse,
            selectedColumns[0].EntityName
          );
          $("#ajaxLoader").removeClass("is-active");
        });
      } else {
        $("#ajaxLoader").removeClass("is-active");
        //window.location.reload();
      }
    };
  }
}

function renameColumn(analysisId, selector, newName) {
  var selectedColumns = Object.values($(selector))
    .filter(function (ele) {
      return ele.checked;
    })
    .map(function (ele) {
      return JSON.parse(unescape(ele.value));
    });
  if (selectedColumns && selectedColumns.length > 0) {
    var fieldCombinations = selectedColumns.map(function (nuoField) {
      var combination = {
        SourceField: nuoField,
        TargetField: {
          DatasetName: nuoField.DatasetName,
          EntityName: nuoField.EntityName,
          FieldName: newName,
          DataType: nuoField.DataType
        }
      };

      return combination;
    });
    $("#ajaxLoader").addClass("is-active");
    directCall("rt163", "NuoGridFeatureOptions", {
      NuoFieldCombinations: fieldCombinations
    })
      .done(function (data) {
        handleResponse(data);
      })
      .fail(function () {
        if (sessionId === "X") {
          var data = {
            StatusCode: 200,
            Status: "OK",
            Content: "Successfully deleted columns."
          };
          handleResponse(data);
        }
      });
    var handleResponse = function (data) {
      if (data.StatusCode && data.StatusCode === 200) {
        var nuoQueryResponse = data.Content;
        refreshNuoTables(function () {
          handlePreviewOperationPromise(
            nuoQueryResponse,
            selectedColumns[0].EntityName
          );
          $("#ajaxLoader").removeClass("is-active");
        });
      } else {
        $("#ajaxLoader").removeClass("is-active");
        //window.location.reload();
      }
    };
  }
}

function replaceColumn(analysisId, selector, saerchTerm, replaceTerm) {
  var selectedColumns = Object.values($(selector))
    .filter(function (ele) {
      return ele.checked;
    })
    .map(function (ele) {
      return JSON.parse(unescape(ele.value));
    });
  if (selectedColumns && selectedColumns.length > 0) {
    var fieldCombinations = selectedColumns.map(function (nuoField) {
      var combination = {
        SourceField: nuoField
      };

      return combination;
    });
    $("#ajaxLoader").addClass("is-active");
    directCall("rt137", "NuoGridFeatureOptions", {
      NuoFieldCombinations: fieldCombinations,
      SearchTerm: saerchTerm,
      ReplaceTerm: replaceTerm
    })
      .done(function (data) {
        handleResponse(data);
      })
      .fail(function () {
        if (sessionId === "X") {
          var data = {
            StatusCode: 200,
            Status: "OK",
            Content: "Successfully deleted columns."
          };
          handleResponse(data);
        }
      });
    var handleResponse = function (data) {
      if (data.StatusCode && data.StatusCode === 200) {
        var nuoQueryResponse = data.Content;
        handlePreviewOperationPromise(
          nuoQueryResponse,
          selectedColumns[0].EntityName
        );

        $("#ajaxLoader").removeClass("is-active");
      } else {
        $("#ajaxLoader").removeClass("is-active");
        //window.location.reload();
      }
    };
  }
}

function mergeColumns(analysisId, selector, targetColumnName, delimiter) {
  var selectedColumns = Object.values($(selector))
    .filter(function (ele) {
      return ele.checked;
    })
    .map(function (ele) {
      return JSON.parse(unescape(ele.value));
    });
  if (selectedColumns && selectedColumns.length > 0) {
    var fieldCombinations = selectedColumns.map(function (nuoField) {
      var combination = {
        SourceField: nuoField,
        TargetField: {
          DatasetName: nuoField.DatasetName,
          EntityName: nuoField.EntityName,
          FieldName: targetColumnName,
          DataType: nuoField.DataType
        }
      };

      return combination;
    });
    $("#ajaxLoader").addClass("is-active");
    directCall("rt139", "NuoGridFeatureOptions", {
      NuoFieldCombinations: fieldCombinations,
      SearchTerm: delimiter
    })
      .done(function (data) {
        handleResponse(data);
      })
      .fail(function () {
        if (sessionId === "X") {
          var data = {
            StatusCode: 200,
            Status: "OK",
            Content: "Successfully deleted columns."
          };
          handleResponse(data);
        }
      });
    var handleResponse = function (data) {
      if (data.StatusCode && data.StatusCode === 200) {
        var nuoQueryResponse = data.Content;
        refreshNuoTables(function () {
          handlePreviewOperationPromise(
            nuoQueryResponse,
            selectedColumns[0].EntityName
          );
          $("#ajaxLoader").removeClass("is-active");
        });
      } else {
        $("#ajaxLoader").removeClass("is-active");
        //window.location.reload();
      }
    };
  }
}

function splitColumn(analysisId, selector, delimiter) {
  var selectedColumns = Object.values($(selector))
    .filter(function (ele) {
      return ele.checked;
    })
    .map(function (ele) {
      return JSON.parse(unescape(ele.value));
    });
  if (selectedColumns && selectedColumns.length > 0) {
    // alert("I am going to split column " + selectedColumns[0] + " using delimiter " + delimiter);
    var fieldCombinations = selectedColumns.map(function (nuoField) {
      var combination = {
        SourceField: nuoField
      };

      return combination;
    });
    $("#ajaxLoader").addClass("is-active");
    directCall("rt149", "NuoGridFeatureOptions", {
      NuoFieldCombinations: fieldCombinations,
      SearchTerm: delimiter
    })
      .done(function (data) {
        handleResponse(data);
      })
      .fail(function () {
        if (sessionId === "X") {
          var data = {
            StatusCode: 200,
            Status: "OK",
            Content: "Successfully deleted columns."
          };
          handleResponse(data);
        }
      });
    var handleResponse = function (data) {
      if (data.StatusCode && data.StatusCode === 200) {
        var nuoQueryResponse = data.Content;
        refreshNuoTables(function () {
          handlePreviewOperationPromise(
            nuoQueryResponse,
            selectedColumns[0].EntityName
          );
          $("#ajaxLoader").removeClass("is-active");
        });
      } else {
        $("#ajaxLoader").removeClass("is-active");
        //window.location.reload();
      }
    };
  }
}

function columnToRows(analysisId, selector, targetColumnName, valueColumnName) {
  var selectedColumns = Object.values($(selector))
    .filter(function (ele) {
      return ele.checked;
    })
    .map(function (ele) {
      return JSON.parse(unescape(ele.value));
    });
  if (selectedColumns && selectedColumns.length > 0) {
    // alert("I am going to convert column " + selectedColumns[0] + " using values from column " + valueColumnName + " into " + targetColumnName);
    var fieldCombinations = selectedColumns.map(function (nuoField) {
      var combination = {
        SourceField: nuoField,
        TargetField: {
          DatasetName: nuoField.DatasetName,
          EntityName: nuoField.EntityName,
          FieldName: targetColumnName,
          DataType: nuoField.DataType
        },
        ValueField: {
          DatasetName: nuoField.DatasetName,
          EntityName: nuoField.EntityName,
          FieldName: valueColumnName,
          DataType: nuoField.DataType
        }
      };

      return combination;
    });
    $("#ajaxLoader").addClass("is-active");
    directCall("rt151", "NuoGridFeatureOptions", {
      NuoFieldCombinations: fieldCombinations
    })
      .done(function (data) {
        handleResponse(data);
      })
      .fail(function () {
        if (sessionId === "X") {
          var data = {
            StatusCode: 200,
            Status: "OK",
            Content: "Successfully deleted columns."
          };
          handleResponse(data);
        }
      });
    var handleResponse = function (data) {
      if (data.StatusCode && data.StatusCode === 200) {
        var nuoQueryResponse = data.Content;
        refreshNuoTables(function () {
          handlePreviewOperationPromise(
            nuoQueryResponse,
            selectedColumns[0].EntityName
          );
          $("#ajaxLoader").removeClass("is-active");
        });
      } else {
        $("#ajaxLoader").removeClass("is-active");
        //window.location.reload();
      }
    };
  }
}

function rowsToColumns(analysisId, selector, valueColumnName) {
  var selectedColumns = Object.values($(selector))
    .filter(function (ele) {
      return ele.checked;
    })
    .map(function (ele) {
      return JSON.parse(unescape(ele.value));
    });
  if (selectedColumns && selectedColumns.length > 0) {
    // alert("I am going to convert column " + selectedColumns[0] + " using values from column " + valueColumnName + " into " + targetColumnName);
    var fieldCombinations = selectedColumns.map(function (nuoField) {
      var combination = {
        SourceField: nuoField,
        TargetField: {
          DatasetName: nuoField.DatasetName,
          EntityName: nuoField.EntityName,
          FieldName: targetColumnName,
          DataType: nuoField.DataType
        },
        ValueField: {
          DatasetName: nuoField.DatasetName,
          EntityName: nuoField.EntityName,
          FieldName: valueColumnName,
          DataType: nuoField.DataType
        }
      };

      return combination;
    });
    $("#ajaxLoader").addClass("is-active");
    directCall("rt157", "NuoGridFeatureOptions", {
      NuoFieldCombinations: fieldCombinations
    })
      .done(function (data) {
        handleResponse(data);
      })
      .fail(function () {
        if (sessionId === "X") {
          var data = {
            StatusCode: 200,
            Status: "OK",
            Content: "Successfully deleted columns."
          };
          handleResponse(data);
        }
      });
    var handleResponse = function (data) {
      if (data.StatusCode && data.StatusCode === 200) {
        var nuoQueryResponse = data.Content;
        refreshNuoTables(function () {
          handlePreviewOperationPromise(
            nuoQueryResponse,
            selectedColumns[0].EntityName
          );
          $("#ajaxLoader").removeClass("is-active");
        });
      } else {
        $("#ajaxLoader").removeClass("is-active");
        //window.location.reload();
      }
    };
  }
}

function addChartExportHandler() {
  $(".historyChartExportButton").off("click");
  $(".historyChartExportButton").on("click", function (e) {
    e.preventDefault();
    var analysisIdWithCounter = e.target.id.substring(
      e.target.className.split(" ")[0].length
    );

    var canvas = $("#historyAnalysisChart" + analysisIdWithCounter).get(0);
    //creates image
    var canvasImg = canvas.toDataURL("application/pdf", 1.0);

    //creates PDF from img
    var pdf = new jsPDF("landscape");
    pdf.setFontSize(20);
    pdf.text(15, 15, "Sample Chart title");
    pdf.addImage(canvasImg, "JPEG", 20, 20);
    pdf.save("Chart.pdf");
  });
}

function addAnalysisAutoCompleteHandler(analysisId) {
  if (historyAnalyses[analysisId]) {
    autocomplete(
      document.getElementById("historyAnalysisSelectionInput" + analysisId)
    );
    autocomplete(
      document.getElementById("historyAnalysisFilterInput" + analysisId)
    );
  } else {
    alert("ERROR: I could not relate results to matching analysis.");
    return null;
  }
}

function addHistoryScrollEvent() {
  $(window).scroll(function () {
    if (
      !isHistoryLoading &&
      $(window).scrollTop() + $(window).height() > $(document).height() - 10
    ) {
      if (currHistoryStartLineNumber > 1) {
        // readFromHistory();
      }
    }
  });
}

function initAnalyzeImagesOptionsWindow() {
  var offset = $(".historyBody").offset();

  $("#analyzeImageOptionsWindow").jqxWindow({
    width: 700,
    height: 400,
    title: "How do you want to analyze the images?",
    resizable: true,
    autoOpen: false,
    // okButton: $("#userChoiceConfirmButton"),
    // cancelButton: $("#userChoiceCancelButton"),
    position: { x: offset.left + 200, y: offset.top + 50 },
    initContent: function () { },
    theme: jqWidgetThemeName
  });
  $("#analyzeImageOptionsConfirmButton").on("click", function (e) {
    e.preventDefault();
    alert("We are changing your experience with this feature!");
  });
  // $("#analyzeImageOptionsConfirmButton").on("click", analyzeImageOptionsConfirmHandler);
}

function analyzeImageFileSelectionHandler() {
  $("#analyzeImageOptionsWindow").jqxWindow("open");
}

function analyzeImageOptionsConfirmHandler() {
  var selectedRows = getGridSelectedRows("fileExplorerGrid");

  if (selectedRows && selectedRows.length > 0) {
    var filesToBeLoaded = [];
    selectedRows
      // .map(function (index) {
      // 	return getGridRowData('fileExplorerGrid', index);
      // })
      .forEach(function (rowData) {
        var selectedFile = rowData.fileName;

        if (rowData.isDirectory) {
          filesToBeLoaded = filesToBeLoaded.concat(
            activeCustomerFiles
              .filter(function (ele) {
                var fileToBeLoaded = encodeURI(rowData.fileLabel) + "/";
                var parentPath = getParentFilePath();

                if (parentPath && parentPath.length > 0 && parentPath !== "/") {
                  fileToBeLoaded = parentPath + fileToBeLoaded;
                }
                return (
                  ele.fileName.startsWith(fileToBeLoaded) &&
                  !ele.fileName.endsWith(systemFileLabel)
                );
              })
              .map(function (ele) {
                return ele.fileName;
              })
          );
        } else {
          if (!selectedFile.endsWith(systemFileLabel)) {
            filesToBeLoaded.push(selectedFile);
          }
        }
      });

    // $('#customerFilesTable').jqxGrid('clearselection');

    var nuoAnalyzeImageOptions = {};

    nuoAnalyzeImageOptions.TargetTableName = $("#targetTableNameImage").val();
    nuoAnalyzeImageOptions.SourceFiles = filesToBeLoaded;
    nuoAnalyzeImageOptions.LanguageHints = $("#languageHint").val();
    nuoAnalyzeImageOptions.ShouldAppend = $("#shouldAppendImage").is(
      ":checked"
    );

    var fileList = selectedIndices.map(function (index) {
      var selectedFile = getGridRowData("fileExplorerGrid", index);
      if (selectedFile.isDirectory) return selectedFile.fileLabel + "/*";
      else return selectedFile.fileLabel;
    });
    var dateString = new Date() + "";
    dateString = dateString.substring(0, dateString.indexOf("("));
    nuoUserMessageHolder.Message =
      "Analyze Images " +
      fileList
        .map(function (ele) {
          return "<b>" + ele + "</b>";
        })
        .join(" AND ") +
      " and lod results into table <b>" +
      nuoAnalyzeImageOptions.TargetTableName +
      "</b>. <br><br>Note: This task has been created by Eva on your behalf on " +
      dateString;

    nuoUserMessageHolder.NuoAnalyzeImageOptions = nuoAnalyzeImageOptions;
    nuoUserMessageHolder.CommunicationType = COMMUNICATION_TYPE_LOAD_INTO_TABLE;

    sendMessageToEva(true, true, true);
  }

  // $("#fileLoadOptionsWindow").jqxWindow("close");
  $("#fileLoadOptionsWindow").fadeOut();
}

// function initFileLoadOptionsWindow() {
// 	var offset = $(".historyBody").offset();

// 	$("#fileLoadOptionsWindow").jqxWindow(
// 		{
// 			width: 700,
// 			height: 400,
// 			title: "How do you want to load the files?",
// 			resizable: true,
// 			autoOpen: false,
// 			// okButton: $("#userChoiceConfirmButton"),
// 			// cancelButton: $("#userChoiceCancelButton"),
// 			position: { x: offset.left + 200, y: offset.top + 50 },
// 			initContent: function () {
// 			},
// 			theme: jqWidgetThemeName
// 		}
// 	);
// 	$("#fileLoadOptionsConfirmButton").on("click", fileLoadOptionsConfirmHandler);
// }

function loadIntoTableFileSelectionHandler() {
  $("#fileLoadOptionsConfirmButton").off("click");
  $("#fileLoadOptionsConfirmButton").on("click", fileLoadOptionsConfirmHandler);

  var targetModal = $(this).attr("data-modal");
  $("#" + targetModal).fadeIn();
  $("#fileLoadOptionsWindow").fadeIn();
  $("#closeFileLoadOptionsWindow").off("click");
  $("#closeFileLoadOptionsWindow").on("click", function (e) {
    e.preventDefault();
    $("#fileLoadOptionsWindow").fadeOut();
  });

  // $("#fileLoadOptionsWindow").jqxWindow("open");
}

function fileLoadOptionsConfirmHandler() {
  var selectedRows = getGridSelectedRows("fileExplorerGrid");

  if (selectedRows && selectedRows.length > 0) {
    var filesToBeLoaded = [];
    selectedRows
      // .map(function (index) {
      // 	return getGridRowData('fileExplorerGrid', index);
      // })
      .forEach(function (rowData) {
        var selectedFile = rowData.fileName;

        if (rowData.isDirectory) {
          filesToBeLoaded = filesToBeLoaded.concat(
            activeCustomerFiles
              .filter(function (ele) {
                var fileToBeLoaded = encodeURI(rowData.fileLabel) + "/";
                var parentPath = getParentFilePath();

                if (parentPath && parentPath.length > 0 && parentPath !== "/") {
                  fileToBeLoaded = parentPath + fileToBeLoaded;
                }
                return (
                  ele.fileName.startsWith(fileToBeLoaded) &&
                  !ele.fileName.endsWith(systemFileLabel)
                );
              })
              .map(function (ele) {
                return ele.fileName;
              })
          );
        } else {
          if (!selectedFile.endsWith(systemFileLabel)) {
            filesToBeLoaded.push(selectedFile);
          }
        }
      });

    // $('#customerFilesTable').jqxGrid('clearselection');

    var nuoFileLoadOptions = {};
    var previewAfterLoading = $("#shouldPreivew").is(":checked");

    nuoFileLoadOptions.AnalysisId = Date.now() + "";
    nuoFileLoadOptions.TargetTableName = $("#targetTableName").val();
    nuoFileLoadOptions.SourceFiles = filesToBeLoaded;
    nuoFileLoadOptions.FileFormat = $("#fileFormat").val();
    nuoFileLoadOptions.Delimiter = $("#delimiter").val();
    nuoFileLoadOptions.QuoteCharacter = $("#quoteCharacter").val();
    nuoFileLoadOptions.ShouldAppend = $("#shouldAppend").is(":checked");
    nuoFileLoadOptions.RowsToSkip = parseInt($("#rowsToSkip").val());
    if (!nuoFileLoadOptions.RowsToSkip) {
      nuoFileLoadOptions.RowsToSkip = 0;
    }

    if (
      nuoFileLoadOptions.TargetTableName &&
      nuoFileLoadOptions.TargetTableName.trim().length > 0 &&
      nuoFileLoadOptions.SourceFiles &&
      nuoFileLoadOptions.SourceFiles.length > 0 &&
      nuoFileLoadOptions.FileFormat &&
      nuoFileLoadOptions.FileFormat.trim().length > 0
    ) {
      $("#ajaxLoader").addClass("is-active");
      directCall("rt101", "NuoFileLoadOptions", nuoFileLoadOptions)
        .done(function (data) {
          handleResponse(data);
        })
        .fail(function () {
          if (sessionId === "X") {
            var data = {
              StatusCode: 200,
              Status: "OK",
              Content: "Successfully deleted columns."
            };
            handleResponse(data);
          }
        });
      var handleResponse = function (data) {
        // if(isExplicitCall)

        if (data.StatusCode && data.StatusCode === 200) {
          if (previewAfterLoading === true) {
            var nuoQueryResponse = data.Content;
            handlePreviewOperationPromise(
              nuoQueryResponse,
              nuoFileLoadOptions.TargetTableName
            );
          }
        } else {
          //window.location.reload();
        }
        $("#ajaxLoader").removeClass("is-active");
      };
    }
  }
  // $("#fileLoadOptionsWindow").jqxWindow("close");
  $("#fileLoadOptionsWindow").fadeOut();
}

function initMapperWindow() {
  var offset = $(".historyBody").offset();
  $("#mapperWindow").jqxWindow({
    width: "60em",
    height: "30em",
    // resizable: true,
    autoOpen: false,
    showCloseButton: true,
    // cancelButton: $("#storageEntitiesCloseButton"),
    position: { x: offset.left + 150, y: offset.top + 25 },
    initContent: function () { },
    theme: jqWidgetThemeName
  });
  var leftSource = ["LeftItem1", "LeftItem2", "LeftItem3", "LeftItem4"];
  var rightSource = ["RightItem1", "RightItem2", "RightItem3", "RightItem4"];
  // $("#mapperLeftPane").jqxListBox({ source: leftSource, width: 800, height: 800 });
  // $("#mapperRightPane").jqxListBox({ source: rightSource, width: 800, height: 800 });
  $("#mapperWindow").jqxWindow(
    "setTitle",
    "Explain the mapping between source and target columns."
  );
  $("#mapperWindow").jqxWindow({ showCloseButton: true });
  $("#confirmMapperButton").on("click", function (e) {
    e.preventDefault();
    nuoUserMessageHolder.Responses = getMapperConnections();
    nuoUserMessageHolder.Message = nuoUserMessageHolder.Responses.map(function (
      ele
    ) {
      return ele.replace("|~|", "->");
    }).join(" <i>AND</i> ");
    $("#mapperWindow").jqxWindow("close");
    sendMessageToEva(false, false, true);
  });
}

function loadConnections() {
  // $(document).ready(function(){

  jsPlumb.ready(function () {
    jsPlumbRef = jsPlumb.getInstance();

    jsPlumbRef.importDefaults({
      Connector: ["Bezier", { margin: 20, curviness: 150 }],
      Anchors: ["TopCenter", "BottomCenter"],
      ConnectionsDetachable: true
    });
    jsPlumbRef.setContainer("mapperBodyContainer");
    $("#mapperLeftPane").scroll(function () {
      jsPlumbRef.repaintEverything();
    });

    $("#mapperRightPane").scroll(function () {
      jsPlumbRef.repaintEverything();
    });

    var sourceEndpointOptions = {
      endpoint: "Dot",
      paintStyle: {
        radius: 8,
        fill: "var(--nuo-orange-solid)",
        opacity: 0.5
      },
      isSource: true,
      connectorStyle: { stroke: "#666" },
      maxConnections: -1
    };

    var targetEndpointOptions = {
      endpoint: sourceEndpointOptions.endpoint,
      paintStyle: {
        radius: sourceEndpointOptions.paintStyle.radius,
        fill: "var(--nuo-blue-solid)",
        opacity: sourceEndpointOptions.paintStyle.opacity
      },
      connectorStyle: sourceEndpointOptions.connectorStyle,
      isTarget: true,
      maxConnections: -1
    };

    var sourceList = nuoEvaMessageHolder.NuoMappingInput.NuoSourceList;
    var targetList = nuoEvaMessageHolder.NuoMappingInput.NuoTargetList;
    // var sourceList = [
    // {
    // label: "LeftItem1",
    // },
    // {
    // label: "LeftItem2",
    // },
    // {
    // label: "LeftItem3",
    // },
    // {
    // label: "LeftItem4",
    // }
    // ];
    // var targetList = [
    // {
    // label: "RightItem1",
    // inputIndex: 1
    // },{
    // label: "RightItem2",
    // inputIndex: 3
    // },{
    // label: "RightItem3",
    // inputIndex: 1
    // },{
    // label: "RightItem4",
    // inputIndex: 0
    // },{
    // label: "RightItem5",
    // inputIndex: 2
    // }
    // ];

    for (var i = 0; i < sourceList.length; i++) {
      var label = sourceList[i].label;
      var elementId = "leftItem" + i;
      var leftElement =
        "<div id='" + elementId + "' class='mapperItem'>" + label + "</div>";

      $("#mapperLeftPane").append(leftElement);

      var endpointLeft = jsPlumbRef.makeSource(
        elementId,
        { anchor: "Right" },
        sourceEndpointOptions
      );
    }

    for (var i = 0; i < targetList.length; i++) {
      var label = targetList[i].label;
      var elementId = "rightItem" + i;
      var rightElement =
        "<div id='" + elementId + "' class='mapperItem'>" + label + "</div>";

      $("#mapperRightPane").append(rightElement);
      $("#" + elementId).dblclick(function (data) {
        var divId = data.target.id;
        var conns = jsPlumbRef.getConnections({ target: divId });
        for (var i = 0; i < conns.length; i++)
          jsPlumbRef.deleteConnection(conns[i]);

        jsPlumbRef.removeAllEndpoints(divId);
      });

      var endpointRight = jsPlumbRef.makeTarget(
        elementId,
        { anchor: "Left" },
        targetEndpointOptions
      );

      var connection = jsPlumbRef.connect({
        source: "leftItem" + targetList[i].inputIndex,
        target: "rightItem" + i
      });
    }
    jsPlumbRef.bind("beforeDrop", function (e) {
      var elementId = e.targetId;
      var conns = jsPlumbRef.getConnections({ target: e.targetId });
      // conns = conns.concat(jsPlumbRef.getConnections({source: e.sourceId }));
      for (var i = 0; i < conns.length; i++)
        jsPlumbRef.deleteConnection(conns[i]);

      return true;
    });
  });
  // })
}

function getMapperConnections() {
  var mapping = jsPlumbRef.getConnections().map(function (c) {
    return c.source.outerText + "|~|" + c.target.outerText;
  });
  return mapping;
}

function loadStorageEntitiesTree() {
  $("#storageEntitiesTree").jqxTree({
    width: "100%",
    height: "95%",
    toggleMode: "click",
    theme: jqWidgetThemeName
  });

  $("#entityRelationshipListBox").jqxListBox({
    multiple: false,
    width: "100%",
    height: "95%",
    theme: jqWidgetThemeName
  });

  $(document).on("dblclick", "#storageEntitiesTree  .jqx-tree-item", function (
    e
  ) {
    var _item = e.target;
    if (_item.tagName != "LI") {
      _item = $(_item).parents("li:first");
    }
    var item = $("#storageEntitiesTree").jqxTree("getItem", _item[0]);
    var field = item.value;
    if (field) {
      if (currRelMesssageFieldCount % 2 == 0) {
        currNuoRelationshipInput.NuoCommonFields.push({
          leftField: field
        });
      } else {
        currNuoRelationshipInput.NuoCommonFields[
          currNuoRelationshipInput.NuoCommonFields.length - 1
        ].rightField = field;
      }
      currRelMesssageFieldCount += 1;
      refreshEntityRelationshipListBox();
      $("#storageEntitiesTree").jqxTree("collapseAll");
    }
  });

  $("#confirmRelationshipButton").on("click", function (e) {
    e.preventDefault();
    currRelMesssageFieldCount = 0;

    $("#entityRelationshipWindow").fadeOut();
    sendQueryToEva();
  });

  $("#redoRelationshipButton").on("click", function (e) {
    e.preventDefault();
    currRelMesssageFieldCount = 0;
    currNuoRelationshipInput.NuoCommonFields = [];
    refreshEntityRelationshipListBox();
  });
}

function refreshEntityRelationshipWindow(nuoEvaMessage) {
  loadStorageEntitiesTree();
  refreshStorageEntitiesTree(nuoEvaMessage);
  refreshEntityRelationshipListBox();

  $("#entityRelationshipMsg > h3").html(
    "What is the relationship between tables " +
    currNuoRelationshipInput.LeftEntityName +
    " and " +
    currNuoRelationshipInput.RightEntityName +
    "?"
  );
  $("#entityRelationshipWindow").fadeIn();
  $("#closeRelationshipWindow").off("click");
  $("#closeRelationshipWindow").on("click", function (e) {
    e.preventDefault();
    $("#entityRelationshipWindow").fadeOut();
  });
}

function refreshMapperWindow() {
  $("#mapperMsg").html(nuoEvaMessageHolder.Message);
  $("#mapperWindow").jqxWindow("open");
  $("#mapperWindow").jqxWindow("focus");
  loadConnections();
}

function refreshStorageEntitiesTree(nuoEvaMessage) {
  $("#storageEntitiesTree").jqxTree({
    source: nuoTables
      .map(function (ele) {
        return {
          label: ele.EntityName,
          expanded: false,
          items: ele.Fields.map(function (field) {
            return {
              label: field.FieldName,
              value: field
            };
          })
        };
      })
      .sort(function (l, r) {
        if (l.label < r.label) return -1;
        if (l.label > r.label) return 1;
        return 0;
      })
  });
  $("#entityRelationshipListBox").jqxListBox({
    source: []
  });
}

function refreshEntityRelationshipListBox() {
  var listBoxSource = currNuoRelationshipInput.NuoCommonFields.map(function (
    commonField
  ) {
    var leftField = commonField.leftField;
    var item =
      "<b>" +
      leftField.FieldName +
      "</b> of <u>" +
      leftField.EntityName +
      "</u> = <b>";

    if (commonField.rightField) {
      var rightField = commonField.rightField;
      item +=
        rightField.FieldName + "</b> of <u>" + rightField.EntityName + "</u>";
    }
    return {
      html: item
    };
  });
  $("#entityRelationshipListBox").jqxListBox({
    source: listBoxSource
  });
}

function sendQueryToEva(
  analysisId,
  selectionRawText,
  filterRawText,
  resultTableName,
  callbackTask
) {
  var nuoUserMessage = {};

  if (selectionRawText && selectionRawText.length > 0) {
    nuoUserMessage.Selection = selectionRawText;
  }

  if (filterRawText && filterRawText.length > 0) {
    nuoUserMessage.Filter = filterRawText;
  }

  if (analysisId && analysisId.length > 0) {
    nuoUserMessage.AnalysisId = analysisId;
  }

  if (resultTableName && resultTableName.length > 0) {
    nuoUserMessage.ResultTableName = resultTableName;
  }

  if (
    currNuoRelationshipInput.NuoCommonFields &&
    currNuoRelationshipInput.NuoCommonFields.length > 0
  ) {
    nuoUserMessage.NuoRelationshipInput = {
      LeftEntityName: currNuoRelationshipInput.LeftEntityName,
      RightEntityName: currNuoRelationshipInput.RightEntityName,
      NuoCommonFields: currNuoRelationshipInput.NuoCommonFields
    };

    if (historyAnalyses[currRelAnalysisId]) {
      nuoUserMessage.Selection =
        historyAnalyses[currRelAnalysisId].SelectionRaw;
      nuoUserMessage.Filter = historyAnalyses[currRelAnalysisId].FilterRaw;
      nuoUserMessage.AnalysisId = currRelAnalysisId;
    } else {
      alert("ERROR: I could not relate results to matching analysis.");
      return null;
    }
  }

  $("#ajaxLoader").addClass("is-active");
  directCall("rt47", "NuoUserMessage", nuoUserMessage)
    .done(function (data) {
      handleResponse(data);
    })
    .fail(function () {
      if (sessionId === "X") {
        var data = {
          StatusCode: 200,
          Status: "OK",
          Content: {
            NuoEvaMessage: {
              AnalysisId: "1541396428965",
              RuleText: "",
              CommunicationType: 7,
              Message: ""
            },
            Result: {
              Metadata: [
                {
                  DatasetName: "",
                  EntityName: "Y",
                  FieldName: "Country",
                  DataType: "STRING"
                },
                {
                  DatasetName: "",
                  EntityName: "Y",
                  FieldName: "GL_2016_Value",
                  DataType: "FLOAT"
                },
                {
                  DatasetName: "",
                  EntityName: "Y",
                  FieldName: "GL_2016_Value_1",
                  DataType: "FLOAT"
                },
                {
                  DatasetName: "",
                  EntityName: "Y",
                  FieldName: "FX_Value",
                  DataType: "FLOAT"
                }
              ],
              Data: [
                ["FI", "37.25882", "18.62941", "1.0"],
                ["FI", "9.4649", "4.73245", "1.0"],
                ["FI", "123.5988", "61.7994", "1.0"],
                ["FI", "-0.09426", "-0.04713", "1.0"],
                ["FI", "-0.62118", "-0.31059", "1.0"],
                ["FI", "81.22279", "40.611395", "1.0"],
                ["FI", "3.952", "1.976", "1.0"],
                ["FI", "-2.57257", "-1.286285", "1.0"],
                ["FI", "-0.573", "-0.2865", "1.0"],
                ["FI", "-0.19244", "-0.09622", "1.0"],
                ["FI", "429.045346", "214.522673", "1.0"],
                ["FI", "-1.3965", "-0.69825", "1.0"],
                ["FI", "0.00152", "0.00076", "1.0"],
                ["FI", "-0.59111", "-0.295555", "1.0"],
                ["FI", "-0.437", "-0.2185", "1.0"],
                ["FI", "106.330308", "53.165154", "1.0"],
                ["FI", "-39.94564", "-19.97282", "1.0"],
                ["FI", "7.611476", "3.805738", "1.0"],
                ["FI", "-0.84378", "-0.42189", "1.0"],
                ["FI", "-12.63425", "-6.317125", "1.0"],
                ["FI", "-0.8568", "-0.4284", "1.0"],
                ["FI", "-127.59843", "-63.799215", "1.0"],
                ["FI", "95.065854", "47.532927", "1.0"],
                ["FI", "2.14054", "1.07027", "1.0"],
                ["FI", "-25.17086", "-12.58543", "1.0"],
                ["FI", "-16.311", "-8.1555", "1.0"],
                ["FI", "0.11497", "0.057485", "1.0"],
                ["FI", "5.927316", "2.963658", "1.0"],
                ["FI", "-0.6335", "-0.31675", "1.0"],
                ["FI", "-0.7344", "-0.3672", "1.0"],
                ["FI", "1.872564", "0.936282", "1.0"],
                ["FI", "0.80866", "0.40433", "1.0"],
                ["FI", "-0.28496", "-0.14248", "1.0"],
                ["FI", "1.452094", "0.726047", "1.0"],
                ["FI", "-1.18793", "-0.593965", "1.0"],
                ["FI", "58.982992", "29.491496", "1.0"],
                ["FI", "-0.15132", "-0.07566", "1.0"],
                ["FI", "-5.1765", "-2.58825", "1.0"],
                ["FI", "314.181074", "157.090537", "1.0"],
                ["FI", "-60.09695", "-30.048475", "1.0"],
                ["FI", "-12.32126", "-6.16063", "1.0"],
                ["FI", "-24.96099", "-12.480495", "1.0"],
                ["FI", "-6.1098", "-3.0549", "1.0"],
                ["FI", "4.623422", "2.311711", "1.0"],
                ["FI", "3.083396", "1.541698", "1.0"],
                ["FI", "-8.10077", "-4.050385", "1.0"],
                ["FI", "-1.73469", "-0.867345", "1.0"],
                ["FI", "53.453536", "26.726768", "1.0"],
                ["FI", "-0.43222", "-0.21611", "1.0"],
                ["FI", "-1.5756", "-0.7878", "1.0"],
                ["FI", "-61.26036", "-30.63018", "1.0"],
                ["FI", "8.62467", "4.312335", "1.0"],
                ["FI", "3.98129", "1.990645", "1.0"],
                ["FI", "15.51086", "7.75543", "1.0"],
                ["FI", "4.93867", "2.469335", "1.0"],
                ["FI", "-38.71035", "-19.355175", "1.0"],
                ["FI", "117.023128", "58.511564", "1.0"],
                ["FI", "200.267752", "100.133876", "1.0"],
                ["FI", "0.150784", "0.075392", "1.0"],
                ["FI", "2.128836", "1.064418", "1.0"],
                ["FI", "-2.97983", "-1.489915", "1.0"],
                ["FI", "-2.9435", "-1.47175", "1.0"],
                ["FI", "2021.053294", "1010.526647", "1.0"],
                ["FI", "0.501334", "0.250667", "1.0"],
                ["FI", "105.349072", "52.674536", "1.0"],
                ["FI", "2181.78007", "1090.890035", "1.0"],
                ["FI", "-0.08629", "-0.043145", "1.0"],
                ["FI", "-0.191", "-0.0955", "1.0"],
                ["FI", "6.43948", "3.21974", "1.0"],
                ["FI", "-0.35262", "-0.17631", "1.0"],
                ["FI", "-1.78765", "-0.893825", "1.0"],
                ["FI", "-1.4544", "-0.7272", "1.0"],
                ["FI", "-1.49061", "-0.745305", "1.0"],
                ["FI", "-0.4792", "-0.2396", "1.0"],
                ["FI", "20.638598", "10.319299", "1.0"],
                ["FI", "-0.06118", "-0.03059", "1.0"],
                ["FI", "37.371746", "18.685873", "1.0"],
                ["FI", "3.009068", "1.504534", "1.0"],
                ["FI", "-16.37869", "-8.189345", "1.0"],
                ["FI", "75.62668", "37.81334", "1.0"],
                ["FI", "14.0581", "7.02905", "1.0"],
                ["FI", "2.99136", "1.49568", "1.0"],
                ["FI", "80.05221", "40.026105", "1.0"],
                ["FI", "-15.95229", "-7.976145", "1.0"],
                ["FI", "48.374", "24.187", "1.0"],
                ["FI", "0.20348", "0.10174", "1.0"],
                ["FI", "-0.66547", "-0.332735", "1.0"],
                ["FI", "-0.9792", "-0.4896", "1.0"],
                ["FI", "-2.4472", "-1.2236", "1.0"],
                ["FI", "-0.1899", "-0.09495", "1.0"],
                ["FI", "0.82916", "0.41458", "1.0"],
                ["FI", "-3.12866", "-1.56433", "1.0"],
                ["FI", "-140.0", "-70.0", "1.0"],
                ["FI", "147.898204", "73.949102", "1.0"],
                ["FI", "18.636264", "9.318132", "1.0"],
                ["FI", "-0.52674", "-0.26337", "1.0"],
                ["FI", "5.77936", "2.88968", "1.0"],
                ["FI", "-1.0428", "-0.5214", "1.0"],
                ["FI", "-6.22652", "-3.11326", "1.0"],
                ["FI", "-3.3544", "-1.6772", "1.0"]
              ]
            }
          }
        };
        handleResponse(data);
      }
    });
  var handleResponse = function (data) {
    if (data.StatusCode && data.StatusCode === 200) {
      var nuoQueryResponse = data.Content;
      if (
        nuoQueryResponse.NuoEvaMessage &&
        nuoQueryResponse.NuoEvaMessage.CommunicationType
      ) {
        handleResponseFromEva(nuoQueryResponse, true);
        if (callbackTask) callbackTask();
      } else {
        console.log("Something went wrong.");
        console.log(JSON.stringify(nuoQueryResponse));
      }
    } else {
      //window.location.reload();
    }
    $("#ajaxLoader").removeClass("is-active");
  };
  // var tempAnalysis = null;
  // if (Object.values(historyAnalyses).length % 2 == 0) {
  // 	tempAnalysis = analysis2Source;
  // } else {
  // 	tempAnalysis = analysis1Source;
  // }

  // if (historyAnalyses[analysisId]) {
  // 	historyAnalyses[analysisId].Result = tempAnalysis.Result;
  // 	historyAnalyses[analysisId].ProfilingResult = tempAnalysis.ProfilingResult;
  // 	historyAnalyses[analysisId].Pattern = tempAnalysis.Pattern;
  // 	drawDashboard(historyAnalyses[analysisId]);
  // }
}

function sleep(ms) {
  var promise = new Promise(function (resolve) {
    setTimeout(resolve, ms);
  });
  return promise;
}

function handleResponseFromEva(nuoQueryResponse, redrawDashboard) {
  var nuoEvaMessage = nuoQueryResponse.NuoEvaMessage;
  if (nuoEvaMessage.CommunicationType === COMMUNICATION_TYPE_ERROR) {
    alert(nuoEvaMessage.Message);
  } else if (
    nuoEvaMessage.CommunicationType === COMMUNICATION_TYPE_RESULT_AVAILABLE
  ) {
    var analysisId = nuoEvaMessage.AnalysisId;
    if (historyAnalyses[analysisId]) {
      if (
        !historyAnalyses[analysisId].ProfilingResult &&
        nuoQueryResponse.ProfilingResult
      ) {
        saveToHistory(historyAnalyses[analysisId]);
      }
      historyAnalyses[analysisId].Result = nuoQueryResponse.Result;
      historyAnalyses[analysisId].ProfilingResult =
        nuoQueryResponse.ProfilingResult;
      historyAnalyses[analysisId].Pattern = nuoQueryResponse.Pattern;

      if (
        redrawDashboard &&
        redrawDashboard === true &&
        historyAnalyses[analysisId].Result &&
        historyAnalyses[analysisId].Result.Data
        // historyAnalyses[analysisId].Result.Data.length > 0
      ) {
        drawDashboard(historyAnalyses[analysisId]);
      }
    } else {
      alert("ERROR: I could not relate results to matching analysis.");
      return null;
    }
  } else if (
    nuoEvaMessage.CommunicationType == COMMUNICATION_TYPE_RELATIONSHIPS
  ) {
    currNuoRelationshipInput.LeftEntityName = nuoEvaMessage.LeftEntityName;
    currNuoRelationshipInput.RightEntityName = nuoEvaMessage.RightEntityName;
    currNuoRelationshipInput.NuoCommonFields = [];
    currRelAnalysisId = nuoEvaMessage.AnalysisId;
    refreshEntityRelationshipWindow(nuoEvaMessage);
  } else if (nuoEvaMessage.CommunicationType == COMMUNICATION_TYPE_MAPPING) {
    refreshMapperWindow();
  }
}

function addRowToHistoryFeed(historyAnalysis) {
  $("#historyBody").prepend(getHistoryRowTag(historyAnalysis));
  $("#historyAnalysisResultTableInput" + historyAnalysis.AnalysisId).hide();

  historyAnalyses[historyAnalysis.AnalysisId] = historyAnalysis;
  // drawAnalysisGrids(historyAnalysis);

  addAnalysisEventHandlers(historyAnalysis.AnalysisId);
}

function formatDate(dateObj) {
  var monthNames = [
    "January",
    "February",
    "March",
    "April",
    "May",
    "June",
    "July",
    "August",
    "September",
    "October",
    "November",
    "December"
  ];

  var dayNames = [
    "Sunday",
    "Monday",
    "Tuesday",
    "Wednesday",
    "Thursday",
    "Friday",
    "Saturday"
  ];

  var dayIndex = dateObj.getDay();
  var date = dateObj.getDate();
  var monthIndex = dateObj.getMonth();
  var year = dateObj.getFullYear();

  return (
    dayNames[dayIndex] +
    ", " +
    monthNames[monthIndex] +
    " " +
    date +
    ", " +
    year
  );
}

function timeSince(timeMillis) {
  var seconds = Math.floor((Date.now() - timeMillis) / 1000);

  var interval = Math.floor(seconds / (60 * 60 * 24 * 30 * 12));

  if (interval > 1) return interval + " years ago";
  else if (interval == 1) return interval + " year ago";

  interval = Math.floor(seconds / (60 * 60 * 24 * 30));
  if (interval > 1) return interval + " months ago";
  else if (interval == 1) return interval + " month ago";

  interval = Math.floor(seconds / (60 * 60 * 24 * 7));
  if (interval > 1) return interval + " weeks ago";
  else if (interval == 1) return interval + " week ago";

  interval = Math.floor(seconds / (60 * 60 * 24));
  if (interval > 1) return interval + " days ago";
  else if (interval == 1) return interval + " day ago";

  interval = Math.floor(seconds / (60 * 60));
  if (interval > 1) return interval + " hours ago";
  else if (interval == 1) return interval + " hour ago";

  interval = Math.floor(seconds / 60);
  if (interval > 1) return interval + " minutes ago";
  else if (interval == 1) return interval + " minute ago";

  interval = Math.floor(seconds);
  if (interval > 1) return interval + " seconds ago";
  else if (interval == 1) return interval + " second ago";
  else return "Just now";
}

function formatTime(date_obj) {
  // formats a javascript Date object into a 12h AM/PM time string
  var hour = date_obj.getHours();
  var minute = date_obj.getMinutes();
  var amPM = hour > 11 ? "PM" : "AM";
  if (hour > 12) {
    hour -= 12;
  } else if (hour == 0) {
    hour = "12";
  }
  if (minute < 10) {
    minute = "0" + minute;
  }
  return hour + ":" + minute + " " + amPM;
}

function formateCurrency(input) {
  return (
    "EUR " +
    parseFloat(input)
      .toFixed(6)
      .replace(".", ",")
  );
}

function saveToHistory(historyAnalysis) {
  historyAnalysis.LastModifiedAt = Date.now() + 0;

  var nuoHistoryRow = {};

  var historyMetadataCols =
    "AnalysisId, CreatedAt, LastModifiedAt, Author, Title, Selection, SelectionRaw, Filter, FilterRaw, ResultTableName, AliasMapping, Result, ProfilingResult, Pattern, Visualization";
  nuoHistoryRow.Metadata = historyMetadataCols.split(",").map(function (ele) {
    var nuoField = {
      DatasetName: "",
      EntityName: "",
      FieldName: ele,
      DataType: "text"
    };
    if (ele == "CreatedAt" || ele == "LastModifiedAt") {
      nuoField.DataType = "bigint";
    }
    return nuoField;
  });
  nuoHistoryRow.Data = [
    historyAnalysis.AnalysisId,
    historyAnalysis.CreatedAt + "",
    historyAnalysis.LastModifiedAt + "",
    historyAnalysis.Author,
    historyAnalysis.Title,
    historyAnalysis.Selection,
    historyAnalysis.SelectionRaw,
    historyAnalysis.Filter,
    historyAnalysis.FilterRaw,
    historyAnalysis.ResultTableName,
    JSON.stringify(historyAnalysis.AliasMapping),
    JSON.stringify(historyAnalysis.Result),
    JSON.stringify(historyAnalysis.ProfilingResult),
    JSON.stringify(historyAnalysis.Pattern),
    JSON.stringify(historyAnalysis.Visualization)
  ];
  $("#ajaxLoader").addClass("is-active");
  directCall("rt37", "NuoHistoryPaginationRequest", {
    NuoHistoryRows: [nuoHistoryRow]
  })
    .done(function (data) {
      handleResponse(data);
    })
    .fail(function () {
      if (sessionId === "X") {
        var data = { StatusCode: 200, Status: "OK", Content: "OK" };
        handleResponse(data);
      }
    });
  var handleResponse = function (data) {
    if (data.StatusCode && data.StatusCode === 200) {
      var response = parseInt(data.Content);
      if (response > 0) {
        lastSaveMarker = parseInt(response);
      }
    } else {
      //window.location.reload();
    }
    $("#ajaxLoader").removeClass("is-active");
  };
}

function readFromHistory() {
  $("#ajaxLoader").addClass("is-active");

  // // TEST BLOCK START

  // renderHistorySection([]);
  // $("#ajaxLoader").removeClass("is-active");

  // // TEST BLOCK END

  directCall("rt41", "NuoHistoryPaginationRequest", {})
    .done(function (data) {
      handleResponse(data);
    })
    .fail(function () {
      if (sessionId === "X") {
        var data = {
          StatusCode: 200,
          Status: "OK",
          Content: [
            [
              "1541396428965",
              "1541396428965",
              "1541770772843",
              "Pulkit Barad",
              "KPMG Pilot Demo Analysis #PilotDemo # KPMG",
              "Country_GL_2016_copy and GL_2016_Value and GL_2016_Value * 0.5 and FX_Value",
              "[GL_2016_copy][Country][STRING] and [GL_2016_copy][GL_2016_Value][FLOAT] and [GL_2016_copy][GL_2016_Value][FLOAT] * 0.5 and [FX_Rate][FX_Value][FLOAT]",
              "FX_Date is 2015",
              "[FX_Rate][FX_Date][DATE] is 2015",
              "Y",
              '{"GL_Date":"[General_Ledger][GL_Date][DATE]","Amount":"[General_Ledger][Amount][FLOAT]","KPMG_Code":"[General_Ledger][KPMG_Code][STRING]","Country":"[General_Ledger][Country][STRING]","Level_0_GL_code":"[General_Ledger][Level_0_GL_code][INTEGER]","Account_Name":"[Account][Account_Name][STRING]","GL_Value":"[General_Ledger][GL_Value][FLOAT]","Subtotal":"[GL_Mapping][Subtotal][STRING]","GL_2016_Value":"[GL_2016_copy][GL_2016_Value][FLOAT]","Country_GL_2016_copy":"[GL_2016_copy][Country][STRING]","KPMG_Code_GL_2016_copy":"[GL_2016_copy][KPMG_Code][STRING]","FX_Value":"[FX_Rate][FX_Value][FLOAT]","Country_FX_Rate":"[FX_Rate][Country][STRING]","FX_Date":"[FX_Rate][FX_Date][DATE]"}',
              '{"Metadata":[{"DatasetName":"","EntityName":"Result_Table","FieldName":"Country","DataType":"STRING"},{"DatasetName":"","EntityName":"Result_Table","FieldName":"GL_2016_Value","DataType":"FLOAT"},{"DatasetName":"","EntityName":"Result_Table","FieldName":"GL_2016_Value_1","DataType":"FLOAT"},{"DatasetName":"","EntityName":"Result_Table","FieldName":"FX_Value","DataType":"FLOAT"}],"Data":[["FI","79.8","39.9","1.0"],["FI","-5.17008","-2.58504","1.0"],["FI","-0.98867","-0.494335","1.0"],["FI","0.85434","0.42717","1.0"],["FI","-0.367","-0.1835","1.0"],["FI","15.884608","7.942304","1.0"],["FI","-0.199196","-0.099598","1.0"],["FI","-1.245","-0.6225","1.0"],["FI","65.2688","32.6344","1.0"],["FI","0.0","0.0","1.0"],["FI","1.3604","0.6802","1.0"],["FI","30.248","15.124","1.0"],["FI","-64.25394","-32.12697","1.0"],["FI","-0.719","-0.3595","1.0"],["FI","12.33712","6.16856","1.0"],["FI","0.26516","0.13258","1.0"],["FI","16.4122","8.2061","1.0"],["FI","-0.15482","-0.07741","1.0"],["FI","-2.51985","-1.259925","1.0"],["FI","-132.627","-66.3135","1.0"],["FI","143.3398","71.6699","1.0"],["FI","646.9158","323.4579","1.0"],["FI","31.134236","15.567118","1.0"],["FI","-0.09","-0.045","1.0"],["FI","1.18503","0.592515","1.0"],["FI","-0.06311","-0.031555","1.0"],["FI","1.0678","0.5339","1.0"],["FI","-4.87155","-2.435775","1.0"],["FI","102.9116","51.4558","1.0"],["FI","0.027","0.0135","1.0"],["FI","433.851662","216.925831","1.0"],["FI","24.804272","12.402136","1.0"],["FI","-1.79373","-0.896865","1.0"],["FI","-10.993","-5.4965","1.0"],["FI","3.994598","1.997299","1.0"],["FI","2.77","1.385","1.0"],["FI","18.595262","9.297631","1.0"],["FI","-70.836","-35.418","1.0"],["FI","53.7358","26.8679","1.0"],["FI","-0.05","-0.025","1.0"],["FI","0.44248","0.22124","1.0"],["FI","18.657962","9.328981","1.0"],["FI","3.4542","1.7271","1.0"],["FI","-12.335","-6.1675","1.0"],["FI","95.9728","47.9864","1.0"],["FI","2253.305","1126.6525","1.0"],["FI","-116.72779","-58.363895","1.0"],["FI","4.1268","2.0634","1.0"],["FI","-26.46269","-13.231345","1.0"],["FI","28.88","14.44","1.0"],["FI","0.089","0.0445","1.0"],["FI","-0.596","-0.298","1.0"],["FI","-2.15991","-1.079955","1.0"],["FI","16.359","8.1795","1.0"],["FI","-0.602","-0.301","1.0"],["FI","-4.0","-2.0","1.0"],["FI","-0.733","-0.3665","1.0"],["FI","2.77292","1.38646","1.0"],["FI","626.62","313.31","1.0"],["FI","14.8618","7.4309","1.0"],["FI","-0.206","-0.103","1.0"],["FI","-0.712297133","-0.3561485665","1.0"],["FI","-29.91113","-14.955565","1.0"],["FI","16.311","8.1555","1.0"],["FI","-0.874297133","-0.4371485665","1.0"],["FI","28.797236","14.398618","1.0"],["FI","-8.556","-4.278","1.0"],["FI","0.8854","0.4427","1.0"],["FI","2.8795","1.43975","1.0"],["FI","-0.57235","-0.286175","1.0"],["FI","111.2488","55.6244","1.0"],["FI","-0.586","-0.293","1.0"],["FI","-1.52189","-0.760945","1.0"],["FI","1.457","0.7285","1.0"],["FI","75.885924","37.942962","1.0"],["FI","81.58042","40.79021","1.0"],["FI","-32.325","-16.1625","1.0"],["FI","-2.315789474","-1.157894737","1.0"],["FI","-11.04668","-5.52334","1.0"],["FI","-0.33311","-0.166555","1.0"],["FI","14.87225","7.436125","1.0"],["FI","-62.989","-31.4945","1.0"],["FI","471.5572","235.7786","1.0"],["FI","-0.761594267","-0.3807971335","1.0"],["FI","-8.977","-4.4885","1.0"],["FI","1.0222","0.5111","1.0"],["FI","18.8214","9.4107","1.0"],["FI","-0.199","-0.0995","1.0"],["FI","-1.88546","-0.94273","1.0"],["FI","-6.267","-3.1335","1.0"],["FI","-7.69274","-3.84637","1.0"],["FI","-0.88415","-0.442075","1.0"],["FI","-0.3354","-0.1677","1.0"],["FI","-0.491","-0.2455","1.0"],["FI","4.76","2.38","1.0"],["FI","-12.113","-6.0565","1.0"],["FI","-1.92887","-0.964435","1.0"],["FI","272.338894","136.169447","1.0"],["FI","367.9502","183.9751","1.0"],["FI","-0.264","-0.132","1.0"]]}',
              "null",
              "null",
              "null"
            ],
            [
              "1535388674894",
              "1535388674893",
              "1541672409379",
              "Pulkit Barad",
              "First Sample Analysis #FirstAnalysis #Tutorial",
              "Total Amount, count of Transaction_Number and Card_Type by Country",
              "Total [Transaction][Amount][FLOAT], count of [Transaction][Transaction_Number][INTEGER] and [Credit_Card][Card_Type][STRING] by [Address][Country][STRING]",
              "",
              "",
              "",
              '{"Sales_Amount":"[Sales][Sales_Amount][Float64]","Customer_Name":"[Customer][Customer_Name][String]","Revenue":"[Account][Revenue][Int64]","Click_Count":"[Web_Traffic][Click_Count][Int64]","Expense":"[Account][Expense][Int64]","Amount":"[Transaction][Amount][FLOAT]","Card_Type":"[Credit_Card][Card_Type][STRING]","Country":"[Address][Country][STRING]","Transaction_Number":"[Transaction][Transaction_Number][INTEGER]"}',
              '{"Metadata":[{"DatasetName":"","EntityName":"98a461ede9e1ebe6d96ebb4260a57617","FieldName":"SUM_Amount","DataType":"FLOAT"},{"DatasetName":"","EntityName":"98a461ede9e1ebe6d96ebb4260a57617","FieldName":"COUNT_Transaction_Number","DataType":"INTEGER"},{"DatasetName":"","EntityName":"98a461ede9e1ebe6d96ebb4260a57617","FieldName":"Card_Type","DataType":"STRING"},{"DatasetName":"","EntityName":"98a461ede9e1ebe6d96ebb4260a57617","FieldName":"Country","DataType":"STRING"}],"Data":[["25006941443.559998","5002381","null","null"],["164689728.55000004","32845","Visa Gold","Ireland"],["165315261.32999998","32879","Visa Gold","Finland"],["161727688.81000006","32342","Visa Gold","Belgium"],["166703744.89000002","33168","Visa Gold","Luxembourg"],["168510252.09000006","33728","Visa Gold","Spain"],["165066367.65999997","32931","Visa Gold","Portugal"],["166379475.08999994","33305","Visa Gold","Bulgaria"],["168744347.06000012","33790","Visa Gold","Hungary"],["160983357.9199999","32236","Visa Gold","Cyprus"],["169273901.16000003","34013","Visa Gold","Denmark"],["159776420.07000005","31950","Visa Gold","Sweden"],["162585640.37999994","32442","Capital One","Czech Republic"],["160544688.30000007","32201","Capital One","Estonia"],["166159185.93000004","33319","Capital One","Greece"],["167674016.74","33660","Capital One","Belgium"],["165002037.72000003","33025","Capital One","Netherlands"],["159946133.60000014","32081","Capital One","Hungary"],["174516824.79000005","34823","Capital One","Germany"],["163849842.85000005","32803","Capital One","Italy"],["167845784.70000005","33514","Capital One","Ireland"],["168594349.98000002","33718","Capital One","Austria"],["162789783.82000005","32710","Capital One","Luxembourg"],["165368571.43000004","33160","Capital One","Sweden"],["167854163.05000004","33662","Visa Platinum","Netherlands"],["164640214.98000005","32951","Visa Platinum","Germany"],["161498842.26","32248","Visa Platinum","Sweden"],["166732934.90000007","33459","Visa Platinum","Austria"],["160666642.61999995","32292","Visa Platinum","Ireland"],["158960681.74","31548","Visa Platinum","Czech Republic"],["162132690.37000009","32465","Visa Platinum","Belgium"],["157080524.32000005","31199","Visa Platinum","Estonia"],["167624982.52000004","33541","Visa Platinum","Cyprus"],["160941506.83000004","32040","Visa Platinum","Spain"],["170104479.89","34259","Visa Platinum","Finland"],["160585300.69999996","32236","Visa Platinum","Portugal"],["169596226.01999998","33996","Visa Platinum","France"],["160148714.08","32237","Visa Platinum","Denmark"],["164791873.57999998","32983","Visa Platinum","Hungary"],["170345804.09","34130","Visa World Card","Netherlands"],["161670723.82000002","32332","Visa World Card","Denmark"],["160631274.44","32140","Visa World Card","Ireland"],["164031050.44000006","32891","Visa World Card","Spain"],["165309695.92000002","33046","Visa World Card","France"],["164478557.22999993","32999","Visa World Card","Hungary"],["169182182.54999995","33646","Visa World Card","Czech Republic"],["161287406.54000005","32147","Visa World Card","Italy"],["164811434.61","32876","Visa World Card","Belgium"],["165586277.12999997","32992","Visa World Card","Germany"],["161036357.14","32078","Visa World Card","Portugal"],["163602407.79","32647","Visa World Card","Luxembourg"],["163648729.48000002","32723","Visa World Card","Bulgaria"],["168788726.83","33772","Visa World Card","Sweden"],["160496420.07000005","32104","MasterCard Black","Ireland"],["166537688.84999996","33168","MasterCard Black","Italy"],["163859292.99","32704","MasterCard Black","Luxembourg"],["162487341.71999997","32555","MasterCard Black","Austria"],["155523944.22000006","30992","MasterCard Black","Netherlands"],["169124612.02","33755","MasterCard Black","Belgium"],["163170168.42000002","32625","MasterCard Black","Finland"],["164986332.49000004","33042","MasterCard Black","Hungary"],["161107748.36999992","32326","MasterCard Black","Estonia"],["153456196.51999989","30524","MasterCard Black","France"],["162410066.13","32368","MasterCard Black","Bulgaria"],["164355258.5","32790","MasterCard Black","Sweden"],["168467549.24","33589","MasterCard Black","Germany"],["161574078.1","32380","American Express Gold","Czech Republic"],["158837731.10000002","32011","American Express Gold","Hungary"],["162495911.06","32731","American Express Gold","Portugal"],["166053432.19","33291","American Express Gold","Belgium"],["162403821.25000003","32589","American Express Gold","Germany"],["153087088.16999996","30481","American Express Gold","Spain"],["169950991.55999997","34031","American Express Gold","Denmark"],["158283415.91000006","31668","American Express Gold","Cyprus"],["171544243.95000005","34235","American Express Gold","Sweden"],["165958271.23000002","33096","American Express Gold","France"],["167787688.85000002","33630","American Express Gold","Estonia"],["156432251.82000005","31086","MasterCard Flying Blue Gold","Spain"],["162321193.98000002","32632","MasterCard Flying Blue Gold","Sweden"],["171742857.69999996","34131","MasterCard Flying Blue Gold","Finland"],["169382269.49999991","33758","MasterCard Flying Blue Gold","Belgium"],["164747201.09","32869","MasterCard Flying Blue Gold","Cyprus"],["165353671.28999996","32893","MasterCard Flying Blue Gold","Luxembourg"],["165454599.89","33037","MasterCard Flying Blue Gold","Germany"],["162723657.40000007","32582","MasterCard Flying Blue Gold","France"],["157771734.35000005","31736","MasterCard Flying Blue Gold","Czech Republic"],["163884064.75000003","32869","MasterCard Flying Blue Gold","Estonia"],["166553455.16000003","33247","MasterCard Flying Blue Silver","Greece"],["161273605.05999997","32228","MasterCard Flying Blue Silver","Sweden"],["156535444.61","31349","MasterCard Flying Blue Silver","Germany"],["159253181.41000003","31873","MasterCard Flying Blue Silver","Bulgaria"],["171447300.00000003","34021","MasterCard Flying Blue Silver","Ireland"],["166596346.47000003","33240","MasterCard Flying Blue Silver","Belgium"],["157049175.45","31529","MasterCard Flying Blue Silver","Czech Republic"],["162572820.92999992","32562","MasterCard Flying Blue Silver","Luxembourg"],["170176396.80999994","34025","MasterCard Flying Blue Silver","Denmark"],["161787376.19","32305","MasterCard Flying Blue Silver","France"],["164391478.40999991","32950","MasterCard Flying Blue Silver","Finland"],["165981492.98","33237","MasterCard Flying Blue Silver","Cyprus"],["160824770.36000004","32224","MasterCard Flying Blue Silver","Italy"]]}',
              "null",
              "{}",
              "[]"
            ],
            [
              "1536278078716",
              "1536278078716",
              "1541395910625",
              "Pulkit Barad",
              "KPMG Demo Analysis #OldDemo #KPMG",
              "number of Country_facility_code, count of Level_0_GL_code and total Transaction_Value by Country",
              "number of [KPMG_Transaction_Normalized][Country_facility_code][STRING], count of [KPMG_Transaction_Normalized][Level_0_GL_code][INTEGER] and total [KPMG_Transaction_Normalized][Transaction_Value][INTEGER] by [KPMG_Transaction_Normalized][Country][STRING]",
              "",
              "",
              "",
              '{"Bank_Name":"[Bank][Bank_Name][STRING]","Account_Holder_Name":"[Account][Account_Holder_Name][STRING]","Country_facility_code":"[KPMG_Transaction_Normalized][Country_facility_code][STRING]","Country":"[KPMG_Transaction_Normalized][Country][STRING]","Level_0_GL_code":"[KPMG_Transaction_Normalized][Level_0_GL_code][INTEGER]","Transaction_Value":"[KPMG_Transaction_Normalized][Transaction_Value][INTEGER]","Transaction_Date":"[KPMG_Transaction_Normalized][Transaction_Date][DATE]"}',
              '{"Metadata":[{"DatasetName":"","EntityName":"f8419064788efb000c053428ccf7ba0a","FieldName":"COUNT_Country_facility_code","DataType":"INTEGER"},{"DatasetName":"","EntityName":"f8419064788efb000c053428ccf7ba0a","FieldName":"COUNT_Level_0_GL_code","DataType":"INTEGER"},{"DatasetName":"","EntityName":"f8419064788efb000c053428ccf7ba0a","FieldName":"SUM_Transaction_Value","DataType":"INTEGER"},{"DatasetName":"","EntityName":"f8419064788efb000c053428ccf7ba0a","FieldName":"Country","DataType":"STRING"}],"Data":[["35631","29933","424948","PT"],["333333","333333","439292","FR"],["925","0","null","null"],["0","111","12","FI"],["3147109","3147109","1346114","ES"]]}',
              "null",
              "{}",
              "null"
            ]
          ]
        };
        handleResponse(data);
      }
    });
  var handleResponse = function (data) {
    if (data.StatusCode && data.StatusCode === 200) {
      if (data && data.Content && data.Content.length > 0) {
        var loadedAnalyses = data.Content.map(function (row) {
          var analysis = {};
          analysis.AnalysisId = row[0];
          analysis.CreatedAt = parseInt(row[1]);
          analysis.LastModifiedAt = parseInt(row[2]);
          analysis.Author = row[3];
          analysis.Author = row[3];
          analysis.Title = row[4];
          analysis.Selection = row[5];
          analysis.SelectionRaw = row[6];
          analysis.Filter = row[7];
          analysis.FilterRaw = row[8];
          analysis.ResultTableName = row[9];
          analysis.AliasMapping = JSON.parse(row[10]);
          analysis.Result = JSON.parse(row[11]);
          analysis.ProfilingResult = JSON.parse(row[12]);
          analysis.Pattern = JSON.parse(row[13]);
          analysis.Visualization = JSON.parse(row[14]);
          return analysis;
        }).sort(function (l, r) {
          if (l.LastModifiedAt > r.LastModifiedAt) return -1;
          if (l.LastModifiedAt < r.LastModifiedAt) return 1;
          return 0;
        });
        loadedAnalyses.forEach(function (analysis) {
          historyAnalyses[analysis.AnalysisId] = analysis;
        });
        renderHistorySection(loadedAnalyses);
      }
      $("#ajaxLoader").removeClass("is-active");
    } else {
      $("#ajaxLoader").removeClass("is-active");
      // window.location = 'https://eva.nuocanvas.ai';
    }
  };
}

function changeTextOnTag(arrText, analysisId) {
  return arrText.map((item) => {
    var has_comma = item.search(",") !== -1;
    for (var alias in historyAnalyses[analysisId].AliasMapping) {
      if (has_comma) {
        item = item.replace(",", "")
      }
      if (alias.trim() === item.trim()) {
        return item = "<span  class='tag' contenteditable='false'><span>" + item + "</span><a class='dismiss'></a></span>" + (has_comma ? ',&nbsp;' : '&nbsp;');
      }
    }
    return item = item + (has_comma ? ',&nbsp;' : '&nbsp;');
  });
}

function renderHistorySection(loadedAnalyses) {
  $("#ajaxLoader").addClass("is-active");
  var firstAnalysis = true;
  loadedAnalyses.forEach(function (historyAnalysis) {
    $("#historyBody").append(getHistoryRowTag(historyAnalysis));
    var analysisId = historyAnalysis.AnalysisId;
    $("#historyAnalysisResultTableInput" + analysisId).hide();

    var historyAnalysis_Selection = historyAnalysis.Selection.split(/\s+/g);
    var historyAnalysis_Filter = historyAnalysis.Filter.split(/\s+/g);

    historyAnalysis_Selection = changeTextOnTag(historyAnalysis_Selection, analysisId);
    historyAnalysis_Filter = changeTextOnTag(historyAnalysis_Filter, analysisId);


    $("#historyAnalysisSelectionInput" + analysisId).html(historyAnalysis_Selection);
    $("#historyAnalysisFilterInput" + analysisId).html(historyAnalysis_Filter);
    $("#historyAnalysisTitleInput" + analysisId).val(historyAnalysis.Title);

    if (historyAnalysis.ResultTableName && historyAnalysis.ResultTableName !== "null" && historyAnalysis.ResultTableName !== "No Result Table") {

      $("#historyAnalysisResultTableInput" + analysisId).val(historyAnalysis.ResultTableName);
    } else {
      $("#historyAnalysisResultTableInput" + analysisId).val("No Result Table");

    }

    var $element = $("#historyAnalysisSelectionInput" + analysisId + "[contenteditable]");
    var $elementF = $("#historyAnalysisFilterInput" + analysisId + "[contenteditable]");
    if ($element.html().length && !$element.text().trim().length) {
      $element.empty();
    }
    if ($elementF.html().length && !$elementF.text().trim().length) {
      $elementF.empty();
    }
    addAnalysisEventHandlers(historyAnalysis.AnalysisId);
    $('.dismiss').click((e) => {  //btn delete tag
      dismiss(e);
    });

    if (
      firstAnalysis &&
      historyAnalysis.Result &&
      historyAnalysis.Result.Data &&
      historyAnalysis.Result.Data.length > 0
    ) {
      drawDashboard(historyAnalysis);
      firstAnalysis = false;
    } else {

      $("#historyAnalysisMinimizeImage" + analysisId).attr(
        "src",
        "./public/maximize_128x128.png"
      );
      $("#historyAnalysisMinimizeImage" + analysisId).attr("title", "Maximize");
      $("#historyAnalysisMinimizeImage" + analysisId).removeClass("_minimize");
    }
  });
  $("#ajaxLoader").removeClass("is-active");
}

function getChartDataFromResult(result, chartFilters) {
  var chartData = [];
  for (var index = 0; index < result.Metadata.length; index++) {
    var nuoField = result.Metadata[index];
    chartData.push({
      nuoField: nuoField,
      data: result.Data.map(function (row) {
        return row[index];
      })
    });
  }
  var invalidIndices = [];

  if (chartFilters && chartFilters.length > 0) {
    chartData.forEach(function (series) {
      if (chartFilters && chartFilters.length > 0) {
        var filter = chartFilters.find(function (chartFilter) {
          return (
            series.nuoField.FieldName.toLowerCase() ===
            chartFilter.nuoField.FieldName.toLowerCase()
          );
        });

        if (filter) {
          isCountryCategory(series);
          var index = 0;
          series.data.forEach(function (cellValue) {
            if (filter.data.indexOf(cellValue) < 0) {
              invalidIndices.push(index);
            }
            index++;
          });
        }
      }
    });
  }
  chartData = chartData.map(function (series) {
    var index = -1;
    series.data = series.data.filter(function () {
      index++;
      if (invalidIndices.length > 0 && invalidIndices.indexOf(index) >= 0)
        return false;
      return true;
    });
    if (series.nuoField.DataType.toLowerCase() === "string") {
      isCountryCategory(series);
    }
    return series;
  });
  return chartData;
}

function drawDashboard(historyAnalysis) {
  var chartData = getChartDataFromResult(
    historyAnalysis.Result,
    historyAnalysis.ChartFilters
  );
  var analysisId = historyAnalysis.AnalysisId;
  if (chartData.length > 0 && chartData[0].data.length > 0) {
    var stringSeriesGroup = getStringSeries(chartData).slice(0, 5);
    var numberSeriesGroup = getNumberSeries(chartData).slice(0, 10);
    var dateSeriesGroup = getDateSeries(chartData).slice(0, 5);

    var recognizedCharts = [];

    var latitude =
      numberSeriesGroup
        .filter(function (ele) {
          return ["latitude", "lat"].indexOf(ele.nuoField.FieldName.toLowerCase()) >= 0
        });
    var longitude = numberSeriesGroup.filter(function (ele) { return ["longitude", "long"].indexOf(ele.nuoField.FieldName.toLowerCase()) >= 0 });

    if (latitude && latitude.length == 1 && longitude && longitude.length == 1) {

      numberSeriesGroup =
        numberSeriesGroup
          .filter(function (nSeries) {

            return [
              latitude[0].nuoField.FieldName,
              longitude[0].nuoField.FieldName
            ].indexOf(nSeries.nuoField.FieldName) < 0
          });

      numberSeriesGroup
        .forEach(function (nSeries) {
          recognizedCharts.push({
            categorySeries: findLocationSeries(stringSeriesGroup),
            numberSeriesGroup: [latitude[0], longitude[0], nSeries],
            chartType: "scattergeo"
          });
        });

    }

    var stackedNumberSeriesGroups = findStackedSeriesGroups(numberSeriesGroup);
    var allCloseNumberSeriesGroups = findAllCloseSeriesGroups(
      numberSeriesGroup
    );
    var dualNumberSeriesGroups = findDualSeriesGroups(
      allCloseNumberSeriesGroups
    );

    var alreadyCorrNumberSeriesLabels =
      stackedNumberSeriesGroups
        .concat(
          dualNumberSeriesGroups.map(function (seriesGroups) {
            return seriesGroups[0].concat(seriesGroups[1]);
          })
        )
        .concat(allCloseNumberSeriesGroups)
        .map(function (seriesGroup) {
          var label = seriesGroup
            .map(function (series) {
              return series.nuoField.FieldName;
            })
            .sort()
            .join("|~|");
          return label;
        });

    createCombinations(numberSeriesGroup, 3)
      .filter(function (seriesGroup) {
        var label = seriesGroup
          .map(function (series) {
            return series.nuoField.FieldName;
          })
          .sort()
          .join("|~|");
        return alreadyCorrNumberSeriesLabels.indexOf(label) < 0;
      })
      .forEach(function (seriesGroup) {
        var chartType = "scatter3d";
        if (scatter3dPlotCount >= 4) {
          chartType = "bubble";
        } else {
          scatter3dPlotCount += 1;
        }
        recognizedCharts.push({
          numberSeriesGroup: seriesGroup,
          chartType: chartType
        });
      });

    stringSeriesGroup.concat(dateSeriesGroup).forEach(function (sSeries) {
      var maxSeriesNameLength = sSeries.data
        .map(function (value) {
          return value.length;
        })
        .reduce(function (acc, l) {
          return Math.max(acc, l);
        });
      if (isCountryCategory(sSeries)) {
        numberSeriesGroup.forEach(function (nSeries) {
          recognizedCharts.push({
            categorySeries: sSeries,
            numberSeriesGroup: [nSeries],
            chartType: "geo-region"
          });
        });
      }
      stackedNumberSeriesGroups.forEach(function (seriesGroup) {
        recognizedCharts.push({
          categorySeries: sSeries,
          numberSeriesGroup: seriesGroup,
          chartType: "stacked"
        });
      });

      if (dualNumberSeriesGroups.length > 0) {
        // combine both series groups in combined bar-line chart
        dualNumberSeriesGroups.forEach(function (seriesGroups) {
          var updatedSeriesGroups = seriesGroups[0]
            .map(function (series) {
              var clone = JSON.parse(JSON.stringify(series));
              clone.chartType = "bar";
              // if (seriesGroups[0].length > 1)
              // 	clone.chartType = "bar"
              // else
              // 	clone.chartType = "line"
              return clone;
            })
            .concat(
              seriesGroups[1].map(function (series) {
                var clone = JSON.parse(JSON.stringify(series));
                clone.chartType = "line";
                // if (seriesGroups[1].length > 1)
                // 	clone.chartType = "bar"
                // else
                // 	clone.chartType = "line"
                return clone;
              })
            );
          if (
            seriesGroups.reduce(function (acc, r) {
              return acc + r.length;
            }, 0) == 3
          ) {
            recognizedCharts.push({
              numberSeriesGroup: [].concat.apply([], seriesGroups),
              chartType: "bubble"
            });
          } else {
            recognizedCharts.push({
              categorySeries: sSeries,
              numberSeriesGroup: [].concat.apply([], updatedSeriesGroups),
              chartType: "combined"
            });
          }
        });
      }

      allCloseNumberSeriesGroups.forEach(function (seriesGroup) {
        var total = null;
        var chartType = "";
        var areaChartCount = recognizedCharts
          .map(function (ele) {
            return ele.chartType;
          })
          .filter(function (type) {
            return type == "area";
          }).length;
        var barChartCount = recognizedCharts
          .map(function (ele) {
            return ele.chartType;
          })
          .filter(function (type) {
            return type == "bar";
          }).length;
        if (maxSeriesNameLength <= 20 && barChartCount >= areaChartCount) {
          //draw a multi area chart for the series group
          chartType = "area";
        } else {
          if (maxSeriesNameLength > 20) {
            //draw a horizontal multi bar chart for the series group
            chartType = "horizontalBar";
          } else {
            //draw a vertical multi bar chart for the series group
            chartType = "bar";
          }
        }
        recognizedCharts.push({
          categorySeries: sSeries,
          numberSeriesGroup: seriesGroup,
          chartType: chartType
        });
      });
    });

    //create doughnut or radar charts for each category
    stringSeriesGroup.forEach(function (sSeries) {
      var doughnutCharts = [];

      numberSeriesGroup
        .filter(function (nSeries) {
          return nSeries.data.reduce(function (acc, r) { return acc || r !== 0 }, false);
        })
        .forEach(function (nSeries) {
          //draw a doughnut or radar chart with sSeries and nSeries
          var doughnutChartCount = recognizedCharts
            .map(function (ele) {
              return ele.chartType;
            })
            .filter(function (type) {
              return type == "doughnut";
            }).length;
          var radarChartCount = recognizedCharts
            .map(function (ele) {
              return ele.chartType;
            })
            .filter(function (type) {
              return type == "radar";
            }).length;
          var chartType = "radar";
          if (radarChartCount >= doughnutChartCount) chartType = "doughnut";

          recognizedCharts.push({
            categorySeries: sSeries,
            numberSeriesGroup: [nSeries],
            chartType: chartType
          });
        });
    });
    createCombinations(stringSeriesGroup, 2).map(function (seriesGroup) {
      var combination = {};
      var x = { data: [] };
      var y = { data: [] };
      var z = { data: [] };
      numberSeriesGroup.forEach(function (nSeries) {
        var rowCount = nSeries.data.length;

        for (var i = 0; i < rowCount; i++) {
          var pair = seriesGroup[0].data[i] + "|~|" + seriesGroup[1].data[i];
          if (combination.hasOwnProperty(pair)) {
            combination[pair] += nSeries.data[i];
          } else {
            combination[pair] = nSeries.data[i];
          }
        }

        for (var i = 0; i < Object.keys(combination).length; i++) {
          var pair = Object.keys(combination)[i].split("|~|");
          x.data[i] = pair[0];
          y.data[i] = pair[1];
          z.data[i] = Object.values(combination)[i];
        }
        x.nuoField = stringSeriesGroup[0].nuoField;
        y.nuoField = stringSeriesGroup[1].nuoField;
        z.nuoField = nSeries.nuoField;
        recognizedCharts.push({
          categorySeriesGroup: [x, y],
          numberSeries: nSeries,
          chartType: "heatmap"
        });
      });
    });
    var chartCounter = 1;
    $("#historyAnalysisDashboard" + analysisId).empty();
    createDashboardSummary(
      historyAnalysis,
      chartCounter,
      stringSeriesGroup,
      numberSeriesGroup
    );
    chartCounter++;
    createDashboardDataGrid(historyAnalysis, chartCounter);
    chartCounter++;

    recognizedCharts.forEach(function (ele) {
      var chartConfig = JSON.parse(JSON.stringify(ele));
      if (chartConfig.numberSeriesGroup && chartConfig.chartType !== "scattergeo") {
        var updCategorySeries = chartConfig.categorySeries;

        chartConfig.numberSeriesGroup = chartConfig.numberSeriesGroup.map(
          function (nSeries) {
            var updSeries = nSeries;

            if (
              chartConfig.categorySeries &&
              chartConfig.categorySeries.data.length > 0
            ) {
              var aggResult = aggregateSeries(
                chartConfig.categorySeries,
                nSeries
              );
              updCategorySeries.data = aggResult[0];
              updSeries.data = aggResult[1];
            }

            return updSeries;
          }
        );
        chartConfig.categorySeries = updCategorySeries;
        var sortedSeriesGroup = sortSeriesGroups(chartConfig.categorySeries, chartConfig.numberSeriesGroup);
        chartConfig.categorySeries = sortedSeriesGroup.categorySeries;
        chartConfig.numberSeriesGroup = sortedSeriesGroup.numberSeriesGroup;
      }

      if (

        chartConfig.chartType == "geo-region"
        || chartConfig.chartType == "scattergeo"
        || (chartConfig.numberSeriesGroup && chartConfig.numberSeriesGroup.filter(function (series) { return series.data.length > 1 }).length > 0)
        || (chartConfig.categorySeriesGroup && chartConfig.categorySeriesGroup.filter(function (series) { return series.data.length > 1 }).length > 0)
      ) {

        if (chartConfig.chartType == "doughnut" || chartConfig.chartType == "radar") {

          var stat = computeSeriesStatistics(chartConfig.numberSeriesGroup[0].data);

          if (new Set(chartConfig.categorySeries.data).size <= 25 || stat.max >= stat.sum * 0.25) {
            drawAnalysisChart(historyAnalysis, chartCounter, chartConfig);
            chartCounter++;
          }
        } else {
          drawAnalysisChart(historyAnalysis, chartCounter, chartConfig);
          chartCounter++;
        }
      }
    });
    if (
      historyAnalysis.ChartFilters &&
      historyAnalysis.ChartFilters.length > 0
    ) {
      $("#historyAnalysisResetButton" + analysisId).show();
    } else {
      $("#historyAnalysisResetButton" + analysisId).hide();
    }
  } else {
    if ($("#staticDataGrid" + analysisId).length === 0) {
      createDataGridDivContent(
        analysisId,
        "historyAnalysisDashboard" + analysisId
      );
    }
    $("#historyAnalysisDashboard" + analysisId + " > .historyAnalysisChartContainer").remove();
    $("#staticDataGrid" + analysisId).html("<h1>No matching results for your search terms found</h1>");
  }
}

function findLocationSeries(stringSeriesGroup) {

  var locationSeries =
    stringSeriesGroup.find(function (ele) {

      return ele.nuoField.FieldName.toLowerCase().indexOf("name") >= 0
    });

  if (!locationSeries || locationSeries.data.length == 0) {

    locationSeries =

      stringSeriesGroup.find(function (ele) {

        return ele.nuoField.FieldName.toLowerCase().indexOf("house") >= 0
          || ele.nuoField.FieldName.toLowerCase().indexOf("home") >= 0
          || ele.nuoField.FieldName.toLowerCase().indexOf("street") >= 0
          || ele.nuoField.FieldName.toLowerCase().indexOf("neighborhood") >= 0
          || ele.nuoField.FieldName.toLowerCase().indexOf("shop") >= 0
          || ele.nuoField.FieldName.toLowerCase().indexOf("business") >= 0

      });
  }

  if (!locationSeries || locationSeries.data.length == 0) {

    locationSeries =
      stringSeriesGroup.find(function (ele) {

        return ele.nuoField.FieldName.toLowerCase().indexOf("city") >= 0
          || ele.nuoField.FieldName.toLowerCase().indexOf("area") >= 0
          || ele.nuoField.FieldName.toLowerCase().indexOf("town") >= 0
          || ele.nuoField.FieldName.toLowerCase().indexOf("village") >= 0
          || ele.nuoField.FieldName.toLowerCase().indexOf("district") >= 0
      });

  }

  if (!locationSeries || locationSeries.data.length == 0) {

    locationSeries =
      stringSeriesGroup.find(function (ele) {

        return ele.nuoField.FieldName.toLowerCase().indexOf("state") >= 0
          || ele.nuoField.FieldName.toLowerCase().indexOf("province") >= 0
      });
  }

  if (!locationSeries || locationSeries.data.length == 0) {

    locationSeries = stringSeriesGroup[0];
  }

  return locationSeries
}

function isCountryCategory(series) {
  var isCountryName =
    series.data.filter(function (value) {
      return countryNames.indexOf(value.toLowerCase()) >= 0;
    }).length >=
    series.data.length * 0.5;

  var isCountryIso2Code =
    series.data.filter(function (value) {
      return countryIso2Codes.indexOf(value.toLowerCase()) >= 0;
    }).length >=
    series.data.length * 0.5;

  var isCountryIso3Code =
    series.data.filter(function (value) {
      return countryIso3Codes.indexOf(value.toLowerCase()) >= 0;
    }).length >=
    series.data.length * 0.5;

  if (isCountryIso2Code) {
    series.data = series.data.map(function (value) {
      var index = countryIso2Codes.indexOf(value.toLowerCase());
      if (index >= 0) return toTitleCase(countryNames[index]);
      else return value;
    });
  } else if (isCountryIso3Code) {
    series.data = series.data.map(function (value) {
      var index = countryIso3Codes.indexOf(value.toLowerCase());
      if (index >= 0) return toTitleCase(countryNames[index]);
      else return value;
    });
  }
  return isCountryName || isCountryIso2Code || isCountryIso3Code;
}

function narrateDashboard(historyAnalysis, stringSeriesGroup, dateSeriesGroup, numberSeriesGroup, languageCode) {

  var categorySeriesGroup = dateSeriesGroup.concat(stringSeriesGroup);

  var narratedCombinations = [];

  var dashboardNarrativeText = '<speak> <amazon:auto-breaths> <prosody pitch="medium">';

  var analysisTitle = historyAnalysis.Title.replace(/#\s*\w+/g, "")



  if (analysisTitle.trim().length > 0) {
    analysisTitle = "New Analysis";
  }

  // if (languageCode && languageCode.toLowerCase() === "nl") {

  //   // dashboardNarrativeText += '<s>Mijn ';
  //   // dashboardNarrativeText += '<emphasis level="moderate">';
  //   // dashboardNarrativeText += " toelichting";
  //   // dashboardNarrativeText += '</emphasis>';
  //   // dashboardNarrativeText += ' voor analysis ';
  //   // dashboardNarrativeText += '<emphasis level="moderate">';
  //   // dashboardNarrativeText += analysisTitle;
  //   // dashboardNarrativeText += '</emphasis>';
  //   // dashboardNarrativeText += ' is as volgt</s><break time="250ms"/>';
  // } else {
  // }
  dashboardNarrativeText += '<s>My ';
  dashboardNarrativeText += '<emphasis level="moderate">';
  dashboardNarrativeText += " narrative";
  dashboardNarrativeText += '</emphasis>';
  dashboardNarrativeText += ' for analysis ';
  dashboardNarrativeText += '<emphasis level="moderate">';
  dashboardNarrativeText += analysisTitle;
  dashboardNarrativeText += '</emphasis>';
  dashboardNarrativeText += ' is as follows</s><break time="250ms"/>';
  categorySeriesGroup
    .forEach(function (sSeries) {

      numberSeriesGroup.forEach(function (nSeries) {

        var refNumberSeriesGroup =
          numberSeriesGroup
            .filter(function (refNumberSeries) {
              return refNumberSeries.nuoField.FieldName !== nSeries.nuoField.FieldName;
            })
        if (refNumberSeriesGroup.length > 0) {

          refNumberSeriesGroup
            .forEach(function (refNumberSeries) {

              var minNarrativeStats = getNarrativeStats(categorySeriesGroup, sSeries, nSeries, refNumberSeries, true);
              var maxNarrativeStats = getNarrativeStats(categorySeriesGroup, sSeries, nSeries, refNumberSeries, false);


              if (minNarrativeStats.orgNumberValue === maxNarrativeStats.orgNumberValue) {
                dashboardNarrativeText += generateNarrativeTextEnglish(narratedCombinations, sSeries, nSeries, refNumberSeries, minNarrativeStats, null);
              } else {

                dashboardNarrativeText += " " + generateNarrativeTextEnglish(narratedCombinations, sSeries, nSeries, refNumberSeries, minNarrativeStats, "lowest");
                dashboardNarrativeText += " " + generateNarrativeTextEnglish(narratedCombinations, sSeries, nSeries, refNumberSeries, maxNarrativeStats, "highest");
              }
            });
        } else {
          var minNarrativeStats = getNarrativeStats(categorySeriesGroup, sSeries, nSeries, null, true);
          var maxNarrativeStats = getNarrativeStats(categorySeriesGroup, sSeries, nSeries, null, false);


          if (minNarrativeStats.orgNumberValue === maxNarrativeStats.orgNumberValue) {
            dashboardNarrativeText += generateNarrativeTextEnglish(narratedCombinations, sSeries, nSeries, null, minNarrativeStats, null);
          } else {

            dashboardNarrativeText += " " + generateNarrativeTextEnglish(narratedCombinations, sSeries, nSeries, null, minNarrativeStats, "lowest");
            dashboardNarrativeText += " " + generateNarrativeTextEnglish(narratedCombinations, sSeries, nSeries, null, maxNarrativeStats, "highest");
          }

        }
      });
    });

  return dashboardNarrativeText + "</prosody> </amazon:auto-breaths> </speak>";
}

function generateNarrativeTextEnglish(narratedCombinations, sSeries, nSeries, refNumberSeries, narrativeStats, superlativePrefix) {
  var narrativeText = "";


  if (!narratedCombinations.includes(narrativeStats.orgCategoryValue + nSeries.nuoField.FieldName)) {
    narratedCombinations.push(narrativeStats.orgCategoryValue + nSeries.nuoField.FieldName)

    narrativeText += '<s><say-as interpret-as="digits">For '
    if (
      narrativeStats.orgCategoryValue &&
      narrativeStats.orgCategoryValue.toLowerCase() !== "null" &&
      narrativeStats.orgCategoryValue.trim().length > 0
    ) {
      narrativeText += narrativeStats.orgCategoryValue;
    } else {
      narrativeText += "Unknown " + sSeries.nuoField.FieldName;
    }
    narrativeText += '</say-as><break time="250ms"/> <say-as interpret-as="digits">'

    narrativeText += nSeries.nuoField.FieldName + "</say-as> is " + getNarrativeNumber(narrativeStats.orgNumberValue);
    if (superlativePrefix && superlativePrefix.trim().length > 0) {
      narrativeText += ", which is the ";
      narrativeText += '<emphasis level="moderate">';
      narrativeText += superlativePrefix;
      narrativeText += '</emphasis>';
      narrativeText += ` across the  <say-as interpret-as="digits">` + sSeries.nuoField.FieldName;
      narrativeText += "</say-as>."
    }
    narrativeText += '</s><break time="250ms"/>'

    if (
      refNumberSeries &&
      narrativeStats.orgCategoryValue &&
      narrativeStats.orgCategoryValue.toLowerCase() !== "null" &&
      narrativeStats.orgCategoryValue.trim().length > 0
    ) {

      if (!narratedCombinations.includes(narrativeStats.orgCategoryValue + refNumberSeries.nuoField.FieldName)) {
        narratedCombinations.push(narrativeStats.orgCategoryValue + refNumberSeries.nuoField.FieldName)

        narrativeText += '<amazon:breath/><s>For <say-as interpret-as="digits">' + narrativeStats.orgCategoryValue + "</say-as>, ";

        if (narrativeStats.refNumberValue != null) {

          narrativeText += '<say-as interpret-as="digits">' + refNumberSeries.nuoField.FieldName + "</say-as> is " + getNarrativeNumber(narrativeStats.refNumberValue);

          var relativeToAvgPercent = ((narrativeStats.refNumberValue - narrativeStats.refNumberAvg) / narrativeStats.refNumberTotal * 100.0).toFixed(2);

          if (relativeToAvgPercent != 0) {

            narrativeText += '<break time="250ms"/>'
            narrativeText += "which is " + Math.abs(relativeToAvgPercent) + "%";

            if (relativeToAvgPercent > 0) {
              narrativeText += '<emphasis level="moderate">';
              narrativeText += "above";
              narrativeText += '</emphasis>';
            } else {
              narrativeText += '<emphasis level="moderate">';
              narrativeText += " below";
              narrativeText += '</emphasis>';
            }
            narrativeText += " the average of " + getNarrativeNumber(narrativeStats.refNumberAvg);
          }
          narrativeText += '</s><amazon:breath/><break time="750ms"/>'

        }
      }
    }
  }
  return narrativeText.replaceAll("_", " ").replaceAll("  ", " ");
}

// function generateNarrativeTextDutch(narratedCombinations, sSeries, nSeries, refNumberSeries, narrativeStats, superlativePrefix) {
//   var narrativeText = "";


//   if (!narratedCombinations.includes(narrativeStats.orgCategoryValue + nSeries.nuoField.FieldName)) {
//     narratedCombinations.push(narrativeStats.orgCategoryValue + nSeries.nuoField.FieldName)

//     narrativeText += ' <say-as interpret-as="digits"><s>Voor '
//     if (
//       narrativeStats.orgCategoryValue &&
//       narrativeStats.orgCategoryValue.toLowerCase() !== "null" &&
//       narrativeStats.orgCategoryValue.trim().length > 0
//     ) {
//       narrativeText += narrativeStats.orgCategoryValue;
//     } else {
//       narrativeText += "onbekende " + sSeries.nuoField.FieldName;
//     }
//     narrativeText += '<break time="250ms"/>'

//     narrativeText += " is " + nSeries.nuoField.FieldName + "</say-as> " + getNarrativeNumber(narrativeStats.orgNumberValue, true);
//     if (superlativePrefix && superlativePrefix.trim().length > 0) {
//       narrativeText += ", wat het ";
//       narrativeText += '<emphasis level="moderate">';
//       narrativeText += superlativePrefix;
//       narrativeText += '</emphasis>';
//       narrativeText += ' is over het <say-as interpret-as="digits">' + sSeries.nuoField.FieldName+"</say-as>";
//     }
//     narrativeText += '</s><amazon:breath/><break time="250ms"/>'

//     if (
//       narrativeStats.orgCategoryValue &&
//       narrativeStats.orgCategoryValue.toLowerCase() !== "null" &&
//       narrativeStats.orgCategoryValue.trim().length > 0
//     ) {

//       if (!narratedCombinations.includes(narrativeStats.orgCategoryValue + refNumberSeries.nuoField.FieldName)) {
//         narratedCombinations.push(narrativeStats.orgCategoryValue + refNumberSeries.nuoField.FieldName)

//         narrativeText += '<say-as interpret-as="digits"><s>Voor ' + narrativeStats.orgCategoryValue + "</say-as>,";

//         if (narrativeStats.refNumberValue != null) {

//           narrativeText += '<break time="250ms"/>'
//           narrativeText += ' is <say-as interpret-as="digits">' + refNumberSeries.nuoField.FieldName + "</say-as> " + getNarrativeNumber(narrativeStats.refNumberValue, true);

//           var relativeToAvgPercent = ((narrativeStats.refNumberValue - narrativeStats.refNumberAvg) / narrativeStats.refNumberTotal * 100.0).toFixed(2);

//           if (relativeToAvgPercent != 0) {

//             narrativeText += '<break time="250ms"/>'
//             narrativeText += " dat is " + Math.abs(relativeToAvgPercent) + "%";

//             narrativeText += '<emphasis level="moderate">';
//             if (relativeToAvgPercent > 0) {
//               narrativeText += 'boven';
//             } else {
//               narrativeText += 'onder';
//             }
//             narrativeText += '</emphasis>';
//             narrativeText += "het gemiddlede van " + getNarrativeNumber(narrativeStats.refNumberAvg, true);
//           }
//           narrativeText += '</s><amazon:breath/><break time="750ms"/>'
//         }
//       }
//     }
//   }

//   return narrativeText.replaceAll("_", " ").replaceAll("  ", " ");
// }

function getNarrativeStats(categorySeriesGroup, sSeries, nSeries, refNumberSeries, isMinimumRef) {

  var updSeriesGroup = aggregateSeries(sSeries, nSeries);
  var aggCategorySeries = updSeriesGroup[0];
  var aggNumberSeries = updSeriesGroup[1];

  var orgNumberValue = null;
  if (isMinimumRef === true) {

    orgNumberValue = aggNumberSeries.reduce(function (acc, r) { return Math.min(acc, r) });
  } else {
    orgNumberValue = aggNumberSeries.reduce(function (acc, r) { return Math.max(acc, r) });
  }
  var orgNumberTotal = aggNumberSeries.reduce(function (acc, r) { return acc + r; });
  var orgNumberAvg = orgNumberTotal / aggNumberSeries.length;

  var index = 0;
  var orgCategoryValue = null;
  aggNumberSeries.forEach(function (value) {
    if (value == orgNumberValue) {

      orgCategoryValue = aggCategorySeries[index]
    }
    index += 1;
  })

  var refValues =
  {
    orgCategoryValue: orgCategoryValue,
    orgNumberValue: orgNumberValue,
    orgNumberAvg: orgNumberAvg,
    orgNumberTotal: orgNumberTotal
  }
  if (refNumberSeries && refNumberSeries.data.length > 0) {

    var refCategorySeries = categorySeriesGroup.find(function (catSeries) {

      return catSeries.data.includes(orgCategoryValue);
    })

    var updRefSeriesGroup = aggregateSeries(refCategorySeries, refNumberSeries);
    var refNumberTotal = updRefSeriesGroup[1].reduce(function (acc, r) { return acc + r; }) / updRefSeriesGroup[1].length;
    var refNumberAvg = refNumberTotal / updRefSeriesGroup[1].length;

    var refNumberValue = null;
    index = 0;
    updRefSeriesGroup[0].forEach(function (value) {
      if (value == orgCategoryValue) {

        refNumberValue = updRefSeriesGroup[1][index]
      }
      index += 1;
    })
    refValues.refNumberValue = refNumberValue;
    refValues.refNumberAvg = refNumberAvg;
    refValues.refNumberTotal = refNumberTotal;
  }
  return refValues;
}

function sortSeriesGroups(categorySeries, numberSeriesGroup) {

  var numberSeriesHead = numberSeriesGroup[0];
  var sortedSeriesGroup =
  {
    categorySeries: JSON.parse(JSON.stringify(categorySeries)),
    numberSeriesGroup: JSON.parse(JSON.stringify(numberSeriesGroup))
  };
  var categorySeriesData = categorySeries.data;
  var numberSeriesDataGroups = numberSeriesGroup.map(function (ele) { return ele.data; });

  var counter = 0;
  numberSeriesHead.data.map(function (ele) {
    var updEle = {
      value: ele,
      index: counter
    };
    counter += 1;
    return updEle;
  })
    .sort(function (l, r) {
      return l.value - r.value;
    })
    .forEach(function (ele, i) {

      sortedSeriesGroup.categorySeries.data[i] = categorySeriesData[ele.index];

      numberSeriesDataGroups
        .forEach(function (numSeries, j) {
          sortedSeriesGroup.numberSeriesGroup[j].data[i] = numSeries[ele.index];
        });
    });
  return sortedSeriesGroup;
}

function aggregateSeries(stringSeries, numberSeries) {
  var rowCount = stringSeries.data.length;
  var categorySum = {};
  for (var r = 0; r < rowCount; r++) {
    var category = stringSeries.data[r].trim();

    if (categorySum.hasOwnProperty(category)) {
      if (numberSeries.data[r] != null)
        categorySum[category] += numberSeries.data[r];
    } else {
      if (numberSeries.data[r] != null)
        categorySum[category] = numberSeries.data[r];
    }
  }
  return [Object.keys(categorySum), Object.values(categorySum)];
}

function findAllCloseSeriesGroups(seriesCollection) {
  var remainingColleciton = seriesCollection;
  var allCloseNumberSeriesGroups = findCloseSeriesGroups(remainingColleciton);

  remainingColleciton = seriesCollection
    .filter(function (series) {
      var groupedSeriesNames = allCloseNumberSeriesGroups.flatMap(function (
        seriesGroup
      ) {
        var result = seriesGroup.map(function (seriesFromGroup) {
          return seriesFromGroup.nuoField.FieldName;
        });
        return result;
      });
      return groupedSeriesNames.indexOf(series.nuoField.FieldName) < 0;
    })
    .map(function (series) {
      return [series];
    });
  allCloseNumberSeriesGroups = allCloseNumberSeriesGroups.concat(
    remainingColleciton
  );

  return allCloseNumberSeriesGroups;
}

function findCloseSeriesGroups(seriesCollection) {
  var combinations = createCombinations(seriesCollection);
  var closeSeriesGroups = combinations.filter(function (seriesGroup) {
    var seriesStat = computeSeriesStatistics(
      seriesGroup.map(function (series) {
        return computeSeriesStatistics(series.data).mean;
      })
    );
    var validCount = seriesGroup.filter(function (series) {
      var stat = computeSeriesStatistics(series.data);
      return (
        Math.sqrt(Math.pow(stat.mean - seriesStat.mean, 2)) <=
        seriesStat.mean * 0.9
      );
    }).length;

    return validCount === seriesGroup.length;
  });

  return closeSeriesGroups;
}

function findStackedSeriesGroups(seriesCollection) {
  var combinations = createCombinations(seriesCollection);

  var stackedNumberSeriesGroups = combinations.filter(function (seriesGroup) {
    var rowCount = seriesGroup[0].data.length;
    var sum = null;

    for (var r = 0; r < rowCount; r++) {
      var newSum = seriesGroup.reduce(function (acc, series) {
        return acc + parseFloat(series.data[r]);
      }, 0);
      if (sum == null) sum = newSum;
      if (newSum !== sum) return false;
    }
    return true;
  });

  return stackedNumberSeriesGroups;
}

function findDualSeriesGroups(seriesCollection) {
  var dualNumberSeriesGroups = [];

  var combinations = createCombinations(seriesCollection);

  var dualNumberSeriesGroups = combinations.filter(function (seriesGroupList) {
    var minSeriesGroupLength = seriesGroupList.reduce(function (
      acc,
      seriesGroup
    ) {
      return Math.min(acc, seriesGroup.length);
    },
      Number.MAX_SAFE_INTEGER);
    return minSeriesGroupLength == 1 && seriesGroupList.length == 2;
  });
  return dualNumberSeriesGroups;
}

function findTripleSeriesGroups(seriesCollection) {
  var tripleSeriesGroups = [];

  var combinations = createCombinations(seriesCollection);

  var tripleSeriesGroups = combinations.filter(function (seriesGroupList) {
    var minSeriesGroupLength = seriesGroupList.reduce(function (
      acc,
      seriesGroup
    ) {
      return Math.min(acc, seriesGroup.length);
    },
      Number.MAX_SAFE_INTEGER);
    return minSeriesGroupLength == 1 && seriesGroupList.length <= 3;
  });
  return tripleSeriesGroups;
}

function createCombinations(seriesCollection, groupSize) {
  var total = seriesCollection.length;

  var combinations = [];

  for (var i = 0; i < total - 1; i++) {
    for (var j = i + 1; j < total; j++) {
      for (var k = j; k < total; k++) {
        var combo = seriesCollection.slice(i, j).concat([seriesCollection[k]]);
        if (
          combo &&
          combo.length > 1 &&
          (!groupSize || groupSize == combo.length)
        ) {
          combinations.push(combo);
        }
      }
    }
  }
  return combinations;
}

function computeSeriesStatistics(numArray) {
  var stat = { count: numArray.length };

  if (numArray.length > 0)
    stat.min = numArray.reduce(function (acc, r) {
      return Math.min(acc, r);
    });
  else stat.min = 0;

  if (numArray.length > 0)
    stat.max = numArray.reduce(function (acc, r) {
      return Math.max(acc, r);
    });
  else stat.max = 0;

  if (numArray.length > 0)
    stat.sum = numArray.reduce(function (acc, r) {
      return acc + r;
    });
  else stat.sum = 0;

  if (numArray.length > 0) stat.mean = parseInt(stat.sum) / stat.count;
  else stat.mean = 0;

  if (numArray.length > 0) {
    stat.stddev = Math.sqrt(
      numArray.reduce(function (acc, r) {
        return acc + Math.pow(stat.mean - r, 2);
      }) / stat.count
    );
  } else stat.stddev = 0;

  return stat;
}

function getStringSeries(chartData) {
  var stringSeriesGroup = chartData.filter(function (series) {
    return (
      ["string", "boolean"].indexOf(series.nuoField.DataType.toLowerCase()) >= 0
    );
  });
  return stringSeriesGroup;
}

function getNumberSeries(chartData) {
  var numberSeriesGroup = chartData
    .filter(function (series) {
      return (
        ["int", "integer", "int64", "float", "float64"].indexOf(
          series.nuoField.DataType.toLowerCase()
        ) >= 0
      );
    })
    .map(function (series) {
      var updSeries = series;
      updSeries.data = series.data.map(function (ele) {
        if (ele.trim().length == 0 || isNaN(parseFloat(ele))) return 0;
        else return parseFloat(ele);
      });
      return updSeries;
    });
  return numberSeriesGroup;
}

function getDateSeries(chartData) {
  var dateSeriesGroup = chartData.filter(function (series) {
    return (
      ["date", "time", "timestamp"].indexOf(
        series.nuoField.DataType.toLowerCase()
      ) >= 0
    );
  });
  return dateSeriesGroup;
}

function createDashboardSummary(
  historyAnalysis,
  chartCounter,
  stringSeriesGroup,
  numberSeriesGroup
) {
  var dashboardSummaryId =
    "historyAnalysisDashboardSummary" + historyAnalysis.AnalysisId;
  var chartDataSummary =
    "<div class='historyAnalysisDashboardSummary' id='" +
    dashboardSummaryId +
    "'>";
  var metricCount = 0;
  stringSeriesGroup.forEach(function (series) {
    chartDataSummary += "<div class='historyDashboardMetric'>";
    chartDataSummary += "<div class='historyDashboardMetricColumn'>";
    chartDataSummary += series.nuoField.FieldName;
    chartDataSummary += "</div>";

    chartDataSummary += "<div class='historyDashboardMetricValue'>";
    chartDataSummary += getReadableNumber(new Set(series.data).size);
    chartDataSummary += "</div>";

    chartDataSummary += "</div>";
    metricCount += 1;
  });

  numberSeriesGroup.forEach(function (series) {
    var total = series.data.reduce(function (acc, r) {
      return acc + r;
    });
    var avg = total / series.data.length;

    chartDataSummary += "<div class='historyDashboardMetric'>";
    chartDataSummary += "<div class='historyDashboardMetricColumn'>";
    chartDataSummary += series.nuoField.FieldName;
    chartDataSummary += "</div>";

    chartDataSummary += "<div class='historyDashboardMetricValue'>";
    chartDataSummary += getReadableNumber(total);
    chartDataSummary += "</div>";

    chartDataSummary += "</div>";
    metricCount += 1;
  });
  chartDataSummary += "</div>";
  $("#" + dashboardSummaryId).remove();
  drawAnalysisChart(historyAnalysis, chartCounter, null, chartDataSummary);
  $("#" + dashboardSummaryId).css(
    "height",
    Math.ceil(metricCount / 5) * 65 + 40 + "px"
  );
}
function createDashboardDataGrid(historyAnalysis, chartCounter) {
  if (historyAnalysis && historyAnalysis.Result) {
    var analysisId = historyAnalysis.AnalysisId;

    createDataGridDivContent(
      analysisId,
      "historyAnalysisDashboard" + analysisId
    );

    var chartData = getChartDataFromResult(
      historyAnalysis.Result,
      historyAnalysis.ChartFilters
    );
    if (chartData.length > 0 && chartData[0].data.length > 0) {
      var seriesGroup = getStringSeries(chartData)
        .concat(getNumberSeries(chartData))
        .concat(getDateSeries(chartData));
      if (seriesGroup && seriesGroup.length > 0) {
        drawDataGrid(analysisId, seriesGroup, true);
      }
    }
  }
}

function getHistoryRowTag(historyAnalysis) {
  var analysisId = historyAnalysis.AnalysisId;
  var rowContentCssClass = "chartRow";

  var rowDiv = "<div class = 'historyRow containerOuter' id='historyRow" + analysisId + "'>";

  var analysisHeaderDiv =
    "<div id='historyAnalysisHeader" +
    analysisId +
    "' class ='historyAnalysisHeader'>";

  var analysisHeaderUserDiv =
    "<div id='historyAnalysisHeaderUser" +
    analysisId +
    "' class ='historyAnalysisHeaderUser'>";

  var imageTag =
    "<img width='48px' height='48px' src='" +
    profileImageUrl +
    "' class='historyUserImage'/>";
  var analysisTitleTag =
    "<div id='historyAnalysisTitle" +
    analysisId +
    "' class='historyAnalysisTitle'><input class='searchInput' id='historyAnalysisTitleInput" +
    analysisId +
    "' type='text' placeholder='Analysis title. #FirstTopic #SecondTopic'/></div>";
  var userImageDiv =
    "<div id='historyUserImageDiv" +
    analysisId +
    "' class='historyUserImageDiv'>" +
    imageTag +
    "</div>";

  var analysisMetadata =
    "<div id='historyAnalysisMetadata" +
    analysisId +
    "' class='historyAnalysisMetadata'>";
  var userNameDiv =
    "<div id='historyUserName" +
    analysisId +
    "' class='historyUserName'>" +
    historyAnalysis.Author +
    "</div>";
  // console.log(historyAnalysis);
  var timeDiv =
    "<div id='historyAnalysisTime" +
    analysisId +
    "' class='historyAnalysisTime'>" +
    timeSince(historyAnalysis.LastModifiedAt) +
    "</div>";
  userImageDiv += userNameDiv;
  userImageDiv += timeDiv;
  analysisMetadata += analysisTitleTag;
  analysisMetadata += "</div>";

  analysisHeaderDiv += analysisMetadata;
  analysisHeaderDiv += analysisHeaderUserDiv;
  analysisHeaderDiv += userImageDiv;
  analysisHeaderDiv += "</div>";
  analysisHeaderDiv += "<a href='#' class='historyAnalysisHeaderDelete' id='historyAnalysisHeaderDelete" + analysisId + "'><svg class='historyAnalysisHeaderDeleteIn' id='historyAnalysisHeaderDeleteIn" + analysisId + "'  alt='close' title='Delete' width='32px' height='32px' viewBox='0 0 32 32' version='1.1' xmlns='http://www.w3.org/2000/svg'><path d='M16,0.5 C7.43958638,0.5 0.5,7.43958638 0.5,16 C0.5,24.5604136 7.43958638,31.5 16,31.5 C24.5604136,31.5 31.5,24.5604136 31.5,16 C31.5,7.43958638 24.5604136,0.5 16,0.5 Z' class='round_path' stroke='#CCD7E2' fill='transparent'></path><g class='plus_path' transform='translate(11.000000, 11.000000)' stroke='#637E9C' stroke-linecap='round' stroke-linejoin='round' stroke-width='2'><path d='M0,0 L10,10'></path><path d='M1.10134124e-13,10 L10,0'></path></g></svg></a>" +
    "</div>";

  var historySelectionBox =
    "<div id='historyAnalysisSelection" +
    analysisId +
    "' class='historyAnalysisSelection'>" +
    "<div contenteditable='true' class='searchInput' id='historyAnalysisSelectionInput" +
    analysisId +
    "' type='text' placeholder='Search terms e.g. avg Sales_Amount, top 10 Customer_Name based on Account_Balance, (Profit / Revenue) * 100.0 '></div></div>";

  var historyFilterBox =
    "<div id='historyAnalysisFilter" +
    analysisId +
    "' class='historyAnalysisFilter'><div contenteditable='true'  class='searchInput' id='historyAnalysisFilterInput" +
    analysisId +
    "' type='text' placeholder='Filters to apply on search e.g. Transaction_Date before 12th April and Country as The Netherlands but Account_Balance above 12.5K'></div></div>";

  var historyAnalysisSerachDiv =
    "<div id='historyAnalysisSerachContainer" +
    analysisId +
    "' class='historyAnalysisSerachContainer'>";
  historyAnalysisSerachDiv += historySelectionBox;
  historyAnalysisSerachDiv += historyFilterBox;
  historyAnalysisSerachDiv += "</div>";

  var historyAnalysisToolbarDiv =
    "<div id='historyAnalysisToolbar" +
    analysisId +
    "' class='historyAnalysisToolbar'>";
  historyAnalysisToolbarDiv +=
    "<input id='historyAnalysisSearchButton" +
    analysisId +
    "' class='historyAnalysisSearchButton button backgroundOrange' type='button' value='Search'/>";
  historyAnalysisToolbarDiv +=
    "<input id='historyAnalysisSaveButton" +
    analysisId +
    "' class='historyAnalysisSaveButton button button-white' type='button' value='Save Analysis'/>";
  historyAnalysisToolbarDiv +=
    "<div id='historyAnalysisResultTableButton" +
    analysisId +
    "' class='historyAnalysisResultTableButton button button-white'>Result Table</div>";
  historyAnalysisToolbarDiv +=
    "<input id='historyAnalysisResultTableInput" +
    analysisId +
    "' class='historyAnalysisResultTableInput' type='text' value='No Result Table'/>";


  historyAnalysisToolbarDiv += "<div id='historyAnalysisNarrativeButton" + analysisId + "' class='historyAnalysisNarrativeButton button button-white'>Narrative"
  // historyAnalysisToolbarDiv += "<div id='historyAnalysisNarrativeOptions" + analysisId + "' class='historyAnalysisNarrativeOptions dropdown-content'>"
  // historyAnalysisToolbarDiv += "<a href='#' id='historyAnalysisNarrativeEn" + analysisId + "' class='historyAnalysisNarrativeOption'>English</a>";
  // historyAnalysisToolbarDiv += "<a href='#' id='historyAnalysisNarrativeNl" + analysisId + "' class='historyAnalysisNarrativeOption'>Nederlands</a>";
  // historyAnalysisToolbarDiv += "</div>";
  historyAnalysisToolbarDiv += "</div>";


  historyAnalysisToolbarDiv +=
    "<input id='historyAnalysisDataButton" +
    analysisId +
    "' class='historyAnalysisDataButton button button-white' type='button' value='Show Data'/>";
  historyAnalysisToolbarDiv +=
    "<input id='historyAnalysisResetButton" +
    analysisId +
    "' class='historyAnalysisResetButton button button-white' type='button' value='Reset Dashboard'/>";
  historyAnalysisToolbarDiv +=
    "<button id='historyAnalysisMinimizeButton" +
    analysisId +
    "' class='historyAnalysisMinimizeButton'><svg class='historyAnalysisMinimizeImage _minimize' title='Minimize' id='historyAnalysisMinimizeImage" +
    analysisId +
    "' width='32px' height='32px' viewBox='0 0 32 32' version='1.1' xmlns='http://www.w3.org/2000/svg'><circle id='Oval' stroke='#CCD7E2' fill='transparent' stroke-width='1' cx='16' cy='16' r='15.5'></circle><polyline id='Path-35' stroke='#637E9C' fill='transparent' stroke-width='2' stroke-linecap='round' stroke-linejoin='round' points='21 14.1666667 16 19.1666667 11 14.1666667'></polyline></svg>" +
    "</button>";
  historyAnalysisToolbarDiv += "</div>";

  var dashboardContainerDiv =
    "<div id='historyAnalysisDashboardContainer" +
    analysisId +
    "' class='historyAnalysisDashboardContainer " +
    rowContentCssClass +
    "'>";
  dashboardContainerDiv +=
    "<div id='historyAnalysisDashboard" +
    analysisId +
    "' class='historyAnalysisDashboard " +
    rowContentCssClass +
    "'>";

  // dashboardDiv += "<div id='historyAnalysisTabs"+analysisId+"' class='historyAnalysisTabs'>";
  // dashboardDiv += "<ul><li>Visualization</li><li>Data</li><li>Profiling Results</li></ul>";

  // dashboardDiv += "<div id='historyAnalysisDashboard"+analysisId+"' class='historyAnalysisDashboard'>";
  // dashboardDiv += "</div>";

  // dashboardDiv += "<div id='historyAnalysisDataGrid"+analysisId+"' class='historyAnalysisDataGrid'></div>";
  // dashboardDiv += "<div id='historyAnalysisProfilingGrid"+analysisId+"' class='historyAnalysisProfilingGrid'></div>";

  // dashboardDiv += "</div>";
  dashboardContainerDiv += "</div>";
  dashboardContainerDiv += "</div>";

  rowDiv += analysisHeaderDiv;
  rowDiv += historyAnalysisSerachDiv;
  rowDiv += historyAnalysisToolbarDiv;
  rowDiv += dashboardContainerDiv;
  rowDiv += "</div>";

  return rowDiv;
}

function drawAnalysisGrids(historyAnalysis) {
  console.log(historyAnalysis);
  $("#historyAnalysisTabs" + historyAnalysis.AnalysisId).jqxTabs({
    width: "100%",
    height: "100%",
    selectionTracker: true,
    animationType: "fade",
    theme: jqWidgetThemeName
  });

  if (
    historyAnalysis.Result &&
    historyAnalysis.Result.Data &&
    historyAnalysis.Result.Metadata
  ) {
    drawChart(
      "historyAnalysisDataGrid" + historyAnalysis.AnalysisId,
      CHART_TYPE_DATA_TABLE,
      historyAnalysis.Title,
      historyAnalysis.Result.Data,
      historyAnalysis.Result.Metadata
    );
  } else {
    $("#historyAnalysisDataGrid" + historyAnalysis.AnalysisId).html(
      "<i>No chart available for this response.</i>"
    );
  }

  if (
    historyAnalysis.ProfilingResult &&
    historyAnalysis.ProfilingResult.Data &&
    historyAnalysis.ProfilingResult.Metadata
  ) {
    drawChart(
      "historyAnalysisProfilingGrid" + historyAnalysis.AnalysisId,
      CHART_TYPE_DATA_TABLE,
      historyAnalysis.Title,
      historyAnalysis.ProfilingResult.Data,
      historyAnalysis.ProfilingResult.Metadata
    );
  } else {
    $("#historyAnalysisProfilingGrid" + historyAnalysis.AnalysisId).html(
      "<i>No profiling results available for this response.</i>"
    );
  }
}

function drawAnalysisChart(
  historyAnalysis,
  chartCounter,
  chartData,
  chartDataSummary
) {
  var isMap =
    chartData &&
    chartData.chartType &&
    (chartData.chartType == "scattergeo" ||
      chartData.chartType == "geo-region");
  if (
    historyAnalysis.Result &&
    historyAnalysis.Result.Data &&
    historyAnalysis.Result.Metadata
  ) {
    var analysisId = historyAnalysis.AnalysisId;

    if (chartDataSummary) {
      $("#historyAnalysisDashboard" + analysisId).prepend(chartDataSummary);
    } else {
      var analysisIdWithCounter = analysisId + "_" + chartCounter;
      var chartDivId = "historyAnalysisChart" + analysisIdWithCounter;

      var chartContainerDiv =
        "<div id='historyAnalysisChartContainer" +
        analysisIdWithCounter +
        "' class='historyAnalysisChartContainer containerOuter resizable'><div class='historyAnalysisChartContainerIn'>";
      chartContainerDiv +=
        "<div id='historyChartToolbar" +
        analysisIdWithCounter +
        "' class='historyChartToolbar'>";
      chartContainerDiv +=
        "<input id='historyChartFormatButton" +
        analysisIdWithCounter +
        "' class='historyChartFormatButton button button-white chartToolbarButton' type='button' value='Customize'/>";
      chartContainerDiv +=
        "<input id='historyChartDataButton" +
        analysisIdWithCounter +
        "' class='historyChartDataButton button button-white chartToolbarButton' type='button' value='Show Data'/>";
      // chartContainerDiv += "<input id='historyChartExportButton"+ analysisIdWithCounter +"' class='historyChartExportButton button chartToolbarButton' type='button' value='Export'/>"
      chartContainerDiv +=
        "<button id='historyChartDeleteButtonContainer" +
        analysisIdWithCounter +
        "' class='historyChartDeleteButtonContainer' type='button'>";
      chartContainerDiv +=
        "<svg class='historyChartDeleteButton' id='historyChartDeleteButton" +
        analysisIdWithCounter +
        "'  alt='close' title='Delete' width='32px' height='32px' viewBox='0 0 32 32' version='1.1' xmlns='http://www.w3.org/2000/svg'><path d='M16,0.5 C7.43958638,0.5 0.5,7.43958638 0.5,16 C0.5,24.5604136 7.43958638,31.5 16,31.5 C24.5604136,31.5 31.5,24.5604136 31.5,16 C31.5,7.43958638 24.5604136,0.5 16,0.5 Z' class='round_path' stroke='#CCD7E2' fill='#ffffff'></path><g class='plus_path' transform='translate(11.000000, 11.000000)' stroke='#637E9C' stroke-linecap='round' stroke-linejoin='round' stroke-width='2'><path d='M0,0 L10,10'></path><path d='M1.10134124e-13,10 L10,0'></path></g></svg>";
      chartContainerDiv +=
        "<button id='historyChartMinimizeButton" +
        analysisIdWithCounter +
        "' class='historyChartMinimizeButton'><svg class='historyAnalysisMinimizeImage _maximize' title='Maximize' id='historyChartMinimizeButton" +
        analysisIdWithCounter +
        "' transform: 'rotate(180deg)' width='32px' height='32px' viewBox='0 0 32 32' version='1.1' xmlns='http://www.w3.org/2000/svg'><circle id='Oval' stroke='#CCD7E2' fill='transparent' stroke-width='1' cx='16' cy='16' r='15.5'></circle><polyline id='Path-35' stroke='#637E9C' fill='transparent' stroke-width='2' stroke-linecap='round' stroke-linejoin='round' points='21 14.1666667 16 19.1666667 11 14.1666667'></polyline></svg>" +
        "</button>";
      chartContainerDiv += "</button>";
      chartContainerDiv += "</div>";
      chartContainerDiv +=
        "<div id='" + chartDivId + "' class='historyAnalysisChart'></div>";
      chartContainerDiv += "</div></div>";

      if ($("#historyAnalysisChartContainer" + analysisIdWithCounter)) {
        $("#historyAnalysisChartContainer" + analysisIdWithCounter).remove();
      }
      $("#historyAnalysisDashboard" + analysisId).append(chartContainerDiv);
      // $("#historyAnalysisChartContainer" + analysisIdWithCounter).drags();
      // var draggie = new Draggabilly( "#historyAnalysisChartContainer" + analysisIdWithCounter, {
      // handle: "#historyChartToolbar" + analysisIdWithCounter
      //   });

      addChartMinimizeHandler();
      addChartDeleteHandler();
      addChartDataHandler();
      // addChartExportHandler();

      // if(isMap){
      if (false) {
        drawMapChart(
          chartDivId,
          "Map Chart",
          chartData.categorySeries,
          chartData.numberSeriesGroup
        );
      } else {
        historyAnalysis[analysisIdWithCounter] = { chartDataGrid: {} };
        var chartType = chartData.chartType;
        var layout = {
          titlefont: {
            family: "Quicksand, sans-serif",
            size: 14
          },
          yaxis: {
            titlefont: { color: "#1f77b4" },
            tickfont: { color: "#1f77b4" }
          },

        };

        var filterAxes = {
          x: [],
          y: [],
          z: []
        };

        if (
          chartData.categorySeries &&
          chartData.categorySeries.data.length > 0
        ) {
          var fieldNames = chartData.numberSeriesGroup.map(function (series) {
            return "<b>" + series.nuoField.FieldName + "</b>";
          });
          if (fieldNames.length > 1) {
            layout.title =
              fieldNames
                .reverse()
                .slice(1)
                .reverse()
                .join(", ") +
              " and " +
              fieldNames[0] +
              " for each <b>" +
              chartData.categorySeries.nuoField.FieldName +
              "</b>";
          } else {
            layout.title =
              fieldNames[0] +
              " for each <b>" +
              chartData.categorySeries.nuoField.FieldName +
              "</b>";
          }

          layout.xaxis = {
            title: chartData.categorySeries.nuoField.FieldName
            // categoryorder: 'array',
            // categoryarray: chartData.numberSeriesGroup[0].data
          };

          historyAnalysis[analysisIdWithCounter].chartDataGrid[
            chartData.categorySeries.nuoField.FieldName
          ] = chartData.categorySeries.data;
          chartData.numberSeriesGroup.forEach(function (series) {
            historyAnalysis[analysisIdWithCounter].chartDataGrid[
              series.nuoField.FieldName
            ] = series.data;
          });
        } else if (chartType == "heatmap") {
          var fieldNames = chartData.categorySeriesGroup.map(function (series) {
            return "<b>" + series.nuoField.FieldName + "</b>";
          });

          layout.title =
            "<b>" +
            chartData.numberSeries.nuoField.FieldName +
            "</b>" +
            " for the combination of " +
            fieldNames
              .reverse()
              .slice(1)
              .reverse()
              .join(", ") +
            " and " +
            fieldNames[0];

          historyAnalysis[analysisIdWithCounter].chartDataGrid[
            chartData.numberSeries.nuoField.FieldName
          ] = chartData.numberSeries.data;
          chartData.categorySeriesGroup.forEach(function (series) {
            historyAnalysis[analysisIdWithCounter].chartDataGrid[
              series.nuoField.FieldName
            ] = series.data;
          });
        } else {
          var fieldNames = chartData.numberSeriesGroup.map(function (series) {
            return "<b>" + series.nuoField.FieldName + "</b>";
          });
          layout.title =
            "Correlation between " +
            fieldNames
              .reverse()
              .slice(1)
              .reverse()
              .join(", ") +
            " and " +
            fieldNames[0];

          if (chartType === "bubble") {
          }
          if (chartType === "scatter3d") {
          }
          chartData.numberSeriesGroup.forEach(function (series) {
            historyAnalysis[analysisIdWithCounter].chartDataGrid[
              series.nuoField.FieldName
            ] = series.data;
          });
        }
        var data = [];

        if (chartType === "combined") {
          // filterAxes.x.push(chartData.numberSeriesGroup[0]);
          drawDualYAxisChart(
            chartDivId,
            chartData,
            layout,
            historyAnalysis,
            filterAxes
          );
        } else if (chartType === "bubble") {
          filterAxes.x.push(chartData.numberSeriesGroup[0]);
          filterAxes.y.push(chartData.numberSeriesGroup[1]);
          drawBubbleChart(
            chartDivId,
            chartData,
            layout,
            historyAnalysis,
            filterAxes
          );
        } else if (chartType === "scatter3d") {
          filterAxes.x.push(chartData.numberSeriesGroup[0]);
          filterAxes.y.push(chartData.numberSeriesGroup[1]);
          filterAxes.z.push(chartData.numberSeriesGroup[2]);
          draw3dScatterPlot(chartDivId, chartData, layout);
        } else if (chartType === "heatmap") {
          filterAxes.x.push(chartData.categorySeriesGroup[0]);
          filterAxes.y.push(chartData.categorySeriesGroup[1]);
          drawHeatmap(
            chartDivId,
            chartData,
            layout,
            historyAnalysis,
            filterAxes
          );
        } else if (chartType === "geo-region") {
          filterAxes.x.push(chartData.categorySeries);
          drawGeoMap(
            chartDivId,
            chartData,
            layout,
            historyAnalysis,
            filterAxes
          );
        } else if (chartType === "scattergeo") {
          filterAxes.x.push(chartData.categorySeries);
          drawScatterGeoMap(
            chartDivId,
            chartData,
            layout,
            historyAnalysis,
            filterAxes
          );
        } else {
          data = chartData.numberSeriesGroup.map(function (series) {
            var seriesNumberMaps = [];

            $(chartData.categorySeries.data).each(function (index, ele) {
              var seriesNumberMap = {
                seriesEle: ele,
                numberEle: series.data[index]
              };
              seriesNumberMaps.push(seriesNumberMap);
            });
            if (
              // ["date", "time", "timestamp"].indexOf(series.nuoField.DataType.toLowerCase()) >= 0
              true
            ) {

              seriesNumberMaps.sort(function (a, b) {
                var leftValue = a.seriesEle;
                var rightValue = b.seriesEle;
                if (
                  chartData.categorySeries.nuoField.DataType.toLowerCase() ===
                  "time"
                ) {
                  leftValue = stringToTime(leftValue);
                  rightValue = stringToTime(rightValue);
                }
                if (leftValue < rightValue) return -1;
                if (leftValue > rightValue) return 1;
                return 0;
              });
            }
            var xData = [];
            var yData = [];
            seriesNumberMaps.forEach(function (seriesNumberMap) {
              xData.push(seriesNumberMap.seriesEle);
              yData.push(seriesNumberMap.numberEle);
            });
            var trace = {
              // x: chartData.categorySeries.data.slice(0,30),
              x: xData,
              y: yData,
              name: series.nuoField.FieldName
            };

            filterAxes.x = [
              {
                data: xData,
                nuoField: chartData.categorySeries.nuoField
              }
            ];
            return trace;
          });

          var isknownType = false;

          switch (chartType) {
            case "line":
              data = data.map(function (trace) {
                var chartColor = getRandomChartColor();
                trace.type = "scatter";
                trace.mode = "lines+markers";
                trace.marker = {
                  color: "rgba(" + chartColor.concat([1]).join(",") + ")",
                  size: 8
                };
                trace.line = {
                  shape: "spline",
                  color: "rgba(" + chartColor.concat([0.75]).join(",") + ")",
                  width: 1
                };
                return trace;
              });
              isknownType = true;
              break;

            case "scatter":
              data = data.map(function (trace) {
                var chartColor = getRandomChartColor();
                trace.type = "scatter";
                trace.mode = "markers";
                trace.marker = {
                  color: "rgba(" + chartColor.concat([0.75]).join(",") + ")",
                  size: 12
                };
                return trace;
              });
              isknownType = true;
              break;

            case "area":
              var traceCounter = 0;
              data = data.map(function (trace) {
                var chartColor = getRandomChartColor();
                trace.type = "scatter";

                trace.marker = {
                  color: "rgba(" + chartColor.concat([0.75]).join(",") + ")"
                };
                trace.line = {
                  shape: "spline",
                  color: "rgba(" + chartColor.concat([1]).join(",") + ")",
                  width: 1
                };

                trace.fill = "tonexty";
                if (traceCounter == 0) trace.fill = "tozeroy";

                traceCounter += 1;
                return trace;
              });
              isknownType = true;
              break;

            case "doughnut":
              data = data.map(function (trace) {
                trace.marker = {
                  colors: trace.x.map(function () {
                    var chartColor = getRandomChartColor();
                    return "rgba(" + chartColor.concat([0.75]).join(",") + ")";
                  })
                };
                trace.labels = trace.x;
                trace.values = trace.y;
                trace.type = "pie";
                trace.hole = 0.5;

                return trace;
              });
              isknownType = true;
              break;

            case "radar":
              data = data.map(function (trace) {
                var chartColor = getRandomChartColor();
                trace.marker = {
                  color: "rgba(" + chartColor.concat([0.75]).join(",") + ")"
                };
                trace.theta = trace.x;
                trace.r = trace.y;
                trace.type = "scatterpolar";
                trace.fill = "toself";

                return trace;
              });
              layout.polar = {
                radialaxis: {
                  visible: true
                  // range: [0, 50]
                }
              };
              isknownType = true;
              break;

            case "stacked":
              layout.barmode = "stack";

            case "horizontalBar":
              data = data.map(function (trace) {
                var temp = trace.y;
                trace.y = trace.x;
                trace.x = temp;

                trace.orientation = "h";
                return trace;
              });
              var tempAxes = filterAxes.y;
              filterAxes.y = filterAxes.x;
              filterAxes.x = tempAxes;

            case "bar":
              data = data.map(function (trace) {
                var chartColor = getRandomChartColor();
                trace.type = "bar";
                trace.marker = {
                  color: "rgba(" + chartColor.concat([0.75]).join(",") + ")",
                  width: 1
                };
                return trace;
              });
              isknownType = true;
              break;
          }
          if (isknownType) {
            plotChart(
              chartDivId,
              data,
              layout,
              historyAnalysis,
              filterAxes,
              chartType
            );
          }
        }
      }
    }
  }
  mresizeChart();
}

function plotChart(uid, data, layout, historyAnalysis, filterAxes, chartType) {
  layout.margin = {
    // l: 0,
    // r: 0,
    // b: 0,
    // t: 0,
    pad: 10
  };

  $("#" + uid).parentNode;
  Plotly.newPlot(uid, data, layout, {
    responsive: true,
    showLink: false,
    displaylogo: false,
    modeBarButtonsToRemove: ["sendDataToCloud"]
  });
  // historyAnalysis.charts =
  // historyAnalysis
  // .charts
  // .filters(function(ele){
  // return ele.uid !== uid ;
  // })

  // historyAnalysis.charts.push(
  // {
  // uid: uid,
  // data: data,
  // layout: layout,
  // filterAxes: filterAxes,
  // chartType: chartType
  // }
  // );

  if (historyAnalysis && filterAxes) {
    $("#" + uid).off("plotly_click");
    $("#" + uid).on("plotly_click", function (eventRef, eventData) {
      var pts = "";
      console.log(eventData.points[0].pointNumber);
      var xStart, xEnd, yStart, yEnd;

      if (eventData.points[0].pointNumber !== null) {
        if (typeof eventData.points[0].pointNumber === "number") {
          if (filterAxes.x.length > 0) {
            filterAxes.x[0].eventInfo = {
              rangeStart: eventData.points[0].pointNumber,
              rangeEnd: eventData.points[0].pointNumber
            };
          } else {
            filterAxes.y[0].eventInfo = {
              rangeStart: eventData.points[0].pointNumber,
              rangeEnd: eventData.points[0].pointNumber
            };
          }
        } else {
          filterAxes.x[0].eventInfo = {
            rangeStart: eventData.points[0].pointNumber[1],
            rangeEnd: eventData.points[0].pointNumber[1]
          };
          filterAxes.y[0].eventInfo = {
            rangeStart: eventData.points[0].pointNumber[0],
            rangeEnd: eventData.points[0].pointNumber[0]
          };
        }
        addChartFilter(uid, historyAnalysis, filterAxes);
      }
    });
    if (chartType !== "geo-region" && chartType !== "scattergeo") {
      $("#" + uid).off("plotly_relayout");
      $("#" + uid).on("plotly_relayout", function (eventRef, eventData) {
        var xStart = eventData["xaxis.range[0]"];
        var xEnd = eventData["xaxis.range[1]"];
        var yStart = eventData["yaxis.range[0]"];
        var yEnd = eventData["yaxis.range[1]"];

        if ((xStart && xEnd) || (yStart && yEnd)) {
          filterAxes.x.map(function (filterAxis) {
            filterAxis.eventInfo = {
              rangeStart: xStart,
              rangeEnd: xEnd
            };
            return filterAxis;
          });

          filterAxes.y.map(function (filterAxis) {
            filterAxis.eventInfo = {
              rangeStart: yStart,
              rangeEnd: yEnd
            };
            return filterAxis;
          });
          addChartFilter(uid, historyAnalysis, filterAxes);
        } else {
          clearChartFilter(uid, historyAnalysis, filterAxes);
        }
      });
    }
  }

  $("#" + uid).attr("data-info", JSON.stringify(data));
  $("#" + uid).attr("data-info2", JSON.stringify(layout));
}

function addChartFilter(uid, historyAnalysis, filterAxes) {
  filterAxes.x.concat(filterAxes.y).forEach(function (filterAxis) {
    var values = filterAxis.data;
    var eventInfo = filterAxis.eventInfo;

    if (eventInfo) {
      if (!historyAnalysis.ChartFilters) {
        historyAnalysis.ChartFilters = [];
      }
      historyAnalysis.ChartFilters = historyAnalysis.ChartFilters.filter(
        function (chartFilter) {
          return (
            chartFilter.nuoField.FieldName !== filterAxis.nuoField.FieldName
          );
        }
      );

      var filterData = [];
      var valueIndex = 0;

      if (
        ["Date", /*"Time",*/ "Timestamp"]
          .map(function (ele) {
            return ele.toLowerCase();
          })
          .indexOf(filterAxis.nuoField.DataType.toLowerCase()) >= 0
      ) {
        values.forEach(function (value) {
          var updValue = new Date(value);
          if (
            updValue >= new Date(eventInfo.rangeStart) &&
            updValue <= new Date(eventInfo.rangeEnd)
          ) {
            filterData.push(value);
          }
          valueIndex++;
        });
      } else if (
        ["Int64", "Int", "Float64", "Float"]
          .map(function (ele) {
            return ele.toLowerCase();
          })
          .indexOf(filterAxis.nuoField.DataType.toLowerCase()) >= 0
      ) {
        values.forEach(function (value) {
          if (
            value >= Math.ceil(eventInfo.rangeStart) &&
            value <= Math.floor(eventInfo.rangeEnd)
          ) {
            filterData.push(value);
          }
          valueIndex++;
        });
      } else {
        if (
          eventInfo &&
          Math.ceil(eventInfo.rangeStart) >= 0 &&
          Math.floor(eventInfo.rangeEnd) < values.length &&
          eventInfo.rangeEnd >= 0 &&
          eventInfo.rangeEnd < values.length
        ) {
          values.forEach(function (value) {
            if (
              valueIndex >= Math.ceil(eventInfo.rangeStart) &&
              valueIndex <= Math.floor(eventInfo.rangeEnd)
            ) {
              filterData.push(values[valueIndex]);
            }
            valueIndex++;
          });
        }
      }
      historyAnalysis.ChartFilters.push({
        nuoField: filterAxis.nuoField,
        data: filterData
      });
    }
  });
  $("#historyAnalysisResetButton" + historyAnalysis.AnalysisId).show();
  drawDashboard(historyAnalysis);
}

function clearChartFilter(uid, historyAnalysis, filterAxes) {
  filterAxes.x.concat(filterAxes.y).forEach(function (filterAxis) {
    var values = filterAxis.data;
    var eventInfo = filterAxis.eventInfo;
    if (
      eventInfo &&
      eventInfo.rangeStart >= 0 &&
      eventInfo.rangeStart < values.length &&
      eventInfo.rangeEnd >= 0 &&
      eventInfo.rangeEnd < values.length
    ) {
      if (!historyAnalysis.ChartFilters) {
        historyAnalysis.ChartFilters = [];
      }
      historyAnalysis.ChartFilters = historyAnalysis.ChartFilters.filter(
        function (chartFilter) {
          return (
            chartFilter.nuoField.FieldName !== filterAxis.nuoField.FieldName
          );
        }
      );
      console.log(JSON.stringify(historyAnalysis.ChartFilters));
    }
  });
  drawDashboard(historyAnalysis);
}

function drawBubbleChart(
  chartDivId,
  chartData,
  layout,
  historyAnalysis,
  filterAxes
) {
  var chartColor = getRandomChartColor();
  var size = chartData.numberSeriesGroup[2].data;
  var data = [
    {
      x: chartData.numberSeriesGroup[0].data,
      y: chartData.numberSeriesGroup[1].data,
      mode: "markers",
      marker: {
        size: size,
        sizeref: (2.0 * Math.max(...size)) / 100 ** 2,
        sizemode: "area",
        color: "rgba(" + chartColor.concat([0.75]).join(",") + ")",
        opacity: 0.6
      }
    }
  ];
  layout.xaxis = {
    title: chartData.numberSeriesGroup[0].nuoField.FieldName
  };
  layout.yaxis = {
    title: chartData.numberSeriesGroup[1].nuoField.FieldName
  };
  // plotChart(chartDivId,data,layout);
  plotChart(chartDivId, data, layout, historyAnalysis, filterAxes);
}

function drawHeatmap(
  chartDivId,
  chartData,
  layout,
  historyAnalysis,
  filterAxes
) {
  // var chartColor = getChartColor();
  var data = [
    {
      x: chartData.categorySeriesGroup[0].data,
      y: chartData.categorySeriesGroup[1].data,
      z: chartData.numberSeries.data,
      reversescale: true,
      type: "heatmap"
    }
  ];
  layout.xaxis = {
    title: chartData.categorySeriesGroup[0].nuoField.FieldName
  };
  layout.yaxis = {
    title: chartData.categorySeriesGroup[1].nuoField.FieldName
  };
  // plotChart(chartDivId,data,layout);
  plotChart(chartDivId, data, layout, historyAnalysis, filterAxes);
}

function drawGeoMap(
  chartDivId,
  chartData,
  layout,
  historyAnalysis,
  filterAxes
) {
  var locations = chartData.categorySeries.data.concat([]);
  var zValues = chartData.numberSeriesGroup[0].data.concat([]);

  var data = [
    {
      type: "choropleth",
      locations: locations,
      locationmode: "country names",
      z: zValues,
      text: locations.map(function (ele) {
        return toTitleCase(ele);
      }),
      // colorscale: "Blues",
      autocolorscale: false,
      reversescale: true,
      marker: {
        line: {
          color: "rgb(180,180,180)",
          width: 0.5
        }
      },
      colorbar: {
        autotic: false,
        // tickprefix: '$',
        title: chartData.numberSeriesGroup[0].nuoField.FieldName
      }
    }
  ];

  layout.geo = {
    showframe: false,
    showcoastlines: true,
    projection: {
      type: "mercator"
    },
    showcountries: true,
    showsubunits: true,
    showland: true,
    landcolor: 'rgb(250,250,250)',
    subunitcolor: 'rgb(180,180,180)',
    countrycolor: 'rgb(150,150,150)',
    countrywidth: 1,
    subunitwidth: 0.5
  };
  plotChart(
    chartDivId,
    data,
    layout,
    historyAnalysis,
    filterAxes,
    "geo-region"
  );
}

function drawScatterGeoMap(
  chartDivId,
  chartData,
  layout,
  historyAnalysis,
  filterAxes
) {

  var labels =
    chartData.categorySeries.data.map(function (ele, i) {
      return ele + " with " + chartData.numberSeriesGroup[2].nuoField.FieldName + " of " + chartData.numberSeriesGroup[2].data[i];
    })

  var data = [
    {
      type: "scattermapbox",
      lat: chartData.numberSeriesGroup[0].data,
      lon: chartData.numberSeriesGroup[1].data,
      hoverinfor: chartData.numberSeriesGroup[2].data,
      text: labels,
      // colorscale: "Blues",
      mode: "markers",
      // marker: {
      //   size: 8,
      //   opacity: 0.8,
      //   reversescale: true,
      //   autocolorscale: true,
      //   symbol: 'square',
      //   line: {
      //     width: 0.5,
      //     color: 'rgb(180,180,180)'
      //   },
      //   // cmin: 0,
      //   color: chartData.numberSeriesGroup[2].data,
      //   colorbar: {
      //     autotic: false,
      //     // tickprefix: '$',
      //     title: chartData.numberSeriesGroup[2].nuoField.FieldName
      //   }
      // }
      marker: {
        color: chartData.numberSeriesGroup[2].data,
        // colorscale: scl,
        // cmin: 0,
        // cmax: 1.4,
        reversescale: true,
        autocolorscale: false,
        // colorscale: "Blues",
        opacity: 0.9,
        size: 6,
        colorbar: {
          autotic: false,
          // tickprefix: '$',
          title: chartData.numberSeriesGroup[2].nuoField.FieldName
        }
      }
    }
  ];

  layout.mapbox = {
    domain: {
      x: [0, 1],
      y: [0, 1]
    },
    style: 'light'
  };

  Plotly.setPlotConfig({
    mapboxAccessToken: 'pk.eyJ1IjoibnVvY2FudmFzIiwiYSI6ImNqcHppZWt3dTBiYnU0Mm9icWRzYnJscm0ifQ.PAjZ77_CmD3qp7RAJeXWfQ'
  })

  layout.geo = {
    showframe: false,
    projection: {
      type: 'mercator'
    },
    showcountries: true,
    showsubunits: true,
    showland: true,
    landcolor: 'rgb(250,250,250)',
    subunitcolor: 'rgb(180,180,180)',
    countrycolor: 'rgb(150,150,150)',
    countrywidth: 1,
    subunitwidth: 0.5
  };
  plotChart(
    chartDivId,
    data,
    layout,
    historyAnalysis,
    filterAxes,
    "scattergeo"
  );
}

// function drawTreeMap(
//   chartDivId,
//   chartData,
//   layout,
//   historyAnalysis,
//   filterAxes
// ) {

//   // declaring arrays
//   var shapes = [];
//   var annotations = [];
//   var counter = 0;

//   // For Hover Text
//   var x_trace = [];
//   var y_trace = [];
//   var text = [];

//   //colors
//   var colors = chartColors.map(function (ele) { return "rgba(" + getRgbChartColorFromHex().concat([0.75]).join(",") + ")"; })

//   // Generate Rectangles using Treemap-Squared
//   var values = chartData.numberSeriesGroup[0].data;
//   var rectangles = Treemap.generate(values, 100, 100);

//   for (var i in rectangles) {
//     var shape = {
//       type: 'rect',
//       x0: rectangles[i][0],
//       y0: rectangles[i][1],
//       x1: rectangles[i][2],
//       y1: rectangles[i][3],
//       line: {
//         width: 2
//       },
//       fillcolor: colors[counter]
//     };
//     shapes.push(shape);
//     var annotation = {
//       x: (rectangles[i][0] + rectangles[i][2]) / 2,
//       y: (rectangles[i][1] + rectangles[i][3]) / 2,
//       text: String(values[counter]),
//       showarrow: false
//     };
//     annotations.push(annotation);

//     // For Hover Text
//     x_trace.push((rectangles[i][0] + rectangles[i][2]) / 2);
//     y_trace.push((rectangles[i][1] + rectangles[i][3]) / 2);
//     text.push(String(values[counter]));

//     // Incrementing Counter		
//     counter++;
//   }

//   // Generating Trace for Hover Text
//   var trace0 = {
//     x: x_trace,
//     y: y_trace,
//     text: text,
//     mode: 'text',
//     type: 'scatter'
//   };

//   var layout = {
//     height: 700,
//     width: 700,
//     shapes: shapes,
//     hovermode: 'closest',
//     annotations: annotations,
//     xaxis: {
//       showgrid: false,
//       zeroline: false
//     },
//     yaxis: {
//       showgrid: false,
//       zeroline: false
//     }
//   };

//   var data = {
//     data: [trace0]
//   };

//   plotChart(
//     chartDivId,
//     data,
//     layout,
//     historyAnalysis,
//     filterAxes,
//     "treemap"
//   );
// }

function drawDualYAxisChart(
  chartDivId,
  chartData,
  layout,
  historyAnalysis,
  filterAxes
) {
  var data = chartData.numberSeriesGroup.map(function (series) {
    var seriesNumberMaps = [];

    $(chartData.categorySeries.data).each(function (index, ele) {
      var seriesNumberMap = {
        seriesEle: ele,
        numberEle: series.data[index]
      };
      seriesNumberMaps.push(seriesNumberMap);
    });
    if (
      // ["date", "time", "timestamp"].indexOf(series.nuoField.DataType.toLowerCase()) >= 0
      true
    ) {
      seriesNumberMaps.sort(function (a, b) {
        var leftValue = a.seriesEle;
        var rightValue = b.seriesEle;
        if (chartData.categorySeries.nuoField.DataType.toLowerCase() === "time") {
          leftValue = stringToTime(leftValue);
          rightValue = stringToTime(rightValue);
        }
        if (leftValue < rightValue) return -1;
        if (leftValue > rightValue) return 1;
        return 0;
      });
    }
    var xData = [];
    var yData = [];
    seriesNumberMaps.forEach(function (seriesNumberMap) {
      xData.push(seriesNumberMap.seriesEle);
      yData.push(seriesNumberMap.numberEle);
    });
    var trace = {
      // x: chartData.categorySeries.data.slice(0,30),
      x: xData,
      y: yData,
      name: series.nuoField.FieldName
    };

    filterAxes.x = [
      {
        data: xData,
        nuoField: chartData.categorySeries.nuoField
      }
    ];
    if (series.chartType) {
      var chartColor = getRandomChartColor();
      //This axis will be asigned for each series. But it is intended to keep code simple and has no side effect.
      layout.yaxis2 = {
        titlefont: { color: "rgba(" + chartColor.concat([1]).join(",") + ")" },
        tickfont: { color: "rgba(" + chartColor.concat([1]).join(",") + ")" },
        anchor: "free",
        overlaying: "y",
        side: "right",
        position: 0.95
      };

      chartColor = getRandomChartColor();
      if (series.chartType == "line") {
        trace.yaxis = "y2";
        trace.type = "scatter";
        trace.mode = "lines+markers";
        trace.marker = {
          color: "rgba(" + chartColor.concat([1]).join(",") + ")",
          size: 8
        };
        trace.line = {
          shape: "spline",
          color: "rgba(" + chartColor.concat([0.75]).join(",") + ")",
          width: 1
        };
      } else {
        trace.type = "bar";
        trace.marker = {
          color: "rgba(" + chartColor.concat([0.75]).join(",") + ")",
          width: 1
        };
      }
    }
    return trace;
  });
  // plotChart(chartDivId,data,layout);
  plotChart(chartDivId, data, layout, historyAnalysis, filterAxes);
}

function draw3dScatterPlot(chartDivId, chartData, layout) {
  var chartColor = getRandomChartColor();
  var data = [
    {
      x: chartData.numberSeriesGroup[0].data,
      y: chartData.numberSeriesGroup[1].data,
      z: chartData.numberSeriesGroup[2].data,
      mode: "markers",
      marker: {
        size: 4,
        color: chartData.numberSeriesGroup[2].data,
        colorscale: "Reds",
        line: {
          color: "rgba(" + chartColor.concat([0.75]).join(",") + ")",
          width: 0.5
        }
      },
      type: "scatter3d"
    }
  ];
  layout.scene = {};
  layout.scene.xaxis = {
    title: chartData.numberSeriesGroup[0].nuoField.FieldName
  };
  layout.scene.yaxis = {
    title: chartData.numberSeriesGroup[1].nuoField.FieldName
  };
  layout.scene.zaxis = {
    title: chartData.numberSeriesGroup[2].nuoField.FieldName
  };
  plotChart(chartDivId, data, layout);
}

function drawMapChart(uid, title, categorySeries, numberSeriesGroup) {
  var options = {};

  var rowCount = categorySeries.data.length;
  var options = {
    title: title,
    height: "100%",
    width: "100%",
    colorAxis: { colors: ["#e7711c", "#4374e0"] } // orange to blue
  };

  var inputData = [];
  var row = [];

  //header
  row.push(categorySeries.nuoField.FieldName);
  row.push(numberSeriesGroup[0].nuoField.FieldName);

  if (numberSeriesGroup.length > 1) {
    options.displayMode = "markers";
    row.push(numberSeriesGroup[1].nuoField.FieldName);
  }
  inputData.push(row);

  //data
  for (var r = 0; r < rowCount; r++) {
    row = [];
    row.push(categorySeries.data[r]);
    row.push(numberSeriesGroup[0].data[r]);

    if (numberSeriesGroup.length > 1) {
      row.push(numberSeriesGroup[1].data[r]);
    }
    inputData.push(row);
  }
  var chartData = google.visualization.arrayToDataTable(inputData);
  var chart = new google.visualization.GeoChart(document.getElementById(uid));

  chart.draw(chartData, options);
}

// function postToServer(reqBodyObj) {
// const postUrl = "http://localhost:6060/someuri";
// return $.post({
// url: postUrl,
// dataType: "json",
// data: reqBodyObj
// });
// }

// function renderJqxDataTable(
// uid,
// gridContent,
// gridColumns,
// enablePaging,
// paramEditable
// ) {
// var source = {
// localData: gridContent,
// dataType: "array"
// };
// var dataAdapter = new $.jqx.dataAdapter(source);
// var dataTableSettings = {
// altRows: false,
// showHeader:false,
// source: dataAdapter,
// columns: gridColumns,
// width: "100%",
// autoheight: true,
// autorowheight: true
// };

// if (paramEditable) {
// dataTableSettings.editable = true;
// }

// if (enablePaging) {
// dataTableSettings.pageable = true;
// dataTableSettings.pagerMode = "advanced";
// }

// $(uid).jqxGrid(dataTableSettings);
// }

// Customer Files block START

function deleteFileSelectionHandler() {
  var selectedRows = getGridSelectedRows("fileExplorerGrid");
  if (
    confirm(
      "You have selected " +
      selectedRows.length +
      " file. Are you sure you want to delete them all?"
    )
  ) {
    var filesToBeDeleted = [];
    selectedRows
      // .map(function (index) {
      // 	return getGridRowData('fileExplorerGrid', index);
      // })
      .forEach(function (rowData) {
        if (rowData.isDirectory) {
          filesToBeDeleted = filesToBeDeleted.concat(
            activeCustomerFiles
              .filter(function (ele) {
                var fileToBeDeleted = encodeURI(rowData.fileLabel) + "/";
                var parentPath = getParentFilePath();

                if (parentPath && parentPath.length > 0 && parentPath !== "/") {
                  fileToBeDeleted = parentPath + fileToBeDeleted;
                }
                return (
                  ele.fileName.startsWith(fileToBeDeleted) &&
                  !ele.fileName.endsWith(systemFileLabel)
                );
              })
              .map(function (ele) {
                return ele.fileName;
              })
          );
        } else {
          var fileToBeDeleted = rowData.fileName;
          if (!fileToBeDeleted.endsWith(systemFileLabel)) {
            filesToBeDeleted.push(fileToBeDeleted);
          }
        }
      });
    deleteFiles(filesToBeDeleted);
    // $('#customerFilesTable').jqxGrid('clearselection');
  }
}

function deleteFiles(filesToBeDeleted) {
  var fileContents = filesToBeDeleted.map(function (fileName) {
    var fileToBeDeleted = {
      FileName: fileName
    };
    return fileToBeDeleted;
  });

  var parentPath = getParentFilePath();

  $("#ajaxLoader").addClass("is-active");

  directCall("rt89", "NuoFileContentList", fileContents)
    .done(function (data) {
      handleResponse(data);
    })
    .fail(function () {
      if (sessionId === "X") {
        var data = {
          StatusCode: 200,
          Status: "OK",
          Content: "Successfully deleted columns."
        };
        handleResponse(data);
      }
    });
  var handleResponse = function (data) {
    if (data.StatusCode && data.StatusCode === 200) {
      if (activeCustomerFiles) {
        activeCustomerFiles = activeCustomerFiles.filter(function (ele) {
          var result =
            filesToBeDeleted.filter(function (child) {
              return ele.fileName === child;
            }).length == 0;
          return result;
        });
      }
      loadCustomerFilesTable(parentPath);
      $("#ajaxLoader").removeClass("is-active");
    } else {
      //window.location.reload();
    }
  };
}

//To create folder
function newFolderButtonHandler() {
  var folderName = prompt("Folder Name", "New Folder");
  if (folderName != null && folderName.trim().length > 0) {
    var parentPath = getParentFilePath();

    var fileObject = {
      author: activeUsername,
      sizeBytes: 0,
      dateCreatedMillis: Date.now(),
      dateModifiedMillis: Date.now()
    };

    var folderFileName = encodeURI(folderName) + "/" + systemFileLabel;
    if (parentPath && parentPath.length > 0 && parentPath !== "/") {
      folderFileName = parentPath + folderFileName;
    }

    fileObject.fileName = folderFileName;
    console.log(JSON.stringify(fileObject));
    activeCustomerFiles.push(fileObject);
    loadCustomerFilesTable(parentPath);
  }
}

function evaFilesButtonHandler() {
  loadCustomerDirWindow(activeUsername);
}

function profileImageSelectionHandler(fileEvent) {
  var files = fileEvent.target.files; // FileList object

  if (files.length > 0) {
    var file = files[0];

    // Loop through the FileList and render image files as thumbnails.
    var reader = new FileReader();

    // Closure to capture the file information.
    reader.onload = (function (loadedFile) {
      return function () {
        // var targetFileName = encodeURI(loadedFile.name);
        var fileToBeUploaded = "ProfileImage/Latest";
        var fileObject = {
          author: activeUsername,
          sizeBytes: loadedFile.size,
          dateCreatedMillis: Date.now(),
          dateModifiedMillis: Date.now()
        };

        fileObject.fileName = fileToBeUploaded;
        fileToBeUploaded = fileToBeUploaded;
        $("#ajaxLoader").addClass("is-active");
        directCall("rt79", "NuoFileContent", {
          FileName: fileToBeUploaded,
          ContentType: loadedFile.type
        })
          .done(function (data) {
            handleResponse(data);
          })
          .fail(function () {
            if (sessionId === "X") {
              var data = {
                StatusCode: 200,
                Status: "OK",
                Content: "Successfully deleted columns."
              };
              handleResponse(data);
            }
          });
        var handleResponse = function (data) {
          if (data.StatusCode && data.StatusCode === 200) {
            $.ajax({
              type: "PUT",
              url: data.Content,
              // Content type must much with the parameter you signed your URL with
              headers: {
                // "Content-Disposition": "attachment; filename=\""+loadedFile.name.split("/").map(function(ele){return encodeURI(ele)}).join("/")+"\""
                "Content-Disposition":
                  'attachment; filename="' + loadedFile.name + '"'
              },
              contentType: loadedFile.type,
              // this flag is important, if not set, it will try to send data as a form
              processData: false,
              // the actual file is sent raw
              data: loadedFile,
              success: function () {
                console.log(JSON.stringify(fileObject));
                profileImageUrl = URL.createObjectURL(loadedFile);
                $("#profileImage").attr("src", profileImageUrl);
                $("#ajaxLoader").removeClass("is-active");
              },
              error: function (data) {
                alert(
                  "File " +
                  loadedFile.name +
                  " could not be uploaded. If this is not because of your betwork, please contact NuoCanvas support."
                );
                console.log(data);
                $("#ajaxLoader").removeClass("is-active");
              }
            });
            return false;
          } else {
            $('#profileImage').attr('src', URL.createObjectURL(loadedFile));
            $("#ajaxLoader").removeClass("is-active");
            //window.location.reload();
          }
        };
      };
    })(file);
    reader.readAsDataURL(file);
  }
}

function uploadFileSeletionHandler(fileEvent) {
  var files = fileEvent.target.files; // FileList object
  var pendingFiles = 0;

  // Loop through the FileList and render image files as thumbnails.
  for (var i = 0, file; (file = files[i]); i++) {
    var reader = new FileReader();

    // Closure to capture the file information.
    reader.onload = (function (loadedFile) {
      return function () {
        // var targetFileName = encodeURI(loadedFile.name);
        var fileToBeUploaded = encodeURI(loadedFile.name);
        var fileObject = {
          author: activeUsername,
          sizeBytes: loadedFile.size,
          dateCreatedMillis: Date.now(),
          dateModifiedMillis: Date.now()
        };

        var parentPath = getParentFilePath();
        if (parentPath && parentPath.length > 0 && parentPath !== "/") {
          fileToBeUploaded = parentPath + fileToBeUploaded;
        }
        fileObject.fileName = fileToBeUploaded;
        fileToBeUploaded = fileToBeUploaded;
        $("#ajaxLoader").addClass("is-active");
        pendingFiles += 1;
        directCall("rt79", "NuoFileContent", {
          FileName: fileToBeUploaded,
          ContentType: loadedFile.type
        })
          .done(function (data) {
            handleResponse(data);
          })
          .fail(function () {
            if (sessionId === "X") {
              var data = {
                StatusCode: 200,
                Status: "OK",
                Content: "Successfully deleted columns."
              };
              handleResponse(data);
            }
          });
        var handleResponse = function (data) {
          if (data.StatusCode && data.StatusCode === 200) {
            $.ajax({
              type: "PUT",
              url: data.Content,
              // Content type must much with the parameter you signed your URL with
              headers: {
                // "Content-Disposition": "attachment; filename=\""+loadedFile.name.split("/").map(function(ele){return encodeURI(ele)}).join("/")+"\""
                "Content-Disposition":
                  'attachment; filename="' + loadedFile.name + '"'
              },
              contentType: loadedFile.type,
              // this flag is important, if not set, it will try to send data as a form
              processData: false,
              // the actual file is sent raw
              data: loadedFile,
              success: function () {
                console.log(JSON.stringify(fileObject));
                activeCustomerFiles.push(fileObject);
                loadCustomerFilesTable(parentPath);
                pendingFiles -= 1;
                if (pendingFiles == 0) {
                  $("#ajaxLoader").removeClass("is-active");
                }
              },
              error: function (data) {
                alert(
                  "File " +
                  loadedFile.name +
                  " could not be uploaded. If this is not because of your betwork, please contact NuoCanvas support."
                );
                console.log(data);
                pendingFiles -= 1;
                if (pendingFiles == 0) {
                  $("#ajaxLoader").removeClass("is-active");
                }
              }
            });
            return false;
          } else {
            //window.location.reload();
          }
        };
      };
    })(file);
    reader.readAsDataURL(file);
  }
}

function downloadFileSeletionHandler(fileEvent) {
  getGridSelectedRows("fileExplorerGrid").forEach(function (rowData) {
    if (rowData) {
      if (rowData.isDirectory) {
        //Logic to download entire folder needs to be added
        alert("Currently you can only download one file at a time.");
      } else {
        var fileToBeDownloaded = encodeURI(rowData.fileLabel);

        if (!fileToBeDownloaded.endsWith(systemFileLabel)) {
          var parentPath = rowData.parentPath;
          if (parentPath && parentPath.length > 0 && parentPath !== "/") {
            fileToBeDownloaded = parentPath + fileToBeDownloaded;
          }
          fileToBeDownloaded = fileToBeDownloaded;
          directCall("rt83", "NuoFileContent", { FileName: fileToBeDownloaded })
            .done(function (data) {
              handleResponse(data);
            })
            .fail(function () {
              if (sessionId === "X") {
                var data = {
                  StatusCode: 200,
                  Status: "OK",
                  Content: "Successfully deleted columns."
                };
                handleResponse(data);
              }
            });
          var handleResponse = function (data) {
            if (data.StatusCode && data.StatusCode === 200) {
              var downloadEle = document.getElementById("downloadTag");
              downloadEle.setAttribute("href", data.Content);
              downloadEle.click();
            } else {
              //window.location.reload();
            }
          };
        }
      }
    }
  });
}

function getParentFilePath() {
  var firstElement = getGridRowData("fileExplorerGrid", 0);
  if (firstElement) return firstElement.parentPath;
  else return "";
}

function loadCustomerDirWindow(activeUsername) {
  // alert(activeUsername);
  $("#fileExplorerWindow").fadeIn();
  lastClickedRowIndex = -1;
  $("#closeFileExplorerWindow").off("click");
  $("#closeFileExplorerWindow").on("click", function (e) {
    e.preventDefault();
    $("#fileExplorerWindow").fadeOut();
    lastClickedRowIndex = -1;
  });

  // var screenWidth = $(window).width();
  // var screenHeight = $(window).height();

  // var windowWidth = 800;
  // var windowHeight = 500;

  // $("#customerDirWindow").jqxWindow(
  // 	{
  // 		width: windowWidth,
  // 		height: windowHeight,
  // 		isModal: true,
  // 		resizable: true,
  // 		autoOpen:	false,
  // 		// cancelButton: $("#storageEntitiesCloseButton"),
  // 		position: { x: (screenWidth / 2) - 300, y: 100},
  // 		theme: jqWidgetThemeName
  // 	}
  // );
  // $("#customerDirWindow").jqxWindow("setTitle","Files");
  loadCustomerFilesTable("");

  // $("#customerDirWindow").jqxWindow("open");
}

function loadCustomerFilesTable(parentPath, shouldRefresh) {
  if (
    shouldRefresh ||
    !activeCustomerFiles ||
    activeCustomerFiles.length == 0
  ) {
    directCall("rt97")
      .done(function (data) {
        handleResponse(data);
      })
      .fail(function () {
        if (sessionId === "X") {
          var data = {
            StatusCode: 200,
            Status: "OK",
            Content: {
              NuoEvaFiles: [
                {
                  fileName: "Banking_Batch_age/predict/000000000000",
                  sizeBytes: 476308,
                  dateCreatedMillis: 1529344191092
                },
                {
                  fileName: "Banking_Batch_age/predict/000000000000%20(1)",
                  sizeBytes: 483444,
                  dateCreatedMillis: 1529344195231
                },
                {
                  fileName: "Banking_Batch_age/predict/000000000000%20(2)",
                  sizeBytes: 476308,
                  dateCreatedMillis: 1529344193327
                },
                {
                  fileName: "Banking_Batch_age/predict/000000000000%20(3)",
                  sizeBytes: 476308,
                  dateCreatedMillis: 1529344193757
                },
                {
                  fileName: "Banking_Batch_age/train/000000000000",
                  sizeBytes: 476308,
                  dateCreatedMillis: 1529344194130
                },
                {
                  fileName:
                    "Banking_age/output/tenant1_Banking_age_Prediction_1526578500072-000000000000.gz",
                  sizeBytes: 26690,
                  dateCreatedMillis: 1529344195215
                },
                {
                  fileName: "Banking_age/predict/000000000000",
                  sizeBytes: 483444,
                  dateCreatedMillis: 1529344195129
                },
                {
                  fileName: "Banking_age/train/000000000000",
                  sizeBytes: 4995382,
                  dateCreatedMillis: 1529344194657
                },
                {
                  fileName: "Eva_Demo_Data/Account/Account_100000.txt",
                  sizeBytes: 6570585,
                  dateCreatedMillis: 1529344195428
                },
                {
                  fileName: "Eva_Demo_Data/Account/Account_1000000.txt",
                  sizeBytes: 6682288,
                  dateCreatedMillis: 1529344191647
                },
                {
                  fileName: "Eva_Demo_Data/Account/Account_1100000.txt",
                  sizeBytes: 6782778,
                  dateCreatedMillis: 1529344194538
                },
                {
                  fileName: "Eva_Demo_Data/Account/Account_1200000.txt",
                  sizeBytes: 6780962,
                  dateCreatedMillis: 1529344194317
                },
                {
                  fileName: "Eva_Demo_Data/Account/Account_1300000.txt",
                  sizeBytes: 6781310,
                  dateCreatedMillis: 1529344191960
                },
                {
                  fileName: "Eva_Demo_Data/Account/Account_1400000.txt",
                  sizeBytes: 6782619,
                  dateCreatedMillis: 1529344191232
                },
                {
                  fileName: "Eva_Demo_Data/Account/Account_1500000.txt",
                  sizeBytes: 6780913,
                  dateCreatedMillis: 1529344192357
                },
                {
                  fileName: "Eva_Demo_Data/Account/Account_1600000.txt",
                  sizeBytes: 6781426,
                  dateCreatedMillis: 1529344194130
                },
                {
                  fileName: "Eva_Demo_Data/Account/Account_1700000.txt",
                  sizeBytes: 6781983,
                  dateCreatedMillis: 1529344193926
                },
                {
                  fileName: "Eva_Demo_Data/Account/Account_1800000.txt",
                  sizeBytes: 6782413,
                  dateCreatedMillis: 1529344191324
                },
                {
                  fileName: "Eva_Demo_Data/Account/Account_1900000.txt",
                  sizeBytes: 6781419,
                  dateCreatedMillis: 1529344191450
                },
                {
                  fileName: "Eva_Demo_Data/Account/Account_200000.txt",
                  sizeBytes: 6682200,
                  dateCreatedMillis: 1529344194499
                },
                {
                  fileName: "Eva_Demo_Data/Account/Account_2000000.txt",
                  sizeBytes: 6781667,
                  dateCreatedMillis: 1529344195723
                },
                {
                  fileName: "Eva_Demo_Data/Account/Account_2100000.txt",
                  sizeBytes: 6782595,
                  dateCreatedMillis: 1529344194125
                },
                {
                  fileName: "Eva_Demo_Data/Account/Account_2200000.txt",
                  sizeBytes: 6781891,
                  dateCreatedMillis: 1529344192157
                },
                {
                  fileName: "Eva_Demo_Data/Account/Account_2300000.txt",
                  sizeBytes: 6781916,
                  dateCreatedMillis: 1529344192290
                },
                {
                  fileName: "Eva_Demo_Data/Account/Account_2400000.txt",
                  sizeBytes: 6781789,
                  dateCreatedMillis: 1529344194032
                },
                {
                  fileName: "Eva_Demo_Data/Account/Account_2500000.txt",
                  sizeBytes: 6781982,
                  dateCreatedMillis: 1529344194749
                },
                {
                  fileName: "Eva_Demo_Data/Account/Account_2600000.txt",
                  sizeBytes: 6782077,
                  dateCreatedMillis: 1529344194527
                },
                {
                  fileName: "Eva_Demo_Data/Account/Account_2700000.txt",
                  sizeBytes: 6780811,
                  dateCreatedMillis: 1529344192957
                },
                {
                  fileName: "Eva_Demo_Data/Account/Account_2800000.txt",
                  sizeBytes: 6781605,
                  dateCreatedMillis: 1529344191118
                },
                {
                  fileName: "Eva_Demo_Data/Account/Account_2900000.txt",
                  sizeBytes: 6781476,
                  dateCreatedMillis: 1529344193888
                },
                {
                  fileName: "Eva_Demo_Data/Account/Account_300000.txt",
                  sizeBytes: 6682986,
                  dateCreatedMillis: 1529344194891
                },
                {
                  fileName: "Eva_Demo_Data/Account/Account_3000000.txt",
                  sizeBytes: 6783678,
                  dateCreatedMillis: 1529344192563
                },
                {
                  fileName: "Eva_Demo_Data/Account/Account_3100000.txt",
                  sizeBytes: 6781790,
                  dateCreatedMillis: 1529344191236
                },
                {
                  fileName: "Eva_Demo_Data/Account/Account_3200000.txt",
                  sizeBytes: 6781683,
                  dateCreatedMillis: 1529344193563
                },
                {
                  fileName: "Eva_Demo_Data/Account/Account_3300000.txt",
                  sizeBytes: 6782780,
                  dateCreatedMillis: 1529344191055
                },
                {
                  fileName: "Eva_Demo_Data/Account/Account_3400000.txt",
                  sizeBytes: 6783628,
                  dateCreatedMillis: 1529344193761
                },
                {
                  fileName: "Eva_Demo_Data/Account/Account_3500000.txt",
                  sizeBytes: 6781486,
                  dateCreatedMillis: 1529344193890
                },
                {
                  fileName: "Eva_Demo_Data/Account/Account_3600000.txt",
                  sizeBytes: 6782587,
                  dateCreatedMillis: 1529344191966
                },
                {
                  fileName: "Eva_Demo_Data/Account/Account_3700000.txt",
                  sizeBytes: 6782713,
                  dateCreatedMillis: 1529344191752
                },
                {
                  fileName: "Eva_Demo_Data/Account/Account_3800000.txt",
                  sizeBytes: 6782145,
                  dateCreatedMillis: 1529344192730
                },
                {
                  fileName: "Eva_Demo_Data/Account/Account_3900000.txt",
                  sizeBytes: 6781345,
                  dateCreatedMillis: 1529344193657
                },
                {
                  fileName: "Eva_Demo_Data/Account/Account_400000.txt",
                  sizeBytes: 6682349,
                  dateCreatedMillis: 1529344193424
                },
                {
                  fileName: "Eva_Demo_Data/Account/Account_4000000.txt",
                  sizeBytes: 6783553,
                  dateCreatedMillis: 1529344194860
                },
                {
                  fileName: "Eva_Demo_Data/Account/Account_4100000.txt",
                  sizeBytes: 6782402,
                  dateCreatedMillis: 1529344195082
                },
                {
                  fileName: "Eva_Demo_Data/Account/Account_4200000.txt",
                  sizeBytes: 6783076,
                  dateCreatedMillis: 1529344191057
                },
                {
                  fileName: "Eva_Demo_Data/Account/Account_4300000.txt",
                  sizeBytes: 6781996,
                  dateCreatedMillis: 1529344195632
                },
                {
                  fileName: "Eva_Demo_Data/Account/Account_4400000.txt",
                  sizeBytes: 6782493,
                  dateCreatedMillis: 1529344191560
                },
                {
                  fileName: "Eva_Demo_Data/Account/Account_4500000.txt",
                  sizeBytes: 6782667,
                  dateCreatedMillis: 1529344195535
                },
                {
                  fileName: "Eva_Demo_Data/Account/Account_4600000.txt",
                  sizeBytes: 6781823,
                  dateCreatedMillis: 1529344192461
                },
                {
                  fileName: "Eva_Demo_Data/Account/Account_4700000.txt",
                  sizeBytes: 6780510,
                  dateCreatedMillis: 1529344194248
                },
                {
                  fileName: "Eva_Demo_Data/Account/Account_4800000.txt",
                  sizeBytes: 6782164,
                  dateCreatedMillis: 1529344191553
                },
                {
                  fileName: "Eva_Demo_Data/Account/Account_4900000.txt",
                  sizeBytes: 6782337,
                  dateCreatedMillis: 1529344194747
                },
                {
                  fileName: "Eva_Demo_Data/Account/Account_500000.txt",
                  sizeBytes: 6681586,
                  dateCreatedMillis: 1529344191750
                },
                {
                  fileName: "Eva_Demo_Data/Account/Account_5000000.txt",
                  sizeBytes: 6783135,
                  dateCreatedMillis: 1529344191137
                },
                {
                  fileName: "Eva_Demo_Data/Account/Account_600000.txt",
                  sizeBytes: 6681731,
                  dateCreatedMillis: 1529344191751
                },
                {
                  fileName: "Eva_Demo_Data/Account/Account_700000.txt",
                  sizeBytes: 6682432,
                  dateCreatedMillis: 1529344191828
                },
                {
                  fileName: "Eva_Demo_Data/Account/Account_800000.txt",
                  sizeBytes: 6679525,
                  dateCreatedMillis: 1529344194257
                },
                {
                  fileName: "Eva_Demo_Data/Account/Account_900000.txt",
                  sizeBytes: 6682643,
                  dateCreatedMillis: 1529344193129
                },
                {
                  fileName: "Eva_Demo_Data/Address/Address_100000.txt",
                  sizeBytes: 6036581,
                  dateCreatedMillis: 1529344191753
                },
                {
                  fileName: "Eva_Demo_Data/Address/Address_1000000.txt",
                  sizeBytes: 6148363,
                  dateCreatedMillis: 1529344194126
                },
                {
                  fileName: "Eva_Demo_Data/Address/Address_1100000.txt",
                  sizeBytes: 6249598,
                  dateCreatedMillis: 1529344193348
                },
                {
                  fileName: "Eva_Demo_Data/Address/Address_1200000.txt",
                  sizeBytes: 6243974,
                  dateCreatedMillis: 1529344193257
                },
                {
                  fileName: "Eva_Demo_Data/Address/Address_1300000.txt",
                  sizeBytes: 6246682,
                  dateCreatedMillis: 1529344195428
                },
                {
                  fileName: "Eva_Demo_Data/Address/Address_1400000.txt",
                  sizeBytes: 6247470,
                  dateCreatedMillis: 1529344192260
                },
                {
                  fileName: "Eva_Demo_Data/Address/Address_1500000.txt",
                  sizeBytes: 6248834,
                  dateCreatedMillis: 1529344192352
                },
                {
                  fileName: "Eva_Demo_Data/Address/Address_1600000.txt",
                  sizeBytes: 6246566,
                  dateCreatedMillis: 1529344192559
                },
                {
                  fileName: "Eva_Demo_Data/Address/Address_1700000.txt",
                  sizeBytes: 6249252,
                  dateCreatedMillis: 1529344191636
                },
                {
                  fileName: "Eva_Demo_Data/Address/Address_1800000.txt",
                  sizeBytes: 6246138,
                  dateCreatedMillis: 1529344194454
                },
                {
                  fileName: "Eva_Demo_Data/Address/Address_1900000.txt",
                  sizeBytes: 6247758,
                  dateCreatedMillis: 1529344193657
                },
                {
                  fileName: "Eva_Demo_Data/Address/Address_200000.txt",
                  sizeBytes: 6147823,
                  dateCreatedMillis: 1529344194127
                },
                {
                  fileName: "Eva_Demo_Data/Address/Address_2000000.txt",
                  sizeBytes: 6246374,
                  dateCreatedMillis: 1529344195042
                },
                {
                  fileName: "Eva_Demo_Data/Address/Address_2100000.txt",
                  sizeBytes: 6246553,
                  dateCreatedMillis: 1529344195054
                },
                {
                  fileName: "Eva_Demo_Data/Address/Address_2200000.txt",
                  sizeBytes: 6247430,
                  dateCreatedMillis: 1529344192831
                },
                {
                  fileName: "Eva_Demo_Data/Address/Address_2300000.txt",
                  sizeBytes: 6247214,
                  dateCreatedMillis: 1529344193652
                },
                {
                  fileName: "Eva_Demo_Data/Address/Address_2400000.txt",
                  sizeBytes: 6247890,
                  dateCreatedMillis: 1529344195353
                },
                {
                  fileName: "Eva_Demo_Data/Address/Address_2500000.txt",
                  sizeBytes: 6245777,
                  dateCreatedMillis: 1529344193130
                },
                {
                  fileName: "Eva_Demo_Data/Address/Address_2600000.txt",
                  sizeBytes: 6245392,
                  dateCreatedMillis: 1529344193257
                },
                {
                  fileName: "Eva_Demo_Data/Address/Address_2700000.txt",
                  sizeBytes: 6247888,
                  dateCreatedMillis: 1529344191459
                },
                {
                  fileName: "Eva_Demo_Data/Address/Address_2800000.txt",
                  sizeBytes: 6245773,
                  dateCreatedMillis: 1529344192102
                },
                {
                  fileName: "Eva_Demo_Data/Address/Address_2900000.txt",
                  sizeBytes: 6247269,
                  dateCreatedMillis: 1529344191055
                },
                {
                  fileName: "Eva_Demo_Data/Address/Address_300000.txt",
                  sizeBytes: 6148849,
                  dateCreatedMillis: 1529344192711
                },
                {
                  fileName: "Eva_Demo_Data/Address/Address_3000000.txt",
                  sizeBytes: 6247694,
                  dateCreatedMillis: 1529344191828
                },
                {
                  fileName: "Eva_Demo_Data/Address/Address_3100000.txt",
                  sizeBytes: 6247404,
                  dateCreatedMillis: 1529344191828
                },
                {
                  fileName: "Eva_Demo_Data/Address/Address_3200000.txt",
                  sizeBytes: 6245488,
                  dateCreatedMillis: 1529344194761
                },
                {
                  fileName: "Eva_Demo_Data/Address/Address_3300000.txt",
                  sizeBytes: 6247966,
                  dateCreatedMillis: 1529344193428
                },
                {
                  fileName: "Eva_Demo_Data/Address/Address_3400000.txt",
                  sizeBytes: 6244246,
                  dateCreatedMillis: 1529344192257
                },
                {
                  fileName: "Eva_Demo_Data/Address/Address_3500000.txt",
                  sizeBytes: 6248638,
                  dateCreatedMillis: 1529344191822
                },
                {
                  fileName: "Eva_Demo_Data/Address/Address_3600000.txt",
                  sizeBytes: 6247693,
                  dateCreatedMillis: 1529344195626
                },
                {
                  fileName: "Eva_Demo_Data/Address/Address_3700000.txt",
                  sizeBytes: 6247823,
                  dateCreatedMillis: 1529344192256
                },
                {
                  fileName: "Eva_Demo_Data/Address/Address_3800000.txt",
                  sizeBytes: 6248076,
                  dateCreatedMillis: 1529344193525
                },
                {
                  fileName: "Eva_Demo_Data/Address/Address_3900000.txt",
                  sizeBytes: 6247299,
                  dateCreatedMillis: 1529344191950
                },
                {
                  fileName: "Eva_Demo_Data/Address/Address_400000.txt",
                  sizeBytes: 6149468,
                  dateCreatedMillis: 1529344192661
                },
                {
                  fileName: "Eva_Demo_Data/Address/Address_4000000.txt",
                  sizeBytes: 6246696,
                  dateCreatedMillis: 1529344194752
                },
                {
                  fileName: "Eva_Demo_Data/Address/Address_4100000.txt",
                  sizeBytes: 6250221,
                  dateCreatedMillis: 1529344193749
                },
                {
                  fileName: "Eva_Demo_Data/Address/Address_4200000.txt",
                  sizeBytes: 6246868,
                  dateCreatedMillis: 1529344191998
                },
                {
                  fileName: "Eva_Demo_Data/Address/Address_4300000.txt",
                  sizeBytes: 6248571,
                  dateCreatedMillis: 1529344191328
                },
                {
                  fileName: "Eva_Demo_Data/Address/Address_4400000.txt",
                  sizeBytes: 6249432,
                  dateCreatedMillis: 1529344195355
                },
                {
                  fileName: "Eva_Demo_Data/Address/Address_4500000.txt",
                  sizeBytes: 6248733,
                  dateCreatedMillis: 1529344195031
                },
                {
                  fileName: "Eva_Demo_Data/Address/Address_4600000.txt",
                  sizeBytes: 6247740,
                  dateCreatedMillis: 1529344192732
                },
                {
                  fileName: "Eva_Demo_Data/Address/Address_4700000.txt",
                  sizeBytes: 6246371,
                  dateCreatedMillis: 1529344193327
                },
                {
                  fileName: "Eva_Demo_Data/Address/Address_4800000.txt",
                  sizeBytes: 6248351,
                  dateCreatedMillis: 1529344194254
                },
                {
                  fileName: "Eva_Demo_Data/Address/Address_4900000.txt",
                  sizeBytes: 6248923,
                  dateCreatedMillis: 1529344192650
                },
                {
                  fileName: "Eva_Demo_Data/Address/Address_500000.txt",
                  sizeBytes: 6148601,
                  dateCreatedMillis: 1529344191965
                },
                {
                  fileName: "Eva_Demo_Data/Address/Address_5000000.txt",
                  sizeBytes: 6246761,
                  dateCreatedMillis: 1529344193158
                },
                {
                  fileName: "Eva_Demo_Data/Address/Address_600000.txt",
                  sizeBytes: 6146732,
                  dateCreatedMillis: 1529344192862
                },
                {
                  fileName: "Eva_Demo_Data/Address/Address_700000.txt",
                  sizeBytes: 6148601,
                  dateCreatedMillis: 1529344194357
                },
                {
                  fileName: "Eva_Demo_Data/Address/Address_800000.txt",
                  sizeBytes: 6148900,
                  dateCreatedMillis: 1529344191658
                },
                {
                  fileName: "Eva_Demo_Data/Address/Address_900000.txt",
                  sizeBytes: 6147543,
                  dateCreatedMillis: 1529344191814
                },
                {
                  fileName: "Eva_Demo_Data/Bank.txt",
                  sizeBytes: 6179,
                  dateCreatedMillis: 1529344194127
                },
                {
                  fileName: "Eva_Demo_Data/Credit_Card/Credit_Card_100000.txt",
                  sizeBytes: 9533196,
                  dateCreatedMillis: 1529344191827
                },
                {
                  fileName: "Eva_Demo_Data/Credit_Card/Credit_Card_1000000.txt",
                  sizeBytes: 9640245,
                  dateCreatedMillis: 1529344191560
                },
                {
                  fileName: "Eva_Demo_Data/Credit_Card/Credit_Card_1100000.txt",
                  sizeBytes: 9743831,
                  dateCreatedMillis: 1529344191052
                },
                {
                  fileName: "Eva_Demo_Data/Credit_Card/Credit_Card_1200000.txt",
                  sizeBytes: 9742533,
                  dateCreatedMillis: 1529344192054
                },
                {
                  fileName: "Eva_Demo_Data/Credit_Card/Credit_Card_1300000.txt",
                  sizeBytes: 9739558,
                  dateCreatedMillis: 1529344191599
                },
                {
                  fileName: "Eva_Demo_Data/Credit_Card/Credit_Card_1400000.txt",
                  sizeBytes: 9743257,
                  dateCreatedMillis: 1529344191051
                },
                {
                  fileName: "Eva_Demo_Data/Credit_Card/Credit_Card_1500000.txt",
                  sizeBytes: 9749341,
                  dateCreatedMillis: 1529344195250
                },
                {
                  fileName: "Eva_Demo_Data/Credit_Card/Credit_Card_1600000.txt",
                  sizeBytes: 9746642,
                  dateCreatedMillis: 1529344191457
                },
                {
                  fileName: "Eva_Demo_Data/Credit_Card/Credit_Card_1700000.txt",
                  sizeBytes: 9744336,
                  dateCreatedMillis: 1529344193939
                },
                {
                  fileName: "Eva_Demo_Data/Credit_Card/Credit_Card_1800000.txt",
                  sizeBytes: 9743051,
                  dateCreatedMillis: 1529344194532
                },
                {
                  fileName: "Eva_Demo_Data/Credit_Card/Credit_Card_1900000.txt",
                  sizeBytes: 9745571,
                  dateCreatedMillis: 1529344191950
                },
                {
                  fileName: "Eva_Demo_Data/Credit_Card/Credit_Card_200000.txt",
                  sizeBytes: 9641355,
                  dateCreatedMillis: 1529344194529
                },
                {
                  fileName: "Eva_Demo_Data/Credit_Card/Credit_Card_2000000.txt",
                  sizeBytes: 9743527,
                  dateCreatedMillis: 1529344192830
                },
                {
                  fileName: "Eva_Demo_Data/Credit_Card/Credit_Card_300000.txt",
                  sizeBytes: 9642356,
                  dateCreatedMillis: 1529344192362
                },
                {
                  fileName: "Eva_Demo_Data/Credit_Card/Credit_Card_400000.txt",
                  sizeBytes: 9646232,
                  dateCreatedMillis: 1529344191058
                },
                {
                  fileName: "Eva_Demo_Data/Credit_Card/Credit_Card_500000.txt",
                  sizeBytes: 9643935,
                  dateCreatedMillis: 1529344195231
                },
                {
                  fileName: "Eva_Demo_Data/Credit_Card/Credit_Card_600000.txt",
                  sizeBytes: 9646168,
                  dateCreatedMillis: 1529344192467
                },
                {
                  fileName: "Eva_Demo_Data/Credit_Card/Credit_Card_700000.txt",
                  sizeBytes: 9645640,
                  dateCreatedMillis: 1529344195311
                },
                {
                  fileName: "Eva_Demo_Data/Credit_Card/Credit_Card_800000.txt",
                  sizeBytes: 9648196,
                  dateCreatedMillis: 1529344192951
                },
                {
                  fileName: "Eva_Demo_Data/Credit_Card/Credit_Card_900000.txt",
                  sizeBytes: 9643630,
                  dateCreatedMillis: 1529344194131
                },
                {
                  fileName: "Eva_Demo_Data/Credit_Card_Known.csv",
                  sizeBytes: 310398,
                  dateCreatedMillis: 1529344193324
                },
                {
                  fileName: "Eva_Demo_Data/Credit_Card_Unknown.csv",
                  sizeBytes: 4041,
                  dateCreatedMillis: 1529344193943
                },
                {
                  fileName: "Eva_Demo_Data/Image/Image1.jpg",
                  sizeBytes: 247223,
                  dateCreatedMillis: 1529827303424
                },
                {
                  fileName: "Eva_Demo_Data/Image/Image2.jpg",
                  sizeBytes: 11733,
                  dateCreatedMillis: 1529827352881
                },
                {
                  fileName: "Eva_Demo_Data/Image/Image3.jpg",
                  sizeBytes: 12926,
                  dateCreatedMillis: 1529829338085
                },
                {
                  fileName: "Eva_Demo_Data/Image/Image4.jpg",
                  sizeBytes: 193958,
                  dateCreatedMillis: 1529855850614
                },
                {
                  fileName: "Eva_Demo_Data/Image/Image5.jpg",
                  sizeBytes: 46865,
                  dateCreatedMillis: 1529860312340
                },
                {
                  fileName: "Eva_Demo_Data/Image/Image6.jpg",
                  sizeBytes: 13352,
                  dateCreatedMillis: 1529855993141
                },
                {
                  fileName: "Eva_Demo_Data/Transaction/Transaction_100000.txt",
                  sizeBytes: 7729157,
                  dateCreatedMillis: 1529344194658
                },
                {
                  fileName: "Eva_Demo_Data/Transaction/Transaction_1000000.txt",
                  sizeBytes: 7729401,
                  dateCreatedMillis: 1529344192555
                },
                {
                  fileName:
                    "Eva_Demo_Data/Transaction/Transaction_10000000.txt",
                  sizeBytes: 7729142,
                  dateCreatedMillis: 1529344193652
                },
                {
                  fileName: "Eva_Demo_Data/Transaction/Transaction_1100000.txt",
                  sizeBytes: 7727864,
                  dateCreatedMillis: 1529344194966
                },
                {
                  fileName: "Eva_Demo_Data/Transaction/Transaction_1200000.txt",
                  sizeBytes: 7728414,
                  dateCreatedMillis: 1529344194658
                },
                {
                  fileName: "Eva_Demo_Data/Transaction/Transaction_1300000.txt",
                  sizeBytes: 7730016,
                  dateCreatedMillis: 1529344194950
                },
                {
                  fileName: "Eva_Demo_Data/Transaction/Transaction_1400000.txt",
                  sizeBytes: 7728538,
                  dateCreatedMillis: 1529344192157
                },
                {
                  fileName: "Eva_Demo_Data/Transaction/Transaction_1500000.txt",
                  sizeBytes: 7728798,
                  dateCreatedMillis: 1529344194040
                },
                {
                  fileName: "Eva_Demo_Data/Transaction/Transaction_1600000.txt",
                  sizeBytes: 7729122,
                  dateCreatedMillis: 1529344191057
                },
                {
                  fileName: "Eva_Demo_Data/Transaction/Transaction_1700000.txt",
                  sizeBytes: 7730090,
                  dateCreatedMillis: 1529344191221
                },
                {
                  fileName: "Eva_Demo_Data/Transaction/Transaction_1800000.txt",
                  sizeBytes: 7729273,
                  dateCreatedMillis: 1529344193526
                },
                {
                  fileName: "Eva_Demo_Data/Transaction/Transaction_1900000.txt",
                  sizeBytes: 7728651,
                  dateCreatedMillis: 1529344193251
                },
                {
                  fileName: "Eva_Demo_Data/Transaction/Transaction_200000.txt",
                  sizeBytes: 7728262,
                  dateCreatedMillis: 1529344191054
                },
                {
                  fileName: "Eva_Demo_Data/Transaction/Transaction_2000000.txt",
                  sizeBytes: 7728258,
                  dateCreatedMillis: 1529344195364
                },
                {
                  fileName: "Eva_Demo_Data/Transaction/Transaction_2100000.txt",
                  sizeBytes: 7728604,
                  dateCreatedMillis: 1529344191232
                },
                {
                  fileName: "Eva_Demo_Data/Transaction/Transaction_2200000.txt",
                  sizeBytes: 7728886,
                  dateCreatedMillis: 1529344191652
                },
                {
                  fileName: "Eva_Demo_Data/Transaction/Transaction_2300000.txt",
                  sizeBytes: 7728592,
                  dateCreatedMillis: 1529344191233
                },
                {
                  fileName: "Eva_Demo_Data/Transaction/Transaction_2400000.txt",
                  sizeBytes: 7730523,
                  dateCreatedMillis: 1529344191451
                },
                {
                  fileName: "Eva_Demo_Data/Transaction/Transaction_2500000.txt",
                  sizeBytes: 7728476,
                  dateCreatedMillis: 1529344191052
                },
                {
                  fileName: "Eva_Demo_Data/Transaction/Transaction_2600000.txt",
                  sizeBytes: 7729760,
                  dateCreatedMillis: 1529344191826
                },
                {
                  fileName: "Eva_Demo_Data/Transaction/Transaction_2700000.txt",
                  sizeBytes: 7728943,
                  dateCreatedMillis: 1529344195361
                },
                {
                  fileName: "Eva_Demo_Data/Transaction/Transaction_2800000.txt",
                  sizeBytes: 7729011,
                  dateCreatedMillis: 1529344193653
                },
                {
                  fileName: "Eva_Demo_Data/Transaction/Transaction_2900000.txt",
                  sizeBytes: 7728885,
                  dateCreatedMillis: 1529344192827
                },
                {
                  fileName: "Eva_Demo_Data/Transaction/Transaction_300000.txt",
                  sizeBytes: 7729147,
                  dateCreatedMillis: 1529344194350
                },
                {
                  fileName: "Eva_Demo_Data/Transaction/Transaction_3000000.txt",
                  sizeBytes: 7728344,
                  dateCreatedMillis: 1529344194474
                },
                {
                  fileName: "Eva_Demo_Data/Transaction/Transaction_3100000.txt",
                  sizeBytes: 7729009,
                  dateCreatedMillis: 1529344192150
                },
                {
                  fileName: "Eva_Demo_Data/Transaction/Transaction_3200000.txt",
                  sizeBytes: 7728798,
                  dateCreatedMillis: 1529344195126
                },
                {
                  fileName: "Eva_Demo_Data/Transaction/Transaction_3300000.txt",
                  sizeBytes: 7729233,
                  dateCreatedMillis: 1529344193026
                },
                {
                  fileName: "Eva_Demo_Data/Transaction/Transaction_3400000.txt",
                  sizeBytes: 7728654,
                  dateCreatedMillis: 1529344193751
                },
                {
                  fileName: "Eva_Demo_Data/Transaction/Transaction_3500000.txt",
                  sizeBytes: 7728893,
                  dateCreatedMillis: 1529344192157
                },
                {
                  fileName: "Eva_Demo_Data/Transaction/Transaction_3600000.txt",
                  sizeBytes: 7729803,
                  dateCreatedMillis: 1529344195028
                },
                {
                  fileName: "Eva_Demo_Data/Transaction/Transaction_3700000.txt",
                  sizeBytes: 7728795,
                  dateCreatedMillis: 1529344192356
                },
                {
                  fileName: "Eva_Demo_Data/Transaction/Transaction_3800000.txt",
                  sizeBytes: 7728569,
                  dateCreatedMillis: 1529344191127
                },
                {
                  fileName: "Eva_Demo_Data/Transaction/Transaction_3900000.txt",
                  sizeBytes: 7728169,
                  dateCreatedMillis: 1529344192552
                },
                {
                  fileName: "Eva_Demo_Data/Transaction/Transaction_400000.txt",
                  sizeBytes: 7728391,
                  dateCreatedMillis: 1529344194210
                },
                {
                  fileName: "Eva_Demo_Data/Transaction/Transaction_4000000.txt",
                  sizeBytes: 7729445,
                  dateCreatedMillis: 1529344191828
                },
                {
                  fileName: "Eva_Demo_Data/Transaction/Transaction_4100000.txt",
                  sizeBytes: 7728653,
                  dateCreatedMillis: 1529344191070
                },
                {
                  fileName: "Eva_Demo_Data/Transaction/Transaction_4200000.txt",
                  sizeBytes: 7729088,
                  dateCreatedMillis: 1529344193136
                },
                {
                  fileName: "Eva_Demo_Data/Transaction/Transaction_4300000.txt",
                  sizeBytes: 7729336,
                  dateCreatedMillis: 1529344193854
                },
                {
                  fileName: "Eva_Demo_Data/Transaction/Transaction_4400000.txt",
                  sizeBytes: 7729026,
                  dateCreatedMillis: 1529344194672
                },
                {
                  fileName: "Eva_Demo_Data/Transaction/Transaction_4500000.txt",
                  sizeBytes: 7727984,
                  dateCreatedMillis: 1529344191626
                },
                {
                  fileName: "Eva_Demo_Data/Transaction/Transaction_4600000.txt",
                  sizeBytes: 7727895,
                  dateCreatedMillis: 1529344194204
                },
                {
                  fileName: "Eva_Demo_Data/Transaction/Transaction_4700000.txt",
                  sizeBytes: 7728013,
                  dateCreatedMillis: 1529344193831
                },
                {
                  fileName: "Eva_Demo_Data/Transaction/Transaction_4800000.txt",
                  sizeBytes: 7730398,
                  dateCreatedMillis: 1529344194136
                },
                {
                  fileName: "Eva_Demo_Data/Transaction/Transaction_4900000.txt",
                  sizeBytes: 7729461,
                  dateCreatedMillis: 1529344191757
                },
                {
                  fileName: "Eva_Demo_Data/Transaction/Transaction_500000.txt",
                  sizeBytes: 7729481,
                  dateCreatedMillis: 1529344194522
                },
                {
                  fileName: "Eva_Demo_Data/Transaction/Transaction_5000000.txt",
                  sizeBytes: 7728590,
                  dateCreatedMillis: 1529344192957
                },
                {
                  fileName: "Eva_Demo_Data/Transaction/Transaction_5100000.txt",
                  sizeBytes: 7729047,
                  dateCreatedMillis: 1529344194038
                },
                {
                  fileName: "Eva_Demo_Data/Transaction/Transaction_5200000.txt",
                  sizeBytes: 7729200,
                  dateCreatedMillis: 1529344193127
                },
                {
                  fileName: "Eva_Demo_Data/Transaction/Transaction_5300000.txt",
                  sizeBytes: 7728754,
                  dateCreatedMillis: 1529344194131
                },
                {
                  fileName: "Eva_Demo_Data/Transaction/Transaction_5400000.txt",
                  sizeBytes: 7729307,
                  dateCreatedMillis: 1529344194251
                },
                {
                  fileName: "Eva_Demo_Data/Transaction/Transaction_5500000.txt",
                  sizeBytes: 7728549,
                  dateCreatedMillis: 1529344191827
                },
                {
                  fileName: "Eva_Demo_Data/Transaction/Transaction_5600000.txt",
                  sizeBytes: 7729694,
                  dateCreatedMillis: 1529344194958
                },
                {
                  fileName: "Eva_Demo_Data/Transaction/Transaction_5700000.txt",
                  sizeBytes: 7729159,
                  dateCreatedMillis: 1529344193933
                },
                {
                  fileName: "Eva_Demo_Data/Transaction/Transaction_5800000.txt",
                  sizeBytes: 7729272,
                  dateCreatedMillis: 1529344194256
                },
                {
                  fileName: "Eva_Demo_Data/Transaction/Transaction_5900000.txt",
                  sizeBytes: 7729469,
                  dateCreatedMillis: 1529344193257
                },
                {
                  fileName: "Eva_Demo_Data/Transaction/Transaction_600000.txt",
                  sizeBytes: 7727664,
                  dateCreatedMillis: 1529344193325
                },
                {
                  fileName: "Eva_Demo_Data/Transaction/Transaction_6000000.txt",
                  sizeBytes: 7728272,
                  dateCreatedMillis: 1529344193331
                },
                {
                  fileName: "Eva_Demo_Data/Transaction/Transaction_6100000.txt",
                  sizeBytes: 7729128,
                  dateCreatedMillis: 1529344193560
                },
                {
                  fileName: "Eva_Demo_Data/Transaction/Transaction_6200000.txt",
                  sizeBytes: 7729035,
                  dateCreatedMillis: 1529344191651
                },
                {
                  fileName: "Eva_Demo_Data/Transaction/Transaction_6300000.txt",
                  sizeBytes: 7728880,
                  dateCreatedMillis: 1529344194746
                },
                {
                  fileName: "Eva_Demo_Data/Transaction/Transaction_6400000.txt",
                  sizeBytes: 7728213,
                  dateCreatedMillis: 1529344194252
                },
                {
                  fileName: "Eva_Demo_Data/Transaction/Transaction_6500000.txt",
                  sizeBytes: 7728615,
                  dateCreatedMillis: 1529344194023
                },
                {
                  fileName: "Eva_Demo_Data/Transaction/Transaction_6600000.txt",
                  sizeBytes: 7728929,
                  dateCreatedMillis: 1529344194255
                },
                {
                  fileName: "Eva_Demo_Data/Transaction/Transaction_6700000.txt",
                  sizeBytes: 7729008,
                  dateCreatedMillis: 1529344192456
                },
                {
                  fileName: "Eva_Demo_Data/Transaction/Transaction_6800000.txt",
                  sizeBytes: 7728244,
                  dateCreatedMillis: 1529344194127
                },
                {
                  fileName: "Eva_Demo_Data/Transaction/Transaction_6900000.txt",
                  sizeBytes: 7728839,
                  dateCreatedMillis: 1529344193030
                },
                {
                  fileName: "Eva_Demo_Data/Transaction/Transaction_700000.txt",
                  sizeBytes: 7729109,
                  dateCreatedMillis: 1529344192956
                },
                {
                  fileName: "Eva_Demo_Data/Transaction/Transaction_7000000.txt",
                  sizeBytes: 7728208,
                  dateCreatedMillis: 1529344194533
                },
                {
                  fileName: "Eva_Demo_Data/Transaction/Transaction_7100000.txt",
                  sizeBytes: 7729207,
                  dateCreatedMillis: 1529344194524
                },
                {
                  fileName: "Eva_Demo_Data/Transaction/Transaction_7200000.txt",
                  sizeBytes: 7728176,
                  dateCreatedMillis: 1529344191643
                },
                {
                  fileName: "Eva_Demo_Data/Transaction/Transaction_7300000.txt",
                  sizeBytes: 7728393,
                  dateCreatedMillis: 1529344191559
                },
                {
                  fileName: "Eva_Demo_Data/Transaction/Transaction_7400000.txt",
                  sizeBytes: 7730015,
                  dateCreatedMillis: 1529344195092
                },
                {
                  fileName: "Eva_Demo_Data/Transaction/Transaction_7500000.txt",
                  sizeBytes: 7729146,
                  dateCreatedMillis: 1529344192156
                },
                {
                  fileName: "Eva_Demo_Data/Transaction/Transaction_7600000.txt",
                  sizeBytes: 7728283,
                  dateCreatedMillis: 1529344194452
                },
                {
                  fileName: "Eva_Demo_Data/Transaction/Transaction_7700000.txt",
                  sizeBytes: 7730539,
                  dateCreatedMillis: 1529344191828
                },
                {
                  fileName: "Eva_Demo_Data/Transaction/Transaction_7800000.txt",
                  sizeBytes: 7728529,
                  dateCreatedMillis: 1529344191627
                },
                {
                  fileName: "Eva_Demo_Data/Transaction/Transaction_7900000.txt",
                  sizeBytes: 7729265,
                  dateCreatedMillis: 1529344191055
                },
                {
                  fileName: "Eva_Demo_Data/Transaction/Transaction_800000.txt",
                  sizeBytes: 7728319,
                  dateCreatedMillis: 1529344191222
                },
                {
                  fileName: "Eva_Demo_Data/Transaction/Transaction_8000000.txt",
                  sizeBytes: 7728619,
                  dateCreatedMillis: 1529344190979
                },
                {
                  fileName: "Eva_Demo_Data/Transaction/Transaction_8100000.txt",
                  sizeBytes: 7729469,
                  dateCreatedMillis: 1529344195026
                },
                {
                  fileName: "Eva_Demo_Data/Transaction/Transaction_8200000.txt",
                  sizeBytes: 7727671,
                  dateCreatedMillis: 1529344194367
                },
                {
                  fileName: "Eva_Demo_Data/Transaction/Transaction_8300000.txt",
                  sizeBytes: 7729420,
                  dateCreatedMillis: 1529344191232
                },
                {
                  fileName: "Eva_Demo_Data/Transaction/Transaction_8400000.txt",
                  sizeBytes: 7728507,
                  dateCreatedMillis: 1529344192166
                },
                {
                  fileName: "Eva_Demo_Data/Transaction/Transaction_8500000.txt",
                  sizeBytes: 7728839,
                  dateCreatedMillis: 1529344194039
                },
                {
                  fileName: "Eva_Demo_Data/Transaction/Transaction_8600000.txt",
                  sizeBytes: 7728485,
                  dateCreatedMillis: 1529344191964
                },
                {
                  fileName: "Eva_Demo_Data/Transaction/Transaction_8700000.txt",
                  sizeBytes: 7729231,
                  dateCreatedMillis: 1529344193525
                },
                {
                  fileName: "Eva_Demo_Data/Transaction/Transaction_8800000.txt",
                  sizeBytes: 7727578,
                  dateCreatedMillis: 1529344192730
                },
                {
                  fileName: "Eva_Demo_Data/Transaction/Transaction_8900000.txt",
                  sizeBytes: 7729591,
                  dateCreatedMillis: 1529344192723
                },
                {
                  fileName: "Eva_Demo_Data/Transaction/Transaction_900000.txt",
                  sizeBytes: 7728477,
                  dateCreatedMillis: 1529344195355
                },
                {
                  fileName: "Eva_Demo_Data/Transaction/Transaction_9000000.txt",
                  sizeBytes: 7729009,
                  dateCreatedMillis: 1529344195120
                },
                {
                  fileName: "Eva_Demo_Data/Transaction/Transaction_9100000.txt",
                  sizeBytes: 7728600,
                  dateCreatedMillis: 1529344194660
                },
                {
                  fileName: "Eva_Demo_Data/Transaction/Transaction_9200000.txt",
                  sizeBytes: 7727934,
                  dateCreatedMillis: 1529344193336
                },
                {
                  fileName: "Eva_Demo_Data/Transaction/Transaction_9300000.txt",
                  sizeBytes: 7728762,
                  dateCreatedMillis: 1529344193657
                },
                {
                  fileName: "Eva_Demo_Data/Transaction/Transaction_9400000.txt",
                  sizeBytes: 7729895,
                  dateCreatedMillis: 1529344191557
                },
                {
                  fileName: "Eva_Demo_Data/Transaction/Transaction_9500000.txt",
                  sizeBytes: 7730144,
                  dateCreatedMillis: 1529344192157
                },
                {
                  fileName: "Eva_Demo_Data/Transaction/Transaction_9600000.txt",
                  sizeBytes: 7728502,
                  dateCreatedMillis: 1529344192553
                },
                {
                  fileName: "Eva_Demo_Data/Transaction/Transaction_9700000.txt",
                  sizeBytes: 7728572,
                  dateCreatedMillis: 1529344192354
                },
                {
                  fileName: "Eva_Demo_Data/Transaction/Transaction_9800000.txt",
                  sizeBytes: 7729579,
                  dateCreatedMillis: 1529344193524
                },
                {
                  fileName: "Eva_Demo_Data/Transaction/Transaction_9900000.txt",
                  sizeBytes: 7728495,
                  dateCreatedMillis: 1529344192054
                },
                {
                  fileName:
                    "Internal/5b895236c4fbcfc7992ab1dc555ea4c1/output/e00cfb5dc781b714153899def3c58f68_Prediction_1534160976368-000000000000.gz",
                  sizeBytes: 2360,
                  dateCreatedMillis: 1534161114364
                },
                {
                  fileName:
                    "Internal/9a9a56183f67857d116f0c2797b807c1/output/0-6",
                  sizeBytes: 13688,
                  dateCreatedMillis: 1530030060482
                },
                {
                  fileName:
                    "Internal/f2bab3c391e62fdbf7c0f4e75d41e9e1/output/c0c89e9f5209699f28f40dfa2ee16a15_Prediction_1534278368712-000000000000.gz",
                  sizeBytes: 2364,
                  dateCreatedMillis: 1534278516499
                },
                {
                  fileName:
                    "KPMG/ES/ES%20300010%2020180824_Dataset_Example_NuoCanvas%20PREPARED.csv",
                  sizeBytes: 413865,
                  dateCreatedMillis: 1536290420760
                },
                {
                  fileName:
                    "KPMG/ES/ES%20300020%2020180824_Dataset_Example_NuoCanvas%20PREPARED.csv",
                  sizeBytes: 340775,
                  dateCreatedMillis: 1536290420085
                },
                {
                  fileName:
                    "KPMG/ES/ES%20300060%2020180824_Dataset_Example_NuoCanvas%20PREPARED.csv",
                  sizeBytes: 299332,
                  dateCreatedMillis: 1536290408111
                },
                {
                  fileName:
                    "KPMG/ES/ES%20300070%2020180824_Dataset_Example_NuoCanvas%20PREPARED.csv",
                  sizeBytes: 322846,
                  dateCreatedMillis: 1536290408540
                },
                {
                  fileName:
                    "KPMG/ES/ES%20300080%2020180824_Dataset_Example_NuoCanvas%20PREPARED.csv",
                  sizeBytes: 506673,
                  dateCreatedMillis: 1536290431392
                },
                {
                  fileName:
                    "KPMG/ES/ES%20300110%2020180824_Dataset_Example_NuoCanvas%20PREPARED.csv",
                  sizeBytes: 311089,
                  dateCreatedMillis: 1536290408087
                },
                {
                  fileName:
                    "KPMG/ES/ES%20300140%2020180824_Dataset_Example_NuoCanvas%20PREPARED.csv",
                  sizeBytes: 310065,
                  dateCreatedMillis: 1536290408128
                },
                {
                  fileName:
                    "KPMG/ES/ES%20301100%2020180824_Dataset_Example_NuoCanvas%20PREPARED.csv",
                  sizeBytes: 425165,
                  dateCreatedMillis: 1536290416506
                },
                {
                  fileName:
                    "KPMG/ES/ES%20301200%2020180824_Dataset_Example_NuoCanvas%20PREPARED.csv",
                  sizeBytes: 323205,
                  dateCreatedMillis: 1536290417729
                },
                {
                  fileName:
                    "KPMG/ES/ES%20302010%2020180824_Dataset_Example_NuoCanvas%20PREPARED.csv",
                  sizeBytes: 306589,
                  dateCreatedMillis: 1536290408103
                },
                {
                  fileName:
                    "KPMG/ES/ES%20302110%2020180824_Dataset_Example_NuoCanvas%20PREPARED.csv",
                  sizeBytes: 388387,
                  dateCreatedMillis: 1536290424911
                },
                {
                  fileName:
                    "KPMG/ES/ES%20310050%2020180824_Dataset_Example_NuoCanvas%20PREPARED.csv",
                  sizeBytes: 298939,
                  dateCreatedMillis: 1536290408058
                },
                {
                  fileName:
                    "KPMG/ES/ES%20310150%2020180824_Dataset_Example_NuoCanvas%20PREPARED.csv",
                  sizeBytes: 299125,
                  dateCreatedMillis: 1536290418415
                },
                {
                  fileName:
                    "KPMG/ES/ES%20311050%2020180824_Dataset_Example_NuoCanvas%20PREPARED.csv",
                  sizeBytes: 296804,
                  dateCreatedMillis: 1536290437343
                },
                {
                  fileName:
                    "KPMG/ES/ES%20311999%2020180824_Dataset_Example_NuoCanvas%20PREPARED.csv",
                  sizeBytes: 587324,
                  dateCreatedMillis: 1536290432431
                },
                {
                  fileName:
                    "KPMG/FR/FR%20300010%2020180824_Dataset_Example_NuoCanvas%20PREPARED.csv",
                  sizeBytes: 60460,
                  dateCreatedMillis: 1536290339614
                },
                {
                  fileName:
                    "KPMG/FR/FR%20300020%2020180824_Dataset_Example_NuoCanvas%20PREPARED.csv",
                  sizeBytes: 51676,
                  dateCreatedMillis: 1536290340862
                },
                {
                  fileName:
                    "KPMG/FR/FR%20300070%2020180824_Dataset_Example_NuoCanvas%20PREPARED.csv",
                  sizeBytes: 41179,
                  dateCreatedMillis: 1536290330641
                },
                {
                  fileName:
                    "KPMG/FR/FR%20300080%2020180824_Dataset_Example_NuoCanvas%20PREPARED.csv",
                  sizeBytes: 59563,
                  dateCreatedMillis: 1536290330298
                },
                {
                  fileName:
                    "KPMG/FR/FR%20300110%2020180824_Dataset_Example_NuoCanvas%20PREPARED.csv",
                  sizeBytes: 41759,
                  dateCreatedMillis: 1536290339608
                },
                {
                  fileName:
                    "KPMG/FR/FR%20300140%2020180824_Dataset_Example_NuoCanvas%20PREPARED.csv",
                  sizeBytes: 39468,
                  dateCreatedMillis: 1536290340763
                },
                {
                  fileName:
                    "KPMG/FR/FR%20301100%2020180824_Dataset_Example_NuoCanvas%20PREPARED.csv",
                  sizeBytes: 73409,
                  dateCreatedMillis: 1536290340883
                },
                {
                  fileName:
                    "KPMG/FR/FR%20302010%2020180824_Dataset_Example_NuoCanvas%20PREPARED.csv",
                  sizeBytes: 41957,
                  dateCreatedMillis: 1536290330189
                },
                {
                  fileName:
                    "KPMG/FR/FR%20302110%2020180824_Dataset_Example_NuoCanvas%20PREPARED.csv",
                  sizeBytes: 79137,
                  dateCreatedMillis: 1536290330369
                },
                {
                  fileName:
                    "KPMG/FR/FR%20310050%2020180824_Dataset_Example_NuoCanvas%20PREPARED.csv",
                  sizeBytes: 39483,
                  dateCreatedMillis: 1536290330274
                },
                {
                  fileName:
                    "KPMG/FR/FR%20310150%2020180824_Dataset_Example_NuoCanvas%20PREPARED.csv",
                  sizeBytes: 39363,
                  dateCreatedMillis: 1536290330041
                },
                {
                  fileName:
                    "KPMG/FR/FR%20311050%2020180824_Dataset_Example_NuoCanvas%20PREPARED.csv",
                  sizeBytes: 39213,
                  dateCreatedMillis: 1536290340879
                },
                {
                  fileName:
                    "KPMG/FR/FR%20311999%2020180824_Dataset_Example_NuoCanvas%20PREPARED.csv",
                  sizeBytes: 67889,
                  dateCreatedMillis: 1536290349313
                },
                {
                  fileName:
                    "KPMG/PT/PT%20300010%2020180824_Dataset_Example_NuoCanvas%20PREPARED.csv",
                  sizeBytes: 9114,
                  dateCreatedMillis: 1536290681660
                },
                {
                  fileName:
                    "KPMG/PT/PT%20300020%2020180824_Dataset_Example_NuoCanvas%20PREPARED.csv",
                  sizeBytes: 8844,
                  dateCreatedMillis: 1536290681771
                },
                {
                  fileName:
                    "KPMG/PT/PT%20300060%2020180824_Dataset_Example_NuoCanvas%20PREPARED.csv",
                  sizeBytes: 7153,
                  dateCreatedMillis: 1536290681723
                },
                {
                  fileName:
                    "KPMG/PT/PT%20300070%2020180824_Dataset_Example_NuoCanvas%20PREPARED.csv",
                  sizeBytes: 7290,
                  dateCreatedMillis: 1536290681669
                },
                {
                  fileName:
                    "KPMG/PT/PT%20300080%2020180824_Dataset_Example_NuoCanvas%20PREPARED.csv",
                  sizeBytes: 7345,
                  dateCreatedMillis: 1536290681769
                },
                {
                  fileName:
                    "KPMG/PT/PT%20300110%2020180824_Dataset_Example_NuoCanvas%20PREPARED.csv",
                  sizeBytes: 7345,
                  dateCreatedMillis: 1536290682145
                },
                {
                  fileName:
                    "KPMG/PT/PT%20301100%2020180824_Dataset_Example_NuoCanvas%20PREPARED.csv",
                  sizeBytes: 10631,
                  dateCreatedMillis: 1536290701448
                },
                {
                  fileName:
                    "KPMG/PT/PT%20301200%2020180824_Dataset_Example_NuoCanvas%20PREPARED.csv",
                  sizeBytes: 8322,
                  dateCreatedMillis: 1536290691340
                },
                {
                  fileName:
                    "KPMG/PT/PT%20302010%2020180824_Dataset_Example_NuoCanvas%20PREPARED.csv",
                  sizeBytes: 9849,
                  dateCreatedMillis: 1536290691761
                },
                {
                  fileName:
                    "KPMG/PT/PT%20302110%2020180824_Dataset_Example_NuoCanvas%20PREPARED.csv",
                  sizeBytes: 9776,
                  dateCreatedMillis: 1536290691833
                },
                {
                  fileName:
                    "KPMG/PT/PT%20310050%2020180824_Dataset_Example_NuoCanvas%20PREPARED.csv",
                  sizeBytes: 4173,
                  dateCreatedMillis: 1536290691829
                },
                {
                  fileName:
                    "KPMG/PT/PT%20310150%2020180824_Dataset_Example_NuoCanvas%20PREPARED.csv",
                  sizeBytes: 4112,
                  dateCreatedMillis: 1536290691760
                },
                {
                  fileName:
                    "KPMG/PT/PT%20311999%2020180824_Dataset_Example_NuoCanvas%20PREPARED.csv",
                  sizeBytes: 10716,
                  dateCreatedMillis: 1536290691813
                },
                {
                  fileName: "KPMG/Pilot/20181105_Mapping_GL.csv",
                  sizeBytes: 4302,
                  dateCreatedMillis: 1541442308849
                },
                {
                  fileName: "KPMG/Pilot/Account_Norway.csv",
                  sizeBytes: 1359114,
                  dateCreatedMillis: 1541390893726
                },
                {
                  fileName: "KPMG/Pilot/Account_Sweden.csv",
                  sizeBytes: 2933686,
                  dateCreatedMillis: 1541391216488
                },
                {
                  fileName: "KPMG/Pilot/Forex_Rate.csv",
                  sizeBytes: 3831,
                  dateCreatedMillis: 1541390877391
                },
                {
                  fileName: "KPMG/Pilot/General_Ledger_Finland.csv",
                  sizeBytes: 137242,
                  dateCreatedMillis: 1541390864900
                },
                {
                  fileName: "KPMG/Pilot/General_Ledger_Norway.csv",
                  sizeBytes: 1539675,
                  dateCreatedMillis: 1541391804560
                },
                {
                  fileName: "KPMG/Pilot/General_Ledger_Norway_2.csv",
                  sizeBytes: 1538071,
                  dateCreatedMillis: 1541718032036
                },
                {
                  fileName: "KPMG/Pilot/General_Ledger_Sweden.csv",
                  sizeBytes: 10371213,
                  dateCreatedMillis: 1541390884960
                },
                {
                  fileName: "KPMG/Pilot/General_Ledger_Sweden_2.csv",
                  sizeBytes: 10371201,
                  dateCreatedMillis: 1541718331419
                },
                {
                  fileName: "KPMG/Pilot/Master_Finland.csv",
                  sizeBytes: 12591,
                  dateCreatedMillis: 1541390867962
                },
                {
                  fileName: "KPMG/Pilot/Master_Norway.csv",
                  sizeBytes: 101223,
                  dateCreatedMillis: 1541390873215
                },
                {
                  fileName: "KPMG/Pilot/Master_Sweden.csv",
                  sizeBytes: 970930,
                  dateCreatedMillis: 1541390871987
                },
                {
                  fileName: "KPMG/Pilot/Normalization_Adjustment_CC.csv",
                  sizeBytes: 1675918,
                  dateCreatedMillis: 1541390866016
                },
                {
                  fileName: "NN/sales.csv",
                  sizeBytes: 1318126,
                  dateCreatedMillis: 1534257299394
                },
                {
                  fileName: "NN/sales_july_2015.csv",
                  sizeBytes: 8589,
                  dateCreatedMillis: 1534276871033
                },
                {
                  fileName: "NN/stores.csv",
                  sizeBytes: 1626,
                  dateCreatedMillis: 1533886952199
                },
                {
                  fileName: "ProfileImage/Latest",
                  sizeBytes: 22819,
                  dateCreatedMillis: 1530212340778
                }
              ]
            }
          };
          handleResponse(data);
        }
      });
    var handleResponse = function (data) {
      if (data.StatusCode && data.StatusCode === 200) {
        if (data.Content && data.Content.NuoEvaFiles) {
          activeCustomerFiles = data.Content.NuoEvaFiles;
          //Render the customerFilesTable
          renderCustomerFilesTable(parentPath);
        } else {
          $("#customerFilesTable").html(
            "I was not able to serve your request. Please contact support"
          );
          $("#customerFilesTable").show();
          //window.location.reload();
        }
      } else {
        $("#customerFilesTable").html(
          "I was not able to serve your request. Please contact support"
        );
        $("#customerFilesTable").show();
        //window.location.reload();
      }
    };
  } else {
    //Render the customerFilesTable
    renderCustomerFilesTable(parentPath);
  }
}

function GetFormattedDate(dateObject) {
  const monthNames = [
    "Jan",
    "Feb",
    "Mar",
    "Apr",
    "May",
    "Jun",
    "Jul",
    "Aug",
    "Sep",
    "Oct",
    "Nov",
    "Dec"
  ];
  var month = dateObject.getMonth();
  var day = dateObject.getDate();
  var year = dateObject.getFullYear();
  return (
    monthNames[month] +
    " " +
    day +
    " " +
    year +
    " " +
    dateObject.getHours() +
    ":" +
    dateObject.getMinutes() +
    ":" +
    dateObject.getSeconds()
  );
}

function renderCustomerFilesTable(parentPath) {
  var filesData = transformCustomerFiles(activeCustomerFiles, parentPath);
  var jexcelConfig = {
    data: [],
    colHeaders: [],
    // colWidths: [50, 400, 100, 150, 200],
    colWidths: ["5%", "48%", "12%", "12%", "20%"],
    colAlignments: ["left", "left", "right", "left", "left"],
    // csvHeaders: true,
    tableOverflow: true,
    allowManualInsertRow: "false",
    editable: "false",
    onselection: function (e) {
      var children = $("#fileExplorerGrid>.jexcel>tbody>.selected")[0].children;
      var isCheckboxSelected = children[1].childNodes[0].childNodes[0].checked;
      if (isCheckboxSelected) {
        children[1].childNodes[0].childNodes[0].checked = false;
        isCheckboxSelected = false;
      } else {
        children[1].childNodes[0].childNodes[0].checked = true;
        isCheckboxSelected = true;
      }
      for (var i = 0; i < children.length; i++) {
        if (isCheckboxSelected) {
          children[i].className += " highlight";
        } else {
          children[i].className = children[i].className.replaceAll(
            "highlight",
            ""
          );
        }
      }
    },
    columns: [
      { type: "text" },
      { type: "text", wordWrap: true },
      { type: "text" },
      { type: "text" },
      { type: "text" }
    ]
  };

  jexcelConfig.colHeaders.push(
    "<div class='allcheckbox-td custom-checkbox'>" +
    `<input class='all-select' type='checkbox' onclick='checkAllCheckbox(this)'/>` +
    "<span></span>" +
    "</div>"
  );
  jexcelConfig.colHeaders.push("<div data-breakpoints='xs'>File Name");
  jexcelConfig.colHeaders.push("<div data-breakpoints='xs'>Size");
  jexcelConfig.colHeaders.push("<div data-breakpoints='xs'>Type");
  jexcelConfig.colHeaders.push("<div data-breakpoints='xs'>Date Uploaded");

  var displayedLables = [];
  var rowCounter = 0;
  $(filesData).each(function (index, currentObject) {
    if (displayedLables.indexOf(currentObject.fileLabel) < 0) {
      var row = [];

      row.push(
        "<div class='dataGridRowCheckboxDiv custom-checkbox'>" +
        "<input id='dataGridRowCheckbox" +
        rowCounter +
        "' class='dataGridRowCheckbox' type='checkbox'/>" +
        "<span></span>" +
        "</div>"
      );

      if (currentObject.isDirectory) {
        row.push(
          "<div style='display: inline-block;'>" +
          "<img src='./public/addFolder.png' class='img-responsive is-folder'/>" +
          "</div><div style=' display: inline-block;'>" +
          currentObject.fileLabel +
          "</div>"
        );
      } else {
        row.push(currentObject.fileLabel);
      }
      row.push(
        "<div id='fileExplorerGrid_rowObject_" +
        rowCounter +
        "' rowObject='" +
        JSON.stringify(currentObject) +
        "'>" +
        currentObject.sizeLabel +
        "</div>"
      );
      rowCounter += 1;
      row.push(currentObject.fileType);
      row.push(GetFormattedDate(currentObject.dateCreated));

      jexcelConfig.data.push(row);
      displayedLables.push(currentObject.fileLabel);
    }
  });

  $("#fileExplorerGrid").jexcel(jexcelConfig);
  addDataGridCheckboxEvents();
  addFileExplorerEventHandlers();
  $("#customerFilePath").html("/");
}
function addDataGridCheckboxEvents() {
  $(".dataGridRowCheckbox").off("change");
  $(".dataGridRowCheckbox").on("change", function (e) {

    // e.preventDefault();
    // e.stopPropagation();
    var currEle = e.currentTarget;
    var parentDivId = $(currEle)
      .closest(".jexcel")
      .parent()[0].id;
    var rowId = $(currEle).closest("tr")[0].id;

    $("#" + parentDivId + ">table>tbody>tr").each(function (trIndex, tr) {
      var isChecked = $(tr).find("input")[0].checked;

      $(tr)
        .children()
        .each(function (tdIndex, td) {
          if (tdIndex > 0) {
            if (isChecked === true) {
              td.className += " highlight";
            } else {
              td.className = td.className.replaceAll("highlight", "");
            }
          }
        });
    });

    if (currEle.checked !== true) {
      $("#" + parentDivId + " .all-select")[0].checked = false;
    } else {
      var allChecked = true;
      $("#" + parentDivId + " .dataGridRowCheckbox").each(function (index, ele) {
        allChecked = allChecked && ele.checked;
      });
      $("#" + parentDivId + " .all-select")[0].checked = allChecked;
    }
  })
  $(".dataGridRowCheckbox").off("click");
  $(".dataGridRowCheckbox").on("click", function (e) {

    // e.preventDefault();
    // e.stopPropagation();
    var currEle = e.currentTarget;

    var currClickedRowIndex = parseInt(
      currEle.id.substring(currEle.className.length)
    );

    var divId = $(currEle).closest(".explorerGrid")[0].id;
    if (shiftKeyDown === false && ctrlKeyDown === false) {
      $("#" + divId + " .dataGridRowCheckbox").each(function (index, ele) {
        if (currEle.id !== ele.id) ele.checked = false;
      });
    } else if (shiftKeyDown === true && lastClickedRowIndex >= 0) {
      if (lastClickedRowIndex < currClickedRowIndex) {
        for (var i = lastClickedRowIndex; i < currClickedRowIndex; i++) {
          $("#" + divId + " #dataGridRowCheckbox" + i)[0].checked =
            currEle.checked;
        }
      } else {
        for (var i = currClickedRowIndex; i < lastClickedRowIndex; i++) {
          $("#" + divId + " #dataGridRowCheckbox" + i)[0].checked =
            currEle.checked;
        }
      }
    }

    lastClickedRowIndex = currClickedRowIndex;
    shiftKeyDown = false;
    ctrlKeyDown = false;
  })
}

//check/uncheck all checkox
function checkAllCheckbox(currEle) {
  var parentDivId = $(currEle)
    .closest(".jexcel")
    .parent()[0].id;
  $("#" + parentDivId + ">table>tbody>tr")
    .toArray()
    .map(function (ele) {
      return ele.id;
    })
    .forEach(function (rowId) {
      var childCells = $("#" + parentDivId + ">table>tbody>tr#" + rowId)[0]
        .childNodes;
      for (var i = 0; i < childCells.length; i++) {
        if (currEle.checked) {
          childCells[i].className += " highlight";
        } else {
          childCells[i].className = childCells[i].className.replaceAll(
            "highlight",
            ""
          );
        }
      }
    });

  if (currEle.checked) {
    $("#" + parentDivId + " .dataGridRowCheckbox").each(function (index, ele) {
      ele.checked = true;
    });
  } else {
    $("#" + parentDivId + " .dataGridRowCheckbox").each(function (index, ele) {
      ele.checked = false;
    });
  }
}

function transformCustomerFiles(activeCustomerFiles, parentPath) {
  var fileElements = [];

  activeCustomerFiles
    .map(function (fileObj) {
      var fileName = fileObj.fileName;
      var fileElement = {};

      if (
        !parentPath ||
        parentPath.length == 0 ||
        (fileName.indexOf(parentPath) >= 0 &&
          fileName.length > parentPath.length)
      ) {
        fileElement.fileName = fileName;
        fileElement.parentPath = parentPath;
        fileElement.author = fileObj.author;

        if (fileName.indexOf("/", parentPath.length) >= 0) {
          fileElement.fileLabel = decodeURI(
            fileName.substring(
              parentPath.length,
              fileName.indexOf("/", parentPath.length + 1)
            )
          );
          fileElement.filePath = fileName.substring(
            0,
            fileName.indexOf("/", parentPath.length + 1) + 1
          );
          fileElement.isDirectory = true;
          fileElement.fileType = "Folder";
        } else {
          fileElement.fileLabel = decodeURI(
            fileName.substring(parentPath.length)
          );
          fileElement.filePath = fileName;
          fileElement.isDirectory = false;
          fileElement.fileType = "File";
          if (fileElement.fileLabel.indexOf(".") > 0) {
            fileElement.isDirectory = false;
            fileElement.fileType =
              fileElement.fileLabel
                .substring(fileElement.fileLabel.lastIndexOf(".") + 1)
                .toUpperCase() + " File";
          }
        }
        fileElement.dateCreated = new Date(parseInt(fileObj.dateCreatedMillis));
        fileElement.dateModified = new Date(
          parseInt(fileObj.dateModifiedMillis)
        );
        fileElement.sizeBytes = parseInt(fileObj.sizeBytes);
        fileElement.sizeLabel = getSizeLabel(fileElement.sizeBytes);
        return fileElement;
      } else return fileElement;
    })
    .filter(function (fileElement) {
      return fileElement.fileLabel;
    })
    .sort(function (l, r) {
      if (l.dateModified > r.dateModified) return -1;
      if (l.dateModified < r.dateModified) return 1;
      return 0;
    })
    .forEach(function (fileElement) {
      var existingElement = fileElements.find(function (ele) {
        return ele.fileLabel === fileElement.fileLabel;
      });
      fileElements.push(fileElement);
      if (!existingElement) {
        fileElements.push(fileElement);
      } else {
        existingElement.sizeBytes += fileElement.sizeBytes;
        existingElement.sizeLabel = getSizeLabel(existingElement.sizeBytes);
      }
    });
  return fileElements;
}

function getSizeLabel(sizeBytes) {
  if (sizeBytes >= 1024 * 1024 * 1024 * 1024.0) {
    return (sizeBytes / 1024.0 / 1024.0 / 1024.0 / 1024.0).toFixed(2) + " TB";
  } else if (sizeBytes >= 1024 * 1024 * 1024.0) {
    return (sizeBytes / 1024.0 / 1024.0 / 1024.0).toFixed(2) + " GB";
  } else if (sizeBytes >= 1024 * 1024.0) {
    return (sizeBytes / 1024.0 / 1024.0).toFixed(2) + " MB";
  } else if (sizeBytes >= 1024.0) {
    return (sizeBytes / 1024.0).toFixed(2) + " KB";
  } else {
    return sizeBytes + " B";
  }
}

function getGridRowData(divId, rowIndex) {
  var rowObjectContent = $("#" + divId + "_rowObject_" + rowIndex).attr(
    "rowObject"
  );
  if (rowObjectContent && rowObjectContent.trim().length > 0) {
    return JSON.parse(rowObjectContent);
  }
  return null;
}

function getGridSelectedRows(divId) {
  var selectedRows = $("#" + divId + " input.dataGridRowCheckbox:checked")
    .map(function (index, selectedCheckbox) {
      return selectedCheckbox.closest("tr").id.split("-")[1];
    })
    .map(function (index, rowIndex) {
      return getGridRowData(divId, rowIndex);
    });
  return selectedRows.toArray();
}

function addFileExplorerEventHandlers() {
  $("#customerFileUpButton").off("click");
  $("#customerFileUpButton").on("click", function (e) {
    e.preventDefault();
    var rowData = getGridRowData("fileExplorerGrid", 0);
    if (rowData) {
      var grandParentPath = "";
      if (rowData.isDirectory) {
        grandParentPath = rowData.parentPath
          .substring(0, rowData.parentPath.length - 1)
          .substring(
            0,
            rowData.parentPath
              .substring(0, rowData.parentPath.lastIndexOf("/"))
              .lastIndexOf("/") + 1
          );
      } else {
        grandParentPath = rowData.parentPath.substring(
          0,
          rowData.parentPath
            .substring(0, rowData.parentPath.lastIndexOf("/"))
            .lastIndexOf("/") + 1
        );
      }
      loadCustomerFilesTable(grandParentPath);

      if (grandParentPath.length == 0) $("#customerFilePath").html("/");
      else $("#customerFilePath").html(decodeURI(grandParentPath));
    }
  });

  $("#customerFileRefreshButton").off("click");
  $("#customerFileRefreshButton").on("click", function (e) {
    e.preventDefault();
    loadCustomerFilesTable("", true);
  });

  var children = $("#fileExplorerGrid>.jexcel>tbody>tr");
  for (var i = 0; i < children.length; i++) {
    $("#fileExplorerGrid>.jexcel>tbody>tr#" + children[i].id).off("dblclick");
    $("#fileExplorerGrid>.jexcel>tbody>tr#" + children[i].id).on(
      "dblclick",
      function (ele) {
        var rowIndex = ele.target.parentNode.id.split("-")[1];
        var rowData = getGridRowData("fileExplorerGrid", rowIndex);
        if (rowData) {
          if (rowData.isDirectory) {
            loadCustomerFilesTable(rowData.filePath);
            $("#customerFilePath").html(decodeURI(rowData.filePath));

            if (rowData.filePath.length == 0) $("#customerFilePath").html("/");
          } else {
            var fileToBeDownloaded = encodeURI(rowData.fileLabel);

            if (!fileToBeDownloaded.endsWith(systemFileLabel)) {
              var parentPath = rowData.parentPath;
              if (parentPath && parentPath.length > 0 && parentPath !== "/") {
                fileToBeDownloaded = parentPath + fileToBeDownloaded;
              }
              fileToBeDownloaded = fileToBeDownloaded;
              directCall("rt83", "NuoFileContent", {
                FileName: fileToBeDownloaded
              })
                .done(function (data) {
                  handleResponse(data);
                })
                .fail(function () {
                  if (sessionId === "X") {
                    var data = {
                      StatusCode: 200,
                      Status: "OK",
                      Content: "Successfully deleted columns."
                    };
                    handleResponse(data);
                  }
                });
              var handleResponse = function (data) {
                if (data.StatusCode && data.StatusCode === 200) {
                  var downloadEle = document.getElementById("downloadTag");
                  downloadEle.setAttribute("href", data.Content);
                  downloadEle.click();
                } else {
                  //window.location.reload();
                }
              };
            }
          }
        }
      }
    );
  }
}

//Customer Files block END

//Progress Monitor block START

function evaProgressButtonHandler() {
  loadProgressMonitorWindow(activeUsername);
}

function loadProgressMonitorWindow(activeUsername) {
  var screenWidth = $(window).width();
  var screenHeight = $(window).height();

  var windowWidth = 800;
  var windowHeight = 500;

  $("#progressMonitorWindow").jqxWindow({
    width: windowWidth,
    height: windowHeight,
    isModal: true,
    resizable: true,
    autoOpen: false,
    // cancelButton: $("#storageEntitiesCloseButton"),
    position: { x: screenWidth / 2 - 300, y: 100 },
    theme: jqWidgetThemeName
  });
  $("#progressMonitorWindow").jqxWindow("setTitle", "Analysis Progress");
  loadProgressMonitorTable(true);
  $("#progressMonitorWindow").jqxWindow("open");
}

function loadProgressMonitorTable(shouldRefresh) {
  if (
    shouldRefresh ||
    !activeProgressMonitors ||
    activeProgressMonitors.length == 0
  ) {
    var promise = null;

    if (shouldRefresh) {
      promise = directCall("rt113");
    } else {
      promise = directCall("rt113");
    }

    promise
      .done(function (data) {
        handleResponse(data);
      })
      .fail(function () {
        if (sessionId === "X") {
          var data = {
            StatusCode: 200,
            Status: "OK",
            Content: "Successfully deleted columns."
          };
          handleResponse(data);
        }
      });
    var handleResponse = function (data) {
      if (data.StatusCode && data.StatusCode === 200) {
        console.log("Get Progress status= " + data.Status);

        if (data.Content && data.Content.NuoQuestionMonitorList) {
          activeProgressMonitors = data.Content.NuoQuestionMonitorList;

          //Render the Storage Explorer
          renderProgressMonitorTable();
        } else {
          $("#progressMonitorTable").html(
            "I was not able to serve your request. Please contact support"
          );
          $("#progressMonitorTable").show();
          //window.location.reload();
        }
      } else {
        $("#progressMonitorTable").html(
          "I was not able to serve your request. Please contact support"
        );
        $("#progressMonitorTable").show();
        //window.location.reload();
      }
    };
  } else {
    //Render the progressMonitorTable
    renderProgressMonitorTable();
  }
}

function renderProgressMonitorTable() {
  var gridColumns = [];
  if (activeProgressMonitors && activeProgressMonitors.length > 0) {
    var gridColumns = [
      { text: "Alias", dataField: "QuestionAlias", width: "20%", height: 20 },
      { text: "Message", dataField: "QuestionText", width: "20%", height: 20 },
      {
        text: "Progress",
        dataField: "Progress",
        width: "20%",
        height: 20,
        cellsrenderer: function (row, colum, value) {
          var cell = '<div style="margin-top:5px;">';
          cell +=
            '<div style="background: #058dc7; float: left; width: ' +
            value +
            'px; height: 16px;"></div>';
          cell +=
            '<div style="margin-left: 5px; float: left;">' +
            parseInt(value).toString() +
            "%" +
            "</div>";
          cell += "</div>";
          return cell;
        }
      },
      {
        text: "StartTime",
        dataField: "StartTimeDate",
        width: "20%",
        height: 20
      },
      { text: "Duration", dataField: "Duration", width: "20%", height: 20 }
    ];

    var gridData = activeProgressMonitors.map(function (activeProgressMonitor) {
      if (
        activeProgressMonitor.TotalSteps &&
        activeProgressMonitor.TotalSteps > 0
      ) {
        activeProgressMonitor.Progress =
          (activeProgressMonitor.StepsCompleted /
            activeProgressMonitor.TotalSteps) *
          100.0;
      } else {
        activeProgressMonitor.Progress = 0.0;
      }

      if (activeProgressMonitor.StartTimeMillis) {
        activeProgressMonitor.StartTimeDate = new Date(
          activeProgressMonitor.StartTimeMillis
        );
        // var startDate = new Date(activeProgressMonitor.StartTimeMillis).toString()
        // activeProgressMonitor.StartTimeDate =
        // startDate.substring(0 ,startDate.indexOf( "(")-1)
      } else {
        activeProgressMonitor.StartTimeDate = "N/A";
      }

      if (
        activeProgressMonitor.StartTimeMillis &&
        activeProgressMonitor.EndTimeMillis
      ) {
        if (activeProgressMonitor.Progress < 100.0) {
          activeProgressMonitor.Duration = getReadableDuration(
            Date.now() - activeProgressMonitor.StartTimeMillis
          );
        } else {
          activeProgressMonitor.Duration = getReadableDuration(
            activeProgressMonitor.EndTimeMillis -
            activeProgressMonitor.StartTimeMillis
          );
        }
      } else {
        activeProgressMonitor.Progress = "N/A";
      }
      return activeProgressMonitor;
    });
    renderJqxGrid("#progressMonitorTable", gridData, gridColumns);
  }
  $("#progressMonitorTable").show();
}

function addProgressMonitorEventHandlers() {
  $("#progressMonitorRefreshButton").on("click", function (e) {
    e.preventDefault();
    loadProgressMonitorTable(true);
  });
}

//Progress Monitor block END

//Storage Explorer block START

function deleteTableSelectionHandler() {
  var selectedRows = getGridSelectedRows("storageExplorerGrid");

  var tablesToBeDeleted = selectedRows

    .filter(function (rowData) {
      return (
        !rowData.FieldName &&
        rowData.EntityName &&
        rowData.EntityName.length > 0
      );
    })
    .map(function (rowData) {
      var tableToBeDeleted = {
        DatasetName: "",
        EntityName: rowData.EntityName
      };
      return tableToBeDeleted;
    });

  $("#ajaxLoader").addClass("is-active");

  directCall("rt109", "NuoEntities", tablesToBeDeleted)
    .done(function (data) {
      handleResponse(data);
    })
    .fail(function () {
      if (sessionId === "X") {
        var data = {
          StatusCode: 200,
          Status: "OK",
          Content: "Successfully deleted columns."
        };
        handleResponse(data);
      }
    });
  var handleResponse = function (data) {
    if (data.StatusCode && data.StatusCode === 200) {
      // console.log("Delete table status= " + data.Content);
      nuoTables = nuoTables.filter(function (ele) {
        return (
          tablesToBeDeleted.filter(function (t) {
            return t.EntityName === ele.EntityName;
          }).length == 0
        );
      });
      loadStorageExplorerTable();
      $("#ajaxLoader").removeClass("is-active");
    } else {
      //window.location.reload();
    }
  };
}

function duplicateTableSelectionHandler() {
  var selectedRows = getGridSelectedRows("storageExplorerGrid");
  if (
    confirm(
      "You have selected " +
      selectedRows.length +
      " tables. Are you sure you want to duplicate them all?"
    )
  ) {
    var tablesToBeDuplicated = selectedRows
      // .map(function (index) {
      // 	return getGridRowData('storageExplorerGrid', index);
      // })
      .filter(function (rowData) {
        return (
          !rowData.FieldName &&
          rowData.EntityName &&
          rowData.EntityName.length > 0
        );
      })
      .map(function (rowData) {
        var tableToBeDeleted = {
          DatasetName: "",
          EntityName: rowData.EntityName
        };
        return tableToBeDeleted;
      });

    $("#ajaxLoader").addClass("is-active");

    directCall("rt211", "NuoEntities", tablesToBeDuplicated)
      .done(function (data) {
        handleResponse(data);
      })
      .fail(function () {
        if (sessionId === "X") {
          var data = {
            StatusCode: 200,
            Status: "OK",
            Content: "Successfully deleted columns."
          };
          handleResponse(data);
        }
      });
    var handleResponse = function (data) {
      if (data.StatusCode && data.StatusCode === 200) {
        // console.log("Duplicate table status= " + data.Content);
        // nuoTables =
        // 	nuoTables
        // 		.filter(function (ele) {
        // 			return tablesToBeDuplicated.filter(function (t) { return t.EntityName === ele.EntityName }).length == 0;
        // 		});
        loadStorageExplorerTable();
        $("#ajaxLoader").removeClass("is-active");
      } else {
        //window.location.reload();
      }
    };
    // $('#storageExplorerTable').jqxGrid('clearselection');
  }
}

function evaTablesButtonHandler() {
  loadStorageExplorerWindow(activeUsername);
}

function loadStorageExplorerWindow(activeUsername) {
  // var screenWidth = $(window).width();
  // var screenHeight = $(window).height();

  // var windowWidth = 800;
  // var windowHeight = 500;

  // $("#storageExplorerWindow").jqxWindow(
  // 	{
  // 		width: windowWidth,
  // 		height: windowHeight,
  // 		isModal: true,
  // 		resizable: true,
  // 		autoOpen: false,
  // 		position: { x: (screenWidth / 2) - 300, y: 100 },
  // 		theme: jqWidgetThemeName
  // 	}
  // );
  $("#storageExplorerWindow").fadeIn();
  lastClickedRowIndex = -1;
  $("#closeStorageExplorerWindow").off("click");
  $("#closeStorageExplorerWindow").on("click", function (e) {
    e.preventDefault();
    $("#storageExplorerWindow").fadeOut();
    lastClickedRowIndex = -1;
  });

  // $("#storageExplorerWindow").jqxWindow("setTitle", "Tables");
  loadStorageExplorerTable();
  // $("#storageExplorerWindow").jqxWindow("open");
}

function loadStorageExplorerTable(tableName, shouldRefresh) {
  if (shouldRefresh || !nuoTables || nuoTables.length == 0) {
    $("#ajaxLoader").addClass("is-active");
    refreshNuoTables(function () {
      renderStorageExplorerTable(tableName);
      $("#ajaxLoader").removeClass("is-active");
    });
  } else {
    //Render the storageExplorerTable
    $("#ajaxLoader").addClass("is-active");
    renderStorageExplorerTable(tableName);
    $("#ajaxLoader").removeClass("is-active");
  }
}

function refreshNuoTables(callbackTask) {
  directCall("rt107")
    .done(function (data) {
      handleResponse(data);
    })
    .fail(function () {
      if (sessionId === "X") {
        var data = {
          StatusCode: 200,
          Status: "OK",
          Content: {
            NuoEvaTables: [
              {
                DatasetName: "tenant1___default",
                EntityName: "FX_Rate",
                Description: null,
                CreationTime: "1541720415218",
                LastModifiedTime: "1541720531548",
                SizeBytes: "12474",
                SizeRows: "378",
                Fields: [
                  {
                    DatasetName: "tenant1___default",
                    EntityName: "FX_Rate",
                    FieldName: "Country",
                    DataType: "STRING"
                  },
                  {
                    DatasetName: "tenant1___default",
                    EntityName: "FX_Rate",
                    FieldName: "Currency",
                    DataType: "STRING"
                  },
                  {
                    DatasetName: "tenant1___default",
                    EntityName: "FX_Rate",
                    FieldName: "FX_Value",
                    DataType: "FLOAT"
                  },
                  {
                    DatasetName: "tenant1___default",
                    EntityName: "FX_Rate",
                    FieldName: "FX_Date",
                    DataType: "DATE"
                  }
                ]
              },
              {
                DatasetName: "tenant1___default",
                EntityName: "GL_2016",
                Description: null,
                CreationTime: "1541719497742",
                LastModifiedTime: "1541719497742",
                SizeBytes: "228792",
                SizeRows: "742",
                Fields: [
                  {
                    DatasetName: "tenant1___default",
                    EntityName: "GL_2016",
                    FieldName: "Country",
                    DataType: "STRING"
                  },
                  {
                    DatasetName: "tenant1___default",
                    EntityName: "GL_2016",
                    FieldName: "KPMG_Code",
                    DataType: "STRING"
                  },
                  {
                    DatasetName: "tenant1___default",
                    EntityName: "GL_2016",
                    FieldName: "Level_0_GL_code",
                    DataType: "INTEGER"
                  },
                  {
                    DatasetName: "tenant1___default",
                    EntityName: "GL_2016",
                    FieldName: "DATE_201312",
                    DataType: "FLOAT"
                  },
                  {
                    DatasetName: "tenant1___default",
                    EntityName: "GL_2016",
                    FieldName: "DATE_201401",
                    DataType: "FLOAT"
                  },
                  {
                    DatasetName: "tenant1___default",
                    EntityName: "GL_2016",
                    FieldName: "DATE_201402",
                    DataType: "FLOAT"
                  },
                  {
                    DatasetName: "tenant1___default",
                    EntityName: "GL_2016",
                    FieldName: "DATE_201403",
                    DataType: "FLOAT"
                  },
                  {
                    DatasetName: "tenant1___default",
                    EntityName: "GL_2016",
                    FieldName: "DATE_201404",
                    DataType: "FLOAT"
                  },
                  {
                    DatasetName: "tenant1___default",
                    EntityName: "GL_2016",
                    FieldName: "DATE_201405",
                    DataType: "FLOAT"
                  },
                  {
                    DatasetName: "tenant1___default",
                    EntityName: "GL_2016",
                    FieldName: "DATE_201406",
                    DataType: "FLOAT"
                  },
                  {
                    DatasetName: "tenant1___default",
                    EntityName: "GL_2016",
                    FieldName: "DATE_201407",
                    DataType: "FLOAT"
                  },
                  {
                    DatasetName: "tenant1___default",
                    EntityName: "GL_2016",
                    FieldName: "DATE_201408",
                    DataType: "FLOAT"
                  },
                  {
                    DatasetName: "tenant1___default",
                    EntityName: "GL_2016",
                    FieldName: "DATE_201409",
                    DataType: "FLOAT"
                  },
                  {
                    DatasetName: "tenant1___default",
                    EntityName: "GL_2016",
                    FieldName: "DATE_201410",
                    DataType: "FLOAT"
                  },
                  {
                    DatasetName: "tenant1___default",
                    EntityName: "GL_2016",
                    FieldName: "DATE_201411",
                    DataType: "FLOAT"
                  },
                  {
                    DatasetName: "tenant1___default",
                    EntityName: "GL_2016",
                    FieldName: "DATE_201412",
                    DataType: "FLOAT"
                  },
                  {
                    DatasetName: "tenant1___default",
                    EntityName: "GL_2016",
                    FieldName: "DATE_201501",
                    DataType: "FLOAT"
                  },
                  {
                    DatasetName: "tenant1___default",
                    EntityName: "GL_2016",
                    FieldName: "DATE_201502",
                    DataType: "FLOAT"
                  },
                  {
                    DatasetName: "tenant1___default",
                    EntityName: "GL_2016",
                    FieldName: "DATE_201503",
                    DataType: "FLOAT"
                  },
                  {
                    DatasetName: "tenant1___default",
                    EntityName: "GL_2016",
                    FieldName: "DATE_201504",
                    DataType: "FLOAT"
                  },
                  {
                    DatasetName: "tenant1___default",
                    EntityName: "GL_2016",
                    FieldName: "DATE_201505",
                    DataType: "FLOAT"
                  },
                  {
                    DatasetName: "tenant1___default",
                    EntityName: "GL_2016",
                    FieldName: "DATE_201506",
                    DataType: "FLOAT"
                  },
                  {
                    DatasetName: "tenant1___default",
                    EntityName: "GL_2016",
                    FieldName: "DATE_201507",
                    DataType: "FLOAT"
                  },
                  {
                    DatasetName: "tenant1___default",
                    EntityName: "GL_2016",
                    FieldName: "DATE_201508",
                    DataType: "FLOAT"
                  },
                  {
                    DatasetName: "tenant1___default",
                    EntityName: "GL_2016",
                    FieldName: "DATE_201509",
                    DataType: "FLOAT"
                  },
                  {
                    DatasetName: "tenant1___default",
                    EntityName: "GL_2016",
                    FieldName: "DATE_201510",
                    DataType: "FLOAT"
                  },
                  {
                    DatasetName: "tenant1___default",
                    EntityName: "GL_2016",
                    FieldName: "DATE_201511",
                    DataType: "FLOAT"
                  },
                  {
                    DatasetName: "tenant1___default",
                    EntityName: "GL_2016",
                    FieldName: "DATE_201512",
                    DataType: "FLOAT"
                  },
                  {
                    DatasetName: "tenant1___default",
                    EntityName: "GL_2016",
                    FieldName: "DATE_201601",
                    DataType: "FLOAT"
                  },
                  {
                    DatasetName: "tenant1___default",
                    EntityName: "GL_2016",
                    FieldName: "DATE_201602",
                    DataType: "FLOAT"
                  },
                  {
                    DatasetName: "tenant1___default",
                    EntityName: "GL_2016",
                    FieldName: "DATE_201603",
                    DataType: "FLOAT"
                  },
                  {
                    DatasetName: "tenant1___default",
                    EntityName: "GL_2016",
                    FieldName: "DATE_201604",
                    DataType: "FLOAT"
                  },
                  {
                    DatasetName: "tenant1___default",
                    EntityName: "GL_2016",
                    FieldName: "DATE_201605",
                    DataType: "FLOAT"
                  },
                  {
                    DatasetName: "tenant1___default",
                    EntityName: "GL_2016",
                    FieldName: "DATE_201606",
                    DataType: "FLOAT"
                  },
                  {
                    DatasetName: "tenant1___default",
                    EntityName: "GL_2016",
                    FieldName: "DATE_201607",
                    DataType: "FLOAT"
                  },
                  {
                    DatasetName: "tenant1___default",
                    EntityName: "GL_2016",
                    FieldName: "DATE_201608",
                    DataType: "FLOAT"
                  },
                  {
                    DatasetName: "tenant1___default",
                    EntityName: "GL_2016",
                    FieldName: "DATE_201609",
                    DataType: "FLOAT"
                  },
                  {
                    DatasetName: "tenant1___default",
                    EntityName: "GL_2016",
                    FieldName: "DATE_201610",
                    DataType: "FLOAT"
                  },
                  {
                    DatasetName: "tenant1___default",
                    EntityName: "GL_2016",
                    FieldName: "DATE_201611",
                    DataType: "FLOAT"
                  },
                  {
                    DatasetName: "tenant1___default",
                    EntityName: "GL_2016",
                    FieldName: "DATE_201612",
                    DataType: "FLOAT"
                  }
                ]
              },
              {
                DatasetName: "tenant1___default",
                EntityName: "GL_2016_copy",
                Description: null,
                CreationTime: "1541719539626",
                LastModifiedTime: "1541719983158",
                SizeBytes: "982664",
                SizeRows: "27454",
                Fields: [
                  {
                    DatasetName: "tenant1___default",
                    EntityName: "GL_2016_copy",
                    FieldName: "Country",
                    DataType: "STRING"
                  },
                  {
                    DatasetName: "tenant1___default",
                    EntityName: "GL_2016_copy",
                    FieldName: "KPMG_Code",
                    DataType: "STRING"
                  },
                  {
                    DatasetName: "tenant1___default",
                    EntityName: "GL_2016_copy",
                    FieldName: "Level_0_GL_code",
                    DataType: "INTEGER"
                  },
                  {
                    DatasetName: "tenant1___default",
                    EntityName: "GL_2016_copy",
                    FieldName: "GL_2016_Value",
                    DataType: "FLOAT"
                  },
                  {
                    DatasetName: "tenant1___default",
                    EntityName: "GL_2016_copy",
                    FieldName: "GL_2016_Date",
                    DataType: "DATE"
                  }
                ]
              },
              {
                DatasetName: "tenant1___default",
                EntityName: "Reference_Detail",
                Description: null,
                CreationTime: "1541389551607",
                LastModifiedTime: "1541390594533",
                SizeBytes: "1204534",
                SizeRows: "9104",
                Fields: [
                  {
                    DatasetName: "tenant1___default",
                    EntityName: "Reference_Detail",
                    FieldName: "KPMG_Code",
                    DataType: "STRING"
                  },
                  {
                    DatasetName: "tenant1___default",
                    EntityName: "Reference_Detail",
                    FieldName: "Country",
                    DataType: "STRING"
                  },
                  {
                    DatasetName: "tenant1___default",
                    EntityName: "Reference_Detail",
                    FieldName: "City",
                    DataType: "STRING"
                  },
                  {
                    DatasetName: "tenant1___default",
                    EntityName: "Reference_Detail",
                    FieldName: "Strategy_City_label",
                    DataType: "STRING"
                  },
                  {
                    DatasetName: "tenant1___default",
                    EntityName: "Reference_Detail",
                    FieldName: "Strategy_Purpose",
                    DataType: "STRING"
                  },
                  {
                    DatasetName: "tenant1___default",
                    EntityName: "Reference_Detail",
                    FieldName: "Contract_Type",
                    DataType: "STRING"
                  },
                  {
                    DatasetName: "tenant1___default",
                    EntityName: "Reference_Detail",
                    FieldName: "Business_Model",
                    DataType: "STRING"
                  },
                  {
                    DatasetName: "tenant1___default",
                    EntityName: "Reference_Detail",
                    FieldName: "Reporting_Structure",
                    DataType: "STRING"
                  },
                  {
                    DatasetName: "tenant1___default",
                    EntityName: "Reference_Detail",
                    FieldName: "Start_month_parking_facility",
                    DataType: "STRING"
                  },
                  {
                    DatasetName: "tenant1___default",
                    EntityName: "Reference_Detail",
                    FieldName: "Start_year_parking_facility",
                    DataType: "INTEGER"
                  },
                  {
                    DatasetName: "tenant1___default",
                    EntityName: "Reference_Detail",
                    FieldName: "End_year_contract",
                    DataType: "INTEGER"
                  },
                  {
                    DatasetName: "tenant1___default",
                    EntityName: "Reference_Detail",
                    FieldName: "Size_num_of_contract_spaces",
                    DataType: "INTEGER"
                  }
                ]
              },
              {
                DatasetName: "tenant1___default",
                EntityName: "Result_Table",
                Description: null,
                CreationTime: "1541720962855",
                LastModifiedTime: "1541737739079",
                SizeBytes: "192160",
                SizeRows: "5350",
                Fields: [
                  {
                    DatasetName: "tenant1___default",
                    EntityName: "Result_Table",
                    FieldName: "RANK_KPMG_Code_Country_GL_2016_Value",
                    DataType: "INTEGER"
                  },
                  {
                    DatasetName: "tenant1___default",
                    EntityName: "Result_Table",
                    FieldName: "KPMG_Code",
                    DataType: "STRING"
                  },
                  {
                    DatasetName: "tenant1___default",
                    EntityName: "Result_Table",
                    FieldName: "Country",
                    DataType: "STRING"
                  },
                  {
                    DatasetName: "tenant1___default",
                    EntityName: "Result_Table",
                    FieldName: "GL_2016_Value",
                    DataType: "FLOAT"
                  },
                  {
                    DatasetName: "tenant1___default",
                    EntityName: "Result_Table",
                    FieldName: "FX_Date",
                    DataType: "DATE"
                  }
                ]
              },
              {
                DatasetName: "tenant1___default",
                EntityName: "Stores",
                Description: null,
                CreationTime: "1533887382338",
                LastModifiedTime: "1540156209084",
                SizeBytes: "2494",
                SizeRows: "49",
                Fields: [
                  {
                    DatasetName: "tenant1___default",
                    EntityName: "Stores",
                    FieldName: "store_id",
                    DataType: "INTEGER"
                  },
                  {
                    DatasetName: "tenant1___default",
                    EntityName: "Stores",
                    FieldName: "store_type",
                    DataType: "STRING"
                  },
                  {
                    DatasetName: "tenant1___default",
                    EntityName: "Stores",
                    FieldName: "assortment_type",
                    DataType: "STRING"
                  },
                  {
                    DatasetName: "tenant1___default",
                    EntityName: "Stores",
                    FieldName: "competition_distance",
                    DataType: "FLOAT"
                  },
                  {
                    DatasetName: "tenant1___default",
                    EntityName: "Stores",
                    FieldName: "promo2",
                    DataType: "INTEGER"
                  },
                  {
                    DatasetName: "tenant1___default",
                    EntityName: "Stores",
                    FieldName: "promo2_since_week",
                    DataType: "FLOAT"
                  },
                  {
                    DatasetName: "tenant1___default",
                    EntityName: "Stores",
                    FieldName: "Promo2_since_year",
                    DataType: "FLOAT"
                  },
                  {
                    DatasetName: "tenant1___default",
                    EntityName: "Stores",
                    FieldName: "competition_open_since_month",
                    DataType: "INTEGER"
                  },
                  {
                    DatasetName: "tenant1___default",
                    EntityName: "Stores",
                    FieldName: "competition_open_since_year",
                    DataType: "INTEGER"
                  }
                ]
              },
              {
                DatasetName: "tenant1___default",
                EntityName: "Transaction",
                Description: null,
                CreationTime: "1529345015486",
                LastModifiedTime: "1529345015486",
                SizeBytes: "439990592",
                SizeRows: "10000000",
                Fields: [
                  {
                    DatasetName: "tenant1___default",
                    EntityName: "Transaction",
                    FieldName: "Transaction_Number",
                    DataType: "INTEGER"
                  },
                  {
                    DatasetName: "tenant1___default",
                    EntityName: "Transaction",
                    FieldName: "Reference_Number",
                    DataType: "INTEGER"
                  },
                  {
                    DatasetName: "tenant1___default",
                    EntityName: "Transaction",
                    FieldName: "Is_Debit",
                    DataType: "BOOLEAN"
                  },
                  {
                    DatasetName: "tenant1___default",
                    EntityName: "Transaction",
                    FieldName: "Amount",
                    DataType: "FLOAT"
                  },
                  {
                    DatasetName: "tenant1___default",
                    EntityName: "Transaction",
                    FieldName: "Transaction_Date",
                    DataType: "TIMESTAMP"
                  },
                  {
                    DatasetName: "tenant1___default",
                    EntityName: "Transaction",
                    FieldName: "Type",
                    DataType: "STRING"
                  }
                ]
              },
              {
                DatasetName: "tenant1___default",
                EntityName: "Y",
                Description: null,
                CreationTime: "1541609028857",
                LastModifiedTime: "1541741264551",
                SizeBytes: "96920",
                SizeRows: "3462",
                Fields: [
                  {
                    DatasetName: "tenant1___default",
                    EntityName: "Y",
                    FieldName: "Country",
                    DataType: "STRING"
                  },
                  {
                    DatasetName: "tenant1___default",
                    EntityName: "Y",
                    FieldName: "GL_2016_Value",
                    DataType: "FLOAT"
                  },
                  {
                    DatasetName: "tenant1___default",
                    EntityName: "Y",
                    FieldName: "GL_2016_Value_1",
                    DataType: "FLOAT"
                  },
                  {
                    DatasetName: "tenant1___default",
                    EntityName: "Y",
                    FieldName: "FX_Value",
                    DataType: "FLOAT"
                  }
                ]
              },
              {
                DatasetName: "tenant1___default",
                EntityName: "src",
                Description: null,
                CreationTime: "1540211246487",
                LastModifiedTime: "1541386348834",
                SizeBytes: "193",
                SizeRows: "4",
                Fields: [
                  {
                    DatasetName: "tenant1___default",
                    EntityName: "src",
                    FieldName: "Q1",
                    DataType: "INTEGER"
                  },
                  {
                    DatasetName: "tenant1___default",
                    EntityName: "src",
                    FieldName: "Q3",
                    DataType: "INTEGER"
                  },
                  {
                    DatasetName: "tenant1___default",
                    EntityName: "src",
                    FieldName: "Name",
                    DataType: "STRING"
                  },
                  {
                    DatasetName: "tenant1___default",
                    EntityName: "src",
                    FieldName: "Int",
                    DataType: "INTEGER"
                  },
                  {
                    DatasetName: "tenant1___default",
                    EntityName: "src",
                    FieldName: "Date",
                    DataType: "DATE"
                  },
                  {
                    DatasetName: "tenant1___default",
                    EntityName: "src",
                    FieldName: "String",
                    DataType: "STRING"
                  }
                ]
              },
              {
                DatasetName: "tenant1___default",
                EntityName: "src_copy",
                Description: null,
                CreationTime: "1541770801140",
                LastModifiedTime: "1541770849560",
                SizeBytes: "161",
                SizeRows: "4",
                Fields: [
                  {
                    DatasetName: "tenant1___default",
                    EntityName: "src_copy",
                    FieldName: "Q3",
                    DataType: "INTEGER"
                  },
                  {
                    DatasetName: "tenant1___default",
                    EntityName: "src_copy",
                    FieldName: "Name",
                    DataType: "STRING"
                  },
                  {
                    DatasetName: "tenant1___default",
                    EntityName: "src_copy",
                    FieldName: "Int",
                    DataType: "INTEGER"
                  },
                  {
                    DatasetName: "tenant1___default",
                    EntityName: "src_copy",
                    FieldName: "Date",
                    DataType: "DATE"
                  },
                  {
                    DatasetName: "tenant1___default",
                    EntityName: "src_copy",
                    FieldName: "String",
                    DataType: "STRING"
                  }
                ]
              }
            ]
          }
        };
        handleResponse(data);
      }
    });
  var handleResponse = function (data) {
    // var data = {};
    // data.StatusCode = 200;
    // data.Content = { "NuoEvaTables": [{ "DatasetName": "tenant1___default", "EntityName": "Account", "Description": null, "CreationTime": "1529406415009", "LastModifiedTime": "1541392001742", "SizeBytes": "3749113", "SizeRows": "22617", "Fields": [{ "DatasetName": "tenant1___default", "EntityName": "Account", "FieldName": "HFM", "DataType": "INTEGER" }, { "DatasetName": "tenant1___default", "EntityName": "Account", "FieldName": "Name", "DataType": "STRING" }, { "DatasetName": "tenant1___default", "EntityName": "Account", "FieldName": "Account", "DataType": "INTEGER" }, { "DatasetName": "tenant1___default", "EntityName": "Account", "FieldName": "Account_Name", "DataType": "STRING" }, { "DatasetName": "tenant1___default", "EntityName": "Account", "FieldName": "KPMG_Code", "DataType": "STRING" }, { "DatasetName": "tenant1___default", "EntityName": "Account", "FieldName": "DATE_201701", "DataType": "FLOAT" }, { "DatasetName": "tenant1___default", "EntityName": "Account", "FieldName": "DATE_201702", "DataType": "FLOAT" }, { "DatasetName": "tenant1___default", "EntityName": "Account", "FieldName": "DATE_201703", "DataType": "FLOAT" }, { "DatasetName": "tenant1___default", "EntityName": "Account", "FieldName": "DATE_201704", "DataType": "FLOAT" }, { "DatasetName": "tenant1___default", "EntityName": "Account", "FieldName": "DATE_201705", "DataType": "FLOAT" }, { "DatasetName": "tenant1___default", "EntityName": "Account", "FieldName": "DATE_201706", "DataType": "FLOAT" }, { "DatasetName": "tenant1___default", "EntityName": "Account", "FieldName": "DATE_201707", "DataType": "FLOAT" }, { "DatasetName": "tenant1___default", "EntityName": "Account", "FieldName": "DATE_201708", "DataType": "FLOAT" }, { "DatasetName": "tenant1___default", "EntityName": "Account", "FieldName": "DATE_201709", "DataType": "FLOAT" }, { "DatasetName": "tenant1___default", "EntityName": "Account", "FieldName": "DATE_201710", "DataType": "FLOAT" }, { "DatasetName": "tenant1___default", "EntityName": "Account", "FieldName": "DATE_201711", "DataType": "FLOAT" }, { "DatasetName": "tenant1___default", "EntityName": "Account", "FieldName": "DATE_201712", "DataType": "FLOAT" }] }, { "DatasetName": "tenant1___default", "EntityName": "Account_Test", "Description": null, "CreationTime": "1541440854071", "LastModifiedTime": "1541440854071", "SizeBytes": "1950213", "SizeRows": "11241", "Fields": [{ "DatasetName": "tenant1___default", "EntityName": "Account_Test", "FieldName": "HFM", "DataType": "INTEGER" }, { "DatasetName": "tenant1___default", "EntityName": "Account_Test", "FieldName": "Name", "DataType": "STRING" }, { "DatasetName": "tenant1___default", "EntityName": "Account_Test", "FieldName": "Account", "DataType": "INTEGER" }, { "DatasetName": "tenant1___default", "EntityName": "Account_Test", "FieldName": "Account_Name", "DataType": "STRING" }, { "DatasetName": "tenant1___default", "EntityName": "Account_Test", "FieldName": "KPMG_Code", "DataType": "STRING" }, { "DatasetName": "tenant1___default", "EntityName": "Account_Test", "FieldName": "DATE_201701", "DataType": "FLOAT" }, { "DatasetName": "tenant1___default", "EntityName": "Account_Test", "FieldName": "DATE_201702", "DataType": "FLOAT" }, { "DatasetName": "tenant1___default", "EntityName": "Account_Test", "FieldName": "DATE_201703", "DataType": "FLOAT" }, { "DatasetName": "tenant1___default", "EntityName": "Account_Test", "FieldName": "DATE_201704", "DataType": "FLOAT" }, { "DatasetName": "tenant1___default", "EntityName": "Account_Test", "FieldName": "DATE_201705", "DataType": "FLOAT" }, { "DatasetName": "tenant1___default", "EntityName": "Account_Test", "FieldName": "DATE_201706", "DataType": "FLOAT" }, { "DatasetName": "tenant1___default", "EntityName": "Account_Test", "FieldName": "DATE_201707", "DataType": "FLOAT" }, { "DatasetName": "tenant1___default", "EntityName": "Account_Test", "FieldName": "DATE_201708", "DataType": "FLOAT" }, { "DatasetName": "tenant1___default", "EntityName": "Account_Test", "FieldName": "DATE_201709", "DataType": "FLOAT" }, { "DatasetName": "tenant1___default", "EntityName": "Account_Test", "FieldName": "DATE_201710", "DataType": "FLOAT" }, { "DatasetName": "tenant1___default", "EntityName": "Account_Test", "FieldName": "DATE_201711", "DataType": "FLOAT" }, { "DatasetName": "tenant1___default", "EntityName": "Account_Test", "FieldName": "DATE_201712", "DataType": "FLOAT" }] }, { "DatasetName": "tenant1___default", "EntityName": "Address", "Description": null, "CreationTime": "1529344476051", "LastModifiedTime": "1529344476051", "SizeBytes": "341814727", "SizeRows": "5000000", "Fields": [{ "DatasetName": "tenant1___default", "EntityName": "Address", "FieldName": "AddressCode", "DataType": "STRING" }, { "DatasetName": "tenant1___default", "EntityName": "Address", "FieldName": "Street", "DataType": "STRING" }, { "DatasetName": "tenant1___default", "EntityName": "Address", "FieldName": "HouseNum", "DataType": "INTEGER" }, { "DatasetName": "tenant1___default", "EntityName": "Address", "FieldName": "City", "DataType": "STRING" }, { "DatasetName": "tenant1___default", "EntityName": "Address", "FieldName": "Country", "DataType": "STRING" }, { "DatasetName": "tenant1___default", "EntityName": "Address", "FieldName": "PostalCode", "DataType": "STRING" }] }, { "DatasetName": "tenant1___default", "EntityName": "Bank", "Description": null, "CreationTime": "1529344495012", "LastModifiedTime": "1529344495012", "SizeBytes": "6140", "SizeRows": "120", "Fields": [{ "DatasetName": "tenant1___default", "EntityName": "Bank", "FieldName": "Bank_Code", "DataType": "STRING" }, { "DatasetName": "tenant1___default", "EntityName": "Bank", "FieldName": "Bank_Name", "DataType": "STRING" }, { "DatasetName": "tenant1___default", "EntityName": "Bank", "FieldName": "Address_Code", "DataType": "STRING" }, { "DatasetName": "tenant1___default", "EntityName": "Bank", "FieldName": "Telephone", "DataType": "INTEGER" }] }, { "DatasetName": "tenant1___default", "EntityName": "Credit_Card", "Description": null, "CreationTime": "1529345664172", "LastModifiedTime": "1529345664172", "SizeBytes": "171770640", "SizeRows": "2000000", "Fields": [{ "DatasetName": "tenant1___default", "EntityName": "Credit_Card", "FieldName": "Credit_Card_Number", "DataType": "INTEGER" }, { "DatasetName": "tenant1___default", "EntityName": "Credit_Card", "FieldName": "Credit_Card_Holder_Name", "DataType": "STRING" }, { "DatasetName": "tenant1___default", "EntityName": "Credit_Card", "FieldName": "Bank_Code", "DataType": "STRING" }, { "DatasetName": "tenant1___default", "EntityName": "Credit_Card", "FieldName": "Address_Code", "DataType": "STRING" }, { "DatasetName": "tenant1___default", "EntityName": "Credit_Card", "FieldName": "Card_Type", "DataType": "STRING" }, { "DatasetName": "tenant1___default", "EntityName": "Credit_Card", "FieldName": "Valid_From", "DataType": "DATE" }, { "DatasetName": "tenant1___default", "EntityName": "Credit_Card", "FieldName": "Valid_Until", "DataType": "DATE" }] }, { "DatasetName": "tenant1___default", "EntityName": "Credit_Card_Known", "Description": null, "CreationTime": "1529344647672", "LastModifiedTime": "1529344647672", "SizeBytes": "230907", "SizeRows": "5296", "Fields": [{ "DatasetName": "tenant1___default", "EntityName": "Credit_Card_Known", "FieldName": "Credit_Card_Number", "DataType": "INTEGER" }, { "DatasetName": "tenant1___default", "EntityName": "Credit_Card_Known", "FieldName": "Card_Type", "DataType": "STRING" }, { "DatasetName": "tenant1___default", "EntityName": "Credit_Card_Known", "FieldName": "Valid_From", "DataType": "DATE" }, { "DatasetName": "tenant1___default", "EntityName": "Credit_Card_Known", "FieldName": "Valid_Until", "DataType": "DATE" }] }, { "DatasetName": "tenant1___default", "EntityName": "Credit_Card_Unknown", "Description": null, "CreationTime": "1529344781965", "LastModifiedTime": "1529344781965", "SizeBytes": "2400", "SizeRows": "100", "Fields": [{ "DatasetName": "tenant1___default", "EntityName": "Credit_Card_Unknown", "FieldName": "Credit_Card_Number", "DataType": "INTEGER" }, { "DatasetName": "tenant1___default", "EntityName": "Credit_Card_Unknown", "FieldName": "Valid_From", "DataType": "DATE" }, { "DatasetName": "tenant1___default", "EntityName": "Credit_Card_Unknown", "FieldName": "Valid_Until", "DataType": "DATE" }] }, { "DatasetName": "tenant1___default", "EntityName": "FX_Test", "Description": null, "CreationTime": "1541439369542", "LastModifiedTime": "1541442141607", "SizeBytes": "351588", "SizeRows": "2073", "Fields": [{ "DatasetName": "tenant1___default", "EntityName": "FX_Test", "FieldName": "HFM", "DataType": "INTEGER" }, { "DatasetName": "tenant1___default", "EntityName": "FX_Test", "FieldName": "Name", "DataType": "STRING" }, { "DatasetName": "tenant1___default", "EntityName": "FX_Test", "FieldName": "Account", "DataType": "INTEGER" }, { "DatasetName": "tenant1___default", "EntityName": "FX_Test", "FieldName": "Account_Name", "DataType": "STRING" }, { "DatasetName": "tenant1___default", "EntityName": "FX_Test", "FieldName": "KPMG_Code", "DataType": "STRING" }, { "DatasetName": "tenant1___default", "EntityName": "FX_Test", "FieldName": "DATE_201701", "DataType": "FLOAT" }, { "DatasetName": "tenant1___default", "EntityName": "FX_Test", "FieldName": "DATE_201702", "DataType": "FLOAT" }, { "DatasetName": "tenant1___default", "EntityName": "FX_Test", "FieldName": "DATE_201703", "DataType": "FLOAT" }, { "DatasetName": "tenant1___default", "EntityName": "FX_Test", "FieldName": "DATE_201704", "DataType": "FLOAT" }, { "DatasetName": "tenant1___default", "EntityName": "FX_Test", "FieldName": "DATE_201705", "DataType": "FLOAT" }, { "DatasetName": "tenant1___default", "EntityName": "FX_Test", "FieldName": "DATE_201706", "DataType": "FLOAT" }, { "DatasetName": "tenant1___default", "EntityName": "FX_Test", "FieldName": "DATE_201707", "DataType": "FLOAT" }, { "DatasetName": "tenant1___default", "EntityName": "FX_Test", "FieldName": "DATE_201708", "DataType": "FLOAT" }, { "DatasetName": "tenant1___default", "EntityName": "FX_Test", "FieldName": "DATE_201709", "DataType": "FLOAT" }, { "DatasetName": "tenant1___default", "EntityName": "FX_Test", "FieldName": "DATE_201710", "DataType": "FLOAT" }, { "DatasetName": "tenant1___default", "EntityName": "FX_Test", "FieldName": "DATE_201711", "DataType": "FLOAT" }, { "DatasetName": "tenant1___default", "EntityName": "FX_Test", "FieldName": "DATE_201712", "DataType": "FLOAT" }] }, { "DatasetName": "tenant1___default", "EntityName": "Forex_Rate", "Description": null, "CreationTime": "1541389336016", "LastModifiedTime": "1541389336016", "SizeBytes": "3126", "SizeRows": "6", "Fields": [{ "DatasetName": "tenant1___default", "EntityName": "Forex_Rate", "FieldName": "Country", "DataType": "STRING" }, { "DatasetName": "tenant1___default", "EntityName": "Forex_Rate", "FieldName": "Currency", "DataType": "STRING" }, { "DatasetName": "tenant1___default", "EntityName": "Forex_Rate", "FieldName": "DATE_201301", "DataType": "FLOAT" }, { "DatasetName": "tenant1___default", "EntityName": "Forex_Rate", "FieldName": "DATE_201302", "DataType": "FLOAT" }, { "DatasetName": "tenant1___default", "EntityName": "Forex_Rate", "FieldName": "DATE_201303", "DataType": "FLOAT" }, { "DatasetName": "tenant1___default", "EntityName": "Forex_Rate", "FieldName": "DATE_201304", "DataType": "FLOAT" }, { "DatasetName": "tenant1___default", "EntityName": "Forex_Rate", "FieldName": "DATE_201305", "DataType": "FLOAT" }, { "DatasetName": "tenant1___default", "EntityName": "Forex_Rate", "FieldName": "DATE_201306", "DataType": "FLOAT" }, { "DatasetName": "tenant1___default", "EntityName": "Forex_Rate", "FieldName": "DATE_201307", "DataType": "FLOAT" }, { "DatasetName": "tenant1___default", "EntityName": "Forex_Rate", "FieldName": "DATE_201308", "DataType": "FLOAT" }, { "DatasetName": "tenant1___default", "EntityName": "Forex_Rate", "FieldName": "DATE_201309", "DataType": "FLOAT" }, { "DatasetName": "tenant1___default", "EntityName": "Forex_Rate", "FieldName": "DATE_201310", "DataType": "FLOAT" }, { "DatasetName": "tenant1___default", "EntityName": "Forex_Rate", "FieldName": "DATE_201311", "DataType": "FLOAT" }, { "DatasetName": "tenant1___default", "EntityName": "Forex_Rate", "FieldName": "DATE_201312", "DataType": "FLOAT" }, { "DatasetName": "tenant1___default", "EntityName": "Forex_Rate", "FieldName": "DATE_201401", "DataType": "FLOAT" }, { "DatasetName": "tenant1___default", "EntityName": "Forex_Rate", "FieldName": "DATE_201402", "DataType": "FLOAT" }, { "DatasetName": "tenant1___default", "EntityName": "Forex_Rate", "FieldName": "DATE_201403", "DataType": "FLOAT" }, { "DatasetName": "tenant1___default", "EntityName": "Forex_Rate", "FieldName": "DATE_201404", "DataType": "FLOAT" }, { "DatasetName": "tenant1___default", "EntityName": "Forex_Rate", "FieldName": "DATE_201405", "DataType": "FLOAT" }, { "DatasetName": "tenant1___default", "EntityName": "Forex_Rate", "FieldName": "DATE_201406", "DataType": "FLOAT" }, { "DatasetName": "tenant1___default", "EntityName": "Forex_Rate", "FieldName": "DATE_201407", "DataType": "FLOAT" }, { "DatasetName": "tenant1___default", "EntityName": "Forex_Rate", "FieldName": "DATE_201408", "DataType": "FLOAT" }, { "DatasetName": "tenant1___default", "EntityName": "Forex_Rate", "FieldName": "DATE_201409", "DataType": "FLOAT" }, { "DatasetName": "tenant1___default", "EntityName": "Forex_Rate", "FieldName": "DATE_201410", "DataType": "FLOAT" }, { "DatasetName": "tenant1___default", "EntityName": "Forex_Rate", "FieldName": "DATE_201411", "DataType": "FLOAT" }, { "DatasetName": "tenant1___default", "EntityName": "Forex_Rate", "FieldName": "DATE_201412", "DataType": "FLOAT" }, { "DatasetName": "tenant1___default", "EntityName": "Forex_Rate", "FieldName": "DATE_201501", "DataType": "FLOAT" }, { "DatasetName": "tenant1___default", "EntityName": "Forex_Rate", "FieldName": "DATE_201502", "DataType": "FLOAT" }, { "DatasetName": "tenant1___default", "EntityName": "Forex_Rate", "FieldName": "DATE_201503", "DataType": "FLOAT" }, { "DatasetName": "tenant1___default", "EntityName": "Forex_Rate", "FieldName": "DATE_201504", "DataType": "FLOAT" }, { "DatasetName": "tenant1___default", "EntityName": "Forex_Rate", "FieldName": "DATE_201505", "DataType": "FLOAT" }, { "DatasetName": "tenant1___default", "EntityName": "Forex_Rate", "FieldName": "DATE_201506", "DataType": "FLOAT" }, { "DatasetName": "tenant1___default", "EntityName": "Forex_Rate", "FieldName": "DATE_201507", "DataType": "FLOAT" }, { "DatasetName": "tenant1___default", "EntityName": "Forex_Rate", "FieldName": "DATE_201508", "DataType": "FLOAT" }, { "DatasetName": "tenant1___default", "EntityName": "Forex_Rate", "FieldName": "DATE_201509", "DataType": "FLOAT" }, { "DatasetName": "tenant1___default", "EntityName": "Forex_Rate", "FieldName": "DATE_201510", "DataType": "FLOAT" }, { "DatasetName": "tenant1___default", "EntityName": "Forex_Rate", "FieldName": "DATE_201511", "DataType": "FLOAT" }, { "DatasetName": "tenant1___default", "EntityName": "Forex_Rate", "FieldName": "DATE_201512", "DataType": "FLOAT" }, { "DatasetName": "tenant1___default", "EntityName": "Forex_Rate", "FieldName": "DATE_201601", "DataType": "FLOAT" }, { "DatasetName": "tenant1___default", "EntityName": "Forex_Rate", "FieldName": "DATE_201602", "DataType": "FLOAT" }, { "DatasetName": "tenant1___default", "EntityName": "Forex_Rate", "FieldName": "DATE_201603", "DataType": "FLOAT" }, { "DatasetName": "tenant1___default", "EntityName": "Forex_Rate", "FieldName": "DATE_201604", "DataType": "FLOAT" }, { "DatasetName": "tenant1___default", "EntityName": "Forex_Rate", "FieldName": "DATE_201605", "DataType": "FLOAT" }, { "DatasetName": "tenant1___default", "EntityName": "Forex_Rate", "FieldName": "DATE_201606", "DataType": "FLOAT" }, { "DatasetName": "tenant1___default", "EntityName": "Forex_Rate", "FieldName": "DATE_201607", "DataType": "FLOAT" }, { "DatasetName": "tenant1___default", "EntityName": "Forex_Rate", "FieldName": "DATE_201608", "DataType": "FLOAT" }, { "DatasetName": "tenant1___default", "EntityName": "Forex_Rate", "FieldName": "DATE_201609", "DataType": "FLOAT" }, { "DatasetName": "tenant1___default", "EntityName": "Forex_Rate", "FieldName": "DATE_201610", "DataType": "FLOAT" }, { "DatasetName": "tenant1___default", "EntityName": "Forex_Rate", "FieldName": "DATE_201611", "DataType": "FLOAT" }, { "DatasetName": "tenant1___default", "EntityName": "Forex_Rate", "FieldName": "DATE_201612", "DataType": "FLOAT" }, { "DatasetName": "tenant1___default", "EntityName": "Forex_Rate", "FieldName": "DATE_201701", "DataType": "FLOAT" }, { "DatasetName": "tenant1___default", "EntityName": "Forex_Rate", "FieldName": "DATE_201702", "DataType": "FLOAT" }, { "DatasetName": "tenant1___default", "EntityName": "Forex_Rate", "FieldName": "DATE_201703", "DataType": "FLOAT" }, { "DatasetName": "tenant1___default", "EntityName": "Forex_Rate", "FieldName": "DATE_201704", "DataType": "FLOAT" }, { "DatasetName": "tenant1___default", "EntityName": "Forex_Rate", "FieldName": "DATE_201705", "DataType": "FLOAT" }, { "DatasetName": "tenant1___default", "EntityName": "Forex_Rate", "FieldName": "DATE_201706", "DataType": "FLOAT" }, { "DatasetName": "tenant1___default", "EntityName": "Forex_Rate", "FieldName": "DATE_201707", "DataType": "FLOAT" }, { "DatasetName": "tenant1___default", "EntityName": "Forex_Rate", "FieldName": "DATE_201708", "DataType": "FLOAT" }, { "DatasetName": "tenant1___default", "EntityName": "Forex_Rate", "FieldName": "DATE_201709", "DataType": "FLOAT" }, { "DatasetName": "tenant1___default", "EntityName": "Forex_Rate", "FieldName": "DATE_201710", "DataType": "FLOAT" }, { "DatasetName": "tenant1___default", "EntityName": "Forex_Rate", "FieldName": "DATE_201711", "DataType": "FLOAT" }, { "DatasetName": "tenant1___default", "EntityName": "Forex_Rate", "FieldName": "DATE_201712", "DataType": "FLOAT" }, { "DatasetName": "tenant1___default", "EntityName": "Forex_Rate", "FieldName": "DATE_201801", "DataType": "FLOAT" }, { "DatasetName": "tenant1___default", "EntityName": "Forex_Rate", "FieldName": "DATE_201802", "DataType": "FLOAT" }, { "DatasetName": "tenant1___default", "EntityName": "Forex_Rate", "FieldName": "DATE_201803", "DataType": "FLOAT" }] }, { "DatasetName": "tenant1___default", "EntityName": "GL_Mapping", "Description": null, "CreationTime": "1541410698461", "LastModifiedTime": "1541411941629", "SizeBytes": "4446", "SizeRows": "62", "Fields": [{ "DatasetName": "tenant1___default", "EntityName": "GL_Mapping", "FieldName": "DisplayName", "DataType": "STRING" }, { "DatasetName": "tenant1___default", "EntityName": "GL_Mapping", "FieldName": "KPMG_Name", "DataType": "STRING" }, { "DatasetName": "tenant1___default", "EntityName": "GL_Mapping", "FieldName": "GL_0_Code", "DataType": "STRING" }, { "DatasetName": "tenant1___default", "EntityName": "GL_Mapping", "FieldName": "Subtotal", "DataType": "STRING" }] }, { "DatasetName": "tenant1___default", "EntityName": "General_Ledger", "Description": null, "CreationTime": "1541390710723", "LastModifiedTime": "1541408240409", "SizeBytes": "130414288", "SizeRows": "3507933", "Fields": [{ "DatasetName": "tenant1___default", "EntityName": "General_Ledger", "FieldName": "KPMG_Code", "DataType": "STRING" }, { "DatasetName": "tenant1___default", "EntityName": "General_Ledger", "FieldName": "Level_0_GL_code", "DataType": "STRING" }, { "DatasetName": "tenant1___default", "EntityName": "General_Ledger", "FieldName": "GL_Value", "DataType": "FLOAT" }, { "DatasetName": "tenant1___default", "EntityName": "General_Ledger", "FieldName": "Country", "DataType": "STRING" }, { "DatasetName": "tenant1___default", "EntityName": "General_Ledger", "FieldName": "GL_Date", "DataType": "DATE" }] }, { "DatasetName": "tenant1___default", "EntityName": "Mapping_Test", "Description": null, "CreationTime": "1541442895563", "LastModifiedTime": "1541442918063", "SizeBytes": "4446", "SizeRows": "62", "Fields": [{ "DatasetName": "tenant1___default", "EntityName": "Mapping_Test", "FieldName": "GL_0_Code", "DataType": "INTEGER" }, { "DatasetName": "tenant1___default", "EntityName": "Mapping_Test", "FieldName": "DisplayName", "DataType": "STRING" }, { "DatasetName": "tenant1___default", "EntityName": "Mapping_Test", "FieldName": "KPMG_Name", "DataType": "STRING" }, { "DatasetName": "tenant1___default", "EntityName": "Mapping_Test", "FieldName": "Subtotal", "DataType": "STRING" }] }, { "DatasetName": "tenant1___default", "EntityName": "Normalization_Adjustment_CC", "Description": null, "CreationTime": "1541392134653", "LastModifiedTime": "1541413059812", "SizeBytes": "2554955", "SizeRows": "36495", "Fields": [{ "DatasetName": "tenant1___default", "EntityName": "Normalization_Adjustment_CC", "FieldName": "Normalisation", "DataType": "INTEGER" }, { "DatasetName": "tenant1___default", "EntityName": "Normalization_Adjustment_CC", "FieldName": "Normalisation_Nr", "DataType": "INTEGER" }, { "DatasetName": "tenant1___default", "EntityName": "Normalization_Adjustment_CC", "FieldName": "Landlord_Code", "DataType": "INTEGER" }, { "DatasetName": "tenant1___default", "EntityName": "Normalization_Adjustment_CC", "FieldName": "Country", "DataType": "STRING" }, { "DatasetName": "tenant1___default", "EntityName": "Normalization_Adjustment_CC", "FieldName": "KPMG_Code", "DataType": "STRING" }, { "DatasetName": "tenant1___default", "EntityName": "Normalization_Adjustment_CC", "FieldName": "HFM", "DataType": "INTEGER" }, { "DatasetName": "tenant1___default", "EntityName": "Normalization_Adjustment_CC", "FieldName": "Value", "DataType": "FLOAT" }, { "DatasetName": "tenant1___default", "EntityName": "Normalization_Adjustment_CC", "FieldName": "Year", "DataType": "INTEGER" }, { "DatasetName": "tenant1___default", "EntityName": "Normalization_Adjustment_CC", "FieldName": "Month", "DataType": "INTEGER" }, { "DatasetName": "tenant1___default", "EntityName": "Normalization_Adjustment_CC", "FieldName": "Normalization_Date", "DataType": "DATE" }] }, { "DatasetName": "tenant1___default", "EntityName": "Reference_Detail", "Description": null, "CreationTime": "1541389551607", "LastModifiedTime": "1541390594533", "SizeBytes": "1204534", "SizeRows": "9104", "Fields": [{ "DatasetName": "tenant1___default", "EntityName": "Reference_Detail", "FieldName": "KPMG_Code", "DataType": "STRING" }, { "DatasetName": "tenant1___default", "EntityName": "Reference_Detail", "FieldName": "Country", "DataType": "STRING" }, { "DatasetName": "tenant1___default", "EntityName": "Reference_Detail", "FieldName": "City", "DataType": "STRING" }, { "DatasetName": "tenant1___default", "EntityName": "Reference_Detail", "FieldName": "Strategy_City_label", "DataType": "STRING" }, { "DatasetName": "tenant1___default", "EntityName": "Reference_Detail", "FieldName": "Strategy_Purpose", "DataType": "STRING" }, { "DatasetName": "tenant1___default", "EntityName": "Reference_Detail", "FieldName": "Contract_Type", "DataType": "STRING" }, { "DatasetName": "tenant1___default", "EntityName": "Reference_Detail", "FieldName": "Business_Model", "DataType": "STRING" }, { "DatasetName": "tenant1___default", "EntityName": "Reference_Detail", "FieldName": "Reporting_Structure", "DataType": "STRING" }, { "DatasetName": "tenant1___default", "EntityName": "Reference_Detail", "FieldName": "Start_month_parking_facility", "DataType": "STRING" }, { "DatasetName": "tenant1___default", "EntityName": "Reference_Detail", "FieldName": "Start_year_parking_facility", "DataType": "INTEGER" }, { "DatasetName": "tenant1___default", "EntityName": "Reference_Detail", "FieldName": "End_year_contract", "DataType": "INTEGER" }, { "DatasetName": "tenant1___default", "EntityName": "Reference_Detail", "FieldName": "Size_num_of_contract_spaces", "DataType": "INTEGER" }] }, { "DatasetName": "tenant1___default", "EntityName": "Stores", "Description": null, "CreationTime": "1533887382338", "LastModifiedTime": "1540156209084", "SizeBytes": "2494", "SizeRows": "49", "Fields": [{ "DatasetName": "tenant1___default", "EntityName": "Stores", "FieldName": "store_id", "DataType": "INTEGER" }, { "DatasetName": "tenant1___default", "EntityName": "Stores", "FieldName": "store_type", "DataType": "STRING" }, { "DatasetName": "tenant1___default", "EntityName": "Stores", "FieldName": "assortment_type", "DataType": "STRING" }, { "DatasetName": "tenant1___default", "EntityName": "Stores", "FieldName": "competition_distance", "DataType": "FLOAT" }, { "DatasetName": "tenant1___default", "EntityName": "Stores", "FieldName": "promo2", "DataType": "INTEGER" }, { "DatasetName": "tenant1___default", "EntityName": "Stores", "FieldName": "promo2_since_week", "DataType": "FLOAT" }, { "DatasetName": "tenant1___default", "EntityName": "Stores", "FieldName": "Promo2_since_year", "DataType": "FLOAT" }, { "DatasetName": "tenant1___default", "EntityName": "Stores", "FieldName": "competition_open_since_month", "DataType": "INTEGER" }, { "DatasetName": "tenant1___default", "EntityName": "Stores", "FieldName": "competition_open_since_year", "DataType": "INTEGER" }] }, { "DatasetName": "tenant1___default", "EntityName": "Transaction", "Description": null, "CreationTime": "1529345015486", "LastModifiedTime": "1529345015486", "SizeBytes": "439990592", "SizeRows": "10000000", "Fields": [{ "DatasetName": "tenant1___default", "EntityName": "Transaction", "FieldName": "Transaction_Number", "DataType": "INTEGER" }, { "DatasetName": "tenant1___default", "EntityName": "Transaction", "FieldName": "Reference_Number", "DataType": "INTEGER" }, { "DatasetName": "tenant1___default", "EntityName": "Transaction", "FieldName": "Is_Debit", "DataType": "BOOLEAN" }, { "DatasetName": "tenant1___default", "EntityName": "Transaction", "FieldName": "Amount", "DataType": "FLOAT" }, { "DatasetName": "tenant1___default", "EntityName": "Transaction", "FieldName": "Transaction_Date", "DataType": "TIMESTAMP" }, { "DatasetName": "tenant1___default", "EntityName": "Transaction", "FieldName": "Type", "DataType": "STRING" }] }, { "DatasetName": "tenant1___default", "EntityName": "src", "Description": null, "CreationTime": "1540211246487", "LastModifiedTime": "1541386348834", "SizeBytes": "193", "SizeRows": "4", "Fields": [{ "DatasetName": "tenant1___default", "EntityName": "src", "FieldName": "Q1", "DataType": "INTEGER" }, { "DatasetName": "tenant1___default", "EntityName": "src", "FieldName": "Q3", "DataType": "INTEGER" }, { "DatasetName": "tenant1___default", "EntityName": "src", "FieldName": "Name", "DataType": "STRING" }, { "DatasetName": "tenant1___default", "EntityName": "src", "FieldName": "Int", "DataType": "INTEGER" }, { "DatasetName": "tenant1___default", "EntityName": "src", "FieldName": "Date", "DataType": "DATE" }, { "DatasetName": "tenant1___default", "EntityName": "src", "FieldName": "String", "DataType": "STRING" }] }] }
    if (data.StatusCode && data.StatusCode === 200) {
      if (data.Content && data.Content.NuoEvaTables) {
        nuoTables = data.Content.NuoEvaTables.filter(function (nuoEntity) {
          return (
            !nuoEntity.EntityName.toLowerCase().endsWith("_profiling") &&
            !nuoEntity.EntityName.toLowerCase().endsWith("_pattern")
          );
        });
      }
    }
    if (callbackTask) {
      callbackTask();
    }
  };
  // if (callbackTask) {
  // callbackTask();
  // }
}

function renderStorageExplorerTable(tableName) {
  var jexcelConfig = {
    data: [],
    colHeaders: [],
    colWidths: [],
    colAlignments: [],
    tableOverflow: true,
    allowManualInsertRow: "false",
    editable: "false",
    onselection: function (e) {
      var children = $("#storageExplorerGrid>.jexcel>tbody>.selected")[0]
        .children;
      var isCheckboxSelected = children[1].childNodes[0].childNodes[0].checked;
      if (isCheckboxSelected) {
        children[1].childNodes[0].childNodes[0].checked = false;
        isCheckboxSelected = false;
      } else {
        children[1].childNodes[0].childNodes[0].checked = true;
        isCheckboxSelected = true;
      }
      for (var i = 0; i < children.length; i++) {
        if (isCheckboxSelected) {
          children[i].className += " highlight";
        } else {
          children[i].className = children[i].className.replaceAll(
            "highlight",
            ""
          );
        }
      }
    },
    columns: []
  };

  jexcelConfig.colHeaders.push(
    "<div class='allcheckbox-td custom-checkbox'>" +
    `<input class='all-select' type='checkbox' onclick='checkAllCheckbox(this)'/>` +
    "<span></span>" +
    "</div>"
  );
  jexcelConfig.columns.push({ type: "text" });
  jexcelConfig.colAlignments.push("left");
  jexcelConfig.colWidths.push("5%");

  if (
    tableName &&
    tableName.trim().length > 0 &&
    nuoTables &&
    nuoTables.length > 0
  ) {
    var evaTableMatches = nuoTables.filter(function (ele) {
      return ele.EntityName === tableName;
    });
    if (evaTableMatches && evaTableMatches.length > 0) {
      jexcelConfig.colHeaders.push("<div data-breakpoints='xs'>Column Name");
      jexcelConfig.columns.push({ type: "text", wordWrap: true });
      jexcelConfig.colAlignments.push("left");
      jexcelConfig.colWidths.push("25%");

      jexcelConfig.colHeaders.push("<div data-breakpoints='xs'>Data Type");
      jexcelConfig.columns.push({ type: "text", wordWrap: true });
      jexcelConfig.colAlignments.push("left");
      jexcelConfig.colWidths.push("25%");

      var evaTable = evaTableMatches[0];

      $(evaTable.Fields).each(function (index, nuoField) {
        var row = [];
        row.push(
          "<div class='dataGridRowCheckboxDiv custom-checkbox'>" +
          "<input id='dataGridRowCheckbox" +
          index +
          "' class='dataGridRowCheckbox' type='checkbox'/>" +
          "<span></span>" +
          "</div>"
        );

        row.push(nuoField.FieldName);
        row.push(
          "<div id='storageExplorerGrid_rowObject_" +
          index +
          "' rowObject='" +
          JSON.stringify(nuoField) +
          "'>" +
          nuoField.DataType +
          "</div>"
        );
        jexcelConfig.data.push(row);
      });
      $("#storageExplorerGrid").jexcel(jexcelConfig);
      addDataGridCheckboxEvents();


      // renderJqxGrid('#storageExplorerTable', evaTable.Fields, gridColumns);
    }
  } else if (nuoTables && nuoTables.length > 0) {
    jexcelConfig.colHeaders.push("<div data-breakpoints='xs'>Table Name");
    jexcelConfig.columns.push({ type: "text", wordWrap: true });
    jexcelConfig.colAlignments.push("left");
    jexcelConfig.colWidths.push("35%");

    jexcelConfig.colHeaders.push("<div data-breakpoints='xs'>Size");
    jexcelConfig.columns.push({ type: "text" });
    jexcelConfig.colAlignments.push("right");
    jexcelConfig.colWidths.push("12%");

    jexcelConfig.colHeaders.push("<div data-breakpoints='xs'>Total Rows");
    jexcelConfig.columns.push({ type: "text" });
    jexcelConfig.colAlignments.push("right");
    jexcelConfig.colWidths.push("12%");

    jexcelConfig.colHeaders.push("<div data-breakpoints='xs'>Created On");
    jexcelConfig.columns.push({ type: "text" });
    jexcelConfig.colAlignments.push("left");
    jexcelConfig.colWidths.push("18%");

    jexcelConfig.colHeaders.push("<div data-breakpoints='xs'>Last Modified on");
    jexcelConfig.columns.push({ type: "text" });
    jexcelConfig.colAlignments.push("left");
    jexcelConfig.colWidths.push("18%");

    var evaTables = nuoTables.map(function (ele) {
      ele.SizeRows = parseInt(ele.SizeRows);
      ele.SizeLabel = getSizeLabel(parseInt(ele.SizeBytes));
      ele.CreatedAt = new Date(parseInt(ele.CreationTime));
      ele.LastModifiedOn = new Date(parseInt(ele.LastModifiedTime));
      return ele;
    });
    $(evaTables).each(function (index, nuoEntity) {
      var row = [];
      row.push(
        "<div class='dataGridRowCheckboxDiv custom-checkbox'>" +
        "<input id='dataGridRowCheckbox" +
        index +
        "' class='dataGridRowCheckbox' type='checkbox'/>" +
        "<span></span>" +
        "</div>"
      );
      row.push(nuoEntity.EntityName);
      row.push(
        "<div id='storageExplorerGrid_rowObject_" +
        index +
        "' rowObject='" +
        JSON.stringify(nuoEntity) +
        "'>" +
        nuoEntity.SizeLabel +
        "</div>"
      );
      row.push(nuoEntity.SizeRows);
      row.push(GetFormattedDate(nuoEntity.CreatedAt));
      row.push(GetFormattedDate(nuoEntity.LastModifiedOn));
      jexcelConfig.data.push(row);
    });
    $("#storageExplorerGrid").jexcel(jexcelConfig);
    addDataGridCheckboxEvents();

    // renderJqxGrid('#storageExplorerTable', evaTables, gridColumns);
  }

  // $("#storageExplorerTable").show();
  if (tableName && tableName.length > 0) {
    $("#storageExplorerTableName").html(
      "Table Name: <span style='font-weight:normal;'> " + tableName + "</span>"
    );
  } else {
    $("#storageExplorerTableName").html("");
  }
  addStorageExplorerEventHandlers();
  $("#storageExplorerTableName").show();
}

function addStorageExplorerEventHandlers() {
  addTablePreviewHandler();
  $("#storageGridUpButton").off("click");
  $("#storageGridUpButton").on("click", function (e) {
    e.preventDefault();
    var rowData = getGridRowData("storageExplorerGrid", 0);
    var grandParentPath = "";
    if (rowData.FieldName) {
      loadStorageExplorerTable(null, false);
    }
    // $('#storageExplorerTable').jqxGrid('clearselection');
  });

  $("#storageGridRefreshButton").on("click");
  $("#storageGridRefreshButton").on("click", function (e) {
    e.preventDefault();
    loadStorageExplorerTable(null, true);
  });
  var children = $("#storageExplorerGrid>.jexcel>tbody>tr");
  for (var i = 0; i < children.length; i++) {
    $("#storageExplorerGrid>.jexcel>tbody>tr#" + children[i].id).off(
      "dblclick"
    );
    $("#storageExplorerGrid>.jexcel>tbody>tr#" + children[i].id).on(
      "dblclick",
      function (ele) {
        var rowIndex = ele.target.parentNode.id.split("-")[1];
        var rowData = getGridRowData("storageExplorerGrid", rowIndex);
        if (rowData) {
          if (
            !rowData.FieldName &&
            rowData.EntityName &&
            rowData.EntityName.length > 0
          ) {
            loadStorageExplorerTable(rowData.EntityName, false);
          }
        }
      }
    );
  }
}

function addTablePreviewHandler() {
  $("#storageGridPreivewButton").off("click");
  $("#storageGridPreivewButton").on("click", function (e) {
    e.preventDefault();
    if ($("#storageExplorerTableName")[0].innerText.trim().length > 0) {
      return;
    }
    var tableName = $("#storageExplorerGrid>table>tbody>tr>td input:checked")
      .closest("tr")
      .children()[2].innerText;

    previewTable(tableName);
  });
}

function previewTable(tableName) {
  if (tableName && tableName.trim().length > 0) {
    var tablePreview = tablePreviews[tableName];
    if (tablePreview) {
      var seriesGroup = $(tablePreview.Result.Metadata).map(function (
        index,
        nuoField
      ) {
        var series = {
          nuoField: nuoField,
          data: tablePreview.Result.Data.map(function (row) {
            return row[index];
          })
        };
        return series;
      });
      drawDataGrid("TablePreview", seriesGroup.toArray());
    } else {
      $("#ajaxLoader").addClass("is-active");
      directCall("rt173", "NuoEntities", [
        {
          DatasetName: "",
          EntityName: tableName,
          Fields: []
        }
      ])
        .done(function (data) {
          handleResponse(data);
        })
        .fail(function () {
          if (sessionId === "X") {
            var data = {
              StatusCode: 200,
              Status: "OK",
              Content: {
                NuoEvaMessage: {
                  AnalysisId: "TableExplorerRequest",
                  RuleText: "",
                  CommunicationType: 7,
                  Message: ""
                },
                Result: {
                  Metadata: [
                    {
                      DatasetName: "",
                      EntityName: "src",
                      FieldName: "Q1",
                      DataType: "INTEGER"
                    },
                    {
                      DatasetName: "",
                      EntityName: "src",
                      FieldName: "Q3",
                      DataType: "INTEGER"
                    },
                    {
                      DatasetName: "",
                      EntityName: "src",
                      FieldName: "Name",
                      DataType: "STRING"
                    },
                    {
                      DatasetName: "",
                      EntityName: "src",
                      FieldName: "Int",
                      DataType: "INTEGER"
                    },
                    {
                      DatasetName: "",
                      EntityName: "src",
                      FieldName: "Date",
                      DataType: "DATE"
                    },
                    {
                      DatasetName: "",
                      EntityName: "src",
                      FieldName: "String",
                      DataType: "STRING"
                    }
                  ],
                  Data: [
                    ["1", "3", "Pulkit Barad", "4", "2015-01-01", "2"],
                    ["1", "3", "Bruce Wayne", "4", "2018-11-01", "2"],
                    ["1", "3", "John MacClane", "4", "2016-01-01", "2"],
                    ["1", "3", "John Wick", "4", "2015-02-01", "2"]
                  ]
                }
              }
            };
            handleResponse(data);
          }
        });
      var handleResponse = function (data) {
        if (data.StatusCode && data.StatusCode === 200) {
          var nuoQueryResponse = data.Content;
          var nuoEvaMessage = nuoQueryResponse.NuoEvaMessage;
          if (nuoEvaMessage.CommunicationType === COMMUNICATION_TYPE_ERROR) {
            alert(nuoEvaMessage.Message);
          } else if (
            nuoEvaMessage.CommunicationType ===
            COMMUNICATION_TYPE_RESULT_AVAILABLE
          ) {
            var result = nuoQueryResponse.Result;
            var profilingResult = nuoQueryResponse.ProfilingResult;
            var pattern = nuoQueryResponse.Pattern;
            var seriesGroup = $(result.Metadata)
              .map(function (index, nuoField) {
                var series = {
                  nuoField: nuoField,
                  data: result.Data.map(function (row) {
                    return row[index];
                  })
                };
                return series;
              })
              .toArray();
            tablePreviews[tableName] = {
              Result: result,
              ProfilingResult: profilingResult,
              Pattern: pattern
            };
            drawDataGrid("TablePreview", seriesGroup);
          } else {
            alert("Oops! Something went wrong while previewing data in");
          }
        } else {
          //window.location.reload();
        }
        $("#ajaxLoader").removeClass("is-active");
      };
    }
  }
}

//Storage Explorer block END

//Serachbox Autocomplete block START

var selectAliases = new Map();
var filterAliases = new Map();
var levenshteinDist = function (s, t) {
  var d = []; //2d matrix

  // Step 1
  var n = s.length;
  var m = t.length;

  if (n == 0) return m;
  if (m == 0) return n;

  //Create an array of arrays in javascript (a descending loop is quicker)
  for (var i = n; i >= 0; i--) d[i] = [];

  // Step 2
  for (var i = n; i >= 0; i--) d[i][0] = i;
  for (var j = m; j >= 0; j--) d[0][j] = j;

  // Step 3
  for (var i = 1; i <= n; i++) {
    var s_i = s.charAt(i - 1);

    // Step 4
    for (var j = 1; j <= m; j++) {
      //Check the jagged ld total so far
      if (i == j && d[i][j] > 4) return n;

      var t_j = t.charAt(j - 1);
      var cost = s_i == t_j ? 0 : 1; // Step 5

      //Calculate the minimum
      var mi = d[i - 1][j] + 1;
      var b = d[i][j - 1] + 1;
      var c = d[i - 1][j - 1] + cost;

      if (b < mi) mi = b;
      if (c < mi) mi = c;

      d[i][j] = mi; // Step 6

      //Damerau transposition
      if (i > 1 && j > 1 && s_i == t.charAt(j - 2) && s.charAt(i - 2) == t_j) {
        d[i][j] = Math.min(d[i][j], d[i - 2][j - 2] + cost);
      }
    }
  }

  // Step 7
  return d[n][m];
};


function dismiss(e) {
  $(e.target).parent('span').remove();
};

function autocomplete(inputField) {
  /*the autocomplete function takes two arguments,
	the text field element and an array of possible autocompleted values:*/
  var currentFocus;
  /*execute a function when someone writes in the text field:*/
  inputField.addEventListener("input", function (e) {

    if (this.innerText.trim().length == 0) {
      var analysisId = "";

      if (inputField.id.startsWith("historyAnalysisSelectionInput")) {
        analysisId = inputField.id.substring(29);
      } else {
        //this auto-complete for historyAnalysisFilterInput
        analysisId = inputField.id.substring(26);
      }
      historyAnalyses[analysisId].AliasMapping = {};
    }
    var currWords = this.innerText.trim().split(/\s+/g);
    // console.log(8888, currWords);
    var enteredText = "";
    if (this.innerText.length > 0 && currWords.length > 0)
      enteredText = currWords.reverse()[0];

    /*close any already open lists of autocompleted values*/
    closeAllLists();
    if (!enteredText) {
      return false;
    }
    currentFocus = -1;
    /*create a DIV element that will contain the items (values):*/
    var suggestions = document.createElement("DIV");
    suggestions.setAttribute("id", this.id + "autocomplete-list");
    suggestions.setAttribute("class", "suggestions");
    /*append the DIV element as a child of the autocomplete container:*/
    this.parentNode.appendChild(suggestions);
    var matchingFields = nuoTables
      .flatMap(function (ele) {
        return ele.Fields;
      })
      .filter(function (field) {
        return (
          matchString(enteredText, field.EntityName) ||
          matchString(enteredText, field.FieldName)
        );
      });
    // .concat(
    //   nuoTables.map(function (ele) {
    //     var allDetailsField = {
    //       EntityName: ele.EntityName,
    //       FieldName: "Everything",
    //       DataType: ele.DataType
    //     };
    //     return allDetailsField;
    //   })
    // );
    if (matchingFields.length > 0) {
      matchingFields.forEach(function (field) {
        var entityName = field.EntityName;
        var fieldName = field.FieldName;
        var datatype = field.DataType;
        var suggestedItem = document.createElement("DIV");
        /*create a DIV element for each matching element:*/
        suggestedItem = document.createElement("DIV");
        suggestedItem.innerHTML =
          "<span class='suggestions_tags'><i>" +
          highlightText(fieldName, enteredText) +
          "</i><font color='grey'> from table </font>" +
          highlightText(entityName, enteredText) + "</span>";
        /*insert a input field that will hold the current array item's value:*/

        suggestedItem.entityName = entityName;
        suggestedItem.fieldName = fieldName;
        suggestedItem.datatype = datatype;
        /*execute a function when someone clicks on the item value (DIV element):*/
        suggestedItem.addEventListener("click", function (e) {
          e.preventDefault();
          /*insert the value for the autocomplete text field:*/
          var oldValue = inputField.innerHTML.trim().replace('&nbsp;', ' ');
          // console.log(111, oldValue, enteredText);
          if (oldValue.length > enteredText.length)
            oldValue = oldValue.substring(
              0,
              oldValue.length - enteredText.length
            );
          else oldValue = '';


          // console.log(222, oldValue, oldValue.length, enteredText.length);
          var alias = this.fieldName;
          var qualifiedName =
            "[" +
            this.entityName +
            "][" +
            this.fieldName +
            "][" +
            this.datatype +
            "]";
          var analysisId = "";

          if (inputField.id.startsWith("historyAnalysisSelectionInput")) {
            analysisId = inputField.id.substring(29);
          } else {
            //this auto-complete for historyAnalysisFilterInput
            analysisId = inputField.id.substring(26);
          }
          if (!historyAnalyses[analysisId].AliasMapping) {
            historyAnalyses[analysisId].AliasMapping = {};
          }

          if (this.fieldName.toLowerCase() === "everything") {
            alias = this.fieldName + " from " + this.entityName;
          } else if (
            historyAnalyses[analysisId].AliasMapping.hasOwnProperty(alias) &&
            historyAnalyses[analysisId].AliasMapping[alias] != qualifiedName
          ) {
            alias = this.fieldName + "_" + this.entityName;
          }
          var selectedEntityName = this.entityName;

          if (this.fieldName.toLowerCase() === "everything") {
            var qualifiedNames = nuoTables
              .flatMap(function (ele) {
                return ele.Fields;
              })
              .filter(function (field) {
                return (
                  field.EntityName.toLowerCase() ===
                  selectedEntityName.toLowerCase()
                );
              })
              .map(function (field) {
                return (
                  "[" +
                  field.EntityName +
                  "][" +
                  field.FieldName +
                  "][" +
                  field.DataType +
                  "]"
                );
              })
              .join(", ");
            historyAnalyses[analysisId].AliasMapping[alias] = qualifiedNames;
          } else {
            historyAnalyses[analysisId].AliasMapping[alias] = qualifiedName;
          }

          inputField.innerHTML = oldValue + "&nbsp;<span contenteditable='false' class='tag'><span>" + alias + "</span><a class='dismiss'></a></span>&nbsp;";
          $('.dismiss').click((e) => {
            dismiss(e);
          });

          // <li class='tag'><a class='dismiss'>×</a><span></span></li>
          // console.log("AliasMapping = "+JSON.stringify(historyAnalyses[analysisId].AliasMapping));
          /*close the list of autocompleted values,
					(or any other open lists of autocompleted values:*/
          closeAllLists();
        });
        suggestions.appendChild(suggestedItem);
      });
    } else {
    }
  });
  function highlightText(inputText, textToMatch) {
    var matchIndex = inputText.toUpperCase().indexOf(textToMatch.toUpperCase());

    if (matchIndex >= 0) {
      var inputTextBefore = inputText.substring(0, matchIndex);
      var inputTextMatch = inputText.substr(matchIndex, textToMatch.length);
      var inputTextAfter = inputText.substring(matchIndex + textToMatch.length);
      return (
        inputTextBefore +
        "<strong>" +
        inputTextMatch +
        "</strong>" +
        inputTextAfter
      );
    } else {
      return inputText;
    }
  }
  /*execute a function presses a key on the keyboard:*/
  inputField.addEventListener("keydown", function (e) {
    var x = document.getElementById(this.id + "autocomplete-list");
    if (x) x = x.getElementsByTagName("div");
    if (e.keyCode == 40) {
      /*If the arrow DOWN key is pressed,
			increase the currentFocus variable:*/
      currentFocus++;
      /*and and make the current item more visible:*/
      addActive(x);
    } else if (e.keyCode == 38) {
      //up
      /*If the arrow UP key is pressed,
			decrease the currentFocus variable:*/
      currentFocus--;
      /*and and make the current item more visible:*/
      addActive(x);
    } else if (e.keyCode == 13) {
      /*If the ENTER key is pressed, prevent the form from being submitted,*/
      e.preventDefault();
      if (currentFocus > -1) {
        /*and simulate a click on the "active" item:*/
        if (x) x[currentFocus].click();
      }
    }
  });
  function addActive(x) {
    /*a function to classify an item as "active":*/
    if (!x) return false;
    /*start by removing the "active" class on all items:*/
    removeActive(x);
    if (currentFocus >= x.length) currentFocus = 0;
    if (currentFocus < 0) currentFocus = x.length - 1;
    /*add class "autocomplete-active":*/
    x[currentFocus].classList.add("autocomplete-active");
  }
  function removeActive(x) {
    /*a function to remove the "active" class from all autocomplete items:*/
    for (var i = 0; i < x.length; i++) {
      x[i].classList.remove("autocomplete-active");
    }
  }
  function closeAllLists(elmnt) {
    /*close all autocomplete lists in the document,
		except the one passed as an argument:*/
    var x = document.getElementsByClassName("suggestions");
    for (var i = 0; i < x.length; i++) {
      if (elmnt != x[i] && elmnt != inputField) {
        x[i].parentNode.removeChild(x[i]);
      }
    }
  }
  /*execute a function when someone clicks in the document:*/
  document.addEventListener("click", function (e) {
    // e.preventDefault();
    closeAllLists(e.target);
  });
}

//Serachbox Autocomplete block END

function renderJqxGrid(
  uid,
  gridContent,
  gridColumns,
  enablePaging,
  paramEditable
) {
  // prepare the data
  var source = {
    localData: gridContent,
    dataType: "json"
  };
  var dataAdapter = new $.jqx.dataAdapter(source);
  var dataTableSettings = {
    columnsResize: true,
    altRows: false,
    sortable: true,
    selectionMode: "checkbox",
    source: dataAdapter,
    columns: gridColumns,
    width: "98%",
    height: "80%",
    // autoheight: true,
    // autorowheight: true,
    scrollmode: "logical",
    theme: jqWidgetThemeName
  };

  if (paramEditable) {
    dataTableSettings.editable = true;
  }

  if (enablePaging) {
    dataTableSettings.pageable = true;
    dataTableSettings.pagerMode = "advanced";
  }

  $(uid).jqxGrid(dataTableSettings);
}

function getReadableDuration(timeMillis) {
  var hh = 0;
  var mm = 0;
  var ss = timeMillis / 1000;

  if (ss >= 3600) {
    hh = parseInt(ss / 3600);
    ss -= hh * 3600;
  }
  if (ss >= 60) {
    mm = parseInt(ss / 60);
    ss -= mm * 60;
  }

  return hh + "h:" + mm + "m:" + parseInt(ss) + "s";
}

function getReadableNumber(number) {

  var numPart = number / (1000 * 1000 * 1000.0);
  var decPart = 3;

  if (Math.abs(numPart) > 1) {
    return numPart.toFixed(decPart) + "B";
  }

  numPart = number / (1000 * 1000.0);

  decPart = 3;
  if (numPart % 1 == 0)
    decPart = 0;

  if (Math.abs(numPart) > 1) {
    return numPart.toFixed(decPart) + "M";
  }

  numPart = number / 1000.0

  decPart = 3;
  if (numPart % 1 == 0)
    decPart = 0;

  if (Math.abs(numPart) > 1) {
    return numPart.toFixed(decPart) + "K";
  }

  numPart = number;
  decPart = 3;
  if (numPart % 1 == 0)
    decPart = 0;

  return numPart.toFixed(decPart);
}

function getNarrativeNumber(number, isDutch) {

  var numPart = number / (1000 * 1000 * 1000.0);
  var decPart = 2;

  if (Math.abs(numPart) > 1) {
    if (isDutch === true)
      return numPart.toFixed(decPart).replace(/\.0+$/g, "") + " Miljard";
    else
      return numPart.toFixed(decPart).replace(/\.0+$/g, "") + " Billion";
  }

  numPart = number / (1000 * 1000.0);

  decPart = 2;
  if (numPart % 1 == 0)
    decPart = 0;

  if (Math.abs(numPart) > 1) {
    if (isDutch === true)
      return numPart.toFixed(decPart).replace(/\.0+$/g, "") + " Miljoen";
    else
      return numPart.toFixed(decPart).replace(/\.0+$/g, "") + " Million";
  }

  numPart = number / 1000.0

  decPart = 2;
  if (numPart % 1 == 0)
    decPart = 0;

  if (Math.abs(numPart) > 1) {
    if (isDutch === true)
      return numPart.toFixed(decPart).replace(/\.0+$/g, "") + " Duizend";
    else
      return numPart.toFixed(decPart).replace(/\.0+$/g, "") + " Thousand";
  }

  numPart = number;
  decPart = 2;
  if (numPart % 1 == 0)
    decPart = 0;

  return numPart.toFixed(decPart).replace(/\.0+$/g, "");
}

function matchString(inputText, targetText) {
  var input = inputText.toUpperCase().replaceAll("_", "");
  var target = targetText.toUpperCase().replaceAll("_", "");
  if (inputText.length > targetText.length) return false;
  else {
    if (target.indexOf(input) >= 0) return true;
    else {
      if (
        levenshteinDist(input, target.substr(0, inputText.length)) /
        inputText.length <=
        0.25
      )
        return true;
    }
  }
  return false;
}

String.prototype.replaceAll = function (search, replace) {
  if (replace === undefined) {
    return this.toString();
  }
  return this.split(search).join(replace);
};

const concat = (x, y) => x.concat(y);

const flatMap = (f, xs) => xs.map(f).reduce(concat, []);

Array.prototype.flatMap = function (f) {
  return flatMap(f, this);
};
const randomColor = (alpha => {
  const randomInt = (min, max) => {
    return Math.floor(Math.random() * (max - min + 1)) + min;
  };

  return () => {
    var h = randomInt(0, 360);
    var s = randomInt(42, 98);
    var l = randomInt(40, 90);
    return `hsl(${h},${s}%,${l}%,${alpha})`;
  };
})();

function toTitleCase(str) {
  return str.replace(/\w\S*/g, function (txt) {
    return txt.charAt(0).toUpperCase() + txt.substr(1).toLowerCase();
  });
}

function stringToTime(inputValue) {
  var timeParts = inputValue.trim().split(":");
  if (timeParts.length == 3) {
    var date = new Date();
    date.setHours(timeParts[0]);
    date.setMinutes(timeParts[1]);
    date.setSeconds(timeParts[2]);
    return date;
  } else return inputValue;
}

(function ($) {
  $.fn.drags = function (opt) {
    opt = $.extend({ handle: "", cursor: "move" }, opt);

    if (opt.handle === "") {
      var $el = this;
    } else {
      var $el = this.find(opt.handle);
    }

    return $el
      .css("cursor", opt.cursor)
      .on("mousedown", function (e) {
        if (opt.handle === "") {
          var $drag = $(this).addClass("draggable");
        } else {
          var $drag = $(this)
            .addClass("active-handle")
            .parent()
            .addClass("draggable");
        }
        var z_idx = $drag.css("z-index"),
          drg_h = $drag.outerHeight(),
          drg_w = $drag.outerWidth(),
          pos_y = $drag.offset().top + drg_h - e.pageY,
          pos_x = $drag.offset().left + drg_w - e.pageX;
        $drag
          .css("z-index", 1000)
          .parents()
          .on("mousemove", function (e) {
            $(".draggable")
              .offset({
                top: e.pageY + pos_y - drg_h,
                left: e.pageX + pos_x - drg_w
              })
              .on("mouseup", function () {
                $(this)
                  .removeClass("draggable")
                  .css("z-index", z_idx);
              });
          });
        e.preventDefault(); // disable selection
      })
      .on("mouseup", function () {
        if (opt.handle === "") {
          $(this).removeClass("draggable");
        } else {
          $(this)
            .removeClass("active-handle")
            .parent()
            .removeClass("draggable");
        }
      });
  };
})(jQuery);

function hexToBytes(str) {
  if (!str) {
    return new Uint8Array();
  }

  var a = [];
  for (var i = 0, len = str.length; i < len; i += 2) {
    a.push(parseInt(str.substr(i, 2), 16));
  }

  return new Uint8Array(a);
}


function playByteArray(byteArray) {

  var arrayBuffer = new ArrayBuffer(byteArray.length);
  var bufferView = new Uint8Array(arrayBuffer);
  for (i = 0; i < byteArray.length; i++) {
    bufferView[i] = byteArray[i];
  }

  audioContext.decodeAudioData(arrayBuffer, function (buffer) {
    audioBuffer = buffer;
    play();
  });
}

// Play the loaded file
function play() {
  // Create a source node from the buffer
  var source = audioContext.createBufferSource();
  source.buffer = audioBuffer;
  // Connect to the final output node (the speakers)
  source.connect(audioContext.destination);
  // Play immediately
  source.start(0);
}