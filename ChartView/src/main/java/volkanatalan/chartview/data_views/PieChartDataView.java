package volkanatalan.chartview.data_views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import volkanatalan.chartview.R;
import volkanatalan.chartview.charts.PieChartView;
import volkanatalan.chartview.datas.PieChartData;

import java.util.ArrayList;

public class PieChartDataView extends RelativeLayout {
  private Context context;
  private int textColor = Color.BLACK;
  private int textSize = 14;
  private int selectorColor = Color.GRAY;
  private int colorBoxDimension = 10;
  private int colorBoxMarginEnd = 20;
  private int selectedSegment = 0;
  private int widthMeasureMode;
  private int containerLPaddingLeft = 0;
  private int containerLPaddingRight = 0;
  private int textPaddingLeft = 0;
  private int[] colorList = getContext().getResources().getIntArray(R.array.pie_chart_color_list);
  private LinearLayout mainContainer;
  private RelativeLayout selector;
  private ArrayList<PieChartData> pieChartValues;
  private ArrayList<LinearLayout> containerLayoutList = new ArrayList<>();
  private PieChartView pieChartView;
  private ColorBoxShape colorBoxShape = ColorBoxShape.CIRCLE;
  public enum ColorBoxShape {RECT, CIRCLE, TRIANGLE_UP, TRIANGLE_RIGHT, TRIANGLE_DOWN, TRIANGLE_LEFT}
  
  
  public PieChartDataView(Context context) {
    super(context);
    this.context = context;
    start();
    if (isInEditMode()) {
      editModeDisplay();
    }
  }
  
  public PieChartDataView(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    this.context = context;
    start();
    if (isInEditMode()) {
      editModeDisplay();
    }
  }
  
  public PieChartDataView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    this.context = context;
    start();
    if (isInEditMode()) {
      editModeDisplay();
    }
  }
  
  private void editModeDisplay() {
    ArrayList<PieChartData> data = new ArrayList<>();
    data.add(new PieChartData(500, "Title 1"));
    data.add(new PieChartData(400, "Title 2"));
    data.add(new PieChartData(300, "Title 3"));
    data.add(new PieChartData(200, "Title 4"));
    data.add(new PieChartData(100, "Title 5"));
  
    this.setData(data)
        .setColorBoxDimension(15)
        .setColorBoxShape(ColorBoxShape.CIRCLE)
        .draw();
  }
  
  @SuppressLint("ClickableViewAccessibility")
  private void start() {
    OnTouchListener onTouchListener = new OnTouchListener() {
      @Override
      public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
          case MotionEvent.ACTION_DOWN:
            double coordY = motionEvent.getY();
            for (int i = 0; i < pieChartValues.size(); i++) {
              if (coordY > mainContainer.getChildAt(i).getTop() + getPaddingTop()
                      && coordY < mainContainer.getChildAt(i).getBottom() + getPaddingTop()) {
                pieChartView.setSelectedSegment(i).invalidate();
              }
            }
            break;
        }
        return true;
      }
    };
    setOnTouchListener(onTouchListener);
    getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
      @Override
      public void onGlobalLayout() {
//        int bottom = containerLayoutList.get(0).getBottom();
//        int top = containerLayoutList.get(0).getTop();
//        calibrateSelector(top, bottom);
        if (widthMeasureMode == MeasureSpec.AT_MOST) {
          setLayoutParams(new LinearLayout.LayoutParams(
              mainContainer.getWidth() + getPaddingRight() + getPaddingLeft(), getHeight()));
        }
        getViewTreeObserver().removeOnGlobalLayoutListener(this);
      }
    });
  }
  
  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    widthMeasureMode = MeasureSpec.getMode(widthMeasureSpec);
  }
  
  @SuppressLint("SetTextI18n")
  public void draw() {
    selector = new RelativeLayout(context);
    mainContainer = new LinearLayout(context);
    
    for (int i = 0; i < pieChartValues.size(); i++) {
      final int pos = i;
  
      LinearLayout containerLayout = new LinearLayout(context);
      TextView titleTV = new TextView(context);
      View colorBox = new View(context) {
    
        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
          setMeasuredDimension(colorBoxDimension, colorBoxDimension);
        }
    
        Path triangle = new Path();
        Paint colorBoxPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    
        @Override
        protected void onDraw(Canvas canvas) {
          super.onDraw(canvas);
          colorBoxPaint.setStyle(Paint.Style.FILL);
          colorBoxPaint.setDither(true);
          colorBoxPaint.setColor(colorList[pos]);
      
          if (colorBoxShape == ColorBoxShape.CIRCLE) {
            canvas.drawCircle(canvas.getWidth() / 2, canvas.getHeight() / 2,
                canvas.getHeight() / 2, colorBoxPaint);
        
          } else if (colorBoxShape == ColorBoxShape.RECT) {
            canvas.drawPaint(colorBoxPaint);
        
          } else if (colorBoxShape == ColorBoxShape.TRIANGLE_UP) {
            triangle.moveTo(canvas.getWidth() / 2, 0);
            triangle.lineTo(canvas.getWidth(), canvas.getHeight());
            triangle.lineTo(0, canvas.getHeight());
            triangle.close();
            canvas.drawPath(triangle, colorBoxPaint);
        
          } else if (colorBoxShape == ColorBoxShape.TRIANGLE_RIGHT) {
            triangle.moveTo(0, 0);
            triangle.lineTo(canvas.getWidth(), canvas.getHeight() / 2);
            triangle.lineTo(0, canvas.getHeight());
            triangle.close();
            canvas.drawPath(triangle, colorBoxPaint);
        
          } else if (colorBoxShape == ColorBoxShape.TRIANGLE_DOWN) {
            triangle.moveTo(0, 0);
            triangle.lineTo(canvas.getWidth(), 0);
            triangle.lineTo(canvas.getWidth() / 2, canvas.getHeight());
            triangle.close();
            canvas.drawPath(triangle, colorBoxPaint);
        
          } else if (colorBoxShape == ColorBoxShape.TRIANGLE_LEFT) {
            triangle.moveTo(canvas.getWidth(), 0);
            triangle.lineTo(canvas.getWidth(), canvas.getHeight());
            triangle.lineTo(0, canvas.getHeight() / 2);
            triangle.close();
            canvas.drawPath(triangle, colorBoxPaint);
          }
        }
      };
  
      titleTV.setLayoutParams(new LayoutParams(
          LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
      containerLayout.setLayoutParams(new LayoutParams(
          LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
      
      containerLayout.setOrientation(LinearLayout.HORIZONTAL);
      containerLayout.setGravity(Gravity.CENTER_VERTICAL);
      containerLayout.setPadding(containerLPaddingLeft, 0, containerLPaddingRight, 0);
    
      titleTV.setTextColor(textColor);
      titleTV.setTextSize(textSize);
      titleTV.setText(pieChartValues.get(i).getTitle() + " (" + pieChartValues.get(i).getValue() + ")");
      titleTV.setPadding(textPaddingLeft, 0, 0, 0);
      
      
      containerLayout.addView(colorBox);
      containerLayout.addView(titleTV);
      mainContainer.addView(containerLayout);
      containerLayoutList.add((LinearLayout) mainContainer.getChildAt(i));
  
      if (i == selectedSegment) {
      
      }
    }
    
    this.addView(selector);
    this.addView(mainContainer);
  
    LayoutParams mainContainerLP = new LayoutParams(
        LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    mainContainerLP.addRule(CENTER_HORIZONTAL);
    mainContainer.setLayoutParams(mainContainerLP);
    mainContainer.setOrientation(LinearLayout.VERTICAL);
  
    calibrateSelector();
  }
  
  @Override
  protected void onFinishInflate() {
    super.onFinishInflate();
  }
  
  @Override
  protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);
  }
  
  private void calibrateSelector() {
    selector.setLayoutParams(new LayoutParams(
        LayoutParams.MATCH_PARENT, 0));
    selector.setTop(0);
    selector.setBottom(0);
    selector.setBackgroundColor(selectorColor);
  }
  
  public PieChartDataView bindTo(PieChartView pieChartView) {
    this.pieChartView = pieChartView;
  
    this.pieChartView.setSelectedSegmentChangeListener(new PieChartView.SelectedSegmentChangeListener() {
      @Override
      public void onChange(int position) {
        selectedSegment = position;
        selector.setTop(containerLayoutList.get(position).getTop() + getPaddingTop());
        selector.setBottom(containerLayoutList.get(position).getBottom() + getPaddingTop());
      }
    });
    return this;
  }
  
  public PieChartDataView setData(ArrayList<PieChartData> pieChartValues) {
    this.pieChartValues = pieChartValues;
    return this;
  }
  
  public int getTextColor() {
    return textColor;
  }
  
  public PieChartDataView setTextColor(int textColor) {
    this.textColor = textColor;
    return this;
  }
  
  public int getTextSize() {
    return textSize;
  }
  
  public PieChartDataView setTextSize(int textSize) {
    this.textSize = textSize;
    return this;
  }
  
  public int getSelectorColor() {
    return selectorColor;
  }
  
  public PieChartDataView setSelectorColor(int selectorColor) {
    this.selectorColor = selectorColor;
    return this;
  }
  
  public int[] getColorList() {
    return colorList;
  }
  
  public PieChartDataView setColorList(int[] colorList) {
    this.colorList = colorList;
    return this;
  }
  
  public PieChartDataView setColorBoxShape(ColorBoxShape shape) {
    this.colorBoxShape = shape;
    return this;
  }
  
  public int getColorBoxDimension() {
    return colorBoxDimension;
  }
  
  public PieChartDataView setColorBoxDimension(int colorBoxDimension) {
    this.colorBoxDimension = colorBoxDimension;
    return this;
  }
  
  public int getColorBoxMarginEnd() {
    return colorBoxMarginEnd;
  }
  
  public PieChartDataView setColorBoxMarginEnd(int colorBoxMarginEnd) {
    this.colorBoxMarginEnd = colorBoxMarginEnd;
    return this;
  }
  
  public PieChartDataView makeSelectorLonger(int px) {
    containerLPaddingLeft = px;
    containerLPaddingRight = px;
    return this;
  }
  
  public PieChartDataView setDistanceBetweenColorBoxAndText(int px) {
    textPaddingLeft = px;
    return this;
  }
}
