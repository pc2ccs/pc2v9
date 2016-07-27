#!/bin/bash

for f in s*/*in; do
	echo $f
	python visualize.py $f
done
