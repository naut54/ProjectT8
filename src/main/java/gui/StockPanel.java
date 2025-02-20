package gui;

import edumdev.DataAccessObject;
import models.Sale;
import utils.Styles;
import utils.Validate;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

import static utils.Styles.createStyledButton;

public class StockPanel extends JPanel {
    private final String[] columnNames = {
            "Código Producto", "Nombre", "Categoria", "Stock"
    };
    private final MainWindow mainWindow;
    private JPanel backPanel;
    private JPanel quickAccessPanel;
    private String query = "";
    private DefaultTableModel model = new DefaultTableModel();
    private JTable table;
    private JPanel stockDetailsPanel;
    private final Color MENU_COLOR = new Color(52, 73, 94);
    private final Color FONT_COLOR = new Color(255, 255, 255);

    public StockPanel(MainWindow mainWindow) {
        if (mainWindow == null) {
            throw new IllegalArgumentException("mainWindow cannot be null");
        }
        this.mainWindow = mainWindow;
        initializePanel();
        createPanels();
        layoutComponents();
        setupStyles();
    }

    private void initializePanel() {
        setLayout(new GridBagLayout());
    }

    private void createPanels() {
        createGoBackButton();
        createQuickAccessPanel();
        try {
            createStockDetailsPanel(new Object[][]{});
            updateList(query);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void createGoBackButton() {
        backPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        JButton goBackButton = Styles.createStyledButton("Volver", MENU_COLOR, 100, 30, new Color(70, 83, 97));
        goBackButton.addActionListener(_ -> mainWindow.showPanel("main"));
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        backPanel.add(goBackButton, gbc);
    }

    private void createStockDetailsPanel(Object[][] data) throws SQLException {
        stockDetailsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        this.model = new DefaultTableModel(columnNames, 0){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(this.model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowSelectionAllowed(true);
        table.setColumnSelectionAllowed(false);
        JTableHeader header = table.getTableHeader();
        utils.Styles.setTableStyle(table, header,
                MENU_COLOR,
                FONT_COLOR,
                new Font("Arial", Font.BOLD, 14),
                30,
                SwingConstants.CENTER
        );

        utils.Styles.setRowStyle(table,
                Color.WHITE,
                new Color(245, 245, 245),
                new Color(40, 167, 69),
                new Color(220, 53, 69),
                Color.BLACK,
                40,
                new Color(230, 230, 230),
                2,
                SwingConstants.CENTER
        );
        table.getTableHeader().setReorderingAllowed(false);

        if (data != null) {
            for (Object[] row : data) {
                this.model.addRow(row);
            }
        }

        JScrollPane scrollPane = new JScrollPane(table);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        stockDetailsPanel.add(scrollPane, gbc);
    }

    private void createQuickAccessPanel() {
        quickAccessPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        Object[][] buttonConfigs = {
                {"Actualizar Stock", (Runnable) () -> updateList(query)},
                {"Modificar Stock", (Runnable) () -> {
                    int selectedRow = table.getSelectedRow();
                    if (selectedRow == -1) {
                        JOptionPane.showMessageDialog(this,
                                "Por favor, seleccione un artículo de stock primero",
                                "Advertencia",
                                JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    try {
                        Object idProductoObj = table.getValueAt(selectedRow, 0);
                        Object cantidadObj = table.getValueAt(selectedRow, 3);

                        if (idProductoObj == null || cantidadObj == null) {
                            throw new Exception("Datos de la tabla inválidos");
                        }

                        int idProducto = Integer.parseInt(idProductoObj.toString());
                        int cantidad = Integer.parseInt(cantidadObj.toString());

                        Sale selectedSale = new Sale(idProducto, cantidad, 0.0);

                        mainWindow.showPanel("editStock", selectedSale);
                    } catch (Exception e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(this,
                                "Error al procesar la selección: " + e.getMessage(),
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }},
                {"Vaciar Stock", (Runnable) () -> {
                    try {
                        emptyStock();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }}
        };

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.NORTHWEST;

        int row = 0;
        int col = 0;

        for (Object[] config : buttonConfigs) {
            String buttonText = (String) config[0];
            Runnable action = (Runnable) config[1];

            JButton button = createStyledButton(buttonText, MENU_COLOR, 150, 60, new Color(70, 83, 97));
            button.addActionListener(e -> action.run());

            gbc.gridx = col;
            gbc.gridy = row;
            gbc.weightx = 1.0;
            quickAccessPanel.add(button, gbc);

            col++;
            if (col == 3) {
                col = 0;
                row++;
            }
        }
        add(quickAccessPanel, gbc);
    }

    private Sale getSelectedStock() {
        if (table == null || table.getRowCount() == 0) {
            return null;
        }

        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            return null;
        }

        try {
            Object codeObj = table.getValueAt(selectedRow, 0);
            Object quantityObj = table.getValueAt(selectedRow, 2);
            Object totalObj = table.getValueAt(selectedRow, 3);

            if (codeObj == null || quantityObj == null || totalObj == null) {
                return null;
            }

            String codeStr = codeObj.toString();
            String quantityStr = quantityObj.toString();
            String totalStr = totalObj.toString();

            int code = Validate.isNumeric(codeStr) ? Integer.parseInt(codeStr) : 0;
            int quantity = Validate.isNumeric(quantityStr) ? Integer.parseInt(quantityStr) : 0;
            double total = Validate.isNumeric(totalStr) ? Double.parseDouble(totalStr) : 0.0;

            return new Sale(code, quantity, total);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void emptyStock() throws SQLException {
        ArrayList<String> params = new ArrayList<>(){
            {
                add("0");
            }
        };

        DataAccessObject.executeUpdate("UPDATE stock_tbl SET iCantidad = ?", params);
        updateList(query);
        JOptionPane.showMessageDialog(this, "Se ha vaciado el stock de los productos", "Informacion", JOptionPane.INFORMATION_MESSAGE);
    }

    private void updateList(String query) {
        try {
            String searchResults = Sale.searchSaleStock(query);

            if (searchResults.trim().isEmpty()) {
                model.setRowCount(0);
                return;
            }

            Object[][] data = convertStringToArray(searchResults);

            model.setRowCount(0);
            for (Object[] row : data) {
                model.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error al buscar productos: " + e.getMessage(),
                    "Error de búsqueda",
                    JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error en el formato de los datos: " + e.getMessage(),
                    "Error de datos",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private Object[][] convertStringToArray(String data) {
        try {
            String[] rows = data.split("\n");

            rows = Arrays.stream(rows)
                    .map(String::trim)
                    .filter(row -> !row.isEmpty())
                    .toArray(String[]::new);

            Object[][] result = new Object[rows.length][4];

            for (int i = 0; i < rows.length; i++) {
                String[] columns = rows[i].split("\t");

                if (columns.length < 4) {
                    throw new IllegalArgumentException("Fila " + (i + 1) + " incompleta. Se esperaban 4 columnas, se encontraron " + columns.length);
                }

                result[i][0] = columns[0].trim();
                result[i][1] = columns[1].trim();
                result[i][2] = columns[2].trim();
                result[i][3] = columns[3].trim();
            }

            return result;
        } catch (Exception e) {
            throw new IllegalArgumentException("Error al procesar los datos: " + e.getMessage(), e);
        }
    }

    private void layoutComponents() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 0.2;
        add(backPanel, gbc);

        gbc.gridy = 1;
        gbc.weighty = 0.4;
        add(quickAccessPanel, gbc);

        gbc.gridy = 2;
        gbc.weighty = 0.4;
        add(stockDetailsPanel, gbc);
    }

    private void setupStyles() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }
}
