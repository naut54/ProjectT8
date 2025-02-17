package models;

import edumdev.DBConnection;
import edumdev.DataAccessObject;
import utils.Format;

import java.sql.*;
import java.util.ArrayList;

public class Product {
    private int id;
    private String name;
    private String description;
    private double price;
    private int idCategory;
    private int productCode;

    public Product(int id, String name, String description, double price, int idCategory, int productCode) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.idCategory = idCategory;
        this.productCode = productCode;
    }

    public Product(String name, String description, double price, int productCode, int idCategory) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.productCode = productCode;
        this.idCategory = idCategory;
    }

    public Product() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getIdCategory() {
        return idCategory;
    }

    public void setIdCategory(int idCategory) {
        this.idCategory = idCategory;
    }

    public int getProductCode() {
        return productCode;
    }

    public void setProductCode(int productCode) {
        this.productCode = productCode;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", idCategory=" + idCategory +
                ", productCode=" + productCode +
                '}';
    }

    public static void addItem(Product product) throws SQLException {
        ArrayList<String> values = new ArrayList<>(){
            {
                add(product.getName());
                add(product.getDescription());
                add(product.getPrice()+"");
                add(product.getProductCode()+"");
                add(product.getIdCategory()+"");
            }
        };

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "INSERT INTO productos_tbl (`sNombre`, `sDescripcion`, `dPrecio`, `iCodigoProducto`, `idCategoria`) VALUES (?, ?, ?, ?, ?)",
                     Statement.RETURN_GENERATED_KEYS // IMPORTANTE: para recuperar la clave generada
             )) {

            for (int i = 0; i < values.size(); i++) {
                pstmt.setString(i + 1, values.get(i));
            }

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("La inserción en productos_tbl falló, no se afectaron filas.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int generatedId = generatedKeys.getInt(1);

                    try (PreparedStatement stockStmt = conn.prepareStatement(
                            "INSERT INTO stock_tbl (`idProducto`, `iCantidad`) VALUES (?, 0)"
                    )) {
                        stockStmt.setInt(1, generatedId);
                        stockStmt.executeUpdate();
                    }
                } else {
                    throw new SQLException("No se pudo obtener el ID generado para productos_tbl.");
                }
            }
        }
    }

    public static ArrayList<String> getItemsDetails(int id) throws SQLException {
        ArrayList<String> values = new ArrayList<>(){
            {
                add(id+"");
            }};
        String res = DataAccessObject.executeQueryValues("SELECT * FROM productos_tbl WHERE idProducto = ?", values);
        ArrayList<String> item = new ArrayList<>();
        for (int i = 0; i < res.length(); i++) {
            String[] row = res.split(" ");
            item.add(row[0]);
        }
        return item;
    }

    public static ArrayList<String> listAllItems() throws SQLException {
        String res = DataAccessObject.executeQueryNoValues("SELECT * FROM productos_tbl");
        ArrayList<String> items = new ArrayList<>();
        for (int i = 0; i < res.length(); i++) {
            String[] row = res.split(" ");
            items.add(row[0] + " " + row[1] + " " + row[2] + " " + row[3]);
        }
        return items;
    }

    public static ArrayList<String> listByCategory(int category) throws SQLException {
        ArrayList<String> values = new ArrayList<>(){
            {
                add(category+"");
            }};
        String res = DataAccessObject.executeQueryValues("SELECT * FROM productos_tbl WHERE idCategoria = ?", values);
        ArrayList<String> item = new ArrayList<>();
        for (int i = 0; i < res.length(); i++) {
            String[] row = res.split(" ");
            item.add(row[0]);
        }
        return item;
    }

    public static void updatePrice(double price, int id) throws SQLException {
        ArrayList<String> values = new ArrayList<>(){
            {
                add(price+"");
                add(id+"");
            }};
        DataAccessObject.executeQueryValues("UPDATE productos_tbl SET dPrecio = ? WHERE idProducto = ?", values);
    }

    public static void duplicatePrice(int id) throws SQLException {
        ArrayList<String> values = new ArrayList<>(){
            {
                add(id+"");
            }};
        String res = DataAccessObject.executeQueryValues("UPDATE productos_tbl SET dPrecio = (dPrecio*2) WHERE idProducto = ?", values);
        ArrayList<String> item = new ArrayList<>();
        for (int i = 0; i < res.length(); i++) {
            String[] row = res.split(" ");
            item.add(row[0]);
        }
    }

    public static void removeItem(int id) throws SQLException {
        ArrayList<String> values = new ArrayList<>(){
            {
                add(id+"");
            }};
        String res = DataAccessObject.executeQueryValues("DELETE FROM productos_tbl WHERE iCodigoProducto = ?", values);
        ArrayList<String> item = new ArrayList<>();
        for (int i = 0; i < res.length(); i++) {
            String[] row = res.split(" ");
        }
    }

    public static String removeAllItems() throws SQLException {
        try {
            DataAccessObject.executeQueryNoValues("DELETE * FROM productos_tbl");
        } catch (SQLException e) {
            return ("Error: " + e);
        }
        return "";
    }

    public static String activateItem(int id) throws SQLException {
        ArrayList<String> values = new ArrayList<>(){
            {
                add(id+"");
            }};
        return DataAccessObject.executeQueryValues("UPDATE productos_tbl SET bActivo = TRUE WHERE idProducto = ?", values);
    }

    public static String deactivateItem(int id) throws SQLException {
        ArrayList<String> values = new ArrayList<>(){
            {
                add(id+"");
            }};
        return DataAccessObject.executeQueryValues("UPDATE productos_tbl SET bActivo = FALSE WHERE idProducto = ?", values);
    }

    public static String modifyItem(String name, String description, double price, int productCode, int idCategory) throws SQLException {
        ArrayList<String> values = new ArrayList<>(){
            {
                add(name);
                add(description);
                add(price+"");
                add(productCode+"");
                add(idCategory+"");
            }};
        return DataAccessObject.executeQueryValues("UPDATE productos_tbl SET sNombre = ?, sDescripcion = ?, dPrecio = ?, iCodigoProducto = ?, idCategoria = ? WHERE idProducto = ?", values);
    }

    public static String searchItemByName(String query) throws SQLException {
        ArrayList<String> values = new ArrayList<>(){
            {
                add("sNombre");
                add("sDescripcion");
                add("dPrecio");
                add("iCodigoProducto");
                add("idCategoria");
            }
        };
        return DataAccessObject.executeQueryValuesLike("productos_tbl", values, query);
    }

    public static String searchItemById(int id) throws SQLException {
        ArrayList<String> values = new ArrayList<>(){
            {
                add(id+"");
            }};
        return DataAccessObject.executeQueryValues("SELECT * FROM productos_tbl WHERE idProducto = ?", values);
    }

    public static String searchItemByCategory(int category) throws SQLException {
        ArrayList<String> values = new ArrayList<>(){
            {
                add(category+"");
            }};
        return DataAccessObject.executeQueryValues("SELECT * FROM productos_tbl WHERE idCategoria = ?", values);
    }

    /**
     * Checks the existence of an item in the database and retrieves related information.
     *
     * @param id the unique identifier of the product to check
     * @return a formatted object containing a boolean indicating the existence of the product
     *         and the result of the query as a string
     * @throws SQLException if a database access error occurs or the query fails
     */
    public static Object checkItem(int id) throws SQLException {
        ArrayList<String> values = new ArrayList<>(){
            {
                add(id+"");
            }};
        String res = DataAccessObject.executeQueryValues("SELECT * FROM productos_tbl WHERE idProducto = ?", values);
        Object[] values2 = new Object[] {
                !res.isEmpty(),
                res
        };
        return Format.formatObject(values2);
    }

    public static String checkStatus(String str) throws SQLException {
        ArrayList<String> values = new ArrayList<>(){
            {
                add(str);
            }};
        String res = DataAccessObject.executeQueryValues("SELECT bActivo FROM productos_tbl WHERE iCodigoProducto = ?", values);
        if (res.equals("1")) {
            res = "Activo";
        } else if (res.equals("0")) {
            res = "Inactivo";
        }
        return res;
    }

    public static String getIdProduct(String name) throws SQLException {
        ArrayList<String> values = new ArrayList<>(){
            {
                add(name);
            }};
        String res = DataAccessObject.executeQueryValues("SELECT idProducto FROM productos_tbl WHERE sNombre = ?", values);
        return res.isEmpty() ? "0" : res;
    }
}
