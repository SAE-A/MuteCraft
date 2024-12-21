import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.SequenceInputStream;
import java.util.ArrayList;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;

public class AcousticPage extends JFrame {
    private boolean isRecording = false; // 녹음 상태 여부
    private boolean isMetronomePlaying = false; // 메트로놈 재생 상태
    private Clip metronomeClip; // 메트로놈 오디오 클립
    private JComboBox<String> metronomeSelector; // BPM 선택 드롭다운
    private JButton metronomebtn; // 메트로놈 버튼
    private File recordedFile = null; // 녹음 파일
    private AudioFormat audioFormat; // 오디오 포맷
    private ByteArrayOutputStream audioStream; // 오디오 데이터 저장
    private TargetDataLine targetLine; // 녹음 장치
    private ArrayList<File> codeFiles = new ArrayList<>(); // 코드 음원 파일 목록

    public AcousticPage() {
        setTitle("Acoustic Guitar");
        setSize(868, 393);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Container contentPane = getContentPane();
        contentPane.setLayout(null);
        contentPane.setBackground(Color.WHITE);

        JPanel panel_buttons = new JPanel();
        panel_buttons.setBackground(Color.WHITE); // 배경색 하얀색으로 설정
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
        JLabel leftLabel = new JLabel("Acoustic");
        leftLabel.setFont(new Font("Arial", Font.BOLD, 20));
        
        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(Color.WHITE); // 배경색 하얀색으로 설정
        leftPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        leftPanel.add(backbtn);
        leftPanel.add(leftLabel);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 2));
        centerPanel.setBackground(Color.WHITE); // 배경색 하얀색으로 설정
        JButton playbtn = new JButton("");
        JButton stopbtn = new JButton("");
        JButton recordbtn = new JButton("");
        JButton metronomebtn = new JButton("");
        centerPanel.add(playbtn);
        centerPanel.add(stopbtn);
        centerPanel.add(recordbtn);
        centerPanel.add(metronomebtn);

        JPanel rightPanel = new JPanel();
        rightPanel.setBackground(Color.WHITE); // 배경색 하얀색으로 설정
        rightPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.add(addbtn);

        addCodeButton(contentPane, "Em", new ImageIcon(getClass().getResource("/img/g_notes/icon_Em.png")), "guitar/acoustic_Em.wav", 105, 100);
        addCodeButton(contentPane, "Am", new ImageIcon(getClass().getResource("/img/g_notes/icon_Am.png")), "guitar/acoustic_Am.wav", 219, 95);
        addCodeButton(contentPane, "Dm", new ImageIcon(getClass().getResource("/img/g_notes/icon_Dm.png")), "guitar/acoustic_Dm.wav", 327, 89);
        addCodeButton(contentPane, "G", new ImageIcon(getClass().getResource("/img/g_notes/icon_G.png")), "guitar/acoustic_G.wav", 429, 83);
        addCodeButton(contentPane, "C", new ImageIcon(getClass().getResource("/img/g_notes/icon_C.png")), "guitar/acoustic_C.wav", 522, 80);
        addCodeButton(contentPane, "F", new ImageIcon(getClass().getResource("/img/g_notes/icon_F.png")), "guitar/acoustic_F.wav", 614, 74);
        addCodeButton(contentPane, "Bb", new ImageIcon(getClass().getResource("/img/g_notes/icon_Bb.png")), "guitar/acoustic_Bb.wav", 700, 74);
        addCodeButton(contentPane, "Bdim", new ImageIcon(getClass().getResource("/img/g_notes/icon_Bdim.png")), "guitar/acoustic_Bdim.wav", 780, 74);
        
        backbtn.setIcon(updateImageSize(button_back, 25, 25));
        backbtn.setContentAreaFilled(false);
        backbtn.setBorderPainted(false);
        backbtn.setFocusPainted(false);
        backbtn.setPreferredSize(new Dimension(40, 40));
        backbtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ChoosePage page = new ChoosePage();
                page.setLocation(350, 220);
               setVisible(false);
                dispose();  // 현재 창 닫기
            }
        });
        
        playbtn.setIcon(updateImageSize(button_play, 25, 25));
        playbtn.setContentAreaFilled(false);
        playbtn.setBorderPainted(false);
        playbtn.setFocusPainted(false);
        playbtn.setPreferredSize(new Dimension(50, 50));
        playbtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                playRecordedSound();
            }
        });
        
        stopbtn.setIcon(updateImageSize(button_stop, 25, 25));
        stopbtn.setContentAreaFilled(false);
        stopbtn.setBorderPainted(false);
        stopbtn.setFocusPainted(false);
        stopbtn.setPreferredSize(new Dimension(50, 50));
        stopbtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stopRecording();
            }
        });

        recordbtn.setIcon(updateImageSize(button_record, 25, 25));
        recordbtn.setContentAreaFilled(false);
        recordbtn.setBorderPainted(false);
        recordbtn.setFocusPainted(false);
        recordbtn.setPreferredSize(new Dimension(50, 50));
        recordbtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               startRecording();  // 녹음 시작
            }
        });
        
        metronomebtn.setIcon(updateImageSize(metronome, 25, 25));
        metronomebtn.setContentAreaFilled(false);
        metronomebtn.setBorderPainted(false);
        metronomebtn.setFocusPainted(false);
        metronomebtn.setPreferredSize(new Dimension(50, 50));
        customizeButton(metronomebtn);
        
        metronomeSelector = new JComboBox<>(new String[]{"none", "60bpm", "80bpm", "100bpm", "120bpm"});
        customizeComboBox(metronomeSelector);
        metronomeSelector.setVisible(false); // 초기에는 숨김 상태
        centerPanel.add(metronomeSelector);

        // 메트로놈 버튼 리스너
        metronomebtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 드롭박스의 가시성을 토글
                boolean isVisible = metronomeSelector.isVisible();
                metronomeSelector.setVisible(!isVisible);
                centerPanel.revalidate(); // 패널 갱신
                centerPanel.repaint();   // 패널 다시 그리기
            }
        });
       
        // 드롭박스 선택 시 BPM에 따라 메트로놈 재생/중지
        metronomeSelector.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedBPM = (String) metronomeSelector.getSelectedItem();
                if (selectedBPM != null) {
                    if (selectedBPM.equals("none")) {
                        stopMetronome(); // 메트로놈 중지
                        metronomeSelector.setVisible(false); // 드롭박스 숨기기
                        return; // 더 이상 진행하지 않음
                    }
                    if (isMetronomePlaying) {
                        stopMetronome(); // 멈추고
                    }
                    playMetronome(selectedBPM); // 재생
                    metronomeSelector.setVisible(false); // 드롭박스 숨기기
                }
            }
        });

        addbtn.setIcon(updateImageSize(button_add, 25, 25));
        addbtn.setContentAreaFilled(false);
        addbtn.setBorderPainted(false);
        addbtn.setFocusPainted(false);
        addbtn.setPreferredSize(new Dimension(40, 40));
        
        addbtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("add 버튼 클릭!");
                Mixing mixing = new Mixing();
                mixing.setVisible(true);
            }
        });
        
        ImageIcon guitarIcon = new ImageIcon(getClass().getResource("/img/acoustic_guitar.png"));
        JLabel guitarLabel = new JLabel(updateImageSize(guitarIcon, 852, 291));
        guitarLabel.setBounds(0, 100, 852, 291);
        contentPane.add(guitarLabel);
        
        panel_buttons.add(leftPanel, BorderLayout.WEST);
        panel_buttons.add(centerPanel, BorderLayout.CENTER);
        panel_buttons.add(rightPanel, BorderLayout.EAST);
        contentPane.add(panel_buttons);
        
        setVisible(true);
    }

    // 코드 추가
    private void addCodeButton(Container contentPane, String codeName, ImageIcon icon, String soundFilePath, int x, int width) {
        // 커스텀 패널 생성
        JPanel codePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // 이미지 그리기
                g.drawImage(icon.getImage(), 0, 0, width, 336, this);
                
                // 텍스트 스타일 설정
                Font font = new Font("Arial", Font.PLAIN, 22); // 폰트, 스타일, 크기 설정
                g.setFont(font);
                g.setColor(Color.black); // 텍스트 색상 설정
                
                // 텍스트 그리기 (이미지 위에 위치)
                g.drawString(codeName, width / 2 - g.getFontMetrics().stringWidth(codeName) / 2, 27); // 텍스트 위치 조정
            }
        };

        codePanel.setBounds(x, 60, width, 336);
        codePanel.setOpaque(false); // 배경 투명하게 설정

        // 버튼 클릭 이벤트 추가
        codePanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                playSound(soundFilePath);
                if (isRecording) {
                    mergeSoundToRecording(soundFilePath);
                }
            }
        });

        contentPane.add(codePanel);
    }

    // 임의의 사운드 파일 재생
    private void playSound(String soundFile) {
        File audioFile = new File("src/resources/lydfiler/audio/" + soundFile);

        // 파일 존재 여부 확인
        if (!audioFile.exists()) {
            System.out.println("파일이 존재하지 않습니다: " + soundFile);
            return;
        }

        try {
            // 오디오 스트림 열기
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
            Clip audioClip = AudioSystem.getClip();
            audioClip.open(audioStream);
            
            System.out.println("사운드 파일 재생 중: " + soundFile);
            audioClip.start();

            // 재생 완료를 감지하고 리소스 해제
            audioClip.addLineListener(event -> {
                if (event.getType() == javax.sound.sampled.LineEvent.Type.STOP) {
                    audioClip.close();
                    System.out.println("사운드 재생 완료: " + soundFile);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void startRecording() {
        try {
            audioFormat = new AudioFormat(44100.0f, 16, 1, true, true);
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, audioFormat);

            if (!AudioSystem.isLineSupported(info)) {
                System.out.println("Line not supported");
                return;
            }

            targetLine = (TargetDataLine) AudioSystem.getLine(info);
            targetLine.open(audioFormat);
            targetLine.start();

            audioStream = new ByteArrayOutputStream();
            isRecording = true;

            System.out.println("녹음 시작");

            new Thread(() -> {
                try {
                    File codeFile = new File("src/resources/lydfiler/audio/record_acoustic.wav");
                    AudioInputStream codeStream = AudioSystem.getAudioInputStream(codeFile);

                    if (!codeStream.getFormat().matches(audioFormat)) {
                        codeStream = AudioSystem.getAudioInputStream(audioFormat, codeStream);
                    }

                    SequenceInputStream combinedStream = new SequenceInputStream(
                            new AudioInputStream(targetLine), codeStream);

                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while (isRecording) {
                        bytesRead = combinedStream.read(buffer, 0, buffer.length);
                        if (bytesRead > 0) {
                            audioStream.write(buffer, 0, bytesRead);
                        }
                    }
                    combinedStream.close();
                    audioStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopRecording() {
        try {
            if (targetLine != null) {
                isRecording = false;
                targetLine.stop();
                targetLine.close();
                System.out.println("녹음 중지.");

                // 병합된 스트림을 AudioInputStream으로 변환
                ByteArrayInputStream bais = new ByteArrayInputStream(audioStream.toByteArray());
                AudioInputStream ais = new AudioInputStream(bais, audioFormat, audioStream.size() / audioFormat.getFrameSize());

                String filePath = "src/resources/lydfiler/audio/record_acoustic.wav";
                recordedFile = new File(filePath);
                recordedFile.getParentFile().mkdirs(); // 필요시 폴더 생성

                AudioSystem.write(ais, AudioFileFormat.Type.WAVE, recordedFile);
                System.out.println("녹음 저장 완료: " + recordedFile.getAbsolutePath());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void playRecordedSound() {
        if (recordedFile == null || !recordedFile.exists()) {
            System.out.println("재생할 녹음 파일이 없습니다.");
            return;
        }

        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(recordedFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
            System.out.println("녹음 파일 재생 중...");

            clip.addLineListener(event -> {
                if (event.getType() == javax.sound.sampled.LineEvent.Type.STOP) {
                    clip.close();
                    System.out.println("녹음 파일 재생 완료.");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void mergeSoundToRecording(String soundFilePath) {
        try {
            File soundFile = new File("src/resources/lydfiler/audio/" + soundFilePath);
            AudioInputStream codeStream = AudioSystem.getAudioInputStream(soundFile);

            if (!codeStream.getFormat().matches(audioFormat)) {
                codeStream = AudioSystem.getAudioInputStream(audioFormat, codeStream);
            }

            AudioInputStream combinedStream = new AudioInputStream(
                    new SequenceInputStream(
                            new ByteArrayInputStream(audioStream.toByteArray()),
                            codeStream),
                    audioFormat,
                    audioStream.size() / audioFormat.getFrameSize() + codeStream.getFrameLength());

            ByteArrayOutputStream updatedStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = combinedStream.read(buffer)) != -1) {
                updatedStream.write(buffer, 0, bytesRead);
            }

            audioStream = updatedStream;
            System.out.println("기타 코드 추가: " + soundFilePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // 이미지 크기 조정
    private ImageIcon updateImageSize(ImageIcon icon, int width, int height) {
        Image image = icon.getImage();
        Image updatedImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(updatedImage);
    }
    
    private void toggleMetronome() {
        // 현재 메트로놈 상태 확인
        if (isMetronomePlaying) {
            stopMetronome(); // 재생 중이면 중지
        } else {
            String selectedBPM = (String) metronomeSelector.getSelectedItem(); // 드롭박스에서 선택된 BPM 가져오기

            // 선택된 BPM이 유효한지 확인
            if (selectedBPM != null && !selectedBPM.equals("none")) {
                playMetronome(selectedBPM); // 유효하면 메트로놈 재생
            } else {
                System.out.println("BPM을 선택하세요.");
            }
        }
    }

    private void playMetronome(String bpm) {
        // BPM에 따라 명시적으로 파일 경로 설정
        String filePath = "";
        switch (bpm) {
            case "60bpm":
                filePath = "metronome/metronome_60bpm.wav";
                break;
            case "80bpm":
                filePath = "metronome/metronome_80bpm.wav";
                break;
            case "100bpm":
                filePath = "metronome/metronome_100bpm.wav";
                break;
            case "120bpm":
                filePath = "metronome/metronome_120bpm.wav";
                break;
            default:
                System.out.println("선택한 BPM의 파일이 없습니다.");
                return;
        }

        // 기존 메트로놈을 멈추고 새로운 메트로놈 재생
        stopMetronome(); // 현재 재생 중인 메트로놈을 멈춤

        try {
            // 오디오 파일을 읽어서 클립을 생성하고 재생
            File audioFile = new File("src/resources/lydfiler/audio/" + filePath);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
            metronomeClip = AudioSystem.getClip();
            metronomeClip.open(audioStream);
            metronomeClip.loop(Clip.LOOP_CONTINUOUSLY); // 메트로놈 반복 재생
            metronomeClip.start();

            isMetronomePlaying = true;
            System.out.println(bpm + " 메트로놈 재생 시작");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopMetronome() {
        if (metronomeClip != null && metronomeClip.isRunning()) {
            metronomeClip.stop();
            metronomeClip.close();
            isMetronomePlaying = false;
            System.out.println("메트로놈 중지");
        }
    }
    
    private void customizeButton(JButton button) {
        button.setBackground(Color.white);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 0));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void customizeComboBox(JComboBox<String> comboBox) {
        comboBox.setPreferredSize(new Dimension(100, 25));
        comboBox.setBackground(Color.white);
        comboBox.setForeground(Color.black);
        comboBox.setFont(new Font("Arial", Font.PLAIN, 14));

        comboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                label.setOpaque(true);
                label.setBackground(isSelected ? Color.darkGray : Color.white);
                label.setForeground(isSelected ? Color.WHITE : Color.BLACK);
                label.setFont(new Font("Arial", Font.PLAIN, 14));
                return label;
            }
        });
    }

    public static void main(String[] args) {
        new AcousticPage();
    }
}