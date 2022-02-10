#!/bin/sh

./build-site.sh

# setup slides/reveal.js framework
mkdir site/EngineeringCulture
cd site/EngineeringCulture
git clone https://github.tools.sap/EngineeringCulture/reveal.js.git
git clone https://github.tools.sap/EngineeringCulture/slides.git
git clone https://github.tools.sap/EngineeringCulture/reveal-plugin-tags.git
git clone https://github.tools.sap/EngineeringCulture/reveal-plugin-external-resources.git
cd slides
git clone https://github.tools.sap/EngineeringCulture/reveal.js.git
ln -s docs/* .
cd ../reveal-plugin-tags
ln -s docs/* .
cd ../reveal-plugin-external-resources
ln -s docs/* .
cd ../../..

npx http-server -c-1 site
