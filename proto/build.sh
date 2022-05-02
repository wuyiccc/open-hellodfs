./protoc.exe --java_out=./ *.proto

./protoc.exe --plugin=protoc-gen-grpc-java=protoc-gen-grpc-java-0.13.2-windows-x86_64.exe --grpc-java_out=./ *.proto