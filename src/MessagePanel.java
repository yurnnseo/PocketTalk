import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextArea;

// 텍스트 말풍선 하나를 그리는 패널
public class MessagePanel extends SameMessagePanel {
	
	public MessagePanel(String text, boolean isSent, Font font) {
        super(isSent);

        JTextArea area = new JTextArea(text == null ? "" : text);

        area.setEditable(false);
        area.setFocusable(false);
        area.setOpaque(false);
        area.setBorder(null);

        area.setLineWrap(true);
        area.setWrapStyleWord(true);

        if (font != null) area.setFont(font.deriveFont(13f));
        else area.setFont(area.getFont().deriveFont(13f));

        add(area); 
    }
}