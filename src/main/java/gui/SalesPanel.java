package gui;

import models.Product;
import models.Sale;
import utils.Styles;
import utils.Validate;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.sql.SQLException;
import java.util.Arrays;

import static utils.Styles.createStyledButton;

public class SalesPanel extends JPanel {
    private final String[] columnNames = {
            "Código Venta", "Fecha", "Cantidad", "Total"
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
        try {
            createSalesDetailsPanel(new Object[][]{});
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

    private void createSalesDetailsPanel(Object[][] data) throws SQLException {
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
                2,
                SwingConstants.CENTER
        );
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

    private Sale getSelectedSale() {
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

    private void updateList(String query) {
        try {
            String searchResults = Sale.searchSale(query);

            if (searchResults.trim().isEmpty()) {
                model.setRowCount(0);
                return;
            }

            Object[][] data = convertStringToArray(searchResults);

            System.out.println( Arrays.deepToString( data ));

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
                String row = rows[i];

                String[] parts = row.split("\\s+");
                if (parts.length < 4) {
                    throw new IllegalArgumentException("Datos incompletos en la fila " + (i + 1) + ": " + Arrays.toString(parts));
                }

                String codigo = parts[0];
                String fecha = parts[1];
                String cantidad = parts[2];
                StringBuilder totalBuilder = new StringBuilder();
                for (int j = 3; j < parts.length; j++) {
                    totalBuilder.append(parts[j]);
                    if (j + 1 < parts.length) {
                        totalBuilder.append(" ");
                    }
                }
                String total = totalBuilder.toString();

                result[i][0] = codigo;
                result[i][1] = fecha;
                result[i][2] = cantidad;
                result[i][3] = total;
            }

            return result;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException("Error inesperado al procesar los datos: " + e.getMessage(), e);
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
        gbc.weightx = 1.0;
        gbc.weighty = 0.8;
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
