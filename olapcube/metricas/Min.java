package olapcube.metricas;

import java.util.List;

/**
 * Clase que representa una métrica para calcular el valor mínimo en un conjunto
 * de datos.
 */
public class Min extends Medida {

    public Min() {
        super("Minimo");
    }

    @Override
    public double calcular(List<Double> valores) {
        if (valores == null || valores.isEmpty() || contieneValoresNulos(valores) || contieneCeros(valores)) {
            return 0.0; // Devuelve 0.0 cuando la lista es nula, vacía
        }

        double minimo = valores.get(0);
        for (double valor : valores) {
            if (valor < minimo) {
                minimo = valor;
            }
        }

        return minimo;
    }

    // Método para verificar si la lista contiene valores nulos
    private boolean contieneValoresNulos(List<Double> valores) {
        for (Double valor : valores) {
            if (valor == null) {
                return true;
            }
        }
        return false;
    }

    // Método para verificar si la lista contiene ceros
    private boolean contieneCeros(List<Double> valores) {
        for (Double valor : valores) {
            if (valor == 0) {
                return true;
            }
        }
        return false;
    }
}