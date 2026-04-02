package com.oay.gestioninventariooay.util;

import com.oay.gestioninventariooay.exception.ValidacionException;

public class Validador {

    public static void requerirPresente(Object valor, String nombreCampo) throws ValidacionException {
        if (valor == null || valor.toString().trim().isEmpty()) {
            throw new ValidacionException("El campo '" + nombreCampo + "' es obligatorio.");
        }
    }

    public static double parsearDoublePositivo(String texto, String nombreCampo) throws ValidacionException {
        requerirPresente(texto, nombreCampo);
        try {
            double valor = Double.parseDouble(texto.replace(",", "."));
            if (valor < 0) throw new ValidacionException("El campo '" + nombreCampo + "' no puede ser negativo.");
            return valor;
        } catch (NumberFormatException e) {
            throw new ValidacionException("El campo '" + nombreCampo + "' debe ser un número válido (ej: 10.5).");
        }
    }

    public static Double parsearDoubleOpcional(String texto, String nombreCampo) throws ValidacionException {
        if (texto == null || texto.trim().isEmpty()) return null;
        try {
            double valor = Double.parseDouble(texto.replace(",", "."));
            if (valor < 0) throw new ValidacionException("El campo '" + nombreCampo + "' no puede ser negativo.");
            return valor;
        } catch (NumberFormatException e) {
            throw new ValidacionException("El campo '" + nombreCampo + "' debe ser un número válido.");
        }
    }

    public static int parsearEnteroPositivo(String texto, String nombreCampo) throws ValidacionException {
        requerirPresente(texto, nombreCampo);
        try {
            int valor = Integer.parseInt(texto.trim());
            if (valor < 0) throw new ValidacionException("El campo '" + nombreCampo + "' no puede ser negativo.");
            return valor;
        } catch (NumberFormatException e) {
            throw new ValidacionException("El campo '" + nombreCampo + "' debe ser un número entero sin decimales.");
        }
    }


    public static double parsearAjusteStock(String texto) throws ValidacionException {
        requerirPresente(texto, "Cantidad a ajustar");
        try {
            double valor = Double.parseDouble(texto.replace(",", "."));
            if (valor <= 0) throw new ValidacionException("Para sumar o restar, introduce una cantidad mayor que 0.");
            return valor;
        } catch (NumberFormatException e) {
            throw new ValidacionException("Has introducido texto. Escribe un número válido para ajustar el stock.");
        }
    }

    public static void comprobarResta(double stockActual, double aRestar, String tipoMedida) throws ValidacionException {
        if (stockActual - aRestar < 0) {
            throw new ValidacionException("Stock insuficiente.\nTienes " + stockActual + " " + tipoMedida + " y estás intentando restar " + aRestar + ".");
        }
    }

    public static Integer parsearEnteroOpcional(String texto, String nombreCampo) throws ValidacionException {
        if (texto == null || texto.trim().isEmpty()) return null;
        try {
            int valor = Integer.parseInt(texto.trim());
            if (valor < 0) throw new ValidacionException("El campo '" + nombreCampo + "' no puede ser negativo.");
            return valor;
        } catch (NumberFormatException e) {
            throw new ValidacionException("El campo '" + nombreCampo + "' debe ser un número entero sin decimales.");
        }
    }
}