import javax.swing.*;
import java.awt.*;

//프로필 사진 + 이름 + 상태메시지를 한 덩어리로 보여주는 컴포넌트
public class ProfileHeaderView extends JPanel {

	// 가로 배치/세로 배치 구분용
    public enum Orientation { HORIZONTAL, VERTICAL }

    private final JButton profileButton;
    private final JLabel nameLabel, messageLabel;

    public ProfileHeaderView(String username, String message, String imagePath, int imgW, int imgH, Orientation orientation) {
        setLayout(null);
        setOpaque(false);

        // 이미지 경로가 없으면 기본 이미지
        if (imagePath == null || imagePath.isEmpty()) {
            imagePath = "/Images/defaultprofileimage.png";
        }

        // 프로필 이미지 로드
        Image img;
        if (imagePath.startsWith("/")) { 
            img = new ImageIcon(getClass().getResource(imagePath)).getImage();
        } 
        else {                         
            img = new ImageIcon(imagePath).getImage();
        }
        
        // 로드한 이미지를 축소해서 아이콘 생성
        ImageIcon icon = new ImageIcon(img.getScaledInstance(imgW, imgH, Image.SCALE_SMOOTH));
        
        // 프로필 이미지 버튼
        profileButton = new JButton(icon);
        profileButton.setContentAreaFilled(false);
        profileButton.setBorderPainted(false);
        profileButton.setFocusPainted(false);
        profileButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        add(profileButton);

        // 이름, 상태메시지는 라벨
        nameLabel = new JLabel(username, SwingConstants.LEFT);
        nameLabel.setFont(FontSource.get(16f));
        nameLabel.setForeground(Color.BLACK);
        add(nameLabel);

        messageLabel = new JLabel(message, SwingConstants.RIGHT);
        messageLabel.setFont(FontSource.get(12f));
        messageLabel.setForeground(Color.DARK_GRAY);
        add(messageLabel);
        
        // 가로 배치일 때
        if (orientation == Orientation.HORIZONTAL) {
        	
            int gap = 15;
            int Area = 150;
            
            profileButton.setBounds(0, 0, imgW, imgH);
            
            nameLabel.setHorizontalAlignment(SwingConstants.LEFT);
            nameLabel.setBounds(imgW + gap, (imgH - 25) / 2, 150, 25);
            
            messageLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            messageLabel.setBounds(imgW + gap + Area / 2, (imgH - 25) / 2 + 1, Area/2, 25);

            // 이 컴포넌트 전체 크기
            int totalW = imgW + gap + 150;
            int totalH = Math.max(imgH, 25);
            setPreferredSize(new Dimension(totalW, totalH));
        } 
        
        // 세로 배치일 때 
        else {
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

    // getter 함수
    public JButton getProfileButton() { return profileButton; }
    public JLabel getNameLabel() { return nameLabel; }
    public JLabel getMessageLabel() { return messageLabel; }
    
    // setter 함수
    public void setUserName(String newName) {
        nameLabel.setText(newName);
    }
    
    public void setMessage(String newMessage) {
    	messageLabel.setText(newMessage);
    }
    
    public void setProfileImage(String imagePath) {
        if (imagePath == null || imagePath.isEmpty()) {
            imagePath = "/Images/defaultprofileimage.png";
        }

        Image img;
        if (imagePath.startsWith("/")) {
            img = new ImageIcon(getClass().getResource(imagePath)).getImage();
        } 
        else {
            img = new ImageIcon(imagePath).getImage();
        }
        
        // 버튼 크기를 기준으로 아이콘 크기를 맞춤
        int w = (profileButton.getWidth() > 0) ? profileButton.getWidth() : 50;
        int h = (profileButton.getHeight() > 0) ? profileButton.getHeight() : 50;

        ImageIcon icon = new ImageIcon(img.getScaledInstance(w, h, Image.SCALE_SMOOTH));
        profileButton.setIcon(icon);
        
        revalidate();
        repaint();
    }
}
