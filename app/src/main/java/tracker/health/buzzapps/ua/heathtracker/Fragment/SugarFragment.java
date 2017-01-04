package tracker.health.buzzapps.ua.heathtracker.Fragment;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import tracker.health.buzzapps.ua.heathtracker.MainActivity;
import tracker.health.buzzapps.ua.heathtracker.Model.User;
import tracker.health.buzzapps.ua.heathtracker.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SugarFragment extends Fragment {


    private static final String DATA = "data";
    private static final String SUGAR = "sugar";
    private static final String SUGAR_VALUE = "sugarValue";
    BarChart barChart;
    float sugarValue;
    DatabaseReference mDatabase;
    User currentUser;
    public SugarFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_sugar, container, false);
        barChart = (BarChart)view.findViewById(R.id.barchart);
        mDatabase = FirebaseDatabase.getInstance().getReference().child(DATA);
        currentUser = MainActivity.getCurrentUser();
        getSugarValue();

        return view;
    }

    private void getSugarValue() {
        mDatabase.child(SUGAR).child(currentUser.getUserid()).child(SUGAR_VALUE).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                sugarValue = Float.parseFloat(dataSnapshot.getValue().toString());
                initChart();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void initChart() {
        List<BarEntry> entriesGroup1 = new ArrayList<>();
        List<BarEntry> entriesGroup2 = new ArrayList<>();


        entriesGroup1.add(new BarEntry(0,sugarValue));
        entriesGroup2.add(new BarEntry(0, 10));

        BarDataSet set1 = new BarDataSet(entriesGroup1, "Your Value");
        BarDataSet set2 = new BarDataSet(entriesGroup2, "Normal Value");
        set1.setColor(Color.parseColor("#3F51B5"));
        set2.setColor(Color.parseColor("#FF66B2"));


        float groupSpace = 0.03f;
        float barSpace = 0.02f; // x2 dataset
        float barWidth = 0.45f;
        BarData data = new BarData(set1, set2);
        data.setBarWidth(barWidth);

       // barChart.getLegend().setEnabled(false);
        barChart.getAxisRight().setEnabled(false);
        barChart.getXAxis().setEnabled(false);
        barChart.setDescription(null);
        barChart.setData(data);
        barChart.groupBars(-0.4f, groupSpace, barSpace);
        barChart.invalidate(); // refresh

    }

}
