package edumdev;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;

public class DataAccessObject {
    private static final String ERRORMSG = "Error: ";

     /**
     * Ejecuta una consulta SQL SELECT sin valores de parámetro y devuelve el resultado como una cadena.
     * Los resultados de la consulta se concatenan fila por fila, con las columnas separadas por espacios y las filas por saltos de línea.
     *
     * @param query la consulta SQL SELECT que se ejecutará. La consulta debe comenzar con la palabra clave "SELECT".
     * @return una cadena que contiene los resultados de la consulta, formateados con columnas separadas por espacios y filas por saltos de línea.
     * @throws SQLException si la consulta no es una declaración SELECT, si ocurre un error en la base de datos,
     *                      o si no se puede establecer la conexión con la base de datos.
     */
    public static String executeQueryNoValues(String query) throws SQLException {
        if (!query.trim().toUpperCase().startsWith("SELECT")) {
            throw new SQLException("Este método solo admite consultas SELECT.");
        }

        StringBuilder result = new StringBuilder();
        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) {
                throw new SQLException("No se pudo establecer una conexión con la base de datos.");
            }
            try (PreparedStatement pstmt = conn.prepareStatement(query);
                 ResultSet rs = pstmt.executeQuery()) {
                int columnCount = rs.getMetaData().getColumnCount();
                while (rs.next()) {
                    for (int i = 1; i <= columnCount; i++) {
                        result.append(rs.getString(i)).append(" ");
                    }
                    result.append("\n");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error ejecutando la consulta: " + e.getMessage());
            throw new SQLException("Se produjo un error al ejecutar una consulta.", e);
        }
        return result.toString();
    }

     /**
     * Ejecuta una consulta con valores especificados y devuelve el resultado.
     * Si la consulta es una declaración SELECT, el resultado será una representación en cadena
     * de la salida de la consulta. Para otros tipos de consulta, el método devuelve
     * un mensaje que indica el número de filas afectadas.
     *
     * @param query la consulta SQL a ejecutar, puede ser una declaración SELECT o una de actualización/insertar/eliminar
     * @param values una lista de valores que se insertarán en los parámetros de la consulta
     * @return una cadena que contiene el resultado de la consulta SELECT o un mensaje que indica el número de filas afectadas
     * @throws SQLException si ocurre un error de acceso a la base de datos o si la consulta falla
     */
    public static String executeQueryValues(String query, ArrayList<String> values) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            for (int i = 0; i < values.size(); i++) {
                pstmt.setString(i + 1, values.get(i));
            }

            if (query.trim().toUpperCase().startsWith("SELECT")) {
                try (ResultSet rs = pstmt.executeQuery()) {
                    StringBuilder result = new StringBuilder();
                    while (rs.next()) {
                        for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                            result.append(rs.getString(i)).append(" ");
                        }
                        result.append("\n");
                    }
                    return result.toString().trim();
                }
            } else {
                int affectedRows = pstmt.executeUpdate();
                return "Filas afectadas: " + affectedRows;
            }
        } catch (SQLException e) {
            System.err.println("Error ejecutando la consulta: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Ejecuta una consulta SQL para buscar registros en una tabla especificada donde los valores combinados de múltiples columnas
     * coincidan con una cadena de consulta dada utilizando una cláusula LIKE. Las columnas para la comparación se determinan dinámicamente, y 
     * el método devuelve el resultado de la consulta como una cadena.
     *
     * @param tableName el nombre de la tabla de la base de datos a consultar. No debe ser nulo o vacío y debe coincidir con el patrón [a-zA-Z0-9_]+.
     * @param values una lista de nombres de columnas a incluir en la búsqueda. Cada nombre debe coincidir con el patrón [a-zA-Z0-9_]+.
     * @param query la cadena de búsqueda para coincidir con las columnas concatenadas utilizando una cláusula LIKE.
     * @return una representación en cadena de los resultados de la consulta, formateada fila por fila. Si no se encuentran resultados, devuelve "Sin resultados".
     * @throws IllegalArgumentException si tableName es nulo, vacío, o inválido, o si algún nombre de columna en values es inválido.
     * @throws SQLException si ocurre un error de acceso a la base de datos o si la ejecución de la consulta falla.
     */
    public static String executeQueryValuesLike(String tableName, ArrayList<String> values, String query) throws SQLException {
        if (tableName == null || tableName.isEmpty() || !tableName.matches("[a-zA-Z0-9_]+")) {
            throw new IllegalArgumentException("Nombre de tabla inválido.");
        }

        StringBuilder val = new StringBuilder();
        for (String value : values) {
            if (!value.matches("[a-zA-Z0-9_]+")) {
                throw new IllegalArgumentException("Nombre de columna inválido: " + value);
            }
            if (!val.isEmpty()) {
                val.append(", ");
            }
            val.append(value);
        }

        String stmt = String.format("SELECT * FROM %s WHERE CONCAT_WS(', ', %s) LIKE ?", tableName, val);

        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) {
                throw new SQLException("No se pudo establecer conexión con la base de datos.");
            }

            try (PreparedStatement pstmt = conn.prepareStatement(stmt)) {
                pstmt.setString(1, "%" + query + "%");

                try (ResultSet rs = pstmt.executeQuery()) {
                    StringBuilder result = new StringBuilder();
                    boolean hasResults = false;

                    while (rs.next()) {
                        hasResults = true;
                        for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                            result.append(rs.getString(i)).append(" ");
                        }
                        result.append("\n");
                    }

                    if (!hasResults) {
                        return "Sin resultados";
                    }

                    return result.toString();
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Error ejecutando la consulta: " + e.getMessage(), e);
        }
    }

    /**
     * Ejecuta una declaración SQL INSERT para agregar datos en una tabla especificada.
     * El método construye dinámicamente la consulta basada en los nombres de columnas y valores proporcionados.
     *
     * @param columnNames una lista de nombres de columnas donde se insertarán los datos. No debe ser nula y debe coincidir con el tamaño de la lista de valores.
     * @param values una lista de valores correspondientes a los nombres de columnas. No debe ser nula y debe coincidir con el tamaño de la lista de nombres de columnas.
     * @param tableName el nombre de la tabla en la que se insertarán los datos. No debe ser nulo o vacío.
     * @throws IllegalArgumentException si columnNames o values son nulos, o si sus tamaños no coinciden.
     * @throws SQLException si ocurre un error de acceso a la base de datos durante la ejecución de la consulta.
     */
    public static void executeInsert(ArrayList<String> columnNames, ArrayList<String> values, String tableName) throws SQLException {
        if (columnNames == null || values == null || columnNames.size() != values.size()) {
            throw new IllegalArgumentException("Los nombres de columnas y los valores no deben ser nulos y deben tener el mismo tamaño.");
        }

        StringBuilder columns = new StringBuilder();
        StringBuilder placeholders = new StringBuilder();

        for (int i = 0; i < columnNames.size(); i++) {
            if (i > 0) {
                columns.append(", ");
                placeholders.append(", ");
            }
            columns.append(columnNames.get(i));
            placeholders.append("?");
        }

        String str = "INSERT INTO " + tableName + " (%s) VALUES (%s)";

        String query = String.format(str, columns, placeholders);

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            for (int i = 0; i < values.size(); i++) {
                pstmt.setString(i + 1, values.get(i));
            }

            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error ejecutando la consulta: " + e.getMessage());
            throw e;
        }
    }

     /**
     * Ejecuta una consulta SQL con parámetros especificados y recupera un único resultado entero.
     * Se espera que la consulta devuelva solo una fila con una columna, que debe ser un entero válido.
     *
     * @param query la consulta SQL a ejecutar, generalmente una instrucción SELECT.
     * @param parameters una lista de valores de parámetros que se sustituirán en la consulta.
     * @return el resultado entero de la consulta.
     * @throws SQLException si ocurre un error de acceso a la base de datos, el resultado no es un entero válido,
     *                      o la consulta no devuelve un resultado.
     */
    public static int executeSingleIntQuery(String query, ArrayList<String> parameters) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            for (int i = 0; i < parameters.size(); i++) {
                pstmt.setString(i + 1, parameters.get(i));
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String result = rs.getString(1);
                    if (!result.matches("\\d+")) {
                        throw new SQLException("Resultado no es un número válido: " + result);
                    }
                    return Integer.parseInt(result);
                } else {
                    throw new SQLException("No se encontraron resultados para la consulta.");
                }
            }

        } catch (SQLException e) {
            System.err.println("Error ejecutando la consulta: " + e.getMessage());
            throw e;
        }
    }

    public static byte[] executeQueryBytes(String query) throws SQLException {
        if (!query.trim().toUpperCase().startsWith("SELECT")) {
            throw new SQLException("Este método solo admite consultas SELECT.");
        }

        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) {
                throw new SQLException("No se pudo establecer una conexión con la base de datos.");
            }
            try (PreparedStatement pstmt = conn.prepareStatement(query);
                 ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBytes("pixel");
                }
                throw new SQLException("No se encontró el icono en la base de datos.");
            }
        } catch (SQLException e) {
            System.err.println("Error ejecutando la consulta: " + e.getMessage());
            throw new SQLException("Se produjo un error al ejecutar una consulta.", e);
        }
    }
}
