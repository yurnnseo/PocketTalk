import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.Image;
import java.io.File; 

// 프로필 수정 내용을 입력하는 패널
public class ProfileEditPanel extends JPanel {
	
    private JButton okbutton, cancelbutton;
    private JTextField name, message;

    // 부모는 ProfileEditFrame
    private final ProfileEditFrame parentFrame;
    
    private JLabel editProfileLabel;
   
    private String selectedImagePath;
    private JButton imageButton;
    
    // 텍스트 색상 정리
    private static final Color TEXT_COLOR = Color.BLACK; // 실제 입력 텍스트
    private static final Color PLACEHOLDER_COLOR = Color.GRAY; // placeholder

    
    public ProfileEditPanel(ProfileEditFrame parentFrame, String username, String ip_addr, String port_no, String currentStatusMessage, String currentProfileImagePath) {
        this.parentFrame = parentFrame;

        setLayout(null);
        setOpaque(true);
        setBackground(Color.decode("#F9F9F9"));

        final String placeholder = "상태메시지";
        
        editProfileLabel = new JLabel("나의 프로필 편집", SwingConstants.LEFT);
        editProfileLabel.setFont(FontSource.get(16f));
        editProfileLabel.setForeground(Color.BLACK);
        editProfileLabel.setBounds(25, 20, 500, 20);
        add(editProfileLabel);
 
        okbutton = UIComponentZip.createTextButton("저장", 150, 395, 60, 28, FontSource.get(11f));
        cancelbutton = UIComponentZip.createTextButton("취소", 230, 395, 60, 28, FontSource.get(11f));

        add(okbutton);
        add(cancelbutton);

        okbutton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String newName = name.getText().trim();
                String text = message.getText().trim();
                
                // placeholder면 빈 문자열로 처리
                String statusToSend = (text.equals(placeholder) || text.isEmpty()) ? "" : text;

                // 프레임에 저장 요청
                parentFrame.onProfileSaved(newName, statusToSend, selectedImagePath);
            }
        });

        // 취소 버튼 누르면 창을 닫음
        cancelbutton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parentFrame.dispose();
            }
        });

        // 이름 입력 필드
        name = new JTextField(username);
        name.setBorder(null);
        name.setOpaque(false);
        name.setBounds(33, 168, 150, 50);
        name.setForeground(TEXT_COLOR);
        
        name.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (name.getText().equals(username)) {
                    name.setText("");
                    name.setForeground(TEXT_COLOR);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (name.getText().isEmpty()) {
                    name.setText(username);
                    name.setForeground(TEXT_COLOR);
                }
            }
        });

        // 상태메시지 입력 필드 초기화
        if (currentStatusMessage == null || currentStatusMessage.isEmpty()) {
            message = new JTextField(placeholder);
            message.setForeground(PLACEHOLDER_COLOR);
        } else {
            message = new JTextField(currentStatusMessage);
            message.setForeground(TEXT_COLOR);
        }

        message.setBounds(33, 235, 150, 30);
        message.setBorder(null);
        message.setOpaque(false);
        
        // 상태메시지 포커스 이벤트
        message.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (message.getText().equals(placeholder)) {
                    message.setText("");
                    message.setForeground(TEXT_COLOR);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (message.getText().isEmpty()) {
                    message.setText(placeholder);
                    message.setForeground(PLACEHOLDER_COLOR);
                }
            }
        });
        
        
        selectedImagePath = (currentProfileImagePath == null || currentProfileImagePath.isEmpty()) ? "/Images/defaultprofileimage.png" : currentProfileImagePath;

        Image img = selectedImagePath.startsWith("/") ? new ImageIcon(getClass().getResource(selectedImagePath)).getImage() : new ImageIcon(selectedImagePath).getImage();

        imageButton = new JButton(new ImageIcon(img.getScaledInstance(80, 80, Image.SCALE_SMOOTH)));

        imageButton.setContentAreaFilled(false);
        imageButton.setBorderPainted(false);
        imageButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        imageButton.setBounds(120, 70, 80, 80);

    	// 이미지 선택
        imageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
	            JFileChooser chooser = new JFileChooser();
	            int result = chooser.showOpenDialog(ProfileEditPanel.this);
	
	            if (result == JFileChooser.APPROVE_OPTION) {
	                File f = chooser.getSelectedFile();
	                selectedImagePath = f.getAbsolutePath();
	
	                Image selImg = new ImageIcon(selectedImagePath).getImage();
	                imageButton.setIcon(
	                        new ImageIcon(selImg.getScaledInstance(80, 80, Image.SCALE_SMOOTH))
	                );
	            }
            }
        });
        

        add(name);
        add(message);
        add(imageButton);
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.BLACK); // 선 색상
        g.fillRect(29, 206, getWidth() - 65, 1);
        g.fillRect(29, 262, getWidth() - 65, 1);
    }
}