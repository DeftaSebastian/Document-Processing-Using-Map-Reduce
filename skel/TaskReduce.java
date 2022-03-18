import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

public class TaskReduce implements Callable {
	String fileName;
	HashMap<Integer, Integer> dictionary = new HashMap<>();
	ArrayList<String> biggestWords = new ArrayList<>();
	ArrayList<MapClass> mapClasses;
	int documentNumber;
	float rank;
	ReduceResults results;

	public TaskReduce(String fileName, ArrayList<MapClass> mapClasses) {
		this.fileName = fileName;
		this.mapClasses = mapClasses;
		this.rank = 0;
	}

	public int fibonacci(int n) {
		if (n == 0)
			return 0;
		else if (n == 1)
			return 1;
		else
			return fibonacci(n - 1) + fibonacci(n - 2);
	}

	@Override
	public ReduceResults call() throws Exception {
		//Combining
		String fileName = "";
		ArrayList<String> biggestWords = new ArrayList<>();
		HashMap<Integer, Integer> dictionary = new HashMap<>();
		for (MapClass mapClass : mapClasses) {	//we search in each mapClass for the file we are currently working on
			if (mapClass.fileName.equals(this.fileName)) {
				fileName = mapClass.fileName;	//we remember the name for future use
				for (int j = 0; j < mapClass.maxLengthWords.size(); j++) {
					if (!biggestWords.contains(mapClass.getMaxLengthWords().get(j))) {	//we check if we already have the longest words from a different task
						biggestWords.add(mapClass.getMaxLengthWords().get(j));			//in our combined longest words array
					}
				}
				for (Map.Entry<Integer, Integer> entry : mapClass.dictionary.entrySet()) {
					if (dictionary.containsKey(entry.getKey())) {	//we check if we already added each word from the dictionary in our combined dictionary
						dictionary.replace(entry.getKey(), dictionary.get(entry.getKey()) + entry.getValue()); //if we did, we increment the already existing one
					} else {
						dictionary.put(entry.getKey(), entry.getValue()); 	//if not, we create a new key for it
					}
				}
			}
		}
		//Calculating rank
		int sum = 0;
		int maxLength = 0;
		int maxApparitions = 0;
		int numberOfWords = 0;
		for (Map.Entry<Integer, Integer> entry : dictionary.entrySet()) {
			sum += fibonacci(entry.getKey() + 1) * entry.getValue();
			numberOfWords += entry.getValue();
			if (entry.getKey() > maxLength) {
				maxLength = entry.getKey();
				maxApparitions = entry.getValue();
			}
		}
		rank = (float) sum / (float) numberOfWords;
		results = new ReduceResults(fileName, maxLength, maxApparitions, rank);
		return results;
	}
}
