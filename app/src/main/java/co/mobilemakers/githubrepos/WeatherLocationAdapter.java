package co.mobilemakers.githubrepos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Juan on 10/02/2015.
 */
public class WeatherLocationAdapter extends ArrayAdapter<WeatherLocation> {


    List<WeatherLocation> mWeatherLocations;

    public class ViewHolder {
        public final TextView textViewLocation;
        public final TextView textViewWeatherUrl;

        public ViewHolder (View view) {
            textViewLocation = (TextView) view.findViewById(R.id.text_view_location);
            textViewWeatherUrl = (TextView) view.findViewById(R.id.text_view_weather_url);
        }
    }
    public WeatherLocationAdapter(Context context, List<WeatherLocation> weatherLocations) {
        super(context, R.layout.list_item_weather, weatherLocations);
        mWeatherLocations = weatherLocations;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = reuseOrGnearateView(convertView, parent);
        displayWeatherInfoInRow(position, rowView);
        return rowView;
    }

    private void displayWeatherInfoInRow(int position, View rowView) {
        ViewHolder viewHolder = (ViewHolder) rowView.getTag();
        viewHolder.textViewLocation.setText(mWeatherLocations.get(position).getRegion());
        viewHolder.textViewWeatherUrl.setText(mWeatherLocations.get(position).getWeatherUrl());
    }

    private View reuseOrGnearateView(View convertView, ViewGroup parent) {
        View rowView;
        if(convertView != null) {
            rowView = convertView;
        } else {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.list_item_weather, parent, false);
            ViewHolder viewHolder = new ViewHolder(rowView);
            rowView.setTag(viewHolder);
        }
        return rowView;
    }
}
