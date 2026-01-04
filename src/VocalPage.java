import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import javax.sound.sampled.*;

public class VocalPage extends JFrame {
    private boolean isRecording = false;
    private boolean isMetronomePlaying = false;
    private Clip metronomeClip;
    private JComboBox<String> metronomeSelector;
    private JButton metronomebtn;
    private File recordedFile = null;
    private AudioFormat audioFormat;
    private ByteArrayOutputStream audioStream;
    private TargetDataLine targetLine;
    private ArrayList<File> codeFiles = new ArrayList<>();
    private JPanel micPanel;
    private ImageIcon waveImage;

    public VocalPage() {
        setTitle("Vocal");
        setSize(868, 393);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 1. 그라데이션 배경을 가진 메인 패널 생성
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(82, 234, 145),
                        0, getHeight(), new Color(39, 174, 96)
                );
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(null); // 기존 null 레이아웃 유지
        setContentPane(mainPanel);

        waveImage = updateImageSize(new ImageIcon(getClass().getResource("/img/wave.png")), 500, 150);

        // 상단 버튼 패널 (투명 설정)
        JPanel panel_buttons = new JPanel();
        panel_buttons.setOpaque(false); // 배경 투명
        panel_buttons.setBounds(0, 0, 852, 50);
        panel_buttons.setLayout(new BorderLayout());

        ImageIcon button_back = new ImageIcon(getClass().getResource("/img/button_back.png"));
        ImageIcon button_play = new ImageIcon(getClass().getResource("/img/button_play.png"));
        ImageIcon button_stop = new ImageIcon(getClass().getResource("/img/button_stop.png"));
        ImageIcon button_record = new ImageIcon(getClass().getResource("/img/button_record.png"));
        ImageIcon metronome = new ImageIcon(getClass().getResource("/img/metronome.png"));
        ImageIcon button_add = new ImageIcon(getClass().getResource("/img/button_add.png"));

        JButton backbtn = new JButton("");
        JButton addbtn = new JButton("");
        JLabel leftLabel = new JLabel("Vocal");
        leftLabel.setFont(new Font("Arial", Font.BOLD, 20));
        leftLabel.setForeground(Color.BLACK);

        JPanel leftPanel = new JPanel();
        leftPanel.setOpaque(false); // 투명
        leftPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        leftPanel.add(backbtn);
        leftPanel.add(leftLabel);

        JPanel centerPanel = new JPanel();
        centerPanel.setOpaque(false); // 투명
        centerPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 2));

        JButton playbtn = new JButton("");
        JButton stopbtn = new JButton("");
        JButton recordbtn = new JButton("");
        metronomebtn = new JButton(""); // 필드 변수 사용

        centerPanel.add(playbtn);
        centerPanel.add(stopbtn);
        centerPanel.add(recordbtn);
        centerPanel.add(metronomebtn);

        JPanel rightPanel = new JPanel();
        rightPanel.setOpaque(false); // 투명
        rightPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.add(addbtn);

        // 버튼 스타일 설정
        setupBtn(backbtn, button_back, 25, 25);
        backbtn.addActionListener(e -> {
            new ChoosePage(); // ChoosePage 내부에서 중앙정렬
            dispose();
        });

        setupBtn(playbtn, button_play, 25, 25);
        playbtn.addActionListener(e -> playRecordedSound());

        setupBtn(stopbtn, button_stop, 25, 25);
        stopbtn.addActionListener(e -> stopRecording());

        setupBtn(recordbtn, button_record, 25, 25);
        recordbtn.addActionListener(e -> startRecording());

        setupBtn(metronomebtn, metronome, 25, 25);

        metronomeSelector = new JComboBox<>(new String[]{"none", "60bpm", "80bpm", "100bpm", "120bpm"});
        customizeComboBox(metronomeSelector);
        metronomeSelector.setVisible(false);
        centerPanel.add(metronomeSelector);

        metronomebtn.addActionListener(e -> {
            metronomeSelector.setVisible(!metronomeSelector.isVisible());
            centerPanel.revalidate();
            centerPanel.repaint();
        });

        metronomeSelector.addActionListener(e -> {
            String selectedBPM = (String) metronomeSelector.getSelectedItem();
            if (selectedBPM != null) {
                if (selectedBPM.equals("none")) {
                    stopMetronome();
                    metronomeSelector.setVisible(false);
                    return;
                }
                if (isMetronomePlaying) stopMetronome();
                playMetronome(selectedBPM);
                metronomeSelector.setVisible(false);
            }
        });

        setupBtn(addbtn, button_add, 25, 25);
        addbtn.addActionListener(e -> {
            try {
                new MixingClient().setVisible(true);
            } catch (Exception ex) { ex.printStackTrace(); }
            dispose();
        });

        // 파형 패널
        micPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (isRecording) {
                    drawWaveImage(g);
                }
            }
        };
        micPanel.setBounds(0, 65, 852, 293);
        micPanel.setOpaque(false);
        mainPanel.add(micPanel);

        // 마이크 아이콘
        ImageIcon micIcon = new ImageIcon(getClass().getResource("/img/mic2.png"));
        JLabel micLabel = new JLabel(updateImageSize(micIcon, 330, 250));
        micLabel.setBounds(0, 65, 852, 293);
        mainPanel.add(micLabel);

        panel_buttons.add(leftPanel, BorderLayout.WEST);
        panel_buttons.add(centerPanel, BorderLayout.CENTER);
        panel_buttons.add(rightPanel, BorderLayout.EAST);
        mainPanel.add(panel_buttons);

        // 하단 패널
        JPanel panel_notes_white = new JPanel();
        panel_notes_white.setBounds(0, 260, 852, 100);
        panel_notes_white.setLayout(new GridLayout(2, 14, 10, 7));
        panel_notes_white.setOpaque(false);
        mainPanel.add(panel_notes_white);

        setVisible(true);
    }

    // 버튼 설정을 위한 헬퍼 메소드
    private void setupBtn(JButton btn, ImageIcon icon, int w, int h) {
        btn.setIcon(updateImageSize(icon, w, h));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(50, 50));
    }

    // ... [기존 오디오 관련 메소드들: startRecording, stopRecording, playMetronome 등은 그대로 유지] ...

    private AudioFormat getAudioFormat() {
        float sampleRate = 44100;
        int sampleSizeInBits = 16;
        int channels = 1;
        boolean signed = true;
        boolean bigEndian = false;
        return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
    }

    private void drawWaveImage(Graphics g) {
        g.drawImage(waveImage.getImage(), 183, 50, this);
    }

    public void startRecording() {
        try {
            audioFormat = getAudioFormat();
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, audioFormat);
            if (!AudioSystem.isLineSupported(info)) return;

            targetLine = (TargetDataLine) AudioSystem.getLine(info);
            targetLine.open(audioFormat);
            targetLine.start();

            audioStream = new ByteArrayOutputStream();
            isRecording = true;
            micPanel.repaint();

            new Thread(() -> {
                byte[] buffer = new byte[1024];
                while (isRecording) {
                    int bytesRead = targetLine.read(buffer, 0, buffer.length);
                    if (bytesRead > 0) audioStream.write(buffer, 0, bytesRead);
                }
            }).start();
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void stopRecording() {
        if (!isRecording) return;
        isRecording = false;
        targetLine.stop();
        targetLine.close();
        micPanel.repaint();
        try {
            byte[] audioData = audioStream.toByteArray();
            recordedFile = new File("src/resources/lydfiler/audio/record_vocal.wav");
            recordedFile.getParentFile().mkdirs();
            AudioSystem.write(new AudioInputStream(new ByteArrayInputStream(audioData), audioFormat, audioData.length / audioFormat.getFrameSize()), AudioFileFormat.Type.WAVE, recordedFile);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void playRecordedSound() {
        if (recordedFile == null || !recordedFile.exists()) return;
        try {
            Clip clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(recordedFile));
            clip.start();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private ImageIcon updateImageSize(ImageIcon icon, int width, int height) {
        Image image = icon.getImage();
        return new ImageIcon(image.getScaledInstance(width, height, Image.SCALE_SMOOTH));
    }

    private void playMetronome(String bpm) {
        String filePath = "metronome/metronome_" + bpm + ".wav";
        stopMetronome();
        try {
            File audioFile = new File("src/resources/lydfiler/audio/" + filePath);
            AudioInputStream stream = AudioSystem.getAudioInputStream(audioFile);
            metronomeClip = AudioSystem.getClip();
            metronomeClip.open(stream);
            metronomeClip.loop(Clip.LOOP_CONTINUOUSLY);
            metronomeClip.start();
            isMetronomePlaying = true;
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void stopMetronome() {
        if (metronomeClip != null && metronomeClip.isRunning()) {
            metronomeClip.stop();
            metronomeClip.close();
            isMetronomePlaying = false;
        }
    }

    private void customizeComboBox(JComboBox<String> comboBox) {
        comboBox.setPreferredSize(new Dimension(100, 25));
        comboBox.setBackground(Color.white);
        comboBox.setForeground(Color.black);
        comboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                label.setBackground(isSelected ? Color.darkGray : Color.white);
                label.setForeground(isSelected ? Color.WHITE : Color.BLACK);
                return label;
            }
        });
    }

    public static void main(String[] args) {
        new VocalPage();
    }
}