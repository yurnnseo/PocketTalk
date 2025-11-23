import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class ChattingFrame extends JFrame{
	
	public ChattingFrame(String username, String ip_addr, String port_no, String groupMembers) {
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(0, 0, 380, 560);
		//setLayout(null);
		setResizable(false);
		//setBackground(Color.decode("#F9F9F9"));
		
		ChattingPanel chatpanel = new ChattingPanel(username, ip_addr, port_no, groupMembers); //패널 부착		
		add(chatpanel);
		
		setVisible(true);
	}

}