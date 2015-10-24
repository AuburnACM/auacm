#!/bin/sh
echo "Setting up submissions and problems data."
mkdir ../auacm/app/data
cp data.zip ../auacm/app/data/data.zip
cd ../auacm/app/data
unzip data.zip
rm data.zip
