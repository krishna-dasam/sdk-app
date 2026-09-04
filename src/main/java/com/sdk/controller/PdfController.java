package com.sdk.controller;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/pdf")
public class PdfController {

	private final Path uploadDir = Paths.get("uploads").toAbsolutePath().normalize();

	public PdfController() throws IOException {
		Files.createDirectories(uploadDir);
	}

	@GetMapping("/list")
	public List<String> list() throws IOException {
		try (Stream<Path> paths = Files.list(uploadDir)) {
			return paths
					.filter(Files::isRegularFile)
					.map(path -> path.getFileName().toString())
					.filter(name -> name.endsWith(".pdf"))
					.sorted()
					.toList();
		}
	}

	@GetMapping("/download/{filename:.+}")
	public ResponseEntity<Resource> download(@PathVariable String filename) throws MalformedURLException {
		Path file = resolveSafePath(filename);
		Resource resource = new UrlResource(file.toUri());
		if (!resource.exists() || !resource.isReadable()) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok()
				.contentType(MediaType.APPLICATION_PDF)
				.header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
				.body(resource);
	}

	@PostMapping("/upload")
	public String upload(@RequestParam("file") MultipartFile file) throws IOException {
		String originalFilename = file.getOriginalFilename();
		if (originalFilename == null || originalFilename.isBlank()) {
			throw new IllegalArgumentException("A file name is required");
		}
		Path destination = resolveSafePath(originalFilename);
		Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
		return "Uploaded " + destination.getFileName();
	}

	private Path resolveSafePath(String filename) {
		Path resolved = uploadDir.resolve(filename).normalize();
		if (!resolved.startsWith(uploadDir) || filename.contains("..") || filename.contains("/")
				|| filename.contains("\\")) {
			throw new IllegalArgumentException("Invalid filename: " + filename);
		}
		return resolved;
	}
}
