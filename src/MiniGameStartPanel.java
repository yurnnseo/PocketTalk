import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class MiniGameStartPanel extends JPanel{
	private MiniGamePlayPanel gamepanel;
	private JButton startbtn, howtoplaybtn;
	private Font font;
	private JLabel title;
	
	public MiniGameStartPanel() {
		setLayout(null);
	    setBackground(Color.decode("#F9F9F9"));
	    setSize(770, 600); // 사이즈 설정
	    setVisible(true); 
	    
	    font = FontSource.get(16f);
	    
	    title = new JLabel("포도게임");
	    title.setFont(FontSource.get(40f));
	    title.setBounds(312, 56, 770, 50);
	    add(title);
	    
	    startbtn = UIComponentZip.createTextButton("게임 시작", 310,450, 150,50,font);
	    howtoplaybtn = UIComponentZip.createTextButton("게임 방법", 310, 520,150,50,font);
	    add(startbtn);
	    add(howtoplaybtn);
	    
	    startbtn.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		 	gamepanel = new MiniGamePlayPanel();
	    	        add(gamepanel);
	    	}
	    });
	}
}
