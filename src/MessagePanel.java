//말풍선 디자인 패널
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class MessagePanel extends JPanel{
	private String text;
    private boolean isSent;
    private static final Color SENT_COLOR = Color.decode("#E3D6F0"); //내 메시지
    private static final Color RECEIVED_COLOR = Color.WHITE;  //상대 메시지
    private static final int ARC = 20; // 둥근 모서리 반지름
    private Font customFont; //폰트 소스
    
    public MessagePanel(String text, boolean isSent, Font font) {
        this.text = text;
        this.isSent = isSent;
        this.customFont = font;
        
        JLabel label = new JLabel(text);
        
        if (customFont != null) {
            // deriveFont:기존 폰트의 일부 속성만 변경할 때 사용
            label.setFont(customFont.deriveFont(14f)); 
        } else {
            // 폰트가 null인 경우 기본 폰트를 사용하되, 크기는 14f로 설정
            label.setFont(label.getFont().deriveFont(14f));
        }
        
        setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        
        setLayout(new BorderLayout());
        add(label, BorderLayout.CENTER);
        setOpaque(false);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        
        // 둥근 모서리를 부드럽게
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // 말풍선 배경색 지정
        Color bubbleColor = isSent ? SENT_COLOR : RECEIVED_COLOR;
        g2d.setColor(bubbleColor);

        // 둥근 사각형 그리기
        RoundRectangle2D rect = new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), ARC, ARC);
        g2d.fill(rect);

        g2d.dispose();
    }
}
