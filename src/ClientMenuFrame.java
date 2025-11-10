//StartPocketTalkPanel에서 버튼 누르면 띄워지는 새로운 창의 프레임
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import javax.swing.*;

public class ClientMenuFrame extends JFrame {
    private String username, ip_addr, port_no;

    public ClientMenuFrame(String username, String ip_addr, String port_no) {
        this.username = username;
        this.ip_addr = ip_addr;
        this.port_no = port_no;

        setTitle("PocketTalk");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(380, 560);
        setLocationRelativeTo(null);
        setResizable(false);

        // 처음엔 친구 목록 패널 표시
        setContentPane(new ClientFriendsMenuPanel(this, username, ip_addr, port_no));
        setVisible(true);
    }

    // 화면 전환용 메소드
    public void showFriendsMenu() {
        setContentPane(new ClientFriendsMenuPanel(this, username, ip_addr, port_no));
        revalidate();
        repaint();
    }

    public void showChattingMenu() {
        setContentPane(new ClientChatingMenuPanel(this, username, ip_addr, port_no));
        revalidate();
        repaint();
    }
}
