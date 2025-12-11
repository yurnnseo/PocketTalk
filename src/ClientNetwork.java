import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

// 서버와 소켓으로 연결하는 네트워크를 관리함.
// 서버가 보낸 문자열 명령(ex. /list, /progile 등)을 읽어서 ClientNetworkInterface (즉, ClientMenuFrame)에게 넘겨주는 역할을 함.
public class ClientNetwork {

    private Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;
    private ClientNetworkInterface listener;

    public ClientNetwork(String ip, int port, ClientNetworkInterface listener) throws IOException {
        this.listener = listener;

        // 서버와 소켓 연결
        socket = new Socket(ip, port);
        dis = new DataInputStream(socket.getInputStream());
        dos = new DataOutputStream(socket.getOutputStream());

        // 서버에서 오는 메시지를 수신하는 스레드 시작
        ListenNetwork net = new ListenNetwork();
        net.start();
    }

    // 서버로 메시지 전송
    public synchronized void sendToServer(String msg) {
        try {
            if (dos != null) {
                dos.writeUTF(msg);
                dos.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 서버에서 오는 메시지를 계속 읽고 메시지 종류에 따라 listener.xx() 호출
    private class ListenNetwork extends Thread {
    	
        public void run() {
            while (true) {
                try {
                    String msg = dis.readUTF();
                    if (msg == null) {
                        continue;
                    }
                    msg = msg.trim();

                    // 접속자 목록 (/list)
                    // "/list "로 시작하는지 확인
                    if (msg.startsWith("/list ")) {
                    	
                        String userListString = msg.substring(6).trim();
                        String[] users;

                        if (userListString.isEmpty()) {
                            users = new String[0];
                        } 
                        else {
                            users = userListString.split(" ");
                        }

                        for (int i = 0; i < users.length; i++) {
                            if (users[i] != null) {
                                users[i] = users[i].trim();
                            }
                        }

                        List<String> userList = Arrays.asList(users); // String[]을 List<String>으로 변환
                        listener.updateUserList(userList); // Listener에게 "접속자 목록 업데이트" 요청
                    }

                    // 프로필 정보 (/profile)
                    // "/profile "로 시작하는지 확인
                    else if (msg.startsWith("/profile ")) {
                        String body = msg.substring("/profile ".length());
                        String[] tokens = body.split(" ", 3); // name, imagePath, statusMsg

                        if (tokens.length >= 2) {
                            String name = tokens[0].trim();
                            String imagePath = tokens[1].trim();
                            String statusMsg = "";

                            // 세 번째 토큰이 있을 경우 상태 메시지로 사용
                            if (tokens.length == 3 && tokens[2] != null) {
                                statusMsg = tokens[2].trim();
                            }

                            listener.updateProfile(name, imagePath, statusMsg); // listener에게 "프로필 정보 갱신" 요청
                        }
                    }

                    // 채팅방 정보 (/room)
                    // "/room "로 시작하는지 확인
                    else if (msg.startsWith("/room ")) {
                        String body = msg.substring("/room ".length()).trim();
                        String[] tokens = body.split("\\s+");

                        if (tokens.length >= 3) {
                            String roomId = tokens[0]; // 채팅방 ID
                            String creatorName = tokens[1]; // 채팅방 만든 사람 이름

                            String members = ""; // 채팅방 나머지 멤버들 하나로 합침
                            for (int i = 2; i < tokens.length; i++) {
                                members += tokens[i];
                                if (i < tokens.length - 1) {
                                    members += " ";
                                }
                            }

                            listener.updateChatRoom(roomId, creatorName, members); // listener에게 "채팅방 정보" 전달
                        }
                    }

                    // 미니 게임 시작 (/game_start_command)
                    // "/game_start_command "로 시작하는지 확인
                    else if (msg.startsWith("/game_start_command ")) {
                        String body = msg.substring("/game_start_command ".length()).trim();
                        listener.startGame(body); // listener에게 "게임 시작" 전달
                    }

                    // 게임 내 포도 제거 (/game_apply_remove)
                    // "/game_apply_remove "로 시작하는지 확인
                    else if (msg.startsWith("/game_apply_remove ")) {
                        String body = msg.substring("/game_apply_remove ".length()).trim();
                        listener.removeGameItem(body); // listener에게 "포도 제거" 전달
                    }

                    // 게임 내 포도 재배치 (/game_refill)
                    // "/game_refill "로 시작하는지 확인
                    else if (msg.startsWith("/game_refill ")) {
                        String body = msg.substring("/game_refill ".length()).trim();
                        listener.refillGameItems(body); // listener에게 "포도 재배치" 전달
                    }

                    // 일반 채팅 메시지
                    else {
                        listener.receiveChatMessage(msg); // listener에게 "채팅 메시지 왔다"고 알려줌
                    }

                } catch (IOException e) {
                    // 서버 연결이 끊어진 경우
                    listener.onDisconnected(e);
                    break;
                }
            }
        }
    }
}
