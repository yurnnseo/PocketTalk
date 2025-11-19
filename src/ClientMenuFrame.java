
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

        String profileImagePath = "/Images/defaultprofileimage.png";
        
        //패널 객체들을 먼저 생성해서 저장
        friendsPanel = new ClientFriendsMenuPanel(this, username, ip_addr, port_no, profileImagePath);
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
    
    class ListenNetwork extends Thread {
        public void run() {
            while (true) {
                try {
                    String msg = dis.readUTF();
                    msg = msg.trim();

                    // 1) 접속자 목록
                    if (msg.startsWith("/list ")) {
                        String userListString = msg.substring(6).trim();

                        String[] users;
                        if (userListString.isEmpty()) {
                            users = new String[0];
                        } else {
                            users = userListString.split(" ");
                        }

                        currentUserList = users;
                        java.util.List<String> usernames = Arrays.asList(currentUserList);

                        SwingUtilities.invokeLater(() -> {
                            if (friendsPanel != null) {
                                friendsPanel.updateFriendList(usernames);
                            }
                        });
                    }

                    // 2) 프로필 정보 (/profile 이름 이미지경로 상태메시지...)
                    else if (msg.startsWith("/profile ")) {
                        // "/profile " 길이만큼 잘라서 뒤를 파싱
                        String body = msg.substring("/profile ".length());

                        // 상태메시지는 공백이 포함될 수 있으므로 4개로 split
                        // tokens[0] = "/profile" 이 아니라 body 를 split하니까:
                        // body = "name imagePath status message ..."
                        String[] tokens = body.split(" ", 3);
                        if (tokens.length >= 2) {
                            String name      = tokens[0];
                            String imagePath = tokens[1];
                            String statusMsg = (tokens.length == 3) ? tokens[2] : "";

                            SwingUtilities.invokeLater(() -> {
                                if (friendsPanel != null) {
                                    friendsPanel.updateFriendProfileFromServer(name, imagePath, statusMsg);
                                }
                            });
                        }
                    }

                    // 3) 그 외(일반 채팅 등)는 아직 안 써놨지만, 나중에 여기서 분기
                    else {
                        // 예: 일반 채팅 메시지 처리
                        // chatPanel.appendMessage(msg); 이런 식으로...
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
    
    public DataOutputStream getDataOutputStream() {
        return dos;
    }
    public List<String> getCurrentUserList() {
        if (currentUserList != null) {
            // 배열을 List로 변환하여 반환
            return Arrays.asList(currentUserList); 
        }
        return null;
    }

    public String getUsername() {
        return username;
    }
}