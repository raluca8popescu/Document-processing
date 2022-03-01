import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public class TaskReduce implements Callable<ResultReduce> {
    private final String fileName;
    private final List<Map<Integer, Integer>> listMaps;
    private final List<List<String>> listWords;

    public TaskReduce(String fileName, List<Map<Integer, Integer>> listMaps, List<List<String>> listWords) {
        this.fileName = fileName;
        this.listMaps = listMaps;
        this.listWords = listWords;
    }

    @Override
    public ResultReduce call() throws Exception {
        float rank;
        float sum = 0;
        float numberOfWords = 0;
        // se calculeaza numaratorul formulei pentru rang
        for (Map<Integer, Integer> listMap : listMaps) {
            for (Map.Entry<Integer, Integer> entry : listMap.entrySet()) {
                numberOfWords += entry.getValue();
                sum += Tema2.nthFibonacciTerm(entry.getKey() + 1) * entry.getValue();
            }
        }
        rank = sum / numberOfWords;
        int maxim = -1;
        int count = 0;

        // se calculeaza lungimea maxima si numarul de aparitii al acesteia
        for (List<String> listWord : listWords) {
            if (!listWord.isEmpty()) {
                if (listWord.get(0).length() >= maxim) {
                    if (listWord.get(0).length() == maxim) {
                        count += listWord.size();
                    } else {
                        maxim = listWord.get(0).length();
                        count = listWord.size();
                    }
                }
            }
        }
        return new ResultReduce(fileName, rank, maxim, count);
    }

    @Override
    public String toString() {
        return "TaskReduce{" +
                "fileName='" + fileName + '\'' +
                ", listMaps=" + listMaps +
                ", listWords=" + listWords +
                '}';
    }
}

