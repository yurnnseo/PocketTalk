import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;

//친구 목록 그려주는 패널
public class FriendsListPanel extends JPanel {

	private static final String DEFAULT_PROFILE_IMAGE = "/Images/defaultprofileimage.png";
    private String myName; 

    // 이름으로 친구들의 프로필 정보를 map으로 저장
    private static final Map<String, FriendProfile> profiles = new HashMap<>();

    // 현재 온라인인 친구 이름을 저장 (중복없이)
    private final LinkedHashSet<String> onlineNames = new LinkedHashSet<>();

    // 대화 상대를 선택할 때 선택된 친구들을 저장
    private final Set<String> selectedUsers = new LinkedHashSet<>();

    private boolean isSelectionMode = false; //채팅 상대를 선택하는 모드인지, 아닌지


    // 한 명의 친구 정보를 담는 클래스
    private static class FriendProfile {
        String name;
        String statusMessage;
        String profileImagePath;

        FriendProfile(String name, String statusMessage, String imagePath) {
            this.name = name;
            this.statusMessage = (statusMessage == null) ? "" : statusMessage;

            if (imagePath == null || imagePath.isEmpty()) {
                this.profileImagePath = DEFAULT_PROFILE_IMAGE;
            } 
            else {
                this.profileImagePath = imagePath;
            }
        }
    }

    // 일반 친구 목록
    public FriendsListPanel(String myName) {
        this(myName, false); 
    }
    
    // 대화 상태 선택 목록 (ChoosePerson에서 사용)
    public FriendsListPanel(String myName, boolean isSelectionMode) {
        this.myName = myName;
        this.isSelectionMode = isSelectionMode;
        setLayout(null);
        setOpaque(false);
    }

    // 내 이름 바뀌었을 때 업데이트
    public void setMyName(String newMyName) {
        if (newMyName != null) {
            this.myName = newMyName.trim();
        }
        refreshView();
    }

    // 친구 목록 전체 업데이트
    public void updateList(List<String> users) {
        updateOnlineList(users);
    }

    // /profile로 온 친구 프로필 정보 저장
    public void updateFriendProfile(String name, String imagePath, String statusMessage) {
        if (name == null) return;
        name = name.trim();
        if (name.isEmpty()) return;

        if (statusMessage != null) statusMessage = statusMessage.trim();
        if (imagePath != null) imagePath = imagePath.trim();

        // 친구 프로필이 있으면 그 정보를 업데이트하고 없으면 새로 만듦
        FriendProfile fp = profiles.get(name);
        if (fp == null) {
            fp = new FriendProfile(name, statusMessage, imagePath);
        } 
        else {
            fp.name = name;

            // 새로 받은 상태메시지가 "실제 내용"이 있을 때만 덮어씀
            if (statusMessage != null && !statusMessage.isEmpty()) {
                fp.statusMessage = statusMessage;
            }
            // 비어있으면 기존 fp.statusMessage 그대로 유지

            if (imagePath != null && !imagePath.isEmpty()) {
                fp.profileImagePath = imagePath;
            }
        }
        
        profiles.put(name, fp);
        refreshView();
    }

    // /list로 온 친구 이름을 저장
    public void updateOnlineList(List<String> onlineList) {
        if (onlineList == null) return;

        onlineNames.clear(); //초기화

        // 새 친구 목록 넣음
        for (String raw : onlineList) {
            if (raw == null) continue;
            
            String name = raw.trim();
            if (!name.isEmpty()) {
                onlineNames.add(name);
            }
        }

        refreshView();
    }

    // 화면 다시 그려주는 함수
    private void refreshView() {
        removeAll(); // 기존 친구 목록을 싹 다 지움

        int y = 15;

        for (String name : onlineNames) {
            // 내 자신은 친구 목록에서 제외
            if (name.equals(myName)) continue;

            FriendProfile fp = profiles.get(name);
            
            if (fp == null) {
                fp = new FriendProfile(name, "", DEFAULT_PROFILE_IMAGE);
                profiles.put(name, fp);
            }

            ProfileHeaderView header = new ProfileHeaderView(fp.name, fp.statusMessage, fp.profileImagePath, 50, 50, ProfileHeaderView.Orientation.HORIZONTAL);
            header.setBounds(30, y, header.getPreferredSize().width, header.getPreferredSize().height);
            add(header);

            
            // 선택 모드일 때 체크 표시 추가
            if(isSelectionMode) {
            	if(selectedUsers.contains(name)) {
            		
                    JLabel checkLabel = new JLabel("✔");
                    
                    int checkX = 10;
                    int checkY = y + (header.getPreferredSize().height - 15) / 2; // 세로 중앙
                    checkLabel.setBounds(checkX, checkY, 15, 15); 
                    
                    add(checkLabel);
            	}
            }
            
            // 친구 클릭하면 선택/해제 되게 함.
            final String userName = fp.name;

	         MouseAdapter clickListener = new MouseAdapter() {
	             @Override
	             public void mouseClicked(MouseEvent e) {
	                 if (!isSelectionMode) return;
	
	                 if (selectedUsers.contains(userName)) {
	                     selectedUsers.remove(userName);
	                 } 
	                 else {
	                     selectedUsers.add(userName);
	                 }
	                 refreshView();
	             }
	         };

	         header.addMouseListener(clickListener);
	
	         // header 안의 모든 자식 컴포넌트에도 클릭 이벤트를 붙임.
	         for (Component c : header.getComponents()) {
	             c.addMouseListener(clickListener);
	         }

            y += header.getPreferredSize().height + 15;
        }

        setPreferredSize(new Dimension(260, Math.max(y, 300)));
        
        revalidate();
        repaint();
    }
    
    // ChoosePerson에서 사용할 선택된 사용자 목록 가져오는 메서드
    public Set<String> getSelectedUsers() {
        return selectedUsers;
    }
    
}
