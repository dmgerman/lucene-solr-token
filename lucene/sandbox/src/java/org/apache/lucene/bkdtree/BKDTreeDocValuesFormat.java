begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.bkdtree
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|bkdtree
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|DocValuesConsumer
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
name|DocValuesFormat
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
name|DocValuesProducer
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
name|lucene50
operator|.
name|Lucene50DocValuesFormat
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

begin_comment
comment|/**  * A {@link DocValuesFormat} to efficiently index geo-spatial lat/lon points  * from {@link BKDPointField} for fast bounding-box ({@link BKDPointInBBoxQuery})  * and polygon ({@link BKDPointInPolygonQuery}) queries.  *  *<p>This wraps {@link Lucene50DocValuesFormat}, but saves its own BKD tree  * structures to disk for fast query-time intersection. See<a  * href="https://www.cs.duke.edu/~pankaj/publications/papers/bkd-sstd.pdf">this paper</a>  * for details.  *  *<p>The BKD tree slices up 2D (lat/lon) space into smaller and  * smaller rectangles, until the smallest rectangles have approximately  * between X/2 and X (X default is 1024) points in them, at which point  * such leaf cells are written as a block to disk, while the index tree  * structure records how space was sub-divided is loaded into HEAP  * at search time.  At search time, the tree is recursed based on whether  * each of left or right child overlap with the query shape, and once  * a leaf block is reached, all documents in that leaf block are collected  * if the cell is fully enclosed by the query shape, or filtered and then  * collected, if not.  *  *<p>The index is also quite compact, because docs only appear once in  * the tree (no "prefix terms").  *  *<p>In addition to the files written by {@link Lucene50DocValuesFormat}, this format writes:  *<ol>  *<li><tt>.kdd</tt>: BKD leaf data and index</li>  *<li><tt>.kdm</tt>: BKD metadata</li>  *</ol>  *  *<p>The disk format is experimental and free to change suddenly, and this code likely has new and exciting bugs!  *  * @lucene.experimental */
end_comment

begin_class
DECL|class|BKDTreeDocValuesFormat
specifier|public
class|class
name|BKDTreeDocValuesFormat
extends|extends
name|DocValuesFormat
block|{
DECL|field|DATA_CODEC_NAME
specifier|static
specifier|final
name|String
name|DATA_CODEC_NAME
init|=
literal|"BKDData"
decl_stmt|;
DECL|field|DATA_VERSION_START
specifier|static
specifier|final
name|int
name|DATA_VERSION_START
init|=
literal|0
decl_stmt|;
DECL|field|DATA_VERSION_CURRENT
specifier|static
specifier|final
name|int
name|DATA_VERSION_CURRENT
init|=
name|DATA_VERSION_START
decl_stmt|;
DECL|field|DATA_EXTENSION
specifier|static
specifier|final
name|String
name|DATA_EXTENSION
init|=
literal|"kdd"
decl_stmt|;
DECL|field|META_CODEC_NAME
specifier|static
specifier|final
name|String
name|META_CODEC_NAME
init|=
literal|"BKDMeta"
decl_stmt|;
DECL|field|META_VERSION_START
specifier|static
specifier|final
name|int
name|META_VERSION_START
init|=
literal|0
decl_stmt|;
DECL|field|META_VERSION_CURRENT
specifier|static
specifier|final
name|int
name|META_VERSION_CURRENT
init|=
name|META_VERSION_START
decl_stmt|;
DECL|field|META_EXTENSION
specifier|static
specifier|final
name|String
name|META_EXTENSION
init|=
literal|"kdm"
decl_stmt|;
DECL|field|maxPointsInLeafNode
specifier|private
specifier|final
name|int
name|maxPointsInLeafNode
decl_stmt|;
DECL|field|maxPointsSortInHeap
specifier|private
specifier|final
name|int
name|maxPointsSortInHeap
decl_stmt|;
DECL|field|delegate
specifier|private
specifier|final
name|DocValuesFormat
name|delegate
init|=
operator|new
name|Lucene50DocValuesFormat
argument_list|()
decl_stmt|;
comment|/** Default constructor */
DECL|method|BKDTreeDocValuesFormat
specifier|public
name|BKDTreeDocValuesFormat
parameter_list|()
block|{
name|this
argument_list|(
name|BKDTreeWriter
operator|.
name|DEFAULT_MAX_POINTS_IN_LEAF_NODE
argument_list|,
name|BKDTreeWriter
operator|.
name|DEFAULT_MAX_POINTS_SORT_IN_HEAP
argument_list|)
expr_stmt|;
block|}
comment|/** Creates this with custom configuration.    *    * @param maxPointsInLeafNode Maximum number of points in each leaf cell.  Smaller values create a deeper tree with larger in-heap index and possibly    *    faster searching.  The default is 1024.    * @param maxPointsSortInHeap Maximum number of points where in-heap sort can be used.  When the number of points exceeds this, a (slower)    *    offline sort is used.  The default is 128 * 1024.    *    * @lucene.experimental */
DECL|method|BKDTreeDocValuesFormat
specifier|public
name|BKDTreeDocValuesFormat
parameter_list|(
name|int
name|maxPointsInLeafNode
parameter_list|,
name|int
name|maxPointsSortInHeap
parameter_list|)
block|{
name|super
argument_list|(
literal|"BKDTree"
argument_list|)
expr_stmt|;
name|BKDTreeWriter
operator|.
name|verifyParams
argument_list|(
name|maxPointsInLeafNode
argument_list|,
name|maxPointsSortInHeap
argument_list|)
expr_stmt|;
name|this
operator|.
name|maxPointsInLeafNode
operator|=
name|maxPointsInLeafNode
expr_stmt|;
name|this
operator|.
name|maxPointsSortInHeap
operator|=
name|maxPointsSortInHeap
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|fieldsConsumer
specifier|public
name|DocValuesConsumer
name|fieldsConsumer
parameter_list|(
specifier|final
name|SegmentWriteState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|BKDTreeDocValuesConsumer
argument_list|(
name|state
operator|.
name|directory
argument_list|,
name|state
operator|.
name|segmentInfo
operator|.
name|name
argument_list|,
name|delegate
operator|.
name|fieldsConsumer
argument_list|(
name|state
argument_list|)
argument_list|,
name|state
argument_list|,
name|maxPointsInLeafNode
argument_list|,
name|maxPointsSortInHeap
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|fieldsProducer
specifier|public
name|DocValuesProducer
name|fieldsProducer
parameter_list|(
name|SegmentReadState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|BKDTreeDocValuesProducer
argument_list|(
name|delegate
operator|.
name|fieldsProducer
argument_list|(
name|state
argument_list|)
argument_list|,
name|state
argument_list|)
return|;
block|}
block|}
end_class

end_unit

