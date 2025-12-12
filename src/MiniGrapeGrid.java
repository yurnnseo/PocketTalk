// 포도 9x8 배치
import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionAdapter;

public class MiniGrapeGrid {

    public static final int ROWS = 9;
    public static final int COLS = 8;

    private JLabel[][] board;
    private JPanel parent;

    private int startX;
    private int startY;
    private int width;
    private int height;
    private int gapX;
    private int gapY;

    private ImageIcon[] icons;
    private boolean isLeftBoard; // 보드 위치 구분

    public MiniGrapeGrid(
            JPanel parent,
            int startX, int startY,
            int width, int height,
            int gapX, int gapY,
            ImageIcon[] icons,
            boolean isLeftBoard
    ) {
        this.parent = parent;
        this.startX = startX;
        this.startY = startY;
        this.width = width;
        this.height = height;
        this.gapX = gapX;
        this.gapY = gapY;
        this.icons = icons;
        this.isLeftBoard = isLeftBoard;

        board = new JLabel[ROWS][COLS];
    }

    public JLabel[][] getBoard() {
        return board;
    }

    // MiniGrapeGameController에서 사용할 아이콘 반환 메서드
    public ImageIcon getIcon(int index) {
        if (index >= 0 && index < icons.length) {
            return icons[index];
        }
        // 인덱스가 잘못된 경우 기본 아이콘 반환
        return icons[0]; 
    }
    
    // 기존 포도 제거
    public void clearBoard() {
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                if (board[r][c] != null) {
                    parent.remove(board[r][c]);
                    board[r][c] = null;
                }
            }
        }
    }

    // valuse 배열의 값 읽어서 그리드 그리기
    public int fillFromValues(int[] values, int startIndex,
                              MouseAdapter clickListener,
                              MouseMotionAdapter motionListener) {

        clearBoard();

        int index = startIndex;

        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                if (index >= values.length) { // 길이 초과시 종료             
                    return index;
                }
                int grapeValue = values[index++];
                int iconIndex = grapeValue - 1;
                if (iconIndex < 0 || iconIndex >= icons.length) {
                    iconIndex = 0;
                }

                ImageIcon icon = icons[iconIndex];

                JLabel grapeLabel = new JLabel(icon);
                grapeLabel.setBounds(
                        startX + c * (width + gapX),
                        startY + r * (height + gapY),
                        width,
                        height
                );

                // 게임 정보 저장
                grapeLabel.putClientProperty("value", grapeValue); // 포도 숫자
                grapeLabel.putClientProperty("row", r);
                grapeLabel.putClientProperty("col", c);
                grapeLabel.putClientProperty("isLeft", isLeftBoard);

                // 마우스 이벤트
                if (clickListener != null) {
                    grapeLabel.addMouseListener(clickListener);
                }
                if (motionListener != null) {
                    grapeLabel.addMouseMotionListener(motionListener);
                }

                board[r][c] = grapeLabel;
                parent.add(grapeLabel);
            }
        }

        parent.revalidate();
        parent.repaint();

        return index;
    }
}
