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

    public static boolean recordSale(ArrayList<Sale> sales) throws SQLException {
        try {
            for (Sale sale : sales) {
                ArrayList<String> paramsStock = new ArrayList<>();
                paramsStock.add(String.valueOf(sale.getIdProducto()));

                int stockActual = DataAccessObject.executeSingleIntQuery(
                        "SELECT iCantidad FROM stock_tbl WHERE idProducto = ?",
                        paramsStock
                );

                if (stockActual < sale.getiCantidad()) {
                    throw new SQLException("Stock insuficiente para el producto ID: " + sale.getIdProducto());
                }
            }

            double totalVenta = 0;
            for (Sale sale : sales) {
                ArrayList<String> paramsProduct = new ArrayList<>();
                paramsProduct.add(String.valueOf(sale.getIdProducto()));

                double precioUnitario = DataAccessObject.executeSingleDoubleQuery(
                        "SELECT dPrecio FROM productos_tbl WHERE idProducto = ?",
                        paramsProduct
                );

                totalVenta += precioUnitario * sale.getiCantidad();
            }

            ArrayList<String> columnasVenta = new ArrayList<>();
            ArrayList<String> valoresVenta = new ArrayList<>();

            columnasVenta.add("dTotal");
            valoresVenta.add(String.valueOf(totalVenta));

            DataAccessObject.executeInsert(columnasVenta, valoresVenta, "ventas_tbl");

            int idVenta = DataAccessObject.executeSingleIntQuery(
                    "SELECT MAX(idVenta) FROM ventas_tbl",
                    new ArrayList<>()
            );

            for (Sale sale : sales) {
                ArrayList<String> paramsProduct = new ArrayList<>();
                paramsProduct.add(String.valueOf(sale.getIdProducto()));
                double precioUnitario = DataAccessObject.executeSingleDoubleQuery(
                        "SELECT dPrecio FROM productos_tbl WHERE idProducto = ?",
                        paramsProduct
                );

                ArrayList<String> columnasDetalle = new ArrayList<>();
                ArrayList<String> valoresDetalle = new ArrayList<>();

                columnasDetalle.add("idVenta");
                columnasDetalle.add("idProducto");
                columnasDetalle.add("iCantidad");
                columnasDetalle.add("dPrecio");

                valoresDetalle.add(String.valueOf(idVenta));
                valoresDetalle.add(String.valueOf(sale.getIdProducto()));
                valoresDetalle.add(String.valueOf(sale.getiCantidad()));
                valoresDetalle.add(String.valueOf(precioUnitario));

                DataAccessObject.executeInsert(columnasDetalle, valoresDetalle, "detalle_ventas_tbl");

                ArrayList<String> paramsUpdate = new ArrayList<>();
                paramsUpdate.add(String.valueOf(sale.getiCantidad()));
                paramsUpdate.add(String.valueOf(sale.getIdProducto()));

                DataAccessObject.executeQueryValues(
                        "UPDATE stock_tbl SET iCantidad = iCantidad - ? WHERE idProducto = ?",
                        paramsUpdate
                );
            }

            return true;

        } catch (SQLException e) {
            throw new SQLException("Error al procesar la venta: " + e.getMessage());
        }
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

    public static String searchSaleStock(String query) throws SQLException {
        ArrayList<String> params = new ArrayList<>();
        params.add("%" + query + "%");
        params.add("%" + query + "%");

        String stmt = "SELECT CONCAT_WS('\t', " +
                "s.idProducto, " +
                "p.sNombre, " +
                "c.sNombre, " +
                "s.iCantidad) " +
                "FROM stock_tbl s " +
                "JOIN productos_tbl p ON s.idProducto = p.idProducto " +
                "JOIN categorias_tbl c ON p.idCategoria = c.idCategoria " +
                "WHERE p.sNombre LIKE ? OR c.sNombre LIKE ?";

        return DataAccessObject.executeQueryValues(stmt, params);
    }

    public static String searchSale(String query) throws SQLException {
        ArrayList<String> params = new ArrayList<>();
        params.add("%" + query + "%");
        params.add("%" + query + "%");
        params.add("%" + query + "%");

        String sqlQuery =
                "SELECT DISTINCT " +
                        "    v.idVenta, " +
                        "    DATE_FORMAT(v.sFecha, '%Y-%m-%d') as sFecha, " +
                        "    v.dTotal, " +
                        "    COALESCE(SUM(d.iCantidad), 0) as totalCantidad " +
                        "FROM " +
                        "    ventas_tbl v " +
                        "LEFT JOIN " +
                        "    detalle_ventas_tbl d ON v.idVenta = d.idVenta " +
                        "WHERE " +
                        "    CAST(v.idVenta AS CHAR) LIKE ? OR " +
                        "    v.sFecha LIKE ? OR " +
                        "    CAST(v.dTotal AS CHAR) LIKE ? " +
                        "GROUP BY " +
                        "    v.idVenta, v.sFecha, v.dTotal";

        return DataAccessObject.executeQueryValues(sqlQuery, params);
    }
}
