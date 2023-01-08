javac $1
FILE=$1
file1=$(echo $FILE | cut -f 1 -d '.')
# echo $FILE
java $file1