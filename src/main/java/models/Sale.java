package models;

import edumdev.DataAccessObject;

import java.sql.SQLException;
import java.util.ArrayList;

public class Sale {
    private int idVenta;
    private int idProducto;
    private int iCantidad;

    public Sale(int idVenta, int idProducto, int iCantidad) {
        this.idVenta = idVenta;
        this.idProducto = idProducto;
        this.iCantidad = iCantidad;
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

    
}
