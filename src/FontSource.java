import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.InputStream;

public class FontSource {
    private Font base;

    public FontSource(String resourcePath) {
        try {
            InputStream is = getClass().getResourceAsStream(resourcePath);
            if (is == null) throw new RuntimeException("리소스를 찾을 수 없음: " + resourcePath);

            base = Font.createFont(Font.TRUETYPE_FONT, is);
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(base); // 안정화
            System.out.println("[FontSource] loaded: " + base.getFontName());
        } catch (Exception e) {
            System.out.println("[FontSource] 로드 실패 → SansSerif: " + e);
            base = new Font("SansSerif", Font.PLAIN, 10);
        }
    }

    public Font getFont(float size) { return base.deriveFont(size); }
}
