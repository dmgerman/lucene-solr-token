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
name|util
operator|.
name|BytesRef
import|;
end_import

begin_comment
comment|/**   * Exposes multi-valued iterator view over a single-valued iterator.  *<p>  * This can be used if you want to have one multi-valued implementation  * that works for single or multi-valued types.  */
end_comment

begin_class
DECL|class|SingletonSortedSetDocValues
specifier|final
class|class
name|SingletonSortedSetDocValues
extends|extends
name|SortedSetDocValues
block|{
DECL|field|in
specifier|private
specifier|final
name|SortedDocValues
name|in
decl_stmt|;
DECL|field|currentOrd
specifier|private
name|long
name|currentOrd
decl_stmt|;
DECL|field|ord
specifier|private
name|long
name|ord
decl_stmt|;
comment|/** Creates a multi-valued view over the provided SortedDocValues */
DECL|method|SingletonSortedSetDocValues
specifier|public
name|SingletonSortedSetDocValues
parameter_list|(
name|SortedDocValues
name|in
parameter_list|)
block|{
if|if
condition|(
name|in
operator|.
name|docID
argument_list|()
operator|!=
operator|-
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"iterator has already been used: docID="
operator|+
name|in
operator|.
name|docID
argument_list|()
argument_list|)
throw|;
block|}
name|this
operator|.
name|in
operator|=
name|in
expr_stmt|;
block|}
comment|/** Return the wrapped {@link SortedDocValues} */
DECL|method|getSortedDocValues
specifier|public
name|SortedDocValues
name|getSortedDocValues
parameter_list|()
block|{
if|if
condition|(
name|in
operator|.
name|docID
argument_list|()
operator|!=
operator|-
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"iterator has already been used: docID="
operator|+
name|in
operator|.
name|docID
argument_list|()
argument_list|)
throw|;
block|}
return|return
name|in
return|;
block|}
annotation|@
name|Override
DECL|method|docID
specifier|public
name|int
name|docID
parameter_list|()
block|{
return|return
name|in
operator|.
name|docID
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|nextOrd
specifier|public
name|long
name|nextOrd
parameter_list|()
block|{
name|long
name|v
init|=
name|currentOrd
decl_stmt|;
name|currentOrd
operator|=
name|NO_MORE_ORDS
expr_stmt|;
return|return
name|v
return|;
block|}
annotation|@
name|Override
DECL|method|nextDoc
specifier|public
name|int
name|nextDoc
parameter_list|()
throws|throws
name|IOException
block|{
name|int
name|docID
init|=
name|in
operator|.
name|nextDoc
argument_list|()
decl_stmt|;
if|if
condition|(
name|docID
operator|!=
name|NO_MORE_DOCS
condition|)
block|{
name|currentOrd
operator|=
name|ord
operator|=
name|in
operator|.
name|ordValue
argument_list|()
expr_stmt|;
block|}
return|return
name|docID
return|;
block|}
annotation|@
name|Override
DECL|method|advance
specifier|public
name|int
name|advance
parameter_list|(
name|int
name|target
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|docID
init|=
name|in
operator|.
name|advance
argument_list|(
name|target
argument_list|)
decl_stmt|;
if|if
condition|(
name|docID
operator|!=
name|NO_MORE_DOCS
condition|)
block|{
name|currentOrd
operator|=
name|ord
operator|=
name|in
operator|.
name|ordValue
argument_list|()
expr_stmt|;
block|}
return|return
name|docID
return|;
block|}
annotation|@
name|Override
DECL|method|lookupOrd
specifier|public
name|BytesRef
name|lookupOrd
parameter_list|(
name|long
name|ord
parameter_list|)
throws|throws
name|IOException
block|{
comment|// cast is ok: single-valued cannot exceed Integer.MAX_VALUE
return|return
name|in
operator|.
name|lookupOrd
argument_list|(
operator|(
name|int
operator|)
name|ord
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getValueCount
specifier|public
name|long
name|getValueCount
parameter_list|()
block|{
return|return
name|in
operator|.
name|getValueCount
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|lookupTerm
specifier|public
name|long
name|lookupTerm
parameter_list|(
name|BytesRef
name|key
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|lookupTerm
argument_list|(
name|key
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|termsEnum
specifier|public
name|TermsEnum
name|termsEnum
parameter_list|()
block|{
return|return
name|in
operator|.
name|termsEnum
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|cost
specifier|public
name|long
name|cost
parameter_list|()
block|{
return|return
name|in
operator|.
name|cost
argument_list|()
return|;
block|}
block|}
end_class

end_unit

