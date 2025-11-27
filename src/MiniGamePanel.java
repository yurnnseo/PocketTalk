import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
    
	private ImageIcon[] grapeIcons; // 3가지 포도 이미지 아이콘
    private JLabel[][] grapes;       // 8x9 포도 레이블 배열(왼쪽 클라이언트)
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
    
	public MiniGamePanel(ClientMenuFrame parentFrame, String username, String oppenetName) {
		System.out.println("전달받은 username: [" + username + "]");
		
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
	    
	    playername = new JLabel(username + "  VS  " + oppenetName);
	    playername.setBounds(500, 40, 300, 50); // 위치와 크기는 조정 필요
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
	    		startPanel.setVisible(false);
	    		
	    		initializeGrapes(); //포도 배치 시작
	    		
	    		initTime = 100; // 시간 초기화
	            time.setText(String.valueOf(initTime)); // 레이블 초기화
	            
	            // 1000ms (1초)마다 실행되는 스윙 타이머 생성
	            timer = new javax.swing.Timer(1000, new ActionListener() {
	                @Override
	                public void actionPerformed(ActionEvent e) {
	                    if (initTime > 0) {
	                    	initTime--; 
	                        time.setText(String.valueOf(initTime)); 
	                    } else {
	                        // 시간이 0이 되면 타이머 중지
	                        timer.stop();
	                        System.out.println("시간 종료!");
	                    }
	                }
	            });
	            timer.start(); // 타이머 시작
	            
	    		repaint();
	    	}
	    });
	    	    
	}
	
	//포도 배치 메서드
	private void initializeGrapes() {
	    Random random = new Random();

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
	            int randomGrapeIndex = random.nextInt(grapeIcons.length); // 0, 1, 2 중 랜덤 선택
	            ImageIcon selectedIcon = grapeIcons[randomGrapeIndex];

	            JLabel grapeLabel = new JLabel(selectedIcon);
	            grapeLabel.setBounds(
	                START_X_LEFT + c * (GRAPE_WIDTH + X_GAP),
	                START_Y + r * (GRAPE_HEIGHT + Y_GAP),
	                GRAPE_WIDTH,
	                GRAPE_HEIGHT
	            );
	            grapes[r][c] = grapeLabel; // 배열에 저장
	            add(grapeLabel); // 패널에 추가
	        }
	    }
	    
	    // 새로운 포도 배치(오른쪽)
	    for (int r = 0; r < 9; r++) {
	        for (int c = 0; c < 8; c++) {
	            int randomGrapeIndex = random.nextInt(grapeIcons.length); //왼쪽과 별개의 랜덤
	            ImageIcon selectedIcon = grapeIcons[randomGrapeIndex];

	            JLabel grapeLabel = new JLabel(selectedIcon);
	            grapeLabel.setBounds(
	                START_X_RIGHT + c * (GRAPE_WIDTH + X_GAP),
	                START_Y + r * (GRAPE_HEIGHT + Y_GAP),
	                GRAPE_WIDTH,
	                GRAPE_HEIGHT
	            );
	            Secondgrapes[r][c] = grapeLabel; // 배열에 저장
	            add(grapeLabel); // 패널에 추가
	        }
	    }
	    revalidate(); 
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
