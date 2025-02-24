package gui;

import edumdev.DataAccessObject;
import models.Product;
import utils.Styles;
import utils.Validate;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.util.ArrayList;

public class EditProduct extends JPanel {
    private final Color MENU_COLOR = new Color(52, 73, 94);
    private JTextField nameField;
    private JTextField codeField;
    private JTextField priceField;
    private JComboBox categoryCombo;
    private JTextArea descriptionArea;
    private final MainWindow mainWindow;

    public EditProduct(MainWindow mainWindow) throws SQLException {
        if (mainWindow == null) {
            throw new IllegalArgumentException("MainWindow no puede ser null");
        }
        this.mainWindow = mainWindow;
        initializePanel();
        createComponents();
        setupStyles();
        addValidations();
    }

    private void initializePanel() {
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    }

    private void createComponents() throws SQLException {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel titleLabel = new JLabel("Añadir Nuevo Producto");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(titleLabel, gbc);

        createFormField("Nombre del Producto *", gbc, 1);
        nameField = new JTextField();
        Styles.setTextFieldsStyle(nameField, 300, 30);
        gbc.gridy = 2;
        add(nameField, gbc);

        createFormField("Código de Producto *", gbc, 3);
        codeField = new JTextField();
        Styles.setTextFieldsStyle(codeField, 300, 30);
        gbc.gridy = 4;
        add(codeField, gbc);

        createFormField("Precio *", gbc, 5);
        priceField = new JTextField();
        Styles.setTextFieldsStyle(priceField, 300, 30);
        gbc.gridy = 6;
        add(priceField, gbc);

        createFormField("Categoría *", gbc, 7);
        categoryCombo = createCategoryDropdown();
        categoryCombo.setPreferredSize(new Dimension(300, 30));
        gbc.gridy = 8;
        add(categoryCombo, gbc);

        createFormField("Descripción", gbc, 9);
        descriptionArea = new JTextArea(5, 25);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(descriptionArea);
        scrollPane.setPreferredSize(new Dimension(300, 100));
        gbc.gridy = 10;
        add(scrollPane, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelButton = Styles.createStyledButton("Volver a Productos", new Color(108, 117, 125), 180, 40, new Color(70, 83, 97));
        JButton saveButton = Styles.createStyledButton("Guardar Producto", MENU_COLOR, 180, 40, new Color(70, 83, 97));

        cancelButton.addActionListener(_ -> mainWindow.showPanel("products"));
        saveButton.addActionListener(_ -> {
            try {
                saveProduct();
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

    private JComboBox createCategoryDropdown() throws SQLException {
        String[] combo = (DataAccessObject.executeQueryNoValues("SELECT sNombre FROM categorias_tbl")).split(" ");
        return new JComboBox<>(combo);
    }

    private void addValidations() {
        priceField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c) && c != '.' && c != KeyEvent.VK_BACK_SPACE) {
                    e.consume();
                }
                if (c == '.' && priceField.getText().contains(".")) {
                    e.consume();
                }
            }
        });

        codeField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isLetterOrDigit(c) && c != KeyEvent.VK_BACK_SPACE) {
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

    private boolean validateFields() throws SQLException {
        if (nameField.getText().trim().isEmpty()) {
            showError("El nombre del producto es obligatorio");
            return false;
        }
        if (codeField.getText().trim().isEmpty()) {
            showError("El código del producto es obligatorio");
            return false;
        }
        if (priceField.getText().trim().isEmpty() || Validate.isDouble(priceField.getText())) {
            showError("El precio debe ser un número válido");
            return false;
        }
        if (getSelectedCategory() == 0) {
            showError("Debe seleccionar una categoría");
            return false;
        }
        return true;
    }

    private int getSelectedCategory() throws SQLException {
        Object selectedItem = categoryCombo.getSelectedItem();
        if (selectedItem == null) {
            throw new IllegalArgumentException("No se ha seleccionado ninguna categoría.");
        }

        String selectedCategory = selectedItem.toString().trim();
        if (selectedCategory.isEmpty()) {
            throw new IllegalArgumentException("La categoría seleccionada está vacía.");
        }

        String query = "SELECT idCategoria FROM categorias_tbl WHERE sNombre LIKE ? LIMIT 1";
        ArrayList<String> params = new ArrayList<>();
        params.add("%" + selectedCategory + "%");

        return DataAccessObject.executeSingleIntQuery(query, params);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this,
                message,
                "Error de validación",
                JOptionPane.ERROR_MESSAGE);
    }

    public void setData(Object[] args) {
        if (args.length > 0 && args[0] instanceof Product) {
            Product product = (Product) args[0];
            Component[] components = getComponents();
            for (Component component : components) {
                if (component instanceof JLabel && ((JLabel) component).getText().contains("Añadir")) {
                    ((JLabel) component).setText("Editar Producto");
                    break;
                }
            }
            updateFields(product);
        }
    }

    private void updateFields(Product product) {
        if (product != null) {
            nameField.setText(product.getName());
            codeField.setText(String.valueOf(product.getProductCode()));
            priceField.setText(String.valueOf(product.getPrice()));
            descriptionArea.setText(product.getDescription());

            try {
                ArrayList<String> params = new ArrayList<>();
                params.add(String.valueOf(product.getIdCategory()));
                String categoryName = DataAccessObject.executeQueryValues(
                        "SELECT sNombre FROM categorias_tbl WHERE idCategoria = ?",
                        params
                ).trim();
                for (int i = 0; i < categoryCombo.getItemCount(); i++) {
                    if (categoryCombo.getItemAt(i).toString().equals(categoryName)) {
                        categoryCombo.setSelectedIndex(i);
                        break;
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showError("Error al cargar la categoría del producto");
            }
        }
    }

    private void saveProduct() throws SQLException {
        if (!validateFields()) {
            return;
        }
        try {
            Product product = setProduct();

            String checkExisting = "SELECT COUNT(*) FROM productos_tbl WHERE iCodigoProducto = ?";
            ArrayList<String> checkParams = new ArrayList<>();
            checkParams.add(String.valueOf(product.getProductCode()));
            int exists = DataAccessObject.executeSingleIntQuery(checkExisting, checkParams);

            if (exists > 0) {
                ArrayList<String> values = new ArrayList<>() {{
                    add(product.getName());
                    add(product.getDescription());
                    add(String.valueOf(product.getPrice()));
                    add(String.valueOf(product.getIdCategory()));
                    add(String.valueOf(product.getProductCode()));
                }};

                String updateQuery = "UPDATE productos_tbl SET sNombre = ?, sDescripcion = ?, dPrecio = ?, idCategoria = ? WHERE iCodigoProducto = ?";
                DataAccessObject.executeQueryValues(updateQuery, values);
            }

            JOptionPane.showMessageDialog(this,
                    "Producto " + (exists > 0 ? "actualizado" : "guardado") + " exitosamente",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);

            mainWindow.showPanel("products");
        } catch (Exception e) {
            showError("Error al " + (codeField.isEnabled() ? "guardar" : "actualizar") + " el producto: " + e.getMessage());
        }
    }

    private Product setProduct() throws SQLException {
        String name = nameField.getText().trim();
        String description = descriptionArea.getText().trim();
        double price = Double.parseDouble(priceField.getText());
        int category = getSelectedCategory();
        int codeF = Integer.parseInt(codeField.getText());

        Product product = new Product(name, description, price, codeF, category);

        return product;
    }
}