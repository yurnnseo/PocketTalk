//JavaChatClientMain.java
//Java Client 시작

import java.awt.EventQueue;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class JavaChatClientMain extends JFrame {

	private JPanel contentPane;
	private JTextField txtUserName;
	private JTextField txtIpAddress;
	private JTextField txtPortNumber;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					JavaChatClientMain frame = new JavaChatClientMain();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public JavaChatClientMain() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 420, 600);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		//배경이미지
		ImageIcon icon = new ImageIcon(getClass().getResource("login22.png"));
        Image scaledImage = icon.getImage().getScaledInstance(380, 584, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);

        JLabel imgLabel = new JLabel(scaledIcon);
        imgLabel.setBounds(0, 0, 420, 570);
        contentPane.add(imgLabel);
        
		txtUserName = new JTextField();
		txtUserName.setHorizontalAlignment(SwingConstants.CENTER);
		txtUserName.setBounds(220, 202, 116, 33);
		imgLabel.add(txtUserName);
		txtUserName.setColumns(10);
		txtUserName.setOpaque(false); //텍스트필드 박스 투명 변경
		txtUserName.setBorder(BorderFactory.createEmptyBorder()); //박스 테두리 안보이게 변경
		
		txtIpAddress = new JTextField();
		txtIpAddress.setHorizontalAlignment(SwingConstants.CENTER);
		txtIpAddress.setText("127.0.0.1");
		txtIpAddress.setColumns(10);
		txtIpAddress.setBounds(220, 290, 116, 33);
		txtIpAddress.setOpaque(false); 
		txtIpAddress.setBorder(BorderFactory.createEmptyBorder());
		imgLabel.add(txtIpAddress);
		
		txtPortNumber = new JTextField();
		txtPortNumber.setText("30000");
		txtPortNumber.setHorizontalAlignment(SwingConstants.CENTER);
		txtPortNumber.setColumns(10);
		txtPortNumber.setBounds(220, 375, 116, 33);
		txtPortNumber.setOpaque(false);
		txtPortNumber.setBorder(BorderFactory.createEmptyBorder()); 
		imgLabel.add(txtPortNumber);
		
		//로그인 버튼 사진으로 안하고 버튼으로 만들고싶은데 그러면 소스 얻어와서 따로 버튼 자바 파일 만들어야댐..
		JButton btnConnect = new JButton();
		btnConnect.setBounds(108, 480, 205, 38);
		btnConnect.setContentAreaFilled(false);
		btnConnect.setBorder(BorderFactory.createEmptyBorder()); 
		imgLabel.add(btnConnect);
		
		Myaction action = new Myaction();
		btnConnect.addActionListener(action);
		txtUserName.addActionListener(action);
		txtIpAddress.addActionListener(action);
		txtPortNumber.addActionListener(action);
	}
	class Myaction implements ActionListener // 내부클래스로 액션 이벤트 처리 클래스
	{
		@Override
		public void actionPerformed(ActionEvent e) {
			String username = txtUserName.getText().trim();
			String ip_addr = txtIpAddress.getText().trim();
			String port_no = txtPortNumber.getText().trim();
			//JavaChatClientView view = new JavaChatClientView(username, ip_addr, port_no);
			ClientMenu cm = new ClientMenu(username, ip_addr, port_no);
			setVisible(false);   //dispose();  // 창을 완전히 닫고 자원 해제
		}
	}
}
