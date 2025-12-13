import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

//채팅 목록 보여주는 패널
public class ClientChatingMenuPanel extends JPanel {

	private final ClientMenuFrame parentFrame; // 메인 프레임 (화면 전환, 서버 통신할 때 필요함.)
	
    private ImageIcon metaicon, metaicon2, chaticon, chaticon2, pluschaticon;
    private JButton metabutton, chatbutton, newchatbutton;
    private JLabel chatingLabel;
    private ChatRoomListPanel roomListPanel; // 채팅방 목록을 보여주는 패널
    
    // 내 정보
    private final String username;

    
    public ClientChatingMenuPanel(ClientMenuFrame parentFrame, String username) {
    	this.parentFrame = parentFrame;
        this.username = username;
    	
    	setLayout(null);
        setBackground(Color.decode("#F9F9F9"));

        chatingLabel = new JLabel("채팅", SwingConstants.LEFT);
        chatingLabel.setFont(FontSource.get(20f));
        chatingLabel.setForeground(Color.BLACK);
        chatingLabel.setBounds(97, 20, 50, 50);
        add(chatingLabel);
        
    	// sidebar로 아이콘 이미지 불러오고 버튼 추가하기
        metaicon  = new ImageIcon(getClass().getResource("/Images/metaIcon.png"));
        metaicon2 = new ImageIcon(getClass().getResource("/Images/metaIcon2.png"));
        chaticon  = new ImageIcon(getClass().getResource("/Images/chatIcon.png"));
        chaticon2 = new ImageIcon(getClass().getResource("/Images/chatIcon2.png"));
        pluschaticon = new ImageIcon(getClass().getResource("/Images/pluschaticon.png"));
        
        metabutton = makeButton(metaicon2, 13, 40);
        chatbutton = makeButton(chaticon2, 13, 120);
        newchatbutton = makeButton(pluschaticon, 310 ,30);
        
        add(metabutton);
        add(chatbutton);
        add(newchatbutton);
        
        // 채팅방 목록을 담아둘 패널 생성 (JScrollPane으로)
        roomListPanel = new ChatRoomListPanel(this);
        JScrollPane roomScroll = new JScrollPane(roomListPanel);
        roomScroll.setBounds(80, 80, 290, 430);
        roomScroll.getViewport().setOpaque(false);
        roomScroll.setOpaque(false);
        roomScroll.setBorder(BorderFactory.createEmptyBorder());
        roomScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        add(roomScroll);
        
        // sidebar에서 메타몽 아이콘을 누르면 채팅 화면으로 이동
        metabutton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	parentFrame.showFriendsMenu();
                
            }
        });
        
        // 새로운 채팅방 생성 버튼
        newchatbutton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	
            	// 서버에서 받은 접속자 목록을 가져와서 대화 상대를 선택하면 ChoosePerson 띄우기
            	List<String> userList = parentFrame.getCurrentUserList();
            	
                ChoosePerson cp = new ChoosePerson(parentFrame, username, userList);
                cp.setVisible(true);
            }
        });
    }
    
    // 서버에서 "/room ..." 메시지를 받았을 때 호출됨
    // 새로운 채팅방을 채팅방 목록에 추가하는 메소드
    public void addChatRoom(String roomId, String creatorName, String membersString) {
        if (roomListPanel != null) {
            roomListPanel.addRoom(roomId, creatorName, membersString);
        }
    }

    // 채팅방 더블클릭 시 채팅창을 여는 메소드
    public void openChatRoom(String roomId, String creatorName, String membersString) {
        ChattingFrame.openRoom(parentFrame, username, roomId, membersString, creatorName);
    }
    

    // 버튼을 만드는 공통 함수
    private JButton makeButton(ImageIcon icon, int x, int y) {
        JButton btn = new JButton(icon);
        btn.setBounds(x, y, icon.getIconWidth(), icon.getIconHeight());
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // 배경 직접 그리기
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.decode("#E3D6F0"));
        g.fillRect(0, 0, 75, getHeight());

    }
}