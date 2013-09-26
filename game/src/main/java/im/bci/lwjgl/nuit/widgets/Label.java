package im.bci.lwjgl.nuit.widgets;

import org.lwjgl.opengl.GL11;

import im.bci.lwjgl.nuit.NuitToolkit;
import im.bci.lwjgl.nuit.utils.TrueTypeFont;

public class Label extends Widget {

    private String text;
    private NuitToolkit toolkit;

    public Label(NuitToolkit toolkit, String text) {
        this.toolkit = toolkit;
        this.text = text;
    }

    @Override
    public boolean isFocusable() {
    	return false;
    }
    
    @Override
    public void draw() {
        GL11.glPushAttrib(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_ENABLE_BIT);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glPushMatrix();
        TrueTypeFont font = toolkit.getFont();
        GL11.glTranslatef(getX() + getWidth()/2.0f - font.getWidth(text)/4.0f, getY() + getHeight()/2.0f + font.getHeight(text) / 2.0f, 0.0f);
        GL11.glScalef(1, -1, 1);
        font.drawString(text);
        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }

}