package gui;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class MainWindow extends JFrame {
    private CardLayout cardLayout;
    private JPanel contentPanel;

    public MainWindow() throws SQLException {
        initializeFrame();
        initializePanels();
    }

    private void initializeFrame() {
        setTitle("GestorT8");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(800, 600));
        setResizable(false);

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        setContentPane(contentPanel);

        setLocationRelativeTo(null);
    }

    private void initializePanels() throws SQLException {
        contentPanel.add(new MainPanel(this), "main");
        contentPanel.add(new ProductPanel(this), "products");
        contentPanel.add(new AddProduct(this), "addProduct");
        contentPanel.add(new EditProduct(this), "editProduct");
        contentPanel.add(new SalesPanel(this), "salesPanel");
        contentPanel.add(new AddSale(this), "addSale");

        cardLayout.show(contentPanel, "main");
        pack();
    }

    public void showPanel(String panelName, Object... args) {
        if (args.length > 0 && panelName.equals("editProduct")) {
            for (Component comp : contentPanel.getComponents()) {
                if (comp instanceof EditProduct editPanel) {
                    editPanel.setData(args);
                    break;
                }
            }
        }
        cardLayout.show(contentPanel, panelName);
        pack();
    }
}