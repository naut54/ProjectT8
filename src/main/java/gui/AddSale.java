package gui;

import edumdev.DataAccessObject;
import models.Product;
import models.Sale;
import utils.Styles;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;

public class AddSale extends JPanel {
    private final Color MENU_COLOR = new Color(52, 73, 94);
    private final MainWindow mainWindow;
    private JTextField searchField;
    private JTable productTable;
    private DefaultTableModel tableModel;
    private JLabel subtotalLabel;
    private JLabel ivaLabel;
    private JLabel totalLabel;
    private ArrayList<Sale> carrito;
    private double subtotal = 0.0;
    private double iva = 0.0;
    private double total = 0.0;

    public AddSale(MainWindow mainWindow) {
        if (mainWindow == null) {
            throw new IllegalArgumentException("MainWindow no puede ser null");
        }
        this.mainWindow = mainWindow;
        this.carrito = new ArrayList<>();
        initializePanel();
        createComponents();
        setupStyles();
    }

    private void initializePanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    }

    private void createComponents() {
        JLabel titleLabel = new JLabel("Nueva Venta");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));

        JPanel searchPanel = new JPanel(new BorderLayout(10, 0));
        searchField = new JTextField();
        Styles.setTextFieldsStyle(searchField, 300, 35);
        searchField.setPreferredSize(new Dimension(500, 35));
        JButton addButton = Styles.createStyledButton("Agregar Producto", MENU_COLOR, 180, 35, new Color(70, 83, 97));
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(addButton, BorderLayout.EAST);
        mainPanel.add(searchPanel, BorderLayout.NORTH);

        String[] columns = {"Producto", "Cantidad", "Precio", "Acciones"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 1;
            }
        };
        productTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(productTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel summaryPanel = createSummaryPanel();
        mainPanel.add(summaryPanel, BorderLayout.EAST);

        add(mainPanel, BorderLayout.CENTER);

        addButton.addActionListener(e -> searchSale());
        setupTableStyles();
    }

    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));

        JLabel summaryTitle = new JLabel("Resumen de Venta");
        summaryTitle.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(summaryTitle);
        panel.add(Box.createVerticalStrut(20));

        subtotalLabel = new JLabel("Subtotal: 0.00€");
        panel.add(subtotalLabel);
        panel.add(Box.createVerticalStrut(10));

        ivaLabel = new JLabel("IVA (21%): 0.00€");
        panel.add(ivaLabel);
        panel.add(Box.createVerticalStrut(10));

        totalLabel = new JLabel("Total: 0.00€");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(totalLabel);
        panel.add(Box.createVerticalStrut(30));

        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 0, 10));
        JButton registerButton = Styles.createStyledButton("Registrar Venta", MENU_COLOR, 180, 10, new Color(70, 83, 97));
        JButton cancelButton = Styles.createStyledButton("Cancelar", new Color(231, 76, 60), 180, 10, new Color(70, 83, 97));

        registerButton.addActionListener(e -> registrarVenta());
        cancelButton.addActionListener(e -> mainWindow.showPanel("ventas"));

        buttonPanel.add(registerButton);
        buttonPanel.add(cancelButton);
        panel.add(buttonPanel);

        return panel;
    }

    private void setupTableStyles() {
        JTableHeader header = productTable.getTableHeader();
        Styles.setTableStyle(
                productTable,
                header,
                MENU_COLOR,
                Color.WHITE,
                new Font("Arial", Font.BOLD, 14),
                40,
                SwingConstants.CENTER
        );

        Styles.setRowStyle(
                productTable,
                Color.WHITE,
                new Color(245, 245, 245),
                new Color(40, 167, 69),
                new Color(220, 53, 69),
                Color.BLACK,
                35,
                new Color(230, 230, 230),
                -1,
                SwingConstants.CENTER
        );
    }

    private void searchSale() {
        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty()) {
            showError("Ingrese un código o nombre de producto");
            return;
        }

        try {
            ArrayList<String> columns = new ArrayList<>();
            columns.add("sNombre");
            columns.add("iCodigoProducto");
            String result = DataAccessObject.executeQueryValuesLike("productos_tbl", columns, searchTerm);
            if (result.equals("Sin resultados")) {
                showError("No se encontró el producto");
                return;
            }
            addSaleToTable(result);
        } catch (SQLException e) {
            showError("Error al buscar el producto: " + e.getMessage());
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this,
                message,
                "Error",
                JOptionPane.ERROR_MESSAGE);
    }

    private void addSaleToTable(String productoInfo) {
        String[] datos = productoInfo.split(" ");
        Object[] row = new Object[]{
                datos[1],
                1,
                datos[3],
                "X"
        };
        tableModel.addRow(row);
        actualizarTotales();
    }

    private void eliminarProducto(int row) {
        tableModel.removeRow(row);
        actualizarTotales();
    }

    private void actualizarCantidad(int row, int cantidad) {
        if (cantidad <= 0) {
            eliminarProducto(row);
            return;
        }
        tableModel.setValueAt(cantidad, row, 1);
        actualizarTotales();
    }

    private void actualizarTotales() {
        subtotal = 0.0;
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            int cantidad = (int) tableModel.getValueAt(i, 1);
            double precio = Double.parseDouble(tableModel.getValueAt(i, 2).toString());
            subtotal += cantidad * precio;
        }

        iva = subtotal * 0.21;
        total = subtotal + iva;

        subtotalLabel.setText(String.format("Subtotal: %.2f€", subtotal));
        ivaLabel.setText(String.format("IVA (21%%): %.2f€", iva));
        totalLabel.setText(String.format("Total: %.2f€", total));
    }

    private boolean validarStock(int idProducto, int cantidad) throws SQLException {
        ArrayList<String> params = new ArrayList<>();
        params.add(String.valueOf(idProducto));

        int stockActual = DataAccessObject.executeSingleIntQuery(
                "SELECT iCantidad FROM stock_tbl WHERE idProducto = ?",
                params
        );

        return stockActual >= cantidad;
    }

    private ArrayList<Sale> prepararVenta() throws SQLException {
        ArrayList<Sale> ventas = new ArrayList<>();

        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String nombreProducto = tableModel.getValueAt(i, 0).toString();
            int cantidad = Integer.parseInt(tableModel.getValueAt(i, 1).toString());
            double precio = Double.parseDouble(tableModel.getValueAt(i, 2).toString());

            String idProducto = Product.getIdProduct(nombreProducto);
            System.out.println(idProducto);

            if (!validarStock(Integer.parseInt(idProducto), cantidad)) {
                throw new SQLException("Stock insuficiente para el producto: " + nombreProducto);
            }

            ventas.add(new Sale(Integer.parseInt(idProducto), cantidad, precio));
        }

        return ventas;
    }

    private void registrarVenta() {
        try {
            if (tableModel.getRowCount() == 0) {
                showError("No hay productos en la venta");
                return;
            }

            ArrayList<Sale> ventas = prepararVenta();
            if (Sale.recordSale(ventas)) {
                JOptionPane.showMessageDialog(this,
                        "Venta registrada exitosamente",
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
                limpiarVenta();
                mainWindow.showPanel("ventas");
            }
        } catch (SQLException e) {
            showError("Error al registrar la venta: " + e.getMessage());
        }
    }

    private void limpiarVenta() {
        while (tableModel.getRowCount() > 0) {
            tableModel.removeRow(0);
        }
        actualizarTotales();
        searchField.setText("");
    }

    private void configurarTabla() {
        productTable.getColumn("Acciones").setCellRenderer((table, value, isSelected, hasFocus, row, column) -> {
            JButton button = new JButton("X");
            button.setBackground(new Color(231, 76, 60));
            button.setForeground(Color.WHITE);
            return button;
        });

        productTable.getColumn("Acciones").setCellEditor(new DefaultCellEditor(new JCheckBox()) {
            @Override
            public Component getTableCellEditorComponent(JTable table, Object value,
                                                         boolean isSelected, int row, int column) {
                JButton button = new JButton("X");
                button.setBackground(new Color(231, 76, 60));
                button.setForeground(Color.WHITE);
                button.addActionListener(e -> eliminarProducto(row));
                return button;
            }
        });

        productTable.getModel().addTableModelListener(e -> {
            if (e.getColumn() == 1) {
                int row = e.getFirstRow();
                Object value = tableModel.getValueAt(row, 1);
                if (value != null) {
                    try {
                        int cantidad = Integer.parseInt(value.toString());
                        actualizarCantidad(row, cantidad);
                    } catch (NumberFormatException ex) {
                        tableModel.setValueAt(1, row, 1);
                    }
                }
            }
        });
    }

    private void setupStyles() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}