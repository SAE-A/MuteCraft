import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class MixingClient extends JFrame {
    private JPanel contentPane;
    private JTextField txtUserName;
    private JTextField txtIpAddress;
    private JTextField txtPortNumber;
    private DataOutputStream dos;
    private DataInputStream dis;
    private Socket socket;
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                MixingClient frame = new MixingClient();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    public MixingClient() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(254, 321);
        setLocationRelativeTo(null);
        contentPane = new JPanel();
        contentPane.setLayout(null);
        setContentPane(contentPane);
        
        JLabel lblUserName = new JLabel("Mixing");
        lblUserName.setBounds(12, 39, 82, 33);
        contentPane.add(lblUserName);
        
        txtUserName = new JTextField();
        txtUserName.setBounds(101, 39, 116, 33);
        contentPane.add(txtUserName);
        txtUserName.setColumns(10);
        
        JLabel lblIpAddress = new JLabel("IP Address");
        lblIpAddress.setBounds(12, 100, 82, 33);
        contentPane.add(lblIpAddress);
        
        txtIpAddress = new JTextField("127.0.0.1");
        txtIpAddress.setBounds(101, 100, 116, 33);
        contentPane.add(txtIpAddress);
        
        JLabel lblPortNumber = new JLabel("Port Number");
        lblPortNumber.setBounds(12, 163, 82, 33);
        contentPane.add(lblPortNumber);
        
        txtPortNumber = new JTextField("30000");
        txtPortNumber.setBounds(101, 163, 116, 33);
        contentPane.add(txtPortNumber);
        
        JButton btnConnect = new JButton("Connect");
        btnConnect.setBounds(12, 223, 205, 38);
        contentPane.add(btnConnect);
        
        btnConnect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                connectToServer();
            }
        });
    }
    
    private void connectToServer() {
        String userName = txtUserName.getText().trim();
        String ipAddress = txtIpAddress.getText().trim();
        String portNumber = txtPortNumber.getText().trim();
        
        if (!userName.isEmpty() && !ipAddress.isEmpty() && !portNumber.isEmpty()) {
            try {
                socket = new Socket(ipAddress, Integer.parseInt(portNumber));
                dos = new DataOutputStream(socket.getOutputStream());
                dis = new DataInputStream(socket.getInputStream());
                // 사용자 이름 전송
                dos.writeUTF("/loginMixing " + userName);
                // 믹싱 화면으로 전환
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
}