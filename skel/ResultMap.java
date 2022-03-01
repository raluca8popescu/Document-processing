import java.util.List;
import java.util.Map;

public class ResultMap {
    Map<Integer, Integer> mapWords;
    List<String> words;
    String fileName;

    public ResultMap(Map<Integer, Integer> mapWords, List<String> words, String fileName) {
        this.mapWords = mapWords;
        this.words = words;
        this.fileName = fileName;
    }

    @Override
    public String toString() {
        return "Result{" +
                "mapWords=" + mapWords +
                ", words=" + words +
                ", fileName='" + fileName + '\'' +
                '}';
    }
}
