
import java.util.List;

public class ChatRoomInfo {
    private String roomId;
    private String creator;
    private List<String> members;

    public ChatRoomInfo(String roomId, String creator, List<String> members) {
        this.roomId = roomId;
        this.creator = creator;
        this.members = members;
    }

    public String getRoomId() {
        return roomId;
    }

    public String getCreator() {
        return creator;
    }

    public List<String> getMembers() {
        return members;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }
}
