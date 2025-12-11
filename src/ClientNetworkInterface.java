import java.io.IOException;
import java.util.List;

public interface ClientNetworkInterface {
	
    void updateUserList(List<String> users); // 서버한테 받은 사용자 목록(/list)을 클라이언트에게 전달

    void updateProfile(String name, String imagePath, String statusMsg); // 특정 클라이언트의 프로필 정보(/profile)을 클라이언트에게 전달

    void updateChatRoom(String roomId, String creatorName, String members); // 채팅방 정보(/room)을 클라이언트에게 전달

    void startGame(String body); // 미니게임 시작(/game_start_command)을 클라이언트에게 전달

    void removeGameItem(String body); // 게임 내 포도 제거 명령(/game_apply_remove)을 클라이언트에게 전달

    void refillGameItems(String body); // 게임 내 포도 재배치 명령(/game_refill)을 클라이언트에게 전달

    void receiveChatMessage(String msg); // 채팅 메시지를 클라이언트의 채팅 패널에 전달

    void onDisconnected(IOException e); // 서버 연결이 끊겼다는 정보를 클라이언트에게 전달
}
