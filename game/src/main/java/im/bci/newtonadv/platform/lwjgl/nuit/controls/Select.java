package im.bci.newtonadv.platform.lwjgl.nuit.controls;

import java.util.List;

import org.lwjgl.opengl.GL11;

import im.bci.newtonadv.platform.lwjgl.TrueTypeFont;
import im.bci.newtonadv.platform.lwjgl.nuit.NuitToolkit;
import im.bci.newtonadv.platform.lwjgl.nuit.widgets.Widget;

public class Select extends Widget {
    private NuitToolkit toolkit;
    private List<?> possibleValues;
    private int selected;
    private int oldSelected;
    private boolean suckFocus = true;

    public Select(NuitToolkit toolkit, List<?> possibleValues) {
        this.toolkit = toolkit;
        this.possibleValues = possibleValues;
    }

    @Override
    public boolean isInputWhore() {
        return true;
    }

    @Override
    public void suckFocus() {
        suckFocus = true;
        oldSelected = selected;
    }

    @Override
    public boolean isSuckingFocus() {
        return suckFocus;
    }

    public Object getSelected() {
        return selected;
    }

    public void setSelected(Object value) {
        this.selected = possibleValues.indexOf(value);
    }

    @Override
    public void onLeft() {
        --selected;
        if (selected < 0) {
            selected = possibleValues.size() - 1;
        }
    }

    @Override
    public void onRight() {
        ++selected;
        if (selected >= possibleValues.size()) {
            selected = 0;
        }
    }
    
    @Override
    public void onOK() {
        suckFocus = false;
    }
    
    @Override
    public void onCancel() {
        selected = oldSelected;
        suckFocus = false;
    }

    @Override
    public void draw() {
        GL11.glPushAttrib(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_ENABLE_BIT);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glPushMatrix();
        TrueTypeFont font = toolkit.getFont();
        String text = possibleValues.get(selected).toString();
        GL11.glTranslatef(getX() + getWidth() / 2.0f - font.getWidth(text) / 4.0f, getY() + getHeight() / 2.0f + font.getHeight(text) / 2.0f, 0.0f);
        GL11.glScalef(1, -1, 1);
        font.drawString(text);
        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }
}
