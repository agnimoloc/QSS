package com.churpi.qualityss.client.helper;

import java.io.File;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;


public class CountingOutputStream extends FilterOutputStream {

	private final ProgressListener listener;
	private long transferred;
	private long total;
	private File file;
	
	public CountingOutputStream(OutputStream out, final ProgressListener listener, long total, File file)
	{
		super(out);
		this.listener = listener;
		this.transferred = 0;
		this.total = total;
		this.file = file;
	}

	public void write(byte[] b, int off, int len) throws IOException
	{
		out.write(b, off, len);
		this.transferred += len;
		this.listener.onProgress(this.transferred, total, file);
	}

	public void write(int b) throws IOException
	{
		out.write(b);
		this.transferred++;
		this.listener.onProgress(this.transferred, total, file);
	}

}
