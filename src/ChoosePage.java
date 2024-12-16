import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChoosePage extends JFrame {

    public ChoosePage() {
        // JFrame 기본 설정
        setTitle("Choose Instrument");
        setBounds(100, 100, 852, 393); // 창 위치 및 크기 설정
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout()); // BorderLayout 사용 (전체 프레임에 배치)

        // 배경색 설정
        getContentPane().setBackground(Color.WHITE); // 하얀색 배경

        // 채팅창 버튼 추가 (오른쪽 끝에 위치)
        JPanel chatPanel = new JPanel(); // 새로운 JPanel 생성
        chatPanel.setBackground(Color.WHITE);
        chatPanel.setLayout(new FlowLayout(FlowLayout.RIGHT)); // 오른쪽 정렬

        JButton chatButton = new JButton();
        ImageIcon chatIcon = new ImageIcon(getClass().getResource("/img/send.png"));
        chatButton.setIcon(updateImageSize(chatIcon, 50, 50)); // 크기 조정
        chatButton.setContentAreaFilled(false);
        chatButton.setBorderPainted(false);
        chatButton.setFocusPainted(false);
        chatButton.addActionListener(new ActionListener() {
            @Override  // ChatServer 먼저 실행!!
            public void actionPerformed(ActionEvent e) {
                System.out.println("Chat button clicked!");  // 디버깅
                //setVisible(false);  // ChoosePage 창 숨기기
                ChatClient chatClient = new ChatClient(); // ChatClient 인스턴스 생성
                chatClient.setVisible(true);  // ChatClient 창 띄우기
            }
        });
        
        // 채팅 버튼 부분 사이즈 조정
        chatButton.setPreferredSize(new Dimension(100, 40));

        // 채팅 버튼을 JPanel에 추가
        chatPanel.add(chatButton);

        // 채팅 버튼을 프레임 상단에 오른쪽에 배치
        add(chatPanel, BorderLayout.NORTH);

        // 피아노 버튼
        JButton pianoButton = createButton("/img/piano2.png", "Piano");
        pianoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new PianoPage(); // PianoPage.java 실행
            }
        });
        add(pianoButton, BorderLayout.WEST); // 피아노 버튼은 왼쪽에 배치

        // 어쿠스틱 기타 버튼
        JButton acousticGuitarButton = createButton("/img/acousticguitar.png", "Acoustic Guitar");
        acousticGuitarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new AcousticPage(); // AcousticPage.java 실행
            }
        });
        add(acousticGuitarButton, BorderLayout.CENTER); // 가운데 배치

        // 일렉기타 버튼
        JButton electricGuitarButton = createButton("/img/electricguitar.png", "Electric Guitar");
        electricGuitarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ElectricPage(); // ElectricPage.java 실행
            }
        });
        add(electricGuitarButton, BorderLayout.EAST); // 일렉기타 버튼은 오른쪽에 배치

        // 창 표시
        setVisible(true);
    }

    private JButton createButton(String imagePath, String text) {
        JButton button = new JButton(text);
        ImageIcon icon = new ImageIcon(getClass().getResource(imagePath));
        button.setIcon(updateImageSize(icon, 200, 230));
        button.setHorizontalTextPosition(SwingConstants.CENTER);
        button.setVerticalTextPosition(SwingConstants.BOTTOM);
        button.setPreferredSize(new Dimension(200, 250));
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        return button;
    }

    // 이미지 크기 조정
    private ImageIcon updateImageSize(ImageIcon icon, int width, int height) {
        Image image = icon.getImage();
        Image updatedImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(updatedImage);
    }

    public static void main(String[] args) {
        new ChoosePage();
    }
}
