import java.awt.Point;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JFrame;

// 실제 채팅창 하나를 나타내는 프레임
public class ChattingFrame extends JFrame {

    // key가 roomId, value가 그 채팅방의 ChattingFrame으로 현재 열려있는 채팅방들을 저장함.
    private static final Map<String, ChattingFrame> OPENROOMS = new HashMap<>();

    private final ChattingPanel chatPanel;  
    private final String roomId;   

    // 채팅방 여는 메소드 (같은 채팅방이 여러 번 생성되는 걸 방지하기 위한 메소드)
    public static void openRoom(ClientMenuFrame parentFrame, String username, String roomId, String groupMembers, String creatorName) {

    	// 이미 같은 roomId 창이 열려있으면 새로 안 만들고 앞으로 가져옴
        ChattingFrame existing = OPENROOMS.get(roomId);
        if (existing != null && existing.isDisplayable()) {
            existing.toFront();
            existing.requestFocus();
            return;
        }

        // 새 채팅 프레임 생성 후 맵에 넣음.
        ChattingFrame frame = new ChattingFrame(parentFrame, username, roomId, groupMembers, creatorName);
        OPENROOMS.put(roomId, frame);
        frame.setVisible(true);

        // 채팅방 열자마자 예전 채팅 로그 요청
        parentFrame.sendToServer("/loadlog " + roomId);
    }

    // 실제 생성자 (외부에서는 openRoom으로 호출)
    private ChattingFrame(ClientMenuFrame parentFrame, String username, String roomId, String groupMembers, String creatorName) {

        this.roomId = roomId;

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(0, 0, 380, 560);
        setResizable(false);

        setTitle(UIComponentZip.makeChatRoomTitle(groupMembers)); // 채팅방 제목

        // 실제 채팅 내용이 담긴 패널을 프레임에 붙임
        this.chatPanel = new ChattingPanel(parentFrame, username, roomId, groupMembers, creatorName);
        add(chatPanel);

        // 창이 닫힐 때 OPENROOMS 맵에서 제거
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                OPENROOMS.remove(ChattingFrame.this.roomId);
            }
        });

        
        try {
            Point parentPos = parentFrame.getLocationOnScreen();

            int offsetX = 40;
            int offsetY = 40;

            setLocation(parentPos.x + offsetX, parentPos.y + offsetY);

        } catch (Exception ex) {
            setLocation(200, 200);
        }
    }

    
    // 서버에서 받은 채팅 메시지를 그 roomId 채팅창으로 보내주는 메소드
    public static void deliverChatMessage(String msg) {

    	// "/msg roomId (메세지 내용)" 이면 roomId로 채팅방을 찾고 그 방에 메시지 전달
        if (msg.startsWith("/msg ")) {
            String body = msg.substring("/msg ".length()).trim();

            int firstSpace = body.indexOf(' ');
            if (firstSpace <= 0) return;

            String roomId = body.substring(0, firstSpace);
            String text  = body.substring(firstSpace + 1); // "이름 : 내용" 

            ChattingFrame frame = OPENROOMS.get(roomId);
            if (frame != null) {
                frame.chatPanel.onReceiveChatMessage(text);
            }
            return;
        }

        // 혹시 roomId 없는 메시지면 (시스템 메시지 등) 열린 방들에 전체 전달
        for (ChattingFrame f : OPENROOMS.values()) {
            f.chatPanel.onReceiveChatMessage(msg);
        }
    }
}