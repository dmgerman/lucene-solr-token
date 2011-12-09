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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|concurrent
operator|.
name|ConcurrentHashMap
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
name|MultiDocValues
operator|.
name|DocValuesIndex
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
name|util
operator|.
name|IOUtils
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
name|ReaderUtil
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
name|ReaderUtil
operator|.
name|Gather
import|;
end_import

begin_comment
comment|/**  * Exposes per-document values, merged from per-document values API of  * sub-segments. This is useful when you're interacting with an {@link IndexReader}  * implementation that consists of sequential sub-readers (eg DirectoryReader  * or {@link MultiReader}).   *   *<p>  *<b>NOTE</b>: for multi readers, you'll get better performance by gathering  * the sub readers using {@link ReaderUtil#gatherSubReaders} and then operate  * per-reader, instead of using this class.  *   * @lucene.experimental  */
end_comment

begin_class
DECL|class|MultiPerDocValues
specifier|public
class|class
name|MultiPerDocValues
extends|extends
name|PerDocValues
block|{
DECL|field|subs
specifier|private
specifier|final
name|PerDocValues
index|[]
name|subs
decl_stmt|;
DECL|field|subSlices
specifier|private
specifier|final
name|ReaderUtil
operator|.
name|Slice
index|[]
name|subSlices
decl_stmt|;
DECL|field|docValues
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|DocValues
argument_list|>
name|docValues
init|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|String
argument_list|,
name|DocValues
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|MultiPerDocValues
specifier|public
name|MultiPerDocValues
parameter_list|(
name|PerDocValues
index|[]
name|subs
parameter_list|,
name|ReaderUtil
operator|.
name|Slice
index|[]
name|subSlices
parameter_list|)
block|{
name|this
operator|.
name|subs
operator|=
name|subs
expr_stmt|;
name|this
operator|.
name|subSlices
operator|=
name|subSlices
expr_stmt|;
block|}
comment|/**    * Returns a single {@link PerDocValues} instance for this reader, merging    * their values on the fly. This method will not return<code>null</code>.    *     *<p>    *<b>NOTE</b>: this is a slow way to access postings. It's better to get the    * sub-readers (using {@link Gather}) and iterate through them yourself.    */
DECL|method|getPerDocs
specifier|public
specifier|static
name|PerDocValues
name|getPerDocs
parameter_list|(
name|IndexReader
name|r
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|IndexReader
index|[]
name|subs
init|=
name|r
operator|.
name|getSequentialSubReaders
argument_list|()
decl_stmt|;
if|if
condition|(
name|subs
operator|==
literal|null
condition|)
block|{
comment|// already an atomic reader
return|return
name|r
operator|.
name|perDocValues
argument_list|()
return|;
block|}
elseif|else
if|if
condition|(
name|subs
operator|.
name|length
operator|==
literal|0
condition|)
block|{
comment|// no fields
return|return
literal|null
return|;
block|}
elseif|else
if|if
condition|(
name|subs
operator|.
name|length
operator|==
literal|1
condition|)
block|{
return|return
name|getPerDocs
argument_list|(
name|subs
index|[
literal|0
index|]
argument_list|)
return|;
block|}
name|PerDocValues
name|perDocValues
init|=
name|r
operator|.
name|retrievePerDoc
argument_list|()
decl_stmt|;
if|if
condition|(
name|perDocValues
operator|==
literal|null
condition|)
block|{
specifier|final
name|List
argument_list|<
name|PerDocValues
argument_list|>
name|producer
init|=
operator|new
name|ArrayList
argument_list|<
name|PerDocValues
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|ReaderUtil
operator|.
name|Slice
argument_list|>
name|slices
init|=
operator|new
name|ArrayList
argument_list|<
name|ReaderUtil
operator|.
name|Slice
argument_list|>
argument_list|()
decl_stmt|;
operator|new
name|ReaderUtil
operator|.
name|Gather
argument_list|(
name|r
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|void
name|add
parameter_list|(
name|int
name|base
parameter_list|,
name|IndexReader
name|r
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|PerDocValues
name|f
init|=
name|r
operator|.
name|perDocValues
argument_list|()
decl_stmt|;
if|if
condition|(
name|f
operator|!=
literal|null
condition|)
block|{
name|producer
operator|.
name|add
argument_list|(
name|f
argument_list|)
expr_stmt|;
name|slices
operator|.
name|add
argument_list|(
operator|new
name|ReaderUtil
operator|.
name|Slice
argument_list|(
name|base
argument_list|,
name|r
operator|.
name|maxDoc
argument_list|()
argument_list|,
name|producer
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
operator|.
name|run
argument_list|()
expr_stmt|;
if|if
condition|(
name|producer
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
literal|null
return|;
block|}
elseif|else
if|if
condition|(
name|producer
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
name|perDocValues
operator|=
name|producer
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|perDocValues
operator|=
operator|new
name|MultiPerDocValues
argument_list|(
name|producer
operator|.
name|toArray
argument_list|(
name|PerDocValues
operator|.
name|EMPTY_ARRAY
argument_list|)
argument_list|,
name|slices
operator|.
name|toArray
argument_list|(
name|ReaderUtil
operator|.
name|Slice
operator|.
name|EMPTY_ARRAY
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|r
operator|.
name|storePerDoc
argument_list|(
name|perDocValues
argument_list|)
expr_stmt|;
block|}
return|return
name|perDocValues
return|;
block|}
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
name|DocValues
name|result
init|=
name|docValues
operator|.
name|get
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
operator|==
literal|null
condition|)
block|{
comment|// Lazy init: first time this field is requested, we
comment|// create& add to docValues:
specifier|final
name|List
argument_list|<
name|MultiDocValues
operator|.
name|DocValuesIndex
argument_list|>
name|docValuesIndex
init|=
operator|new
name|ArrayList
argument_list|<
name|MultiDocValues
operator|.
name|DocValuesIndex
argument_list|>
argument_list|()
decl_stmt|;
name|int
name|docsUpto
init|=
literal|0
decl_stmt|;
name|DocValues
operator|.
name|Type
name|type
init|=
literal|null
decl_stmt|;
comment|// Gather all sub-readers that share this field
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|subs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|DocValues
name|values
init|=
name|subs
index|[
name|i
index|]
operator|.
name|docValues
argument_list|(
name|field
argument_list|)
decl_stmt|;
specifier|final
name|int
name|start
init|=
name|subSlices
index|[
name|i
index|]
operator|.
name|start
decl_stmt|;
specifier|final
name|int
name|length
init|=
name|subSlices
index|[
name|i
index|]
operator|.
name|length
decl_stmt|;
if|if
condition|(
name|values
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|docsUpto
operator|!=
name|start
condition|)
block|{
name|type
operator|=
name|values
operator|.
name|type
argument_list|()
expr_stmt|;
name|docValuesIndex
operator|.
name|add
argument_list|(
operator|new
name|MultiDocValues
operator|.
name|DocValuesIndex
argument_list|(
operator|new
name|MultiDocValues
operator|.
name|EmptyDocValues
argument_list|(
name|start
argument_list|,
name|type
argument_list|)
argument_list|,
name|docsUpto
argument_list|,
name|start
operator|-
name|docsUpto
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|docValuesIndex
operator|.
name|add
argument_list|(
operator|new
name|MultiDocValues
operator|.
name|DocValuesIndex
argument_list|(
name|values
argument_list|,
name|start
argument_list|,
name|length
argument_list|)
argument_list|)
expr_stmt|;
name|docsUpto
operator|=
name|start
operator|+
name|length
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|i
operator|+
literal|1
operator|==
name|subs
operator|.
name|length
operator|&&
operator|!
name|docValuesIndex
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|docValuesIndex
operator|.
name|add
argument_list|(
operator|new
name|MultiDocValues
operator|.
name|DocValuesIndex
argument_list|(
operator|new
name|MultiDocValues
operator|.
name|EmptyDocValues
argument_list|(
name|start
argument_list|,
name|type
argument_list|)
argument_list|,
name|docsUpto
argument_list|,
name|start
operator|-
name|docsUpto
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|docValuesIndex
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
name|result
operator|=
operator|new
name|MultiDocValues
argument_list|(
name|docValuesIndex
operator|.
name|toArray
argument_list|(
name|DocValuesIndex
operator|.
name|EMPTY_ARRAY
argument_list|)
argument_list|)
expr_stmt|;
name|docValues
operator|.
name|put
argument_list|(
name|field
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
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
name|this
operator|.
name|subs
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

