import java.awt.Point;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;

public class ChattingFrame extends JFrame {

    // 현재 열려 있는 채팅방들 (key: 방 인원 문자열)
    private static final Map<String, ChattingFrame> OPENROOMS = new HashMap<>();

    private final ChattingPanel chatpanel;
    private final String roomKey;

    private static String makeRoomKey(String membersString) {
        // 멤버 순서를 그대로 key로 사용 (membersString 통일이 중요)
        return membersString.trim();
    }
    
    private static String makeRoomTitle(String membersString) {
        String[] names = membersString.trim().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String n : names) {
            n = n.trim();
            if (n.isEmpty()) continue;
            if (sb.length() > 0) sb.append(", ");
            sb.append(n);
        }
        return sb.toString();   // 예) "ss, ff, dd"
    }

    /**
     * 채팅방 열기 (외부에서 new 대신 이걸 호출)
     * 이미 같은 membersString 방이 열려 있으면 아무 동작 안 함.
     */
    public static void openRoom(ClientMenuFrame parentFrame, String username, String groupMembers, String creatorName) {

        String key = makeRoomKey(groupMembers);

        ChattingFrame existing = OPENROOMS.get(key);

        if (existing != null && existing.isDisplayable()) {
            // 이미 열려 있으면 새로 만들지 않고 그냥 앞으로 가져오기
            existing.toFront();
            existing.requestFocus();
            return;
        }

        ChattingFrame frame = new ChattingFrame(parentFrame, username, groupMembers, creatorName, key);
        OPENROOMS.put(key, frame);
        frame.setVisible(true);
    }

    // 실제 생성자 (외부에서는 openRoom으로만 호출)
    private ChattingFrame(ClientMenuFrame parentFrame, String username, String groupMembers, String creatorName, String roomKey) {

        this.roomKey = roomKey;

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(0, 0, 380, 560);
        setResizable(false);
        
        setTitle(makeRoomTitle(groupMembers));

        this.chatpanel = new ChattingPanel(parentFrame, username, groupMembers, creatorName);
        add(chatpanel);

        // 창이 닫힐 때 맵에서 제거
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                OPENROOMS.remove(ChattingFrame.this.roomKey);
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

    // 서버에서 받은 일반 채팅 메시지를 열려있는 모든 채팅창으로 전달
    public static void deliverChatMessage(String msg) {
        for (ChattingFrame f : OPENROOMS.values()) {
            f.chatpanel.onReceiveChatMessage(msg);
        }
    }
}
