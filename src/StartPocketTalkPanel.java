import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class StartPocketTalkPanel extends JPanel {

    private Image backgroundImg = null;
    private JTextField txtUserName, txtIpAddress, txtPortNumber = null;
    private JButton loginButton = null;
    private ImageIcon customIcon = new ImageIcon("/Images/metamong.png"); 

    public StartPocketTalkPanel(JFrame mainFrame, String initialBackground) {
    	this.backgroundImg = new ImageIcon(getClass().getResource("/" + initialBackground)).getImage();

        setLayout(null);
        
        Font textFont = new Font("맑은 고딕", Font.PLAIN, 14); 

        txtUserName = createTextField("", 175, 168, 130, 33, textFont); 
        txtIpAddress = createTextField("127.0.0.1", 175, 250, 130, 33, textFont); 
        txtPortNumber = createTextField("30000", 175, 330, 130, 33, textFont); 
        
        loginButton = createButton(42, 397, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onLogin(mainFrame);
            }
        });
        
        add(txtUserName);
        add(txtIpAddress);
        add(txtPortNumber);
        add(loginButton);
    }
    
    private JTextField createTextField(String text, int x, int y, int w, int h, Font font) {
        JTextField field = new JTextField(text);
        field.setHorizontalAlignment(SwingConstants.CENTER);
        field.setBounds(x, y, w, h);
        field.setColumns(10);
        field.setOpaque(false);
        field.setBorder(BorderFactory.createEmptyBorder()); // 테두리 제거
        field.setForeground(Color.BLACK);
        field.setFont(font);
        return field;
    }

    private JButton createButton(int x, int y, ActionListener action) {

        JButton button = new JButton(); 
        button.setBounds(x, y, 275, 42); 
        button.setOpaque(false); 
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.addActionListener(action);
        return button;
    }
    
    // 로그인 값이 제대로 입력됐는지 확인
    private void onLogin(JFrame mainFrame) {
        String username = txtUserName.getText().trim();
        String ip_adr = txtIpAddress.getText().trim();
        String port_no  = txtPortNumber.getText().trim();

        // 이름 확인
        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "이름을 입력하세요.", "이름 입력 오류", JOptionPane.WARNING_MESSAGE);
            txtUserName.requestFocus();
            return;
        }

        // IP 주소 확인
        if (!ip_adr.matches("\\d+\\.\\d+\\.\\d+\\.\\d+")) {
            JOptionPane.showMessageDialog(this, "IP 주소 형식이 올바르지 않습니다.\n예) 127.0.0.1", "IP 주소 입력 오류", JOptionPane.WARNING_MESSAGE);
            txtIpAddress.requestFocus();
            return;
        }

        // 포트 번호 확인
        try {
            int port = Integer.parseInt(port_no);
            if (port < 1 || port > 65535) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "포트 번호는 1~65535 사이의 숫자여야 합니다.", "Port 번호 입력 오류", JOptionPane.WARNING_MESSAGE);
            txtPortNumber.requestFocus();
            return;
        }

        showClientPanel(mainFrame, username, ip_adr, port_no);
    }
 
    // 컨텐트팬을 ClientFriendsMenu으로 교체
    private void showClientPanel(JFrame mainFrame, String username, String ip_addr, String port_no) {
    	mainFrame.dispose();
    	ClientMenuFrame cmf = new ClientMenuFrame(username, ip_addr, port_no); // JFrame이어야 함
    	cmf.setVisible(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImg != null) {
            g.drawImage(backgroundImg, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
