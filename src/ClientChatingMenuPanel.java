//채팅 목록 보여주는 패널
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ClientChatingMenuPanel extends JPanel {
    private Image backgroundImg;
    private ImageIcon metaicon, metaicon2, chaticon, chaticon2, pluschaticon;
    private JButton metabutton, chatbutton, newchatbutton;

    public ClientChatingMenuPanel(ClientMenuFrame parentFrame, String username, String ip_addr, String port_no) {
        setLayout(null);

        // 배경 이미지 로드
        backgroundImg = new ImageIcon(getClass().getResource("/chatingbackscreen1.png")).getImage();

        // 아이콘
        metaicon  = new ImageIcon(getClass().getResource("/metaIcon.png"));
        metaicon2 = new ImageIcon(getClass().getResource("/metaIcon2.png"));
        chaticon  = new ImageIcon(getClass().getResource("/chatIcon.png"));
        chaticon2 = new ImageIcon(getClass().getResource("/chatIcon2.png"));
        pluschaticon = new ImageIcon(getClass().getResource("/pluschaticon.png"));

        // 버튼 생성
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

        // 채팅 버튼 (현재 화면 유지)
        /*chatbutton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                metabutton.setIcon(metaicon2);
                chatbutton.setIcon(chaticon2);
            }
        });*/
        
        newchatbutton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	//대화 상대 고르는 팝업창띄우고 고르면 그 사람이랑 대화하는 뷰 떠야함
            	//ChoosePerson cp = new ChoosePerson();
            	//cp.setVisible(true);
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
