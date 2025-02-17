package models;

import edumdev.DataAccessObject;

import java.sql.SQLException;
import java.util.ArrayList;

public class Stock {
    private int id;
    private int productId;
    private int quantity;

    public Stock(int id, int productId, int quantity) {
        this.id = id;
        this.productId = productId;
        this.quantity = quantity;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "Stock{" + "id=" + id + ", productId=" + productId + ", quantity=" + quantity + '}';
    }

    public static void updateStock(int productId, int quantity) throws SQLException {
        ArrayList<String> values = new ArrayList<>(){
            {
                add(productId+"");
                add(quantity+"");
            }};
        DataAccessObject.executeQueryValues("UPDATE stock_tbl SET iCantidad = ? WHERE idProducto = ?", values);
    }

    public static boolean checkStock(int productId) throws SQLException {
        ArrayList<String> values = new ArrayList<>(){
            {
                add(productId+"");
            }};
        String res = DataAccessObject.executeQueryValues("SELECT * FROM stock_tbl WHERE idProducto = ?", values);
        return !res.isEmpty();
    }

    public static String removeStock(int productId) throws SQLException {
        ArrayList<String> values = new ArrayList<>(){
            {
                add(productId+"");
            }};
        return DataAccessObject.executeQueryValues("DELETE FROM stock_tbl WHERE idProducto = ?", values);
    }

    public static boolean removeAllStock() throws SQLException {
        try {
            DataAccessObject.executeQueryNoValues("DELETE FROM stock_tbl");
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    public static boolean alertLowStock(int productId) throws SQLException {
        ArrayList<String> values = new ArrayList<>(){
            {
                add(productId+"");
            }};
        String res = DataAccessObject.executeQueryValues("SELECT iCantidad FROM stock_tbl WHERE idProducto = ?", values);
        String[] row = res.split(" ");
        return Integer.parseInt(row[0]) <= 5;
    }

    public static String getItemsStock() throws SQLException {
        return DataAccessObject.executeQueryNoValues("SELECT * FROM stock_tbl");
    }

    public static String getItemStock(int productId) throws SQLException {
        ArrayList<String> values = new ArrayList<>(){
            {
                add(productId+"");
            }};
        return DataAccessObject.executeQueryValues("SELECT iCantidad FROM stock_tbl WHERE idProducto = ?", values);
    }
}
