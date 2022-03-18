import java.util.ArrayList;
import java.util.HashMap;

public class MapClass {
	String fileName;
	HashMap<Integer, Integer> dictionary;
	ArrayList<String> maxLengthWords;

	public ArrayList<String> getMaxLengthWords() {
		return maxLengthWords;
	}

	public String getFileName() {
		return fileName;
	}

	public HashMap<Integer, Integer> getDictionary() {
		return dictionary;
	}

	public MapClass(String fileName, HashMap<Integer, Integer> dictionary, ArrayList<String> maxLengthWords) {
		this.fileName = fileName;
		this.dictionary = dictionary;
		this.maxLengthWords = maxLengthWords;
	}
}
