# :star: ChartViewProject :star:
![ChartViewProject](https://github.com/thehorsebuyer/ChartViewProject/blob/master/images/ChartViewProjectSS.png)

<br>
ChartViewProject is an unfinished project which contains a pie chart for now. But in the future other chart types will be added. The charts in this project will be interactive, animated and good-looking. <br>

:point_right:[Watch usage video](https://youtu.be/-3yu9N0Ysa8)

## Views in this project
1- PieChartView<br>
2- PieChartDataView<br>
3- LockableScrollView

### 1- PieChartView

![PieChartView](https://github.com/thehorsebuyer/ChartViewProject/blob/master/images/PieChartView.jpg)

With this view you can add a pie chart to your project. PieChartView is interactable. When you touch a segment of the chart, the segment comes apart and the percentage of the segment is written on the center of the chart. PieChartView is customizable. You can define your own color set to it, you can change its starting angle and etc.

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
ArrayList<PieChartData> pieChartData = new ArrayList<>();
pieChartData.add(new PieChartData(815, "Item 1"));
pieChartData.add(new PieChartData(516, "Item 2"));
pieChartData.add(new PieChartData(249, "Item 3"));
pieChartData.add(new PieChartData(241, "Item 4"));
pieChartData.add(new PieChartData(168, "Item 5"));
                .
                .
                .
    
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
    app:startAngle="270"
    app:animationDuration="300"/>
```

```Java
pieChartView.setData(pieChartData)
    .setApartDistance(10)
    .setPercentageTextSize(50)
    .setPercentageTextColor(Color.WHITE)
    .setCenterCircleColor(getContext().getResources().getColor(R.color.spaceGray))
    .setColorList(getContext().getResources().getIntArray(R.array.color_list))
    .setAnimationDuration(300);
```

### 2- PieChartDataView

![PieChartDataView](https://github.com/thehorsebuyer/ChartViewProject/blob/master/images/PieChartDataView.jpg)

With this view you can show the data of the PieChartView. It shows the color of the item, item name and item amount in the PieChartView. PieChartDataView is interactable. If you touch any segment of the PieChartView, required item of the PieChartDataView gets selected.In the same way, if you touch any item of the PieChartDataView, that item gets selected and required segment of the PieChartView comes apart. PieChartDataView is also customizable. You can change the selector color, set the color box shape as square, circle and triangle, and etc.

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
    .bindTo(pieChartView);
```

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
    app:colorBoxPosition="LEFT"
    app:animationDuration="300" />
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
    .setAnimationDuration(300);
```
ColorBoxShape enums: **SQUARE**, **CIRCLE**, **TRIANGLE_UP**, **TRIANGLE_RIGHT**, **TRIANGLE_DOWN**, **TRIANGLE_LEFT**
ColorBoxPosition enums: **LEFT**, **RIGHT**

### 3- LockableScrollView
LockableScrollView is a ScrollView which lets you lock and unlock itself whenever you want. It is written by [chittaranjan-khuntia](https://gist.github.com/chittaranjan-khuntia/42d5429ac37b7aea3cb22fb51c8729b4). It is needed in this project, because if you put PieChartView into a ScrollView and move your finger on the PieChartView, LockableScrollView locks itself.

### Usage
```XML
<volkanatalan.chartview.LockableScrollView
    android:id="@+id/lockableScrollView"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:gravity="center"
        android:orientation="vertical">

        <volkanatalan.chartview.charts.PieChartView
            android:id="@+id/pieChartView"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content" />

        <volkanatalan.chartview.data_views.PieChartDataView
            android:id="@+id/pieChartDataView"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content" />

    </LinearLayout>
</volkanatalan.chartview.LockableScrollView>
```

```Java
LockableScrollView lockableScrollView = findViewById(R.id.lockableScrollView);
PieChartView pieChartView = findViewById(R.id.pieChartView);

pieChartView.setData(data)
    .setLockableScrollView(lockableScrollView);
```
