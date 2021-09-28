package com.example.myapplication;

import android.graphics.Camera;
import android.graphics.Color;
import android.graphics.PointF;
import android.location.Address;
import android.location.Geocoder;
import android.nfc.tech.NfcA;
import android.os.AsyncTask;
import android.os.strictmode.NonSdkApiUsedViolation;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.geometry.LatLngBounds;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.InfoWindow;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.PolygonOverlay;
import com.naver.maps.map.overlay.PolylineOverlay;
import com.naver.maps.map.util.FusedLocationSource;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;

import javax.net.ssl.HttpsURLConnection;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {


    Spinner maptype_spinner;
    NaverMap mMap;
    PolygonOverlay mPolygon;
    ArrayList<Marker> mMarkerArrayLIst;
    ArrayList<LatLng> mLatLngList;
    EditText editText;


    public  NaverAddrApi naverAddrApi;
    Button btnTest;
    Button btnMove;
    Button btnsearch;
    Marker marker = new Marker();


    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private FusedLocationSource locationSource;
    private NaverMap naverMap;

    private URL url;
    private HttpsURLConnection http;
    public class addressMarker extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... strings) {


            String api ="https://naveropenapi.apigw.ntruss.com/map-geocode/v2/geocode?query="+strings[0];
            StringBuilder sb = new StringBuilder();
            try {
                url = new URL(api.toString());
                http = (HttpsURLConnection) url.openConnection();
                http.setRequestMethod("GET");
                http.setRequestProperty("Content-Type", "application/josn");
                http.setRequestProperty("X-NCP-APIGW-API-KEY-ID", "hruw818xqa");
                http.setRequestProperty("X-NCP-APIGW-API-KEY", "BxzEOvfUCUfMLnPUiggy1iXke77Q9pIgalmqLa8R");


                BufferedReader rd;
                if (http.getResponseCode() >= 200 && http.getResponseCode() <= 300) {
                    rd = new BufferedReader(new InputStreamReader(http.getInputStream()));
                } else {
                    rd = new BufferedReader(new InputStreamReader(http.getErrorStream()));
                }
                String line;
                while ((line = rd.readLine()) != null) {
                    sb.append(line);
                }
                rd.close();
                http.disconnect();
            } catch (Exception e) {
                return null;
            }
            return sb.toString();
        }

        protected void onPostExecute(String jsonStr) {
            JsonParser jsonParser = new JsonParser();
            double pnuLatitude;
            double pnuLongitude;
            JsonObject jsonObj = (JsonObject) jsonParser.parse(jsonStr);
            JsonArray jsonArray = (JsonArray) jsonObj.get("addresses");
            jsonObj = (JsonObject) jsonArray.get(0);
            pnuLatitude =jsonObj.get("x").getAsDouble();
            pnuLongitude =jsonObj.get("y").getAsDouble();

            Marker markers = new Marker();
            markers.setPosition(new LatLng(pnuLongitude, pnuLatitude));
            markers.setMap(naverMap);

        }

        private String makeStringNum(String number) {
            String strNum="";
            for (int i=0; i<4-number.length(); i++) {
                strNum = strNum + "0";
            }
            strNum=strNum+number;
            return strNum;
        }


    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        FragmentManager fm = getSupportFragmentManager();
        MapFragment mapFragment = (MapFragment) fm.findFragmentById(R.id.map);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            fm.beginTransaction().add(R.id.map, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);


        initViews();

        locationSource =
                new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);

    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,  @NonNull int[] grantResults) {
        if (locationSource.onRequestPermissionsResult(
                requestCode, permissions, grantResults)) {
            if (!locationSource.isActivated()) { // 권한 거부됨
                naverMap.setLocationTrackingMode(LocationTrackingMode.None);
            }
            return;
        }
        super.onRequestPermissionsResult(
                requestCode, permissions, grantResults);
    }

    @Override
    public void onMapReady(@NonNull @org.jetbrains.annotations.NotNull NaverMap naverMap) {

        mMap = naverMap;
        mPolygon= new PolygonOverlay();
        mMarkerArrayLIst = new ArrayList<>();
        mLatLngList = new ArrayList<>();

        this.naverMap = naverMap;
        naverMap.setLocationSource(locationSource);


        naverMap.setOnMapLongClickListener((point, coord) -> {

                    marker.setPosition(new LatLng(coord.latitude, coord.longitude));
                    marker.setMap(naverMap);
                    naverAddrApi = new NaverAddrApi(this);
                    naverAddrApi.execute(new LatLng(coord.latitude, coord.longitude));

           });



        CameraPosition cameraPosition = new CameraPosition(
                new LatLng(35.945255, 126.682155), 16
        );
        naverMap.setCameraPosition(cameraPosition);



        Marker marker = new Marker();
        marker.setPosition(new LatLng(35.9462369805542, 126.68215506925468));
        marker.setMap(naverMap);

        Marker marker2 = new Marker();
        marker2.setPosition(new LatLng(35.9674211, 126.7364801));
        marker2.setMap(naverMap);

        Marker marker3 = new Marker();
        marker3.setPosition(new LatLng(35.97627792560719, 126.62464725845604));
        marker3.setMap(naverMap);

        PolygonOverlay polygon = new PolygonOverlay();
        polygon.setCoords(Arrays.asList(
                new LatLng(35.9462369805542, 126.68215506925468),
                new LatLng(35.9674211, 126.7364801),
                new LatLng(35.97627792560719, 126.62464725845604)

        ));
        polygon.setMap(naverMap);
        polygon.setColor(Color.argb(100,255,0,0));

        naverMap.setLocationTrackingMode(LocationTrackingMode.Follow);


        maptype_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    mMap.setMapType(NaverMap.MapType.Basic);
                } else if (position == 1) {
                    mMap.setMapType(NaverMap.MapType.Satellite);
                } else if (position == 2) {
                    mMap.setMapType(NaverMap.MapType.Terrain);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



    }

    private void initViews() {
        maptype_spinner = findViewById(R.id.maptype_spinner);
        btnTest = findViewById(R.id.btnTest);
        btnMove = findViewById(R.id.btnMove);
        btnsearch = findViewById(R.id.btnsearch);
        editText = findViewById(R.id.sample_EditText);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.map_type, R.layout.custom_spinner_item);
        adapter.setDropDownViewResource(R.layout.custom_spinner_item_click);
        maptype_spinner.setAdapter(adapter);

        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < mMarkerArrayLIst.size(); i++) {
                    mMarkerArrayLIst.get(i).setMap(null);
                }
                mPolygon.setMap(null);
                marker.setMap(null);
            }
        });
        btnMove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CameraPosition cameraPosition = new CameraPosition(
                        new LatLng(35.945255, 126.682155), 11.7
                );
                naverMap.setCameraPosition(cameraPosition);

            }
        });
        btnsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String address = editText.getText().toString();
                new addressMarker().execute(address);

            }});

    }}