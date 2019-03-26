package com.kr4tenz.heredemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.widget.EditText
import com.here.android.mpa.common.GeoCoordinate
import com.here.android.mpa.common.OnEngineInitListener
import com.here.android.mpa.mapping.Map
import com.here.android.mpa.mapping.SupportMapFragment
import com.here.android.mpa.mapping.MapMarker
import com.here.android.mpa.search.ErrorCode
import com.here.android.mpa.search.GeocodeRequest2
import android.view.View


class MainActivity : AppCompatActivity() {

    private var map : Map = Map()
    private var mapFragment : SupportMapFragment = SupportMapFragment()
    private lateinit var marker : MapMarker
    private lateinit var editText : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mapFragment = getSupportFragmentManager().findFragmentById(R.id.mapfragment) as SupportMapFragment
        mapFragment.init { error ->
            if (error == OnEngineInitListener.Error.NONE) {
                map = mapFragment.map
                map.setCenter(GeoCoordinate(37.7397, -121.4252, 0.0), Map.Animation.NONE)
                map.zoomLevel = (map.maxZoomLevel + map.minZoomLevel) / 2
            }
        }
        editText = findViewById(R.id.query)

        editText.setOnKeyListener(View.OnKeyListener { _, keyCode, keyevent ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && keyevent.action == KeyEvent.ACTION_UP) {
                dropMarker(editText.text.toString())
                editText.setText("")
                return@OnKeyListener true
            }
            false
        })
    }

    fun dropMarker(query: String) {
        if (::marker.isInitialized) {
            map.removeMapObject(marker)
        }
        val tracy = GeoCoordinate(37.7397, -121.4252)
        val request = GeocodeRequest2(query).setSearchArea(tracy, 5000)
        request.execute { results, error ->
            if (error != ErrorCode.NONE) {
                Log.e("HERE", error.toString())
            } else {
                for (result in results) {
                    marker = MapMarker()
                    marker.coordinate = GeoCoordinate(result.location.coordinate.latitude, result.location.coordinate.longitude, 0.0)
                    map.addMapObject(marker)
                }
            }
        }
    }
}