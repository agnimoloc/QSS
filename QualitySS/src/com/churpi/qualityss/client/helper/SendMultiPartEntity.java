package com.churpi.qualityss.client.helper;

import java.io.File;
import java.io.IOException;

import ch.boye.httpclientandroidlib.HttpEntity;
import ch.boye.httpclientandroidlib.HttpResponse;
import ch.boye.httpclientandroidlib.client.ClientProtocolException;
import ch.boye.httpclientandroidlib.client.HttpClient;
import ch.boye.httpclientandroidlib.client.methods.HttpPost;
import ch.boye.httpclientandroidlib.entity.ContentType;
import ch.boye.httpclientandroidlib.entity.mime.MultipartEntityBuilder;
import ch.boye.httpclientandroidlib.impl.client.HttpClientBuilder;
import ch.boye.httpclientandroidlib.util.EntityUtils;

public class SendMultiPartEntity {
    private static final String FILE_PART_NAME = "file";
    
    
    private ProgressHttpEntityWrapper mHttpEntity;
    private HttpClient httpClient;
    //private HttpContext httpContext;
    private HttpPost httpPost;
    private File file;
    private ProgressListener listener;
    
    public SendMultiPartEntity(File file, String url, ProgressListener listener){
    	HttpClientBuilder clientBuilder = HttpClientBuilder.create();
    	httpClient = clientBuilder.build();
		//httpContext = new BasicHttpContext();
		httpPost = new HttpPost(url);
        mHttpEntity = new ProgressHttpEntityWrapper(buildMultipartEntity(file), listener, file);
        this.file = file;
        this.listener = listener;
    }
    
	private HttpEntity buildMultipartEntity(File file) {
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.addBinaryBody(FILE_PART_NAME, file, ContentType.create("image/jpeg"), file.getName());
        //builder.addBinaryBody(FILE_PART_NAME, file);
        return builder.build();
    }
	
	public void send(){		
		
		httpPost.setEntity(mHttpEntity);
		
		/*new Thread(new Runnable() {
		    public void run() {*/
				try {
					//HttpResponse response = httpClient.execute(httpPost, httpContext);
					HttpResponse response = httpClient.execute(httpPost);
					String serverResponse = EntityUtils.toString(response.getEntity());
					listener.onFinish(serverResponse, file);
				} catch (ClientProtocolException e) {
					listener.onError(e, file);
					e.printStackTrace();
				} catch (IOException e) {
					listener.onError(e, file);
					e.printStackTrace();
				}
		    /*}
		  }).start();*/		
	}

}
