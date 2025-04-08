package com.mockxpert.interview_marketplace.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.Collections;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/api")
public class FileUploadController {

    // Absolute path to your Next.js public/images folder
    private static final String IMAGE_DIR =
        "C:/Users/moham/Downloads/Frontend/interview-marketplace/public/images/";

    /**
     * 
     * Uploads the image inside the /public/images folder.
     * 
     * @param file
     * @param fullName
     * @param interviewerId
     * @return
     */
    @PostMapping("/upload-profile-pic")
    public ResponseEntity<?> uploadProfilePic(
        @RequestParam("file") MultipartFile file,
        @RequestParam("fullName") String fullName,
        @RequestParam("interviewerId") Long interviewerId
    ) {
        try {
            // 1) Validate the file
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || originalFilename.isEmpty()) {
                return ResponseEntity.badRequest().body("No file name found!");
            }
            // e.g. .jpg, .png
            String ext = originalFilename.substring(originalFilename.lastIndexOf('.') + 1);

            // 2) Build final file name: e.g. "mohammad-ali10.jpg"
            String safeName = fullName.trim().toLowerCase().replaceAll("\\s+", "-");
            String fileName = safeName + interviewerId + "." + ext;

            // 3) Ensure the folder exists
            Path destination = Paths.get(IMAGE_DIR, fileName);
            Files.createDirectories(destination.getParent());

            // 4) Save file physically
            Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);

            // 5) Return the relative path that Next.js can serve
            String imageUrl = "/images/" + fileName;

            return ResponseEntity.ok(Collections.singletonMap("url", imageUrl));

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Failed to upload image: " + e.getMessage());
        }
    }
}
