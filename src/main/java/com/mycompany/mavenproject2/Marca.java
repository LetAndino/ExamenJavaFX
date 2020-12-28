package com.mycompany.mavenproject2;



/**
 *
 * @author leti
 */
public class Marca {
    
    private Integer codigo;
    private String descripcion;
    
    public Marca(Integer codigo, String descripcion){
        this.descripcion = descripcion;
        this.codigo = codigo;
    }
    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer Codigo) {
        this.codigo = Codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String Descripcion) {
        this.descripcion = Descripcion;
    }

    
}
