package com.cardiag.models.maps;

/**
 * Created by Lucas on 22/04/2017.
 */

public class Mapa {
    private Double latitud;
    private Double longitud;
    private Integer markerIcon;

    public Integer getMarkerIcon() {
        return markerIcon;
    }

    public void setMarkerIcon(Integer markerIcon) {
        this.markerIcon = markerIcon;
    }

    public Double getLatitud() {
        return latitud;
    }

    public void setLatitud(Double latitud) {
        this.latitud = latitud;
    }

    public Double getLongitud() {
        return longitud;
    }

    public void setLongitud(Double longitud) {
        this.longitud = longitud;
    }
}
