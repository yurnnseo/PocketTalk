//친구 목록 패널
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ClientFriendsMenuPanel extends JPanel {
	private Image backgroundImg;
    private ImageIcon metaicon, metaicon2, chaticon, chaticon2;
    private JButton metabutton, chatbutton, edit;
    private JLabel profilePicLabel, nameLabel;
    
    public ClientFriendsMenuPanel(ClientMenuFrame parentFrame, String username, String ip_addr, String port_no) {
        setLayout(null);

        // 배경 이미지 로드
        backgroundImg = new ImageIcon(getClass().getResource("/friendsbackscreen1.png")).getImage();

        // 아이콘
        metaicon  = new ImageIcon(getClass().getResource("/metaIcon.png"));
        metaicon2 = new ImageIcon(getClass().getResource("/metaIcon2.png"));
        chaticon  = new ImageIcon(getClass().getResource("/chatIcon.png"));
        chaticon2 = new ImageIcon(getClass().getResource("/chatIcon2.png"));
        
        //임시로 버튼으로 만듦
        edit = new JButton("프로필 수정");
        edit.setBounds(100,120,70,30);
        
        // 버튼 생성
        metabutton = makeButton(metaicon, 13, 40);
        chatbutton = makeButton(chaticon, 13, 120);
        
        add(metabutton);
        add(chatbutton);
        add(edit);
        
        // 친구 버튼: 자기 화면 유지
        //화면 유지라서 버튼 누르는 동작 필요없어서 주석처리함!
        /*metabutton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //metabutton.setIcon(metaicon);
                chatbutton.setIcon(chaticon);
            }
        });*/

        // 채팅 버튼: 화면 전환
        chatbutton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                metabutton.setIcon(metaicon);
                chatbutton.setIcon(chaticon2);
                parentFrame.showChattingMenu();
            }
        });
        
        edit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EditProfile ep = new EditProfile(parentFrame, username, ip_addr, port_no);
                parentFrame.showeditprofile();
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
        g.drawImage(backgroundImg, 0, 0, getWidth(), getHeight(), this);
    }
}
