package volkanatalan.chartviewproject.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.ArrayList;

import volkanatalan.chartview.charts.PieChartView;
import volkanatalan.chartview.data_views.PieChartDataView;
import volkanatalan.chartview.datas.PieChartData;
import volkanatalan.chartviewproject.R;

public class PieChart extends Fragment {
  PieChartView pieChartView;
  PieChartDataView pieChartDataView;
  ArrayList<PieChartData> pieChartData;
  
  public PieChart() {
    // Required empty public constructor
  }
  
  public static PieChart newInstance() {
    return new PieChart();
  }
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    pieChartData = new ArrayList<>();
    pieChartData.add(new PieChartData(815, "Item 1"));
    pieChartData.add(new PieChartData(516, "Item 2"));
    pieChartData.add(new PieChartData(249, "Item 3"));
    pieChartData.add(new PieChartData(241, "Item 4"));
    pieChartData.add(new PieChartData(168, "Item 5"));
  }
  
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View v = inflater.inflate(R.layout.fragment_pie_chart, container, false);
    FrameLayout root = v.findViewById(R.id.root);
    pieChartView = v.findViewById(R.id.pieChartView);
    pieChartDataView = v.findViewById(R.id.pieChartDataView);
  
    pieChartView.setData(pieChartData);
    
    pieChartDataView.setData(pieChartData)
        .bindTo(pieChartView)
        .draw();
    
    root.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
      }
    });
    return v;
  }
}
