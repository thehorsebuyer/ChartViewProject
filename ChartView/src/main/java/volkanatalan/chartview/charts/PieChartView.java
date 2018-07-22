package volkanatalan.chartview.charts;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
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
import java.util.Random;

public class PieChartView extends View {
  
  private Context context;
  private TypedArray typedArray;
  private int apartDistance = 10;
  private float animatedApartDistance;
  private int selectedSegment = 0;
  private int oldSelectedSegment = selectedSegment;
  private int percentageTextSize = 30;
  private int animationDuration = 300;
  private int percentageTextColor = Color.BLACK;
  private int centerCircleColor = Color.WHITE;
  private int[] colorList = getContext().getResources().getIntArray(R.array.pie_chart_color_list);
  private float radius, xCenter, yCenter;
  private float startAngle = 270;
  private Paint paintSegment, paintText, paintCenterCircle;
  private RectF selectedArcRect, unselectedArcRect;
  private ArrayList<PieChartData> data;
  private LockableScrollView lockableScrollView;
  private SelectedSegmentListener selectedSegmentListener;
  private DataListener dataListener;
  
  public PieChartView(Context context) {
    super(context);
    this.context = context;
    start();
  }
  
  public PieChartView(Context context, AttributeSet attrs) {
    super(context, attrs);
    this.context = context;
    this.typedArray = context.obtainStyledAttributes(attrs, R.styleable.PieChartView);
    start();
  }
  
  public PieChartView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    this.context = context;
    this.typedArray = context.obtainStyledAttributes(attrs, R.styleable.PieChartView, defStyleAttr, 0);
    start();
  }
  
  private void editModeDisplay() {
    ArrayList<PieChartData> data = new ArrayList<>();
    data.add(new PieChartData(500, "Title1"));
    data.add(new PieChartData(400, "Title2"));
    data.add(new PieChartData(300, "Title3"));
    data.add(new PieChartData(200, "Title4"));
    data.add(new PieChartData(100, "Title5"));
    
    this.setData(data);
  }
  
  @SuppressLint("ClickableViewAccessibility")
  private void start() {
    if (isInEditMode()) {
      editModeDisplay();
    }
    setAttrs();
  
    animatedApartDistance = apartDistance;
    
    paintSegment = new Paint(Paint.ANTI_ALIAS_FLAG);
    paintSegment.setStyle(Paint.Style.FILL);
    paintSegment.setDither(true);
  
    paintCenterCircle = new Paint(Paint.ANTI_ALIAS_FLAG);
    paintCenterCircle.setStyle(Paint.Style.FILL);
    paintCenterCircle.setDither(true);
  
    paintText = new Paint(Paint.ANTI_ALIAS_FLAG);
    paintText.setDither(true);
    paintText.setTextAlign(Paint.Align.CENTER);
    
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
    setMeasuredDimension(measureWidth(widthMeasureSpec, heightMeasureSpec),
        measureHeight(widthMeasureSpec, heightMeasureSpec));
  }
  
  private int measureWidth(int widthMeasureSpec, int heightMeasureSpec) {
    int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
    int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
    int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
    int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
    
    if (widthSpecMode == MeasureSpec.EXACTLY) {
      return widthSpecSize;
    } else if (widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.EXACTLY) {
      if (widthSpecSize < heightSpecSize) {
        return widthSpecSize;
      } else {
        return heightSpecSize + getPaddingLeft() + getPaddingRight();
      }
    } else {
      return Math.min(widthSpecSize, heightSpecSize);
    }
  }
  
  private int measureHeight(int widthMeasureSpec, int heightMeasureSpec) {
    int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
    int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
    int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
    int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
  
    if (heightSpecMode == MeasureSpec.EXACTLY) {
      return heightSpecSize;
    } else if (heightSpecMode == MeasureSpec.AT_MOST && widthSpecMode == MeasureSpec.EXACTLY) {
      if (heightSpecSize < widthSpecSize) {
        return heightSpecSize;
      } else {
        return widthSpecSize + getPaddingTop() + getPaddingBottom();
      }
    } else {
      return Math.min(widthSpecSize, heightSpecSize);
    }
  }
  
  @Override
  protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);
    setMeasuredDimension(w, h);
    
    xCenter = (w / 2) + getPaddingLeft() - getPaddingRight();
    yCenter = (h / 2) + getPaddingTop() - getPaddingBottom();
  
    float topToCenter = yCenter - getPaddingTop();
    float rightToCenter = w - xCenter - getPaddingRight();
    float bottomToCenter = h - yCenter - getPaddingBottom();
    float leftToCenter = xCenter - getPaddingLeft();
    
    radius = Math.min(Math.min(topToCenter, rightToCenter), Math.min(bottomToCenter, leftToCenter));
  
    // Dimension of unselected circle segments' rect
    unselectedArcRect.left = xCenter - radius + apartDistance;
    unselectedArcRect.top = yCenter - radius + apartDistance;
    unselectedArcRect.right = xCenter + radius - apartDistance;
    unselectedArcRect.bottom = yCenter + radius - apartDistance;
    
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
          data.get(i).setColor(colorList[i]);
          startAngle += degree;
        }
      }
    }
  }
  
  @Override
  protected void onDraw(Canvas canvas) {
    if (data != null) {
      if (data.size() > 0) {
        paintText.setColor(percentageTextColor);
        paintText.setTextSize(percentageTextSize);
        
        paintCenterCircle.setColor(centerCircleColor);
        
        // Set the selected circle segment's color
        if (data.get(selectedSegment).getColor() == 0)
          paintSegment.setColor(colorList[selectedSegment]);
        else
          paintSegment.setColor(data.get(selectedSegment).getColor());
        
        // Draw selected circle segment
        double middleAng = data.get(selectedSegment).getMiddleAngleRadian();
        selectedArcRect.left = xCenter - radius + (apartDistance - animatedApartDistance) +
                                   (float) Math.cos(middleAng) * animatedApartDistance;
        selectedArcRect.right = xCenter + radius - (apartDistance - animatedApartDistance) +
                                    (float) Math.cos(middleAng) * animatedApartDistance;
        selectedArcRect.top = yCenter - radius + (apartDistance - animatedApartDistance) +
                                  (float) Math.sin(middleAng) * animatedApartDistance;
        selectedArcRect.bottom = yCenter + radius - (apartDistance - animatedApartDistance) +
                                     (float) Math.sin(middleAng) * animatedApartDistance;
  
        canvas.drawArc(selectedArcRect, data.get(selectedSegment).getStartAngle(),
            data.get(selectedSegment).getDrawingDegree(),
            true, paintSegment);
  
        paintSegment.setColor(centerCircleColor);
        
        // Trim selected circle segment
        canvas.drawCircle(xCenter + (float) Math.cos(middleAng) * animatedApartDistance,
            yCenter + (float) Math.sin(middleAng) * animatedApartDistance, radius / 2,
            paintCenterCircle);
        
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
        
        // Make a hole
        canvas.drawCircle(xCenter, yCenter, radius / 2, paintCenterCircle);
        
        // Draw percentage text
        canvas.drawText(Calc.round(data.get(selectedSegment).getPercentage(), 1) + "%",
            xCenter, yCenter + percentageTextSize / 3, paintText);
      }
    }
  }
  
  private void setAttrs() {
    if (typedArray != null) {
      centerCircleColor = typedArray.getColor(
          R.styleable.PieChartView_centerCircleColor, centerCircleColor);
      
      percentageTextColor = typedArray.getColor(
          R.styleable.PieChartView_percentageTextColor, percentageTextColor);
      
      float percentageTextSizeSp = typedArray.getDimension(
          R.styleable.PieChartView_percentageTextSize, percentageTextSize);
      percentageTextSize = Calc.spToPx(context, percentageTextSizeSp);
      
      float apartDistanceDp = typedArray.getDimension(R.styleable.PieChartView_apartDistance,
          Calc.pxToDp(context, apartDistance));
      apartDistance = Calc.dpToPx(context, apartDistanceDp);
      
      startAngle = typedArray.getInt(R.styleable.PieChartView_startAngle, (int) startAngle);
      
      startAngle = startAngle % 360;
  
      animationDuration = typedArray.getInt(
          R.styleable.PieChartView_animationDuration, animationDuration);
      
      typedArray.recycle();
    }
  }
  
  private int generateRandomColor() {
    Random random = new Random();
    int red = random.nextInt(255);
    int green = random.nextInt(255);
    int blue = random.nextInt(255);
    int color = Color.rgb(red, green, blue);
    
    if (color != centerCircleColor) {
      return color;
    }else
      // If generated color is the same with the centerCircleColor's, generate new color.
      return generateRandomColor();
  }
  
  
  public ArrayList<PieChartData> getData() {
    return data;
  }
  
  public PieChartView setData(ArrayList<PieChartData> data) {
    this.data = data;
    if (dataListener != null)
      dataListener.onChange(data);
  
    // If data list size bigger than color list size, add random color to color list.
    if (data.size() > colorList.length) {
      int difference = data.size() - colorList.length;
      ArrayList<Integer> colorAL = new ArrayList<>();
      
      // Take all color from colorList to a new ArrayList.
      for (int color : colorList) colorAL.add(color);
      
      // Generate new random colors and add them to new ArrayList.
      for (int i = 0; i < difference; i++) colorAL.add(generateRandomColor());
  
      // Take all colors from new ArrayList to new colorList
      colorList = new int[colorAL.size()];
      for (int i = 0; i < colorAL.size(); i++) colorList[i] = colorAL.get(i);
    }
    
    invalidate();
    return this;
  }
  
  public int[] getColorList() {
    return colorList;
  }
  
  public PieChartView setColorList(int[] colorList) {
    this.colorList = colorList;
    invalidate();
    return this;
  }
  
  public int getPercentageTextSize() {
    return percentageTextSize;
  }
  
  public PieChartView setPercentageTextSize(int textSize) {
    this.percentageTextSize = textSize;
    invalidate();
    return this;
  }
  
  public int getPercentageTextColor() {
    return percentageTextColor;
  }
  
  public PieChartView setPercentageTextColor(int textColor) {
    this.percentageTextColor = textColor;
    invalidate();
    return this;
  }
  
  public PieChartView setCenterCircleColor(int centerCircleColor) {
    this.centerCircleColor = centerCircleColor;
    invalidate();
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
    // Set selected segment
    this.selectedSegment = selectedSegment;
    
    // Set animation of selected segment
    ValueAnimator apartDistanceAnimator = ValueAnimator.ofFloat(0, apartDistance);
    apartDistanceAnimator.setDuration(animationDuration);
    apartDistanceAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
      @Override
      public void onAnimationUpdate(ValueAnimator animation) {
        animatedApartDistance = (float) animation.getAnimatedValue();
        invalidate();
      }
    });
    
    // Animate chart segments
    if (oldSelectedSegment != selectedSegment) {
      apartDistanceAnimator.start();
      oldSelectedSegment = selectedSegment;
    }
    
    // Set selected segment listener
    if (selectedSegmentListener != null)
      selectedSegmentListener.onChange(selectedSegment);
    return this;
  }
  
  public PieChartView setLockableScrollView(LockableScrollView lockableScrollView) {
    this.lockableScrollView = lockableScrollView;
    return this;
  }
  
  public interface SelectedSegmentListener {
    void onChange(int position);
  }
  
  public SelectedSegmentListener getSelectedSegmentListener() {
    return selectedSegmentListener;
  }
  
  public void setSelectedSegmentListener(SelectedSegmentListener listener) {
    this.selectedSegmentListener = listener;
  }
  
  public interface DataListener{
    void onChange(ArrayList<PieChartData> pieChartData);
  }
  
  public void setDataListener(DataListener dataListener) {
    this.dataListener = dataListener;
  }
  
  public DataListener getDataListener() {
    return dataListener;
  }
  
  public int getApartDistance() {
    return apartDistance;
  }
  
  public PieChartView setApartDistance(int apartDistance) {
    this.apartDistance = apartDistance;
    invalidate();
    return this;
  }
  
  public float getAnimationDuration () {
    return animationDuration;
  }
  
  public PieChartView setAnimationDuration(int milliseconds) {
    animationDuration = milliseconds;
    invalidate();
    return this;
  }
  
  public int getCenterCircleColor() {
    return centerCircleColor;
  }
  
  public float getStartAngle() {
    return startAngle;
  }
  
  public Paint getPaintSegment() {
    return paintSegment;
  }
  
  public Paint getPaintText() {
    return paintText;
  }
  
  public Paint getPaintCenterCircle() {
    return paintCenterCircle;
  }
  
  public RectF getSelectedArcRect() {
    return selectedArcRect;
  }
  
  public RectF getUnselectedArcRect() {
    return unselectedArcRect;
  }
}