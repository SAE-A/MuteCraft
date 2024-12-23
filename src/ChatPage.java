import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class ChatPage extends JFrame {
    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextPane textPane;
    private JTextField txtInput;
    private String userName;
    private DataOutputStream dos;
    private Socket socket;
    private DataInputStream dis;
    private boolean isUserMessage = false;
    private boolean isMessageSending = false;

    public ChatPage(String userName, String ipAddress, String port) {
        this.userName = userName;

        setTitle("Chatting");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(868, 393);
        setLocationRelativeTo(null);
        contentPane = new JPanel();
        contentPane.setBackground(Color.WHITE);
        contentPane.setLayout(null);
        setContentPane(contentPane);

        JPanel panel_buttons = new JPanel();
        panel_buttons.setBounds(0, 0, 860, 50);
        panel_buttons.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 15));
        panel_buttons.setBackground(Color.WHITE);

        JLabel userNameLabel = new JLabel(userName);
        userNameLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 20));
        contentPane.add(userNameLabel);
       
        // 사용자 이름 레이블의 크기를 측정하고 중앙에 배치
        int labelWidth = userNameLabel.getPreferredSize().width;
        int labelHeight = userNameLabel.getPreferredSize().height;
        int centerX = (contentPane.getWidth() - labelWidth) / 2;
        int centerY = 15;
        userNameLabel.setBounds(centerX, centerY, labelWidth, labelHeight);

        // 윈도우가 리사이즈될 때에도 중앙 유지
        contentPane.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int labelWidth = userNameLabel.getPreferredSize().width;
                int labelHeight = userNameLabel.getPreferredSize().height;
                int centerX = (contentPane.getWidth() - labelWidth) / 2;
                int centerY = 15;
                userNameLabel.setBounds(centerX, centerY, labelWidth, labelHeight);
            }
        });
        
        ImageIcon button_back = new ImageIcon(getClass().getResource("/img/button_back.png"));
        Image img = button_back.getImage();
        Image resizedImg = img.getScaledInstance(25, 25, Image.SCALE_SMOOTH);
        button_back = new ImageIcon(resizedImg);

        JButton btnBack = new JButton(button_back);
        btnBack.setContentAreaFilled(false);
        btnBack.setBorderPainted(false);
        btnBack.setFocusPainted(false);
        btnBack.setPreferredSize(new Dimension(25, 25));
        btnBack.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ChoosePage page = new ChoosePage();
                page.setLocation(350, 220);
            	setVisible(false);
                dispose();  // 현재 창 닫기
            }
        });        
        panel_buttons.add(btnBack);

        JLabel leftLabel = new JLabel("Chat");
        leftLabel.setBackground(Color.WHITE);
        leftLabel.setFont(new Font("Arial", Font.BOLD, 20));
        panel_buttons.add(leftLabel);

        contentPane.add(panel_buttons);

        RoundedPanel textPanel = new RoundedPanel(15);
        textPanel.setLayout(new BorderLayout());
        textPanel.setBackground(new Color(230, 230, 230));
        textPanel.setBounds(12, 60, 830, 230);
        textPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 30, 30));
        contentPane.add(textPanel);

        textPane = new JTextPane();
        textPane.setEditable(false);
        textPane.setContentType("text/html");
        textPane.setBackground(new Color(230, 230, 230));

        JScrollPane scrollPane = new JScrollPane(textPane);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUI(new CustomScrollBarUI());
        scrollPane.getHorizontalScrollBar().setUI(new CustomScrollBarUI());
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0));
        
        // 패널에 스크롤바와 텍스트 패널 간 간격 추가
        scrollPane.setBounds(0, 0, textPanel.getWidth() - 50, textPanel.getHeight());
        textPanel.add(scrollPane, BorderLayout.CENTER);

        RoundedPanel inputPanel = new RoundedPanel(15);
        inputPanel.setLayout(null);
        inputPanel.setBackground(new Color(200, 200, 200));
        inputPanel.setBounds(12, 300, 830, 45);
        contentPane.add(inputPanel);

        txtInput = new JTextField();
        txtInput.setBackground(new Color(200, 200, 200));
        txtInput.setForeground(Color.BLACK);
        txtInput.setBounds(10, 7, 760, 30);
        txtInput.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        txtInput.setFont(new Font("Malgun Gothic", Font.PLAIN, 16));
        inputPanel.add(txtInput);

        ImageIcon sendIcon = new ImageIcon(getClass().getResource("/img/send.png"));
        Image imgSend = sendIcon.getImage();
        Image resizedImgSend = imgSend.getScaledInstance(25, 25, Image.SCALE_SMOOTH);
        sendIcon = new ImageIcon(resizedImgSend);

        JButton btnSend = new JButton(sendIcon);
        btnSend.setBounds(790, 6, 30, 30);
        btnSend.setContentAreaFilled(false);
        btnSend.setBorderPainted(false);
        btnSend.setFocusPainted(false);
        inputPanel.add(btnSend);

        try {
            socket = new Socket(ipAddress, Integer.parseInt(port));
            InputStream is = socket.getInputStream();
            dis = new DataInputStream(is);
            OutputStream os = socket.getOutputStream();
            dos = new DataOutputStream(os);

            sendMessage("/login " + userName);

            new ListenNetwork().start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Myaction action = new Myaction();
        btnSend.addActionListener(action);
        txtInput.addActionListener(action);
    }

    private void sendMessage(String message) {
        try {
            if (message != null && !message.isEmpty()) {
                dos.writeUTF(message + "\n");
                txtInput.setText("");
            }
        } catch (IOException e) {
            textPane.setText("메세지 전송 실패: " + e.getMessage() + "\n");
        }
    }

    class ListenNetwork extends Thread {
        public void run() {
            while (true) {
                try {
                    String msg = dis.readUTF();
                    if (!isUserMessage) {
                        appendMessage(msg, false);
                    }
                    textPane.setCaretPosition(textPane.getDocument().getLength());
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }
    }

    private void appendMessage(String msg, boolean isRightAligned) {
        try {
            StyledDocument doc = textPane.getStyledDocument();

            String[] parts = msg.split("\n", 2);
            String userName = parts[0];
            String message = parts.length > 1 ? parts[1] : "";

            SimpleAttributeSet userNameStyle = new SimpleAttributeSet();
            StyleConstants.setAlignment(userNameStyle, isRightAligned ? StyleConstants.ALIGN_RIGHT : StyleConstants.ALIGN_LEFT);
            StyleConstants.setFontFamily(userNameStyle, "Malgun Gothic");
            StyleConstants.setFontSize(userNameStyle, 14);

            SimpleAttributeSet messageStyle = new SimpleAttributeSet();
            StyleConstants.setAlignment(messageStyle, isRightAligned ? StyleConstants.ALIGN_RIGHT : StyleConstants.ALIGN_LEFT);
            StyleConstants.setFontFamily(messageStyle, "Malgun Gothic");
            StyleConstants.setFontSize(messageStyle, 14);
            StyleConstants.setBold(messageStyle, true);

            int length = doc.getLength();
            doc.insertString(length, userName + "\n", userNameStyle);

            length = doc.getLength();
            doc.insertString(length, message + "\n", messageStyle);

            doc.setParagraphAttributes(length - userName.length() - 1, userName.length(), userNameStyle, false);
            doc.setParagraphAttributes(length, message.length(), messageStyle, false);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    class Myaction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (isMessageSending) return;

            String msg = txtInput.getText().trim();
            if (!msg.isEmpty()) {
                isUserMessage = true;
                isMessageSending = true;

                String fullMessage = userName + "\n" + msg;
                sendMessage(fullMessage);
                appendMessage(fullMessage, true);

                isMessageSending = false;
                isUserMessage = false;
            }
        }
    }

    class RoundedPanel extends JPanel {
        private int cornerRadius;

        public RoundedPanel(int radius) {
            this.cornerRadius = radius;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
        }
    }

    class CustomScrollBarUI extends BasicScrollBarUI {
        @Override
        protected void configureScrollBarColors() {
            thumbColor = new Color(200, 200, 200);
            trackColor = new Color(230, 230, 230);
        }

        @Override
        protected JButton createDecreaseButton(int orientation) {
            return createZeroButton();
        }

        @Override
        protected JButton createIncreaseButton(int orientation) {
            return createZeroButton();
        }

        private JButton createZeroButton() {
            JButton button = new JButton();
            button.setPreferredSize(new Dimension(0, 0));
            button.setMinimumSize(new Dimension(0, 0));
            button.setMaximumSize(new Dimension(0, 0));
            return button;
        }

        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(thumbColor);
            g2.fillRoundRect(thumbBounds.x, thumbBounds.y, thumbBounds.width, thumbBounds.height, 10, 10);
        }

        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(trackColor);
            g2.fillRoundRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height, 10, 10);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ChatPage("User1", "localhost", "12345").setVisible(true);
        });
    }
}
