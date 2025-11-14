import java.util.List;
import javax.swing.JPanel;

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
		 
		 int y = 0; //시작위치
		 
		 for(String name : friendsName) {
			 if(name.equals(myname)) continue;
			 
			 ProfileHeaderView header = new ProfileHeaderView(name,profileImagePath,50,50,ProfileHeaderView.Orientation.HORIZONTAL);
			 
			 header.setBounds(0, y, header.getPreferredSize().width, header.getPreferredSize().height);
			 
			 add(header);

	         y += header.getPreferredSize().height + 10; // 다음 친구 위치
		 }
		
		 revalidate();
		 repaint();
	 }
}
