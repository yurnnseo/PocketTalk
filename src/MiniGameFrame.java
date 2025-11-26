import java.awt.Color;

import javax.swing.JFrame;

public class MiniGameFrame extends JFrame {
	private MiniGamePanel gamepanel;

	public MiniGameFrame(ClientMenuFrame parentFrame, String username, String oppenetName) {
        setTitle("미니게임"); 
        setLayout(null);
        setBackground(Color.decode("#F9F9F9"));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 프레임 종료 설정
        setSize(770, 600); // 사이즈 설정
        setResizable(false); // 프레임 크기 고정. 사용자가 크기 조절 불가능
        setVisible(true); // 프레임을 화면에 출력
        
        this.gamepanel = new MiniGamePanel();
        add(gamepanel);
    }

}
