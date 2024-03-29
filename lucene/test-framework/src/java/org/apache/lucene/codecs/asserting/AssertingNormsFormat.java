begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.codecs.asserting
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|asserting
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
name|Collection
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
name|NormsConsumer
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
name|NormsFormat
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
name|NormsProducer
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
name|AssertingLeafReader
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
name|FieldInfo
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
name|NumericDocValues
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
name|util
operator|.
name|Accountable
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
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
import|;
end_import

begin_comment
comment|/**  * Just like the default but with additional asserts.  */
end_comment

begin_class
DECL|class|AssertingNormsFormat
specifier|public
class|class
name|AssertingNormsFormat
extends|extends
name|NormsFormat
block|{
DECL|field|in
specifier|private
specifier|final
name|NormsFormat
name|in
init|=
name|TestUtil
operator|.
name|getDefaultCodec
argument_list|()
operator|.
name|normsFormat
argument_list|()
decl_stmt|;
annotation|@
name|Override
DECL|method|normsConsumer
specifier|public
name|NormsConsumer
name|normsConsumer
parameter_list|(
name|SegmentWriteState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
name|NormsConsumer
name|consumer
init|=
name|in
operator|.
name|normsConsumer
argument_list|(
name|state
argument_list|)
decl_stmt|;
assert|assert
name|consumer
operator|!=
literal|null
assert|;
return|return
operator|new
name|AssertingNormsConsumer
argument_list|(
name|consumer
argument_list|,
name|state
operator|.
name|segmentInfo
operator|.
name|maxDoc
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|normsProducer
specifier|public
name|NormsProducer
name|normsProducer
parameter_list|(
name|SegmentReadState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|state
operator|.
name|fieldInfos
operator|.
name|hasNorms
argument_list|()
assert|;
name|NormsProducer
name|producer
init|=
name|in
operator|.
name|normsProducer
argument_list|(
name|state
argument_list|)
decl_stmt|;
assert|assert
name|producer
operator|!=
literal|null
assert|;
return|return
operator|new
name|AssertingNormsProducer
argument_list|(
name|producer
argument_list|,
name|state
operator|.
name|segmentInfo
operator|.
name|maxDoc
argument_list|()
argument_list|)
return|;
block|}
DECL|class|AssertingNormsConsumer
specifier|static
class|class
name|AssertingNormsConsumer
extends|extends
name|NormsConsumer
block|{
DECL|field|in
specifier|private
specifier|final
name|NormsConsumer
name|in
decl_stmt|;
DECL|field|maxDoc
specifier|private
specifier|final
name|int
name|maxDoc
decl_stmt|;
DECL|method|AssertingNormsConsumer
name|AssertingNormsConsumer
parameter_list|(
name|NormsConsumer
name|in
parameter_list|,
name|int
name|maxDoc
parameter_list|)
block|{
name|this
operator|.
name|in
operator|=
name|in
expr_stmt|;
name|this
operator|.
name|maxDoc
operator|=
name|maxDoc
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|addNormsField
specifier|public
name|void
name|addNormsField
parameter_list|(
name|FieldInfo
name|field
parameter_list|,
name|NormsProducer
name|valuesProducer
parameter_list|)
throws|throws
name|IOException
block|{
name|NumericDocValues
name|values
init|=
name|valuesProducer
operator|.
name|getNorms
argument_list|(
name|field
argument_list|)
decl_stmt|;
name|int
name|docID
decl_stmt|;
name|int
name|lastDocID
init|=
operator|-
literal|1
decl_stmt|;
while|while
condition|(
operator|(
name|docID
operator|=
name|values
operator|.
name|nextDoc
argument_list|()
operator|)
operator|!=
name|NO_MORE_DOCS
condition|)
block|{
assert|assert
name|docID
operator|>=
literal|0
operator|&&
name|docID
operator|<
name|maxDoc
assert|;
assert|assert
name|docID
operator|>
name|lastDocID
assert|;
name|lastDocID
operator|=
name|docID
expr_stmt|;
name|long
name|value
init|=
name|values
operator|.
name|longValue
argument_list|()
decl_stmt|;
block|}
name|in
operator|.
name|addNormsField
argument_list|(
name|field
argument_list|,
name|valuesProducer
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// close again
block|}
block|}
DECL|class|AssertingNormsProducer
specifier|static
class|class
name|AssertingNormsProducer
extends|extends
name|NormsProducer
block|{
DECL|field|in
specifier|private
specifier|final
name|NormsProducer
name|in
decl_stmt|;
DECL|field|maxDoc
specifier|private
specifier|final
name|int
name|maxDoc
decl_stmt|;
DECL|method|AssertingNormsProducer
name|AssertingNormsProducer
parameter_list|(
name|NormsProducer
name|in
parameter_list|,
name|int
name|maxDoc
parameter_list|)
block|{
name|this
operator|.
name|in
operator|=
name|in
expr_stmt|;
name|this
operator|.
name|maxDoc
operator|=
name|maxDoc
expr_stmt|;
comment|// do a few simple checks on init
assert|assert
name|toString
argument_list|()
operator|!=
literal|null
assert|;
assert|assert
name|ramBytesUsed
argument_list|()
operator|>=
literal|0
assert|;
assert|assert
name|getChildResources
argument_list|()
operator|!=
literal|null
assert|;
block|}
annotation|@
name|Override
DECL|method|getNorms
specifier|public
name|NumericDocValues
name|getNorms
parameter_list|(
name|FieldInfo
name|field
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|field
operator|.
name|hasNorms
argument_list|()
assert|;
name|NumericDocValues
name|values
init|=
name|in
operator|.
name|getNorms
argument_list|(
name|field
argument_list|)
decl_stmt|;
assert|assert
name|values
operator|!=
literal|null
assert|;
return|return
operator|new
name|AssertingLeafReader
operator|.
name|AssertingNumericDocValues
argument_list|(
name|values
argument_list|,
name|maxDoc
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// close again
block|}
annotation|@
name|Override
DECL|method|ramBytesUsed
specifier|public
name|long
name|ramBytesUsed
parameter_list|()
block|{
name|long
name|v
init|=
name|in
operator|.
name|ramBytesUsed
argument_list|()
decl_stmt|;
assert|assert
name|v
operator|>=
literal|0
assert|;
return|return
name|v
return|;
block|}
annotation|@
name|Override
DECL|method|getChildResources
specifier|public
name|Collection
argument_list|<
name|Accountable
argument_list|>
name|getChildResources
parameter_list|()
block|{
name|Collection
argument_list|<
name|Accountable
argument_list|>
name|res
init|=
name|in
operator|.
name|getChildResources
argument_list|()
decl_stmt|;
name|TestUtil
operator|.
name|checkReadOnly
argument_list|(
name|res
argument_list|)
expr_stmt|;
return|return
name|res
return|;
block|}
annotation|@
name|Override
DECL|method|checkIntegrity
specifier|public
name|void
name|checkIntegrity
parameter_list|()
throws|throws
name|IOException
block|{
name|in
operator|.
name|checkIntegrity
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getMergeInstance
specifier|public
name|NormsProducer
name|getMergeInstance
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|new
name|AssertingNormsProducer
argument_list|(
name|in
operator|.
name|getMergeInstance
argument_list|()
argument_list|,
name|maxDoc
argument_list|)
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
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|"("
operator|+
name|in
operator|.
name|toString
argument_list|()
operator|+
literal|")"
return|;
block|}
block|}
block|}
end_class

end_unit

