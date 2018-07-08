package volkanatalan.chartview.data_views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import volkanatalan.chartview.R;
import volkanatalan.chartview.charts.PieChartView;
import volkanatalan.chartview.datas.PieChartData;

import java.util.ArrayList;

public class PieChartDataView extends LinearLayout {
  LinearLayout root = this;
  private Context context;
  private int textColor = Color.BLACK;
  private int textSize = 14;
  private int colorBoxDimension = 10;
  private int colorBoxMarginEnd = 20;
  private int padding = -1;
  private int paddingStart, paddingEnd, paddingLeft, paddingTop, paddingRight, paddingBottom;
  private int margin, marginLeft, marginTop, marginRight, marginBottom;
  private int selectedSegment = 0;
  private int[] colorList = getContext().getResources().getIntArray(R.array.pie_chart_color_list);
  private ArrayList<PieChartData> pieChartValues;
  private LinearLayout horizontalLL;
  private LinearLayout colorBox;
  private TextView labelTV;
  private AttributeSet attrs;
  private PieChartView pieChartView;
  
  public PieChartDataView(Context context) {
    super(context);
    this.context = context;
    start();
  }
  
  public PieChartDataView(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    this.context = context;
    this.attrs = attrs;
    start();
  }
  
  public PieChartDataView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    this.context = context;
    this.attrs = attrs;
    start();
  }
  
  @SuppressLint("ClickableViewAccessibility")
  private void start() {
    if (attrs != null) {
      TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PieChartValueView);
      padding = typedArray.getDimensionPixelOffset(R.styleable.PieChartValueView_android_padding, padding);
  
      if (padding != -1) {
        paddingLeft = padding;
        paddingTop = padding;
        paddingRight = padding;
        paddingBottom = padding;
      }
  
      paddingStart = typedArray.getDimensionPixelOffset(R.styleable.PieChartValueView_android_paddingStart, paddingStart);
      paddingEnd = typedArray.getDimensionPixelOffset(R.styleable.PieChartValueView_android_paddingEnd, paddingEnd);
      paddingLeft = typedArray.getDimensionPixelOffset(R.styleable.PieChartValueView_android_paddingLeft, paddingLeft);
      paddingTop = typedArray.getDimensionPixelOffset(R.styleable.PieChartValueView_android_paddingTop, paddingTop);
      paddingRight = typedArray.getDimensionPixelOffset(R.styleable.PieChartValueView_android_paddingRight, paddingRight);
      paddingBottom = typedArray.getDimensionPixelOffset(R.styleable.PieChartValueView_android_paddingBottom, paddingBottom);
      typedArray.recycle();
    }
    
    OnTouchListener onTouchListener = new OnTouchListener() {
      @Override
      public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
          case MotionEvent.ACTION_DOWN:
            double coordY = motionEvent.getY();
            for (int i = 0; i < pieChartValues.size(); i++) {
              if (coordY > root.getChildAt(i).getTop() && coordY < root.getChildAt(i).getBottom()) {
                pieChartView.setSelectedSegment(i);
                pieChartView.invalidate();
              }
            }
            break;
        }
        return true;
      }
    };
    setOnTouchListener(onTouchListener);
  }
  
  @SuppressLint("ClickableViewAccessibility")
  public void draw() {
    removeAllViews();
    LayoutParams colorBoxParams = new LayoutParams(colorBoxDimension, colorBoxDimension);
    colorBoxParams.setMarginEnd(colorBoxMarginEnd);
  
    for (int i = 0; i < pieChartValues.size(); i++) {
      horizontalLL = new LinearLayout(context);
      colorBox = new LinearLayout(context);
      labelTV = new TextView(context);
  
      Log.e("selectedSegment", selectedSegment + "");
      if (i == selectedSegment)
        labelTV.setTypeface(Typeface.DEFAULT_BOLD);
      else
        labelTV.setTypeface(Typeface.DEFAULT);
    
      this.setOrientation(VERTICAL);
      this.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
    
      LayoutParams horizontalLLParams = new LayoutParams(
          LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
      
      if (i == pieChartValues.size() - 1) {
        horizontalLLParams.setMargins(0, 0, 0, 0);
      } else {
        horizontalLLParams.setMargins(
            marginLeft, marginTop, marginRight, marginBottom);
      }
    
      horizontalLL.setLayoutParams(horizontalLLParams);
      horizontalLL.setOrientation(HORIZONTAL);
      horizontalLL.setGravity(Gravity.CENTER_VERTICAL);
    
      colorBox.setLayoutParams(colorBoxParams);
    
      labelTV.setTextColor(Color.BLACK);
      labelTV.setTextSize(30);
    
      colorBox.setBackgroundColor(colorList[i]);
      labelTV.setText(pieChartValues.get(i).getTitle() + " (" + pieChartValues.get(i).getValue() + ")");
      horizontalLL.addView(colorBox);
      horizontalLL.addView(labelTV);
      this.addView(horizontalLL);
    }
  }
  
  public void bindTo(PieChartView pieChartView) {
    this.pieChartView = pieChartView;
  
    this.pieChartView.setSelectedSegmentChangeListener(new PieChartView.SelectedSegmentChangeListener() {
      @Override
      public void onChange(int position) {
        selectedSegment = position;
        draw();
      }
    });
  }
  
  public void setData(ArrayList<PieChartData> pieChartValues) {
    this.pieChartValues = pieChartValues;
  }
  
  public int getTextColor() {
    return textColor;
  }
  
  public void setTextColor(int textColor) {
    this.textColor = textColor;
  }
  
  public int getTextSize() {
    return textSize;
  }
  
  public void setTextSize(int textSize) {
    this.textSize = textSize;
  }
  
  public int[] getColorList() {
    return colorList;
  }
  
  public void setColorList(int[] colorList) {
    this.colorList = colorList;
  }
  
  public int getColorBoxDimension() {
    return colorBoxDimension;
  }
  
  public void setColorBoxDimension(int colorBoxDimension) {
    this.colorBoxDimension = colorBoxDimension;
  }
  
  public int getColorBoxMarginEnd() {
    return colorBoxMarginEnd;
  }
  
  public void setColorBoxMarginEnd(int colorBoxMarginEnd) {
    this.colorBoxMarginEnd = colorBoxMarginEnd;
  }
  
  @Override
  public int getPaddingLeft() {
    return paddingLeft;
  }
  
  public void setPaddingLeft(int paddingLeft) {
    this.paddingLeft = paddingLeft;
  }
  
  @Override
  public int getPaddingTop() {
    return paddingTop;
  }
  
  public void setPaddingTop(int paddingTop) {
    this.paddingTop = paddingTop;
  }
  
  @Override
  public int getPaddingRight() {
    return paddingRight;
  }
  
  public void setPaddingRight(int paddingRight) {
    this.paddingRight = paddingRight;
  }
  
  @Override
  public int getPaddingBottom() {
    return paddingBottom;
  }
  
  public void setPaddingBottom(int paddingBottom) {
    this.paddingBottom = paddingBottom;
  }
  
  public int getMarginLeft() {
    return marginLeft;
  }
  
  public void setMarginLeft(int marginLeft) {
    this.marginLeft = marginLeft;
  }
  
  public int getMarginTop() {
    return marginTop;
  }
  
  public void setMarginTop(int marginTop) {
    this.marginTop = marginTop;
  }
  
  public int getMarginRight() {
    return marginRight;
  }
  
  public void setMarginRight(int marginRight) {
    this.marginRight = marginRight;
  }
  
  public int getMarginBottom() {
    return marginBottom;
  }
  
  public void setMarginBottom(int marginBottom) {
    this.marginBottom = marginBottom;
  }
}
