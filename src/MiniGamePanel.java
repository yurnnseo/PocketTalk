import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class MiniGamePanel extends JPanel{
	private JButton startbtn, howtoplaybtn;
	private Font font;
	private JLabel title;
	private Image backgroundImg = null;
	private String Background = "Images/gamebackground.png";
	private JPanel startPanel; // 반투명 패널
	
	public MiniGamePanel() {
		setLayout(null);
		this.backgroundImg = new ImageIcon(getClass().getResource("/" + Background)).getImage();
		setSize(770, 600);
		
		//시작버튼이 달린 반투명 패널
		startPanel = new JPanel() {
			 @Override
			    protected void paintComponent(Graphics g) {
			        super.paintComponent(g);
			        //25% 투명 배경
			        g.setColor(new Color(0xF9, 0xF9, 0xF9, 25)); 
			        g.fillRect(0, 0, getWidth(), getHeight());
			    }
		};
		
		startPanel.setLayout(null);
		startPanel.setBounds(0, 0, 770, 600);
	    startPanel.setOpaque(false);
	    add(startPanel);
    
	    font = FontSource.get(16f);
	    
	    title = new JLabel("포도게임");
	    title.setFont(FontSource.get(40f));
	    title.setBounds(312, 56, 770, 50);
	    startPanel.add(title);
	    
	    startbtn = UIComponentZip.createTextButton("게임 시작", 310,420, 150,50,font);
	    howtoplaybtn = UIComponentZip.createTextButton("게임 방법", 310, 490,150,50,font);
	    startPanel.add(startbtn);
	    startPanel.add(howtoplaybtn);
	    
	    startbtn.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		startPanel.setVisible(false);
	    		repaint();
	    	}
	    });
	}
	
	 // 배경 이미지 그리기
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if(backgroundImg != null) {
            g.drawImage(backgroundImg, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
