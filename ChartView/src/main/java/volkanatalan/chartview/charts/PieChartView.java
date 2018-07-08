package volkanatalan.chartview.charts;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
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
  private Paint paintSegment, paintText, paintMiddleCircle;
  private RectF arcRect;
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
    
    arcRect = new RectF();
  
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
  protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);
    setMeasuredDimension(Math.round(w), Math.round(h));
    
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
  }
  
  @Override
  protected void onDraw(Canvas canvas) {
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
        arcRect.left = xCenter - radius + apartDistance + (float) Math.cos(middleAng) * apartDistance;
        arcRect.right = xCenter + radius - apartDistance + (float) Math.cos(middleAng) * apartDistance;
        arcRect.top = yCenter - radius + apartDistance + (float) Math.sin(middleAng) * apartDistance;
        arcRect.bottom = yCenter + radius - apartDistance + (float) Math.sin(middleAng) * apartDistance;
  
        canvas.drawArc(arcRect, data.get(selectedSegment).getStartAngle(),
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
            arcRect.left = xCenter - radius + apartDistance;
            arcRect.top = yCenter - radius + apartDistance;
            arcRect.right = xCenter + radius - apartDistance;
            arcRect.bottom = yCenter + radius - apartDistance;
  
            canvas.drawArc(arcRect, data.get(i).getStartAngle(), data.get(i).getDrawingDegree(),
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
  
  public void setAlignment(int alignment) {
    this.alignment = alignment;
  }
  
  public ArrayList<PieChartData> getData() {
    return data;
  }
  
  public void setData(ArrayList<PieChartData> data) {
    this.data = data;
  }
  
  public int[] getColorList() {
    return colorList;
  }
  
  public void setColorList(int[] colorList) {
    this.colorList = colorList;
  }
  
  public int getTextSize() {
    return textSize;
  }
  
  public void setTextSize(int textSize) {
    this.textSize = textSize;
  }
  
  public int getTextColor() {
    return textColor;
  }
  
  public void setTextColor(int textColor) {
    this.textColor = textColor;
  }
  
  public void setMiddleCircleColor(int middleCircleColor) {
    this.middleCircleColor = middleCircleColor;
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
  
  public void setSelectedSegment(int selectedSegment) {
    this.selectedSegment = selectedSegment;
    if (selectedSegmentChangeListener != null) selectedSegmentChangeListener.onChange(selectedSegment);
  }
  
  public void setLockableScrollView(LockableScrollView lockableScrollView) {
    this.lockableScrollView = lockableScrollView;
  }
  
  public SelectedSegmentChangeListener getSelectedSegmentChangeListener() {
    return selectedSegmentChangeListener;
  }
  
  public void setSelectedSegmentChangeListener(SelectedSegmentChangeListener listener) {
    this.selectedSegmentChangeListener = listener;
  }
}