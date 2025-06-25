package olapcube;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import olapcube.estructura.Celda;
import olapcube.estructura.Cubo;
import olapcube.estructura.Dimension;
import olapcube.metricas.Medida;

/**
 * Clase que representa una proyeccion de un cubo OLAP
 */
public class Proyeccion {
    private Cubo cubo; // Cubo sobre el que se realiza la proyeccion
    private int maxFilas = 50; // Maximo de filas a mostrar
    private String hecho; // Hecho a proyectar
    private Medida medida; // Medida a proyectar
    private Map<String, Medida> medidasPorHecho; // Medidas disponibles por hecho

    // Atributos para mostrar en consola
    private static final String FORMATO_CELDA = "%20.20s";
    private static final String FORMATO_CELDA_IZQUIERDA = "%-20.20s";
    private static final String SEPARADOR = " | ";
    private static final String SEPARADOR_DIM = " / ";

    /**
     * Constructor de la clase
     * 
     * @param cubo Cubo sobre el que se realiza la proyeccion
     */
    public Proyeccion(Cubo cubo) {
        this.cubo = cubo;
        this.hecho = cubo.getNombresHechos().get(0); // Selecciona el primer hecho por defecto
        this.medida = cubo.getMedidas().get(0); // Selecciona la primera medida por defecto
        this.medidasPorHecho = new HashMap<>();
    }

    public void seleccionarHecho(String hecho) {
        this.hecho = hecho;
    }

    public void seleccionarMedida(String nombreMedida) {
        Medida medidaSeleccionada = cubo.getMedida(nombreMedida);
        if (medidaSeleccionada != null) {
            this.medida = medidaSeleccionada;
        } else {
            throw new IllegalArgumentException("Medida no encontrada: " + nombreMedida);
        }
    }

    public void seleccionarMedidaPorHecho(String hecho, String nombreMedida) {
        Medida medidaSeleccionada = cubo.getMedida(nombreMedida);
        if (medidaSeleccionada != null) {
            medidasPorHecho.put(hecho, medidaSeleccionada);
        } else {
            throw new IllegalArgumentException("Medida no encontrada: " + nombreMedida);
        }
    }

    public void printPivot(String nombreDim1, String nombreDim2) {
        printPivot(nombreDim1, nombreDim2, false, false);
    }

    public void printPivot(String nombreDim1, String nombreDim2, boolean mostrarUltimaParteDim1,
            boolean mostrarUltimaParteDim2) {
        Dimension dimension1 = cubo.getDimension(nombreDim1);
        Dimension dimension2 = cubo.getDimension(nombreDim2);
        System.out.println("Proyección Pivot de " + dimension1.getNombre() + " en nivel " + dimension1.getNivelActual()
                + " y " + dimension2.getNombre() + " en nivel " + dimension2.getNivelActual() + " - "
                + hecho + " (" + medida.getNombre() + ")");

        String[] valoresDim1 = dimension1.getValoresNivel(dimension1.getNivelActual());
        String[] valoresDim2 = dimension2.getValoresNivel(dimension2.getNivelActual());

        Arrays.sort(valoresDim1);
        Arrays.sort(valoresDim2);

        // Combinar valores de ambas dimensiones
        String[] valoresCombinados = new String[valoresDim1.length * valoresDim2.length];
        int k = 0;
        for (String valorDim1 : valoresDim1) {
            for (String valorDim2 : valoresDim2) {
                valoresCombinados[k++] = valorDim1 + SEPARADOR_DIM + valorDim2;
            }
        }

        // Calcular valores para la tabla pivote
        Double[] valores = new Double[valoresCombinados.length];
        for (int i = 0; i < valoresCombinados.length; i++) {
            String[] valoresSeparados = valoresCombinados[i].split(SEPARADOR_DIM);
            Celda celdaAgrupada = cubo.getCelda(dimension1, valoresSeparados[0], dimension2, valoresSeparados[1]);
            valores[i] = medida.calcular(celdaAgrupada.getValores(hecho));
        }

        printTablaPivot(valoresCombinados, hecho, valores, mostrarUltimaParteDim1, mostrarUltimaParteDim2);
    }

    private void printTablaPivot(String[] indice, String header, Double[] valores, boolean mostrarUltimaParteDim1,
            boolean mostrarUltimaParteDim2) {
        if (indice.length > maxFilas) {
            indice = Arrays.copyOfRange(indice, 0, maxFilas);
        }

        int longitudSeparador = 20 + SEPARADOR.length() + 20;
        char[] separadorArray = new char[longitudSeparador];
        Arrays.fill(separadorArray, '-');
        String lineaSeparadora = new String(separadorArray);

        // Encabezado con el hecho
        System.out.printf(FORMATO_CELDA, "");
        System.out.print(SEPARADOR);
        System.out.printf(FORMATO_CELDA, header);
        System.out.println(" |");
        System.out.println(lineaSeparadora);

        // Filas con índice y valor
        for (int i = 0; i < indice.length; i++) {
            if (valores[i] != null && valores[i] != 0.0) {
                String[] partesIndice = indice[i].split(SEPARADOR_DIM);
                String dim1 = mostrarUltimaParteDim1 ? obtenerUltimaParte(partesIndice[0]) : partesIndice[0];
                String dim2 = mostrarUltimaParteDim2 ? obtenerUltimaParte(partesIndice[1]) : partesIndice[1];

                System.out.printf(FORMATO_CELDA_IZQUIERDA, dim1 + " | " + dim2);
                System.out.print(SEPARADOR);

                String valorFormateado = String.format("%.2f", valores[i]);
                System.out.printf(FORMATO_CELDA, valorFormateado);
                System.out.println(" |");
            }
        }
    }

    public void printPivotAllHechos(String nombreDim1, String nombreDim2) {
        printPivotAllHechos(nombreDim1, nombreDim2, false, false);
    }

    public void printPivotAllHechos(String nombreDim1, String nombreDim2, boolean mostrarUltimaParteDim1,
            boolean mostrarUltimaParteDim2) {
        Dimension dimension1 = cubo.getDimension(nombreDim1);
        Dimension dimension2 = cubo.getDimension(nombreDim2);
        System.out.println("Proyección Pivot de " + dimension1.getNombre() + " en nivel " + dimension1.getNivelActual()
                + " y " + dimension2.getNombre() + " en nivel " + dimension2.getNivelActual()
                + " - Todos los Hechos");

        String[] valoresDim1 = dimension1.getValoresNivel(dimension1.getNivelActual());
        String[] valoresDim2 = dimension2.getValoresNivel(dimension2.getNivelActual());

        Arrays.sort(valoresDim1);
        Arrays.sort(valoresDim2);

        // Combinar valores de ambas dimensiones
        String[] valoresCombinados = new String[valoresDim1.length * valoresDim2.length];
        int k = 0;
        for (String valorDim1 : valoresDim1) {
            for (String valorDim2 : valoresDim2) {
                valoresCombinados[k++] = valorDim1 + SEPARADOR_DIM + valorDim2;
            }
        }

        List<String> nombresHechos = cubo.getNombresHechos();

        // Calcular valores para la tabla pivote
        Double[][] valores = new Double[valoresCombinados.length][nombresHechos.size()];
        for (int i = 0; i < valoresCombinados.length; i++) {
            String[] valoresSeparados = valoresCombinados[i].split(SEPARADOR_DIM);
            Celda celdaAgrupada = cubo.getCelda(dimension1, valoresSeparados[0], dimension2, valoresSeparados[1]);
            for (int j = 0; j < nombresHechos.size(); j++) {
                String hecho = nombresHechos.get(j);
                Medida medida = medidasPorHecho.getOrDefault(hecho, this.medida);
                valores[i][j] = medida.calcular(celdaAgrupada.getValores(hecho));
            }
        }

        printTablaPivotAllHechos(valoresCombinados, nombresHechos.toArray(new String[0]), valores,
                mostrarUltimaParteDim1, mostrarUltimaParteDim2);
    }

    private void printTablaPivotAllHechos(String[] indice, String[] headers, Double[][] valores,
            boolean mostrarUltimaParteDim1, boolean mostrarUltimaParteDim2) {
        if (indice.length > maxFilas) {
            indice = Arrays.copyOfRange(indice, 0, maxFilas);
        }

        int longitudSeparador = 20 + SEPARADOR.length() * (headers.length + 1) + 20 * headers.length;
        char[] separadorArray = new char[longitudSeparador];
        Arrays.fill(separadorArray, '-');
        String lineaSeparadora = new String(separadorArray);

        // Encabezados con todos los hechos
        System.out.printf(FORMATO_CELDA, "");
        for (String header : headers) {
            System.out.print(SEPARADOR);
            System.out.printf(FORMATO_CELDA, header);
        }
        System.out.println(" |");

        // Línea con nombres de medidas
        System.out.printf(FORMATO_CELDA, "");
        for (String header : headers) {
            System.out.print(SEPARADOR);
            Medida medida = medidasPorHecho.getOrDefault(header, this.medida);
            System.out.printf(FORMATO_CELDA, "(" + medida.getNombre() + ")");
        }
        System.out.println(" |");

        System.out.println(lineaSeparadora);

        // Filas con índice y valores
        for (int i = 0; i < indice.length; i++) {
            if (Arrays.stream(valores[i]).anyMatch(val -> val != null && val != 0.0)) {
                String[] partesIndice = indice[i].split(SEPARADOR_DIM);
                String fecha = mostrarUltimaParteDim1 ? obtenerUltimaParte(partesIndice[0]) : partesIndice[0];
                String producto = mostrarUltimaParteDim2 ? obtenerUltimaParte(partesIndice[1]) : partesIndice[1];

                System.out.printf(FORMATO_CELDA_IZQUIERDA, fecha + " | " + producto);
                for (int j = 0; j < headers.length; j++) {
                    System.out.print(SEPARADOR);
                    String valorFormateado = valores[i][j] != null && valores[i][j] != 0.0
                            ? String.format("%.2f", valores[i][j])
                            : ""; // Mostrar vacío si es cero o null
                    System.out.printf(FORMATO_CELDA, valorFormateado);
                }
                System.out.println(" |");
            }
        }
    }

    private String obtenerUltimaParte(String valor) {
        if (valor == null || valor.isEmpty()) {
            return valor;
        }
        String[] partes = valor.split("/");
        return partes[partes.length - 1];
    }
}
