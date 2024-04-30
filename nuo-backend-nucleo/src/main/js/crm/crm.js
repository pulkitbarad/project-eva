
/**
  * Copyright 2015-2018 NuoCanvas.
  *
  *
  * Created by Pulkit on 03APR2018
  *
  * Content of this file is proprietary and confidential.
  * It shall not be reused or disclosed without prior consent
  * of distributor
  **/

var pollingIntervalMilli = 3000;
var nuoEvaMessageHolder = {};
var nuoUserMessageHolder = {};
var nuoConversationResponse = null;
var mapHolder;
var geocoderHolder = null;
var placesServiceHolder = null;
const	entryPosition = {
	lat: 52.0933702,
	lng: 6.2846642
};
const	entryZoomLevel = 7.5;
const	searchZoomLevel = 17;
const	clickZoomLevel = 18;
const markerCircleSize = 10;
const systemFileLabel = "Systeembestand";
var activeMarker;
var nuoCustomers = [];
var activePlaceDetails;
var activeLocationDetails;
var activeCustomerDetails;
var activityPropertyName = "Activiteiten"
var colorCriDateName = "Uitvoeringsdatum"
var colorCriStatusName = "Status"
var colorCriStatusComplete = "Opgeleverd"
var markerColorGreen = "green";
var markerColorRed = "red";
var markerColorOrange = "orange";
var markerColorBlue = "blue";
var isExplicitSave = false;
var customerSearchKeywords = ["klanten", "klant"];
var conditionPositivePrefix = ["met"];
var conditionNegativePrefix = ["zonder","geen"];
var conditionOpEq = ["hetzelfde als", "is gelijk aan", "zijn gelijk aan", "="];
var conditionOpGt = ["meer dan", ">"];
var conditionOpGtEq = ["meer dan of gelijk aan", ">=", "> ="];
var conditionOpLt = ["minder dan", "<"];
var conditionOpLtEq = ["minder dan of gelijk aan","<=", "< ="];
var conditionOpContains = ["bevat"];
var conditionOpFrom = ["uit"];
const conditionOperatorType = {
		EQ: "==",
		GT: ">",
		GT_EQ: ">=",
		LT: "<",
		LT_EQ: "<=",
		CONTAINS: "__CONTAINS",
		FROM: "__FROM",
		DEFAULT: "__DEFAULT"
	};
var maskedOperands = {};

var nuoCustomersSample = {
	id:"1",
	name: "AVAB",
	value:18,
	location: {
		lat: 52.1192104,
		lng: 5.039440000000013
	},
	files:[
		{
			fileName: "Home/",
			author: "System",
			sizeBytes: 0,
			dateCreatedMillis: Date.now(),
			dateModifiedMillis: Date.now()
		}
	]
};

function initUI(){

	hideCustomerBanner();
	$("#customerFilesTable").hide();
	$("#customerFilePath").hide();
	$("#customerDetailsGrid").hide();
	$("#mouseOverContainer").hide();
	$("#activityMenu").hide();
	$("#activeUsername").text("Welkom "+ activeUsername);
	getCustomers();

	addCustomerTableEventHandlers();
	activeCustomerDetails = getNewCustomerDetails();
	createCustomerDetailsGrid();

	$("#searchCloseButton").jqxToggleButton({ toggled: false, template:'default'});
	$("#signOutButton").on("click",signOutHandler)
	$("#fileInput").on("change",uploadFileSeletionHandler)
	$("#customerFileDeleteButton").on("click",deleteFileSelectionHandler);
	$("#customerFileNewFolderButton").on("click",newFolderButtonHandler);
	$("#searchButton").on("click",searchButtonHandler);
	$("#searchCloseButton").on("click",searchCloseButtonHandler);
	$("#deleteCustomerButton").on("click",deleteCustomerButtonHandler);
	$("#customerFilesButton").on("click",customerFilesButtonHandler);
	$("#placeActionButton").on("click",placeActionButtonHandler);
	$("#mapResetButton").on("click",mapResetButtonHandler);
	$("#searchBox").on("keypress",function(e){
		if(e.keyCode && e.keyCode == 13){
			searchButtonHandler();
		}
	});
}

function signOutHandler(){

	directCall(
		"rt73"
	)
	.done(function(data){
		window.location = 'https://avab.nuocanvas.ai';
	})

}

function getCustomers(){

	directCall(
		"rt3"
	)
	.done(function(data){

		if(data.StatusCode && data.StatusCode === 200){
			if(data.Content && data.Content.trim().length > 0){
				nuoCustomers =
					data.Content.trim()
					.split("|~|")
					.map(function(customerContent){
						return JSON.parse(customerContent);
					});
			}
			initMap();
		}else{
			window.location = 'https://avab.nuocanvas.ai';
		}
	})
}

function deleteFileSelectionHandler(){

	if(confirm("Weet je zeker dat je geselecteerde bestanden wilt verwijderen?")){
		var selectedIndices = $("#customerFilesTable").jqxGrid('getselectedrowindexes');
		var pendingFiles = 0;

		// Loop through the FileList and render image files as thumbnails.
		selectedIndices
		.map(function(index){
			return $('#customerFilesTable').jqxGrid('getrowdata', index);
		})
		.forEach(function(rowData){

			// var rowData = $('#customerFilesTable').jqxGrid('getrowdata', index);
			var fileToBeDeleted = rowData.fileName;

			if(rowData.isDirectory){
				var filesToBeDeleted =
					getCustomer(activeCustomerDetails)
					.files
					.filter(function(ele){
						var parentPath = getParentFilePath();
						return ele.fileName.startsWith(parentPath + rowData.fileLabel);
					})
				deleteFiles(filesToBeDeleted,pendingFiles);
			}else{

				if(!fileToBeDeleted.endsWith(systemFileLabel)){

					var parentPath = getParentFilePath();

					var customerId = getCustomerId(activeCustomerDetails);
					if(customerId){
						fileToBeDeleted = "CRM/UserFiles/" + customerId + "/" + fileToBeDeleted;
					}
					$("#customerFilePath").css("background-color","yellow");
					pendingFiles += 1;
					directCall("rt13", "NuoFileContent", {FileName: fileToBeDeleted})
					.done(function(data){

						if(data.StatusCode && data.StatusCode === 200){
							console.log("Delete files status= " + data.Content);
							getCustomer(activeCustomerDetails).files =
								getCustomer(activeCustomerDetails)
								.files
								.filter(function(ele){
									return ele.fileName !== rowData.fileName;
								})
							loadCustomerFilesTable(getFileList(parentPath));
							pendingFiles -= 1;
							if(pendingFiles == 0){
								writeCustomerDetails();
								$("#customerFilePath").css("background-color","white");
							}
						}else{
							window.location = 'https://avab.nuocanvas.ai';
						}
					})
				}
			}
		})
		$('#customerFilesTable').jqxGrid('clearselection');
	}
}

function deleteFiles(filesToBeDeleted,paramPendingFiles){

	var pendingFiles = paramPendingFiles;

	filesToBeDeleted
	.forEach(function(customerFile){
		var fileToBeDeleted = customerFile.fileName;
		var parentPath = getParentFilePath();
		if(!fileToBeDeleted.endsWith(systemFileLabel)){
			var customerId = getCustomerId(activeCustomerDetails);
			if(customerId){
				fileToBeDeleted = "CRM/UserFiles/" + customerId + "/" + fileToBeDeleted;
			}
			$("#customerFilePath").css("background-color","yellow");
			pendingFiles+=1;
			directCall("rt13", "NuoFileContent", {FileName: fileToBeDeleted})
			.done(function(data){
				if(data.StatusCode && data.StatusCode === 200){
					if(getCustomer(activeCustomerDetails)){

						getCustomer(activeCustomerDetails).files =
							getCustomer(activeCustomerDetails)
							.files
							.filter(function(ele){
								return ele.fileName !== customerFile.fileName;
							})
					}
					loadCustomerFilesTable(getFileList(parentPath));
					pendingFiles-=1;
					if(pendingFiles == 0){
						writeCustomerDetails();
						$("#customerFilePath").css("background-color","white");
					}
				}else{
					window.location = 'https://avab.nuocanvas.ai';
				}
			})
		}else{
			getCustomer(activeCustomerDetails).files =
				getCustomer(activeCustomerDetails)
				.files
				.filter(function(ele){
					return ele.fileName !== customerFile.fileName;
				})
			loadCustomerFilesTable(getFileList(parentPath));
		}
	})
}

function newFolderButtonHandler(){

	var folderName = prompt("Naam van de map", "Nieuwe Map");
	if (folderName != null && folderName.trim().length > 0) {
		var parentPath = getParentFilePath();

		var fileObject = {
			author: activeUsername,
			sizeBytes: 0,
			dateCreatedMillis: Date.now(),
			dateModifiedMillis: Date.now()
		}

		var folderFileName = folderName + "/" + systemFileLabel;
		if(parentPath && parentPath.length > 0 && parentPath!=="/"){
			folderFileName = parentPath + folderFileName;
		}

		fileObject.fileName = folderFileName;
		console.log(JSON.stringify(fileObject));
		getCustomer(activeCustomerDetails).files.push(fileObject);
		loadCustomerFilesTable(getFileList(parentPath));
		writeCustomerDetails();
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
				var fileToBeUploaded = loadedFile.name;
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
				var customerId = getCustomerId(activeCustomerDetails);
				if(customerId){
					fileToBeUploaded = "CRM/UserFiles/" + customerId + "/" + fileToBeUploaded;
				}
				$("#customerFilePath").css("background-color","green");
				$("#customerFilePath").css("color","white");
				pendingFiles+=1;
				directCall("rt7",
					"NuoFileContent",
					{
						FileName: fileToBeUploaded,
						ContentType: loadedFile.type

					}
				)
				.done(function(data){
					if(data.StatusCode && data.StatusCode === 200){

						console.log("Received presigned url "+data.Content);
						$.ajax({
							type: 'PUT',
							url: data.Content,
							// Content type must much with the parameter you signed your URL with
							headers: {
								"Content-Disposition": "attachment; filename=\""+loadedFile.name+"\""
							},
							contentType: loadedFile.type,
							// this flag is important, if not set, it will try to send data as a form
							processData: false,
							// the actual file is sent raw
							data: loadedFile,
							success: function() {
								console.log(JSON.stringify(fileObject));
								getCustomer(activeCustomerDetails).files.push(fileObject);
								loadCustomerFilesTable(getFileList(parentPath));
								pendingFiles-=1;
								writeCustomerDetails();
								if(pendingFiles == 0){
									$("#customerFilePath").css("background-color","white");
									$("#customerFilePath").css("color","black");
								}
							},
							error: function(data) {
								alert("Bestand "+loadedFile.name+" kan niet worden geüpload.");
								console.log( data);
								pendingFiles-=1;
								if(pendingFiles == 0){
									writeCustomerDetails();
									$("#customerFilePath").css("background-color","white");
									$("#customerFilePath").css("color","black");
								}
							}
						})
						return false;
					}else{
						window.location = 'https://avab.nuocanvas.ai';
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

function placeActionButtonHandler(){

	if($("#placeActionButton").val() ==="Nieuwe Klant"){

		updateCustomerDetails();
	}else if($("#placeActionButton").val() ==="Klant Opslaan"){

		if(activeLocationDetails && activePlaceDetails){
			saveCustomerDetails();
		}
		removeActiveMarker();
		createMarkerCircles(nuoCustomers);
	}
}

function deleteCustomerButtonHandler(){
	if(confirm("Weet u zeker dat u de klant en bestanden wilt verwijderen?"))
		deleteCustomerOnActiveLocation();
}

function customerFilesButtonHandler(){
	loadCustomerDirWindow(getCustomerName(activeCustomerDetails));
}

function resetPlaceButtons(){
	$("#placeActionButton").val("Nieuwe Klant")
	$("#deleteCustomerButton").hide();
	$("#customerFilesButton").hide();
}

function saveCustomerDetails(){

	isExplicitSave = true;
	var customerDetails =  $("#customerDetailsGrid").jqxTreeGrid("getRows");

	var existingCustomer = findCustomerOnActiveLocation();
	if(existingCustomer){

		existingCustomer.customerDetails = customerDetails;
		existingCustomer.customerDetailsFlat = flattenCustomerDetails(customerDetails);
		existingCustomer.placeDetails = activePlaceDetails;
	}else{
		var newCustomer =
			{
				id: "Customer_"+ Date.now(),
				locationDetails: activeLocationDetails,
				placeDetails: activePlaceDetails,
				customerDetails: customerDetails,
				customerDetailsFlat: flattenCustomerDetails(customerDetails),
				files: [
					{
						fileName: "Offerte/"+systemFileLabel,
						author: "Systeem",
						sizeBytes: 0,
						dateCreatedMillis: Date.now(),
						dateModifiedMillis: Date.now()
					},{
						fileName: "Facturen/"+systemFileLabel,
						author: "Systeem",
						sizeBytes: 0,
						dateCreatedMillis: Date.now(),
						dateModifiedMillis: Date.now()
					},{
						fileName: "Overige/"+systemFileLabel,
						author: "Systeem",
						sizeBytes: 0,
						dateCreatedMillis: Date.now(),
						dateModifiedMillis: Date.now()
					}
				]
			};
		nuoCustomers.push(newCustomer);
		$('#customerFilesButton').prop('disabled', false)
		$('#deleteCustomerButton').prop('disabled', false)
	}
	writeCustomerDetails();
	isExplicitSave = false;
}

function writeCustomerDetails(){

	directCall(
		"rt5",
		"NuoCrmUserMetadata",
		nuoCustomers
		.map(function(ele){
			var updEle = ele;
			updEle.customerDetails = transformCustomerDetail(ele.customerDetails);
			return updEle;
		})
		.map(function(ele){return JSON.stringify(ele)})
		.join("|~|")
	)
	.done(function(data){
		if(data.StatusCode && data.StatusCode === 200){
			if(isExplicitSave)
				alert("Klantgegevens Opgeslagen.");
		}else{
			window.location = 'https://avab.nuocanvas.ai';
		}
	})
}

function transformCustomerDetail(customerDetails){
	var updCustomerDetail = customerDetails.map(function(ele){
			var detail = {
					id: ele.id,
					property: ele.property,
					value: ele.value,
					type: ele.type
			};
			if(ele.children && ele.children.length > 0){
				detail.children = transformCustomerDetail(ele.children);
			}
			if(ele.sourceValues && ele.sourceValues.length > 0){
				detail.sourceValues = ele.sourceValues;
			}
		return detail;
	});
	return updCustomerDetail;
}

function findCustomerOnActiveLocation(){
	var customer = nuoCustomers.find(function(cust){ return JSON.stringify(cust.locationDetails) == JSON.stringify(activeLocationDetails);});
	return customer;
}

function deleteCustomerOnActiveLocation(){
	var fileToBeDeleted = getCustomer(activeCustomerDetails).files;
	deleteFiles(fileToBeDeleted,0);
	nuoCustomers =
		nuoCustomers
		.filter(function(cust){
			return JSON.stringify(cust.locationDetails) != JSON.stringify(activeLocationDetails)
		});
	writeCustomerDetails();
	// createMarkerCircles();
	clearCustomerBanner();
	$("#customerDirWindow").hide("fast");
	initMap(mapHolder.getCenter(),mapHolder.getZoom());
}

function searchButtonHandler(){

	clearCustomerBanner();
	var query = $("#searchBox").val();
	var prefix = checkIfStartsWithArrayElement(query.toLowerCase(),customerSearchKeywords);
	if(prefix){
		searchCustomers(query.toLowerCase(),prefix.length);
	}else{
		searchAddress(query);
	}
}

function checkIfStartsWithArrayElement(inputText, paramArray){
	for(var i=0; i < paramArray.length; i++){
		if(inputText.startsWith(paramArray[i])){
			return paramArray[i];
		}
	}
	return null;
}

function searchAddress(address){
	
	if(address && address.length > 0 && address.trim().length > 0){

		if(!geocoderHolder) {
			geocoderHolder = new google.maps.Geocoder();
		}
		var geoCoderCallback =
			function(results, status) {

				if (status === "OK") {
					placeMarkerAndPanTo(results[0].geometry.location,results[0].place_id,true);
 				} else {
					alert("Geocode was not successful for the following reason: " + status);
				}
			};
		var geocodeRequest = {
			"address": address
		};
		geocoderHolder.geocode(
			geocodeRequest,
			geoCoderCallback
		);
	}
}

function searchCustomers(paramQuery,prefixLength){
	
	var query = paramQuery.substring(prefixLength);
	
	query = removeConsecutiveWS(query);
	
	var regex = /"([^\"]+)"/g;
	
	var match = regex.exec(query);
	var counter = 0;
	while(match){
		console.log(match[0]);
		
		var maskedValue = "__match_"+counter;
		maskedOperands[maskedValue] = match[1].trim();
		
		query = query.replace(match[0],maskedValue);
		
		match = regex.exec(query);
		counter+=1;
	}
	var conditionDetails = 
		query
			.split(/\s+en\s+/)
			.map(function(condition){
				return parseCondition(condition);
			});
	initMap(entryPosition,entryZoomLevel,applyConditions(conditionDetails));
}

function flattenCustomerDetails(customerDetails){
	
	var result = [];
	customerDetails
		.forEach(function(detail){
			
			var resultDetail = {};
			resultDetail.id = detail.id;
			resultDetail.type = detail.type;
			resultDetail.property = detail.property;
			resultDetail.value = detail.value;
			
			if(resultDetail.type === "date" && resultDetail.value)
				resultDetail.value = resultDetail.value.toDateMillis();
			else if(resultDetail.value && resultDetail.value.toNumber()){
				resultDetail.type = "number";
				resultDetail.value = resultDetail.value.toNumber();
			}else
				resultDetail.value = resultDetail.value;
			
			result.push(resultDetail);
			if(detail.children && detail.children.length > 0){
				result = result.concat(flattenCustomerDetails(detail.children));
			}
		})
	return result;
}

function parseCondition(paramConditionText){
	
	var conditionText = paramConditionText.trim();
	
	var conditionDetails = {
			input: conditionText,
			isDate: false,
			isNegation: false,
			isNumber: false,
			operatorType: null,
			operands: []
		}
	
	var prefix = checkIfStartsWithArrayElement(conditionText,conditionPositivePrefix);
	if(prefix){
		conditionText = conditionText.substring(prefix.length).trim();
	}
	
	prefix = checkIfStartsWithArrayElement(conditionText,conditionNegativePrefix);
	if(prefix){
		conditionText = conditionText.substring(prefix.length).trim();
		conditionDetails.isNegation = true;
	}

	var operator = checkIfContainsArrayElement(conditionText,conditionOpGtEq);
	if(operator){
		conditionDetails.operatorType = conditionOperatorType.GT_EQ;
	}else{
		operator = checkIfContainsArrayElement(conditionText,conditionOpLtEq);
		if(operator){
			conditionDetails.operatorType = conditionOperatorType.LT_EQ;
		}else{
			operator = checkIfContainsArrayElement(conditionText,conditionOpEq);
			if(operator){
				conditionDetails.operatorType = conditionOperatorType.EQ;
			}else{
				operator = checkIfContainsArrayElement(conditionText,conditionOpGt);
				if(operator){
					conditionDetails.operatorType = conditionOperatorType.GT;
				}else{
					operator = checkIfContainsArrayElement(conditionText,conditionOpLt);
					if(operator){
						conditionDetails.operatorType = conditionOperatorType.LT;
					}else{
						operator = checkIfContainsArrayElement(conditionText,conditionOpContains);
						if(operator){
							conditionDetails.operatorType = conditionOperatorType.CONTAINS;
						}else{
							operator = checkIfContainsArrayElement(conditionText,conditionOpFrom);
							if(operator){
								conditionDetails.operatorType = conditionOperatorType.FROM;
							}else{
								operator = conditionOperatorType.DEFAULT;
								conditionDetails.operatorType = conditionOperatorType.DEFAULT;
							}
						}
					}
				}	
			}
		}
	}

	if(operator){
		var operands = 
			conditionText
				.split(operator)
				.map(function(operand){
					var maskedValue = operand.trim();
					if(maskedOperands[maskedValue]) 
						return maskedOperands[maskedValue];
					else 
						return maskedValue;
				});
		
		if(operands.length > 2) 
			alert("'"+conditionText + "' is geen geldige voorwaarde.");
		else{
			if(conditionDetails.operatorType == conditionOperatorType.DEFAULT)
				conditionDetails.operands.push(operands[0]);
			else if( conditionDetails.operatorType == conditionOperatorType.FROM)
				conditionDetails.operands.push(operands[1]);
			else{
				conditionDetails.operands.push(operands[0]);
				if(operands.length > 1 && operands[1].toNumber()){
					conditionDetails.isNumber = true;
					conditionDetails.operands.push(operands[1].toNumber());
				}else if(operands.length > 1 && operands[1].toDateMillis()){
					conditionDetails.isDate = true;
					conditionDetails.operands.push(operands[1].toDateMillis());
				}else
					conditionDetails.operands.push(operands[1]);
			}
		}
	}else
		alert("'"+conditionText + "' is geen geldige voorwaarde.");
	
	return conditionDetails;
}

function applyConditions(conditionDetails){
	
	var filteredCustomeres = 
		nuoCustomers
			.filter(function(customer){
				var filteredConditions = 
					conditionDetails
						.filter(function(conditionDetail){
							var operands = conditionDetail.operands;
							if(operands.length == 0 ){
								return false;
							}else if(operands.length > 1){
								var conditionEval = false;
								var filteredDetails = 
									customer
										.customerDetailsFlat
										.filter(function(detail){
											
											var result =  standardize(detail.property) === standardize(operands[0]);
											
											if(result && detail.value){
												if(result){
													if(detail.type === "string" || detail.type === "array"){
														
														if(result && conditionDetail.operatorType == conditionOperatorType.FROM){
												
															result = standardize(customer.placeDetails.placeAddress).indexOf(standardize(operands[0])) >= 0;
														} else if(result && conditionDetail.operatorType == conditionOperatorType.CONTAINS){
															
															result = standardize(detail.value).indexOf(standardize(operands[1])) >= 0;
														} else 
															
															result = eval("'" + standardize(detail.value) + "'" + conditionDetail.operatorType + "'" + standardize(operands[1]) + "'");
													} else
														result = eval(detail.value + conditionDetail.operatorType + operands[1]);
												}
											}else 
												result = false;
											
											return result;
										})
								conditionEval =  filteredDetails.length > 0;
							}else{
								if(conditionDetail.operatorType == conditionOperatorType.FROM){
									conditionEval =  standardize(customer.placeDetails.placeAddress).indexOf(standardize(operands[0])) >= 0;
								}else{
									var filteredDetails = 
										customer
											.customerDetailsFlat
											.filter(function(detail){

												return standardize(detail.property) === standardize(operands[0])
													&& detail.value;
											})
									conditionEval =  filteredDetails.length > 0;
								}
							}
							
							if(conditionDetail.isNegation)
								return !conditionEval;
							else 
								return conditionEval;
						});
						
				return filteredConditions.length == conditionDetails.length;
			});
	return filteredCustomeres;
}

function checkIfContainsArrayElement(inputText, paramArray){
	for(var i=0; i < paramArray.length; i++){
		if((" "+inputText).indexOf(" "+paramArray[i]+" ") >= 0){
			return paramArray[i];
		}
	}
	return null;
}

function clearCustomerBanner(){
	$("#customerDetailsGrid").hide();
	hideCustomerBanner();
	activeLocationDetails = null;
	activePlaceDetails = null;
}

function showCustomerBanner(){
	$("#placeBanner").show("fast");
	$("#customerBanner").show("fast");
	$("#searchCloseButton")[0].value = '\u25c4';
	$("#searchCloseButton").jqxToggleButton('check');
}

function hideCustomerBanner(){
	$("#placeBanner").hide("fast");
	$("#customerBanner").hide("fast");
	$("#searchCloseButton")[0].value = '\u25ba';
	$("#searchCloseButton").jqxToggleButton('unCheck');
}

function searchCloseButtonHandler(){

	var toggled = $("#searchCloseButton").jqxToggleButton('toggled');
	if (toggled) {
		showCustomerBanner();
	}else {
		hideCustomerBanner();
	}
}

function removeActiveMarker(){
	if(activeMarker){

		activeMarker.setMap(null);
		activeMarker = null;
	}
}

function mapResetButtonHandler(){

	hideCustomerBanner();
	removeActiveMarker();
	initMap(entryPosition,entryZoomLevel);
}

function initMap(paramCenter,paramZoomLevel,customers){

	var mapOptions =
		{
			center: entryPosition,
			clickableIcons: true,
			zoom: entryZoomLevel,
			mapTypeControlOptions: {
				style: google.maps.MapTypeControlStyle.DROPDOWN_MENU,
				position: google.maps.ControlPosition.TOP_RIGHT
			}
		};

	if(paramCenter) mapOptions.center = paramCenter;
	if(paramZoomLevel) mapOptions.zoom = paramZoomLevel;

	mapHolder = new google.maps.Map(document.getElementById("mapContainer"), mapOptions);
	if(customers) 
		createMarkerCircles(customers);
	else
		createMarkerCircles(nuoCustomers);
	addMapMouseListeners();
}

function addMapMouseListeners(){

	mapHolder.addListener('click', function(e) {

		e.stop();
		clearCustomerBanner();
		$("#searchBox").val("");

		if(e.placeId){
			stop();
			placeMarkerAndPanTo(e.latLng,e.placeId,false);
		}else{
			if(!geocoderHolder) {
				geocoderHolder = new google.maps.Geocoder();
			}
			var geoCoderCallback =
				function(results, status) {

					if (status === "OK") {
						placeMarkerAndPanTo(e.latLng,results[0].place_id,false);
					} else {
						alert("Geocode was not successful for the following reason: " + status);
					}
				};
			var geocodeRequest = {
				"location": e.latLng
			};
			geocoderHolder.geocode(
				geocodeRequest,
				geoCoderCallback
			);
		}
  });
	mapHolder.data.addListener('click', function(event) {
		clearCustomerBanner();
		$("#searchBox").val("");
		placeMarkerAndPanTo(event.feature.getProperty('location'),null,true);
	});
}

function createMarkerCircles(customers){

	var mapData = {
		type: "FeatureCollection",
		features:
			customers
			.map(function(customer){
				
				var addr = customer.placeDetails.placeAddress.split(",");
				var featureObj = {
					type: "Feature",
					properties:{
						name: getCustomerInternalName(customer.customerDetails),
						city: addr[addr.length-1].trim(),
						customerDetails: customer.customerDetails,
						location: customer.locationDetails
					},
					geometry:{
						type: "Point",
						coordinates: [customer.locationDetails.lng,customer.locationDetails.lat]
					},
					id: customer.id
				};
				return featureObj;
			})
	}

	mapHolder.data.addGeoJson(mapData);

	mapHolder.data.setStyle(function(feature) {
		return {
			icon: getCircle(feature),
			title: feature.getProperty("name") + " (" + feature.getProperty("city") +")"
		};
	});
	function getCircle(feature) {
		return {
			path: google.maps.SymbolPath.CIRCLE,
			fillColor: getMarkerColor(feature),
			fillOpacity: .95,
			scale: markerCircleSize,
			strokeColor: 'black',
			strokeWeight: .7
		};
	}

}

function getMarkerColor(feature){
	
	var markerColor = markerColorGreen;
	var currDateMillis = Date.now();
	var activities = getActivities(feature.getProperty("customerDetails"))
	
	if(activities && activities.children && activities.children.length > 0){
		
		var incompleteActivities = 
			activities
			.children
			.filter(function(activity){
				
				var statusValue  = null;
				
				var statusProperty  = 
					activity.children.find(function(child){
						return child.property === colorCriStatusName;
					});
					
				if(statusProperty) statusValue = statusProperty.value;
				return statusProperty && statusValue && statusValue !== colorCriStatusComplete;
			});
			
		if(incompleteActivities && incompleteActivities.length > 0){
			
			var minDateActivity = null;
			incompleteActivities
				.forEach(function(activity){
					
					var dateValue = null;
					var dateProperty = 
						activity
							.children
							.find(function(child){
								return child.property === colorCriDateName;
							});
					if(dateProperty && dateProperty.value)
						dateValue = dateProperty.value.toDateMillis()

					if(dateValue && (!minDateActivity || dateValue < minDateActivity.dateValue)){
						activity.dateValue = dateValue;
						minDateActivity = activity;
					}
				});
				
			if(minDateActivity){
				
				if(minDateActivity.dateValue && minDateActivity.dateValue < currDateMillis) // activity date value is in past
					markerColor = markerColorRed;
				else if(minDateActivity.dateValue && minDateActivity.dateValue > currDateMillis && minDateActivity.dateValue < currDateMillis + (30 * 24 * 3600 * 1000)) // activity date value is less than 30 days in future
					markerColor = markerColorOrange;
				// else if(minDateActivity.dateValue && minDateActivity.dateValue > currDateMillis && minDateActivity.dateValue > currDateMillis + (30 * 24 * 3600 * 1000)) // activity date value is more than 30 days in future
					// markerColor = markerColorBlue;
			}
		}
	}
	return markerColor;
}

function getActivities(customerDetails){
	
	if(customerDetails){
		
		var result = customerDetails
			.find(function(detail){
				return detail.property === activityPropertyName;
			})
		return result;
	}else null;
}

function getCustomerId(customerDetails){
	var customerId =  getCustomer(customerDetails).id;
	return customerId;
}

function getCustomer(customerDetails){
	var customer =
		nuoCustomers
		.find(function(ele){
			return getCustomerName(ele.customerDetails) === getCustomerName(customerDetails)
		});
	return customer;
}

function getCustomerName(customerDetails){
	
	var customerName = 
		customerDetails
			.find(function(ele){
				return ele.property === "Organisatienaam"
			})
			.value;
	return customerName;
}

function getCustomerInternalName(customerDetails){
	
	var customerName = 
		customerDetails
			.find(function(ele){
				return ele.property === "Interne naam"
			})
			.value;
	return customerName;
}

function placeMarkerAndPanTo(location, placeId, shouldZoom) {

	if(location && location.lat && location.lng){
		removeActiveMarker();
		activeLocationDetails = JSON.parse(JSON.stringify(location));
		var existingCustomer = findCustomerOnActiveLocation();

		if(existingCustomer){

			$("#placeActionButton").val("Klant Opslaan");
			activePlaceDetails = existingCustomer.placeDetails;
			updateCustomerPlaceDetails(existingCustomer.customerDetails.customerName);
			updateCustomerDetails(existingCustomer);
		}else{
			resetPlaceButtons();
			var marker = new google.maps.Marker({
				map: mapHolder,
				position: location
			});
			activeMarker = marker;

			if(placeId) getAndUpdatePlaceDetails(placeId);
		}

		if(shouldZoom){
			mapHolder.setZoom(searchZoomLevel);
			mapHolder.setCenter(location);
		}
	}
}

function getAndUpdatePlaceDetails(placeLocator){

	var placesRequest = null;
	if(placeLocator.latLng && placeLocator.latLng.lat && placeLocator.latLng.lng){
		placesRequest = {
			placeId: placeLocator
		};

	}else if(placeLocator && placeLocator.length>0){

		placesRequest = {
			placeId: placeLocator
		};
		if(!placesServiceHolder){
			placesServiceHolder = new google.maps.places.PlacesService(mapHolder);
		}

		placesRequest = {
			placeId: placeLocator
		};

		var placesServiceCallback =
			function(place, status) {
				if (status == google.maps.places.PlacesServiceStatus.OK) {

					activePlaceDetails = {
						placeName: place.name,
						placeAddress: getAddress(place.address_components),
						placePhone: place.international_phone_number,
						placeWebsite: place.website,
						placeOpeningHours: place.opening_hours
					}
					updateCustomerPlaceDetails();
				}else{
					console.log("Received status '"+ status+ "' while finding details for "+placeLocator);
				}
			};
		placesServiceHolder.getDetails(placesRequest, placesServiceCallback);
	}
}

function updateCustomerPlaceDetails(customerName){

	if(activePlaceDetails){

		if(customerName)
			$("#placeName").html(customerName);
		else if(activePlaceDetails.placeName)
			$("#placeName").html(activePlaceDetails.placeName);
		else
			$("#placeName").html("Geen naam beschikbaar");

		if(activePlaceDetails.placeAddress)
			$("#placeAddress").html(activePlaceDetails.placeAddress);
		else
			$("#placeAddress").html("Geen adres beschikbaar");

		if(activePlaceDetails.placePhone)
			$("#placePhone").html(activePlaceDetails.placePhone);
		else
			$("#placePhone").html("Geen telefoon beschikbaar");

		if(activePlaceDetails.placeWebsite)
			$("#placeWebsite").html("<a href='"+activePlaceDetails.placeWebsite+"' target='_blank'>"+activePlaceDetails.placeWebsite+"</a>");
		else
			$("#placeWebsite").html("Geen website beschikbaar");

		if(activePlaceDetails.placeOpeningHours && activePlaceDetails.placeOpeningHours.open_now && activePlaceDetails.placeOpeningHours.weekday_text){

			var workingHoursOptions = activePlaceDetails.placeOpeningHours.weekday_text;
			workingHoursOptions.unshift(workingHoursOptions.pop());

			var dayIndex = -1;
			workingHoursOptions =
			workingHoursOptions
			.map(function(ele){
				dayIndex+=1;
				return "<option value='"+dayIndex+"'>"+ele+"</option>";
			})
			.join(" ") + "</select>";
			var scheduleHtml = "";
			if(activePlaceDetails.placeOpeningHours.open_now){
				scheduleHtml += "<span>Nu geopend </span>";
				$("#placeWorkingHours").css("color","green");
			}else{
				scheduleHtml += "<span>Gesloten </span>";
				$("#placeWorkingHours").css("color","red");
			}
			scheduleHtml += "<select id = 'schedule'>"+workingHoursOptions+"</select>"
			$("#placeWorkingHours").html(scheduleHtml);
			$("#schedule").val(new Date().getDay());
		} else
			$("#placeWorkingHours").html("Geen openingstijden beschikbaar");

		showCustomerBanner();
	}
}

function updateCustomerDetails(existingCustomer){

	if(existingCustomer)
		activeCustomerDetails = existingCustomer.customerDetails;
	else{
		activeCustomerDetails = getNewCustomerDetails();
		activeCustomerDetails.find(function(ele){
			return ele.property == 'Organisatienaam';
		})
		.value = activePlaceDetails.placeName;
		activeCustomerDetails.find(function(ele){
			return ele.property == 'Interne naam';
		})
		.value = activePlaceDetails.placeName;
		document.getElementById("customerFilesButton").disabled = true;
		document.getElementById("deleteCustomerButton").disabled = true;
	}
	$("#placeActionButton").val("Klant Opslaan");
	$("#deleteCustomerButton").show();
	$("#customerFilesButton").show();
	$("#customerDetailsGrid").show();
	refreshCustomerDetailsGrid();
}

function refreshCustomerDetailsGrid(){

	$("#customerDetailsGrid").jqxTreeGrid(
	{
		source: getCustomersGridSource()
	});
	$("#customerDetailsGrid").jqxTreeGrid("updateBoundData");

}

function getCustomersGridSource(){
	var source =
		{
			dataType: "json",
			dataFields: [
				{ name: "id", type: "string" },
				{ name: "property", type: "string" },
				{ name: "value", type: "string" },
				{ name: "type", type: "string" },
				{ name: "sourceValues", type: "array" },
				{ name: "children", type: "array" }
			],
			hierarchy:
			 {
					 root: "children"
			 },
			localData: activeCustomerDetails
		};
	return new $.jqx.dataAdapter(source);
}

function getAddress(address_components){

	var streetNumber =
		address_components
		.find(function(ele){
			return ele.types[0].toLowerCase() === "street_number";
		});

	var streetName =
		address_components
		.find(function(ele){
			return ele.types[0].toLowerCase() === "route";
		});

	var city =
		address_components
		.find(function(ele){
			return ele.types[0].toLowerCase() === "locality";
		});

	var country =
		address_components
		.find(function(ele){
			return ele.types[0].toLowerCase() === "country";
		});

	var postalCode =
		address_components
		.find(function(ele){
			return ele.types[0].toLowerCase() === "postal_code";
		});

	var addressHtml = "";

	if(streetName && streetName.long_name)
		addressHtml += streetName.long_name + " ";

	if(streetNumber && streetNumber.long_name)
		addressHtml += streetNumber.long_name + ", ";

	if(postalCode && postalCode.long_name)
		addressHtml += postalCode.long_name + ", ";

	if(city && city.long_name)
		addressHtml += city.long_name;

	if(country && country.long_name)
		// addressHtml += country.long_name;

	return addressHtml;
}

function loadCustomerDirWindow(customerName){

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
	$("#customerDirWindow").jqxWindow("setTitle","Bestanden opgeslagen voor "+customerName);
	$("#customerFilePath").html("/");
	loadCustomerFilesTable(getFileList(""));
	$("#customerDirWindow").jqxWindow("open");
}

function getFileList(parentPath){

	var fileElements = [];

	var existingCustomer = getCustomer(activeCustomerDetails);

	if(existingCustomer){

		existingCustomer
		.files
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
					fileElement.fileType = "Map";
				}else{
					fileElement.fileLabel = decodeURI(fileName.substring(parentPath.length));
					fileElement.filePath = fileName;
					fileElement.isDirectory = false;

					if(fileElement.fileLabel.indexOf(".") > 0){
						fileElement.isDirectory = false;
						fileElement.fileType = fileElement.fileLabel.substring(fileElement.fileLabel.lastIndexOf(".")+1).toUpperCase() + " Bestand";
					}
				}
				fileElement.author =  fileObj.author;
				fileElement.sizeBytes =  fileObj.sizeBytes;
				fileElement.dateCreated = new Date(parseInt(fileObj.dateCreatedMillis));
				fileElement.dateModified = new Date(parseInt(fileObj.dateModifiedMillis));
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
	}
	return fileElements;
}

function loadCustomerFilesTable(fileList){

	// if(fileList != null && fileList.length > 0){
		var gridColumns = [
			{ text: 'Bestandsnaam', dataField: 'fileLabel', width: '40%', height: 20 },
			{ text: 'Auteur', dataField: 'author', width: '10%', height: 20 },
			{ text: 'Grootte', dataField: 'sizeLabel', width: '10%', cellsalign: 'right', height: 20 },
			{ text: 'Type', dataField: 'fileType', width: '15%', height: 20 },
			{
				text: 'Datum Gewijzigd',
				dataField: 'dateModified',
				width: '25%',
				height: 20,
				cellsformat: 'yyyy-MM-dd  HH:mm:ss.fff'
			}
		];
		renderJqxGrid('#customerFilesTable', fileList, gridColumns);
		$("#customerFilesTable").show();
		$("#customerFilePath").show();
	// }
}

function getSizeLabel(sizeBytes){
	if(sizeBytes >=1024 * 1024 * 1024 * 1024.0){
			return  (sizeBytes/1024.0/1024.0/1024.0/1024.0).toFixed(2) + ' TB';
	}else if(sizeBytes >=1024 * 1024 * 1024.0){
			return  (sizeBytes/1024.0/1024.0/1024.0).toFixed(2) + ' GB';
	}else if(sizeBytes >=1024 * 1024.0){
			return  (sizeBytes/1024.0/1024.0).toFixed(2) + ' MB';
	}else if(sizeBytes >=1024.0){
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
		loadCustomerFilesTable(getFileList(grandParentPath));
		if(grandParentPath.length == 0)
			$("#customerFilePath").html("/");
		else
			$("#customerFilePath").html(grandParentPath);
		$('#customerFilesTable').jqxGrid('clearselection');
	});
	$('#customerFilesTable').on('rowdoubleclick', function (event){

		var clickedIndex = event.args.rowindex;
		var rowData = $('#customerFilesTable').jqxGrid('getrowdata', clickedIndex);

		if(rowData.isDirectory){

			loadCustomerFilesTable(getFileList(rowData.filePath));
			$("#customerFilePath").html(rowData.filePath);

			if(rowData.filePath.length == 0)
				$("#customerFilePath").html("/");
		}else{
			var fileToBeDownloaded = rowData.fileLabel;

			if(!fileToBeDownloaded.endsWith(systemFileLabel)){

				var parentPath = rowData.parentPath;
				if(parentPath && parentPath.length > 0 && parentPath!=="/"){
					fileToBeDownloaded = parentPath + fileToBeDownloaded;
				}
				var customerId = getCustomerId(activeCustomerDetails);
				if(customerId){
					fileToBeDownloaded = "CRM/UserFiles/" + customerId + "/" + fileToBeDownloaded;
				}
				directCall("rt11", "NuoFileContent", {FileName: fileToBeDownloaded}
				)
				.done(function(data){
					if(data.StatusCode && data.StatusCode === 200){
						var downloadEle = document.getElementById("downloadTag");
						downloadEle.setAttribute("href",data.Content);
						downloadEle.click();
					}else{
						window.location = 'https://avab.nuocanvas.ai';
					}
				})
			}
		}
		$('#customerFilesTable').jqxGrid('clearselection');
	});
}

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
    selectionMode: 'multiplerows',
    source: dataAdapter,
    columns: gridColumns,
		width: "100%",
		height: 400,
    autoheight: true,
    autorowheight: true,
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

function createCustomerDetailsGrid(){

	var cellsRenderer= function (row, column, value, rowData) {
		var container = '<div class="tooltip">' + value + '</div>';
        return container;
    }

	$("#customerDetailsGrid").jqxTreeGrid(
	{
		source: getCustomersGridSource(),
		width: "98%",
		altRows: true,
		autoRowHeight: false,
		editable: true,
		editSettings: {
			saveOnPageChange: true,
			saveOnBlur: true,
			saveOnSelectionChange: true,
			cancelOnEsc: true,
			saveOnEnter: true,
			editSingleCell: true,
			editOnDoubleClick: true,
			editOnF2: true
		},
		showHeader: true,
		ready: function(){
			$(".tooltip").jqxTooltip({position: 'mouse', content: $(this).text() });
				attachTooltip();
		},
		columnsResize: true,
		columns: [
			{
				text: 'Attribuut', dataField: 'property', width: "40%", height: 15, columnType: "custom", editable: true,
				createEditor: function (rowNum, cellvalue, editor, cellText, width, height) {
					var row = $("#customerDetailsGrid").jqxTreeGrid('getRow', rowNum);
					var input = $("<input id='textbox"+rowNum+"' class='textbox' style='border: none;'/>").appendTo(editor);
					editor.jqxInput({ width: '100%', height: '100%' });

				},
				initEditor: function (rowNum, cellvalue, editor, celltext, width, height) {
					var row = $("#customerDetailsGrid").jqxTreeGrid('getRow', rowNum);
					editor.children(0).val(cellvalue);
				},
				getEditorValue: function (rowNum, cellvalue, editor) {
					// return the editor's value.
					var row = $("#customerDetailsGrid").jqxTreeGrid('getRow', rowNum);
					return editor.children(0).val();
				}
			},
			{
				text: 'Waarde', dataField: 'value', width: "60%", height: 15, columnType: "custom", editable: true,
				cellsRenderer: cellsRenderer,
				createEditor: function (rowNum, cellvalue, editor, cellText, width, height) {
					 // construct the editor.
					var row = $("#customerDetailsGrid").jqxTreeGrid('getRow', rowNum);
					switch (row.type) {
						case "hide":
							break;

						case "string":
							 var input = $("<input id='textbox"+rowNum+"' class='textbox' style='border: none;'/>").appendTo(editor);
							 editor.jqxInput({ width: '100%', height: '100%' });
							 break;

						case "array":
							if(row.sourceValues){
								// editor.jqxDropDownList({autoDropDownHeight: true, source: row.sourceValues, placeHolder: 'selecteer', width: '100%', height: '100%' });
								editor.jqxDropDownList( {autoDropDownHeight: true, source: row.sourceValues, width: '100%', height: '100%' });
								editor.jqxDropDownList( { placeHolder: 'Selecteer', enableBrowserBoundsDetection: true });
								editor.jqxDropDownList("selectItem",row.value);
							}
							break;

						case "date":
							var dropDownButton = $("<div class='dropDownButton"+rowNum+"' style='border: none;'><div style='padding: 5px;'><div class='datePicker" + rowNum + "'></div></div></div>");
							dropDownButton.appendTo(editor);
							dropDownButton.jqxDropDownButton({ width: '100%', height: '100%',dropDownVerticalAlignment: 'top' });
							var datePicker = $($.find(".datePicker" + rowNum));

							if(row.value){

							 datePicker.jqxDateTimeInput({value: new Date(row.value.toDateMillis()),showFooter: true});
							 dropDownButton.jqxDropDownButton('setContent', row.value);

							}else{

							 datePicker.jqxDateTimeInput({ showFooter: true });
							 dropDownButton.jqxDropDownButton('setContent', new Date());
							}
							datePicker.on('change', function (event) {
							 var date = event.args.date;
							 dropDownButton.jqxDropDownButton('setContent', date);
							});
							break;
					}
				},
				initEditor: function (rowNum, cellvalue, editor, celltext, width, height) {
					// set the editor's current value. The callback is called each time the editor is displayed.
					var row = $("#customerDetailsGrid").jqxTreeGrid('getRow', rowNum);
					switch (row.type) {
						case "string":
							editor.children(0).val(cellvalue);
							break;

						case "date":
							editor.jqxDropDownButton('setContent', cellvalue);
							break;

						case "array":
							editor.jqxDropDownList('selectItem', cellvalue);
					}
				},
				getEditorValue: function (rowNum, cellvalue, editor) {
					// return the editor's value.
					var row = $("#customerDetailsGrid").jqxTreeGrid('getRow', rowNum);
					switch (row.type) {
						case "string":
							return editor.children(0).val();
							break;

						case "date":
							return $($.find('.datePicker' + rowNum)).val();
							break;

						case "array":
						    var item = editor.jqxDropDownList('getSelectedItem')
							if (item) return item.label
							else return cellvalue
					}
				}
			}
		]
	});


	$('#customerDetailsGrid').on('rowClick rowExpand rowCollapse', function (event) {
		attachTooltip();
	});

	function attachTooltip(){
		$(".tooltip").jqxTooltip({position: 'mouse', content: $(this).text() });
		$(".tooltip").on("mouseenter", function () {
            $(this).jqxTooltip({position: 'mouse', content: $(this).text() });
        });
	}

	$("#customerDetailsGrid").on('contextmenu', function () {
			return false;
	});
	$("#customerSettingsButton").on('click', customerSettingsClickHandler);
	$("#customerDetailsGrid").on('rowClick', customerGridRightClickHandler);
	$("#activityMenu").on('itemclick', customerGridContextMenuHandler);
}

function customerSettingsClickHandler(event) {
	
	var scrollTop = $(window).scrollTop();
	var scrollLeft = $(window).scrollLeft();
	// create context menu
	var contextMenu = $("#activityMenu").jqxMenu({ width: 300, height: 135, autoOpenPopup: false, mode: 'popup' });
	contextMenu.jqxMenu('open', parseInt(event.clientX) + 5 + scrollLeft, parseInt(event.clientY) + 5 + scrollTop);
	return false;
}

function customerGridRightClickHandler(event) {
	
		var args = event.args;
		if (args.originalEvent.button == 2) {
			var scrollTop = $(window).scrollTop();
			var scrollLeft = $(window).scrollLeft();
			var contextMenuHeight = 120;
			// create context menu
			var contextMenu = $("#activityMenu").jqxMenu({ width: 300, height: 135, autoOpenPopup: false, mode: 'popup' });
			contextMenu.jqxMenu('open', parseInt(event.args.originalEvent.clientX) + 5 + scrollLeft, parseInt(event.args.originalEvent.clientY) + 5 + scrollTop - contextMenuHeight);
			return false;
		}
	}

function isRightClick(event) {
	
	var rightclick;
	if (!event) var event = window.event;
	if (event.which) rightclick = (event.which == 3);
	else if (event.button) rightclick = (event.button == 2);
	return rightclick;
}

function customerGridContextMenuHandler(event) {

	var args = event.args;
	var selection = $("#customerDetailsGrid").jqxTreeGrid('getSelection');
	var menuItem = $.trim($(args).text());

	if ( menuItem == "Attribuut Verwijderen") {
		selection.forEach(function(row){
			$("#customerDetailsGrid").jqxTreeGrid('deleteRow', row.uid);
		});
	}else {

		var row = selection[0];
		$("#customerDetailsGrid").jqxTreeGrid('expandRow', row.uid);

		var newStatusDetails =
			{
				id: Date.now()+"_s_"+activeUsername,
				property: "Status",
				value: "Onbekend",
				type: "array",
				sourceValues:[
					"Onbekend",
					"Gepland",
					"Opgeleverd"
				]
			};
			var newProductDetails =
			{ id: Date.now()+"_p0_"+activeUsername, property: "Nieuw Product", value: "", type: "string",
				children: [
					{ id: Date.now()+"_p00_"+activeUsername, property: "Product Naam", value: "Onbekend", type: "array",
						sourceValues:[
							"Onbekend",
							"Brandblusser 1",
							"Brandblusser 2",
							"Brandblusser 3",
							"Brandblusser 4",
							"EHBO Kit",
							"Nooduitgang",
							"RI&E",
							"Evacuatieplan"
						]
					},
					{ id: Date.now()+"_p01_"+activeUsername, property: "Aantal Producten", value: "0", type: "string" },
					{ id: Date.now()+"_p02_"+activeUsername, property: "Prijs per Eenheid", value: "0", type: "string" }
				]
			};
		var newActivityDetails =
			{ id: Date.now()+"_a0_"+activeUsername, property: "Nieuwe Activiteit", value: "", type: "string",
				children: [
					{ id: Date.now()+"_a00_"+activeUsername, property: "Uitvoeringsdatum", value: "", type: "date" },
					newStatusDetails,
					newProductDetails
				]
			}

		switch(menuItem){
			case "Nieuw Tekst Attribuut":
				addPropertyToActiveCustomer(row, row.id,{ id: Date.now()+"_"+activeUsername, property: "Nieuwe Tekst Attribuut", value: "", type: "string"});
				break;

			case "Nieuw Datum Attribuut":
				addPropertyToActiveCustomer(row, row.id,{ id: Date.now()+"_"+activeUsername, property: "Nieuwe Datum Attribuut", value: "", type: "date"});
				break;

			case "Nieuwe Activiteit":
				addPropertyToActiveCustomer(row, row.id,newActivityDetails);
				break;

			case "Nieuw Product Attribuut":
				addPropertyToActiveCustomer(row, row.id,newProductDetails);
				break;

			case "Nieuw Status Attribuut":
				addPropertyToActiveCustomer(row, row.id,newStatusDetails);
				break;
		}
	}
}

function addPropertyToActiveCustomer(parentRow,parentPropertyId, newPropertyObj){

	var parentRowId = parentRow.uid;
	if(activeCustomerDetails){
		var parentProperty = null;

		for(var i=0; parentProperty == null && i < activeCustomerDetails.length; i++){
			var result = findProperty(parentPropertyId,activeCustomerDetails[i]);
			if(result)
				parentProperty = result;
		}
		if(parentProperty){

			if(!parentProperty.children)
				parentProperty.children = []

			parentProperty.children.push(newPropertyObj);
			refreshCustomerDetailsGrid();
			$("#customerDetailsGrid").jqxTreeGrid('collapseAll');
			expandRow(parentRow);
		}
	}
}

function findProperty(propertyId,propertyObj){

	if(propertyObj){

		if(propertyObj.id === propertyId)
			return propertyObj;
		else if(propertyObj.children && propertyObj.children.length > 0){

			for(var i=0; i < propertyObj.children.length; i++){

				var childResult = findProperty(propertyId,propertyObj.children[i]);
				if(childResult) return childResult;
			}
		}
	}
	return null;
}

function expandRow(row){
	$("#customerDetailsGrid").jqxTreeGrid('expandRow',row.uid);
	if(row.parent){
		expandRow(row.parent);
	}
}

function getNewCustomerDetails(){

	var newCustomerDetails = 
		[
			{
				"id": "96",
				"property": "Organisatienaam",
				"value": "",
				"type": "string"
			},
			{
				"id": "97",
				"property": "Interne naam",
				"value": "",
				"type": "string"
			},
			{
				"id": "98",
				"property": "Relatiecode",
				"value": "",
				"type": "string"
			},
			{
				"id": "99",
				"property": "Relatietype",
				"value": "Onbekend",
				"type": "array",
				"sourceValues": [
					"Onbekend",
					"Contactpersoon",
					"Leverancier",
					"Klant",
					"Zakelijke partner",
					"Medewerker"
				]
			},
			{
				"id": "100",
				"property": "Contactpersoon",
				"value": "",
				"type": "string",
				"children": [
					{
						"id": "101",
						"property": "Naam",
						"value": "",
						"type": "string"
					},
					{
						"id": "102",
						"property": "Functie",
						"value": "",
						"type": "string"
					},
					{
						"id": "103",
						"property": "Mobiele Telefoon",
						"value": "",
						"type": "string"
					},
					{
						"id": "104",
						"property": "E-mail",
						"value": "",
						"type": "string"
					}
				]
			},
			{
				"id": "105",
				"property": "Voorkeursadres",
				"value": "Onbekend",
				"type": "array",
				"sourceValues": [
					"Onbekend",
					"bezoekadres",
					"factuuradres",
					"postadres"
				]
			},
			{
				"id": "106",
				"property": "Telefoonnummer",
				"value": "",
				"type": "string"
			},
			{
				"id": "107",
				"property": "E-mail",
				"value": "",
				"type": "string"
			},
			{
				"id": "108",
				"property": "Fax",
				"value": "",
				"type": "string"
			},
			{
				"id": "109",
				"property": "Website",
				"value": "",
				"type": "string"
			},
			{
				"id": "110",
				"property": "Bezoekadres",
				"value": "",
				"type": "string",
				"children": [
					{
						"id": "111",
						"property": "Straat",
						"value": "",
						"type": "string"
					},
					{
						"id": "112",
						"property": "Nummer",
						"value": "",
						"type": "string"
					},
					{
						"id": "113",
						"property": "Toevoeging",
						"value": "",
						"type": "string"
					},
					{
						"id": "114",
						"property": "Postcode",
						"value": "",
						"type": "string"
					},
					{
						"id": "115",
						"property": "Plaats",
						"value": "",
						"type": "string"
					},
					{
						"id": "116",
						"property": "Land",
						"value": "Nederland",
						"type": "string"
					}
				]
			},
			{
				"id": "117",
				"property": "Postadres",
				"value": "",
				"type": "string",
				"children": [
					{
						"id": "118",
						"property": "Straat",
						"value": "",
						"type": "string"
					},
					{
						"id": "119",
						"property": "Nummer",
						"value": "",
						"type": "string"
					},
					{
						"id": "120",
						"property": "Toevoeging",
						"value": "",
						"type": "string"
					},
					{
						"id": "121",
						"property": "Postcode",
						"value": "",
						"type": "string"
					},
					{
						"id": "122",
						"property": "Plaats",
						"value": "",
						"type": "string"
					},
					{
						"id": "123",
						"property": "Land",
						"value": "Nederland",
						"type": "string"
					}
				]
			},
			{
				"id": "124",
				"property": "Activiteiten",
				"value": "",
				"type": "string",
				"children": [
					{
						"id": "125",
						"property": "Activiteit1",
						"value": "",
						"type": "string",
						"children": [
							{
								"id": "126",
								"property": "Uitvoeringsdatum",
								"value": "",
								"type": "date"
							},
							{
								"id": "127",
								"property": "Status",
								"value": "Onbekend",
								"type": "array",
								"sourceValues": [
									"Onbekend",
									"Gepland",
									"Opgeleverd"
								]
							},
							{
								"id": "128",
								"property": "Product1",
								"value": "",
								"type": "string",
								"children": [
									{
										"id": "129",
										"property": "Product Naam",
										"value": "Onbekend",
										"type": "array",
										"sourceValues": [
											"Onbekend",
											"Jaarlijks onderhoud brandveiligheidsunits",
											"Jaarlijks onderhoud Brandmeldinstallatie",
											"Jaarlijks onderhoud brandblusmiddelen ",
											"Maandelijkse controle Brandmeldinstallatie",
											"EHBO Kit",
											"Nooduitgang",
											"RI&E",
											"Ontruimingsplan",
											"BHV cursus",
											"BHV cursus (herhaling)"
										]
									},
									{
										"id": "130",
										"property": "Aantal Producten",
										"value": "0",
										"type": "string"
									},
									{
										"id": "131",
										"property": "Prijs per Eenheid",
										"value": "0",
										"type": "string"
									}
								]
							},
							{
								"id": "132",
								"property": "Product2",
								"value": "",
								"type": "string",
								"children": [
									{
										"id": "133",
										"property": "Product Naam",
										"value": "Onbekend",
										"type": "array",
										"sourceValues": [
											"Onbekend",
											"Jaarlijks onderhoud brandveiligheidsunits",
											"Jaarlijks onderhoud Brandmeldinstallatie",
											"Jaarlijks onderhoud brandblusmiddelen ",
											"Maandelijkse controle Brandmeldinstallatie",
											"EHBO Kit",
											"Nooduitgang",
											"RI&E",
											"Ontruimingsplan",
											"BHV cursus",
											"BHV cursus (herhaling)"
										]
									},
									{
										"id": "134",
										"property": "Aantal Producten",
										"value": "0",
										"type": "string"
									},
									{
										"id": "135",
										"property": "Prijs per Eenheid",
										"value": "0",
										"type": "string"
									}
								]
							},
							{
								"id": "136",
								"property": "Product3",
								"value": "",
								"type": "string",
								"children": [
									{
										"id": "137",
										"property": "Product Naam",
										"value": "Onbekend",
										"type": "array",
										"sourceValues": [
											"Onbekend",
											"Jaarlijks onderhoud brandveiligheidsunits",
											"Jaarlijks onderhoud Brandmeldinstallatie",
											"Jaarlijks onderhoud brandblusmiddelen ",
											"Maandelijkse controle Brandmeldinstallatie",
											"EHBO Kit",
											"Nooduitgang",
											"RI&E",
											"Ontruimingsplan",
											"BHV cursus",
											"BHV cursus (herhaling)"
										]
									},
									{
										"id": "138",
										"property": "Aantal Producten",
										"value": "0",
										"type": "string"
									},
									{
										"id": "139",
										"property": "Prijs per Eenheid",
										"value": "0",
										"type": "string"
									}
								]
							}
						]
					},
					{
						"id": "140",
						"property": "Activiteit2",
						"value": "",
						"type": "string",
						"children": [
							{
								"id": "141",
								"property": "Uitvoeringsdatum",
								"value": "",
								"type": "date"
							},
							{
								"id": "142",
								"property": "Status",
								"value": "Onbekend",
								"type": "array",
								"sourceValues": [
									"Onbekend",
									"Gepland",
									"Opgeleverd"
								]
							},
							{
								"id": "143",
								"property": "Product1",
								"value": "",
								"type": "string",
								"children": [
									{
										"id": "144",
										"property": "Product Naam",
										"value": "Onbekend",
										"type": "array",
										"sourceValues": [
											"Onbekend",
											"Jaarlijks onderhoud brandveiligheidsunits",
											"Jaarlijks onderhoud Brandmeldinstallatie",
											"Jaarlijks onderhoud brandblusmiddelen ",
											"Maandelijkse controle Brandmeldinstallatie",
											"EHBO Kit",
											"Nooduitgang",
											"RI&E",
											"Ontruimingsplan",
											"BHV cursus",
											"BHV cursus (herhaling)"
										]
									},
									{
										"id": "145",
										"property": "Aantal Producten",
										"value": "0",
										"type": "string"
									},
									{
										"id": "146",
										"property": "Prijs per Eenheid",
										"value": "0",
										"type": "string"
									}
								]
							},
							{
								"id": "147",
								"property": "Product2",
								"value": "",
								"type": "string",
								"children": [
									{
										"id": "148",
										"property": "Product Naam",
										"value": "Onbekend",
										"type": "array",
										"sourceValues": [
											"Onbekend",
											"Jaarlijks onderhoud brandveiligheidsunits",
											"Jaarlijks onderhoud Brandmeldinstallatie",
											"Jaarlijks onderhoud brandblusmiddelen ",
											"Maandelijkse controle Brandmeldinstallatie",
											"EHBO Kit",
											"Nooduitgang",
											"RI&E",
											"Ontruimingsplan",
											"BHV cursus",
											"BHV cursus (herhaling)"
										]
									},
									{
										"id": "149",
										"property": "Aantal Producten",
										"value": "0",
										"type": "string"
									},
									{
										"id": "150",
										"property": "Prijs per Eenheid",
										"value": "0",
										"type": "string"
									}
								]
							},
							{
								"id": "151",
								"property": "Product3",
								"value": "",
								"type": "string",
								"children": [
									{
										"id": "152",
										"property": "Product Naam",
										"value": "Onbekend",
										"type": "array",
										"sourceValues": [
											"Onbekend",
											"Jaarlijks onderhoud brandveiligheidsunits",
											"Jaarlijks onderhoud Brandmeldinstallatie",
											"Jaarlijks onderhoud brandblusmiddelen ",
											"Maandelijkse controle Brandmeldinstallatie",
											"EHBO Kit",
											"Nooduitgang",
											"RI&E",
											"Ontruimingsplan",
											"BHV cursus",
											"BHV cursus (herhaling)"
										]
									},
									{
										"id": "153",
										"property": "Aantal Producten",
										"value": "0",
										"type": "string"
									},
									{
										"id": "154",
										"property": "Prijs per Eenheid",
										"value": "0",
										"type": "string"
									}
								]
							}
						]
					},
					{
						"id": "155",
						"property": "Activiteit3",
						"value": "",
						"type": "string",
						"children": [
							{
								"id": "156",
								"property": "Uitvoeringsdatum",
								"value": "",
								"type": "date"
							},
							{
								"id": "157",
								"property": "Status",
								"value": "Onbekend",
								"type": "array",
								"sourceValues": [
									"Onbekend",
									"Gepland",
									"Opgeleverd"
								]
							},
							{
								"id": "158",
								"property": "Product1",
								"value": "",
								"type": "string",
								"children": [
									{
										"id": "159",
										"property": "Product Naam",
										"value": "Onbekend",
										"type": "array",
										"sourceValues": [
											"Onbekend",
											"Jaarlijks onderhoud brandveiligheidsunits",
											"Jaarlijks onderhoud Brandmeldinstallatie",
											"Jaarlijks onderhoud brandblusmiddelen ",
											"Maandelijkse controle Brandmeldinstallatie",
											"EHBO Kit",
											"Nooduitgang",
											"RI&E",
											"Ontruimingsplan",
											"BHV cursus",
											"BHV cursus (herhaling)"
										]
									},
									{
										"id": "160",
										"property": "Aantal Producten",
										"value": "0",
										"type": "string"
									},
									{
										"id": "161",
										"property": "Prijs per Eenheid",
										"value": "0",
										"type": "string"
									}
								]
							},
							{
								"id": "162",
								"property": "Product2",
								"value": "",
								"type": "string",
								"children": [
									{
										"id": "163",
										"property": "Product Naam",
										"value": "Onbekend",
										"type": "array",
										"sourceValues": [
											"Onbekend",
											"Jaarlijks onderhoud brandveiligheidsunits",
											"Jaarlijks onderhoud Brandmeldinstallatie",
											"Jaarlijks onderhoud brandblusmiddelen ",
											"Maandelijkse controle Brandmeldinstallatie",
											"EHBO Kit",
											"Nooduitgang",
											"RI&E",
											"Ontruimingsplan",
											"BHV cursus",
											"BHV cursus (herhaling)"
										]
									},
									{
										"id": "164",
										"property": "Aantal Producten",
										"value": "0",
										"type": "string"
									},
									{
										"id": "165",
										"property": "Prijs per Eenheid",
										"value": "0",
										"type": "string"
									}
								]
							},
							{
								"id": "166",
								"property": "Product3",
								"value": "",
								"type": "string",
								"children": [
									{
										"id": "167",
										"property": "Product Naam",
										"value": "Onbekend",
										"type": "array",
										"sourceValues": [
											"Onbekend",
											"Jaarlijks onderhoud brandveiligheidsunits",
											"Jaarlijks onderhoud Brandmeldinstallatie",
											"Jaarlijks onderhoud brandblusmiddelen ",
											"Maandelijkse controle Brandmeldinstallatie",
											"EHBO Kit",
											"Nooduitgang",
											"RI&E",
											"Ontruimingsplan",
											"BHV cursus",
											"BHV cursus (herhaling)"
										]
									},
									{
										"id": "168",
										"property": "Aantal Producten",
										"value": "0",
										"type": "string"
									},
									{
										"id": "169",
										"property": "Prijs per Eenheid",
										"value": "0",
										"type": "string"
									}
								]
							}
						]
					}
				]
			},
			{
				"id": "170",
				"property": "Status",
				"value": "Onbekend",
				"type": "array",
				"sourceValues": [
					"Onbekend",
					"Actief",
					"Inactief"
				]
			},
			{
				"id": "171",
				"property": "Financieel",
				"value": "",
				"type": "string",
				"children": [
					{
						"id": "172",
						"property": "Debiteurnummer",
						"value": "",
						"type": "string"
					},
					{
						"id": "173",
						"property": "Factuuradres",
						"value": "",
						"type": "string",
						"children": [
							{
								"id": "174",
								"property": "Straat",
								"value": "",
								"type": "string"
							},
							{
								"id": "175",
								"property": "Nummer",
								"value": "",
								"type": "string"
							},
							{
								"id": "176",
								"property": "Toevoeging",
								"value": "",
								"type": "string"
							},
							{
								"id": "177",
								"property": "Postcode",
								"value": "",
								"type": "string"
							},
							{
								"id": "178",
								"property": "Plaats",
								"value": "",
								"type": "string"
							},
							{
								"id": "179",
								"property": "Land",
								"value": "Nederland",
								"type": "string"
							}
						]
					},
					{
						"id": "180",
						"property": "Bank",
						"value": "",
						"type": "string",
						"children": [
							{
								"id": "181",
								"property": "Naam",
								"value": "",
								"type": "string"
							},
							{
								"id": "182",
								"property": "IBAN",
								"value": "",
								"type": "string"
							},
							{
								"id": "183",
								"property": "BIC",
								"value": "",
								"type": "string"
							}
						]
					}
				]
			},
			{
				"id": "184",
				"property": "Typering",
				"value": "",
				"type": "string",
				"children": [
					{
						"id": "185",
						"property": "Bedrijfsactiviteit",
						"value": "",
						"type": "string"
					},
					{
						"id": "186",
						"property": "Branche",
						"value": "Onbekend",
						"type": "array",
						"sourceValues": [
							"Onbekend",
							"A. Landbouw bosbouw en visserij",
							"B. Winning van delfstoffen",
							"C. Industrie",
							"D. Productie en distributie van en handel in electiciteit, aardgas, stoom, en gekoelde lucht",
							"E. Winning en distributievan water; afval- en afvalwaterbeheer en saneringen",
							"F. Bouwnijverheid",
							"G. Groothandel en detailhandel; reparatie van auto's",
							"H. Vervoer en opslag",
							"I. Logies-, maaltijd- en drankverstrekking",
							"J. Informatie en communicatie",
							"K. Financiele instelling",
							"L. Verhuurd van en handel in onroerend goed",
							"M. Advisering, onderzoek, van overige specialistische zakelijke dienstverlening",
							"N. Verhuurd van roerende goederen en overige zakelijke dienstverlening",
							"O. Openbaar bestuur, overheidsdiensten en verplichte socialte verzekerninge",
							"P. Onderwijs",
							"Q. Gezondheidszorg",
							"R. Cultuur, sport en recreatie"
						]
					},
					{
						"id": "187",
						"property": "Aantal medewerkers",
						"value": "",
						"type": "string"
					}
				]
			},
			{
				"id": "188",
				"property": "Identificatie",
				"value": "",
				"type": "string",
				"children": [
					{
						"id": "189",
						"property": "KvK nummer",
						"value": "",
						"type": "string"
					},
					{
						"id": "190",
						"property": "Rechtsvorm",
						"value": "Onbekend",
						"type": "array",
						"sourceValues": [
							"Onbekend",
							"Besloten Vennootschap",
							"Eenmanszaak",
							"Naamloze Vennootschap",
							"Publieksrechtelijk Lichaam",
							"Stichting",
							"Vennootschap onder Firma",
							"Vereniging"
						]
					}
				]
			}
		]
	;
	return newCustomerDetails;
}

String.prototype.initCap = function(){
   return this.toLowerCase().replace(/(?:^|\s)[a-z]/g, function (m) {
      return m.toUpperCase();
   });
};
	
function removeConsecutiveWS(input){
	var updInput = input;
	while(updInput.indexOf("  ") >= 0){
		updInput = updInput.replace("  "," ");
	}
	return updInput;
};
	
function standardize(input){
	var updInput = input.toLowerCase();
	while(updInput.indexOf(" ") >= 0){
		updInput = updInput.replace(" ","");
	}
	return updInput;
};

String.prototype.toDateMillis = function(){
	
	if(/^\d{1,2}\/\d{1,2}\/\d{2,4}$/.test(this)){
		
		var dateObj = this.split("/");
		return new Date(dateObj[2],dateObj[1]-1,dateObj[0]).getTime();
	}else 
		return null;
};

	
String.prototype.toNumber = function(){
	
	if(/^-?\d*\.\d*$/.test(this))
		return parseFloat(this);
	else 
		return null;
};

function updateAttrId(attr){
	attr.id = counter+"";
	counter+=1;
	if(attr.children && attr.children.length>0){
		var attrUpdChildren = 
			attr.children.map(function(child){
				return updateAttrId(child);
			});
		attr.children = attrUpdChildren;
	}
	return attr;
}
var counter = 1;

getNewCustomerDetails().map(function(attr){
		var updAttr = updateAttrId(attr);
		return updAttr;
});