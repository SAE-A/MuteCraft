import java.awt.*;
import java.io.File;
import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.util.ArrayList;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Mixing extends JFrame {
    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private String audioFile1 = "src/resources/lydfiler/audio/record_piano.wav";
    private String audioFile2 = "src/resources/lydfiler/audio/record_acoustic.wav";
    private String audioFile3 = "src/resources/lydfiler/audio/record_electric.wav";
    private ArrayList<JPanel> trackPanels = new ArrayList<>(); // 트랙의 색깔 상자를 저장할 리스트
    private boolean[] trackStates = {false, false, false}; // 각 트랙의 상태 (색깔 변경 여부)
    private JLabel nowLabel; // 현재 재생 위치 표시를 위한 이미지

    public Mixing() {
    	setTitle("Mixing");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 852, 393);

        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setBackground(Color.WHITE); // 배경색 흰색 설정
        setContentPane(contentPane);
        contentPane.setLayout(new BorderLayout());

        // 상단 패널 생성
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        contentPane.add(topPanel, BorderLayout.NORTH);

        // 왼쪽 (Back 버튼과 Mixing 레이블)
        JPanel leftTopPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 0));
        leftTopPanel.setBackground(Color.WHITE);

        // Back 버튼
        JButton backbtn = new JButton();
        ImageIcon button_back = new ImageIcon(getClass().getResource("/img/button_back.png"));
        backbtn.setIcon(updateImageSize(button_back, 25, 25));
        backbtn.setContentAreaFilled(false);
        backbtn.setBorderPainted(false);
        backbtn.setFocusPainted(false);
        backbtn.setPreferredSize(new Dimension(40, 40));
        backbtn.addActionListener(e -> {
            ChoosePage page = new ChoosePage();
            page.setLocation(350, 220);
            setVisible(false);
            dispose();
        });
        leftTopPanel.add(backbtn);

        // Mixing 레이블
        JLabel leftLabel = new JLabel(" Mixing");
        leftLabel.setFont(new Font("Arial", Font.BOLD, 20));
        leftTopPanel.add(leftLabel);

        // 중앙 (Play All 버튼과 Stop 버튼)
        JPanel centerTopPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        centerTopPanel.setBackground(Color.WHITE);

        // Play All 버튼
        JButton playAllButton = new JButton("");
        ImageIcon button_play = new ImageIcon(getClass().getResource("/img/button_play.png"));
        playAllButton.setIcon(updateImageSize(button_play, 25, 25));
        playAllButton.setContentAreaFilled(false);
        playAllButton.setBorderPainted(false);
        playAllButton.setFocusPainted(false);
        playAllButton.setPreferredSize(new Dimension(50, 50));
        playAllButton.addActionListener(e -> {
            new Thread(() -> playAudio(audioFile1)).start();
            new Thread(() -> playAudio(audioFile2)).start();
            new Thread(() -> playAudio(audioFile3)).start();
        });
        centerTopPanel.add(playAllButton);

        // Stop 버튼
        JButton stopbtn = new JButton("");
        ImageIcon button_stop = new ImageIcon(getClass().getResource("/img/button_stop.png"));
        stopbtn.setIcon(updateImageSize(button_stop, 25, 25));
        stopbtn.setContentAreaFilled(false);
        stopbtn.setBorderPainted(false);
        stopbtn.setFocusPainted(false);
        stopbtn.setPreferredSize(new Dimension(40, 40));
        centerTopPanel.add(stopbtn);

        // 왼쪽과 중앙 패널을 topPanel에 추가
        topPanel.add(leftTopPanel, BorderLayout.WEST);
        topPanel.add(centerTopPanel, BorderLayout.CENTER);

        // 왼쪽에 세로로 정렬된 버튼 패널 생성
        JPanel leftPanel2 = new JPanel();
        leftPanel2.setLayout(new BoxLayout(leftPanel2, BoxLayout.Y_AXIS));
        leftPanel2.setBackground(Color.WHITE); // 배경색 흰색 설정
        contentPane.add(leftPanel2, BorderLayout.WEST);
        
        nowLabel = new JLabel(new ImageIcon(getClass().getResource("/img/now.png"))); // 현재 위치 표시 이미지
        nowLabel.setSize(10, 50); // 이미지 크기 설정
        contentPane.add(nowLabel); // 메인 패널에 추가
        
        // 상단에 간격 추가
        leftPanel2.add(Box.createVerticalStrut(0));  // 위쪽 여백 추가
        leftPanel2.add(createButtonWithRectangle("/img/piano.png", "piano", audioFile1, 0));
        leftPanel2.add(createButtonWithRectangle("/img/acousticGuitar.png", "acoustic", audioFile2, 1));
        leftPanel2.add(createButtonWithRectangle("/img/electricGuitar.png", "electric", audioFile3, 2));
    }

    // 오디오 재생 메서드
    private void playAudio(String filePath) {
        try {
            File audioFile = new File(filePath);
            if (!audioFile.exists()) {
                System.out.println("파일이 존재하지 않습니다: " + filePath);
                return;
            }

            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();

            // 오디오 재생 중 대기
            Thread.sleep(clip.getMicrosecondLength() / 1000);

            clip.close(); // 리소스 해제
            System.out.println("재생 완료: " + filePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 버튼과 색깔 상자를 포함하는 패널 생성
    private JPanel createButtonWithRectangle(String imagePath, String text, String audioFile, int index) {
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 0));

        ImageIcon buttonIcon = new ImageIcon(getClass().getResource(imagePath));
        Image img = buttonIcon.getImage();
        Image resizedImg = img.getScaledInstance(70, 70, Image.SCALE_SMOOTH);
        buttonIcon = new ImageIcon(resizedImg);

        JButton playButton = new JButton(buttonIcon);
        playButton.setContentAreaFilled(false);
        playButton.setBorderPainted(false);
        playButton.setFocusPainted(false);
        playButton.setText(text);
        playButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        playButton.setHorizontalTextPosition(SwingConstants.CENTER);
        
        // 오디오 길이 가져오기
        int audioLength = getAudioLength(audioFile); // 초 단위
        int boxWidth = Math.max(50, audioLength * 20); // 최소 50px, 초당 20px 크기

        // 회색 상자 패널 생성
        JPanel trackPanel = new JPanel();
        trackPanel.setPreferredSize(new Dimension(700, 70));
        trackPanel.setBackground(Color.LIGHT_GRAY); // 기본 색깔
        trackPanels.add(trackPanel); // 색깔 상자를 trackPanels 리스트에 추가

        // 작은 색깔 박스 생성
        JPanel smallTrackPanel = new JPanel();
        smallTrackPanel.setPreferredSize(new Dimension(boxWidth, 50)); // 폭은 오디오 길이에 비례
        smallTrackPanel.setBackground(getColorForTrack(index)); // 색상 설정

        // 작은 색깔 박스의 Y 위치를 중앙에 설정
        int trackPanelHeight = trackPanel.getPreferredSize().height;
        int smallTrackPanelHeight = smallTrackPanel.getPreferredSize().height;
        int yPosition = (trackPanelHeight - smallTrackPanelHeight) / 2; // 중앙 위치 계산

        // 드래그 이벤트 추가
        smallTrackPanel.addMouseListener(new MouseAdapter() {
            private int startX;

            @Override
            public void mousePressed(MouseEvent e) {
                startX = e.getX(); // 드래그 시작 위치 저장
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                int newX = e.getXOnScreen() - smallTrackPanel.getParent().getLocationOnScreen().x; // 새로운 X 위치
                int newStart = Math.max(0, Math.min(newX, 700 - smallTrackPanel.getWidth())); // 범위 제한
                
                // 음악 재생 시작 시간 조정
                int offset = newStart / 10; // 10px 당 1초로 간주 (조정 가능)
                
                // 작은 색깔 박스를 새로운 위치로 설정
                smallTrackPanel.setBounds(newStart, yPosition, smallTrackPanel.getWidth(), smallTrackPanel.getHeight()); // Y 위치 설정
            }
        });

        // trackPanel에 작은 색깔 박스를 추가
        trackPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 15)); // 여백 조정
        smallTrackPanel.setBounds(0, yPosition, smallTrackPanel.getWidth(), smallTrackPanel.getHeight()); // 초기 위치 설정
        trackPanel.add(smallTrackPanel);

        // playButton과 trackPanel을 panel에 추가
        panel.add(playButton);
        panel.add(trackPanel);

        return panel; // panel을 반환
    }

    // 오디오 재생 메서드 (시작 시간 오프셋 추가)
    private void playAudioWithOffset(String filePath, int offset) {
        try {
            File audioFile = new File(filePath);
            if (!audioFile.exists()) {
                System.out.println("파일이 존재하지 않습니다: " + filePath);
                return;
            }

            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);

            // 오프셋에 따라 재생 시작
            clip.setMicrosecondPosition(offset * 1_000_000); // 초를 마이크로초로 변환하여 설정
            clip.start();

            // 오디오 재생 중 대기
            Thread.sleep(clip.getMicrosecondLength() / 1000);

            clip.close(); // 리소스 해제
            System.out.println("재생 완료: " + filePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Color getColorForTrack(int index) {
        switch (index) {
            case 0: return new Color(251, 48, 156); // 피아노 색상
            case 1: return new Color(48, 187, 251); // 어쿠스틱 기타 색상
            case 2: return new Color(106, 251, 48); // 일렉트릭 기타 색상
            default: return Color.LIGHT_GRAY; // 기본 색상
        }
    }

    // 특정 트랙의 색깔을 토글하는 메서드
    private void toggleTrackColor(int index) {
        if (trackStates[index]) {
            trackPanels.get(index).setBackground(Color.LIGHT_GRAY); // 기본 색깔
        } else {
            trackPanels.get(index).setBackground(Color.LIGHT_GRAY); // 활성화된 색깔
        }
        trackStates[index] = !trackStates[index];
    }

    // 모든 트랙 색깔을 토글하는 메서드 (Play All 버튼에서 호출)
    private void toggleAllTracks() {
        for (int i = 0; i < trackStates.length; i++) {
            if (trackStates[i]) {
                trackPanels.get(i).setBackground(Color.LIGHT_GRAY);
            } else {
                trackPanels.get(i).setBackground(Color.LIGHT_GRAY);
            }
            trackStates[i] = !trackStates[i];
        }
    }

    private int getAudioLength(String filePath) {
        try {
            File audioFile = new File(filePath);
            if (!audioFile.exists()) {
                System.out.println("파일이 존재하지 않습니다: " + filePath);
                return 0;
            }

            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);

            // 오디오 길이 (마이크로초 → 초 단위 변환)
            long microseconds = clip.getMicrosecondLength();
            int seconds = (int) (microseconds / 1_000_000);
            clip.close(); // 리소스 해제
            return seconds;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
 
    private ImageIcon updateImageSize(ImageIcon icon, int width, int height) {
        Image image = icon.getImage();
        Image updatedImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(updatedImage);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                Mixing frame = new Mixing();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
