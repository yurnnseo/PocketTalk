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
import javax.swing.SwingUtilities;
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
    private JDialog loadingDialog;
    private boolean isGameInitiator = false; //게임 요청 판단
    private String groupMembers; // 그룹채팅 멤버 문자열
    private String creatorName;
    private MessageContainerPanel messageContainer;
    private String opponent;
    private final String roomId;
    private JScrollPane scrollPane;
    
    private final ClientMenuFrame parentFrame;
    private final ChattingMessageHandler messageProcessor;
    
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
            String roomId,
            String groupMembers,
            String creatorName) {

        this.parentFrame = parentFrame;
        this.groupMembers = groupMembers;
        this.creatorName = creatorName;
        this.UserName = username;
        this.roomId = roomId;
        this.messageProcessor = new ChattingMessageHandler(this, username);
        
        setBorder(new EmptyBorder(5, 5, 5, 5));
        setLayout(null);
        setBackground(Color.decode("#F9F9F9"));

        messageContainer = new MessageContainerPanel(FontSource.get(13f));
        scrollPane = new JScrollPane(messageContainer);
        scrollPane.setBounds(12, 10, 356, 465);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        add(scrollPane);

        // 하단 영역
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

        // 전송 버튼
        btnSend = UIComponentZip.createTextButton("전송", 300, 485, 55, 30, FontSource.get(12f));
        add(btnSend);
  
        // 시스템 초대 메시지
        showInviteMessage();

        // 리스너들
        Myaction action = new Myaction();
        btnSend.addActionListener(action);
        txtInput.addActionListener(action);
        txtInput.requestFocus();

        btnSendImage.addActionListener(e -> onClickSendImage());
        btnSendEmoji.addActionListener(e -> onClickSendEmoji());
        btnPlayGame.addActionListener(e -> {         
        	int memberCount = getGroupMemberCount();
            
            if (memberCount != 2) {
                // 단체 채팅방이거나 (멤버가 2명 초과) 자기 자신만 있는 경우 (멤버가 1명)
                JOptionPane.showMessageDialog(
                    parentFrame, 
                    "현재 채팅방은 " + memberCount + "명으로, 1:1 게임만 가능합니다.", 
                    "게임 참가 불가", 
                    JOptionPane.WARNING_MESSAGE
                );
                return;
            }
            
         // 1. 참여자 이름 배열 생성 
            String[] names = groupMembers.trim().split("\\s+");
            opponent = "";

            // 2. 상대방 이름 찾기
            for (String name : names) {
                if (!name.equals(UserName) && !name.isEmpty()) {
                    opponent = name;
                    break;
                }
            }

            // 3. 메시지 전송 시 순서를 [나의 이름] [상대방 이름]으로 강제 재정렬
            String membersToSend;
            if (opponent.isEmpty()) {
                // 1명일 경우
                membersToSend = UserName; 
            } else {
                // 항상 [내 이름] [상대방 이름] 순서로
                membersToSend = UserName + " " + opponent; 
            }
            
            final String participants = membersToSend;
        	isGameInitiator = true;
        	// 서버에 게임 참가 요청 전송  
            parentFrame.sendToServer("/game_request " + membersToSend + "\n");
        	
        	loadingDialog = new JDialog(parentFrame, "잠시 대기", false);
            JLabel loadingLabel = new JLabel("참가자를 기다리는 중...", SwingConstants.CENTER);
            loadingDialog.add(loadingLabel);
            
            loadingDialog.setSize(200, 150);      
            loadingDialog.setLocationRelativeTo(null);
            loadingDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
 
            //서버에게 취소 요청
            loadingDialog.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                    if (isGameInitiator) { // 내가 요청자일 때만 취소 요청
                        parentFrame.sendToServer("/game_cancel " + participants + "\n");
                        loadingDialog = null;
                        isGameInitiator = false;
                    }
                }
            });
            
            
            loadingDialog.setVisible(true);
        });
    }

    // 초대 시스템 메시지
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

        addTextMessage(text, false);
    }

    // 서버에서 온 일반 채팅 메시지 처리
    public void onReceiveChatMessage(String msg) {
    	messageProcessor.processMessage(msg);
    }

    // 텍스트 메시지 보내기
    class Myaction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == btnSend || e.getSource() == txtInput) {
                String msg = txtInput.getText();
                if (msg == null || msg.trim().isEmpty()) return;

                parentFrame.sendToServer("/msg " + roomId + " " + msg + "\n");

                txtInput.setText("");
                txtInput.requestFocus();
            }
        }
    }
    
    // 항상 채팅 맨 아래로 스크롤
    private void scrollToBottom() {
        SwingUtilities.invokeLater(() -> {
            scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
        });
    }
    
    // 텍스트 말풍선 추가 + 자동 스크롤
    public void addTextMessage(String text, boolean isSent) {
        messageContainer.addMessage(text, isSent);
        scrollToBottom();
    }

    // 이미지 말풍선 추가 + 자동 스크롤
    public void addImageMessage(ImageIcon icon, boolean isSent) {
        messageContainer.addImageMessage(icon, isSent);
        scrollToBottom();
    }


    // 이미지 전송
    private void onClickSendImage() {
        JFileChooser chooser = new JFileChooser();
        int result = chooser.showOpenDialog(ChattingPanel.this);
        if (result != JFileChooser.APPROVE_OPTION) return;

        File file = chooser.getSelectedFile();
        if (file == null) return;
       
        String command = "/msg " + roomId + " /image " + file.getAbsolutePath();
        parentFrame.sendToServer(command + "\n");
    }


    // 이모티콘 버튼 눌렀을 때 선택 창 띄우기
    private void onClickSendEmoji() {
        EmojiDialog dialog = new EmojiDialog();
        
        int dw = dialog.getWidth();
        int dh = dialog.getHeight();
        
        // ChattingPanel의 화면 좌표 가져오기
        Point p = ChattingPanel.this.getLocationOnScreen();
        int px = p.x;
        int py = p.y;
        int pw = ChattingPanel.this.getWidth();
        int ph = ChattingPanel.this.getHeight();
          
        int x = px + pw - dw;       
        int y = py + ph - dh;
        
        dialog.setLocation(x, y);
        dialog.setVisible(true);
    }

    // PNG 아이콘 버튼
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
    public ImageIcon loadScaledIcon(String resourcePath, int w, int h) {
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
    public ImageIcon loadScaledIconFromFile(String filePath, int w, int h) {
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


    // 이모티콘 선택 다이얼로그
    private class EmojiDialog extends JDialog {
        public EmojiDialog() {
            // modal dialog 로 띄우기
            super((java.awt.Frame) null, "이모티콘", true);

            setLayout(new BorderLayout());
            
            JPanel emojiPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 20));
            emojiPanel.setBackground(Color.WHITE);

            JButton emo1 = createEmojiButton(EMOTICON_HAPPY, "happy");
            JButton emo2 = createEmojiButton(EMOTICON_HELLO, "hello");
            JButton emo3 = createEmojiButton(EMOTICON_REST, "rest");
            JButton emo4 = createEmojiButton(EMOTICON_SAD, "sad");

            emojiPanel.add(emo1);
            emojiPanel.add(emo2);
            emojiPanel.add(emo3);
            emojiPanel.add(emo4);

            add(emojiPanel, BorderLayout.CENTER);
            
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
                // 서버로 이모티콘 전송 (방 ID 포함)
                parentFrame.sendToServer("/msg " + roomId + " /emoji " + code + "\n");
                dispose();
            });


            return btn;
        }
    }
    
    private int getGroupMemberCount() {
        // 쉼표로 구분된 문자열을 공백으로 먼저 치환하여 처리 용이하게 함
        String normalizedMembers = groupMembers.replace(",", " ").trim(); 
            
        String[] names = normalizedMembers.split("\\s+");
        
        // 유효한 이름만 카운트
        int count = 0;
        for (String name : names) {
            if (!name.trim().isEmpty()) {
                count++;
            }
        }
        return count;
    }
    
    // 게임 프레임 호출
    public void openGameFrame(String gameData) {
        String[] participants = gameData.split("\\s+"); 
        String opponentName = "";

        // 상대방 이름 추출
        if (participants.length == 2) {
            // 내가 UserA면 상대는 UserB, 내가 UserB면 상대는 UserA
            opponentName = participants[0].equals(UserName) ? participants[1] : participants[0];
        } else {
            JOptionPane.showMessageDialog(parentFrame, "게임 참가자 정보 오류!", "오류", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {        	
        	// ClientMenuFrame을 넘겨서 게임 중에도 서버와 통신할 수 있도록 해야 함
        	new MiniGameFrame(parentFrame, UserName, opponentName, roomId).setVisible(true);
             if (loadingDialog != null) {
                 SwingUtilities.invokeLater(() -> {
                     if (loadingDialog != null) {
                         loadingDialog.dispose(); 
                         loadingDialog = null;  
                     }
                 });
                 // 요청자 상태 초기화
                 isGameInitiator = false; 
             }

        } catch (Exception e) {
            System.err.println("게임 프레임 생성 오류: " + e.getMessage());
            JOptionPane.showMessageDialog(parentFrame, "게임 로딩 중 오류가 발생했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void closeLoadingDialog() {
        if (loadingDialog != null) {
            SwingUtilities.invokeLater(() -> {
                if (loadingDialog != null) {
                    loadingDialog.dispose();
                    loadingDialog = null;
                }
            });
            isGameInitiator = false; // 상태 초기화
        }
    }
}
