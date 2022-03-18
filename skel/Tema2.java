import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Tema2 {
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		if (args.length < 3) {
			System.err.println("Usage: Tema2 <workers> <in_file> <out_file>");
			return;
		}
		int numberOfDocuments = 0;
		int numberOfWorkers = Integer.parseInt(args[0]);
		String fileIn = args[1];
		String fileOut = args[2];
		Set<Callable<MapClass>> taskMaps = new HashSet<Callable<MapClass>>();
		String[] fileNames = new String[numberOfDocuments];
		try {
			if (!fileIn.isEmpty()) {
				File myObj = new File(fileIn);
				Scanner myReader = new Scanner(myObj);
				int fragmentSize = Integer.parseInt(myReader.nextLine());
				numberOfDocuments = Integer.parseInt(myReader.nextLine());
				fileNames = new String[numberOfDocuments];
				for (int i = 0; i < numberOfDocuments; i++) {    //we start creating the tasks for Map
					String file = myReader.nextLine();
					fileNames[i] = file;
					long offSet = 0;
					Path path = Paths.get(file);
					long size = Files.size(path.toAbsolutePath());  //we get the absolute path to see the file Size
					while (offSet < size) {
						if (size - offSet < fragmentSize) {
							taskMaps.add(new TaskMap(file, offSet, (int) (size - offSet)));
						} else {
							taskMaps.add(new TaskMap(file, offSet, fragmentSize));
						}
						offSet += (long) fragmentSize;
					}
				}
				myReader.close();
			}
		} catch (FileNotFoundException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}


		//Crearea de task-uri functioneaza momentan bine
		ExecutorService tpe = Executors.newFixedThreadPool(numberOfWorkers);
		List<Future<MapClass>> taskMapFutures = tpe.invokeAll(taskMaps);
		ArrayList<MapClass> mapClasses = new ArrayList<>();
		for (Future<MapClass> taskMapFuture : taskMapFutures) {
			mapClasses.add(taskMapFuture.get());
		}
		tpe.shutdown();
		ExecutorService tpeReduce = Executors.newFixedThreadPool(numberOfWorkers);
		Set<Callable<ReduceResults>> taskReduces = new HashSet<Callable<ReduceResults>>();
		for (int i = 0; i < numberOfDocuments; i++) {
			String fileName = fileNames[i];
			taskReduces.add(new TaskReduce(fileName, mapClasses));
		}
		List<Future<ReduceResults>> taskResults = tpeReduce.invokeAll(taskReduces);
		ArrayList<ReduceResults> results = new ArrayList<>();
		for (Future<ReduceResults> taskResult : taskResults) {
			results.add(taskResult.get());
		}
		tpeReduce.shutdown();

		try {
			File myObj = new File(fileOut);
			myObj.createNewFile();
			FileWriter myWriter = new FileWriter(fileOut);
			for(int i = 0; i < numberOfDocuments; i++){
				results.sort(new SortByRank());
				String fileName = results.get(i).fileName;
				String[] folder = fileName.split("[/]");
				myWriter.write(folder[folder.length - 1] + "," + String.format("%.2f", results.get(i).rank)
						+ "," + results.get(i).maxLength + "," + results.get(i).apparitionsMaxLength + "\n");
			}
			myWriter.close();
		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
	}
}

