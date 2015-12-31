package letshangllc.onroute;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
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
        ImageView img_direction;
    }

    /* todo set small miles to ft */
    /* todo add in image */
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
            viewHolder.img_direction = (ImageView) convertView.findViewById(R.id.img_direction);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // Populate the data into the template view using the data object
        viewHolder.tv_direction.setText(direction.getDirection());

        viewHolder.tv_length.setText(String.format("%.2f",direction.getLength()) + " mi  ");
        if(position==0){
            viewHolder.tv_direction.setTextSize(24);

        }
        String directionString = direction.getDirection();

        /* todo move to string folder */
        if(directionString.contains("Turn left")){
            viewHolder.img_direction.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_turn_left));
        }else if(directionString.contains("Turn right")){
            viewHolder.img_direction.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_turn_right));
        }else if (directionString.contains("You arrived")){
            viewHolder.img_direction.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_current_location));
        }else if (directionString.contains("Take exit") || directionString.contains("Keep right")){
            viewHolder.img_direction.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_ne_arrow));
        }else if (directionString.contains("Keep left")){
            viewHolder.img_direction.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_nw_arrow));
        }else if (directionString.contains("Merge")){
            viewHolder.img_direction.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_call_merge_black_48dp));
        }else{
            viewHolder.img_direction.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_go_straight));
        }

        // Return the completed view to render on screen
        return convertView;
    }


}
