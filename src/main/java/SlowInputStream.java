import java.io.IOException;
import java.io.InputStream;

/**
 * Hack to simulate a slow client.  Every 1mb of data published, sleeps for inputStreamDelay milliseconds.
 */
public class SlowInputStream extends InputStream {
    private int bytesRead = 0;

    private int inputStreamDelay;
    private InputStream delegate;

    public SlowInputStream(int inputStreamDelay, InputStream delegate) {
        this.inputStreamDelay = inputStreamDelay;
        this.delegate = delegate;
    }

    @Override
    public int read() throws IOException {
        bytesRead++;
        if (bytesRead % (1024 * 1024) == 0) {
            // Sleep so simulate slowness of this client.
            try {
                Thread.sleep(inputStreamDelay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return delegate.read();
    }
}
