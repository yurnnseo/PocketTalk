import java.awt.Window;
import javax.swing.JFrame;

public class MyProfileViewFrame extends JFrame {
	
	private final ClientFriendsMenuPanel parentMenuPanel;
    private MyProfileViewPanel myProfileViewPanel;

    public MyProfileViewFrame(ClientFriendsMenuPanel parentMenuPanel, String username, String ip_addr, String port_no, String profileImagePath) {
        this.parentMenuPanel = parentMenuPanel;

        setTitle("나의 프로필");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        // MyProfileViewPanel의 부모는 이 프레임
        setContentPane(new MyProfileViewPanel(this, username, ip_addr, port_no, profileImagePath));
        setSize(330, 490);
        
        myProfileViewPanel = new MyProfileViewPanel(this, username, ip_addr, port_no, profileImagePath);
        setContentPane(myProfileViewPanel);
    }

    public void onMyProfileUpdated(String newName, String newStatus) {
        if (parentMenuPanel != null) {
            parentMenuPanel.updateMyProfileName(newName);
        }
        // TODO: 필요하면 상태메시지도 어딘가에 표시 가능
    }
}
