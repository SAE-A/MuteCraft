import java.awt.*;
import javax.sound.sampled.*;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionListener;
import java.io.File;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class PianoPage extends JFrame {

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
        leftPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        leftPanel.add(backbtn);
        leftPanel.add(leftLabel);

        // 중앙 패널
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 30, 10));
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

        stopbtn.setIcon(resizeIcon(button_stop, 25, 25));
        stopbtn.setContentAreaFilled(false);
        stopbtn.setBorderPainted(false);
        stopbtn.setFocusPainted(false);
        stopbtn.setPreferredSize(new Dimension(50, 50));

        recordbtn.setIcon(resizeIcon(button_record, 25, 25));
        recordbtn.setContentAreaFilled(false);
        recordbtn.setBorderPainted(false);
        recordbtn.setFocusPainted(false);
        recordbtn.setPreferredSize(new Dimension(50, 50));

        metronomebtn.setIcon(resizeIcon(metronome, 25, 25));
        metronomebtn.setContentAreaFilled(false);
        metronomebtn.setBorderPainted(false);
        metronomebtn.setFocusPainted(false);
        metronomebtn.setPreferredSize(new Dimension(50, 50));

        addbtn.setIcon(resizeIcon(button_add, 25, 25));
        addbtn.setContentAreaFilled(false);
        addbtn.setBorderPainted(false);
        addbtn.setFocusPainted(false);
        addbtn.setPreferredSize(new Dimension(50, 50));

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
        label.setBounds(0, 0, 852,
                343);
        panel_keyboard.add(label);
    }

    private void playSound(String soundFile) {
        try {
            // 상대 경로로 음원 파일을 찾는다
            File audioFile = new File("src/resources/lydfiler/audio/" + soundFile);  // 상대경로로 수정

            System.out.println("파일 경로: " + audioFile.getAbsolutePath());

            // 오디오 입력 스트림 생성
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);

            // Clip 객체 생성 (재생을 위한 객체)
            Clip audioClip = AudioSystem.getClip();

            // 오디오 스트림을 Clip 객체에 연결
            audioClip.open(audioStream);

            // 오디오 재생
            audioClip.start();

            // 음원이 끝날 때까지 대기
            Thread.sleep(audioClip.getMicrosecondLength() / 1000);  // 밀리초 단위로 대기

        } catch (UnsupportedAudioFileException e) {
            System.err.println("지원하지 않는 오디오 파일 형식입니다.");
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("파일을 읽는 중 문제가 발생했습니다.");
            e.printStackTrace();
        } catch (LineUnavailableException e) {
            System.err.println("라인이 사용할 수 없습니다.");
            e.printStackTrace();
        } catch (InterruptedException e) {
            System.err.println("재생 대기 중 인터럽트가 발생했습니다.");
            e.printStackTrace();
        }
    }
}
