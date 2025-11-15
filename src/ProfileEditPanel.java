// ProfileEditPanel.java
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.LineBorder;

public class ProfileEditPanel extends JPanel {
    private Image backgroundImg;
    private JButton okbutton,cancelbutton;
    private JTextField name, message;

    // 부모는 ProfileEditFrame
    private final ProfileEditFrame parentFrame;
    private FontSource fontSource = new FontSource("/IM_Hyemin-Bold.ttf"); // 폰트
    private JLabel editProfileLabel;
    
    public ProfileEditPanel(ProfileEditFrame parentFrame, String username, String ip_addr, String port_no, String currentStatusMessage) {
        this.parentFrame = parentFrame;

        setLayout(null);
        setOpaque(true);

        setBackground(Color.decode("#F9F9F9"));
        
        editProfileLabel = new JLabel("나의 프로필 편집", SwingConstants.LEFT);
        editProfileLabel.setFont(fontSource.getFont(16f));
        editProfileLabel.setForeground(Color.BLACK);
        editProfileLabel.setBounds(25, 20, 500, 20);
        add(editProfileLabel);
 
        // 버튼 생성
        okbutton = makeButton("저장",  60, 28, 150, 395);      
        cancelbutton = makeButton("취소",  60, 28, 230, 395);

        add(okbutton);
        add(cancelbutton);
       
        // 클릭 동작
        okbutton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String newName = name.getText().trim();
                String text = message.getText().trim();
                String placeholder = "상태메시지";

                String statusToSend;
                if (text.equals(placeholder) || text.isEmpty()) {
                    statusToSend = "";  // ★ 서버에는 빈 문자열로 저장
                } 
                else {
                    statusToSend = text;
                }

                // ProfileEditFrame 쪽에 “저장됨” 알려주기 (거기서 dos.writeUTF() 하게)
                parentFrame.onProfileSaved(newName, statusToSend);

                parentFrame.dispose();
            }
        });


        cancelbutton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parentFrame.dispose();
            }
        });

        // 이름 입력 필드
        name = new JTextField(username);
        name.setForeground(Color.BLACK);
        name.setBorder(null);
        name.setOpaque(false);
        name.setBounds(33, 168, 150, 50);
        
        // 이름 포커스 이벤트
        name.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (name.getText().equals(username)) {
                    name.setText("");
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (name.getText().isEmpty()) {
                    name.setText(username);
                }
            }
        });

        // 상태 메시지 입력 필드
        String placeholder = "";
        
        // 서버에서 받은 상태메시지가 없으면 placeholder로 보이게
        if (currentStatusMessage == null || currentStatusMessage.isEmpty()) {
            message = new JTextField(placeholder);
            message.setForeground(Color.GRAY);
        } 
        else {
            message = new JTextField(currentStatusMessage);
            message.setForeground(Color.BLACK);
        }
        
        message.setBorder(null);
        message.setOpaque(false);
        message.setBounds(33, 235, 150, 30);
        
        // 상태메시지 포커스 이벤트
        message.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (message.getText().equals(placeholder)) {
                    message.setText("");
                    message.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (message.getText().isEmpty()) {
                    message.setText(placeholder);
                    message.setForeground(Color.GRAY);
                }
            }
        });

        add(name);
        add(message);
    }

    // 버튼 생성 함수
    private JButton makeButton(String text, int width, int height, int x, int y) {
        JButton btn = new JButton(text);
        btn.setBounds(x, y, width, height);
        btn.setBackground(Color.WHITE); 
        btn.setBorder(new LineBorder(Color.BLACK));
        btn.setFont(fontSource.getFont(11f));
        
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

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.BLACK); // 선 색상
        g.fillRect(29, 206, getWidth() - 65, 1);
        g.fillRect(29, 262, getWidth() - 65, 1);
    }
}
