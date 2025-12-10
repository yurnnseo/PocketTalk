//말풍선 위치, 스크롤 디자인 패널
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class MessageContainerPanel extends JPanel{
	private Font customFont;
    
    public MessageContainerPanel(Font font) {
        this.customFont = font;
        
        // 메시지를 위에서 아래로 쌓기 위해 BoxLayout 사용
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        setBackground(Color.decode("#F9F9F9"));
    }
    
    public void addMessage(String messageText, boolean isSent) {
    	
    	MessagePanel messagepanel = new MessagePanel(messageText, isSent, customFont); //말풍선 생성
    	
    	// 좌우 정렬 컨테이너
        JPanel alignPanel = new JPanel() {
            @Override
            public Dimension getMaximumSize() {
                Dimension d = getPreferredSize();
                d.width = Integer.MAX_VALUE;
                return d;
            }
        };

    	//내 메시지 isSent=true, 상대는 false
        alignPanel.setLayout(new FlowLayout(isSent ? FlowLayout.RIGHT : FlowLayout.LEFT, 0, 3));
        alignPanel.setBackground(Color.decode("#F9F9F9"));

        alignPanel.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));

        alignPanel.add(messagepanel);

        // 최대 높이를 현재 높이로 고정
        alignPanel.setMaximumSize(
            new Dimension(Integer.MAX_VALUE, alignPanel.getPreferredSize().height)
        );

        add(alignPanel);

        revalidate();
        repaint();		
    }
    
    // 이미지 말풍선 추가용 메서드
    public void addImageMessage(ImageIcon icon, boolean isSent) {
        if (icon == null) return;

        ImageMessagePanel imagePanel = new ImageMessagePanel(icon, isSent);

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
        alignPanel.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));
        alignPanel.add(imagePanel);
        alignPanel.setMaximumSize(
            new Dimension(Integer.MAX_VALUE, alignPanel.getPreferredSize().height)
        );

        add(alignPanel);
        revalidate();
        repaint();
    }
}
