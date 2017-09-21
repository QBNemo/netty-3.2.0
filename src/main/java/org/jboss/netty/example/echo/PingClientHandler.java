package org.jboss.netty.example.echo;

import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.channel.WriteCompletionEvent;

public class PingClientHandler extends SimpleChannelUpstreamHandler {

    private static final Logger logger = Logger.getLogger(
            PingClientHandler.class.getName());

    private final ChannelBuffer pingMessage;
    private final AtomicLong transferredBytes = new AtomicLong();
    private final AtomicLong writeBytes = new AtomicLong();
    /**
     * Creates a client-side handler.
     */
    public PingClientHandler(String pingMsg) {
        if (pingMsg==null || pingMsg.isEmpty()) {
            throw new IllegalArgumentException("pingMsg: null or empty");
        }
    	int pingMsgSize = pingMsg.length();
        pingMessage = ChannelBuffers.buffer(pingMsgSize);
        for (int i = 0; i < pingMessage.capacity(); i++) {
            pingMessage.writeByte((byte)pingMsg.charAt(i));
        }
        new String();
    }

    public long getTransferredBytes() {
        return transferredBytes.get();
    }

    @Override
    public void channelConnected(
            ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        // Send the first message.  Server will not send anything here
        // because the firstMessage's capacity is 0.
        e.getChannel().write(pingMessage);
    }

    @Override
    public void messageReceived(
            ChannelHandlerContext ctx, MessageEvent e) {
        transferredBytes.addAndGet(((ChannelBuffer) e.getMessage()).readableBytes());
        log.debug("[recv: " + ((ChannelBuffer) e.getMessage()).readableBytes() + "]");
        if(transferredBytes.get() == writeBytes.get()) {
        	// 关闭连接
            e.getChannel().close();
        }
    }

    
    @Override
	public void writeComplete(ChannelHandlerContext ctx, WriteCompletionEvent e) throws Exception {
    	writeBytes.addAndGet(e.getWrittenAmount());
    	log.debug("[writeComplete: " + e.getWrittenAmount() + "]");
	}

	@Override
    public void exceptionCaught(
            ChannelHandlerContext ctx, ExceptionEvent e) {
        // Close the connection when an exception is raised.
        logger.log(
                Level.WARNING,
                "Unexpected exception from downstream.",
                e.getCause());
        e.getChannel().close();
    }
}
