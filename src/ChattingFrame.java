import java.awt.Point;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;

public class ChattingFrame extends JFrame {

    // key: roomId
    private static final Map<String, ChattingFrame> OPENROOMS = new HashMap<>();

    private final ChattingPanel chatpanel;
    private final String roomKey;   // = roomId
    private final String roomId;    // 방 ID

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
     * 이미 같은 roomId 방이 열려 있으면 그 창만 앞으로 가져옴.
     */
    public static void openRoom(ClientMenuFrame parentFrame,
                                String username,
                                String roomId,
                                String groupMembers,
                                String creatorName) {

        String key = roomId; // key = roomId

        ChattingFrame existing = OPENROOMS.get(key);
        if (existing != null && existing.isDisplayable()) {
            existing.toFront();
            existing.requestFocus();
            return;
        }

        ChattingFrame frame = new ChattingFrame(parentFrame, username, roomId, groupMembers, creatorName, key);
        OPENROOMS.put(key, frame);
        frame.setVisible(true);

        // ★ 여기서 이전 로그 요청
        parentFrame.sendToServer("/loadlog " + roomId);
    }

    // 실제 생성자 (외부에서는 openRoom으로만 호출)
    private ChattingFrame(ClientMenuFrame parentFrame,
                          String username,
                          String roomId,
                          String groupMembers,
                          String creatorName,
                          String roomKey) {

        this.roomId = roomId;
        this.roomKey = roomKey;

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(0, 0, 380, 560);
        setResizable(false);

        setTitle(makeRoomTitle(groupMembers));

        this.chatpanel = new ChattingPanel(parentFrame, username, roomId, groupMembers, creatorName);
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

    /**
     * 서버에서 받은 채팅 메시지 전달
     * 형식: "/msg roomId 내용..."
     */
    public static void deliverChatMessage(String msg) {

        // 방 채팅 형식이면 roomId 기준으로 분배
        if (msg.startsWith("/msg ")) {
            String body = msg.substring("/msg ".length()).trim();
            int firstSpace = body.indexOf(' ');
            if (firstSpace <= 0) return;

            String roomId = body.substring(0, firstSpace);
            String text   = body.substring(firstSpace + 1); // "유저 : 내용"

            ChattingFrame frame = OPENROOMS.get(roomId);
            if (frame != null) {
                frame.chatpanel.onReceiveChatMessage(text);
            }
            return;
        }

        // 그 외(예전 broadcast용 메시지)는 필요하다면 모든 창에 뿌릴 수도 있음
        for (ChattingFrame f : OPENROOMS.values()) {
            f.chatpanel.onReceiveChatMessage(msg);
        }
    }
}
