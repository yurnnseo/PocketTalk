import javax.swing.JFrame;

//내 프로필 상세 화면을 띄우는 프레임
public class MyProfileViewFrame extends JFrame {
	
    private final ClientFriendsMenuPanel parentMenuPanel; // 친구 목록 화면 (내 이름/상태메시지 갱신용)
    private final ClientMenuFrame parentFrame; // 서버로 명령을 보낼 때 사용
    private MyProfileViewPanel myProfileViewPanel;
    private String profileImagePath; // 현재 프로필 이미지 경로
    
    private String ip;
    private String port;

    
    public MyProfileViewFrame(ClientFriendsMenuPanel parentMenuPanel, ClientMenuFrame parentFrame, String username, String profileImagePath, String statusM) {
        
        this.parentMenuPanel = parentMenuPanel;
        this.parentFrame = parentFrame;
        
        this.ip = parentFrame.getIp();
        this.port = parentFrame.getPort();
        
        // 프로필 이미지가 없으면 기본 이미지 사용
        this.profileImagePath = (profileImagePath == null || profileImagePath.isEmpty()) ? "/Images/defaultprofileimage.png" : profileImagePath;
        

        setTitle("나의 프로필");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        setSize(330, 490);
        
        // 프로필 상세 내용을 그리는 패널
        myProfileViewPanel = new MyProfileViewPanel(this, username, ip, port, this.profileImagePath, statusM);
        setContentPane(myProfileViewPanel);
    }

    // 내 프로필(이름, 상태메시지)이 변경되었을 때 상단 친구 패널 헤더도 갱신
    public void onMyProfileUpdated(String newName, String newStatus) {
        if (parentMenuPanel != null) {
            parentMenuPanel.updateMyProfileName(newName, newStatus);
        }      
    }
    
    // 프로필 변경 내용을 서버에 알림
    public void sendProfileUpdateToServer(String newName, String newStatus, String newImagePath) {
        if (parentFrame == null) return;

        String safeName = (newName == null || newName.isEmpty()) ? "noname" : newName;
        
        String safeStatus = (newStatus == null) ? "" : newStatus;

        // 새 이미지가 있다면 현재 이미지 경로 갱신
        if (newImagePath != null && !newImagePath.isEmpty()) {
            this.profileImagePath = newImagePath;
        }
        
        String imgPath = (profileImagePath == null || profileImagePath.isEmpty()) ? "/Images/defaultprofileimage.png" : profileImagePath;

        String msg = "/profile_update " + safeName + " " + imgPath + " " + safeStatus;

        // ClientMenuFrame을 통해 서버로 전송
        parentFrame.sendToServer(msg);
    }

    // 프로필 이미지가 추후에 바뀌는 경우를 위한 setter
    public void setProfileImagePath(String newPath) {
        this.profileImagePath = newPath;
    }
}
