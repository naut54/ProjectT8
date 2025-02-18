package gui;

import utils.Styles;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static utils.Styles.createStyledButton;

public class MainPanel extends JPanel {
    private JPanel quickAccessPanel;
    private JPanel modulesPanel;
    private final Color MENU_COLOR = new Color(52, 73, 94);
    private final MainWindow mainWindow;

    public MainPanel(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        initializePanel();
        createComponents();
        layoutComponents();
        setupStyles();
    }

    private void initializePanel() {
        setLayout(new GridBagLayout());
    }

    private void createComponents() {
        createMenuBar();
        createQuickAccessPanel();
        createModulesPanel();
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(MENU_COLOR);
        menuBar.setOpaque(true);

        JMenu configMenu = new JMenu("Configuración");
        JMenu viewMenu = new JMenu("Ver");
        JMenu helpMenu = new JMenu("Ayuda");

        configMenu.setForeground(Color.WHITE);
        viewMenu.setForeground(Color.WHITE);
        helpMenu.setForeground(Color.WHITE);

        configMenu.add(createMenuItem("Cargar Datos"));
        configMenu.add(createMenuItem("Guardar Datos"));
        configMenu.add(createMenuItem("Borrar Datos"));

        viewMenu.add(createMenuItem("Consulta Rápida"));

        helpMenu.add(createMenuItem("Acerca de"));

        menuBar.add(configMenu);
        menuBar.add(viewMenu);
        menuBar.add(helpMenu);

        mainWindow.setJMenuBar(menuBar);
    }

    private JMenuItem createMenuItem(String text) {
        JMenuItem item = new JMenuItem(text);
        Styles.setItemStyle(item);
        return item;
    }

    private void createQuickAccessPanel() {
        quickAccessPanel = new JPanel(new GridBagLayout());
        quickAccessPanel.setBorder(BorderFactory.createTitledBorder("Accesos Rápidos"));

        Object[][] quickAccessButtons = {
                {"Nuevo Producto", (Runnable) () -> mainWindow.showPanel("addProduct")},
                {"Nueva Venta", (Runnable) () -> mainWindow.showPanel("addSale")},
                {"Generar Factura", (Runnable) () -> mainWindow.showPanel("addProduct")},
                {"Actualizar Stock", (Runnable) () -> mainWindow.showPanel("addProduct")},
                {"Nueva Categoria", (Runnable) () -> mainWindow.showPanel("addProduct")},
                {"Consulta Rapida", (Runnable) () -> mainWindow.showPanel("addProduct")}
        };

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.NORTHWEST;

        int row = 0;
        int col = 0;

        for (Object[] buttonText : quickAccessButtons) {
            String buttonTextValue = (String) buttonText[0];
            Runnable action = (Runnable) buttonText[1];

            JButton button = createStyledButton(buttonTextValue, MENU_COLOR, 150, 60, new Color(70, 83, 97));
            button.addActionListener(e -> action.run());

            button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            Styles.setSizeButton(button, 150, 60);
            gbc.gridx = col;
            gbc.gridy = row;
            gbc.weightx = 1.0;
            quickAccessPanel.add(button, gbc);

            col++;
            if (col == 4) {
                col = 0;
                row++;
            }
        }
    }

    private void createModulesPanel() {
        modulesPanel = new JPanel(new GridBagLayout());

        String[][] modules = {
                {"Productos", "Gestionar catálogo de productos"},
                {"Stock", "Control de inventario"},
                {"Ventas", "Registro y control de ventas"},
                {"Categorías", "Administrar categorías"}
        };

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;

        int col = 0;
        int row = 0;

        for (String[] moduleInfo : modules) {
            JPanel modulePanel = createModulePanel(moduleInfo[0], moduleInfo[1]);
            gbc.gridx = col;
            gbc.gridy = row;
            modulesPanel.add(modulePanel, gbc);

            col++;
            if (col == 2) {
                col = 0;
                row++;
            }
        }
    }

    private JPanel createModulePanel(String title, String description) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(MENU_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 46f));

        JLabel descLabel = new JLabel(description);
        descLabel.setForeground(Color.WHITE);

        panel.add(titleLabel, gbc);
        panel.add(descLabel, gbc);

        panel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                panel.setBackground(MENU_COLOR.darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                panel.setBackground(MENU_COLOR);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                handleModuleClick(title);
            }
        });

        return panel;
    }

    private void handleModuleClick(String moduleName) {
        switch (moduleName) {
            case "Productos" -> {
                mainWindow.showPanel("products");
            }
            case "Stock" -> {
                System.out.println("Abriendo módulo de stock");
            }
            case "Ventas" -> {
                mainWindow.showPanel("salesPanel");
            }
            case "Categorías" -> {
                System.out.println("Abriendo módulo de categorías");
                mainWindow.showPanel("addProduct");
            }
            default -> System.out.println("Módulo no reconocido");
        }
    }

    private void layoutComponents() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = GridBagConstraints.REMAINDER;

        gbc.gridy = 0;
        gbc.weightx = 1.0;
        add(quickAccessPanel, gbc);

        gbc.gridy = 1;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        add(modulesPanel, gbc);
    }

    private void setupStyles() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
