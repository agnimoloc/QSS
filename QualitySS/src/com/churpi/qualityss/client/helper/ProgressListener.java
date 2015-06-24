package com.churpi.qualityss.client.helper;

import java.io.File;

public abstract class ProgressListener {

	public abstract void onProgress(long progress, long total, File file);
	public abstract void onFinish(String response, File file);
	public abstract void onError(Exception e, File file);
}
