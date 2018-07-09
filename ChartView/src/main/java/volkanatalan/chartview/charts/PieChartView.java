package volkanatalan.chartview.charts;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import volkanatalan.chartview.Calc;
import volkanatalan.chartview.LockableScrollView;
import volkanatalan.chartview.R;
import volkanatalan.chartview.datas.PieChartData;

import java.util.ArrayList;

public class PieChartView extends View {
  
  private int alignment = 0;
  private int textSize = 30;
  private int apartDistance = 10;
  private int selectedSegment = 0;
  private int textColor = Color.BLACK;
  private int middleCircleColor = Color.WHITE;
  private float radius, xCenter, yCenter;
  private float startAngle = 270;
  private Paint paintSegment, paintText, paintMiddleCircle, paintBackground;
  private RectF selectedArcRect, unselectedArcRect;
  private ArrayList<PieChartData> data;
  private LockableScrollView lockableScrollView;
  private int[] colorList = getContext().getResources().getIntArray(R.array.pie_chart_color_list);
  private SelectedSegmentChangeListener selectedSegmentChangeListener;
  
  public PieChartView(Context context) {
    super(context);
    start();
  }
  
  public PieChartView(Context context, AttributeSet attrs) {
    super(context, attrs);
    start();
  }
  
  public PieChartView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    start();
  }
  
  public interface SelectedSegmentChangeListener {
    void onChange(int position);
  }
  
  @SuppressLint("ClickableViewAccessibility")
  private void start() {
    paintSegment = new Paint(Paint.ANTI_ALIAS_FLAG);
    paintSegment.setStyle(Paint.Style.FILL);
    paintSegment.setDither(true);
  
    paintMiddleCircle = new Paint(Paint.ANTI_ALIAS_FLAG);
    paintMiddleCircle.setStyle(Paint.Style.FILL);
    paintMiddleCircle.setDither(true);
  
    paintText = new Paint(Paint.ANTI_ALIAS_FLAG);
    paintText.setDither(true);
    paintText.setTextAlign(Paint.Align.CENTER);
  
    paintBackground = new Paint(Paint.ANTI_ALIAS_FLAG);
    paintBackground.setStyle(Paint.Style.FILL);
    paintBackground.setDither(true);
    paintBackground.setColor(Color.GRAY);
    
    selectedArcRect = new RectF();
    
    unselectedArcRect = new RectF();
  
    OnTouchListener onTouchListener = new OnTouchListener() {
      @Override
      public boolean onTouch(View view, MotionEvent motionEvent) {
        double touchX = motionEvent.getX();
        double touchY = motionEvent.getY();
        double coordX = touchX - xCenter;
        double coordY = touchY - yCenter;
        double touchAngle = Math.atan2(coordY, coordX);
    
        if (touchAngle < 0) {
          touchAngle = 2 * Math.PI + touchAngle;
        }
    
        switch (motionEvent.getAction()) {
          case MotionEvent.ACTION_DOWN:
            if (lockableScrollView != null)
            lockableScrollView.setScrollingEnabled(false);
            for (int i = 0; i < data.size(); i++) {
              if ((data.get(i).getStartAngleRadian() < touchAngle &&
                       data.get(i).getSweepAngleRadian() > touchAngle) ||
                      (data.get(i).getStartAngleRadian() < touchAngle + 2 * Math.PI &&
                           data.get(i).getSweepAngleRadian() > touchAngle + 2 * Math.PI))
              {
                setSelectedSegment(i);
                invalidate();
              }
            }
            break;
          case MotionEvent.ACTION_MOVE:
            if (lockableScrollView != null)
              lockableScrollView.setScrollingEnabled(false);
            for (int i = 0; i < data.size(); i++) {
            if ((data.get(i).getStartAngleRadian() < touchAngle &&
                     data.get(i).getSweepAngleRadian() > touchAngle) ||
                    (data.get(i).getStartAngleRadian() < touchAngle + 2 * Math.PI &&
                         data.get(i).getSweepAngleRadian() > touchAngle + 2 * Math.PI))
            {
              setSelectedSegment(i);
              invalidate();
            }
          }
        
            break;
          case MotionEvent.ACTION_UP:
            if (lockableScrollView != null)
              lockableScrollView.setScrollingEnabled(true);
        }
        return true;
      }
    };
    setOnTouchListener(onTouchListener);
  }
  
  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    int desiredWidth = getSuggestedMinimumWidth() + getPaddingLeft() + getPaddingRight();
    int desiredHeight = getSuggestedMinimumHeight() + getPaddingTop() + getPaddingBottom();
    
    setMeasuredDimension(measureDimension(widthMeasureSpec, heightMeasureSpec),
        measureDimension(heightMeasureSpec, widthMeasureSpec));
  }
  
  private int measureDimension(int thisMeasureSpec, int otherMeasureSpec) {
    int thisSpecMode = MeasureSpec.getMode(thisMeasureSpec);
    int thisSpecSize = MeasureSpec.getSize(thisMeasureSpec);
    int otherSpecSize = MeasureSpec.getSize(otherMeasureSpec);
    
    if (thisSpecMode == MeasureSpec.EXACTLY)
      return thisSpecSize;
    else
      return Math.min(thisSpecSize, otherSpecSize) + getPaddingLeft() + getPaddingRight();
  }
  
  @Override
  protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);
    setMeasuredDimension(w, h);
    
    radius = Math.min(w, h) / 2;
    xCenter = w / 2;
    yCenter = h / 2;
  
    if (data != null) {
      if (data.size() > 0) {
        double sum = 0;
        for (PieChartData value : data) {
          sum += value.getValue();
        }
        double devisor360 = sum / 360;
        double devisor100 = sum / 100;
        
        for (int i = 0; i < data.size(); i++) {
          startAngle = startAngle % 360;
          double degree = data.get(i).getValue() / devisor360;
          float drawingDegree = (float) degree;
          double percentage = data.get(i).getValue() / devisor100;
          double startAngleRadian = Calc.degreeToRadian(startAngle);
          double middleAngleRadian = Calc.degreeToRadian(startAngle + degree / 2);
          double sweepAngleRadian = Calc.degreeToRadian(startAngle + degree);
  
          data.get(i).setStartAngle(startAngle);
          data.get(i).setStartAngleRadian(startAngleRadian);
          data.get(i).setDegree(degree);
          data.get(i).setPercentage(percentage);
          data.get(i).setDrawingDegree(drawingDegree);
          data.get(i).setMiddleAngleRadian(middleAngleRadian);
          data.get(i).setSweepAngleRadian(sweepAngleRadian);
          startAngle += degree;
        }
      }
    }
  
    unselectedArcRect.left = xCenter - radius + apartDistance;
    unselectedArcRect.top = yCenter - radius + apartDistance;
    unselectedArcRect.right = xCenter + radius - apartDistance;
    unselectedArcRect.bottom = yCenter + radius - apartDistance;
  }
  
  @Override
  protected void onDraw(Canvas canvas) {
    //canvas.drawPaint(paintBackground);
    if (data != null) {
      if (data.size() > 0) {
        paintText.setColor(textColor);
        paintText.setTextSize(textSize);
        
        paintMiddleCircle.setColor(middleCircleColor);
        
        // Set the selected circle segment's color
        if (data.get(selectedSegment).getColor() == 0)
          paintSegment.setColor(colorList[selectedSegment]);
        else
          paintSegment.setColor(data.get(selectedSegment).getColor());
        
        // Draw selected circle segment
        double middleAng = data.get(selectedSegment).getMiddleAngleRadian();
        selectedArcRect.left = xCenter - radius + apartDistance + (float) Math.cos(middleAng) * apartDistance;
        selectedArcRect.right = xCenter + radius - apartDistance + (float) Math.cos(middleAng) * apartDistance;
        selectedArcRect.top = yCenter - radius + apartDistance + (float) Math.sin(middleAng) * apartDistance;
        selectedArcRect.bottom = yCenter + radius - apartDistance + (float) Math.sin(middleAng) * apartDistance;
  
        canvas.drawArc(selectedArcRect, data.get(selectedSegment).getStartAngle(),
            data.get(selectedSegment).getDrawingDegree(),
            true, paintSegment);
  
        paintSegment.setColor(middleCircleColor);
        canvas.drawCircle(xCenter + (float) Math.cos(middleAng) * apartDistance,
            yCenter + (float) Math.sin(middleAng) * apartDistance, radius / 2, paintMiddleCircle);
        
        // Draw other circle segments
        for (int i = 0; i < data.size(); i++) {
          
          // Set the circle segment's color
          if (data.get(i).getColor() == 0) {
            paintSegment.setColor(colorList[i]);
            paintSegment.setColor(colorList[i]);
          } else {
            paintSegment.setColor(data.get(i).getColor());
            paintSegment.setColor(data.get(i).getColor());
          }
          
          // Draw a circle segment
          if (i != selectedSegment) {
            canvas.drawArc(unselectedArcRect, data.get(i).getStartAngle(), data.get(i).getDrawingDegree(),
                true, paintSegment);
          }
        }
        
        // Draw gradient
        //canvas.drawCircle(xCenter, yCenter, radius, paintGradient);
  
        // Make hole
        canvas.drawCircle(xCenter, yCenter, radius / 2, paintMiddleCircle);
        
        canvas.drawText(Calc.round(data.get(selectedSegment).getPercentage(), 1) + "%",
            xCenter, yCenter + textSize / 3, paintText);
  
        //arcShape.draw(canvas, paintMiddleCircle);
      }
    }
  }
  
  public void draw() {
    invalidate();
  }
  
  public int getAlignment() {
    return alignment;
  }
  
  public PieChartView setAlignment(int alignment) {
    this.alignment = alignment;
    return this;
  }
  
  public ArrayList<PieChartData> getData() {
    return data;
  }
  
  public PieChartView setData(ArrayList<PieChartData> data) {
    this.data = data;
    return this;
  }
  
  public int[] getColorList() {
    return colorList;
  }
  
  public PieChartView setColorList(int[] colorList) {
    this.colorList = colorList;
    return this;
  }
  
  public int getPercentageTextSize() {
    return textSize;
  }
  
  public PieChartView setPercentageTextSize(int textSize) {
    this.textSize = textSize;
    return this;
  }
  
  public int getPercentageTextColor() {
    return textColor;
  }
  
  public PieChartView setPercentageTextColor(int textColor) {
    this.textColor = textColor;
    return this;
  }
  
  public PieChartView setMiddleCircleColor(int middleCircleColor) {
    this.middleCircleColor = middleCircleColor;
    return this;
  }
  
  public float getRadius() {
    return radius;
  }
  
  public float getxCenter() {
    return xCenter;
  }
  
  public float getyCenter() {
    return yCenter;
  }
  
  public int getSelectedSegment() {
    return selectedSegment;
  }
  
  public PieChartView setSelectedSegment(int selectedSegment) {
    this.selectedSegment = selectedSegment;
    if (selectedSegmentChangeListener != null)
      selectedSegmentChangeListener.onChange(selectedSegment);
    return this;
  }
  
  public PieChartView setLockableScrollView(LockableScrollView lockableScrollView) {
    this.lockableScrollView = lockableScrollView;
    return this;
  }
  
  public SelectedSegmentChangeListener getSelectedSegmentChangeListener() {
    return selectedSegmentChangeListener;
  }
  
  public void setSelectedSegmentChangeListener(SelectedSegmentChangeListener listener) {
    this.selectedSegmentChangeListener = listener;
  }
}