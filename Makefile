ifndef VERBOSE
.SILENT:
endif

lint:
	echo "Linting Angular..."
	cd auacm/angular/ && ng lint
	echo "Linting Python"
	pylint --load-plugins=pylint_flask auacm/app

nonprod: build_nonprod clean
	echo "Build complete"

prod: build_prod clean
	echo "Build complete"

build_nonprod:
	cd auacm/angular/ && ng build --deploy-url static/

build_prod:
	cd auacm/angular/ && ng build --prod --deploy-url static/
	
clean:
	cp auacm/angular/dist/index.html auacm/app/templates/index.html
	rm -rf auacm/app/static/assets/
	rm -f auacm/app/static/*.bundle.js
	rm -f auacm/app/static/*.bundle.css
	rm -f auacm/app/static/*.html
	rm -f auacm/app/static/*.bundle.js.map
	cp -r auacm/angular/dist/* auacm/app/static/
