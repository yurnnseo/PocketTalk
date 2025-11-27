import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class ChatRoomListPanel extends JPanel {

    private static final String DEFAULT_PROFILE_IMAGE = "/Images/defaultprofileimage.png";

    private final ClientChatingMenuPanel parentPanel;

    private final List<ChatRoomInfo> rooms = new ArrayList<>();

    public ChatRoomListPanel(ClientChatingMenuPanel parentPanel) {
        this.parentPanel = parentPanel;
        setLayout(null);
        setOpaque(false);
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
    
    public void addRoom(String roomId, String creatorName, String membersString) {
        // "A B C" -> ["A","B","C"]
        String[] arr = membersString.trim().split("\\s+");
        List<String> members = new ArrayList<>();
        for (String n : arr) {
            n = n.trim();
            if (!n.isEmpty()) members.add(n);
        }

        rooms.add(new ChatRoomInfo(roomId, creatorName, members));
        refreshView();
    }



    private void refreshView() {
        removeAll();

        int y = 10;

        for (ChatRoomInfo room : rooms) {
            // List<String> -> "A B C" 형태로 합치기
            String membersString = String.join(" ", room.getMembers());

            String roomTitle = makeRoomTitle(membersString);

            ProfileHeaderView header = new ProfileHeaderView(
                    roomTitle,
                    "",
                    DEFAULT_PROFILE_IMAGE,
                    50, 50,
                    ProfileHeaderView.Orientation.HORIZONTAL
            );

            header.setBounds(15, y, header.getPreferredSize().width, header.getPreferredSize().height);
            add(header);

            MouseAdapter clickListener = new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        parentPanel.openChatRoom(room.getRoomId(), room.getCreator(), membersString);
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
