import javax.swing.*;
import java.awt.*;

public class ProfileHeaderView extends JPanel {

    public enum Orientation { HORIZONTAL, VERTICAL }

    private final JButton profileButton;
    private final JLabel nameLabel, messageLabel;
    private FontSource fontSource = new FontSource("/IM_Hyemin-Bold.ttf"); // 폰트

    public ProfileHeaderView(String username, String message, String imagePath, int imgW, int imgH, Orientation orientation) {
        setLayout(null);
        setOpaque(false);

        if (imagePath == null || imagePath.isEmpty()) {
            imagePath = "/Images/defaultprofileimage.png";
        }

        // 아이콘 로드 + 스케일
        Image img;
        if (imagePath.startsWith("/")) { // 리소스 경로
            img = new ImageIcon(getClass().getResource(imagePath)).getImage();
        } 
        else {                         // 파일 시스템 경로
            img = new ImageIcon(imagePath).getImage();
        }
        ImageIcon icon = new ImageIcon(img.getScaledInstance(imgW, imgH, Image.SCALE_SMOOTH));
        
        // 이미지 버튼
        profileButton = new JButton(icon);
        profileButton.setContentAreaFilled(false);
        profileButton.setBorderPainted(false);
        profileButton.setFocusPainted(false);
        profileButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        add(profileButton);

        // 이름 라벨
        nameLabel = new JLabel(username, SwingConstants.LEFT);
        nameLabel.setFont(fontSource.getFont(16f));
        nameLabel.setForeground(Color.BLACK);
        add(nameLabel);

        messageLabel = new JLabel(message, SwingConstants.RIGHT);
        messageLabel.setFont(fontSource.getFont(12f));
        messageLabel.setForeground(Color.DARK_GRAY);
        add(messageLabel);
        
        // 배치
        if (orientation == Orientation.HORIZONTAL) {
            int gap = 15;
            int Area = 150;
            profileButton.setBounds(0, 0, imgW, imgH);
            nameLabel.setHorizontalAlignment(SwingConstants.LEFT);
            nameLabel.setBounds(imgW + gap, (imgH - 25) / 2, 150, 25);
            
            messageLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            messageLabel.setBounds(imgW + gap + Area / 2, (imgH - 25) / 2 + 1, Area/2, 25);

            int totalW = imgW + gap + 150;
            int totalH = Math.max(imgH, 25);
            setPreferredSize(new Dimension(totalW, totalH));
        } 
        
        else {
            // 세로 정렬  → 내 프로필용
            // 너비 살짝 여유를 두고 가운데 정렬
            int width = Math.max(imgW + 40, 180);
            int imgX = (width - imgW) / 2;
            int imgY = 0;

            profileButton.setBounds(imgX, imgY, imgW, imgH);

            nameLabel.setHorizontalAlignment(SwingConstants.CENTER);
            nameLabel.setBounds(0, imgY + imgH + 10, width, 28);
            
            messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
            messageLabel.setBounds(0, imgY + imgH + 10 + 38, width, 18);

            int height = imgH + 10 + 70;
            setPreferredSize(new Dimension(width, height));
        }
    }

    public JButton getProfileButton() { return profileButton; }
    public JLabel getNameLabel() { return nameLabel; }
    public JLabel getMessageLabel() { return messageLabel; }
    
    public void setUserName(String newName) {
        nameLabel.setText(newName);
    }
    
    public void setMessage(String newMessage) {
    	messageLabel.setText(newMessage);
    }
}
