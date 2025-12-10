// 포도 제거 및 리필 로직 분리
import java.util.List;

import javax.swing.JLabel;

public class MiniGrapeGameController {

    private MiniGrapeGrid leftGrid;
    private MiniGrapeGrid rightGrid;
    private MiniGrapeSelectionController selectionController;
    private MiniGrapeGameManager gameManager;

    private MiniGamePanel uiPanel; 

    private int myScore = 0;
    private int opponentScore = 0;

    private String userName;
    private String opponentName;

    public MiniGrapeGameController(
            MiniGamePanel uiPanel,
            MiniGrapeGrid leftGrid,
            MiniGrapeGrid rightGrid,
            MiniGrapeSelectionController selectionController,
            MiniGrapeGameManager gameManager,
            String userName,
            String opponentName
    ) {
        this.uiPanel = uiPanel;
        this.leftGrid = leftGrid;
        this.rightGrid = rightGrid;
        this.selectionController = selectionController;
        this.gameManager = gameManager;
        this.userName = userName;
        this.opponentName = opponentName;
    }

    // 선택된 포도 제거 시도
    public void tryRemoveSelected() {
        List<JLabel> selected = selectionController.getSelected();
        int total = gameManager.removeIfSumIsFive(selected);

        if (total == 5 && !selected.isEmpty()) {
            // 점수 증가
            myScore += 10;
            uiPanel.updateScore(myScore, opponentScore);

            // 서버로 전달할 좌표 문자열 생성
            String coordString = makeCoordString(selected);

            uiPanel.sendRemoveToServer(userName, opponentName, coordString);

            // 더 이상 지울 수 있는 조합 없으면 리필 요청
            boolean hasMove = gameManager.hasAnyMoveOnGameBoard();
            if (!hasMove) {
                uiPanel.sendRefillRequest(userName, opponentName);
            }
        }

        selectionController.clearSelection();
        uiPanel.repaint();
    }

    // 서버에서 상대가 지운 정보를 반영
    public void applyRemoteRemove(String owner, String coordString) {
        JLabel[][] board = owner.equals(userName)
                ? rightGrid.getBoard()
                : leftGrid.getBoard();

        String[] tokens = coordString.split(";");
        for (String t : tokens) {
            String[] rc = t.split(",");
            if (rc.length != 2) continue;

            int r = Integer.parseInt(rc[0].trim());
            int c = Integer.parseInt(rc[1].trim());

            JLabel grape = board[r][c];
            if (grape != null) gameManager.removeGrape(grape);
        }

        // 점수 반영
        if (!owner.equals(userName)) {
        	opponentScore += 10;
        }
        
        uiPanel.updateScore(myScore, opponentScore);
        uiPanel.repaint();
    }

    // 서버에서 리필 값 받았을 때 실행
    public void applyRefill(String owner, int[] values) {
        JLabel[][] board = owner.equals(userName)
                ? rightGrid.getBoard()
                : leftGrid.getBoard();

        int idx = 0;
        for (int r = 0; r < MiniGrapeGameManager.ROWS; r++) {
            for (int c = 0; c < MiniGrapeGameManager.COLS; c++) {
                JLabel g = board[r][c];
                if (g == null) continue;
                g.putClientProperty("value", values[idx]);

                int iconIndex = values[idx] - 1;
                MiniGrapeGrid targetGrid = owner.equals(userName) ? rightGrid : leftGrid; 
                g.setIcon(targetGrid.getIcon(iconIndex));
                idx++;
            }
        }
        uiPanel.repaint();
    }

    private String makeCoordString(List<JLabel> selected) {
        StringBuilder sb = new StringBuilder();
        for (JLabel grape : selected) {
            int r = (Integer) grape.getClientProperty("row");
            int c = (Integer) grape.getClientProperty("col");
            if (sb.length() > 0) sb.append(";");
            sb.append(r).append(",").append(c);
        }
        return sb.toString();
    }
}
