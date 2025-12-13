import java.awt.Color;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class ChattingPanel extends JPanel {
	
	// 메시지 입력 창, 전송/이미지/이모티콘/게임 버튼, 전송자 이름
    private JTextField txtInput;
    private JButton btnSend;
    private JButton btnSendImage;
    private JButton btnSendEmoji;
    private JButton btnPlayGame;
    private JLabel lblUserName;
    
    private JScrollPane scrollPane;
    private MessageContainerPanel messageContainer;
    
    private final String roomId;
    private String userName;
    private String groupMembers; 
    private String creatorName;
    
    // 게임 관련 변수
    private boolean isGameInitiator = false; // 게임 요청자 판단
    private String opponent; // 상대 이름
    private JDialog loadingDialog; // 대기창
    
    private final ChattingMessageHandler messageProcessor;

    private final ClientMenuFrame parentFrame;
    
    // 하단 아이콘 버튼 PNG 경로 
    private static final String IMG_SEND_IMAGE = "/Images/sendImagebutton.png";
    private static final String IMG_SEND_EMOJI = "/Images/sendEmogibutton.png";
    private static final String IMG_PLAY_GAME  = "/Images/playgamebutton.png";
    
    public ChattingPanel(ClientMenuFrame parentFrame, String username, String roomId, String groupMembers, String creatorName) {

        this.parentFrame = parentFrame;
        this.groupMembers = groupMembers;
        this.creatorName = creatorName;
        this.userName = username;
        this.roomId = roomId;
        
        // 서버에서 받은 메시지를 어떻게 표시할 지 처리해주는 클래스
        this.messageProcessor = new ChattingMessageHandler(this, username);
        
        setBorder(new EmptyBorder(5, 5, 5, 5));
        setLayout(null);
        setBackground(Color.decode("#F9F9F9"));

        // 메시지 내용 영역
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
        lblUserName.setText(userName + " >");

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
  
        showInviteMessage(); // 처음 채팅방에 들어오면 초대 메시지 띄우기

        // 이벤트 리스너
        Myaction action = new Myaction();
        btnSend.addActionListener(action);
        txtInput.addActionListener(action);
        txtInput.requestFocus();

        btnSendImage.addActionListener(e -> onClickSendImage());
        btnSendEmoji.addActionListener(e -> onClickSendEmoji());
        btnPlayGame.addActionListener(e -> onClickPlayGame());
    }
    
    // 전송 버튼(엔터 포함) 눌렀을 때 이벤트
    class Myaction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
        	
            String msg = txtInput.getText();
            if (msg == null || msg.trim().isEmpty()) return;

            parentFrame.sendToServer("/msg " + roomId + " " + msg + "\n"); // 서버로 보낼 때 "/msg roomId 내용" 형태로 보냄.

            txtInput.setText("");
            txtInput.requestFocus();
        }
    }

	// 처음 채팅방에 들어왔을 때 띄우는 초대 메시지 
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
    
    // 말풍선(텍스트) 추가
    public void addTextMessage(String text, boolean isSent) {
        messageContainer.addMessage(text, isSent);
        scrollToBottom();
    }
    
    // 말풍선(이미지) 추가
    public void addImageMessage(ImageIcon icon, boolean isSent) {
        messageContainer.addImageMessage(icon, isSent);
        scrollToBottom();
    }

    // 항상 스크롤을 맨 아래로 내려줌
    private void scrollToBottom() {
        SwingUtilities.invokeLater(() -> {
            scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
        });
    }

    // 서버에서 온 일반 채팅 메시지 처리
    public void onReceiveChatMessage(String msg) {
    	messageProcessor.processMessage(msg);
    }

    // 이미지 전송
    private void onClickSendImage() {
        JFileChooser chooser = new JFileChooser();
        int result = chooser.showOpenDialog(ChattingPanel.this);
        if (result != JFileChooser.APPROVE_OPTION) return;

        File file = chooser.getSelectedFile();
        if (file == null) return;
       
        // 서버에 "/msg roomId /image 파일경로" 형태로 전송
        String command = "/msg " + roomId + " /image " + file.getAbsolutePath();
        parentFrame.sendToServer(command + "\n");
    }


    // 이모티콘 버튼 눌렀을 때 선택 창 띄우기
    private void onClickSendEmoji() {
    	
    	JFrame owner = (JFrame) SwingUtilities.getWindowAncestor(this);
    	
        ChatEmoticonDialog dialog = new ChatEmoticonDialog(owner, parentFrame, roomId);

        dialog.pack(); // 크기 먼저 계산
        
        // 채팅창 오른쪽 아래에 뜨도록 위치 계산
        Point p = this.getLocationOnScreen();

        int x = p.x + this.getWidth() - dialog.getWidth() - 10;  
        int y = p.y + this.getHeight() - dialog.getHeight() - 10; 

        dialog.setLocation(x, y);
        dialog.setVisible(true);
    }
    
    // 게임 버튼 클릭 처리 
    private void onClickPlayGame() {
        int memberCount = getGroupMemberCount();
        if (memberCount != 2) {
        	JFrame owner = (JFrame) SwingUtilities.getWindowAncestor(this);
            JOptionPane.showMessageDialog(owner, "현재 채팅방은 " + memberCount + "명으로, 1:1 게임만 가능합니다.", "게임 참가 불가", JOptionPane.WARNING_MESSAGE);
            return;
        }

        opponent = findOpponentName();
        String membersToSend = (opponent.isEmpty()) ? userName : (userName + " " + opponent);

        isGameInitiator = true;
        parentFrame.sendToServer("/game_request " + membersToSend + "\n");

        showLoadingDialog(membersToSend);
    }

    // 상대방 이름 찾기 (멤버 중에서 내 이름 아닌 사람)
    private String findOpponentName() {
        String[] names = groupMembers.trim().split("\\s+");
        for (String n : names) {
            if (!n.equals(userName) && !n.isEmpty()) return n;
        }
        return "";
    }

    // 멤버 수 세기
    private int getGroupMemberCount() {
        String normalized = groupMembers.replace(",", " ").trim();
        String[] names = normalized.split("\\s+");

        int count = 0;
        for (String n : names) {
            if (!n.trim().isEmpty()) count++;
        }
        return count;
    }

    // 게임 대기창 띄우기
    private void showLoadingDialog(String participants) {
    	JFrame owner = (JFrame) SwingUtilities.getWindowAncestor(this);
    	
        loadingDialog = new JDialog(owner, "잠시 대기", false);
        loadingDialog.add(new JLabel("참가자를 기다리는 중...", SwingConstants.CENTER));
        loadingDialog.setSize(200, 150);
        
        loadingDialog.setLocationRelativeTo(ChattingPanel.this);
        
        loadingDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        // 대기창 닫으면 서버에 취소 요청 보내기
        loadingDialog.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                if (isGameInitiator) {
                    parentFrame.sendToServer("/game_cancel " + participants + "\n");
                    loadingDialog = null;
                    isGameInitiator = false;
                }
            }
        });

        loadingDialog.setVisible(true);
    }


    // 파일 경로에서 이미지 아이콘 로드 (이미지 메시지 표시할 때 사용 가능)
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

    // 게임 프레임 열기 (서버가 게임 시작 명령 보내면 호출됨.)
    public void openGameFrame(String gameData) {
        String[] participants = gameData.split("\\s+");
        
        if (participants.length != 2) {
            JOptionPane.showMessageDialog(parentFrame, "게임 참가자 정보 오류!", "오류", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String opponentName = participants[0].equals(userName) ? participants[1] : participants[0];

        try {
            new MiniGameFrame(parentFrame, userName, opponentName, roomId).setVisible(true);
            closeLoadingDialog();
        } catch (Exception e) {
            System.err.println("게임 프레임 생성 오류: " + e.getMessage());
            JOptionPane.showMessageDialog(parentFrame, "게임 로딩 중 오류가 발생했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
        }
    }

    // 대기창 닫기(게임 시작/취소 등)
    public void closeLoadingDialog() {
        if (loadingDialog != null) {
            SwingUtilities.invokeLater(() -> {
                if (loadingDialog != null) {
                    loadingDialog.dispose();
                    loadingDialog = null;
                }
            });
            isGameInitiator = false;
        }
    }
}