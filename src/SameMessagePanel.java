import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

// 말풍선 배경(둥근 사각형)을 그리는 공통 패널
public abstract class SameMessagePanel extends JPanel {

    protected final boolean isSent; // 내 메시지인지(오른쪽) 상대 메시지인지(왼쪽)

    protected static final Color SENT_COLOR = Color.decode("#E3D6F0"); // 내 말풍선
    protected static final Color RECEIVED_COLOR = Color.WHITE; // 상대 말풍선
    protected static final int ARC = 20; // 둥근 모서리 반지름

    public SameMessagePanel(boolean isSent) {
        this.isSent = isSent;

        setOpaque(false); 
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(8, 12, 8, 12)); // 말풍선 안쪽 여백
    }

    // 메시지 패널이 가로로 꽉 차게
    @Override
    public Dimension getMaximumSize() {
        Dimension d = getPreferredSize();
        d.width = Integer.MAX_VALUE; // 가로는 컨테이너가 늘릴 수 있게
        return d;
    }

    // 말풍선 배경을 그림
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g.create();
        
        // 둥근 모서리를 부드럽게
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 말풍선 배경색 지정
        g2d.setColor(isSent ? SENT_COLOR : RECEIVED_COLOR);

        // 둥근 사각형 그리기
        RoundRectangle2D rect = new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), ARC, ARC);
        g2d.fill(rect);

        g2d.dispose();
    }
}
