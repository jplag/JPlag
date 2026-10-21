package de.jplag.pdf.pdfComponents;

import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.layout.element.Link;
import com.itextpdf.layout.element.Paragraph;
import de.jplag.pdf.utils.Colors;
import de.jplag.pdf.utils.JPlagLinkType;

import java.util.function.Consumer;

public class Links {
    public static Paragraph createTextLink(String text, JPlagLinkType type, Object linkIdentifier, Consumer<Paragraph> styler) {
        Link link = new Link(text, PdfAction.createGoTo(type.resolve(linkIdentifier)));
        Paragraph p = new Paragraph();
        p.setFontColor(Colors.BLUE).setUnderline();
        styler.accept(p);
        p.add(link);
        return p;
    }
}
