// ProfileEditPanel.java
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.*;
import javax.swing.*;

public class ProfileEditPanel extends JPanel {
    private Image backgroundImg;
    private ImageIcon okicon1, okicon2, cancelicon1, cancelicon2;
    private JButton okbutton1, okbutton2, cancelbutton1, cancelbutton2;
    private JTextField name, message;

    // 부모는 ProfileEditFrame
    private final ProfileEditFrame parentFrame;
    private FontSource fontSource = new FontSource("/IM_Hyemin-Bold.ttf"); // 폰트
    private JLabel editProfileLabel;
    
    public ProfileEditPanel(ProfileEditFrame parentFrame, String username, String ip_addr, String port_no) {
        this.parentFrame = parentFrame;

        setLayout(null);
        setOpaque(true);

        setBackground(Color.decode("#F9F9F9"));
        
        editProfileLabel = new JLabel("프로필 수정", SwingConstants.LEFT);
        editProfileLabel.setFont(fontSource.getFont(16f));
        editProfileLabel.setForeground(Color.BLACK);
        editProfileLabel.setBounds(25, 20, 100, 20);
        add(editProfileLabel);

        // 아이콘 로드
        okicon1 = new ImageIcon(getClass().getResource("/okicon1.png"));
        okicon2 = new ImageIcon(getClass().getResource("/okicon2.png"));
        cancelicon1 = new ImageIcon(getClass().getResource("/cancelicon1.png"));
        cancelicon2 = new ImageIcon(getClass().getResource("/cancelicon2.png"));

        // 버튼 생성
        okbutton1 = makeButton(okicon1, 150, 395);
        okbutton2 = makeButton(okicon2, 150, 395);
        cancelbutton1 = makeButton(cancelicon1, 230, 395);
        cancelbutton2 = makeButton(cancelicon2, 230, 395);

        add(okbutton1);
        add(okbutton2);
        add(cancelbutton1);
        add(cancelbutton2);

        // 처음에는 hover용 버튼 숨기기
        okbutton2.setVisible(false);
        cancelbutton2.setVisible(false);

        // 확인 버튼 호버
        okbutton1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                okbutton1.setVisible(false);
                okbutton2.setVisible(true);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                okbutton2.setVisible(false);
                okbutton1.setVisible(true);
            }
        });

        // 취소 버튼 호버
        cancelbutton1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                cancelbutton1.setVisible(false);
                cancelbutton2.setVisible(true);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                cancelbutton2.setVisible(false);
                cancelbutton1.setVisible(true);
            }
        });

        // 클릭 동작 (okbutton2에만 리스너 추가)
        okbutton2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parentFrame.dispose(); // 저장 후 창 닫기 
                ClientMenuFrame root = (ClientMenuFrame) SwingUtilities.getWindowAncestor(parentFrame.getParentPanel());
                root.showFriendsMenu();
            }
        });

        cancelbutton2.addActionListener(new ActionListener() {
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

        // 상태 메시지 입력 필드
        message = new JTextField("상태메시지");
        message.setForeground(Color.gray);
        message.setBorder(null);
        message.setOpaque(false);
        message.setBounds(33, 235, 150, 30);

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

        // 상태메시지 포커스 이벤트
        message.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (message.getText().equals("상태메시지")) {
                    message.setText("");
                    message.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (message.getText().isEmpty()) {
                    message.setText("상태메시지");
                    message.setForeground(Color.GRAY);
                }
            }
        });

        add(name);
        add(message);
    }

    // 버튼 생성 함수
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
