// 실제 서버 기능

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

    private ServerSocket socket; // 서버 소켓
    private Socket client_socket; // accept()에서 생성되는 클라이언트 소켓
    private Vector<UserService> UserVec = new Vector<>(); // 접속 중인 사용자 목록

    private static final int BUF_LEN = 128; // 현재는 사용 안 하지만 남겨둠 (필요 시 사용)

    
    // 이름 기준으로 유저 프로필 관리 (TXT 저장용)
    private Map<String, ClientProfile> clientProfiles = Collections.synchronizedMap(new HashMap<>());

    private static final String CLIENT_TXT_FILE = "./client_profiles.txt";
    
    public JavaChatServerPanel() {
        setLayout(null);
        setBorder(new EmptyBorder(5, 5, 5, 5));

        // 로그 출력 영역
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(12, 10, 320, 260);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Server Log"));
        add(scrollPane);

        textArea = new JTextArea();
        textArea.setEditable(false);
        scrollPane.setViewportView(textArea);
        
        loadProfilesFromTxt(); // 서버 켤 때 기존 TXT에서 프로필 로드

        // 포트 라벨
        JLabel lblPort = new JLabel("Port Number");
        lblPort.setBounds(12, 285, 90, 26);
        add(lblPort);

        // 포트 입력 필드
        txtPortNumber = new JTextField();
        txtPortNumber.setHorizontalAlignment(SwingConstants.CENTER);
        txtPortNumber.setText("30000");
        txtPortNumber.setBounds(111, 285, 221, 26);
        add(txtPortNumber);
        txtPortNumber.setColumns(10);

        // 서버 시작 버튼
        btnServerStart = new JButton("Server Start");
        btnServerStart.setBounds(12, 320, 320, 35);
        add(btnServerStart);

        btnServerStart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startServer();
            }
        });
    }

    // 서버 시작 
    private void startServer() {
        try {
            int port = Integer.parseInt(txtPortNumber.getText().trim());
            socket = new ServerSocket(port);
        } 
        catch (NumberFormatException | IOException e1) {
            AppendText("[ERROR] 서버 소켓 생성 실패: " + e1.getMessage());
            e1.printStackTrace();
            return;
        }

        AppendText("Chat Server Running on port " + txtPortNumber.getText());
        btnServerStart.setText("Chat Server Running...");
        btnServerStart.setEnabled(false);
        txtPortNumber.setEnabled(false);

        // AcceptServer 스레드 시작. 클라이언트 접속 대기
        AcceptServer accept_server = new AcceptServer();
        accept_server.start();
    }

    
    // 서버의 JTextArea에 문자열 출력
    public void AppendText(String str) {
        textArea.append(str + "\n");
        textArea.setCaretPosition(textArea.getText().length());
    }
    
    // 새 클라이언트가 접속할 때마다 UserService 스레드 하나씩 생성
    class AcceptServer extends Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    AppendText("Waiting clients ...");
                    client_socket = socket.accept();   // 클라이언트 접속 대기
                    AppendText("새로운 참가자 from " + client_socket);

                    // User 당 하나의 스레드 생성
                    UserService new_user = new UserService(client_socket);
                    UserVec.add(new_user);
                    AppendText("사용자 입장. 현재 참가자 수: " + UserVec.size());

                    new_user.start();
                } 
                catch (IOException e) {
                    AppendText("!!!! accept 에러 발생... !!!!");
                    break; // 서버 소켓이 닫히면 반복 탈출
                }
            }
        }
    }
    
    // .txt 파일 로드
    private void loadProfilesFromTxt() {
    	
        File file = new File(CLIENT_TXT_FILE);

        if (!file.exists()) {
            AppendText("프로필 TXT 없음. 새로 시작합니다.");
            return;
        }

        // .txt 파일을 읽음
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            
        	String line;
            int count = 0;

            while ((line = br.readLine()) != null) {

                String[] tokens = line.split("\\|", 3);
                
                if (tokens.length == 3) {
                    String name = tokens[0];
                    String status = tokens[1];
                    String img = tokens[2];

                    clientProfiles.put(name, new ClientProfile(name, status, img));
                    count++;
                }
            }

            AppendText("TXT 프로필 로드 완료: " + count + "명");
        } catch (Exception e) {
            AppendText("TXT 로드 오류: " + e.getMessage());
        }
    }

    // .txt 파일 저장
    public void saveProfilesToTxt() {
        try {
            File f = new File(CLIENT_TXT_FILE);
            
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(f))) {

            	// 여러 유저가 동시에 프로필을 수정할 때를 위해 synchronized
                synchronized (clientProfiles) {
                    AppendText("===== TXT로 저장할 프로필 목록 =====");
                    
                    for (ClientProfile p : clientProfiles.values()) {
                        String line = p.getName() + "|" + p.getStatusMessage() + "|" + p.getProfileImagePath();
                        bw.write(line);
                        bw.newLine();

                        // 여기서 한 줄씩 어떤 내용이 저장되는지 서버 로그에 찍기
                        AppendText("-> " + line);
                    }
                    AppendText("=================================");
                }
            }

            AppendText("TXT 프로필 저장 완료: " + clientProfiles.size() + "명");
        } catch (Exception e) {
            AppendText("TXT 저장 오류: " + e.getMessage());
        }
    }


    // 서버를 종료하면 모든 클라이언트 종료 
    public void shutdownServer() {
        // 모든 클라이언트에게 서버 종료 알림 보내기
        synchronized (UserVec) {
            for (UserService u : UserVec) {
                try {
                    u.WriteOne("/server_shutdown"); // 클라이언트가 이걸 받으면 종료하도록 클라이언트에게 메시지를 보냄.
                } catch (Exception ignore) {}
            }
        }

        // 서버 소켓 닫기
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } 
        catch (IOException e) {
            AppendText("서버 소켓 종료 중 오류: " + e.getMessage());
        }

        // 각 클라이언트 소켓/스트림 정리
        synchronized (UserVec) {
            for (UserService u : UserVec) {
                u.closeConnection(); // 밑에 함수 정의되어있음.
            }
            UserVec.clear();
        }

        // 프로필 TXT 한 번 더 저장
        saveProfilesToTxt();

        AppendText("서버 및 클라이언트 연결 정리 완료.");
    }


    // 각 유저를 담당하는 스레드
    class UserService extends Thread {
        private InputStream is;
        private OutputStream os;
        private DataInputStream dis;
        private DataOutputStream dos;
        private Socket client_socket;
        private Vector<UserService> user_vc; // 전체 사용자 목록
        private String UserName = "";
        private ClientProfile clientProfile;

        public UserService(Socket client_socket) {
            this.client_socket = client_socket;
            this.user_vc = UserVec;

            try {
                is = client_socket.getInputStream();
                dis = new DataInputStream(is);
                os = client_socket.getOutputStream();
                dos = new DataOutputStream(os);
            } catch (Exception e) {
                AppendText("UserService 생성 중 error: " + e.getMessage());
            }
        }

        // 특정 클라이언트에게 메시지 전송
        public void WriteOne(String msg) {
            try {
                dos.writeUTF(msg);
            } catch (IOException e) {
                AppendText("dos.writeUTF() error: 클라이언트 연결 끊김");
                try {
                    dos.close();
                    dis.close();
                    client_socket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                UserVec.removeElement(this);
                AppendText("사용자 퇴장. 현재 참가자 수: " + UserVec.size());
            }
        }

        // 모든 클라이언트에게 메시지 전송 (단톡)
        public void WriteAll(String str) {
            for (int i = 0; i < user_vc.size(); i++) {
                UserService user = user_vc.get(i);
                user.WriteOne(str);
            }
        }

        
        // 접속자 이름 목록 보내기 ("/list " 프로토콜)
        public void BroadcastUserList() {
            StringBuilder sb = new StringBuilder("/list ");

            synchronized (user_vc) {
                for (int i = 0; i < user_vc.size(); i++) {
                    sb.append(user_vc.get(i).UserName).append(" ");
                }
            }

            String userListMsg = sb.toString().trim();
            AppendText("현재 접속자 목록 전송: " + userListMsg);
            WriteAll(userListMsg + "\n");
        }
        
        // 이 유저의 프로필을 "/profile 이름 이미지경로 상태메시지" 형태로 전송
        private void sendProfileToSelf() {
            if (clientProfile == null) return;

            // 상태메시지에 공백 포함 가능. 클라에서는 split(" ", 4) 같은 식으로 파싱 추천.
            String msg = "/profile " + clientProfile.getName() + " " + clientProfile.getProfileImagePath() + " " + clientProfile.getStatusMessage();
            WriteOne(msg);
        }

        // 프로필 변경 내용을 전체에게 알리기
        private void broadcastProfileUpdate() {
            if (clientProfile == null) return;

            String msg = "/profile " + clientProfile.getName() + " " + clientProfile.getProfileImagePath() + " " + clientProfile.getStatusMessage();
            WriteAll(msg);
        }
        
        public void closeConnection() {
            try {
                if (dos != null) dos.close();
                if (dis != null) dis.close();
                if (client_socket != null && !client_socket.isClosed()) {
                    client_socket.close();
                }
            } catch (IOException e) {
                // 무시하거나 로그
            }
        }

        @Override
        public void run() {
            while (true) {
                try {
                    // 1) 첫 메시지: "/login username"
                    String line1 = dis.readUTF();
                    String[] msg = line1.split(" ");

                    UserName = msg[1].trim();

                    // 이름 기준으로 프로필 찾기/생성
                    synchronized (clientProfiles) {
                        clientProfile = clientProfiles.get(UserName);
                        if (clientProfile == null) {
                            clientProfile = new ClientProfile(UserName, "", "/Images/defaultprofileimage.png");
                            clientProfiles.put(UserName, clientProfile);
                            AppendText("새 프로필 생성: " + UserName);
                        }
                        else {
                            AppendText("기존 프로필 로드: " + UserName);
                        }
                    }
                    
                    saveProfilesToTxt();

                    AppendText("새로운 참가자 " + UserName + " 입장. " + "상태: " + clientProfile.getStatusMessage() + ", 이미지: " + clientProfile.getProfileImagePath());
                    WriteOne("Welcome to Java chat server");
                    WriteOne(UserName + "님 환영합니다.");

                    // 접속한 본인에게 자신의 프로필 전송
                    sendProfileToSelf();

                    // 새 참가자 포함해서 전체 /list 다시 보내기
                    BroadcastUserList();

                    // 2) 이후부터는 채팅/프로필 업데이트 루프
                    while (true) {
                        String chat_msg = dis.readUTF();
                        chat_msg = chat_msg.trim();

                        // --- 프로필 업데이트 프로토콜 ---
                        // 예) "/profile_update 이름 이미지경로 상태메시지..."
                        if (chat_msg.startsWith("/profile_update ")) {
                            String body = chat_msg.substring("/profile_update ".length());
                            String[] tokens = body.split(" ", 3);
                            // tokens[0] = 새이름, tokens[1] = 이미지경로, tokens[2] = 상태메시지

                            if (tokens.length >= 3) {
                                String newName      = tokens[0];   // 새 이름
                                String newImagePath = tokens[1];   // 새 프로필 이미지 경로
                                String newStatus    = tokens[2];   // 새 상태메시지

                                synchronized (clientProfiles) {
                                    // 1) 현재 접속자의 기존 프로필을 "현재 UserName"으로 가져오기
                                    ClientProfile p = clientProfiles.get(UserName);

                                    if (p == null) {
                                        // 혹시 없으면 새로 만듦 (이름 바뀐 상태 기준으로)
                                        p = new ClientProfile(newName, newStatus, newImagePath);
                                    } else {
                                        // 기존 프로필 값들 갱신
                                        p.setProfileImagePath(newImagePath);
                                        p.setStatusMessage(newStatus);
                                        // ClientProfile에 setName()이 있다면 같이 호출
                                        // p.setName(newName);
                                    }

                                    // 2) 이름이 실제로 변경된 경우 → Map의 key도 변경해야 함
                                    if (!newName.equals(UserName)) {
                                        // 기존 키 제거하고
                                        clientProfiles.remove(UserName);
                                        // 새 이름으로 다시 put
                                        clientProfiles.put(newName, p);

                                        AppendText("[프로필 이름 변경] " + UserName + " → " + newName);

                                        // 이 스레드가 들고 있는 현재 사용자 이름도 바꿔줌
                                        UserName = newName;
                                    } else {
                                        // 이름이 안 바뀐 경우, 그냥 덮어쓰기
                                        clientProfiles.put(UserName, p);
                                    }

                                    // 이 유저의 현재 프로필 객체 갱신
                                    clientProfile = p;
                                }

                                // 3) 서버 로그에 최종 수정 내용 찍기
                                AppendText("[프로필 수정됨] " + UserName +
                                           " / 상태: " + clientProfile.getStatusMessage() +
                                           " / 이미지: " + clientProfile.getProfileImagePath());

                                // 4) 모든 클라이언트에게 브로드캐스트 (/profile ...)
                                broadcastProfileUpdate();

                                // 5) .txt 파일로 전체 프로필 저장
                                saveProfilesToTxt();
                            }
                            continue;
                        }

                        // --- 일반 채팅 ---
                        AppendText("[MSG] " + UserName + " : " + chat_msg);
                        WriteAll(UserName + " : " + chat_msg);
                    }

                } catch (IOException e) {
                    AppendText("dis.readUTF() error: " + e.getMessage());
                    try {
                        dos.close();
                        dis.close();
                        client_socket.close();
                    } catch (Exception ee) {
                        // 무시
                    }

                    UserVec.removeElement(this);
                    AppendText("사용자 퇴장. 남은 참가자 수: " + UserVec.size());
                    BroadcastUserList();  // 나간 후 /list 다시 전송
                    break;
                }
            }
        }
    }
}