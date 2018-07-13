package volkanatalan.chartview.data_views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import volkanatalan.chartview.Calc;
import volkanatalan.chartview.R;
import volkanatalan.chartview.charts.PieChartView;
import volkanatalan.chartview.datas.PieChartData;

import java.util.ArrayList;

public class PieChartDataView extends RelativeLayout {
  private Context context;
  private TypedArray typedArray;
  private int widthMeasureMode;
  private LinearLayout mainContainer;
  private RelativeLayout selector;
  private ArrayList<PieChartData> pieChartValues;
  private ArrayList<LinearLayout> containerLayoutList = new ArrayList<>();
  private PieChartView pieChartView;
  private OnContainerLayoutDimensionChanged onContainerLayoutDimensionChanged;
  
  private int textColor = Color.BLACK;
  private int textSize = 14;
  private int selectorColor = getContext().getResources().getColor(R.color.selector_color);
  private int colorBoxDimension = 10;
  private int colorBoxMarginEnd = 20;
  private int selectedSegment = 0;
  private int selectorOverage = 0;
  private int distanceBetweenColorBoxAndText = 10;
  private int[] colorList = getContext().getResources().getIntArray(R.array.pie_chart_color_list);
  private ColorBoxShape colorBoxShape = ColorBoxShape.CIRCLE;
  private ColorBoxPosition colorBoxPosition = ColorBoxPosition.LEFT;
  
  public enum ColorBoxShape {
    SQUARE(0), CIRCLE(1), TRIANGLE_UP(2), TRIANGLE_RIGHT(3), TRIANGLE_DOWN(4), TRIANGLE_LEFT(5);
    int id;
  
    ColorBoxShape(int id) {
      this.id = id;
    }
  
    static ColorBoxShape fromId(int id) {
      for (ColorBoxShape c : ColorBoxShape.values()) {
        if (c.id == id) return c;
      }
      throw new IllegalArgumentException();
    }
  }
  
  public enum ColorBoxPosition {
    LEFT(0), RIGHT(1);
    int id;
  
    ColorBoxPosition(int id) {
      this.id = id;
    }
  
    static ColorBoxPosition fromId(int id) {
      for (ColorBoxPosition c : ColorBoxPosition.values()) {
        if (c.id == id) return c;
      }
      throw new IllegalArgumentException();
    }
  }
  
  
  public PieChartDataView(Context context) {
    super(context);
    this.context = context;
    init();
    if (isInEditMode()) {
      editModeDisplay();
    }
  }
  
  public PieChartDataView(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    this.context = context;
    this.typedArray = context.obtainStyledAttributes(attrs, R.styleable.PieChartDataView);
    init();
    if (isInEditMode()) {
      editModeDisplay();
    }
  }
  
  public PieChartDataView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    this.context = context;
    this.typedArray = context.obtainStyledAttributes(attrs, R.styleable.PieChartDataView, defStyleAttr, 0);
    init();
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
  
  private void init() {
  
    getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
      @Override
      public void onGlobalLayout() {
        // Fetch data
        if (pieChartValues == null && pieChartView != null) {
          pieChartValues = pieChartView.getData();
          
          start();
          draw();
          
          getViewTreeObserver().removeOnGlobalLayoutListener(this);
        }
      }
    });
  }
  
  @SuppressLint("ClickableViewAccessibility")
  private void start() {
    setAttrs();
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
    
    setOnContainerLayoutDimensionChanged(new OnContainerLayoutDimensionChanged() {
      @Override
      public void onChanged(int top, int bottom) {
        calibrateSelector(top, bottom);
        selector.requestLayout();
      }
    });
    
    setOnTouchListener(onTouchListener);
    
    getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
      @Override
      public void onGlobalLayout() {
        // Set layout parameters of PieChartDataView. If width is wrap_content, wrap mainContainer.
        // Without this PieChartDataView wraps selector.
        if (containerLayoutList.size() == pieChartValues.size()) {
          if (containerLayoutList.get(0).getTop() > 0 || containerLayoutList.get(0).getBottom() > 0) {
      
            onContainerLayoutDimensionChanged.onChanged(containerLayoutList.get(0).getTop(),
                containerLayoutList.get(0).getBottom());
      
            if (widthMeasureMode == MeasureSpec.AT_MOST) {
              setLayoutParams(new LinearLayout.LayoutParams(
                  mainContainer.getWidth() + getPaddingRight() + getPaddingLeft(), getHeight()));
            }
            
            getViewTreeObserver().removeOnGlobalLayoutListener(this);
          }
        }
    
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
        
          } else if (colorBoxShape == ColorBoxShape.SQUARE) {
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
      containerLayout.setPadding(
          containerLayout.getPaddingLeft() + selectorOverage,
          containerLayout.getPaddingTop(),
          containerLayout.getPaddingRight() + selectorOverage,
          containerLayout.getPaddingBottom());
  
      titleTV.setTextColor(textColor);
      titleTV.setTextSize(textSize);
      titleTV.setText(pieChartValues.get(i).getTitle() + " (" + pieChartValues.get(i).getValue() + ")");
  
      if (colorBoxPosition == ColorBoxPosition.LEFT) {
        titleTV.setPadding(titleTV.getPaddingLeft() + distanceBetweenColorBoxAndText, titleTV.getPaddingTop(),
            titleTV.getPaddingLeft(), titleTV.getPaddingBottom());
        containerLayout.addView(colorBox);
        containerLayout.addView(titleTV);
      } else if (colorBoxPosition == ColorBoxPosition.RIGHT) {
        titleTV.setPadding(titleTV.getPaddingLeft(), titleTV.getPaddingTop(),
            titleTV.getPaddingLeft() + distanceBetweenColorBoxAndText, titleTV.getPaddingBottom());
        containerLayout.addView(titleTV);
        containerLayout.addView(colorBox);
      }
  
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
  
  }
  
  private void calibrateSelector(int top, int bottom) {
    selector.setLayoutParams(new LayoutParams(
        LayoutParams.MATCH_PARENT, 0));
    selector.setTop(top + getPaddingTop());
    selector.setBottom(bottom + getPaddingTop());
    selector.setBackgroundColor(selectorColor);
  }
  
  public PieChartView getPieChartView() {
    return pieChartView;
  }
  
  public PieChartDataView bindTo(PieChartView pieChartView) {
    this.pieChartView = pieChartView;
    
    this.pieChartView.setDataListener(new PieChartView.DataListener() {
      @Override
      public void onChange(ArrayList<PieChartData> pieChartData) {
        setData(pieChartData);
        draw();
      }
    });
    
    this.pieChartView.setSelectedSegmentListener(new PieChartView.SelectedSegmentListener() {
      @Override
      public void onChange(int position) {
        selectedSegment = position;
        selector.setTop(containerLayoutList.get(position).getTop() + getPaddingTop());
        selector.setBottom(containerLayoutList.get(position).getBottom() + getPaddingTop());
      }
    });
    return this;
  }
  
  private void setAttrs() {
    textColor = typedArray.getColor(R.styleable.PieChartDataView_textColor, textColor);
    
    float textSizeSp = typedArray.getDimension(R.styleable.PieChartDataView_textSize, textSize);
    
    textSize = Calc.spToPx(context, textSizeSp);
    
    selectorColor = typedArray.getColor(R.styleable.PieChartDataView_selectorColor, selectorColor);
    
    float selectorOverageDp = typedArray.getDimension(
        R.styleable.PieChartDataView_selectorOverage, selectorOverage);
    
    selectorOverage = Calc.dpToPx(context, selectorOverageDp);
    
    float colorBoxDimensionDp = typedArray.getDimension(
        R.styleable.PieChartDataView_colorBoxDimension, colorBoxDimension);
    
    colorBoxDimension = Calc.dpToPx(context, colorBoxDimensionDp);
    
    float distanceBetweenColorBoxAndTextDp = typedArray.getDimension(
        R.styleable.PieChartDataView_distanceBetweenColorBoxAndText, distanceBetweenColorBoxAndText);
  
    distanceBetweenColorBoxAndText = Calc.dpToPx(context, distanceBetweenColorBoxAndText);
    
    colorBoxShape = ColorBoxShape.fromId(
        typedArray.getInt(R.styleable.PieChartDataView_colorBoxShape, 1));
    
    colorBoxPosition = ColorBoxPosition.fromId(
        typedArray.getInt(R.styleable.PieChartDataView_colorBoxPosition, 0));
    
    typedArray.recycle();
  }
  
  private interface OnContainerLayoutDimensionChanged {
    void onChanged(int top, int bottom);
  }
  
  private void setOnContainerLayoutDimensionChanged(OnContainerLayoutDimensionChanged onChanged) {
    this.onContainerLayoutDimensionChanged = onChanged;
  }
  
  private void setContainerLayoutDimension(int top, int bottom) {
    this.onContainerLayoutDimensionChanged.onChanged(top, bottom);
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
  
  public ColorBoxShape getColorBoxShape() {
    return colorBoxShape;
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
  
  public int getSelectorOverage() {
    return selectorOverage;
  }
  
  public PieChartDataView setSelectorOverage(int px) {
    selectorOverage = px;
    return this;
  }
  
  public int getDistanceBetweenColorBoxAndText() {
    return distanceBetweenColorBoxAndText;
  }
  
  public PieChartDataView setDistanceBetweenColorBoxAndText(int px) {
    distanceBetweenColorBoxAndText = px;
    return this;
  }
  
  public ColorBoxPosition getColorBoxPosition() {
    return colorBoxPosition;
  }
  
  public PieChartDataView setColorBoxPosition(ColorBoxPosition colorBoxPosition) {
    this.colorBoxPosition = colorBoxPosition;
    return this;
  }
  
  public int getSelectedSegment() {
    return selectedSegment;
  }
  
  public PieChartDataView setSelectedSegment(int selectedSegment) {
    this.selectedSegment = selectedSegment;
    return this;
  }
}