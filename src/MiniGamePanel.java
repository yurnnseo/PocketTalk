import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;


// UI/타이머/이름 + 마우스 이벤트만

public class MiniGamePanel extends JPanel{
	
	private JButton startbtn, howtoplaybtn;
	private Font font;
	private JLabel title, playername;
	private Image backgroundImg = null;
	private String Background = "Images/gamebackground.png";
	private JPanel startPanel; // 반투명 패널
	private GameRulePanel rulepanel;
	
	private String userName; // 플레이어 이름 
    private String oppenetName; // 상대방 이름
    private ClientMenuFrame parentFrame;
    private String mySide = "NONE"; // 게임 판 위치(기본값)
    
    private int myScore = 0;          // 내 점수
    private int opponentScore = 0;    // 상대 점수
    
    private String roomId;
    
	private ImageIcon[] grapeIcons; // 4가지 포도 이미지 아이콘
	
	// 게임판/선택/로직
    private MiniGrapeGrid leftGrid;
    private MiniGrapeGrid rightGrid;
    private MiniGrapeSelectionController selectionController;
    private MiniGrapeGameManager grapeGameManager;
    private MiniGrapeGameController gameController;
    
    // 포도 배치 공통
    private final int GRAPE_WIDTH = 35; // 이전 계산에 따라 조정
    private final int GRAPE_HEIGHT = 43; // 이전 계산에 따라 조정
    private final int START_Y = 120; // 왼쪽 영역 시작 Y 좌표 (상단 UI 아래)
    private final int X_GAP = 5; // 포도 사이의 가로 간격
    private final int Y_GAP = 5; // 포도 사이의 세로 간격
    //x좌표 분리
    private final int START_X_LEFT = 30;  // 왼쪽 영역 시작 X 좌표
    private final int START_X_RIGHT = 410;
    
    // 드래그 관련
    private Point dragStartPoint = null;
    private Point dragCurrentPoint = null;
    
    // 타이머 + 게임 종료 여부 + 점수 관련 필드 추가
    private MiniGrapeTimerPanel timerPanel;
    private boolean gameOver = false;
    
	public MiniGamePanel(ClientMenuFrame parentFrame, String username, String oppenetName, String roomId) {
		this.parentFrame = parentFrame;	
		this.userName = username; 
        this.oppenetName = oppenetName;
        this.roomId = roomId; 
        
		setLayout(null);
		this.backgroundImg = new ImageIcon(getClass().getResource("/" + Background)).getImage();
		setSize(770, 600);
	    
		playername = new JLabel();
		playername.setFont(FontSource.get(20f));
		playername.setBounds(350, 40, 400, 30); 
		add(playername);
		playername.setVisible(false);

		updateScoreTitle(); // 처음 생성 시 점수는 0:0


        // 포도 이미지 초기화
        grapeIcons = new ImageIcon[4];
        grapeIcons[0] = new ImageIcon(getClass().getResource("/Images/grape1.png"));
        grapeIcons[1] = new ImageIcon(getClass().getResource("/Images/grape2.png"));
        grapeIcons[2] = new ImageIcon(getClass().getResource("/Images/grape3.png"));
        grapeIcons[3] = new ImageIcon(getClass().getResource("/Images/grape4.png"));

        // 그리드 / 선택컨트롤러 / 매니저 생성
        leftGrid = new MiniGrapeGrid(this, START_X_LEFT, START_Y, GRAPE_WIDTH, GRAPE_HEIGHT, X_GAP, Y_GAP, grapeIcons, true);
        rightGrid = new MiniGrapeGrid(this, START_X_RIGHT, START_Y, GRAPE_WIDTH, GRAPE_HEIGHT, X_GAP, Y_GAP, grapeIcons, false);

        selectionController = new MiniGrapeSelectionController();
        grapeGameManager = new MiniGrapeGameManager(
                leftGrid.getBoard(),
                rightGrid.getBoard()
        );

        gameController = new MiniGrapeGameController(
                this,
                leftGrid,
                rightGrid,
                selectionController,
                grapeGameManager,
                userName,
                oppenetName
        );

		//첫 번째 패널
        startPanel = new JPanel();
        startPanel.setLayout(null);
        startPanel.setBounds(0, 0, 770, 600);
        startPanel.setBackground(Color.decode("#F9F9F9"));
        add(startPanel);

	    font = FontSource.get(16f);
	    
	    title = new JLabel("포도게임");
	    title.setFont(FontSource.get(40f));
	    title.setBounds(312, 56, 770, 50);
	    startPanel.add(title);
	    
	    startbtn = UIComponentZip.createTextButton("게임 시작", 310,420, 150,50,font);
	    howtoplaybtn = UIComponentZip.createTextButton("게임 방법", 310, 490,150,50,font);
	    startPanel.add(startbtn);
	    startPanel.add(howtoplaybtn);

	    // 패널 변경 위한 선언
	    rulepanel = new GameRulePanel(this);
	    rulepanel.setBounds(0, 0, 770, 600);
	    rulepanel.setVisible(false);
	    add(rulepanel);
	    
	    startbtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parentFrame.sendToServer("/game_start_ready " + oppenetName);
                startbtn.setEnabled(false);
                playername.setVisible(true);
                System.out.println("서버로 게임 시작 요청 메시지 전송.");
            }
        }); 	
	    
	    howtoplaybtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	showRulePanel();
            }
        }); 	
	}
	
	// 이름과 점수를 한 줄로 표시하는 라벨 갱신
	private void updateScoreTitle() {
		String text = userName + " : " + myScore
                + "   VS   "
                + oppenetName + " : " + opponentScore;
		playername.setText(text);
	}
	
	public void updateScore(int newMyScore, int newOpponentScore) {
	    // 1. 필드 갱신
	    this.myScore = newMyScore;
	    this.opponentScore = newOpponentScore;
	    
	    // 2. UI 갱신 (기존 메서드 재활용)
	    updateScoreTitle(); 
	}
	
	public void sendRemoveToServer(String ownerName, String opponentName, String coordString) {
	    parentFrame.sendToServer(
	            "/game_remove " + ownerName + " " + opponentName + " " + coordString
	    );
	}

	public void sendRefillRequest(String ownerName, String opponentName) {
	    parentFrame.sendToServer("/game_refill_request " + ownerName + " " + opponentName);
	}
	
	public void handleRemoteRemoveMessage(String ownerName, String coordString) {
	    if (gameOver) return;

	    // 1. 점수/제거 로직을 Controller에게 위임
	    gameController.applyRemoteRemove(ownerName, coordString);
	    
	    // 2. UI 갱신
	    revalidate();
	    repaint();
	}

	public void handleRefillMessage(String ownerName, int[] grapeValues) {
	    // 로직을 Controller에게 위임
	    gameController.applyRefill(ownerName, grapeValues);
	    
	    // UI 갱신
	    revalidate();
	    repaint();
	}
	
	// 서버에서 포도 숫자들을 받았을 때 호출됨
	public void initializeGrapes(int[] myGrapeValues, int[] opponentGrapeValues, String mySide) {       
		this.mySide = mySide;

	    GrapeMouseListener mouseListener = new GrapeMouseListener();
	    GrapeMouseMotionListener motionListener = new GrapeMouseMotionListener();

	    int index = 0;
	    
	    // 그리드 객체에 포도 값을 채우는 실제 로직 필요 (기존 코드에서 누락됨)
	    rightGrid.fillFromValues(myGrapeValues, index, mouseListener, motionListener);
	    leftGrid.fillFromValues(opponentGrapeValues, index, mouseListener, motionListener);
	    
	    updateScoreTitle(); // 점수/이름 표시 갱신
	    
	    revalidate();
	    repaint();
    }

    // 내부 클래스
    // 선택된 포도 계산해서 제거
    class GrapeMouseMotionListener extends MouseMotionAdapter {
        @Override
        public void mouseDragged(MouseEvent e) {
        	
        	if (gameOver) return; // 시간 끝나면 드래그 무시
        	
            if (dragStartPoint != null) {
                dragCurrentPoint = e.getLocationOnScreen();
                
                // 1. 드래그 영역 계산 (패널 상대 좌표 기준)
                Point startPanelPoint = new Point(
                    dragStartPoint.x - getLocationOnScreen().x,
                    dragStartPoint.y - getLocationOnScreen().y
                );
                Point currentPanelPoint = new Point(
                    dragCurrentPoint.x - getLocationOnScreen().x,
                    dragCurrentPoint.y - getLocationOnScreen().y
                );
                
                int x = Math.min(startPanelPoint.x, currentPanelPoint.x);
                int y = Math.min(startPanelPoint.y, currentPanelPoint.y);
                int w = Math.abs(startPanelPoint.x - currentPanelPoint.x);
                int h = Math.abs(startPanelPoint.y - currentPanelPoint.y);
                Rectangle dragRect = new Rectangle(x, y, w, h);
                
                // 항상 "오른쪽 보드(나)"만 드래그의 대상
                JLabel[][] myBoard = rightGrid.getBoard();

                // 드래그 영역에 포함된 포도 선택/테두리 표시
                selectionController.updateSelection(myBoard, dragRect);

                repaint();
            }
        }
    }
    

    class GrapeMouseListener extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
        	
        	if (gameOver) return; // 시간 끝나면 클릭 무시
            // 드래그 시작 지점 기록
            dragStartPoint = e.getLocationOnScreen();
            dragCurrentPoint = dragStartPoint;
            selectionController.clearSelection();
            repaint();
        }

        @Override
        public void mouseReleased(java.awt.event.MouseEvent e) {
        	
        	if (gameOver) return;   // 시간 끝나면 선택 처리 안 함
        	gameController.tryRemoveSelected();
            
            resetSelectionAndDrag();
            revalidate();
        }
    }

    private void resetSelectionAndDrag() {
        selectionController.clearSelection();
        dragStartPoint = null;
        dragCurrentPoint = null;
        repaint();
    }
	 

    // 서버에게 명령 받아 양쪽 클라이언트가 동시에 게임 시작
    public void startSynchronizedGame() {
        startPanel.setVisible(false);   // 시작 패널 숨기기

        // 게임 상태 초기화
        gameOver = false;
        myScore = 0; 
        opponentScore = 0;
        updateScoreTitle();

        // 혹시 이전 타이머가 있으면 제거
        if (timerPanel != null) {
            timerPanel.stopTimer();
            remove(timerPanel);
            timerPanel = null;
        }

        // 60초짜리 타이머 패널 생성 + 콜백 (시간 끝났을 때)
        timerPanel = new MiniGrapeTimerPanel(60, () -> onTimeOver());

        // 위치는 원하는 대로 조절
        timerPanel.setBounds(0, 0, 780, 30);
        add(timerPanel);

        revalidate();
        repaint();
    }
    
    // 시간이 끝났을 때 호출되는 메서드
    private void onTimeOver() {
        gameOver = true;   // 더 이상 게임 진행 불가

        // 승자 판정
        String winnerText;
        if (myScore > opponentScore) {
            winnerText = userName + " 승리!";
        } else if (myScore < opponentScore) {
            winnerText = oppenetName + " 승리!";
        } else {
            winnerText = "무승부!";
        }

        // 결과 메시지 (채팅에 그대로 뿌릴 문구)
        String summaryForChat =
                "[포도게임 결과] "
                + userName + " " + myScore + "점, "
                + oppenetName + " " + opponentScore + "점 → "
                + winnerText;

        SwingUtilities.invokeLater(() -> {
            // 1. 다이얼로그로 결과 보여주기
            JOptionPane.showMessageDialog(
                    this,
                    "⏰ 시간이 종료되었습니다!\n\n"
                    + userName + " : " + myScore + "점\n"
                    + oppenetName + " : " + opponentScore + "점\n\n"
                    + "결과 : " + winnerText,
                    "게임 종료",
                    JOptionPane.INFORMATION_MESSAGE
            );

            // 2. 서버로 결과 전송 (roomId + 두 사람 이름 + 결과문)
            parentFrame.sendToServer(
                    "/game_result " 
                    + roomId + " "          // ⭐ 가장 먼저 roomId
                    + userName + " " 
                    + oppenetName + " " 
                    + summaryForChat + "\n"
            );

            // 3. 게임 프레임 닫기
            java.awt.Window w = SwingUtilities.getWindowAncestor(MiniGamePanel.this);
            if (w != null) {
                w.dispose();
            }
        });
    }


    // 배경 이미지 그리기
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if(backgroundImg != null) {
            g.drawImage(backgroundImg, 0, 0, getWidth(), getHeight(), this);
        }

        // 화면 분할 선
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(380, 130, 1, getHeight() - 130);
    } 
    
    // 게임 시작 패널 호출
    public void showStartPanel() {
        startPanel.setVisible(true);  
        rulepanel.setVisible(false); 
        repaint();
    }
    
    // 게임 방법 패널 호출
    public void showRulePanel() {
        startPanel.setVisible(false);  
        rulepanel.setVisible(true);  
        repaint();
    }


}
