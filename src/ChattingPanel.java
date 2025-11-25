import java.awt.Color;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class ChattingPanel extends JPanel {
    private JTextField txtInput;
    private String UserName;
    private JButton btnSend;
    private JButton btnSendImage;
    private JButton btnSendEmoji;
    private JButton btnPlayGame;
    private JLabel lblUserName;

    private String groupMembers; // 그룹채팅 멤버 문자열
    private String creatorName;
    private MessageContainerPanel messageContainer;

    private final ClientMenuFrame parentFrame;

    // 하단 아이콘버튼 PNG 경로 
    private static final String IMG_SEND_IMAGE = "/Images/sendImagebutton.png";
    private static final String IMG_SEND_EMOJI = "/Images/sendEmogibutton.png";
    private static final String IMG_PLAY_GAME  = "/Images/playgamebutton.png";

    // 이모티콘 선택창에서 쓸 PNG 경로
    private static final String EMOTICON_HAPPY = "/Images/Emoticon_happy.png";
    private static final String EMOTICON_HELLO = "/Images/Emoticon_hello.png";
    private static final String EMOTICON_REST = "/Images/Emoticon_rest.png";
    private static final String EMOTICON_SAD = "/Images/Emoticon_sad.png";
    
    public ChattingPanel(ClientMenuFrame parentFrame,
                         String username,
                         String groupMembers,
                         String creatorName) {

        this.parentFrame = parentFrame;
        this.groupMembers = groupMembers;
        this.creatorName = creatorName;
        this.UserName = username;

        setBorder(new EmptyBorder(5, 5, 5, 5));
        setLayout(null);
        setBackground(Color.decode("#F9F9F9"));

        messageContainer = new MessageContainerPanel(FontSource.get(13f));
        JScrollPane scrollPane = new JScrollPane(messageContainer);
        scrollPane.setBounds(12, 10, 356, 465);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        add(scrollPane);

        // 메시지 추가될 때마다 스크롤 자동 내림
        scrollPane.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
            public void adjustmentValueChanged(AdjustmentEvent e) {
                if (e.getAdjustable().getMaximum()
                        == e.getValue() + e.getAdjustable().getVisibleAmount()) {
                    e.getAdjustable().setValue(e.getAdjustable().getMaximum());
                }
            }
        });

        // ----- 하단 영역 -----
        lblUserName = new JLabel("Name");
        lblUserName.setHorizontalAlignment(SwingConstants.CENTER);
        lblUserName.setBounds(0, 485, 52, 30);
        add(lblUserName);
        lblUserName.setText(UserName + " >");

        // 이미지 전송 아이콘 버튼
        btnSendImage = UIComponentZip.createIconButton(IMG_SEND_IMAGE, 18, 48, 490);
        add(btnSendImage);

        // 입력창
        txtInput = new JTextField();
        txtInput.setBounds(74, 484, 169, 30);
        add(txtInput);
        txtInput.setColumns(10);

        // 게임 아이콘 버튼
        btnPlayGame = UIComponentZip.createIconButton(IMG_PLAY_GAME, 20, 247, 490);
        add(btnPlayGame);

        // 이모티콘 아이콘 버튼
        btnSendEmoji = UIComponentZip.createIconButton(IMG_SEND_EMOJI, 18, 277, 490);
        add(btnSendEmoji);

        // 전송 버튼(텍스트)
        btnSend = UIComponentZip.createTextButton("전송", 300, 485, 55, 30, FontSource.get(12f));
        add(btnSend);
        // ---- 하단 영역 끝 ----

        
        // ----- 방장 처리 -----
        boolean isCreator = UserName.equals(creatorName);
        if (isCreator) {
            parentFrame.sendToServer("/createroom " + this.groupMembers);
        }

        // 시스템 초대 메시지
        showInviteMessage();

        // ----- 리스너들 -----
        Myaction action = new Myaction();
        btnSend.addActionListener(action);
        txtInput.addActionListener(action);
        txtInput.requestFocus();

        btnSendImage.addActionListener(e -> onClickSendImage());
        btnSendEmoji.addActionListener(e -> onClickSendEmoji());
        btnPlayGame.addActionListener(e -> {
            // TODO: 여기서 게임 시작 로직 구현
        	JDialog loadingDialog = new JDialog(parentFrame, "잠시 대기", true);
            
            // 2. 다이얼로그에 표시할 내용 
            JLabel loadingLabel = new JLabel("참가자를 기다리는 중", SwingConstants.CENTER);
            loadingDialog.add(loadingLabel);
            
            // 3. 다이얼로그 크기 설정
            loadingDialog.setSize(200, 150);
            
            // 4. 다이얼로그 위치 설정 (화면 중앙에 표시)
            loadingDialog.setLocationRelativeTo(null);
            
            // 5. 다이얼로그 닫기 버튼==대기 취소
            loadingDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            
            // 6. 다이얼로그 표시 (이 시점부터 다이얼로그가 닫힐 때까지 호출을 막습니다: Modal)
            loadingDialog.setVisible(true);
            //loadingDialog.dispose();
        });
    }

    // ===== 초대 시스템 메시지 =====
    private void showInviteMessage() {
        String[] names = groupMembers.split("\\s+");
        StringBuilder invited = new StringBuilder();
        for (String n : names) {
            n = n.trim();
            if (n.isEmpty()) continue;
            if (n.equals(creatorName)) continue;
            if (invited.length() > 0) invited.append(", ");
            invited.append(n);
        }

        String text;
        if (invited.length() == 0) {
            text = creatorName + "님이 채팅방을 생성했습니다.";
        } 
        else {
            text = creatorName + "님이 " + invited + "님을 초대했습니다.";
        }

        messageContainer.addMessage(text, false);
    }

    // ===== 서버에서 온 일반 채팅 메시지 처리 =====
    public void onReceiveChatMessage(String msg) {
        if (msg == null) return;
        msg = msg.trim();

        if (msg.isEmpty()) return; // 제어 메시지 방어

        boolean isMine = false;
        String content = msg;
        String sender = null;

        int idx = msg.indexOf(" : ");
        if (idx > 0) {
            sender = msg.substring(0, idx).trim();
            content = msg.substring(idx + 3).trim();
            isMine = sender.equals(UserName);
        }

        // --- 이모티콘 처리 ---
        if (content.startsWith("/emoji ")) {
            String code = content.substring("/emoji ".length()).trim();

            String path;
            switch (code) {
                case "happy": path = EMOTICON_HAPPY; break;
                case "hello": path = EMOTICON_HELLO; break;
                case "rest":  path = EMOTICON_REST;  break;
                case "sad":   path = EMOTICON_SAD;   break;
                default:
                    // 알 수 없는 코드는 텍스트로 표시
                    messageContainer.addMessage((sender != null ? sender + " : " : "") + content, isMine);
                    return;
            }

            ImageIcon icon = loadScaledIcon(path, 90, 90);
            if (icon != null) {
                messageContainer.addImageMessage(icon, isMine);
            } 
            else {
                // 로드 실패 시 텍스트로 표시
                messageContainer.addMessage((sender != null ? sender + " : " : "") + "[이모티콘 로드 실패: " + code + "]", isMine);
            }
            return;
        }

        // --- 사진 처리 ---
        if (content.startsWith("/image ")) {
            String filePath = content.substring("/image ".length()).trim();

            ImageIcon icon = loadScaledIconFromFile(filePath, 180, 180); // 크기 조절
            if (icon != null) {
                messageContainer.addImageMessage(icon, isMine);
            } 
            else {
                messageContainer.addMessage((sender != null ? sender + " : " : "") + "[이미지 로드 실패]", isMine);
            }
            return;
        }

        // --- 그 외 일반 텍스트 메시지 ---
        String bubbleText;
        if (sender != null) bubbleText = sender + " : " + content;
        else bubbleText = content;

        messageContainer.addMessage(bubbleText, isMine);
    }


    // ===== 텍스트 메시지 보내기 =====
    class Myaction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == btnSend || e.getSource() == txtInput) {
                String msg = txtInput.getText();
                if (msg == null || msg.trim().isEmpty()) return;

                parentFrame.sendToServer(msg + "\n");

                txtInput.setText("");
                txtInput.requestFocus();
            }
        }
    }

    // ===== 이미지 전송 =====
    private void onClickSendImage() {
        JFileChooser chooser = new JFileChooser();
        int result = chooser.showOpenDialog(ChattingPanel.this);
        if (result != JFileChooser.APPROVE_OPTION) return;

        File file = chooser.getSelectedFile();
        if (file == null) return;

        // 서버로만 /image 명령을 전송하고 → 서버 브로드캐스트를 onReceiveChatMessage()에서 처리
        String command = "/image " + file.getAbsolutePath();
        parentFrame.sendToServer(command + "\n");
    }


    // ===== 이모티콘 버튼 눌렀을 때 선택 창 띄우기 =====
    private void onClickSendEmoji() {
        EmojiDialog dialog = new EmojiDialog();
        
        // 다이얼로그 크기 가져오기
        int dw = dialog.getWidth();
        int dh = dialog.getHeight();
        
        // ChattingPanel의 화면 좌표 가져오기
        Point p = ChattingPanel.this.getLocationOnScreen();
        int px = p.x;
        int py = p.y;
        int pw = ChattingPanel.this.getWidth();
        int ph = ChattingPanel.this.getHeight();
        
        // 채팅창의 오른쪽 끝 근처에 위치시키기
        int x = px + pw - dw;
        // 아래쪽에 살짝 띄워서 배치
        int y = py + ph - dh;
        
        dialog.setLocation(x, y);
        dialog.setVisible(true);
    }

    // ===== PNG 아이콘 버튼 =====
    private JButton makeIconButton(String resourcePath, int size, int x, int y) {
        ImageIcon icon = loadScaledIcon(resourcePath, size, size);

        JButton btn = new JButton(icon);
        btn.setBounds(x, y, size, size);

        // 테두리/배경 제거
        btn.setBorder(null);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        return btn;
    }


    // 아이콘 로더
    private ImageIcon loadScaledIcon(String resourcePath, int w, int h) {
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource(resourcePath));
            Image scaled = icon.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
            return new ImageIcon(scaled);
        } catch (Exception e) {
            System.out.println("아이콘 로드 실패: " + resourcePath + " → " + e);
            return null;
        }
    }

    // 파일 경로에서 아이콘 로드 (사진용)
    private ImageIcon loadScaledIconFromFile(String filePath, int w, int h) {
        try {
            ImageIcon icon = new ImageIcon(filePath);
            Image img = icon.getImage();
            if (w > 0 && h > 0) {
                img = img.getScaledInstance(w, h, Image.SCALE_SMOOTH);
            }
            return new ImageIcon(img);
        } catch (Exception e) {
            System.out.println("이미지 파일 로드 실패: " + filePath + " → " + e);
            return null;
        }
    }


    //   이모티콘 선택 다이얼로그
    private class EmojiDialog extends JDialog {
        public EmojiDialog() {
            // 부모 프레임을 찾아서 modal dialog 로 띄우기
            super((java.awt.Frame) null, "이모티콘", true);

            setLayout(new BorderLayout());
            
            JPanel emojiPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 20));
            emojiPanel.setBackground(Color.WHITE);

            // 이모티콘 3개 버튼
            JButton emo1 = createEmojiButton(EMOTICON_HAPPY, "happy");
            JButton emo2 = createEmojiButton(EMOTICON_HELLO, "hello");
            JButton emo3 = createEmojiButton(EMOTICON_REST, "rest");
            JButton emo4 = createEmojiButton(EMOTICON_SAD, "sad");

            emojiPanel.add(emo1);
            emojiPanel.add(emo2);
            emojiPanel.add(emo3);
            emojiPanel.add(emo4);

            add(emojiPanel, BorderLayout.CENTER);

//            // 닫기 버튼 (닫기 버튼 해 말아?? x 누르면 되긴 해)
//            JButton closeBtn = new JButton("닫기");
//            closeBtn.setBackground(Color.decode("#FFD54F"));
//            closeBtn.setBorder(new LineBorder(Color.YELLOW.darker()));
//            closeBtn.setFocusPainted(false);
//            closeBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
//            closeBtn.addActionListener(e -> dispose());

//            JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
//            bottom.setBackground(Color.WHITE);
//            bottom.add(closeBtn);
//
//            add(bottom, BorderLayout.SOUTH);

            setSize(480, 150);
            setResizable(false);
        }

        // 이모티콘용 버튼 (PNG + 클릭 시 전송)
        private JButton createEmojiButton(String imgPath, String code) {
            ImageIcon icon = loadScaledIcon(imgPath, 90, 90); // 크기 적당히
            JButton btn = new JButton(icon);
            btn.setBorder(null);
            btn.setBorderPainted(false);
            btn.setContentAreaFilled(false);
            btn.setFocusPainted(false);
            btn.setOpaque(false);
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            btn.addActionListener(e -> {
                // 서버로 이모티콘 전송
                parentFrame.sendToServer("/emoji " + code + "\n");

                dispose();
            });

            return btn;
        }

    }
}
