import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChoosePage extends JFrame {

    public ChoosePage() {
        // JFrame 기본 설정
        setTitle("Choose Instrument");
        setBounds(100, 100, 867, 393); // 창 위치 및 크기 설정
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout()); // GridBagLayout 사용

        // 배경색 설정
        getContentPane().setBackground(Color.WHITE); // 하얀색 배경

        // GridBagConstraints 설정
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20); // 버튼 간의 여백 설정
        gbc.anchor = GridBagConstraints.CENTER; // 버튼을 중앙에 위치

        // 피아노 버튼
        JButton pianoButton = createButton("/img/piano2.png", "Piano");
        pianoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new PianoPage2(); // PianoPage.java 실행
            }
        });
        gbc.gridx = 0; // 첫 번째 열
        gbc.gridy = 0; // 첫 번째 행
        add(pianoButton, gbc);

        // 어쿠스틱 기타 버튼
        JButton acousticGuitarButton = createButton("/img/acousticguitar.png", "Acoustic Guitar");
        acousticGuitarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new AcousticPage(); // AcousticPage.java 실행
            }
        });
        gbc.gridx = 1; // 두 번째 열
        gbc.gridy = 0; // 첫 번째 행
        add(acousticGuitarButton, gbc);

        // 일렉기타 버튼
        JButton electricGuitarButton = createButton("/img/electricguitar.png", "Electric Guitar");
        electricGuitarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ElectricPage(); // ElectricPage.java 실행
            }
        });
        gbc.gridx = 2; // 세 번째 열
        gbc.gridy = 0; // 첫 번째 행
        add(electricGuitarButton, gbc);

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