//채팅창
import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class ChattingPanel extends JPanel {
    private JTextField txtInput;
    private String UserName;
    private JButton btnSend;
    private JLabel lblUserName;

    private String groupMembers; // 그룹채팅 멤버 문자열
    private String creatorName;
    private FontSource fontSource = new FontSource("/IM_Hyemin-Bold.ttf");
    private MessageContainerPanel messageContainer;
    private JScrollPane scrollPane;
    private final ClientMenuFrame parentFrame;

    public ChattingPanel(ClientMenuFrame parentFrame,
                         String username,
                         String groupMembers,
                         String creatorName) {

        this.parentFrame = parentFrame;
        this.groupMembers = groupMembers;
        this.creatorName = creatorName;
        this.UserName = username;

        setBorder(new EmptyBorder(5, 5, 5, 5));
        setLayout(null);
        setBackground(Color.decode("#F9F9F9"));

        messageContainer = new MessageContainerPanel(fontSource.getFont(13f));
        scrollPane = new JScrollPane(messageContainer);
        scrollPane.setBounds(12, 12, 350, 455);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        //scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        add(scrollPane);

        // 메시지 추가될 때마다 스크롤 자동 내림 기능
        scrollPane.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
            public void adjustmentValueChanged(AdjustmentEvent e) {
                if (e.getAdjustable().getMaximum()
                        == e.getValue() + e.getAdjustable().getVisibleAmount()) {
                    e.getAdjustable().setValue(e.getAdjustable().getMaximum());
                }
            }
        });

        txtInput = new JTextField();
        txtInput.setBounds(10, 475, 250, 40);
        add(txtInput);
        txtInput.setColumns(10);

        btnSend = makeButton("전송", 70, 40, 270, 475);
        add(btnSend);

        lblUserName = new JLabel("Name");
        lblUserName.setHorizontalAlignment(SwingConstants.CENTER);
        lblUserName.setBounds(12, 364, 62, 40);
        add(lblUserName);

        lblUserName.setText(UserName + " >");

        // 방장 여부
        boolean isCreator = UserName.equals(creatorName);
        if (isCreator) {
            // 방을 처음 만든 사람만 서버에 /createroom 전송
            parentFrame.sendToServer("/createroom " + this.groupMembers);
        }

        // 초대 시스템 메시지 표시
        showInviteMessage();

        Myaction action = new Myaction();
        btnSend.addActionListener(action);
        txtInput.addActionListener(action);
        txtInput.requestFocus();
    }

    // 초대 시스템 메시지
    private void showInviteMessage() {
        String[] names = groupMembers.split("\\s+");
        StringBuilder invited = new StringBuilder();
        for (String n : names) {
            n = n.trim();
            if (n.isEmpty()) continue;
            if (n.equals(creatorName)) continue;
            if (invited.length() > 0) invited.append(", ");
            invited.append(n);
        }

        String text;
        if (invited.length() == 0) {
            text = creatorName + "님이 채팅방을 생성했습니다.";
        } else {
            text = creatorName + "님이 " + invited + "님을 초대했습니다.";
        }

        // 시스템 메시지는 상대 메시지처럼 왼쪽에 표시 (isSent=false)
        messageContainer.addMessage(text, false);
    }

    // 서버에서 온 일반 채팅 메시지 하나 처리
    public void onReceiveChatMessage(String msg) {
        if (msg == null) return;
        msg = msg.trim();

        // 혹시 제어 메시지가 흘러들어와도 방어
        if (msg.startsWith("/")) return;

        boolean isMine = false;
        String content = msg;
        String sender = null;

        int idx = msg.indexOf(" : ");
        if (idx > 0) {
            sender = msg.substring(0, idx).trim();
            content = msg.substring(idx + 3).trim();
            isMine = sender.equals(UserName);
        }

     // 말풍선에 찍을 텍스트 구성
        String bubbleText;
        if (sender != null) {
            bubbleText = sender + " : " + content; // "이름 : 메시지"
        } 
        else {
            bubbleText = content; // 혹시 포맷이 다를 경우 대비
        }

        // 말풍선 추가 (내 메시지면 보라색, 상대는 흰색)
        messageContainer.addMessage(bubbleText, isMine);
        
    }

    // 메시지 보내기 액션
    class Myaction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == btnSend || e.getSource() == txtInput) {
                String msg = txtInput.getText();
                if (msg == null || msg.trim().isEmpty()) return;

                // 이제는 부모 프레임을 통해 서버로 전송
                parentFrame.sendToServer(msg + "\n");

                txtInput.setText("");
                txtInput.requestFocus();

                if (msg.contains("/exit")) {
                    // 여기서는 그냥 메시지 전송만 하고,
                    // 실제 창 닫기는 추후 규칙 정해서 처리해도 됨
                }
            }
        }
    }

    private JButton makeButton(String text, int width, int height, int x, int y) {
        JButton btn = new JButton(text);
        btn.setBounds(x, y, width, height);
        btn.setBackground(Color.WHITE);
        btn.setBorder(new LineBorder(Color.BLACK));
        btn.setFont(fontSource.getFont(12f));

        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        Color hoverColor = Color.decode("#E3D6F0"); // 연보라
        Color normalColor = Color.WHITE;

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(hoverColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(normalColor);
            }
        });

        return btn;
    }
}
