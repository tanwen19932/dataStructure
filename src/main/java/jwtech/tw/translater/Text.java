package jwtech.tw.translater;

/**
 * @author TW
 * @date Administrator on 2016/11/18.
 */
public abstract class Text {
    protected String languageCode;
    protected String text;

    public Text(String languageCode, String text) {
        this.languageCode = languageCode;
        this.text = text;
    }


    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSegText() {
        return segText;
    }

    public void setSegText(String segText) {
        this.segText = segText;
    }

    protected String segText;

    public abstract void genSeg();
}
