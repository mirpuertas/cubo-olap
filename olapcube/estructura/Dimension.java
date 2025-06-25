package olapcube.estructura;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import olapcube.configuration.ConfigDimension;

/**
 * Clase que representa una dimensión de un cubo OLAP.
 */
public class Dimension {
    private String nombre; // Nombre de la dimensión
    private List<Map<String, Set<Integer>>> valoresToCeldasConNiveles; // Nivel -> Valor -> Celdas
    private Map<Integer, String[]> idToValores; // Mapeo de ids (pk) de la dimensión a valores
    private Map<String, List<Integer>> valorToPk; // Mapeo de valores de la dimensión a ids (pk)
    private int columnaFkHechos; // Columna que contiene la clave foránea en la tabla de los hechos
    private List<Integer> niveles; // Columnas de los niveles
    private int nivelActual; // Nivel actual de la dimensión

    /**
     * Constructor de la clase
     * 
     * @param nombre Nombre de la dimensión
     */
    private Dimension(String nombre) {
        this.nombre = nombre;
        this.valoresToCeldasConNiveles = new ArrayList<>();
        this.idToValores = new HashMap<>();
        this.valorToPk = new HashMap<>();
        this.niveles = new ArrayList<>();
        this.nivelActual = 0;
    }

    /**
     * Método constructor que permite crear una dimensión a partir de una
     * configuración
     * 
     * @param configDimension Configuración de la dimensión
     * @return Dimension
     */
    public static Dimension crear(ConfigDimension configDimension) {
        Dimension dim = new Dimension(configDimension.getNombre());
        dim.columnaFkHechos = configDimension.getColumnaFkHechos();
        dim.niveles = configDimension.getNiveles();

        // Inicializar mapas
        for (int i = 0; i < dim.niveles.size(); i++) {
            dim.valoresToCeldasConNiveles.add(new LinkedHashMap<>());
        }

        // Cargar datos para todos los niveles
        for (String[] datos : configDimension.getDatasetReader().read()) {
            int pkDimension = Integer.parseInt(datos[configDimension.getColumnaKey()]);
            String[] valores = new String[dim.niveles.size()];

            for (int i = 0; i < dim.niveles.size(); i++) {
                int nivelColumna = dim.niveles.get(i);
                valores[i] = datos[nivelColumna];
            }
            dim.idToValores.put(pkDimension, valores);
        }

        return dim;
    }

    @Override
    public String toString() {
        return "Dimension [nombre=" + nombre + ", nivelActual=" + getNivelActual() + "]";
    }

    public String getNombre() {
        return nombre;
    }

    public int getColumnaFkHechos() {
        return columnaFkHechos;
    }

    public int getNivelActual() {
        return nivelActual;
    }

    public Set<Integer> getIndicesCeldas(String valor) {
        return valoresToCeldasConNiveles.get(nivelActual).getOrDefault(valor, new HashSet<>());
    }

    public String[] getValoresNivel(int nivel) {
        return valoresToCeldasConNiveles.get(nivel).keySet().toArray(new String[0]);
    }

    public void rollUp() {
        if (nivelActual > 0) {
            nivelActual--;
        }
    }

    public void drillDown() {
        if (nivelActual < valoresToCeldasConNiveles.size() - 1) {
            nivelActual++;
        }
    }

    public void agregarHecho(int idValor, int indiceCelda) {
        String[] valores = idToValores.get(idValor);
        if (valores == null) {
            throw new IllegalArgumentException("El id " + idValor + " del valor no existe en la dimensión " + nombre);
        }

        StringBuilder valorAcumulado = new StringBuilder();
        for (int i = 0; i < valores.length; i++) {
            if (i > 0) {
                valorAcumulado.append("/");
            }
            valorAcumulado.append(valores[i]);
            String valorAcumuladoStr = valorAcumulado.toString();
            valoresToCeldasConNiveles.get(i).computeIfAbsent(valorAcumuladoStr, k -> new HashSet<>()).add(indiceCelda);
        }
    }

    public Dimension copiar() {
        Dimension nueva = new Dimension(this.nombre);
        nueva.valoresToCeldasConNiveles = new ArrayList<>();
        for (Map<String, Set<Integer>> nivel : this.valoresToCeldasConNiveles) {
            Map<String, Set<Integer>> nuevoNivel = new LinkedHashMap<>();
            for (Map.Entry<String, Set<Integer>> entry : nivel.entrySet()) {
                nuevoNivel.put(entry.getKey(), new HashSet<>(entry.getValue()));
            }
            nueva.valoresToCeldasConNiveles.add(nuevoNivel);
        }
        nueva.idToValores = new HashMap<>(this.idToValores);
        nueva.columnaFkHechos = this.columnaFkHechos;
        nueva.niveles = new ArrayList<>(this.niveles);
        nueva.nivelActual = this.nivelActual;
        nueva.valorToPk = new HashMap<>(this.valorToPk);
        return nueva;
    }

    public void filtrar(String valor) {
        filtrar(new String[] { valor });
    }

    public void filtrar(String[] valores) {
        Map<String, Set<Integer>> nivelAct = valoresToCeldasConNiveles.get(nivelActual);
        Map<String, Set<Integer>> nuevosValores = new LinkedHashMap<>();
        for (String valor : valores) {
            if (nivelAct.containsKey(valor)) {
                nuevosValores.put(valor, nivelAct.get(valor));
            }
        }
        valoresToCeldasConNiveles.set(nivelActual, nuevosValores);
    }

}
