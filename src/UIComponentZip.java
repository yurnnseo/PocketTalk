// 공통 UI 모음
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.border.LineBorder;

public class UIComponentZip {

    private static final Color BUTTON_NORMAL = Color.WHITE;
    private static final Color BUTTON_HOVER  = Color.decode("#E3D6F0");

    private UIComponentZip() {} 
    // 텍스트 버튼
    public static JButton createTextButton(String text, int x, int y, int width, int height, Font font) {
        JButton btn = new JButton(text);
        btn.setBounds(x, y, width, height);
        btn.setBackground(BUTTON_NORMAL);
        btn.setBorder(new LineBorder(Color.BLACK));
        btn.setFont(font);

        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(BUTTON_HOVER);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(BUTTON_NORMAL);
            }
        });

        return btn;
    }

    // 리소스 경로에서 아이콘 로드 + 스케일링
    public static ImageIcon loadScaledIcon(String resourcePath, int w, int h) {
        try {
            ImageIcon icon = new ImageIcon(UIComponentZip.class.getResource(resourcePath));
            Image scaled = icon.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
            return new ImageIcon(scaled);
        } catch (Exception e) {
            System.out.println("아이콘 로드 실패: " + resourcePath + " → " + e);
            return null;
        }
    }

    // PNG 아이콘 버튼
    public static JButton createIconButton(String resourcePath, int size, int x, int y) {
        ImageIcon icon = loadScaledIcon(resourcePath, size, size);
        JButton btn = new JButton(icon);
        btn.setBounds(x, y, size, size);

        btn.setBorder(null);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        return btn;
    }
}
