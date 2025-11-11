package com.cloud.jml.utils.aws;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "cloud.aws")
public class S3Properties {

    private String bucket = "elasticbeanstalk-logos-us-east-1-536322508385";
    private String region = "us-east-1";
    private String accessKey;
    private String secretKey;
}
