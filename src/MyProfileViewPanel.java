import javax.swing.*;

public class MyProfileViewPanel extends JPanel {
	
	// 부모는 ProfileEditFrame
    private final MyProfileViewFrame parentFrame;

    public MyProfileViewPanel(MyProfileViewFrame parentFrame, String username, String ip_addr, String port_no) {
        this.parentFrame = parentFrame;

        setLayout(null);
        setOpaque(true);
        
        
        
        
    }

}
