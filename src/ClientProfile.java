
// 클라이언트 프로필 정보 (이름, 상태메시지, 프로필 이미지 경로)
public class ClientProfile {
    private String name; // 사용자 이름 (key)
    private String statusMessage; // 상태 메시지
    private String profileImagePath; // 프로필 이미지 경로

    public ClientProfile(String name, String statusMessage, String profileImagePath) {
        this.name = name;
        this.statusMessage = statusMessage;
        this.profileImagePath = profileImagePath;
    }

    public String getName() {
        return name;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public String getProfileImagePath() {
        return profileImagePath;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public void setProfileImagePath(String profileImagePath) {
        this.profileImagePath = profileImagePath;
    }
}