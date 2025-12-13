import java.awt.*;
import javax.swing.*;

public class ChatEmoticonDialog extends JDialog {

	private final ClientMenuFrame sender;
    private final String roomId;

    // 이모티콘 PNG 경로 
    private static final String EMOTICON_HAPPY = "/Images/Emoticon_happy.png";
    private static final String EMOTICON_HELLO = "/Images/Emoticon_hello.png";
    private static final String EMOTICON_REST  = "/Images/Emoticon_rest.png";
    private static final String EMOTICON_SAD   = "/Images/Emoticon_sad.png";

    public ChatEmoticonDialog(Window owner, ClientMenuFrame sender, String roomId) {
    	
    	super(owner); 
    	this.sender = sender;
        this.roomId = roomId;
        
        setTitle("이모티콘");
        setModal(false); // 앞으로 뜨는 걸 막음    
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
            sender.sendToServer("/msg " + roomId + " /emoji " + code + "\n");
            dispose();
        });

        return btn;
    }
}
