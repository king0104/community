const { S3Client, PutObjectCommand } = require('@aws-sdk/client-s3');
const Busboy = require('busboy');
const axios = require('axios');

// 환경 변수
const S3_BUCKET = process.env.S3_BUCKET;
const AWS_REGION = process.env.AWS_REGION || 'ap-northeast-2';
const SPRING_BOOT_API_URL = process.env.SPRING_BOOT_API_URL; // e.g., http://your-spring-boot-url/api/v1/images/metadata

// S3 클라이언트 초기화
const s3Client = new S3Client({ region: AWS_REGION });

// 허용된 이미지 확장자 및 MIME 타입
const ALLOWED_EXTENSIONS = ['jpg', 'jpeg', 'png', 'gif', 'webp'];
const ALLOWED_MIME_TYPES = ['image/jpeg', 'image/png', 'image/gif', 'image/webp'];
const MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

exports.handler = async (event) => {
    console.log('Event:', JSON.stringify(event, null, 2));

    try {
        // API Gateway에서 전달된 데이터 파싱
        const contentType = event.headers['content-type'] || event.headers['Content-Type'];

        if (!contentType || !contentType.includes('multipart/form-data')) {
            return createResponse(400, {
                error: 'Content-Type must be multipart/form-data'
            });
        }

        // multipart/form-data 파싱
        const { file, fileName, mimeType, fileSize } = await parseMultipartFormData(event, contentType);

        // 파일 유효성 검증
        const validationError = validateFile(fileName, mimeType, fileSize);
        if (validationError) {
            return createResponse(400, { error: validationError });
        }

        // S3 업로드
        const s3Key = generateS3Key(fileName);
        const s3Url = await uploadToS3(file, s3Key, mimeType);

        console.log('S3 upload successful:', s3Url);

        // Spring Boot API 호출 (메타데이터 저장)
        const metadata = {
            fileName: fileName,
            s3Key: s3Key,
            s3Url: s3Url,
            fileSize: fileSize,
            contentType: mimeType
        };

        const springBootResponse = await saveMetadataToSpringBoot(metadata);

        console.log('Metadata saved to Spring Boot:', springBootResponse);

        // 성공 응답
        return createResponse(201, {
            message: 'Image uploaded successfully',
            data: springBootResponse
        });

    } catch (error) {
        console.error('Error:', error);
        return createResponse(500, {
            error: 'Internal server error',
            message: error.message
        });
    }
};

/**
 * multipart/form-data 파싱
 */
function parseMultipartFormData(event, contentType) {
    return new Promise((resolve, reject) => {
        const busboy = Busboy({
            headers: {
                'content-type': contentType
            },
            limits: {
                fileSize: MAX_FILE_SIZE
            }
        });

        let fileBuffer = null;
        let fileName = null;
        let mimeType = null;
        let fileSize = 0;
        let fileSizeExceeded = false;

        busboy.on('file', (fieldname, file, info) => {
            fileName = info.filename;
            mimeType = info.mimeType;

            const chunks = [];

            file.on('data', (data) => {
                chunks.push(data);
                fileSize += data.length;

                if (fileSize > MAX_FILE_SIZE) {
                    fileSizeExceeded = true;
                    file.resume(); // 파일 스트림 소비
                }
            });

            file.on('end', () => {
                if (!fileSizeExceeded) {
                    fileBuffer = Buffer.concat(chunks);
                }
            });

            file.on('limit', () => {
                fileSizeExceeded = true;
            });
        });

        busboy.on('finish', () => {
            if (fileSizeExceeded) {
                reject(new Error('File size exceeds 5MB limit'));
            } else if (!fileBuffer) {
                reject(new Error('No file uploaded'));
            } else {
                resolve({ file: fileBuffer, fileName, mimeType, fileSize });
            }
        });

        busboy.on('error', (error) => {
            reject(error);
        });

        // API Gateway에서 전달된 body를 busboy에 전달
        const body = event.isBase64Encoded
            ? Buffer.from(event.body, 'base64')
            : event.body;

        busboy.write(body);
        busboy.end();
    });
}

/**
 * 파일 유효성 검증
 */
function validateFile(fileName, mimeType, fileSize) {
    if (!fileName) {
        return 'File name is required';
    }

    if (!mimeType || !ALLOWED_MIME_TYPES.includes(mimeType)) {
        return 'Invalid file type. Only image files are allowed (jpg, jpeg, png, gif, webp)';
    }

    const extension = fileName.split('.').pop().toLowerCase();
    if (!ALLOWED_EXTENSIONS.includes(extension)) {
        return 'Invalid file extension. Allowed: jpg, jpeg, png, gif, webp';
    }

    if (fileSize > MAX_FILE_SIZE) {
        return 'File size exceeds 5MB limit';
    }

    return null;
}

/**
 * S3 키 생성 (중복 방지)
 */
function generateS3Key(originalFileName) {
    const timestamp = Date.now();
    const uuid = Math.random().toString(36).substring(2, 10);
    const extension = originalFileName.substring(originalFileName.lastIndexOf('.'));

    return `profiles/${timestamp}_${uuid}${extension}`;
}

/**
 * S3에 파일 업로드
 */
async function uploadToS3(fileBuffer, s3Key, mimeType) {
    const command = new PutObjectCommand({
        Bucket: S3_BUCKET,
        Key: s3Key,
        Body: fileBuffer,
        ContentType: mimeType,
        CacheControl: 'max-age=31536000', // 1년 캐싱
        ACL: 'public-read'
    });

    await s3Client.send(command);

    // S3 URL 생성
    return `https://${S3_BUCKET}.s3.${AWS_REGION}.amazonaws.com/${s3Key}`;
}

/**
 * Spring Boot API 호출 (메타데이터 저장)
 */
async function saveMetadataToSpringBoot(metadata) {
    try {
        const response = await axios.post(SPRING_BOOT_API_URL, metadata, {
            headers: {
                'Content-Type': 'application/json'
            },
            timeout: 10000 // 10초 타임아웃
        });

        return response.data;
    } catch (error) {
        console.error('Failed to save metadata to Spring Boot:', error.message);
        if (error.response) {
            console.error('Response data:', error.response.data);
            console.error('Response status:', error.response.status);
        }
        throw new Error(`Failed to save metadata: ${error.message}`);
    }
}

/**
 * API Gateway 응답 생성
 */
function createResponse(statusCode, body) {
    return {
        statusCode: statusCode,
        headers: {
            'Content-Type': 'application/json',
            'Access-Control-Allow-Origin': '*', // CORS 설정 (필요시 수정)
            'Access-Control-Allow-Methods': 'POST, OPTIONS',
            'Access-Control-Allow-Headers': 'Content-Type'
        },
        body: JSON.stringify(body)
    };
}