//프로필 수정 패널
import java.awt.Color;
import java.awt.Cursor;
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
    private ImageIcon okicon1, okicon2, cancel;
    private JButton okbutton, okbutton2, cancelbutton;
    private JLabel profilePicLabel, nameLabel;
    private JTextField name, message;
    
    public EditProfile(ClientMenuFrame parentFrame, String username, String ip_addr, String port_no) {
    	setLayout(null);
    	setOpaque(true);
    	// 배경 이미지 로드
        backgroundImg = new ImageIcon(getClass().getResource("/edit.png")).getImage();
        
        // 아이콘
        okicon1  = new ImageIcon(getClass().getResource("/okicon1.png"));
        okicon2  = new ImageIcon(getClass().getResource("/okicon2.png"));
        cancel = new ImageIcon(getClass().getResource("/cancelicon.png"));
        
        okbutton = makeButton(okicon1, 170, 410);
        okbutton2 = makeButton(okicon2, 170, 410);
        cancelbutton = makeButton(cancel, 250, 410);
        
        add(okbutton);
        add(okbutton2);
        add(cancelbutton);
        okbutton2.setVisible(false);
        
        name = new JTextField(username);
        name.setForeground(Color.BLACK);
        name.setBorder(null);
        name.setOpaque(false);
        name.setBounds(53,223,150,50);
        
        message = new JTextField("상태메시지");
        message.setForeground(Color.gray);
        message.setBorder(null);
        message.setOpaque(false);
        message.setBounds(53,318,150,30);
        
        
      //이름 수정
        name.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                //텍스트필드를 클릭했을 때
                if (name.getText().equals(username)) {
                    name.setText(""); 
                    
                    okbutton.setVisible(false);
                    okbutton2.setVisible(true);
                }
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                // 다른 곳을 클릭 시
                if (name.getText().isEmpty()) {
                    //다시 기본값 설정
                    name.setText(username);
                    okbutton2.setVisible(false);
                    okbutton.setVisible(true);
                }
            }
        });
        
        //상태메시지 수정
        message.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                //텍스트필드를 클릭했을 때
                if (message.getText().equals("상태메시지")) {
                    message.setText(""); 
                    message.setForeground(Color.BLACK);
                    
                    okbutton.setVisible(false);
                    okbutton2.setVisible(true);
                }
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                // 다른 곳을 클릭 시
                if (message.getText().isEmpty()) {
                    //다시 기본값 설정
                    message.setForeground(Color.gray);
                    message.setText("상태메시지");
                    okbutton2.setVisible(false);
                    okbutton.setVisible(true);
                }
            }
        });
        
     
        okbutton2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	//변경 상태 저장하는 거는 아직 안 함
            	
            	parentFrame.showFriendsMenu(); //목록으로 돌아가기
            }
        });
        
      
        cancelbutton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	parentFrame.showFriendsMenu(); //목록으로 돌아가기
            }
        });
        
        add(name);
        add(message);
    }
    
    private JButton makeButton(ImageIcon icon, int x, int y) {
        JButton btn = new JButton(icon);
        btn.setBounds(x, y, icon.getIconWidth(), icon.getIconHeight());
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }
  
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImg, 0, 0, getWidth(), getHeight(), this);
    }

}
