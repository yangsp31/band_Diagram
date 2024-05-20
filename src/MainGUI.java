import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Flow;
import java.util.concurrent.atomic.AtomicInteger;

public class MainGUI extends JFrame {
    private JPanel Base = new JPanel(new BorderLayout());
    private JPanel Select = new JPanel();
    private JPanel Input = new JPanel(new GridBagLayout());
    private JPanel EditPane = new JPanel(new BorderLayout());
    private JPanel SelectEditPane = new JPanel(new FlowLayout());
    private JScrollPane ShowPane;
    private JScrollPane ListPane;
    private GridBagConstraints GBC = new GridBagConstraints();
    private DefaultListModel<String> ListModel = new DefaultListModel<>();
    private JList<String> List = new JList<>(ListModel);

    private JLabel Type = new JLabel("Select Material");
    private JLabel Name = new JLabel("Name");
    private JLabel HighValue = new JLabel("High Value");
    private JLabel LowValue = new JLabel("Low Value");
    private JTextField InputName = new JTextField(20);
    private JTextField InputHighValue = new JTextField(20);
    private JTextField InputLowValue = new JTextField(20);
    private JComboBox<String> setType = new JComboBox<>(new String[]{"Metal", "Organic", "Other"});
    private JComboBox<Map.Entry<String, MaterialDTO>> setMaterial = new JComboBox<>();
    private JButton Apply = new JButton("Apply");
    private JButton Full = new JButton("Full Screen");
    private JButton Undo = new JButton("Undo");
    private JCheckBox SelectEdit = new JCheckBox("Edit");
    private JCheckBox SelectDelete = new JCheckBox("Delete");
    private JCheckBox SelectAdd = new JCheckBox("Add");


    private Map<String, MaterialDTO> DTOMap = new LinkedHashMap<>();
    private int SelectIndex = -1;
    private boolean CheckRefresh = false;
    private boolean CheckDeleteAll = false;
    private double Scale = 1.0;
    private double OriginalScale = 1.0;

    public MainGUI() {
        Select.setLayout(new BoxLayout(Select, BoxLayout.Y_AXIS));
        Input.setPreferredSize(new Dimension(240, 300));
        Select.add(Input);
        EditPane.setPreferredSize(new Dimension(240, 500));
        Select.add(EditPane);

        Show show = new Show();
        show.setPreferredSize(new Dimension(15000, 2500));
        ShowPane = new JScrollPane(show);

        List.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ListPane = new JScrollPane(List);
        ListPane.setEnabled(false);
        SelectEditPane.add(SelectDelete);
        SelectEditPane.add(SelectAdd);
        SelectEditPane.add(SelectEdit);

        EditPane.add(SelectEditPane, BorderLayout.NORTH);
        EditPane.add(ListPane, BorderLayout.CENTER);
        EditPane.add(Undo, BorderLayout.SOUTH);

        Base.add(ShowPane, BorderLayout.CENTER);
        Base.add(Select, BorderLayout.WEST);


        GBC.gridx = 0;
        GBC.gridy = 0;
        GBC.gridwidth = 2;
        GBC.anchor = GridBagConstraints.WEST;
        GBC.insets = new Insets(0, 2, 5, 0);
        Input.add(Type, GBC);

        GBC.gridy = 1;
        Input.add(setType, GBC);

        GBC.gridy = 2;
        Input.add(Name, GBC);

        GBC.gridy = 3;
        Input.add(InputName, GBC);

        GBC.gridy = 4;
        Input.add(HighValue, GBC);

        GBC.gridy = 5;
        Input.add(InputHighValue, GBC);

        GBC.gridy = 6;
        Input.add(LowValue, GBC);
        LowValue.setEnabled(false);

        GBC.gridy = 7;
        Input.add(InputLowValue, GBC);
        InputLowValue.setEnabled(false);

        GBC.gridy = 8;
        GBC.insets = new Insets(25, 2, 0, 0);
        GBC.fill = GridBagConstraints.HORIZONTAL;
        Input.add(Apply, GBC);

        GBC.gridy = 9;
        GBC.insets = new Insets(9, 2, 0, 0);
        Input.add(Full, GBC);

        Apply.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Object getComboBoxValue = setType.getSelectedItem();
                String Type = (String)getComboBoxValue;

                MaterialDTO Material = new MaterialDTO(Type, InputName.getText());

                if(!SelectDelete.isSelected() && !SelectEdit.isSelected() && !SelectAdd.isSelected())
                {
                    try {
                        if(Type.equals("Metal")) {
                            double Value = Double.parseDouble(InputHighValue.getText());

                            if (Value > 0) {
                                Value = Value * -1;
                            }

                            Material.setValue(Value);
                            DTOMap.put((ListModel.size() + 1) + ") " + InputName.getText(), Material);
                            ListModel.addElement((ListModel.size() + 1) + ") " + InputName.getText());
                        }
                        else {
                            double HighValue = Double.parseDouble(InputHighValue.getText());
                            double LowValue = Double.parseDouble(InputLowValue.getText());

                            if(HighValue > 0) {
                                HighValue = HighValue * -1;
                            }

                            if(LowValue > 0) {
                                LowValue = LowValue * -1;
                            }

                            if(HighValue < LowValue)
                            {
                                throw new IllegalArgumentException("NoValid");
                            }

                            Material.setValue(HighValue, LowValue);
                            DTOMap.put((ListModel.size() + 1) + ") " + InputName.getText(), Material);
                            ListModel.addElement((ListModel.size() + 1) + ") " + InputName.getText());
                        }
                    }
                    catch (IllegalArgumentException error) {
                        System.out.println(error);

                        JOptionPane.showMessageDialog(null, "유효한 범위의 값들이 아닙니다.", "Warning", JOptionPane.WARNING_MESSAGE);
                    }
                    catch (Exception error) {
                        System.out.println(error);

                        JOptionPane.showMessageDialog(null, "정수 또는 실수만 입력하십시오.", "Warning", JOptionPane.WARNING_MESSAGE);
                    }
                }
                else if(SelectDelete.isSelected()) {
                    int Index = List.getSelectedIndex();

                    if(Index != -1) {
                        DTOMap.remove(ListModel.getElementAt(Index));
                        ListModel.removeElementAt(Index);

                        for(int i = Index; i < ListModel.size(); i++) {
                            String DataName = DTOMap.get(ListModel.getElementAt(i)).getName();
                            DTOMap.put((i + 1) + ") " + DataName, DTOMap.get(ListModel.getElementAt(i)));
                            DTOMap.remove(ListModel.getElementAt(i));

                            ListModel.setElementAt((i + 1) + ") " + DataName, i );
                        }
                    }
                }
                else if(SelectAdd.isSelected()) {
                    int Index = List.getSelectedIndex();
                    int countIndex;

                    if(Index != -1) {
                        try {
                            if(Index == ListModel.size() - 1) {
                                throw new IllegalArgumentException("Index is End Point");
                            }

                            if(Type.equals("Metal")) {
                                double Value = Double.parseDouble(InputHighValue.getText());

                                if (Value > 0) {
                                    Value = Value * -1;
                                }

                                Material.setValue(Value);
                            }
                            else {
                                double HighValue = Double.parseDouble(InputHighValue.getText());
                                double LowValue = Double.parseDouble(InputLowValue.getText());

                                if(HighValue > 0) {
                                    HighValue = HighValue * -1;
                                }

                                if(LowValue > 0) {
                                    LowValue = LowValue * -1;
                                }

                                if(HighValue < LowValue)
                                {
                                    throw new IllegalArgumentException("Not Valid Value of HighValue and LowValue");
                                }

                                Material.setValue(HighValue, LowValue);
                            }

                            MaterialDTO saveData = DTOMap.get(ListModel.getElementAt(Index + 1));
                            DTOMap.remove(ListModel.getElementAt(Index + 1));
                            DTOMap.put((Index + 2) + ") " + Material.getName(), Material);
                            ListModel.insertElementAt((Index + 2) + ") " + Material.getName(), Index + 1);

                            for(countIndex = Index + 2; countIndex < ListModel.size() - 1; countIndex ++) {
                                MaterialDTO nextData = DTOMap.get(ListModel.getElementAt(countIndex + 1));
                                DTOMap.remove(ListModel.getElementAt(countIndex + 1));
                                DTOMap.put((countIndex + 1) + ") " + saveData.getName(), saveData);
                                ListModel.setElementAt((countIndex + 1) + ") " + saveData.getName(), countIndex);

                                saveData = nextData;
                            }

                            DTOMap.put((countIndex + 1) + ") " + saveData.getName(), saveData);
                            ListModel.setElementAt((countIndex + 1) + ") " + saveData.getName(), countIndex);
                        }
                        catch (IllegalArgumentException error) {
                            System.out.println(error.getMessage());

                            if(error.getMessage().equals("Index is End Point")) {
                                JOptionPane.showMessageDialog(null, "해당 기능은 재료 사이의 추가만 지원합니다.", "Warning", JOptionPane.WARNING_MESSAGE);
                            }
                            else {
                                JOptionPane.showMessageDialog(null, "유효한 범위의 값들이 아닙니다.", "Warning", JOptionPane.WARNING_MESSAGE);
                            }
                        }
                        catch (Exception error) {
                            System.out.println(error);

                            JOptionPane.showMessageDialog(null, "정수 또는 실수만 입력하십시오.", "Warning", JOptionPane.WARNING_MESSAGE);
                        }
                    }
                }
                else if(SelectEdit.isSelected()) {
                    int Index = List.getSelectedIndex();

                    if(Index != -1) {
                        try {
                            if(Type.equals("Metal")) {
                                double Value = Double.parseDouble(InputHighValue.getText());

                                if (Value > 0) {
                                    Value = Value * -1;
                                }

                                Material.setValue(Value);
                            }
                            else {
                                double HighValue = Double.parseDouble(InputHighValue.getText());
                                double LowValue = Double.parseDouble(InputLowValue.getText());

                                if(HighValue > 0) {
                                    HighValue = HighValue * -1;
                                }

                                if(LowValue > 0) {
                                    LowValue = LowValue * -1;
                                }

                                if(HighValue < LowValue)
                                {
                                    throw new IllegalArgumentException("Not Valid Value of HighValue and LowValue");
                                }

                                Material.setValue(HighValue, LowValue);
                            }

                            DTOMap.remove(ListModel.getElementAt(Index));
                            DTOMap.put((Index + 1) + ") " + Material.getName(), Material);

                            for(int i = Index + 1; i < ListModel.size(); i ++) {
                                MaterialDTO saveData = DTOMap.get(ListModel.getElementAt(i));
                                DTOMap.remove(ListModel.getElementAt(i));
                                DTOMap.put((i + 1) + ") " + saveData.getName(), saveData);
                            }

                            ListModel.setElementAt((Index + 1) + ") " + Material.getName(), Index);
                        }
                        catch (IllegalArgumentException error) {
                            System.out.println(error);

                            JOptionPane.showMessageDialog(null, "유효한 범위의 값들이 아닙니다.", "Warning", JOptionPane.WARNING_MESSAGE);
                        }
                        catch (Exception error) {
                            System.out.println(error);

                            JOptionPane.showMessageDialog(null, "정수 또는 실수만 입력하십시오.", "Warning", JOptionPane.WARNING_MESSAGE);
                        }
                    }
                }

                InputName.setText("");
                InputHighValue.setText("");
                InputLowValue.setText("");
                SelectDelete.setSelected(false);
                SelectAdd.setSelected(false);
                SelectEdit.setSelected(false);

                show.repaint();
            }
        });

        Full.addActionListener((new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame fullScreen = new JFrame("Full Screen");

                fullScreen.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                fullScreen.setExtendedState(JFrame.MAXIMIZED_BOTH);
                fullScreen.getContentPane().add(ShowPane);

                setVisible(false);
                fullScreen.setVisible(true);

                fullScreen.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosed(WindowEvent e) {
                        Base.add(ShowPane, BorderLayout.CENTER);
                        show.repaint();
                        setVisible(true);
                    }
                });

            }
        }));

        Undo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Object[] options = {"Refresh", "Delete All"};

                int result = JOptionPane.showOptionDialog(null, "Select an Undo Option", "Undo Option", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

                if(result == 0) {
                    InputName.setText("");
                    InputHighValue.setText("");
                    InputLowValue.setText("");
                    SelectDelete.setSelected(false);
                    SelectEdit.setSelected(false);
                    SelectIndex = -1;
                    List.clearSelection();
                    CheckRefresh = true;
                    Scale = OriginalScale;

                    show.repaint();
                }
                else if(result == 1) {
                    InputName.setText("");
                    InputHighValue.setText("");
                    InputLowValue.setText("");
                    SelectDelete.setSelected(false);
                    SelectEdit.setSelected(false);
                    SelectIndex = -1;
                    List.clearSelection();
                    DTOMap.clear();
                    ListModel.clear();
                    CheckDeleteAll = true;
                    Scale = OriginalScale;

                    show.repaint();
                }
            }
        });

        setType.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String ComboBoxValue = (String)setType.getSelectedItem();

                if(ComboBoxValue.equals("Metal")) {
                    InputHighValue.setEnabled(true);
                    LowValue.setEnabled(false);
                    InputLowValue.setEnabled(false);
                }
                else {
                    InputHighValue.setEnabled(true);
                    LowValue.setEnabled(true);
                    InputLowValue.setEnabled(true);
                }
            }
        });

        SelectDelete.addItemListener(e -> {
            if(SelectDelete.isSelected()) {
                SelectAdd.setEnabled(false);
                SelectEdit.setEnabled(false);
                List.clearSelection();
            }
            else {
                SelectAdd.setEnabled(true);
                SelectEdit.setEnabled(true);
            }
        });

        SelectAdd.addItemListener(e -> {
            if(SelectAdd.isSelected()) {
                SelectDelete.setEnabled(false);
                SelectEdit.setEnabled(false);
                List.clearSelection();
            }
            else {
                SelectDelete.setEnabled(true);
                SelectEdit.setEnabled(true);
            }
        });

        SelectEdit.addItemListener(e -> {
            if(SelectEdit.isSelected()) {
                SelectDelete.setEnabled(false);
                SelectAdd.setEnabled(false);
                List.clearSelection();
            }
            else {
                SelectDelete.setEnabled(true);
                SelectAdd.setEnabled(true);
            }
        });

        List.addListSelectionListener(e -> {
            if(!e.getValueIsAdjusting()) {
                Object Value = List.getSelectedValue();

                if(SelectEdit.isSelected() && List.getSelectedIndex() != -1)
                {
                    MaterialDTO material = DTOMap.get(Value);

                    setType.setSelectedItem(material.getType());
                    InputName.setText(material.getName());
                    InputHighValue.setText(String.valueOf(material.getHighValue() * -1));
                    InputLowValue.setText(String.valueOf(material.getLowValue() * -1));
                }

                SelectIndex = List.getSelectedIndex();
                show.repaint();
            }
        });

        Select.setPreferredSize(new Dimension(240, Base.getHeight()));
        show.setBackground(Color.WHITE);

        setTitle("Band Diagram 2.5");
        setContentPane(Base);
        setSize(1600, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private String[] Chunk(String input, int Size) {
        int length = input.length();
        int ChunkNum = (int) Math.ceil((double) length / Size);

        String[] chunk = new String[ChunkNum];

        for(int i = 0; i < ChunkNum; i++) {
            int start = i * Size;
            int end = Math.min((i + 1) * Size, length);

            chunk[i] = input.substring(start, end);
        }

        return chunk;
    }

    class Show extends JPanel implements MouseWheelListener, MouseListener, MouseMotionListener {
        private static final double ZOOM_FACTOR = 1.1;
        private Point OriginalViewPos;
        private Point dragStart;

        public Show() {
            addMouseWheelListener(this);
            addMouseListener(this);
            addMouseMotionListener(this);

            OriginalViewPos = new Point(0, 0);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            double Xpos = 300;
            double Ypos = 1;
            int NametextX = 0;
            int HightextX = 0;
            int LowtextX = 0;
            int textY = 0;
            int textWidth = 0;
            int textHeight = 0;
            int textYAbove = 0;
            int textYBelow = 0;
            int Size = 0;
            int NameTextAbove = 0;
            int xCount = 0;

            Font Namefont = new Font("Verdana", Font.BOLD, 20);
            Font Valuefont = new Font("Arial", Font.BOLD, 16);

            Graphics2D G2D = (Graphics2D) g;
            G2D.setStroke(new BasicStroke(2));
            FontMetrics NamefontMetrics = G2D.getFontMetrics(Namefont);
            FontMetrics ValuefontMetrics = G2D.getFontMetrics(Valuefont);
            G2D.scale(Scale, Scale);

            if(!(CheckRefresh || CheckDeleteAll)) {
                for (Map.Entry<String, MaterialDTO> entry : DTOMap.entrySet()) {
                    if(entry.getValue().getType().equals("Metal")) {
                        double HighValue = (Ypos - entry.getValue().getHighValue()) * 70;
                        String[] Chunks = Chunk(entry.getValue().getName(), 9);
                        Size = Chunks.length;

                        if(SelectIndex == ListModel.indexOf(entry.getKey()) && SelectIndex != -1) {
                            G2D.setColor(new Color(173, 216, 230));
                        }
                        else {
                            G2D.setColor(null);
                        }

                        G2D.drawLine((int)Xpos + xCount, (int)Math.round(HighValue), (int)Xpos + xCount + 150, (int)Math.round(HighValue));
                        G2D.setColor(Color.BLACK);

                        textHeight = NamefontMetrics.getHeight();
                        textY = (int)Math.round(HighValue) - textHeight * Size;

                        G2D.setFont(Namefont);

                        for (String chunk : Chunks) {
                            NametextX = (int) Xpos + xCount + 75 - NamefontMetrics.stringWidth(chunk) / 2;
                            G2D.drawString(chunk, NametextX, textY);
                            textY += NamefontMetrics.getHeight();
                        }

                        textWidth = ValuefontMetrics.stringWidth(String.valueOf(entry.getValue().getHighValue() * -1) + "eV");
                        textHeight = ValuefontMetrics.getHeight();
                        HightextX = (int) Xpos + xCount + 75 - textWidth / 2;
                        textY = (int)HighValue + textHeight + 5;

                        G2D.setFont(Valuefont);
                        G2D.drawString(String.valueOf(entry.getValue().getHighValue() * -1) + " eV", HightextX, textY);

                        xCount += 150;
                    }
                    else if(entry.getValue().getType().equals("Organic")) {
                        double HighValue = (Ypos - entry.getValue().getHighValue()) * 70;
                        double LowValue = (Ypos - entry.getValue().getLowValue()) * 70;
                        String[] Chunks = Chunk(entry.getValue().getName(), 10);
                        Size = Chunks.length;

                        if(Size == 1) {
                            Size -= 1;
                        }
                        else {
                            Size -= 2;
                        }

                        if(SelectIndex == ListModel.indexOf(entry.getKey()) && SelectIndex != -1) {
                            G2D.setColor(new Color(173, 216, 230));
                            G2D.fillRect((int)Xpos + xCount, (int)Math.round(HighValue), 150, (int)Math.round(LowValue - HighValue));

                            G2D.setColor(Color.BLACK);
                            G2D.drawRect((int)Xpos + xCount, (int)Math.round(HighValue), 150, (int)Math.round(LowValue - HighValue));
                        }
                        else {
                            G2D.setColor(null);
                            G2D.drawRect((int)Xpos + xCount, (int)Math.round(HighValue), 150, (int)Math.round(LowValue - HighValue));
                        }

                        HightextX = (int) Math.round(Xpos + xCount + 150.0 / 2.0 - ValuefontMetrics.stringWidth(String.valueOf(entry.getValue().getHighValue() * -1) + "eV") / 2.0);
                        LowtextX = (int) Math.round(Xpos + xCount + 150.0 / 2.0 - ValuefontMetrics.stringWidth(String.valueOf(entry.getValue().getLowValue() * -1) + "eV") / 2.0);
                        textY = ((int) Math.round(((LowValue + HighValue) / 2) - (Size * NamefontMetrics.getHeight())));
                        textYAbove = (int) Math.round(HighValue - 10);
                        textYBelow = (int) Math.round(LowValue + 20);

                        G2D.setFont(Namefont);
                        G2D.setColor(null);

                        for (String chunk : Chunks) {
                            NametextX = (int) Math.round(Xpos + xCount + 150.0 / 2.0 - NamefontMetrics.stringWidth(chunk) / 2.0);
                            G2D.drawString(chunk, NametextX, textY);
                            textY += NamefontMetrics.getHeight();
                        }

                        G2D.setFont(Valuefont);
                        G2D.drawString(String.valueOf(entry.getValue().getHighValue() * -1) + " eV", HightextX, textYAbove);
                        G2D.drawString(String.valueOf(entry.getValue().getLowValue() * -1) + " eV", LowtextX, textYBelow);

                        xCount += 150;
                    }
                    else {
                        double HighValue = (Ypos - entry.getValue().getHighValue()) * 70;
                        double LowValue = (Ypos - entry.getValue().getLowValue()) * 70;
                        String[] Chunks = Chunk(entry.getValue().getName(), 10);
                        Size = Chunks.length - 1;

                        if(SelectIndex == ListModel.indexOf(entry.getKey()) && SelectIndex != -1) {
                            G2D.setColor(new Color(173, 216, 230));
                            G2D.fillRect((int)Xpos + xCount, (int)Math.round(HighValue), 60, (int)Math.round(LowValue - HighValue));

                            G2D.setColor(Color.BLACK);
                            G2D.drawRect((int)Xpos + xCount, (int)Math.round(HighValue), 60, (int)Math.round(LowValue - HighValue));
                        }
                        else {
                            G2D.setColor(null);
                            G2D.drawRect((int)Xpos + xCount, (int)Math.round(HighValue), 60, (int)Math.round(LowValue - HighValue));
                        }

                        HightextX = (int) Math.round(Xpos + xCount + 60.0 / 2.0 - ValuefontMetrics.stringWidth(String.valueOf(entry.getValue().getHighValue() * -1) + "eV") / 2.0);
                        LowtextX = (int) Math.round(Xpos + xCount + 60.0 / 2.0 - ValuefontMetrics.stringWidth(String.valueOf(entry.getValue().getLowValue() * -1) + "eV") / 2.0);
                        textYAbove = (int) Math.round(HighValue - 10);
                        textYBelow = (int) Math.round(LowValue + 20);
                        NameTextAbove = textYAbove - NamefontMetrics.getHeight() * Size - 30;

                        G2D.setColor(null);
                        G2D.setFont(Namefont);

                        for (String chunk : Chunks) {
                            NametextX = (int) Math.round(Xpos + xCount + 60.0 / 2.0 - NamefontMetrics.stringWidth(chunk) / 2.0);
                            G2D.drawString(chunk, NametextX, NameTextAbove);
                            NameTextAbove += NamefontMetrics.getHeight();
                        }

                        G2D.setFont(Valuefont);
                        G2D.drawString(String.valueOf(entry.getValue().getHighValue() * -1) + " eV", HightextX, textYAbove);
                        G2D.drawString(String.valueOf(entry.getValue().getLowValue() * -1) + " eV", LowtextX, textYBelow);


                        xCount += 60;
                    }
                }
            }
            else {
                JViewport viewport = (JViewport) SwingUtilities.getAncestorOfClass(JViewport.class, this);
                viewport.setViewPosition(OriginalViewPos);

                if(CheckRefresh) {
                    CheckRefresh = false;
                }
                else if(CheckDeleteAll) {
                    CheckDeleteAll = false;
                }

                repaint();
            }
        }

        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            int WheelRate = e.getWheelRotation();

            if(WheelRate < 0) {
                Scale *= ZOOM_FACTOR;
            }
            else {
                Scale /= ZOOM_FACTOR;
            }

            revalidate();
            repaint();
        }

        @Override
        public void mouseClicked(MouseEvent e) {}

        @Override
        public void mousePressed(MouseEvent e) {
            dragStart = e.getPoint();
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            dragStart = null;
        }

        @Override
        public void mouseEntered(MouseEvent e) {}

        @Override
        public void mouseExited(MouseEvent e) {}

        @Override
        public void mouseDragged(MouseEvent e) {
            if (dragStart != null) {
                int dx = e.getX() - dragStart.x;
                int dy = e.getY() - dragStart.y;
                dragStart = e.getPoint();

                JViewport viewport = (JViewport) SwingUtilities.getAncestorOfClass(JViewport.class, this);

                if (viewport != null) {
                    Point viewPosition = viewport.getViewPosition();
                    viewPosition.translate(-dx, -dy);

                    int maxX = getWidth() - viewport.getWidth();
                    int maxY = getHeight() - viewport.getHeight();

                    viewPosition.x = Math.max(0, Math.min(maxX, viewPosition.x));
                    viewPosition.y = Math.max(0, Math.min(maxY, viewPosition.y));

                    viewport.setViewPosition(viewPosition);
                }
            }
        }

        @Override
        public void mouseMoved(MouseEvent e) {}
    }
}
