Create the vulcanized webjar

* Open console in the vulcanize-polymer-src folder
* install bower: npm install -g bower
* install vulcanize: npm install -g vulcanize
* download elements: bower install --save
* create folder: mkdir -p META-INF/resources/webjars
* run vulcanize: vulcanize -o META-INF/resources/webjars/vulcanized-polymer.html elements.html
* Zip the META-INF Folder and rename the META-INF.zip file to vulcanized-polymer.jar
* Move the jar into the web/lib folder