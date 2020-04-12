package services;

import java.util.ArrayList;
import java.util.List;

import edu.csus.ecs.pc2.api.PC2APIFile;
import edu.csus.ecs.pc2.core.model.IFile;
import models.File;

public class FileService {

	public static IFile[] createFileArray(File[] files) {
		List<IFile> s = new ArrayList<IFile>();
		for(File f : files) {
			s.add(new PC2APIFile(f.getFileName(), f.getByteData()));
		}
		
		return s.toArray(new IFile[files.length]);
	}
	
	public static List<File> convertFilesToModel(IFile[] stdErr, IFile[] stdOut) {
		
		List<File> f = new ArrayList<File>();
		
		if(stdErr != null) {
			for(IFile file : stdErr)
				f.add(new File(file.getFileName(), file.getBase64Data()));
		}
		
		for(IFile file : stdOut)
			f.add(new File(file.getFileName(), file.getBase64Data()));
		
		return f;
	}
}
