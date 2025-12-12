import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

// ChatingMenuPanel에서 채팅방 목록을 보여주는 패널

public class ChatRoomListPanel extends JPanel {

	// 기본 프로필 이미지 경로
    private static final String DEFAULT_PROFILE_IMAGE = "/Images/defaultprofileimage.png";

    private final ClientChatingMenuPanel parentPanel; // 더블클릭 시 openChatRoom() 호출할 때 사용

    private final List<ChatRoomInfo> rooms = new ArrayList<>(); // 화면에 표시할 채팅방 정보들을 담는 리스트

    public ChatRoomListPanel(ClientChatingMenuPanel parentPanel) {
        this.parentPanel = parentPanel;
        setLayout(null);
        setOpaque(false);
    }
    
    // 새 채팅방을 채팅 목록에 추가할 때 호출
    public void addRoom(String roomId, String creatorName, String membersString) {

        String[] arr = membersString.trim().split("\\s+");
        List<String> members = new ArrayList<>();
        for (String n : arr) {
            n = n.trim();
            if (!n.isEmpty()) members.add(n);
        }

        // 채팅방 정보가 담긴 객체를 채팅 목록 리스트에 추가
        rooms.add(new ChatRoomInfo(roomId, creatorName, members));
        refreshView(); // 화면 갱신 함수
    }


    // rooms 리스트 내용으로 로 화면 갱신하는 메소드
    private void refreshView() {
        removeAll(); // 기존 컴포넌트 제거

        int y = 10;

        // rooms 리스트에 들어있는 각 방을 ProfileHeaderView 형태로 만듦.
        for (ChatRoomInfo room : rooms) {

            String membersString = String.join(" ", room.getMembers());

            String roomTitle = UIComponentZip.makeChatRoomTitle(membersString);

            // 프로필 + 채팅방 멤버로 붙임.
            ProfileHeaderView header = new ProfileHeaderView(roomTitle, "", DEFAULT_PROFILE_IMAGE, 50, 50, ProfileHeaderView.Orientation.HORIZONTAL);
            header.setBounds(15, y, header.getPreferredSize().width, header.getPreferredSize().height);
            add(header);

            // 채팅방 더블클릭 시 채팅방 입장
            MouseAdapter clickListener = new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                    	// 부모 패널 (ClientChatingMenuPanel)에게 openChatRoom() 함수를 불러 채팅창을 엶.
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
