import javax.swing.JFrame;

public class MiniGameFrame extends JFrame {
	private MiniGameStartPanel gamestartpanel;
	public MiniGameFrame(ClientMenuFrame parentFrame, String username, String oppenetName) {
        setTitle("미니게임"); 
        setLayout(null); 
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // 프레임 종료 설정
        setSize(770, 600); // 사이즈 설정
        setResizable(false); // 프레임 크기 고정. 사용자가 크기 조절 불가능

        gamestartpanel = new MiniGameStartPanel();
        add(gamestartpanel);
        setVisible(true); // 프레임을 화면에 출력
    }
}
