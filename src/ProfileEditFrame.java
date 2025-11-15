import java.awt.Window;
import javax.swing.JFrame;

public class ProfileEditFrame extends JFrame {

    // 논리적 부모(생성 트리 상의 부모 패널)
    private final MyProfileViewPanel parentPanel;

    public ProfileEditFrame(MyProfileViewPanel parentPanel, String username, String ip_addr, String port_no, String profileImagePath, String currentStatusMessage) {
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
        System.out.println("[ProfileEditFrame] 저장됨: name=" + newName + ", status=" + statusToSend);

        // 1) 내 프로필 화면(MyProfileViewPanel) 갱신
        parentPanel.updateProfile(newName, statusToSend);

        // 2) TODO: 여기서 서버로 /profile_update 전송 (나중에 구현)
        // ex) parentPanel.getParentFrame().getClient().sendProfileUpdate(...);

        // 3) 편집창 닫기
        dispose();
    }
}
