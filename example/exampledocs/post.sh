#!/bin/sh
FILES=$*
URL=http://localhost:8983/solr/update

for f in $FILES; do
  echo Posting file $f to $URL
  curl $URL --data-binary @$f -H 'Content-type:text/xml; charset=utf-8' 
  echo
done

#send the commit command to make sure all the changes are flushed and visible
curl $URL --data-binary '<commit/>'
echo
