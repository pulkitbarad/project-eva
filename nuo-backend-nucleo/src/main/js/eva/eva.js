
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
const COMMUNICATION_TYPE_ERROR = -1;
const COMMUNICATION_TYPE_CHOICE = 2;
const COMMUNICATION_TYPE_LITERAL = 3;
const COMMUNICATION_TYPE_FIELD_SUGG = 5;
const COMMUNICATION_TYPE_UNARY_SUGG = 7;
const COMMUNICATION_TYPE_QUESTION_SUGG = 11;
const COMMUNICATION_TYPE_RELATIONSHIPS = 13;
const COMMUNICATION_TYPE_DEFAULT = 17;
const COMMUNICATION_TYPE_UPD_FIELD = 19;
const COMMUNICATION_TYPE_UPD_LITERAL = 23;
const COMMUNICATION_TYPE_UPD_QUESTION = 29;
const COMMUNICATION_TYPE_UPD_CRIT_FIELD = 31;
const COMMUNICATION_TYPE_UPD_SELECT_FIELD = 37;
const COMMUNICATION_TYPE_EXECUTION_STARTED = 41;
const COMMUNICATION_TYPE_GET_RESULT = 43;
const COMMUNICATION_TYPE_RESULT_AVAILABLE = 47;
const COMMUNICATION_TYPE_EXECUTION_RUNNING = 53;
const COMMUNICATION_TYPE_EXECUTION_FAILED = 59;
const COMMUNICATION_TYPE_MAPPING = 61;
const COMMUNICATION_TYPE_LOAD_INTO_TABLE = 67;

const LiteralSuggestionMessage = "Take the input literally.";
const FieldSuggestionMessage = "I have a suggestion for it.";
const UnarySuggestionMessage = "I have a suggestion for the expression.";
const QuestionSuggestionMessage = "I have a question that will explain it.";

const SaveResultSuggestionMessage = "Save the results with a name.";
const GetResultSuggestionMessage = "Get me the results.";


const systemFileLabel = "System File";
var activeCustomerFiles = [];
var activeEvaTables = [];
var activeProgressMonitors = [];

var loadingMessage = 
	{
		lineNumber: 0,
		messageTimeMillis: Date.now(),
		userName:"Eva",
		text:"Please wait while I load messages",
		isChart:false,
		metadata:[],
		data:[],
		topics:[]
	};

var emptyMessage = 
	{
		lineNumber: 0,
		messageTimeMillis: Date.now(),
		userName:"Eva",
		text:"No history available for selected topics. Please scroll down to load older messages or change the topic.",
		isChart:false,
		metadata:[],
		data:[],
		topics:[]
	};
	
var historyMessages = [loadingMessage];
	
var currHistoryStartLineNumber = 0;
var currHistoryEndLineNumber = 0;

var currRelMesssageFieldCount = 0;
var nuoEvaMessageHolder = {};
var nuoUserMessageHolder = {};
var nuoConversationResponse = null;
var isHistoryLoading = false;
var isHistorySaving = false;
var lastSaveMarker = 2;

function initUI() {
	
	loadChartLib(loadRestOfUI);
	$('html, body').animate({ scrollTop: 0 }, 'fast');
	// loadRestOfUI();
	
}

function loadChartLib(callback){
	
	// Load the Visualization API and the corechart package.
	google.charts.load("current", {"packages":["corechart","gantt","sankey","calendar","line","bar","map","scatter"]});

	// Set a callback to run when the Google Visualization API is loaded.
	google.charts.setOnLoadCallback(callback);
}

function loadRestOfUI(){
	
	$("#moveToTopButton").on("click", function(){
		$('html, body').animate({ scrollTop: 0 }, 'slow');
	});
	
	$("#customerFilesTable").hide();
	$("#customerFilePath").hide();

	//Files Handler START
	$("#evaFilesButton").on("click",evaFilesButtonHandler);
	$("#fileInput").on("change",uploadFileSeletionHandler);
	$("#customerFileDeleteButton").on("click",deleteFileSelectionHandler);
	$("#customerFileNewFolderButton").on("click",newFolderButtonHandler);
	$("#customerFileLoadTableButton").on("click",loadIntoTableFileSelectionHandler);
	$("#customerFileAnalyzeImageButton").on("click",analyzeImageFileSelectionHandler);
	addCustomerTableEventHandlers();
	//Files Handler END
	
	//Storage Explorer Handler START
	$("#evaTablesButton").on("click",evaTablesButtonHandler);
	$("#storageExplorerDeleteButton").on("click",deleteTableSelectionHandler);
	addStorageExplorerEventHandlers();
	//Storage Explorer Handler END
	
	//Progress Monitor Handler START
	$("#evaProgressButton").on("click",evaProgressButtonHandler);
	addProgressMonitorEventHandlers();
	//Progress Monitor Handler END

	addTopicButtonHandlers()
	
	$("#activeUsername").text("Welcome "+activeUsername+"!")
	
	if(!profileImageUrl || profileImageUrl.trim().length == 0){
		profileImageUrl = "./public/assets/images/unknown_user.png"
	} 
	var imageTag = "<label for='fileInputImage' class='fileInputImage'>";
	imageTag += "<img id='profileImageFile' width='128px' height='128px' src='"+ profileImageUrl+"'/>";
	imageTag += "</label>";
	$("#profileImage").append(imageTag);

	// var editIconUrl = "./public/assets/images/edit_image_24x24.png";
	// var editIconTag = "<div class='editImage'><i class='fa fa-pencil fa-lg'></i></div>"
	// $("#profileImage").append(editIconTag);
	
	$("#fileInputImage").on("change",profileImageSelectionHandler);

	$("#profileUserName").text(activeUsername);

	loadMessageContainer();
	renderHistorySection();

	initUserChoiceWindow();
	initUserSuggestionWindow();
	initEntityRelationshipWindow();
	initMapperWindow();
	initFileLoadOptionsWindow();
	initAnalyzeImagesOptionsWindow();

	// loadUserEntityPairWindow();
	readFromHistory();
	addHistoryScrollEvent();
	// $(document).ready(function () {
		// refreshMapperWindow();
	// })
	createPlatformMetrics();
}

function addTopicButtonHandlers(){
	
	$("#topicComboBox")
	.jqxComboBox(
		{ 
			source: historyTopics, 
			width: "100%", 
			height: "2.5em", 
			multiSelect: true,
			theme: jqWidgetThemeName
		}
	);
	// if(historyTopics.length > 0)
		// $("#topicComboBox").jqxComboBox('selectItem', historyTopics[0] ); 
	$("#topicComboBox").jqxComboBox({autoDropDownHeight: true});
	$('#topicComboBox').on('change', function (event){
		var args = event.args;
		if (args) {
			var item = args.item;
			// get item's label and value.
			var label = item.label;
			var value = item.value;
			var type = args.type; // keyboard, mouse or null depending on how the item was selected.
			renderHistorySection();
		}
	});
	$("#newTopicButton").on("click", function(){
		var topicName = prompt("What is the name of topic?", "New topic");
		if (topicName != null && topicName.trim().length > 0) {
			historyTopics.push(topicName);
		}
		$("#topicComboBox").jqxComboBox('addItem', topicName); 
		$("#topicComboBox").jqxComboBox("selectItem", topicName); 
	});
	
	$("#removeTopicButton").on("click", function(){
		var selectedTopics = getSelectedTopics();
		selectedTopics = selectedTopics.filter(function(topicName){
			
			if(topicName !== "Default"){
				$("#topicComboBox").jqxComboBox("unselectItem", topicName); 
				$("#topicComboBox").jqxComboBox("removeItem", topicName); 
				return true;
			}else 
				return false
		});
		historyTopics = 
			historyTopics.filter(function(topicName){
				return selectedTopics.indexOf(topicName) < 0;
			})
		// if(historyTopics.length > 0)
			// $("#topicComboBox").jqxComboBox("selectItem", historyTopics[0]); 
	});
	
}

function getSelectedTopics(){
	var selectedTopics = $("#topicComboBox").jqxComboBox('getSelectedItems').map(function(item){ return item.label});
	return selectedTopics;
}

function addHistoryScrollEvent(){
	
	$(window).scroll(function() {
		 if(!isHistoryLoading && $(window).scrollTop() + $(window).height() > $(document).height() - 10) {
				 if(currHistoryStartLineNumber > 1){
					 readFromHistory();
				 }
		 }
	});	
}


function loadSplitters(screenWidth,screenHeight){
	
	$("#mainContainer").jqxSplitter(
		{ 
			width: $(window).width()-20, 
			height: $(window).height()-20, 
			panels: [
				{ 
					size: "10%", 
					collapsible: true 
				},
				{ 
					size: "90%", 
					collapsible: false 
				}
			],
			theme: jqWidgetThemeName 
		}
	);
	
}

function loadMessageContainer(){
	
	$("#sendMessage").on("click", sendMessageHandler);
	$("#messageBox").keydown(function (e) {
		if (e.ctrlKey && e.keyCode == 13) {
			sendMessageHandler();
		}
	});
}

function initAnalyzeImagesOptionsWindow(){
	var offset = $(".historyBody").offset();
	
	$("#analyzeImageOptionsWindow").jqxWindow(
		{ width: 700,
			height: 400, 
			title: "How do you want to analyze the selected images?",
			resizable: true,
			autoOpen:	false,
			// okButton: $("#userChoiceConfirmButton"),
			// cancelButton: $("#userChoiceCancelButton"),
			position: { x: offset.left + 200, y: offset.top + 50},
			initContent: function () {
			},
			theme: jqWidgetThemeName
		}
	);
	$("#analyzeImageOptionsConfirmButton").on("click", analyzeImageOptionsConfirmHandler);
}

function analyzeImageFileSelectionHandler(){
	$("#analyzeImageOptionsWindow").jqxWindow("open");
}

function analyzeImageOptionsConfirmHandler(){


	var selectedIndices = $("#customerFilesTable").jqxGrid('getselectedrowindexes');
	
	if(selectedIndices && selectedIndices.length > 0){

		var filesToBeLoaded = [];
		selectedIndices
		.map(function(index){
			return $('#customerFilesTable').jqxGrid('getrowdata', index);
		})
		.forEach(function(rowData){

			var selectedFile = rowData.fileName;

			if(rowData.isDirectory){
				
				filesToBeLoaded =
					filesToBeLoaded.concat(
						activeCustomerFiles
						.filter(function(ele){
							var fileToBeLoaded = encodeURI(rowData.fileLabel)+"/";
							var parentPath = getParentFilePath();
							
							if(parentPath && parentPath.length > 0 && parentPath!=="/"){
								fileToBeLoaded = parentPath + fileToBeLoaded;
							}
							return ele.fileName.startsWith(fileToBeLoaded) && !ele.fileName.endsWith(systemFileLabel);
						})
						.map(function(ele){return ele.fileName})
					)
			}else{

				if(!selectedFile.endsWith(systemFileLabel)){
					filesToBeLoaded.push(selectedFile);
				}
			}
		});
		
		$('#customerFilesTable').jqxGrid('clearselection');
		
		var nuoAnalyzeImageOptions = {};
		
		nuoAnalyzeImageOptions.TargetTableName = $("#targetTableNameImage").val();
		nuoAnalyzeImageOptions.SourceFiles = filesToBeLoaded;
		nuoAnalyzeImageOptions.LanguageHints = $("#languageHint").val();
		nuoAnalyzeImageOptions.ShouldAppend = $("#shouldAppendImage").is(":checked");

		var fileList = 
			selectedIndices
			.map(function(index){
				var selectedFile = $('#customerFilesTable').jqxGrid('getrowdata', index);
				if(selectedFile.isDirectory)
						return selectedFile.fileLabel + "/*";
				else 
					return selectedFile.fileLabel;
			});
		var dateString = new Date() + "";
		dateString = dateString.substring(0,dateString.indexOf("("));
		nuoUserMessageHolder.Message = 
			"Analyze Images " + 
			fileList.map(function(ele){ return "<b>"+ele+"</b>"}).join(" AND ")+
			" and lod results into table <b>" + nuoAnalyzeImageOptions.TargetTableName+"</b>. <br><br>Note: This task has been created by Eva on your behalf on "+ dateString;
			
		nuoUserMessageHolder.NuoAnalyzeImageOptions = nuoAnalyzeImageOptions;
		nuoUserMessageHolder.CommunicationType = COMMUNICATION_TYPE_LOAD_INTO_TABLE;
		
		sendMessageToEva(true,true,true);
	}
	
	$("#fileLoadOptionsWindow").jqxWindow("close");
	
}


function initFileLoadOptionsWindow(){
	var offset = $(".historyBody").offset();
	
	$("#fileLoadOptionsWindow").jqxWindow(
		{ width: 700,
			height: 400, 
			title: "How do you want to load the selected files?",
			resizable: true,
			autoOpen:	false,
			// okButton: $("#userChoiceConfirmButton"),
			// cancelButton: $("#userChoiceCancelButton"),
			position: { x: offset.left + 200, y: offset.top + 50},
			initContent: function () {
			},
			theme: jqWidgetThemeName
		}
	);
	$("#fileLoadOptionsConfirmButton").on("click", fileLoadOptionsConfirmHandler);
}

function loadIntoTableFileSelectionHandler(){
	$("#fileLoadOptionsWindow").jqxWindow("open");
}

function fileLoadOptionsConfirmHandler(){


	var selectedIndices = $("#customerFilesTable").jqxGrid('getselectedrowindexes');
	
	if(selectedIndices && selectedIndices.length > 0){

		var filesToBeLoaded = [];
		selectedIndices
		.map(function(index){
			return $('#customerFilesTable').jqxGrid('getrowdata', index);
		})
		.forEach(function(rowData){

			var selectedFile = rowData.fileName;

			if(rowData.isDirectory){
				
				filesToBeLoaded =
					filesToBeLoaded.concat(
						activeCustomerFiles
						.filter(function(ele){
							var fileToBeLoaded = encodeURI(rowData.fileLabel)+"/";
							var parentPath = getParentFilePath();
							
							if(parentPath && parentPath.length > 0 && parentPath!=="/"){
								fileToBeLoaded = parentPath + fileToBeLoaded;
							}
							return ele.fileName.startsWith(fileToBeLoaded) && !ele.fileName.endsWith(systemFileLabel);
						})
						.map(function(ele){return ele.fileName})
					)
			}else{

				if(!selectedFile.endsWith(systemFileLabel)){
					filesToBeLoaded.push(selectedFile);
				}
			}
		});
		
		$('#customerFilesTable').jqxGrid('clearselection');
		
		var nuoFileLoadOptions = {};
		
		nuoFileLoadOptions.TargetTableName = $("#targetTableName").val();
		nuoFileLoadOptions.SourceFiles = filesToBeLoaded;
		nuoFileLoadOptions.FileFormat = $("#fileFormat").val();
		nuoFileLoadOptions.Delimiter = $("#delimiter").val();
		nuoFileLoadOptions.QuoteCharacter = $("#quoteCharacter").val();
		nuoFileLoadOptions.ShouldAppend = $("#shouldAppend").is(":checked");
		nuoFileLoadOptions.RowsToSkip = parseInt($("#rowsToSkip").val());
		if(!nuoFileLoadOptions.RowsToSkip){
			nuoFileLoadOptions.RowsToSkip = 0;
			
		}

		var fileList = 
			selectedIndices
			.map(function(index){
				var selectedFile = $('#customerFilesTable').jqxGrid('getrowdata', index);
				if(selectedFile.isDirectory)
						return selectedFile.fileLabel + "/*";
				else 
					return selectedFile.fileLabel;
			});
		var dateString = new Date() + "";
		dateString = dateString.substring(0,dateString.indexOf("("));
		nuoUserMessageHolder.Message = 
			"Load files " + 
			fileList.map(function(ele){ return "<b>"+ele+"</b>"}).join(" AND ")+
			" into table <b>" + nuoFileLoadOptions.TargetTableName+"</b>. <br><br>Note: This task has been created by Eva on your behalf on "+ dateString;
			
		nuoUserMessageHolder.NuoFileLoadOptions = nuoFileLoadOptions;
		nuoUserMessageHolder.CommunicationType = COMMUNICATION_TYPE_LOAD_INTO_TABLE;
		
		sendMessageToEva(true,true,true);
	}
	
	$("#fileLoadOptionsWindow").jqxWindow("close");
	
}


function initUserChoiceWindow(){
	var offset = $(".historyBody").offset();
	
	$("#userChoiceWindow").jqxWindow(
		{ width: 700,
			height: 400, 
			title: "Please make the appropriate choice",
			resizable: true,
			autoOpen:	false,
			// okButton: $("#userChoiceConfirmButton"),
			// cancelButton: $("#userChoiceCancelButton"),
			position: { x: offset.left + 200, y: offset.top + 50},
			initContent: function () {
			},
			theme: jqWidgetThemeName
		}
	);
	$("#userChoiceListBox").jqxListBox(
		{ 
			source: [], 
			multiple: false, 
			width: 400, 
			height: 300,
			theme: jqWidgetThemeName
		}
	);

	$("#userChoiceListBox").on("dblclick",userChoiceConfirmationHandler);
}

function initUserSuggestionWindow(){
	var offset = $(".historyBody").offset();
	
	$("#userSuggestionWindow").jqxWindow(
		{ width: 700,
			height: 400, 
			resizable: true,
			autoOpen:	false,
			title: "Please make a suggestion",
			okButton: $("#userSuggConfirmButton"),
			// cancelButton: $("#userSuggCancelButton"),
			position: { x: offset.left + 200, y: offset.top + 50},
			initContent: function () {
			},
			theme: jqWidgetThemeName
		}
	);
	$("#userSuggestionTextBox1").jqxTextArea({placeHolder: "Type your suggestion here...", height: 100, width: 500, minLength: 1,  source: [], theme: jqWidgetThemeName });
	$("#userSuggestionTextBox2").jqxTextArea({placeHolder: "Type your suggestion here...", height: 100, width: 500, minLength: 1,  source: [], theme: jqWidgetThemeName });

	$("#userSuggConfirmButton").jqxButton({ width: "80px", disabled: false, theme: jqWidgetThemeName });
	$("#userSuggConfirmButton").on("click",userSuggConfirmationHandler);
}

function userChoiceConfirmationHandler(){
	updUserSuggWindowElements();
	$("#userChoiceWindow").jqxWindow("close");
}

function initEntityRelationshipWindow(){
	var offset = $(".historyBody").offset();
	$("#entityRelationshipWindow").jqxWindow(
		{ width: 700,
			height: 400, 
			resizable: true,
			autoOpen:	false,
			// cancelButton: $("#storageEntitiesCloseButton"),
			position: { x: offset.left + 150, y: offset.top + 25},
			initContent: function () {
			},
			theme: jqWidgetThemeName
		}
	);
	$("#entityRelationshipWindow").jqxWindow("setTitle","Explain relationship between tables");
	loadStorageEntitiesTree();
}

function initMapperWindow(){
	var offset = $(".historyBody").offset();
	$("#mapperWindow").jqxWindow(
		{ width: "60em",
			height: "30em", 
			// resizable: true,
			autoOpen:	false,
			showCloseButton: true,
			// cancelButton: $("#storageEntitiesCloseButton"),
			position: { x: offset.left + 150, y: offset.top + 25},
			initContent: function () {
			},
			theme: jqWidgetThemeName
		}
	);
	var leftSource = [
			"LeftItem1",
			"LeftItem2",
			"LeftItem3",
			"LeftItem4"
		]
	var rightSource = [
			"RightItem1",
			"RightItem2",
			"RightItem3",
			"RightItem4"
		]
	// $("#mapperLeftPane").jqxListBox({ source: leftSource, width: 800, height: 800 });
	// $("#mapperRightPane").jqxListBox({ source: rightSource, width: 800, height: 800 });
	$("#mapperWindow").jqxWindow("setTitle","Explain the mapping between source and target columns.");
	$("#mapperWindow").jqxWindow({ showCloseButton: true }); 
	$("#confirmMapperButton").on("click", function(){
		
		nuoUserMessageHolder.Responses = 
			getMapperConnections();
		nuoUserMessageHolder.Message = 
			nuoUserMessageHolder.Responses
			.map(function(ele){
				return ele.replace("|~|","->");
			})
			.join(" <i>AND</i> ");
		$("#mapperWindow").jqxWindow("close");
		sendMessageToEva(false,false,true);
		
	});
}

function loadConnections(){
	// $(document).ready(function(){
		
		jsPlumb.ready(function() {
			
			jsPlumbRef = jsPlumb.getInstance();
			
			jsPlumbRef.importDefaults({
				Connector : [ "Bezier", { margin: 20, curviness: 150}],
				Anchors : [ "TopCenter", "BottomCenter" ],
				ConnectionsDetachable:true
			})
			jsPlumbRef.setContainer("mapperBodyContainer");
			$("#mapperLeftPane").scroll(function() {
					jsPlumbRef.repaintEverything();
			});
			
			$("#mapperRightPane").scroll(function() {
					jsPlumbRef.repaintEverything();
			});
			
			var sourceEndpointOptions = {
				endpoint:"Dot",
				paintStyle: { 
					radius: 8, 
					fill: 'var(--nuo-orange-solid)',
					opacity: 0.5
				},
				isSource: true,
				connectorStyle: { stroke:"#666" },
				maxConnections: -1
			};
			
			var targetEndpointOptions = {
				endpoint: sourceEndpointOptions.endpoint,
				paintStyle:{ 
					radius: sourceEndpointOptions.paintStyle.radius,
					fill: 'var(--nuo-blue-solid)', 
					opacity: sourceEndpointOptions.paintStyle.opacity
				},
				connectorStyle : sourceEndpointOptions.connectorStyle,
				isTarget:true,
				maxConnections: -1
			};
			
			var counter = 1;
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
			
			for(var i=0; i < sourceList.length; i++){
			
				var label = sourceList[i].label;
				var elementId = 'leftItem'+i;
				var leftElement = "<div id='"+elementId+"' class='mapperItem'>"+label+"</div>";
				
				$("#mapperLeftPane").append(leftElement);

				var endpointLeft = jsPlumbRef.makeSource(elementId, {anchor:"Right"},sourceEndpointOptions);
			}
			
			for(var i=0; i < targetList.length; i++){
				
				var label = targetList[i].label;
				var elementId = 'rightItem'+i;
				var rightElement = "<div id='"+elementId+"' class='mapperItem'>"+label+"</div>";
				
				$("#mapperRightPane").append(rightElement);
				$("#"+elementId).dblclick(function(data){
					var divId = data.target.id;
					var conns = jsPlumbRef.getConnections({target: divId });
					for (var i = 0; i < conns.length; i++)
						 jsPlumbRef.deleteConnection(conns[i]);

					jsPlumbRef.removeAllEndpoints(divId);
				});

				var endpointRight = jsPlumbRef.makeTarget(elementId, {anchor:"Left"},targetEndpointOptions);
				
				var connection = jsPlumbRef.connect({
					source: "leftItem"+targetList[i].inputIndex, 
					target: "rightItem"+i
				});

			}
			jsPlumbRef.bind('beforeDrop', function (e) {
				
				var elementId = e.targetId 
				var conns = jsPlumbRef.getConnections({target: e.targetId });
				// conns = conns.concat(jsPlumbRef.getConnections({source: e.sourceId }));
				for (var i = 0; i < conns.length; i++)
					 jsPlumbRef.deleteConnection(conns[i]);

				return true;
			});
		})
	// })
}

function getMapperConnections(){

	var mapping = 
		jsPlumbRef
		.getConnections()
		.map(function(c){
			return c.source.outerText+"|~|"+c.target.outerText;
		});
	return mapping;
}

function loadStorageEntitiesTree(){
	$("#storageEntitiesTree").jqxTree(
		{ 
			width: "95%", 
			height: "95%",
			toggleMode: "click",
			theme: jqWidgetThemeName
		}
	);

	$("#entityRelationshipListBox").jqxListBox(
		{ 
			multiple: false, 
			width: "95%", 
			height: "95%",
			theme: jqWidgetThemeName
		}
	);

	$(document).on("dblclick", "#storageEntitiesTree  .jqx-tree-item",function(event) {
		var _item = event.target;
		if (_item.tagName != "LI") {
				_item = $(_item).parents("li:first");
		};
		var item = $("#storageEntitiesTree").jqxTree("getItem", _item[0]);
		var field = item.value;
		if(field){
			var messageContent = $("#messageBox").val();
			var sep = "";
			
			if( nuoUserMessageHolder.CommunicationType != COMMUNICATION_TYPE_RELATIONSHIPS){

				if(messageContent.length == 0 );
				else sep = ", ";
				$("#messageBox").val(messageContent+sep+field.FieldName+" from "+field.EntityName);
			
			}else{
				if(currRelMesssageFieldCount % 2 == 0){
				
					nuoUserMessageHolder.NuoCommonFields.push({
						leftField:field,
						rightFields:[]
					});
				}else {
					nuoUserMessageHolder
					.NuoCommonFields[nuoUserMessageHolder.NuoCommonFields.length - 1]
					.rightFields
					.push(field);
				}
				currRelMesssageFieldCount+=1;
				refreshEntityRelationshipListBox();
				$("#storageEntitiesTree").jqxTree("collapseAll");
			} 
		}
	});
	
	$("#confirmRelationshipButton").on("click", function(){
		currRelMesssageFieldCount = 0;
		nuoUserMessageHolder.Message = 
			$("#entityRelationshipListBox")
			.jqxListBox("getItems")
			.map(function(jqItem){
				return jqItem.html;
			})
			.join(" <i>AND</i> ");
		$("#entityRelationshipWindow").jqxWindow("close");
		sendMessageToEva(false,false,true);
	});
	
	$("#redoRelationshipButton").on("click", function(){
		currRelMesssageFieldCount = 0
		nuoUserMessageHolder.NuoCommonFields = [];
		refreshEntityRelationshipListBox();
	});
	
}

function refreshEntityRelationshipWindow(){
	refreshStorageEntitiesTree();
	refreshEntityRelationshipListBox();
	$("#entityRelationshipMsg").html(nuoEvaMessageHolder.Message);
	$("#entityRelationshipWindow").jqxWindow("open");
}

function refreshMapperWindow(){
	$("#mapperMsg").html(nuoEvaMessageHolder.Message);
	$("#mapperWindow").jqxWindow("open");
	$("#mapperWindow").jqxWindow("focus");
	loadConnections();
}

function refreshStorageEntitiesTree(){
	$("#storageEntitiesTree").jqxTree(
		{
			source: 
				nuoEvaMessageHolder
				.NuoEntities
				.map(function(ele){
					return {
						label: ele.EntityName,
						expanded: false,
						items: 
							ele.Fields
							.map(function(field){
								return {
									label:field.FieldName,
									value:field
								}
							})
					}
				})
				.sort(function(l, r){
					if(l.label < r.label) return -1;
					if(l.label > r.label) return 1;
					return 0;
				})
		}
	);
	$("#entityRelationshipListBox").jqxListBox(
		{ 
			source: []
		}
	);
}

function refreshEntityRelationshipListBox(){
	
	var listBoxSource = nuoUserMessageHolder
		.NuoCommonFields
		.map(function(commonField){
			
			var leftField = commonField.leftField;
			var item = "<b>" + leftField.FieldName + "</b> of <u>" + leftField.EntityName + "</u> = <b>";
				
			if(commonField.rightFields && commonField.rightFields.length > 0){
			var rightField = commonField.rightFields[0];
				item += rightField.FieldName + "</b> of <u>" + rightField.EntityName + "</u>";
			}
			return {
				html:item
			};
		});
	$("#entityRelationshipListBox").jqxListBox(
		{ 
			source: listBoxSource, 
		}
	);
}

function sendMessageHandler() {
	
	var userMsg = $("#messageBox").val();
	
	if(userMsg && userMsg.length>0){
		
		nuoUserMessageHolder.Message = userMsg;

		if(nuoUserMessageHolder.CommunicationType % COMMUNICATION_TYPE_RELATIONSHIPS == 0){

			nuoUserMessageHolder.Responses = nuoUserMessageHolder.Message.split(new RegExp("and","i"));
			currRelMesssageFieldCount = 0
			sendMessageToEva(false,false,true);
		}else{
			sendMessageToEva(true,true,true);
		}
	}
}

function sendMessageToEva(isFreshQuestion,appendToHistory,isExplicitCall){
	
	if(appendToHistory){
		
		addMessageToHistory({
			userName: activeUsername,
			text: nuoUserMessageHolder.Message,
			isChart:false
		});
	}

	if(isFreshQuestion){
		
		nuoUserMessageHolder.CommunicationType = COMMUNICATION_TYPE_DEFAULT;
		nuoUserMessageHolder.QuestionAlias = "Result_"+(currHistoryEndLineNumber);
		nuoUserMessageHolder.QuestionText = nuoUserMessageHolder.Message;
	}
	// if(isExplicitCall)
		// $("#ajaxLoader").jqxLoader("open");
	var requestType = "rt47";
	if(nuoUserMessageHolder.NuoFileLoadOptions){
		requestType = "rt101";
	}

	if(nuoUserMessageHolder.NuoAnalyzeImageOptions){
		requestType = "rt127";
	}

	directCall(
		requestType,
		"NuoUserMessage",
		nuoUserMessageHolder
	)
	.done(function(data){
		
		// if(isExplicitCall)
			// $("#ajaxLoader").jqxLoader("close");
		
		if(data.StatusCode && data.StatusCode === 200){

			nuoUserMessageHolder = {};
			nuoEvaMessageHolder = {};
			nuoConversationResponse = data.Content;
			
			if(nuoConversationResponse.NuoEvaMessage && nuoConversationResponse.NuoEvaMessage.CommunicationType){
				handleResponseFromEva(nuoConversationResponse);
			}else{
				console.log("Something went wrong.");
				console.log(JSON.stringify(nuoConversationResponse));
			}
		}else{
			//window.location = 'https://eva.nuocanvas.ai';
		}		
	});
}

function sleep(ms) {
	var promise = new Promise(function(resolve){
		setTimeout(resolve, ms);
	});
  return promise;
}

  function handleResponseFromEva(nuoConversationResponse){
	
	nuoEvaMessageHolder = nuoConversationResponse.NuoEvaMessage;
	passInfoToUserMessage();

	if(nuoEvaMessageHolder.CommunicationType === COMMUNICATION_TYPE_ERROR){
		
		addMessageToHistory({
			userName: "Eva",
			text: nuoEvaMessageHolder.Message,
			isChart: false
		});
	
	}else if(nuoEvaMessageHolder.CommunicationType === COMMUNICATION_TYPE_EXECUTION_RUNNING){
		
		var sleepTime;
		if(nuoEvaMessageHolder.NuoPollingDetails){
			if(nuoEvaMessageHolder.NuoPollingDetails.EndTimeMillis
				&& nuoEvaMessageHolder.NuoPollingDetails.EndTimeMillis <= Date.now()){
				
				console.log("Time out occured");
				addMessageToHistory({
					userName: "Eva",
					text: "TIME_OUT: I could not get result within maxium allowed time. Please contact the NuoCanvas Support team at support@nuocanvas.com",
					isChart: false
				});
			}else if(nuoEvaMessageHolder.NuoPollingDetails.StartTimeMillis
				&& nuoEvaMessageHolder.NuoPollingDetails.StartTimeMillis > Date.now()){
				
				sleepTime = Date.now() - nuoEvaMessageHolder.NuoPollingDetails.StartTimeMillis;
			}else{
				sleepTime = nuoEvaMessageHolder.NuoPollingDetails.PollingIntervalMillis;
			}
		}else{
			sleepTime = 3000;
		}
		
		sleepTime = nuoEvaMessageHolder.NuoPollingDetails.PollingIntervalMillis;
		console.log("Going to sleep for "+  sleepTime +" before sending message to Eva");
		  sleep(sleepTime);
		console.log("Awake now and sending message to Eva");
		sendMessageToEva(false,false,false)
		
	}else if(nuoEvaMessageHolder.CommunicationType === COMMUNICATION_TYPE_RESULT_AVAILABLE){
		
		addMessageToHistory({
			userName: "Eva",
			text: nuoEvaMessageHolder.QuestionText,
			isChart: true
		});
	
	}else if(nuoEvaMessageHolder.CommunicationType == COMMUNICATION_TYPE_RELATIONSHIPS){
		
		refreshEntityRelationshipWindow();
	}else if(nuoEvaMessageHolder.CommunicationType == COMMUNICATION_TYPE_MAPPING){
		
		refreshMapperWindow();
	}else{
		getUserChoice();
	}
}

function passInfoToUserMessage(){

	nuoUserMessageHolder.QuestionAlias = nuoEvaMessageHolder.QuestionAlias;
	nuoUserMessageHolder.QuestionText = nuoEvaMessageHolder.QuestionText;
	nuoUserMessageHolder.QuestionHash = nuoEvaMessageHolder.QuestionHash;
	nuoUserMessageHolder.RuleText = nuoEvaMessageHolder.RuleText;
	nuoUserMessageHolder.CommunicationType = nuoEvaMessageHolder.CommunicationType;
	nuoUserMessageHolder.NuoCommonFields = [];
	nuoUserMessageHolder.NuoFileLoadOptions = nuoEvaMessageHolder.NuoFileLoadOptions;
	nuoUserMessageHolder.NuoAnalyzeImageOptions = nuoEvaMessageHolder.NuoAnalyzeImageOptions;
}

function getUserChoice(){
	
	var communicationType = nuoEvaMessageHolder.CommunicationType;
	var listBoxSource = 
			nuoEvaMessageHolder
			.NuoUserChoices
			.map(function(ele){ return ele.ChoiceText;});
	
	var evaMessageContent = nuoEvaMessageHolder.Message;
	
	if(communicationType % COMMUNICATION_TYPE_LITERAL == 0){
		listBoxSource.push(LiteralSuggestionMessage);
	}
	
	if(communicationType % COMMUNICATION_TYPE_FIELD_SUGG == 0){
		listBoxSource.push(FieldSuggestionMessage);
	}
	
	if(communicationType % COMMUNICATION_TYPE_UNARY_SUGG == 0){
		listBoxSource.push(UnarySuggestionMessage);
	}
	
	if(communicationType % COMMUNICATION_TYPE_QUESTION_SUGG == 0 ){
		listBoxSource.push(QuestionSuggestionMessage);
	}

	if(communicationType % COMMUNICATION_TYPE_EXECUTION_STARTED == 0 ){
		listBoxSource=[];
		listBoxSource.push(SaveResultSuggestionMessage);
		listBoxSource.push(GetResultSuggestionMessage);

		evaMessageContent =
			evaMessageContent.replace(new RegExp("<br>", "g"),"");
			// + " The cost to answer your question was <mark class='markCost'>" 
			// + formateCurrency(nuoEvaMessageHolder.RuleText) 
			// +"</mark>";
	}
	
	addMessageToHistory({
		userName: "Eva",
		text: evaMessageContent,
		isChart: false
	});
	$("#userChoiceListBox").jqxListBox(
		{ 
			source: listBoxSource
		}
	);
	
	
	$("#userChoiceMessage").html(nuoEvaMessageHolder.Message);
	$("#userChoiceWindow").jqxWindow("open");
}

function updUserSuggWindowElements(){
			
	var selectedChoices = $("#userChoiceListBox").jqxListBox("getSelectedItems");
	var allChoices = $("#userChoiceListBox").jqxListBox("getItems");

	var communicationType = -1;
	if(selectedChoices.length > 0){
		communicationType = getCommunicationType(selectedChoices[0].label);
	}
	
	nuoUserMessageHolder.CommunicationType = communicationType;
	var userSuggMsgHtml = null;
	
	if(communicationType == COMMUNICATION_TYPE_GET_RESULT){

		nuoUserMessageHolder.Message  = selectedChoices[0].label;
		if(nuoUserMessageHolder.Message === SaveResultSuggestionMessage){
			
			nuoUserMessageHolder.Message  = "Save the result as <mark>"+nuoUserMessageHolder.QuestionAlias+"</mark> and get me  the results.";
		
			$("#userSuggestionTextBox2").hide();
			$("#userSuggestionTextBox1").val("For example, Preferred_Customer_Analysis_Result.");
			userSuggMsgHtml = "Please name this result so that you can ask questions as if it were a table.</i>";
			$("#userSuggestionWindow").jqxWindow("setTitle",nuoEvaMessageHolder.Message);
			
		}else if(nuoUserMessageHolder.Message === GetResultSuggestionMessage){
			
			sendMessageToEva(false,false,true);
		}
		
	}else if(communicationType == COMMUNICATION_TYPE_FIELD_SUGG){
		
		$("#userSuggestionTextBox2").hide();
		$("#userSuggestionTextBox1").val("For example, name of Customer.");
		userSuggMsgHtml = nuoEvaMessageHolder.Message;
		$("#userSuggestionWindow").jqxWindow("setTitle","What is your suggestion for the column?");
	
	} else	if(communicationType == COMMUNICATION_TYPE_UNARY_SUGG){
		
		$("#userSuggestionTextBox2").show();
		$("#userSuggestionTextBox1").val("For example, name of Customer.");
		$("#userSuggestionTextBox2").val("For example, Eva.");
		$("#userSuggestionWindow").jqxWindow("setTitle","What is your suggestion for the expression?");
		
		userSuggMsgHtml = nuoEvaMessageHolder.Message
			+"Please note, expression consist of two parts as following"
			+"<br><br><i>name of column</i> <br><span>     =<br><i> filter on that column</i></span><br><br>";
			
	
	} else if(communicationType == COMMUNICATION_TYPE_QUESTION_SUGG){

		$("#userSuggestionTextBox2").hide();
		$("#userSuggestionTextBox1").val("For example, name of companies with revenue above 10K");
		userSuggMsgHtml = nuoEvaMessageHolder.Message;
		$("#userSuggestionWindow").jqxWindow("setTitle","What question does explain the column?");
	
	}else if(communicationType == COMMUNICATION_TYPE_LITERAL){
		
		nuoUserMessageHolder.Responses =  [nuoEvaMessageHolder.RuleText];
		nuoUserMessageHolder.Message = selectedChoices[0].label;
		sendMessageToEva(false,false,true);

	}else{
		var response = nuoEvaMessageHolder.NuoUserChoices.find(function(userChoice){
				return userChoice.ChoiceText === selectedChoices[0].label;
			})
			.ChoiceIndex;
		nuoUserMessageHolder["Responses"] =  [response];
		nuoUserMessageHolder.Message  = selectedChoices[0].label;
		sendMessageToEva(false,false,true);
	}
	if(userSuggMsgHtml){
		$("#userSuggestionMsg").html(userSuggMsgHtml);
		$("#userSuggestionWindow").jqxWindow("open");
	}
}

function userSuggConfirmationHandler(){
		
	if(nuoUserMessageHolder.CommunicationType === COMMUNICATION_TYPE_UNARY_SUGG){
	
		nuoUserMessageHolder.Responses =  [$("#userSuggestionTextBox1").val(),$("#userSuggestionTextBox2").val()];
		nuoUserMessageHolder.Message  = nuoUserMessageHolder.Responses.join(" = ");
	
	}else if(nuoUserMessageHolder.CommunicationType === COMMUNICATION_TYPE_GET_RESULT){
		
		nuoUserMessageHolder.ResultTableName  = $("#userSuggestionTextBox1").val();
		nuoUserMessageHolder.Message  = "Save the result as <mark>"+nuoUserMessageHolder.QuestionAlias+"</mark> and get the results.";

	}else{
		nuoUserMessageHolder.Responses =  [$("#userSuggestionTextBox1").val()];
		nuoUserMessageHolder.Message  = nuoUserMessageHolder.Responses[0];
	}
	sendMessageToEva(false,false,true);
		
	$("#userSuggestionWindow").jqxWindow("close");
}

function addMessageToHistory(messageObject){
	
	currHistoryEndLineNumber +=1;
	messageObject.lineNumber = currHistoryEndLineNumber;
	messageObject.topics = getSelectedTopics();
	messageObject.messageTimeMillis = Date.now();
	historyMessages.unshift(messageObject);
	addRowToHistoryFeed(messageObject);
	$("#messageBox").val("");
		
	if(messageObject.isChart){
		
		if(nuoConversationResponse.NuoConversationResult){
			
			messageObject.Result = {
				data: nuoConversationResponse.NuoConversationResult.Data,
				metadata:nuoConversationResponse.NuoConversationResult.Metadata
			};
		}
		if(nuoConversationResponse.NuoConversationProfilingResult){
			messageObject.ProfilingResult = {
				data: nuoConversationResponse.NuoConversationProfilingResult.Data,
				metadata: nuoConversationResponse.NuoConversationProfilingResult.Metadata
			};
		}
		messageObject.chartType = CHART_TYPE_AREA;
		drawMessageChart(messageObject);
		saveToHistory();
		renderHistorySection();
	}else{
		
		messageObject.Result = {
			data:[],
			metadata:[],
		};
		messageObject.ProfilingResult = {
			data:[],
			metadata:[]
		};
	}
}

function drawMessageChart(messageObject){

	if(messageObject.Result && messageObject.Result.data && messageObject.Result.metadata){

		var chartTypeComboBoxId = "#historyChartComboBox" + messageObject.lineNumber;
		$(chartTypeComboBoxId)
		.jqxComboBox(
			{ 
				source: ChartTypes, 
				width: 150, 
				height: "98%", 
				selectedIndex: 0,
				theme: jqWidgetThemeName
			}
		);
		$(chartTypeComboBoxId).on("select", function (event) {
			var args = event.args;
			if (args != undefined) {
				var item = event.args.item;
				if (item != null) {
					var comboBoxId = $(this).attr("id");;
					var lineNum = comboBoxId[comboBoxId.length-1];
					messageObject.chartType = item.label;
					drawChart(
						"historyMessageChart" + messageObject.lineNumber, 
						messageObject.chartType, 
						messageObject.text,
						messageObject.Result.data, 
						messageObject.Result.metadata
					);
				}
			}
		});
	}
	$("#historyMessageTabs" + messageObject.lineNumber)
	.jqxTabs(
		{ 
			width: "100%", 
			height: "100%",
			selectionTracker: true,
			animationType: "fade",
			theme: jqWidgetThemeName
		}
	);

	if(messageObject.Result && messageObject.Result.data && messageObject.Result.metadata){
		
		drawChart(
			"historyMessageDataGrid" + messageObject.lineNumber, 
			CHART_TYPE_DATA_TABLE, 
			messageObject.text,
			messageObject.Result.data, 
			messageObject.Result.metadata
		);
		$(chartTypeComboBoxId).jqxComboBox("selectItem",messageObject.chartType);
	}else{
		$("#historyMessageDataGrid" + messageObject.lineNumber).html("<i>No chart available for this response.</i>")
	}
	
	if(messageObject.ProfilingResult && messageObject.ProfilingResult.data && messageObject.ProfilingResult.metadata){
		
		drawChart(
			"historyMessageProfilingGrid" + messageObject.lineNumber, 
			CHART_TYPE_DATA_TABLE, 
			messageObject.text,
			messageObject.ProfilingResult.data, 
			messageObject.ProfilingResult.metadata
		);
	}else{
		$("#historyMessageProfilingGrid" + messageObject.lineNumber).html("<i>No profiling results available for this response.</i>")
	}
}

function getCommunicationType(selectedChoice){
	
	var userInputType = -1;
	
	if(selectedChoice == LiteralSuggestionMessage ){
		 userInputType = COMMUNICATION_TYPE_LITERAL;
	}else if(selectedChoice == FieldSuggestionMessage ){
		 userInputType = COMMUNICATION_TYPE_FIELD_SUGG;
	}else if(selectedChoice == UnarySuggestionMessage ){
		 userInputType = COMMUNICATION_TYPE_UNARY_SUGG;
	}else if(selectedChoice == QuestionSuggestionMessage ){
		 userInputType = COMMUNICATION_TYPE_QUESTION_SUGG;
	}else if(selectedChoice == SaveResultSuggestionMessage || selectedChoice == GetResultSuggestionMessage){
		 userInputType = COMMUNICATION_TYPE_GET_RESULT;
	}else{
		 userInputType = COMMUNICATION_TYPE_CHOICE;
	}
	return userInputType;
}

function loadIndexTree(){

	$("#indexTree").jqxTree({ height: "100%", width: "100%",theme: jqWidgetThemeName });
	var data  = [
			{
				label:"Sales",
				expanded:true,
				items:[
					{
						label:"Inside"
					}
				]
				
			},
			{
				label:"Marketing",
				expanded:true,
				items:[
					{
						label:"Online",
						items:[
							{
								label:"Facebook"
							}
						]
					},
					{
						label:"Media"
					}
				]
			},
			{
				label:"Manufacturing"
			}
		];
  $("#indexTree").jqxTree({ source: data });
  $("#indexTree")
	.jqxTree(
    "selectItem",
    $("#indexTree").find("li:first")[0]
  );
}

function addRowToHistoryFeed(historyMessage){
	
	$("#historyBody")
	.prepend(getHistoryRowTag(historyMessage));
	
	if(!historyMessage.isChart) {
		saveToHistory();
		renderHistorySection();
	}
}

function renderHistorySection(){
	
	var messagesToBeRendered = historyMessages;
	var selectedTopics = getSelectedTopics();
	if(selectedTopics.length > 0 && historyMessages[0].lineNumber > 0){
		messagesToBeRendered = 
			messagesToBeRendered
				.filter(function(message){
					var count = 
						selectedTopics
							.map(function(topicName){
								if(message.topics.indexOf(topicName) < 0)
									return 1;
								else 
									return 0;
							})
							.reduce(function(acc, val){
								return acc + val; 
							});
					return count == 0;
				})
	}
	if(messagesToBeRendered.length == 0){
		messagesToBeRendered.push(emptyMessage);
	}
	$("#historyBody").html(
		messagesToBeRendered
			.map(function(historyMessage){ 
				return getHistoryRowTag(historyMessage);
			})
			.join("\n")		
	);
	
	messagesToBeRendered
	.forEach(function(historyMessage){ 
		if(historyMessage.isChart) drawMessageChart(historyMessage);
	});
}

function getHistoryRowTag(historyMessage){
	
	var lineNum = historyMessage.lineNumber;
	var messageTimeMillis = historyMessage.messageTimeMillis;
	
	var rowContentCssClass = "";
	
	if(historyMessage.isChart===true){
		rowContentCssClass = "chartRow";
	}else{
		rowContentCssClass = "textRow";
	}
	var rowDiv = "<div class = 'historyRow "+rowContentCssClass+"'>";
	
	var lineNumDiv = "<div id='historyLineNumberContainer"+lineNum+"' class='historyLineNumberContainer'>";
	lineNumDiv += "<div id='historyLineNumber"+lineNum+"' class='historyLineNumber'>"+lineNum+"</div>";
	lineNumDiv += "</div>";
	var historyMsgContentDiv = "<div id='historyMessageContainer"+lineNum+"' class='historyMessageContainer containerOuter' style='500px'>";

	var messageHeaderDiv = "<div id='historyMessageHeader"+lineNum+"' class ='historyMessageHeader'>";

	var	imgurl = "./public/assets/images/Eva_512x512.png";
	if(historyMessage.userName === activeUsername)
		imgurl = profileImageUrl;
		
	var imageTag = "<img width='48px' height='48px' src='"+ imgurl+"'/>";
	var userImageDiv = "<div id='historyUserImage"+lineNum+"' class='historyUserImage'>"+imageTag+"</div>";

	var userNameAndTimeDiv = "<div id='historyUserNameAndTime"+lineNum+"' class='historyUserNameAndTime'>";
	var userNameDiv = "<div id='historyUserName"+lineNum+"' class='historyUserName'>"+ historyMessage.userName + "</div>";

	var timeDiv = "<div id='historyMessageTime"+lineNum+"' class='historyMessageTime'>"+ timeSince(messageTimeMillis) + "</div>";
	userNameAndTimeDiv += userNameDiv;
	userNameAndTimeDiv += timeDiv;
	userNameAndTimeDiv +="</div>"
	
	var topicCounter = 0;
	var messageTopicsDiv = 
		"<div id='historyMessageTopicContainer"+lineNum+"' class='historyMessageTopicContainer'>" + 
			historyMessage
				.topics
				.map(function(topicName){
					
					topicCounter+=1;
					return "<div id='historyMessageTopic"+topicCounter+"' class='historyMessageTopic'>"+topicName+"</div>"
				})
				.join("") + 
		"</div>";
	
	messageHeaderDiv += userImageDiv;
	messageHeaderDiv += userNameAndTimeDiv;
	messageHeaderDiv += messageTopicsDiv;
	messageHeaderDiv += "</div>";

	// var bgClass = "oddMsgBackground";
	// if(lineNum % 2==0){
		// bgClass = "evenMsgBackground";
	// }
	var messageInfoDiv = "<div id='historyMessageInfo"+lineNum+"' class='historyMessageInfo "+rowContentCssClass+"'>";
	var addOnCssClass = "";
	if(historyMessage.isChart===true){
		
		
		messageInfoDiv += "<div id='historyMessageTabs"+lineNum+"' class='historyMessageTabs'>";
			messageInfoDiv += "<ul><li>Visualization</li><li>Data</li><li>Profiling Results</li></ul>";
			messageInfoDiv += "<div id='historyMessageChartContainer"+lineNum+"' class='historyMessageChartContainer'>";
				messageInfoDiv += "<div id='historyMessageToolbar"+lineNum+"' class='historyMessageToolbar'>";
					messageInfoDiv += "<div id='historyChartComboBox"+lineNum+"' class='historyChartComboBox'></div>";
				messageInfoDiv += "</div>";
				messageInfoDiv += "<div id='historyMessageChart"+lineNum+"' class='historyMessageChart'></div>";
			messageInfoDiv += "</div>";
			messageInfoDiv += "<div id='historyMessageDataGrid"+lineNum+"' class='historyMessageDataGrid'></div>";
			messageInfoDiv += "<div id='historyMessageProfilingGrid"+lineNum+"' class='historyMessageProfilingGrid'></div>";
		messageInfoDiv += "</div>";
	}else{
		messageInfoDiv += "<div id='historyMessageContent"+lineNum+"' class='historyMessageContent containerInner'>"+ historyMessage.text +"</div>";
	}
	messageInfoDiv += "</div>";

	// var messageFooterDiv = "";
	// if(historyMessage.isChart===true){
		// messageFooterDiv += "<div id='historyMessageFooter"+lineNum+"' class='historyMessageFooter'>";
		// messageFooterDiv += "<div id='historyChartComboBox"+lineNum+"' class='historyChartComboBox'></div>";
		// messageFooterDiv += "</div>";
	// }


	historyMsgContentDiv += messageHeaderDiv;
	historyMsgContentDiv += messageInfoDiv;
	// historyMsgContentDiv += messageFooterDiv;
	historyMsgContentDiv += "</div>";
	
	rowDiv += lineNumDiv;
	rowDiv += historyMsgContentDiv;

	rowDiv += "</div>";
	
	return  rowDiv ;
}

function formatDate(dateObj){
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

  return dayNames[dayIndex] + ", " + monthNames[monthIndex] + " "+ date + ", " + year;
}

function timeSince(timeMillis) {

  var seconds = Math.floor((Date.now() - timeMillis) / 1000);

  var interval = Math.floor(seconds / (60 * 60 * 24 * 30 * 12));

  if (interval > 1) return interval + " years ago";
  else if(interval == 1) return interval + " year ago";
	
  interval = Math.floor(seconds / (60 * 60 * 24 * 30));
  if (interval > 1) return interval + " months ago";
  else if(interval == 1) return interval + " month ago";

  interval = Math.floor(seconds / (60 * 60 * 24 * 7));
  if (interval > 1) return interval + " weeks ago";
  else if(interval == 1) return interval + " week ago";

  interval = Math.floor(seconds / (60 * 60 * 24));
  if (interval > 1) return interval + " days ago";
  else if(interval == 1) return interval + " day ago";

  interval = Math.floor(seconds / (60*60));
  if (interval > 1) return interval + " hours ago";
  else if(interval == 1) return interval + " hour ago";

  interval = Math.floor(seconds / 60);
  if (interval > 1) return interval + " minutes ago";
  else if(interval == 1) return interval + " minute ago";
	
  interval = Math.floor(seconds);
  if (interval > 1) return interval + " seconds ago";
  else if(interval == 1) return interval + " second ago";
	else return "Just now";
}

function formatTime(date_obj) {
	
  // formats a javascript Date object into a 12h AM/PM time string
  var hour = date_obj.getHours();
  var minute = date_obj.getMinutes();
  var amPM = (hour > 11) ? "PM" : "AM";
  if(hour > 12) {
    hour -= 12;
  } else if(hour == 0) {
    hour = "12";
  }
  if(minute < 10) {
    minute = "0" + minute;
  }
  return hour + ":" + minute + " "+amPM;
}

function formateCurrency(input){
	return "EUR " + parseFloat(input).toFixed(6).replace(".",",")
}

function saveToHistory(){
	
	if(!isHistorySaving){
		
		if(lastSaveMarker < currHistoryEndLineNumber){
			
			var unsavedMessages =
				historyMessages
				.filter(function(message){
					return message.lineNumber > lastSaveMarker;
				});
				
			var lineNumbers = 
				unsavedMessages
				.map(function(message){ 
					return message.lineNumber;
				});
			isHistorySaving = true;
			directCall(
				"rt37",
				"NuoHistoryPaginationRequest",
				{
					StartLineNumber: lastSaveMarker + 1,
					EndLineNumber: currHistoryEndLineNumber,
					NuoHistoryMessages: 
						unsavedMessages
						.map(function(message){ 
							var nuoHistoryMessage = {
								LineNumber: message.lineNumber,
								Content: JSON.stringify(message)
							} 
							return nuoHistoryMessage;
						})
				}
			)
			.done(function(data){
				isHistorySaving = false;
				if(data.StatusCode && data.StatusCode === 200){

					var response = parseInt(data.Content);
					if(response>0){
						lastSaveMarker = parseInt(response);
					}
				}else{
					//window.location = 'https://eva.nuocanvas.ai';
				}
			});
		}
	}
}

function readFromHistory(){
	
	if(!isHistoryLoading){
		
		var lineNumbers = historyMessages.map(function(message){ 
			return message.lineNumber
		});
		
		var prevPageLineNumber = Math.min.apply(Math, lineNumbers) - 1
		if(lineNumbers.length == 1){
			prevPageLineNumber = -1;
		}
		if(prevPageLineNumber == -1 && lineNumbers.length == 1){
			historyMessages = [];
		}
		
		if(prevPageLineNumber > 1 || historyMessages.length == 0 ){
			
			isHistoryLoading = true;
			// $("#ajaxLoader").jqxLoader("open");
			directCall(
				"rt41",
				"NuoHistoryPaginationRequest",
				{
					EndLineNumber: prevPageLineNumber
				}
			)
			.done(function(data){
				isHistoryLoading = false;
				// $("#ajaxLoader").jqxLoader("close");
				if(data.StatusCode && data.StatusCode === 200){

					if(data && data.Content && data.Content.NuoHistoryMessages && data.Content.NuoHistoryMessages.length > 0){
						
						loadedMessages = 
							data
							.Content
							.NuoHistoryMessages
							.sort(function(l, r){
								if(l.LineNumber > r.LineNumber) return -1;
								if(l.LineNumber < r.LineNumber) return 1;
								return 0;
							})
							.map(function(nuoHistoryMessage){
								return JSON.parse(nuoHistoryMessage.Content);
							});
						historyMessages = historyMessages.concat(loadedMessages);
						currHistoryStartLineNumber = data.Content.StartLineNumber;
						currHistoryEndLineNumber = data.Content.EndLineNumber;
						lastSaveMarker = data.Content.EndLineNumber;
						renderHistorySection();
					}
				}else{
					//window.location = 'https://eva.nuocanvas.ai';
				}
			});
		}
	}
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

function deleteFileSelectionHandler(){

	var selectedIndices = $("#customerFilesTable").jqxGrid('getselectedrowindexes');
	if(confirm("You have selected "+selectedIndices.length+" file. Are you sure you want to delete them all?")){

		var selectedIndices = $("#customerFilesTable").jqxGrid('getselectedrowindexes');

		var filesToBeDeleted = [];
		selectedIndices
		.map(function(index){
			return $('#customerFilesTable').jqxGrid('getrowdata', index);
		})
		.forEach(function(rowData){

			if(rowData.isDirectory){
				
				filesToBeDeleted =
					filesToBeDeleted.concat(
						activeCustomerFiles
						.filter(function(ele){

							var fileToBeDeleted = encodeURI(rowData.fileLabel)+"/";
							var parentPath = getParentFilePath();
							
							if(parentPath && parentPath.length > 0 && parentPath!=="/"){
								fileToBeDeleted = parentPath + fileToBeDeleted;
							}
							return ele.fileName.startsWith(fileToBeDeleted) && !ele.fileName.endsWith(systemFileLabel);
						})
						.map(function(ele){return ele.fileName})
					)
			}else{
				var fileToBeDeleted = rowData.fileName;
				if(!fileToBeDeleted.endsWith(systemFileLabel)){
					filesToBeDeleted.push(fileToBeDeleted)
				}
			}
		})
		deleteFiles(filesToBeDeleted);
		$('#customerFilesTable').jqxGrid('clearselection');
	}
}

function deleteFiles(filesToBeDeleted){


	var fileContents = 
		filesToBeDeleted
			.map(function(fileName){
				
				var fileToBeDeleted = 
					{
						FileName: fileName
					}
				return fileToBeDeleted;
			});
			
	var parentPath = getParentFilePath();
	
	$("#customerFilePath").css("background-color","yellow");
	
	directCall("rt89", "NuoFileContentList", fileContents)
	.done(function(data){
		
		if(data.StatusCode && data.StatusCode === 200){
			
			if(activeCustomerFiles){

				activeCustomerFiles = activeCustomerFiles
					.filter(function(ele){
						return filesToBeDeleted.filter(function(child){ return ele.fileName === child}).length == 0;
					})
			}
			loadCustomerFilesTable(parentPath);
			$("#customerFilePath").css("background-color","white");
		}else{
			//window.location = 'https://eva.nuocanvas.ai';
		}
	})
}

function newFolderButtonHandler(){

	var folderName = prompt("Folder Name", "New Folder");
	if (folderName != null && folderName.trim().length > 0) {
		var parentPath = getParentFilePath();

		var fileObject = {
			author: activeUsername,
			sizeBytes: 0,
			dateCreatedMillis: Date.now(),
			dateModifiedMillis: Date.now()
		}

		var folderFileName = encodeURI(folderName) + "/" + systemFileLabel;
		if(parentPath && parentPath.length > 0 && parentPath!=="/"){
			folderFileName = parentPath + folderFileName;
		}

		fileObject.fileName = folderFileName;
		console.log(JSON.stringify(fileObject));
		activeCustomerFiles.push(fileObject);
		loadCustomerFilesTable(parentPath);
	}
}

function evaFilesButtonHandler(){
	loadCustomerDirWindow(activeUsername);
}


function profileImageSelectionHandler(fileEvent){

	var files = fileEvent.target.files; // FileList object
	
	if(files.length > 0){
		
		var file = files[0];

		// Loop through the FileList and render image files as thumbnails.
		var reader = new FileReader();

		// Closure to capture the file information.
		reader.onload = (function(loadedFile) {
			return function() {

				// var targetFileName = encodeURI(loadedFile.name);
				var fileToBeUploaded = "ProfileImage/Latest"
				var fileObject = {
					author: activeUsername,
					sizeBytes: loadedFile.size,
					dateCreatedMillis: Date.now(),
					dateModifiedMillis: Date.now()
				}

				fileObject.fileName = fileToBeUploaded;
				fileToBeUploaded = fileToBeUploaded;
				directCall("rt79",
					"NuoFileContent",
					{
						FileName: fileToBeUploaded,
						ContentType: loadedFile.type

					}
				)
				.done(function(data){
					if(data.StatusCode && data.StatusCode === 200){

						$.ajax({
							type: 'PUT',
							url: data.Content,
							// Content type must much with the parameter you signed your URL with
							headers: {
								// "Content-Disposition": "attachment; filename=\""+loadedFile.name.split("/").map(function(ele){return encodeURI(ele)}).join("/")+"\""
								"Content-Disposition": "attachment; filename=\""+loadedFile.name+"\""
							},
							contentType: loadedFile.type,
							// this flag is important, if not set, it will try to send data as a form
							processData: false,
							// the actual file is sent raw
							data: loadedFile,
							success: function() {
								console.log(JSON.stringify(fileObject));
								profileImageUrl = URL.createObjectURL(loadedFile);
								$('#profileImageFile').attr('src',profileImageUrl);
							},
							error: function(data) {
								alert("File "+loadedFile.name+" could not be uploaded. If this is not because of your betwork, please contact NuoCanvas support.");
								console.log(data);
							}
						})
						return false;
					}else{
						// $('#profileImageFile').attr('src',URL.createObjectURL(loadedFile));
						//window.location = 'https://eva.nuocanvas.ai';
					}
				});
			};
		})(file);
		reader.readAsDataURL(file);
	}
}

function uploadFileSeletionHandler(fileEvent){

	var files = fileEvent.target.files; // FileList object
	var pendingFiles = 0;

	// Loop through the FileList and render image files as thumbnails.
	for (var i = 0, file; file = files[i]; i++) {

		var reader = new FileReader();

		// Closure to capture the file information.
		reader.onload = (function(loadedFile) {
			return function() {

				// var targetFileName = encodeURI(loadedFile.name);
				var fileToBeUploaded = encodeURI(loadedFile.name);
				var fileObject = {
					author: activeUsername,
					sizeBytes: loadedFile.size,
					dateCreatedMillis: Date.now(),
					dateModifiedMillis: Date.now()
				}

				var parentPath = getParentFilePath();
				if(parentPath && parentPath.length > 0 && parentPath!=="/"){
					fileToBeUploaded = parentPath + fileToBeUploaded;
				}
				fileObject.fileName = fileToBeUploaded;
				fileToBeUploaded = fileToBeUploaded;
				$("#customerFilePath").css("background-color","green");
				$("#customerFilePath").css("color","white");
				pendingFiles+=1;
				directCall("rt79",
					"NuoFileContent",
					{
						FileName: fileToBeUploaded,
						ContentType: loadedFile.type

					}
				)
				.done(function(data){
					if(data.StatusCode && data.StatusCode === 200){

						$.ajax({
							type: 'PUT',
							url: data.Content,
							// Content type must much with the parameter you signed your URL with
							headers: {
								// "Content-Disposition": "attachment; filename=\""+loadedFile.name.split("/").map(function(ele){return encodeURI(ele)}).join("/")+"\""
								"Content-Disposition": "attachment; filename=\""+loadedFile.name+"\""
							},
							contentType: loadedFile.type,
							// this flag is important, if not set, it will try to send data as a form
							processData: false,
							// the actual file is sent raw
							data: loadedFile,
							success: function() {
								console.log(JSON.stringify(fileObject));
								activeCustomerFiles.push(fileObject);
								loadCustomerFilesTable(parentPath);
								pendingFiles-=1;
								if(pendingFiles == 0){
									$("#customerFilePath").css("background-color","white");
									$("#customerFilePath").css("color","black");
								}
							},
							error: function(data) {
								alert("File "+loadedFile.name+" could not be uploaded. If this is not because of your betwork, please contact NuoCanvas support.");
								console.log( data);
								pendingFiles-=1;
								if(pendingFiles == 0){
									$("#customerFilePath").css("background-color","white");
									$("#customerFilePath").css("color","black");
								}
							}
						})
						return false;
					}else{
						//window.location = 'https://eva.nuocanvas.ai';
					}
				});
			};
		})(file);
		reader.readAsDataURL(file);
	}

}

function getParentFilePath(){
	var firstElement = $('#customerFilesTable').jqxGrid('getrowdata', 0);
	if(firstElement) 
		return firstElement.parentPath;
	else 
		return "";
}


function loadCustomerDirWindow(activeUsername){

	var screenWidth = $(window).width();
	var screenHeight = $(window).height();

	var windowWidth = 800;
	var windowHeight = 500;

	$("#customerDirWindow").jqxWindow(
		{
			width: windowWidth,
			height: windowHeight,
			isModal: true,
			resizable: true,
			autoOpen:	false,
			// cancelButton: $("#storageEntitiesCloseButton"),
			position: { x: (screenWidth / 2) - 300, y: 100},
			theme: jqWidgetThemeName
		}
	);
	$("#customerDirWindow").jqxWindow("setTitle","Files");
	$("#customerFilePath").html("/");
	loadCustomerFilesTable("");
	$("#customerDirWindow").jqxWindow("open");
}

function loadCustomerFilesTable(parentPath,shouldRefresh){

		if(shouldRefresh || !activeCustomerFiles || activeCustomerFiles.length == 0){

			directCall("rt97")
			.done(function(data){

				if(data.StatusCode && data.StatusCode === 200){

					console.log("Get files status= " + data.Status);

					if(data.Content && data.Content.NuoEvaFiles){
					
						activeCustomerFiles = data.Content.NuoEvaFiles
							
						//Render the customerFilesTable
						renderCustomerFilesTable(parentPath);
					}else{
						$("#customerFilesTable").html("I was not able to serve your request. Please contact support");
						$("#customerFilesTable").show();
						//window.location = 'https://eva.nuocanvas.ai';
					}
				}else{
						$("#customerFilesTable").html("I was not able to serve your request. Please contact support");
						$("#customerFilesTable").show();
					//window.location = 'https://eva.nuocanvas.ai';
				}
			});
		}else {
			//Render the customerFilesTable
			renderCustomerFilesTable(parentPath);
		}
}


function renderCustomerFilesTable(parentPath){
	
		var gridColumns = [
			{ text: 'File Name', dataField: 'fileLabel', width: '45%', height: 20 },
			// { text: 'Auteur', dataField: 'author', width: '10%', height: 20 },
			{ text: 'Size', dataField: 'sizeLabel', width: '10%', cellsalign: 'right', height: 20 },
			{ text: 'Type', dataField: 'fileType', width: '15%', height: 20 },
			{
				text: 'Date Uploaded',
				dataField: 'dateCreated',
				width: '25%',
				height: 20,
				cellsformat: 'yyyy-MM-dd  HH:mm:ss.fff'
			}
		];

	renderJqxGrid('#customerFilesTable', transformCustomerFiles(activeCustomerFiles,parentPath), gridColumns);
	// $('#customerFilesTable').jqxGrid('clearselection');
	$("#customerFilesTable").show();
	$("#customerFilePath").show();
}

function transformCustomerFiles(activeCustomerFiles,parentPath){

	var fileElements = [];
	
	activeCustomerFiles
		.map(function(fileObj){

			var fileName = fileObj.fileName;
			var fileElement = {};

			if(!parentPath || parentPath.length == 0 || (fileName.indexOf(parentPath) >= 0 && fileName.length > parentPath.length)){

				fileElement.fileName = fileName;
				fileElement.parentPath = parentPath;

				if(fileName.indexOf("/",parentPath.length) >= 0){

					fileElement.fileLabel = decodeURI(fileName.substring(parentPath.length,fileName.indexOf("/",parentPath.length + 1)));
					fileElement.filePath = fileName.substring(0,fileName.indexOf("/",parentPath.length + 1) + 1);
					fileElement.isDirectory = true;
					fileElement.fileType = "Folder";
				}else{
					fileElement.fileLabel = decodeURI(fileName.substring(parentPath.length));
					fileElement.filePath = fileName;
					fileElement.isDirectory = false;

					if(fileElement.fileLabel.indexOf(".") > 0){
						fileElement.isDirectory = false;
						fileElement.fileType = fileElement.fileLabel.substring(fileElement.fileLabel.lastIndexOf(".")+1).toUpperCase() + " File";
					}
				}
				// fileElement.author =  fileObj.author;
				fileElement.dateCreated = new Date(parseInt(fileObj.dateCreatedMillis));
				// fileElement.dateModified = new Date(parseInt(fileObj.dateModifiedMillis));
				fileElement.sizeBytes = parseInt(fileObj.sizeBytes);
				fileElement.sizeLabel = getSizeLabel(fileElement.sizeBytes);
				
				return fileElement;
			}else return fileElement;
		})
		.filter(function(fileElement) {
			return fileElement.fileLabel;
		})
		.sort(function(l, r){
			if(l.dateModified > r.dateModified) return -1;
			if(l.dateModified < r.dateModified) return 1;
			return 0;
		})
		.forEach(function(fileElement){
			var existingElement = fileElements.find(function(ele){
				return ele.fileLabel === fileElement.fileLabel;
			});
			if(!existingElement){
				fileElements.push(fileElement);
			}else{
				existingElement.sizeBytes += fileElement.sizeBytes;
				existingElement.sizeLabel = getSizeLabel(existingElement.sizeBytes);
			}
		});
	
	return fileElements;

}

function getSizeLabel(sizeBytes){
	if(sizeBytes >= 1024 * 1024 * 1024 * 1024.0){
			return  (sizeBytes/1024.0/1024.0/1024.0/1024.0).toFixed(2) + ' TB';
	}else if(sizeBytes >= 1024 * 1024 * 1024.0){
			return  (sizeBytes/1024.0/1024.0/1024.0).toFixed(2) + ' GB';
	}else if(sizeBytes >= 1024 * 1024.0){
			return  (sizeBytes/1024.0/1024.0).toFixed(2) + ' MB';
	}else if(sizeBytes >= 1024.0){
			return  (sizeBytes/1024.0).toFixed(2) + ' KB';
	}else{
		return sizeBytes + ' B';
	}
}

function addCustomerTableEventHandlers(){

	$('#customerFileUpButton').on('click', function (event){
		var rowData = $('#customerFilesTable').jqxGrid('getrowdata', 0);
		var grandParentPath = "";
		if(rowData.isDirectory){
			grandParentPath = rowData.parentPath.substring(0,rowData.parentPath.length - 1).substring(0,rowData.parentPath.substring(0,rowData.parentPath.lastIndexOf("/")).lastIndexOf("/") + 1)
		}else{
			grandParentPath = rowData.parentPath.substring(0,rowData.parentPath.substring(0,rowData.parentPath.lastIndexOf("/")).lastIndexOf("/") + 1)
		}
		loadCustomerFilesTable(grandParentPath);
		
		if(grandParentPath.length == 0)
			$("#customerFilePath").html("/");
		else
			$("#customerFilePath").html(decodeURI(grandParentPath));
		
		$('#customerFilesTable').jqxGrid('clearselection');
	});

	$('#customerFileRefreshButton').on('click', function (event){
		loadCustomerFilesTable("",true)
	});
	
	$('#customerFilesTable').on('rowdoubleclick', function (event){

		var clickedIndex = event.args.rowindex;
		var rowData = $('#customerFilesTable').jqxGrid('getrowdata', clickedIndex);

		if(rowData.isDirectory){

			loadCustomerFilesTable(rowData.filePath);
			$("#customerFilePath").html(decodeURI(rowData.filePath));

			if(rowData.filePath.length == 0)
				$("#customerFilePath").html("/");
		}else{
			var fileToBeDownloaded = encodeURI(rowData.fileLabel);

			if(!fileToBeDownloaded.endsWith(systemFileLabel)){

				var parentPath = rowData.parentPath;
				if(parentPath && parentPath.length > 0 && parentPath!=="/"){
					fileToBeDownloaded = parentPath + fileToBeDownloaded;
				}
				fileToBeDownloaded = fileToBeDownloaded;
				directCall("rt83", "NuoFileContent", {FileName: fileToBeDownloaded}
				)
				.done(function(data){
					if(data.StatusCode && data.StatusCode === 200){
						var downloadEle = document.getElementById("downloadTag");
						downloadEle.setAttribute("href",data.Content);
						downloadEle.click();
					}else{
						//window.location = 'https://eva.nuocanvas.ai';
					}
				})
			}
		}
		$('#customerFilesTable').jqxGrid('clearselection');
	});
}


//Customer Files block END

//Progress Monitor block START


function evaProgressButtonHandler(){
	loadProgressMonitorWindow(activeUsername);
}

function loadProgressMonitorWindow(activeUsername){

	var screenWidth = $(window).width();
	var screenHeight = $(window).height();

	var windowWidth = 800;
	var windowHeight = 500;

	$("#progressMonitorWindow").jqxWindow(
		{
			width: windowWidth,
			height: windowHeight,
			isModal: true,
			resizable: true,
			autoOpen:	false,
			// cancelButton: $("#storageEntitiesCloseButton"),
			position: { x: (screenWidth / 2) - 300, y: 100},
			theme: jqWidgetThemeName
		}
	);
	$("#progressMonitorWindow").jqxWindow("setTitle","Analysis Progress");
	loadProgressMonitorTable(true);
	$("#progressMonitorWindow").jqxWindow("open");
}

function loadProgressMonitorTable(shouldRefresh){

		if(shouldRefresh || !activeProgressMonitors || activeProgressMonitors.length == 0){

			var promise = null;
			
			if(shouldRefresh){
				promise = directCall("rt113");
			}else{
				promise = directCall("rt113");
			} 
			
			promise
			.done(function(data){

				if(data.StatusCode && data.StatusCode === 200){

					console.log("Get Progress status= " + data.Status);

					if(data.Content && data.Content.NuoQuestionMonitorList){
					
						activeProgressMonitors = data.Content.NuoQuestionMonitorList
							
						//Render the Storage Explorer
						renderProgressMonitorTable();
					}else{
						$("#progressMonitorTable").html("I was not able to serve your request. Please contact support");
						$("#progressMonitorTable").show();
						//window.location = 'https://eva.nuocanvas.ai';
					}
				}else{
						$("#progressMonitorTable").html("I was not able to serve your request. Please contact support");
						$("#progressMonitorTable").show();
					//window.location = 'https://eva.nuocanvas.ai';
				}
			});
		}else {
			//Render the progressMonitorTable
			renderProgressMonitorTable();
		}
}

function renderProgressMonitorTable(){
	
	var gridColumns = [];
	if( activeProgressMonitors && activeProgressMonitors.length > 0){

		var gridColumns = 
		[
			{ text: 'Alias', dataField: 'QuestionAlias', width: '20%', height: 20 },
			{ text: 'Message', dataField: 'QuestionText', width: '20%', height: 20 },
			{ text: 'Progress', 
				dataField: 'Progress', 
				width: '20%', 
				height: 20,
				cellsrenderer: function (row, colum, value) { 
					var cell = '<div style="margin-top:5px;">'; 
					cell += '<div style="background: #058dc7; float: left; width: ' + value + 'px; height: 16px;"></div>'; 
					cell += '<div style="margin-left: 5px; float: left;">' + parseInt(value).toString() + '%' + '</div>'; 
					cell += '</div>'; 
					return cell; 
				}
			},
			{ text: 'StartTime', dataField: 'StartTimeDate', width: '20%', height: 20 },
			{ text: 'Duration', dataField: 'Duration', width: '20%', height: 20 }
		];
		
		var gridData = 
			activeProgressMonitors
			.map(function(activeProgressMonitor){
				
				if(activeProgressMonitor.TotalSteps && activeProgressMonitor.TotalSteps > 0){
					activeProgressMonitor.Progress = activeProgressMonitor.StepsCompleted / activeProgressMonitor.TotalSteps * 100.0 
				}else{
					activeProgressMonitor.Progress = 0.0 
				}

				if(activeProgressMonitor.StartTimeMillis){
					
					activeProgressMonitor.StartTimeDate = new Date(activeProgressMonitor.StartTimeMillis)
						// var startDate = new Date(activeProgressMonitor.StartTimeMillis).toString()
					// activeProgressMonitor.StartTimeDate = 
						// startDate.substring(0 ,startDate.indexOf( "(")-1)
				}else{
					activeProgressMonitor.StartTimeDate = "N/A" 
				}

				if(activeProgressMonitor.StartTimeMillis && activeProgressMonitor.EndTimeMillis){
					
					if(activeProgressMonitor.Progress < 100.0 ){
						
						activeProgressMonitor.Duration = 
							getReadableDuration(Date.now() - activeProgressMonitor.StartTimeMillis)
					}else{
						
						activeProgressMonitor.Duration = 
							getReadableDuration(activeProgressMonitor.EndTimeMillis - activeProgressMonitor.StartTimeMillis)
					}
				}else{
					activeProgressMonitor.Progress = "N/A" 
				}
				return activeProgressMonitor;
			})
		renderJqxGrid('#progressMonitorTable',gridData, gridColumns);
	} 
	$("#progressMonitorTable").show();
}

function addProgressMonitorEventHandlers(){

 
	$('#progressMonitorRefreshButton').on('click', function (event){
		loadProgressMonitorTable(true)
	});

}

//Progress Monitor block END

//Storage Explorer block START


function deleteTableSelectionHandler(){

	var selectedIndices = $("#storageExplorerTable").jqxGrid('getselectedrowindexes');
	if(confirm("You have selected "+selectedIndices.length+" tables. Are you sure you want to delete them all?")){

		var tablesToBeDeleted = 
			selectedIndices
			.map(function(index){
				return $('#storageExplorerTable').jqxGrid('getrowdata', index);
			})
			.filter(function(rowData){
				return !rowData.FieldName && rowData.EntityName && rowData.EntityName.length > 0;
			})
			.map(function(rowData){

				var tableToBeDeleted = 
					{
						DatasetName: "",
						EntityName: rowData.EntityName
					};
				return tableToBeDeleted;
			})
			
		$("#storageExplorerTableName").css("background-color","yellow");
		
		directCall("rt109", "NuoEntities", tablesToBeDeleted)
		.done(function(data){

			if(data.StatusCode && data.StatusCode === 200){
				console.log("Delete table status= " + data.Content);
				activeEvaTables = 
					activeEvaTables
						.filter(function(ele){
							return tablesToBeDeleted.filter(function(t){ return t.EntityName === ele.EntityName}).length == 0;
						});
				loadStorageExplorerTable();
				$("#storageExplorerTableName").css("background-color","white");
			}else{
				//window.location = 'https://eva.nuocanvas.ai';
			}
		})
		$('#storageExplorerTable').jqxGrid('clearselection');
	}
}

function evaTablesButtonHandler(){
	loadStorageExplorerWindow(activeUsername);
}

function loadStorageExplorerWindow(activeUsername){

	var screenWidth = $(window).width();
	var screenHeight = $(window).height();

	var windowWidth = 800;
	var windowHeight = 500;

	$("#storageExplorerWindow").jqxWindow(
		{
			width: windowWidth,
			height: windowHeight,
			isModal: true,
			resizable: true,
			autoOpen:	false,
			// cancelButton: $("#storageEntitiesCloseButton"),
			position: { x: (screenWidth / 2) - 300, y: 100},
			theme: jqWidgetThemeName
		}
	);
	$("#storageExplorerWindow").jqxWindow("setTitle","Tables");
	loadStorageExplorerTable();
	$("#storageExplorerWindow").jqxWindow("open");
}


function loadStorageExplorerTable(tableName,shouldRefresh){

		if(shouldRefresh || !activeEvaTables || activeEvaTables.length == 0){

			var promise = null;
			
			if(shouldRefresh){
				promise = directCall("rt107");
			}else{
				promise = directCall("rt103");
			} 
			
			$("#storageExplorerTableName").css("background-color","yellow");
			promise
			.done(function(data){

				if(data.StatusCode && data.StatusCode === 200){

					console.log("Get tables status= " + data.Status);

					if(data.Content && data.Content.NuoEvaTables){
					
						activeEvaTables = data.Content.NuoEvaTables
							
						//Render the Storage Explorer
						renderStorageExplorerTable(tableName);
					}else{
						$("#storageExplorerTable").html("I was not able to serve your request. Please contact support");
						$("#storageExplorerTable").show();
						//window.location = 'https://eva.nuocanvas.ai';
					}
				}else{
						$("#storageExplorerTable").html("I was not able to serve your request. Please contact support");
						$("#storageExplorerTable").show();
					//window.location = 'https://eva.nuocanvas.ai';
				}
			});
		}else {
			//Render the storageExplorerTable
			renderStorageExplorerTable(tableName);
		}
}

function renderStorageExplorerTable(tableName){
	
	var gridColumns = [];
	if(tableName && tableName.trim().length > 0 && activeEvaTables && activeEvaTables.length > 0){
		
		var evaTableMatches = activeEvaTables.filter(function(ele){ return ele.EntityName === tableName;});
		if(evaTableMatches && evaTableMatches.length > 0){

			var gridColumns = 
			[
				{ text: 'Column Name', dataField: 'FieldName', width: '30%', height: 20 },
				{ text: 'Data Type', dataField: 'DataType', width: '30%', height: 20 }
			];
			
			var evaTable = evaTableMatches[0];
			
			renderJqxGrid('#storageExplorerTable', evaTable.Fields, gridColumns);
		}
	}else if(activeEvaTables && activeEvaTables.length > 0){

		var gridColumns = 
		[
			{ text: 'Table Name', dataField: 'EntityName', width: '20%', height: 20 },
			{ text: 'Size', dataField: 'SizeLabel', cellsalign: 'right',width: '12%', height: 20 },
			{ text: 'Total Rows', dataField: 'SizeRows', cellsalign: 'right',width: '12%', height: 20 },
			{
				text: 'Created At',
				dataField: 'CreatedAt',
				width: '25%',
				height: 20,
				cellsformat: 'yyyy-MM-dd  HH:mm:ss.fff'
			},
			{
				text: 'Last Modified on',
				dataField: 'LastModifiedOn',
				width: '25%',
				height: 20,
				cellsformat: 'yyyy-MM-dd  HH:mm:ss.fff'
			}
		];
		
		var evaTables = 
			activeEvaTables
			.map(function(ele){
				ele.SizeRows = parseInt(ele.SizeRows);
				ele.SizeLabel = getSizeLabel(parseInt(ele.SizeBytes));
				ele.CreatedAt = new Date(parseInt(ele.CreationTime));
				ele.LastModifiedOn = new Date(parseInt(ele.LastModifiedTime));
				return ele;
			})
		
		renderJqxGrid('#storageExplorerTable', evaTables, gridColumns);
	}
	$("#storageExplorerTable").show();
	if(tableName && tableName.length > 0)
		$("#storageExplorerTableName").html(tableName);
	else
		$("#storageExplorerTableName").html("Tables");
	$("#storageExplorerTableName").show();
	$("#storageExplorerTableName").css("background-color","white");
}

function addStorageExplorerEventHandlers(){

	$('#storageExplorerUpButton').on('click', function (event){
		var rowData = $('#storageExplorerTable').jqxGrid('getrowdata', 0);
		var grandParentPath = "";
		if(rowData.FieldName){
			loadStorageExplorerTable(null,false);
		} 
		$('#storageExplorerTable').jqxGrid('clearselection');
	});

	$('#storageExplorerRefreshButton').on('click', function (event){
		loadStorageExplorerTable(null,true)
	});
	
	$('#storageExplorerTable').on('rowdoubleclick', function (event){

		var clickedIndex = event.args.rowindex;
		var rowData = $('#storageExplorerTable').jqxGrid('getrowdata', clickedIndex);

		if(!rowData.FieldName && rowData.EntityName && rowData.EntityName.length > 0){

			loadStorageExplorerTable(rowData.EntityName,false);

		} 
		$('#storageExplorerTable').jqxGrid('clearselection');
	});
}

//Storage Explorer block END


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
    dataType: 'json'
  };
  var dataAdapter = new $.jqx.dataAdapter(source);
  var dataTableSettings = {
    columnsResize: true,
    altRows: false,
    sortable: true,
    selectionMode: 'checkbox',
    source: dataAdapter,
    columns: gridColumns,
		width: "98%",
		height: "80%",
    // autoheight: true,
    // autorowheight: true,
		scrollmode: 'logical',
		theme: jqWidgetThemeName
  };

  if (paramEditable) {
    dataTableSettings.editable = true;
  }

  if (enablePaging) {
    dataTableSettings.pageable = true;
    dataTableSettings.pagerMode = 'advanced';
  }

  $(uid).jqxGrid(dataTableSettings);
}

function createPlatformMetrics(){
		// Create jqxProgressBar.
	var renderText = function (text, value) {
		if (value < 55) {
			return "<span style='color: #333;'>" + text + "</span>";
		}
		return "<span style='color: #fff;'>" + text + "</span>";
	}

	$("#metricDataProcessedProgress").jqxProgressBar({ animationDuration: 0, showText: true, renderText: renderText, template: "primary"});
	$("#metricStorageGBProgress").jqxProgressBar({ animationDuration: 0, showText: true, renderText: renderText, template: "primary"});
	$("#metricModelTrainingProgress").jqxProgressBar({ animationDuration: 0, showText: true, renderText: renderText, template: "primary"});
	$("#metricPredictionsProgress").jqxProgressBar({ animationDuration: 0, showText: true, renderText: renderText, template: "primary"});
	$("#metricImageAnalysisProgress").jqxProgressBar({ animationDuration: 0, showText: true, renderText: renderText, template: "primary"});
	
	$("#metricDataProcessedProgress").val(60);
	$("#metricStorageGBProgress").val(15);
	$("#metricModelTrainingProgress").val(32);
	$("#metricPredictionsProgress").val(85);
	$("#metricImageAnalysisProgress").val(72);

}

function getReadableDuration(timeMillis){
	
	var hh = 0
	var mm = 0
	var ss = timeMillis / 1000

 	if(ss >= 3600){
		hh = parseInt(ss / 3600)
		ss -= hh * 3600
	}
	if(ss >= 60){
		mm = parseInt(ss / 60)
		ss -= mm * 60
	}
	
	return hh+"h:"+mm+"m:"+parseInt(ss)+"s"
}
