// 프로필 수정 프레임
import java.awt.Window;
import javax.swing.JFrame;

public class ProfileEditFrame extends JFrame {

    private final MyProfileViewPanel parentPanel;
    private final MyProfileViewFrame parentFrame;
    private final String currentProfileImagePath;

    public ProfileEditFrame(MyProfileViewFrame parentFrame, MyProfileViewPanel parentPanel, String username, String ip_addr, String port_no, String profileImagePath, String currentStatusMessage) {
    	this.parentFrame = parentFrame;
    	this.parentPanel = parentPanel;
    	this.currentProfileImagePath = profileImagePath;

        setTitle("프로필 편집");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        
        // ProfileEditPanel의 부모는 이 프레임
        setContentPane(new ProfileEditPanel(this, username, ip_addr, port_no, currentStatusMessage, currentProfileImagePath));
        setSize(330, 490);
    }

    // ProfileEditPanel에서 저장 눌렀을 때 호출
    public void onProfileSaved(String newName, String statusToSend, String newImagePath) {

        // MyProfileViewPanel 갱신 (이미지 포함)
        parentPanel.updateProfile(newName, statusToSend, newImagePath);

        // 서버로 /profile_update 전송 (이미지 포함)
        parentFrame.sendProfileUpdateToServer(newName, statusToSend, newImagePath);

        dispose();
    }

}