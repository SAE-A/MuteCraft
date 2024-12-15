import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class ElectricPage extends JFrame {
	private boolean isRecording = false; // 녹음 상태 여부
    private TargetDataLine targetLine;
    private boolean isMetronomePlaying = false; // 메트로놈 재생 상태
    private Clip metronomeClip; // 메트로놈 오디오 클립
    private JComboBox<String> metronomeSelector; // BPM 선택 드롭다운
    private JButton metronomebtn; // 메트로놈 버튼

    public ElectricPage() {
        // JFrame 기본 설정
        setTitle("Acoustic Guitar");
        setBounds(100, 100, 867, 393); // 창 위치 및 크기 설정
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 컨테이너 설정
        Container contentPane = getContentPane();
        contentPane.setLayout(null);
        contentPane.setBackground(Color.WHITE);

        // panel_buttons (버튼을 담을 패널)
        JPanel panel_buttons = new JPanel();
        panel_buttons.setBackground(Color.WHITE); // 배경색 하얀색으로 설정
        panel_buttons.setBounds(0, 0, 852, 50);
        panel_buttons.setLayout(new BorderLayout());

        // 이미지 아이콘
        ImageIcon button_back = new ImageIcon(getClass().getResource("/img/button_back.png"));
        ImageIcon button_play = new ImageIcon(getClass().getResource("/img/button_play.png"));
        ImageIcon button_stop = new ImageIcon(getClass().getResource("/img/button_stop.png"));
        ImageIcon button_record = new ImageIcon(getClass().getResource("/img/button_record.png"));
        ImageIcon metronome = new ImageIcon(getClass().getResource("/img/metronome.png"));
        ImageIcon button_add = new ImageIcon(getClass().getResource("/img/button_add.png"));
        
        JButton backbtn = new JButton("");
        JButton addbtn = new JButton("");
        JLabel leftLabel = new JLabel("Electric");
        leftLabel.setFont(new Font("Serif", Font.PLAIN, 20));
        
        // 왼쪽 패널
        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(Color.WHITE); // 배경색 하얀색으로 설정
        leftPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        leftPanel.add(backbtn);
        leftPanel.add(leftLabel);

        // 중앙 패널
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

        // 오른쪽 패널
        JPanel rightPanel = new JPanel();
        rightPanel.setBackground(Color.WHITE); // 배경색 하얀색으로 설정
        rightPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.add(addbtn);

        // 코드 버튼 추가 (Em, Am, Dm 등)
        addCodeButton(contentPane, "Em", new ImageIcon(getClass().getResource("/img/g_notes/icon_Em2.png")), "guitar/electric_Em.wav", 115, 100);
        addCodeButton(contentPane, "Am", new ImageIcon(getClass().getResource("/img/g_notes/icon_Am2.png")), "guitar/electric_Am.wav", 225, 95);
        addCodeButton(contentPane, "Dm", new ImageIcon(getClass().getResource("/img/g_notes/icon_Dm2.png")), "guitar/electric_Dm.wav", 330, 89);
        addCodeButton(contentPane, "G", new ImageIcon(getClass().getResource("/img/g_notes/icon_G2.png")), "guitar/electric_G.wav", 429, 83);
        addCodeButton(contentPane, "C", new ImageIcon(getClass().getResource("/img/g_notes/icon_C2.png")), "guitar/electric_C.wav", 522, 80);
        addCodeButton(contentPane, "F", new ImageIcon(getClass().getResource("/img/g_notes/icon_F2.png")), "guitar/electric_F.wav", 614, 74);
        addCodeButton(contentPane, "Bb", new ImageIcon(getClass().getResource("/img/g_notes/icon_Bb2.png")), "guitar/electric_Bb.wav", 695, 74);
        addCodeButton(contentPane, "Bdim", new ImageIcon(getClass().getResource("/img/g_notes/icon_Bdim2.png")), "guitar/electric_Bdim.wav", 775, 74);
        
        // 이미지 버튼 설정
        backbtn.setIcon(updateImageSize(button_back, 25, 25));
        backbtn.setContentAreaFilled(false);
        backbtn.setBorderPainted(false);
        backbtn.setFocusPainted(false);
        backbtn.setPreferredSize(new Dimension(50, 50));

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

        metronomeSelector = new JComboBox<>(new String[]{"none", "60bpm", "80bpm", "100bpm", "120bpm"});
        metronomeSelector.setPreferredSize(new Dimension(100, 25));
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
                    // none이 선택된 경우 메트로놈 중지
                    if (selectedBPM.equals("none")) {
                        stopMetronome(); // 메트로놈 중지
                        metronomeSelector.setVisible(false); // 드롭박스 숨기기
                        return; // 더 이상 진행하지 않음
                    }
                    // 현재 재생 중인 메트로놈이 있다면 중지
                    if (isMetronomePlaying) {
                        stopMetronome(); // 멈추고
                    }
                    // 새로운 BPM으로 메트로놈 재생
                    playMetronome(selectedBPM); // 재생
                    metronomeSelector.setVisible(false); // 드롭박스 숨기기
                }
            }
        });

        addbtn.setIcon(updateImageSize(button_add, 25, 25));
        addbtn.setContentAreaFilled(false);
        addbtn.setBorderPainted(false);
        addbtn.setFocusPainted(false);
        addbtn.setPreferredSize(new Dimension(50, 50));
        
        addbtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("add 버튼 클릭!");
                Mixing mixing = new Mixing();
                mixing.setVisible(true);
            }
        });
        
        // 기타 이미지 추가
        ImageIcon guitarIcon = new ImageIcon(getClass().getResource("/img/electric_guitar.png"));
        JLabel guitarLabel = new JLabel(updateImageSize(guitarIcon, 852, 291));
        guitarLabel.setBounds(0, 100, 852, 291);
        contentPane.add(guitarLabel);
        
        // 패널에 추가
        panel_buttons.add(leftPanel, BorderLayout.WEST);
        panel_buttons.add(centerPanel, BorderLayout.CENTER);
        panel_buttons.add(rightPanel, BorderLayout.EAST);
        contentPane.add(panel_buttons);

        // panel_notes (버튼을 담을 패널)
        JPanel panel_notes_white = new JPanel();
        panel_notes_white.setBounds(0, 260, 852, 100); // 윈도우의 세로 260부터 시작, 패널 높이를 100으로 설정 (버튼 간 세로 간격을 충분히 띄우기 위해 높이를 늘림)
        panel_notes_white.setLayout(new GridLayout(2, 14, 10, 7));  // 14개의 버튼, 2줄로 배치, 간격 설정
        panel_notes_white.setBackground(null);  // 배경을 투명하게 설정
        panel_notes_white.setOpaque(false);    // 패널도 투명하게 설정
        contentPane.add(panel_notes_white);    // contentPane에 패널을 추가
        
        // 화면 표시
        setVisible(true);
    }

    // 코드 버튼 추가
    private void addCodeButton(Container contentPane, String codeName, ImageIcon icon, String soundFilePath, int x, int width) {
        JButton codeButton = new JButton(updateImageSize(icon, width, 336));
        codeButton.setBounds(x, 55, width, 336);
        codeButton.setContentAreaFilled(false);
        codeButton.setBorderPainted(false);
        codeButton.setFocusPainted(false);
        codeButton.setOpaque(false);
        codeButton.addActionListener(e -> playSound(soundFilePath));
        contentPane.add(codeButton);

        JLabel codeTextLabel = new JLabel(codeName, SwingConstants.CENTER);
        codeTextLabel.setFont(new Font("Arial", Font.BOLD, 23));
        codeTextLabel.setForeground(Color.BLACK);
        codeTextLabel.setBounds(x, 400, width, 20);
        contentPane.add(codeTextLabel);
    }
    
    public void startRecording() {
        if (isRecording) {
            System.out.println("이미 녹음 중입니다.");
            return;
        }

        isRecording = true;

        new Thread(() -> {
            try {
                AudioFormat format = new AudioFormat(44100, 16, 1, true, false);
                DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
                if (!AudioSystem.isLineSupported(info)) {
                    System.err.println("오디오 라인이 지원되지 않습니다.");
                    return;
                }

                targetLine = (TargetDataLine) AudioSystem.getLine(info);
                targetLine.open(format);
                targetLine.start();

                File outputDir = new File("src/resources/lydfiler/audio");
                if (!outputDir.exists()) {
                    outputDir.mkdirs();
                }

                File outputFile = new File(outputDir, "record_electric.wav");

                AudioInputStream audioStream = new AudioInputStream(targetLine);

                System.out.println("녹음 중... 저장 위치: " + outputFile.getAbsolutePath());
                AudioSystem.write(audioStream, AudioFileFormat.Type.WAVE, outputFile);

            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                isRecording = false;
                System.out.println("녹음 완료.");
            }
        }).start();
    }

    public void stopRecording() {
        if (!isRecording || targetLine == null) {
            System.out.println("녹음이 진행 중이 아닙니다.");
            return;
        }

        targetLine.stop(); // 녹음 중지
        targetLine.close(); // 리소스 해제
        targetLine = null;
        isRecording = false;
        System.out.println("녹음 종료.");
        
        File outputFile = new File("src/resources/lydfiler/audio/record_electric.wav");
        if (outputFile.exists()) {
            System.out.println("파일 저장 성공: " + outputFile.getAbsolutePath());
        } else {
            System.out.println("파일 저장 실패.");
        }
        
    }
    
    private void playRecordedSound() {
        File audioFile = new File("src/resources/lydfiler/audio/record_electric.wav");

        // 파일이 존재하는지 확인
        if (!audioFile.exists()) {
            System.out.println("파일이 존재하지 않습니다: " + audioFile.getAbsolutePath());
            return;
        }

        try {
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
            Clip audioClip = AudioSystem.getClip();
            audioClip.open(audioStream);
            audioClip.start();
            Thread.sleep(audioClip.getMicrosecondLength() / 1000);  // 재생이 끝날 때까지 대기
            audioClip.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 사운드 재생 메서드
    private void playSound(String soundFile) {
        try {
            File audioFile = new File("src/resources/lydfiler/audio/" + soundFile);

            // 파일이 존재하는지 확인
            if (!audioFile.exists()) {
                System.out.println("파일이 존재하지 않습니다: " + soundFile);
                return;
            }

            // 오디오 파일을 읽어서 클립을 생성하고 재생
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
            Clip audioClip = AudioSystem.getClip();
            audioClip.open(audioStream);
            audioClip.start();

            // 비동기로 재생 (대기하지 않음)
            audioClip.addLineListener(event -> {
                if (event.getType() == javax.sound.sampled.LineEvent.Type.STOP) {
                    audioClip.close();
                }
            });
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

    public static void main(String[] args) {
        new ElectricPage();
    }
}