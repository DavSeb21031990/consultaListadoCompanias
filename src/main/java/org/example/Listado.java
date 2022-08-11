package org.example;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Listado {

    private String identificacion;
    private String nombre;
    private String correo1;
    private String correo2;

    @Override
    public String toString() {
        return "Listado{" +
                "identificacion='" + identificacion + '\'' +
                ", nombre='" + nombre + '\'' +
                ", correo1='" + correo1 + '\'' +
                ", correo2='" + correo2 + '\'' +
                '}';
    }
}
