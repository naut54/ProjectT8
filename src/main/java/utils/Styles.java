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
    public static void setHoverButton(JButton btn, Color hoverColor, Color normalColor) {
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(hoverColor);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(normalColor);
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
    public static JButton createStyledButton(String text, Color color, int width, int height, Color hoverColor) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        try {
            setRoundedButton(button);
            setHoverButton(button, hoverColor, color);
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
     * Aplica un estilo personalizado al encabezado de una tabla (JTable).
     *
     * @param table           La tabla a la que se le aplicará el estilo.
     * @param header          El encabezado de la tabla.
     * @param backgroundColor El color de fondo del encabezado.
     * @param foregroundColor El color del texto del encabezado.
     * @param font            La fuente utilizada para el texto del encabezado.
     * @param headerHeight    La altura preferida para el encabezado.
     * @param alignment       La alineación horizontal del texto dentro de las celdas del encabezado.
     * @since 16
     */
    public static void setTableStyle(JTable table, JTableHeader header,
                                     Color backgroundColor, Color foregroundColor,
                                     Font font, int headerHeight, int alignment) {
        header.setBackground(backgroundColor);
        header.setForeground(foregroundColor);

        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value,
                        isSelected, hasFocus, row, column);

                // Verifica si la alineación es válida
                if (alignment == SwingConstants.LEFT || alignment == SwingConstants.CENTER || alignment == SwingConstants.RIGHT) {
                    setHorizontalAlignment(alignment);
                } else {
                    // Usa una alineación predeterminada en caso de valor inválido
                    setHorizontalAlignment(SwingConstants.LEFT);
                }

                setBorder(BorderFactory.createEmptyBorder());
                setBackground(backgroundColor);
                setForeground(foregroundColor);
                setFont(font);

                return c;
            }
        };

        header.setDefaultRenderer(headerRenderer);
        header.setReorderingAllowed(false);
        header.setResizingAllowed(false);
        header.setBorder(BorderFactory.createEmptyBorder());

        table.getTableHeader().setReorderingAllowed(false);
        table.setAutoCreateRowSorter(true);
        header.setPreferredSize(new Dimension(header.getWidth(), headerHeight));
    }

    /**
     * Aplica un estilo personalizado a las filas de una tabla (JTable) con características específicas:
     * <ul>
     *     <li>Colores alternados para filas pares e impares.</li>
     *     <li>Colores personalizados para texto según el valor en una columna de estado específica.</li>
     *     <li>Altura personalizada para cada fila de la tabla.</li>
     *     <li>Estilo de borde para las celdas de la tabla.</li>
     * </ul>
     *
     * @param table             La tabla (JTable) a la que se aplicará el estilo.
     * @param evenRowColor      El color de fondo para las filas pares.
     * @param oddRowColor       El color de fondo para las filas impares.
     * @param activeTextColor   El color del texto cuando el estado es "Activo".
     * @param inactiveTextColor El color del texto cuando el estado es "Inactivo".
     * @param defaultTextColor  El color del texto para el resto de celdas.
     * @param rowHeight         La altura personalizada para las filas.
     * @param borderColor       El color del borde de las celdas.
     * @param statusColumn      El índice de la columna que representa el estado (para aplicar colores de texto).
     * @param alignment         Alineación horizontal del texto en las celdas.
     * @since 16
     */
    public static void setRowStyle(JTable table, Color evenRowColor, Color oddRowColor,
                                   Color activeTextColor, Color inactiveTextColor,
                                   Color defaultTextColor, int rowHeight,
                                   Color borderColor, int statusColumn, int alignment) {

        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value,
                        isSelected, hasFocus, row, column);

                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? evenRowColor : oddRowColor);

                    if (column == statusColumn && value != null) {
                        String estado = value.toString();
                        setForeground(estado.equalsIgnoreCase("Activo") ?
                                activeTextColor :
                                estado.equalsIgnoreCase("Inactivo") ?
                                        inactiveTextColor : defaultTextColor);
                    } else {
                        setForeground(defaultTextColor);
                    }
                }

                setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, borderColor));
                setHorizontalAlignment(alignment);

                return c;
            }
        };

        table.setDefaultRenderer(Object.class, renderer);
        table.setRowHeight(rowHeight);
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
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            JLabel label = (JLabel) super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);

            if (row % 2 == 0) {
                c.setBackground(Color.WHITE);
            } else {
                c.setBackground(new Color(245, 245, 245));
            }

            if (column == 6) {
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
