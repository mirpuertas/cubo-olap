package olapcube.metricas;

import java.util.List;

/**
 * Clase que representa la medida Count, que cuenta el número de valores en una
 * lista.
 */
public class Count extends Medida {

    public Count() {
        super("Count");
    }

    @Override
    public double calcular(List<Double> valores) {
        return valores.size();
    }
}