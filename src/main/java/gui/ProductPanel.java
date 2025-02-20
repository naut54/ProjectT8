package gui;

import models.Product;
import utils.Styles;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.sql.SQLException;

import static utils.Styles.createStyledButton;

public class ProductPanel extends JPanel {
    private final String[] columnNames = {
            "Código", "Nombre", "Descripcion", "Precio", "Categoria", "Estado", "Stock"
    };
    private final Font FONT = new Font("Arial", Font.PLAIN, 14);
    private JPanel backPanel;
    private String query = "";
    private JPanel searchPanel;
    private JPanel quickAccessPanel;
    private JPanel productDetailsPanel;
    private final Color MENU_COLOR = new Color(52, 73, 94);
    private final Color FONT_COLOR = new Color(255, 255, 255);
    private DefaultTableModel model = new DefaultTableModel();
    private JTable table;
    private final MainWindow mainWindow;

    public ProductPanel(MainWindow mainWindow) {
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
        createQuickAccessPanel();
        try {
            createProductDetailsPanel(new Object[][]{});
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        updateList(query);
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

    private void createSearchPanel() {
        searchPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        JTextField searchField = new JTextField();
        utils.Styles.setTextFieldsStyle(searchField, 600, 30);
        searchField.setToolTipText("Buscar producto por nombre");
        searchField.addActionListener(_ -> updateList(query = searchField.getText()));

        JButton searchButton = Styles.createStyledButton("Buscar", MENU_COLOR, 100, 30, new Color(70, 83, 97));
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

        Object[][] buttonConfigs = {
                {"Nuevo Producto", (Runnable) () -> mainWindow.showPanel("addProduct")},
                {"Modificar Producto", (Runnable) () -> {
                    Product selectedProduct = getSelectedProduct();
                    if (selectedProduct == null) {
                        JOptionPane.showMessageDialog(this,
                                "Por favor, seleccione un producto primero",
                                "Advertencia",
                                JOptionPane.WARNING_MESSAGE);
                    } else {
                        mainWindow.showPanel("editProduct", selectedProduct);
                    }
                }},
                {"Eliminar Producto", (Runnable) () -> {
                    try {
                        deleteSelectedProduct();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }},
                {"Actualizar Lista", (Runnable) () -> updateList("")}
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

    private Product getSelectedProduct() {
        if (table == null || table.getRowCount() == 0) {
            return null;
        }

        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            return null;
        }

        try {
            String codigo = table.getValueAt(selectedRow, 0).toString();
            String nombre = table.getValueAt(selectedRow, 1).toString();
            String descripcion = table.getValueAt(selectedRow, 2).toString();
            double precio = Double.parseDouble(table.getValueAt(selectedRow, 3).toString());
            int categoria = Integer.parseInt(table.getValueAt(selectedRow, 4).toString());

            return new Product(nombre, descripcion, precio, Integer.parseInt(codigo), categoria);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void deleteSelectedProduct() throws SQLException {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            Object value = model.getValueAt(selectedRow, 0);
            try {
                int productId = Integer.parseInt(value.toString());

                int confirm = JOptionPane.showConfirmDialog(this,"¿Esta seguro?", "¿Eliminar?" , JOptionPane.YES_NO_CANCEL_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    Product.removeItem(productId);
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
                6,
                SwingConstants.CENTER
        );
        table.getTableHeader().setReorderingAllowed(false);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Product selected = getSelectedProduct();
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

    private Object[][] convertStringToArray(String data) {
        try {
            String[] rows = data.trim().split("\n");
            Object[][] result = new Object[rows.length][7];

            for (int i = 0; i < rows.length; i++) {
                String row = rows[i].trim();

                String[] parts = row.split("\\s+(?=\\d+(\\.\\d+)?\\s+\\d+\\s+\\d+\\s+\\d+$)");

                if (parts.length != 2) {
                    throw new IllegalArgumentException("Formato incorrecto en la fila " + (i + 1));
                }

                try {
                    String firstPart = parts[0].trim();
                    String[] numericParts = parts[1].trim().split("\\s+");

                    String[] nameDesc = firstPart.split("\\s+", 2);
                    String nombre = nameDesc[0];
                    String descripcion = nameDesc.length > 1 ? nameDesc[1] : "";

                    double precio = Double.parseDouble(numericParts[0]);
                    int codigoProducto = Integer.parseInt(numericParts[1]);
                    int categoria = Integer.parseInt(numericParts[2]);
                    int activo = Integer.parseInt(numericParts[3]);

                    result[i][0] = codigoProducto;
                    result[i][1] = nombre;
                    result[i][2] = descripcion;
                    result[i][3] = precio;
                    result[i][4] = categoria;
                    result[i][5] = activo;
                    result[i][6] = 0;

                } catch (Exception e) {
                    System.out.println("Error procesando valores en fila " + (i + 1) + ": " + e.getMessage());
                    e.printStackTrace();
                    throw new IllegalArgumentException("Error procesando la fila " + (i + 1) + ": " + e.getMessage());
                }
            }
            return result;
        } catch (Exception e) {
            System.out.println("Error general: " + e.getMessage());
            throw new IllegalArgumentException("Error en el procesamiento de datos: " + e.getMessage());
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
