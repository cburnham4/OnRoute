package letshangllc.onroute;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;

public class Activity_DirectionsList extends AppCompatActivity {
    private ArrayList<Direction> directions;
    private DirectionsAdapter directionsAdapter;
    private ListView lv_directions;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directions_list);

        this.CreateDirectionList();
    }


    private void CreateDirectionList(){
        /* Get the data passed in by caller activity */
        Intent recievedIntent = getIntent();
        directions = recievedIntent.getParcelableArrayListExtra(getResources().getString(R.string.DirectionsIntent));

        /* Use the data in the adapter */
        directionsAdapter = new DirectionsAdapter(this, directions);

        /* Set the listview's adapter */
        lv_directions = (ListView) findViewById(R.id.lv_directions);
        lv_directions.setAdapter(directionsAdapter);
    }
}
