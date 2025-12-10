// 채팅패널 내 메시지 파싱, 요청 처리 분리
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;

public class ChattingMessageHandler {
 private static final String EMOTICON_HAPPY = "/Images/Emoticon_happy.png";
 private static final String EMOTICON_HELLO = "/Images/Emoticon_hello.png";
 private static final String EMOTICON_REST = "/Images/Emoticon_rest.png";
 private static final String EMOTICON_SAD = "/Images/Emoticon_sad.png";

 private final ChattingPanel chattingPanel;
 private final String currentUserName;

 public ChattingMessageHandler(ChattingPanel panel, String userName) {
     this.chattingPanel = panel;
     this.currentUserName = userName;
 }


 public void processMessage(String fullMessage) {
     if (fullMessage == null) return;
     String msg = fullMessage.trim();
     if (msg.isEmpty()) return;

     String sender = null;
     String content = msg;
     boolean isMine = false;

     // 1. 발신자 및 내용 분리 (예: "UserA : /msg content")
     int idx = msg.indexOf(" : ");
     if (idx > 0) {
         sender = msg.substring(0, idx).trim();
         content = msg.substring(idx + 3).trim();
         isMine = sender.equals(currentUserName);
     }

     // 2. 커맨드 처리 (이모티콘, 이미지, 게임 관련)
     if (content.startsWith("/")) {
         // 커맨드 문자열 (예: "/emoji happy")
         String[] parts = content.split("\\s+", 2);
         String command = parts[0];
         String commandData = parts.length > 1 ? parts[1].trim() : "";
         
         // 각 커맨드별 처리 메서드 호출
         if (command.equals("/emoji")) {
             handleEmojiMessage(commandData, sender, isMine);
         } else if (command.equals("/image")) {
             handleImageMessage(commandData, sender, isMine);
         } else if (command.equals("/game_start")) {
             handleGameStart(commandData);
         } else if (command.equals("/game_prompt")) {
             handleGamePrompt(commandData);
         } else if (command.equals("/game_canceled")) {
             handleGameCanceled(commandData);
         } else {
             // 알 수 없는 커맨드는 일반 텍스트로 처리
             displayTextMessage((sender != null ? sender + " : " : "") + content, isMine);
         }
     } else {
         // 3. 일반 텍스트 메시지 처리
         String bubbleText;
         if (sender != null) bubbleText = sender + " : " + content;
         else bubbleText = content;
         
         displayTextMessage(bubbleText, isMine);
     }
 }
 
 // 커맨드별 처리 메서드

 private void handleEmojiMessage(String code, String sender, boolean isMine) {
     String path;
     switch (code) {
         case "happy": path = EMOTICON_HAPPY; break;
         case "hello": path = EMOTICON_HELLO; break;
         case "rest":  path = EMOTICON_REST;  break;
         case "sad":   path = EMOTICON_SAD;   break;
         default:
             // 알 수 없는 코드는 텍스트로 표시
             displayTextMessage((sender != null ? sender + " : " : "") + "/emoji " + code, isMine);
             return;
     }

     ImageIcon icon = chattingPanel.loadScaledIcon(path, 90, 90);
     if (icon != null) {
         chattingPanel.addImageMessage(icon, isMine); 
     } else {
         displayTextMessage((sender != null ? sender + " : " : "") + "[이모티콘 로드 실패: " + code + "]", isMine);
     }
 }

 private void handleImageMessage(String filePath, String sender, boolean isMine) {
     ImageIcon icon = chattingPanel.loadScaledIconFromFile(filePath, 180, 180);
     if (icon != null) {
         chattingPanel.addImageMessage(icon, isMine);
     } else {
         displayTextMessage((sender != null ? sender + " : " : "") + "[이미지 로드 실패: " + filePath + "]", isMine);
     }
 }

 private void handleGameStart(String gameData) {
     // 대기 다이얼로그 닫기 및 게임 프레임 열기
     chattingPanel.closeLoadingDialog(); 
     chattingPanel.openGameFrame(gameData);
 }
 
 private void handleGamePrompt(String senderName) {
     // 상대방에게 게임 요청 알림 메시지 출력
     displayTextMessage(
         "[" + senderName + "] 님이 게임을 요청했습니다. 게임을 시작하려면 [게임 버튼]을 다시 눌러주세요.",
         false
     );
 }
 
 private void handleGameCanceled(String canceledName) {
     // 대기 다이얼로그 닫기 및 취소 메시지 출력
     chattingPanel.closeLoadingDialog(); 
     displayTextMessage(
         canceledName + " 님이 게임 요청을 취소했습니다.",
         false
     );
 }
 
 //UI 업데이트 (ChattingPanel의 메서드를 래핑)
 private void displayTextMessage(String text, boolean isMine) {
     chattingPanel.addTextMessage(text, isMine); // ChattingPanel의 public 메서드 호출
 }
}