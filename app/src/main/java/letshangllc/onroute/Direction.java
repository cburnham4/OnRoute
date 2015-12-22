package letshangllc.onroute;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by Carl on 12/21/2015.
 */
public class Direction implements Parcelable{
    private int routeNum;
    private String direction;
    private double length;

    protected Direction(Parcel in) {
        routeNum = in.readInt();
        direction = in.readString();
        length = in.readDouble();
    }

    public static final Creator<Direction> CREATOR = new Creator<Direction>() {
        @Override
        public Direction createFromParcel(Parcel in) {
            return new Direction(in);
        }

        @Override
        public Direction[] newArray(int size) {
            return new Direction[size];
        }
    };

    public int getRouteNum() {
        return routeNum;
    }

    public void setRouteNum(int routeNum) {
        this.routeNum = routeNum;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public Direction(int routeNum, String direction, double length) {

        this.routeNum = routeNum;
        this.direction = direction;
        this.length = length;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.routeNum);
        dest.writeString(this.direction);
        dest.writeDouble(this.length);
    }
}
