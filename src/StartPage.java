import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StartPage {

    public StartPage() {
        // JFrame 생성
        JFrame frame = new JFrame("Mute Craft");

        // 기본 설정
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(868, 393);
        frame.setLocationRelativeTo(null);

        // 메인 패널
        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                // 초록색 그라데이션 배경
                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(82, 234, 145),
                        0, getHeight(), new Color(39, 174, 96)
                );
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        // 왼쪽 이미지
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setOpaque(false);
        leftPanel.setBorder(new EmptyBorder(0, 20, 0, 0));
        try {
            ImageIcon singIcon = new ImageIcon(getClass().getResource("/img/pr_dancing.png"));
            Image singImg = singIcon.getImage();
            Image resizedSingImg = singImg.getScaledInstance(160, 360, Image.SCALE_SMOOTH);
            JLabel imgLabel1 = new JLabel(new ImageIcon(resizedSingImg));
            imgLabel1.setHorizontalAlignment(SwingConstants.CENTER);
            leftPanel.add(imgLabel1, BorderLayout.CENTER);
        } catch (Exception e) {
            System.out.println("pr_dancing.png 이미지를 찾을 수 없습니다: " + e.getMessage());
        }

        // 중앙 패널 (로고 + 부제목 + 버튼)
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();

        // 로고
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(20, 20, 5, 20);
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel logoLabel;
        try {
            ImageIcon logoIcon = new ImageIcon(getClass().getResource("/img/Logo.png"));
            Image img = logoIcon.getImage();
            Image resizedImg = img.getScaledInstance(250, 150, Image.SCALE_SMOOTH);
            logoIcon = new ImageIcon(resizedImg);
            logoLabel = new JLabel(logoIcon);
        } catch (Exception e) {
            logoLabel = new JLabel("Mute Craft");
            logoLabel.setFont(new Font("Arial", Font.BOLD, 36));
            logoLabel.setForeground(Color.WHITE);
        }
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        centerPanel.add(logoLabel, gbc);

        // 부제목
        gbc.gridy = 1;
        gbc.insets = new Insets(5, 20, 15, 20);
        JLabel subtitleLabel = new JLabel("음악으로 소통하는 즐거움!");
        subtitleLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 16));
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        centerPanel.add(subtitleLabel, gbc);

        // 버튼 패널
        gbc.gridy = 2;
        gbc.insets = new Insets(10, 20, 30, 20);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 0));
        buttonPanel.setOpaque(false);

        JButton button1 = createStyledButton("SOLO");
        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ChoosePage();
                frame.dispose();
            }
        });

        JButton button2 = createStyledButton("TEAM");
        button2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ChoosePage();
                frame.dispose();
            }
        });

        buttonPanel.add(button1);
        buttonPanel.add(button2);
        centerPanel.add(buttonPanel, gbc);

        // 오른쪽 이미지
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setOpaque(false);
        rightPanel.setBorder(new EmptyBorder(0, 0, 0, 50));
        try {
            ImageIcon playIcon = new ImageIcon(getClass().getResource("/img/pr_playing.png"));
            Image playImg = playIcon.getImage();
            Image resizedPlayImg = playImg.getScaledInstance(160, 320, Image.SCALE_SMOOTH);
            JLabel imgLabel2 = new JLabel(new ImageIcon(resizedPlayImg));
            imgLabel2.setHorizontalAlignment(SwingConstants.CENTER);
            rightPanel.add(imgLabel2, BorderLayout.CENTER);
        } catch (Exception e) {
            System.out.println("pr_playing.jpg 이미지를 찾을 수 없습니다: " + e.getMessage());
        }

        // 메인 패널에 추가
        panel.add(leftPanel, BorderLayout.WEST);
        panel.add(centerPanel, BorderLayout.CENTER);
        panel.add(rightPanel, BorderLayout.EAST);

        frame.add(panel);
        frame.setVisible(true);
    }

    // 스타일리시한 버튼 생성
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // 버튼 배경색 - black
                g2d.setColor(Color.BLACK);

                // 둥근 사각형 배경
                int rectY = 5;
                int rectHeight = getHeight() - 15;
                g2d.fillRoundRect(0, rectY, getWidth() - 15, rectHeight, 30, 30);

                // 텍스트 - SOLO/TEAM
                g2d.setColor(new Color(39, 174, 96));
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();

                // x축: 중앙 정렬
                int x = (getWidth() - fm.stringWidth(getText())) / 2 - 7;
                // y축: 배경 사각형 가운데
                int y = rectY + (rectHeight - fm.getHeight()) / 2 + fm.getAscent();

                g2d.drawString(getText(), x, y);

                g2d.dispose();
            }
        };

        button.setPreferredSize(new Dimension(180, 65));
        button.setFont(new Font("Gulim", Font.BOLD, 22));
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return button;
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> new StartPage());
    }
}