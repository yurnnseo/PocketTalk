//StartPocketTalkPanel에서 버튼 누르면 띄워지는 새로운 창의 프레임
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import javax.swing.*;

public class ClientMenuFrame extends JFrame {
    private String username, ip_addr, port_no;
    private Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;
    private String[] currentUserList = new String[0];
    
    public ClientMenuFrame(String username, String ip_addr, String port_no) {
        this.username = username;
        this.ip_addr = ip_addr;
        this.port_no = port_no;

        setTitle("PocketTalk");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(380, 560);
        setLocationRelativeTo(null);
        setResizable(false);

        // 처음엔 친구 목록 패널 표시
        setContentPane(new ClientFriendsMenuPanel(this, username, ip_addr, port_no));
        //setVisible(true);
        
        try {
            // (중요) 메인 프레임이 생성될 때 연결합니다.
            socket = new Socket(ip_addr, Integer.parseInt(port_no));
            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());

            ListenNetwork net = new ListenNetwork();
            net.start();
            
            dos.writeUTF("/login " + username); // 리스너 켜고 로그인

        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "서버 연결 실패!");
            System.exit(0);
        }
        
        setVisible(true);
    }

    class ListenNetwork extends Thread {
        public void run() {
            while (true) {
                try {
                    String msg = dis.readUTF();
                    
                    if (msg.startsWith("/userlist ")) {
                        // (중요) 서버가 보낸 목록을 멤버 변수에 저장
                        String[] users = msg.substring(10).split(",");
                        currentUserList = users;
                    }
                    // (여기에 나중에 /whisper, /groupchat 등 다른 메시지 처리 로직 추가)
                    
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(null, "서버 연결이 끊겼습니다.");
                    System.exit(0); // 연결 끊기면 종료
                    break;
                }
            }
        }
    }
    
    // 화면 전환용 메소드
    public void showFriendsMenu() {
        setContentPane(new ClientFriendsMenuPanel(this, username, ip_addr, port_no));
        revalidate();
        repaint();
    }

    public void showChattingMenu() {
        setContentPane(new ClientChatingMenuPanel(this, username, ip_addr, port_no));
        revalidate();
        repaint();
    }
    
    public String[] getCurrentUserList() {
        return currentUserList;
    }
    
    /** 현재 로그인한 내 사용자 이름을 반환합니다. */
    public String getUsername() {
        return username;
    }
    
    /** 서버로 메시지를 보내는 DataOutputStream을 반환합니다. */
    public DataOutputStream getDos() {
        return dos;
    }
}