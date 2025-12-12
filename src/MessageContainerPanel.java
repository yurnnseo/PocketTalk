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
    	
    	MessagePanel messagepanel = new MessagePanel(messageText, isSent, customFont); //말풍선 생성
    	
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
        JPanel alignPanel = new JPanel() {
            @Override
            public Dimension getMaximumSize() {
                Dimension d = getPreferredSize();
                d.width = Integer.MAX_VALUE;
                return d;
            }
        };

        alignPanel.setLayout(new FlowLayout(isSent ? FlowLayout.RIGHT : FlowLayout.LEFT, 0, 3));
        alignPanel.setBackground(Color.decode("#F9F9F9"));
        alignPanel.setBorder(new EmptyBorder(2, 0, 2, 0));

        alignPanel.add(message);
        alignPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, alignPanel.getPreferredSize().height));

        return alignPanel;
    }
}
