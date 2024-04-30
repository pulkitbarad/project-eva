		// height: $(".historyRow.chartRow").css("height"),
		// width: $(".historyRow").css("width")
const CHART_TYPE_DATA_TABLE = "Data Table";
const CHART_TYPE_AREA = "Area Chart";
const CHART_TYPE_BAR = "Bar Chart";
const CHART_TYPE_BUBBLE = "Bubble Chart";
const CHART_TYPE_CALENDAR = "Calendar Chart";
const CHART_TYPE_CANDLESTICK = "Candlestick Chart";
const CHART_TYPE_COLUMN = "Column Chart";
const CHART_TYPE_DONUT = "Donut Chart";
const CHART_TYPE_GANTT = "Gantt Chart";
const CHART_TYPE_HISTOGRAM = "Histogram";
const CHART_TYPE_LINE = "Line Chart";
const CHART_TYPE_MAP = "Map";
const CHART_TYPE_PIE = "Pie Chart";
const CHART_TYPE_SANKEY = "Sankey Diagram";
const CHART_TYPE_SCATTER = "Scatter Plot";
const CHART_TYPE_STEPPEDAREA = "SteppedArea Chart";
const ChartTypes = 
	[
		CHART_TYPE_AREA,
		CHART_TYPE_BAR,
		CHART_TYPE_BUBBLE,
		CHART_TYPE_CALENDAR,
		CHART_TYPE_CANDLESTICK,
		CHART_TYPE_COLUMN,
		CHART_TYPE_DONUT,
		CHART_TYPE_GANTT,
		CHART_TYPE_HISTOGRAM,
		CHART_TYPE_LINE,
		CHART_TYPE_MAP,
		CHART_TYPE_PIE,
		CHART_TYPE_SANKEY,
		CHART_TYPE_SCATTER,
		CHART_TYPE_STEPPEDAREA
	];

function drawChart(uid,chartType,title,data,metadata){
	var options = 
		{
			title: "",
			height: "100%",
			width: "100%",
			backgroundColor: { 
				fill:"transparent"
			}
		};

	var isDataGrid = false;
	if(chartType === CHART_TYPE_DATA_TABLE) isDataGrid = true;
	var chartData = prepareChartData(metadata,data,isDataGrid);

	switch(chartType){
			
		case CHART_TYPE_DATA_TABLE: 
			drawDataTable(uid,options,metadata,data);
			break;
			
		default: 
			drawGoogleChart(uid,chartType,options,chartData);
	}
}

function drawGoogleChart(
					uid,
					chartType,
					options,
					chartData){

	var chart = null;
					
	switch(chartType){
			
		case CHART_TYPE_AREA:
		
			chart = new google.visualization.AreaChart(document.getElementById(uid));
			break;
			
		case CHART_TYPE_BAR: options.bars =  "horizontal";
		case CHART_TYPE_COLUMN: 
		
			options =  google.charts.Bar.convertOptions(options);
			chart = new google.charts.Bar(document.getElementById(uid));
			break;
			
		case CHART_TYPE_BUBBLE:
		
			chart = new google.visualization.BubbleChart(document.getElementById(uid));
			break;
			
		case CHART_TYPE_CALENDAR: 
		
			chart = new google.visualization.Calendar(document.getElementById(uid));
			break;
			
		case CHART_TYPE_CANDLESTICK: 
		
			chart = new google.visualization.CandlestickChart(document.getElementById(uid));
			break;
		
		case CHART_TYPE_DONUT: options.pieHole =  0.4;
		case CHART_TYPE_PIE: 
		
			chart = new google.visualization.PieChart(document.getElementById(uid));
			break;
			
		case CHART_TYPE_GANTT: 
		
			chart = new google.visualization.Gantt(document.getElementById(uid));
			break;
						
		case CHART_TYPE_HISTOGRAM: 
		
			chart = new google.visualization.Histogram(document.getElementById(uid));
			break;
			
		case CHART_TYPE_LINE: 
		
			options =  google.charts.Line.convertOptions(options);
			chart = new google.charts.Line(document.getElementById(uid));
			break;
						
		case CHART_TYPE_MAP: 
		
			options.showTooltip = true;
			options.showInfoWindow = true;
			chart = new google.visualization.Map(document.getElementById(uid));
			break;
						
		case CHART_TYPE_SANKEY: 
		
			chart = new google.visualization.Sankey(document.getElementById(uid));
			break;		
			
		case CHART_TYPE_SCATTER: 
		
			options.trendlines = 
				{ 
					0: {
						type: "exponential",
						visibleInLegend: true,
					}
				};
			options =  google.charts.Scatter.convertOptions(options);
			chart = new google.charts.Scatter(document.getElementById(uid));
			break;
						
		case CHART_TYPE_STEPPEDAREA: 
		
			options.isStacked = true;
			chart = new google.visualization.SteppedAreaChart(document.getElementById(uid));
			break;		
	}
	chart.draw(chartData, options);
}

function prepareChartData(nuoFields,data,isDataGrid){
	
	if(isDataGrid) 
		return getChartRows(nuoFields,data);
	else{
		var chartData = new google.visualization.DataTable();
		chartData.addRows(getChartRows(nuoFields,data,chartData));
		return chartData;
	}
}

function drawDataTable(uid,options,nuoFields,data){
	
	var tableData = 
		data
		.map(function(row){
			
			var colIndex = 0;
			var rowObject = {};
			row.forEach(function(colValue){
				var colDataType = nuoFields[colIndex].DataType;
				rowObject[nuoFields[colIndex].FieldName] = convertToChartColumn(colDataType,colValue).value;
				
				colIndex+=1;
			});
			return rowObject;
		});
		
	var dataFields = getDataTableFields(nuoFields);
	var source =
		{
				localData: tableData,
				dataType: "array",
				dataFields: dataFields
		};
	var dataAdapter = new $.jqx.dataAdapter(source);
	$("#" + uid).jqxGrid(
		{
			width: options.width,
			height: options.height,
			source: dataAdapter,
			pageable: true,
			altRows: true,
			sortable: true,
			filterable: true,
			filtermode: "excel",
			// showfilterrow: true,
			columnsresize: true,
			autoshowfiltericon: false,
			columns: 
				dataFields
				.map(function(df){
					
					var column =
						{
							text: df.name,
							dataField: df.name,
							width: 200
						};
					return column;
				}),
			theme: jqWidgetThemeName
		}
	);
}

function getChartRows(nuoFields,data,chartDataObject){

	var header = true;
	var chartRows = 
		data
		.map(function(row){
			
			var rowData = [];
			for(var colIndex =0; colIndex < nuoFields.length; colIndex++){
				
				var chartColumn = convertToChartColumn(nuoFields[colIndex].DataType,row[colIndex]);
				if(header && chartDataObject){
					chartDataObject.addColumn(chartColumn.dataType,nuoFields[colIndex].FieldName);
				}
				rowData.push(chartColumn.value);
			}
				header=false;
				return rowData;
		});
	if(chartDataObject)
		return chartRows;
	else {
		chartRows.unshift(nuoFields.map(function(field){
			return field.FieldName;
		}));
		return chartRows;
	}
}

function convertToChartColumn(colDataType, colValue){
	
	var dataType = colDataType.toLowerCase();
	var updValue = colValue;
	
	if(dataType === "date"){
						
		updValue = new Date(updValue);
		
	}else if(dataType === "time"){
		
		var dateObj = new Date("1970-01-01T" + updValue + "Z");
		updValue = [dateObj.getHours(),dateObj.dateObj.getMinutes(),dateObj.getSeconds(),dateObj.getMilliseconds()];
		dataType = "timeofday";
		
	}else if(dataType === "timestamp"){
			
		updValue = new Date(parseInt(updValue) * 1000);
		dataType = "datetime";
		
	}else if(["integer","int","int64"].indexOf(dataType) >= 0){
			
		updValue = parseInt(updValue);
		dataType = "number";

	}else if(["float","float64"].indexOf(dataType) >= 0){

		updValue = parseFloat(updValue);
		dataType = "number";
		
	}else if(dataType === "boolean"){
		
		if(updValue === "true") updValue = true 
		else updValue = false
	}
	var result =  
		{
			value: updValue,
			dataType: dataType
			
		};
	return result;
}

function getDataTableFields(nuoFields){
	
	var dataFields = 
	nuoFields
	.map(function(field){
		
		var dataFieldObj = 
			{
				type: field.DataType.toLowerCase(),
				name: field.FieldName
			};

		if(["time","timestamp"].indexOf(dataFieldObj.type) >= 0){
			
			dataFieldObj.type = "datetime";
			
		}else if(["int","int64","float","float64"].indexOf(dataFieldObj.type) >= 0){
				
			dataFieldObj.type = "number";
		}
		return dataFieldObj;		
	});
	
	return dataFields;
}
