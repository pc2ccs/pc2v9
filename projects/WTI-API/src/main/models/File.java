package models;

public class File{

	private String byteData;
	private String fileName;
	public File(String fileName, String byteData ) {
		super();
		this.byteData = byteData;
		this.fileName = fileName;
	}
	public File() {
		
	}
	public String getByteData() {
		return byteData;
	}
	public void setByteData(String byteData) {
		this.byteData = byteData;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
}
