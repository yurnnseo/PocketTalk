// 상세 프로필 보기 프레임
import java.awt.Window;
import java.io.DataOutputStream;
import java.io.IOException;

import javax.swing.JFrame;

public class MyProfileViewFrame extends JFrame {
	
	private final ClientFriendsMenuPanel parentMenuPanel;
    private MyProfileViewPanel myProfileViewPanel;
    private final DataOutputStream dos; // 서버로 보낼 스트림
    private String profileImagePath;        

    public MyProfileViewFrame(ClientFriendsMenuPanel parentMenuPanel, String username, String ip_addr, String port_no, String profileImagePath, String statusM, DataOutputStream dos) {
    	this.parentMenuPanel = parentMenuPanel;
        this.dos = dos;
        this.profileImagePath = (profileImagePath == null || profileImagePath.isEmpty()) ? "/Images/defaultprofileimage.png" : profileImagePath;

        setTitle("나의 프로필");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        setSize(330, 490);
        
        myProfileViewPanel = new MyProfileViewPanel(this, username, ip_addr, port_no, this.profileImagePath, statusM);
        setContentPane(myProfileViewPanel);
    }

    public void onMyProfileUpdated(String newName, String newStatus) {
        if (parentMenuPanel != null) {
            parentMenuPanel.updateMyProfileName(newName, newStatus);
        }      
    }
    
    public void sendProfileUpdateToServer(String newName, String newStatus, String newImagePath) {
        if (dos == null) return;

        String safeName = (newName == null || newName.isEmpty()) ? "noname" : newName;
        String safeStatus = (newStatus == null) ? "" : newStatus;

        // 새 이미지가 넘어오면 그걸 우선 사용
        if (newImagePath != null && !newImagePath.isEmpty()) {
            this.profileImagePath = newImagePath;
        }
        String imgPath = (profileImagePath == null || profileImagePath.isEmpty()) ? "/Images/defaultprofileimage.png" : profileImagePath;

        String msg = "/profile_update " + safeName + " " + imgPath + " " + safeStatus;

        try {
            dos.writeUTF(msg);
            dos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 프로필 이미지가 추후에 바뀌는 경우를 위해 setter 만들어두기
    public void setProfileImagePath(String newPath) {
        this.profileImagePath = newPath;
    }
}