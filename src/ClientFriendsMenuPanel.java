import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

//친구 목록 보여주는 패널
public class ClientFriendsMenuPanel extends JPanel {

    private final ClientMenuFrame parentFrame; // 메인 프레임 (화면 전환, 서버 통신할 때 필요함.)

    private ImageIcon metaicon, metaicon2, chaticon, chaticon2;
    private JButton metabutton, chatbutton;
    private JLabel friendsLabel;
    private FriendsListPanel friendsListPanel; // 실제 친구들 목록을 보여주는 패널

    // 내 프로필 정보
    private String profileImagePath;
    private ProfileHeaderView myHeader;
    private String myCurrentName;
    private String myCurrentStatusMessage = "";

    public ClientFriendsMenuPanel(ClientMenuFrame parentFrame, String username, String ip_addr, String port_no, String profileImagePath) {
        this.parentFrame = parentFrame;
        this.myCurrentName = username;

        setLayout(null);
        setBackground(Color.decode("#F9F9F9"));

        // 프로필 이미지 경로 지정 (처음엔 기본 프로필)
        if (profileImagePath == null || profileImagePath.isEmpty()) {
            this.profileImagePath = "/Images/defaultprofileimage.png";
        } 
        else {
            this.profileImagePath = profileImagePath;
        }

        // 친구 목록을 담아둘 패널 생성 (JScrollPane으로)
        friendsListPanel = new FriendsListPanel(this.myCurrentName);

        JScrollPane scrollPane = new JScrollPane(friendsListPanel);
        scrollPane.setBounds(65, 151, 295, 375);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        add(scrollPane);

        // sidebar로 아이콘 이미지 불러오고 버튼 추가하기
        metaicon  = new ImageIcon(getClass().getResource("/Images/metaIcon.png"));
        metaicon2 = new ImageIcon(getClass().getResource("/Images/metaIcon2.png"));
        chaticon  = new ImageIcon(getClass().getResource("/Images/chatIcon.png"));
        chaticon2 = new ImageIcon(getClass().getResource("/Images/chatIcon2.png"));

        metabutton = makeButton(metaicon, 13, 40);
        chatbutton = makeButton(chaticon, 13, 120);

        add(metabutton);
        add(chatbutton);

        friendsLabel = new JLabel("친구", SwingConstants.LEFT);
        friendsLabel.setFont(FontSource.get(20f));
        friendsLabel.setForeground(Color.BLACK);
        friendsLabel.setBounds(97, 20, 50, 50);
        add(friendsLabel);

        // 내 프로필 (이름 + 상태메시지 + 프로필 이미지)
        myHeader = new ProfileHeaderView(this.myCurrentName, this.myCurrentStatusMessage, this.profileImagePath, 50, 50, ProfileHeaderView.Orientation.HORIZONTAL);
        myHeader.setBounds(95, 80, myHeader.getPreferredSize().width, myHeader.getPreferredSize().height);
        add(myHeader);

        // 내 프로필 클릭 시 내 프로필 편집 창(MyProfileViewFrame) 띄우기
        myHeader.getProfileButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	MyProfileViewFrame pef = new MyProfileViewFrame(
                        ClientFriendsMenuPanel.this,             
                        parentFrame,                             
                        myCurrentName,                            
                        ip_addr,                                  
                        port_no,                                  
                        ClientFriendsMenuPanel.this.profileImagePath, 
                        myCurrentStatusMessage                    
                );
                pef.setVisible(true);
            }
        });

        // sidebar에서 채팅 아이콘을 누르면 채팅 화면으로 이동
        chatbutton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parentFrame.showChattingMenu();
            }
        });
    }
    
    // 서버에서 /list 명령을 받았을 때 친구 목록 갱신
    public void updateFriendList(List<String> usernames) {
        if (friendsListPanel != null) {
            friendsListPanel.updateOnlineList(usernames);
        }
    }

    // 서버에서 /profile 명령을 받았을 때 친구 프로필 정보 업데이트
    public void updateFriendProfileFromServer(String name, String imagePath, String statusMsg) {

        if (name != null) name = name.trim();
        if (imagePath != null) imagePath = imagePath.trim();
        if (statusMsg != null) statusMsg = statusMsg.trim();

        // 친구 목록 (FriendsListPanel의 profiles 맵)에 저장
        if (friendsListPanel != null) {
            friendsListPanel.updateFriendProfile(name, imagePath, statusMsg);
        }

        // 만약 이 정보가 자기 자신이면 내 프로필도 같이 바꿈
        if (name != null && name.equals(myCurrentName)) {
            this.myCurrentStatusMessage = (statusMsg == null) ? "" : statusMsg;
            this.profileImagePath = (imagePath == null || imagePath.isEmpty()) ? "/Images/defaultprofileimage.png" : imagePath;

            if (myHeader != null) {
                myHeader.setUserName(myCurrentName);
                myHeader.setMessage(myCurrentStatusMessage);
                myHeader.setProfileImage(this.profileImagePath);
                myHeader.revalidate();
                myHeader.repaint();
            }
        }
    }

    // 내가 직접 내 프로필을 수정했을 때 화면 갱신하는 메소드
    public void updateMyProfileName(String newName, String newStatus) {
        if (newName != null && !newName.isEmpty()) {
            this.myCurrentName = newName.trim();
        }
        if (newStatus != null) {
            this.myCurrentStatusMessage = newStatus;
        }

        if (myHeader != null) {
            myHeader.setUserName(this.myCurrentName);
            myHeader.setMessage(this.myCurrentStatusMessage);
        }

        if (friendsListPanel != null) {
            friendsListPanel.setMyName(this.myCurrentName);
        }
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

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.decode("#E3D6F0"));
        g.fillRect(0, 0, 75, getHeight());

        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(75, 150, getWidth() - 60, 1);
    }
}
