// 폰트 설정
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.InputStream;

public class FontSource {

    private static Font baseFont;

    static {
        try {
            InputStream is = FontSource.class.getResourceAsStream("/IM_Hyemin-Bold.ttf");
            if (is == null) throw new RuntimeException("폰트 파일을 찾을 수 없음: /IM_Hyemin-Bold.ttf");

            baseFont = Font.createFont(Font.TRUETYPE_FONT, is);
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(baseFont);
            
        } catch (Exception e) {
            System.out.println("[FontManager] 폰트 로드 실패 → SansSerif 사용");
            baseFont = new Font("SansSerif", Font.PLAIN, 12);
        }
    }

    // 외부에서 사용할 폰트 사이즈
    public static Font get(float size) {
        return baseFont.deriveFont(size);
    }
}
