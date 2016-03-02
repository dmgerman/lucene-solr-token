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
name|BitSet
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
name|codecs
operator|.
name|FilterCodec
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
name|codecs
operator|.
name|PointFormat
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
name|codecs
operator|.
name|PointReader
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
name|codecs
operator|.
name|PointWriter
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
name|codecs
operator|.
name|lucene60
operator|.
name|Lucene60PointReader
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
name|codecs
operator|.
name|lucene60
operator|.
name|Lucene60PointWriter
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
name|index
operator|.
name|SegmentReadState
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
name|SegmentWriteState
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
name|TestUtil
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
name|bkd
operator|.
name|BKDWriter
import|;
end_import

begin_comment
comment|/** Simple tests for {@link LatLonPoint#newDistanceQuery} */
end_comment

begin_class
DECL|class|TestLatLonPointDistanceQuery
specifier|public
class|class
name|TestLatLonPointDistanceQuery
extends|extends
name|LuceneTestCase
block|{
comment|/** test we can search for a point */
DECL|method|testBasics
specifier|public
name|void
name|testBasics
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
comment|// add a doc with a location
name|Document
name|document
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|document
operator|.
name|add
argument_list|(
operator|new
name|LatLonPoint
argument_list|(
literal|"field"
argument_list|,
literal|18.313694
argument_list|,
operator|-
literal|65.227444
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|document
argument_list|)
expr_stmt|;
comment|// search within 50km and verify we found our doc
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
name|newSearcher
argument_list|(
name|reader
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|searcher
operator|.
name|count
argument_list|(
name|LatLonPoint
operator|.
name|newDistanceQuery
argument_list|(
literal|"field"
argument_list|,
literal|18
argument_list|,
operator|-
literal|65
argument_list|,
literal|50_000
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
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
comment|/** negative distance queries are not allowed */
DECL|method|testNegativeRadius
specifier|public
name|void
name|testNegativeRadius
parameter_list|()
block|{
name|IllegalArgumentException
name|expected
init|=
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|LatLonPoint
operator|.
name|newDistanceQuery
argument_list|(
literal|"field"
argument_list|,
literal|18
argument_list|,
literal|19
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|expected
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"radiusMeters"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|expected
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"is invalid"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** NaN distance queries are not allowed */
DECL|method|testNaNRadius
specifier|public
name|void
name|testNaNRadius
parameter_list|()
block|{
name|IllegalArgumentException
name|expected
init|=
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|LatLonPoint
operator|.
name|newDistanceQuery
argument_list|(
literal|"field"
argument_list|,
literal|18
argument_list|,
literal|19
argument_list|,
name|Double
operator|.
name|NaN
argument_list|)
expr_stmt|;
block|}
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|expected
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"radiusMeters"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|expected
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"is invalid"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Inf distance queries are not allowed */
DECL|method|testInfRadius
specifier|public
name|void
name|testInfRadius
parameter_list|()
block|{
name|IllegalArgumentException
name|expected
decl_stmt|;
name|expected
operator|=
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|LatLonPoint
operator|.
name|newDistanceQuery
argument_list|(
literal|"field"
argument_list|,
literal|18
argument_list|,
literal|19
argument_list|,
name|Double
operator|.
name|POSITIVE_INFINITY
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|expected
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"radiusMeters"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|expected
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"is invalid"
argument_list|)
argument_list|)
expr_stmt|;
name|expected
operator|=
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|LatLonPoint
operator|.
name|newDistanceQuery
argument_list|(
literal|"field"
argument_list|,
literal|18
argument_list|,
literal|19
argument_list|,
name|Double
operator|.
name|NEGATIVE_INFINITY
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|expected
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"radiusMeters"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|expected
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"is invalid"
argument_list|)
argument_list|)
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
name|IndexWriterConfig
name|iwc
init|=
name|newIndexWriterConfig
argument_list|()
decl_stmt|;
name|int
name|pointsInLeaf
init|=
literal|2
operator|+
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|4
argument_list|)
decl_stmt|;
name|iwc
operator|.
name|setCodec
argument_list|(
operator|new
name|FilterCodec
argument_list|(
literal|"Lucene60"
argument_list|,
name|TestUtil
operator|.
name|getDefaultCodec
argument_list|()
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|PointFormat
name|pointFormat
parameter_list|()
block|{
return|return
operator|new
name|PointFormat
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|PointWriter
name|fieldsWriter
parameter_list|(
name|SegmentWriteState
name|writeState
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|Lucene60PointWriter
argument_list|(
name|writeState
argument_list|,
name|pointsInLeaf
argument_list|,
name|BKDWriter
operator|.
name|DEFAULT_MAX_MB_SORT_IN_HEAP
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|PointReader
name|fieldsReader
parameter_list|(
name|SegmentReadState
name|readState
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|Lucene60PointReader
argument_list|(
name|readState
argument_list|)
return|;
block|}
block|}
return|;
block|}
block|}
argument_list|)
expr_stmt|;
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
argument_list|,
name|iwc
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
name|radius
init|=
literal|50000000
operator|*
name|random
argument_list|()
operator|.
name|nextDouble
argument_list|()
decl_stmt|;
name|BitSet
name|expected
init|=
operator|new
name|BitSet
argument_list|()
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
name|double
name|docLatitude
init|=
name|reader
operator|.
name|document
argument_list|(
name|doc
argument_list|)
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
name|reader
operator|.
name|document
argument_list|(
name|doc
argument_list|)
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
name|double
name|distance
init|=
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
decl_stmt|;
if|if
condition|(
name|distance
operator|<=
name|radius
condition|)
block|{
name|expected
operator|.
name|set
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
name|TopDocs
name|topDocs
init|=
name|searcher
operator|.
name|search
argument_list|(
name|LatLonPoint
operator|.
name|newDistanceQuery
argument_list|(
literal|"field"
argument_list|,
name|lat
argument_list|,
name|lon
argument_list|,
name|radius
argument_list|)
argument_list|,
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|,
name|Sort
operator|.
name|INDEXORDER
argument_list|)
decl_stmt|;
name|BitSet
name|actual
init|=
operator|new
name|BitSet
argument_list|()
decl_stmt|;
for|for
control|(
name|ScoreDoc
name|doc
range|:
name|topDocs
operator|.
name|scoreDocs
control|)
block|{
name|actual
operator|.
name|set
argument_list|(
name|doc
operator|.
name|doc
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|actual
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AssertionError
name|e
parameter_list|)
block|{
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
name|double
name|docLatitude
init|=
name|reader
operator|.
name|document
argument_list|(
name|doc
argument_list|)
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
name|reader
operator|.
name|document
argument_list|(
name|doc
argument_list|)
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
name|double
name|distance
init|=
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
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|""
operator|+
name|doc
operator|+
literal|": ("
operator|+
name|docLatitude
operator|+
literal|","
operator|+
name|docLongitude
operator|+
literal|"), distance="
operator|+
name|distance
argument_list|)
expr_stmt|;
block|}
throw|throw
name|e
throw|;
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

