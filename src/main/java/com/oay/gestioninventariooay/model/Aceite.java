package com.oay.gestioninventariooay.model;

import jakarta.persistence.*;

@Entity
@Table(name = "aceites")
public class Aceite extends ElementoInventario {

    @Column(name = "tipo_aceite", nullable = false)
    private String tipoAceite;

    @Column(name = "maquina_destino", nullable = false)
    private String maquinaDestino; // Fijo: torno mazak, fresa mikron, fresa kondia, fresa puente, sierra

    private String referencia;

    // Aquí lo trataremos por unidades de garrafas/bidones
    @Column(name = "capacidad_litros", nullable = false)
    private Double capacidadLitros; // Ej: 25 (garrafa de 25L)

    @Column(nullable = false)
    private Integer cantidad; // Unidades (Ej: 2 garrafas)

    public Aceite() {}

    // --- Getters y Setters ---
    public String getTipoAceite() { return tipoAceite; }
    public void setTipoAceite(String tipoAceite) { this.tipoAceite = tipoAceite; }

    public String getMaquinaDestino() { return maquinaDestino; }
    public void setMaquinaDestino(String maquinaDestino) { this.maquinaDestino = maquinaDestino; }

    public String getReferencia() { return referencia; }
    public void setReferencia(String referencia) { this.referencia = referencia; }

    public Double getCapacidadLitros() { return capacidadLitros; }
    public void setCapacidadLitros(Double capacidadLitros) { this.capacidadLitros = capacidadLitros; }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
}
