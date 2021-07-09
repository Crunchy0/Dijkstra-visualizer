import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

class AutoMode extends Thread{
    private Solver solver;
    private CustomLogger logger;
    private TextArea textArea;
    private GPanel canvas;
    private JToggleButton autoButton;
    private JButton stepButton;
    private int period;
    private boolean active = true;
    private boolean alive = true;

    public AutoMode(Solver sol, CustomLogger log, TextArea ta, GPanel canvas, JToggleButton ab, JButton sb, int period){
        super();
        this.solver = sol;
        this.logger = log;
        this.textArea = ta;
        this.canvas = canvas;
        this.autoButton = ab;
        this.stepButton = sb;
        this.period = period;
    }

    public void run() {
        try {
            boolean running = solver.step(logger);
            canvas.getParent().repaint();
            while (running) {
                synchronized (this){
                    if(!active){
                        wait();
                    }
                }
                if(!alive){
                    alive = true;
                    return;
                }
                textArea.append(logger.getNextMessage() + "\n\n");
                try {
                    Thread.sleep(period);
                    synchronized (this){
                        if(!active){
                            wait();
                        }
                    }
                    if(!alive){
                        alive = true;
                        return;
                    }
                    running = solver.step(logger);
                    canvas.getParent().repaint();
                } catch (InterruptedException e) {
                    interrupt();
                }
            }
        }
        catch(InterruptedException e){
            e.printStackTrace();
        }
        String results = "";
        for(String s : solver.results()) {
            results = results + s + "\n";
        }
        textArea.append("\nИтоги:\n" + results);
        autoButton.setSelected(false);
        autoButton.setEnabled(false);
        stepButton.setEnabled(false);
    }

    public void disable(){
        this.active = false;
    }

    public void enable(){
        this.active = true;
        synchronized (this) {
            notify();
        }
    }

    public void kill(){
        alive = false;
        synchronized (this){
            notify();
        }
    }
}

public class Window extends JFrame{
    private AutoMode thr;
    private final Boolean edgeChosen = false;
    private final Solver solver = new Solver();
    private final  CustomLogger logger = new CustomLogger(10);
    private final JPanel rootPanel = new JPanel();
    private final JMenuItem saveButton = new JMenuItem("Сохранить");
    private final JMenuItem loadButton = new JMenuItem("Загрузить");
    private final JPanel annotationsPanel = new JPanel();
    private final JPanel bottomPanel = new JPanel();
    private final JPanel settingsPanel = new JPanel();
    private final GPanel canvasPanel = new GPanel(solver);
    private final TextArea textArea = new TextArea();
    private final JTextField textField = new JTextField();
    private final JLabel infoLabel = new JLabel("Информация");
    private final JButton approveButton = new JButton("Подтвердить");
    private final JButton setTimeButton = new JButton("Задать интервал");
    private final JButton setButton = new JButton("Применить");
    private final JButton deleteButton = new JButton("Удалить");
    private final JToggleButton autoButton = new JToggleButton("Авто");
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
        textArea.setText("");
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

        beginButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                //canvasPanel.solverInit();
                solver.setInit(1);
                beginButton.setEnabled(false);
                resetButton.setEnabled(true);
                clearButton.setEnabled(false);
                autoButton.setEnabled(true);
                stepButton.setEnabled(true);
                saveButton.setEnabled(true);//Было false
                loadButton.setEnabled(true);//Было false
                onEdgeUnchoice();
                canvasPanel.getParent().repaint();
            }
        });

        resetButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                solver.reset();
                if(thr != null && thr.isAlive()){
                    thr.kill();
                    thr.enable();
                }
                textArea.setText("");
                canvasPanel.getParent().repaint();
                resetButton.setEnabled(false);
                beginButton.setEnabled(true);
                clearButton.setEnabled(true);
                autoButton.setEnabled(false);
                autoButton.setSelected(false);
                stepButton.setEnabled(false);
                saveButton.setEnabled(true);
                loadButton.setEnabled(true);
            }
        });

        approveButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                canvasPanel.setMainVertex();
            }
        });

        setButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                canvasPanel.setEdgeWeight(Integer.parseInt(textField.getText()));
            }
        });

        deleteButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                infoLabel.setText("Информация");
                deleteButton.setVisible(false);
                textField.setVisible(false);
                setButton.setVisible(false);
                canvasPanel.deleteVertex();
                canvasPanel.deleteEdge();
            }
        });

        autoButton.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange() == ItemEvent.SELECTED){
                    stepButton.setEnabled(false);
                    if(thr == null){
                        thr = new AutoMode(solver, logger, textArea, canvasPanel, autoButton, stepButton, 2000);
                    }
                    if(thr.isAlive()) {
                        thr.enable();
                    }
                    else{
                        thr = new AutoMode(solver, logger, textArea, canvasPanel, autoButton, stepButton, 2000);
                        thr.start();
                    }
                }
                else if(e.getStateChange() == ItemEvent.DESELECTED){
                    thr.disable();
                    stepButton.setEnabled(true);
                }
            }
        });

        clearButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                onGraphEmpty();
                onEdgeUnchoice();
                solver.clear();
                canvasPanel.clear();
            }
        });

        closeButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                if(thr != null && thr.isAlive()){
                    thr.kill();
                }
                dispose();
            }
        });

        stepButton.setEnabled(false);
        stepButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (stepButton.isEnabled()) {
                    super.mouseClicked(e);
                boolean running = solver.step(logger);
                if (!running) {
                    if (thr != null && thr.isAlive()) {
                        thr.kill();
                    }
                    String results = "";
                    for (String s : solver.results()) {
                        results = results + s + "\n\n";
                    }
                    textArea.append("\nИтоги:\n" + results);//Добавляется строка вместо установки исходного текста
                    //textArea.setText(textArea.getText() + "\nИтоги:\n" + results);
                    autoButton.setEnabled(false);
                    stepButton.setEnabled(false);
                } else {
                    textArea.append(logger.getNextMessage() + "\n");//Вместо установки нового текста к исходному
                    //textArea.setText(textArea.getText() + logger.getNextMessage() + "\n");//добавляется строка
                }
                canvasPanel.getParent().repaint();
            }
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
        saveButton.setEnabled(false);
        beginButton.setEnabled(false);
        clearButton.setEnabled(false);
        approveButton.setVisible(false);
        setTimeButton.setVisible(false);
        setButton.setVisible(false);
        deleteButton.setVisible(false);
        textField.setVisible(false);
        autoButton.setEnabled(false);
        stepButton.setEnabled(false);
        resetButton.setEnabled(false);
        pack();
        setVisible(true);
    }

    private void createMenu(){
        // Инициализация меню

        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("Файл");
        loadButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                canvasPanel.load();
            }
        });
        saveButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                canvasPanel.save();
            }
        });
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

    public void onVertexChoice(int id){
        infoLabel.setText("Выбрана вершина " + id);
        deleteButton.setVisible(true);
    }

    public void onEdgeChoice(int weight){
        infoLabel.setText("<html>Задать вес ребра /<br>удалить ребро</html>");
        deleteButton.setVisible(true);
        textField.setText(Integer.toString(weight));
        textField.setVisible(true);
        setButton.setVisible(true);
    }

    public void onEdgeUnchoice(){
        infoLabel.setText("Информация");
        textField.setVisible(false);
        setButton.setVisible(false);
        deleteButton.setVisible(false);
    }

    public void onGraphNotEmpty(){
        saveButton.setEnabled(true);
        beginButton.setEnabled(true);
        clearButton.setEnabled(true);
    }
    public void onGraphEmpty(){
        saveButton.setEnabled(false);
        beginButton.setEnabled(false);
        clearButton.setEnabled(false);
    }

    public static void main(String[] args){
        new Window();
    }
}
