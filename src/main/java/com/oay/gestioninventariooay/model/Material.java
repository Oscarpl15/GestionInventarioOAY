package com.oay.gestioninventariooay.model;

import jakarta.persistence.*;

@Entity
@Table(name = "materiales")
public class Material extends ElementoInventario {

    @Column(nullable = false)
    private String categoria;

    @Column(name = "tipo_material", nullable = false)
    private String tipoMaterial;

    // Longitud (ahora puede ser nula si es plancha)
    private Double longitud;

    // Unidades (solo para plancha)
    private Integer unidades;

    private Double diametro; // Redonda
    private Double lado;     // Cuadrada/Rectangular (Lado 1)
    private Double lado2;    // Cuadrada/Rectangular (Lado 2)
    private Double largo;    // Plancha
    private Double ancho;    // Plancha
    private Double espesor;  // Plancha

    public Material() {}

    // --- Getters y Setters ---
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public String getTipoMaterial() { return tipoMaterial; }
    public void setTipoMaterial(String tipoMaterial) { this.tipoMaterial = tipoMaterial; }

    public Double getLongitud() { return longitud; }
    public void setLongitud(Double longitud) { this.longitud = longitud; }

    public Integer getUnidades() { return unidades; }
    public void setUnidades(Integer unidades) { this.unidades = unidades; }

    public Double getDiametro() { return diametro; }
    public void setDiametro(Double diametro) { this.diametro = diametro; }

    public Double getLado() { return lado; }
    public void setLado(Double lado) { this.lado = lado; }

    public Double getLado2() { return lado2; }
    public void setLado2(Double lado2) { this.lado2 = lado2; }

    public Double getLargo() { return largo; }
    public void setLargo(Double largo) { this.largo = largo; }

    public Double getAncho() { return ancho; }
    public void setAncho(Double ancho) { this.ancho = ancho; }

    public Double getEspesor() { return espesor; }
    public void setEspesor(Double espesor) { this.espesor = espesor; }
}