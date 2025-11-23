//말풍선 위치, 스크롤 디자인 패널


import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

public class MessageContainerPanel extends JPanel{
	private Font customFont;
    
    public MessageContainerPanel(Font font) {
        this.customFont = font;
        
        // 메시지를 위에서 아래로 쌓기 위해 BoxLayout 사용
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        
        // 배경색 설정
        setBackground(Color.decode("#F9F9F9"));
    }
    
    public void addMessage(String messageText, boolean isSent) {
    	//말풍선 생성
    	MessagePanel messagepanel = new MessagePanel(messageText, isSent, customFont);
    	//좌우정렬 컨테이너 생성
    	JPanel alignPanel = new JPanel();
    	alignPanel.setLayout(new FlowLayout(
                isSent ? FlowLayout.RIGHT : FlowLayout.LEFT //내 메시지 isSent=true, 상대는 false
            ));
            alignPanel.setBackground(Color.decode("#F9F9F9"));
            
            alignPanel.add(messagepanel);
            add(alignPanel);
            
            //UI 갱신
            revalidate();
            repaint();
    			
    }
}
