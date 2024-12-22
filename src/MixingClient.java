import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class MixingClient extends JFrame {
    private static String[] userNames = new String[2];  // 사용자 이름을 저장할 배열 (두 창에서 각각 이름을 입력 받음)
    private JPanel contentPane;
    private JTextField txtUserName;
    private JTextField txtIpAddress;
    private JTextField txtPortNumber;
    private DataOutputStream dos;
    private DataInputStream dis;
    private Socket socket;

    // 기본 생성자 추가
    public MixingClient() {
        this(0);  // 기본적으로 첫 번째 창으로 설정
    }

    // 생성자에 창 번호를 전달받아 각각 다른 배열 위치에 이름 저장
    public MixingClient(int clientIndex) {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 400);
        setLocationRelativeTo(null);
        contentPane = new JPanel();
        contentPane.setLayout(null);
        setContentPane(contentPane);

        JLabel lblUserName = new JLabel("User Name");
        lblUserName.setBounds(12, 39, 82, 33);
        contentPane.add(lblUserName);

        txtUserName = new JTextField();
        txtUserName.setBounds(101, 39, 160, 33);
        contentPane.add(txtUserName);
        txtUserName.setColumns(10);

        JLabel lblIpAddress = new JLabel("IP Address");
        lblIpAddress.setBounds(12, 100, 82, 33);
        contentPane.add(lblIpAddress);

        txtIpAddress = new JTextField("127.0.0.1");
        txtIpAddress.setBounds(101, 100, 160, 33);
        contentPane.add(txtIpAddress);

        JLabel lblPortNumber = new JLabel("Port Number");
        lblPortNumber.setBounds(12, 163, 82, 33);
        contentPane.add(lblPortNumber);

        txtPortNumber = new JTextField("30000");
        txtPortNumber.setBounds(101, 163, 160, 33);
        contentPane.add(txtPortNumber);

        JButton btnConnect = new JButton("Connect");
        btnConnect.setBounds(12, 223, 250, 38);
        contentPane.add(btnConnect);

        // 엔터 키로 전송
        txtUserName.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                connectToServer();  // Enter 키로 연결 시도
            }
        });

        // 버튼 클릭 시 해당 창의 사용자 이름을 배열에 저장
        btnConnect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                connectToServer();  // 배열의 인덱스에 맞게 사용자 이름 저장
            }
        });
    }
 
    private void connectToServer() {
        String userName = txtUserName.getText().trim();
        String ipAddress = txtIpAddress.getText().trim();
        String portNumber = txtPortNumber.getText().trim();

        // 사용자 이름을 배열에 저장 (배열에 두 개의 이름을 입력)
        if (userNames[0] == null) {
            userNames[0] = userName; // 첫 번째 창에 입력된 사용자 이름
        } else if (userNames[1] == null) {
            userNames[1] = userName; // 두 번째 창에 입력된 사용자 이름
        }

        // 소켓 연결 및 MixingPage로 이동
        if (!userName.isEmpty() && !ipAddress.isEmpty() && !portNumber.isEmpty()) {
            try {
                socket = new Socket(ipAddress, Integer.parseInt(portNumber));
                dos = new DataOutputStream(socket.getOutputStream());
                dis = new DataInputStream(socket.getInputStream());

                // 사용자 이름 전송
                dos.writeUTF("/loginMixing " + userName);

                // MixingPage로 이동
                setVisible(false);
                new MixingPage(socket, userName).setVisible(true);
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Connection failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please fill in all fields", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // userName 반환 메소드 (배열에서 userName을 가져옴)
    public static String getUserName(int index) {
        if (index >= 0 && index < userNames.length) {
            return userNames[index];
        }
        return null;
    }
}