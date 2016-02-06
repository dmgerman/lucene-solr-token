begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
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
name|FieldsProducer
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
name|codecs
operator|.
name|StoredFieldsReader
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
name|TermVectorsReader
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

begin_comment
comment|/** this is a hack to make SortingMP fast! */
end_comment

begin_class
DECL|class|MergeReaderWrapper
class|class
name|MergeReaderWrapper
extends|extends
name|LeafReader
block|{
DECL|field|in
specifier|final
name|SegmentReader
name|in
decl_stmt|;
DECL|field|fields
specifier|final
name|FieldsProducer
name|fields
decl_stmt|;
DECL|field|norms
specifier|final
name|NormsProducer
name|norms
decl_stmt|;
DECL|field|docValues
specifier|final
name|DocValuesProducer
name|docValues
decl_stmt|;
DECL|field|store
specifier|final
name|StoredFieldsReader
name|store
decl_stmt|;
DECL|field|vectors
specifier|final
name|TermVectorsReader
name|vectors
decl_stmt|;
DECL|method|MergeReaderWrapper
name|MergeReaderWrapper
parameter_list|(
name|SegmentReader
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|in
operator|=
name|in
expr_stmt|;
name|FieldsProducer
name|fields
init|=
name|in
operator|.
name|getPostingsReader
argument_list|()
decl_stmt|;
if|if
condition|(
name|fields
operator|!=
literal|null
condition|)
block|{
name|fields
operator|=
name|fields
operator|.
name|getMergeInstance
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|fields
operator|=
name|fields
expr_stmt|;
name|NormsProducer
name|norms
init|=
name|in
operator|.
name|getNormsReader
argument_list|()
decl_stmt|;
if|if
condition|(
name|norms
operator|!=
literal|null
condition|)
block|{
name|norms
operator|=
name|norms
operator|.
name|getMergeInstance
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|norms
operator|=
name|norms
expr_stmt|;
name|DocValuesProducer
name|docValues
init|=
name|in
operator|.
name|getDocValuesReader
argument_list|()
decl_stmt|;
if|if
condition|(
name|docValues
operator|!=
literal|null
condition|)
block|{
name|docValues
operator|=
name|docValues
operator|.
name|getMergeInstance
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|docValues
operator|=
name|docValues
expr_stmt|;
name|StoredFieldsReader
name|store
init|=
name|in
operator|.
name|getFieldsReader
argument_list|()
decl_stmt|;
if|if
condition|(
name|store
operator|!=
literal|null
condition|)
block|{
name|store
operator|=
name|store
operator|.
name|getMergeInstance
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|store
operator|=
name|store
expr_stmt|;
name|TermVectorsReader
name|vectors
init|=
name|in
operator|.
name|getTermVectorsReader
argument_list|()
decl_stmt|;
if|if
condition|(
name|vectors
operator|!=
literal|null
condition|)
block|{
name|vectors
operator|=
name|vectors
operator|.
name|getMergeInstance
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|vectors
operator|=
name|vectors
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|addCoreClosedListener
specifier|public
name|void
name|addCoreClosedListener
parameter_list|(
name|CoreClosedListener
name|listener
parameter_list|)
block|{
name|in
operator|.
name|addCoreClosedListener
argument_list|(
name|listener
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|removeCoreClosedListener
specifier|public
name|void
name|removeCoreClosedListener
parameter_list|(
name|CoreClosedListener
name|listener
parameter_list|)
block|{
name|in
operator|.
name|removeCoreClosedListener
argument_list|(
name|listener
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|fields
specifier|public
name|Fields
name|fields
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|fields
return|;
block|}
annotation|@
name|Override
DECL|method|getNumericDocValues
specifier|public
name|NumericDocValues
name|getNumericDocValues
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|FieldInfo
name|fi
init|=
name|getFieldInfos
argument_list|()
operator|.
name|fieldInfo
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|fi
operator|==
literal|null
condition|)
block|{
comment|// Field does not exist
return|return
literal|null
return|;
block|}
if|if
condition|(
name|fi
operator|.
name|getDocValuesType
argument_list|()
operator|!=
name|DocValuesType
operator|.
name|NUMERIC
condition|)
block|{
comment|// Field was not indexed with doc values
return|return
literal|null
return|;
block|}
return|return
name|docValues
operator|.
name|getNumeric
argument_list|(
name|fi
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getBinaryDocValues
specifier|public
name|BinaryDocValues
name|getBinaryDocValues
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|FieldInfo
name|fi
init|=
name|getFieldInfos
argument_list|()
operator|.
name|fieldInfo
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|fi
operator|==
literal|null
condition|)
block|{
comment|// Field does not exist
return|return
literal|null
return|;
block|}
if|if
condition|(
name|fi
operator|.
name|getDocValuesType
argument_list|()
operator|!=
name|DocValuesType
operator|.
name|BINARY
condition|)
block|{
comment|// Field was not indexed with doc values
return|return
literal|null
return|;
block|}
return|return
name|docValues
operator|.
name|getBinary
argument_list|(
name|fi
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getSortedDocValues
specifier|public
name|SortedDocValues
name|getSortedDocValues
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|FieldInfo
name|fi
init|=
name|getFieldInfos
argument_list|()
operator|.
name|fieldInfo
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|fi
operator|==
literal|null
condition|)
block|{
comment|// Field does not exist
return|return
literal|null
return|;
block|}
if|if
condition|(
name|fi
operator|.
name|getDocValuesType
argument_list|()
operator|!=
name|DocValuesType
operator|.
name|SORTED
condition|)
block|{
comment|// Field was not indexed with doc values
return|return
literal|null
return|;
block|}
return|return
name|docValues
operator|.
name|getSorted
argument_list|(
name|fi
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getSortedNumericDocValues
specifier|public
name|SortedNumericDocValues
name|getSortedNumericDocValues
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|FieldInfo
name|fi
init|=
name|getFieldInfos
argument_list|()
operator|.
name|fieldInfo
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|fi
operator|==
literal|null
condition|)
block|{
comment|// Field does not exist
return|return
literal|null
return|;
block|}
if|if
condition|(
name|fi
operator|.
name|getDocValuesType
argument_list|()
operator|!=
name|DocValuesType
operator|.
name|SORTED_NUMERIC
condition|)
block|{
comment|// Field was not indexed with doc values
return|return
literal|null
return|;
block|}
return|return
name|docValues
operator|.
name|getSortedNumeric
argument_list|(
name|fi
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getSortedSetDocValues
specifier|public
name|SortedSetDocValues
name|getSortedSetDocValues
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|FieldInfo
name|fi
init|=
name|getFieldInfos
argument_list|()
operator|.
name|fieldInfo
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|fi
operator|==
literal|null
condition|)
block|{
comment|// Field does not exist
return|return
literal|null
return|;
block|}
if|if
condition|(
name|fi
operator|.
name|getDocValuesType
argument_list|()
operator|!=
name|DocValuesType
operator|.
name|SORTED_SET
condition|)
block|{
comment|// Field was not indexed with doc values
return|return
literal|null
return|;
block|}
return|return
name|docValues
operator|.
name|getSortedSet
argument_list|(
name|fi
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getDocsWithField
specifier|public
name|Bits
name|getDocsWithField
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|FieldInfo
name|fi
init|=
name|getFieldInfos
argument_list|()
operator|.
name|fieldInfo
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|fi
operator|==
literal|null
condition|)
block|{
comment|// Field does not exist
return|return
literal|null
return|;
block|}
if|if
condition|(
name|fi
operator|.
name|getDocValuesType
argument_list|()
operator|==
name|DocValuesType
operator|.
name|NONE
condition|)
block|{
comment|// Field was not indexed with doc values
return|return
literal|null
return|;
block|}
return|return
name|docValues
operator|.
name|getDocsWithField
argument_list|(
name|fi
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getNormValues
specifier|public
name|NumericDocValues
name|getNormValues
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|FieldInfo
name|fi
init|=
name|getFieldInfos
argument_list|()
operator|.
name|fieldInfo
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|fi
operator|==
literal|null
operator|||
operator|!
name|fi
operator|.
name|hasNorms
argument_list|()
condition|)
block|{
comment|// Field does not exist or does not index norms
return|return
literal|null
return|;
block|}
return|return
name|norms
operator|.
name|getNorms
argument_list|(
name|fi
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getFieldInfos
specifier|public
name|FieldInfos
name|getFieldInfos
parameter_list|()
block|{
return|return
name|in
operator|.
name|getFieldInfos
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getLiveDocs
specifier|public
name|Bits
name|getLiveDocs
parameter_list|()
block|{
return|return
name|in
operator|.
name|getLiveDocs
argument_list|()
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
DECL|method|getTermVectors
specifier|public
name|Fields
name|getTermVectors
parameter_list|(
name|int
name|docID
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|checkBounds
argument_list|(
name|docID
argument_list|)
expr_stmt|;
if|if
condition|(
name|vectors
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|vectors
operator|.
name|get
argument_list|(
name|docID
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getPointValues
specifier|public
name|PointValues
name|getPointValues
parameter_list|()
block|{
return|return
name|in
operator|.
name|getPointValues
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|numDocs
specifier|public
name|int
name|numDocs
parameter_list|()
block|{
return|return
name|in
operator|.
name|numDocs
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|maxDoc
specifier|public
name|int
name|maxDoc
parameter_list|()
block|{
return|return
name|in
operator|.
name|maxDoc
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|document
specifier|public
name|void
name|document
parameter_list|(
name|int
name|docID
parameter_list|,
name|StoredFieldVisitor
name|visitor
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|checkBounds
argument_list|(
name|docID
argument_list|)
expr_stmt|;
name|store
operator|.
name|visitDocument
argument_list|(
name|docID
argument_list|,
name|visitor
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doClose
specifier|protected
name|void
name|doClose
parameter_list|()
throws|throws
name|IOException
block|{
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getCoreCacheKey
specifier|public
name|Object
name|getCoreCacheKey
parameter_list|()
block|{
return|return
name|in
operator|.
name|getCoreCacheKey
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getCombinedCoreAndDeletesKey
specifier|public
name|Object
name|getCombinedCoreAndDeletesKey
parameter_list|()
block|{
return|return
name|in
operator|.
name|getCombinedCoreAndDeletesKey
argument_list|()
return|;
block|}
DECL|method|checkBounds
specifier|private
name|void
name|checkBounds
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
if|if
condition|(
name|docID
operator|<
literal|0
operator|||
name|docID
operator|>=
name|maxDoc
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IndexOutOfBoundsException
argument_list|(
literal|"docID must be>= 0 and< maxDoc="
operator|+
name|maxDoc
argument_list|()
operator|+
literal|" (got docID="
operator|+
name|docID
operator|+
literal|")"
argument_list|)
throw|;
block|}
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
literal|"MergeReaderWrapper("
operator|+
name|in
operator|+
literal|")"
return|;
block|}
block|}
end_class

end_unit

