# 🧊 Cubo OLAP en Java

Este proyecto implementa desde cero un **cubo OLAP** en Java como trabajo final de la materia **Algoritmos I** (1.º Cuatrimestre 2024). 

El sistema permite almacenar datos multidimensionales, aplicar medidas agregadas, y consultar resultados por combinaciones de dimensiones.

## Funcionalidades

- Construcción de un cubo OLAP con múltiples dimensiones (Producto, Fecha, Punto de Venta)
- Lectura desde archivos `.csv` de hechos y dimensiones
- Agregación con medidas como:
  - Suma
  - Mínimo
  - Máximo
  - Conteo
- Modularidad total: las medidas son clases independientes
- Configuración flexible mediante clases `ConfigHechos`, `ConfigDimension`, `ConfigCubo`


## Estructura del proyecto
````
cubo-olap/
├── src/ # Código fuente Java
│ ├── olapcube/
│ │ ├── estructura/ # Cubo, Celda, Dimensiones
│ │ ├── configuration/ # Configuración del cubo y dimensiones
│ │ ├── metricas/ # Medidas agregadas (Suma, Max, etc.)
│ │ ├── datasets-olap/ # CSV de entrada
│ │ └── AppCubo.java # Clase principal (main)
````
## Ejecución

### Requisitos

- Java 11 o superior
- Editor recomendado: Visual Studio Code o IntelliJ IDEA

### Cómo correrlo

1. Clonar el repositorio
2. Abrir el proyecto en tu IDE
3. Ejecutar la clase `AppCubo.java`

El sistema cargará los datasets y mostrará resultados de agregación en consola.

## Datasets incluidos

- `ventas.csv` — hechos: cantidad, valor unitario, valor total, costo
- `productos.csv` — jerarquía de producto (ej. categoría → subcategoría)
- `fechas.csv` — jerarquía temporal (año → mes → día)
- `puntos_venta.csv` — jerarquía geográfica (país → provincia → ciudad)

## Autores

- Mateo Panico (coautor en la etapa inicial del proyecto)
- Miguel Ignacio Rodríguez Puertas — mantenimiento actual y evolución del código  
 [GitHub](https://github.com/mirpuertas)

