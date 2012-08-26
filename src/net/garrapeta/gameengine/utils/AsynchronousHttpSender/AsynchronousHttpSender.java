package net.garrapeta.gameengine.utils.AsynchronousHttpSender;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.Handler;

public class AsynchronousHttpSender extends Thread {

	private DefaultHttpClient httpClient;
	private HttpUriRequest request;
	private Handler handler;
	private CallbackWrapper wrapper;

	public static void sendRequest(final HttpUriRequest request, ResponseListener listener) {

		AsynchronousHttpSender sender = 
				new AsynchronousHttpSender(request, 
						                   new Handler(), 
						                   new CallbackWrapper(listener));
		sender.start();
	}

	protected AsynchronousHttpSender(HttpUriRequest request, Handler handler,
			CallbackWrapper wrapper) {
		
		this.httpClient = new DefaultHttpClient();
		this.request = request;
		this.handler = handler;
		this.wrapper = wrapper;
	}

	public void run() {
		try {
			final HttpResponse response;
			synchronized (httpClient) {
				response = httpClient.execute(request);
			}
			// process response
			wrapper.setResponse(response);
			handler.post(wrapper);
		} catch (ClientProtocolException cpe) {
			cpe.printStackTrace();
			wrapper.listener.onClientProtocolExceptionWhenSending(cpe);
		} catch (IOException ioe) {
			ioe.printStackTrace();
			wrapper.listener.onIOExceptionWhenSending(ioe);
		}
	}


}

class CallbackWrapper implements Runnable {

	ResponseListener listener;
	HttpResponse response;

	public CallbackWrapper(ResponseListener listener) {
		this.listener = listener;
	}

	public void run() {
		listener.onResponseReceived(response);
	}

	public void setResponse(HttpResponse response) {
		this.response = response;
	}
}
