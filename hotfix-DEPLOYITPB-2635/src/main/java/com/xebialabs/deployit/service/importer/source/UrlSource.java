package com.xebialabs.deployit.service.importer.source;

import static org.apache.commons.io.FilenameUtils.getExtension;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.ByteStreams;
import com.xebialabs.deployit.exception.RuntimeIOException;
import com.xebialabs.deployit.server.api.importer.ImportSource;

public class UrlSource implements ImportSource {

	private final URL location;
	private FileSource downloaded;

	public UrlSource(URL location) {
		this.location = location;
	}

	private void download() {
		try {
		    logger.debug("Preparing to download package from {}", location);
		    HttpClient client = new HttpClient();
		    String uri = location.toURI().toString();
			HttpMethod method = new GetMethod(uri);
		    int statusCode = client.executeMethod(method);
		    if (statusCode != HttpStatus.SC_OK) {
		        throw new RuntimeIOException("Failed to download package,status="+ statusCode +", from url " + location);
		    }
		   save(uri, method.getResponseBodyAsStream());
		} catch (URISyntaxException e) {
		    throw new IllegalArgumentException("Invalid URL", e);
		} catch (HttpException e) {
		    throw new RuntimeIOException(e);
		} catch (IOException e) {
		    throw new RuntimeIOException(e);
		}
	}

	private void save(String sourceUri, InputStream responseBodyAsStream) throws IOException {
		try {
			File archive = File.createTempFile("uploaded-package", "." + getExtension(sourceUri));
			ByteStreams.copy(responseBodyAsStream, new FileOutputStream(archive));
			this.downloaded = new FileSource(archive, true);
		} finally {
			responseBodyAsStream.close();
		}
	}

	private static final Logger logger = LoggerFactory.getLogger(UrlSource.class);

	@Override
	public File getFile() {
		if (downloaded == null) {
			download();
		}

		return downloaded.getFile();
	}

	@Override
	public void cleanUp() {
		if (downloaded != null) {
			downloaded.cleanUp();
		}
	}

	@Override
	public String toString() {
		return "UrlSource[" + location + "]";
	}
}
