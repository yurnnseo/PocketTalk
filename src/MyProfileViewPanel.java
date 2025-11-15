import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MyProfileViewPanel extends JPanel {

    private final MyProfileViewFrame parentFrame;
    private JButton editButton;
    private FontSource fontSource = new FontSource("/IM_Hyemin-Bold.ttf"); // 폰트
    
    private String username;          
    private String statusMessage = ""; 
    private String profileImagePath;  

    private ProfileHeaderView header;  
    private JLabel statusLabel; 
    
    public MyProfileViewPanel(MyProfileViewFrame parentFrame, String username, String ip_addr, String port_no, String profileImagePath) {
        this.parentFrame = parentFrame;
        this.username = username;         
        this.statusMessage = ""; 
        
        setLayout(null);
        setOpaque(true);
        setBackground(Color.decode("#F9F9F9"));

        // 프로필 헤더 (친구목록과 동일)
        if (profileImagePath == null || profileImagePath.isEmpty()) {
            profileImagePath = "/Images/defaultprofileimage.png";
        }
        final String finalProfileImagePath = profileImagePath;
        
        // 프로필 헤더 (친구목록과 동일)
        header = new ProfileHeaderView(this.username, this.profileImagePath, 60, 60, ProfileHeaderView.Orientation.VERTICAL);
        Dimension hSize = header.getPreferredSize();
        int headerX = 66;
        int headerY = 230; // 살짝 위로 땡겨도 되고 취향대로
        header.setBounds(headerX, headerY, hSize.width, hSize.height);
        add(header);

        statusLabel = new JLabel(statusMessage, SwingConstants.CENTER);
        statusLabel.setFont(fontSource.getFont(12f));
        statusLabel.setForeground(Color.DARK_GRAY);

        int statusY = headerY + hSize.height + 8;
        statusLabel.setBounds(headerX, statusY, hSize.width, 20);
        add(statusLabel);

        // 편집 버튼 
        ImageIcon normal = new ImageIcon(getClass().getResource("/Images/profileeditbutton.png"));
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
                ProfileEditFrame pef = new ProfileEditFrame(MyProfileViewPanel.this, MyProfileViewPanel.this.username, ip_addr, port_no, MyProfileViewPanel.this.profileImagePath, MyProfileViewPanel.this.statusMessage);
                pef.setLocationRelativeTo(parentFrame);
                pef.setVisible(true);
            }
        });
    }
    
 // 편집 결과를 반영하는 메서드
    public void updateProfile(String newName, String newStatus) {
        if (newName != null && !newName.isEmpty()) {
            this.username = newName;
            header.setUserName(newName);       // 프로필 헤더에 이름 갱신
        }
        if (newStatus != null) {
            this.statusMessage = newStatus;
            statusLabel.setText(newStatus);    // 상태메시지 라벨 갱신
        }

        // 필요하면 여기서 parentFrame/친구패널까지 알려줄 수도 있음
        parentFrame.onMyProfileUpdated(newName, newStatus);

        revalidate();
        repaint();
    }
    
    // 배경 직접 그리기
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.decode("#ECECEC"));
        g.fillRect(0, 0, getWidth(), 290);
    }
}
