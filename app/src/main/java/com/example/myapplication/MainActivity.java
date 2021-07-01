package com.example.myapplication;

import android.graphics.PointF;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.Spinner;
import android.widget.Toast;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;

import org.jetbrains.annotations.NotNull;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    Spinner maptype_spinner;
    NaverMap mMap;
    CheckBox checkBoxLayer;
    Button btnNaverAddr;
    NaverAddrApi naverAddrApi;

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
    }




    @Override
    public void onMapReady(@NonNull @org.jetbrains.annotations.NotNull NaverMap naverMap) {
        mMap = naverMap;





        mMap.setOnMapClickListener((point, coord) ->
                Toast.makeText(this, coord.latitude + ", " + coord.longitude,
                        Toast.LENGTH_SHORT).show());


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
        CameraUpdate cameraUpdate = CameraUpdate.scrollTo(new LatLng(35.9462369805542, 126.68215506925468));
        naverMap.moveCamera(cameraUpdate);


    }

    private void initViews() {
        maptype_spinner = findViewById(R.id.maptype_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.map_type, R.layout.custom_spinner_item);
        adapter.setDropDownViewResource(R.layout.custom_spinner_item_click);
        maptype_spinner.setAdapter(adapter);
        checkBoxLayer = findViewById(R.id.checkBoxLayer);
        btnNaverAddr = findViewById(R.id.btnNaverAddr);

        btnNaverAddr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                naverAddrApi = new NaverAddrApi();
                naverAddrApi.execute(new LatLng(35.9462369805542, 126.68215506925468));
            }
        });

    }
}