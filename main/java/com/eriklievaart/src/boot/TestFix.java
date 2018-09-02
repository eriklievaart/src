package com.eriklievaart.src.boot;

import java.util.Hashtable;
import java.util.Map;

import com.eriklievaart.src.scanner.FileContents;
import com.eriklievaart.src.scanner.JavaFile;
import com.eriklievaart.src.scanner.JavaLine;
import com.eriklievaart.src.structure.Project;
import com.eriklievaart.toolkit.io.api.Console;
import com.eriklievaart.toolkit.lang.api.check.Check;
import com.eriklievaart.toolkit.lang.api.str.Str;
import com.eriklievaart.toolkit.vfs.api.file.SystemFile;
import com.eriklievaart.toolkit.vfs.api.file.VirtualFile;

public class TestFix {

	public static void main(String[] args) {
		Project project = new Project("toolkit");

		Map<String, String> simpleToFull = new Hashtable<>();
		for (JavaFile file : project.getMainJavaFiles()) {
			simpleToFull.put(file.getSimpleName(), file.getFullyQualifiedName());
		}

		for (JavaFile test : project.getTestJavaFiles()) {
			String name = test.getSimpleName();
			if (name.endsWith("U")) {
				String full = simpleToFull.get(name.replaceFirst("U$", ""));
				boolean testMoved = !test.getFullyQualifiedName().equals(full + "U");
				if (testMoved) {
					moveToPackage(project, test, full);
				}
			}
		}
		deleteEmptyTestDirectories(project.getTestJavaDir());
	}

	private static void deleteEmptyTestDirectories(VirtualFile file) {
		if (!file.isDirectory()) {
			return;
		}
		for (VirtualFile child : file.getChildren()) {
			deleteEmptyTestDirectories(child);
		}
		if (file.getChildren().isEmpty()) {
			file.delete();
		}
	}

	private static void moveToPackage(Project project, JavaFile test, String fullyQualifiedClass) {
		if (fullyQualifiedClass == null) {
			Console.println("Could not find class for test %", test.getFullyQualifiedName());

		} else {
			FileContents contents = test.getContents();
			JavaLine declaration = contents.getPackageDeclaration();
			String destinationPackage = fullyQualifiedClass.replaceFirst("[.][^.]++$", "");
			declaration.replace(Str.sub("package $;", destinationPackage));
			SystemFile original = contents.getFile();
			SystemFile destination = project.resolveTestFile(fullyQualifiedClass.replace('.', '/') + "U.java");
			Check.isFalse(original.getPath().equals(destination.getPath()));
			Console.println("moving $ => $", original, destination);
			contents.writeback(destination);
			original.delete();
		}
	}
}
