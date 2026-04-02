package com.oay.gestioninventariooay.model;

import jakarta.persistence.*;

@MappedSuperclass
public abstract class ElementoInventario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "empresa_compra") // Ya no es nullable = false
    private String empresaCompra;

    private Double precio; // Ya no es nullable = false

    public ElementoInventario() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getEmpresaCompra() { return empresaCompra; }
    public void setEmpresaCompra(String empresaCompra) { this.empresaCompra = empresaCompra; }

    public Double getPrecio() { return precio; }
    public void setPrecio(Double precio) { this.precio = precio; }
}
