package letshangllc.onroute;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Carl on 12/22/2015.
 */
public class DirectionsAdapter extends ArrayAdapter<Direction> {
    private ArrayList<Direction> directions;

    public DirectionsAdapter(Context context, ArrayList<Direction> directions){
        super(context, R.layout.item_direction, directions);
    }

    private static class ViewHolder {
        TextView tv_direction;
        TextView tv_length;
    }

    /*todo Set the direction of top position to bigger txt Size */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Direction direction = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_direction, parent, false);
            viewHolder.tv_direction = (TextView) convertView.findViewById(R.id.tv_direction);
            viewHolder.tv_length = (TextView) convertView.findViewById(R.id.tv_distance);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // Populate the data into the template view using the data object
        viewHolder.tv_direction.setText(direction.getDirection());
        viewHolder.tv_length.setText("" +direction.getLength());
        // Return the completed view to render on screen
        return convertView;
    }


}
