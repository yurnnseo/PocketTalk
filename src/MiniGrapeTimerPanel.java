// 미니게임 타이머 시간 계산, 표시 ui
import javax.swing.*;
import java.awt.*;

public class MiniGrapeTimerPanel extends JPanel {

    private int timeRemaining;      // 남은 시간
    private final int totalTime;    // 전체 시간
    private JProgressBar progressBar; // 화면 상단에 표시할 시간 표시 바
    private Timer timer;
    private Runnable onTimeEnd;

    public MiniGrapeTimerPanel(int totalSeconds, Runnable onTimeEnd) {
        this.totalTime = totalSeconds;
        this.timeRemaining = totalSeconds;
        this.onTimeEnd = onTimeEnd;

        setLayout(new BorderLayout());
        setOpaque(false);

        // 기본 설정
        progressBar = new JProgressBar(0, totalTime);
        progressBar.setValue(totalTime);
        progressBar.setForeground(Color.decode("#e3d6f0"));
        progressBar.setBackground(Color.decode("#d6d6d6"));
        progressBar.setBorder(null);
        progressBar.setStringPainted(true);
        progressBar.setFont(FontSource.get(11f));

        updateProgressBarText();
        add(progressBar, BorderLayout.CENTER);

        startTimer();   // 생성 시 자동 시작
    }

    // 스윙타이머
    private void startTimer() {
        timer = new Timer(1000, e -> { // 1초 간격
            if (timeRemaining > 0) {
                timeRemaining--;
                progressBar.setValue(timeRemaining);
                updateProgressBarText();
            } else { // 종료
                timer.stop(); 
                progressBar.setValue(0);
                progressBar.setString("시간 종료!");

                if (onTimeEnd != null) {
                    onTimeEnd.run();   // 시간이 끝났을 때 외부에서 전달된 콜백
                }
            }
        });
        
        timer.start();
    }

    // 임의 중단 시
    public void stopTimer() {
        if (timer != null) {
            timer.stop();
        }
    }

    // 바에 시간 텍스트 출력
    private void updateProgressBarText() {
        int minutes = timeRemaining / 60;
        int seconds = timeRemaining % 60;
        progressBar.setString(String.format("남은 시간: %d:%02d", minutes, seconds));
    }
}
