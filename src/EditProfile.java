//프로필 수정 패널
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class EditProfile extends JPanel{
	private Image backgroundImg;
    private ImageIcon ok, cancel;
    private JButton okbutton, cancelbutton;
    private JLabel profilePicLabel, nameLabel;
    private JTextField name, message;
    
    public EditProfile(ClientMenuFrame parentFrame, String username, String ip_addr, String port_no) {
    	setLayout(null);
    	setOpaque(true);
    	// 배경 이미지 로드
        backgroundImg = new ImageIcon(getClass().getResource("/edit.png")).getImage();
        
        name = new JTextField(username);
        name.setForeground(Color.BLACK);
        name.setBorder(null);
        name.setOpaque(false);
        name.setBounds(53,240,150,50);
        
        message = new JTextField("상태메시지");
        message.setForeground(Color.gray);
        message.setBorder(null);
        message.setOpaque(false);
        message.setBounds(53,340,150,30);
        
        //상태메시지 수정
        message.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                //텍스트필드를 클릭했을 때
                if (message.getText().equals("상태메시지")) {
                    message.setText(""); 
                    message.setForeground(Color.BLACK);
                    //변경 상태 저장하는 거는 아직 안 함
                }
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                // 다른 곳을 클릭 시
                if (message.getText().isEmpty()) {
                    //다시 기본값 설정
                    message.setForeground(Color.gray);
                    message.setText("상태메시지");
                }
            }
        });
        
        add(name);
        add(message);
    }
  
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImg, 0, 0, getWidth(), getHeight(), this);
    }

}
