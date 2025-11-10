import java.awt.Window;
import javax.swing.JFrame;

public class MyProfileViewFrame extends JFrame {

    // 논리적 부모(생성 트리 상의 부모 패널)
    private final ClientFriendsMenuPanel parentPanel;

    public MyProfileViewFrame(ClientFriendsMenuPanel parentPanel, String username, String ip_addr, String port_no) {
        this.parentPanel = parentPanel;

        setTitle("나의 프로필");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        // MyProfileViewPanel의 부모는 이 프레임
        setContentPane(new MyProfileViewPanel(this, username, ip_addr, port_no));
        setSize(330, 490);
    }

    // 필요시 외부에서 parentPanel을 얻고 싶을 때 쓸 수 있음
    public ClientFriendsMenuPanel getParentPanel() {
        return parentPanel;
    }
}
