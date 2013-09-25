#!/bin/sh

# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

###

# Prepares an RC of the Solr Ref Guide by doing local file operations to:
#  - create a directory for the RC files
#  - move the PDF files into the RC directory with the appropriate name
#  - generate a SHA1 of the PDF file
#  - GPG sign the PDF files
#
# See: https://cwiki.apache.org/confluence/display/solr/Internal+-+How+To+Publish+This+Documentation

if [ $# -lt 2 ] || [ 3 -lt $# ] ; then
    echo "Usage: $0 <exported-file.pdf> <X.Y-RCZ> [gpg-pubkey-id]"
    echo ""
    echo "Examples: "
    echo "    $0 solr-123456-7890-6543.pdf 4.5-RC0"
    echo "or  $0 solr-123456-7890-6543.pdf 4.5-RC0 DEADBEEF"
    echo ""
    echo "If no GPG key ID is specified, GPG will use your default key"
    exit 1;
fi

PREFIX="apache-solr-ref-guide"
SRC_FILE=$1
DIR="$PREFIX-$2"
PDF="$DIR/$PREFIX-$2.pdf"
SHA="$PDF.sha1"
GPG="$PDF.asc"

if [ ! -e $SRC_FILE ] ; then
   echo "! ! ! Can't proceed, file does not exist: $SRC_FILE"
   exit 1;
fi

if [ -d $DIR ] ; then
   echo "! ! ! Can't proceed, directory already exists: $DIR"
   exit 1;
fi

GPG_ID_ARG=""
if [ ! -z "$3" ] ; then
  GPG_ID_ARG="-u $3"
fi

# from here on, use set -x to echo progress and rely on decent error messages
# from shell commands that might fail.
 
set -x

mkdir $DIR || exit 1
mv $SRC_FILE $PDF || exit 1
sha1sum $PDF > $SHA || exit 1
gpg $GPG_ID_ARG --armor --output $GPG --detach-sig $PDF|| exit 1

