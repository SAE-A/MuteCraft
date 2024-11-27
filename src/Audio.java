import java.awt.*;
import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

public class Audio extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private TargetDataLine targetLine;
    private boolean isRecording = false; // 녹음 상태 확인용

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
            	Audio frame = new Audio();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public Audio() {
        setType(Type.UTILITY);
        setTitle("Piano");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 852, 393);

        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        // 패널 및 버튼 생성
        JPanel panel_buttons = new JPanel();
        panel_buttons.setBounds(0, 0, 852, 50);
        panel_buttons.setLayout(new BorderLayout());
        contentPane.add(panel_buttons);

        JButton recordbtn = new JButton("Record");
        JButton stopbtn = new JButton("Stop");

        // 녹음 버튼 액션
        recordbtn.addActionListener(e -> startRecording());

        // 정지 버튼 액션
        stopbtn.addActionListener(e -> stopRecording());

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 30, 10));
        centerPanel.add(recordbtn);
        centerPanel.add(stopbtn);
        panel_buttons.add(centerPanel, BorderLayout.CENTER);
    }

 // 녹음 시작
    private void startRecording() {
        if (isRecording) {
            System.out.println("이미 녹음 중입니다.");
            return;
        }

        isRecording = true;
        new Thread(() -> {
            try {
                AudioFormat format = getAudioFormat();
                DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
                if (!AudioSystem.isLineSupported(info)) {
                    System.err.println("오디오 라인이 지원되지 않습니다.");
                    return;
                }

                targetLine = (TargetDataLine) AudioSystem.getLine(info);
                targetLine.open(format);
                targetLine.start();

                // 상대 경로에 저장
                File outputDir = new File("record");
                if (!outputDir.exists()) {
                    outputDir.mkdirs(); // 디렉토리가 없으면 생성
                }
                File outputFile = new File(outputDir, "recorded_audio.wav");

                AudioInputStream audioStream = new AudioInputStream(targetLine);

                System.out.println("녹음 시작: " + outputFile.getAbsolutePath());
                AudioSystem.write(audioStream, AudioFileFormat.Type.WAVE, outputFile);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }).start();
    }

    // 녹음 중지
    private void stopRecording() {
        if (!isRecording) {
            System.out.println("현재 녹음 중이 아닙니다.");
            return;
        }

        isRecording = false;
        if (targetLine != null) {
            targetLine.stop();
            targetLine.close();
            System.out.println("녹음이 중지되었습니다.");
        }
    }

    // 오디오 형식 설정
    private AudioFormat getAudioFormat() {
        float sampleRate = 44100; // 샘플링 레이트
        int sampleSizeInBits = 16; // 샘플 크기
        int channels = 2; // 스테레오
        boolean signed = true; // 서명된 데이터
        boolean bigEndian = false; // 리틀 엔디안
        return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
    }
}
