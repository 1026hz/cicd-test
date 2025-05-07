package com.kakaobase.snsapp.global.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * AWS S3 관련 설정을 담당하는 Configuration 클래스
 *
 * <p>이 클래스는 AWS S3 서비스에 연결하기 위한 클라이언트 빈을 생성합니다.
 * AWS 자격 증명(AccessKey, SecretKey)과 리전 정보를 외부 설정에서 주입받아
 * AmazonS3 클라이언트를 구성합니다.</p>
 *
 * <p>주요 기능:</p>
 * <ul>
 *   <li>AWS S3 클라이언트 빈 등록</li>
 *   <li>AWS 자격 증명 설정</li>
 *   <li>AWS 리전 설정</li>
 * </ul>
 *
 * <p>application.yml 또는 application.properties에 다음 속성들이 설정되어 있어야 합니다:</p>
 * <pre>
 * cloud:
 *   aws:
 *     credentials:
 *       access-key: YOUR_ACCESS_KEY
 *       secret-key: YOUR_SECRET_KEY
 *     region:
 *       static: ap-northeast-2
 * </pre>
 */
@Configuration
public class AwsS3Config {

    /**
     * AWS 액세스 키
     * <p>AWS IAM에서 발급받은 액세스 키를 설정합니다.</p>
     */
    @Value("${cloud.aws.credentials.access-key}")
    private String accessKey;

    /**
     * AWS 시크릿 키
     * <p>AWS IAM에서 발급받은 시크릿 키를 설정합니다.</p>
     */
    @Value("${cloud.aws.credentials.secret-key}")
    private String secretKey;

    /**
     * AWS 리전 정보
     * <p>S3 버킷이 위치한 AWS 리전을 설정합니다.</p>
     * <p>한국의 경우 일반적으로 'ap-northeast-2'를 사용합니다.</p>
     */
    @Value("${cloud.aws.region.static}")
    private String region;

    /**
     * AmazonS3 클라이언트 빈을 생성합니다.
     *
     * <p>이 빈은 S3 서비스에 접근하기 위한 클라이언트 객체를 제공합니다.
     * 설정된 자격 증명과 리전 정보를 사용하여 S3 클라이언트를 구성합니다.</p>
     *
     * @return 구성된 AmazonS3 클라이언트 객체
     */
    @Bean
    public AmazonS3 amazonS3Client() {
        // AWS 자격 증명 객체 생성
        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);

        // AmazonS3 클라이언트 빌더를 사용하여 S3 클라이언트 구성 및 생성
        return AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .withRegion(Regions.fromName(region))
                .build();
    }
}