package de.jplag.cli.antlrtesttool;

import com.google.common.reflect.ClassPath;
import de.jplag.antlr.AbstractAntlrParserAdapter;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * The main menu for the language tester
 */
public class MainMenu {
    /**
     * New instance. Automatically runs the tool and waits for the window to exit
     * @throws IOException -
     * @throws RuntimeException -
     */
    public MainMenu() throws IOException {
        JFrame frame = new JFrame();

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(100, 1));

        panel.add(new JLabel("Choose your adapter"));
        panel.add(new JLabel("  "));

        ClassPath path = ClassPath.from(AntlrLanguageTester.class.getClassLoader());
        path.getTopLevelClasses().stream().filter(it -> {
            try {
                return it.getName().startsWith("de.jplag") && AbstractAntlrParserAdapter.class.isAssignableFrom(it.load());
            } catch (Throwable e) {
                return false;
            }
        }).forEach(it -> {
                    JButton button = new JButton(it.getSimpleName());
                    button.addActionListener((event) -> {
                        try {
                            AbstractAntlrParserAdapter<?> instance = (AbstractAntlrParserAdapter<?>) it.load().getConstructor().newInstance();
                            frame.setContentPane(new AntlrLanguageTester(instance));
                            frame.revalidate();
                        } catch (NoSuchMethodException | InstantiationException |
                                 IllegalAccessException | InvocationTargetException e) {
                            throw new RuntimeException(e);
                        }
                    });
                    panel.add(button);
                }
        );

        JPanel wrapper = new JPanel();
        wrapper.setLayout(new FlowLayout(FlowLayout.CENTER));

        wrapper.add(panel);

        frame.setContentPane(wrapper);

        frame.pack();
        frame.setVisible(true);
        synchronized (frame) {
            while (frame.isShowing()) {
                try {
                    frame.wait(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
