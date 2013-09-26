package im.bci.lwjgl.nuit.widgets;

import java.util.List;

import org.lwjgl.opengl.GL11;

import im.bci.lwjgl.nuit.NuitToolkit;
import im.bci.lwjgl.nuit.utils.TrueTypeFont;

public class Select<T> extends Widget {
    private NuitToolkit toolkit;
    private List<T> possibleValues;
    private int selected;
    private int oldSelected;
    private boolean suckFocus;

    public Select(NuitToolkit toolkit, List<T> possibleValues) {
        this.toolkit = toolkit;
        this.possibleValues = possibleValues;
    }
    
    
    @Override
    public float getMinWidth() {
        float minWidth = 0.0f;
        for(T value : possibleValues) {
            minWidth = Math.max(toolkit.getFont().getWidth(value.toString()), minWidth);
        }
        return minWidth;
    }
    
    @Override
    public float getMinHeight() {
        float minHeight = 0.0f;
        for(T value : possibleValues) {
            minHeight = Math.max(toolkit.getFont().getHeight(value.toString()), minHeight);
        }
        return minHeight;
    }

    @Override
    public boolean isFocusWhore() {
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

    public T getSelected() {
        return possibleValues.get(selected);
    }

    public void setSelected(T value) {
        this.selected = possibleValues.indexOf(value);
        if(this.selected<0) {
        	this.selected = 0;
        }
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
    public void onMouseClick(float mouseX, float mouseY) {
    	onRight();
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