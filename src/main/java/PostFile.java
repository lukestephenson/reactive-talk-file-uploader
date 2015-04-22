import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class PostFile {
    public static void main(String[] args) throws Exception {
//        if (args.length != 1)  {
//            System.out.println("File path not given");
//            System.exit(1);
//        }
        ExecutorService executor = Executors.newFixedThreadPool(100);

        List<Callable<Void>> jobs = new ArrayList<>();
        for (int i = 0; i < 150; i++) {
            jobs.add(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    try {
                        uploadFile();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    return null;
                }
            });
        }

        long startTime = System.currentTimeMillis();
        executor.invokeAll(jobs);
        executor.shutdown();
        executor.awaitTermination(30, TimeUnit.MINUTES);
        long endTime = System.currentTimeMillis();

        System.out.println("Took " + (endTime - startTime) + " ms to complete");
    }

    public static void uploadFile() throws IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            HttpPost httppost = new HttpPost("http://localhost:9000/save-size");

            File file = new File("C:\\Users\\Luke\\Music\\Vietnamese\\Vietnamese 1B CD2\\01 Track 1.wma");

            InputStream inputStream = new SlowInputStream(new BufferedInputStream(new FileInputStream(file)));

            InputStreamEntity reqEntity = new InputStreamEntity(inputStream, -1, ContentType.APPLICATION_OCTET_STREAM);
            reqEntity.setChunked(true);
            // It may be more appropriate to use FileEntity class in this particular
            // instance but we are using a more generic InputStreamEntity to demonstrate
            // the capability to stream out data from any arbitrary source
            //
            // FileEntity entity = new FileEntity(file, "binary/octet-stream");

            httppost.setEntity(reqEntity);

            System.out.println("Executing request: " + httppost.getRequestLine());
            CloseableHttpResponse response = httpclient.execute(httppost);
            try {
//                System.out.println("----------------------------------------");
//                System.out.println(response.getStatusLine());
                System.out.println(toString(response.getEntity().getContent()));
                EntityUtils.consume(response.getEntity());
            } finally {
                response.close();
            }
        } finally {
            httpclient.close();
        }
    }

    public static String toString(InputStream in) throws IOException {
        InputStreamReader is = new InputStreamReader(in);
        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(is);
        String read = br.readLine();

        while (read != null) {
            //System.out.println(read);
            sb.append(read);
            read = br.readLine();

        }

        return sb.toString();
    }
}