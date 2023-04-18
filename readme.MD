# Algoritmos para la detección de colisiones

Implementación de algoritmos para la detección de colisiones.

Autor: Sergio Martí Torregrosa.

## Contenido

El proyecto contiene dos carpetas principales: "main" y "test":

Dentro de la carpeta "main", encontramos los siguientes paquetes:

- *base*: contiene las clases necesarias para lanzar una aplicación gráfica JavaFX.

- *panAndZoom*: contiene las clases que implementan la funcionalidad de hacer zoom y moverse por el mundo.

- *physics*: contiene la implementación del "*QuadTree*" y de "*Grid*", además de las clases que representan objetos como rectángulos y círculos.

Dentro de la carpeta "test", encontramos el código que testea cada algoritmo.

- *SweepAndPruneGame*: esta clase implementa los algoritmos de "*All vs All*", "todos vs todos" o búsqueda exhaustiva, y el algoritmo de "*Sweep And Prune*" (SAP).

- *GridCircleGame*: esta clase implementa el algoritmo de división del espacio mediante el uso de una rejilla o "*grid*".

- *QuadTreeCircleGame*: esta clase implementa el "*QuadTree*" para la división del espacio. Muestra el mejor rendimiento para la detección de colisiones.

- *GameTester*: esta clase es la aplicación ejecutable. Si se quiere cambiar de simulación, descomentar la línea que se quiera probar. 

```java
public class GameTester extends GameApplication {

    @Override
    public void init() throws Exception {
        super.init();
        setAppName("Test collisions");
        setGame(new QuadTreeCircleGame());
        // setGame(new GridCircleGame());
        // setGame(new SweepAndPruneGame());
    }
}
```

## Controles

Utilizar el botón de la rueda del ratón para desplazarse por el plano 2D. Utilizar la rueda del ratón para acercarse o alejarse.

El botón izquierdo del ratón sirve para añadir más pelotas en la posición del cursor. El botón secundario del ratón sirve para seleccionar y darle velocidad a una pelota, como si fuera un billar.

La tecla del espacio se utiliza para parar la simulación o reanudarla.

En la simulación "*SweepAndPruneGame*", se puede cambiar el algoritmo utilizado pulsando tabulador. En las demás simulaciones, sirve para mostrar o esconder la estructura que divide el espacio.

En las simulaciones de "*GridCircleGame*" y "*QuadTreeCircleGame*", las teclas "Q" y "A" sirven para aumentar o disminuir el área de borrado. Para borrar, pulsar suprimir. Se eliminarán los objetos que se encuentren debajo del área de selección.

