// 미니게임 타이머 시간 계산
import javax.swing.*;
import java.awt.*;

public class MiniGrapeTimerPanel extends JPanel {

    private int timeRemaining;      // 남은 시간
    private final int totalTime;    // 전체 시간
    private JProgressBar progressBar;
    private Timer timer;
    private Runnable onTimeEnd;

    public MiniGrapeTimerPanel(int totalSeconds, Runnable onTimeEnd) {
        this.totalTime = totalSeconds;
        this.timeRemaining = totalSeconds;
        this.onTimeEnd = onTimeEnd;

        setLayout(new BorderLayout());
        setOpaque(false);

        progressBar = new JProgressBar(0, totalTime);
        progressBar.setValue(totalTime);
        progressBar.setForeground(Color.decode("#e3d6f0"));
        progressBar.setBackground(Color.decode("#d6d6d6"));
        progressBar.setBorder(null);
        progressBar.setStringPainted(true);
        progressBar.setFont(FontSource.get(11f));

        updateProgressBarText();
        add(progressBar, BorderLayout.CENTER);

        startTimer();   // 생성되면 바로 1초씩 줄어듦
    }

    private void startTimer() {
        timer = new Timer(1000, e -> {
            if (timeRemaining > 0) {
                timeRemaining--;
                progressBar.setValue(timeRemaining);
                updateProgressBarText();
            } else {
                timer.stop();
                progressBar.setValue(0);
                progressBar.setString("시간 종료!");

                if (onTimeEnd != null) {
                    onTimeEnd.run();   // 시간이 끝났을 때 외부에서 전달한 작업 실행
                }
            }
        });
        timer.start();
    }

    // 필요하면 중간에 멈출 때 사용
    public void stopTimer() {
        if (timer != null) {
            timer.stop();
        }
    }

    private void updateProgressBarText() {
        int minutes = timeRemaining / 60;
        int seconds = timeRemaining % 60;
        progressBar.setString(String.format("남은 시간: %d:%02d", minutes, seconds));
    }
}
