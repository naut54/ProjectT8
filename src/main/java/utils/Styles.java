package utils;

import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class Styles {
    /**
     * Aplica un diseño personalizado a un botón para que tenga bordes redondeados.
     *
     * @param btn El botón al que se le aplicará el estilo con bordes redondeados.
     *
     * @since 16
     */
    public static void setRoundedButton(JButton btn) {
        btn.setUI(new StyledButtonUI());
    }

    /**
     * Aplica un efecto de cambio de color al botón cuando el cursor del mouse entra o sale de su área.
     *
     * @param btn El botón al que se le aplicará el efecto hover.
     *
     * @since 16
     */
    public static void setHoverButton(JButton btn) {
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(70, 83, 97));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(52, 73, 94));
            }
        });
    }

    /**
     * Establece un tamaño personalizado para el botón especificado.
     *
     * @param btn    El botón al que se aplicará el tamaño.
     * @param width  El ancho deseado para el botón.
     * @param height La altura deseada para el botón.
     *
     * @since 16
     */
    public static void setSizeButton(JButton btn, int width, int height) {
        btn.setPreferredSize(new Dimension(width, height));
    }

    /**
     * Crea un botón con estilo personalizado.
     *
     * @param text   El texto que se mostrará en el botón.
     * @param color  El color de fondo del botón.
     * @param width  El ancho deseado del botón.
     * @param height La altura deseada del botón.
     * @return Un botón configurado con el texto, color, tamaño y estilos personalizados especificados.
     *
     * @since 16
     */
    public static JButton createStyledButton(String text, Color color, int width, int height) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        try {
            setRoundedButton(button);
            setHoverButton(button);
            button.setPreferredSize(new Dimension(width, height));
            button.setFont(new Font("Arial", Font.BOLD, 16));
        } catch (Exception e) {
            System.err.println("Error al aplicar estilo al botón: " + e.getMessage());
        }
        return button;
    }

    /**
     * Aplica un estilo personalizado a un elemento de menú (JMenuItem).
     *
     * @param itm El elemento de menú al que se aplicará el estilo personalizado.
     *
     * @since 16
     */
    public static void setItemStyle(JMenuItem itm) {
        itm.setBackground(new Color(52, 73, 94));
        itm.setForeground(Color.WHITE);
        itm.setRolloverEnabled(true);
        itm.setOpaque(true);
    }

    /**
     * Aplica un estilo personalizado a un campo de texto (JTextField) estableciendo su tamaño preferido.
     *
     * @param txt    El campo de texto al que se aplicará el estilo.
     * @param width  El ancho deseado para el campo de texto.
     * @param height La altura deseada para el campo de texto.
     *
     * @since 16
     */
    public static void setTextFieldsStyle(JTextField txt, int width, int height) {
        txt.setPreferredSize(new Dimension(width, height));
    }

    /**
     * Aplica un estilo personalizado a una tabla y a su cabecera.
     *
     * @param table  La tabla a la que se aplicará el estilo.
     * @param header La cabecera de la tabla a la que se aplicará el estilo.
     * @param color  El color de fondo que se aplicará a la cabecera de la tabla.
     *
     * @since 16
     */
    public static void setTableStyle(JTable table, JTableHeader header, Color color) {
        header.setBackground(color);
        header.setForeground(Color.WHITE);

        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value,
                        isSelected, hasFocus, row, column);

                setHorizontalAlignment(SwingConstants.CENTER);
                setBorder(BorderFactory.createEmptyBorder());
                setBackground(color);
                setForeground(Color.WHITE);

                setFont(new Font("Arial", Font.BOLD, 14));

                return c;
            }
        };

        header.setDefaultRenderer(headerRenderer);
        header.setReorderingAllowed(false);
        header.setResizingAllowed(false);
        header.setBorder(BorderFactory.createEmptyBorder());

        table.getTableHeader().setReorderingAllowed(false);
        table.setAutoCreateRowSorter(true);
        header.setPreferredSize(new Dimension(header.getWidth(), 30));
    }

    /**
     * Aplica un estilo personalizado a las filas de una tabla (JTable).
     * Establece un renderer para las celdas, define la altura de las filas
     * y elimina las líneas de separación entre celdas.
     *
     * @param table La tabla a la que se le aplicará el estilo de filas.
     *
     * @since 16
     */
    public static void setRowStyle(JTable table) {
        CustomTableCellRenderer renderer = new CustomTableCellRenderer();
        table.setDefaultRenderer(Object.class, renderer);

        table.setRowHeight(40);
        table.setShowHorizontalLines(false);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 0));
    }

    /**
     * Esta clase proporciona una implementación personalizada de {@link BasicButtonUI}
     * para personalizar la apariencia de botones en una interfaz gráfica.
     */
    static class StyledButtonUI extends BasicButtonUI {
        /**
         * Instala la interfaz de usuario personalizada para el componente especificado.
         * Configura el botón para que sea transparente y establece un borde vacío.
         *
         * @param c El componente al que se le aplicará esta interfaz de usuario personalizada.
         *
         * @since 16
         */
        @Override
        public void installUI(JComponent c) {
            super.installUI(c);
            AbstractButton button = (AbstractButton) c;
            button.setOpaque(false);
            button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        }

        /**
         * Pinta el componente utilizando un fondo personalizado y el estilo general del botón.
         *
         * @param g El objeto {@link Graphics} que se utiliza para dibujar.
         * @param c El componente que se pintará.
         *
         * @since 16
         */
        @Override
        public void paint(Graphics g, JComponent c) {
            AbstractButton b = (AbstractButton) c;
            paintBackground(g, b);
            super.paint(g, c);
        }

        /**
         * Pinta el fondo del componente con bordes redondeados, usando el color de fondo del componente.
         *
         * @param g El objeto {@link Graphics} que se utiliza para dibujar.
         * @param c El componente al que se le aplicará el fondo personalizado.
         *
         * @since 16
         */
        private void paintBackground(Graphics g, JComponent c) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = c.getWidth();
            int height = c.getHeight();
            int radius = 15;

            g2.setColor(c.getBackground());
            g2.fillRoundRect(0, 0, width - 1, height - 1, radius, radius);
        }
    }

    /**
     * Esta clase extiende {@link DefaultTableCellRenderer} para proporcionar un estilo personalizado
     * a las celdas de una tabla (JTable). Permite:
     * <ul>
     *     <li>Establecer un color de fondo alternado entre las filas.</li>
     *     <li>Configurar colores específicos para la columna 5 dependiendo del valor ("Activo" o "Inactivo").</li>
     *     <li>Definir estilos visuales personalizados tanto para filas seleccionadas como no seleccionadas.</li>
     *     <li>Agregar un borde inferior ligero en cada celda para separar las filas claramente.</li>
     * </ul>
     * Esto mejora la presentación visual de la tabla y facilita su lectura.
     *
     * @since 16
     */
    static class CustomTableCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            JLabel label = (JLabel) super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);

            if (row % 2 == 0) {
                c.setBackground(Color.WHITE);
            } else {
                c.setBackground(new Color(245, 245, 245));
            }

            if (column == 5) {
                String estado = value.toString();
                if (estado.equalsIgnoreCase("Activo")) {
                    setForeground(new Color(40, 167, 69));
                } else if (estado.equalsIgnoreCase("Inactivo")) {
                    setForeground(new Color(220, 53, 69));
                }
            } else {
                setForeground(Color.BLACK);
            }

            if (isSelected) {
                label.setBackground(table.getSelectionBackground());
                label.setForeground(table.getSelectionForeground());
            } else {
                label.setBackground(table.getBackground());
                label.setForeground(table.getForeground());
            }

            setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)));
            setHorizontalAlignment(SwingConstants.CENTER);

            return c;
        }
    }
}
