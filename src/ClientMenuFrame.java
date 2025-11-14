//StartPocketTalkPanel에서 버튼 누르면 띄워지는 새로운 창의 프레임
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

import javax.swing.*;

public class ClientMenuFrame extends JFrame {
    private String username, ip_addr, port_no;
    private Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;
    private String[] currentUserList; //서버에게서 받은 최신 목록
    private ClientFriendsMenuPanel friendsPanel; 
    private ClientChatingMenuPanel chatPanel;
    
    public ClientMenuFrame(String username, String ip_addr, String port_no) {
        this.username = username;
        this.ip_addr = ip_addr;
        this.port_no = port_no;

        setTitle("PocketTalk");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(380, 560);
        setLocationRelativeTo(null);
        setResizable(false);

        //패널 객체들을 먼저 생성해서 저장
        friendsPanel = new ClientFriendsMenuPanel(this, username, ip_addr, port_no);
        chatPanel = new ClientChatingMenuPanel(this, username, ip_addr, port_no);
        //처음에는 친구 패널
        setContentPane(friendsPanel);
        
        setVisible(true);
        
        try {
            //메인 프레임이 생성될 때 연결
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
    }
    
    // 이 프레임이 직접 리스너를 가짐
    class ListenNetwork extends Thread {
        public void run() {
            while (true) {
                try {
                    String msg = dis.readUTF();
                    
                    if (msg.startsWith("/list ")) { 
                    	// "/list" 6글자 제외
                    	String userListString = msg.substring(6).trim();
                    	
                    	String[] users;
                        if (userListString.isEmpty()) {
                            users = new String[0]; // 빈 목록
                        } else {
                            //공백으로 분리
                            users = userListString.split(" "); 
                        }
                        //멤버 변수에 저장
                        currentUserList = users;
                        //형식 변환
                        List<String> usernames = Arrays.asList(currentUserList);
                        //Swing UI업데이트(람다 표현식 사용)
                        SwingUtilities.invokeLater(() -> {
                            //ClientFriendsMenuPanel에 메서드 있음.
                            if (friendsPanel != null) {
                                friendsPanel.updateFriendList(usernames); 
                            }
                        });
                        
                    }
                } catch (IOException e) {
                    break;
                }
            }
        }
    }

    // 화면 전환용 메소드
    public void showFriendsMenu() {
        //setContentPane(new ClientFriendsMenuPanel(this, username, ip_addr, port_no));
    	setContentPane(friendsPanel);
        revalidate();
        repaint();
    }

    public void showChattingMenu() {
        //setContentPane(new ClientChatingMenuPanel(this, username, ip_addr, port_no));
    	setContentPane(chatPanel);
        revalidate();
        repaint();
    }
}