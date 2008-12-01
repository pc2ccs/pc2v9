package edu.csus.ecs.pc2.core.log;

import java.io.OutputStream;
import java.util.Vector;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.StreamHandler;

/**
 * This class will handle notifying registered IStreamListeners of new log messages.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

//$HeadURL$

public class LogStreamHandler extends StreamHandler {

    private Vector<IStreamListener> streamListenerList = new Vector<IStreamListener>();
    
    public LogStreamHandler() {
        super();
        setupStream();
    }

    public LogStreamHandler(OutputStream out, Formatter formatter) {
        super(out, formatter);
        setupStream();
    }

    public void addStreamListener(IStreamListener streamListener) {
        streamListenerList.addElement(streamListener);
    }
    
    public void removeStreamListener(IStreamListener streamListener) {
        streamListenerList.remove(streamListener);
    }
    
    private void fireStreamListener(String inString) {
        for (int i = 0; i < streamListenerList.size(); i++) {
            streamListenerList.elementAt(i).messageAdded(inString);
        }
    }

    @Override
    public synchronized void publish(LogRecord arg0) {
        super.publish(arg0);
        super.flush();
    }
    
    public void setupStream() {
        setOutputStream(new OutputStream() {
            public void write(int b) {
            } // not called

            public void write(byte[] b, int off, int len) {
                String inString = new String(b, off, len);
                fireStreamListener(inString);
            }
        });
    }
}
