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
package im.bci.newtonadv;

import im.bci.newtonadv.platform.lwjgl.GameView;
import java.io.File;
import java.io.FileInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import im.bci.newtonadv.game.FrameTimeInfos;
import java.io.IOException;
import java.util.Properties;
import im.bci.newtonadv.game.MainMenuSequence;
import im.bci.newtonadv.game.QuestMenuSequence;
import im.bci.newtonadv.game.Sequence;
import im.bci.newtonadv.game.Sequence.TransitionException;
import im.bci.newtonadv.game.StoryboardSequence;
import im.bci.newtonadv.platform.lwjgl.GameInput;
import im.bci.newtonadv.score.GameScore;

/**
 *
 * @author devnewton
 */
public strictfp class Game {

    private final GameView view;
    private final GameInput input;
    private boolean running = true;
    static public final int FPS = 60;
    static public final float FPSf = (float) FPS;
    static public final int DEFAULT_SCREEN_WIDTH = 1280;
    static public final int DEFAULT_SCREEN_HEIGHT = 800;
    private FrameTimeInfos frameTimeInfos = new FrameTimeInfos();
    private Properties config = new Properties();
    private SoundCache soundCache = null;
    private MainMenuSequence mainMenuSequence;
    private GameScore score = new GameScore();

    public Properties getConfig() {
        return config;
    }

    public FrameTimeInfos getFrameTimeInfos() {
        return frameTimeInfos;
    }

    public GameView getView() {
        return view;
    }

    public SoundCache getSoundCache() {
        return soundCache;
    }

    Game() throws Exception {
        try {
            config.setProperty("view.width", Integer.toString(DEFAULT_SCREEN_WIDTH));
            config.setProperty("view.height", Integer.toString(DEFAULT_SCREEN_HEIGHT));
            config.setProperty("view.quality", "NICEST");
            config.setProperty("sound.enabled", "true");

            FileInputStream f = new FileInputStream("data/config.properties");
            try {
                config.load(f);
            } finally {
                f.close();
            }
        } catch (IOException ex) {
            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
        }

        this.soundCache = new SoundCache(config.getProperty("sound.enabled").equals("true"));
        this.view = new GameView(this);
        this.input = new GameInput(config);
    }

    void stopGame() {
        running = false;
        getView().getTextureCache().clearAll();
        getSoundCache().clearAll();
    }

    Sequence setupSequences() {
        Sequence outroSequence = new StoryboardSequence(this, "data" + File.separator + "outro.jpg", "data" + File.separator + "The_End.ogg", null);
        QuestMenuSequence questMenuSequence = new QuestMenuSequence(this);
        mainMenuSequence = new MainMenuSequence(this, questMenuSequence, outroSequence);
        questMenuSequence.setNextSequence(mainMenuSequence);
        return mainMenuSequence;
    }

    public void start() throws IOException {

        Sequence currentSequence = setupSequences();
        currentSequence.start();
        while (running) {
            try {
                if (bShowMainMenu) {
                    bShowMainMenu = false;
                    if (currentSequence != mainMenuSequence) {
                        mainMenuSequence.setResumeSequence(currentSequence);
                        currentSequence = mainMenuSequence;
                        mainMenuSequence.start();
                    }
                }
                frameTimeInfos.update();
                view.draw(currentSequence);
                processInputs(currentSequence);
                if (!frameTimeInfos.paused) {
                    currentSequence.processInputs();
                    currentSequence.update();
                }
            } catch (TransitionException ex) {
                if (mainMenuSequence.isResumeSequence(ex.getNextSequence())) {
                    mainMenuSequence.stop();
                    currentSequence = ex.getNextSequence();
                } else {
                    currentSequence.stop();
                    currentSequence = ex.getNextSequence();
                    System.gc();
                    getView().getTextureCache().clearUseless();
                    getSoundCache().clearUseless();
                    if (currentSequence == null) {
                        stopGame();
                    } else {
                        currentSequence.start();
                    }
                }
            }
        }
    }
    private boolean bToggleFullscreen = false;
    private boolean bTogglePause = false;
    private boolean bShowMainMenu = false;

    private void processInputs(Sequence currentSequence) throws TransitionException {
        if (input.isKeyReturnToMenuDown()) {
            bShowMainMenu = true;
        }
        if (input.isKeyToggleFullscreenDown()) {
            bToggleFullscreen = true;
        } else if (bToggleFullscreen) {
            bToggleFullscreen = false;
            view.toggleFullscreen();
        }
        if (input.isKeyPauseDown()) {
            bTogglePause = true;
        } else if (bTogglePause) {
            bTogglePause = false;
            frameTimeInfos.togglePause();
        }
    }

    public GameScore getScore() {
        return score;
    }

    final public GameInput getInput() {
        return input;
    }
}
