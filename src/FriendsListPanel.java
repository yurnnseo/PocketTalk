//친구메뉴패널에 뜨는 친구 목록
import java.util.List;
import javax.swing.JPanel;
import java.awt.*;

public class FriendsListPanel extends JPanel{
	private String myname;
	private final String profileImagePath = "/Images/defaultprofileimage.png";
	
	 public FriendsListPanel(String name) {
	        this.myname = name;
	        setLayout(null);
	        setOpaque(false);
	    }
	 
	 public void updateList(List<String> friendsName) {
		removeAll(); //목록 갱신 시 중복 방지
		 
		 int y = 15; //시작위치
		 
		 for(String name : friendsName) {
			 if(name.equals(myname)) continue;
			 
			 ProfileHeaderView header = new ProfileHeaderView(name,profileImagePath,50,50,ProfileHeaderView.Orientation.HORIZONTAL);
			 header.setBounds(20, y, header.getPreferredSize().width, header.getPreferredSize().height);
			 add(header);

	         y += header.getPreferredSize().height + 15; // 다음 친구 위치
		 }
		 
		// 스크롤 영역 계산: 패널의 preferredSize 직접 설정
	    int friendslistpanelWidth = 260; // scrollPane 내용영역보다 살짝 작게 잡아도 됨
	    int friendslistpanelHeight = Math.max(y, 300); // 최소 300임. 친구 많으면 그 이상으로
	    setPreferredSize(new Dimension(friendslistpanelWidth, friendslistpanelHeight));
		
		 revalidate();
		 repaint();
	 }
}
