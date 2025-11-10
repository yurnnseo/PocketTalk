// 친구 목록 패널
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ClientFriendsMenuPanel extends JPanel {
    private Image backgroundImg;
    private ImageIcon metaicon, metaicon2, chaticon, chaticon2;
    private JButton metabutton, chatbutton;

    // 프로필 아이콘/버튼, 이름 라벨
    private ImageIcon profileDefaultIcon;
    private JButton profileButton;
    private JLabel nameLabel;

    public ClientFriendsMenuPanel(ClientMenuFrame parentFrame, String username, String ip_addr, String port_no) {
        setLayout(null);

        // 배경 이미지 로드
        backgroundImg = new ImageIcon(getClass().getResource("/friendsbackscreen1.png")).getImage();
        System.out.println(getClass().getResource("/friendsbackscreen1.png"));

        // 아이콘
        metaicon  = new ImageIcon(getClass().getResource("/metaIcon.png"));
        metaicon2 = new ImageIcon(getClass().getResource("/metaIcon2.png"));
        chaticon  = new ImageIcon(getClass().getResource("/chatIcon.png"));
        chaticon2 = new ImageIcon(getClass().getResource("/chatIcon2.png"));

//        // 임시 버튼
//        editbutton = new JButton("프로필 수정");
//        editbutton.setBounds(100, 120, 100, 30);

        // 버튼 생성
        metabutton = makeButton(metaicon, 13, 40);
        chatbutton = makeButton(chaticon, 13, 120);

        // === 프로필 이미지 버튼 ===
        // 원본 아이콘 로드
        ImageIcon originalprofileIcon = new ImageIcon(getClass().getResource("/defaultprofileimage.png"));

        // 크기 조정 
        int newWidth = 50;  
        int newHeight = 50;

        // 이미지 스케일링
        Image scaledImage = originalprofileIcon.getImage().getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
        ImageIcon profileIcon = new ImageIcon(scaledImage);
        
        profileButton = new JButton(profileIcon);
        profileButton.setBounds(95, 80, newWidth, newHeight); // 위치/크기 조절
        profileButton.setContentAreaFilled(false);
        profileButton.setBorderPainted(false);
        profileButton.setFocusPainted(false);
        profileButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        profileButton.setToolTipText("프로필 편집 열기");
        
        nameLabel = new JLabel(username, SwingConstants.CENTER);
        nameLabel.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        nameLabel.setForeground(Color.BLACK);
        
     // 프로필 오른쪽으로 고정된 간격 띄우기
        int labelX = newWidth + 60; 
        int labelY = 80 + (newHeight / 2) - 10; 

        nameLabel.setBounds(labelX, labelY, 150, 25);

        // 클릭하면 새 프레임(ProfileEditFrame) 생성/표시
        profileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 필요한 값들 전달 가능 (username, ip_addr, port_no)
                ProfileEditFrame pef = new ProfileEditFrame(ClientFriendsMenuPanel.this, username, ip_addr, port_no);
                pef.setVisible(true);
            }
        });

        add(metabutton);
        add(chatbutton);
//        add(editbutton);
        add(profileButton); 
        add(nameLabel);
        
        // 채팅 버튼: 화면 전환
        chatbutton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                metabutton.setIcon(metaicon);
                chatbutton.setIcon(chaticon2);
                parentFrame.showChattingMenu();
            }
        });

//        // 기존 "프로필 수정" 임시 버튼: 새 프레임으로 열기
//        editbutton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                // 부모 체인: ProfileEditFrame의 부모는 이 패널(ClientFriendsMenuPanel)
//                ProfileEditFrame pef = new ProfileEditFrame(ClientFriendsMenuPanel.this, username, ip_addr, port_no);
//                pef.setVisible(true);
//            }
//        });

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
