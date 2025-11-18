import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class JavaChatServerFrame extends JFrame {

    private JavaChatServerPanel serverPanel;

    public JavaChatServerFrame() {
        setTitle("PocketTalk Server");
        
        setSize(500, 600); 
        setLocationRelativeTo(null); // 화면 가운데

        serverPanel = new JavaChatServerPanel();
        setContentPane(serverPanel);
        
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        
        // 프레임이 닫힐 때 동작 수행됨.
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {

                int result = JOptionPane.showConfirmDialog(
                    JavaChatServerFrame.this, "서버를 종료하시겠습니까?\n(모든 클라이언트 연결이 끊어집니다.)", "서버 종료", JOptionPane.YES_NO_OPTION);

                if (result == JOptionPane.YES_OPTION) {
                    serverPanel.shutdownServer(); // 서버 정리
                    dispose();     
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
