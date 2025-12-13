import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextArea;

// 텍스트 말풍선 하나를 그리는 패널
public class MessagePanel extends SameMessagePanel {

    private final JTextArea area;
    private final int maxWidth; // 말풍선의 최대 가로

    public MessagePanel(String text, boolean isSent, Font font, int maxWidth) {
        super(isSent);
        this.maxWidth = maxWidth;

        setLayout(new BorderLayout());

        area = new JTextArea(text == null ? "" : text); // 메시지 텍스트 영역
        
        area.setEditable(false);
        area.setFocusable(false);
        
        area.setOpaque(false); 
        area.setBorder(null);

        area.setLineWrap(true); // 가로 폭 초과 시 줄바꿈
        area.setWrapStyleWord(true); // 단어 단위로 줄바꿈

        if (font != null) area.setFont(font.deriveFont(13f));
        else area.setFont(area.getFont().deriveFont(13f));

        add(area, BorderLayout.CENTER);

        updateSize(); // 최대 폭 기준으로 말풍선 크기 계산
    }

    // 텍스트 길이에 따라 말풍선 크기를 자동 계산하는 메소드
    private void updateSize() {
        FontMetrics fm = area.getFontMetrics(area.getFont());

        
        int textW = fm.stringWidth(area.getText()) + 6; // 텍스트가 한 줄일 때 폭
        int targetW = Math.min(textW, maxWidth); // 말풍선 폭 

        // JTextArea 폭만 고정함. 긴 텍스트는 자동 줄바꿈, 높이 자동 증가
        area.setColumns(1);              
        area.setSize(new Dimension(targetW, Short.MAX_VALUE));
        Dimension pref = area.getPreferredSize();

        area.setPreferredSize(new Dimension(targetW, pref.height));

        // 말풍선 배경 크기 맞추기
        setPreferredSize(new Dimension(targetW + getInsets().left + getInsets().right, pref.height + getInsets().top + getInsets().bottom));
        setMaximumSize(getPreferredSize());
    }

}