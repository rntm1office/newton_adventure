package im.bci.newtonadv.platform.lwjgl.twl;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

import de.matthiasmann.twl.GUI;
import de.matthiasmann.twl.renderer.lwjgl.LWJGLRenderer;
import de.matthiasmann.twl.theme.ThemeManager;
import im.bci.newtonadv.game.Sequence;
import im.bci.newtonadv.platform.interfaces.IOptionsSequence;
import im.bci.newtonadv.platform.lwjgl.GameView;
import im.bci.newtonadv.platform.lwjgl.GameViewQuality;

public class OptionsSequence implements IOptionsSequence {

	private GUI gui;
	private OptionsGUI optionsGui;
	private Sequence nextSequence;
	private final GameView view;

	public OptionsSequence(GameView view) {
		this.view = view;
	}

	public void start() {
		LWJGLRenderer renderer;
		try {
			renderer = new LWJGLRenderer();
			optionsGui = new OptionsGUI(view);
			gui = new GUI(optionsGui, renderer);
			File themeFile = new File("twl/simple.xml");
			ThemeManager themeManager = ThemeManager
					.createThemeManager(themeFile.toURI().toURL(), renderer);
			gui.applyTheme(themeManager);
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void stop() {
		gui.destroy();
	}

	@Override
	public void draw() {
		GL11.glClearColor(0.5f, 0.5f, 0.5f, 0.0f);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
		gui.update();

	}

	@Override
	public void processInputs() throws TransitionException {
	}

	@Override
	public void update() throws TransitionException {
		if(optionsGui.okPressed) {
			try {
				applyOptions();
			} catch (LWJGLException e) {
				Logger.getLogger(getClass().getName()).log(Level.SEVERE, "cannot apply options", e);
			}
			throw new TransitionException(nextSequence);
		}
		if(optionsGui.cancelPressed) {
			throw new TransitionException(nextSequence);
		}
	}

	private void applyOptions() throws LWJGLException {
			view.setDisplayMode(optionsGui.fullscreen.isActive(), getSelectedQuality(), getSelectedMode());
	}
	
	DisplayMode getSelectedMode() {
		int selectedModeIndex = optionsGui.mode.getSelected();
		if(selectedModeIndex >= 0 && selectedModeIndex<optionsGui.mode.getModel().getNumEntries()) {
			return optionsGui.mode.getModel().getEntry(selectedModeIndex);
		} else {
			return Display.getDisplayMode();
		}
	}
	
	GameViewQuality getSelectedQuality() {
		int selected = optionsGui.quality.getSelected();
		if(selected>=0 && selected<optionsGui.quality.getModel().getNumEntries())
			return optionsGui.quality.getModel().getEntry(selected);
		else
			return view.getQuality();
	}

	@Override
	public void setNextSequence(Sequence mainMenuSequence) {
		this.nextSequence = mainMenuSequence;		
	}

}