begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.codecs.lucene41.values
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|lucene41
operator|.
name|values
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
name|Closeable
import|;
end_import

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
name|SimpleDVProducer
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
name|BinaryDocValues
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
name|DocValues
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
name|IndexFileNames
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
name|SegmentInfo
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
name|SortedDocValues
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
name|CompoundFileDirectory
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
name|IOContext
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
name|IOUtils
import|;
end_import

begin_comment
comment|// nocommit
end_comment

begin_class
DECL|class|Lucene41DocValuesProducer
specifier|public
class|class
name|Lucene41DocValuesProducer
extends|extends
name|SimpleDVProducer
block|{
DECL|field|cfs
specifier|private
specifier|final
name|CompoundFileDirectory
name|cfs
decl_stmt|;
comment|// nocommit: remove this
DECL|field|info
specifier|private
specifier|final
name|SegmentInfo
name|info
decl_stmt|;
DECL|field|context
specifier|private
specifier|final
name|IOContext
name|context
decl_stmt|;
DECL|method|Lucene41DocValuesProducer
specifier|public
name|Lucene41DocValuesProducer
parameter_list|(
name|SegmentReadState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|String
name|suffix
decl_stmt|;
if|if
condition|(
name|state
operator|.
name|segmentSuffix
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
name|suffix
operator|=
name|Lucene41DocValuesConsumer
operator|.
name|DV_SEGMENT_SUFFIX
expr_stmt|;
block|}
else|else
block|{
name|suffix
operator|=
name|state
operator|.
name|segmentSuffix
operator|+
literal|"_"
operator|+
name|Lucene41DocValuesConsumer
operator|.
name|DV_SEGMENT_SUFFIX
expr_stmt|;
block|}
name|String
name|cfsFileName
init|=
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|state
operator|.
name|segmentInfo
operator|.
name|name
argument_list|,
name|suffix
argument_list|,
name|IndexFileNames
operator|.
name|COMPOUND_FILE_EXTENSION
argument_list|)
decl_stmt|;
name|this
operator|.
name|cfs
operator|=
operator|new
name|CompoundFileDirectory
argument_list|(
name|state
operator|.
name|directory
argument_list|,
name|cfsFileName
argument_list|,
name|state
operator|.
name|context
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|this
operator|.
name|info
operator|=
name|state
operator|.
name|segmentInfo
expr_stmt|;
name|this
operator|.
name|context
operator|=
name|state
operator|.
name|context
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
name|IOUtils
operator|.
name|close
argument_list|(
name|cfs
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|clone
specifier|public
name|SimpleDVProducer
name|clone
parameter_list|()
block|{
return|return
name|this
return|;
comment|// nocommit ? actually safe since we open new each time from cfs?
block|}
annotation|@
name|Override
DECL|method|getNumeric
specifier|public
name|NumericDocValues
name|getNumeric
parameter_list|(
name|FieldInfo
name|field
parameter_list|)
throws|throws
name|IOException
block|{
comment|// nocommit
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|getBinary
specifier|public
name|BinaryDocValues
name|getBinary
parameter_list|(
name|FieldInfo
name|field
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|DocValues
operator|.
name|isBytes
argument_list|(
name|field
operator|.
name|getDocValuesType
argument_list|()
argument_list|)
operator|||
name|DocValues
operator|.
name|isSortedBytes
argument_list|(
name|field
operator|.
name|getDocValuesType
argument_list|()
argument_list|)
condition|)
block|{
return|return
operator|new
name|Lucene41BinaryDocValues
operator|.
name|Factory
argument_list|(
name|this
operator|.
name|cfs
argument_list|,
name|this
operator|.
name|info
argument_list|,
name|field
argument_list|,
name|context
argument_list|)
operator|.
name|getDirect
argument_list|()
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|getSorted
specifier|public
name|SortedDocValues
name|getSorted
parameter_list|(
name|FieldInfo
name|field
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|DocValues
operator|.
name|isSortedBytes
argument_list|(
name|field
operator|.
name|getDocValuesType
argument_list|()
argument_list|)
condition|)
block|{
return|return
operator|new
name|Lucene41SortedDocValues
operator|.
name|Factory
argument_list|(
name|this
operator|.
name|cfs
argument_list|,
name|this
operator|.
name|info
argument_list|,
name|field
argument_list|,
name|context
argument_list|)
operator|.
name|getDirect
argument_list|()
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
DECL|class|DocValuesFactory
specifier|public
specifier|static
specifier|abstract
class|class
name|DocValuesFactory
parameter_list|<
name|T
parameter_list|>
implements|implements
name|Closeable
block|{
DECL|method|getDirect
specifier|public
specifier|abstract
name|T
name|getDirect
parameter_list|()
throws|throws
name|IOException
function_decl|;
DECL|method|getInMemory
specifier|public
specifier|abstract
name|T
name|getInMemory
parameter_list|()
throws|throws
name|IOException
function_decl|;
block|}
block|}
end_class

end_unit

