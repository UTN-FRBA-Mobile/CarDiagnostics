package com.cardiag.utils.enums;

/**
 * Created by Lucas on 30/07/2017.
 */

public  enum MapService {
    GAS_STATION(0, "Búsqueda de estaciones de servicio"), CAR_REPAIR(1, "Búsqueda de talleres mecánicos");

    private final Integer tipo;
    private final String nombre;

    MapService(Integer tipo, String nombre){
        this.tipo = tipo;
        this.nombre = nombre;
    }

    public Integer getTipo(){
        return this.tipo;
    }

    public String getNombre(){
        return this.nombre;
    }

}
