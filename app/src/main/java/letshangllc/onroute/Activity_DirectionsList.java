package letshangllc.onroute;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;

public class Activity_DirectionsList extends AppCompatActivity {
    private ArrayList<Direction> directions;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directions_list);

        Intent recievedIntent = getIntent();
        directions = recievedIntent.getParcelableArrayListExtra(getResources().getString(R.string.DirectionsIntent));


    }
}
