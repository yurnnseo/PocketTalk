import javax.swing.*;
import java.awt.*;
import java.util.List;

// 합 계산 + 제거 전담

public class MiniGrapeGameManager {

    public static final int ROWS = 9;
    public static final int COLS = 8;

    private JLabel[][] leftBoard;
    private JLabel[][] rightBoard;

    public MiniGrapeGameManager(JLabel[][] leftBoard, JLabel[][] rightBoard) {
        this.leftBoard = leftBoard;
        this.rightBoard = rightBoard;
    }

    // 선택된 포도들의 합 계산 후, 합이 5이면 제거
    public int removeIfSumIsFive(List<JLabel> selected) {
        int total = 0;
        for (JLabel grape : selected) {
            Object v = grape.getClientProperty("value");
            if (v instanceof Integer) {
                total += (Integer) v;
            }
        }
        if (total == 5 && !selected.isEmpty()) {
            for (JLabel grape : selected) {
                removeGrape(grape);
            }
        }
        return total;
    }

    // 실제 배열 + 패널에서 제거
    public void removeGrape(JLabel grape) {
        if (grape == null) return;

        int r = (int) grape.getClientProperty("row");
        int c = (int) grape.getClientProperty("col");
        boolean isLeft = (boolean) grape.getClientProperty("isLeft");

        Container parent = grape.getParent();
        if (parent != null) parent.remove(grape);

        if (isLeft) {
            leftBoard[r][c] = null;
        } else {
            rightBoard[r][c] = null;
        }
    }

    // 오른쪽 보드(내 판)에 "합이 5가 되는 사각형 선택"이 실제로 가능한지 확인
    public boolean hasAnyMoveOnGameBoard() {
        if (rightBoard == null) return false;

        // 모든 시작점 (r1, c1)
        for (int r1 = 0; r1 < ROWS; r1++) {
            for (int c1 = 0; c1 < COLS; c1++) {

                // 모든 끝점 (r2, c2) - r2 >= r1, c2 >= c1 인 직사각형
                for (int r2 = r1; r2 < ROWS; r2++) {
                    for (int c2 = c1; c2 < COLS; c2++) {

                        int sum = 0;
                        int count = 0;

                        // (r1, c1) ~ (r2, c2) 사각형 안의 포도들 합 계산
                        for (int r = r1; r <= r2; r++) {
                            for (int c = c1; c <= c2; c++) {
                                JLabel g = rightBoard[r][c];
                                if (g == null) continue; // 빈 칸은 선택 안 된다고 보고 무시

                                Object v = g.getClientProperty("value");
                                if (!(v instanceof Integer)) continue;

                                sum += (Integer) v;
                                count++;
                            }
                        }

                        // 최소 1개 이상 선택되어 있고, 합이 5인 사각형이 있다면 → 실제로 지울 수 있는 조합 존재
                        if (count > 0 && sum == 5) {
                            return true;
                        }
                    }
                }
            }
        }

        // 위 모든 사각형 중 합이 5인 경우가 하나도 없으면 → 더 이상 지울 수 없음
        return false;
    }

}
