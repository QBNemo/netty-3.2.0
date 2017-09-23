package org.jboss.netty.buffer;

import org.jboss.netty.channel.AdaptiveReceiveBufferSizePredictor;
import org.jboss.netty.channel.ReceiveBufferSizePredictor;

import org.junit.Test;
import static org.junit.Assert.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import junit.framework.Assert;

public class AdaptiveReceiveBufferSizePredictorTest {
	private static Logger log = LoggerFactory.getLogger(AdaptiveReceiveBufferSizePredictorTest.class);
	
	//@Test
	public void testAdaptive() {
	    ReceiveBufferSizePredictor receiveBufferSizePredictor = new AdaptiveReceiveBufferSizePredictor();
	    receiveBufferSizePredictor.previousReceiveBufferSize(960);
	    receiveBufferSizePredictor.previousReceiveBufferSize(960);
	    receiveBufferSizePredictor.previousReceiveBufferSize(960);
	    log.debug("current buf size: " + receiveBufferSizePredictor.nextReceiveBufferSize());
	    assertEquals(receiveBufferSizePredictor.nextReceiveBufferSize(), 1024);

	    receiveBufferSizePredictor.previousReceiveBufferSize(1025);
	    receiveBufferSizePredictor.previousReceiveBufferSize(1300);
	    log.debug("current buf size: " + receiveBufferSizePredictor.nextReceiveBufferSize());
	    Assert.assertTrue(receiveBufferSizePredictor.nextReceiveBufferSize() > 1024);

	    receiveBufferSizePredictor.previousReceiveBufferSize(4000);
	    log.debug("current buf size: " + receiveBufferSizePredictor.nextReceiveBufferSize());
	    Assert.assertTrue(receiveBufferSizePredictor.nextReceiveBufferSize() > 2000);
    }
	
	@Test
	public void testAdaptiveDecrease() {
		ReceiveBufferSizePredictor receiveBufferSizePredictor = new AdaptiveReceiveBufferSizePredictor();
	    assertEquals(receiveBufferSizePredictor.nextReceiveBufferSize(), 1024); // initial size
	    receiveBufferSizePredictor.previousReceiveBufferSize(768);              // true
	    receiveBufferSizePredictor.previousReceiveBufferSize(1023);
	    receiveBufferSizePredictor.previousReceiveBufferSize(768);              // decrease
    }
}
