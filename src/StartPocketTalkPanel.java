import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class StartPocketTalkPanel extends JPanel {

    private Image backgroundImg = null;
    private JTextField txtUserName, txtIpAddress, txtPortNumber = null;
    private JButton loginButton = null;
    private ImageIcon customIcon = new ImageIcon("metamong.png"); // 사용자 정의 아이콘 (다이얼로그에서 사용)

    // 생성자
    public StartPocketTalkPanel(JFrame mainFrame, String initialBackground) {
        // 로그인창 배경 이미지 불러오기
    	// 클래스패스 기준으로 불러오기
    	this.backgroundImg = new ImageIcon(getClass().getResource("/" + initialBackground)).getImage();

        setLayout(null);
        
        Font textFont = new Font("맑은 고딕", Font.PLAIN, 14); // 공통 폰트 설정 (일반체)

        txtUserName = createTextField("", 175, 168, 130, 33, textFont); // 이름 입력 TextField 생성
        txtIpAddress = createTextField("127.0.0.1", 175, 250, 130, 33, textFont); // IP주소 입력 TextField 생성
        txtPortNumber = createTextField("30000", 175, 330, 130, 33, textFont); // Port번호 입력 TextField 생성
        
//        // 로그인 버튼 생성 -> 로그인 버튼이 눌리면 showClientPanel 함수 실행
//        loginButton = createButton("loginButton.png", 42, 397, new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//            	showClientPanel(mainFrame); 
//            }
//        });
        
        // 로그인 버튼 생성 
        loginButton = createButton(42, 397, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onLogin(mainFrame);
            }
        });
        
        // 텍스트 필드 3개와 로그인 버튼 패널에 추가
        add(txtUserName);
        add(txtIpAddress);
        add(txtPortNumber);
        add(loginButton);
    }
    
    // 공통 텍스트필드 생성 함수
    private JTextField createTextField(String text, int x, int y, int w, int h, Font font) {
        JTextField field = new JTextField(text);
        field.setHorizontalAlignment(SwingConstants.CENTER);
        field.setBounds(x, y, w, h);
        field.setColumns(10);
        field.setOpaque(false); // 배경 투명
        field.setBorder(BorderFactory.createEmptyBorder()); // 테두리 제거
        field.setForeground(Color.BLACK);
        field.setFont(font);
        return field;
    }

    
//    // 버튼 생성 메소드 (이미지 크기를 버튼 크기에 맞게 스케일) -> 이렇게 했더니 이미지가 깨져서 주석처리함.
//    private JButton createButton(String iconPath, int x, int y, ActionListener action) {
//
//        // 이미지 불러오기
//        ImageIcon icon = new ImageIcon(iconPath);
//        Image scaledImage = icon.getImage().getScaledInstance(275, 42, Image.SCALE_SMOOTH);
//        ImageIcon scaledIcon = new ImageIcon(scaledImage);
//
//        JButton button = new JButton(scaledIcon);
//        button.setBounds(x, y, bw, bh);  // 버튼 위치와 크기
//        button.setContentAreaFilled(false);  // 배경 제거
//        button.setBorderPainted(false);      // 테두리 제거
//        button.setFocusPainted(false);       // 포커스 제거
//        button.addActionListener(action);
//        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // 마우스 손모양
//
//        return button;
//    }
    
    // 버튼 생성 메소드 (이미지 없이 완전 투명 버튼)
    private JButton createButton(int x, int y, ActionListener action) {

        JButton button = new JButton(); // 이미지 없이 버튼 생성
        button.setBounds(x, y, 275, 42); // 위치와 크기 설정
        button.setOpaque(false); // 완전 투명 (배경 없음)
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

        // 입력값이 모두 제대로 입력되면 showClientPanel 함수를 실행해서 ClientFriendsMenu 실행
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
