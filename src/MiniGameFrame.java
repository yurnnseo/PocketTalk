import javax.swing.JFrame;

public class MiniGameFrame extends JFrame {
	public MiniGameFrame() {
        setTitle("미니게임"); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 프레임 종료 설정
        setSize(550, 400); // 사이즈 설정
        setResizable(false); // 프레임 크기 고정. 사용자가 크기 조절 불가능
        setVisible(true); // 프레임을 화면에 출력
    }

}
