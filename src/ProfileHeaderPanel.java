import javax.swing.*;
import java.awt.*;

public class ProfileHeaderPanel extends JPanel {
    private final JButton profileButton;
    private final JLabel nameLabel;

    public ProfileHeaderPanel(String username, String profileImagePath, int profileW, int profileH) {
        setLayout(null);
        setOpaque(false);

        // === 내부 구성만 고정 ===
        // 프로필 이미지
        ImageIcon original = new ImageIcon(getClass().getResource(profileImagePath));
        Image scaled = original.getImage().getScaledInstance(profileW, profileH, Image.SCALE_SMOOTH);
        ImageIcon profileIcon = new ImageIcon(scaled);

        profileButton = new JButton(profileIcon);
        profileButton.setBounds(0, 0, profileW, profileH);
        profileButton.setContentAreaFilled(false);
        profileButton.setBorderPainted(false);
        profileButton.setFocusPainted(false);
        profileButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        add(profileButton);

        // 이름 라벨 (프로필 오른쪽 15px 간격)
        nameLabel = new JLabel(username, SwingConstants.LEFT);
        nameLabel.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        nameLabel.setForeground(Color.BLACK);
        nameLabel.setBounds(profileW + 15, (profileH / 2) - 10, 150, 25);
        add(nameLabel);

        // 내부 구성만 고정하고, 외부 배치에 맞춰 부모가 조정하도록 함
        int totalWidth = profileW + 15 + 150;
        int totalHeight = Math.max(profileH, 25);
        setPreferredSize(new Dimension(totalWidth, totalHeight));
    }

    public JButton getProfileButton() { return profileButton; }
    public JLabel getNameLabel() { return nameLabel; }
}
