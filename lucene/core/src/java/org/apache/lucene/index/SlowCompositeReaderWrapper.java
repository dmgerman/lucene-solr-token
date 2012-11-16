begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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
name|HashMap
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
name|index
operator|.
name|DirectoryReader
import|;
end_import

begin_comment
comment|// javadoc
end_comment

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
name|MultiReader
import|;
end_import

begin_comment
comment|// javadoc
end_comment

begin_comment
comment|/**  * This class forces a composite reader (eg a {@link  * MultiReader} or {@link DirectoryReader}) to emulate an  * atomic reader.  This requires implementing the postings  * APIs on-the-fly, using the static methods in {@link  * MultiFields}, {@link MultiDocValues}, by stepping through  * the sub-readers to merge fields/terms, appending docs, etc.  *  *<p><b>NOTE</b>: this class almost always results in a  * performance hit.  If this is important to your use case,  * you'll get better performance by gathering the sub readers using  * {@link IndexReader#getContext()} to get the  * atomic leaves and then operate per-AtomicReader,  * instead of using this class.  */
end_comment

begin_class
DECL|class|SlowCompositeReaderWrapper
specifier|public
specifier|final
class|class
name|SlowCompositeReaderWrapper
extends|extends
name|AtomicReader
block|{
DECL|field|in
specifier|private
specifier|final
name|CompositeReader
name|in
decl_stmt|;
DECL|field|normsCache
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|DocValues
argument_list|>
name|normsCache
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|DocValues
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|fields
specifier|private
specifier|final
name|Fields
name|fields
decl_stmt|;
DECL|field|liveDocs
specifier|private
specifier|final
name|Bits
name|liveDocs
decl_stmt|;
comment|/** This method is sugar for getting an {@link AtomicReader} from    * an {@link IndexReader} of any kind. If the reader is already atomic,    * it is returned unchanged, otherwise wrapped by this class.    */
DECL|method|wrap
specifier|public
specifier|static
name|AtomicReader
name|wrap
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|reader
operator|instanceof
name|CompositeReader
condition|)
block|{
return|return
operator|new
name|SlowCompositeReaderWrapper
argument_list|(
operator|(
name|CompositeReader
operator|)
name|reader
argument_list|)
return|;
block|}
else|else
block|{
assert|assert
name|reader
operator|instanceof
name|AtomicReader
assert|;
return|return
operator|(
name|AtomicReader
operator|)
name|reader
return|;
block|}
block|}
comment|/** Sole constructor, wrapping the provided {@link    *  CompositeReader}. */
DECL|method|SlowCompositeReaderWrapper
specifier|public
name|SlowCompositeReaderWrapper
parameter_list|(
name|CompositeReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|()
expr_stmt|;
name|in
operator|=
name|reader
expr_stmt|;
name|fields
operator|=
name|MultiFields
operator|.
name|getFields
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|liveDocs
operator|=
name|MultiFields
operator|.
name|getLiveDocs
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|in
operator|.
name|registerParentReader
argument_list|(
name|this
argument_list|)
expr_stmt|;
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
literal|"SlowCompositeReaderWrapper("
operator|+
name|in
operator|+
literal|")"
return|;
block|}
annotation|@
name|Override
DECL|method|fields
specifier|public
name|Fields
name|fields
parameter_list|()
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|fields
return|;
block|}
annotation|@
name|Override
DECL|method|docValues
specifier|public
name|DocValues
name|docValues
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
return|return
name|MultiDocValues
operator|.
name|getDocValues
argument_list|(
name|in
argument_list|,
name|field
argument_list|)
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
parameter_list|,
name|boolean
name|direct
parameter_list|)
throws|throws
name|IOException
block|{
comment|// nocommit todo
return|return
literal|null
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
parameter_list|,
name|boolean
name|direct
parameter_list|)
throws|throws
name|IOException
block|{
comment|// nocommit todo
return|return
literal|null
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
parameter_list|,
name|boolean
name|direct
parameter_list|)
throws|throws
name|IOException
block|{
comment|// nocommit todo
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|normValues
specifier|public
specifier|synchronized
name|DocValues
name|normValues
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
name|DocValues
name|values
init|=
name|normsCache
operator|.
name|get
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|values
operator|==
literal|null
condition|)
block|{
name|values
operator|=
name|MultiDocValues
operator|.
name|getNormDocValues
argument_list|(
name|in
argument_list|,
name|field
argument_list|)
expr_stmt|;
name|normsCache
operator|.
name|put
argument_list|(
name|field
argument_list|,
name|values
argument_list|)
expr_stmt|;
block|}
return|return
name|values
return|;
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
return|return
name|in
operator|.
name|getTermVectors
argument_list|(
name|docID
argument_list|)
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
comment|// Don't call ensureOpen() here (it could affect performance)
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
comment|// Don't call ensureOpen() here (it could affect performance)
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
name|in
operator|.
name|document
argument_list|(
name|docID
argument_list|,
name|visitor
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getLiveDocs
specifier|public
name|Bits
name|getLiveDocs
parameter_list|()
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|liveDocs
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
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|MultiFields
operator|.
name|getMergedFieldInfos
argument_list|(
name|in
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|hasDeletions
specifier|public
name|boolean
name|hasDeletions
parameter_list|()
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|liveDocs
operator|!=
literal|null
return|;
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
comment|// TODO: as this is a wrapper, should we really close the delegate?
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

