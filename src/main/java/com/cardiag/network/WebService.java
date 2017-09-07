package com.cardiag.network;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Lucas on 22/04/2017.
 */

public class WebService {

    public JSONObject obtenerDireccionesEstacionesServicio(Double... params){
        String cadena = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=";

        //http://maps.googleapis.com/maps/api/geocode/json?latlng=-34.604027,-58.410619&sensor=false

        //https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=-34.604027,-58.410619&radius=500&types=car_repair&key=AIzaSyAf2ic2q9p_-uzyYaJP7jarlhQXzZcLhS4
        //https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=-34.604027,-58.410619&radius=500&types=gas_station&key=AIzaSyAf2ic2q9p_-uzyYaJP7jarlhQXzZcLhS4
        cadena = cadena + params[0];
        cadena = cadena + ",";
        cadena = cadena + params[1];
        cadena = cadena + "&radius=500&types=gas_station&key=AIzaSyAf2ic2q9p_-uzyYaJP7jarlhQXzZcLhS4";

        return crearConexion(cadena);
    }

    public JSONObject obtenerDireccionesTalleresMecanicos(Double... params){
        String cadena = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=";

        //http://maps.googleapis.com/maps/api/geocode/json?latlng=-34.604027,-58.410619&sensor=false

        //https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=-34.604027,-58.410619&radius=500&types=car_repair&key=AIzaSyAf2ic2q9p_-uzyYaJP7jarlhQXzZcLhS4
        //https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=-34.604027,-58.410619&radius=500&types=gas_station&key=AIzaSyAf2ic2q9p_-uzyYaJP7jarlhQXzZcLhS4
        cadena = cadena + params[0];
        cadena = cadena + ",";
        cadena = cadena + params[1];
        cadena = cadena + "&radius=500&types=car_repair&key=AIzaSyAf2ic2q9p_-uzyYaJP7jarlhQXzZcLhS4";

        return crearConexion(cadena);
    }

    private JSONObject crearConexion(String cadena) {
        URL url = null; // Url de donde queremos obtener información
        JSONObject respuestaJSON = null;
        try {
            url = new URL(cadena);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection(); //Abrir la conexión
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-length", "0");
            connection.setUseCaches(false);
            connection.setAllowUserInteraction(false);
            //connection.setHeader("content-type", "application/json");

            int respuesta = connection.getResponseCode();
            StringBuilder result = new StringBuilder();

            if (respuesta == HttpURLConnection.HTTP_OK) {


                InputStream in = new BufferedInputStream(connection.getInputStream());  // preparo la cadena de entrada

                BufferedReader reader = new BufferedReader(new InputStreamReader(in));  // la introduzco en un BufferedReader

                // El siguiente proceso lo hago porque el JSONOBject necesita un String y tengo
                // que tranformar el BufferedReader a String. Esto lo hago a traves de un
                // StringBuilder.

                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);        // Paso toda la entrada al StringBuilder
                }

                //Creamos un objeto JSONObject para poder acceder a los atributos (campos) del objeto.
                respuestaJSON = new JSONObject(result.toString());
            }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return respuestaJSON;
    }
}
