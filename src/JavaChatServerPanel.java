import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class JavaChatServerPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private JTextArea textArea;
    private JTextField txtPortNumber;
    private JButton btnServerStart;

    private ServerSocket socket;
    private Socket client_socket;
    private Vector<UserService> UserVec = new Vector<>();

    private static final int BUF_LEN = 128;

    // ---- 프로필 TXT 관리 ----
    private Map<String, ClientProfile> clientProfiles = Collections.synchronizedMap(new HashMap<>());

    private static final String CLIENT_TXT_FILE = "./client_profiles.txt";

    public JavaChatServerPanel() {
        setLayout(null);
        setBorder(new EmptyBorder(5, 5, 5, 5));

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(12, 10, 460, 450);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Server Log"));
        add(scrollPane);

        textArea = new JTextArea();
        textArea.setEditable(false);
        scrollPane.setViewportView(textArea);

        loadProfilesFromTxt(); // loadProfilesFromTxt() 호출 -> .txt 파일 읽어서 이름 | 상태메시지 | 이미지경로를 clientProfiles 맵에 저장

        JLabel lblPort = new JLabel("Port Number");
        lblPort.setBounds(17, 466, 90, 26);
        add(lblPort);

        txtPortNumber = new JTextField();
        txtPortNumber.setHorizontalAlignment(SwingConstants.CENTER);
        txtPortNumber.setText("30000");
        txtPortNumber.setBounds(111, 467, 359, 26);
        add(txtPortNumber);
        txtPortNumber.setColumns(10);

        btnServerStart = new JButton("Server Start");
        btnServerStart.setBounds(17, 510, 450, 35);
        add(btnServerStart);

        // “Server Start” 버튼 누르면 startServer() 호출
        btnServerStart.addActionListener(e -> startServer());
    }

    // ServerSocket 열고 AcceptServer 스레드 시작
    private void startServer() {
        try {
            int port = Integer.parseInt(txtPortNumber.getText().trim());
            socket = new ServerSocket(port);
        } catch (NumberFormatException | IOException e1) {
            e1.printStackTrace();
        }

        AppendText("Chat Server Running on port " + txtPortNumber.getText());
        btnServerStart.setText("Chat Server Running...");
        btnServerStart.setEnabled(false);
        txtPortNumber.setEnabled(false);

        AcceptServer accept_server = new AcceptServer();
        accept_server.start();
    }

    
    public void AppendText(String str) {
        SwingUtilities.invokeLater(() -> {
            textArea.append(str + "\n");
            textArea.setCaretPosition(textArea.getText().length());
        });
    }

    // 클라이언트 접속하면 클라이언트를 UserVec에 넣고 클라이언트 한 명당 스레드 생성
    class AcceptServer extends Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    AppendText("Waiting clients ...");
                    client_socket = socket.accept();
                    AppendText("새로운 참가자 from " + client_socket);

                    UserService new_user = new UserService(client_socket);
                    UserVec.add(new_user);
                    AppendText("사용자 입장. 현재 참가자 수: " + UserVec.size());

                    new_user.start();
                } catch (IOException e) {
                    AppendText("!!!! accept 에러 발생... !!!!");
                    break;
                }
            }
        }
    }

    // ---- TXT 로드 / 저장 ----
    private void loadProfilesFromTxt() {
        File file = new File(CLIENT_TXT_FILE);
        if (!file.exists()) {
            AppendText("프로필 TXT 없음. 새로 시작합니다.");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            int count = 0;

            while ((line = br.readLine()) != null) {
                String[] tokens = line.split("\\|", 3);
                if (tokens.length == 3) {
                    String name   = tokens[0].trim();
                    String status = tokens[1].trim();
                    String img    = tokens[2].trim();

                    clientProfiles.put(name, new ClientProfile(name, status, img));
                    count++;
                }
            }
            AppendText("TXT 프로필 로드 완료: " + count + "명");
        } catch (Exception e) {
            AppendText("TXT 로드 오류: " + e.getMessage());
        }
    }



    public void saveProfilesToTxt() {
        try {
            File f = new File(CLIENT_TXT_FILE);
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(f))) {
                synchronized (clientProfiles) {
                    AppendText("===== TXT로 저장할 프로필 목록 =====");
                    for (ClientProfile p : clientProfiles.values()) {
                        String line = p.getName() + "|" + p.getStatusMessage() + "|" + p.getProfileImagePath();
                        bw.write(line);
                        bw.newLine();
                        AppendText("저장할 프로필 -> " + line);
                    }
                    AppendText("=================================");
                }
            }
            AppendText("TXT 프로필 저장 완료: " + clientProfiles.size() + "명");
        } catch (Exception e) {
            AppendText("TXT 저장 오류: " + e.getMessage());
        }
    }

    public void shutdownServer() {
        synchronized (UserVec) {
            for (UserService u : UserVec) {
                try {
                    u.WriteOne("/server_shutdown");
                } catch (Exception ignore) {}
            }
        }

        try {
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) {
            AppendText("서버 소켓 종료 중 오류: " + e.getMessage());
        }

        synchronized (UserVec) {
            for (UserService u : UserVec) {
                u.closeConnection();
            }
            UserVec.clear();
        }

        saveProfilesToTxt();
        AppendText("서버 및 클라이언트 연결 정리 완료.");
    }

    // ====== 각 유저 스레드 ======
    class UserService extends Thread {
        private DataInputStream dis;
        private DataOutputStream dos;
        private Socket client_socket;
        private Vector<UserService> user_vc;
        private String UserName = "";
        private ClientProfile clientProfile;

        public UserService(Socket client_socket) {
            this.client_socket = client_socket;
            this.user_vc = UserVec;

            try {
                dis = new DataInputStream(client_socket.getInputStream());
                dos = new DataOutputStream(client_socket.getOutputStream());
            } catch (Exception e) {
                AppendText("UserService 생성 중 error: " + e.getMessage());
            }
        }

        public void WriteOne(String msg) {
            try {
                dos.writeUTF(msg);
            } catch (IOException e) {
                AppendText("dos.writeUTF() error: 클라이언트 연결 끊김");
                closeConnection();
                UserVec.removeElement(this);
                AppendText("사용자 퇴장. 현재 참가자 수: " + UserVec.size());
            }
        }

        public void WriteAll(String str) {
            for (UserService user : user_vc) {
                user.WriteOne(str);
            }
        }

        private void applyProfileChange(String newName, String newImagePath, String newStatus) {
            synchronized (clientProfiles) {
                ClientProfile p = clientProfiles.get(UserName);

                if (p == null) {
                    p = new ClientProfile(newName, newStatus, newImagePath);
                } else {
                    if (newImagePath != null && !newImagePath.isEmpty())
                        p.setProfileImagePath(newImagePath);
                    p.setStatusMessage(newStatus);
                    p.setName(newName);
                }

                if (!newName.equals(UserName)) {
                    clientProfiles.remove(UserName);
                    clientProfiles.put(newName, p);
                    AppendText("[프로필 이름 변경] " + UserName + " → " + newName);
                    UserName = newName;
                } else {
                    clientProfiles.put(UserName, p);
                }

                clientProfile = p;
            }

            AppendText("[프로필 수정됨] " + UserName +
                    " / 상태: " + clientProfile.getStatusMessage() +
                    " / 이미지: " + clientProfile.getProfileImagePath());

            saveProfilesToTxt();

	         // 이 사람만이 아니라, 전체 프로필을 통째로 다시 뿌린다
	         broadcastAllProfilesToAllClients();
	
	         // 접속자 목록도 다시 전송 (이름 바뀌었을 수도 있으니까)
	         BroadcastUserList();

        }

        public void BroadcastUserList() {
            StringBuilder sb = new StringBuilder("/list ");

            synchronized (user_vc) {
                for (UserService u : user_vc) {
                    sb.append(u.UserName).append(" ");
                }
            }

            String userListMsg = sb.toString().trim();
            AppendText("현재 접속자 목록 전송: " + userListMsg);
            WriteAll(userListMsg);
        }

     // 이 유저에게 서버가 알고 있는 모든 프로필 정보 보내기
        private void sendAllProfilesToThisUser() {
            synchronized (clientProfiles) {
                for (ClientProfile p : clientProfiles.values()) {
                    String msg = "/profile " + p.getName().trim() + " " +
                            p.getProfileImagePath().trim() + " " +
                            p.getStatusMessage().trim();
                    AppendText("[개별전송 프로필] to " + UserName + " : " + msg);
                    WriteOne(msg);
                }
            }
        }

        // 한 명 변경 후 전체에게 변경분만 보내는 함수 쓴다면 여기에도 trim
        private void broadcastProfileUpdate() {
            if (clientProfile == null) return;
            String msg = "/profile " + clientProfile.getName().trim() + " " +
                    clientProfile.getProfileImagePath().trim() + " " +
                    clientProfile.getStatusMessage().trim();
            AppendText("[브로드캐스트 프로필] " + msg);
            WriteAll(msg);
        }
        
     // 전체에게 모든 프로필 뿌리기
     // ★ 서버가 알고 있는 모든 프로필을 "모든 클라이언트"에게 뿌려주는 함수
        private void broadcastAllProfilesToAllClients() {
            synchronized (clientProfiles) {
                for (ClientProfile p : clientProfiles.values()) {
                    String msg = "/profile " + p.getName().trim() + " " +
                            p.getProfileImagePath().trim() + " " +
                            p.getStatusMessage().trim();
                    AppendText("[브로드캐스트 전체 프로필] " + msg);
                    WriteAll(msg);   // ★ 모든 클라이언트에게 전송
                }
            }
        }



        public void closeConnection() {
            try {
                if (dos != null) dos.close();
                if (dis != null) dis.close();
                if (client_socket != null && !client_socket.isClosed())
                    client_socket.close();
            } catch (IOException e) { }
        }

        @Override
        public void run() {
            while (true) {
                try {
                    // 첫 메시지: "/login username"
                    String line1 = dis.readUTF();
                    String[] msg = line1.split(" ", 2);

                    if (msg.length < 2) {
                        AppendText("[경고] 잘못된 로그인 메시지: " + line1);
                        continue;
                    }

                    UserName = msg[1].trim();

                    synchronized (clientProfiles) {
                        clientProfile = clientProfiles.get(UserName);
                        if (clientProfile == null) {
                            clientProfile = new ClientProfile(UserName, "",
                                    "/Images/defaultprofileimage.png");
                            clientProfiles.put(UserName, clientProfile);
                            AppendText("새 프로필 생성: " + UserName);
                        } else {
                            AppendText("기존 프로필 로드: " + UserName);
                        }
                    }

                    saveProfilesToTxt();

                    AppendText("새로운 참가자 " + UserName + " 입장. " + "상태: " + clientProfile.getStatusMessage() + ", 이미지: " + clientProfile.getProfileImagePath());

                    //WriteOne("Welcome to Java chat server\n");
                   // WriteOne(UserName + "님 환영합니다.");

                    broadcastAllProfilesToAllClients(); // 모든 프로필을 전체 클라에게 /profile 로 뿌림
                    BroadcastUserList(); // 현재 온라인 유저들 이름 /list 로 뿌림


                    // 이후부터는 채팅/프로필 업데이트 루프
                    while (true) {
                        String chat_msg = dis.readUTF().trim();

                        if (chat_msg.startsWith("/profile_update ")) {
                            String body = chat_msg.substring("/profile_update ".length());
                            String[] tokens = body.split(" ", 3);

                            if (tokens.length >= 2) {
                                String newName = tokens[0].trim();
                                String newImagePath = tokens[1].trim();
                                String newStatus = (tokens.length == 3) ? tokens[2] : "";
                                applyProfileChange(newName, newImagePath, newStatus);
                            }
                            continue;
                        }

                        //AppendText("[MSG] " + UserName + " : " + chat_msg);
                        WriteAll(UserName + " : " + chat_msg+"\n");
                    }

                } catch (IOException e) {
                    AppendText("dis.readUTF() error: " + e.getMessage());
                    closeConnection();
                    UserVec.removeElement(this);
                    AppendText("사용자 퇴장. 남은 참가자 수: " + UserVec.size());
                    BroadcastUserList();
                    break;
                }
            }
        }
    }
}
