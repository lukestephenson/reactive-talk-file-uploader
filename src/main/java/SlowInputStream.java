import java.io.IOException;
import java.io.InputStream;

/**
 * Hack to simulate a slow client.  Every 1mb of data published, sleeps for 5 seconds.
 */
public class SlowInputStream extends InputStream {
    private int bytesRead = 0;

    private InputStream delegate;

    public SlowInputStream(InputStream delegate) {
        this.delegate = delegate;
    }

    @Override
    public int read() throws IOException {
        bytesRead++;
        if (bytesRead % (1024 * 1024) == 0) {
            // Sleep so similate slowness of this client.
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return delegate.read();
    }
}
