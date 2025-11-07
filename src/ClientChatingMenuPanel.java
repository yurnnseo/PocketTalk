//채팅 목록 보여주는 패널
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ClientChatingMenuPanel extends JPanel {
    private Image backgroundImg;
    private ImageIcon metaicon, metaicon2, chaticon, chaticon2;
    private JButton metabutton, chatbutton;

    public ClientChatingMenuPanel(ClientMenuFrame parentFrame, String username, String ip_addr, String port_no) {
        setLayout(null);

        // 배경 이미지 로드
        backgroundImg = new ImageIcon(getClass().getResource("/chatingbackscreen.png")).getImage();

        // 아이콘
        metaicon  = new ImageIcon(getClass().getResource("/metaIcon.png"));
        metaicon2 = new ImageIcon(getClass().getResource("/metaIcon2.png"));
        chaticon  = new ImageIcon(getClass().getResource("/chatIcon.png"));
        chaticon2 = new ImageIcon(getClass().getResource("/chatIcon2.png"));

        // 버튼 생성
        metabutton = makeButton(metaicon2, 23, 40);
        chatbutton = makeButton(chaticon2, 23, 120);
        add(metabutton);
        add(chatbutton);

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
