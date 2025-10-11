package rayyan.asia.application.services.storage;

import java.io.IOException;
import java.io.InputStream;

/**
 * Abstraction for S3 storage operations.
 */
public interface S3Service {


    /**
     * Upload raw bytes as an object and return the key.
     *
     * @param data bytes to upload
     * @param filename suggested filename/key
     * @param contentType content type (e.g. application/pdf)
     * @return the generated storage key
     */
    String uploadBytes(byte[] data, String filename, String contentType) throws IOException;

    String getFileUrl(String key);
}