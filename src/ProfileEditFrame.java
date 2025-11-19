import java.awt.Window;
import javax.swing.JFrame;

public class ProfileEditFrame extends JFrame {

    private final MyProfileViewPanel parentPanel;
    private final MyProfileViewFrame parentFrame;

    public ProfileEditFrame(MyProfileViewFrame parentFrame, MyProfileViewPanel parentPanel, String username, String ip_addr, String port_no, String profileImagePath, String currentStatusMessage) {
    	this.parentFrame = parentFrame;
    	this.parentPanel = parentPanel;

        setTitle("프로필 편집");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        
        // ProfileEditPanel의 부모는 이 프레임
        setContentPane(new ProfileEditPanel(this, username, ip_addr, port_no, currentStatusMessage));
        setSize(330, 490);
    }

    // ProfileEditPanel에서 저장 눌렀을 때 호출
    public void onProfileSaved(String newName, String statusToSend) {

        // 내 프로필 화면(MyProfileViewPanel) 갱신
        parentPanel.updateProfile(newName, statusToSend);

        // 서버로 /profile_update 전송 
        parentFrame.sendProfileUpdateToServer(newName, statusToSend);

        // 편집창 닫기
        dispose();
    }
}