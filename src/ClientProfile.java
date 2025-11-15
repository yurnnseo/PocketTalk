import java.io.Serializable;

// 클라이언트 프로필 정보 (이름, 상태메시지, 프로필 이미지 경로)
public class ClientProfile implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;             // 유저 이름 (key)
    private String statusMessage;    // 상태 메시지
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

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public String getProfileImagePath() {
        return profileImagePath;
    }

    public void setProfileImagePath(String profileImagePath) {
        this.profileImagePath = profileImagePath;
    }
}
