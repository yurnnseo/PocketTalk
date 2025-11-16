// 친구 목록 패널
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class ClientFriendsMenuPanel extends JPanel {
	private ImageIcon metaicon, metaicon2, chaticon, chaticon2;
    private JButton metabutton, chatbutton;
	private Image backgroundImg;
    private JLabel friendsLabel;
    private FriendsListPanel friendsListPanel;
    private FontSource fontSource = new FontSource("/IM_Hyemin-Bold.ttf"); // 폰트
    private String profileImagePath;
    private ProfileHeaderView myHeader; 
    private String myCurrentName;
    private String myCurrentStatusM="";
   
    
    public ClientFriendsMenuPanel(ClientMenuFrame parentFrame, String username, String ip_addr, String port_no, String profileImagePath) {
        setLayout(null);
        setBackground(Color.decode("#F9F9F9"));
        
        this.myCurrentName = username; //초기값을 현재 정보 변수에 저장 
        
        if (profileImagePath == null || profileImagePath.isEmpty()) {
            this.profileImagePath = "/Images/defaultprofileimage.png";
        } 
        else {
            this.profileImagePath = profileImagePath;
        }
        
        friendsListPanel = new FriendsListPanel(username); //본인은 제외하기 위해 내이름 전달
        
        //스크롤팬 커스텀
        JScrollPane scrollPane = new JScrollPane(friendsListPanel);
        int contentX = 75;
        int contentY = 151; 
        int contentWidth = 295; 
        int contentHeight = 375; 
        scrollPane.setBounds(contentX, contentY, contentWidth, contentHeight);
        scrollPane.getViewport().setOpaque(false); //배경 투명화
        scrollPane.setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder()); //테두리 투명화
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS); //스크롤바 보이게
        add(scrollPane);
        

        // 아이콘
        metaicon  = new ImageIcon(getClass().getResource("/Images/metaIcon.png"));
        metaicon2 = new ImageIcon(getClass().getResource("/Images/metaIcon2.png"));
        chaticon  = new ImageIcon(getClass().getResource("/Images/chatIcon.png"));
        chaticon2 = new ImageIcon(getClass().getResource("/Images/chatIcon2.png"));

        // 버튼 생성
        metabutton = makeButton(metaicon, 13, 40);
        chatbutton = makeButton(chaticon, 13, 120);

        
        add(metabutton);
        add(chatbutton);
        
        friendsLabel = new JLabel("친구", SwingConstants.LEFT);
        friendsLabel.setFont(fontSource.getFont(20f));
        friendsLabel.setForeground(Color.BLACK);
        friendsLabel.setBounds(97, 20, 50, 50);
        add(friendsLabel);
        
        // 필드에 저장해두기
        myHeader = new ProfileHeaderView(this.myCurrentName, this.profileImagePath, 50, 50, ProfileHeaderView.Orientation.HORIZONTAL);
        myHeader.setBounds(95, 80, myHeader.getPreferredSize().width, myHeader.getPreferredSize().height);
        add(myHeader);

        // 프로필 버튼 클릭 시: 내 프로필 보기 프레임 띄우기
        myHeader.getProfileButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MyProfileViewFrame pef = new MyProfileViewFrame(ClientFriendsMenuPanel.this, myCurrentName, ip_addr, port_no, ClientFriendsMenuPanel.this.profileImagePath, myCurrentStatusM);
                pef.setVisible(true);
            }
        });
        
        // 채팅 버튼: 화면 전환
        chatbutton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                metabutton.setIcon(metaicon);
                chatbutton.setIcon(chaticon2);
                parentFrame.showChattingMenu();
            }
        });
        
    }

    //서버에서 받은 목록으로 친구 패널 업데이트
    public void updateFriendList(List<String> usernames) {
    	if(friendsListPanel != null) {
    		friendsListPanel.updateList(usernames);
    	}
    }
    
    // 내 이름,상메 바뀌었을 때 호출할 힘수
    public void updateMyProfileName(String newName, String newStatus) {
    	if (newName != null && !newName.isEmpty()) {
            this.myCurrentName = newName;
        }
       if (newStatus != null) {
            this.myCurrentStatusM = newStatus;
        }

        if (myHeader != null) {
            myHeader.setUserName(this.myCurrentName);
            
        }
    }
    
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
        g.drawImage(backgroundImg, 0, 0, getWidth(), getHeight(), this);
        
        g.setColor(Color.LIGHT_GRAY); 
        g.fillRect(75, 150, getWidth() - 60, 1);
    }
    
    
}
