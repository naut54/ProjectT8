package utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

public class Format {
    /**
     * Formatea un precio dado como un número decimal a una cadena con dos decimales seguido de un símbolo de euro (€).
     *
     * @param price El precio a formatear.
     * @return Una representación en cadena del precio formateado con dos decimales y el símbolo de euro (€).
     *
     * @since 16
     */
    public static String formatPrice(double price) {
        return String.format("%.2f€", price);
    }

    /**
     * Formatea un arreglo de objetos en una cadena sin corchetes.
     *
     * @param object El arreglo de objetos a formatear.
     * @return Una representación en cadena de los objetos, separada por comas y sin corchetes.
     *
     * @since 16
     */
    public static String formatObject(Object[] object) {
        return Arrays.toString(object).replaceAll("[\\[\\]]", "");
    }

    /**
     * Formatea una fecha dada en el formato "dd/MM/yyyy".
     *
     * @param date La fecha a formatear.
     * @return La fecha formateada como una cadena en el formato "dd/MM/yyyy".
     *
     * @since 16
     */
    public static String formatDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return date.format(formatter);
    }
}
