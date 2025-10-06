package rayyan.asia.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Component
public class S3BucketInitializer {

    private static final Logger LOG = LoggerFactory.getLogger(S3BucketInitializer.class);

    private final S3Client s3Client;

    @Value("${aws.s3.bucket:}")
    private String bucket;

    public S3BucketInitializer(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void ensureBucketExists() {
        if (bucket == null || bucket.isBlank()) {
            LOG.warn("No aws.s3.bucket configured; skipping S3 bucket initialization");
            return;
        }

        try {
            LOG.info("Checking S3 bucket '{}'", bucket);
            HeadBucketRequest head = HeadBucketRequest.builder().bucket(bucket).build();
            s3Client.headBucket(head);
            LOG.info("S3 bucket '{}' already exists", bucket);
        } catch (S3Exception e) {
            // If the bucket does not exist, create it
            if (e.statusCode() == 404 || "NotFound".equals(e.awsErrorDetails() != null ? e.awsErrorDetails().errorCode() : null)) {
                try {
                    LOG.info("S3 bucket '{}' not found; creating...", bucket);
                    CreateBucketRequest create = CreateBucketRequest.builder().bucket(bucket).build();
                    s3Client.createBucket(create);
                    LOG.info("S3 bucket '{}' created", bucket);
                } catch (Exception ex) {
                    LOG.error("Failed to create S3 bucket '{}': {}", bucket, ex.getMessage(), ex);
                }
            } else {
                LOG.warn("Error while checking S3 bucket '{}': {}", bucket, e.awsErrorDetails() != null ? e.awsErrorDetails().errorMessage() : e.getMessage());
            }
        } catch (Exception ex) {
            LOG.warn("Exception while checking/creating S3 bucket '{}': {}", bucket, ex.getMessage());
        }
    }
}

