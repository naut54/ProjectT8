package edumdev;

import gui.MainWindow;

import javax.swing.*;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws SQLException {
//        Product miProducto = new Product("Test2", "Descripcion", 600, 4002, 1);
        SwingUtilities.invokeLater(() -> {
            MainWindow mainWindow = null;
            try {
                mainWindow = new MainWindow();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            mainWindow.setVisible(true);
        });
    }
}