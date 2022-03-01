public class ResultReduce implements Comparable<ResultReduce>{
    String fileName;
    float rank;
    int maximumLength;
    int numberOfWords;

    public ResultReduce(String fileName, float rank, int maximumLength, int numberOfWords) {
        this.fileName = fileName;
        this.rank = rank;
        this.maximumLength = maximumLength;
        this.numberOfWords = numberOfWords;
    }

    public float getRank() {
        return rank;
    }

    @Override
    public int compareTo(ResultReduce e) {
        if (e.rank > this.rank) {
            return 1;
        } else {
            return -1;
        }
    }

    @Override
    public String toString() {
        return fileName +
                "," + String.format("%.2f", rank) +
                "," + maximumLength +
                "," + numberOfWords;
    }

    public int compareTo(float rank) {
        if (rank > this.rank) {
            return 1;
        } else {
            return -1;
        }
    }
}
