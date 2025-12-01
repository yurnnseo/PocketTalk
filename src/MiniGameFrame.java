import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

// 창 + 서버 명령 수신

public class MiniGameFrame extends JFrame {
	
	private MiniGamePanel gamestartpanel;
	private ClientMenuFrame parentFrame;
	private static MiniGameFrame activeInstance;
	
	public MiniGameFrame(ClientMenuFrame parentFrame, String username, String oppenetName) {
		this.parentFrame = parentFrame;
		
        setTitle("포도게임"); 
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
	        
	        // 1. 메시지 파싱 (참가자/상대방, myside, 내 포도 데이터, 상대방 포도 데이터)
	        String[] parts = messageBody.split(" ", 4);
	        
	        if (parts.length < 4) {
	            System.err.println("오류: 포도 데이터가 누락되었습니다: " + messageBody);
	            return;
	        }
	        String mySide = parts[1].trim(); 
	        String myGrapeDataPart = parts[2].trim();
	    	String opponentGrapeDataPart = parts[3].trim();
	        
	    	// 2. 내 포도 데이터 파싱
	    	int[] myGrapeValues = parseGrapeData(myGrapeDataPart, "내 포도 데이터");
	    	if (myGrapeValues == null) return;
	    	
	    	// 3. 상대방 포도 데이터 파싱
	    	int[] opponentGrapeValues = parseGrapeData(opponentGrapeDataPart, "상대방 포도 데이터");
	    	if (opponentGrapeValues == null) return;
	    	
	    	// 4. 파싱된 데이터로 게임 시작 (함수 시그니처 변경)
	    	activeInstance.gamestartpanel.initializeGrapes(myGrapeValues, opponentGrapeValues, mySide); 
	    	activeInstance.gamestartpanel.startSynchronizedGame();
	        
	    } else {
            System.err.println("오류: MiniGameFrame 인스턴스가 활성화되지 않았는데 시작 명령을 받았습니다.");
        }
    }
	
	// 서버에서 받은 /game_apply_remove 처리
	public static void receiveRemoveCommand(String messageBody) {
	    // messageBody 형식: "ownerName r1,c1;r2,c2;..."
	    if (activeInstance == null) {
	        System.err.println("MiniGameFrame 활성 인스턴스 없음 (/game_apply_remove 무시)");
	        return;
	    }

	    String[] parts = messageBody.split(" ", 2);
	    if (parts.length < 2) {
	        System.err.println("잘못된 remove 메시지: " + messageBody);
	        return;
	    }

	    String ownerName   = parts[0].trim();
	    String coordString = parts[1].trim();

	    activeInstance.gamestartpanel.applyRemoteRemove(ownerName, coordString);
	}
	
	// 서버에서 받은 /game_refill 처리
	// messageBody 형식: "ownerName v1,v2,v3,..."
	public static void receiveRefillCommand(String messageBody) {
	    if (activeInstance == null) {
	        System.err.println("MiniGameFrame 활성 인스턴스 없음 (/game_refill 무시)");
	        return;
	    }

	    String[] parts = messageBody.split(" ", 2);
	    if (parts.length < 2) {
	        System.err.println("잘못된 refill 메시지: " + messageBody);
	        return;
	    }

	    String ownerName   = parts[0].trim();
	    String valuesPart  = parts[1].trim();

	    String[] valueStrings = valuesPart.split(",");
	    int[] grapeValues = new int[valueStrings.length];
	    try {
	        for (int i = 0; i < valueStrings.length; i++) {
	            grapeValues[i] = Integer.parseInt(valueStrings[i].trim());
	        }
	    } catch (NumberFormatException e) {
	        System.err.println("리필 값 파싱 오류: " + e.getMessage());
	        return;
	    }

	    activeInstance.gamestartpanel.applyRefill(ownerName, grapeValues);
	}

	private static int[] parseGrapeData(String grapeDataPart, String label) {
	    String[] valueStrings = grapeDataPart.split(",");
	    int[] grapeValues = new int[valueStrings.length];
	    
	    try {
	        for (int i = 0; i < valueStrings.length; i++) {
	            grapeValues[i] = Integer.parseInt(valueStrings[i].trim());
	        }
	        return grapeValues;
	    } catch (NumberFormatException e) {
	        System.err.println("오류: " + label + " 파싱 실패! 데이터 형식 오류: " + e.getMessage());
	        System.err.println("수신된 데이터 부분: [" + grapeDataPart + "]");
	        return null;
	    }
	}

}
