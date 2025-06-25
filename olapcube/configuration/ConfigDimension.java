package olapcube.configuration;

import java.util.List;

import olapcube.readers.CSVReader;
import olapcube.readers.DatasetReader;

/**
 * Clase que representa la configuración de una dimensión
 * 
 * Esta clase utiliza un constructor privado y métodos estáticos para la
 * creación de instancias.
 */
public class ConfigDimension {
    private String nombre; // Nombre de la dimensión
    private DatasetReader datasetReader; // DatasetReader de la dimensión
    private int columnaKey; // Columna que contiene la clave primaria en el dataset de la dimensión
    private int columnaFkHechos; // Columna que contiene la clave foránea en el dataset de los hechos
    private List<Integer> niveles; // Columnas que representan los diferentes niveles de la dimensión

    /**
     * Constructor privado de la clase
     * 
     * @param nombre          Nombre de la dimensión
     * @param datasetReader   DatasetReader de la dimensión
     * @param columnaKey      Columna que contiene la clave primaria en el dataset
     *                        de la dimensión
     * @param columnaFkHechos Columna que contiene la clave foránea en el dataset de
     *                        los hechos
     * @param niveles         Columnas que representan los diferentes niveles de la
     *                        dimensión
     */
    private ConfigDimension(String nombre, DatasetReader datasetReader, int columnaKey, int columnaFkHechos,
            List<Integer> niveles) {
        this.nombre = nombre;
        this.datasetReader = datasetReader;
        this.columnaKey = columnaKey;
        this.columnaFkHechos = columnaFkHechos;
        this.niveles = niveles;
    }

    /**
     * Método que permite crear una configuración de dimensión a partir de un
     * archivo CSV
     * 
     * @param nombre          Nombre de la dimensión
     * @param filePath        Ruta del archivo CSV
     * @param columnaKey      Columna que contiene la clave primaria en la tabla de
     *                        la dimensión
     * @param columnaFkHechos Columna que contiene la clave foránea en la tabla de
     *                        los hechos
     * @param niveles         Columnas que representan los diferentes niveles de la
     *                        dimensión
     * @return Configuración de la dimensión
     */
    public static ConfigDimension configCSV(String nombre, String filePath, int columnaKey, int columnaFkHechos,
            List<Integer> niveles) {
        return new ConfigDimension(nombre, new CSVReader(filePath), columnaKey, columnaFkHechos, niveles);
    }

    public String getNombre() {
        return nombre;
    }

    public DatasetReader getDatasetReader() {
        return datasetReader;
    }

    public int getColumnaKey() {
        return columnaKey;
    }

    public int getColumnaFkHechos() {
        return columnaFkHechos;
    }

    public List<Integer> getNiveles() {
        return niveles;
    }
}
