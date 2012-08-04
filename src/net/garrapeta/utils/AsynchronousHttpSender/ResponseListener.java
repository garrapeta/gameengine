package net.garrapeta.utils.AsynchronousHttpSender;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;

public interface ResponseListener {

	public void onResponseReceived(HttpResponse response);

	public void onClientProtocolExceptionWhenSending(ClientProtocolException cpe);

	public void onIOExceptionWhenSending(IOException ioe);
	 
}
