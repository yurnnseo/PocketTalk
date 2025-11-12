//대화상대 선택
import java.awt.Color;
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
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

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
    private JLabel lblUserName, choiceLabel, okLabel, cancelLabel;
    private Image backgroundImg;
    
    //private ImageIcon okicon1, cancel;
    private JButton okbutton, cancelbutton;
    
    private FontSource fontSource = new FontSource("/IM_Hyemin-Bold.ttf"); // 폰트
    
	public ChoosePerson() {
		setLayout(null);
		setBackground(Color.decode("#F9F9F9"));

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 260, 370); // 크기 조정
        setResizable(false);
        
        //배경 이미지 그리는 패널
        contentPane = new JPanel() {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(Color.decode("#E3D6F0"));
            g.fillRect(0, 0, getWidth(), 40);
            g.drawImage(backgroundImg, 0, 0, getWidth(), getHeight(), this);
        	}
        };
        
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(null);
        setContentPane(contentPane);
        
        choiceLabel = new JLabel("대화상대 선택", SwingConstants.CENTER);
        choiceLabel.setFont(fontSource.getFont(12f));
        choiceLabel.setForeground(Color.BLACK);
        choiceLabel.setBounds(85, 5, 80, 30);
        add(choiceLabel);
        
        //버튼 생성
        okbutton = makeButton("확인", 40, 28, 135, 295);
        cancelbutton = makeButton("취소", 40, 28, 185, 295);
      
        contentPane.add(okbutton);
        contentPane.add(cancelbutton);
        
        
	}
	
	 private JButton makeButton(String text, int width, int height, int x, int y) {
	        JButton btn = new JButton(text);
	        btn.setBounds(x, y, width, height);
	        btn.setBackground(Color.WHITE); 
	        btn.setBorder(new LineBorder(Color.BLACK));
	        btn.setFont(fontSource.getFont(10f));
	        btn.setFocusPainted(false);
	        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	        return btn;
	    }
}