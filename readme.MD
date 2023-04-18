# Algoritmos para la detección de colisiones

Autor: Sergio Martí Torregrosa.

## Contenido:

El proyecto contiene dos carpetas principales: "main" y "test":

Dentro de la carpeta "main", encontramos los siguientes paquetes:

- *base*: contiene las clases necesarias para lanzar una aplicación gráfica JavaFX.

- *panAndZoom*: contiene las clases que implementan la funcionalidad de hacer zoom y moverse por el mundo.

- *physics*: contiene la implementación del "QuadTree" y de "Grid", además de las clases que representan objetos como rectángulos y círculos.

Dentro de la carpeta "test", encontramos el código que testea cada algoritmo.

- *SweepAndPruneGame*: esta clase implementa los algoritmos de "*All vs All*", "todos vs todos" o búsqueda exhaustiva, y el algoritmo de "*Sweep And Prune*" (SAP). Se puede cambiar el algoritmo utilizado pulsando TAB.

- *GridCircleGame*: esta clase implementa el algoritmo de división del espacio mediante el uso de una rejilla o "*grid*".

- *QuadTreeCircleGame*: esta clase implementa el "*QuadTree*" para la división del espacio. Muestra el mejor rendimiento

- *GameTester*: esta clase es la aplicación ejecutable. Si se quiere cambiar de simulación, descomentar la línea que se quiera probar. 

```java
public class GameTester extends GameApplication {

    @Override
    public void init() throws Exception {
        super.init();
        setAppName("Test collisions");
        setGame(new QuadTreeCircleGame());
        // setGame(new QuadTreeRectGame());
        // setGame(new GridCircleGame());
        // setGame(new SweepAndPruneGame());
    }
}
```