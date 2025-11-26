import java.awt.Color;
import java.awt.Font;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class MiniGamePlayPanel extends JPanel{
	private Font font;
	private Image backgroundImg = null;
	private String Background = "Images/gamebackground.png";
	
	public MiniGamePlayPanel() {
			setLayout(null);
			this.backgroundImg = new ImageIcon(getClass().getResource("/" + Background)).getImage();
		    setSize(770, 600); // 사이즈 설정
		    setVisible(true); 
		    
	}
}
