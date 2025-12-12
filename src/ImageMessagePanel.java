import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

// 이미지 말풍선 하나를 그리는 패널
public class ImageMessagePanel extends SameMessagePanel {

 public ImageMessagePanel(ImageIcon icon, boolean isSent) {
     super(isSent);
     add(new JLabel(icon));
 }
}