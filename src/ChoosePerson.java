//대화상대 선택
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
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
    private JLabel lblUserName, choiceLabel, okLabel; 
    private FriendsListPanel parentPanel;
    private String username;
    private JButton okbutton;
    //이렇게 적는거 말고 받는거로 하고 싶은디.. 생성자 매개변수를 바꿔야되나
    private final String serverIp = "127.0.0.1"; 
    private final String serverPort = "30000";
    private FontSource fontSource = new FontSource("/IM_Hyemin-Bold.ttf"); // 폰트
    
	public ChoosePerson(String username, List<String> users) {
		this.username = username; //본인 이름
		
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
        	}
        };
        
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(null);
        setContentPane(contentPane);
        
        //친구 목록 화면 출력
        parentPanel = new FriendsListPanel(username, true); //본인은 제외
        parentPanel.updateList(users);
        
        JScrollPane scrollPane = new JScrollPane(parentPanel);
        scrollPane.setBounds(10, 42, 238, 240);
        scrollPane.getViewport().setOpaque(false); //배경 투명화
        scrollPane.setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder()); //테두리 투명화
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS); //스크롤바 보이게
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        contentPane.add(scrollPane);
        
        choiceLabel = new JLabel("대화상대 선택", SwingConstants.CENTER);
        choiceLabel.setFont(fontSource.getFont(12f));
        choiceLabel.setForeground(Color.BLACK);
        choiceLabel.setBounds(85, 5, 80, 30);
        contentPane.add(choiceLabel);
        
        //버튼 생성
        okbutton = makeButton("선택 완료", 56, 28, 165, 290);

        contentPane.add(okbutton); //취소는 창닫기 버튼 누르면 됨
               
        okbutton.addActionListener(new ActionListener(){
        	public void actionPerformed(ActionEvent e) {
        		Set<String> selected = parentPanel.getSelectedUsers();
                if (selected.isEmpty()) {
                    JOptionPane.showMessageDialog(ChoosePerson.this, 
                        "대화 상대를 한 명 이상 선택해주세요.", "선택 오류", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                selected.add(username); //자기자신 추가
                
                String members = String.join(" ", selected);
                
                dispose(); //창 제거
                
                new ChattingFrame(username, serverIp, serverPort, members);
        	}
        });
	}
	
	 private JButton makeButton(String text, int width, int height, int x, int y) {
	        JButton btn = new JButton(text);
	        btn.setBounds(x, y, width, height);
	        btn.setBackground(Color.WHITE); 
	        btn.setBorder(new LineBorder(Color.BLACK));
	        btn.setFont(fontSource.getFont(10f));
	        
	        btn.setFocusPainted(false);
	        btn.setContentAreaFilled(false);
	        btn.setOpaque(true);
	        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	        
	        Color hoverColor = Color.decode("#E3D6F0"); // 연보라
	        Color normalColor = Color.WHITE;

	        btn.addMouseListener(new MouseAdapter() {
	            @Override
	            public void mouseEntered(MouseEvent e) {
	                btn.setBackground(hoverColor);
	            }

	            @Override
	            public void mouseExited(MouseEvent e) {
	                btn.setBackground(normalColor);
	            }
	        });
	        
	        return btn;
	    }
	 
}