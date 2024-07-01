###PROBLEMSET_YAML=problemset.yaml
###PROBLEMSET_YAML_PATH=${PC2_CDP}/${PROBLEMSET_YAML}

###declare -A problet_to_name

###ParseProblemYaml()
###{
###	CURDIR="$PWD"
###	tmpdir=/tmp/probset$$
###	mkdir $tmpdir
###	# Need a copy since web doesnt have access to full path
###	cp ${PROBLEMSET_YAML_PATH} $tmpdir
###	cd $tmpdir
###	csplit --prefix="x$USER" -q ${PROBLEMSET_YAML} "/^  *- /" "{*}"
###	for file in $(echo "x$USER"*)
###	do
###		letter=`sed -n -e 's/^  *letter: \([A-Z]\).*/\1/p' < $file`
###		short=`sed -n -e 's/.* short-name: \(.*\)$/\1/p' < $file`
###		if test -n "$letter" -a -n "$short"
###		then
###			problet_to_name[$letter]="$short"
###		fi
###	done
###	cd $CURDIR
###	rm -r $tmpdir
###}
