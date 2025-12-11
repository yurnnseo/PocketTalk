// 대화상대 선택
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class ChoosePerson extends JFrame {

    private JPanel contentPane;
    private FriendsListPanel parentPanel;
    private String username;
    private JButton okbutton;
    private JLabel choiceLabel;
    
    private final ClientMenuFrame parentFrame;

    public ChoosePerson(ClientMenuFrame parentFrame, String username, List<String> users) {
        this.parentFrame = parentFrame;
        this.username = username; // 본인 이름

        setLayout(null);
        setBackground(Color.decode("#F9F9F9"));

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 260, 370); // 크기
        setResizable(false);

        // 배경 이미지 그리는 패널
        contentPane = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(Color.decode("#E3D6F0"));
                g.fillRect(0, 0, getWidth(), 40);
            }
        };

        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(null);
        setContentPane(contentPane);

        // 친구 목록 화면 출력 (선택 모드)
        parentPanel = new FriendsListPanel(username, true); // 본인은 제외
        parentPanel.updateList(users);

        JScrollPane scrollPane = new JScrollPane(parentPanel);
        scrollPane.setBounds(10, 42, 238, 240);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder()); 
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        contentPane.add(scrollPane);

        choiceLabel = new JLabel("대화상대 선택", SwingConstants.CENTER);
        choiceLabel.setFont(FontSource.get(12f));
        choiceLabel.setForeground(Color.BLACK);
        choiceLabel.setBounds(85, 5, 80, 30);
        contentPane.add(choiceLabel);

        okbutton = UIComponentZip.createTextButton("선택 완료", 165, 290, 56, 28, FontSource.get(10f));
        contentPane.add(okbutton);
        
        okbutton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Set<String> selected = parentPanel.getSelectedUsers();
                if (selected.isEmpty()) {
                    JOptionPane.showMessageDialog(
                            ChoosePerson.this,
                            "대화 상대를 한 명 이상 선택해주세요.",
                            "선택 오류",
                            JOptionPane.WARNING_MESSAGE
                    );
                    return;
                }

                List<String> membersList = new ArrayList<>();
                membersList.add(username); // creator

                for (String s : selected) {
                    if (!s.equals(username)) {
                        membersList.add(s);
                    }
                }

                String members = String.join(" ", membersList);

                // 서버에게 방 생성 요청
                parentFrame.sendToServer("/createroom " + members);

                dispose();
            }
        });

    }
}
