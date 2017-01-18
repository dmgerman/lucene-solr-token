begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.codecs.lucene60
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|lucene60
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
name|codecs
operator|.
name|Codec
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
name|PointsFormat
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
name|PointsReader
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
name|PointsWriter
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
name|BinaryPoint
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
name|index
operator|.
name|BasePointsFormatTestCase
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
name|LeafReader
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
name|PointValues
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
name|index
operator|.
name|PointValues
operator|.
name|IntersectVisitor
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
name|PointValues
operator|.
name|Relation
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
name|StringHelper
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
name|bkd
operator|.
name|BKDWriter
import|;
end_import

begin_comment
comment|/**  * Tests Lucene60PointsFormat  */
end_comment

begin_class
DECL|class|TestLucene60PointsFormat
specifier|public
class|class
name|TestLucene60PointsFormat
extends|extends
name|BasePointsFormatTestCase
block|{
DECL|field|codec
specifier|private
specifier|final
name|Codec
name|codec
decl_stmt|;
DECL|field|maxPointsInLeafNode
specifier|private
specifier|final
name|int
name|maxPointsInLeafNode
decl_stmt|;
DECL|method|TestLucene60PointsFormat
specifier|public
name|TestLucene60PointsFormat
parameter_list|()
block|{
comment|// standard issue
name|Codec
name|defaultCodec
init|=
name|TestUtil
operator|.
name|getDefaultCodec
argument_list|()
decl_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
comment|// randomize parameters
name|maxPointsInLeafNode
operator|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|50
argument_list|,
literal|500
argument_list|)
expr_stmt|;
name|double
name|maxMBSortInHeap
init|=
literal|3.0
operator|+
operator|(
literal|3
operator|*
name|random
argument_list|()
operator|.
name|nextDouble
argument_list|()
operator|)
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
literal|"TEST: using Lucene60PointsFormat with maxPointsInLeafNode="
operator|+
name|maxPointsInLeafNode
operator|+
literal|" and maxMBSortInHeap="
operator|+
name|maxMBSortInHeap
argument_list|)
expr_stmt|;
block|}
comment|// sneaky impersonation!
name|codec
operator|=
operator|new
name|FilterCodec
argument_list|(
name|defaultCodec
operator|.
name|getName
argument_list|()
argument_list|,
name|defaultCodec
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|PointsFormat
name|pointsFormat
parameter_list|()
block|{
return|return
operator|new
name|PointsFormat
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|PointsWriter
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
name|Lucene60PointsWriter
argument_list|(
name|writeState
argument_list|,
name|maxPointsInLeafNode
argument_list|,
name|maxMBSortInHeap
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|PointsReader
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
name|Lucene60PointsReader
argument_list|(
name|readState
argument_list|)
return|;
block|}
block|}
return|;
block|}
block|}
expr_stmt|;
block|}
else|else
block|{
comment|// standard issue
name|codec
operator|=
name|defaultCodec
expr_stmt|;
name|maxPointsInLeafNode
operator|=
name|BKDWriter
operator|.
name|DEFAULT_MAX_POINTS_IN_LEAF_NODE
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getCodec
specifier|protected
name|Codec
name|getCodec
parameter_list|()
block|{
return|return
name|codec
return|;
block|}
annotation|@
name|Override
DECL|method|testMergeStability
specifier|public
name|void
name|testMergeStability
parameter_list|()
throws|throws
name|Exception
block|{
name|assumeFalse
argument_list|(
literal|"TODO: mess with the parameters and test gets angry!"
argument_list|,
name|codec
operator|instanceof
name|FilterCodec
argument_list|)
expr_stmt|;
name|super
operator|.
name|testMergeStability
argument_list|()
expr_stmt|;
block|}
DECL|method|testEstimatePointCount
specifier|public
name|void
name|testEstimatePointCount
parameter_list|()
throws|throws
name|IOException
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|w
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|newIndexWriterConfig
argument_list|()
argument_list|)
decl_stmt|;
name|byte
index|[]
name|pointValue
init|=
operator|new
name|byte
index|[
literal|3
index|]
decl_stmt|;
name|byte
index|[]
name|uniquePointValue
init|=
operator|new
name|byte
index|[
literal|3
index|]
decl_stmt|;
name|random
argument_list|()
operator|.
name|nextBytes
argument_list|(
name|uniquePointValue
argument_list|)
expr_stmt|;
specifier|final
name|int
name|numDocs
init|=
name|atLeast
argument_list|(
literal|10000
argument_list|)
decl_stmt|;
comment|// make sure we have several leaves
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
operator|++
name|i
control|)
block|{
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
if|if
condition|(
name|i
operator|==
name|numDocs
operator|/
literal|2
condition|)
block|{
name|doc
operator|.
name|add
argument_list|(
operator|new
name|BinaryPoint
argument_list|(
literal|"f"
argument_list|,
name|uniquePointValue
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
do|do
block|{
name|random
argument_list|()
operator|.
name|nextBytes
argument_list|(
name|pointValue
argument_list|)
expr_stmt|;
block|}
do|while
condition|(
name|Arrays
operator|.
name|equals
argument_list|(
name|pointValue
argument_list|,
name|uniquePointValue
argument_list|)
condition|)
do|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|BinaryPoint
argument_list|(
literal|"f"
argument_list|,
name|pointValue
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|w
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|)
expr_stmt|;
specifier|final
name|IndexReader
name|r
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|w
argument_list|)
decl_stmt|;
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
specifier|final
name|LeafReader
name|lr
init|=
name|getOnlyLeafReader
argument_list|(
name|r
argument_list|)
decl_stmt|;
name|PointValues
name|points
init|=
name|lr
operator|.
name|getPointValues
argument_list|(
literal|"f"
argument_list|)
decl_stmt|;
comment|// If all points match, then the point count is numLeaves * maxPointsInLeafNode
specifier|final
name|int
name|numLeaves
init|=
operator|(
name|int
operator|)
name|Math
operator|.
name|ceil
argument_list|(
operator|(
name|double
operator|)
name|numDocs
operator|/
name|maxPointsInLeafNode
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|numLeaves
operator|*
name|maxPointsInLeafNode
argument_list|,
name|points
operator|.
name|estimatePointCount
argument_list|(
operator|new
name|IntersectVisitor
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|visit
parameter_list|(
name|int
name|docID
parameter_list|,
name|byte
index|[]
name|packedValue
parameter_list|)
throws|throws
name|IOException
block|{}
annotation|@
name|Override
specifier|public
name|void
name|visit
parameter_list|(
name|int
name|docID
parameter_list|)
throws|throws
name|IOException
block|{}
annotation|@
name|Override
specifier|public
name|Relation
name|compare
parameter_list|(
name|byte
index|[]
name|minPackedValue
parameter_list|,
name|byte
index|[]
name|maxPackedValue
parameter_list|)
block|{
return|return
name|Relation
operator|.
name|CELL_INSIDE_QUERY
return|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
comment|// Return 0 if no points match
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|points
operator|.
name|estimatePointCount
argument_list|(
operator|new
name|IntersectVisitor
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|visit
parameter_list|(
name|int
name|docID
parameter_list|,
name|byte
index|[]
name|packedValue
parameter_list|)
throws|throws
name|IOException
block|{}
annotation|@
name|Override
specifier|public
name|void
name|visit
parameter_list|(
name|int
name|docID
parameter_list|)
throws|throws
name|IOException
block|{}
annotation|@
name|Override
specifier|public
name|Relation
name|compare
parameter_list|(
name|byte
index|[]
name|minPackedValue
parameter_list|,
name|byte
index|[]
name|maxPackedValue
parameter_list|)
block|{
return|return
name|Relation
operator|.
name|CELL_OUTSIDE_QUERY
return|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
comment|// If only one point matches, then the point count is (maxPointsInLeafNode + 1) / 2
name|assertEquals
argument_list|(
operator|(
name|maxPointsInLeafNode
operator|+
literal|1
operator|)
operator|/
literal|2
argument_list|,
name|points
operator|.
name|estimatePointCount
argument_list|(
operator|new
name|IntersectVisitor
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|visit
parameter_list|(
name|int
name|docID
parameter_list|,
name|byte
index|[]
name|packedValue
parameter_list|)
throws|throws
name|IOException
block|{}
annotation|@
name|Override
specifier|public
name|void
name|visit
parameter_list|(
name|int
name|docID
parameter_list|)
throws|throws
name|IOException
block|{}
annotation|@
name|Override
specifier|public
name|Relation
name|compare
parameter_list|(
name|byte
index|[]
name|minPackedValue
parameter_list|,
name|byte
index|[]
name|maxPackedValue
parameter_list|)
block|{
if|if
condition|(
name|StringHelper
operator|.
name|compare
argument_list|(
literal|3
argument_list|,
name|uniquePointValue
argument_list|,
literal|0
argument_list|,
name|maxPackedValue
argument_list|,
literal|0
argument_list|)
operator|>
literal|0
operator|||
name|StringHelper
operator|.
name|compare
argument_list|(
literal|3
argument_list|,
name|uniquePointValue
argument_list|,
literal|0
argument_list|,
name|minPackedValue
argument_list|,
literal|0
argument_list|)
operator|<
literal|0
condition|)
block|{
return|return
name|Relation
operator|.
name|CELL_OUTSIDE_QUERY
return|;
block|}
return|return
name|Relation
operator|.
name|CELL_CROSSES_QUERY
return|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
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
comment|// The tree is always balanced in the N dims case, and leaves are
comment|// not all full so things are a bit different
DECL|method|testEstimatePointCount2Dims
specifier|public
name|void
name|testEstimatePointCount2Dims
parameter_list|()
throws|throws
name|IOException
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|w
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|newIndexWriterConfig
argument_list|()
argument_list|)
decl_stmt|;
name|byte
index|[]
index|[]
name|pointValue
init|=
operator|new
name|byte
index|[
literal|2
index|]
index|[]
decl_stmt|;
name|pointValue
index|[
literal|0
index|]
operator|=
operator|new
name|byte
index|[
literal|3
index|]
expr_stmt|;
name|pointValue
index|[
literal|1
index|]
operator|=
operator|new
name|byte
index|[
literal|3
index|]
expr_stmt|;
name|byte
index|[]
index|[]
name|uniquePointValue
init|=
operator|new
name|byte
index|[
literal|2
index|]
index|[]
decl_stmt|;
name|uniquePointValue
index|[
literal|0
index|]
operator|=
operator|new
name|byte
index|[
literal|3
index|]
expr_stmt|;
name|uniquePointValue
index|[
literal|1
index|]
operator|=
operator|new
name|byte
index|[
literal|3
index|]
expr_stmt|;
name|random
argument_list|()
operator|.
name|nextBytes
argument_list|(
name|uniquePointValue
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|random
argument_list|()
operator|.
name|nextBytes
argument_list|(
name|uniquePointValue
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
specifier|final
name|int
name|numDocs
init|=
name|atLeast
argument_list|(
literal|10000
argument_list|)
decl_stmt|;
comment|// make sure we have several leaves
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
operator|++
name|i
control|)
block|{
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
if|if
condition|(
name|i
operator|==
name|numDocs
operator|/
literal|2
condition|)
block|{
name|doc
operator|.
name|add
argument_list|(
operator|new
name|BinaryPoint
argument_list|(
literal|"f"
argument_list|,
name|uniquePointValue
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
do|do
block|{
name|random
argument_list|()
operator|.
name|nextBytes
argument_list|(
name|pointValue
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|random
argument_list|()
operator|.
name|nextBytes
argument_list|(
name|pointValue
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
do|while
condition|(
name|Arrays
operator|.
name|equals
argument_list|(
name|pointValue
index|[
literal|0
index|]
argument_list|,
name|uniquePointValue
index|[
literal|0
index|]
argument_list|)
operator|||
name|Arrays
operator|.
name|equals
argument_list|(
name|pointValue
index|[
literal|1
index|]
argument_list|,
name|uniquePointValue
index|[
literal|1
index|]
argument_list|)
condition|)
do|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|BinaryPoint
argument_list|(
literal|"f"
argument_list|,
name|pointValue
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|w
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|)
expr_stmt|;
specifier|final
name|IndexReader
name|r
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|w
argument_list|)
decl_stmt|;
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
specifier|final
name|LeafReader
name|lr
init|=
name|getOnlyLeafReader
argument_list|(
name|r
argument_list|)
decl_stmt|;
name|PointValues
name|points
init|=
name|lr
operator|.
name|getPointValues
argument_list|(
literal|"f"
argument_list|)
decl_stmt|;
comment|// With>1 dims, the tree is balanced
name|int
name|actualMaxPointsInLeafNode
init|=
name|numDocs
decl_stmt|;
while|while
condition|(
name|actualMaxPointsInLeafNode
operator|>
name|maxPointsInLeafNode
condition|)
block|{
name|actualMaxPointsInLeafNode
operator|=
operator|(
name|actualMaxPointsInLeafNode
operator|+
literal|1
operator|)
operator|/
literal|2
expr_stmt|;
block|}
comment|// If all points match, then the point count is numLeaves * maxPointsInLeafNode
specifier|final
name|int
name|numLeaves
init|=
name|Integer
operator|.
name|highestOneBit
argument_list|(
operator|(
name|numDocs
operator|-
literal|1
operator|)
operator|/
name|actualMaxPointsInLeafNode
argument_list|)
operator|<<
literal|1
decl_stmt|;
name|assertEquals
argument_list|(
name|numLeaves
operator|*
name|actualMaxPointsInLeafNode
argument_list|,
name|points
operator|.
name|estimatePointCount
argument_list|(
operator|new
name|IntersectVisitor
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|visit
parameter_list|(
name|int
name|docID
parameter_list|,
name|byte
index|[]
name|packedValue
parameter_list|)
throws|throws
name|IOException
block|{}
annotation|@
name|Override
specifier|public
name|void
name|visit
parameter_list|(
name|int
name|docID
parameter_list|)
throws|throws
name|IOException
block|{}
annotation|@
name|Override
specifier|public
name|Relation
name|compare
parameter_list|(
name|byte
index|[]
name|minPackedValue
parameter_list|,
name|byte
index|[]
name|maxPackedValue
parameter_list|)
block|{
return|return
name|Relation
operator|.
name|CELL_INSIDE_QUERY
return|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
comment|// Return 0 if no points match
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|points
operator|.
name|estimatePointCount
argument_list|(
operator|new
name|IntersectVisitor
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|visit
parameter_list|(
name|int
name|docID
parameter_list|,
name|byte
index|[]
name|packedValue
parameter_list|)
throws|throws
name|IOException
block|{}
annotation|@
name|Override
specifier|public
name|void
name|visit
parameter_list|(
name|int
name|docID
parameter_list|)
throws|throws
name|IOException
block|{}
annotation|@
name|Override
specifier|public
name|Relation
name|compare
parameter_list|(
name|byte
index|[]
name|minPackedValue
parameter_list|,
name|byte
index|[]
name|maxPackedValue
parameter_list|)
block|{
return|return
name|Relation
operator|.
name|CELL_OUTSIDE_QUERY
return|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
comment|// If only one point matches, then the point count is (actualMaxPointsInLeafNode + 1) / 2
name|assertEquals
argument_list|(
operator|(
name|actualMaxPointsInLeafNode
operator|+
literal|1
operator|)
operator|/
literal|2
argument_list|,
name|points
operator|.
name|estimatePointCount
argument_list|(
operator|new
name|IntersectVisitor
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|visit
parameter_list|(
name|int
name|docID
parameter_list|,
name|byte
index|[]
name|packedValue
parameter_list|)
throws|throws
name|IOException
block|{}
annotation|@
name|Override
specifier|public
name|void
name|visit
parameter_list|(
name|int
name|docID
parameter_list|)
throws|throws
name|IOException
block|{}
annotation|@
name|Override
specifier|public
name|Relation
name|compare
parameter_list|(
name|byte
index|[]
name|minPackedValue
parameter_list|,
name|byte
index|[]
name|maxPackedValue
parameter_list|)
block|{
for|for
control|(
name|int
name|dim
init|=
literal|0
init|;
name|dim
operator|<
literal|2
condition|;
operator|++
name|dim
control|)
block|{
if|if
condition|(
name|StringHelper
operator|.
name|compare
argument_list|(
literal|3
argument_list|,
name|uniquePointValue
index|[
literal|0
index|]
argument_list|,
literal|0
argument_list|,
name|maxPackedValue
argument_list|,
name|dim
operator|*
literal|3
argument_list|)
operator|>
literal|0
operator|||
name|StringHelper
operator|.
name|compare
argument_list|(
literal|3
argument_list|,
name|uniquePointValue
index|[
literal|0
index|]
argument_list|,
literal|0
argument_list|,
name|minPackedValue
argument_list|,
name|dim
operator|*
literal|3
argument_list|)
operator|<
literal|0
condition|)
block|{
return|return
name|Relation
operator|.
name|CELL_OUTSIDE_QUERY
return|;
block|}
block|}
return|return
name|Relation
operator|.
name|CELL_CROSSES_QUERY
return|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
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
block|}
end_class

end_unit

