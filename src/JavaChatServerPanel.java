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

        loadProfilesFromTxt(); // .txt에서 프로필 로드

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

    // ---- 공통 로그 함수 (한국어 카테고리, 시간 없음) ----
    private void log(String category, String msg) {
        AppendText("[" + category + "]  " + msg);
    }

    public void AppendText(String str) {
        SwingUtilities.invokeLater(() -> {
            textArea.append(str + "\n");
            textArea.setCaretPosition(textArea.getText().length());
        });
    }

    // ServerSocket 열고 AcceptServer 스레드 시작
    private void startServer() {
        try {
            int port = Integer.parseInt(txtPortNumber.getText().trim());
            socket = new ServerSocket(port);
        } catch (NumberFormatException | IOException e1) {
            e1.printStackTrace();
            log("에러", "서버 시작 실패: " + e1.getMessage());
            return;
        }

        log("시스템", "채팅 서버 시작 (포트 " + txtPortNumber.getText() + ")");
        btnServerStart.setText("Chat Server Running...");
        btnServerStart.setEnabled(false);
        txtPortNumber.setEnabled(false);

        AcceptServer accept_server = new AcceptServer();
        accept_server.start();
    }

    // 클라이언트 접속하면 클라이언트를 UserVec에 넣고 클라이언트 한 명당 스레드 생성
    class AcceptServer extends Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    log("접속", "클라이언트 접속 대기 중...");
                    client_socket = socket.accept();
                    log("접속", "새 클라이언트 소켓 연결: " + client_socket);

                    UserService new_user = new UserService(client_socket);
                    UserVec.add(new_user);
                    log("접속", "새 UserService 생성 (현재 접속자 수: " + UserVec.size() + "명)");

                    new_user.start();
                } catch (IOException e) {
                    log("에러", "accept 중 오류: " + e.getMessage());
                    break;
                }
            }
        }
    }

    // ---- TXT 로드 / 저장 ----
    private void loadProfilesFromTxt() {
        File file = new File(CLIENT_TXT_FILE);
        if (!file.exists()) {
            log("프로필", "프로필 TXT 없음. 새로 시작합니다.");
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
            log("프로필", "TXT 프로필 로드 완료: " + count + "명");
        } catch (Exception e) {
            log("프로필", "TXT 로드 오류: " + e.getMessage());
        }
    }

    public void saveProfilesToTxt() {
        try {
            File f = new File(CLIENT_TXT_FILE);
            
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(f))) {
                synchronized (clientProfiles) {
                	
                	log("프로필", "==============================");
                    log("프로필", "TXT로 저장할 프로필 목록 ↓");
                    
                    for (ClientProfile p : clientProfiles.values()) {
                        String name   = p.getName();
                        String status = p.getStatusMessage();
                        String img    = p.getProfileImagePath();

                        // 파일에 쓰는 원본 포맷
                        String line = name + "|" + status + "|" + img;
                        bw.write(line);
                        bw.newLine();

                        // 로그용은 보기 좋게 가공
                        String statusForLog = (status == null || status.isEmpty()) ? "(없음)" : status;
                        String imgForLog    = (img != null && img.contains("default")) ? "default" : img;

                        log("프로필", " - " + name + " | 상태: " + statusForLog + " | 이미지: " + imgForLog);
                    }
                    log("프로필", "==============================\n");
                }
            }
            log("프로필", "TXT 프로필 저장 완료: " + clientProfiles.size() + "명\n");
        } catch (Exception e) {
            log("프로필", "TXT 저장 오류: " + e.getMessage());
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
            log("에러", "서버 소켓 종료 중 오류: " + e.getMessage());
        }

        synchronized (UserVec) {
            for (UserService u : UserVec) {
                u.closeConnection();
            }
            UserVec.clear();
        }

        saveProfilesToTxt();
        log("시스템", "서버 및 클라이언트 연결 정리 완료.");
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
                log("에러", "UserService 생성 중 error: " + e.getMessage());
            }
        }

        public void WriteOne(String msg) {
            try {
                dos.writeUTF(msg);
            } catch (IOException e) {
                log("에러", "dos.writeUTF() error: 클라이언트 연결 끊김 (" + UserName + ")");
                closeConnection();
                UserVec.removeElement(this);
                log("접속", UserName + " 연결 끊김. 현재 접속자 수: " + UserVec.size() + "명");
            }
        }

        public void WriteAll(String str) {
            for (UserService user : user_vc) {
                user.WriteOne(str);
            }
        }

        private void applyProfileChange(String newName, String newImagePath, String newStatus) {
            String oldName = UserName;

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
                    log("프로필", "이름 변경: '" + oldName + "' → '" + newName + "'");
                    UserName = newName;
                } else {
                    clientProfiles.put(UserName, p);
                }

                clientProfile = p;
            }

            log("프로필",
                    "수정됨 - 이름: " + UserName +
                    ", 상태: " + clientProfile.getStatusMessage() +
                    ", 이미지: " + clientProfile.getProfileImagePath());

            saveProfilesToTxt();

            // 전체 프로필 다시 뿌리기
            broadcastAllProfilesToAllClients();

            // 접속자 목록도 다시 전송 (이름 바뀌었을 수도 있으니까)
            BroadcastUserList();
        }

        // 이 유저에게 서버가 알고 있는 모든 프로필 정보 보내기
        @SuppressWarnings("unused")
        private void sendAllProfilesToThisUser() {
            synchronized (clientProfiles) {
                for (ClientProfile p : clientProfiles.values()) {
                    String msg = "/profile " + p.getName().trim() + " " +
                            p.getProfileImagePath().trim() + " " +
                            p.getStatusMessage().trim();
                    log("프로필", "개별 전송 to " + UserName + " : " + msg);
                    WriteOne(msg);
                }
            }
        }

        public void BroadcastUserList() {
            StringBuilder sb = new StringBuilder("/list ");

            // 이름 중복 제거를 위해 Set 사용
            LinkedHashSet<String> nameSet = new LinkedHashSet<>();

            synchronized (user_vc) {
                for (UserService u : user_vc) {
                    String n = u.UserName;
                    if (n != null) {
                        n = n.trim();
                        if (!n.isEmpty()) nameSet.add(n);   // 같은 이름은 한 번만
                    }
                }
            }

            for (String n : nameSet) {
                sb.append(n).append(" ");
            }

            String userListMsg = sb.toString().trim();
            log("접속", "현재 접속자 목록 전송: " + userListMsg);
            WriteAll(userListMsg);
        }

        // 한 명 변경 후 전체에게 변경분만 보내는 함수 쓰려면 여기 사용
        @SuppressWarnings("unused")
        private void broadcastProfileUpdate() {
            if (clientProfile == null) return;
            String msg = "/profile " + clientProfile.getName().trim() + " " +
                    clientProfile.getProfileImagePath().trim() + " " +
                    clientProfile.getStatusMessage().trim();
            log("프로필", "변경 브로드캐스트: " + msg);
            WriteAll(msg);
        }

        // 전체에게 모든 프로필 뿌리기
        private void broadcastAllProfilesToAllClients() {
        	
        	log("프로필", "전체 프로필 브로드캐스트 (총 " + clientProfiles.size() + "명)");
        	
            synchronized (clientProfiles) {
                for (ClientProfile p : clientProfiles.values()) {
                    String msg = "/profile " + p.getName().trim() + " " +
                            p.getProfileImagePath().trim() + " " +
                            p.getStatusMessage().trim();

                    WriteAll(msg);
                }
            }
        }

        public void closeConnection() {
            try {
                if (dos != null) dos.close();
                if (dis != null) dis.close();
                if (client_socket != null && !client_socket.isClosed())
                    client_socket.close();
            } catch (IOException e) {
                // 무시
            }
        }

        @Override
        public void run() {
            while (true) {
                try {
                    // 첫 메시지: "/login username"
                    String line1 = dis.readUTF();
                    String[] msg = line1.split(" ", 2);

                    if (msg.length < 2) {
                        log("경고", "잘못된 로그인 메시지: " + line1);
                        continue;
                    }

                    UserName = msg[1].trim();
                    log("인증", "로그인 요청: " + UserName);

                    synchronized (clientProfiles) {
                        clientProfile = clientProfiles.get(UserName);
                        if (clientProfile == null) {
                            clientProfile = new ClientProfile(UserName, "",
                                    "/Images/defaultprofileimage.png");
                            clientProfiles.put(UserName, clientProfile);
                            log("프로필", "새 프로필 생성: " + UserName);
                        } else {
                            log("프로필", "기존 프로필 로드: " + UserName);
                        }
                    }

                    saveProfilesToTxt();

                    log("접속",
                            UserName + " 님 입장했습니다 ! 상태: " +
                            clientProfile.getStatusMessage() +
                            ", 이미지: " + clientProfile.getProfileImagePath());

                    // 모든 프로필 /list 전송
                    broadcastAllProfilesToAllClients(); // /profile
                    BroadcastUserList();                // /list

                    // 이후부터는 채팅/프로필 업데이트 루프
                    while (true) {
                        String chat_msg = dis.readUTF().trim();

                        // 프로필 변경
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

                        // 채팅방 생성 요청 처리
                        if (chat_msg.startsWith("/createroom ")) {
                            String membersLine = chat_msg.substring("/createroom ".length()).trim();
                            if (membersLine.isEmpty()) continue;

                            // membersLine = "ww ff qq" 처럼
                            String[] members = membersLine.split("\\s+");

                            // 그대로 /room 으로 보냄
                            String roomMsg = "/room " + membersLine;
                            log("채팅방", UserName + " 님이 채팅방 생성: [" + membersLine + "]");

                            synchronized (user_vc) {
                                for (UserService u : user_vc) {
                                    for (String m : members) {
                                        if (u.UserName.equals(m)) {
                                            u.WriteOne(roomMsg);
                                            break;
                                        }
                                    }
                                }
                            }
                            continue;
                        }

                        // 일반 채팅
                        log("메시지", UserName + " : " + chat_msg);
                        WriteAll(UserName + " : " + chat_msg + "\n");
                    }

                } catch (IOException e) {
                    log("에러", "클라이언트 수신 오류 (" + UserName + "): " + e.getMessage());
                    closeConnection();
                    UserVec.removeElement(this);
                    log("접속", UserName + " 님 퇴장. 현재 접속자 수: " + UserVec.size() + "명");
                    BroadcastUserList();
                    break;
                }
            }
        }
    }
}
