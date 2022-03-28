import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextEditor extends JFrame {
    String filePath = "";
    boolean useRegex = false;
    boolean isNewFile;
    int newFileCounter = 1;
    String regex;
    Pattern pattern = Pattern.compile("");
    Matcher matcher;
    boolean isTextChanged;
    ArrayList<Integer> searchStartIndex = new ArrayList<>();
    ArrayList<Integer> searchEndIndex = new ArrayList<>();
    int iterator;

    JTextArea textArea;
    JTextField textField;
    JCheckBox useRegexCBx;
    JFileChooser chooser;





    public TextEditor() {
        super("Text Editor");
        createGUI();
    }

    private void createGUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        initMenu();
        initComponents();
        newFile();

        setVisible(true);
    }

    private void initComponents() {

        var saveIcon = new ImageIcon("src/images/icons8-save-30.png");
        var openIcon = new ImageIcon("src/images/icons8-opened-folder-30.png");
        var searchIcon = new ImageIcon("src/images/icons8-search-30.png");
        var nextSearchResultIcon = new ImageIcon("src/images/icons8-chevron-right-30.png");
        var previousSearchResultIcon = new ImageIcon("src/images/icons8-chevron-left-30.png");

        var saveBtn = new JButton();
        saveBtn.setName("SaveButton");
        //saveBtn.setText("Save");
        saveBtn.setIcon(saveIcon);
        saveBtn.addActionListener(l -> saveFile());

        var openBtn = new JButton();
        openBtn.setName("OpenButton");
        //openBtn.setText("Open");
        openBtn.setIcon(openIcon);
        openBtn.addActionListener(l -> chooseFile());

        textField = new JTextField(25);
        textField.setName("SearchField");
        textField.getDocument().addDocumentListener(new MyDocumentListener());

        var searchBtn = new JButton();
        searchBtn.setName("StartSearchButton");
        //searchBtn.setText("Search");
        searchBtn.setIcon(searchIcon);
        searchBtn.addActionListener(l -> searchText());

        var previousResultBtn = new JButton();
        previousResultBtn.setName("PreviousMatchButton");
        //previousResultBtn.setText("<");
        previousResultBtn.setIcon(previousSearchResultIcon);
        previousResultBtn.addActionListener(l -> previousSearchResult());

        var nextResultBtn = new JButton();
        nextResultBtn.setName("NextMatchButton");
        //nextResultBtn.setText(">");
        nextResultBtn.setIcon(nextSearchResultIcon);
        nextResultBtn.addActionListener(l -> nextSearchResult());

        useRegexCBx = new JCheckBox();
        useRegexCBx.setName("UseRegExCheckbox");
        useRegexCBx.setText("Use Regex");
        useRegexCBx.addActionListener(l -> {
            useRegex = useRegexCBx.isSelected();
            setPattern();
            System.out.println("useRegex = " + useRegex);
        });

        File desktop = new File(FileSystemView.
                getFileSystemView().getDefaultDirectory().getAbsolutePath() + File.separator
                + "Desktop");
        chooser = new JFileChooser(desktop);
        chooser.setName("FileChooser");
        FileNameExtensionFilter filter = new FileNameExtensionFilter("text file","txt");
        chooser.setDialogTitle("Select a txt file");
        chooser.setFileFilter(filter);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        add(chooser);
        chooser.setVisible(false);

        var panel = new JPanel();
//        panel.setBounds(100, 20, 260, 260);
        panel.setLayout(new FlowLayout(FlowLayout.LEADING));
        panel.add(openBtn);
        panel.add(saveBtn);
        panel.add(textField);
        panel.add(searchBtn);
        panel.add(previousResultBtn);
        panel.add(nextResultBtn);
        panel.add(useRegexCBx);
        panel.setComponentOrientation(
                ComponentOrientation.LEFT_TO_RIGHT);
        add(panel,BorderLayout.PAGE_START);



        textArea = new JTextArea(20, 20);
        textArea.setLineWrap(true);
        textArea.setName("TextArea");
        textArea.getDocument().addDocumentListener(new MyDocumentListener());

        var scrollPane = new JScrollPane(textArea);
        scrollPane.setName("ScrollPane");
        scrollPane.setBounds(10,10, 480, 480);
        scrollPane.setWheelScrollingEnabled(true);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        this.getContentPane().add(scrollPane, BorderLayout.CENTER);

        var leftMargin = new JPanel();
        leftMargin.setSize(10, 480);
        getContentPane().add(leftMargin, BorderLayout.WEST);

        var rightMargin = new JPanel();
        rightMargin.setSize(10, 480);
        getContentPane().add(rightMargin, BorderLayout.EAST);

        var bottomMargin = new JPanel();
        bottomMargin.setSize(500, 10);
        getContentPane().add(bottomMargin, BorderLayout.PAGE_END);


    }

    private void initMenu() {
        var menuBar = new JMenuBar();

        var fileMenu = new JMenu();
        fileMenu.setName("MenuFile");
        fileMenu.setText("File");

        var newFileMenuItem = new JMenuItem();
        newFileMenuItem.setName("MenuNewFile");
        newFileMenuItem.setText("New");
        newFileMenuItem.addActionListener(l -> newFile());

        var openMenuItem = new JMenuItem();
        openMenuItem.setName("MenuOpen");
        openMenuItem.setText("Open");
        openMenuItem.addActionListener(l -> chooseFile());

        var saveMenuItem = new JMenuItem();
        saveMenuItem.setName("MenuSave");
        saveMenuItem.setText("Save");
        saveMenuItem.addActionListener(l -> saveFile());

        var exitMenuItem = new JMenuItem();
        exitMenuItem.setName("MenuExit");
        exitMenuItem.setText("exit");
        exitMenuItem.addActionListener(l -> System.exit(0));

        var separateMenuBar = new JSeparator();

        var searchMenu = new JMenu();
        searchMenu.setName("MenuSearch");
        searchMenu.setText("Search");

        var startSearchMenuItem = new JMenuItem();
        startSearchMenuItem.setName("MenuStartSearch");
        startSearchMenuItem.setText("Start search");
        startSearchMenuItem.addActionListener(l -> searchText());

        var previousSearchMenuItem = new JMenuItem();
        previousSearchMenuItem.setName("MenuPreviousMatch");
        previousSearchMenuItem.setText("Previous search");
        previousSearchMenuItem.addActionListener(l -> previousSearchResult());

        var nextMatchMenuItem = new JMenuItem();
        nextMatchMenuItem.setName("MenuNextMatch");
        nextMatchMenuItem.setText("Next mach");
        newFileMenuItem.addActionListener(l -> newFile());

        var useRegexMenuItem = new JMenuItem();
        useRegexMenuItem.setName("MenuUseRegExp");
        useRegexMenuItem.setText("Use regular expressions");
        useRegexMenuItem.addActionListener(l -> {
            useRegex = !useRegex;
            useRegexCBx.setSelected(useRegex);
            setPattern();
            System.out.println("useRegex = " + useRegex);
        });

        fileMenu.add(newFileMenuItem);
        fileMenu.add(openMenuItem);
        fileMenu.add(saveMenuItem);
        fileMenu.add(separateMenuBar);
        fileMenu.add(exitMenuItem);

        searchMenu.add(startSearchMenuItem);
        searchMenu.add(previousSearchMenuItem);
        searchMenu.add(nextMatchMenuItem);
        searchMenu.add(useRegexMenuItem);

        menuBar.add(fileMenu);
        menuBar.add(searchMenu);

        setJMenuBar(menuBar);

    }

    @Deprecated
    private void loadFile() {
        filePath = textField.getText();
        System.out.println("load path: " + filePath);
        File file = new File(filePath);
        try {
            var reader = new FileReader(file);
            textArea.read(reader, null);
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            textArea.setText("");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void chooseFile() {
        chooser.setVisible(true);

        int returnValue = chooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            if (chooser.getSelectedFile().isFile()) {
                System.out.println("Open " + chooser.getSelectedFile());
                filePath = chooser.getSelectedFile().getAbsolutePath();
                File file = new File(filePath);
                System.out.println("chose " + file.getAbsolutePath());
                try {
                    var reader = new FileReader(file);
                    textArea.read(reader, null);
                    reader.close();
                    setTitle(file.getName());
//                    System.out.println(textArea.getText());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    textArea.setText("");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void saveFile() {
        File file = new File(filePath);
        if (file.exists()) {
            if (file.isFile()) {
                System.out.println("Save " + file.getName());
                try {
                    var writer = new FileWriter(filePath);
                    writer.write(textArea.getText());
                    writer.close();
//                    System.out.println(textArea.getText());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            try {
                System.out.println("Create new file: " + file.getName());
                if (file.createNewFile()) {
                    var writer = new FileWriter(filePath);
                    writer.write(textArea.getText());
                    writer.close();
                    System.out.println(textArea.getText());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private void newFile() {
        textArea.setText("");
        isNewFile = true;
        filePath = FileSystemView.
                getFileSystemView().getDefaultDirectory().getAbsolutePath() + File.separator
                + "Desktop" + File.separator + "untitled" + newFileCounter + ".txt";
        newFileCounter++;
        File file = new File(filePath);
        setTitle(file.getName());
    }

    private boolean setPattern() {
//        System.out.println("Set pattern: " + regex);
        regex = textField.getText();
        if ("".equals(regex)) {
            return false;
        }

        if (!isTextChanged) {
            System.out.println("No text changed. Will not restart search");
            return false;
        }

        if (useRegex) {
            pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
            System.out.println("Search regex, case insensitive");
        } else {
            pattern = Pattern.compile(regex, Pattern.LITERAL);
            System.out.println("Search plain text");
        }

        matcher = pattern.matcher(textArea.getText());
        searchStartIndex.clear();
        searchEndIndex.clear();
        while(matcher.find()) {
            searchStartIndex.add(matcher.start());
            searchEndIndex.add(matcher.end());
        }

        isTextChanged = false;
        return true;
    }

    private void searchText() {

        System.out.println("\nSearch text: " + regex);

        if (setPattern()) {nextSearchResult();}

    }

    private void previousSearchResult() {

        if (iterator == 0) {
            iterator = searchStartIndex.size();
        }
        iterator--;
        textArea.setCaretPosition(searchEndIndex.get(iterator));
        textArea.select(searchStartIndex.get(iterator), searchEndIndex.get(iterator));
        textArea.grabFocus();
    }

    private void nextSearchResult() {
        iterator++;
        if (iterator == searchStartIndex.size()) {
            iterator = 0;
        }
        textArea.setCaretPosition(searchEndIndex.get(iterator));
        textArea.select(searchStartIndex.get(iterator), searchEndIndex.get(iterator));
        textArea.grabFocus();
    }

    private class MyDocumentListener implements DocumentListener {

        @Override
        public void insertUpdate(DocumentEvent documentEvent) {
            isTextChanged = true;
            System.out.println("New text inserted");
            searchStartIndex.clear();
        }

        @Override
        public void removeUpdate(DocumentEvent documentEvent) {
            isTextChanged = true;
            System.out.println("New text removed");
            searchStartIndex.clear();
        }

        @Override
        public void changedUpdate(DocumentEvent documentEvent) {
        }
    }
}
