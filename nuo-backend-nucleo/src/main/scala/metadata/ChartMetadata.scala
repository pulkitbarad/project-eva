package metadata

import java.io.Serializable

/**
  * Copyright 2016 Nuocanvas Inc.
  *
  * Created by Pulkit on 7/27/16.
  *
  * Content of this file is proprietary and confidential.
  * It should not be reused or disclosed without prior consent
  * of distributor
  */

object ChartMetadata {


  //  case class MultiLineChart(Header: List[String],
  //                            Values: List[List[String]],
  //                            Graphics: MultiLineGraphics)
  //
  //  case class MultiBarChart(Header: List[String],
  //                           Values: List[List[String]],
  //                           Graphics: MultiBarGraphics)
  //
  //  case class StackedAreaChart(Header: List[String],
  //                              Values: List[List[String]],
  //                              Graphics: StackedAreaGraphics)

  case class Chart(Header: List[String],
                   Values: List[List[String]],
                   Graphics: ChartGraphics)

  class ChartGraphics extends Serializable

  case class DataGridGraphics(Title: String,
                              Description: String,
                              ChartBackgroundColor: String,
                              ListOfItemColors: List[String]) extends ChartGraphics

  case class VariedHeightPieGraphics(Title: String,
                                     Description: String,
                                     ChartBackgroundColor: String,
                                     DrawLineArc: String,
                                     ShowLegend: String,
                                     ListOfItemColors: List[String]) extends ChartGraphics

  case class PolarAreaGraphics(Title: String,
                               Description: String,
                               ChartBackgroundColor: String,
                               DrawLineArc: String,
                               ShowLegend: String,
                               ListOfItemColors: List[String]) extends ChartGraphics

  case class PieGraphics(Title: String,
                         Description: String,
                         ChartBackgroundColor: String,
                         ShowLegend: String,
                         ListOfItemColors: List[String]) extends ChartGraphics

  case class DoughnutGraphics(Title: String,
                              Description: String,
                              ChartBackgroundColor: String,
                              ShowLegend: String,
                              ListOfItemColors: List[String]) extends ChartGraphics

  case class MultiRadialGraphics(Title: String,
                                 Description: String,
                                 ChartBackgroundColor: String,
                                 ShowLegend: String,
                                 ListOfItemColors: List[String]) extends ChartGraphics

  case class LiquidGraphics(Title: String,
                            Description: String,
                            ChartBackgroundColor: String,
                            ShowLegend: String,
                            ListOfItemColors: List[String]) extends ChartGraphics

  case class MultiLineGraphics(Title: String,
                               Description: String,
                               ChartBackgroundColor: String,
                               XAxisLabel: String,
                               YAxisLabel: String,
                               CurvedLines: String,
                               ShowXLegend: String,
                               ShowYLegend: String,
                               ListOfItemColors: List[String]) extends ChartGraphics

  case class ScatterGraphics(Title: String,
                             Description: String,
                             ChartBackgroundColor: String,
                             XAxisLabel: String,
                             YAxisLabel: String,
                             ShowXLegend: String,
                             ShowYLegend: String,
                             ListOfItemColors: List[String]) extends ChartGraphics

  case class StackedAreaGraphics(Title: String,
                                 Description: String,
                                 ChartBackgroundColor: String,
                                 XAxisLabel: String,
                                 YAxisLabel: String,
                                 ShowXLegend: String,
                                 ShowYLegend: String,
                                 ListOfItemColors: List[String]) extends ChartGraphics

  case class MultiBarGraphics(Title: String,
                              Description: String,
                              ChartBackgroundColor: String,
                              XAxisLabel: String,
                              YAxisLabel: String,
                              IsVertical: String,
                              ShowXLegend: String,
                              ShowYLegend: String,
                              ListOfItemColors: List[String]) extends ChartGraphics

  case class RadarGraphics(Title: String,
                           Description: String,
                           ChartBackgroundColor: String,
                           CurvedLines: String,
                           ListOfItemColors: List[String]) extends ChartGraphics

  case class GeoLocationGraphics(Title: String,
                                 Description: String,
                                 ChartBackgroundColor: String,
                                 ShowLegend: String,
                                 ListOfItemColors: List[String]) extends ChartGraphics

  case class GeoCountryGraphics(Title: String,
                                Description: String,
                                ChartBackgroundColor: String,
                                ShowItemInToolTip: String,
                                ListOfItemColors: List[String]) extends ChartGraphics

}
