/**
 * 
 */
package org.accada.epcis.repository.test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Set;

/**
 * @author Marco Steybe
 */
public class SubscriptionNotification {

    /**
     * The port on which this test listens for notifications (results for
     * subscribed queries).
     */
    private static final int PORT = 9999;

    private String notification = null;

    /**
     * Listen for notifications for <timeToWait> ms. If the <timeToWait> is
     * expired, null is returned.
     * 
     * @param timeToWait
     *            The time to wait for notifications (in milliseconds).
     * @return The response received without http headers.
     * @throws IOException
     *             If an I/O error occured.
     * @throws SocketTimeoutException
     *             If the <timeToWait> expired before a notification came in.
     */
    public String waitForNotification(int timeToWait) throws IOException,
            SocketTimeoutException {
        // listen on localhost:<PORT> in non-blocking fasion
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);
        ssc.socket().bind(new InetSocketAddress("127.0.0.1", PORT));

        // register a selector for the socket channel
        Selector selector = Selector.open();
        ssc.register(selector, SelectionKey.OP_ACCEPT);

        // wait for notifications (1 min)
        NotificationListener listener = new NotificationListener(selector);
        listener.start();
        long timeout = 60 * 1000 + 1;
        synchronized (listener) {
            try {
                listener.wait(timeout);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        // stop & close everything
        listener.interrupt();
        listener.stopRunning();
        selector.close();
        ssc.socket().close();
        ssc.close();

        return notification;
    }

    private class NotificationListener extends Thread {

        private boolean keepRunning = true;
        private Selector selector;

        NotificationListener(Selector selector) {
            this.selector = selector;
        }

        public void run() {
            try {
                while (selector.select() > 0 && keepRunning) {
                    Set<SelectionKey> keys = selector.selectedKeys();
                    for (SelectionKey key : keys) {
                        ServerSocketChannel ssch = (ServerSocketChannel) key.channel();
                        SocketChannel ch = ssch.accept();
                        handleNotification(ch);
                        System.out.println("a");
                        this.notify();
                        System.out.println("b");
                        this.interrupt();
                        System.out.println("c");
                        keys.remove(key);
                    }
                }
            } catch (IOException e) {
            }
        }

        public void stopRunning() {
            keepRunning = false;
        }
    }

    private void handleNotification(SocketChannel sc) throws IOException {
        ByteBuffer bb = ByteBuffer.allocate(131072);
        sc.read(bb);
        ByteArrayInputStream bais = new ByteArrayInputStream(bb.array());

        StringBuffer sb = new StringBuffer();
        BufferedReader br = new BufferedReader(new InputStreamReader(bais));
        String line = null;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        int xmlIndex = sb.indexOf("<?xml");
        int index = sb.indexOf("<", xmlIndex + 1);
        notification = sb.substring(index).trim();
    }
}
