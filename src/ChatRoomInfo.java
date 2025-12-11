import java.util.List;

// 채팅방 하나에 대한 정보 (방ID, 채팅방 만든 사람, 멤버 리스트)
public class ChatRoomInfo {
	
    private String roomId;
    private String creator;
    private List<String> members;

    public ChatRoomInfo(String roomId, String creator, List<String> members) {
        this.roomId = roomId;
        this.creator = creator;
        this.members = members;
    }

    // getter 함수
    public String getRoomId() {
        return roomId;
    }

    public String getCreator() {
        return creator;
    }

    public List<String> getMembers() {
        return members;
    }

    // setter 함수
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
