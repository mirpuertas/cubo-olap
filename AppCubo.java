
import java.util.List;

import olapcube.Proyeccion;
import olapcube.configuration.ConfigCubo;
import olapcube.configuration.ConfigDimension;
import olapcube.configuration.ConfigHechos;
import olapcube.estructura.Cubo;
import olapcube.metricas.RegistroMedida;

public class AppCubo {

        private static final String[] NOMBRES_HECHOS = new String[] { "cantidad", "valor_unitario", "valor_total",
                        "costo" };
        private static final Integer[] COLUMNAS_HECHOS = new Integer[] { 3, 4, 5, 6 };

        private static final List<Integer> COL_DIM_PRODUCTOS = List.of(3, 2, 1);

        private static final List<Integer> COL_DIM_FECHAS = List.of(5, 4, 3, 2, 1);

        private static final List<Integer> COL_DIM_POS = List.of(5, 4, 3, 2, 1);

        private static ConfigCubo crearConfigCubo() {
                return new ConfigCubo(
                                "Cubo de ventas",
                                ConfigHechos.configCSV(
                                                "src/olapcube/datasets-olap/ventas.csv",
                                                NOMBRES_HECHOS,
                                                COLUMNAS_HECHOS),
                                new ConfigDimension[] {
                                                ConfigDimension.configCSV("Productos",
                                                                "src/olapcube/datasets-olap/productos.csv", 0, 0,
                                                                COL_DIM_PRODUCTOS),
                                                ConfigDimension.configCSV("Fechas",
                                                                "src/olapcube/datasets-olap/fechas.csv", 0, 2,
                                                                COL_DIM_FECHAS),
                                                ConfigDimension.configCSV("POS",
                                                                "src/olapcube/datasets-olap/puntos_venta.csv", 0, 1,
                                                                COL_DIM_POS)
                                });
        }

        public static void main(String[] args) {
                ConfigCubo config = crearConfigCubo();
                Cubo cubo = Cubo.crearFromConfig(config);

                System.out.println("Cubo creado: " + cubo);
                Proyeccion proyeccion = cubo.proyectar();

                // Medidas disponibles para el usuario
                RegistroMedida.getMedidas().forEach((k, v) -> System.out.println(k + " -> " +
                                v.getNombre()));

                // Método posible de usar para saber Dimensión y su nivel
                // cubo.printDimensionYNivel("Fechas");

                // Descomentar para probar: Slice en un nivel más detallado
                cubo.drillDown("Fechas");
                Cubo subCubo = cubo.slice("Fechas", "2017/3");
                Proyeccion subProyeccion = subCubo.proyectar();
                subProyeccion.seleccionarHecho("cantidad");
                subProyeccion.seleccionarMedida("Count");
                subProyeccion.printPivot("Fechas", "Productos", false, true);

                // Descomentar para probar: Dice en un nivel más detallado
                // cubo.drillDown("Fechas");
                // Cubo subCubo2 = cubo.dice("Fechas", new String[] { "2017/3", "2017/4" },
                // "Productos",
                // new String[] { "Clothing", "Components" });
                // Proyeccion subProyeccion2 = subCubo2.proyectar();
                // subProyeccion2.seleccionarHecho("cantidad");
                // subProyeccion2.seleccionarMedida("Count");

                // subProyeccion2.printPivot("Fechas", "Productos", false, true);

                // Descomentar para probar
                // Cubo cubo2 = Cubo.crearFromConfig(config);
                // System.out.println("Cubo creado: " + cubo);
                // Proyeccion proyeccion2 = cubo2.proyectar();
                // cubo2.drillDown("Fechas");
                // cubo2.drillDown("Productos");
                // cubo2.drillDown("Productos");
                // proyeccion2.seleccionarMedidaPorHecho("cantidad", "Count");
                // proyeccion2.seleccionarMedidaPorHecho("costo", "Suma");
                // proyeccion2.seleccionarMedidaPorHecho("valor_total", "Suma");
                // proyeccion2.seleccionarMedidaPorHecho("valor_unitario", "Suma");
                // proyeccion2.printPivot("Fechas", "Productos", false, true);
                // proyeccion2.printPivotAllHechos("Fechas", "Productos", false, true);

        }
}