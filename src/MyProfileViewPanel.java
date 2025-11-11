import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MyProfileViewPanel extends JPanel {

    private final MyProfileViewFrame parentFrame;
    private JButton editButton;

    public MyProfileViewPanel(MyProfileViewFrame parentFrame, String username, String ip_addr, String port_no, String profileImagePath) {
        this.parentFrame = parentFrame;
        setLayout(null);

        setOpaque(true);
        setBackground(Color.decode("#F9F9F9"));

        // 프로필 헤더 (친구목록과 동일)
        ProfileHeaderView header = new ProfileHeaderView(username, "/defaultprofileimage.png", 80, 80, ProfileHeaderView.Orientation.VERTICAL);
        header.setBounds(60, 250, header.getPreferredSize().width, header.getPreferredSize().height);
        add(header);


        // 이미지 버튼 (필요 시 hover/pressed 이미지는 선택) 
        ImageIcon normal = new ImageIcon(getClass().getResource("/profileeditbutton.png"));
        Image scaled = normal.getImage().getScaledInstance(400, 50, Image.SCALE_SMOOTH);
        ImageIcon editIcon = new ImageIcon(scaled);

        editButton = new JButton(editIcon);
        editButton.setBounds(95, 120, 120, 32);
        editButton.setBorderPainted(false);
        editButton.setContentAreaFilled(false);
        editButton.setFocusPainted(false);
        editButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        add(editButton);

        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
	            ProfileEditFrame pef = new ProfileEditFrame(MyProfileViewPanel.this, username, ip_addr, port_no, profileImagePath);
	            pef.setLocationRelativeTo(parentFrame);
	            pef.setVisible(true);
            }
        });
    }
}
