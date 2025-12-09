import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionAdapter;

// 포도 9x8 배치

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
    private boolean isLeftBoard;

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
        // 인덱스가 잘못된 경우 방어적으로 처리 (예: 첫 번째 아이콘 반환)
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

    /**
     * values[startIndex]부터 9x8 개수를 채운 뒤, 마지막 인덱스를 반환
     */
    public int fillFromValues(int[] values, int startIndex,
                              MouseAdapter clickListener,
                              MouseMotionAdapter motionListener) {

        clearBoard();

        int index = startIndex;

        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                if (index >= values.length) {
                    // 값이 부족하면 그냥 종료
                    return index;
                }
                int grapeValue = values[index++];
                int iconIndex = grapeValue - 1;
                if (iconIndex < 0 || iconIndex >= icons.length) {
                    iconIndex = 0; // 방어코드
                }

                ImageIcon icon = icons[iconIndex];

                JLabel grapeLabel = new JLabel(icon);
                grapeLabel.setBounds(
                        startX + c * (width + gapX),
                        startY + r * (height + gapY),
                        width,
                        height
                );

                grapeLabel.putClientProperty("value", grapeValue);
                grapeLabel.putClientProperty("row", r);
                grapeLabel.putClientProperty("col", c);
                grapeLabel.putClientProperty("isLeft", isLeftBoard);

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
