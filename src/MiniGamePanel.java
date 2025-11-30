import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionAdapter;
import java.awt.Graphics2D;
import java.awt.BasicStroke;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

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
    private JLabel[][] grapes;       // 9x8 포도 레이블 배열(왼쪽 클라이언트)
    private JLabel[][] Secondgrapes; //오른쪽 클라이언트 포도 레이블
    // 포도 배치 공통
    private final int GRAPE_WIDTH = 35; // 이전 계산에 따라 조정
    private final int GRAPE_HEIGHT = 43; // 이전 계산에 따라 조정
    private final int START_Y = 120; // 왼쪽 영역 시작 Y 좌표 (상단 UI 아래)
    private final int X_GAP = 5; // 포도 사이의 가로 간격
    private final int Y_GAP = 5; // 포도 사이의 세로 간격
    //x좌표 분리
    private final int START_X_LEFT = 30;  // 왼쪽 영역 시작 X 좌표
    private final int START_X_RIGHT = 410;
    //게임에 필요한 변수들
    private List<JLabel> selectedGrapes = new ArrayList<>(); //드래그 된 포도 리스트
    private Point dragStartPoint = null; //드래그 시작 화면 좌표
    private Point dragCurrentPoint = null; //현재 드래그 중인 좌표
    
	public MiniGamePanel(ClientMenuFrame parentFrame, String username, String oppenetName) {
		this.parentFrame = parentFrame;	
		this.userName = userName; 
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
        
	    // 랜덤 포도 이미지 로그&초기화
		grapeIcons = new ImageIcon[4];
		grapeIcons[0] = new ImageIcon(getClass().getResource("/Images/grape1.png"));
        grapeIcons[1] = new ImageIcon(getClass().getResource("/Images/grape2.png"));
        grapeIcons[2] = new ImageIcon(getClass().getResource("/Images/grape3.png"));
        grapeIcons[3] = new ImageIcon(getClass().getResource("/Images/grape4.png"));
	    grapes = new JLabel[9][8];
		Secondgrapes = new JLabel[9][8];
	    
		//첫 번째 패널: 시작버튼이 달린 반투명 패널
		startPanel = new JPanel() {
			 @Override
			    protected void paintComponent(Graphics g) {
			        super.paintComponent(g);
			        //40% 투명 배경
			        g.setColor(new Color(0xF9, 0xF9, 0xF9, 102)); 
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
	    	public void actionPerformed(ActionEvent e) {
	    		parentFrame.sendToServer("/game_start_ready " + oppenetName);
	            
	            // 2. 버튼 비활성화 (여러 번 누르는 것을 방지)
	            startbtn.setEnabled(false); 
	            
	            System.out.println("서버로 게임 시작 요청 메시지 전송.");
	    	}
	    });
	    	    
	}
	
	//포도 배치 메서드
	public void initializeGrapes(int[] grapeValues, String mySide) {
		this.mySide = mySide;
		
        GrapeMouseListener mouseListener = new GrapeMouseListener();
	    GrapeMouseMotionListener motionListener = new GrapeMouseMotionListener();
	    
	    int index = 0;
	    
	    // 기존에 추가된 포도가 있다면 모두 제거
	    for (int r = 0; r < 9; r++) {
	        for (int c = 0; c < 8; c++) {
	            if (grapes[r][c] != null) {
	                remove(grapes[r][c]); // 패널에서 제거
	            }
	            if (Secondgrapes[r][c] != null) {
	                remove(Secondgrapes[r][c]); // 패널에서 제거
	            }
	        }
	    }

	    // 새로운 포도 배치(왼쪽)
	    for (int r = 0; r < 9; r++) {
	        for (int c = 0; c < 8; c++) {
	            int grapeValue = grapeValues[index++]; // 서버에게서 받은 값 사용
	            int randomGrapeIndex = grapeValue - 1; // 값 1~4 -> 인덱스 0~3
	            ImageIcon selectedIcon = grapeIcons[randomGrapeIndex];
	            
	            JLabel grapeLabel = new JLabel(selectedIcon);
	            grapeLabel.setBounds(
	                START_X_LEFT + c * (GRAPE_WIDTH + X_GAP),
	                START_Y + r * (GRAPE_HEIGHT + Y_GAP),
	                GRAPE_WIDTH,
	                GRAPE_HEIGHT
	            );
	            //포도 값, 위치 정보 설정
	            grapeLabel.putClientProperty("value", grapeValue);
	            grapeLabel.putClientProperty("row", r);
	            grapeLabel.putClientProperty("col", c);
	            grapeLabel.putClientProperty("isLeft", true); // 왼쪽 클라이언트 판 식별자
	            
	            // 마우스 리스너 부착
	            grapeLabel.addMouseListener(mouseListener);
	            grapeLabel.addMouseMotionListener(motionListener);
	            
	            grapes[r][c] = grapeLabel; // 배열에 저장
	            add(grapeLabel); // 패널에 추가
	        }
	    }
	    
	    // 새로운 포도 배치(오른쪽)
	    for (int r = 0; r < 9; r++) {
	        for (int c = 0; c < 8; c++) {
	        	int grapeValue = grapeValues[index++];
	            int randomGrapeIndex = grapeValue - 1;
	            ImageIcon selectedIcon = grapeIcons[randomGrapeIndex];
	            
	            JLabel grapeLabel = new JLabel(selectedIcon);
	            grapeLabel.setBounds(
	                START_X_RIGHT + c * (GRAPE_WIDTH + X_GAP),
	                START_Y + r * (GRAPE_HEIGHT + Y_GAP),
	                GRAPE_WIDTH,
	                GRAPE_HEIGHT
	            );
	            grapeLabel.putClientProperty("value", grapeValue);
	            grapeLabel.putClientProperty("row", r);
	            grapeLabel.putClientProperty("col", c);
	            grapeLabel.putClientProperty("isLeft", false); // 오른쪽 클라이언트 판 식별자

	            grapeLabel.addMouseListener(mouseListener);
	            grapeLabel.addMouseMotionListener(motionListener);
	            
	            Secondgrapes[r][c] = grapeLabel; // 배열에 저장
	            add(grapeLabel); // 패널에 추가
	        }
	    }
	    revalidate(); 
	    repaint();  
	}
	
	// 드래그 영역 체크
	private boolean isGrapeInDragArea(JLabel grape, Rectangle dragRect) {
	    if (grape == null || !grape.isVisible()) return false;
	    
	    // 포도의 경계(Bounds)를 가져와 드래그 사각형과 교차하는지 확인
	    return grape.getBounds().intersects(dragRect);
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
                
                for (JLabel grape : MiniGamePanel.this.selectedGrapes) {
                    if (grape != null) grape.setBorder(null);
               }
               MiniGamePanel.this.selectedGrapes.clear();
               
                // 2. 자신의 판만 순회하며 드래그 영역 검사
                for (int r = 0; r < 9; r++) {
                    for (int c = 0; c < 8; c++) {
                        
                        JLabel targetGrape = null;
                        String displayString; // 이름 레이블 위치 조정
                        int xPosition;
                        
                        if ("LEFT".equals(MiniGamePanel.this.mySide)) {
                        	displayString = MiniGamePanel.this.userName + " VS " + MiniGamePanel.this.oppenetName;
                            xPosition = 450;
                            // 내가 LEFT면 왼쪽 판을 조작
                            targetGrape = grapes[r][c];
                            
                            
                        } else if ("RIGHT".equals(MiniGamePanel.this.mySide)) {
                        	displayString = MiniGamePanel.this.oppenetName + " VS " + MiniGamePanel.this.userName; 
                            xPosition = 450;
                            // 내가 RIGHT면 오른쪽 판을 조작
                            targetGrape = Secondgrapes[r][c];
                            
                        }else {
                            // 기본값 (예외 처리)
                            displayString = MiniGamePanel.this.userName + " VS " + MiniGamePanel.this.oppenetName; 
                            xPosition = 500;
                        }
                        
                        MiniGamePanel.this.playername.setText(displayString); 
                        MiniGamePanel.this.playername.setBounds(xPosition, 40, 300, 50);
                        
                        // 선택된 targetGrape에 대해 드래그 검사 수행
                        if (targetGrape != null && isGrapeInDragArea(targetGrape, dragRect)) {
                        	MiniGamePanel.this.selectedGrapes.add(targetGrape);
                            targetGrape.setBorder(javax.swing.BorderFactory.createLineBorder(new Color(128, 0, 128), 3));
                        }
                    }
                }
                
                MiniGamePanel.this.repaint(); // 드래그 영역과 포도 테두리 시각화를 위해 다시 그립니다.
            }
        }
    }
    

    class GrapeMouseListener extends MouseAdapter {
        @Override
        public void mousePressed(java.awt.event.MouseEvent e) {
            // 드래그 시작 지점 기록
        	MiniGamePanel.this.dragStartPoint = e.getLocationOnScreen();
            MiniGamePanel.this.dragCurrentPoint = MiniGamePanel.this.dragStartPoint;
            MiniGamePanel.this.selectedGrapes.clear(); // 드래그 시작 시 선택 리스트 초기화
            MiniGamePanel.this.repaint();
        }

        @Override
        public void mouseReleased(java.awt.event.MouseEvent e) {
            // 1. 합계 계산
            int totalValue = 0;
            for (JLabel grape : selectedGrapes) {
                // "value" 속성이 없는 경우를 대비하여 null 체크 추가
                if (grape != null && grape.getClientProperty("value") != null) {
                    totalValue += (int) grape.getClientProperty("value");
                }
            }
            
            // 2. 합이 5이고, 최소한 1개 이상의 포도가 선택되었는지 검사
            if (totalValue == 5 && selectedGrapes.size() > 0) {
                System.out.println("✅ 합이 5이므로 선택된 " + selectedGrapes.size() + "개의 포도를 제거합니다.");
                
                // 3. 포도 제거 실행
                for (JLabel grape : selectedGrapes) {
                    if (grape != null) {
                        removeGrape(grape);
                    }
                }
                
            } else if (selectedGrapes.size() > 0) {
                System.out.println("❌ 선택된 포도들의 합은 " + totalValue + "입니다. (5가 아님)");
            }

            // 4. 선택 상태 및 드래그 범위 초기화 (성공/실패와 무관)
            MiniGamePanel.this.resetSelectionAndDrag();
            MiniGamePanel.this.revalidate();
            MiniGamePanel.this.repaint();
        }
    }
    
	 // 단일 포도 JLabel을 받아 제거하는 메서드
	 private void removeGrape(JLabel grape) {
	     if (grape == null) return;
	     
	     // 논리적 배열 위치 정보 가져오기
	     int r = (int) grape.getClientProperty("row");
	     int c = (int) grape.getClientProperty("col");
	     boolean isLeft = (boolean) grape.getClientProperty("isLeft");
	     
	     // 패널에서 시각적으로 제거
	     remove(grape); 
	     
	     // 논리적 배열에서 제거 (null로 설정)
	     if (isLeft) {
	         grapes[r][c] = null;
	     } else {
	         Secondgrapes[r][c] = null;
	     }
	     
	 }
	
	 private void resetSelectionAndDrag() {
	     // 선택된 포도의 시각적 피드백 제거
	     for (JLabel grape : selectedGrapes) {
	         if (grape != null) {
	             grape.setBorder(null); 
	         }
	     }
	     
	     // 상태 필드 초기화
	     selectedGrapes.clear();
	     dragStartPoint = null;
	     dragCurrentPoint = null;
	     
	     repaint(); // 드래그 범위 제거를 위해 화면 다시 그리기
	 }
	 
	 // 서버에게 명령 받아 양쪽 클라이언트가 동시에 게임 시작
	 public void startSynchronizedGame() {
		    // 1. 시작 패널 숨기기
		    startPanel.setVisible(false);
	    
		    // 2. 타이머 초기화 및 시작 
		    initTime = 100; 
		    time.setText(String.valueOf(initTime)); 
		    
		    // 1초마다 실행되는 스윙 타이머
		    timer = new javax.swing.Timer(1000, new ActionListener() {
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
