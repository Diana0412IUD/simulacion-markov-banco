import java.util.Random;

public class SimulacionBanco {

    static Random random = new Random();

    // Parámetros de la simulación
    static final int NUM_SIMULACIONES = 10000; // Número de simulaciones
    static final int DURACION = 480;           // Minutos por simulación (8 horas)
    static final double LAMBDA = 2.0;          // Promedio de llegadas por minuto

    public static void main(String[] args) {

        double sumaClientes = 0;
        int tiempoVacio = 0;
        int tiempoCongestion = 0;
        int maxClientes = 0;

        // Ejecutar todas las simulaciones
        for (int sim = 0; sim < NUM_SIMULACIONES; sim++) {

            int clientesEnFila = 0;
            int tiempoServicio = 0; // Tiempo restante del servicio actual

            // Simular minuto a minuto
            for (int minuto = 0; minuto < DURACION; minuto++) {

                // 1. Generar número de clientes que llegan este minuto
                int llegadas = generarPoisson(LAMBDA);
                clientesEnFila += llegadas;

                // 2. Si el cajero está libre y hay clientes en fila, iniciar atención
                if (tiempoServicio == 0 && clientesEnFila > 0) {
                    clientesEnFila--;
                    tiempoServicio = generarUniforme(2, 5);
                }

                // 3. Reducir el tiempo restante del servicio actual
                if (tiempoServicio > 0) {
                    tiempoServicio--;
                }

                // 4. Si terminó el servicio y aún hay clientes, atender al siguiente
                if (tiempoServicio == 0 && clientesEnFila > 0) {
                    clientesEnFila--;
                    tiempoServicio = generarUniforme(2, 5);
                }

                // 5. Total de clientes en el sistema:
                //    clientes en fila + 1 si el cajero está ocupado
                int totalClientes = clientesEnFila + (tiempoServicio > 0 ? 1 : 0);

                // 6. Acumular estadísticas
                sumaClientes += totalClientes;

                if (totalClientes == 0) {
                    tiempoVacio++;
                }

                if (totalClientes >= 3) {
                    tiempoCongestion++;
                }

                if (totalClientes > maxClientes) {
                    maxClientes = totalClientes;
                }
            }
        }

        // Total de observaciones realizadas
        int totalObservaciones = NUM_SIMULACIONES * DURACION;

        // Calcular indicadores finales
        double promedioClientes = sumaClientes / totalObservaciones;
        double probabilidadVacio = (100.0 * tiempoVacio) / totalObservaciones;
        double probabilidadCongestion = (100.0 * tiempoCongestion) / totalObservaciones;

        // Mostrar resultados
        System.out.println("RESULTADOS DE LA SIMULACIÓN");
        System.out.println("--------------------------------------");
        System.out.println("Número de simulaciones: " + NUM_SIMULACIONES);
        System.out.println("Duración por simulación: " + DURACION + " minutos");
        System.out.printf("Promedio de clientes en el sistema: %.2f%n", promedioClientes);
        System.out.printf("Probabilidad de sistema vacío: %.2f%%%n", probabilidadVacio);
        System.out.printf("Probabilidad de congestión (3 o más clientes): %.2f%%%n",
                probabilidadCongestion);
        System.out.println("Máximo número de clientes observado: " + maxClientes);
    }

    /**
     * Genera un número aleatorio con distribución de Poisson.
     *
     * @param lambda media de la distribución
     * @return número de eventos generados
     */
    public static int generarPoisson(double lambda) {
        double L = Math.exp(-lambda);
        int k = 0;
        double p = 1.0;

        do {
            k++;
            p *= random.nextDouble();
        } while (p > L);

        return k - 1;
    }

    /**
     * Genera un número entero aleatorio uniforme entre min y max (inclusive).
     *
     * @param min valor mínimo
     * @param max valor máximo
     * @return número aleatorio entre min y max
     */
    public static int generarUniforme(int min, int max) {
        return random.nextInt(max - min + 1) + min;
    }
}