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
    private final Map<String, String> gameRequests = Collections.synchronizedMap(new HashMap<>()); // key:요청 클라, value:상대 클라
    private static final int BUF_LEN = 128;

    // ---- 프로필 TXT 관리 ----
    private Map<String, ClientProfile> clientProfiles = Collections.synchronizedMap(new HashMap<>());

    // ---- 채팅방 TXT/로그 관리 ----
    private static final String CLIENT_TXT_FILE = "./client_profiles.txt";
    private static final String CHATROOM_TXT_FILE = "./chat_rooms.txt";
    private static final String CHAT_LOG_DIR      = "./chat_logs";

    private Map<String, ChatRoomInfo> chatRooms = Collections.synchronizedMap(new LinkedHashMap<>());


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
        
        File logDir = new File(CHAT_LOG_DIR);
        if (!logDir.exists()) logDir.mkdir();

        loadProfilesFromTxt();    // 프로필 TXT 로드
        loadChatRoomsFromTxt();   // 채팅방 TXT 로드

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
    
    private void loadChatRoomsFromTxt() {
        File file = new File(CHATROOM_TXT_FILE);
        if (!file.exists()) {
            log("채팅방", "채팅방 TXT 없음. 새로 시작합니다.");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            int count = 0;

            while ((line = br.readLine()) != null) {
                // roomId|creator|member1,member2,member3
                String[] tokens = line.split("\\|", 3);
                if (tokens.length == 3) {
                    String roomId   = tokens[0].trim();
                    String creator  = tokens[1].trim();
                    String membersStr = tokens[2].trim();

                    List<String> members = new ArrayList<>();
                    if (!membersStr.isEmpty()) {
                        for (String m : membersStr.split(",")) {
                            String n = m.trim();
                            if (!n.isEmpty()) members.add(n);
                        }
                    }

                    if (!roomId.isEmpty()) {
                        chatRooms.put(roomId,
                                new ChatRoomInfo(roomId, creator, members));
                        count++;
                    }
                }
            }
            log("채팅방", "TXT 채팅방 로드 완료: " + count + "개");
        } catch (Exception e) {
            log("채팅방", "TXT 채팅방 로드 오류: " + e.getMessage());
        }
    }

    private void saveChatRoomsToTxt() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(CHATROOM_TXT_FILE))) {
            synchronized (chatRooms) {
                for (ChatRoomInfo c : chatRooms.values()) {
                    String membersStr = String.join(",", c.getMembers());
                    String line = c.getRoomId() + "|" + c.getCreator() + "|" + membersStr;
                    bw.write(line);
                    bw.newLine();
                }
            }
            log("채팅방", "TXT 채팅방 저장 완료: " + chatRooms.size() + "개");
        } catch (Exception e) {
            log("채팅방", "TXT 채팅방 저장 오류: " + e.getMessage());
        }
    }

    
    private synchronized String generateRoomId() {
        return "R" + System.currentTimeMillis();
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

            // 전체 친구 목록을 clientProfiles 기준으로 전송
            synchronized (clientProfiles) {
                for (ClientProfile p : clientProfiles.values()) {
                    String n = p.getName();
                    if (n != null) {
                        n = n.trim();
                        if (!n.isEmpty()) {
                            sb.append(n).append(" ");
                        }
                    }
                }
            }

            String userListMsg = sb.toString().trim();
            log("접속", "전체 친구 목록 전송: " + userListMsg);
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
        
        //게임 메시지를 특정 사용자들에게만 보냄
        public void sendToSpecificUsers(String msg, String... userNames) {
            Set<String> targetUsers = new HashSet<>(Arrays.asList(userNames));
            
            synchronized (user_vc) {
                for (UserService user : user_vc) {
                    // 접속 중인 유저의 이름이 타겟 목록에 있으면 메시지 전송
                    if (targetUsers.contains(user.UserName)) {
                        user.WriteOne(msg);
                        log("게임", "전송 To: " + user.UserName + " - " + msg);
                    }
                }
            }
        }
        
        // 접속자 목록에서 특정 사용자 이름 찾기
        public UserService findUserService(String username) {
            if (username == null || username.isEmpty()) return null;
            
            synchronized (user_vc) {
                for (UserService user : user_vc) {
                    // 접속이 완료되어 UserName이 설정된 경우에만 비교
                    if (user.UserName != null && user.UserName.equals(username)) {
                        return user;
                    }
                }
            }
            return null;
        }
        
        private void sendMyChatRooms() {
            synchronized (chatRooms) {
                for (ChatRoomInfo c : chatRooms.values()) {
                    if (c.getMembers().contains(UserName)) {
                        String membersLine = String.join(" ", c.getMembers());
                        // 프로토콜: /room roomId creator members...
                        String roomMsg = "/room " + c.getRoomId() + " " + c.getCreator() + " " + membersLine;
                        WriteOne(roomMsg);
                    }
                }
            }
        }

        
        private void handleRoomMessage(String roomId, String text) {
            ChatRoomInfo room;
            synchronized (chatRooms) {
                room = chatRooms.get(roomId);
            }
            if (room == null) {
                log("채팅방", "존재하지 않는 roomId로 메시지 수신: " + roomId);
                return;
            }

            String fullLine = UserName + " : " + text;

            // 로그 파일에 저장
            File logFile = new File(CHAT_LOG_DIR, roomId + ".txt");
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(logFile, true))) {
                bw.write(fullLine);
                bw.newLine();
            } catch (IOException e) {
                log("채팅방", "메시지 로그 저장 오류(" + roomId + "): " + e.getMessage());
            }

            // 방 멤버에게만 전송
            String sendMsg = "/msg " + roomId + " " + fullLine;
            synchronized (user_vc) {
                for (UserService u : user_vc) {
                    if (room.getMembers().contains(u.UserName)) {
                        u.WriteOne(sendMsg);
                    }
                }
            }
        }

        private void sendRoomHistory(String roomId) {
            File logFile = new File(CHAT_LOG_DIR, roomId + ".txt");
            if (!logFile.exists()) return;

            try (BufferedReader br = new BufferedReader(new FileReader(logFile))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String sendMsg = "/msg " + roomId + " " + line;
                    WriteOne(sendMsg);   // 이제 this.WriteOne(...) 가능
                }
            } catch (IOException e) {
                log("채팅방", "로그 전송 오류(" + roomId + "): " + e.getMessage());
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

                    // 내가 들어있는 채팅방 목록 전송
                    sendMyChatRooms();
                    
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

                            // membersLine = "me friend1 friend2 ..."
                            String[] memberArr = membersLine.split("\\s+");
                            List<String> members = new ArrayList<>();
                            for (String m : memberArr) {
                                String n = m.trim();
                                if (!n.isEmpty()) members.add(n);
                            }

                            // 방 ID 생성
                            String roomId = generateRoomId();
                            ChatRoomInfo roomInfo = new ChatRoomInfo(roomId, UserName, members);

                            synchronized (chatRooms) {
                                chatRooms.put(roomId, roomInfo);
                            }
                            saveChatRoomsToTxt();

                            // 클라이언트에게 전송할 members 문자열
                            String sendMembersLine = String.join(" ", members);

                            // 프로토콜: /room roomId creator members...
                            String roomMsg = "/room " + roomId + " " + UserName + " " + sendMembersLine;

                            log("채팅방", "새 채팅방 생성: roomId=" + roomId + ", 멤버=" + sendMembersLine);

                            // 현재 접속 중인 멤버들에게만 방 정보 전송
                            synchronized (user_vc) {
                                for (UserService u : user_vc) {
                                    if (members.contains(u.UserName)) {
                                        u.WriteOne(roomMsg);
                                    }
                                }
                            }
                            continue;
                        }

                        // 로그 요청
                        if (chat_msg.startsWith("/loadlog ")) {
                            String roomId = chat_msg.substring("/loadlog ".length()).trim();
                            sendRoomHistory(roomId);
                            continue;
                        }

                        // 게임 요청 처리
                        if (chat_msg.startsWith("/game_request ")) {
                            String participantsLine = chat_msg.substring("/game_request ".length()).trim();
                            String[] participants = participantsLine.split("[,\\s]+");
                            
                            // 1. 유효성 검사 (클라이언트가 1:1 요청을 보내지만, 서버에서도 재확인)
                            if (participants.length != 2) {
                                log("게임", "경고: " + UserName + " 님이 1:1이 아닌 게임 요청을 보냄: " + participantsLine);                            
                                continue;
                            }
                            
                            String sender = participants[0]; // 요청자 (본인)
                            String receiver = participants[1]; // 상대방
                            
                            if (!sender.equals(UserName)) {
                                // 요청자와 메시지 보낸 UserName이 일치해야 함
                                log("게임", "경고: 요청자 불일치! " + UserName + " != " + sender);
                                continue;
                            }
           
                            gameRequests.put(sender, receiver);
                            log("게임", sender + " -> " + receiver + " 게임 요청 (대기 중)");
                            
                            // 3. 상대방도 나에게 요청했는지 확인 (쌍방 요청 확인)
                            if (gameRequests.containsKey(receiver) && gameRequests.get(receiver).equals(sender)) {
                                // 쌍방 요청이 확인되면 게임 시작                              
                                String gameStartCmd = "/game_start " + participantsLine;
                                  
                                sendToSpecificUsers(gameStartCmd, sender, receiver);
                                
                                // 5. 요청 상태 초기화
                                gameRequests.remove(sender);
                                gameRequests.remove(receiver);
                                log("게임", "=== " + sender + " & " + receiver + " 게임 시작! ===");
                                
                            } else {                               
                            	sendToSpecificUsers("/game_prompt " + sender, receiver);
                                log("게임", receiver + " (상대방)에게 " + sender + "의 요청 알림 전송");
                            }
                            continue;
                        }                      
                        
                        // 게임 취소 처리
                        if (chat_msg.startsWith("/game_cancel ")) {
                            
                            String participantsLine = chat_msg.substring("/game_cancel ".length()).trim();
                            String[] participants = participantsLine.split("[,\\s]+");
                            
                            String canceler = UserName; // 취소한 사람
                            String target = "";         // 상대방 이름
                            
                            if (participants.length == 2) {
                                target = participants[0].equals(canceler) ? participants[1] : participants[0];
                            }
                            
                            // 요청자 (UserName)의 상태를 맵에서 제거
                            gameRequests.remove(canceler);
                            log("게임", canceler + " 님이 게임 요청을 취소했습니다. 상대: " + target);
                            
                            // 상대방이 혹시 나에게 요청했던 상태라면 그것도 제거 (안전 장치)
                            if (target != null && gameRequests.containsKey(target) && gameRequests.get(target).equals(canceler)) {
                                gameRequests.remove(target);
                                log("게임", "상대방(" + target + ")의 요청 대기 상태도 해제함.");
                            }
                            
                            // 상대방에게 취소되었음을 알림
                            if (!target.isEmpty()) {
                                sendToSpecificUsers("/game_canceled " + canceler, target);
                            }
                            
                            continue;
                        }
                                               
                        //게임 즉시 시작 요청 처리
                        if (chat_msg.startsWith("/game_start_ready ")) {                          
                            String opponentName = chat_msg.substring("/game_start_ready ".length()).trim();
                            String sender = UserName;

                            // Key: 요청자, Value: 상대방 이름
                            gameRequests.put(sender, opponentName);
                            log("게임", sender + " -> " + opponentName + " 준비 완료 (대기 중)");

                            // 2. 상대방도 준비 완료했는지 확인
                            if (gameRequests.containsKey(opponentName) && gameRequests.get(opponentName).equals(sender)) {  	
                            	// 포도 난수 생성 책임을 서버에게 옮김 (동일 배열을 받기 위함)
                                Random random = new Random();
                                
                                // 1.Left판에 할당할 포도 데이터
                                StringBuilder grapeData_for_sender = new StringBuilder();
                                for (int r = 0; r < 9; r++) {
                                    for (int c = 0; c < 8; c++) {
                                        grapeData_for_sender.append(random.nextInt(4) + 1).append(","); // 1~4 값
                                    }
                                }
                                String finalGrapeData_SENDER = grapeData_for_sender.substring(0, grapeData_for_sender.length() - 1);
                                
                                
                                // 2. Right판에 할당할 포도 데이터
                                StringBuilder grapeData_for_opponent = new StringBuilder();
                                for (int r = 0; r < 9; r++) {
                                    for (int c = 0; c < 8; c++) {
                                        grapeData_for_opponent.append(random.nextInt(4) + 1).append(","); // 1~4 값
                                    }
                                }
                                String finalGrapeData_OPPONENT = grapeData_for_opponent.substring(0, grapeData_for_opponent.length() - 1);
                              
                                // 3. 게임 시작 명령에 포도 데이터 포함
                                String participantsLine = sender + "," + opponentName;
                                
                                // 1. LEFT를 할당
                                String senderCmd ="/game_start_command " + participantsLine + " LEFT " + finalGrapeData_SENDER + " " + finalGrapeData_OPPONENT;                              
                                sendToSpecificUsers(senderCmd, sender);

                                // 2. RIGHT를 할당
                                String receiverCmd = "/game_start_command " + participantsLine + " RIGHT " + finalGrapeData_OPPONENT + " " + finalGrapeData_SENDER;
                                sendToSpecificUsers(receiverCmd, opponentName);
                                
                                // 3. 요청 상태 초기화
                                gameRequests.remove(sender);
                                gameRequests.remove(opponentName);
                                log("게임", "=== " + sender + " & " + opponentName + " 게임 동기화 시작 명령 전송! ===");
                            } else {
                                log("게임", opponentName + "의 준비 완료를 기다립니다.");
                            }                            
                            continue;
                        }
                        
                        // ★ 포도 제거 동기화
                        if (chat_msg.startsWith("/game_remove ")) {
                            // 형식: /game_remove ownerName opponentName r1,c1;r2,c2;...
                            String body = chat_msg.substring("/game_remove ".length()).trim();
                            String[] tokens = body.split(" ", 3);  // owner, opponent, coords

                            if (tokens.length == 3) {
                                String ownerName    = tokens[0].trim();
                                String opponentName = tokens[1].trim();
                                String coordString  = tokens[2].trim();

                                // 클라이언트들에게 보낼 명령
                                String forward = "/game_apply_remove " + ownerName + " " + coordString;

                                // 두 명에게만 전송 (본인 + 상대)
                                sendToSpecificUsers(forward, ownerName, opponentName);

                                log("게임", "포도 제거 브로드캐스트: " + forward);
                            } else {
                                log("게임", "잘못된 /game_remove 포맷: " + chat_msg);
                            }
                            continue;
                        }
                        
                        // ★ 포도 리필 요청 처리
                        if (chat_msg.startsWith("/game_refill_request ")) {
                            // 형식: /game_refill_request ownerName opponentName
                            String body = chat_msg.substring("/game_refill_request ".length()).trim();
                            String[] tokens = body.split("\\s+");
                            if (tokens.length < 2) {
                                log("게임", "잘못된 /game_refill_request: " + chat_msg);
                                continue;
                            }

                            String ownerName    = tokens[0].trim(); // 보드 주인 (요청자)
                            String opponentName = tokens[1].trim(); // 상대

                            // 9x8 전체 셀 수 (서버는 보드 상태를 모르므로 넉넉하게 전부 생성)
                            int totalCells = 9 * 8; // MiniGrapeGameManager.ROWS * COLS 과 같음

                            Random random = new Random();
                            StringBuilder sb = new StringBuilder();
                            for (int i = 0; i < totalCells; i++) {
                                int v = random.nextInt(4) + 1; // 1~4
                                if (i > 0) sb.append(",");
                                sb.append(v);
                            }
                            String values = sb.toString();

                            // 형식: /game_refill ownerName v1,v2,v3,...,v72
                            String refillMsg = "/game_refill " + ownerName + " " + values;

                            // ★ ownerName의 보드(오른쪽판 & 상대의 왼쪽판)에만 보내기
                            sendToSpecificUsers(refillMsg, ownerName, opponentName);

                            log("게임", "리필 명령 전송: " + refillMsg);
                            continue;
                        }
                        
                    	// 포도게임 결과 처리 (roomId를 포함해서 받음)
                        if (chat_msg.startsWith("/game_result ")) {
                            // 형식: /game_result roomId user1 user2 summary...
                            String body = chat_msg.substring("/game_result ".length()).trim();
                            String[] parts = body.split(" ", 4); // roomId, user1, user2, summary...

                            if (parts.length < 4) {
                                log("게임", "잘못된 /game_result 포맷: " + chat_msg);
                                continue;
                            }

                            String roomId         = parts[0].trim(); // ⭐ 어느 방에 보낼지
                            String user1          = parts[1].trim(); // 참가자 1
                            String user2          = parts[2].trim(); // 참가자 2
                            String summaryForChat = parts[3].trim(); // "[포도게임 결과] ..." 전체 문자열

                            // 로그 파일에 저장
                            File logFile = new File(CHAT_LOG_DIR, roomId + ".txt");
                            try (BufferedWriter bw = new BufferedWriter(new FileWriter(logFile, true))) {
                                bw.write(summaryForChat);
                                bw.newLine();
                            } catch (IOException e) {
                                log("채팅방", "게임 결과 로그 저장 오류(" + roomId + "): " + e.getMessage());
                            }

                            // 방 멤버에게만 전송  (/msg roomId 내용)
                            ChatRoomInfo room;
                            synchronized (chatRooms) {
                                room = chatRooms.get(roomId);
                            }
                            if (room == null) {
                                log("채팅방", "존재하지 않는 roomId로 게임 결과 수신: " + roomId);
                                continue;
                            }

                            String sendMsg = "/msg " + roomId + " " + summaryForChat;

                            synchronized (user_vc) {
                                for (UserService u : user_vc) {
                                    if (room.getMembers().contains(u.UserName)) {
                                        u.WriteOne(sendMsg);
                                    }
                                }
                            }

                            log("게임", "포도게임 결과 전송: roomId=" + roomId + ", msg=" + summaryForChat);
                            continue;
                        }


                        // 방 메시지: /msg roomId 내용
                        if (chat_msg.startsWith("/msg ")) {
                            String body = chat_msg.substring("/msg ".length()).trim();
                            int firstSpace = body.indexOf(' ');
                            if (firstSpace <= 0) continue;

                            String roomId = body.substring(0, firstSpace);
                            String text   = body.substring(firstSpace + 1);

                            handleRoomMessage(roomId, text);
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
