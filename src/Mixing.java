import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.File;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class Mixing extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;

    // 오디오 파일 경로
    private String audioFile1 = "src/resources/lydfiler/audio/record_piano.wav";
    private String audioFile2 = "src/resources/lydfiler/audio/record_acoustic.wav";
    private String audioFile3 = "src/resources/lydfiler/audio/record_electric.wav";

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

    public Mixing() {
        setTitle("Mixing");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 852, 393);

        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(new BorderLayout());

        // 상단 레이아웃을 FlowLayout으로 설정 (Mixing 레이블은 왼쪽 정렬)
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));  // 왼쪽 정렬
        contentPane.add(topPanel, BorderLayout.NORTH);

        // "Mixing" 레이블 생성 (왼쪽에 배치)
        JLabel leftLabel = new JLabel("Mixing");
        leftLabel.setFont(new Font("Serif", Font.PLAIN, 20));
        topPanel.add(leftLabel);  // 레이블을 왼쪽에 추가

        // "Play All" 버튼을 가운데 배치하기 위한 별도 패널 생성
        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 260, 0));  // 가운데 정렬
        JButton playAllButton = new JButton("Play All");
        playAllButton.addActionListener(e -> {
            // 각 오디오 파일을 별도 스레드에서 재생
            new Thread(() -> playAudio(audioFile1)).start();
            new Thread(() -> playAudio(audioFile2)).start();
            new Thread(() -> playAudio(audioFile3)).start();
        });

        // 가운데 정렬을 위해 버튼을 centerPanel에 추가
        centerPanel.add(playAllButton);  // 버튼을 가운데에 배치

        // 가운데 패널을 topPanel에 추가하여 레이블과 버튼이 각각 왼쪽과 가운데에 배치되도록 함
        topPanel.add(Box.createHorizontalGlue());  // 남은 공간을 채우기 위한 Glue 추가
        topPanel.add(centerPanel);

        // 왼쪽에 세로로 정렬된 버튼 패널 생성
        JPanel leftPanel2 = new JPanel();
        leftPanel2.setLayout(new BoxLayout(leftPanel2, BoxLayout.Y_AXIS));
        contentPane.add(leftPanel2, BorderLayout.WEST);

        // 상단에 간격을 추가하여 버튼들이 너무 위쪽에 붙지 않게 함
        leftPanel2.add(Box.createVerticalStrut(50));  // 위쪽 여백 추가

        // 첫 번째 오디오 재생 버튼
        JButton playAudio1Button = new JButton("Play Audio 1");
        playAudio1Button.addActionListener(e -> playAudio(audioFile1));
        leftPanel2.add(playAudio1Button);

        // 두 번째 오디오 재생 버튼
        JButton playAudio2Button = new JButton("Play Audio 2");
        playAudio2Button.addActionListener(e -> playAudio(audioFile2));
        leftPanel2.add(Box.createVerticalStrut(10)); // 간격 추가
        leftPanel2.add(playAudio2Button);

        // 세 번째 오디오 재생 버튼
        JButton playAudio3Button = new JButton("Play Audio 3");
        playAudio3Button.addActionListener(e -> playAudio(audioFile3));
        leftPanel2.add(Box.createVerticalStrut(10)); // 간격 추가
        leftPanel2.add(playAudio3Button);
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

            // 클립이 끝날 때까지 대기
            Thread.sleep(clip.getMicrosecondLength() / 1000);

            clip.close(); // 리소스 해제
            System.out.println("재생 완료: " + filePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
