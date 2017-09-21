package org.jboss.netty.example.echo;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

public class Ping implements Runnable{
    String host = "127.0.0.1";
    int port = 8765;
    String pingMsg = "ping";
    
    public Ping() {}
    
    public Ping(String host, int port, String pingMsg) {
    	if(host==null) {
    		throw new IllegalArgumentException("host: " + host);
    	}
    	if(port<=0) {
    		throw new IllegalArgumentException("port: " + port);
    	}
    	if(pingMsg==null) {
    		throw new IllegalArgumentException("pingMsg: " + pingMsg);
    	}
    	this.host = host;
    	this.port = port;
    	this.pingMsg = pingMsg;
    }
    
    public Ping(int pingMsgSize) {
    	if(pingMsgSize<=0) {
    		throw new IllegalArgumentException("pingMsgSize: " + pingMsgSize);
    	}
    	StringBuilder sb = new StringBuilder();
    	for(int i=0; i<pingMsgSize; i++) {
    		if(i%1024==1023) {
    			sb.append('K');
    		} else {
    		    sb.append('0');
    		}
    	}
    	this.pingMsg = sb.toString();
    }
    
	@Override
	public void run() {
        // Configure the client.
        ClientBootstrap bootstrap = new ClientBootstrap(
                new NioClientSocketChannelFactory(
                        Executors.newCachedThreadPool(),
                        Executors.newCachedThreadPool()));

        // Set up the pipeline factory.
        bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
            public ChannelPipeline getPipeline() throws Exception {
                return Channels.pipeline(
                        new PingClientHandler(pingMsg));
            }
        });

        //bootstrap.setOption("receiveBufferSize", 512);
        //bootstrap.setOption("sendBufferSize", 1024);
        
        // Start the connection attempt.
        ChannelFuture future = bootstrap.connect(new InetSocketAddress(host, port));

        // Wait until the connection is closed or the connection attempt fails.
        future.getChannel().getCloseFuture().awaitUninterruptibly();

        // Shut down thread pools to exit.
        bootstrap.releaseExternalResources();
	}
	
    public static void main(String[] args) throws Exception {
    	Thread thread = null;
    	for(int i=1; i<=1; i++) {
    		thread = new Thread(new Ping(25600), "ping-client-" + i); // big size, INTEREST_CHANGED
    		thread.start();
    	}
    }
}
