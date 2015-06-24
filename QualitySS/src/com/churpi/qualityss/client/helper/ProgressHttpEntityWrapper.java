package com.churpi.qualityss.client.helper;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import ch.boye.httpclientandroidlib.HttpEntity;
import ch.boye.httpclientandroidlib.entity.HttpEntityWrapper;

public class ProgressHttpEntityWrapper extends HttpEntityWrapper{

    private ProgressListener listener;
    private File file;
    
	public ProgressHttpEntityWrapper(HttpEntity wrappedEntity, ProgressListener listener, File file) {
		super(wrappedEntity);
		this.listener = listener;		
		this.file = file;
	}
	
	@Override
	public void writeTo(OutputStream outstream) throws IOException {
		
		super.writeTo(new CountingOutputStream(outstream, this.listener, this.getContentLength(), file));
	}

}
