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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MixingPage extends JFrame {
    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private String[] trackFiles = {
            "src/resources/lydfiler/audio/record_piano.wav",
            "src/resources/lydfiler/audio/record_acoustic.wav",
            "src/resources/lydfiler/audio/record_electric.wav",
            "src/resources/lydfiler/audio/record_vocal.wav"
    };

    private ArrayList<JPanel> trackPanels = new ArrayList<>();
    private int[] trackOffsets = {0, 0, 0, 0};

    private boolean isMetronomePlaying = false;
    private Clip metronomeClip;
    private JComboBox<String> metronomeSelector;

    private DataInputStream dis;
    private DataOutputStream dos;
    private Socket socket;

    public MixingPage(Socket socket, String userName) {
        this.socket = socket;
        setupNetwork(userName);

        setTitle("Mixing");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setSize(868, 393);
        setLocationRelativeTo(null);
        setResizable(false); // 사이즈 변형 방지

        contentPane = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(0, 0, new Color(82, 234, 145), 0, getHeight(), new Color(39, 174, 96));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);

        setupTopPanel();

        // 트랙 컨테이너
        JPanel tracksContainer = new JPanel();
        tracksContainer.setLayout(new BoxLayout(tracksContainer, BoxLayout.Y_AXIS));
        tracksContainer.setOpaque(false);

        String[] labels = {"piano", "acoustic", "electric", "vocal"};
        String[] icons = {"/img/piano.png", "/img/acousticGuitar.png", "/img/electricGuitar.png", "/img/mic.png"};

        for (int i = 0; i < 4; i++) {
            tracksContainer.add(createTrackRow(icons[i], labels[i], trackFiles[i], i));
        }

        contentPane.add(tracksContainer, BorderLayout.CENTER);
    }

    private JPanel createTrackRow(String iconPath, String text, String audioFile, int index) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 2));
        row.setOpaque(false); // 줄 전체 투명

        // 악기 아이콘 버튼
        JButton playButton = new JButton(text, updateImageSize(new ImageIcon(getClass().getResource(iconPath)), 40, 40));
        playButton.setForeground(Color.BLACK);
        playButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        playButton.setHorizontalTextPosition(SwingConstants.CENTER);
        playButton.setContentAreaFilled(false);
        playButton.setBorderPainted(false);
        playButton.setFocusPainted(false);
        playButton.addActionListener(e -> new Thread(() -> playAudio(audioFile)).start());

        // 배경
        JPanel trackPanel = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                // 부모의 그라데이션이 비치도록 투명 배경 위에 반투명 흰색 막대 색칠
                Graphics2D g2d = (Graphics2D) g;
                g2d.setColor(new Color(255, 255, 255, 60));
                g2d.fillRoundRect(0, 5, getWidth(), getHeight()-10, 10, 10);
            }
        };
        trackPanel.setOpaque(false); // 시스템 기본 회색 배경 사용 안함
        trackPanel.setPreferredSize(new Dimension(720, 55));
        trackPanels.add(trackPanel);

        int boxWidth = Math.max(50, getAudioLength(audioFile) * 15);
        JPanel smallTrackPanel = new JPanel();
        smallTrackPanel.setBackground(getColorForTrack(index));
        smallTrackPanel.setBounds(5, 10, boxWidth, 35);

        // 드래그 앤 드롭 로직
        smallTrackPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                // 현재 마우스 위치를 trackPanel 기준으로 변환
                Point p = SwingUtilities.convertPoint(smallTrackPanel, e.getPoint(), trackPanel);
                int newX = p.x - (smallTrackPanel.getWidth() / 2);
                int maxX = trackPanel.getWidth() - smallTrackPanel.getWidth();
                int finalX = Math.max(0, Math.min(newX, maxX));

                smallTrackPanel.setLocation(finalX, 10);
                trackOffsets[index] = finalX / 10;
                sendTrackOffsetToServer(index, trackOffsets[index]);

                // 이동 후 전체 화면 갱신
                trackPanel.repaint();
                contentPane.repaint();
            }
        });

        trackPanel.add(smallTrackPanel);
        row.add(playButton);
        row.add(trackPanel);
        return row;
    }

    // 상단 패널
    private void setupTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        JPanel leftGroup = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        leftGroup.setOpaque(false);
        JButton backBtn = createIconButton("/img/button_back.png", 25, 25);
        backBtn.addActionListener(e -> { stopMetronome(); new ChoosePage(); dispose(); });        JLabel titleLabel = new JLabel("Mixing");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.BLACK);
        leftGroup.add(backBtn);
        leftGroup.add(titleLabel);

        // 중앙 그룹 (Play All + Metronome)
        JPanel centerGroup = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        centerGroup.setOpaque(false);

        JButton playAllBtn = createIconButton("/img/button_play.png", 25, 25);
        playAllBtn.addActionListener(e -> playAllTracks());

        JButton metroBtn = createIconButton("/img/metronome.png", 25, 25);
        metronomeSelector = new JComboBox<>(new String[]{"none", "60bpm", "80bpm", "100bpm", "120bpm"});
        metronomeSelector.setVisible(false);

        // 메트로놈 버튼 클릭 시 드롭다운 토글
        metroBtn.addActionListener(e -> {
            metronomeSelector.setVisible(!metronomeSelector.isVisible());
            topPanel.revalidate();
            topPanel.repaint();
        });

        // 드롭다운 선택 시 로직
        metronomeSelector.addActionListener(e -> {
            String selectedBPM = (String) metronomeSelector.getSelectedItem();
            if (selectedBPM != null) {
                if (selectedBPM.equals("none")) {
                    stopMetronome();
                } else {
                    if (isMetronomePlaying) stopMetronome();
                    playMetronome(selectedBPM);
                }
                metronomeSelector.setVisible(false); // 선택 후 숨기기
            }
        });

        centerGroup.add(playAllBtn);
        centerGroup.add(metroBtn);
        centerGroup.add(metronomeSelector);

        JButton chatBtn = createIconButton("/img/send.png", 30, 35);
        chatBtn.addActionListener(e -> { stopMetronome(); new ChatClient().setVisible(true); dispose(); });

        topPanel.add(leftGroup, BorderLayout.WEST);
        topPanel.add(centerGroup, BorderLayout.CENTER);
        topPanel.add(chatBtn, BorderLayout.EAST);
        contentPane.add(topPanel, BorderLayout.NORTH);
    }

    // 유틸리티 메서드
    private void playAudio(String path) {
        try {
            Clip clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(new File(path)));
            clip.start();
        } catch (Exception e) {}
    }

    private void playAllTracks() {
        new Thread(() -> {
            try {
                int total = 0;
                for (int i = 0; i < 4; i++) total = Math.max(total, trackOffsets[i] + getAudioLength(trackFiles[i]));
                for (int t = 0; t <= total; t++) {
                    for (int i = 0; i < 4; i++) if (t == trackOffsets[i]) playAudio(trackFiles[i]);
                    Thread.sleep(1000);
                }
            } catch (Exception e) {}
        }).start();
    }

    private void playMetronome(String bpm) {
        try {
            // 파일 경로 확인 필요 (PianoPage와 동일한 경로)
            File f = new File("src/resources/lydfiler/audio/metronome/metronome_" + bpm + ".wav");
            if (!f.exists()) {
                System.out.println("메트로놈 파일을 찾을 수 없습니다: " + f.getAbsolutePath());
                return;
            }
            AudioInputStream ais = AudioSystem.getAudioInputStream(f);
            metronomeClip = AudioSystem.getClip();
            metronomeClip.open(ais);

            // 무한 반복 재생
            metronomeClip.loop(Clip.LOOP_CONTINUOUSLY);
            metronomeClip.start();
            isMetronomePlaying = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopMetronome() {
        if (metronomeClip != null && metronomeClip.isRunning()) {
            metronomeClip.stop();
            metronomeClip.close();
            isMetronomePlaying = false;
        }
    }

    private void setupNetwork(String name) {
        try {
            dos = new DataOutputStream(socket.getOutputStream());
            dis = new DataInputStream(socket.getInputStream());
            dos.writeUTF("/loginMixing " + name);
            new Thread(() -> {
                while (true) {
                    try {
                        String msg = dis.readUTF();
                        if (msg.startsWith("/trackOffset")) {
                            String[] p = msg.split(" ");
                            updateTrackOffsetFromServer(Integer.parseInt(p[1]), Integer.parseInt(p[2]));
                        }
                    } catch (Exception e) { break; }
                }
            }).start();
        } catch (Exception e) {}
    }

    private void updateTrackOffsetFromServer(int idx, int off) {
        trackOffsets[idx] = off;
        if (idx < trackPanels.size()) {
            JPanel tp = trackPanels.get(idx);
            JPanel box = (JPanel) tp.getComponent(0);
            box.setLocation(off * 10, 10);
            tp.repaint();
        }
    }

    private void sendTrackOffsetToServer(int idx, int off) {
        try { dos.writeUTF("/trackOffset " + idx + " " + off); } catch (Exception e) {}
    }

    private JButton createIconButton(String path, int w, int h) {
        JButton b = new JButton(updateImageSize(new ImageIcon(getClass().getResource(path)), w, h));
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        return b;
    }

    private ImageIcon updateImageSize(ImageIcon icon, int w, int h) {
        return new ImageIcon(icon.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH));
    }

    private int getAudioLength(String path) {
        try {
            AudioInputStream as = AudioSystem.getAudioInputStream(new File(path));
            return (int) (as.getFrameLength() / as.getFormat().getFrameRate());
        } catch (Exception e) { return 5; }
    }

    private Color getColorForTrack(int i) {
        Color[] colors = {new Color(251, 48, 156), new Color(48, 187, 251), new Color(106, 251, 48), new Color(248, 251, 48)};
        return colors[i];
    }
}