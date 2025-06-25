package olapcube.estructura;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import olapcube.Proyeccion;
import olapcube.configuration.ConfigCubo;
import olapcube.configuration.ConfigDimension;
import olapcube.metricas.Medida;
import olapcube.metricas.RegistroMedida;

/**
 * Representa un cubo OLAP.
 */
public class Cubo {
    private Map<String, Dimension> dimensiones; // Mapeo de nombres de dimensión al objeto de la dimensión
    private Map<String, Medida> medidas; // Mapeo de nombres de medida al objeto de la medida
    private List<Celda> celdas; // Lista de celdas del cubo
    private List<String> nombresHechos; // Nombres de los hechos (columnas con valores del dataset de hechos)

    private Cubo() {
        dimensiones = new HashMap<>();
        celdas = new ArrayList<>();
        nombresHechos = new ArrayList<>();
        medidas = RegistroMedida.getMedidas();

    }

    /**
     * Método constructor que permite crear un cubo a partir de una configuración
     * 
     * @param config Configuración del cubo
     * @return Cubo
     */
    public static Cubo crearFromConfig(ConfigCubo config) {
        Cubo cubo = new Cubo();

        // Creación de dimensiones
        for (ConfigDimension configDimension : config.getDimensiones()) {
            cubo.agregarDimension(Dimension.crear(configDimension));
        }

        // Creación de hechos
        cubo.nombresHechos = List.of(config.getHechos().getNombresHechos());

        int indiceCelda = 0;
        for (String[] datos : config.getHechos().getDatasetReader().read()) {
            Celda celda = new Celda();
            for (String hecho : cubo.nombresHechos) {
                int columnaHecho = config.getHechos().getColumnaHecho(hecho);
                celda.agregarHecho(hecho, Double.parseDouble(datos[columnaHecho]));
            }
            cubo.agregarCelda(celda);

            // Agrega la celda a las dimensiones
            for (Dimension dimension : cubo.dimensiones.values()) {
                int columnaFkHechos = dimension.getColumnaFkHechos();
                int fk = Integer.parseInt(datos[columnaFkHechos]);
                dimension.agregarHecho(fk, indiceCelda);
            }

            indiceCelda++;
        }

        return cubo;
    }

    private void agregarDimension(Dimension dim1) {
        dimensiones.put(dim1.getNombre(), dim1);
    }

    private void agregarCelda(Celda celda) {
        if (!celdas.isEmpty()) {
            Celda primeraCelda = celdas.get(0);
            if (!primeraCelda.getHechos().keySet().equals(celda.getHechos().keySet())) {
                throw new IllegalArgumentException("La celda no contiene los mismos hechos que las celdas anteriores");
            }
            if (celda.getHechos().size() != nombresHechos.size()) {
                throw new IllegalArgumentException(
                        "La celda no contiene la misma cantidad de hechos que los hechos del cubo");
            }
            for (String hecho : nombresHechos) {
                if (celda.getHechos().get(hecho).size() != primeraCelda.getHechos().get(hecho).size()) {
                    throw new IllegalArgumentException(
                            "La celda no contiene la misma cantidad de valores para el hecho: " + hecho);
                }
            }
        }
        celdas.add(celda);
    }

    /**
     * Obtiene las celdas a partir de un conjunto de indices
     * 
     * @param indices Conjunto de indices
     * @return Lista de celdas
     */
    private List<Celda> celdasFromIndices(Set<Integer> indices) {
        List<Celda> celdas = new ArrayList<>();
        for (Integer indice : indices) {
            celdas.add(this.celdas.get(indice));
        }
        return celdas;
    }

    /**
     * Obtiene el conjunto de índices que existen en ambos conjuntos (intersección)
     * 
     * @param set1 El primer conjunto de índices
     * @param set2 El segundo conjunto de índices
     * @return Conjunto de índices que representa la intersección de ambos conjuntos
     */
    private static Set<Integer> celdasComunes(Set<Integer> set1, Set<Integer> set2) {
        Set<Integer> nuevo = new HashSet<>(set1);
        nuevo.retainAll(set2);
        return nuevo;
    }

    /**
     * Obtiene una celda a partir de una dimensión y un valor, reduciendo las dos
     * dimensiones restantes.
     * 
     * @param dimension La dimensión a la que pertenece el valor
     * @param valor     El valor de la dimensión a buscar
     * @return Celda que agrupa todas las celdas que contienen el valor en esa
     *         dimensión
     */
    public Celda getCelda(Dimension dimension, String valor) {
        return Celda.agrupar(celdasFromIndices(dimension.getIndicesCeldas(valor)));
    }

    /**
     * Obtiene una celda a partir de dos dimensiones y dos valores, reduciendo la
     * dimensión restante.
     * 
     * @param dim1   La primera dimensión
     * @param valor1 El valor de la primera dimensión
     * @param dim2   La segunda dimensión
     * @param valor2 El valor de la segunda dimensión
     * @return Celda que agrupa todas las celdas que contienen los valores en esas
     *         dos dimensiones
     */
    public Celda getCelda(Dimension dim1, String valor1, Dimension dim2, String valor2) {
        Set<Integer> indicesComunes = celdasComunes(dim1.getIndicesCeldas(valor1), dim2.getIndicesCeldas(valor2));
        return Celda.agrupar(celdasFromIndices(indicesComunes));
    }

    public List<String> getNombresHechos() {
        return nombresHechos;
    }

    public List<Medida> getMedidas() {
        return new ArrayList<>(medidas.values());
    }

    public Medida getMedida(String nombre) {
        return RegistroMedida.getMedida(nombre);
    }

    public Dimension getDimension(String nombre) {
        if (!dimensiones.containsKey(nombre)) {
            throw new IllegalArgumentException("Dimensión no encontrada: " + nombre);
        }
        return dimensiones.get(nombre);
    }

    @Override
    public String toString() {
        return "Cubo [celdas=" + celdas.size() + ", dimensiones=" + dimensiones.keySet() + ", medidas=" + medidas.size()
                + "]";
    }

    public void printDimensionYNivel(String nombre) {
        Dimension dimension = getDimension(nombre);
        System.out.println(dimension);
    }

    public Proyeccion proyectar() {
        return new Proyeccion(this);
    }

    private Cubo copiar() {
        Cubo cubo = new Cubo();
        cubo.dimensiones = new HashMap<>();
        for (Dimension dimension : this.dimensiones.values()) {
            cubo.dimensiones.put(dimension.getNombre(), dimension.copiar());
        }
        cubo.celdas = new ArrayList<>(this.celdas);
        cubo.nombresHechos = new ArrayList<>(this.nombresHechos);
        cubo.medidas = new HashMap<>(this.medidas);
        return cubo;
    }

    public Cubo slice(String dimension, String valor) {
        Cubo cubo = this.copiar();
        Dimension dim = cubo.getDimension(dimension);
        if (dim != null) {
            dim.filtrar(valor);
        } else {
            System.out.println("Dimensión no encontrada: " + dimension);
        }
        return cubo;
    }

    public Cubo dice(String dimension, String[] valor) {
        Cubo cubo = this.copiar();
        cubo.dimensiones.get(dimension).filtrar(valor);
        return cubo;
    }

    public Cubo dice(String dimension, String[] valor, String dimension2, String[] valor2) {
        Cubo cubo = this.copiar();
        cubo.dimensiones.get(dimension).filtrar(valor);
        cubo.dimensiones.get(dimension2).filtrar(valor2);
        return cubo;
    }

    public Cubo dice(String dimension, String[] valor, String dimension2, String[] valor2, String dimension3,
            String[] valor3) {
        Cubo cubo = this.copiar();
        cubo.dimensiones.get(dimension).filtrar(valor);
        cubo.dimensiones.get(dimension2).filtrar(valor2);
        cubo.dimensiones.get(dimension3).filtrar(valor3);
        return cubo;
    }

    public void rollUp(String dimension) {
        getDimension(dimension).rollUp();
    }

    public void drillDown(String dimension) {
        getDimension(dimension).drillDown();
    }

}
