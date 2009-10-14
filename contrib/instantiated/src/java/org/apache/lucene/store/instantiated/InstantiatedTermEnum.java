begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.store.instantiated
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|instantiated
package|;
end_package

begin_comment
comment|/**  * Copyright 2006 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|Term
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
name|TermEnum
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
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_comment
comment|/**  * A {@link org.apache.lucene.index.TermEnum} navigating an {@link org.apache.lucene.store.instantiated.InstantiatedIndexReader}.  */
end_comment

begin_class
DECL|class|InstantiatedTermEnum
specifier|public
class|class
name|InstantiatedTermEnum
extends|extends
name|TermEnum
block|{
DECL|field|reader
specifier|private
specifier|final
name|InstantiatedIndexReader
name|reader
decl_stmt|;
DECL|method|InstantiatedTermEnum
specifier|public
name|InstantiatedTermEnum
parameter_list|(
name|InstantiatedIndexReader
name|reader
parameter_list|)
block|{
name|this
operator|.
name|nextTermIndex
operator|=
literal|0
expr_stmt|;
name|this
operator|.
name|reader
operator|=
name|reader
expr_stmt|;
block|}
DECL|method|InstantiatedTermEnum
specifier|public
name|InstantiatedTermEnum
parameter_list|(
name|InstantiatedIndexReader
name|reader
parameter_list|,
name|int
name|startPosition
parameter_list|)
block|{
name|this
operator|.
name|reader
operator|=
name|reader
expr_stmt|;
name|this
operator|.
name|nextTermIndex
operator|=
name|startPosition
expr_stmt|;
name|next
argument_list|()
expr_stmt|;
block|}
DECL|field|nextTermIndex
specifier|private
name|int
name|nextTermIndex
decl_stmt|;
DECL|field|term
specifier|private
name|InstantiatedTerm
name|term
decl_stmt|;
comment|/**    * Increments the enumeration to the next element.  True if one exists.    */
DECL|method|next
specifier|public
name|boolean
name|next
parameter_list|()
block|{
if|if
condition|(
name|reader
operator|.
name|getIndex
argument_list|()
operator|.
name|getOrderedTerms
argument_list|()
operator|.
name|length
operator|<=
name|nextTermIndex
condition|)
block|{
return|return
literal|false
return|;
block|}
else|else
block|{
name|term
operator|=
name|reader
operator|.
name|getIndex
argument_list|()
operator|.
name|getOrderedTerms
argument_list|()
index|[
name|nextTermIndex
index|]
expr_stmt|;
name|nextTermIndex
operator|++
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
comment|/**    * Returns the current Term in the enumeration.    */
DECL|method|term
specifier|public
name|Term
name|term
parameter_list|()
block|{
return|return
name|term
operator|==
literal|null
condition|?
literal|null
else|:
name|term
operator|.
name|getTerm
argument_list|()
return|;
block|}
comment|/**    * Returns the docFreq of the current Term in the enumeration.    */
DECL|method|docFreq
specifier|public
name|int
name|docFreq
parameter_list|()
block|{
return|return
name|term
operator|.
name|getAssociatedDocuments
argument_list|()
operator|.
name|length
return|;
block|}
comment|/**    * Closes the enumeration to further activity, freeing resources.    */
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{   }
block|}
end_class

end_unit

