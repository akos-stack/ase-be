package com.bloxico.ase.testutil;

import com.amazonaws.services.s3.AmazonS3;
import io.findify.s3mock.S3Mock;
import org.junit.After;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

/**
 * This class contains before and after methods for setting up amazon s3 mock.
 * Every test class that has interactions with amazon s3 should extend it.
 */
public abstract class AbstractSpringTestWithAWS extends AbstractSpringTest {

    @Autowired
    protected AmazonS3 amazonS3;

    @Value("${s3.bucketName}")
    protected String bucketName;

    @Value("${s3.port}")
    protected int port;

    private S3Mock api;

    @Before
    public void setUpAmazonS3Mock() {
        api = new S3Mock.Builder().withPort(port).withInMemoryBackend().build();
        api.start();
        amazonS3.createBucket(bucketName);
    }

    @After
    public void teardownAmazonS3Mock() {
        api.shutdown();
    }

}
