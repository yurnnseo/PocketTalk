import javax.swing.*;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;


// 로그인 후 보여지는 메인 메뉴 프레임 (화면 전환과 패널 관리만 담당)
// ClientNetworkInterface를 구현해서 서버에서 온 메시지를 실제 UI 쪽으로 넘김.

public class ClientMenuFrame extends JFrame implements ClientNetworkInterface {

    private String username;
    private String ip_addr;
    private String port_no;

    private ClientNetwork network; // 서버 통신 담당 
    private List<String> currentUserList = new ArrayList<String>(); // 최근에 받은 접속자 목록 (/list 결과 저장)

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

        // 친구, 채팅 패널 생성
        friendsPanel = new ClientFriendsMenuPanel(this, this.username, profileImagePath);
        chatPanel = new ClientChatingMenuPanel(this, this.username);

        setContentPane(friendsPanel); // 처음 화면은 친구 목록 화면
        setVisible(true);

        
        // 서버 연결하고 로그인 요청 보냄
        try {
        	
        	// ClientNetwork 객체를 만들면서 this(현재 프레임)를 넘겨주면 
        	// 서버에서 온 메시지를 콜백 함수들(ex. updateUserList 등)로 전달받게 됨.
            network = new ClientNetwork(ip_addr, Integer.parseInt(port_no), this);

            // 접속 후 로그인 명령 전송
            network.sendToServer("/login " + this.username);

        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "서버 연결 실패!", "오류", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
    }

    
    // ClientNetworkInterface 구현

    // 서버한테 받은 사용자 목록(/list)을 클라이언트에게 전달
    public void updateUserList(final List<String> users) {
    	
        // 최근 목록을 리스트로 저장해두고 싶으면 이렇게 저장
    	currentUserList.clear();
    	if (users != null) {
    	    currentUserList.addAll(users);
    	}

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (friendsPanel != null) {
                    friendsPanel.updateFriendList(users);
                }
            }
        });
    }

    // 특정 클라이언트의 프로필 정보(/profile)을 클라이언트에게 전달
    public void updateProfile(final String name, final String imagePath, final String statusMsg) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (friendsPanel != null) {
                    friendsPanel.updateFriendProfileFromServer(name, imagePath, statusMsg);
                }
            }
        });
    }

    // 채팅방 정보(/room)을 클라이언트에게 전달
    public void updateChatRoom(final String roomId, final String creatorName, final String members) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (chatPanel != null) {
                    chatPanel.addChatRoom(roomId, creatorName, members);
                }
            }
        });
    }

    // 미니게임 시작(/game_start_command)을 클라이언트에게 전달
    public void startGame(final String body) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                MiniGameFrame.receiveStartCommand(body);
            }
        });
    }

    // 게임 내 포도 제거 명령(/game_apply_remove)을 클라이언트에게 전달
    public void removeGameItem(final String body) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                MiniGameFrame.receiveRemoveCommand(body);
            }
        });
    }

    // 게임 내 포도 재배치 명령(/game_refill)을 클라이언트에게 전달
    public void refillGameItems(final String body) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                MiniGameFrame.receiveRefillCommand(body);
            }
        });
    }

    // 채팅 메시지를 클라이언트의 채팅 패널에 전달
    public void receiveChatMessage(final String msg) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                ChattingFrame.deliverChatMessage(msg);
            }
        });
    }

    // 서버 연결이 끊겼다는 정보를 클라이언트에게 전달
    public void onDisconnected(final IOException e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                System.out.println("[서버 연결 종료] " + e.getMessage());
                JOptionPane.showMessageDialog(
                        ClientMenuFrame.this,
                        "서버와의 연결이 종료되었습니다.",
                        "연결 종료",
                        JOptionPane.WARNING_MESSAGE
                );
            }
        });
    }

    // ClientNetworkInterface 구현 끝
    

    // 다른 패널(친구목록, 채팅창 등)에서 서버로 명령을 보내고 싶을 때 사용
    public void sendToServer(String msg) {
        if (network != null) {
            network.sendToServer(msg);
        }
    }
    
    // 현재 접속자 목록 반환 (채팅 패널에서 새 대화방 만들 때 사용)
    public List<String> getCurrentUserList() {
        return Collections.unmodifiableList(currentUserList);
    }


    // 화면 전환
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


    public String getUsername() { return username; }
    public String getIp() { return ip_addr; }
    public String getPort() { return port_no; }

}
