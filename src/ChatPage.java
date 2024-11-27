import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.text.*;

public class ChatPage extends JFrame {
    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextPane textPane;  // JTextArea 대신 JTextPane 사용
    private JTextField txtInput;
    private String userName;
    private DataOutputStream dos;
    private Socket socket;
    private DataInputStream dis;
    private boolean isUserMessage = false;  // 사용자 메시지를 구분하는 플래그
    private boolean isMessageSending = false;  // 메시지가 전송 중인지 확인하는 플래그

    public ChatPage(String userName, String ipAddress, String port) {
        this.userName = userName;  // ChatClient에서 받은 사용자 이름

        // JFrame 설정
        setTitle("Chat Room");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 852, 393);
        setLocationRelativeTo(null);  // 창을 화면 중앙에 배치
        contentPane = new JPanel();
        contentPane.setBackground(Color.WHITE);
        contentPane.setLayout(null);
        setContentPane(contentPane);

        // 상단 버튼 및 'Chat' 텍스트 설정
        JPanel panel_buttons = new JPanel();
        panel_buttons.setBounds(0, 0, 852, 50);  // 상단 크기 조정
        panel_buttons.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10)); // 왼쪽 정렬

        // 이미지 아이콘 로드
        ImageIcon button_back = new ImageIcon(getClass().getResource("/img/button_back.png"));
        Image img = button_back.getImage();
        Image resizedImg = img.getScaledInstance(25, 25, Image.SCALE_SMOOTH);  // 크기 조정 (30x30으로 설정)
        button_back = new ImageIcon(resizedImg);

        // 버튼 생성
        JButton btnBack = new JButton(button_back);
        btnBack.setContentAreaFilled(false);  // 버튼 배경을 투명으로 설정
        btnBack.setBorderPainted(false);  // 버튼 테두리 없애기
        btnBack.setFocusPainted(false);  // 버튼 클릭 시 테두리 효과 없애기

        // 버튼 크기 조정
        btnBack.setPreferredSize(new Dimension(25, 25));  // 버튼 크기 설정

        // 버튼을 패널에 추가
        panel_buttons.add(btnBack);

        // 뒤로가기 버튼 동작 추가
        btnBack.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();  // 현재 창 닫기
            }
        });

        JLabel leftLabel = new JLabel("Chat");
        leftLabel.setFont(new Font("Serif", Font.PLAIN, 20));
        panel_buttons.add(leftLabel);

        contentPane.add(panel_buttons);

        // 텍스트 영역 설정 (JTextPane 사용)
        textPane = new JTextPane();
        textPane.setEditable(false);  // 수정 불가
        textPane.setContentType("text/html");  // HTML 콘텐츠 타입 사용
        textPane.setBackground(new Color(230, 230, 230)); 
        JScrollPane scrollPane = new JScrollPane(textPane);
        scrollPane.setBounds(12, 60, 812, 230);
        contentPane.add(scrollPane);

        // 메시지 입력 필드
        txtInput = new JTextField();
        txtInput.setBounds(12, 300, 740, 40);  // 입력 필드 크기 조정
        txtInput.setBackground(new Color(230, 230, 230)); 
        contentPane.add(txtInput);
        txtInput.setColumns(10);

        // 메시지 전송 버튼 이미지로 변경
        ImageIcon sendIcon = new ImageIcon(getClass().getResource("/img/send.png"));  // send.png 이미지 로드
        Image imgSend = sendIcon.getImage();
        Image resizedImgSend = imgSend.getScaledInstance(54, 54, Image.SCALE_SMOOTH);  // 버튼 크기에 맞게 이미지 크기 조정
        sendIcon = new ImageIcon(resizedImgSend);

        // 버튼 생성
        JButton btnSend = new JButton(sendIcon);
        btnSend.setBounds(757, 295, 54, 54);  // 버튼 위치 및 크기 조정
        btnSend.setContentAreaFilled(false);  // 버튼 배경을 투명으로 설정
        btnSend.setBorderPainted(false);  // 버튼 테두리 없애기
        btnSend.setFocusPainted(false);  // 버튼 클릭 시 테두리 효과 없애기
        contentPane.add(btnSend);


        // 서버 연결 및 로그인 처리
        try {
            socket = new Socket(ipAddress, Integer.parseInt(port));
            InputStream is = socket.getInputStream();
            dis = new DataInputStream(is);
            OutputStream os = socket.getOutputStream();
            dos = new DataOutputStream(os);

            // 로그인 메시지 전송
            sendMessage("/login " + userName);

            // 메시지 수신 대기 스레드 시작
            new ListenNetwork().start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 메시지 전송 버튼 클릭 및 Enter 키 처리
        Myaction action = new Myaction();
        btnSend.addActionListener(action);
        txtInput.addActionListener(action);
    }

    // 서버로 메시지 전송
    private void sendMessage(String message) {
        try {
            if (message != null && !message.isEmpty()) {
                dos.writeUTF(message);  // 서버로 메시지 전송
                txtInput.setText("");  // 텍스트 입력 필드 초기화
            }
        } catch (IOException e) {
            textPane.setText("Failed to send message: " + e.getMessage() + "\n");
        }
    }

    // 서버로부터 메시지 수신
    class ListenNetwork extends Thread {
        public void run() {
            while (true) {
                try {
                    String msg = dis.readUTF();
                    // 서버에서 받은 메시지 출력
                    if (!isUserMessage) {
                        appendMessage(msg, false);  // 서버 메시지는 왼쪽 정렬
                    }
                    textPane.setCaretPosition(textPane.getDocument().getLength());  // 스크롤바 맨 아래로
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }
    }

    // 메시지 오른쪽 정렬하여 JTextPane에 추가
    private void appendMessage(String msg, boolean isRightAligned) {
        try {
            StyledDocument doc = textPane.getStyledDocument();
            SimpleAttributeSet rightAlign = new SimpleAttributeSet();
            if (isRightAligned) {
                StyleConstants.setAlignment(rightAlign, StyleConstants.ALIGN_RIGHT);  // 오른쪽 정렬
            }

            doc.insertString(doc.getLength(), msg + "\n", rightAlign);  // 메시지 추가
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    // 메시지 전송 버튼 클릭 및 Enter 키 처리
    class Myaction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            // 이미 메시지를 전송 중이면 다시 전송하지 않도록 방지
            if (isMessageSending) return;

            String msg = txtInput.getText().trim();
            if (!msg.isEmpty()) {
                isUserMessage = true;  // 사용자 메시지인 경우 플래그 설정
                isMessageSending = true;  // 메시지 전송 중으로 설정

                sendMessage("[" + userName + "] " + msg);  // 사용자 이름과 메시지를 포함한 전송
                appendMessage("[" + userName + "] " + msg, true);  // 사용자 메시지 오른쪽 정렬하여 추가

                // 메시지 전송 후 플래그 초기화
                isMessageSending = false;  
                isUserMessage = false;  // 메시지 전송 후 플래그 초기화
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ChatPage("User1", "localhost", "12345").setVisible(true);
        });
    }
}
