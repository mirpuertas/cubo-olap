package olapcube.metricas;

import java.util.List;

/**
 * Clase que representa una métrica para calcular el valor máximo en un conjunto
 * de datos.
 */
public class Max extends Medida {

    public Max() {
        super("Maximo");
    }

    @Override
    public double calcular(List<Double> valores) {
        if (valores == null || valores.isEmpty() || contieneValoresNulos(valores) || contieneCeros(valores)) {
            return 0.0; // Devuelve 0.0 cuando la lista es nula, vacía
        }

        double maximo = valores.get(0);
        for (double valor : valores) {
            if (valor > maximo) {
                maximo = valor;
            }
        }

        return maximo;
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