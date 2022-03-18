public class ReduceResults {
	String fileName;
	int maxLength;

	public int getMaxLength() {
		return maxLength;
	}

	public int getApparitionsMaxLength() {
		return apparitionsMaxLength;
	}

	public double getRank() {
		return rank;
	}

	public String getFileName() {
		return fileName;
	}

	int apparitionsMaxLength;
	double rank;

	public ReduceResults(String fileName, int maxLength, int apparitionsMaxLength, double rank) {
		this.fileName = fileName;
		this.maxLength = maxLength;
		this.apparitionsMaxLength = apparitionsMaxLength;
		this.rank = rank;
	}
}
