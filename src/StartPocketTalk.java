import javax.swing.JFrame;

public class StartPocketTalk extends JFrame {

    public StartPocketTalk() {
        setTitle("포켓톡"); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 프레임 종료 설정
        setSize(420, 580); // 사이즈 설정
        setContentPane(new StartPocketTalkPanel(this, "초기화면.png")); // 초기 배경 이미지 설정
        setResizable(false); // 프레임 크기 고정. 사용자가 크기 조절 불가능
        setVisible(true); // 프레임을 화면에 출력
    }

    public static void main(String[] args) {
        new StartPocketTalk();
    }
}