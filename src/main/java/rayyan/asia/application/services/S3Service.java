package rayyan.asia.application.services;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.web.multipart.MultipartFile;

/**
 * Abstraction for S3 storage operations.
 */
public interface S3Service {

    /**
     * Uploads the given file to storage and returns the storage key.
     *
     * @param file the multipart file to upload
     * @return the generated storage key
     * @throws IOException if an I/O error occurs while reading the file
     */
    String upload(MultipartFile file) throws IOException;

    /**
     * Upload raw bytes as an object and return the key.
     *
     * @param data bytes to upload
     * @param filename suggested filename/key
     * @param contentType content type (e.g. application/pdf)
     * @return the generated storage key
     */
    String uploadBytes(byte[] data, String filename, String contentType) throws IOException;

    /**
     * Retrieves the file as a byte array.
     *
     * @param key the storage key
     * @return file bytes
     */
    byte[] getFileBytes(String key);

    /**
     * Retrieves the file as an InputStream.
     *
     * @param key the storage key
     * @return InputStream for the file
     */
    InputStream getFileStream(String key);
}