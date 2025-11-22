import javax.swing.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

public class ClientMenuFrame extends JFrame {

    private String username, ip_addr, port_no;
    private Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;

    // 서버에게서 받은 최신 접속자 목록(/list 결과)
    private String[] currentUserList;

    private ClientFriendsMenuPanel friendsPanel;
    private ClientChatingMenuPanel chatPanel;

    public ClientMenuFrame(String username, String ip_addr, String port_no) {
        this.username = (username == null) ? "" : username.trim();
        this.ip_addr = ip_addr;
        this.port_no = port_no;

        setTitle("PocketTalk");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(380, 560);
        setLocationRelativeTo(null);
        setResizable(false);

        String profileImagePath = "/Images/defaultprofileimage.png";

        // 메인 패널들 먼저 생성
        friendsPanel = new ClientFriendsMenuPanel(this, this.username, ip_addr, port_no, profileImagePath);
        chatPanel    = new ClientChatingMenuPanel(this, this.username, ip_addr, port_no);

        // 처음 화면은 친구 목록 화면
        setContentPane(friendsPanel);
        setVisible(true);

        // ---- 서버 연결 ----
        try {
            socket = new Socket(ip_addr, Integer.parseInt(port_no));
            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());

            // ★ 수신 스레드 먼저 시작
            ListenNetwork net = new ListenNetwork();
            net.start();

            // ★ 그 다음 로그인 메시지 전송
            dos.writeUTF("/login " + this.username);
            dos.flush();

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
                    if (msg == null) continue;
                    msg = msg.trim();

                    if (msg.startsWith("/list ")) {
                        String userListString = msg.substring(6).trim();

                        String[] users;
                        if (userListString.isEmpty()) {
                            users = new String[0];
                        } else {
                            users = userListString.split(" ");
                        }

                        // 여기서도 한 번 정리해 주면 더 안전
                        for (int i = 0; i < users.length; i++) {
                            if (users[i] != null) users[i] = users[i].trim();
                        }

                        currentUserList = users;
                        List<String> usernames = Arrays.asList(currentUserList);

                        SwingUtilities.invokeLater(() -> {
                            if (friendsPanel != null) {
                                friendsPanel.updateFriendList(usernames);
                            }
                        });
                    }
                    else if (msg.startsWith("/profile ")) {

                        String body = msg.substring("/profile ".length());
                        String[] tokens = body.split(" ", 3);

                        if (tokens.length >= 2) {
                            String name      = tokens[0].trim();
                            String imagePath = tokens[1].trim();
                            String statusMsg = (tokens.length == 3) ? tokens[2].trim() : "";

                           // System.out.println("[클라 수신] /profile name=" + name +
                            //        " status=" + statusMsg);

                            SwingUtilities.invokeLater(() -> {
                                if (friendsPanel != null) {
                                    friendsPanel.updateFriendProfileFromServer(name, imagePath, statusMsg);
                                }
                            });
                        }
                    }
                    else {
                        // 나중에 채팅 패널에 연결
                    }

                } catch (IOException e) {
                    System.out.println("[클라 수신 스레드 종료] " + e.getMessage());
                    break;
                }
            }
        }
    }

     

    // ---- 화면 전환 ----
    public void showFriendsMenu() {
        setContentPane(friendsPanel);
        revalidate();
        repaint();
    }

    public void showChattingMenu() {
        setContentPane(chatPanel);
        revalidate();
        repaint();
    }

    // ---- getter ----
    public DataOutputStream getDataOutputStream() {
        return dos;
    }

    public List<String> getCurrentUserList() {
        if (currentUserList != null) {
            return Arrays.asList(currentUserList);
        }
        return null;
    }

    public String getUsername() {
        return username;
    }
}
