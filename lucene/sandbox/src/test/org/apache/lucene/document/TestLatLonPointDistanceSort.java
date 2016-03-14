begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.document
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
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
name|util
operator|.
name|Arrays
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
name|FieldDoc
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
name|MatchAllDocsQuery
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
name|spatial
operator|.
name|util
operator|.
name|GeoDistanceUtils
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
comment|/** Simple tests for {@link LatLonPoint#newDistanceSort} */
end_comment

begin_class
DECL|class|TestLatLonPointDistanceSort
specifier|public
class|class
name|TestLatLonPointDistanceSort
extends|extends
name|LuceneTestCase
block|{
comment|/** Add three points and sort by distance */
DECL|method|testDistanceSort
specifier|public
name|void
name|testDistanceSort
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|RandomIndexWriter
name|iw
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir
argument_list|)
decl_stmt|;
comment|// add some docs
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|LatLonPoint
argument_list|(
literal|"location"
argument_list|,
literal|40.759011
argument_list|,
operator|-
literal|73.9844722
argument_list|)
argument_list|)
expr_stmt|;
name|iw
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|doc
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|LatLonPoint
argument_list|(
literal|"location"
argument_list|,
literal|40.718266
argument_list|,
operator|-
literal|74.007819
argument_list|)
argument_list|)
expr_stmt|;
name|iw
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|doc
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|LatLonPoint
argument_list|(
literal|"location"
argument_list|,
literal|40.7051157
argument_list|,
operator|-
literal|74.0088305
argument_list|)
argument_list|)
expr_stmt|;
name|iw
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|IndexReader
name|reader
init|=
name|iw
operator|.
name|getReader
argument_list|()
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
name|iw
operator|.
name|close
argument_list|()
expr_stmt|;
name|Sort
name|sort
init|=
operator|new
name|Sort
argument_list|(
name|LatLonPoint
operator|.
name|newDistanceSort
argument_list|(
literal|"location"
argument_list|,
literal|40.7143528
argument_list|,
operator|-
literal|74.0059731
argument_list|)
argument_list|)
decl_stmt|;
name|TopDocs
name|td
init|=
name|searcher
operator|.
name|search
argument_list|(
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|,
literal|3
argument_list|,
name|sort
argument_list|)
decl_stmt|;
name|FieldDoc
name|d
init|=
operator|(
name|FieldDoc
operator|)
name|td
operator|.
name|scoreDocs
index|[
literal|0
index|]
decl_stmt|;
name|assertEquals
argument_list|(
literal|462.61748421408186D
argument_list|,
operator|(
name|Double
operator|)
name|d
operator|.
name|fields
index|[
literal|0
index|]
argument_list|,
literal|0.0D
argument_list|)
expr_stmt|;
name|d
operator|=
operator|(
name|FieldDoc
operator|)
name|td
operator|.
name|scoreDocs
index|[
literal|1
index|]
expr_stmt|;
name|assertEquals
argument_list|(
literal|1056.1630445911035D
argument_list|,
operator|(
name|Double
operator|)
name|d
operator|.
name|fields
index|[
literal|0
index|]
argument_list|,
literal|0.0D
argument_list|)
expr_stmt|;
name|d
operator|=
operator|(
name|FieldDoc
operator|)
name|td
operator|.
name|scoreDocs
index|[
literal|2
index|]
expr_stmt|;
name|assertEquals
argument_list|(
literal|5291.798081404466D
argument_list|,
operator|(
name|Double
operator|)
name|d
operator|.
name|fields
index|[
literal|0
index|]
argument_list|,
literal|0.0D
argument_list|)
expr_stmt|;
name|reader
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
comment|/** Add two points (one doc missing) and sort by distance */
DECL|method|testMissingLast
specifier|public
name|void
name|testMissingLast
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|RandomIndexWriter
name|iw
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir
argument_list|)
decl_stmt|;
comment|// missing
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|iw
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|doc
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|LatLonPoint
argument_list|(
literal|"location"
argument_list|,
literal|40.718266
argument_list|,
operator|-
literal|74.007819
argument_list|)
argument_list|)
expr_stmt|;
name|iw
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|doc
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|LatLonPoint
argument_list|(
literal|"location"
argument_list|,
literal|40.7051157
argument_list|,
operator|-
literal|74.0088305
argument_list|)
argument_list|)
expr_stmt|;
name|iw
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|IndexReader
name|reader
init|=
name|iw
operator|.
name|getReader
argument_list|()
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
name|iw
operator|.
name|close
argument_list|()
expr_stmt|;
name|Sort
name|sort
init|=
operator|new
name|Sort
argument_list|(
name|LatLonPoint
operator|.
name|newDistanceSort
argument_list|(
literal|"location"
argument_list|,
literal|40.7143528
argument_list|,
operator|-
literal|74.0059731
argument_list|)
argument_list|)
decl_stmt|;
name|TopDocs
name|td
init|=
name|searcher
operator|.
name|search
argument_list|(
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|,
literal|3
argument_list|,
name|sort
argument_list|)
decl_stmt|;
name|FieldDoc
name|d
init|=
operator|(
name|FieldDoc
operator|)
name|td
operator|.
name|scoreDocs
index|[
literal|0
index|]
decl_stmt|;
name|assertEquals
argument_list|(
literal|462.61748421408186D
argument_list|,
operator|(
name|Double
operator|)
name|d
operator|.
name|fields
index|[
literal|0
index|]
argument_list|,
literal|0.0D
argument_list|)
expr_stmt|;
name|d
operator|=
operator|(
name|FieldDoc
operator|)
name|td
operator|.
name|scoreDocs
index|[
literal|1
index|]
expr_stmt|;
name|assertEquals
argument_list|(
literal|1056.1630445911035D
argument_list|,
operator|(
name|Double
operator|)
name|d
operator|.
name|fields
index|[
literal|0
index|]
argument_list|,
literal|0.0D
argument_list|)
expr_stmt|;
name|d
operator|=
operator|(
name|FieldDoc
operator|)
name|td
operator|.
name|scoreDocs
index|[
literal|2
index|]
expr_stmt|;
name|assertEquals
argument_list|(
name|Double
operator|.
name|POSITIVE_INFINITY
argument_list|,
operator|(
name|Double
operator|)
name|d
operator|.
name|fields
index|[
literal|0
index|]
argument_list|,
literal|0.0D
argument_list|)
expr_stmt|;
name|reader
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
comment|/** Add two points (one doc missing) and sort by distance */
DECL|method|testMissingFirst
specifier|public
name|void
name|testMissingFirst
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|RandomIndexWriter
name|iw
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir
argument_list|)
decl_stmt|;
comment|// missing
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|iw
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|doc
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|LatLonPoint
argument_list|(
literal|"location"
argument_list|,
literal|40.718266
argument_list|,
operator|-
literal|74.007819
argument_list|)
argument_list|)
expr_stmt|;
name|iw
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|doc
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|LatLonPoint
argument_list|(
literal|"location"
argument_list|,
literal|40.7051157
argument_list|,
operator|-
literal|74.0088305
argument_list|)
argument_list|)
expr_stmt|;
name|iw
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|IndexReader
name|reader
init|=
name|iw
operator|.
name|getReader
argument_list|()
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
name|iw
operator|.
name|close
argument_list|()
expr_stmt|;
name|SortField
name|sortField
init|=
name|LatLonPoint
operator|.
name|newDistanceSort
argument_list|(
literal|"location"
argument_list|,
literal|40.7143528
argument_list|,
operator|-
literal|74.0059731
argument_list|)
decl_stmt|;
name|sortField
operator|.
name|setMissingValue
argument_list|(
name|Double
operator|.
name|NEGATIVE_INFINITY
argument_list|)
expr_stmt|;
name|Sort
name|sort
init|=
operator|new
name|Sort
argument_list|(
name|sortField
argument_list|)
decl_stmt|;
name|TopDocs
name|td
init|=
name|searcher
operator|.
name|search
argument_list|(
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|,
literal|3
argument_list|,
name|sort
argument_list|)
decl_stmt|;
name|FieldDoc
name|d
init|=
operator|(
name|FieldDoc
operator|)
name|td
operator|.
name|scoreDocs
index|[
literal|0
index|]
decl_stmt|;
name|assertEquals
argument_list|(
name|Double
operator|.
name|NEGATIVE_INFINITY
argument_list|,
operator|(
name|Double
operator|)
name|d
operator|.
name|fields
index|[
literal|0
index|]
argument_list|,
literal|0.0D
argument_list|)
expr_stmt|;
name|d
operator|=
operator|(
name|FieldDoc
operator|)
name|td
operator|.
name|scoreDocs
index|[
literal|1
index|]
expr_stmt|;
name|assertEquals
argument_list|(
literal|462.61748421408186D
argument_list|,
operator|(
name|Double
operator|)
name|d
operator|.
name|fields
index|[
literal|0
index|]
argument_list|,
literal|0.0D
argument_list|)
expr_stmt|;
name|d
operator|=
operator|(
name|FieldDoc
operator|)
name|td
operator|.
name|scoreDocs
index|[
literal|2
index|]
expr_stmt|;
name|assertEquals
argument_list|(
literal|1056.1630445911035D
argument_list|,
operator|(
name|Double
operator|)
name|d
operator|.
name|fields
index|[
literal|0
index|]
argument_list|,
literal|0.0D
argument_list|)
expr_stmt|;
name|reader
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
comment|/** Run a few iterations with just 10 docs, hopefully easy to debug */
DECL|method|testRandom
specifier|public
name|void
name|testRandom
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|int
name|iters
init|=
literal|0
init|;
name|iters
operator|<
literal|100
condition|;
name|iters
operator|++
control|)
block|{
name|doRandomTest
argument_list|(
literal|10
argument_list|,
literal|100
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Runs with thousands of docs */
annotation|@
name|Nightly
DECL|method|testRandomHuge
specifier|public
name|void
name|testRandomHuge
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|int
name|iters
init|=
literal|0
init|;
name|iters
operator|<
literal|10
condition|;
name|iters
operator|++
control|)
block|{
name|doRandomTest
argument_list|(
literal|2000
argument_list|,
literal|100
argument_list|)
expr_stmt|;
block|}
block|}
comment|// result class used for testing. holds an id+distance.
comment|// we sort these with Arrays.sort and compare with lucene's results
DECL|class|Result
specifier|static
class|class
name|Result
implements|implements
name|Comparable
argument_list|<
name|Result
argument_list|>
block|{
DECL|field|id
name|int
name|id
decl_stmt|;
DECL|field|distance
name|double
name|distance
decl_stmt|;
DECL|method|Result
name|Result
parameter_list|(
name|int
name|id
parameter_list|,
name|double
name|distance
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
name|this
operator|.
name|distance
operator|=
name|distance
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|compareTo
specifier|public
name|int
name|compareTo
parameter_list|(
name|Result
name|o
parameter_list|)
block|{
name|int
name|cmp
init|=
name|Double
operator|.
name|compare
argument_list|(
name|distance
argument_list|,
name|o
operator|.
name|distance
argument_list|)
decl_stmt|;
if|if
condition|(
name|cmp
operator|==
literal|0
condition|)
block|{
return|return
name|Integer
operator|.
name|compare
argument_list|(
name|id
argument_list|,
name|o
operator|.
name|id
argument_list|)
return|;
block|}
return|return
name|cmp
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
specifier|final
name|int
name|prime
init|=
literal|31
decl_stmt|;
name|int
name|result
init|=
literal|1
decl_stmt|;
name|long
name|temp
decl_stmt|;
name|temp
operator|=
name|Double
operator|.
name|doubleToLongBits
argument_list|(
name|distance
argument_list|)
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
call|(
name|int
call|)
argument_list|(
name|temp
operator|^
operator|(
name|temp
operator|>>>
literal|32
operator|)
argument_list|)
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
name|id
expr_stmt|;
return|return
name|result
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|obj
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|obj
operator|==
literal|null
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|getClass
argument_list|()
operator|!=
name|obj
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
name|Result
name|other
init|=
operator|(
name|Result
operator|)
name|obj
decl_stmt|;
if|if
condition|(
name|Double
operator|.
name|doubleToLongBits
argument_list|(
name|distance
argument_list|)
operator|!=
name|Double
operator|.
name|doubleToLongBits
argument_list|(
name|other
operator|.
name|distance
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|id
operator|!=
name|other
operator|.
name|id
condition|)
return|return
literal|false
return|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"Result [id="
operator|+
name|id
operator|+
literal|", distance="
operator|+
name|distance
operator|+
literal|"]"
return|;
block|}
block|}
DECL|method|doRandomTest
specifier|private
name|void
name|doRandomTest
parameter_list|(
name|int
name|numDocs
parameter_list|,
name|int
name|numQueries
parameter_list|)
throws|throws
name|IOException
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|RandomIndexWriter
name|writer
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir
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
name|numDocs
condition|;
name|i
operator|++
control|)
block|{
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|StoredField
argument_list|(
literal|"id"
argument_list|,
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|NumericDocValuesField
argument_list|(
literal|"id"
argument_list|,
name|i
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
operator|>
literal|7
condition|)
block|{
name|double
name|latRaw
init|=
operator|-
literal|90
operator|+
literal|180.0
operator|*
name|random
argument_list|()
operator|.
name|nextDouble
argument_list|()
decl_stmt|;
name|double
name|lonRaw
init|=
operator|-
literal|180
operator|+
literal|360.0
operator|*
name|random
argument_list|()
operator|.
name|nextDouble
argument_list|()
decl_stmt|;
comment|// pre-normalize up front, so we can just use quantized value for testing and do simple exact comparisons
name|double
name|lat
init|=
name|LatLonPoint
operator|.
name|decodeLatitude
argument_list|(
name|LatLonPoint
operator|.
name|encodeLatitude
argument_list|(
name|latRaw
argument_list|)
argument_list|)
decl_stmt|;
name|double
name|lon
init|=
name|LatLonPoint
operator|.
name|decodeLongitude
argument_list|(
name|LatLonPoint
operator|.
name|encodeLongitude
argument_list|(
name|lonRaw
argument_list|)
argument_list|)
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|LatLonPoint
argument_list|(
literal|"field"
argument_list|,
name|lat
argument_list|,
name|lon
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
literal|"lat"
argument_list|,
name|lat
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
literal|"lon"
argument_list|,
name|lon
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// otherwise "missing"
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|IndexReader
name|reader
init|=
name|writer
operator|.
name|getReader
argument_list|()
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
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numQueries
condition|;
name|i
operator|++
control|)
block|{
name|double
name|lat
init|=
operator|-
literal|90
operator|+
literal|180.0
operator|*
name|random
argument_list|()
operator|.
name|nextDouble
argument_list|()
decl_stmt|;
name|double
name|lon
init|=
operator|-
literal|180
operator|+
literal|360.0
operator|*
name|random
argument_list|()
operator|.
name|nextDouble
argument_list|()
decl_stmt|;
name|double
name|missingValue
init|=
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|?
name|Double
operator|.
name|POSITIVE_INFINITY
else|:
name|Double
operator|.
name|NEGATIVE_INFINITY
decl_stmt|;
name|Result
name|expected
index|[]
init|=
operator|new
name|Result
index|[
name|reader
operator|.
name|maxDoc
argument_list|()
index|]
decl_stmt|;
for|for
control|(
name|int
name|doc
init|=
literal|0
init|;
name|doc
operator|<
name|reader
operator|.
name|maxDoc
argument_list|()
condition|;
name|doc
operator|++
control|)
block|{
name|Document
name|targetDoc
init|=
name|reader
operator|.
name|document
argument_list|(
name|doc
argument_list|)
decl_stmt|;
specifier|final
name|double
name|distance
decl_stmt|;
if|if
condition|(
name|targetDoc
operator|.
name|getField
argument_list|(
literal|"lat"
argument_list|)
operator|==
literal|null
condition|)
block|{
name|distance
operator|=
name|missingValue
expr_stmt|;
comment|// missing
block|}
else|else
block|{
name|double
name|docLatitude
init|=
name|targetDoc
operator|.
name|getField
argument_list|(
literal|"lat"
argument_list|)
operator|.
name|numericValue
argument_list|()
operator|.
name|doubleValue
argument_list|()
decl_stmt|;
name|double
name|docLongitude
init|=
name|targetDoc
operator|.
name|getField
argument_list|(
literal|"lon"
argument_list|)
operator|.
name|numericValue
argument_list|()
operator|.
name|doubleValue
argument_list|()
decl_stmt|;
name|distance
operator|=
name|GeoDistanceUtils
operator|.
name|haversin
argument_list|(
name|lat
argument_list|,
name|lon
argument_list|,
name|docLatitude
argument_list|,
name|docLongitude
argument_list|)
expr_stmt|;
block|}
name|int
name|id
init|=
name|targetDoc
operator|.
name|getField
argument_list|(
literal|"id"
argument_list|)
operator|.
name|numericValue
argument_list|()
operator|.
name|intValue
argument_list|()
decl_stmt|;
name|expected
index|[
name|doc
index|]
operator|=
operator|new
name|Result
argument_list|(
name|id
argument_list|,
name|distance
argument_list|)
expr_stmt|;
block|}
name|Arrays
operator|.
name|sort
argument_list|(
name|expected
argument_list|)
expr_stmt|;
comment|// randomize the topN a bit
name|int
name|topN
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|)
decl_stmt|;
comment|// sort by distance, then ID
name|SortField
name|distanceSort
init|=
name|LatLonPoint
operator|.
name|newDistanceSort
argument_list|(
literal|"field"
argument_list|,
name|lat
argument_list|,
name|lon
argument_list|)
decl_stmt|;
name|distanceSort
operator|.
name|setMissingValue
argument_list|(
name|missingValue
argument_list|)
expr_stmt|;
name|Sort
name|sort
init|=
operator|new
name|Sort
argument_list|(
name|distanceSort
argument_list|,
operator|new
name|SortField
argument_list|(
literal|"id"
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|INT
argument_list|)
argument_list|)
decl_stmt|;
name|TopDocs
name|topDocs
init|=
name|searcher
operator|.
name|search
argument_list|(
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|,
name|topN
argument_list|,
name|sort
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|resultNumber
init|=
literal|0
init|;
name|resultNumber
operator|<
name|topN
condition|;
name|resultNumber
operator|++
control|)
block|{
name|FieldDoc
name|fieldDoc
init|=
operator|(
name|FieldDoc
operator|)
name|topDocs
operator|.
name|scoreDocs
index|[
name|resultNumber
index|]
decl_stmt|;
name|Result
name|actual
init|=
operator|new
name|Result
argument_list|(
operator|(
name|Integer
operator|)
name|fieldDoc
operator|.
name|fields
index|[
literal|1
index|]
argument_list|,
operator|(
name|Double
operator|)
name|fieldDoc
operator|.
name|fields
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
index|[
name|resultNumber
index|]
argument_list|,
name|actual
argument_list|)
expr_stmt|;
block|}
comment|// get page2 with searchAfter()
if|if
condition|(
name|topN
operator|<
name|reader
operator|.
name|maxDoc
argument_list|()
condition|)
block|{
name|int
name|page2
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
name|reader
operator|.
name|maxDoc
argument_list|()
operator|-
name|topN
argument_list|)
decl_stmt|;
name|TopDocs
name|topDocs2
init|=
name|searcher
operator|.
name|searchAfter
argument_list|(
name|topDocs
operator|.
name|scoreDocs
index|[
name|topN
operator|-
literal|1
index|]
argument_list|,
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|,
name|page2
argument_list|,
name|sort
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|resultNumber
init|=
literal|0
init|;
name|resultNumber
operator|<
name|page2
condition|;
name|resultNumber
operator|++
control|)
block|{
name|FieldDoc
name|fieldDoc
init|=
operator|(
name|FieldDoc
operator|)
name|topDocs2
operator|.
name|scoreDocs
index|[
name|resultNumber
index|]
decl_stmt|;
name|Result
name|actual
init|=
operator|new
name|Result
argument_list|(
operator|(
name|Integer
operator|)
name|fieldDoc
operator|.
name|fields
index|[
literal|1
index|]
argument_list|,
operator|(
name|Double
operator|)
name|fieldDoc
operator|.
name|fields
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
index|[
name|topN
operator|+
name|resultNumber
index|]
argument_list|,
name|actual
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|writer
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
block|}
end_class

end_unit

