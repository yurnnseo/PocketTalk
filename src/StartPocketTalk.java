import javax.swing.JFrame;

public class StartPocketTalk extends JFrame {

    public StartPocketTalk() {
        setTitle("포켓톡"); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 프레임 종료 설정
        setSize(370, 510);
        setContentPane(new StartPocketTalkPanel(this, "Images/loginScreen.png")); // 초기 배경 이미지 설정
        setResizable(false); // 프레임 크기 고정
        setVisible(true); 
    }

    public static void main(String[] args) 
    {
        new StartPocketTalk();
    }
}  