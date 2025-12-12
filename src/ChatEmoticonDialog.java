import java.awt.*;
import javax.swing.*;

public class ChatEmoticonDialog extends JDialog {

    private final ClientMenuFrame parentFrame;
    private final String roomId;

    // 이모티콘 PNG 경로 
    private static final String EMOTICON_HAPPY = "/Images/Emoticon_happy.png";
    private static final String EMOTICON_HELLO = "/Images/Emoticon_hello.png";
    private static final String EMOTICON_REST  = "/Images/Emoticon_rest.png";
    private static final String EMOTICON_SAD   = "/Images/Emoticon_sad.png";

    public ChatEmoticonDialog(ClientMenuFrame parentFrame, String roomId) {
    	
        super(parentFrame, "이모티콘", true); 
        this.parentFrame = parentFrame;
        this.roomId = roomId;

        setLayout(new BorderLayout());

        JPanel emojiPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 20));
        emojiPanel.setBackground(Color.WHITE);

        emojiPanel.add(createEmojiButton(EMOTICON_HAPPY, "happy"));
        emojiPanel.add(createEmojiButton(EMOTICON_HELLO, "hello"));
        emojiPanel.add(createEmojiButton(EMOTICON_REST, "rest"));
        emojiPanel.add(createEmojiButton(EMOTICON_SAD, "sad"));

        add(emojiPanel, BorderLayout.CENTER);

        setSize(480, 150);
        setResizable(false);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }

    private JButton createEmojiButton(String imgPath, String code) {
        ImageIcon icon = UIComponentZip.loadScaledIcon(imgPath, 90, 90);
        JButton btn = new JButton(icon);

        btn.setBorder(null);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btn.addActionListener(e -> {
            parentFrame.sendToServer("/msg " + roomId + " /emoji " + code + "\n");
            dispose();
        });

        return btn;
    }
}
