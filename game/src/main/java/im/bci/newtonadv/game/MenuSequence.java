/*
 * Copyright (c) 2009-2010 devnewton <devnewton@bci.im>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'devnewton <devnewton@bci.im>' nor the names of
 *   its contributors may be used to endorse or promote products derived
 *   from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package im.bci.newtonadv.game;

import java.util.ArrayList;
import im.bci.newtonadv.Game;
import im.bci.newtonadv.platform.interfaces.ITexture;
import im.bci.newtonadv.platform.interfaces.ITextureCache;

import java.util.List;

import net.phys2d.math.ROVector2f;
import net.phys2d.math.Vector2f;

/**
 * 
 * @author devnewton
 */
public abstract class MenuSequence implements Sequence {

	public static final float ortho2DBottom = Game.DEFAULT_SCREEN_HEIGHT;
	public static final float ortho2DLeft = 0;
	public static final float ortho2DRight = Game.DEFAULT_SCREEN_WIDTH;
	public static final float ortho2DTop = 0;
	private ArrayList<Button> buttons = new ArrayList<Button>();
	private int currentButtonIndex;
	private boolean redraw;
	private boolean horizontalSelectNextButton = false,
			horizontalSelectPreviousButton = false;
	private boolean verticalSelectNextButton = false,
			verticalSelectPreviousButton = false;
	private boolean activateCurrentButton = false;
	protected int horizontalIncrement = 1;
	protected int verticalIncrement = 1;
	protected Game game;
	private String backgroundTexturePath;
	private ITexture backgroundTexture;
	private Vector2f oldMousePos = new Vector2f();
	private boolean mouseActivateCurrentButton;
        private Button defaultButton;

	public MenuSequence(Game game) {
		this.game = game;
	}

        protected void setDefaultButton(Button b) {
            this.defaultButton = b;
        }

	public void setBackgroundTexturePath(String backgroundImage) {
		this.backgroundTexturePath = backgroundImage;
	}

	public List<Button> getButtons() {
		return buttons;
	}

	protected void clearButtons() {
		buttons.clear();
	}

	@Override
	public void draw() {
		game.getView().drawMenuSequence(this);
	}

	@Override
	public void processInputs() throws Sequence.NormalTransitionException,
			ResumeTransitionException, 
			ResumableTransitionException {
		if (game.getInput().isKeyRightDown()) {
			horizontalSelectNextButton = true;
		} else if (horizontalSelectNextButton) {
			horizontalSelectNextButton = false;
			buttons.get(currentButtonIndex).setOff();
			currentButtonIndex += horizontalIncrement;
			if (currentButtonIndex >= buttons.size()) {
				currentButtonIndex = 0;
			}
			buttons.get(currentButtonIndex).setOn();
			redraw = true;
		}
		if (game.getInput().isKeyLeftDown()) {
			horizontalSelectPreviousButton = true;
		} else if (horizontalSelectPreviousButton) {
			horizontalSelectPreviousButton = false;
			buttons.get(currentButtonIndex).setOff();
			currentButtonIndex -= horizontalIncrement;
			if (currentButtonIndex < 0) {
				currentButtonIndex = buttons.size() - 1;
			}
			buttons.get(currentButtonIndex).setOn();
			redraw = true;
		}
		if (game.getInput().isKeyDownDown()) {
			verticalSelectNextButton = true;
		} else if (verticalSelectNextButton) {
			verticalSelectNextButton = false;
			buttons.get(currentButtonIndex).setOff();
			currentButtonIndex += verticalIncrement;
			if (currentButtonIndex >= buttons.size()) {
				currentButtonIndex = 0;
			}
			buttons.get(currentButtonIndex).setOn();
			redraw = true;
		}
		if (game.getInput().isKeyUpDown()) {
			verticalSelectPreviousButton = true;
		} else if (verticalSelectPreviousButton) {
			verticalSelectPreviousButton = false;
			buttons.get(currentButtonIndex).setOff();
			currentButtonIndex -= verticalIncrement;
			if (currentButtonIndex < 0) {
				currentButtonIndex = buttons.size() - 1;
			}
			buttons.get(currentButtonIndex).setOn();
			redraw = true;
		}
		if (game.getInput().isKeyReturnDown()) {
			activateCurrentButton = true;
		} else if (activateCurrentButton) {
			activateCurrentButton = false;
			buttons.get(currentButtonIndex).activate();
		}

		ROVector2f mousePos = game.getInput().getMousePos();
		if (null != mousePos) {
			float viewWidth = game.getView().getWidth();
			float viewHeight = game.getView().getHeight();
			if (viewWidth != 0.0f && viewHeight != 0.0f) {
				float mouseX = mousePos.getX() * ortho2DRight / viewWidth;
				float mouseY = ortho2DBottom
						- (mousePos.getY() * ortho2DBottom / viewHeight);
				for (Button button : buttons) {
					if (mouseX > button.x && mouseX < (button.x + button.w)
							&& mouseY > button.y
							&& mouseY < (button.y + button.h)) {
						if ((oldMousePos.getX() != mousePos.getX() || oldMousePos
								.getY() != mousePos.getY())
								|| game.getInput().isMouseButtonDown()) {
							buttons.get(currentButtonIndex).setOff();
							currentButtonIndex = buttons.indexOf(button);
							button.setOn();
						}
						if (game.getInput().isMouseButtonDown()) {
							mouseActivateCurrentButton = true;
						} else if (mouseActivateCurrentButton) {
							mouseActivateCurrentButton = false;
							button.activate();
						}
						break;
					}
				}
			}
			oldMousePos.set(mousePos);
		}
	}

	@Override
	public void start() {
		final ITextureCache textureCache = game.getView().getTextureCache();
		if (null != backgroundTexturePath) {
			backgroundTexture = textureCache.getTexture(backgroundTexturePath);
		}
                currentButtonIndex = 0;
		activateCurrentButton = false;
		mouseActivateCurrentButton = false;
		redraw = true;
                if(buttons.contains(defaultButton)) {
                    setCurrentButton(defaultButton);
                } else {
                    setCurrentButton(buttons.isEmpty() ? null : buttons.get(0));
                }
		for (Button button : buttons) {
			button.start();
			button.onTexture = textureCache.getTexture(button.onTextureName);
			button.offTexture = textureCache.getTexture(button.offTextureName);
		}
	}

	@Override
	public void stop() {
		// ensure that textures can be deleted
		backgroundTexture = null;
		for (Button button : buttons) {
			button.onTexture = null;
			button.offTexture = null;
		}
	}

	@Override
	public void update() {
		// NOTHING
	}

	protected void addButton(Button b) {
		buttons.add(b);
	}

	protected void setCurrentButton(Button button) {
		for (int i = 0; i < buttons.size(); ++i) {
			Button b = buttons.get(i);
			if (b == button) {
				currentButtonIndex = i;
				b.setOn();
			} else {
				b.setOff();
			}

		}
		redraw = true;
	}

	public boolean isDirty() {
		return redraw;
	}

	public void setDirty(boolean b) {
		redraw = b;
	}

	public ITexture getBackgroundImage() {
		return backgroundTexture;
	}

	public abstract class Button {

		public String onTextureName;
		public String offTextureName;
		public float x = 273, y;
		public float w = -1.0f;
		public float h = -1.0f;

		private ITexture onTexture, offTexture;
		private boolean on;

		public ITexture getTexture() {
			return on ? onTexture : offTexture;
		}

		public void draw() {
			game.getView().drawButton(this);
		}

		void setOn() {
			on = true;
		}

		void setOff() {
			on = false;
		}

		abstract void activate() throws Sequence.NormalTransitionException,
				Sequence.ResumeTransitionException, Sequence.ResumableTransitionException;
		
		void start() {
		}
	}
}
