package im.bci.newtonadv.platform.interfaces;

import im.bci.tmxloader.TmxMap;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface IGameData {

    List<String> listQuests();

    String getFile(String file);

    String getQuestFile(String questName, String file);

    List<String> listQuestLevels(String questName);

    TmxMap openLevelTmx(String questName, String levelName) throws Exception;

    String getLevelFilePath(String questName, String levelName, String filename);

    InputStream openFile(String path) throws IOException;

    boolean fileExists(String path);

    List<String> listQuestsToCompleteToUnlockQuest(String questName);
}
