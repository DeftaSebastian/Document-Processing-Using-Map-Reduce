import java.util.Comparator;

public class SortByRank implements Comparator<ReduceResults> {
	public int compare(ReduceResults a, ReduceResults b) {
		if (a.rank == b.rank) {
			int A = Integer.parseInt(a.fileName.substring(a.fileName.length() - 1));
			int B = Integer.parseInt(b.fileName.substring(b.fileName.length() - 1));
			return A - B;
		} else {
			if(b.rank - a.rank > 0) {
				return 1;
			} else
			{
				return - 1;
			}
		}
	}
}
