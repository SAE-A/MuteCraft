import java.awt.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MixingPage extends JFrame {
    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private String audioFile1 = "src/resources/lydfiler/audio/record_piano.wav";
    private String audioFile2 = "src/resources/lydfiler/audio/record_acoustic.wav";
    private String audioFile3 = "src/resources/lydfiler/audio/record_electric.wav";
    private String audioFile4 = "src/resources/lydfiler/audio/record_vocal.wav";

    private ArrayList<JPanel> trackPanels = new ArrayList<>(); // 트랙의 색깔 상자를 저장할 리스트
    private boolean[] trackStates = {false, false, false}; // 각 트랙의 상태 (색깔 변경 여부)
    
    private boolean isMetronomePlaying = false; // 메트로놈 재생 상태
    private Clip metronomeClip; // 메트로놈 오디오 클립
    private JComboBox<String> metronomeSelector; // BPM 선택 드롭다운
    private JButton metronomebtn; // 메트로놈 버튼
    
    private int[] trackOffsets;
    private String[] trackFiles = {audioFile1, audioFile2, audioFile3, audioFile4};

    private DataInputStream dis; // 데이터 입력 스트림
    private DataOutputStream dos; // 데이터 출력 스트림
    private Socket socket;

    public MixingPage(Socket socket, String userName) {
        this.socket = socket;

        try {
            this.dos = new DataOutputStream(socket.getOutputStream());
            this.dis = new DataInputStream(socket.getInputStream());
            
            // 사용자 이름을 서버에 전송하는 부분
            dos.writeUTF("/loginMixing " + userName);
            
            // 메시지를 수신하는 스레드 시작
            startListeningForMessages();

        } catch (IOException e) {
            e.printStackTrace();
        }
        
        trackOffsets = new int[4]; // Four tracks initialized with offsets of 0
        for (int i = 0; i < trackOffsets.length; i++) {
            trackOffsets[i] = 0;
        }

        setTitle("Mixing");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(868, 393);
        setLocationRelativeTo(null);
        
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
            int[] maxDuration = {0}; // 배열로 최대 길이 감싸기
            for (int i = 0; i < trackOffsets.length; i++) {
                int trackEnd = trackOffsets[i] + getAudioLength(trackFiles[i]);
                maxDuration[0] = Math.max(maxDuration[0], trackEnd); // 최대 길이 업데이트
            }

            new Thread(() -> {
                try {
                    for (int currentTime = 0; currentTime <= maxDuration[0]; currentTime++) {
                        for (int i = 0; i < trackOffsets.length; i++) {
                            final int trackIndex = i; // `i`를 복사하여 `final` 변수로 사용
                            if (currentTime == trackOffsets[trackIndex]) {
                                int offset = currentTime - trackOffsets[trackIndex];
                                new Thread(() -> playAudioWithOffset(trackFiles[trackIndex], offset)).start();
                            }
                        }
                        Thread.sleep(400);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }).start();
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
        
        // 메트로놈 버튼
        JButton metronomebtn = new JButton("");
        ImageIcon metronome = new ImageIcon(getClass().getResource("/img/metronome.png"));
        metronomebtn.setIcon(updateImageSize(metronome, 25, 25));
        metronomebtn.setContentAreaFilled(false);
        metronomebtn.setBorderPainted(false);
        metronomebtn.setFocusPainted(false);
        metronomebtn.setPreferredSize(new Dimension(50, 50));
        centerTopPanel.add(metronomebtn);

        customizeButton(metronomebtn);
        metronomeSelector = new JComboBox<>(new String[]{"none", "60bpm", "80bpm", "100bpm", "120bpm"});
        customizeComboBox(metronomeSelector);
        metronomeSelector.setVisible(false); // 초기에는 숨김 상태
        centerTopPanel.add(metronomeSelector);

        // 메트로놈 버튼 리스너
        metronomebtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 드롭박스의 가시성을 토글
                boolean isVisible = metronomeSelector.isVisible();
                metronomeSelector.setVisible(!isVisible);
                centerTopPanel.revalidate(); // 패널 갱신
                centerTopPanel.repaint();   // 패널 다시 그리기
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

        // 왼쪽과 중앙 패널을 topPanel에 추가
        topPanel.add(leftTopPanel, BorderLayout.WEST);
        topPanel.add(centerTopPanel, BorderLayout.CENTER);

        // 채팅 버튼
        JButton chatButton = new JButton();
        ImageIcon chatIcon = new ImageIcon(getClass().getResource("/img/send.png"));
        chatButton.setIcon(updateImageSize(chatIcon, 30, 35)); // 크기 조정
        chatButton.setContentAreaFilled(false);
        chatButton.setBorderPainted(false);
        chatButton.setFocusPainted(false);
        chatButton.setPreferredSize(new Dimension(40, 40));
        chatButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Chat button clicked!");  // 디버깅
                ChatClient chatClient = new ChatClient(); // ChatClient 인스턴스 생성
                chatClient.setLocation(650, 200);
                chatClient.setVisible(true);  // ChatClient 창 띄우기
                dispose();
            }
        });
        topPanel.add(chatButton, BorderLayout.EAST); // 채팅 버튼을 topPanel의 오른쪽에 추가

        // 왼쪽에 세로로 정렬된 버튼 패널 생성
        JPanel leftPanel2 = new JPanel();
        leftPanel2.setLayout(new BoxLayout(leftPanel2, BoxLayout.Y_AXIS));
        leftPanel2.setBackground(Color.WHITE); // 배경색 흰색 설정
        contentPane.add(leftPanel2, BorderLayout.WEST);
        
        leftPanel2.add(createButtonWithRectangle("/img/piano.png", "piano", audioFile1, 0));
        leftPanel2.add(createButtonWithRectangle("/img/acousticGuitar.png", "acoustic", audioFile2, 1));
        leftPanel2.add(createButtonWithRectangle("/img/electricGuitar.png", "electric", audioFile3, 2));
        leftPanel2.add(createButtonWithRectangle("/img/mic.png", "vocal", audioFile4, 3));
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
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        
        ImageIcon buttonIcon = new ImageIcon(getClass().getResource(imagePath));
        Image img = buttonIcon.getImage();
        Image resizedImg = img.getScaledInstance(44, 44, Image.SCALE_SMOOTH);
        buttonIcon = new ImageIcon(resizedImg);

        JButton playButton = new JButton(buttonIcon);
        playButton.setContentAreaFilled(false);
        playButton.setBorderPainted(false);
        playButton.setFocusPainted(false);
        playButton.setText(text);
        playButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        playButton.setHorizontalTextPosition(SwingConstants.CENTER);
        playButton.addActionListener(e -> {
            new Thread(() -> playAudio(audioFile)).start(); // 클릭 시 오디오 재생
        });
        
        // 오디오 길이 가져오기
        int audioLength = getAudioLength(audioFile); // 초 단위
        int boxWidth = Math.max(50, audioLength * 20); // 최소 50px, 초당 20px 크기

        // 회색 상자 패널 생성
        JPanel trackPanel = new JPanel();
        trackPanel.setPreferredSize(new Dimension(750, 60));
        trackPanel.setBackground(Color.LIGHT_GRAY); // 기본 색깔
        trackPanels.add(trackPanel); // 색깔 상자를 trackPanels 리스트에 추가
        trackPanel.setLayout(null); // null 레이아웃으로 수동 위치 지정

        // 작은 색깔 박스 생성
        JPanel smallTrackPanel = new JPanel();
        smallTrackPanel.setPreferredSize(new Dimension(boxWidth, 50)); // 폭은 오디오 길이에 비례
        smallTrackPanel.setBackground(getColorForTrack(index)); // 색상 설정

        // 작은 색깔 박스의 Y 위치를 중앙에 설정
        int yPosition = (trackPanel.getPreferredSize().height - smallTrackPanel.getPreferredSize().height) / 2;
        smallTrackPanel.setBounds(5, yPosition, smallTrackPanel.getPreferredSize().width, smallTrackPanel.getPreferredSize().height);
        trackPanel.add(smallTrackPanel);

        // 작은 색깔 박스를 trackPanel에 추가
        smallTrackPanel.setBounds(5, yPosition, smallTrackPanel.getPreferredSize().width, smallTrackPanel.getPreferredSize().height);
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
                int offset = newStart / 10; // 10px 당 1초로 간주 (조정 가능)
                trackOffsets[index] = offset; // 오프셋 저장
                smallTrackPanel.setBounds(newStart, yPosition, smallTrackPanel.getWidth(), smallTrackPanel.getHeight());
                
                // 서버에 트랙 오프셋 변경 정보 전송
                sendTrackOffsetToServer(index, offset);
            }
        });
        trackPanel.add(smallTrackPanel);
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

            // 오프셋 적용
            long totalLengthMicroseconds = clip.getMicrosecondLength();
            long startPosition = offset * 1_000_000L;

            if (startPosition < totalLengthMicroseconds) {
                clip.setMicrosecondPosition(startPosition); // 오프셋에서 시작
                clip.start();
            }

            long remainingTimeMilliseconds = (totalLengthMicroseconds - startPosition) / 1000;
            if (remainingTimeMilliseconds > 0) {
                Thread.sleep(remainingTimeMilliseconds); // 남은 재생 시간 대기
            }

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
            case 3: return new Color(248, 251, 48); // 보컬 색상
            default: return Color.LIGHT_GRAY; // 기본 색상
        }
    }

    private void toggleTrackColor(int index) {
        if (trackStates[index]) {
            trackPanels.get(index).setBackground(Color.LIGHT_GRAY); // 기본 색깔
        } else {
            trackPanels.get(index).setBackground(Color.LIGHT_GRAY); // 활성화된 색깔
        }
        trackStates[index] = !trackStates[index];
    }

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
    
    private void toggleMetronome() {
        if (isMetronomePlaying) {
            stopMetronome(); // 재생 중이면 중지
        } else {
            String selectedBPM = (String) metronomeSelector.getSelectedItem(); // 드롭박스에서 선택된 BPM 가져오기

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
    
    // 트랙 오프셋 정보를 서버에 전송하는 메서드
    private void sendTrackOffsetToServer(int index, int offset) {
        // 메시지 형식: /trackOffset <트랙 인덱스> <오프셋>
        String message = "/trackOffset " + index + " " + offset;
        // 서버에 메시지 전송 (여기서는 socketOutputStream 변수를 사용해야 합니다)
        try {
            dos.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 서버로부터 트랙 오프셋 변경 메시지를 받는 메서드를 추가 (예: 스레드에서 호출)
    private void receiveTrackOffsetFromServer(String message) {
        // 메시지 형식이 올바른지 확인
        if (message.startsWith("/trackOffset ")) {
            String[] parts = message.split(" ");
            if (parts.length == 3) {
                try {
                    int trackIndex = Integer.parseInt(parts[1]);
                    int offset = Integer.parseInt(parts[2]);
                    updateTrackOffsetFromServer(trackIndex, offset);
                } catch (NumberFormatException e) {
                    System.err.println("Invalid number format: " + e.getMessage());
                }
            }
        } else {
            // /trackOffset 메시지가 아닐 경우 처리
            System.out.println("Received message: " + message);
        }
    }

    private void updateTrackOffsetFromServer(int index, int offset) {
        // 오프셋을 업데이트
        trackOffsets[index] = offset;

        // 작은 색깔 박스만 이동시키기
        if (index < trackPanels.size()) {
            JPanel trackPanel = trackPanels.get(index); // 회색 박스
            Component[] components = trackPanel.getComponents();

            // 작은 색깔 박스가 존재하는지 확인
            if (components.length > 0 && components[0] instanceof JPanel) {
                JPanel smallTrackPanel = (JPanel) components[0]; // 작은 색깔 박스
                int newX = offset * 10; // 10px 당 1초로 간주
                int yPosition = (trackPanel.getPreferredSize().height - smallTrackPanel.getPreferredSize().height) / 2;

                // 작은 색깔 박스 위치만 변경
                smallTrackPanel.setBounds(newX, yPosition, smallTrackPanel.getWidth(), smallTrackPanel.getHeight());
                trackPanel.revalidate(); // 레이아웃 갱신
                trackPanel.repaint(); // 화면 다시 그리기
            }
        }
    }
    
    private void startListeningForMessages() {
        new Thread(() -> {
            while (true) {
                try {
                    String message = dis.readUTF(); // 서버로부터 메시지 수신
                    receiveTrackOffsetFromServer(message);
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }).start();
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                Socket socket = new Socket("localhost", 12345);
                MixingPage mixingPage = new MixingPage(socket, "UserName"); // 사용자 이름도 전달
                mixingPage.setVisible(true); // MixingPage 창 띄우기
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
