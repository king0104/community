const { S3Client, PutObjectCommand } = require('@aws-sdk/client-s3');
const Busboy = require('busboy');
const axios = require('axios');

// ============================================
// 환경 변수
// ============================================
const S3_BUCKET = process.env.S3_BUCKET;
const AWS_REGION = process.env.AWS_REGION || 'ap-northeast-2';
const SPRING_BOOT_API_URL = process.env.SPRING_BOOT_API_URL;

// S3 클라이언트 초기화
const s3Client = new S3Client({ region: AWS_REGION });

// ============================================
// 상수 정의
// ============================================
const ALLOWED_EXTENSIONS = ['jpg', 'jpeg', 'png', 'gif', 'webp'];
const ALLOWED_MIME_TYPES = ['image/jpeg', 'image/png', 'image/gif', 'image/webp'];
const MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

// ============================================
// Lambda Handler (메인 함수)
// ============================================
exports.handler = async (event) => {
    console.log('🔵 Lambda 함수 시작');
    console.log('Event:', JSON.stringify(event, null, 2));

    try {
        // ----------------------------------------
        // STEP 1: 환경 변수 검증
        // ----------------------------------------
        if (!S3_BUCKET) {
            throw new Error('❌ S3_BUCKET environment variable is not set');
        }

        if (!SPRING_BOOT_API_URL) {
            throw new Error('❌ SPRING_BOOT_API_URL environment variable is not set');
        }

        console.log('✅ 환경 변수 검증 완료');
        console.log('S3_BUCKET:', S3_BUCKET);
        console.log('SPRING_BOOT_API_URL:', SPRING_BOOT_API_URL);

        // ----------------------------------------
        // STEP 2: Content-Type 검증
        // ----------------------------------------
        const contentType = event.headers['content-type'] || event.headers['Content-Type'];

        if (!contentType || !contentType.includes('multipart/form-data')) {
            return createResponse(400, {
                error: 'Content-Type must be multipart/form-data'
            });
        }

        console.log('✅ Content-Type 검증 완료:', contentType);

        // ----------------------------------------
        // STEP 3: multipart/form-data 파싱
        // ----------------------------------------
        console.log('📦 파일 파싱 중...');
        const { file, fileName, mimeType, fileSize } = await parseMultipartFormData(event, contentType);

        console.log('✅ 파일 파싱 완료:');
        console.log('  - 파일명:', fileName);
        console.log('  - MIME 타입:', mimeType);
        console.log('  - 파일 크기:', fileSize, 'bytes');

        // ----------------------------------------
        // STEP 4: 파일 유효성 검증
        // ----------------------------------------
        const validationError = validateFile(fileName, mimeType, fileSize);
        if (validationError) {
            console.error('❌ 파일 검증 실패:', validationError);
            return createResponse(400, { error: validationError });
        }

        console.log('✅ 파일 유효성 검증 완료');

        // ----------------------------------------
        // STEP 5: S3 업로드
        // ----------------------------------------
        console.log('📤 S3 업로드 시작...');
        const s3Key = generateS3Key(fileName);
        const s3Url = await uploadToS3(file, s3Key, mimeType);

        console.log('✅ S3 업로드 성공:');
        console.log('  - S3 Key:', s3Key);
        console.log('  - S3 URL:', s3Url);

        // ----------------------------------------
        // STEP 6: Spring Boot 메타데이터 저장 (필수!)
        // ----------------------------------------
        console.log('📤 Spring Boot 메타데이터 저장 중...');

        const metadata = {
            fileName: fileName,
            s3Key: s3Key,
            s3Url: s3Url,
            fileSize: fileSize,
            contentType: mimeType
        };

        console.log('메타데이터:', JSON.stringify(metadata, null, 2));

        const springBootResponse = await saveMetadataToSpringBoot(metadata);

        console.log('✅ Spring Boot 메타데이터 저장 완료:');
        console.log('응답:', JSON.stringify(springBootResponse, null, 2));

        // ----------------------------------------
        // STEP 7: imageId 추출 및 검증
        // ----------------------------------------
        const imageId = springBootResponse.imageId;

        if (!imageId) {
            throw new Error('❌ Spring Boot 응답에 imageId가 없습니다');
        }

        console.log('✅ imageId 추출 완료:', imageId);

        // ----------------------------------------
        // STEP 8: 성공 응답 반환
        // ----------------------------------------
        const successResponse = {
            message: 'Image uploaded successfully',
            imageId: imageId,           // 👈 프론트엔드가 필요한 imageId
            imageUrl: s3Url,            // 👈 프론트엔드가 기대하는 이름 (s3Url → imageUrl)
            s3Key: s3Key,
            fileName: fileName,
            fileSize: fileSize
        };

        console.log('🎉 Lambda 함수 성공 완료');
        console.log('응답:', JSON.stringify(successResponse, null, 2));

        return createResponse(201, successResponse);

    } catch (error) {
        // ----------------------------------------
        // 에러 처리
        // ----------------------------------------
        console.error('❌ Lambda 함수 에러 발생:');
        console.error('에러 메시지:', error.message);
        console.error('에러 스택:', error.stack);

        return createResponse(500, {
            error: 'Internal server error',
            message: error.message
        });
    }
};

// ============================================
// multipart/form-data 파싱 함수
// ============================================
function parseMultipartFormData(event, contentType) {
    return new Promise((resolve, reject) => {
        const busboy = Busboy({
            headers: {
                'content-type': contentType
            }
        });

        let fileBuffer = null;
        let fileName = null;
        let mimeType = null;
        let fileSize = 0;

        // 파일 데이터 수신
        busboy.on('file', (fieldname, file, info) => {
            console.log('📥 파일 수신 중:', info.filename);

            fileName = info.filename;
            mimeType = info.mimeType;

            const chunks = [];

            file.on('data', (chunk) => {
                chunks.push(chunk);
                fileSize += chunk.length;
            });

            file.on('end', () => {
                fileBuffer = Buffer.concat(chunks);
                console.log('✅ 파일 수신 완료');
            });
        });

        // 파싱 완료
        busboy.on('finish', () => {
            if (!fileBuffer) {
                reject(new Error('No file uploaded'));
                return;
            }

            resolve({
                file: fileBuffer,
                fileName: fileName,
                mimeType: mimeType,
                fileSize: fileSize
            });
        });

        // 에러 처리
        busboy.on('error', (error) => {
            console.error('Busboy 파싱 에러:', error);
            reject(error);
        });

        // API Gateway에서 전달된 body 처리
        const body = event.isBase64Encoded
            ? Buffer.from(event.body, 'base64')
            : event.body;

        busboy.write(body);
        busboy.end();
    });
}

// ============================================
// 파일 유효성 검증 함수
// ============================================
function validateFile(fileName, mimeType, fileSize) {
    // MIME 타입 검증
    if (!ALLOWED_MIME_TYPES.includes(mimeType)) {
        return `Invalid MIME type: ${mimeType}. Allowed: ${ALLOWED_MIME_TYPES.join(', ')}`;
    }

    // 확장자 검증
    const extension = fileName.split('.').pop().toLowerCase();
    if (!ALLOWED_EXTENSIONS.includes(extension)) {
        return `Invalid file extension: ${extension}. Allowed: ${ALLOWED_EXTENSIONS.join(', ')}`;
    }

    // 파일 크기 검증
    if (fileSize > MAX_FILE_SIZE) {
        return `File size ${fileSize} bytes exceeds 5MB limit`;
    }

    return null;
}

// ============================================
// S3 키 생성 함수 (중복 방지)
// ============================================
function generateS3Key(originalFileName) {
    const timestamp = Date.now();
    const uuid = Math.random().toString(36).substring(2, 10);
    const extension = originalFileName.substring(originalFileName.lastIndexOf('.'));

    return `profiles/${timestamp}_${uuid}${extension}`;
}

// ============================================
// S3 업로드 함수
// ============================================
async function uploadToS3(fileBuffer, s3Key, mimeType) {
    try {
        const command = new PutObjectCommand({
            Bucket: S3_BUCKET,
            Key: s3Key,
            Body: fileBuffer,
            ContentType: mimeType,
            CacheControl: 'max-age=31536000' // 1년 캐싱
        });

        await s3Client.send(command);

        // S3 URL 생성
        return `https://${S3_BUCKET}.s3.${AWS_REGION}.amazonaws.com/${s3Key}`;

    } catch (error) {
        console.error('❌ S3 업로드 에러:', error);
        throw new Error(`S3 upload failed: ${error.message}`);
    }
}

// ============================================
// Spring Boot API 호출 (메타데이터 저장)
// ============================================
async function saveMetadataToSpringBoot(metadata) {
    try {
        console.log('📤 Spring Boot API 호출:', SPRING_BOOT_API_URL);

        const response = await axios.post(SPRING_BOOT_API_URL, metadata, {
            headers: {
                'Content-Type': 'application/json'
            },
            timeout: 10000 // 10초 타임아웃
        });

        console.log('✅ Spring Boot 응답 상태:', response.status);
        console.log('✅ Spring Boot 응답 데이터:', JSON.stringify(response.data, null, 2));

        return response.data;

    } catch (error) {
        console.error('❌ Spring Boot API 호출 실패');
        console.error('에러 메시지:', error.message);

        if (error.response) {
            console.error('응답 상태:', error.response.status);
            console.error('응답 데이터:', JSON.stringify(error.response.data, null, 2));
        }

        // 에러를 그대로 던져서 Lambda가 실패하도록 함
        throw new Error(`Failed to save metadata to Spring Boot: ${error.message}`);
    }
}

// ============================================
// API Gateway 응답 생성 함수
// ============================================
function createResponse(statusCode, body) {
    return {
        statusCode: statusCode,
        headers: {
            'Content-Type': 'application/json',
            'Access-Control-Allow-Origin': '*',  // CORS (프로덕션에서는 특정 도메인으로 변경)
            'Access-Control-Allow-Methods': 'POST, OPTIONS',
            'Access-Control-Allow-Headers': 'Content-Type'
        },
        body: JSON.stringify(body)
    };
}