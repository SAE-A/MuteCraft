import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StartPage {

    public StartPage() {
        // JFrame 생성
        JFrame frame = new JFrame("Mute Craft");

        // 기본 설정
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(868, 393);  // 창 크기 설정
        frame.setLocationRelativeTo(null);  // 창을 화면 중앙에 배치

        // 기본 JPanel 생성 (GridBagLayout 사용)
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);  // 배경을 흰색으로 설정

        // GridBagConstraints 설정
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // 컴포넌트 사이의 여백

        // 로고 이미지 설정
        ImageIcon logoIcon = new ImageIcon(getClass().getResource("/img/Logo2.png"));
        Image img = logoIcon.getImage();
        Image resizedImg = img.getScaledInstance(250, 200, Image.SCALE_SMOOTH); // 크기 조정
        logoIcon = new ImageIcon(resizedImg);

        JLabel logoLabel = new JLabel(logoIcon);
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // 로고 위치 (살짝 아래로 내림)
        gbc.gridx = 0;
        gbc.gridy = 0; // 첫 번째 행
        gbc.anchor = GridBagConstraints.CENTER; // 중앙 정렬
        gbc.weighty = 0.3; // 상단 여백 비율 (이전 0.2 → 0.3으로 조정)
        panel.add(logoLabel, gbc);

        // 버튼 패널 생성
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        buttonPanel.setBackground(Color.WHITE);

        // 버튼 생성
        JButton button1 = new JButton("SOLO");
        JButton button2 = new JButton("TEAM");

        button1.setFocusPainted(false);
        button1.setContentAreaFilled(false);
        button1.setBorderPainted(false);
        button1.setOpaque(false);
        button1.setFont(new Font("Arial", Font.BOLD, 25)); // 폰트 크기 25로 설정

        button2.setFocusPainted(false);
        button2.setContentAreaFilled(false);
        button2.setBorderPainted(false);
        button2.setOpaque(false);
        button2.setFont(new Font("Arial", Font.BOLD, 25)); // 폰트 크기 25로 설정

        // SOLO 버튼 리스너
        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ChoosePage page = new ChoosePage();
                page.setLocation(350, 220);
                frame.dispose();
            }
        });

        // TEAM 버튼 리스너
        button2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ChoosePage page1 = new ChoosePage();
                ChoosePage page2 = new ChoosePage();
                page1.setLocation(350, 30);
                page2.setLocation(350, 420);
                frame.dispose();
            }
        });

        // 버튼 패널에 버튼 추가
        buttonPanel.add(button1);
        buttonPanel.add(button2);

        gbc.gridy = 1; // 두 번째 행
        gbc.anchor = GridBagConstraints.NORTH; // 상단 정렬
        gbc.weighty = 0.25; // 버튼 패널의 위치 조정
        panel.add(buttonPanel, gbc);

        // JFrame에 JPanel 추가
        frame.add(panel);

        // JFrame을 보이게 설정
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        new StartPage();  // StartPage 인스턴스 생성
    }
}
