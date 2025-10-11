package rayyan.asia.application.services.storage;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.Duration;
import java.util.NoSuchElementException;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

@Service
@RequiredArgsConstructor
public class S3ServiceImpl implements S3Service {

    public static final int EXPIRATION_IN_MINUTES = 15;
    private static final Logger LOG = LoggerFactory.getLogger(S3ServiceImpl.class);

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    @Value("${aws.s3.bucket}")
    private String bucket;

    @Value("${aws.endpoint:http://localhost:4566}")
    private String awsEndpoint;


    public String uploadBytes(byte[] data, String filename, String contentType) {
        String key = UUID.randomUUID() + "_" + filename;

        PutObjectRequest putReq = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(contentType)
                .build();

        s3Client.putObject(putReq, RequestBody.fromBytes(data));
        return key;
    }

    @Override
    public String getFileUrl(String key) {
        if (key == null || key.isBlank()) {
            throw new NoSuchElementException("s3 key was not provided: " + key );
        }

        var presignedUri = getPresignedUri(key);

        return getParsedUrl(key, presignedUri);
    }

    private URI getPresignedUri(String s3Key) {
        if (bucket == null || bucket.isBlank()) {
            throw new IllegalStateException("aws.s3.bucket is not configured");
        }

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(s3Key)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(EXPIRATION_IN_MINUTES))
                .getObjectRequest(getObjectRequest)
                .build();

        var presignedResp = s3Presigner.presignGetObject(presignRequest);
        URL presignedUrl = presignedResp.url();

        try {
            return presignedUrl.toURI();
        } catch (URISyntaxException e) {
            return URI.create(presignedUrl.toString());
        }
    }


    private String getParsedUrl(String s3Key, URI presignedUri) {
        var query = presignedUri.getRawQuery();
        try {
            URI endpointUri = new URI(awsEndpoint);
            String scheme = endpointUri.getScheme() != null ? endpointUri.getScheme() : "http";
            String host = endpointUri.getHost();
            int port = endpointUri.getPort();
            String hostPort = host + (port == -1 ? "" : ":" + port);
            String base = scheme + "://" + hostPort;
            String path = "/" + bucket + "/" + s3Key;
            return base + path + (query != null && !query.isBlank() ? "?" + query : "");
        } catch (URISyntaxException e) {
            LOG.warn("aws.endpoint is not a valid URI ({}), falling back to presigned URL: {}", awsEndpoint, presignedUri);
            return presignedUri.toString();
        }
    }


}
