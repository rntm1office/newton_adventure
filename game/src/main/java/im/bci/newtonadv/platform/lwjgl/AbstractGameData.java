package im.bci.newtonadv.platform.lwjgl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import im.bci.newtonadv.platform.interfaces.IGameData;

abstract class AbstractGameData implements IGameData {

	protected final String dataDir;

	public AbstractGameData(String dataDir) {
		this.dataDir = dataDir;
	}

	@Override
	public String getFile(String name) {
		return dataDir + name;
	}

	@Override
	public String getQuestFile(String questName, String file) {
		return dataDir + "quests/" + questName + "/" + file;
	}

	@Override
	public InputStream openLevelTmx(String questName, String levelName)
			throws Exception {
				String path = dataDir + "quests/" + questName + "/levels/" + levelName
						+ "/" + levelName + ".tmx";
				return openFile(path);
			}
	@Override
	public InputStream openLevelNal(String questName, String levelName) throws Exception {
		String path = dataDir + "quests/" + questName + "/levels/" + levelName
				+ "/" + levelName + ".nal";
		return openFile(path);
	}


	@Override
	public String getLevelFilePath(String questName, String levelName, String filename) {
		String path = dataDir + "quests/" + questName + "/levels/" + levelName
				+ "/" + filename;
		try {
			InputStream s = openFile(path);
			if (s != null) {
				s.close();
				return path;
			}
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}
		return dataDir + "default_level_data/" + filename;
	}

	@Override
	public boolean fileExists(String path) {
		try {
			InputStream s = openFile(path);
			if (s != null) {
				s.close();
				return true;
			}
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}
		return false;
	}

}