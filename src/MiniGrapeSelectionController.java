// 드래그 영역 선택 전담 컨트롤러
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MiniGrapeSelectionController {
	// 선택된 포도 레이블 저장
    private List<JLabel> selected = new ArrayList<>();

    public List<JLabel> getSelected() {
        return selected;
    }

    public void clearSelection() { // 선택 상태 초기화
        for (JLabel lb : selected) {
            if (lb != null) {
                lb.setBorder(null);
            }
        }
        selected.clear();
    }

    //  드래그 영역과 겹치는 포도 찾아서 selection에 추가
    public void updateSelection(JLabel[][] board, Rectangle dragRect) {
        clearSelection();

        if (board == null || dragRect == null) return;

        int rows = board.length;
        int cols = (rows > 0) ? board[0].length : 0;

        for (int r = 0; r < rows; r++) { // 보드 스캔
            for (int c = 0; c < cols; c++) {
                JLabel g = board[r][c];
                if (g == null || !g.isVisible()) continue;

                if (g.getBounds().intersects(dragRect)) { // 겹치는지 체크
                    selected.add(g);
                    // 선택된 포도 보더 표시
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
