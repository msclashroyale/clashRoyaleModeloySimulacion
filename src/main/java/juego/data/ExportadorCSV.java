package juego.data;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * Clase de utilidad para exportar una lista de registros a un fichero CSV.
 */
public class ExportadorCSV {

    /**
     * Exporta una lista de objetos RegistroCSV a un fichero.
     *
     * @param registros La lista de registros a exportar.
     * @param rutaArchivo La ruta del fichero de salida.
     */
    public static void exportar(List<RegistroCSV> registros, String rutaArchivo) {
        try (PrintWriter out = new PrintWriter(new FileWriter(rutaArchivo))) {
            // Escribir la cabecera
            out.println(RegistroCSV.getHeader());

            // Escribir cada registro
            for (RegistroCSV registro : registros) {
                out.println(registro.toString());
            }

            System.out.println("Exportación a CSV completada con éxito. Fichero: " + rutaArchivo);

        } catch (IOException e) {
            System.err.println("Error al exportar a CSV: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
