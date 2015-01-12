package rh.resources;

import java.io.IOException;
import java.io.InputStream;

public class Resource {
	private int size;
	
	private InputStream inputStream;

	protected Resource(int size, InputStream inputStream) {
		this.setSize(size);
		this.setInputStream(inputStream);
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	public byte[] getBytes() throws IOException {
		byte[] data = new byte[size];
		int offset = 0;
		while(size - offset > 0) {
			offset += inputStream.read(data, offset, size - offset);
		}
		return data;
	}
}