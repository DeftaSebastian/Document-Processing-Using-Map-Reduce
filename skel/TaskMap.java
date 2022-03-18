import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.RecursiveTask;
import java.util.stream.Stream;

public class TaskMap implements Callable {
	String fileName;
	long startOffset;
	int fragmentSize;

	public TaskMap(String file, long offSet, int fragmentSize) {
		this.fileName = file;
		this.startOffset = offSet;
		this.fragmentSize = fragmentSize;
	}

	@Override
	public MapClass call() throws Exception {
		HashMap<Integer, Integer> dictionary = new HashMap<>();
		ArrayList<String> biggestWords = new ArrayList<>();
		ArrayList<String> addedWords = new ArrayList<>();
		String delimiters = "[^a-zA-Z0-9]";
		try {
			Reader reader = new InputStreamReader(new FileInputStream(this.fileName), Charset.defaultCharset());
			Reader buffer = new BufferedReader(reader);
			char[] sectionChar = new char[fragmentSize + 100];
			String separators = ";:/?~\\.,><`[]{}()!@#$%^&-_+'=*\"| \t\r\n";
			if (startOffset == 0)    //if the offset is zero, we don't have to verify the need to skip the first word
			{
				String section;
				int biggestWordSize = 0;
				int wordCounter = 0;
				buffer.read(sectionChar, 0, this.fragmentSize);

				int sectionOffset = 0;
				sectionOffset += this.fragmentSize;
				if (!separators.contains(String.valueOf(sectionChar[sectionOffset - 1]))) { //if our section has an unfinished word at the end
					while (buffer.read(sectionChar, sectionOffset, 1) != -1			//we add the rest of it
							&& !separators.contains(String.valueOf(sectionChar[sectionOffset]))) {
						sectionOffset++;
					}
				}
				section = String.valueOf(sectionChar);
				Stream<String> filteredSection = Arrays.stream(section.split(delimiters)).filter(sec -> sec.length() > 0);
				Object[] words = filteredSection.toArray();

				while (wordCounter < words.length) {    //with this while we don't need to verify
					String word = (String) words[wordCounter];    //the need to take or not to take the last word
					/////////////////////////////////////////////////////
					if (word.length() > biggestWordSize)    //if we find a new BiggestWord
					{
						biggestWords.clear();    //we delete all of our previous biggest words found of the same length
						biggestWords.add(word);    //we add this new word in our biggest words list
						biggestWordSize = word.length();    //we modify the biggest word size
						dictionary.put(word.length(), 1);    //we add this new word to our dictionary
					} else if (word.length() == biggestWordSize) {    //if our new word is the same size as the other biggest words
						//and put him in the dictionary
						if (!biggestWords.contains(word)) {
							biggestWords.add((word)); //else we add this word to the list of the biggest words
						}
						dictionary.replace(word.length(), dictionary.get(word.length()) + 1);    //we increase the number of apparitions of this word
					} else {
						if (dictionary.containsKey(word.length()))    //if this word is smaller than what we found in the past
						{                                            //we check if we at lest found it before
							dictionary.replace(word.length(), dictionary.get(word.length()) + 1);    //if yes, we increase its number of apparitions
							addedWords.add(word);
						} else {
							dictionary.put(word.length(), 1);    //if not, we add it in our dictionary
						}
					}
					///////////////////////////////////////////////////
					wordCounter++;
				}
			} else {
				int wordCounter = 0;
				int sectionOffset = 1;
				String section;
				buffer.skip(startOffset - 1);
				buffer.read(sectionChar, 0, this.fragmentSize + sectionOffset);
				if (!separators.contains(String.valueOf(sectionChar[0]))) {	//if the previous char was a letter we skip the word
					wordCounter++;
				}
				sectionOffset += this.fragmentSize;
				if (!separators.contains(String.valueOf(sectionChar[sectionOffset - 1]))) { //if our section has an unfinished word at the end
					while (buffer.read(sectionChar, sectionOffset, 1) != -1			//we add the rest of it
							&& !separators.contains(String.valueOf(sectionChar[sectionOffset]))) {
						sectionOffset++;
					}
				}
				section = String.valueOf(sectionChar);
				Stream<String> filteredSection = Arrays.stream(section.split(delimiters)).filter(sec -> sec.length() > 0);
				Object[] words = filteredSection.toArray();
				int biggestWordSize = 0;
				while (wordCounter < words.length) {	//we go through all the words we found
					String word = (String) words[wordCounter];
					if (word.length() > biggestWordSize)    //if we find a new BiggestWord
					{
						biggestWords.clear();    //we delete all of our previous biggest words found of the same length
						biggestWords.add(word);    //we add this new word in our biggest words list
						biggestWordSize = word.length();    //we modify the biggest word size
						dictionary.put(word.length(), 1);    //we add this new word to our dictionary
					} else if (word.length() == biggestWordSize) {    //if our new word is the same size as the other biggest words
						//and put him in the dictionary
						if (!biggestWords.contains(word)) {
							biggestWords.add((word)); //else we add this word to the list of the biggest words
						}
						dictionary.replace(word.length(), dictionary.get(word.length()) + 1);    //we increase the number of apparitions of this word
					} else {
						if (dictionary.containsKey(word.length()))    //if this word is smaller than what we found in the past
						{                                            //we check if we at lest found it before
							dictionary.replace(word.length(), dictionary.get(word.length()) + 1);    //if yes, we increase its number of apparitions
						} else {
							dictionary.put(word.length(), 1);    //if not, we add it in our dictionary
						}
					}
					///////////////////////////////////////////////////
					wordCounter++;
				}
				//mai trebuie sa creez obiectele MapClass, sa le returnez si sa fac partea de shutdown
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		MapClass mapClass = new MapClass(this.fileName, dictionary, biggestWords);
		return mapClass;
	}
}
