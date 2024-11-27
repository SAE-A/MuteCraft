import javax.swing.*;
import java.awt.*;

public class StartPage {

    public StartPage() {
        // JFrame 생성
        JFrame frame = new JFrame("Start Page");

        // 기본 설정
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(852, 393);  // 창 크기 설정
        frame.setLocationRelativeTo(null);  // 창을 화면 중앙에 배치

        // JPanel 생성 (배경색 설정)
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);  // 배경색을 흰색으로 설정
        panel.setLayout(new BorderLayout());  // 중앙 배치할 수 있도록 레이아웃 설정

        // 이미지 불러오기 (src 폴더 내 img/logo.png)
        ImageIcon logoIcon = new ImageIcon(getClass().getResource("/img/logo.jpg"));
        Image img = logoIcon.getImage();  // Image로 변환
        Image resizedImg = img.getScaledInstance(400, 400, Image.SCALE_SMOOTH);  // 400x400으로 크기 조정
        logoIcon = new ImageIcon(resizedImg);  // 다시 ImageIcon으로 변환

        // 이미지 아이콘을 JLabel로 설정
        JLabel logoLabel = new JLabel(logoIcon);
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);  // 이미지를 중앙에 배치
        logoLabel.setVerticalAlignment(SwingConstants.CENTER);

        // JPanel에 JLabel 추가
        panel.add(logoLabel, BorderLayout.CENTER);

        // JFrame에 JPanel 추가
        frame.add(panel);

        // JFrame을 보이게 설정
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        new StartPage();  // StartPage 인스턴스 생성
    }
}
