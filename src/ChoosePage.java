import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChoosePage extends JFrame {
    protected ChoosePage() {
        // JFrame 기본 설정
        setTitle("Choose Instrument");
        setSize(868, 393);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout()); // BorderLayout 사용 (전체 프레임에 배치)

        // 배경색 설정
        getContentPane().setBackground(Color.WHITE); // 하얀색 배경

        // 채팅창 버튼 추가 (오른쪽 끝에 위치)
        JPanel chatPanel = new JPanel(); // 새로운 JPanel 생성
        chatPanel.setBackground(Color.WHITE);
        chatPanel.setLayout(new FlowLayout(FlowLayout.RIGHT)); // 오른쪽 정렬

        // 채팅 버튼
        JButton chatButton = new JButton();
        ImageIcon chatIcon = new ImageIcon(getClass().getResource("/img/send.png"));
        chatButton.setIcon(updateImageSize(chatIcon, 30, 35)); // 크기 조정
        chatButton.setContentAreaFilled(false);
        chatButton.setBorderPainted(false);
        chatButton.setFocusPainted(false);
        chatButton.setPreferredSize(new Dimension(40, 40));
        chatButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Chat button clicked!");  // 디버깅
                ChatClient chatClient = new ChatClient(); // ChatClient 인스턴스 생성
                chatClient.setLocation(650, 300);
                chatClient.setVisible(true);  // ChatClient 창 띄우기
                dispose();
            }
        });

        // 뒤로가기 버튼
        JButton backbtn = new JButton();
        ImageIcon button_back = new ImageIcon(getClass().getResource("/img/button_back.png"));
        backbtn.setIcon(updateImageSize(button_back, 25, 25));
        backbtn.setContentAreaFilled(false);
        backbtn.setBorderPainted(false);
        backbtn.setFocusPainted(false);
        backbtn.setPreferredSize(new Dimension(40, 40));
        backbtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Back button clicked!");
                new StartPage();
                setVisible(false);
                dispose();  // 현재 창 닫기
            }
        });

        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setLayout(new FlowLayout(FlowLayout.LEFT)); // 왼쪽 정렬
        leftPanel.add(backbtn);  // 뒤로가기 버튼을 왼쪽에 추가

        JPanel rightPanel = new JPanel();
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setLayout(new FlowLayout(FlowLayout.RIGHT)); // 오른쪽 정렬
        rightPanel.add(chatButton);  // 채팅 버튼을 오른쪽에 추가

        // 두 패널을 상단에 추가
        add(leftPanel, BorderLayout.WEST);  // 왼쪽에 배치
        add(rightPanel, BorderLayout.EAST);  // 오른쪽에 배치
        
        // 버튼 패널
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        
        // 보컬 버튼
        JButton vocalButton = createButton("/img/mic.png", "Vocal");
        vocalButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                VocalPage vocal = new VocalPage(); // MicPage.java 실행
                vocal.setLocation(350, 220);
                dispose();
            }
        });
        
        // 피아노 버튼
        JButton pianoButton = createButton("/img/piano.png", "Piano");
        pianoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PianoPage piano = new PianoPage(); // PianoPage.java 실행
                piano.setLocation(350, 220);
                dispose();
            }
        });
        
        // 어쿠스틱 기타 버튼
        JButton acousticGuitarButton = createButton("/img/acousticGuitar.png", "Acoustic Guitar");
        acousticGuitarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AcousticPage aGuitar = new AcousticPage(); // AcousticPage.java 실행
                aGuitar.setLocation(350, 220);
                dispose();
            }
        });

        // 일렉기타 버튼
        JButton electricGuitarButton = createButton("/img/electricGuitar.png", "Electric Guitar");
        electricGuitarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ElectricPage eGuitar = new ElectricPage(); // ElectricPage.java 실행
                eGuitar.setLocation(350, 220);
                dispose();
            }
        });

        // 버튼 패널에 버튼과 간격 추가
        buttonPanel.add(Box.createHorizontalGlue()); // 왼쪽 여백
        buttonPanel.add(vocalButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(10, 0))); // 버튼 간격
        buttonPanel.add(pianoButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(10, 0))); // 버튼 간격
        buttonPanel.add(acousticGuitarButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(10, 0))); // 버튼 간격
        buttonPanel.add(electricGuitarButton);
        buttonPanel.add(Box.createHorizontalGlue()); // 오른쪽 여백

        add(buttonPanel, BorderLayout.CENTER); // 가운데 배치

        // 창 표시
        setVisible(true);
    }

    private JButton createButton(String imagePath, String text) {
        JButton button = new JButton(text);
        ImageIcon icon = new ImageIcon(getClass().getResource(imagePath));
        button.setIcon(updateImageSize(icon, 150, 150));
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
