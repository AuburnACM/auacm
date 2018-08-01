ifndef VERBOSE
.SILENT:
endif

lint: nglint pylint

nglint:
	echo "Linting Angular..."
	cd src/main/angular/ && ng lint --type-check

pylint:
	echo "Linting Python"
	pylint --load-plugins=pylint_flask auacm/app

nonprod: build_nonprod clean mvn
	echo "Build complete"

prod: build_prod clean mvn
	echo "Build complete"

build_nonprod:
	cd src/main/angular/ && ng build

build_prod:
	cd src/main/angular/ && ng build --prod

mvn:
	mvn clean package -DskipTests=true

test:
	mvn clean test

proto:
	protoc --java_out=src/main/java/ src/main/resources/proto/Blog.proto
	protoc --java_out=src/main/java/ src/main/resources/proto/Competition.proto
	protoc --java_out=src/main/java/ src/main/resources/proto/Problem.proto
	protoc --java_out=src/main/java/ src/main/resources/proto/Profile.proto
	protoc --java_out=src/main/java/ src/main/resources/proto/Submission.proto
	protoc --java_out=src/main/java/ src/main/resources/proto/User.proto

clean:
	cp src/main/angular/dist/index.html auacm/app/templates/index.html
	rm -rf src/main/resources/public/
	rm -rf auacm/app/static/assets/
	rm -f auacm/app/static/*.bundle.js
	rm -f auacm/app/static/*.bundle.css
	rm -f auacm/app/static/*.html
	rm -f auacm/app/static/*.bundle.js.map
	mkdir src/main/resources/public/
	cp -r src/main/angular/dist/* src/main/resources/public/
