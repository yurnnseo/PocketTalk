//친구 목록 화면 ClietnMenu.java
import java.awt.Color;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class ClientMenu2 extends JFrame {
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
    private JTextField textField;
    private ImageIcon meicon;
    private ImageIcon meicon2;
    private ImageIcon chaticon;
    private ImageIcon chaticon2;
    private JButton chb;
    
	public ClientMenu2(String username, String ip_addr, String port_no) {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 420, 600);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setBackground(Color.white);
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		//배경이미지
		ImageIcon icon = new ImageIcon(getClass().getResource("chating.png"));
		Image scaledImage = icon.getImage().getScaledInstance(420, 600, Image.SCALE_SMOOTH);
		ImageIcon scaledIcon = new ImageIcon(scaledImage);

		JLabel imgLabel = new JLabel(scaledIcon);
	    imgLabel.setBounds(0, 0, 420, 570);
	    imgLabel.setLayout(null);
	    contentPane.add(imgLabel);
	    
	    //친구 버튼
	    meicon = new ImageIcon(getClass().getResource("meta.png")); 
	    meicon2 = new ImageIcon(getClass().getResource("me.png")); 
	    JButton meb = new JButton(meicon2);
	    meb.setBounds(30,50,50,50);
	    meb.setContentAreaFilled(false);
	    meb.setBorderPainted(false);
	    meb.setFocusPainted(false);
	    imgLabel.add(meb);
	    
	    //친구 목록으로 이동
	    meb.addActionListener(new ActionListener() {
	    	@Override
	    	public void actionPerformed(ActionEvent e) {
	    		if(meb.getIcon()==meicon2) {
	    			meb.setIcon(meicon);
	    			chb.setIcon(chaticon2);
	    			//이거 창 새로 뜨는거 말고 자연스럽게 바뀌는?거로 하고싶다..
	    			ClientMenu cm = new ClientMenu(username, ip_addr, port_no);
	    		}
	    		else {
	    			meb.setIcon(meicon2);
	    			chb.setIcon(chaticon);
	    		}
	    	}
	    });
	    
	  //채팅 버튼
	    chaticon = new ImageIcon(getClass().getResource("chat.png"));
	    chaticon2 = new ImageIcon(getClass().getResource("chat2.png"));
	    chb = new JButton(chaticon2);
	    chb.setBounds(30,120,50,50);
	    chb.setContentAreaFilled(false);
	    chb.setBorderPainted(false);
	    chb.setFocusPainted(false);
	    imgLabel.add(chb);
	    
	    chb.addActionListener(new ActionListener() {
	    	@Override
	    	public void actionPerformed(ActionEvent e) {
	    		if(chb.getIcon()==chaticon2) {
	    			chb.setIcon(chaticon);
	    			meb.setIcon(meicon2);
	    		}
	    		else {
	    			chb.setIcon(chaticon2);
	    			meb.setIcon(meicon);
	    		}
	    	}
	    });
		/*JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(12, 10, 352, 340);
		contentPane.add(scrollPane);*/
	        
	    setVisible(true);
	}
}
