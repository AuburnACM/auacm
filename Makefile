nonprod:
	cd auacm/angular/ && ng build --deploy-url static/
	cp auacm/angular/dist/index.html auacm/app/templates/index.html
	rm -rf auacm/app/static/assets/
	rm -f auacm/app/static/*.bundle.js
	rm -f auacm/app/static/*.bundle.css
	rm -f auacm/app/static/*.html
	cp -r auacm/angular/dist/* auacm/app/static/
prod:
	cd auacm/angular/ && ng build --prod --deploy-url static/
	cp auacm/angular/dist/index.html auacm/app/templates/index.html
	rm -rf auacm/app/static/assets/
	rm -f auacm/app/static/*.bundle.js
	rm -f auacm/app/static/*.bundle.css
	rm -f auacm/app/static/*.html
	cp -r auacm/angular/dist/* auacm/app/static/
