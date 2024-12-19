import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

class WaveformPanel extends JPanel {
    private ArrayList<Double> audioData = new ArrayList<>();

    public void updateWaveform(byte[] audioBytes, int bytesRead) {
        // byte 배열을 double 값으로 변환하여 오디오 데이터에 추가
        for (int i = 0; i < bytesRead; i += 2) {
            // 16비트 오디오 데이터이므로 두 바이트를 하나의 double 값으로 변환
            int sample = (audioBytes[i] | (audioBytes[i + 1] << 8));
            audioData.add(sample / 32768.0); // -1.0 ~ 1.0 범위로 정규화
        }
        repaint(); // 패널을 다시 그려서 업데이트
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // 배경색을 하얀색으로 설정
        g.setColor(Color.white);
        g.fillRect(0, 0, getWidth(), getHeight());
        
        if (audioData.isEmpty()) return;

        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.BLACK); // 선 색상을 검정색으로 설정
        int width = getWidth();
        int height = getHeight();
        int midY = height / 2;

        // 파형 그리기
        for (int i = 1; i < audioData.size(); i++) {
            int x1 = (i - 1) * width / audioData.size();
            int y1 = midY - (int) (audioData.get(i - 1) * midY);
            int x2 = i * width / audioData.size();
            int y2 = midY - (int) (audioData.get(i) * midY);
            g2.drawLine(x1, y1, x2, y2);
        }
    }
}
