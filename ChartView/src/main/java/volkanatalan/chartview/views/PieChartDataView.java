package volkanatalan.chartview.views;

import android.animation.ValueAnimator;
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

import volkanatalan.chartview.models.Calc;
import volkanatalan.chartview.R;
import volkanatalan.chartview.views.charts.PieChartView;
import volkanatalan.chartview.models.PieChartData;

import java.util.ArrayList;

public class PieChartDataView extends RelativeLayout {
  private Context context;
  private TypedArray typedArray;
  private int widthMeasureMode;
  private LinearLayout mainContainer;
  private RelativeLayout selector;
  private int selectorOldTop, selectorOldBottom;
  private ArrayList<PieChartData> pieChartData;
  private ArrayList<LinearLayout> containerLayoutList = new ArrayList<>();
  private PieChartView pieChartView;
  
  private int textColor = Color.BLACK;
  private int textSize = 14;
  private int selectorColor = getContext().getResources().getColor(R.color.selector_color);
  private int colorBoxDimension = 10;
  private int colorBoxMarginEnd = 20;
  private int selectedSegment = 0;
  private int selectorOverage = 0;
  private int distanceBetweenColorBoxAndText = 10;
  private int animationDuration = 300;
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
  }
  
  public PieChartDataView(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    this.context = context;
    this.typedArray = context.obtainStyledAttributes(attrs, R.styleable.PieChartDataView);
    init();
  }
  
  public PieChartDataView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    this.context = context;
    this.typedArray = context.obtainStyledAttributes(attrs, R.styleable.PieChartDataView, defStyleAttr, 0);
    init();
  }
  
  private void editModeDisplay() {
    // Create PieChartData list to see the preview of PieChartDataView.
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
    if (isInEditMode()) {
      editModeDisplay();
      draw();
    } else {
      getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
          // Fetch the data from PieChartView.
          if (pieChartData == null && pieChartView != null) {
            pieChartData = pieChartView.getPieChartData();
            draw();
        
            getViewTreeObserver().removeOnGlobalLayoutListener(this);
          }
        }
      });
    }
  }
  
  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    widthMeasureMode = MeasureSpec.getMode(widthMeasureSpec);
  }
  
  @SuppressLint("SetTextI18n")
  public void draw() {
    // Set the attributes of PieChartDataView.
    setAttrs();
    
    selector = new RelativeLayout(context);
    mainContainer = new LinearLayout(context);
    
    LayoutParams mainContainerLP = new LayoutParams(
        LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    mainContainerLP.addRule(CENTER_HORIZONTAL);
    
    mainContainer.setLayoutParams(mainContainerLP);
    mainContainer.setOrientation(LinearLayout.VERTICAL);
  
    OnTouchListener onTouchListener = new OnTouchListener() {
      @SuppressLint("ClickableViewAccessibility")
      @Override
      public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
          case MotionEvent.ACTION_DOWN:
            
            // When PieChartDataView is touched, get the vertical coordinate of touch point.
            double coordY = motionEvent.getY();
            
            // When an item is selected, select it in PieChartView too.
            for (int i = 0; i < pieChartData.size(); i++) {
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
    
    // Add all items to PieChartDataView.
    for (int i = 0; i < pieChartData.size(); i++) {
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
          colorBoxPaint.setColor(pieChartData.get(pos).getColor());
      
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
      
      containerLayout.setLayoutParams(new LayoutParams(
          LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
      containerLayout.setOrientation(LinearLayout.HORIZONTAL);
      containerLayout.setGravity(Gravity.CENTER_VERTICAL);
      containerLayout.setPadding(
          containerLayout.getPaddingLeft() + selectorOverage,
          containerLayout.getPaddingTop(),
          containerLayout.getPaddingRight() + selectorOverage,
          containerLayout.getPaddingBottom());
  
      titleTV.setLayoutParams(new LayoutParams(
          LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
      titleTV.setTextColor(textColor);
      titleTV.setTextSize(textSize);
      titleTV.setText(pieChartData.get(i).getTitle() + " (" + pieChartData.get(i).getValue() + ")");
  
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
    }

    this.addView(selector);
    this.addView(mainContainer);
  
    // Make a selector.
    getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
      @Override
      public void onGlobalLayout() {
        if (mainContainer.getWidth() > 0) {
          if (widthMeasureMode == MeasureSpec.AT_MOST) {
            setLayoutParams(new LinearLayout.LayoutParams(
                mainContainer.getWidth() + getPaddingRight() + getPaddingLeft(), getHeight()));
          }
  
          calibrateSelector(containerLayoutList.get(0).getTop(),
              containerLayoutList.get(0).getBottom());
          selector.requestLayout();
          
          getViewTreeObserver().removeOnGlobalLayoutListener(this);
        }
      }
    });
  }
  
  private void calibrateSelector(int top, int bottom) {
    int selectorTop = top + getPaddingTop();
    int selectorBottom = bottom + getPaddingTop();
    selector.setLayoutParams(new LayoutParams(
        LayoutParams.MATCH_PARENT, selectorBottom - selectorTop));
    selector.setTop(selectorTop);
    selector.setBottom(selectorBottom);
    selectorOldTop = selectorTop;
    selectorOldBottom = selectorBottom;
    selector.setBackgroundColor(selectorColor);
  
    // selectorOldTop and selectorOldBottom are required for the animation.
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
        R.styleable.PieChartDataView_distanceBetweenColorBoxAndText,
        Calc.pxToDp(context, distanceBetweenColorBoxAndText));
    
    distanceBetweenColorBoxAndText = Calc.dpToPx(context, distanceBetweenColorBoxAndTextDp);
    
    colorBoxShape = ColorBoxShape.fromId(
        typedArray.getInt(R.styleable.PieChartDataView_colorBoxShape, 1));
    
    colorBoxPosition = ColorBoxPosition.fromId(
        typedArray.getInt(R.styleable.PieChartDataView_colorBoxPosition, 0));
    
    animationDuration = typedArray.getInt(
        R.styleable.PieChartView_animationDuration, animationDuration);
    
    typedArray.recycle();
  }
  
  public PieChartDataView bindTo(PieChartView pieChartView) {
    this.pieChartView = pieChartView;
    
    // When the data of PieChartView is changed, take the new data.
    this.pieChartView.setDataListener(new PieChartView.DataListener() {
      @Override
      public void onChange(ArrayList<PieChartData> pieChartData) {
        setData(pieChartData);
        draw();
      }
    });
    
    // When a segment of PieChartView is selected, inform that which one it is.
    this.pieChartView.setSelectedSegmentListener(new PieChartView.SelectedSegmentListener() {
      @Override
      public void onChange(int position) {
        selectedSegment = position;
        int slcTop = containerLayoutList.get(position).getTop() + getPaddingTop();
        int slcBottom = containerLayoutList.get(position).getBottom() + getPaddingTop();
  
        /**
         * The top and the bottom of the selector must be animated separately. Because the width of
         * every item can be different. If only the position of the selector is changed, then the
         * selector cannot wrap the items. It would be shorter all bigger than the items. In this
         * way selector gets smaller or gets bigger according to every individual item.
         */
        
        ValueAnimator topAnimator = ValueAnimator.ofInt(selectorOldTop, slcTop);
        topAnimator.setDuration(animationDuration);
        topAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
          @Override
          public void onAnimationUpdate(ValueAnimator valueAnimator) {
            selector.setTop((int) valueAnimator.getAnimatedValue());
            selectorOldTop = (int) valueAnimator.getAnimatedValue();
          }
        });
  
        ValueAnimator bottomAnimator = ValueAnimator.ofInt(selectorOldBottom, slcBottom);
        bottomAnimator.setDuration(animationDuration);
        bottomAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
          @Override
          public void onAnimationUpdate(ValueAnimator valueAnimator) {
            selector.setBottom((int) valueAnimator.getAnimatedValue());
            selectorOldBottom = (int) valueAnimator.getAnimatedValue();
          }
        });
        
        if (selectorOldTop != slcTop || selectorOldBottom != slcBottom) {
          topAnimator.start();
          bottomAnimator.start();
        }
      }
    });
    return this;
  }
  
  public PieChartDataView setData(ArrayList<PieChartData> pieChartValues) {
    this.pieChartData = pieChartValues;
    return this;
  }
  
  public PieChartView getPieChartView() {
    return pieChartView;
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
  
  public int getAnimationDuration() {
    return animationDuration;
  }
  
  public void setAnimationDuration(int animationDuration) {
    this.animationDuration = animationDuration;
  }
  
  public ArrayList<LinearLayout> getContainerLayoutList() {
    return containerLayoutList;
  }
}