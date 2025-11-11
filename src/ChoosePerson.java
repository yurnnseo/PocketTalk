//대화상대 선택
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Image;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class ChoosePerson extends JFrame{
	private JPanel contentPane;
    private JTextField txtInput;
    private String UserName;
    private JButton btnSend;
    private JTextArea textArea;
    private static final int BUF_LEN = 128; // Windows 처럼 BUF_LEN 을 정의
    private Socket socket; // 연결소켓
    private InputStream is;
    private OutputStream os;
    private DataInputStream dis;
    private DataOutputStream dos;
    private JLabel lblUserName;
    
    private Image backgroundImg = null;
    private ImageIcon okicon1, cancel;
    private JButton okbutton, cancelbutton;
    
	public ChoosePerson() {
	
		// 배경 이미지 로드
        backgroundImg = new ImageIcon(getClass().getResource("/selecting.png")).getImage();
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 260, 370); // 크기 조정
        setResizable(false);
        
        //배경 이미지 그리는 패널
        contentPane = new JPanel() {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(backgroundImg, 0, 0, getWidth(), getHeight(), this);
        	}
        };
        
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(null);
        setContentPane(contentPane);
        
     // 아이콘
        okicon1  = new ImageIcon(getClass().getResource("/okicon1.png"));
        cancel = new ImageIcon(getClass().getResource("/cancelicon1.png"));
        
        okbutton = makeButton(okicon1, 40, 30, 120, 280);
        cancelbutton = makeButton(cancel, 40, 30, 180, 280);
        
        contentPane.add(okbutton);
        contentPane.add(cancelbutton);
	}
	
	 private JButton makeButton(ImageIcon icon, int width, int height, int x, int y) {
		 	Image img = icon.getImage();

	        // 크기 조정
	        Image scaledImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
	        icon.setImage(scaledImg);
	        
	        JButton btn = new JButton(icon);
	        btn.setBounds(x, y, icon.getIconWidth(), icon.getIconHeight());
	        btn.setContentAreaFilled(false);
	        btn.setBorderPainted(false);
	        btn.setFocusPainted(false);
	        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	        return btn;
	    }
}