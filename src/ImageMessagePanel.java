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

public class ImageMessagePanel extends JPanel {

    private boolean isSent;
    private static final Color SENT_COLOR = Color.decode("#E3D6F0"); // 내 메시지
    private static final Color RECEIVED_COLOR = Color.WHITE;         // 상대 메시지
    private static final int ARC = 20;

    private ImageIcon imageIcon;

    public ImageMessagePanel(ImageIcon icon, boolean isSent) {
        this.isSent = isSent;
        this.imageIcon = icon;

        JLabel label = new JLabel(imageIcon);

        setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        setLayout(new BorderLayout());
        add(label, BorderLayout.CENTER);
        setOpaque(false);
    }

    @Override
    public Dimension getMaximumSize() {
        Dimension d = getPreferredSize();
        d.width = Integer.MAX_VALUE;
        return d;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color bubbleColor = isSent ? SENT_COLOR : RECEIVED_COLOR;
        g2d.setColor(bubbleColor);

        RoundRectangle2D rect =
            new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), ARC, ARC);
        g2d.fill(rect);

        g2d.dispose();
    }
}
