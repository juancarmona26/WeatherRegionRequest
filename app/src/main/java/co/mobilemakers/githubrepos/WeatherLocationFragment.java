package co.mobilemakers.githubrepos;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class WeatherLocationFragment extends ListFragment {

    private static final String LOG_TAG = WeatherLocationFragment.class.getSimpleName();
    EditText mEditTextLocation;
    WeatherLocationAdapter mAdapter;


    public WeatherLocationFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_weather_location, container, false);
        wireUpView(rootView);
        prepareButton(rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        prepareListView();
    }

    private void prepareListView() {
        List<WeatherLocation> repos = new ArrayList<>();
        mAdapter = new WeatherLocationAdapter(getActivity(), repos);
        setListAdapter(mAdapter);
    }

    private void prepareButton(View rootView) {
        Button button_get_Repos = (Button) rootView.findViewById(R.id.button_get_location);
        button_get_Repos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String location = mEditTextLocation.getText().toString();
                fetchReposInQueue(location);
            }
        });
    }

    private void wireUpView(View rootView) {
        mEditTextLocation = (EditText) rootView.findViewById(R.id.edit_text_location);
    }

    private void fetchReposInQueue(String location) {
        try {
            URL url  = constructQuery(location);
            Request request  = new Request.Builder().url(url.toString()).build();
            OkHttpClient client = new OkHttpClient();
            client.newCall(request).enqueue(new com.squareup.okhttp.Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    String responseString = response.body().string();
                    final List<WeatherLocation> listOfLocations = parseResponse(responseString);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.clear();
                            mAdapter.addAll(listOfLocations);
                            mAdapter.notifyDataSetChanged();

                        }
                    });


                }
            });


        } catch (java.io.IOException e) {
            e.printStackTrace();
        }


    }
    private URL constructQuery(String location) throws MalformedURLException {
        final String API_WEATHER = "api.worldweatheronline.com";
        final String LOCATION_PATH = "free/v2";
        final String REPOS_ENDPOINT  = "search.ashx";
        final String KEY_PARAMETER = "key";
        final String KEY_VALUE = "26204372f38320e5b6c04305fd0ed";
        final String LOCATION_PARAMETER = "q";
        final String FORMAT_PARAMETER  = "format";
        final String JSON_FORMAT = "json";

        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https").authority(API_WEATHER).
                appendEncodedPath(LOCATION_PATH).
                appendPath(REPOS_ENDPOINT).appendQueryParameter(KEY_PARAMETER, KEY_VALUE).
                appendQueryParameter(LOCATION_PARAMETER, location).appendQueryParameter(FORMAT_PARAMETER, JSON_FORMAT);

        Uri uri = builder.build();
        Log.d(LOG_TAG, "Built URI: " + uri.toString());

        return new URL(uri.toString());
    }



    private List<WeatherLocation> parseResponse(String response) {
        final String SEARCH_API = "search_api";
        final String RESULT = "result";
        final String REGION = "region";
        final String WEATHER_URL = "weatherUrl";
        final String VALUE = "value";
        List<WeatherLocation> repos = new ArrayList<>();
        WeatherLocation repo;
        try {

            JSONObject searchApiObject = new JSONObject(response).getJSONObject(SEARCH_API);

            JSONArray responseJsonArray = searchApiObject.getJSONArray(RESULT);
            Log.d(LOG_TAG, responseJsonArray.toString());
            JSONArray objectToRegion;
            JSONArray objectToWeatherURL;
            for(int i = 0; i < responseJsonArray.length(); i++) {
                objectToRegion = responseJsonArray.getJSONObject(i).getJSONArray(REGION);
                objectToWeatherURL = responseJsonArray.getJSONObject(i).getJSONArray(WEATHER_URL);
                repo = new WeatherLocation();
                repo.setRegion(objectToRegion.getJSONObject(0).getString(VALUE));
                repo.setWeatherUrl(objectToWeatherURL.getJSONObject(0).getString(VALUE));
                repos.add(repo);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return repos;
    }
}
