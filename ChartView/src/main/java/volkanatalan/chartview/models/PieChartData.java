package volkanatalan.chartview.models;

public class PieChartData {
  private String title;
  private int color;
  private double value;
  private float drawingDegree;
  private double degree;
  private float startAngle;
  private double startAngleRadian;
  private double middleAngleRadian;
  private double sweepAngleRadian;
  private double percentage;
  private double listPosTop;
  private double listPosBottom;
  
  public PieChartData(double value, String title) {
    this.value = value;
    this.title = title;
  }
  
  public PieChartData(double value, String title, int color) {
    this.value = value;
    this.title = title;
    this.color = color;
  }
  
  public double getValue() {
    return value;
  }
  
  public void setValue(double value) {
    this.value = value;
  }
  
  public String getTitle() {
    return title;
  }
  
  public void setTitle(String title) {
    this.title = title;
  }
  
  public int getColor() {
    return color;
  }
  
  public void setColor(int color) {
    this.color = color;
  }
  
  public double getDegree() {
    return degree;
  }
  
  public void setDegree(double degree) {
    this.degree = degree;
  }
  
  public float getStartAngle() {
    return startAngle;
  }
  
  public void setStartAngle(float startAngle) {
    this.startAngle = startAngle;
  }
  
  public double getSweepAngleRadian() {
    return sweepAngleRadian;
  }
  
  public void setSweepAngleRadian(double sweepAngleRadian) {
    this.sweepAngleRadian = sweepAngleRadian;
  }
  
  public double getMiddleAngleRadian() {
    return middleAngleRadian;
  }
  
  public void setMiddleAngleRadian(double middleAngleRadian) {
    this.middleAngleRadian = middleAngleRadian;
  }
  
  public float getDrawingDegree() {
    return drawingDegree;
  }
  
  public void setDrawingDegree(float drawingDegree) {
    this.drawingDegree = drawingDegree;
  }
  
  public double getPercentage() {
    return percentage;
  }
  
  public void setPercentage(double percentage) {
    this.percentage = percentage;
  }
  
  public double getStartAngleRadian() {
    return startAngleRadian;
  }
  
  public void setStartAngleRadian(double startAngleRadian) {
    this.startAngleRadian = startAngleRadian;
  }
  
  public double getListPosTop() {
    return listPosTop;
  }
  
  public void setListPosTop(double listPosTop) {
    this.listPosTop = listPosTop;
  }
  
  public double getListPosBottom() {
    return listPosBottom;
  }
  
  public void setListPosBottom(double listPosBottom) {
    this.listPosBottom = listPosBottom;
  }
}
