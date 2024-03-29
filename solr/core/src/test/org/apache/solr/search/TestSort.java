begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.search
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|invoke
operator|.
name|MethodHandles
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|core
operator|.
name|SimpleAnalyzer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|Document
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|Field
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|StringField
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|DirectoryReader
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|IndexWriter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|IndexWriterConfig
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|LeafReaderContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|Collector
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|DocIdSet
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|FilterCollector
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|FilterLeafCollector
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|IndexSearcher
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|LeafCollector
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|ScoreDoc
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|Sort
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|SortField
operator|.
name|Type
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|SortField
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|TopDocs
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|TopFieldCollector
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|Directory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|RAMDirectory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|BitDocIdSet
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|Bits
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|FixedBitSet
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|TestUtil
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|SolrTestCaseJ4
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|request
operator|.
name|SolrQueryRequest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|schema
operator|.
name|SchemaField
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|uninverting
operator|.
name|UninvertingReader
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_class
DECL|class|TestSort
specifier|public
class|class
name|TestSort
extends|extends
name|SolrTestCaseJ4
block|{
DECL|field|log
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|MethodHandles
operator|.
name|lookup
argument_list|()
operator|.
name|lookupClass
argument_list|()
argument_list|)
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|beforeClass
specifier|public
specifier|static
name|void
name|beforeClass
parameter_list|()
throws|throws
name|Exception
block|{
name|initCore
argument_list|(
literal|"solrconfig.xml"
argument_list|,
literal|"schema-minimal.xml"
argument_list|)
expr_stmt|;
block|}
DECL|field|r
name|Random
name|r
decl_stmt|;
DECL|field|ndocs
name|int
name|ndocs
init|=
literal|77
decl_stmt|;
DECL|field|iter
name|int
name|iter
init|=
literal|50
decl_stmt|;
DECL|field|qiter
name|int
name|qiter
init|=
literal|1000
decl_stmt|;
DECL|field|commitCount
name|int
name|commitCount
init|=
name|ndocs
operator|/
literal|5
operator|+
literal|1
decl_stmt|;
DECL|field|maxval
name|int
name|maxval
init|=
name|ndocs
operator|*
literal|2
decl_stmt|;
annotation|@
name|Override
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|r
operator|=
name|random
argument_list|()
expr_stmt|;
block|}
DECL|class|MyDoc
specifier|static
class|class
name|MyDoc
block|{
DECL|field|doc
name|int
name|doc
decl_stmt|;
DECL|field|val
name|String
name|val
decl_stmt|;
DECL|field|val2
name|String
name|val2
decl_stmt|;
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"{id="
operator|+
name|doc
operator|+
literal|" val1="
operator|+
name|val
operator|+
literal|" val2="
operator|+
name|val2
operator|+
literal|"}"
return|;
block|}
block|}
DECL|method|testRandomFieldNameSorts
specifier|public
name|void
name|testRandomFieldNameSorts
parameter_list|()
throws|throws
name|Exception
block|{
name|SolrQueryRequest
name|req
init|=
name|lrf
operator|.
name|makeRequest
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|)
decl_stmt|;
specifier|final
name|int
name|iters
init|=
name|atLeast
argument_list|(
literal|5000
argument_list|)
decl_stmt|;
comment|// infinite loop abort when trying to generate a non-blank sort "name"
specifier|final
name|int
name|nonBlankAttempts
init|=
literal|37
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|iters
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|StringBuilder
name|input
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
specifier|final
name|String
index|[]
name|names
init|=
operator|new
name|String
index|[
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|r
argument_list|,
literal|1
argument_list|,
literal|10
argument_list|)
index|]
decl_stmt|;
specifier|final
name|boolean
index|[]
name|reverse
init|=
operator|new
name|boolean
index|[
name|names
operator|.
name|length
index|]
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|names
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|names
index|[
name|j
index|]
operator|=
literal|null
expr_stmt|;
for|for
control|(
name|int
name|k
init|=
literal|0
init|;
name|k
operator|<
name|nonBlankAttempts
operator|&&
literal|null
operator|==
name|names
index|[
name|j
index|]
condition|;
name|k
operator|++
control|)
block|{
name|names
index|[
name|j
index|]
operator|=
name|TestUtil
operator|.
name|randomRealisticUnicodeString
argument_list|(
name|r
argument_list|,
literal|1
argument_list|,
literal|100
argument_list|)
expr_stmt|;
comment|// munge anything that might make this a function
name|names
index|[
name|j
index|]
operator|=
name|names
index|[
name|j
index|]
operator|.
name|replaceFirst
argument_list|(
literal|"\\{"
argument_list|,
literal|"\\{\\{"
argument_list|)
expr_stmt|;
name|names
index|[
name|j
index|]
operator|=
name|names
index|[
name|j
index|]
operator|.
name|replaceFirst
argument_list|(
literal|"\\("
argument_list|,
literal|"\\(\\("
argument_list|)
expr_stmt|;
name|names
index|[
name|j
index|]
operator|=
name|names
index|[
name|j
index|]
operator|.
name|replaceFirst
argument_list|(
literal|"(\\\"|\\')"
argument_list|,
literal|"$1$1z"
argument_list|)
expr_stmt|;
name|names
index|[
name|j
index|]
operator|=
name|names
index|[
name|j
index|]
operator|.
name|replaceFirst
argument_list|(
literal|"(\\d)"
argument_list|,
literal|"$1x"
argument_list|)
expr_stmt|;
comment|// eliminate pesky problem chars
name|names
index|[
name|j
index|]
operator|=
name|names
index|[
name|j
index|]
operator|.
name|replaceAll
argument_list|(
literal|"\\p{Cntrl}|\\p{javaWhitespace}"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
if|if
condition|(
literal|0
operator|==
name|names
index|[
name|j
index|]
operator|.
name|length
argument_list|()
condition|)
block|{
name|names
index|[
name|j
index|]
operator|=
literal|null
expr_stmt|;
block|}
block|}
comment|// with luck this bad, never go to vegas
comment|// alternatively: if (null == names[j]) names[j] = "never_go_to_vegas";
name|assertNotNull
argument_list|(
literal|"Unable to generate a (non-blank) names["
operator|+
name|j
operator|+
literal|"] after "
operator|+
name|nonBlankAttempts
operator|+
literal|" attempts"
argument_list|,
name|names
index|[
name|j
index|]
argument_list|)
expr_stmt|;
name|reverse
index|[
name|j
index|]
operator|=
name|r
operator|.
name|nextBoolean
argument_list|()
expr_stmt|;
name|input
operator|.
name|append
argument_list|(
name|r
operator|.
name|nextBoolean
argument_list|()
condition|?
literal|" "
else|:
literal|""
argument_list|)
expr_stmt|;
name|input
operator|.
name|append
argument_list|(
name|names
index|[
name|j
index|]
argument_list|)
expr_stmt|;
name|input
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
name|input
operator|.
name|append
argument_list|(
name|reverse
index|[
name|j
index|]
condition|?
literal|"desc,"
else|:
literal|"asc,"
argument_list|)
expr_stmt|;
block|}
name|input
operator|.
name|deleteCharAt
argument_list|(
name|input
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
name|SortField
index|[]
name|sorts
init|=
literal|null
decl_stmt|;
name|List
argument_list|<
name|SchemaField
argument_list|>
name|fields
init|=
literal|null
decl_stmt|;
try|try
block|{
name|SortSpec
name|spec
init|=
name|SortSpecParsing
operator|.
name|parseSortSpec
argument_list|(
name|input
operator|.
name|toString
argument_list|()
argument_list|,
name|req
argument_list|)
decl_stmt|;
name|sorts
operator|=
name|spec
operator|.
name|getSort
argument_list|()
operator|.
name|getSort
argument_list|()
expr_stmt|;
name|fields
operator|=
name|spec
operator|.
name|getSchemaFields
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Failed to parse sort: "
operator|+
name|input
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|assertEquals
argument_list|(
literal|"parsed sorts had unexpected size"
argument_list|,
name|names
operator|.
name|length
argument_list|,
name|sorts
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"parsed sort schema fields had unexpected size"
argument_list|,
name|names
operator|.
name|length
argument_list|,
name|fields
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|names
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|assertEquals
argument_list|(
literal|"sorts["
operator|+
name|j
operator|+
literal|"] had unexpected reverse: "
operator|+
name|input
argument_list|,
name|reverse
index|[
name|j
index|]
argument_list|,
name|sorts
index|[
name|j
index|]
operator|.
name|getReverse
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|Type
name|type
init|=
name|sorts
index|[
name|j
index|]
operator|.
name|getType
argument_list|()
decl_stmt|;
if|if
condition|(
name|Type
operator|.
name|SCORE
operator|.
name|equals
argument_list|(
name|type
argument_list|)
condition|)
block|{
name|assertEquals
argument_list|(
literal|"sorts["
operator|+
name|j
operator|+
literal|"] is (unexpectedly) type score : "
operator|+
name|input
argument_list|,
literal|"score"
argument_list|,
name|names
index|[
name|j
index|]
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|Type
operator|.
name|DOC
operator|.
name|equals
argument_list|(
name|type
argument_list|)
condition|)
block|{
name|assertEquals
argument_list|(
literal|"sorts["
operator|+
name|j
operator|+
literal|"] is (unexpectedly) type doc : "
operator|+
name|input
argument_list|,
literal|"_docid_"
argument_list|,
name|names
index|[
name|j
index|]
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|Type
operator|.
name|CUSTOM
operator|.
name|equals
argument_list|(
name|type
argument_list|)
operator|||
name|Type
operator|.
name|REWRITEABLE
operator|.
name|equals
argument_list|(
name|type
argument_list|)
condition|)
block|{
name|fail
argument_list|(
literal|"sorts["
operator|+
name|j
operator|+
literal|"] resulted in a '"
operator|+
name|type
operator|.
name|toString
argument_list|()
operator|+
literal|"', either sort parsing code is broken, or func/query "
operator|+
literal|"semantics have gotten broader and munging in this test "
operator|+
literal|"needs improved: "
operator|+
name|input
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertEquals
argument_list|(
literal|"sorts["
operator|+
name|j
operator|+
literal|"] ("
operator|+
name|type
operator|.
name|toString
argument_list|()
operator|+
literal|") had unexpected field in: "
operator|+
name|input
argument_list|,
name|names
index|[
name|j
index|]
argument_list|,
name|sorts
index|[
name|j
index|]
operator|.
name|getField
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"fields["
operator|+
name|j
operator|+
literal|"] ("
operator|+
name|type
operator|.
name|toString
argument_list|()
operator|+
literal|") had unexpected name in: "
operator|+
name|input
argument_list|,
name|names
index|[
name|j
index|]
argument_list|,
name|fields
operator|.
name|get
argument_list|(
name|j
argument_list|)
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|testSort
specifier|public
name|void
name|testSort
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
name|Field
name|f
init|=
operator|new
name|StringField
argument_list|(
literal|"f"
argument_list|,
literal|"0"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
decl_stmt|;
name|Field
name|f2
init|=
operator|new
name|StringField
argument_list|(
literal|"f2"
argument_list|,
literal|"0"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|iterCnt
init|=
literal|0
init|;
name|iterCnt
operator|<
name|iter
condition|;
name|iterCnt
operator|++
control|)
block|{
name|IndexWriter
name|iw
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
operator|new
name|IndexWriterConfig
argument_list|(
operator|new
name|SimpleAnalyzer
argument_list|()
argument_list|)
operator|.
name|setOpenMode
argument_list|(
name|IndexWriterConfig
operator|.
name|OpenMode
operator|.
name|CREATE
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|MyDoc
index|[]
name|mydocs
init|=
operator|new
name|MyDoc
index|[
name|ndocs
index|]
decl_stmt|;
name|int
name|v1EmptyPercent
init|=
literal|50
decl_stmt|;
name|int
name|v2EmptyPercent
init|=
literal|50
decl_stmt|;
name|int
name|commitCountdown
init|=
name|commitCount
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|ndocs
condition|;
name|i
operator|++
control|)
block|{
name|MyDoc
name|mydoc
init|=
operator|new
name|MyDoc
argument_list|()
decl_stmt|;
name|mydoc
operator|.
name|doc
operator|=
name|i
expr_stmt|;
name|mydocs
index|[
name|i
index|]
operator|=
name|mydoc
expr_stmt|;
name|Document
name|document
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
if|if
condition|(
name|r
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
operator|<
name|v1EmptyPercent
condition|)
block|{
name|mydoc
operator|.
name|val
operator|=
name|Integer
operator|.
name|toString
argument_list|(
name|r
operator|.
name|nextInt
argument_list|(
name|maxval
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|setStringValue
argument_list|(
name|mydoc
operator|.
name|val
argument_list|)
expr_stmt|;
name|document
operator|.
name|add
argument_list|(
name|f
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|r
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
operator|<
name|v2EmptyPercent
condition|)
block|{
name|mydoc
operator|.
name|val2
operator|=
name|Integer
operator|.
name|toString
argument_list|(
name|r
operator|.
name|nextInt
argument_list|(
name|maxval
argument_list|)
argument_list|)
expr_stmt|;
name|f2
operator|.
name|setStringValue
argument_list|(
name|mydoc
operator|.
name|val2
argument_list|)
expr_stmt|;
name|document
operator|.
name|add
argument_list|(
name|f2
argument_list|)
expr_stmt|;
block|}
name|iw
operator|.
name|addDocument
argument_list|(
name|document
argument_list|)
expr_stmt|;
if|if
condition|(
operator|--
name|commitCountdown
operator|<=
literal|0
condition|)
block|{
name|commitCountdown
operator|=
name|commitCount
expr_stmt|;
name|iw
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
name|iw
operator|.
name|close
argument_list|()
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|UninvertingReader
operator|.
name|Type
argument_list|>
name|mapping
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|mapping
operator|.
name|put
argument_list|(
literal|"f"
argument_list|,
name|UninvertingReader
operator|.
name|Type
operator|.
name|SORTED
argument_list|)
expr_stmt|;
name|mapping
operator|.
name|put
argument_list|(
literal|"f2"
argument_list|,
name|UninvertingReader
operator|.
name|Type
operator|.
name|SORTED
argument_list|)
expr_stmt|;
name|DirectoryReader
name|reader
init|=
name|UninvertingReader
operator|.
name|wrap
argument_list|(
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
argument_list|,
name|mapping
argument_list|)
decl_stmt|;
name|IndexSearcher
name|searcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|reader
argument_list|)
decl_stmt|;
comment|// System.out.println("segments="+searcher.getIndexReader().getSequentialSubReaders().length);
name|assertTrue
argument_list|(
name|reader
operator|.
name|leaves
argument_list|()
operator|.
name|size
argument_list|()
operator|>
literal|1
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|qiter
condition|;
name|i
operator|++
control|)
block|{
name|Filter
name|filt
init|=
operator|new
name|Filter
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|DocIdSet
name|getDocIdSet
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|,
name|Bits
name|acceptDocs
parameter_list|)
block|{
return|return
name|BitsFilteredDocIdSet
operator|.
name|wrap
argument_list|(
name|randSet
argument_list|(
name|context
operator|.
name|reader
argument_list|()
operator|.
name|maxDoc
argument_list|()
argument_list|)
argument_list|,
name|acceptDocs
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|(
name|String
name|field
parameter_list|)
block|{
return|return
literal|"TestSortFilter"
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|other
parameter_list|)
block|{
return|return
name|other
operator|==
name|this
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|System
operator|.
name|identityHashCode
argument_list|(
name|this
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|int
name|top
init|=
name|r
operator|.
name|nextInt
argument_list|(
operator|(
name|ndocs
operator|>>
literal|3
operator|)
operator|+
literal|1
argument_list|)
operator|+
literal|1
decl_stmt|;
specifier|final
name|boolean
name|luceneSort
init|=
name|r
operator|.
name|nextBoolean
argument_list|()
decl_stmt|;
specifier|final
name|boolean
name|sortMissingLast
init|=
operator|!
name|luceneSort
operator|&&
name|r
operator|.
name|nextBoolean
argument_list|()
decl_stmt|;
specifier|final
name|boolean
name|sortMissingFirst
init|=
operator|!
name|luceneSort
operator|&&
operator|!
name|sortMissingLast
decl_stmt|;
specifier|final
name|boolean
name|reverse
init|=
name|r
operator|.
name|nextBoolean
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|SortField
argument_list|>
name|sfields
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|boolean
name|secondary
init|=
name|r
operator|.
name|nextBoolean
argument_list|()
decl_stmt|;
specifier|final
name|boolean
name|luceneSort2
init|=
name|r
operator|.
name|nextBoolean
argument_list|()
decl_stmt|;
specifier|final
name|boolean
name|sortMissingLast2
init|=
operator|!
name|luceneSort2
operator|&&
name|r
operator|.
name|nextBoolean
argument_list|()
decl_stmt|;
specifier|final
name|boolean
name|sortMissingFirst2
init|=
operator|!
name|luceneSort2
operator|&&
operator|!
name|sortMissingLast2
decl_stmt|;
specifier|final
name|boolean
name|reverse2
init|=
name|r
operator|.
name|nextBoolean
argument_list|()
decl_stmt|;
if|if
condition|(
name|r
operator|.
name|nextBoolean
argument_list|()
condition|)
name|sfields
operator|.
name|add
argument_list|(
operator|new
name|SortField
argument_list|(
literal|null
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|SCORE
argument_list|)
argument_list|)
expr_stmt|;
comment|// hit both use-cases of sort-missing-last
name|sfields
operator|.
name|add
argument_list|(
name|Sorting
operator|.
name|getStringSortField
argument_list|(
literal|"f"
argument_list|,
name|reverse
argument_list|,
name|sortMissingLast
argument_list|,
name|sortMissingFirst
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|secondary
condition|)
block|{
name|sfields
operator|.
name|add
argument_list|(
name|Sorting
operator|.
name|getStringSortField
argument_list|(
literal|"f2"
argument_list|,
name|reverse2
argument_list|,
name|sortMissingLast2
argument_list|,
name|sortMissingFirst2
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|r
operator|.
name|nextBoolean
argument_list|()
condition|)
name|sfields
operator|.
name|add
argument_list|(
operator|new
name|SortField
argument_list|(
literal|null
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|SCORE
argument_list|)
argument_list|)
expr_stmt|;
name|Sort
name|sort
init|=
operator|new
name|Sort
argument_list|(
name|sfields
operator|.
name|toArray
argument_list|(
operator|new
name|SortField
index|[
name|sfields
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|String
name|nullRep
init|=
name|luceneSort
operator|||
name|sortMissingFirst
operator|&&
operator|!
name|reverse
operator|||
name|sortMissingLast
operator|&&
name|reverse
condition|?
literal|""
else|:
literal|"zzz"
decl_stmt|;
specifier|final
name|String
name|nullRep2
init|=
name|luceneSort2
operator|||
name|sortMissingFirst2
operator|&&
operator|!
name|reverse2
operator|||
name|sortMissingLast2
operator|&&
name|reverse2
condition|?
literal|""
else|:
literal|"zzz"
decl_stmt|;
name|boolean
name|trackScores
init|=
name|r
operator|.
name|nextBoolean
argument_list|()
decl_stmt|;
name|boolean
name|trackMaxScores
init|=
name|r
operator|.
name|nextBoolean
argument_list|()
decl_stmt|;
name|boolean
name|scoreInOrder
init|=
name|r
operator|.
name|nextBoolean
argument_list|()
decl_stmt|;
specifier|final
name|TopFieldCollector
name|topCollector
init|=
name|TopFieldCollector
operator|.
name|create
argument_list|(
name|sort
argument_list|,
name|top
argument_list|,
literal|true
argument_list|,
name|trackScores
argument_list|,
name|trackMaxScores
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|MyDoc
argument_list|>
name|collectedDocs
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
comment|// delegate and collect docs ourselves
name|Collector
name|myCollector
init|=
operator|new
name|FilterCollector
argument_list|(
name|topCollector
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|LeafCollector
name|getLeafCollector
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|int
name|docBase
init|=
name|context
operator|.
name|docBase
decl_stmt|;
return|return
operator|new
name|FilterLeafCollector
argument_list|(
name|super
operator|.
name|getLeafCollector
argument_list|(
name|context
argument_list|)
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|collect
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|collect
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|collectedDocs
operator|.
name|add
argument_list|(
name|mydocs
index|[
name|docBase
operator|+
name|doc
index|]
argument_list|)
expr_stmt|;
block|}
block|}
return|;
block|}
block|}
decl_stmt|;
name|searcher
operator|.
name|search
argument_list|(
name|filt
argument_list|,
name|myCollector
argument_list|)
expr_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|collectedDocs
argument_list|,
parameter_list|(
name|o1
parameter_list|,
name|o2
parameter_list|)
lambda|->
block|{
name|String
name|v1
init|=
name|o1
operator|.
name|val
operator|==
literal|null
condition|?
name|nullRep
else|:
name|o1
operator|.
name|val
decl_stmt|;
name|String
name|v2
init|=
name|o2
operator|.
name|val
operator|==
literal|null
condition|?
name|nullRep
else|:
name|o2
operator|.
name|val
decl_stmt|;
name|int
name|cmp
init|=
name|v1
operator|.
name|compareTo
argument_list|(
name|v2
argument_list|)
decl_stmt|;
if|if
condition|(
name|reverse
condition|)
name|cmp
operator|=
operator|-
name|cmp
expr_stmt|;
if|if
condition|(
name|cmp
operator|!=
literal|0
condition|)
return|return
name|cmp
return|;
if|if
condition|(
name|secondary
condition|)
block|{
name|v1
operator|=
name|o1
operator|.
name|val2
operator|==
literal|null
condition|?
name|nullRep2
else|:
name|o1
operator|.
name|val2
expr_stmt|;
name|v2
operator|=
name|o2
operator|.
name|val2
operator|==
literal|null
condition|?
name|nullRep2
else|:
name|o2
operator|.
name|val2
expr_stmt|;
name|cmp
operator|=
name|v1
operator|.
name|compareTo
argument_list|(
name|v2
argument_list|)
expr_stmt|;
if|if
condition|(
name|reverse2
condition|)
name|cmp
operator|=
operator|-
name|cmp
expr_stmt|;
block|}
name|cmp
operator|=
name|cmp
operator|==
literal|0
condition|?
name|o1
operator|.
name|doc
operator|-
name|o2
operator|.
name|doc
else|:
name|cmp
expr_stmt|;
return|return
name|cmp
return|;
block|}
argument_list|)
expr_stmt|;
name|TopDocs
name|topDocs
init|=
name|topCollector
operator|.
name|topDocs
argument_list|()
decl_stmt|;
name|ScoreDoc
index|[]
name|sdocs
init|=
name|topDocs
operator|.
name|scoreDocs
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|sdocs
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|int
name|id
init|=
name|sdocs
index|[
name|j
index|]
operator|.
name|doc
decl_stmt|;
if|if
condition|(
name|id
operator|!=
name|collectedDocs
operator|.
name|get
argument_list|(
name|j
argument_list|)
operator|.
name|doc
condition|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Error at pos "
operator|+
name|j
operator|+
literal|"\n\tsortMissingFirst="
operator|+
name|sortMissingFirst
operator|+
literal|" sortMissingLast="
operator|+
name|sortMissingLast
operator|+
literal|" reverse="
operator|+
name|reverse
operator|+
literal|"\n\tEXPECTED="
operator|+
name|collectedDocs
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|id
argument_list|,
name|collectedDocs
operator|.
name|get
argument_list|(
name|j
argument_list|)
operator|.
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|randSet
specifier|public
name|DocIdSet
name|randSet
parameter_list|(
name|int
name|sz
parameter_list|)
block|{
name|FixedBitSet
name|obs
init|=
operator|new
name|FixedBitSet
argument_list|(
name|sz
argument_list|)
decl_stmt|;
name|int
name|n
init|=
name|r
operator|.
name|nextInt
argument_list|(
name|sz
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|n
condition|;
name|i
operator|++
control|)
block|{
name|obs
operator|.
name|set
argument_list|(
name|r
operator|.
name|nextInt
argument_list|(
name|sz
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|BitDocIdSet
argument_list|(
name|obs
argument_list|)
return|;
block|}
block|}
end_class

end_unit

