import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class ChatRoomListPanel extends JPanel {

    private static final String DEFAULT_PROFILE_IMAGE = "/Images/defaultprofileimage.png";
    private FontSource fontSource = new FontSource("/IM_Hyemin-Bold.ttf");

    private final ClientChatingMenuPanel parentPanel;

    // 채팅방 정보
    private static class ChatRoom {
        String creatorName;
        String membersString;

        ChatRoom(String creatorName, String membersString) {
            this.creatorName = creatorName;
            this.membersString = membersString;
        }
    }

    private final List<ChatRoom> rooms = new ArrayList<>();

    public ChatRoomListPanel(ClientChatingMenuPanel parentPanel) {
        this.parentPanel = parentPanel;
        setLayout(null);
        setOpaque(false);
    }

    public void addRoom(String creatorName, String membersString) {
        rooms.add(new ChatRoom(creatorName, membersString));
        refreshView();
    }
    
    private String makeRoomTitle(String membersString) {
        // "A B C" → ["A","B","C"]
        String[] names = membersString.trim().split("\\s+");

        if (names.length == 1) return names[0];

        // 2명일 때: A, B
        if (names.length == 2) {
            return names[0] + ", " + names[1];
        }

        // 3명 이상일 때: A, B, C 형태
        return String.join(", ", names);
    }


    private void refreshView() {
        removeAll();

        int y = 10;

        for (ChatRoom room : rooms) {
            
        	// 상태메시지 대신 비워둠 (필요하면 "사람 수" 같은 거 넣어도 됨)
        	String roomTitle = makeRoomTitle(room.membersString);
        	// 방 하나를 프로필 + 이름 형태로 표현
        	ProfileHeaderView header = new ProfileHeaderView(roomTitle, "", DEFAULT_PROFILE_IMAGE, 50, 50, ProfileHeaderView.Orientation.HORIZONTAL);
        	
            header.setBounds(15, y, header.getPreferredSize().width, header.getPreferredSize().height);
            add(header);

            MouseAdapter clickListener = new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    // 더블클릭일 때만 열기
                    if (e.getClickCount() == 2) {
                        parentPanel.openChatRoom(room.creatorName, room.membersString);
                    }
                }
            };

            header.addMouseListener(clickListener);
            for (Component c : header.getComponents()) {
                c.addMouseListener(clickListener);
            }

            y += header.getPreferredSize().height + 15;
        }

        int width = 260;
        int height = Math.max(y, 300);
        setPreferredSize(new Dimension(width, height));

        revalidate();
        repaint();
    }
}
