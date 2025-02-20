package gui;

import edumdev.DataAccessObject;
import models.Sale;
import utils.Styles;
import utils.Validate;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

public class EditStock extends JPanel {
    private final Color MENU_COLOR = new Color(52, 73, 94);
    private JTextField productCodeField;
    private JTextField productNameField;
    private JTextField categoryField;
    private JTextField currentStockField;
    private JTextField newStockField;
    private final MainWindow mainWindow;
    private Sale currentStock;

    public EditStock(MainWindow mainWindow) {
        if (mainWindow == null) {
            throw new IllegalArgumentException("MainWindow no puede ser null");
        }
        this.mainWindow = mainWindow;
        this.currentStock = null;

        initializePanel();
        createComponents();
        setupStyles();
        addValidations();

    }

    private void initializePanel() {
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    }

    private void createComponents() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel titleLabel = new JLabel("Modificar Stock");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(titleLabel, gbc);

        createFormField("Código de Producto", gbc, 1);
        productCodeField = new JTextField();
        productCodeField.setEditable(false);
        Styles.setTextFieldsStyle(productCodeField, 300, 30);
        gbc.gridy = 2;
        add(productCodeField, gbc);

        createFormField("Nombre del Producto", gbc, 3);
        productNameField = new JTextField();
        productNameField.setEditable(false);
        Styles.setTextFieldsStyle(productNameField, 300, 30);
        gbc.gridy = 4;
        add(productNameField, gbc);

        createFormField("Categoría", gbc, 5);
        categoryField = new JTextField();
        categoryField.setEditable(false);
        Styles.setTextFieldsStyle(categoryField, 300, 30);
        gbc.gridy = 6;
        add(categoryField, gbc);

        createFormField("Stock Actual", gbc, 7);
        currentStockField = new JTextField();
        currentStockField.setEditable(false);
        Styles.setTextFieldsStyle(currentStockField, 300, 30);
        gbc.gridy = 8;
        add(currentStockField, gbc);

        createFormField("Nuevo Stock *", gbc, 9);
        newStockField = new JTextField();
        Styles.setTextFieldsStyle(newStockField, 300, 30);
        gbc.gridy = 10;
        add(newStockField, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelButton = Styles.createStyledButton("Volver", new Color(108, 117, 125), 180, 40, new Color(70, 83, 97));
        JButton saveButton = Styles.createStyledButton("Guardar Stock", MENU_COLOR, 180, 40, new Color(70, 83, 97));

        cancelButton.addActionListener(_ -> mainWindow.showPanel("stockPanel"));
        saveButton.addActionListener(_ -> {
            try {
                saveStock();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        gbc.gridy = 11;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.LINE_END;
        add(buttonPanel, gbc);
    }

    private void createFormField(String labelText, GridBagConstraints gbc, int gridy) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = gridy;
        gbc.gridwidth = 2;
        add(label, gbc);
    }

    private void addValidations() {
        newStockField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c) && c != KeyEvent.VK_BACK_SPACE) {
                    e.consume();
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

    public void setData(Object[] args) {
        if (args == null || args.length == 0) {
            return;
        }

        if (!(args[0] instanceof Sale)) {
            return;
        }

        this.currentStock = (Sale) args[0];

        if (this.currentStock != null) {
            updateFields();
        }
    }

    private void updateFields() {
        if (currentStock != null) {
            try {
                ArrayList<String> params = new ArrayList<>();
                params.add(String.valueOf(currentStock.getIdProducto()));

                String query = "SELECT CONCAT_WS('\t', " +
                        "p.iCodigoProducto, " +
                        "p.sNombre, " +
                        "c.sNombre, " +
                        "s.iCantidad) " +
                        "FROM productos_tbl p " +
                        "JOIN categorias_tbl c ON p.idCategoria = c.idCategoria " +
                        "JOIN stock_tbl s ON p.idProducto = s.idProducto " +
                        "WHERE p.idProducto = ?";

                String productInfo = DataAccessObject.executeQueryValues(query, params);

                if (!productInfo.isEmpty()) {
                    String[] info = productInfo.trim().split("\t");

                    if (info.length >= 4) {
                        productCodeField.setText(info[0].trim());
                        productNameField.setText(info[1].trim());
                        categoryField.setText(info[2].trim());
                        currentStockField.setText(info[3].trim());

                        Color backgroundColor = new Color(240, 240, 240);
                        productCodeField.setBackground(backgroundColor);
                        productNameField.setBackground(backgroundColor);
                        categoryField.setBackground(backgroundColor);
                        currentStockField.setBackground(backgroundColor);
                    } else {
                        throw new IllegalArgumentException("Datos incompletos del producto");
                    }
                } else {
                    throw new IllegalArgumentException("No se encontró información del producto");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showError("Error al cargar la información del producto: " + e.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
                showError("Error inesperado: " + e.getMessage());
            }
        }
    }

    private boolean validateFields() {
        if (newStockField.getText().trim().isEmpty()) {
            showError("Debe ingresar una cantidad de stock");
            return false;
        }
        if (!Validate.isInteger(newStockField.getText().trim())) {
            showError("La cantidad debe ser un número entero válido");
            return false;
        }
        return true;
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this,
                message,
                "Error de validación",
                JOptionPane.ERROR_MESSAGE);
    }

    private void saveStock() throws SQLException {
        if (!validateFields()) {
            return;
        }

        try {
            int newStock = Integer.parseInt(newStockField.getText().trim());

            ArrayList<String> params = new ArrayList<>();
            params.add(String.valueOf(newStock));
            params.add(String.valueOf(currentStock.getIdProducto()));

            String updateQuery = "UPDATE stock_tbl SET iCantidad = ? WHERE idProducto = ?";

            int rowsAffected = DataAccessObject.executeUpdate(updateQuery, params);

            if (rowsAffected == 0) {
                throw new SQLException("No se actualizó ningún registro");
            }

            JOptionPane.showMessageDialog(this,
                    "Stock actualizado exitosamente",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);

            mainWindow.showPanel("stock");
        } catch (Exception e) {
            showError("Error al actualizar el stock: " + e.getMessage());
        }
    }
}