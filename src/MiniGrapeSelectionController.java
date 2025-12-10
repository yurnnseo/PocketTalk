// 드래그 영역 선택 전담
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MiniGrapeSelectionController {

    private List<JLabel> selected = new ArrayList<>();

    public List<JLabel> getSelected() {
        return selected;
    }

    public void clearSelection() {
        for (JLabel lb : selected) {
            if (lb != null) {
                lb.setBorder(null);
            }
        }
        selected.clear();
    }

    public void updateSelection(JLabel[][] board, Rectangle dragRect) {
        clearSelection();

        if (board == null || dragRect == null) return;

        int rows = board.length;
        int cols = (rows > 0) ? board[0].length : 0;

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                JLabel g = board[r][c];
                if (g == null || !g.isVisible()) continue;

                if (g.getBounds().intersects(dragRect)) {
                    selected.add(g);
                    g.setBorder(
                            javax.swing.BorderFactory.createLineBorder(
                                    new Color(128, 0, 128), 3
                            )
                    );
                }
            }
        }
    }
}
