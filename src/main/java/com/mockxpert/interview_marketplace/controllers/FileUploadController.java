package com.mockxpert.interview_marketplace.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Autowired;

import com.libralink.repository.UserRepository;
import com.mockxpert.interview_marketplace.dto.InterviewerDto;
import com.mockxpert.interview_marketplace.entities.Interviewer;
import com.mockxpert.interview_marketplace.mappers.InterviewerMapper;
import com.mockxpert.interview_marketplace.repositories.InterviewerRepository;
import com.mockxpert.interview_marketplace.services.InterviewerService;
import com.mockxpert.interview_marketplace.services.UserService;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.*;
import java.util.Collections;

import javax.imageio.ImageIO;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/api")
public class FileUploadController {

    private static final String IMAGE_DIR =
        "C:/Users/moham/Downloads/Frontend/interview-marketplace/public/images/";
    
    @Autowired
    private InterviewerService interviewerService;
    
    @Autowired
    private InterviewerRepository interviewerRepository;
    
    @Autowired
    private UserService userService;
    
    
    private InterviewerMapper interviewerMapper;
    
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
        @RequestParam("interviewerId") Long userId
    ) {
        try {
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || originalFilename.isEmpty()) {
                return ResponseEntity.badRequest().body("No file name found!");
            }
            String safeName = fullName.trim().toLowerCase().replaceAll("\\s+", "-");
            String fileName = safeName + userId + ".png";
            Path destination = Paths.get(IMAGE_DIR, fileName);
            Files.createDirectories(destination.getParent());
            BufferedImage image = ImageIO.read(file.getInputStream());
            ImageIO.write(image, "png", destination.toFile());
            String imageUrl = "/images/" + fileName;
            
            InterviewerDto interviewerDto = interviewerService.findInterviewerByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Interviewer not found"));
            interviewerDto.setProfileImage(imageUrl);
            
            System.out.println(interviewerDto);
            
            Interviewer entity = interviewerMapper.toEntity(interviewerDto);
            entity.setUser(userService.getUserByUserId(userId));
            
            System.out.println(entity);
            
            interviewerRepository.save(entity);
            
   
            
            return ResponseEntity.ok(Collections.singletonMap("url", imageUrl));
        } catch (Exception e) {
        	System.out.println(e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Failed to upload image: " + e.getMessage());
        }
    }



}
