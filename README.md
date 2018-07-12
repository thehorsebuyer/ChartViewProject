# ChartViewProject
![ChartViewProject](https://github.com/thehorsebuyer/ChartViewProject/blob/master/images/ChartViewProjectSS.png)
ChartViewProject is an unfinished project which contains a pie chart for now. But in the future other chart types will be added. The charts in this project will be interactive, animated and good-looking.

## Views in this project
1- PieChartView<br>
2- PieChartDataView

### 1- PieChartView
With this view you can add a pie chart to your project. PieChartView is interactable. When you touch a segment of the chart, the segment comes apart and the percentage of the the segment is written on the center of the chart. PieChartView is customizable. You can define your own color set to it, you can change its starting angle and etc.

### Usage
#### XML
```XML
<volkanatalan.chartview.charts.PieChartView
    android:id="@+id/pieChartView"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content" />
```

#### Java
```Java
PieChartView pieChartView = findViewById(R.id.pieChartView);
pieChartView.setData(pieChartData);
```
<br>

**If you need more customization:**

```XML
<volkanatalan.chartview.charts.PieChartView
    android:id="@+id/pieChartView"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:padding="30dp"
    app:centerCircleColor="@color/spaceGray"
    app:percentageTextColor="@color/white"
    app:percentageTextSize="50sp"
    app:apartDistance="10dp"
    app:startAngle="270"/>
```

```Java
pieChartView.setData(pieChartData)
    .setApartDistance(10)
    .setPercentageTextSize(50)
    .setPercentageTextColor(Color.WHITE)
    .setCenterCircleColor(getContext().getResources().getColor(R.color.spaceGray))
    .setColorList(getContext().getResources().getIntArray(R.array.color_list))
    .draw();
```
You have to call `draw()` method if you make changes programmatically.

### 2- PieChartDataView
With this view you can show the data of your PieChartView. It shows the color of the item in the PieChartView, item name and item amount. PieChartDataView is interactable. If you touch any segment of the PieChartView, required item of the PieChartDataView gets selected.In the same way, if you touch any item of the PieChartDataView, that item gets selected and required segment of the PieChartView comes apart. PieChartDataView is also customizable. You can change the selector color, set the color box shape as square, circle and triangle, and etc.

### Usage
#### XML
```XML
<volkanatalan.chartview.data_views.PieChartDataView
    android:id="@+id/pieChartDataView"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content" />
```

#### Java
```Java
PieChartDataView pieChartDataView = findViewById(R.id.pieChartDataView);
pieChartDataView.setData(pieChartData)
    .bindTo(pieChartView)
    .draw();
```
`.draw()` method always have to be called.<br><br>

**If you need more customization:**

```XML
<volkanatalan.chartview.data_views.PieChartDataView
    android:id="@+id/pieChartDataView"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    app:textColor="@color/white"
    app:textSize="40sp"
    app:selectorOverage="50dp"
    app:colorBoxDimension="15dp"
    app:distanceBetweenColorBoxAndText="10dp"
    app:colorBoxShape="CIRCLE"
    app:colorBoxPosition="LEFT" />
```

```Java
pieChartDataView.setData(pieChartData)
    .bindTo(pieChartView)
    .setTextColor(Color.WHITE)
    .setTextSize(40)
    .setSelectorColor(getContext().getResources().getColor(R.color.selector_color))
    .setColorBoxShape(PieChartDataView.ColorBoxShape.SQUARE)
    .setColorBoxDimension(15)
    .setColorBoxPosition(PieChartDataView.ColorBoxPosition.LEFT)
    .setSelectorOverage(50)
    .setDistanceBetweenColorBoxAndText(10)
    .draw();
```
ColorBoxShape enums: **SQUARE**, **CIRCLE**, **TRIANGLE_UP**, **TRIANGLE_RIGHT**, **TRIANGLE_DOWN**, **TRIANGLE_LEFT**
ColorBoxPosition enums: **LEFT**, **RIGHT**
