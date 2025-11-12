import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MyProfileViewPanel extends JPanel {

    private final MyProfileViewFrame parentFrame;
    private JButton editButton;
    private FontSource fontSource = new FontSource("/IM_Hyemin-Bold.ttf"); // 폰트

    public MyProfileViewPanel(MyProfileViewFrame parentFrame, String username, String ip_addr, String port_no, String profileImagePath) {
        this.parentFrame = parentFrame;
        setLayout(null);

        setOpaque(true);
        setBackground(Color.decode("#F9F9F9"));

        // 프로필 헤더 (친구목록과 동일)
        ProfileHeaderView header = new ProfileHeaderView(username, "/defaultprofileimage.png", 60, 60, ProfileHeaderView.Orientation.VERTICAL);
        header.setBounds(66, 250, header.getPreferredSize().width, header.getPreferredSize().height);
        add(header);


        // 이미지 버튼 (필요 시 hover/pressed 이미지는 선택) 
        ImageIcon normal = new ImageIcon(getClass().getResource("/profileeditbutton.png"));
        Image scaled = normal.getImage().getScaledInstance(400, 50, Image.SCALE_SMOOTH);
        ImageIcon editIcon = new ImageIcon(scaled);

        editButton = new JButton("프로필 편집");
        editButton.setFont(fontSource.getFont(10f));
        editButton.setBackground(Color.decode("#E3D6F0")); // 버튼 색상
        editButton.setForeground(Color.BLACK); // 글자색
        editButton.setFocusPainted(false);
        editButton.setBorderPainted(false);
        editButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        editButton.setBounds(28, 390, 260, 35); // 버튼 위치와 크기 조정
        add(editButton);

        // 버튼 클릭 시 프로필 수정 창 열기
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ProfileEditFrame pef = new ProfileEditFrame(MyProfileViewPanel.this, username, ip_addr, port_no, profileImagePath);
                pef.setLocationRelativeTo(parentFrame);
                pef.setVisible(true);
            }
        });
    }
    
    // 배경 직접 그리기
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.decode("#ECECEC"));
        g.fillRect(0, 0, getWidth(), 290);
    }
}
