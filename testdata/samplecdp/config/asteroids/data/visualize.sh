#!/bin/bash

for i in s*/*.in; do
	echo $i
	cat $i ${i%.in}.ans | asy -f png visualize.asy -o ${i%.in}.png
done
