//말풍선 디자인 패널
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

public class MessagePanel extends JPanel {
    private boolean isSent;
    private static final Color SENT_COLOR = Color.decode("#E3D6F0"); //내 메시지
    private static final Color RECEIVED_COLOR = Color.WHITE;  //상대 메시지
    private static final int ARC = 20; // 둥근 모서리 반지름
    private Font customFont; //폰트 소스

    // 한 줄에 허용할 최대 글자 수
    private static final int MAX_CHARS_PER_LINE = 20;

    public MessagePanel(String text, boolean isSent, Font font) {
        this.isSent = isSent;
        this.customFont = font;

        // 글자 개수 기준으로 \n 넣어서 줄바꿈
        String wrappedText = wrapText(text, MAX_CHARS_PER_LINE);

        JTextArea area = new JTextArea(wrappedText);
        area.setEditable(false);
        area.setFocusable(false);
        area.setOpaque(false);      // 배경은 말풍선에서 그릴 거라 투명
        area.setLineWrap(false);    // 직접 \n으로만 줄바꿈
        area.setBorder(null);

        if (customFont != null) {
            area.setFont(customFont.deriveFont(13f));
        } else {
            area.setFont(area.getFont().deriveFont(13f));
        }

        setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        setLayout(new BorderLayout());
        add(area, BorderLayout.CENTER);
        setOpaque(false);
    }

    // 글자 수 기준으로 줄 나누기
    private String wrapText(String text, int maxCharsPerLine) {
        if (text == null) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            if (i > 0 && i % maxCharsPerLine == 0) {
                sb.append('\n'); // maxCharsPerLine마다 줄바꿈
            }
            sb.append(text.charAt(i));
        }
        return sb.toString();
    }

    @Override
    public Dimension getMaximumSize() {
        Dimension d = getPreferredSize();
        d.width = Integer.MAX_VALUE; // 가로는 컨테이너에서 조절
        return d;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();

        // 둥근 모서리를 부드럽게
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                             RenderingHints.VALUE_ANTIALIAS_ON);

        // 말풍선 배경색 지정
        Color bubbleColor = isSent ? SENT_COLOR : RECEIVED_COLOR;
        g2d.setColor(bubbleColor);

        // 둥근 사각형 그리기
        RoundRectangle2D rect =
                new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), ARC, ARC);
        g2d.fill(rect);

        g2d.dispose();
    }
}
