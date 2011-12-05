package im.bci.newtonadv.game;

import org.lwjgl.input.Keyboard;
import im.bci.newtonadv.Game;
import im.bci.newtonadv.game.Sequence.TransitionException;

public class GameOverSequence extends StoryboardSequence {

    private LevelSequence level;

    GameOverSequence(Game game, LevelSequence level, Sequence nextSequence) {
        super(game, "data/gameover.jpg", "data/Game_Over.mid", nextSequence);
        this.level = level;
    }

    @Override
    public void processInputs() throws TransitionException {
        if (Keyboard.isKeyDown(Keyboard.KEY_R)) {
            throw new Sequence.TransitionException(level);
        }
        super.processInputs();
    }
}