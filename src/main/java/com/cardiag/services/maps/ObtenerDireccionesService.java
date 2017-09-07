package com.cardiag.services.maps;

import android.os.AsyncTask;

import com.cardiag.R;
import com.cardiag.activity.MapsActivity;
import com.cardiag.models.maps.Mapa;
import com.cardiag.network.WebService;
import com.cardiag.utils.enums.MapService;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lucas on 22/04/2017.
 */

public class ObtenerDireccionesService extends AsyncTask<Double, Void, List<Mapa>> {

    private GoogleMap googleMap;
    private Mapa ubicacionActual;
    private Integer tipo;
    private MapsActivity activity;

    public ObtenerDireccionesService(GoogleMap mMap, Integer tipo, MapsActivity activity){
        this.googleMap = mMap;
        this.tipo = tipo;
        this.activity = activity;
        this.ubicacionActual = new Mapa();
    }

    @Override
    protected List<Mapa> doInBackground(Double... values) {

        WebService webService = new WebService();
        List<Mapa> mapas = new ArrayList<Mapa>();
        JSONObject jsonObject;

        //Estaciones de servicio
        if(tipo == MapService.GAS_STATION.getTipo()){
            jsonObject = webService.obtenerDireccionesEstacionesServicio(values);
            mapas.addAll(convertJsonToMapas(jsonObject, R.drawable.ic_local_gas_station_black_24dp));
            if(mapas.isEmpty()){
                activity.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        activity.popUpError("No se encontraron ubicaciones de estaciones de servicio cerca de su localización actual");
                    }
                });

                this.cancel(true);
            }
        }
        //Talleres mecanicos
        else if (tipo == MapService.CAR_REPAIR.getTipo()){
            jsonObject = webService.obtenerDireccionesTalleresMecanicos(values);
            mapas.addAll(convertJsonToMapas(jsonObject, R.drawable.ic_build_black_24dp));
            if(mapas.isEmpty()){
                activity.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        activity.popUpError("No se encontraron ubicaciones de talleres mecánicos cerca de su localización actual");
                    }
                });
               this.cancel(true);
            }
        }

        //Ubicacion del usuario
        ubicacionActual.setLatitud(values[0]);
        ubicacionActual.setLongitud(values[1]);

        return mapas;
    }

    private List<Mapa> convertJsonToMapas(JSONObject jsonObject, Integer markerIcon) {
        List<Mapa> mapas = new ArrayList<Mapa>();
        try{
            JSONArray resultJSON = jsonObject.getJSONArray("results");

            for(int i = 0; i < resultJSON.length(); i++){

                Mapa mapa = new Mapa();

                JSONObject resultado = resultJSON.getJSONObject(i);
                JSONObject geometry = resultado.getJSONObject("geometry");
                JSONObject location = geometry.getJSONObject("location");

                mapa.setLatitud(location.getDouble("lat"));
                mapa.setLongitud(location.getDouble("lng"));
                mapa.setMarkerIcon(markerIcon);
                mapas.add(mapa);
            }

        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        return mapas;
    }


    @Override
    protected void onPostExecute(List<Mapa> mapas) {
        for(int i = 0 ; i < mapas.size() ; i++ ) {
            crearMarker(mapas.get(i), googleMap);
        }
    }

    private Marker crearMarker(Mapa mapa, GoogleMap googleMap) {

        return googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(mapa.getLatitud(), mapa.getLongitud()))
                .anchor(0.5f, 0.5f)
                .icon(BitmapDescriptorFactory.fromResource(mapa.getMarkerIcon())));
    }

}

