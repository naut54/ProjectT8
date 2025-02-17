package models;

import edumdev.DataAccessObject;

import java.sql.SQLException;
import java.util.ArrayList;

public class Sale {
    private int idVenta;
    private int idProducto;
    private int iCantidad;
    private double total;

    /**
     * Constructor de la clase Sale.
     * Crea una nueva venta con el ID de la venta, el ID del producto,
     * la cantidad vendida y el monto total de la venta.
     *
     * @param idVenta    El ID único de la venta.
     * @param idProducto El ID del producto asociado a la venta.
     * @param iCantidad  La cantidad de unidades vendidas.
     * @param total      El monto total de la venta.
     */
    public Sale(int idVenta, int idProducto, int iCantidad, double total) {
        this.idVenta = idVenta;
        this.idProducto = idProducto;
        this.iCantidad = iCantidad;
        this.total = total;
    }

    /**
     * Constructor de la clase Sale.
     * Crea una nueva venta con el ID del producto, la cantidad y el total.
     *
     * @param idProducto El ID del producto asociado a la venta.
     * @param iCantidad  La cantidad de unidades vendidas.
     * @param total      El monto total de la venta.
     */
    public Sale(int idProducto, int iCantidad, double total) {
        this.idProducto = idProducto;
        this.iCantidad = iCantidad;
        this.total = total;
    }

    public int getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(int idVenta) {
        this.idVenta = idVenta;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public int getiCantidad() {
        return iCantidad;
    }

    public void setiCantidad(int iCantidad) {
        this.iCantidad = iCantidad;
    }

    @Override
    public String toString() {
        return "Sale{" +
                "idVenta=" + idVenta +
                ", idProducto=" + idProducto +
                ", iCantidad=" + iCantidad +
                '}';
    }

    public static void recordSale(Sale sale) throws SQLException {
        ArrayList<String> values1 = new ArrayList<>(){
            {
                add(sale.getIdVenta()+"");
                add(sale.getIdProducto()+"");
                add(sale.getiCantidad()+"");
            }
        };
        ArrayList<String> values2 = new ArrayList<>(){
            {
                add(sale.getIdVenta()+"");
                add(sale.getIdProducto()+"");
                add(sale.getiCantidad()+"");
                add(Product.getItemsDetails(sale.getIdProducto()).getFirst());
                add(sale.getiCantidad()*Double.parseDouble(Product.getItemsDetails(sale.getIdProducto()).getFirst())+"");
            }
        };
        DataAccessObject.executeQueryValues("INSERT INTO ventas_tbl (`idVenta`, `idProducto`, `iCantidad`) VALUES (?, ?, ?)", values1);
        DataAccessObject.executeQueryValues("INSERT INTO detalle_ventas_tbl (`idVenta`, `idProducto`, `iCantidad`, `dPrecioUnitario`, `dTotal` VALUES (?, ?, ?, ?, ?))", values2);
    }

    public static void removeSale(int id) throws SQLException {
        ArrayList<String> values = new ArrayList<>(){
            {
                add(id+"");
            }};
        String res = DataAccessObject.executeQueryValues("DELETE FROM detalle_ventas_tbl WHERE idVenta = ?", values);
        res = DataAccessObject.executeQueryValues("DELETE FROM ventas_tbl WHERE idVenta = ?", values);
        ArrayList<String> item = new ArrayList<>();
    }

    public static ArrayList<String> getSalesHistory() throws SQLException {
        String res = DataAccessObject.executeQueryNoValues("SELECT * FROM ventas_tbl");
        ArrayList<String> sales = new ArrayList<>();
        for (int i = 0; i < res.length(); i++) {
            String[] row = res.split(" ");
            sales.add(row[0] + " " + row[1] + " " + row[2]);
        }
        return sales;
    }

    public static ArrayList<String> getSalesDetails(int id) throws SQLException {
        ArrayList<String> values = new ArrayList<>(){
            {
                add(id+"");
            }};
        String res = DataAccessObject.executeQueryValues("SELECT * FROM detalle_ventas_tbl WHERE idVenta = ?", values);
        ArrayList<String> item = new ArrayList<>();
        for (int i = 0; i < res.length(); i++) {
            String[] row = res.split(" ");
            item.add(row[0]);
        }
        return item;
    }

    public static String searchSale(String query) throws SQLException {
        ArrayList<String> values = new ArrayList<>(){
            {
                add("idVenta");
                add("sFecha");
                add("dTotal");
            }
        };
        return DataAccessObject.executeQueryValuesLike("ventas_tbl", values, query);
    }
}
