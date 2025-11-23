//채팅창
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class ChattingPanel extends JPanel {
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
    private String groupMembers; //그룹채팅위한 멤버 저장 변수
    private Image backgroundImg;
    private FontSource fontSource = new FontSource("/IM_Hyemin-Bold.ttf");
    private MessageContainerPanel messageContainer;
    
	public ChattingPanel(String username, String ip_addr, String port_no, String groupMembers) {
		this.groupMembers = groupMembers;	

		setBorder(new EmptyBorder(5, 5, 5, 5));
		setLayout(null);
		setBackground(Color.decode("#F9F9F9"));
		
		messageContainer = new MessageContainerPanel(fontSource.getFont(13f));
		JScrollPane scrollPane = new JScrollPane(messageContainer);
		scrollPane.setBounds(12, 12, 350, 455);   
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		add(scrollPane);

		//메시지 추가될 때마다 스크롤 자동 내림 기능
		scrollPane.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {  
            public void adjustmentValueChanged(AdjustmentEvent e) {  
                if (e.getAdjustable().getMaximum() == e.getValue() + e.getAdjustable().getVisibleAmount()) {
                    // 스크롤이 맨 아래에 있을 때만 자동으로 아래로 내림
                    e.getAdjustable().setValue(e.getAdjustable().getMaximum());
                }
            }
        });

		txtInput = new JTextField();
		txtInput.setBounds(10, 475, 250, 40);
		add(txtInput);
		txtInput.setColumns(10);

		btnSend = makeButton("전송", 70, 40, 270, 475);
		add(btnSend);
		
		lblUserName = new JLabel("Name");
		lblUserName.setHorizontalAlignment(SwingConstants.CENTER);
		lblUserName.setBounds(12, 364, 62, 40);
		add(lblUserName);
		

		//AppendText("User " + username + " connecting " + ip_addr + " " + port_no + "\n");
		UserName = username;
		lblUserName.setText(username + " >");

        try {
            socket = new Socket(ip_addr, Integer.parseInt(port_no));
            is = socket.getInputStream();
            dis = new DataInputStream(is);
            os = socket.getOutputStream();
            dos = new DataOutputStream(os);

            SendMessage("/login " + UserName);
            SendMessage("/createroom " + this.groupMembers);
            ListenNetwork net = new ListenNetwork();
            net.start();
            Myaction action = new Myaction();
            btnSend.addActionListener(action); 
            txtInput.addActionListener(action);
            txtInput.requestFocus();
        } catch (NumberFormatException | IOException e) {
            e.printStackTrace();
            //AppendText("connect error");
        }
    }

    // Server Message를 수신해서 화면에 표시
	//이거를 로그인 접속 시에 뜨느거로 옮기는 게 낫나?
    class ListenNetwork extends Thread {
        public void run() {
            while (true) {
                try {
                    // Use readUTF to read messages
                    String msg = dis.readUTF();
                    //AppendText(msg);
                    boolean isMine = msg.startsWith("[" + UserName + "]");
                    
                    // 메시지 내용만 추출 
                    String content = msg.substring(msg.indexOf("] ") + 2).trim();
                    
                    // messageContainer에 메시지 버블 추가
                    messageContainer.addMessage(content, isMine);
                } catch (IOException e) {
                   // AppendText("dis.read() error");
                    try {
                        dos.close();
                        dis.close();
                        socket.close();
                        break;
                    } catch (Exception ee) {
                        break;
                    }
                }
            }
        }
    }

	// 메시지를 입력 후 Send 버튼 또는  keyboard enter key를 치면 서버로(다른 사용자에게) 전송
	class Myaction implements ActionListener // 내부클래스로 액션 이벤트 처리 클래스
	{
		@Override
		public void actionPerformed(ActionEvent e) {
			// Send button을 누르거나 메시지 입력하고 Enter key 치면
			if (e.getSource() == btnSend || e.getSource() == txtInput) {
				String msg = txtInput.getText();		
				SendMessage(msg + "\n");
				txtInput.setText(""); // 메세지를 보내고 나면 메세지 쓰는창을 비운다.
				txtInput.requestFocus(); // 메세지를 보내고 커서를 다시 텍스트 필드로 위치시킨다
				if (msg.contains("/exit")) // 종료 처리
					System.exit(0);
				// 지금은 system.exit로 창까지 닫았으나(이 방법은 테스트용으로 쓰는 것은 괜찮으나), 
				// 실무적으로는 소켓만 닫고(단톡방 퇴장) GUI는 사용자가 닫게 하거나,
				// "/exit"를 입력하는 순간, 로그 메시지를 내보내서 GUI 종료를 유도하는 방법이 보통의 표준적인 패턴
			}
		}
	}

    // 화면에 출력
    /*public void AppendText(String msg) {
        textArea.append(msg);
        textArea.setCaretPosition(textArea.getText().length());
    }
*/

    // Server에게 network로 전송
    public void SendMessage(String msg) {
        try {
            // Use writeUTF to send messages
            dos.writeUTF(msg);
        } catch (IOException e) {
           // AppendText("dos.write() error");
            try {
                dos.close();
                dis.close();
                socket.close();
            } catch (IOException e1) {
                e1.printStackTrace();
                System.exit(0);
            }
        }
    }
    
    private JButton makeButton(String text, int width, int height, int x, int y) {
        JButton btn = new JButton(text);
        btn.setBounds(x, y, width, height);
        btn.setBackground(Color.WHITE); 
        btn.setBorder(new LineBorder(Color.BLACK));
        btn.setFont(fontSource.getFont(12f));
        
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
