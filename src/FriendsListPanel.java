// 친구메뉴패널에 뜨는 친구 목록
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.Map;

public class FriendsListPanel extends JPanel {

    private String myName; // 내 이름 (리스트에서 나를 빼기 위함)

    // 친구 프로필 저장
    private final Map<String, FriendProfile> friends = new LinkedHashMap<>();
    private static final String DEFAULT_PROFILE_IMAGE = "/Images/defaultprofileimage.png";
    private boolean isSelectedMode = false;
    private FontSource fontSource = new FontSource("/IM_Hyemin-Bold.ttf");
    private final Set<String> selectedUsers = new LinkedHashSet<>();
    
    // 친구 한 명에 대한 정보 (이름, 상태메시지, 프로필 이미지 경로)
    private static class FriendProfile {
        String name;
        String statusMessage;
        String profileImagePath;

        FriendProfile(String name, String statusMessage, String profileImagePath) {
            this.name = name;
            this.statusMessage = statusMessage;
            this.profileImagePath = (profileImagePath == null || profileImagePath.isEmpty()) ? DEFAULT_PROFILE_IMAGE : profileImagePath;
        }
    }

    public FriendsListPanel(String myName) {
        this.myName = myName;
        setLayout(null);
        setOpaque(false);
    }

    // 내 이름/상태가 바뀌었을 때 호출 (리스트에서 나 자신은 제외지만 이름 비교를 위해)
    public void setMyProfile(String newMyName, String newStatusMessage) {
        this.myName = newMyName;
    }
    
    // ClientFriendsMenuPanel에서 호출하는 이름과 맞추기 위해 
    public void setMyName(String newMyName, String newStatusMessage) {
        setMyProfile(newMyName, newStatusMessage);
    }
    
    // ChoosePerson에서 쓰던 updateList(users)를 유지하기 위해
    public void updateList(java.util.List<String> users) {
        // 선택 창에서는 그냥 온라인 목록처럼 취급해도 문제 없음
        updateOnlineList(users);
    }

    public void updateFriendProfile(String name, String imagePath, String statusMessage) {
        if (name == null || name.isEmpty()) return;

        // 내 이름이면 친구 목록에는 표시하지 않으므로 그냥 무시
        if (name.equals(myName)) {
            return;
        }

        FriendProfile fp = friends.get(name);
        if (fp == null) {
            fp = new FriendProfile(name, statusMessage, imagePath);
            friends.put(name, fp);
        } 
        else {
            fp.name = name;
            fp.statusMessage = (statusMessage == null) ? "" : statusMessage;
            if (imagePath != null && !imagePath.isEmpty()) {
                fp.profileImagePath = imagePath;
            }
        }

        refreshView();
    }

    
    // /list name1 name2 ... 를 받았을 때 호출해줄 메서드
    // 현재 온라인인 이름 목록을 기준으로 friends 맵을 정리한다
    // 오프라인이 된 친구는 friends에서 제거
    
    public void updateOnlineList(List<String> onlineNames) {
        if (onlineNames == null) return;

        // LinkedHashSet 을 써서 순서를 유지
        Set<String> onlineSet = new LinkedHashSet<>(onlineNames);

        // 오프라인 친구 제거 (현재 friends 에 있는데 onlineSet 에 없는 경우)
        friends.keySet().removeIf(name -> !onlineSet.contains(name));

        // 새로 접속한 사람 중, 아직 friends 에 없는 경우 기본 프로필로 추가
        for (String name : onlineSet) {
            if (name.equals(myName)) continue; // 나 자신은 건너뜀
            if (!friends.containsKey(name)) {
                friends.put(name, new FriendProfile(name, "", DEFAULT_PROFILE_IMAGE));
            }
        }

        refreshView();
    }

    
     // 화면을 다시 그리는 메서드
     // friends 맵에 있는 순서대로 ProfileHeaderView 를 다시 추가함
    private void refreshView() {
        removeAll();

        int y = 15;

        for (FriendProfile fp : friends.values()) {
            
            if (fp.name.equals(myName)) continue; // 혹시라도 myName이 friends에 들어있다면 건너뜀

            ProfileHeaderView header = new ProfileHeaderView(fp.name, fp.statusMessage, fp.profileImagePath, 50, 50, ProfileHeaderView.Orientation.HORIZONTAL);
            header.setBounds(30, y, header.getPreferredSize().width, header.getPreferredSize().height);
            add(header);
    
                header.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if(selectedUsers.contains(fp.name)) selectedUsers.remove(fp.name);
                        else selectedUsers.add(fp.name);
                        refreshView();
                    }
                });
        
            y += header.getPreferredSize().height + 15;
        }

        int friendslistpanelWidth = 260;
        int friendslistpanelHeight = Math.max(y, 300);
        setPreferredSize(new Dimension(friendslistpanelWidth, friendslistpanelHeight));

        revalidate();
        repaint();
    }
}
