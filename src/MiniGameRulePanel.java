import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class MiniGameRulePanel extends JPanel{
	private Font title, text, font;
	private JButton backbtn;
	private MiniGamePanel parentPanel;
	
	public MiniGameRulePanel(MiniGamePanel parent) {
		this.parentPanel = parent;
		
		setLayout(null);
		setSize(770, 600);
	    setBackground(Color.decode("#F9F9F9"));
	   
	    title = FontSource.get(40f);
	    text = FontSource.get(25f);
	    
	    backbtn = UIComponentZip.createTextButton("← 뒤로", 20, 20, 100, 40, font);
	    backbtn.setFont(FontSource.get(15f));
        add(backbtn);

        backbtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               parent.showStartPanel(); // MiniGamePanel로 돌아가기
            }
        });
	    
	}
	
	@Override
	    protected void paintComponent(Graphics g) {
	        super.paintComponent(g);
	        
	        //제목 배경
	        g.setColor(Color.decode("#D7C6E9"));
	        g.fillRect(0, 0, getWidth(), 130);
	        
	        //타이틀 텍스트
	        g.setColor(Color.BLACK);
	        g.setFont(title);
	        g.drawString("게임 방법", 265, 85);
	        
	        //설명 텍스트
	        g.setFont(text);
	        g.drawString("자신의 게임 보드는 항상 오른쪽", 80, 220);

	        
	        String fullRule = "마우스 드래그로 합이 5가 되는 포도를 제거할 때마다";
	        String target = "합이 5";

	        // Draw sentence
	        g.setColor(Color.BLACK);
	        g.setFont(FontSource.get(22f));
	        g.drawString(fullRule, 80, 300);

	        // Find target position
	        int baseX = 80;
	        int baseY = 300;
	        int preWidth = g.getFontMetrics().stringWidth(fullRule.substring(0, fullRule.indexOf(target)));
	        int targetWidth = g.getFontMetrics().stringWidth(target);
	        int textHeight = g.getFontMetrics().getHeight();

	        // Draw circle
	        int circleX = baseX + preWidth - 5;
	        int circleY = baseY - textHeight + 5;
	        int circleW = targetWidth + 10;
	        int circleH = textHeight;

	        g.setColor(Color.decode("#B9A0E6"));
	        g.drawOval(circleX, circleY, circleW, circleH);
	        
	        g.drawString("+10점 획득", 80, 340);
	        g.setColor(Color.BLACK);
	        String rule = "제한시간 30초 내에 많은 점수를 따는 플레이어가 승리";
	        
	        g.drawString(rule, 80, 430);

	        //밑줄 강조
	        int underlineX = 80;
	        int underlineY = 435;
	        int underlineWidth = g.getFontMetrics().stringWidth("제한시간 30초");

	        g.setColor(Color.decode("#B9A0E6"));
	        g.fillRect(underlineX, underlineY + 5, underlineWidth, 4);
	  }
}
