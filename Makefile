ifndef VERBOSE
.SILENT:
endif

lint: nglint pylint

nglint:
	echo "Linting Angular..."
	cd auacm/angular/ && ng lint --type-check

pylint:
	echo "Linting Python"
	pylint --load-plugins=pylint_flask auacm/app

nonprod: build_nonprod clean mvn
	echo "Build complete"

prod: build_prod clean mvn
	echo "Build complete"

build_nonprod:
	cd auacm/angular/ && ng build

build_prod:
	cd auacm/angular/ && ng build --prod

mvn:
	mvn clean package

clean:
	cp auacm/angular/dist/index.html auacm/app/templates/index.html
	rm -rf src/main/resources/public/
	rm -rf auacm/app/static/assets/
	rm -f auacm/app/static/*.bundle.js
	rm -f auacm/app/static/*.bundle.css
	rm -f auacm/app/static/*.html
	rm -f auacm/app/static/*.bundle.js.map
	mkdir src/main/resources/public/
	cp -r auacm/angular/dist/* src/main/resources/public/
