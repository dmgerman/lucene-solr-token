begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|Comparator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|Random
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|NumericDocValuesField
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
name|SortedDocValuesField
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
name|StoredField
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
name|AtomicReaderContext
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
name|IndexReader
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
name|RandomIndexWriter
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
name|BooleanClause
operator|.
name|Occur
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
name|BytesRef
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
name|LuceneTestCase
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

begin_comment
comment|/** random sorting tests */
end_comment

begin_class
DECL|class|TestSortRandom
specifier|public
class|class
name|TestSortRandom
extends|extends
name|LuceneTestCase
block|{
DECL|method|testRandomStringSort
specifier|public
name|void
name|testRandomStringSort
parameter_list|()
throws|throws
name|Exception
block|{
name|Random
name|random
init|=
operator|new
name|Random
argument_list|(
name|random
argument_list|()
operator|.
name|nextLong
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|int
name|NUM_DOCS
init|=
name|atLeast
argument_list|(
literal|100
argument_list|)
decl_stmt|;
specifier|final
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
specifier|final
name|RandomIndexWriter
name|writer
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|,
name|dir
argument_list|)
decl_stmt|;
specifier|final
name|boolean
name|allowDups
init|=
name|random
operator|.
name|nextBoolean
argument_list|()
decl_stmt|;
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|seen
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|int
name|maxLength
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|,
literal|5
argument_list|,
literal|100
argument_list|)
decl_stmt|;
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TEST: NUM_DOCS="
operator|+
name|NUM_DOCS
operator|+
literal|" maxLength="
operator|+
name|maxLength
operator|+
literal|" allowDups="
operator|+
name|allowDups
argument_list|)
expr_stmt|;
block|}
name|int
name|numDocs
init|=
literal|0
decl_stmt|;
specifier|final
name|List
argument_list|<
name|BytesRef
argument_list|>
name|docValues
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
comment|// TODO: deletions
while|while
condition|(
name|numDocs
operator|<
name|NUM_DOCS
condition|)
block|{
specifier|final
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
comment|// 10% of the time, the document is missing the value:
specifier|final
name|BytesRef
name|br
decl_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
operator|!=
literal|7
condition|)
block|{
specifier|final
name|String
name|s
decl_stmt|;
if|if
condition|(
name|random
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|s
operator|=
name|TestUtil
operator|.
name|randomSimpleString
argument_list|(
name|random
argument_list|,
name|maxLength
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|s
operator|=
name|TestUtil
operator|.
name|randomUnicodeString
argument_list|(
name|random
argument_list|,
name|maxLength
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|allowDups
condition|)
block|{
if|if
condition|(
name|seen
operator|.
name|contains
argument_list|(
name|s
argument_list|)
condition|)
block|{
continue|continue;
block|}
name|seen
operator|.
name|add
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  "
operator|+
name|numDocs
operator|+
literal|": s="
operator|+
name|s
argument_list|)
expr_stmt|;
block|}
name|br
operator|=
operator|new
name|BytesRef
argument_list|(
name|s
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|SortedDocValuesField
argument_list|(
literal|"stringdv"
argument_list|,
name|br
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|newStringField
argument_list|(
literal|"string"
argument_list|,
name|s
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|docValues
operator|.
name|add
argument_list|(
name|br
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|br
operator|=
literal|null
expr_stmt|;
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  "
operator|+
name|numDocs
operator|+
literal|":<missing>"
argument_list|)
expr_stmt|;
block|}
name|docValues
operator|.
name|add
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
name|doc
operator|.
name|add
argument_list|(
operator|new
name|NumericDocValuesField
argument_list|(
literal|"id"
argument_list|,
name|numDocs
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|StoredField
argument_list|(
literal|"id"
argument_list|,
name|numDocs
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|numDocs
operator|++
expr_stmt|;
if|if
condition|(
name|random
operator|.
name|nextInt
argument_list|(
literal|40
argument_list|)
operator|==
literal|17
condition|)
block|{
comment|// force flush
name|writer
operator|.
name|getReader
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|final
name|IndexReader
name|r
init|=
name|writer
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|writer
operator|.
name|shutdown
argument_list|()
expr_stmt|;
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  reader="
operator|+
name|r
argument_list|)
expr_stmt|;
block|}
specifier|final
name|IndexSearcher
name|s
init|=
name|newSearcher
argument_list|(
name|r
argument_list|,
literal|false
argument_list|)
decl_stmt|;
specifier|final
name|int
name|ITERS
init|=
name|atLeast
argument_list|(
literal|100
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|iter
init|=
literal|0
init|;
name|iter
operator|<
name|ITERS
condition|;
name|iter
operator|++
control|)
block|{
specifier|final
name|boolean
name|reverse
init|=
name|random
operator|.
name|nextBoolean
argument_list|()
decl_stmt|;
specifier|final
name|TopFieldDocs
name|hits
decl_stmt|;
specifier|final
name|SortField
name|sf
decl_stmt|;
specifier|final
name|boolean
name|sortMissingLast
decl_stmt|;
specifier|final
name|boolean
name|missingIsNull
decl_stmt|;
if|if
condition|(
name|random
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|sf
operator|=
operator|new
name|SortField
argument_list|(
literal|"stringdv"
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|STRING
argument_list|,
name|reverse
argument_list|)
expr_stmt|;
comment|// Can only use sort missing if the DVFormat
comment|// supports docsWithField:
name|sortMissingLast
operator|=
name|defaultCodecSupportsDocsWithField
argument_list|()
operator|&&
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
expr_stmt|;
name|missingIsNull
operator|=
name|defaultCodecSupportsDocsWithField
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|sf
operator|=
operator|new
name|SortField
argument_list|(
literal|"string"
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|STRING
argument_list|,
name|reverse
argument_list|)
expr_stmt|;
name|sortMissingLast
operator|=
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
expr_stmt|;
name|missingIsNull
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
name|sortMissingLast
condition|)
block|{
name|sf
operator|.
name|setMissingValue
argument_list|(
name|SortField
operator|.
name|STRING_LAST
argument_list|)
expr_stmt|;
block|}
specifier|final
name|Sort
name|sort
decl_stmt|;
if|if
condition|(
name|random
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|sort
operator|=
operator|new
name|Sort
argument_list|(
name|sf
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|sort
operator|=
operator|new
name|Sort
argument_list|(
name|sf
argument_list|,
name|SortField
operator|.
name|FIELD_DOC
argument_list|)
expr_stmt|;
block|}
specifier|final
name|int
name|hitCount
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|,
literal|1
argument_list|,
name|r
operator|.
name|maxDoc
argument_list|()
operator|+
literal|20
argument_list|)
decl_stmt|;
specifier|final
name|RandomFilter
name|f
init|=
operator|new
name|RandomFilter
argument_list|(
name|random
argument_list|,
name|random
operator|.
name|nextFloat
argument_list|()
argument_list|,
name|docValues
argument_list|)
decl_stmt|;
name|int
name|queryType
init|=
name|random
operator|.
name|nextInt
argument_list|(
literal|3
argument_list|)
decl_stmt|;
if|if
condition|(
name|queryType
operator|==
literal|0
condition|)
block|{
comment|// force out of order
name|BooleanQuery
name|bq
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
comment|// Add a Query with SHOULD, since bw.scorer() returns BooleanScorer2
comment|// which delegates to BS if there are no mandatory clauses.
name|bq
operator|.
name|add
argument_list|(
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
comment|// Set minNrShouldMatch to 1 so that BQ will not optimize rewrite to return
comment|// the clause instead of BQ.
name|bq
operator|.
name|setMinimumNumberShouldMatch
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|hits
operator|=
name|s
operator|.
name|search
argument_list|(
name|bq
argument_list|,
name|f
argument_list|,
name|hitCount
argument_list|,
name|sort
argument_list|,
name|random
operator|.
name|nextBoolean
argument_list|()
argument_list|,
name|random
operator|.
name|nextBoolean
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|queryType
operator|==
literal|1
condition|)
block|{
name|hits
operator|=
name|s
operator|.
name|search
argument_list|(
operator|new
name|ConstantScoreQuery
argument_list|(
name|f
argument_list|)
argument_list|,
literal|null
argument_list|,
name|hitCount
argument_list|,
name|sort
argument_list|,
name|random
operator|.
name|nextBoolean
argument_list|()
argument_list|,
name|random
operator|.
name|nextBoolean
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|hits
operator|=
name|s
operator|.
name|search
argument_list|(
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|,
name|f
argument_list|,
name|hitCount
argument_list|,
name|sort
argument_list|,
name|random
operator|.
name|nextBoolean
argument_list|()
argument_list|,
name|random
operator|.
name|nextBoolean
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"\nTEST: iter="
operator|+
name|iter
operator|+
literal|" "
operator|+
name|hits
operator|.
name|totalHits
operator|+
literal|" hits; topN="
operator|+
name|hitCount
operator|+
literal|"; reverse="
operator|+
name|reverse
operator|+
literal|"; sortMissingLast="
operator|+
name|sortMissingLast
operator|+
literal|" sort="
operator|+
name|sort
argument_list|)
expr_stmt|;
block|}
comment|// Compute expected results:
name|Collections
operator|.
name|sort
argument_list|(
name|f
operator|.
name|matchValues
argument_list|,
operator|new
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|BytesRef
name|a
parameter_list|,
name|BytesRef
name|b
parameter_list|)
block|{
if|if
condition|(
name|a
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|b
operator|==
literal|null
condition|)
block|{
return|return
literal|0
return|;
block|}
if|if
condition|(
name|sortMissingLast
condition|)
block|{
return|return
literal|1
return|;
block|}
else|else
block|{
return|return
operator|-
literal|1
return|;
block|}
block|}
elseif|else
if|if
condition|(
name|b
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|sortMissingLast
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
else|else
block|{
return|return
literal|1
return|;
block|}
block|}
else|else
block|{
return|return
name|a
operator|.
name|compareTo
argument_list|(
name|b
argument_list|)
return|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
if|if
condition|(
name|reverse
condition|)
block|{
name|Collections
operator|.
name|reverse
argument_list|(
name|f
operator|.
name|matchValues
argument_list|)
expr_stmt|;
block|}
specifier|final
name|List
argument_list|<
name|BytesRef
argument_list|>
name|expected
init|=
name|f
operator|.
name|matchValues
decl_stmt|;
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  expected:"
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|idx
init|=
literal|0
init|;
name|idx
operator|<
name|expected
operator|.
name|size
argument_list|()
condition|;
name|idx
operator|++
control|)
block|{
name|BytesRef
name|br
init|=
name|expected
operator|.
name|get
argument_list|(
name|idx
argument_list|)
decl_stmt|;
if|if
condition|(
name|br
operator|==
literal|null
operator|&&
name|missingIsNull
operator|==
literal|false
condition|)
block|{
name|br
operator|=
operator|new
name|BytesRef
argument_list|()
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"    "
operator|+
name|idx
operator|+
literal|": "
operator|+
operator|(
name|br
operator|==
literal|null
condition|?
literal|"<missing>"
else|:
name|br
operator|.
name|utf8ToString
argument_list|()
operator|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|idx
operator|==
name|hitCount
operator|-
literal|1
condition|)
block|{
break|break;
block|}
block|}
block|}
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  actual:"
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|hitIDX
init|=
literal|0
init|;
name|hitIDX
operator|<
name|hits
operator|.
name|scoreDocs
operator|.
name|length
condition|;
name|hitIDX
operator|++
control|)
block|{
specifier|final
name|FieldDoc
name|fd
init|=
operator|(
name|FieldDoc
operator|)
name|hits
operator|.
name|scoreDocs
index|[
name|hitIDX
index|]
decl_stmt|;
name|BytesRef
name|br
init|=
operator|(
name|BytesRef
operator|)
name|fd
operator|.
name|fields
index|[
literal|0
index|]
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"    "
operator|+
name|hitIDX
operator|+
literal|": "
operator|+
operator|(
name|br
operator|==
literal|null
condition|?
literal|"<missing>"
else|:
name|br
operator|.
name|utf8ToString
argument_list|()
operator|)
operator|+
literal|" id="
operator|+
name|s
operator|.
name|doc
argument_list|(
name|fd
operator|.
name|doc
argument_list|)
operator|.
name|get
argument_list|(
literal|"id"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|int
name|hitIDX
init|=
literal|0
init|;
name|hitIDX
operator|<
name|hits
operator|.
name|scoreDocs
operator|.
name|length
condition|;
name|hitIDX
operator|++
control|)
block|{
specifier|final
name|FieldDoc
name|fd
init|=
operator|(
name|FieldDoc
operator|)
name|hits
operator|.
name|scoreDocs
index|[
name|hitIDX
index|]
decl_stmt|;
name|BytesRef
name|br
init|=
name|expected
operator|.
name|get
argument_list|(
name|hitIDX
argument_list|)
decl_stmt|;
if|if
condition|(
name|br
operator|==
literal|null
operator|&&
name|missingIsNull
operator|==
literal|false
condition|)
block|{
name|br
operator|=
operator|new
name|BytesRef
argument_list|()
expr_stmt|;
block|}
comment|// Normally, the old codecs (that don't support
comment|// docsWithField via doc values) will always return
comment|// an empty BytesRef for the missing case; however,
comment|// if all docs in a given segment were missing, in
comment|// that case it will return null!  So we must map
comment|// null here, too:
name|BytesRef
name|br2
init|=
operator|(
name|BytesRef
operator|)
name|fd
operator|.
name|fields
index|[
literal|0
index|]
decl_stmt|;
if|if
condition|(
name|br2
operator|==
literal|null
operator|&&
name|missingIsNull
operator|==
literal|false
condition|)
block|{
name|br2
operator|=
operator|new
name|BytesRef
argument_list|()
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|br
argument_list|,
name|br2
argument_list|)
expr_stmt|;
block|}
block|}
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|class|RandomFilter
specifier|private
specifier|static
class|class
name|RandomFilter
extends|extends
name|Filter
block|{
DECL|field|random
specifier|private
specifier|final
name|Random
name|random
decl_stmt|;
DECL|field|density
specifier|private
name|float
name|density
decl_stmt|;
DECL|field|docValues
specifier|private
specifier|final
name|List
argument_list|<
name|BytesRef
argument_list|>
name|docValues
decl_stmt|;
DECL|field|matchValues
specifier|public
specifier|final
name|List
argument_list|<
name|BytesRef
argument_list|>
name|matchValues
init|=
name|Collections
operator|.
name|synchronizedList
argument_list|(
operator|new
name|ArrayList
argument_list|<
name|BytesRef
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
comment|// density should be 0.0 ... 1.0
DECL|method|RandomFilter
specifier|public
name|RandomFilter
parameter_list|(
name|Random
name|random
parameter_list|,
name|float
name|density
parameter_list|,
name|List
argument_list|<
name|BytesRef
argument_list|>
name|docValues
parameter_list|)
block|{
name|this
operator|.
name|random
operator|=
name|random
expr_stmt|;
name|this
operator|.
name|density
operator|=
name|density
expr_stmt|;
name|this
operator|.
name|docValues
operator|=
name|docValues
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getDocIdSet
specifier|public
name|DocIdSet
name|getDocIdSet
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|,
name|Bits
name|acceptDocs
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|int
name|maxDoc
init|=
name|context
operator|.
name|reader
argument_list|()
operator|.
name|maxDoc
argument_list|()
decl_stmt|;
specifier|final
name|FieldCache
operator|.
name|Ints
name|idSource
init|=
name|FieldCache
operator|.
name|DEFAULT
operator|.
name|getInts
argument_list|(
name|context
operator|.
name|reader
argument_list|()
argument_list|,
literal|"id"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|idSource
argument_list|)
expr_stmt|;
specifier|final
name|FixedBitSet
name|bits
init|=
operator|new
name|FixedBitSet
argument_list|(
name|maxDoc
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|docID
init|=
literal|0
init|;
name|docID
operator|<
name|maxDoc
condition|;
name|docID
operator|++
control|)
block|{
if|if
condition|(
name|random
operator|.
name|nextFloat
argument_list|()
operator|<=
name|density
operator|&&
operator|(
name|acceptDocs
operator|==
literal|null
operator|||
name|acceptDocs
operator|.
name|get
argument_list|(
name|docID
argument_list|)
operator|)
condition|)
block|{
name|bits
operator|.
name|set
argument_list|(
name|docID
argument_list|)
expr_stmt|;
comment|//System.out.println("  acc id=" + idSource.getInt(docID) + " docID=" + docID);
name|matchValues
operator|.
name|add
argument_list|(
name|docValues
operator|.
name|get
argument_list|(
name|idSource
operator|.
name|get
argument_list|(
name|docID
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|bits
return|;
block|}
block|}
block|}
end_class

end_unit

