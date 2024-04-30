package canvas

/**
  * Copyright 2015 Nuocanvas Inc.
  *
  *
  * Created by Pulkit on 29SEP2015.
  *
  * Content of this file is proprietry and confidential.
  * It should not be reused or disclosed without prior consent
  * of distributor
  **/
object NuoModifier {


  object FileFormat {

    val CSV = "CSV"
    val TSV = "TSV"
    val NLD_JSON = "NLD_JSON"
    val DELIMITED = "DELIMITED"
  }
  object RunPlanName {

    val RunCustomStrict: String = "Run_Custom_Strict"
    val RunCustomLenient: String = "Run_Custom_Lenient"
    val RunUntilHereStrict: String = "Run_Until_Here_Strict"
    val RunUntilHereLenient: String = "Run_Until_Here_Lenient"
    val RunProcessStrict: String = "Run_Process_Strict"
    val RunProcessLenient: String = "Run_Process_Lenient"
    val RunFromHereLenient: String = "Run_From_Here_Lenient"
    val RunFromHereStrict: String = "Run_From_Here_Strict"
    val RerunAll: String = "Rerun_All"
  }

  object UDFName {

    /*
    * UDF names
    * */
    val UDFConcat: String = "CONCAT"
    val UDFLength: String = "LENGTH"
    val UDFMd5: String = "MD5"
    val UDFSha256: String = "SHA256"
  }

  object ActionPropertyName {

    object Common {
      val ElementTypeList = "Element_Type_List"
    }

    object Dml {

      object Common {
        val DistinctResult: String = "Distinct Result"

      }

      object Join {

        val JoinType: String = "Join Type"
        val JoinCondition: String = "Join Condition"
        val JoinColumns: String = "Join Columns"
        val JoinOrder: String = "Join Order"

      }

      object Filter {
        val FilterCondition: String = "Filter Condition"

      }

      object RemoveDuplicates {}

      object Sort {
        val SortByClause: String = "Sort By Clause"

      }

      object Aggregate {
        val GroupByClause: String = "Group By Clause"

      }

      object Union {
      }

      object Intersect {

      }

      object Minus {
        val InputOrder: String = "Input Order"
      }

      object Compute {}

      object Merge {
        val KeyColumns: String = "Key Columns"
        val MergeSource: String = "Merge Source"
        val MergeTarget: String = "Merge Target"
        val IsInsert: String = "Insert On No Match"
        val IsUpdate: String = "Update On Match"
        val IsDelete: String = "Delete On Match"
        val MergeCondition: String = "Merge Condition"
        val UpdateCondition: String = "Update Condition"
        val DeleteCondition: String = "Delete Condition"

      }

      object Match {
        val InputPatternColumn: String = "Input Pattern Column"

      }

      object Case {
        val CasePatternValue: String = "Case Pattern"

      }

      object CaseDefault {}

    }

    object Storage {

      object Common {
        val TableName: String = "Table Name"
        val DatasetName: String = "Dataset Name"

        val S3BucketName = "S3 Bucket Name"
        val S3FilePrefix = "S3 File Prefix"
        val AwsAccessKey = "AWS Access Key"
        val AwsSecretKey = "AWS Secret Key"
        val NuoFilePrefix = "NuoCanvas File Prefix"

        object WriteIntoTable {
          val AppendOutput: String = "Append Output"
          val WriteIfEmpty: String = "Write If Empty"

        }

        object ExportTableToFile {
          val OutputFormat: String = "Output Format"
          val EnableCompression: String = "Enable Compression"
          val PrintHeader: String = "Print Header"
          val OutputFilePrefix: String = "Output File Prefix"

        }

        object LoadDelimitedFiles {
          val Delimiter = "Field Delimiter"
          val QuoteCharacter = "Quote Character"
          val LeadingRowsToSkip = "Leading Rows To Skip"
        }

        object S3FileReader {

          val S3PrefixIncludeList = "File Prefix to Include"
          val S3PrefixExcludeList = "File Prefix to Exclude"
          val DeleteSourceAfterTransfer = "Delete Source Files after Transfer"

        }

        object GcsFileDetails {
          //          val GCSBucketName = "GCS Bucket Name"
          //          val GCPCredentials = "GCP Credentials"
          val SourceFileList = "Source File List"
        }

      }

      object CreateDataset {}

      object DeleteDataset {}

      object Insert {}

      object Update {}

      object Delete {}

      object SaveAsNuoTable {

      }

      object ReadNuoTable {
        val WhereClause: String = "Filter"

      }

      object LoadDelimitedS3Files {

      }

      object TransferS3FilesToGCS {

      }

      object LoadDelimitedGCSFiles {

      }

      object MergeIntoTable {
        val KeyColumns: String = "Key Columns"

        val TgtDatasetName: String = "Target Dataset"
        val TgtTableName: String = "Target Table"
        val MergeCondition: String = "Merge Condition"

        val UpdateCondition: String = "Update Condition"
        val DeleteCondition: String = "Delete Condition"
        val IsInsert: String = "Insert On No Match"
        val IsUpdate: String = "Update On Match"
        val IsDelete: String = "Delete On Match"

      }

      object CreateTable {

      }

      object UpdateTableStructure {}

      object DoesTableExist {}

      object DeleteTable {}

    }

    object External {

      object Summarizer {
        val NumOfSentence: String = "Condition"
      }

      object MLaaS {

        object Common {
          val ScoreThresholdPercent = "Model Score Threshold Percent"
          val TrainDataPercent = "Train Data Percent"
          val ModelName = "Model Name"
          val TargetColumn = "Target Column"
        }

      }

      object AIaaS {

        object Common {
          val InputFilePrefix: String = "Input File Prefix"
          val OutputFilePrefix: String = "Output File Prefix"
          val LanguageHint: String = "Language Hint"
        }

        object ImageIntelligence {

          object Common {
            val HighlightColor: String = "Highlight Color"
            val MaxDetections: String = "Max Detections"

          }

          object DetectCropHint {
            val DesiredAspectRatios: String = "Desired Aspect Ratios"
          }

        }

        object VideoIntelligence {

        }

        object SpeechIntelligence {
          val PhrasesHint: String = "Phrases Hint"
          val AudioEncoding: String = "Audio Encoding"
          val EnableWordList: String = "Enable Word List"
          val ProfanityFilter: String = "Profanity Filter"
          val MaxAlternatives: String = "Max Alternatives"
          val SampleRateHz: String = "Sample Rate(Hz)"
          val VoiceName: String = "Voice Name"
          val OutputFormat: String = "Output Format"

        }

        object LanguageIntelligence {
          val TextEncoding: String = "Text Encoding"
          val TargetLanguage: String = "Target Language"
        }

      }

    }

    object LoopAndVariable {

      object Common {

        val CurrentItem: String = "Current_Item"
        val Condition: String = "Condition"
      }

      object Repeat {
        val TotalIterations: String = "Total Iterations"
      }

      object While {

      }

      object DoUntil {}

      object CreateVariable {}

      object UpdateVariable {}

    }

  }

  object ActionAttrName {

    /*Node attribute names
    * */
    val NodeName: String = "Node_Name"
    val NodeType: String = "Node_Type"

    /*Column attribute names
    * */

    val ColumnName: String = "Target"
    val DataType: String = "Data_Type"
    val Precision: String = "Precision"
    val Scale: String = "Scale"
    val ColumnSource: String = "Source"
    //    val SourceColumn: String = "Source_Column"
    val IsMandatory: String = "IsMandatory"
    val ColumnMapping: String = "ColumnMapping"


    /*File attributes
    * */
    //    val IsPersistentVariable: String = "Is_Persistent"
    //    val FileName: String = "File_Name"
    //    val ContainsHeader: String = "Contains_Header"
    //    val Delimiter: String = "Delimiter"
    //    val QuoteCharacter: String = "Quote_Character"
    //    val EscapeCharacter: String = "Escape_Character"
    //    val NullValue: String = "Null_Value"
    //    val SaveMode: String = "Save_Mode"
    //    val QuoteMode: String = "Quote_Mode"
    //    val CompressionCodec: String = "Compression_Codec"
    //    val ParsingMode: String = "Parsing_Mode"
    //    val CharacterSet: String = "Character_Set"
    //    val CommentChar: String = "Comment_Char"
  }


  object Constant {


    /*Generic modifiers
    * */
    val _nullValue: String = "__null"
    val _businessCanvas: String = "__businessCanvas"
    val _variable: String = "__variable"
    val _dollarAsData = "`~!@#DollarAsData#@!~`"
    val _backslashAsData = "`~!@#BackslashAsData#@!~`"

  }

  object ChartAttribute {

    val Title = "Chart Title"
    val Description = "Chart Description"
    val ChartBackgroundColor = "Chart Background Color"
    val ListOfItemColors = "List Of Item Colors"
    val DrawLineArc = "Draw Line for Arc"
    //    val ShowLegend = "Show Chart Legend"
    val XAxisLabel = "X-Axis Label"
    val YAxisLabel = "Y-Axis Label"
    //    val ShowXLegend = "Show X-Axis Legend"
    //    val ShowYLegend = "Show Y-Axis Legend"
    val CurvedLines = "Draw Curved Lines"
    val IsVertical = "Is Vertical"
    val ShowItemInToolTip = "Show Item Name In ToolTip"
  }

  object ActionType {

    val Process: String = "Process"

    object DataProfiling {
      val ProfileResult: String = "Profile Result"
    }

    object Dml {

      val Join: String = "Join"
      val Filter: String = "Filter"
      val RemoveDuplicate: String = "Remove Duplicate"
      val Sort: String = "Sort"
      val Aggregate: String = "Aggregate"
      val Compute: String = "Compute"

      val Union: String = "Union"
      val Intersect: String = "Intersect"
      val Minus: String = "Minus"

      val Merge: String = "Merge"
      val Match: String = "Match"
      val Case: String = "Case"
      val DefaultCase: String = "Default Case"
    }

    object Storage {


      val CreateDataset: String = "Create Dataset"
      val DeleteDataset: String = "Delete Dataset"

      val CreateTable: String = "Create Table"
      val UpdateTableStructure: String = "Update Table Structure"
      val CheckTableExist: String = "Check Table Exists"
      val DeleteTable: String = "Delete Table"

      val SaveAsTable: String = "Save As NuoTable"
      val ExportTableToFile: String = "Export NuoTable to File"
      val ReadNuoTable: String = "Read NuoTable"
      val MergeIntoTable: String = "Merge Into NuoTable"
      val LoadDelimitedFilesFromS3: String = "Load Delimited Files from AWS S3"
      val LoadDelimitedFiles: String = "Load Delimited Files"
      val LoadNLDJsonFilesFromS3: String = "Load Newline Delimited JSON Files from AWS S3"
      val LoadNLDJsonFiles: String = "Load Newline Delimited JSON Files"
      val ImportFilesFromS3: String = "Import Files from AWS S3"
      val ExportFilesToS3: String = "Export Files to AWS S3"
    }

    object LoopAndVariable {

      val Repeat: String = "Repeat"
      val While: String = "While"
      val DoUntil: String = "Do Until"

      //      val CreateVariable: String = "Create Variable"
      val CreateOrUpdateVariable: String = "Create/Update Variable"
    }


    object Chart {

      val DataGrid = "Show Data Grid"
      val VariedHeightPieChart = "Show Varied Height Pie Chart"
      val PolarAreaChart = "Show Polar Area Chart"
      val PieChart = "Show Pie Chart"
      val DoughnutChart = "Show Doughnut Chart"
      val RadialChart = "Show Radial Chart"
      val LiquidGaugeChart = "Show Liquid Gauge Chart"
      val LineChart = "Show Line Chart"
      val ScatterPlot = "Show Scatter Plot"
      val StackedAreaChart = "Show Stacked Area Chart"
      val BarChart = "Show Bar Chart"
      val RadarChart = "Show Radar Chart"
      val GeoLocationMap = "Show Geo Location Map"
      val GeoCountryMap = "Show Geo Country Map"
    }

    object External {

      object InHouse {
        val AnalyzeSentiment = "Analyze Sentiment"
      }

      object ImageIntelligence {

        val DetectCropHint = "Detect Crop Hint"
        val DetectExplicitContent = "Detect Explicit Content"
        val DetectFace = "Detect Face"
        val DetectLabel = "Detect Label"
        val DetectLandmark = "Detect Landmark"
        val DetectLogo = "Detect Logo"
        val DetectProperties = "Detect Properties"
        val DetectTextInDocument = "Detect Text in Document"
        val DetectTextInImage = "Detect Text in Image"
        val FindSimilarImage = "Find Similar Image"
      }

      object VideoIntelligence {

        val DetectExplicitContentInVideo = "Detect Explicit Content in Video"
        val DetectFaceInVideo = "Detect Face in Video"
        val DetectLabelInVideo = "Detect Label in Video"
        val DetectShotChange = "Detect Shot Change"
      }

      object SpeechIntelligence {

        val ConvertSpeechToText = "Convert Speech to Text"
        val ConvertTextToSpeech = "Convert Text to Speech"
      }

      object MLaaS {

        val TrainModel = "Train Model"
        val PredictValue = "Predict Value"
      }

      object NlpIntelligence {

        val AnalyzeTextEntities = "Analyze Text Entities"
        val AnalyzeTextSentiment = "Analyze Text Sentiment"
        val AnalyzeTextSyntax = "Analyze Text Syntax"
        val ClassifyText = "Classify Text"
      }

      object TranslateIntelligence {
        val TranslateText = "Translate Text"
      }

    }

    object UserInput {

      val ChoiceInput = "Choice Input"
      val TextInput = "Text Input"
    }

  }

}

