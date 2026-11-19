package letshangllc.onroute;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Locale;

/**
 * List adapter for turn-by-turn direction rows.
 */
public class DirectionsAdapter extends ArrayAdapter<Direction> {

    public DirectionsAdapter(Context context, ArrayList<Direction> directions) {
        super(context, R.layout.item_direction, directions);
    }

    private static class ViewHolder {
        TextView tv_direction;
        TextView tv_length;
        ImageView img_direction;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        Direction direction = getItem(position);
        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_direction, parent, false);
            viewHolder.tv_direction = convertView.findViewById(R.id.tv_direction);
            viewHolder.tv_length = convertView.findViewById(R.id.tv_distance);
            viewHolder.img_direction = convertView.findViewById(R.id.img_direction);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (direction == null) {
            return convertView;
        }

        String directionString = direction.getDirection();
        viewHolder.tv_direction.setText(directionString);
        viewHolder.tv_length.setText(
                String.format(Locale.US, "%.2f mi  ", direction.getLength())
        );
        if (position == 0) {
            viewHolder.tv_direction.setTextSize(24);
        }

        int drawableRes;
        if (directionString.contains("Turn left")) {
            drawableRes = R.drawable.ic_turn_left;
        } else if (directionString.contains("Turn right")) {
            drawableRes = R.drawable.ic_turn_right;
        } else if (directionString.contains("You arrived")) {
            drawableRes = R.drawable.ic_current_location;
        } else if (directionString.contains("exit") || directionString.contains("Keep right")) {
            drawableRes = R.drawable.ic_ne_arrow;
        } else if (directionString.contains("Keep left")) {
            drawableRes = R.drawable.ic_nw_arrow;
        } else if (directionString.contains("Merge")) {
            drawableRes = R.drawable.ic_call_merge_black_48dp;
        } else {
            drawableRes = R.drawable.ic_go_straight;
        }
        viewHolder.img_direction.setImageDrawable(
                ContextCompat.getDrawable(getContext(), drawableRes)
        );

        return convertView;
    }
}
