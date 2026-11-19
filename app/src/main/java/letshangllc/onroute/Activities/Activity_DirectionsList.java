package letshangllc.onroute.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

import letshangllc.onroute.Direction;
import letshangllc.onroute.DirectionsAdapter;
import letshangllc.onroute.R;

public class Activity_DirectionsList extends AppCompatActivity {
    private ArrayList<Direction> directions;
    private DirectionsAdapter directionsAdapter;
    private ListView lv_directions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        setContentView(R.layout.activity_directions_list);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        createDirectionList();

        if (directions == null || directions.isEmpty()) {
            findViewById(R.id.tv_noDirections).setVisibility(View.VISIBLE);
        }
    }

    private void createDirectionList() {
        Intent receivedIntent = getIntent();
        directions = receivedIntent.getParcelableArrayListExtra(getString(R.string.DirectionsIntent));
        if (directions == null) {
            directions = new ArrayList<>();
        }

        directionsAdapter = new DirectionsAdapter(this, directions);
        lv_directions = findViewById(R.id.lv_directions);
        lv_directions.setAdapter(directionsAdapter);
    }
}
