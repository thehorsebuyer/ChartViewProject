package volkanatalan.chartviewproject;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

import volkanatalan.chartviewproject.fragments.PieChart;

public class MainActivity extends AppCompatActivity {
  ListView listView;
  FrameLayout fragmentContainer;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
  
    listView = findViewById(R.id.listView);
  
    String[] charts = {"Pie Chart"};
    ListAdapter listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, charts);
    listView.setAdapter(listAdapter);
    fragmentContainer = findViewById(R.id.fragmentContainer);
    
    listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
      @Override
      public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        String chart = String.valueOf(adapterView.getItemAtPosition(i));
  
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (chart.equals("Pie Chart")) {
          Fragment pieChartFragment = new PieChart();
          fragmentTransaction.add(R.id.fragmentContainer, pieChartFragment);
          fragmentTransaction.addToBackStack("pie_chart");
          fragmentTransaction.commit();
        }
      }
    });
  }
}
