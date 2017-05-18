package ensharp.decibelcheck;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Semin on 2017-02-21.
 */
public class WeeklyInfoActivity extends AppCompatActivity implements WeeklyRecyclerAdapter.OnItemClickListener{

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private WeeklyRecyclerAdapter mWeeklyRecylerAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weeklyinfo);
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.weeklytoolbar);
        setSupportActionBar(toolbar);
        TextView weeklyInfoTitleTxt = (TextView) findViewById(R.id.weeklyInfoTitleTxt);
        weeklyInfoTitleTxt.setText("주간 분석");
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mRecyclerView = (RecyclerView) findViewById(R.id.weeklyRecyclerView);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mWeeklyRecylerAdapter = new WeeklyRecyclerAdapter(getDataset());
        mWeeklyRecylerAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mWeeklyRecylerAdapter);

    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(getBaseContext(), WeeklyAnalysisActivity.class);
        intent.putExtra("week",mWeeklyRecylerAdapter.getItem(position).getDetailWeek());
        intent.putExtra("year", mWeeklyRecylerAdapter.getItem(position).getYear());
        startActivity(intent);

//        startActivityForResult(intent, REQUEST_CODE_SPOTINFO);
//        Toast.makeText(this, "다른액티비티로 "+adapter.getItem(position).getSpotName(), Toast.LENGTH_SHORT).show();
    }

    private ArrayList<WeeklyData> getDataset() {

        ArrayList<WeeklyData> dataset = new ArrayList<>();
        dataset.add(new WeeklyData("2017", "W8", "2월 19일~25일"));
        dataset.add(new WeeklyData("2017", "W7", "2월 12일~18일"));
        dataset.add(new WeeklyData("2017", "W6", "2월 5일~11일"));
        dataset.add(new WeeklyData("2017", "W5", "1월 29일~2월 4일"));
        dataset.add(new WeeklyData("2017", "W4", "1월 22일~28일"));
        dataset.add(new WeeklyData("2017", "W3", "1월 15일~21일"));
        dataset.add(new WeeklyData("2017", "W2", "1월 8일~14일"));
        dataset.add(new WeeklyData("2017", "W1", "1월 1일~7일"));
        dataset.add(new WeeklyData("2016", "W52", "12월 25일~31일"));
        dataset.add(new WeeklyData("2016", "W51", "12월 18일~24일"));
        dataset.add(new WeeklyData("2016", "W50", "12월 11일~17일"));
        dataset.add(new WeeklyData("2016", "W49", "12월 4일~10일"));
        dataset.add(new WeeklyData("2016", "W48", "11월 27일~12월 3일"));
        dataset.add(new WeeklyData("2016", "W47", "11월 20일~26일"));
        dataset.add(new WeeklyData("2016", "W46", "11월 13일~19일"));
        return dataset;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }
}
