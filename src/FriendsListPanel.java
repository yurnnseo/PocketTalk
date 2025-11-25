import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;


public class FriendsListPanel extends JPanel {

    private String myName; // 내 이름 (리스트에서 나를 빼기 위함)

    // 프로필 정보: name -> FriendProfile(이름, 상태메시지, 이미지경로)
    private final Map<String, FriendProfile> profiles = new HashMap<>();

    // 현재 온라인인 친구 이름 순서 ( /list 결과 )
    private final LinkedHashSet<String> onlineNames = new LinkedHashSet<>();

    private final Set<String> selectedUsers = new LinkedHashSet<>();

    private boolean isSelectionMode = false; //채팅 상대 선택 모드
    
    private static final String DEFAULT_PROFILE_IMAGE = "/Images/defaultprofileimage.png";
    

    // ----- 한 명의 친구 정보 -----
    private static class FriendProfile {
        String name;
        String statusMessage;
        String profileImagePath;

        FriendProfile(String name, String statusMessage, String imagePath) {
            this.name = name;
            this.statusMessage = (statusMessage == null) ? "" : statusMessage;

            if (imagePath == null || imagePath.isEmpty()) {
                this.profileImagePath = DEFAULT_PROFILE_IMAGE;
            } else {
                this.profileImagePath = imagePath;
            }
        }
    }

    //기본 생성자
    public FriendsListPanel(String myName) {
        this(myName, false); 
    }
    
    //선택 모드 생성자
    public FriendsListPanel(String myName, boolean isSelectionMode) {
        this.myName = myName;
        this.isSelectionMode = isSelectionMode;
        setLayout(null);
        setOpaque(false);
    }

    // 내 이름 변경 (리스트에서 나 자신 제외용)
    public void setMyName(String newMyName) {
        if (newMyName != null) {
            this.myName = newMyName.trim();
        }
        refreshView();
    }

    // ChoosePerson에서 쓰던 버전 → 결국 온라인 목록과 동일하게 취급
    public void updateList(List<String> users) {
        updateOnlineList(users);
    }

    /**
     * 서버에서 "/profile name imagePath status..." 를 받았을 때 호출
     * → 프로필 정보만 갱신 (온라인 여부는 건드리지 않음)
     */
    public void updateFriendProfile(String name, String imagePath, String statusMessage) {
        if (name == null) return;
        name = name.trim();
        if (name.isEmpty()) return;

        if (statusMessage != null) statusMessage = statusMessage.trim();
        if (imagePath != null) imagePath = imagePath.trim();


        FriendProfile fp = profiles.get(name);
        if (fp == null) {
            // 처음 받는 경우: 그대로 사용
            fp = new FriendProfile(name, statusMessage, imagePath);
        } else {
            fp.name = name;

            // 새로 받은 상태메시지가 "실제 내용"이 있을 때만 덮어쓴다.
            if (statusMessage != null && !statusMessage.isEmpty()) {
                fp.statusMessage = statusMessage;
            }
            // 비어있으면 기존 fp.statusMessage 그대로 유지

            // 이미지 경로는 이전처럼, 새 값이 있으면 덮어쓰기
            if (imagePath != null && !imagePath.isEmpty()) {
                fp.profileImagePath = imagePath;
            }
        }
        profiles.put(name, fp);
        refreshView();
    }

    /**
     * 서버에서 "/list name1 name2 ..." 를 받았을 때 호출
     * → "누가 온라인인지"만 관리, 프로필 내용은 그대로 둠
     */
    public void updateOnlineList(List<String> onlineList) {
        if (onlineList == null) return;

        onlineNames.clear();

        // 여기서도 trim을 해 줘야 /list "권제이" 와 /profile "권제이 " 같은 게 안 엇갈림
        for (String raw : onlineList) {
            if (raw == null) continue;
            String name = raw.trim();
            if (!name.isEmpty()) {
                onlineNames.add(name);
            }
        }

        refreshView();
    }

    /**
     * profiles + onlineNames 기준으로 실제 UI 다시 그림
     */
    private void refreshView() {
        removeAll();

        int y = 15;


        for (String name : onlineNames) {
            // 내 자신은 친구 목록에서 제외
            if (name.equals(myName)) continue;

            FriendProfile fp = profiles.get(name);
            if (fp == null) {
                // 아직 프로필 정보 안 온 친구면 기본값으로 만들어둠
                fp = new FriendProfile(name, "", DEFAULT_PROFILE_IMAGE);
                profiles.put(name, fp);
            }

            ProfileHeaderView header = new ProfileHeaderView(
                    fp.name,
                    fp.statusMessage,
                    fp.profileImagePath,
                    50, 50,
                    ProfileHeaderView.Orientation.HORIZONTAL
            );

            header.setBounds(30, y,
                    header.getPreferredSize().width,
                    header.getPreferredSize().height);
            add(header);

            
            //선택 모드 -> 체크 표시 추가
            if(isSelectionMode) {
            	if(selectedUsers.contains(name)) {
            		
                    JLabel checkLabel = new JLabel("✔");
                    
                    int checkX = 10; // 패널 왼쪽에서 10 픽셀
                    int checkY = y + (header.getPreferredSize().height - 15) / 2; // 세로 중앙
                    checkLabel.setBounds(checkX, checkY, 15, 15); 
                    
                    add(checkLabel);
            	}
            }
            
            final String userName = fp.name;

	         // 공통으로 쓸 클릭 리스너 하나 만들기
	         MouseAdapter clickListener = new MouseAdapter() {
	             @Override
	             public void mouseClicked(MouseEvent e) {
	                 if (!isSelectionMode) return;
	
	                 if (selectedUsers.contains(userName)) {
	                     selectedUsers.remove(userName);
	                 } else {
	                     selectedUsers.add(userName);
	                 }
	                 refreshView();
	             }
	         };
	
	         // header 자체에 달고
	         header.addMouseListener(clickListener);
	
	         // header 안의 모든 자식 컴포넌트에도 동일 리스너 달기
	         for (Component c : header.getComponents()) {
	             c.addMouseListener(clickListener);
	         }


            y += header.getPreferredSize().height + 15;
        }

        int friendslistpanelWidth = 260;
        int friendslistpanelHeight = Math.max(y, 300);
        setPreferredSize(new Dimension(friendslistpanelWidth, friendslistpanelHeight));

        revalidate();
        repaint();
    }
    
    //ChoosePerson에서 활용 할 선택된 사용자 목록 가져오는 메서드
    public Set<String> getSelectedUsers() {
        return selectedUsers;
    }
    
}
