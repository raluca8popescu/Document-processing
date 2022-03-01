import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.Callable;

public class TaskMap implements Callable<ResultMap> {
    private final String filePath;
    private final int offset;
    private final int dimension;

    public TaskMap(String fileName, int offset, int dimension) {
        this.filePath = fileName;
        this.offset = offset;
        this.dimension = dimension;
    }

    @Override
    public String toString() {
        return "Task{" +
                "fileName='" + filePath + '\'' +
                ", offset=" + offset +
                ", dimension=" + dimension +
                '}';
    }

    @Override
    public ResultMap call() throws Exception {
        File file = new File(filePath);
        Map<Integer, Integer> map = new HashMap<>();
        List<String> maximumWords =  new ArrayList<>();
        int content;
        int dim = 0;
        boolean startWithMiddleWord = false;
        ResultMap result = null;
        String fragment = "";

        try (FileReader fr = new FileReader(file)) {
            String separators = ";:/?~\\., ><`[]{}()!@#$%^&-_+'=*\"|\t\r\n";
            if (offset != 0) {
                /*
                    se sare peste offset-ul dat -1 pentru a se verifica daca
                    ne aflam in mijlocul unui cuvant la inceput de fragment
                 */
                fr.skip(offset - 1);
                content = fr.read();
                String beforeChr = String.valueOf((char) content);
                if (!separators.contains(beforeChr)) {
                    startWithMiddleWord = true;
                }
            }

            while (dim < dimension) {
                content = fr.read();
                dim++;
                String chr = String.valueOf((char) content);
                // daca s-a gasit un separator, atunci citirea nu se mai afla in mijlocul unui cuvant
                if (separators.contains(chr)) {
                    startWithMiddleWord = false;
                }

                if (!startWithMiddleWord) {
                    fragment += chr;
                }
            }
            // se verifica daca suntem in mijlocul unui cuvant la final de fragment
            while ((content = fr.read()) != -1) {
                String chr = String.valueOf((char) content);
                if (!separators.contains(chr) && (!separators.contains(fragment.substring(fragment.length() - 1)))) {
                    fragment += chr;
                } else {
                    break;
                }
            }
            // se separa fragmentul in cuvinte in functie de separatori si se elimina string-urile goale
            String [] words = Arrays.stream(fragment.split("[;:/?˜.,><'\\[\\]{}()!@#$%ˆ&\\- +’=*”| \\n\\r]+"))
                    .filter(e -> e.trim().length() > 0).toArray(String[]::new);

            // se creeaza dictionarul si lista de cuvinte de lungime maxima
            int maximumDimension = -1;
            for (int i = 0; i < words.length; i++) {
                if (words[i].length() >= maximumDimension) {
                    if (words[i].length() == maximumDimension) {
                        maximumWords.add(words[i]);
                    } else {
                        maximumDimension = words[i].length();
                        maximumWords.clear();
                        maximumWords.add(words[i]);
                    }
                }
                if (map.containsKey(words[i].length())) {
                    map.replace(words[i].length(), map.get(words[i].length()) + 1);
                } else {
                    map.put(words[i].length(), 1);
                }
            }
            result = new ResultMap(map, maximumWords, filePath);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }


}
