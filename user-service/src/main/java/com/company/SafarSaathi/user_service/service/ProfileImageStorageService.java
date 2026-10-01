package com.company.SafarSaathi.user_service.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ProfileImageStorageService {

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/webp"
    );

    private final Cloudinary cloudinary;

    public String uploadProfileImage(MultipartFile file) throws IOException {

        validateFile(file);

        Map<?, ?> uploadResult = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap(
                        "folder", "safarsaathi/profile-photos",
                        "resource_type", "image"
                )
        );

        Object secureUrl = uploadResult.get("secure_url");

        if (secureUrl == null) {
            throw new IOException("Cloudinary did not return a secure URL");
        }

        return secureUrl.toString();
    }

    private void validateFile(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Profile image cannot be empty");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException(
                    "Profile image must not exceed 5 MB"
            );
        }

        String contentType = file.getContentType();

        if (contentType == null ||
                !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new IllegalArgumentException(
                    "Only JPEG, PNG, and WebP images are allowed"
            );
        }
    }
}
