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

		// 메인 패널 생성
		JPanel mainPanel = new JPanel(new BorderLayout()) {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2d = (Graphics2D) g;
				g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

				// 배경색 설정 - 초록 그라데이션
				GradientPaint gp = new GradientPaint(
						0, 0, new Color(82, 234, 145),
						0, getHeight(), new Color(39, 174, 96)
				);
				g2d.setPaint(gp);
				g2d.fillRect(0, 0, getWidth(), getHeight());
			}
		};

		// 상단 패널 생성 (투명 설정)
		JPanel topPanel = new JPanel();
		topPanel.setOpaque(false); // 배경 투명
		topPanel.setLayout(new BorderLayout());

		// 뒤로가기 버튼
		JButton backbtn = new JButton();
		try {
			ImageIcon button_back = new ImageIcon(getClass().getResource("/img/button_back.png"));
			backbtn.setIcon(updateImageSize(button_back, 25, 25));
		} catch (Exception e) { System.out.println("이미지 로드 실패"); }
		backbtn.setContentAreaFilled(false);
		backbtn.setBorderPainted(false);
		backbtn.setFocusPainted(false);
		backbtn.setPreferredSize(new Dimension(40, 40));
		backbtn.addActionListener(e -> {
			new StartPage();
			dispose();
		});

		// "Instruments" 레이블
		JLabel instrumentsLabel = new JLabel("Instruments");
		instrumentsLabel.setFont(new Font("Arial", Font.BOLD, 20));
		instrumentsLabel.setForeground(Color.BLACK);

		// 왼쪽 상단 패널 (투명 설정)
		JPanel leftPanel = new JPanel();
		leftPanel.setOpaque(false);
		leftPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		leftPanel.add(backbtn);
		leftPanel.add(instrumentsLabel);

		topPanel.add(leftPanel, BorderLayout.WEST);

		// 채팅 버튼
		JButton chatButton = new JButton();
		try {
			ImageIcon chatIcon = new ImageIcon(getClass().getResource("/img/send.png"));
			chatButton.setIcon(updateImageSize(chatIcon, 30, 30));
		} catch (Exception e) { System.out.println("이미지 로드 실패"); }
		chatButton.setContentAreaFilled(false);
		chatButton.setBorderPainted(false);
		chatButton.setFocusPainted(false);
		chatButton.setPreferredSize(new Dimension(55, 50));
		chatButton.addActionListener(e -> {
			ChatClient chatClient = new ChatClient();
			chatClient.setVisible(true);
			dispose();
		});

		topPanel.add(chatButton, BorderLayout.EAST);
		mainPanel.add(topPanel, BorderLayout.NORTH);

		// 버튼 패널
		JPanel buttonPanel = new JPanel();
		buttonPanel.setOpaque(false); // 배경 투명
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));

		// 악기 버튼들 생성
		JButton vocalButton = createButton("/img/mic.png", "Vocal");
		vocalButton.addActionListener(e -> {
			new VocalPage();
			dispose();
		});

		JButton pianoButton = createButton("/img/piano.png", "Piano");
		pianoButton.addActionListener(e -> {
			new PianoPage();
			dispose();
		});

		JButton acousticGuitarButton = createButton("/img/acousticGuitar.png", "Acoustic Guitar");
		acousticGuitarButton.addActionListener(e -> {
			new AcousticPage();
			dispose();
		});

		JButton electricGuitarButton = createButton("/img/electricGuitar.png", "Electric Guitar");
		electricGuitarButton.addActionListener(e -> {
			new ElectricPage();
			dispose();
		});

		// 버튼 패널에 추가
		buttonPanel.add(Box.createHorizontalGlue());
		buttonPanel.add(vocalButton);
		buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
		buttonPanel.add(pianoButton);
		buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
		buttonPanel.add(acousticGuitarButton);
		buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
		buttonPanel.add(electricGuitarButton);
		buttonPanel.add(Box.createHorizontalGlue());

		mainPanel.add(buttonPanel, BorderLayout.CENTER);

		// 최종적으로 메인 패널을 프레임에 추가
		add(mainPanel);
		setVisible(true);
	}

	private JButton createButton(String imagePath, String text) {
		JButton button = new JButton(text);
		try {
			ImageIcon icon = new ImageIcon(getClass().getResource(imagePath));
			button.setIcon(updateImageSize(icon, 170, 240));
		} catch (Exception e) {
			System.out.println(imagePath + " 로드 실패");
		}
		button.setHorizontalTextPosition(SwingConstants.CENTER);
		button.setVerticalTextPosition(SwingConstants.BOTTOM);
		button.setPreferredSize(new Dimension(200, 250));
		button.setContentAreaFilled(false);
		button.setBorderPainted(false);
		button.setFocusPainted(false);

		button.setForeground(Color.BLACK);
		button.setFont(new Font("Arial", Font.BOLD, 14));

		return button;
	}

	private ImageIcon updateImageSize(ImageIcon icon, int width, int height) {
		Image image = icon.getImage();
		Image updatedImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
		return new ImageIcon(updatedImage);
	}

	public static void main(String[] args) {
		new ChoosePage();
	}
}