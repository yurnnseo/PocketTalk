import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class StartPocketTalkPanel extends JPanel {

   private Image backgroundImg = null; // 배경 이미지 저장변수
   
   public StartPocketTalkPanel(JFrame mainFrame, String initialBackground) {
       backgroundImg = new ImageIcon(getClass().getResource("/초기화면.png")).getImage();
       setLayout(null);
   }

   
   @Override
    protected void paintComponent(Graphics g) {
      super.paintComponent(g);
        if (backgroundImg != null) {
            g.drawImage(backgroundImg, 0, 0, getWidth(), getHeight(), this);
        }
   }
}
