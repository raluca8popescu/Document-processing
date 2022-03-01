import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class Creator {
    public static List<TaskMap> createTaskMap(String filePath, int fragmentDimension, String separators) {
        File file = new File(filePath);
        List<TaskMap> fragments = new ArrayList<>();
        try (FileReader fr = new FileReader(file))
        {
            int content;
            int offset = 0;
            int size = 0;
            String fragment = "";
            while ((content = fr.read()) != -1) {
                size++;
                if (size <= fragmentDimension) {
                    fragment += String.valueOf((char) content);
                } else {
                    TaskMap task = new TaskMap(filePath, offset, fragment.length());
                    fragments.add(task);
                    offset += fragment.length();
                    fragment = "";
                    size = 1;
                    fragment += String.valueOf((char) content);
                }
            }
            TaskMap task = new TaskMap(filePath, offset, fragment.length());
            fragments.add(task);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fragments;
    }

    public static List<TaskReduce> createTaskReduce(List<String> filePaths, List<Future<ResultMap>> resultList) {
        List<TaskReduce> tasksReduce = new ArrayList<>();
        for (int i = 0; i < filePaths.size(); i++) {
            List<Map<Integer, Integer>> listMaps = new ArrayList<>();
            List <List<String>> listWords =  new ArrayList<>();
            ListIterator<Future<ResultMap>> iter = resultList.listIterator();

            while(iter.hasNext()){
                Future<ResultMap> future = iter.next();
                try {
                    ResultMap result = future.get();
                    // se verifica daca rezultatul de tip Map corespunde fisierului respectiv
                    if (result.fileName.compareTo(filePaths.get(i)) == 0) {
                        listMaps.add(result.mapWords);
                        listWords.add(result.words);
                        iter.remove();
                    }

                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
            // se elimina calea fisierului, pastrandu-se doar numele acestuia
            String [] path = filePaths.get(i).split("/");
            String fileName = path[path.length - 1];
            TaskReduce taskReduce = new TaskReduce(fileName, listMaps, listWords);
            tasksReduce.add(taskReduce);
        }
        return tasksReduce;
    }
}
