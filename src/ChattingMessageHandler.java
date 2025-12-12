import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;

public class ChattingMessageHandler {

    // 이모티콘 PNG 경로
    private static final String EMOTICON_HAPPY = "/Images/Emoticon_happy.png";
    private static final String EMOTICON_HELLO = "/Images/Emoticon_hello.png";
    private static final String EMOTICON_REST  = "/Images/Emoticon_rest.png";
    private static final String EMOTICON_SAD   = "/Images/Emoticon_sad.png";

    private final ChattingPanel panel; // 이 핸들러를 통해 실제로 패널에 출력
    private final String myName;

    public ChattingMessageHandler(ChattingPanel panel, String myName) {
        this.panel = panel;
        this.myName = myName;
    }

    // 서버에서 받은 "한 줄 메시지" 분석
    public void processMessage(String fullMessage) {
        if (fullMessage == null) return;

        String msg = fullMessage.trim();
        if (msg.isEmpty()) return;

        // 기본값
        String sender = null;
        String content = msg;
        boolean isMine = false; // 내가 보낸 메시지인지, 아닌지 결정

        // "발신자 : 내용" 형태면 발신자와 내용을 분리
        int idx = msg.indexOf(" : ");
        if (idx > 0) {
            sender = msg.substring(0, idx).trim();
            content = msg.substring(idx + 3).trim();
            isMine = sender.equals(myName);
        }

        // "/"로 시작하면 커맨드라고 판단
        if (content.startsWith("/")) {
            handleCommand(sender, content, isMine);
            return;
        }

        // 일반 텍스트
        String bubbleText = (sender != null) ? (sender + " : " + content) : content;
        showText(bubbleText, isMine);
    }

    // "/emoji happy" 같은 커맨드 처리 메소드
    private void handleCommand(String sender, String content, boolean isMine) {
        
    	String[] parts = content.split("\\s+", 2);
    	
        String command = parts[0]; // 명령어 종류
        String data = (parts.length > 1) ? parts[1].trim() : "";

        // 명령어 종류에 따라 처리함.
        switch (command) {
            case "/emoji":
                handleEmoji(data, sender, isMine);
                break;

            case "/image":
                handleImage(data, sender, isMine);
                break;

            case "/game_start":
                handleGameStart(data);
                break;

            case "/game_prompt":
                handleGamePrompt(data); // data는 요청한 사람 이름
                break;

            case "/game_canceled":
                handleGameCanceled(data); // data는 취소한 사람 이름
                break;

            default:
                // 모르는 커맨드는 텍스트로 보여줌
                String text = (sender != null) ? (sender + " : " + content) : content;
                showText(text, isMine);
        }
    }

    // 이모티콘 메시지 처리
    private void handleEmoji(String code, String sender, boolean isMine) {
        String path = null;
        if (code.equals("happy")) path = EMOTICON_HAPPY;
        else if (code.equals("hello")) path = EMOTICON_HELLO;
        else if (code.equals("rest")) path = EMOTICON_REST;
        else if (code.equals("sad")) path = EMOTICON_SAD;

        if (path == null) {
            String text = (sender != null) ? (sender + " : " + "/emoji " + code) : ("/emoji " + code);
            showText(text, isMine);
            return;
        }

        ImageIcon icon = UIComponentZip.loadScaledIcon(path, 90, 90);
        
        if (icon != null) {
            showImage(icon, isMine);
        } 
        else {
            showText("[이모티콘 로드 실패: " + code + "]", isMine);
        }
    }

    // 이미지 메시지 처리
    private void handleImage(String filePath, String sender, boolean isMine) {
        
    	ImageIcon icon = panel.loadScaledIconFromFile(filePath, 180, 180);
        
    	if (icon != null) {
            showImage(icon, isMine);
        } 
        else {
            String text = (sender != null) ? (sender + " : [이미지 로드 실패: " + filePath + "]") : ("[이미지 로드 실패: " + filePath + "]");
            showText(text, isMine);
        }
    }

    // 게임 시작 처리
    private void handleGameStart(String gameData) {
        // 게임 시작하면 대기창 닫고 게임 프레임 열기
        SwingUtilities.invokeLater(() -> {
            panel.closeLoadingDialog();
            panel.openGameFrame(gameData);
        });
    }

    // 상대방이 게임을 요청했다는 알림 처리
    private void handleGamePrompt(String senderName) {
        showText("[" + senderName + "] 님이 게임을 요청했습니다.", false); // 게임 요청 왔다는 안내 메시지
    }

    // 게임 요청 취소 처리
    private void handleGameCanceled(String canceledName) {
        SwingUtilities.invokeLater(() -> {
            panel.closeLoadingDialog();
            panel.addTextMessage(canceledName + " 님이 게임 요청을 취소했습니다.", false);
        });
    }

    // 텍스트 말풍선 출력
    private void showText(String text, boolean isMine) {
        SwingUtilities.invokeLater(() -> panel.addTextMessage(text, isMine));
    }

    // 이미지 말풍선 출력
    private void showImage(ImageIcon icon, boolean isMine) {
        SwingUtilities.invokeLater(() -> panel.addImageMessage(icon, isMine));
    }
}
