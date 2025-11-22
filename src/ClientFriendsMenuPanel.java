import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class ClientFriendsMenuPanel extends JPanel {

    private final ClientMenuFrame parentFrame;

    private ImageIcon metaicon, metaicon2, chaticon, chaticon2;
    private JButton metabutton, chatbutton;
    private Image backgroundImg;
    private JLabel friendsLabel;
    private FriendsListPanel friendsListPanel;

    private FontSource fontSource = new FontSource("/IM_Hyemin-Bold.ttf");

    private String profileImagePath;
    private ProfileHeaderView myHeader;
    private String myCurrentName;
    private String myCurrentStatusMessage = "";

    public ClientFriendsMenuPanel(ClientMenuFrame parentFrame,
                                  String username,
                                  String ip_addr,
                                  String port_no,
                                  String profileImagePath) {

        this.parentFrame = parentFrame;

        setLayout(null);
        setBackground(Color.decode("#F9F9F9"));

        this.myCurrentName = username;

        if (profileImagePath == null || profileImagePath.isEmpty()) {
            this.profileImagePath = "/Images/defaultprofileimage.png";
        } else {
            this.profileImagePath = profileImagePath;
        }

        // ★ 딱 1개만 생성되는 FriendsListPanel
        friendsListPanel = new FriendsListPanel(this.myCurrentName);

        JScrollPane scrollPane = new JScrollPane(friendsListPanel);
        scrollPane.setBounds(65, 151, 295, 375);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        add(scrollPane);

        metaicon  = new ImageIcon(getClass().getResource("/Images/metaIcon.png"));
        metaicon2 = new ImageIcon(getClass().getResource("/Images/metaIcon2.png"));
        chaticon  = new ImageIcon(getClass().getResource("/Images/chatIcon.png"));
        chaticon2 = new ImageIcon(getClass().getResource("/Images/chatIcon2.png"));

        metabutton = makeButton(metaicon, 13, 40);
        chatbutton = makeButton(chaticon, 13, 120);

        add(metabutton);
        add(chatbutton);

        friendsLabel = new JLabel("친구", SwingConstants.LEFT);
        friendsLabel.setFont(fontSource.getFont(20f));
        friendsLabel.setForeground(Color.BLACK);
        friendsLabel.setBounds(97, 20, 50, 50);
        add(friendsLabel);

        myHeader = new ProfileHeaderView(
                this.myCurrentName,
                this.myCurrentStatusMessage,
                this.profileImagePath,
                50, 50,
                ProfileHeaderView.Orientation.HORIZONTAL
        );
        myHeader.setBounds(95, 80,
                myHeader.getPreferredSize().width,
                myHeader.getPreferredSize().height);
        add(myHeader);

        myHeader.getProfileButton().addActionListener(e -> {
            MyProfileViewFrame pef = new MyProfileViewFrame(
                    ClientFriendsMenuPanel.this,
                    myCurrentName,
                    ip_addr,
                    port_no,
                    ClientFriendsMenuPanel.this.profileImagePath,
                    myCurrentStatusMessage,
                    parentFrame.getDataOutputStream()
            );
            pef.setVisible(true);
        });

        chatbutton.addActionListener(e -> parentFrame.showChattingMenu());
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

    // /list 수신 시
    public void updateFriendList(List<String> usernames) {
        if (friendsListPanel != null) {
            friendsListPanel.updateOnlineList(usernames);
        }
    }

    // /profile 수신 시
    public void updateFriendProfileFromServer(String name, String imagePath, String statusMsg) {

       // System.out.println("[ClientFriendsMenuPanel] updateFriendProfileFromServer name="
        //        + name + ", status=" + statusMsg);

        if (name != null) name = name.trim();
        if (imagePath != null) imagePath = imagePath.trim();
        if (statusMsg != null) statusMsg = statusMsg.trim();

        // 1) 친구 목록(= FriendsListPanel의 profiles 맵)에 무조건 저장
        if (friendsListPanel != null) {
            friendsListPanel.updateFriendProfile(name, imagePath, statusMsg);
        }

        // 2) 그 중에서 "나 자신"이면, 위쪽 내 프로필 헤더도 같이 갱신
        if (name != null && name.equals(myCurrentName)) {
            this.myCurrentStatusMessage = (statusMsg == null) ? "" : statusMsg;

            if (myHeader != null) {
                myHeader.setUserName(myCurrentName);
                myHeader.setMessage(myCurrentStatusMessage);
                myHeader.revalidate();
                myHeader.repaint();
            }
        }
    }

    // 내 프로필(이름, 상태메시지) 변경 시 – 상단 헤더/UI용
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
