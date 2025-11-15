//채팅 목록 보여주는 패널
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ClientChatingMenuPanel extends JPanel {
    private Image backgroundImg;
    private ImageIcon metaicon, metaicon2, chaticon, chaticon2, pluschaticon;
    private JButton metabutton, chatbutton, newchatbutton;
    private JLabel chatingLabel;
    private FontSource fontSource = new FontSource("/IM_Hyemin-Bold.ttf"); // 폰트

    public ClientChatingMenuPanel(ClientMenuFrame parentFrame, String username, String ip_addr, String port_no) {
        setLayout(null);
        setBackground(Color.decode("#F9F9F9"));

        chatingLabel = new JLabel("채팅", SwingConstants.LEFT);
        chatingLabel.setFont(fontSource.getFont(20f));
        chatingLabel.setForeground(Color.BLACK);
        chatingLabel.setBounds(97, 20, 50, 50);
        add(chatingLabel);
        
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
        
        // 친구 목록으로 돌아가기
        metabutton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                metabutton.setIcon(metaicon);
                chatbutton.setIcon(chaticon);
                parentFrame.showFriendsMenu();
            }
        });
        
        newchatbutton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	//대화 상대 고르는 팝업창띄우고 고르면 그 사람이랑 대화하는 뷰 떠야함
            	ChoosePerson cp = new ChoosePerson();
            	cp.setVisible(true);
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
    }
}