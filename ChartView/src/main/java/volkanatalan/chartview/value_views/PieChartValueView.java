package volkanatalan.chartview.value_views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import volkanatalan.chartview.R;
import volkanatalan.chartview.charts.PieChartView;
import volkanatalan.chartview.datas.PieChartData;

import java.util.ArrayList;

public class PieChartValueView extends LinearLayout {
  LinearLayout root = this;
  private Context context;
  private int textColor = Color.BLACK;
  private int textSize = 14;
  private int colorBoxDimension = 10;
  private int colorBoxMarginEnd = 20;
  private int padding = -1;
  private int paddingStart = 0;
  private int paddingEnd = 0;
  private int paddingLeft = 0;
  private int paddingTop = 0;
  private int paddingRight = 0;
  private int paddingBottom = 0;
  private int horizontalLLMarginLeft = 0;
  private int horizontalLLMarginTop = 0;
  private int horizontalLLMarginRight = 0;
  private int horizontalLLMarginBottom = 10;
  private ArrayList<PieChartData> pieChartValues;
  private int[] colorList = getContext().getResources().getIntArray(R.array.pie_chart_color_list);
  private LinearLayout horizontalLL;
  private LinearLayout colorBox;
  private TextView labelTV;
  private AttributeSet attrs;
  private PieChartView pieChartView;
  private Vibrator vibrator;
  
  public PieChartValueView(Context context) {
    super(context);
    this.context = context;
    start();
  }
  
  public PieChartValueView(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    this.context = context;
    this.attrs = attrs;
    start();
  }
  
  public PieChartValueView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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
                vibrator.vibrate(500);
              }
            }
            break;
        }
        return true;
      }
    };
    setOnTouchListener(onTouchListener);
  
    vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
  }
  
  @SuppressLint("ClickableViewAccessibility")
  public void draw() {
    LayoutParams colorBoxParams = new LayoutParams(colorBoxDimension, colorBoxDimension);
    colorBoxParams.setMarginEnd(colorBoxMarginEnd);
  
    for (int i = 0; i < pieChartValues.size(); i++) {
      horizontalLL = new LinearLayout(context);
      colorBox = new LinearLayout(context);
      labelTV = new TextView(context);
    
      this.setOrientation(VERTICAL);
      this.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
    
      LayoutParams horizontalLLParams = new LayoutParams(
          LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
      
      if (i == pieChartValues.size() - 1) {
        horizontalLLParams.setMargins(0, 0, 0, 0);
      } else {
        horizontalLLParams.setMargins(
            horizontalLLMarginLeft, horizontalLLMarginTop, horizontalLLMarginRight, horizontalLLMarginBottom);
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
  
  public int getHorizontalLLMarginLeft() {
    return horizontalLLMarginLeft;
  }
  
  public void setHorizontalLLMarginLeft(int horizontalLLMarginLeft) {
    this.horizontalLLMarginLeft = horizontalLLMarginLeft;
  }
  
  public int getHorizontalLLMarginTop() {
    return horizontalLLMarginTop;
  }
  
  public void setHorizontalLLMarginTop(int horizontalLLMarginTop) {
    this.horizontalLLMarginTop = horizontalLLMarginTop;
  }
  
  public int getHorizontalLLMarginRight() {
    return horizontalLLMarginRight;
  }
  
  public void setHorizontalLLMarginRight(int horizontalLLMarginRight) {
    this.horizontalLLMarginRight = horizontalLLMarginRight;
  }
  
  public int getHorizontalLLMarginBottom() {
    return horizontalLLMarginBottom;
  }
  
  public void setHorizontalLLMarginBottom(int horizontalLLMarginBottom) {
    this.horizontalLLMarginBottom = horizontalLLMarginBottom;
  }
}
