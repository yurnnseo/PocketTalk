import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class JavaChatServerFrame extends JFrame {

    private JavaChatServerPanel serverPanel;

    public JavaChatServerFrame() {
        setTitle("PocketTalk Server");
        
        setSize(360, 420); 
        setLocationRelativeTo(null); // 화면 가운데

        serverPanel = new JavaChatServerPanel();
        setContentPane(serverPanel);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {

                int result = JOptionPane.showConfirmDialog(
                    JavaChatServerFrame.this, "서버를 종료하시겠습니까?\n(모든 클라이언트 연결이 끊어집니다.)", "서버 종료", JOptionPane.YES_NO_OPTION);

                if (result == JOptionPane.YES_OPTION) {
                    serverPanel.shutdownServer(); // 서버 정리
                    // 그 다음에 EXIT_ON_CLOSE 가 작동해서 종료됨
                } 
                else {
                    // NO → 종료 무시 → 기본 EXIT_ON_CLOSE 를 막기 위해 이벤트 consume
                    e.getWindow().setVisible(true);
                }
            }
        });
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    JavaChatServerFrame frame = new JavaChatServerFrame();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
