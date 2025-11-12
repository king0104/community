#!/bin/bash

# Lambda 함수 배포 스크립트

echo "Installing dependencies..."
npm install

echo "Creating deployment package..."
zip -r function.zip index.js node_modules/ package.json

echo "✅ Deployment package created: function.zip"
echo ""
echo "Next steps:"
echo "1. Upload function.zip to AWS Lambda console"
echo "2. Or use AWS CLI:"
echo "   aws lambda update-function-code --function-name image-upload-lambda --zip-file fileb://function.zip"