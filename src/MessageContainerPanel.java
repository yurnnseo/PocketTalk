import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

//말풍선 위치 지정, 말풍선들을 위에서 아래로 쌓아주는 패널
public class MessageContainerPanel extends JPanel{
	
	private Font customFont;
    
    public MessageContainerPanel(Font font) {
        this.customFont = font;
        
        // 메시지를 위에서 아래로 쌓기 위해 BoxLayout 사용함.
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        setBackground(Color.decode("#F9F9F9"));
    }
    
    // 텍스트 말풍선 추가
    public void addMessage(String messageText, boolean isSent) {
    	int maxBubbleWidth = (int)(getWidth() * 0.65); // 원하는 비율
        if (maxBubbleWidth <= 0) maxBubbleWidth = 260; // 초기 보호값
    	
        MessagePanel messagepanel = new MessagePanel(messageText, isSent, customFont, maxBubbleWidth); //말풍선 생성
    	
    	add(createAlignPanel(isSent, messagepanel));

        revalidate();
        repaint();		
    }
    
    // 이미지 말풍선 추가
    public void addImageMessage(ImageIcon icon, boolean isSent) {
        if (icon == null) return;

        // 이미지 말풍선 생성
        ImageMessagePanel imagePanel = new ImageMessagePanel(icon, isSent);

        add(createAlignPanel(isSent, imagePanel));
        revalidate();
        repaint();
    }
    
    // 좌/우 정렬용 컨테이너 만들기
    private JPanel createAlignPanel(boolean isSent, JComponent message) {
    	JPanel alignPanel = new JPanel(new BorderLayout()) {
            @Override
            public Dimension getMaximumSize() {
                Dimension d = getPreferredSize();
                d.width = Integer.MAX_VALUE;
                return d;
            }
        };

        alignPanel.setOpaque(true);
        alignPanel.setBackground(Color.decode("#F9F9F9"));
        alignPanel.setBorder(new EmptyBorder(2, 8, 2, 8));

        message.setMaximumSize(message.getPreferredSize());

        if (isSent) alignPanel.add(message, BorderLayout.EAST); // 오른쪽 붙이기
        else        alignPanel.add(message, BorderLayout.WEST); // 왼쪽 붙이기

        return alignPanel;
    }
}
