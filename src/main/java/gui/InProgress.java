package gui;

import utils.Styles;
import javax.swing.*;
import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

public class InProgress extends JPanel {
    private final MainWindow mainWindow;
    private JPanel backPanel;
    private JPanel mainContentPanel;
    private JProgressBar progressBar;
    private Timer animationTimer;
    private int progressValue = 0;
    private boolean progressDirection = true;
    private final Color MENU_COLOR = new Color(52, 73, 94);
    private final Color FONT_COLOR = new Color(255, 255, 255);
    private final Color BACKGROUND_COLOR = new Color(248, 249, 250);

    public InProgress(MainWindow mainWindow) {
        if (mainWindow == null) {
            throw new IllegalArgumentException("mainWindow cannot be null");
        }
        this.mainWindow = mainWindow;
        initializePanel();
        createPanels();
        layoutComponents();
        setupStyles();
        startAnimation();
    }

    private void initializePanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
    }

    private void createPanels() {
        createBackPanel();
        createMainContentPanel();
    }

    private void createBackPanel() {
        backPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        backPanel.setBackground(Color.WHITE);

        JButton goBackButton = Styles.createStyledButton("Volver", MENU_COLOR, 100, 30, new Color(70, 83, 97));
        goBackButton.addActionListener(_ -> mainWindow.showPanel("main"));
        backPanel.add(goBackButton);
    }

    private void createMainContentPanel() {
        mainContentPanel = new JPanel();
        mainContentPanel.setLayout(new BoxLayout(mainContentPanel, BoxLayout.Y_AXIS));
        mainContentPanel.setBackground(Color.WHITE);

        createConstructionContent();
    }

    private void createConstructionContent() {
        JPanel constructionPanel = new JPanel();
        constructionPanel.setLayout(new BoxLayout(constructionPanel, BoxLayout.Y_AXIS));
        constructionPanel.setBackground(Color.WHITE);
        constructionPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 40, 40));

        JLabel titleLabel = new JLabel("Trabajando en ello");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(MENU_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel progressPanel = new JPanel();
        progressPanel.setBackground(Color.WHITE);
        progressBar = new JProgressBar(0, 100);
        progressBar.setPreferredSize(new Dimension(400, 8));
        progressBar.setBackground(new Color(236, 240, 241));
        progressBar.setForeground(MENU_COLOR);
        progressBar.setBorderPainted(false);
        progressPanel.add(progressBar);

        JPanel featuresPanel = createFeaturesPanel();
        featuresPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel etaLabel = new JLabel("Fecha estimada de finalización: Ya si eso...");
        etaLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        etaLabel.setForeground(new Color(149, 165, 166));
        etaLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        constructionPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        constructionPanel.add(titleLabel);
        constructionPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        constructionPanel.add(progressPanel);
        constructionPanel.add(Box.createRigidArea(new Dimension(0, 40)));
        constructionPanel.add(featuresPanel);
        constructionPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        constructionPanel.add(etaLabel);

        mainContentPanel.add(Box.createVerticalGlue());
        mainContentPanel.add(constructionPanel);
        mainContentPanel.add(Box.createVerticalGlue());
    }

    private JPanel createFeaturesPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(1, 3, 20, 0));
        panel.setBackground(Color.WHITE);
        panel.setMaximumSize(new Dimension(800, 150));

        panel.add(createFeatureCard("Gestión de Categorías",
                "Organiza y clasifica tus productos de manera intuitiva"));
        panel.add(createFeatureCard("Estadísticas",
                "Análisis detallado por categoría"));
        panel.add(createFeatureCard("Sincronización",
                "Actualización automática de productos"));

        return panel;
    }

    private JPanel createFeatureCard(String title, String description) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(BACKGROUND_COLOR);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, MENU_COLOR),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setForeground(MENU_COLOR);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel descLabel = new JLabel("<html><div style='width: 180px;'>" + description + "</div></html>");
        descLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(titleLabel);
        card.add(Box.createRigidArea(new Dimension(0, 8)));
        card.add(descLabel);

        return card;
    }

    private void layoutComponents() {
        add(backPanel, BorderLayout.NORTH);
        add(mainContentPanel, BorderLayout.CENTER);
    }

    private void setupStyles() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

    private void startAnimation() {
        animationTimer = new Timer();
        animationTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (progressDirection) {
                    progressValue += 2;
                    if (progressValue >= 100) progressDirection = false;
                } else {
                    progressValue -= 2;
                    if (progressValue <= 0) progressDirection = true;
                }
                SwingUtilities.invokeLater(() -> progressBar.setValue(progressValue));
            }
        }, 0, 50);
    }

    public void dispose() {
        if (animationTimer != null) {
            animationTimer.cancel();
        }
    }
}