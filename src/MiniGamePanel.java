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


// UI/타이머/이름 + 마우스 이벤트만

public class MiniGamePanel extends JPanel{
	
	private JButton startbtn, howtoplaybtn;
	private Font font;
	private JLabel title, time, playername;
	private Image backgroundImg = null;
	private String Background = "Images/gamebackground.png";
	private JPanel startPanel; // 반투명 패널
	private Timer timer; //javax.swing.Timer 사용
	private int initTime = 100;
	
	private String userName; // 플레이어 이름 
    private String oppenetName; // 상대방 이름
    private ClientMenuFrame parentFrame;
    private String mySide = "NONE"; // 게임 판 위치(기본값)
    
	private ImageIcon[] grapeIcons; // 3가지 포도 이미지 아이콘
	
	// 게임판/선택/로직
    private MiniGrapeGrid leftGrid;
    private MiniGrapeGrid rightGrid;
    private MiniGrapeSelectionController selectionController;
    private MiniGrapeGameManager grapeGameManager;

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

	public MiniGamePanel(ClientMenuFrame parentFrame, String username, String oppenetName) {
		this.parentFrame = parentFrame;	
		this.userName = username; 
        this.oppenetName = oppenetName;
        
		setLayout(null);
		this.backgroundImg = new ImageIcon(getClass().getResource("/" + Background)).getImage();
		setSize(770, 600);
		
		// 두 번째 패널: 게임 플레이 패널
	    time = new JLabel("100");
	    time.setFont(FontSource.get(20f));
	    time.setBounds(100, 38, 770, 50);
	    add(time);
	    
	    playername = new JLabel();    
	    playername.setFont(FontSource.get(20f)); // 폰트 크기 조정
        add(playername);
        
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

		//첫 번째 패널: 시작버튼이 달린 반투명 패널
        startPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(0xF9, 0xF9, 0xF9, 102)); // 40% 투명
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        startPanel.setLayout(null);
        startPanel.setBounds(0, 0, 770, 600);
        startPanel.setOpaque(false);
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

	    startbtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parentFrame.sendToServer("/game_start_ready " + oppenetName);
                startbtn.setEnabled(false);
                System.out.println("서버로 게임 시작 요청 메시지 전송.");
            }
        }); 	    
	}
	
	// 서버에서 포도 숫자들을 받았을 때 호출됨
	public void initializeGrapes(int[] grapeValues, String mySide) {
        this.mySide = mySide;

        GrapeMouseListener mouseListener = new GrapeMouseListener();
        GrapeMouseMotionListener motionListener = new GrapeMouseMotionListener();

        int index = 0;
        index = leftGrid.fillFromValues(grapeValues, index, mouseListener, motionListener);
        index = rightGrid.fillFromValues(grapeValues, index, mouseListener, motionListener);

        playername.setText(oppenetName + " VS " + userName);
        playername.setBounds(450, 40, 300, 50);
        
        revalidate();
        repaint();
    }

    // 내부 클래스
    // 선택된 포도 계산해서 제거
    class GrapeMouseMotionListener extends MouseMotionAdapter {
        @Override
        public void mouseDragged(java.awt.event.MouseEvent e) {
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

                // VS 표시는 이미 initializeGrapes에서 설정했지만
                // 혹시 몰라 다시 한 번 설정
                playername.setText(oppenetName + " VS " + userName);
                playername.setBounds(450, 40, 300, 50);

                // 드래그 영역에 포함된 포도 선택/테두리 표시
                selectionController.updateSelection(myBoard, dragRect);

                repaint();
            }
        }
    }
    

    class GrapeMouseListener extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            // 드래그 시작 지점 기록
            dragStartPoint = e.getLocationOnScreen();
            dragCurrentPoint = dragStartPoint;
            selectionController.clearSelection();
            repaint();
        }

        @Override
        public void mouseReleased(java.awt.event.MouseEvent e) {
            List<JLabel> selected = selectionController.getSelected();

            int totalValue = grapeGameManager.removeIfSumIsFive(selected);

            if (totalValue == 5 && !selected.isEmpty()) {

                // ★ 여기서 "어떤 포도들이 지워졌는지" 좌표 문자열로 만들기
                StringBuilder sb = new StringBuilder();
                for (JLabel grape : selected) {
                    if (grape == null) continue;

                    Object rowObj = grape.getClientProperty("row");
                    Object colObj = grape.getClientProperty("col");

                    if (!(rowObj instanceof Integer) || !(colObj instanceof Integer)) {
                        continue;
                    }

                    int r = (Integer) rowObj;
                    int c = (Integer) colObj;

                    if (sb.length() > 0) sb.append(";");
                    sb.append(r).append(",").append(c);
                }

                String coordString = sb.toString();

                // 서버로 "누가, 누구와 게임 중인지, 어떤 좌표를 지웠는지" 보냄
                // 형식: /game_remove <ownerName> <opponentName> <r1,c1;r2,c2;...>
                parentFrame.sendToServer(
                        "/game_remove " + userName + " " + oppenetName + " " + coordString
                );
            } else if (!selected.isEmpty()) {
                // 합이 5가 아니면 아무 것도 안 함
            }

            resetSelectionAndDrag();
            revalidate();
            repaint();
        }
    }

    // 서버에서 넘어온 /game_apply_remove 명령 반영
    public void applyRemoteRemove(String ownerName, String coordString) {
        if (coordString == null || coordString.isEmpty()) return;

        // ownerName == userName 이면 내 오른쪽 보드, 아니면 왼쪽 보드
        boolean isMyBoard = ownerName.equals(userName);
        JLabel[][] targetBoard = isMyBoard ? rightGrid.getBoard() : leftGrid.getBoard();

        String[] tokens = coordString.split(";");
        for (String t : tokens) {
            String[] rc = t.split(",");
            if (rc.length != 2) continue;

            try {
                int r = Integer.parseInt(rc[0].trim());
                int c = Integer.parseInt(rc[1].trim());

                if (r < 0 || r >= MiniGrapeGameManager.ROWS) continue;
                if (c < 0 || c >= MiniGrapeGameManager.COLS) continue;

                JLabel grape = targetBoard[r][c];

                if (grape != null) {
                    grapeGameManager.removeGrape(grape); // 실제 제거는 GameManager
                }
            } catch (NumberFormatException ignore) {
            }
        }

        // 여기에서 "더 이상 지울 게 없으면 서버에 리필 요청"
        if (ownerName.equals(userName)) { // 내가 지운 턴인 경우만 검사
            boolean hasMove = grapeGameManager.hasAnyMoveOnGameBoard(); // 오른쪽(내 판) 기준 검사
            if (!hasMove) {
                // 형식: /game_refill_request <ownerName> <opponentName>
                parentFrame.sendToServer("/game_refill_request " + userName + " " + oppenetName);
                System.out.println("더 이상 지울 조합 없음. 서버에 리필 요청 보냄");
            }
        }

        revalidate();
        repaint();
    }

    // 서버에서 받은 리필 값을 적용하는 메서드
    public void applyRefill(String ownerName, int[] grapeValues) {
        // ownerName == userName 이면 내 오른쪽 보드, 아니면 상대의 왼쪽 보드
        boolean isMyBoard = ownerName.equals(userName);
        JLabel[][] targetBoard = isMyBoard ? rightGrid.getBoard() : leftGrid.getBoard();

        int idx = 0;

        for (int r = 0; r < MiniGrapeGameManager.ROWS; r++) {
            for (int c = 0; c < MiniGrapeGameManager.COLS; c++) {
                JLabel g = targetBoard[r][c];
                if (g == null) {
                    // 이미 지워진 자리면 건너뜀 (숫자도 소비하지 않음)
                    continue;
                }
                if (idx >= grapeValues.length) {
                    // 방어 코드: 값이 모자라면 그냥 종료
                    break;
                }

                int v = grapeValues[idx++];
                g.putClientProperty("value", v);

                // 아이콘도 숫자에 맞게 다시 설정
                int iconIndex = v - 1;
                if (iconIndex < 0 || iconIndex >= grapeIcons.length) {
                    iconIndex = 0;
                }
                g.setIcon(grapeIcons[iconIndex]);
            }
        }

        revalidate();
        repaint();
    }




    private void resetSelectionAndDrag() {
        selectionController.clearSelection();
        dragStartPoint = null;
        dragCurrentPoint = null;
        repaint();
    }
	 

	// 서버에게 명령 받아 양쪽 클라이언트가 동시에 게임 시작
    public void startSynchronizedGame() {
        startPanel.setVisible(false); // 시작 패널 숨기기

        // 타이머 초기화 및 시작 
        initTime = 100;
        time.setText(String.valueOf(initTime));

        // 1초마다 실행되는 타이머
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (initTime > 0) {
                    initTime--;
                    time.setText(String.valueOf(initTime));
                } else {
                    timer.stop();
                    System.out.println("시간 종료!");
                }
            }
        });
        timer.start();

        repaint();
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
}
