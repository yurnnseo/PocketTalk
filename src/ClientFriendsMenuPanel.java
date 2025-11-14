// 친구 목록 패널
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ClientFriendsMenuPanel extends JPanel {
	private ImageIcon metaicon, metaicon2, chaticon, chaticon2;
    private JButton metabutton, chatbutton;
	private Image backgroundImg;
    private JLabel friendsLabel;
    private FontSource fontSource = new FontSource("/IM_Hyemin-Bold.ttf"); // 폰트
    
    private final String profileImagePath = "/Images/defaultprofileimage.png";

    public ClientFriendsMenuPanel(ClientMenuFrame parentFrame, String username, String ip_addr, String port_no) {
        setLayout(null);
        setBackground(Color.decode("#F9F9F9"));

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
        
        ProfileHeaderView header = new ProfileHeaderView(username, profileImagePath, 50, 50, ProfileHeaderView.Orientation.HORIZONTAL);
        header.setBounds(95, 80, header.getPreferredSize().width, header.getPreferredSize().height);
        add(header);

       
        // 프로필 버튼 클릭 시: 프로필 편집 프레임 띄우기
        header.getProfileButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MyProfileViewFrame pef = new MyProfileViewFrame(ClientFriendsMenuPanel.this, username, ip_addr, port_no, profileImagePath);
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
