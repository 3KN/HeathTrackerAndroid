package tracker.health.buzzapps.ua.heathtracker.Fragment;


import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.BounceInterpolator;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.charts.SeriesItem;
import com.hookedonplay.decoviewlib.events.DecoEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import tracker.health.buzzapps.ua.heathtracker.MainActivity;
import tracker.health.buzzapps.ua.heathtracker.Model.User;
import tracker.health.buzzapps.ua.heathtracker.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class PulseFragment extends Fragment implements SeriesItem.SeriesItemListener, CompoundButton.OnCheckedChangeListener {

    private static final String DATA = "data";
    private static final String PULSE = "pulse";
    private static final String PULSE_VALUE = "pulseValue";
    private static final String PULSE_HISTORY = "pulseHistory";
    DecoView arcView;
    TextView pulseText;
    ImageView pulseImage;
    Animation pulse;
    DatabaseReference mDatabase;
    User currentUser;
    int series1Index;

    LineChart chart;
    ArrayList<Long> pulseData;
    Switch aSwitch;
    boolean chartIsInit;
    public long previous = 0;
    private boolean cancleBool = false;

    public PulseFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view =  inflater.inflate(R.layout.fragment_pulse, container, false);
        arcView = (DecoView)view.findViewById(R.id.dynamicArcView);
        pulseText = (TextView)view.findViewById(R.id.pulse_count);
        pulseImage = (ImageView)view.findViewById(R.id.pulse_Image);
        chart = (LineChart) view.findViewById(R.id.chart);
        aSwitch = (Switch)view.findViewById(R.id.switchPulse);
        aSwitch.setOnCheckedChangeListener(this);
        pulse = AnimationUtils.loadAnimation(getActivity(), R.anim.pulse);
        mDatabase = FirebaseDatabase.getInstance().getReference().child(DATA);
        currentUser = MainActivity.getCurrentUser();
        pulseData = new ArrayList<>();
        getPulseData();
        initDecoView();
        getPulse();
        //initChart();

        // Create background track



        return view;
    }

    private void getPulseData() {
        mDatabase.child(PULSE_HISTORY).child(currentUser.getUserid()).limitToLast(40).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                pulseData.clear();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    pulseData.add((long) postSnapshot.getValue());
                }
                initChart();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void initChart() {
        List<Entry> barEntries = new ArrayList<>();
        for (int i = 0; i < pulseData.size(); i++) {
            barEntries.add(new Entry(i,pulseData.get(i)));
        }

        LineDataSet dataSet = new LineDataSet(barEntries,"Pulse diagramm");
        dataSet.setColor(Color.parseColor("#3F51B5"));
        dataSet.setCircleColor(R.color.accent);
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(3f);
        LineData barData = new LineData(dataSet);
        chart.setData(barData);

        chart.getLegend().setEnabled(false);
        chart.getAxisRight().setEnabled(false);
        chart.getXAxis().setEnabled(false);
        chart.setDescription(null);
        chart.invalidate();



    }

    private void testPulse(Random random) {

        long x= random.nextInt(20)+60;
        currentUser = MainActivity.getCurrentUser();
        mDatabase.child(PULSE).child(currentUser.getUserid()).child(PULSE_VALUE).setValue(x);
        mDatabase.child(PULSE_HISTORY).child(currentUser.getUserid()).push().setValue(x);


    }

    private void getPulse() {
        mDatabase.child(PULSE).child(currentUser.getUserid()).child(PULSE_VALUE).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                arcView.addEvent(new DecoEvent.Builder((long)dataSnapshot.getValue()).setIndex(series1Index).setDelay(1000).build());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void initDecoView() {
        arcView.addSeries(new SeriesItem.Builder(Color.argb(255, 218, 218, 218))
                .setRange(0, 100, 100)
                .setInitialVisibility(false)
                .setLineWidth(40f)
                .build());

        //Create data series track
        final SeriesItem seriesItem1 = new SeriesItem.Builder(Color.parseColor("#3F51B5"))
                .setRange(60,90,60)
                .setLineWidth(40f)
                .setInterpolator(new AccelerateInterpolator())
                .build();
        seriesItem1.addArcSeriesItemListener(this);

        series1Index = arcView.addSeries(seriesItem1);

        arcView.addEvent(new DecoEvent.Builder(DecoEvent.EventType.EVENT_SHOW, true)
                .setDelay(1000)
                .setDuration(2000)
                .build());
       // arcView.addEvent(new DecoEvent.Builder(50).setIndex(series1Index).setDelay(2000).build());
        //arcView.addEvent(new DecoEvent.Builder(100).setIndex(series1Index).setDelay(8000).build());


    }



    @Override
    public void onSeriesItemAnimationProgress(float v, float v1) {
        int currentValue = (int) v1;
        pulseText.setText(String.valueOf(currentValue));
        if(!pulse.hasStarted() || pulse.hasEnded()) {
            pulseImage.startAnimation(pulse);
        }

    }

    @Override
    public void onSeriesItemDisplayProgress(float v) {

    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if(b){
            try{
                cancleBool = false;
                new MyAsynk().execute();
            }catch (Exception e){

            }
        }else{
            cancleBool = true;
        }
    }

    public class MyAsynk extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {

            Random random = new Random();
            for (int i = 0; i < 300; i++) {
                if(cancleBool){
                    return null;
                }
                testPulse(random);
                try {
                    Thread.sleep(3000);
                }catch (InterruptedException e){
                    return null;
                }

            }
            return null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
