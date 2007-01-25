/**
 * 
 */
package org.accada.epcis.repository.test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * @author Marco Steybe
 */
public class SubscriptionResponseListener extends Thread {

    private static final int BUFFER_SIZE = 1024;
    
    private boolean isRunning = false;

    private ByteBuffer buf = ByteBuffer.allocate(BUFFER_SIZE);

    private Selector selector = null;
    private String response = null;

    public SubscriptionResponseListener(int port) throws IOException {
        // create a non-blocking server socket
        ServerSocketChannel ssChannel = ServerSocketChannel.open();
        ssChannel.configureBlocking(false);
        ssChannel.socket().bind(new InetSocketAddress("127.0.0.1", port));

        // create the selector and register the server socket with the selector
        selector = Selector.open();
        ssChannel.register(selector, SelectionKey.OP_ACCEPT);
    }

    public void run() {
        isRunning = true;
        try {
            while (isRunning) {
                // select an event and process it
                selector.select();
                Iterator<SelectionKey> it = selector.selectedKeys().iterator();
                while (it.hasNext()) {
                    SelectionKey key = it.next();
                    it.remove();

                    // check if it is a connection request
                    if (key.isAcceptable()) {
                        SocketChannel channel = null;
                        try {
                            ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
                            channel = ssc.accept();
                        } catch (IOException e) {
                            e.printStackTrace();
                            key.cancel();
                        }

                        if (channel != null) {
                            try {
                                channel.configureBlocking(false);
                                channel.register(selector, SelectionKey.OP_READ);
                            } catch (IOException e) {
                                e.printStackTrace();
                                key.cancel();
                            }
                        }
                    }

                    if (key.isReadable()) {
                        SocketChannel channel = (SocketChannel) key.channel();
                        buf.clear();

                        // read data
                        int numBytesRead = channel.read(buf);
                        if (numBytesRead > 0) {
                            // to read the bytes, flip the buffer
                            buf.flip();
                            byte[] data = buf.array();
                            response = readData(data);
                        }
                        buf.clear();

                        // write OK response
                        ByteBuffer bb = ByteBuffer.wrap("HTTP/1.0 200 OK".getBytes());
                        channel.write(bb);

                        // close channel
                        try {
                            channel.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                            key.cancel();
                        }

                        // notify everyone waiting on us
                        synchronized (this) {
                            this.notifyAll();
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String readData(byte[] data) {
        String response = null;
        String s = new String(data).trim();
        int xmlIndex = s.indexOf("<?xml");
        int index = s.indexOf("<", xmlIndex + 1);
        if (index >= 0) {
            response = s.substring(index).trim();
        }
        return response;
    }

    public String fetchResponse() {
        String resp = this.response;
        this.response = null; // reset
        return resp;
    }
    
    public boolean isRunning() {
        return isRunning;
    }
    
    public void stopRunning() {
        isRunning = false;
        try {
            selector.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
