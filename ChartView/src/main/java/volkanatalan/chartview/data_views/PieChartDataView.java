package volkanatalan.chartview.data_views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;
import android.graphics.drawable.ShapeDrawable;
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
  private View colorBox;
  private TextView labelTV;
  private AttributeSet attrs;
  private PieChartView pieChartView;
  public enum ColorBoxShape {RECT, CIRCLE, TRIANGLE_UP, TRIANGLE_RIGHT, TRIANGLE_DOWN, TRIANGLE_LEFT}
  private ColorBoxShape colorBoxShape = ColorBoxShape.CIRCLE;
  
  
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
      final int pos = i;
      
      horizontalLL = new LinearLayout(context);
      labelTV = new TextView(context);
      colorBox = new View(context){
        
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
  
      if (i == selectedSegment)
        labelTV.setTypeface(Typeface.DEFAULT_BOLD);
      else
        labelTV.setTypeface(Typeface.DEFAULT);
      labelTV.setPadding(10, 0, 0, 0);
    
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
    
      //colorBox.setLayoutParams(colorBoxParams);
    
      labelTV.setTextColor(Color.BLACK);
      labelTV.setTextSize(30);
    
      labelTV.setText(pieChartValues.get(i).getTitle() + " (" + pieChartValues.get(i).getValue() + ")");
      horizontalLL.addView(colorBox);
      horizontalLL.addView(labelTV);
      this.addView(horizontalLL);
    }
  }
  
  public PieChartDataView bindTo(PieChartView pieChartView) {
    this.pieChartView = pieChartView;
  
    this.pieChartView.setSelectedSegmentChangeListener(new PieChartView.SelectedSegmentChangeListener() {
      @Override
      public void onChange(int position) {
        selectedSegment = position;
        draw();
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
  
  @Override
  public int getPaddingLeft() {
    return paddingLeft;
  }
  
  public PieChartDataView setPaddingLeft(int paddingLeft) {
    this.paddingLeft = paddingLeft;
    return this;
  }
  
  @Override
  public int getPaddingTop() {
    return paddingTop;
  }
  
  public PieChartDataView setPaddingTop(int paddingTop) {
    this.paddingTop = paddingTop;
    return this;
  }
  
  @Override
  public int getPaddingRight() {
    return paddingRight;
  }
  
  public PieChartDataView setPaddingRight(int paddingRight) {
    this.paddingRight = paddingRight;
    return this;
  }
  
  @Override
  public int getPaddingBottom() {
    return paddingBottom;
  }
  
  public PieChartDataView setPaddingBottom(int paddingBottom) {
    this.paddingBottom = paddingBottom;
    return this;
  }
  
  public int getMarginLeft() {
    return marginLeft;
  }
  
  public PieChartDataView setMarginLeft(int marginLeft) {
    this.marginLeft = marginLeft;
    return this;
  }
  
  public int getMarginTop() {
    return marginTop;
  }
  
  public PieChartDataView setMarginTop(int marginTop) {
    this.marginTop = marginTop;
    return this;
  }
  
  public int getMarginRight() {
    return marginRight;
  }
  
  public PieChartDataView setMarginRight(int marginRight) {
    this.marginRight = marginRight;
    return this;
  }
  
  public int getMarginBottom() {
    return marginBottom;
  }
  
  public PieChartDataView setMarginBottom(int marginBottom) {
    this.marginBottom = marginBottom;
    return this;
  }
}
