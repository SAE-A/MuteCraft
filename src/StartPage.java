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

        // BackgroundPanel 생성 (배경 이미지가 그려진 패널)
        BackgroundPanel panel = new BackgroundPanel();
        panel.setLayout(new BorderLayout());  // 중앙 배치할 수 있도록 레이아웃 설정

        // 이미지 불러오기 (src 폴더 내 img/logo.jpg)
        ImageIcon logoIcon = new ImageIcon(getClass().getResource("/img/logo.png"));
        Image img = logoIcon.getImage();  // Image로 변환
        Image resizedImg = img.getScaledInstance(300, 300, Image.SCALE_SMOOTH);  // 크기 조정
        logoIcon = new ImageIcon(resizedImg);  // 다시 ImageIcon으로 변환

        // 이미지 아이콘을 JLabel로 설정
        JLabel logoLabel = new JLabel(logoIcon);
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);  // 이미지를 중앙에 배치
        logoLabel.setVerticalAlignment(SwingConstants.CENTER);

        // JPanel에 JLabel 추가 (배경 이미지 위에 로고)
        panel.add(logoLabel, BorderLayout.CENTER);

        // 버튼 패널 생성
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));  // 버튼 중앙 정렬
        buttonPanel.setBackground(Color.WHITE);  // 버튼 패널 배경색 설정

        // 버튼 생성
        JButton button1 = new JButton("SOLO");
        JButton button2 = new JButton("TEAM");

        button1.setFocusPainted(false);  // 버튼 선택 시 생기는 테두리 제거
        button1.setContentAreaFilled(false);  // 배경 제거
        button1.setBorderPainted(false);  // 테두리 제거
        button1.setOpaque(false);  // 불투명 상태 해제
        button1.setFont(new Font("Arial", Font.BOLD, 20));

        button2.setFocusPainted(false);
        button2.setContentAreaFilled(false);
        button2.setBorderPainted(false);
        button2.setOpaque(false);
        button2.setFont(new Font("Arial", Font.BOLD, 20));

        // SOLO 버튼에 ActionListener 추가
        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // SOLO 버튼 클릭 시 ChoosePage로 전환
                ChoosePage page = new ChoosePage();  // ChoosePage 화면을 생성
                page.setLocation(350, 220);
                frame.dispose();  // 현재 페이지 종료
            }
        });

        // TEAM 버튼에 ActionListener 추가 (ChoosePage 두 개 열기)
        button2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TEAM 버튼 클릭 시 ChoosePage 두 개를 각각 생성
                ChoosePage page1 = new ChoosePage();  // 첫 번째 ChoosePage
                ChoosePage page2 = new ChoosePage();  // 두 번째 ChoosePage

                page1.setLocation(350, 30);  // 첫 번째 창 위치
                page2.setLocation(350, 420);  // 두 번째 창 위치

                frame.dispose();  // 현재 페이지 종료
            }
        });

        // 버튼 패널에 버튼 추가
        buttonPanel.add(button1);
        buttonPanel.add(button2);

        // 버튼 패널을 메인 패널의 SOUTH에 추가
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // JFrame에 JPanel 추가
        frame.add(panel);

        // JFrame을 보이게 설정
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        new StartPage();  // StartPage 인스턴스 생성
    }
}

// 배경 이미지를 그릴 JPanel 클래스
class BackgroundPanel extends JPanel {
    private Image backgroundImage;

    public BackgroundPanel() {
        // 배경 이미지 로딩
        backgroundImage = new ImageIcon(getClass().getResource("/img/back.png")).getImage();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        setBackground(Color.WHITE);  // 패널의 배경색 설정
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
    }

}
