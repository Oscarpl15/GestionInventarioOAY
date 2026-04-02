package com.oay.gestioninventariooay.model;

import jakarta.persistence.*;

@Entity
@Table(name = "herramientas")
public class Herramienta extends ElementoInventario {

    @Column(nullable = false)
    private String categoria;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(name = "material_mecanizar")
    private String materialMecanizar;

    private String referencia;

    // --- Torno ---
    @Column(name = "tipo_mecanizado")
    private String tipoMecanizado;
    private String portaherramientas;
    @Column(name = "radio_punta")
    private Double radioPunta;
    @Column(name = "angulo_corte")
    private Double anguloCorte;
    @Column(name = "diametro_minimo")
    private Double diametroMinimo;
    @Column(name = "angulo_punta")
    private Double anguloPunta;
    @Column(name = "paso_minimo")
    private Double pasoMinimo;
    @Column(name = "paso_maximo")
    private Double pasoMaximo;
    private Double ancho;
    @Column(name = "profundidad_maxima")
    private Double profundidadMaxima;

    // --- Fresa ---
    @Column(name = "num_labios")
    private Integer numLabios;
    @Column(name = "tipo_fresa")
    private String tipoFresa;
    @Column(name = "referencia_plaquita")
    private String referenciaPlaquita; // Para plato cuchillas

    // --- Fresa y Broca y Torno(Broca) ---
    private Double diametro;
    @Column(name = "longitud_corte")
    private Double longitudCorte;

    // --- Machos ---
    @Column(name = "tipo_rosca")
    private String tipoRosca;
    @Column(name = "medida_rosca")
    private String medidaRosca;
    @Column(name = "tipo_agujero")
    private String tipoAgujero;

    public Herramienta() {}

    // (Aquí debes generar todos los Getters y Setters de los nuevos atributos. Por brevedad, asume que están todos).
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    public String getMaterialMecanizar() { return materialMecanizar; }
    public void setMaterialMecanizar(String materialMecanizar) { this.materialMecanizar = materialMecanizar; }
    public String getReferencia() { return referencia; }
    public void setReferencia(String referencia) { this.referencia = referencia; }

    // Torno
    public String getTipoMecanizado() { return tipoMecanizado; }
    public void setTipoMecanizado(String tipoMecanizado) { this.tipoMecanizado = tipoMecanizado; }
    public String getPortaherramientas() { return portaherramientas; }
    public void setPortaherramientas(String portaherramientas) { this.portaherramientas = portaherramientas; }
    public Double getRadioPunta() { return radioPunta; }
    public void setRadioPunta(Double radioPunta) { this.radioPunta = radioPunta; }
    public Double getAnguloCorte() { return anguloCorte; }
    public void setAnguloCorte(Double anguloCorte) { this.anguloCorte = anguloCorte; }
    public Double getDiametroMinimo() { return diametroMinimo; }
    public void setDiametroMinimo(Double diametroMinimo) { this.diametroMinimo = diametroMinimo; }
    public Double getAnguloPunta() { return anguloPunta; }
    public void setAnguloPunta(Double anguloPunta) { this.anguloPunta = anguloPunta; }
    public Double getPasoMinimo() { return pasoMinimo; }
    public void setPasoMinimo(Double pasoMinimo) { this.pasoMinimo = pasoMinimo; }
    public Double getPasoMaximo() { return pasoMaximo; }
    public void setPasoMaximo(Double pasoMaximo) { this.pasoMaximo = pasoMaximo; }
    public Double getAncho() { return ancho; }
    public void setAncho(Double ancho) { this.ancho = ancho; }
    public Double getProfundidadMaxima() { return profundidadMaxima; }
    public void setProfundidadMaxima(Double profundidadMaxima) { this.profundidadMaxima = profundidadMaxima; }

    // Fresa
    public Integer getNumLabios() { return numLabios; }
    public void setNumLabios(Integer numLabios) { this.numLabios = numLabios; }
    public String getTipoFresa() { return tipoFresa; }
    public void setTipoFresa(String tipoFresa) { this.tipoFresa = tipoFresa; }
    public String getReferenciaPlaquita() { return referenciaPlaquita; }
    public void setReferenciaPlaquita(String referenciaPlaquita) { this.referenciaPlaquita = referenciaPlaquita; }
    public Double getDiametro() { return diametro; }
    public void setDiametro(Double diametro) { this.diametro = diametro; }
    public Double getLongitudCorte() { return longitudCorte; }
    public void setLongitudCorte(Double longitudCorte) { this.longitudCorte = longitudCorte; }

    // Machos
    public String getTipoRosca() { return tipoRosca; }
    public void setTipoRosca(String tipoRosca) { this.tipoRosca = tipoRosca; }
    public String getMedidaRosca() { return medidaRosca; }
    public void setMedidaRosca(String medidaRosca) { this.medidaRosca = medidaRosca; }
    public String getTipoAgujero() { return tipoAgujero; }
    public void setTipoAgujero(String tipoAgujero) { this.tipoAgujero = tipoAgujero; }
}