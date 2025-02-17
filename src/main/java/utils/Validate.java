package utils;

public class Validate {
    private static final String DOUBLE_REGEX = "-?\\d*\\.?\\d+";

    /**
     * Verifica si una cadena dada representa un número (entero o decimal).
     *
     * @param str La cadena a verificar.
     * @return true si la cadena representa un número válido, de lo contrario, false.
     *
     * @since 16
     */
    public static boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");
    }

    /**
     * Verifica si una cadena dada representa un número entero.
     *
     * @param str La cadena a verificar.
     * @return true si la cadena representa un número entero válido, de lo contrario, false.
     *
     * @since 16
     */
    public static boolean isInteger(String str) {
        return str.matches("-?\\d+");
    }

    /**
     * Verifica si una cadena dada representa un número decimal (double).
     *
     * @param str La cadena a verificar.
     * @return true si la cadena es nula, está vacía o no representa un número decimal válido, de lo contrario, false.
     *
     * @since 16
     */
    public static boolean isDouble(String str) {
        if (str == null || str.trim().isEmpty()) {
            return true;
        }
        return !str.matches(DOUBLE_REGEX);
    }

    /**
     * Verifica si una cadena dada contiene solo caracteres alfabéticos (letras mayúsculas o minúsculas).
     *
     * @param str La cadena a verificar.
     * @return true si la cadena contiene únicamente letras (a-z, A-Z), de lo contrario, false.
     *
     * @since 16
     */
    public static boolean isString(String str) {
        return str.matches("[a-zA-Z]+");
    }

    /**
     * Verifica si una cadena dada representa un valor booleano ("true" o "false").
     *
     * @param str La cadena a verificar.
     * @return true si la cadena es "true" o "false", de lo contrario, false.
     *
     * @since 16
     */
    public static boolean isBoolean(String str) {
        return str.matches("true|false");
    }

    /**
     * Verifica si una cadena dada representa una fecha válida en uno de los formatos soportados: "dd/MM/yyyy" o "yyyy-MM-dd".
     *
     * @param str La cadena que se desea verificar.
     * @return true si la cadena coincide con alguno de los formatos de fecha válidos, de lo contrario, false.
     *
     * @since 16
     */
    public static boolean isValidDate(String str) {
        return str.matches("\\d{2}/\\d{2}/\\d{4}") || str.matches("\\d{4}-\\d{2}-\\d{2}");
    }
}
