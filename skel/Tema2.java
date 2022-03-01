import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class Tema2 {

    public static int nthFibonacciTerm(int n) {
        if (n == 1 || n == 0) {
            return n;
        }
        return nthFibonacciTerm(n-1) + nthFibonacciTerm(n-2);
    }

    /**
     * Parcurge rezultatele operatiei Reduce si le scrie in fisierul de iesire
     */
    public static void writeOutputFile(String outFilePath, List<Future<ResultReduce>> resultsReduce)
            throws IOException {
        FileWriter myWriter = new FileWriter(outFilePath);
        for (Future<ResultReduce> future : resultsReduce) {
            try {
                ResultReduce finalResult = future.get();
                myWriter.write(finalResult.fileName + "," + String.format("%.2f", finalResult.rank) + ","
                        + finalResult.maximumLength + "," + finalResult.numberOfWords + "\n");
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        myWriter.close();
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        if (args.length < 3) {
            System.err.println("Usage: Tema2 <workers> <in_file> <out_file>");
            return;
        }

        int workers = Integer.parseInt(args[0]);
        String separators = ";:/?~\\., ><`[]{}()!@#$%^&-_+'=*\"|\t\r\n";

        Scanner in = new Scanner(new FileReader(args[1]));
        int fragmentDimension = Integer.parseInt(in.nextLine());
        int numberOfFiles = Integer.parseInt(in.nextLine());

        ExecutorService executorMap = Executors.newFixedThreadPool(workers);
        ExecutorService executorReduce = Executors.newFixedThreadPool(workers);

        List<TaskMap> tasksMap = new ArrayList<>();
        List<TaskReduce> tasksReduce;

        List<Future<ResultMap>> resultsMap = null;
        List<Future<ResultReduce>> resultsReduce = null;
        List<String> filePaths = new ArrayList<>();

        // pargurge fisierele de intrare si creeaza task-urile de tip Map
        for (int i = 0; i < numberOfFiles; i++) {
            String filePath = in.nextLine();
            filePaths.add(filePath);
            List<TaskMap> newFragments = Creator.createTaskMap(filePath, fragmentDimension, separators);
            tasksMap.addAll(newFragments);
        }

        /*
            se executa lista de Callable tasks de tip Map si se returneaza
            o lista de obiecte ResultMap de tip Future
        */
        try {
            resultsMap = executorMap.invokeAll(tasksMap);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        executorMap.shutdown();

        // se creeaza task-urile de tip Reduce
        tasksReduce = Creator.createTaskReduce(filePaths, resultsMap);

        /*
            se executa lista de Callable tasks de tip Reduce si se returneaza
            o lista de obiecte ResultReduce de tip Future
        */
        try {
            resultsReduce = executorReduce.invokeAll(tasksReduce);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        executorReduce.shutdown();

        // se sorteaza lista de rezultate de tip Reduce in functie de rang
        Comparator<Future<ResultReduce>> compareByRank = (o1, o2) -> {
            try {
                return o1.get().compareTo(o2.get().rank);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            return -1;
        };
        assert resultsReduce != null;
        resultsReduce.sort(compareByRank);

        // se scriu rezultatele in fisierul de iesire
        writeOutputFile(args[2], resultsReduce);
    }
}
