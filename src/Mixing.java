import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.util.ArrayList;

public class Mixing extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;

    // 오디오 파일 경로
    private String audioFile1 = "src/resources/lydfiler/audio/record_piano.wav";
    private String audioFile2 = "src/resources/lydfiler/audio/record_acoustic.wav";
    private String audioFile3 = "src/resources/lydfiler/audio/record_electric.wav";

    // 각 이미지 레이블을 저장할 리스트
    private ArrayList<JLabel> imageLabels = new ArrayList<>();
    // 각 버튼의 이미지 상태를 추적할 배열 (true -> clickedSoundWave.png, false -> soundWave.png)
    private boolean[] imageStates = {false, false, false};

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
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));  // 왼쪽 정렬
        contentPane.add(topPanel, BorderLayout.NORTH);
        
        // 뒤로가기 버튼
        JButton backbtn = new JButton();
        ImageIcon button_back = new ImageIcon(getClass().getResource("/img/button_back.png"));
        backbtn.setIcon(updateImageSize(button_back, 25, 25));
        backbtn.setContentAreaFilled(false);
        backbtn.setBorderPainted(false);
        backbtn.setFocusPainted(false);
        backbtn.setPreferredSize(new Dimension(40, 40));
        backbtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ChoosePage();
                setVisible(false);
                dispose();
            }
        });
        
        // "Mixing" 레이블 생성
        JLabel leftLabel = new JLabel(" Mixing");
        leftLabel.setFont(new Font("Serif", Font.PLAIN, 20));

        // 뒤로가기 버튼을 먼저 추가하고, 그 다음에 "Mixing" 레이블을 추가
        topPanel.add(backbtn);  // 뒤로가기 버튼을 왼쪽에 추가
        topPanel.add(leftLabel);  // "Mixing" 레이블을 뒤로가기 버튼 뒤에 추가

        // "Play All" 버튼을 가운데 배치하기 위한 별도 패널 생성
        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 260, 0));  // 가운데 정렬
        JButton playAllButton = new JButton("Play All");
        playAllButton.addActionListener(e -> {
            // 각 오디오 파일을 별도 스레드에서 재생
            new Thread(() -> playAudio(audioFile1)).start();
            new Thread(() -> playAudio(audioFile2)).start();
            new Thread(() -> playAudio(audioFile3)).start();

            // "Play All" 버튼을 눌렀을 때 모든 이미지를 토글
            toggleAllImages();  // 세 개의 이미지 상태 토글
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

        // 상단에 간격 추가
        leftPanel2.add(Box.createVerticalStrut(5));  // 위쪽 여백 추가
        
        // 첫 번째 오디오 재생 버튼과 사각형
        leftPanel2.add(createButtonWithRectangle("/img/piano.png", "piano", audioFile1, 0));

        // 두 번째 오디오 재생 버튼과 사각형
        leftPanel2.add(createButtonWithRectangle("/img/acousticGuitar.png", "acoustic", audioFile2, 1));

        // 세 번째 오디오 재생 버튼과 사각형
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

            // 클립이 끝날 때까지 대기
            Thread.sleep(clip.getMicrosecondLength() / 1000);

            clip.close(); // 리소스 해제
            System.out.println("재생 완료: " + filePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JPanel createButtonWithRectangle(String imagePath, String text, String audioFile, int index) {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 0));

        // 이미지 아이콘 로드 (버튼용 이미지)
        ImageIcon buttonIcon = new ImageIcon(getClass().getResource(imagePath));
        Image img = buttonIcon.getImage();
        Image resizedImg = img.getScaledInstance(70, 70, Image.SCALE_SMOOTH);  // 크기 조정
        buttonIcon = new ImageIcon(resizedImg);

        // 오디오 재생 버튼
        JButton playButton = new JButton(buttonIcon);
        playButton.setContentAreaFilled(false);
        playButton.setBorderPainted(false);
        playButton.setFocusPainted(false);
        playButton.setText(text);
        playButton.setVerticalTextPosition(SwingConstants.BOTTOM); // 텍스트를 버튼의 아래쪽에 위치시키기
        playButton.setHorizontalTextPosition(SwingConstants.CENTER); // 텍스트를 버튼의 가운데에 위치시키기
        playButton.addActionListener(e -> {
            // 오디오 파일 재생
            playAudio(audioFile);
            // 이미지 변경
            toggleImage(index);  // 이미지 토글
            System.out.println("click!");
        });

        // 이미지 아이콘 로드 (사각형 위에 표시할 이미지)
        ImageIcon imageIcon = new ImageIcon(getClass().getResource("/img/soundWave.png"));
        Image image = imageIcon.getImage();
        Image resizedImage = image.getScaledInstance(700, 50, Image.SCALE_SMOOTH);  // 크기 조정
        imageIcon = new ImageIcon(resizedImage);

        // 이미지 표시용 JLabel 생성
        JLabel imageLabel = new JLabel(imageIcon);
        imageLabels.add(imageLabel);  // 해당 이미지 레이블을 리스트에 저장

        // 패널에 버튼과 이미지 추가
        panel.add(playButton);
        panel.add(imageLabel);

        return panel;
    }

    // 이미지를 토글하는 메서드
    private void toggleImage(int index) {
        // 이미지 상태에 따라 토글
        if (imageStates[index]) {
            // 원본 이미지로 변경
            changeImage(index, "/img/soundWave.png");
        } else {
            // soundWave.png로 변경
            changeImage(index, "/img/clickedSoundWave.png");
        }
        // 상태 반전
        imageStates[index] = !imageStates[index];
    }

    // 이미지를 변경하는 메서드
    private void changeImage(int index, String imagePath) {
        ImageIcon newIcon = new ImageIcon(getClass().getResource(imagePath));
        Image image = newIcon.getImage();
        Image resizedImage = image.getScaledInstance(700, 50, Image.SCALE_SMOOTH);  // 크기 조정
        newIcon = new ImageIcon(resizedImage);
        imageLabels.get(index).setIcon(newIcon);  // 해당 인덱스의 JLabel에 새 아이콘 설정
    }

    // "Play All" 버튼을 눌렀을 때 세 개의 이미지 상태 토글
    private void toggleAllImages() {
        for (int i = 0; i < imageStates.length; i++) {
            if (imageStates[i]) {
                // 원본 이미지로 변경
                changeImage(i, "/img/soundWave.png");
            } else {
                // soundWave.png로 변경
                changeImage(i, "/img/clickedSoundWave.png");
            }
            // 상태 반전
            imageStates[i] = !imageStates[i];
        }
    }
    
    // 이미지 크기 조정
    private ImageIcon updateImageSize(ImageIcon icon, int width, int height) {
        Image image = icon.getImage();
        Image updatedImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(updatedImage);
    }
}
