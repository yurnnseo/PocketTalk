import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

public class MiniGameFrame extends JFrame {
	private MiniGamePanel gamestartpanel;
	private ClientMenuFrame parentFrame;
	private static MiniGameFrame activeInstance;
	
	public MiniGameFrame(ClientMenuFrame parentFrame, String username, String oppenetName) {
		this.parentFrame = parentFrame;
		
        setTitle("미니게임"); 
        setLayout(null); 
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // 프레임 종료 설정
        setSize(770, 600); // 사이즈 설정
        setResizable(false); // 프레임 크기 고정. 사용자가 크기 조절 불가능

        activeInstance = this; // 인스턴스 생성 시 static 필드에 현재 객체 저장
        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                // 이 프레임이 닫힐 때, activeInstance를 해제합니다.
                if (activeInstance == MiniGameFrame.this) {
                    activeInstance = null;
                    System.out.println("MiniGameFrame이 닫혔습니다. activeInstance 해제됨.");
                }
            }
        });
        
        gamestartpanel = new MiniGamePanel(parentFrame, username, oppenetName);
        add(gamestartpanel);
        setVisible(true); // 프레임을 화면에 출력
    }
	
	public static void receiveStartCommand(String messageBody) {
		if (activeInstance != null) {
	        
	        // 1. 메시지 파싱
	        String[] parts = messageBody.split(" ", 3);
	        
	        if (parts.length < 3) {
	            System.err.println("오류: 포도 데이터가 누락되었습니다: " + messageBody);
	            return;
	        }
	        String mySide = parts[1].trim(); // "LEFT" 또는 "RIGHT"
	        String grapeDataPart = parts[2].trim();
	        
	        // 2. 포도 데이터 파싱
	        String[] valueStrings = grapeDataPart.split(",");
	        int[] grapeValues = new int[valueStrings.length];
	        
	        try {
	            for (int i = 0; i < valueStrings.length; i++) {
	                grapeValues[i] = Integer.parseInt(valueStrings[i].trim());
	            }
	        } catch (NumberFormatException e) {
	        	System.err.println("❌ 오류: 포도 데이터 파싱 실패! 데이터 형식 오류: " + e.getMessage());
	            System.err.println("수신된 포도 데이터 부분: [" + grapeDataPart + "]");
	            
	            // ⭐ 서버가 보낸 메시지를 그대로 출력하여 문제의 근원을 찾습니다.
	            System.err.println("원천 메시지: " + messageBody);
	            return;
	        }
	        
	        // 3. 파싱된 데이터로 게임 시작
	        activeInstance.gamestartpanel.initializeGrapes(grapeValues, mySide); // ⬅️ 데이터 전달
	        activeInstance.gamestartpanel.startSynchronizedGame(); 
	        
	    } else {
            System.err.println("오류: MiniGameFrame 인스턴스가 활성화되지 않았는데 시작 명령을 받았습니다.");
        }
    }
}
