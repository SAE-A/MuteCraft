import java.awt.*;
import javax.sound.sampled.*;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionListener;
import java.io.File;
import java.awt.event.ActionEvent;
import java.io.IOException;

public class PianoPage extends JFrame {
	private boolean isMetronomePlaying = false; // 메트로놈 재생 상태
    private Clip metronomeClip; // 메트로놈 오디오 클립
    private JComboBox<String> metronomeSelector; // BPM 선택 드롭다운
    private JButton metronomebtn; // 메트로놈 버튼

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    PianoPage frame = new PianoPage();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // 이미지 아이콘 리사이즈 메서드
    private ImageIcon resizeIcon(ImageIcon icon, int width, int height) {
        Image img = icon.getImage();
        Image resizedImage = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(resizedImage);
    }

    public PianoPage() {
        setType(Type.UTILITY);
        setTitle("Piano");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 852, 393);
        
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        // panel_buttons (버튼을 담을 패널)
        JPanel panel_buttons = new JPanel();
        panel_buttons.setBackground(Color.WHITE); // 배경색 하얀색으로 설정
        panel_buttons.setBounds(0, 0, 852, 50);
        panel_buttons.setLayout(new BorderLayout());

        // 이미지 아이콘 설정
        ImageIcon keyboard = new ImageIcon(getClass().getResource("/img/keyboard.png"));
        ImageIcon button_back = new ImageIcon(getClass().getResource("/img/button_back.png"));
        ImageIcon button_play = new ImageIcon(getClass().getResource("/img/button_play.png"));
        ImageIcon button_stop = new ImageIcon(getClass().getResource("/img/button_stop.png"));
        ImageIcon button_record = new ImageIcon(getClass().getResource("/img/button_record.png"));
        ImageIcon metronome = new ImageIcon(getClass().getResource("/img/metronome.png"));
        ImageIcon button_add = new ImageIcon(getClass().getResource("/img/button_add.png"));

        JButton backbtn = new JButton("");
        JButton addbtn = new JButton("");
        JLabel leftLabel = new JLabel("Piano");
        leftLabel.setFont(new Font("Serif", Font.PLAIN, 20));

        // 왼쪽 패널
        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(Color.WHITE); // 배경색 하얀색으로 설정
        leftPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        leftPanel.add(backbtn);
        leftPanel.add(leftLabel);

        // 중앙 패널
        JPanel centerPanel = new JPanel();
        centerPanel.setBackground(Color.WHITE); // 배경색 하얀색으로 설정
        centerPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 2));
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

        // 이미지 버튼 설정
        backbtn.setIcon(resizeIcon(button_back, 25, 25));
        backbtn.setContentAreaFilled(false);
        backbtn.setBorderPainted(false);
        backbtn.setFocusPainted(false);
        backbtn.setPreferredSize(new Dimension(50, 50));

        playbtn.setIcon(resizeIcon(button_play, 25, 25));
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

        stopbtn.setIcon(resizeIcon(button_stop, 25, 25));
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

        recordbtn.setIcon(resizeIcon(button_record, 25, 25));
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

        metronomebtn.setIcon(resizeIcon(metronome, 25, 25));
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
        
        addbtn.setIcon(resizeIcon(button_add, 25, 25));
        addbtn.setContentAreaFilled(false);
        addbtn.setBorderPainted(false);
        addbtn.setFocusPainted(false);
        addbtn.setPreferredSize(new Dimension(50, 50));
        
        addbtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("add 버튼 클릭!");
                Mixing mixing = new Mixing();
                // mixing.setAudioFilePath("src/resources/lydfiler/audio/record_piano.wav");
                mixing.setVisible(true);
            }
        });

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

        // 1번부터 28번까지 버튼을 동적으로 생성하여 이미지 로드
        for (int i = 1; i <= 28; i++) {
            JButton button = new JButton();
            String imagePath = "/img/notes/notes_" + String.format("%02d", i) + ".png";

            // getClass().getResource()를 사용하여 이미지를 불러오기
            ImageIcon noteIcon = new ImageIcon(getClass().getResource(imagePath));

            if (noteIcon.getImageLoadStatus() == MediaTracker.COMPLETE) {
                // 이미지를 버튼 크기에 맞게 리사이즈
                ImageIcon resizedIcon = new ImageIcon(noteIcon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH));
                button.setIcon(resizedIcon);

                // 버튼의 텍스트를 없애고 이미지만 표시되게 설정
                button.setText("");  // 텍스트 없애기
                button.setContentAreaFilled(false); // 버튼 배경을 없앰
                button.setBorderPainted(false);     // 버튼 테두리 없애기
                button.setPreferredSize(new java.awt.Dimension(40, 40));  // 버튼 크기 설정

                final int buttonIndex = i;  // 버튼 인덱스를 final로 처리
                button.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        System.out.println("btn" + buttonIndex + "버튼클릭!"); // 버튼 번호 출력
                        // 클릭된 버튼에 해당하는 음원 파일을 재생
                        String soundFile = "s" + String.format("%02d", buttonIndex) + ".wav";
                        playSound(soundFile);
                    }
                });

                // 버튼을 패널에 추가
                panel_notes_white.add(button);
            } else {
                System.out.println("이미지 파일이 존재하지 않습니다: " + imagePath);
            }
        }

        JPanel panel_notes_black = new JPanel();
        panel_notes_black.setBounds(0, 50, 852, 100);  // 패널 크기
        panel_notes_black.setLayout(null); // null 레이아웃으로 버튼 위치 자유 설정
        panel_notes_black.setOpaque(false);  // 배경색이 보이도록 설정
        contentPane.add(panel_notes_black);

        // 이미지 아이콘 로드
        ImageIcon c = new ImageIcon(getClass().getResource("/img/notes/notes_c#.png"));
        ImageIcon e = new ImageIcon(getClass().getResource("/img/notes/notes_eb.png"));
        ImageIcon f = new ImageIcon(getClass().getResource("/img/notes/notes_f#.png"));
        ImageIcon a = new ImageIcon(getClass().getResource("/img/notes/notes_ab.png"));
        ImageIcon b = new ImageIcon(getClass().getResource("/img/notes/notes_bb.png"));

        Image imgC = c.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
        ImageIcon resizedIconC = new ImageIcon(imgC);
        Image imgE = e.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
        ImageIcon resizedIconE = new ImageIcon(imgE);
        Image imgF = f.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
        ImageIcon resizedIconF = new ImageIcon(imgF);
        Image imgA = a.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
        ImageIcon resizedIconA = new ImageIcon(imgA);
        Image imgB = b.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
        ImageIcon resizedIconB = new ImageIcon(imgB);

        // 버튼 생성 및 이미지 설정
        JButton btn_c_sharp02 = new JButton();
        btn_c_sharp02.setIcon(resizedIconC);  // 버튼에 리사이즈된 이미지 아이콘 설정
        btn_c_sharp02.setBounds(25, 50, 50, 50);  // 버튼 위치 설정
        btn_c_sharp02.setContentAreaFilled(false);
        btn_c_sharp02.setBorderPainted(false);
        panel_notes_black.add(btn_c_sharp02);

        btn_c_sharp02.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("btn_c_sharp02 버튼클릭!"); // 버튼 번호 출력
                // 버튼 클릭 시 playSound 함수 실행
                playSound("c#2.wav");
            }
        });

        JButton btn_e_flat02 = new JButton();
        btn_e_flat02.setIcon(resizedIconE);
        btn_e_flat02.setBounds(95, 50, 50, 50);
        btn_e_flat02.setContentAreaFilled(false);
        btn_e_flat02.setBorderPainted(false);
        panel_notes_black.add(btn_e_flat02);

        btn_e_flat02.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("btn_e_flat02 버튼클릭!"); // 버튼 번호 출력
                // 버튼 클릭 시 playSound 함수 실행
                playSound("eb2.wav");
            }
        });

        JButton btn_f_sharp02 = new JButton();
        btn_f_sharp02.setIcon(resizedIconF);
        btn_f_sharp02.setBounds(210, 50, 50, 50);
        btn_f_sharp02.setContentAreaFilled(false);
        btn_f_sharp02.setBorderPainted(false);
        panel_notes_black.add(btn_f_sharp02);

        btn_f_sharp02.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("btn_f_sharp02 버튼클릭!"); // 버튼 번호 출력
                // 버튼 클릭 시 playSound 함수 실행
                playSound("f#2.wav");
            }
        });

        JButton btn_a_flat02 = new JButton();
        btn_a_flat02.setIcon(resizedIconA);
        btn_a_flat02.setBounds(275, 50, 50, 50);
        btn_a_flat02.setContentAreaFilled(false);
        btn_a_flat02.setBorderPainted(false);
        panel_notes_black.add(btn_a_flat02);

        btn_a_flat02.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("btn_a_flat02 버튼클릭!"); // 버튼 번호 출력
                // 버튼 클릭 시 playSound 함수 실행
                playSound("ab2.wav");
            }
        });

        JButton btn_b_flat02 = new JButton();
        btn_b_flat02.setIcon(resizedIconB);
        btn_b_flat02.setBounds(345, 50, 50, 50);
        btn_b_flat02.setContentAreaFilled(false);
        btn_b_flat02.setBorderPainted(false);
        panel_notes_black.add(btn_b_flat02);

        btn_b_flat02.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("btn_b_flat02 버튼클릭!"); // 버튼 번호 출력
                // 버튼 클릭 시 playSound 함수 실행
                playSound("bb2.wav");
            }
        });

        JButton btn_c_sharp03 = new JButton();
        btn_c_sharp03.setIcon(resizedIconC);
        btn_c_sharp03.setBounds(460, 50, 50, 50);
        btn_c_sharp03.setContentAreaFilled(false);
        btn_c_sharp03.setBorderPainted(false);
        panel_notes_black.add(btn_c_sharp03);

        btn_c_sharp03.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("btn_c_sharp03 버튼클릭!"); // 버튼 번호 출력
                // 버튼 클릭 시 playSound 함수 실행
                playSound("c#3.wav");
            }
        });

        JButton btn_e_flat03 = new JButton();
        btn_e_flat03.setIcon(resizedIconE);
        btn_e_flat03.setBounds(530, 50, 50, 50);
        btn_e_flat03.setContentAreaFilled(false);
        btn_e_flat03.setBorderPainted(false);
        panel_notes_black.add(btn_e_flat03);

        btn_e_flat03.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("btn_e_flat03 버튼클릭!"); // 버튼 번호 출력
                // 버튼 클릭 시 playSound 함수 실행
                playSound("eb3.wav");
            }
        });

        JButton btn_f_sharp03 = new JButton();
        btn_f_sharp03.setIcon(resizedIconF);
        btn_f_sharp03.setBounds(645, 50, 50, 50);
        btn_f_sharp03.setContentAreaFilled(false);
        btn_f_sharp03.setBorderPainted(false);
        panel_notes_black.add(btn_f_sharp03);

        btn_f_sharp03.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("btn_f_sharp03 버튼클릭!"); // 버튼 번호 출력
                // 버튼 클릭 시 playSound 함수 실행
                playSound("f#3.wav");
            }
        });

        JButton btn_a_flat03 = new JButton();
        btn_a_flat03.setIcon(resizedIconA);
        btn_a_flat03.setBounds(710, 50, 50, 50);
        btn_a_flat03.setContentAreaFilled(false);
        btn_a_flat03.setBorderPainted(false);
        panel_notes_black.add(btn_a_flat03);

        btn_a_flat03.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("btn_a_flat03 버튼클릭!"); // 버튼 번호 출력
                // 버튼 클릭 시 playSound 함수 실행
                playSound("ab3.wav");
            }
        });

        JButton btn_b_flat03 = new JButton();
        btn_b_flat03.setIcon(resizedIconB);
        btn_b_flat03.setBounds(780, 50, 50, 50);
        btn_b_flat03.setContentAreaFilled(false);
        btn_b_flat03.setBorderPainted(false);
        panel_notes_black.add(btn_b_flat03);

        btn_b_flat03.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("btn_b_flat03 버튼클릭!"); // 버튼 번호 출력
                // 버튼 클릭 시 playSound 함수 실행
                playSound("bb3.wav");
            }
        });


        // panel_keyboard (배경 이미지용 패널)
        JPanel panel_keyboard = new JPanel();
        panel_keyboard.setBounds(0, 50, 852, 343);
        contentPane.add(panel_keyboard);

        Image scaledImage = keyboard.getImage().getScaledInstance(852, 343,
                Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);

        JLabel label = new JLabel(scaledIcon);
        label.setBounds(0, 0, 852, 343);
        panel_keyboard.add(label);
        
        setVisible(true);
    }
    
    private boolean isRecording = false; // 녹음 상태 확인용

    private TargetDataLine targetLine;

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

                File outputFile = new File(outputDir, "record_piano.wav");

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
        
        File outputFile = new File("src/resources/lydfiler/audio/record_piano.wav");
        if (outputFile.exists()) {
            System.out.println("파일 저장 성공: " + outputFile.getAbsolutePath());
        } else {
            System.out.println("파일 저장 실패.");
        }
        
    }

    private void playRecordedSound() {
        File audioFile = new File("src/resources/lydfiler/audio/record_piano.wav");

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
            Thread.sleep(audioClip.getMicrosecondLength() / 1000);  // 재생이 끝날 때까지 대기
        } catch (Exception e) {
            e.printStackTrace();
        }

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
}
