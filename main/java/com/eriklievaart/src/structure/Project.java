package com.eriklievaart.src.structure;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.eriklievaart.src.scanner.JavaFile;
import com.eriklievaart.toolkit.io.api.UrlTool;
import com.eriklievaart.toolkit.vfs.api.file.SystemFile;
import com.eriklievaart.toolkit.vfs.api.file.VirtualFile;

public class Project {
	private static final String JAVA = "java";

	private String name;

	public Project(String name) {
		this.name = name;
	}

	private SystemFile getGitDir() {
		String home = System.getProperty("user.home");
		return new SystemFile(UrlTool.append(home, "Development/git", name));
	}

	public SystemFile getMainJavaDir() {
		return getGitDir().resolve("main/java");
	}

	public SystemFile getTestJavaDir() {
		return getGitDir().resolve("test/java");
	}

	public SystemFile resolveTestFile(String replace) {
		return getTestJavaDir().resolve(replace);
	}

	public List<JavaFile> getMainJavaFiles() {
		return toJavaFiles(getMainJavaDir().scan(JAVA));
	}

	public List<JavaFile> getTestJavaFiles() {
		return toJavaFiles(getTestJavaDir().scan(JAVA));
	}

	public List<JavaFile> getAllJavaFiles() {
		return toJavaFiles(getGitDir().scan(JAVA));
	}

	private List<JavaFile> toJavaFiles(Iterable<VirtualFile> sources) {
		Stream<VirtualFile> stream = StreamSupport.stream(sources.spliterator(), false);
		return stream.map(JavaFile::new).collect(Collectors.toList());
	}
}
