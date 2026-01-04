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

        // 사이즈 868, 393 고정
        setSize(868, 393);
        setLocationRelativeTo(null);
        setResizable(false);

        // 1. 그라데이션 배경
        contentPane = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(82, 234, 145),
                        0, getHeight(), new Color(39, 174, 96)
                );
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        contentPane.setLayout(null);
        setContentPane(contentPane);

        // 상단 영역
        JButton btnBack = createIconButton("/img/button_back.png", 25, 25);
        btnBack.setBounds(15, 15, 25, 25);
        btnBack.addActionListener(e -> { new ChoosePage(); dispose(); });
        contentPane.add(btnBack);

        JLabel leftLabel = new JLabel("Chat");
        leftLabel.setBounds(50, 12, 100, 30);
        leftLabel.setFont(new Font("Arial", Font.BOLD, 22));
        leftLabel.setForeground(Color.BLACK); // 검정색 통일
        contentPane.add(leftLabel);

        JLabel userNameLabel = new JLabel(userName);
        userNameLabel.setBounds(334, 15, 200, 30);
        userNameLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 18));
        userNameLabel.setForeground(Color.BLACK); // 검정색 통일
        userNameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        contentPane.add(userNameLabel);

        // 2. 채팅 로그 영역 (반투명 흰색을 조금 더 진하게 해서 검정 글씨가 잘 보이게 함)
        RoundedPanel textPanel = new RoundedPanel(15);
        textPanel.setLayout(new BorderLayout());
        textPanel.setBackground(new Color(255, 255, 255, 120)); // 투명도 조절 (120/255)
        textPanel.setBounds(15, 65, 825, 220);
        textPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        contentPane.add(textPanel);

        textPane = new JTextPane();
        textPane.setEditable(false);
        textPane.setOpaque(false);
        textPane.setBackground(new Color(0, 0, 0, 0));

        JScrollPane scrollPane = new JScrollPane(textPane);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUI(new CustomScrollBarUI());
        textPanel.add(scrollPane, BorderLayout.CENTER);

        // 3. 입력 영역
        RoundedPanel inputPanel = new RoundedPanel(15);
        inputPanel.setLayout(null);
        inputPanel.setBackground(new Color(255, 255, 255, 120));
        inputPanel.setBounds(15, 300, 825, 45);
        contentPane.add(inputPanel);

        txtInput = new JTextField();
        txtInput.setOpaque(false);
        txtInput.setBorder(null);
        txtInput.setForeground(Color.BLACK); // 검정색 통일
        txtInput.setBounds(15, 7, 750, 30);
        txtInput.setFont(new Font("Malgun Gothic", Font.PLAIN, 16));
        inputPanel.add(txtInput);

        JButton btnSend = createIconButton("/img/send.png", 28, 28);
        btnSend.setBounds(780, 8, 30, 30);
        inputPanel.add(btnSend);

        // 네트워크 및 리스너
        connectToServer(ipAddress, port);
        Myaction action = new Myaction();
        btnSend.addActionListener(action);
        txtInput.addActionListener(action);
    }

    private void appendMessage(String msg, boolean isRightAligned) {
        try {
            StyledDocument doc = textPane.getStyledDocument();

            // 스타일 설정 (모두 검정색)
            SimpleAttributeSet style = new SimpleAttributeSet();
            StyleConstants.setAlignment(style, isRightAligned ? StyleConstants.ALIGN_RIGHT : StyleConstants.ALIGN_LEFT);
            StyleConstants.setForeground(style, Color.BLACK); // 검정색 통일
            StyleConstants.setFontFamily(style, "Malgun Gothic");
            StyleConstants.setFontSize(style, 15);

            String[] parts = msg.split("\n", 2);
            String uName = parts[0];
            String mBody = parts.length > 1 ? parts[1] : "";

            // 단락 스타일 적용 후 텍스트 삽입
            int start = doc.getLength();
            doc.insertString(start, "[" + uName + "]\n" + mBody + "\n\n", style);
            doc.setParagraphAttributes(start, doc.getLength() - start, style, false);

        } catch (BadLocationException e) { e.printStackTrace(); }
    }

    // --- 유틸리티 메서드 ---
    private void connectToServer(String ip, String port) {
        try {
            socket = new Socket(ip, Integer.parseInt(port));
            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());
            sendMessage("/login " + userName);
            new ListenNetwork().start();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void sendMessage(String message) {
        try {
            if (message != null && !message.isEmpty()) {
                dos.writeUTF(message);
                txtInput.setText("");
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    private JButton createIconButton(String path, int w, int h) {
        ImageIcon icon = new ImageIcon(getClass().getResource(path));
        JButton btn = new JButton(new ImageIcon(icon.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH)));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        return btn;
    }

    class ListenNetwork extends Thread {
        public void run() {
            while (true) {
                try {
                    String msg = dis.readUTF();
                    if (!isUserMessage) appendMessage(msg, false);
                    textPane.setCaretPosition(textPane.getDocument().getLength());
                } catch (IOException e) { break; }
            }
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
        public RoundedPanel(int radius) { this.cornerRadius = radius; setOpaque(false); }
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
        protected void configureScrollBarColors() {
            thumbColor = new Color(0, 0, 0, 50); // 스크롤바도 살짝 검은빛 투명
            trackColor = new Color(0, 0, 0, 0);
        }
        protected JButton createDecreaseButton(int o) { return new JButton() {{ setPreferredSize(new Dimension(0,0)); }}; }
        protected JButton createIncreaseButton(int o) { return new JButton() {{ setPreferredSize(new Dimension(0,0)); }}; }
    }
}