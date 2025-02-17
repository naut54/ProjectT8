package gui;

import models.Product;
import models.Sale;
import utils.Styles;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.sql.SQLException;

import static utils.Styles.createStyledButton;

public class SalesPanel extends JPanel {
    private final String[] columnNames = {
            "Código", "Nombre", "Descripcion", "Precio", "Categoria", "Estado", "Stock"
    };
    private JPanel backPanel;
    private String query = "";
    private JPanel searchPanel;
    private JPanel quickAccessPanel;
    private JPanel productDetailsPanel;
    private final Color MENU_COLOR = new Color(52, 73, 94);
    private DefaultTableModel model = new DefaultTableModel();
    private JTable table;
    private final MainWindow mainWindow;

    public SalesPanel(MainWindow mainWindow) {
        if (mainWindow == null) {
            throw new IllegalArgumentException("MainWindow no puede ser null");
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
        createSearchPanel();
    }

    private void createGoBackButton() {
        backPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        JButton goBackButton = Styles.createStyledButton("Volver", MENU_COLOR, 100, 30);
        goBackButton.addActionListener(_ -> mainWindow.showPanel("main"));
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        backPanel.add(goBackButton, gbc);
    }

    private void createSearchPanel() {
        searchPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        JTextField searchField = new JTextField();
        utils.Styles.setTextFieldsStyle(searchField, 600, 30);
        searchField.setToolTipText("Buscar producto por nombre");
        searchField.addActionListener(_ -> updateList(query = searchField.getText()));

        JButton searchButton = Styles.createStyledButton("Buscar", MENU_COLOR, 100, 30);
        searchButton.setToolTipText("Buscar producto por nombre");
        searchButton.addActionListener(_ -> updateList(searchField.getText()));

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.gridx = 0;
        gbc.gridy = 1;
        searchPanel.add(searchField, gbc);
        gbc.gridx = 2;
        searchPanel.add(searchButton, gbc);

        add(searchPanel, gbc);
    }

    private void createQuickAccessPanel() {
        quickAccessPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        Sale data = getSelectedSale();

        Object[][] buttonConfigs = {
                {"Nuevo Producto", (Runnable) () -> mainWindow.showPanel("addProduct")},
                {"Modificar Producto", (Runnable) () -> {
                    Sale selectedSale = getSelectedSale();
                    if (selectedSale == null) {
                        JOptionPane.showMessageDialog(this,
                                "Por favor, seleccione un producto primero",
                                "Advertencia",
                                JOptionPane.WARNING_MESSAGE);
                    } else {
                        mainWindow.showPanel("editProduct", selectedSale);
                    }
                }},
                {"Eliminar Producto", (Runnable) () -> {
                    try {
                        deleteSelectedSale();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }},
                {"Actualizar Lista", (Runnable) () -> updateList("")}
        };
    }

    private Sale getSelectedSale() {
        if (table == null || table.getRowCount() == 0) {
            return null;
        }

        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            return null;
        }

        try {
            int codigo = (int) table.getValueAt(selectedRow, 0);
            int cantidad = Integer.parseInt(table.getValueAt(selectedRow, 1).toString());
            double precio = Double.parseDouble(table.getValueAt(selectedRow, 2).toString());

            return new Sale(codigo, cantidad, precio);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void deleteSelectedSale() throws SQLException {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            Object value = model.getValueAt(selectedRow, 0);
            try {
                int saleId = Integer.parseInt(value.toString());

                int confirm = JOptionPane.showConfirmDialog(this,"¿Esta seguro?", "¿Eliminar?" , JOptionPane.YES_NO_CANCEL_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    Sale.removeSale(saleId);
                    updateList(query);
                    JOptionPane.showMessageDialog(null, "Producto eliminado con exito");
                } else if (confirm == JOptionPane.NO_OPTION) {
                    JOptionPane.showMessageDialog(null, "Operacion Cancelada");
                } else if (confirm == JOptionPane.CLOSED_OPTION) {
                    JOptionPane.showMessageDialog(null, "Operacion Cancelada");
                } else {
                    JOptionPane.showMessageDialog(null, "Error al eliminar producto");
                    throw new RuntimeException("Error al eliminar producto");
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this,
                        "El valor en la columna 'Código' no es un número válido: " + value,
                        "Error de tipo",
                        JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "Por favor, seleccione un producto antes de eliminar.",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void createProductDetailsPanel(Object[][] data) throws SQLException {
        productDetailsPanel = new JPanel(new GridBagLayout());
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
        utils.Styles.setTableStyle(table, header, MENU_COLOR);
        utils.Styles.setRowStyle(table);
        table.getTableHeader().setReorderingAllowed(false);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Sale selected = getSelectedSale();
            }
        });

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
        productDetailsPanel.add(scrollPane, gbc);
    }

    public Object[][] convertStringToArray(String data) {
        try {
            String[] rows = data.split("\n");
            Object[][] result = new Object[rows.length][8];

            for (int i = 0; i < rows.length; i++) {
                String[] columns = rows[i].trim().split("\\s+", 7);

                if (columns.length < 5) {
                    throw new IllegalArgumentException("Datos incompletos en la fila " + (i + 1));
                }

                try {
                    result[i][0] = columns[4];
                    result[i][1] = columns[1];
                    result[i][2] = columns[2];
                    result[i][3] = Double.parseDouble(columns[3]);
                    result[i][4] = columns[5];
                    result[i][5] = Product.checkStatus(columns[4]);
                    result[i][6] = 0;
                } catch (Exception e) {
                    throw new IllegalArgumentException("Error procesando la fila " + (i+1) + ": " + e.getMessage());
                }
            }
            return result;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Error en el formato de los números: " + e.getMessage());
        }
    }

    private void updateList(String query) {
        try {
            String searchResults = Product.searchItemByName(query);

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

    private void layoutComponents() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weighty = 0.2;
        add(backPanel, gbc);

        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 0.2;
        add(searchPanel, gbc);

        gbc.gridy = 2;
        gbc.weighty = 0.8;
        add(quickAccessPanel, gbc);

        gbc.gridy = 3;
        gbc.weighty = 1.0;
        add(productDetailsPanel, gbc);
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
