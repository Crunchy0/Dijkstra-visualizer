import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class Window extends JFrame{
    private final JPanel rootPanel = new JPanel();
    private final JPanel annotationsPanel = new JPanel();
    private final JPanel bottomPanel = new JPanel();
    private final JPanel settingsPanel = new JPanel();
    private final GPanel canvasPanel = new GPanel();
    private final TextArea textArea = new TextArea();
    private final JTextField textField = new JTextField();
    private final JLabel infoLabel = new JLabel("Информация");
    private final JButton approveButton = new JButton("Подтвердить");
    private final JButton setTimeButton = new JButton("Задать интервал");
    private final JButton setButton = new JButton("Применить");
    private final JButton deleteButton = new JButton("Удалить");
    private final JButton autoButton = new JButton("Авто");
    private final JButton stepButton = new JButton("Шаг");
    private final JButton beginButton = new JButton("Начать");
    private final JButton resetButton = new JButton("Сброс");
    private final JButton clearButton = new JButton("Очистить");
    private final JButton closeButton = new JButton("Закрыть");

    public Window(){
        super();
        init();
    }

    private void init(){
        // Ограничители для раскладки GridBagLayout

        GridBagConstraints gbc = new GridBagConstraints();

        // Настройка компонентов 3 уровня (кнопок, текстовых полей и т.п.)

        textArea.setEditable(false);
        textArea.setPreferredSize(new Dimension(50, 80));
        textArea.setText("З\nА\nГ\nЛ\nУ\nШ\nК\nА");
        textArea.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
                textArea.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
            }
        });

        infoLabel.setHorizontalAlignment(SwingConstants.LEFT);
        infoLabel.setVerticalAlignment(SwingConstants.TOP);
        infoLabel.setBackground(Color.WHITE);
        infoLabel.setOpaque(true);
        infoLabel.setPreferredSize(new Dimension(-1, 80));

        closeButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                dispose();
            }
        });

        // Настройка компонентов 2 уровня (панелей)

        annotationsPanel.setLayout(new GridBagLayout());
        annotationsPanel.setBackground(new Color(223,163,159));
        annotationsPanel.setBorder(BorderFactory.createTitledBorder("Аннотации"));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1f;
        gbc.weighty = 0f;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTH;
        annotationsPanel.add(textArea, gbc);


        canvasPanel.setBorder(BorderFactory.createTitledBorder("Граф"));
        canvasPanel.setBackground(new Color(151,248,255));
        canvasPanel.setOpaque(true);


        settingsPanel.setLayout(new GridBagLayout());
        settingsPanel.setBackground(new Color(222,227,119));
        settingsPanel.setBorder(BorderFactory.createTitledBorder("Установки"));
        settingsPanel.setPreferredSize(new Dimension(200, -1));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weighty = 0.005;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.fill = GridBagConstraints.BOTH;
        settingsPanel.add(infoLabel, gbc);
        gbc.gridwidth = 2;
        gbc.gridy = 1;
        settingsPanel.add(textField, gbc);
        gbc.gridy = 2;
        settingsPanel.add(approveButton, gbc);
        gbc.gridwidth = 1;
        settingsPanel.add(setButton, gbc);
        gbc.gridx = 1;
        settingsPanel.add(deleteButton, gbc);
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        settingsPanel.add(setTimeButton, gbc);
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        gbc.weightx = 1f;
        gbc.weighty = 0.495;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.SOUTH;
        settingsPanel.add(autoButton, gbc);
        gbc.gridx = 1;
        settingsPanel.add(stepButton, gbc);


        bottomPanel.setLayout(new GridBagLayout());
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(3,3,3,3));
        bottomPanel.setBackground(new Color(140, 223, 122));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.02f;
        bottomPanel.add(beginButton, gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.02f;
        bottomPanel.add(resetButton, gbc);
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 0.88f;
        bottomPanel.add(Box.createHorizontalStrut(0), gbc);
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.weightx = 0.02f;
        bottomPanel.add(clearButton, gbc);
        gbc.gridx = 4;
        gbc.gridy = 0;
        gbc.weightx = 0.02f;
        bottomPanel.add(closeButton, gbc);

        // Настройка компонентов 1 уровня (главная панель)

        rootPanel.setLayout(new GridBagLayout());
        rootPanel.setBackground(Color.GRAY);

        createMenu();

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.9f;
        gbc.weighty = 0.01f;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets.set(3,3,0,0);
        rootPanel.add(annotationsPanel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.9f;
        gbc.weighty = 0.8f;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets.set(3,3,3,0);
        rootPanel.add(canvasPanel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0f;
        gbc.weighty = 1f;
        gbc.gridheight = 3;
        gbc.insets.set(3,3,3,3);
        gbc.fill = GridBagConstraints.BOTH;
        rootPanel.add(settingsPanel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.9f;
        gbc.weighty = 0.005f;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets.set(0,3,3,0);
        rootPanel.add(bottomPanel, gbc);

        // Настройка компонента 0 уровня (окна)

        setTitle("Алгоритм Дейкстры");
        setSize(new Dimension(1024, 1024));
        setMinimumSize(new Dimension(700,700));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        add(rootPanel);
        setContentPane(rootPanel);
        setVisible(true);
    }

    private void createMenu(){
        // Инициализация меню

        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("Файл");
        JMenuItem loadButton = new JMenuItem("Загрузить");
        JMenuItem saveButton = new JMenuItem("Сохранить");
        JMenuItem closeButton = new JMenuItem("Закрыть");
        closeButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                dispose();
                super.mouseReleased(e);
            }
        });
        fileMenu.add(loadButton);
        fileMenu.add(saveButton);
        fileMenu.add(closeButton);
        menuBar.add(fileMenu);
        menuBar.setAlignmentX(Component.RIGHT_ALIGNMENT);
        menuBar.setMaximumSize(new Dimension(800, 300));

        // Ограничители меню

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        rootPanel.add(menuBar, gbc);
    }

    public static void main(String[] args){
        new Window();
    }
}
