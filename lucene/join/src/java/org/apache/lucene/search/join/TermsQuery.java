begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.search.join
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|join
package|;
end_package

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
name|FilteredTermsEnum
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
name|Terms
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
name|TermsEnum
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
name|MultiTermQuery
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
name|Query
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
name|AttributeSource
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
name|BytesRefHash
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
name|Comparator
import|;
end_import

begin_comment
comment|/**  * A query that has an array of terms from a specific field. This query will match documents have one or more terms in  * the specified field that match with the terms specified in the array.  *  * @lucene.experimental  */
end_comment

begin_class
DECL|class|TermsQuery
class|class
name|TermsQuery
extends|extends
name|MultiTermQuery
block|{
DECL|field|terms
specifier|private
specifier|final
name|BytesRefHash
name|terms
decl_stmt|;
DECL|field|ords
specifier|private
specifier|final
name|int
index|[]
name|ords
decl_stmt|;
DECL|field|fromQuery
specifier|private
specifier|final
name|Query
name|fromQuery
decl_stmt|;
comment|// Used for equals() only
comment|/**    * @param field The field that should contain terms that are specified in the previous parameter    * @param terms The terms that matching documents should have. The terms must be sorted by natural order.    */
DECL|method|TermsQuery
name|TermsQuery
parameter_list|(
name|String
name|field
parameter_list|,
name|Query
name|fromQuery
parameter_list|,
name|BytesRefHash
name|terms
parameter_list|)
block|{
name|super
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|this
operator|.
name|fromQuery
operator|=
name|fromQuery
expr_stmt|;
name|this
operator|.
name|terms
operator|=
name|terms
expr_stmt|;
name|ords
operator|=
name|terms
operator|.
name|sort
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getTermsEnum
specifier|protected
name|TermsEnum
name|getTermsEnum
parameter_list|(
name|Terms
name|terms
parameter_list|,
name|AttributeSource
name|atts
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|this
operator|.
name|terms
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
name|TermsEnum
operator|.
name|EMPTY
return|;
block|}
return|return
operator|new
name|SeekingTermSetTermsEnum
argument_list|(
name|terms
operator|.
name|iterator
argument_list|()
argument_list|,
name|this
operator|.
name|terms
argument_list|,
name|ords
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|(
name|String
name|string
parameter_list|)
block|{
return|return
literal|"TermsQuery{"
operator|+
literal|"field="
operator|+
name|field
operator|+
literal|'}'
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|obj
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
operator|!
name|super
operator|.
name|equals
argument_list|(
name|obj
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|getClass
argument_list|()
operator|!=
name|obj
operator|.
name|getClass
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|TermsQuery
name|other
init|=
operator|(
name|TermsQuery
operator|)
name|obj
decl_stmt|;
if|if
condition|(
operator|!
name|fromQuery
operator|.
name|equals
argument_list|(
name|other
operator|.
name|fromQuery
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
specifier|final
name|int
name|prime
init|=
literal|31
decl_stmt|;
name|int
name|result
init|=
name|super
operator|.
name|hashCode
argument_list|()
decl_stmt|;
name|result
operator|+=
name|prime
operator|*
name|fromQuery
operator|.
name|hashCode
argument_list|()
expr_stmt|;
return|return
name|result
return|;
block|}
DECL|class|SeekingTermSetTermsEnum
specifier|static
class|class
name|SeekingTermSetTermsEnum
extends|extends
name|FilteredTermsEnum
block|{
DECL|field|terms
specifier|private
specifier|final
name|BytesRefHash
name|terms
decl_stmt|;
DECL|field|ords
specifier|private
specifier|final
name|int
index|[]
name|ords
decl_stmt|;
DECL|field|lastElement
specifier|private
specifier|final
name|int
name|lastElement
decl_stmt|;
DECL|field|lastTerm
specifier|private
specifier|final
name|BytesRef
name|lastTerm
decl_stmt|;
DECL|field|spare
specifier|private
specifier|final
name|BytesRef
name|spare
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
DECL|field|comparator
specifier|private
specifier|final
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|comparator
decl_stmt|;
DECL|field|seekTerm
specifier|private
name|BytesRef
name|seekTerm
decl_stmt|;
DECL|field|upto
specifier|private
name|int
name|upto
init|=
literal|0
decl_stmt|;
DECL|method|SeekingTermSetTermsEnum
name|SeekingTermSetTermsEnum
parameter_list|(
name|TermsEnum
name|tenum
parameter_list|,
name|BytesRefHash
name|terms
parameter_list|,
name|int
index|[]
name|ords
parameter_list|)
block|{
name|super
argument_list|(
name|tenum
argument_list|)
expr_stmt|;
name|this
operator|.
name|terms
operator|=
name|terms
expr_stmt|;
name|this
operator|.
name|ords
operator|=
name|ords
expr_stmt|;
name|comparator
operator|=
name|BytesRef
operator|.
name|getUTF8SortedAsUnicodeComparator
argument_list|()
expr_stmt|;
name|lastElement
operator|=
name|terms
operator|.
name|size
argument_list|()
operator|-
literal|1
expr_stmt|;
name|lastTerm
operator|=
name|terms
operator|.
name|get
argument_list|(
name|ords
index|[
name|lastElement
index|]
argument_list|,
operator|new
name|BytesRef
argument_list|()
argument_list|)
expr_stmt|;
name|seekTerm
operator|=
name|terms
operator|.
name|get
argument_list|(
name|ords
index|[
name|upto
index|]
argument_list|,
name|spare
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|nextSeekTerm
specifier|protected
name|BytesRef
name|nextSeekTerm
parameter_list|(
name|BytesRef
name|currentTerm
parameter_list|)
throws|throws
name|IOException
block|{
name|BytesRef
name|temp
init|=
name|seekTerm
decl_stmt|;
name|seekTerm
operator|=
literal|null
expr_stmt|;
return|return
name|temp
return|;
block|}
annotation|@
name|Override
DECL|method|accept
specifier|protected
name|AcceptStatus
name|accept
parameter_list|(
name|BytesRef
name|term
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|comparator
operator|.
name|compare
argument_list|(
name|term
argument_list|,
name|lastTerm
argument_list|)
operator|>
literal|0
condition|)
block|{
return|return
name|AcceptStatus
operator|.
name|END
return|;
block|}
name|BytesRef
name|currentTerm
init|=
name|terms
operator|.
name|get
argument_list|(
name|ords
index|[
name|upto
index|]
argument_list|,
name|spare
argument_list|)
decl_stmt|;
if|if
condition|(
name|comparator
operator|.
name|compare
argument_list|(
name|term
argument_list|,
name|currentTerm
argument_list|)
operator|==
literal|0
condition|)
block|{
if|if
condition|(
name|upto
operator|==
name|lastElement
condition|)
block|{
return|return
name|AcceptStatus
operator|.
name|YES
return|;
block|}
else|else
block|{
name|seekTerm
operator|=
name|terms
operator|.
name|get
argument_list|(
name|ords
index|[
operator|++
name|upto
index|]
argument_list|,
name|spare
argument_list|)
expr_stmt|;
return|return
name|AcceptStatus
operator|.
name|YES_AND_SEEK
return|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|upto
operator|==
name|lastElement
condition|)
block|{
return|return
name|AcceptStatus
operator|.
name|NO
return|;
block|}
else|else
block|{
comment|// Our current term doesn't match the the given term.
name|int
name|cmp
decl_stmt|;
do|do
block|{
comment|// We maybe are behind the given term by more than one step. Keep incrementing till we're the same or higher.
if|if
condition|(
name|upto
operator|==
name|lastElement
condition|)
block|{
return|return
name|AcceptStatus
operator|.
name|NO
return|;
block|}
comment|// typically the terms dict is a superset of query's terms so it's unusual that we have to skip many of
comment|// our terms so we don't do a binary search here
name|seekTerm
operator|=
name|terms
operator|.
name|get
argument_list|(
name|ords
index|[
operator|++
name|upto
index|]
argument_list|,
name|spare
argument_list|)
expr_stmt|;
block|}
do|while
condition|(
operator|(
name|cmp
operator|=
name|comparator
operator|.
name|compare
argument_list|(
name|seekTerm
argument_list|,
name|term
argument_list|)
operator|)
operator|<
literal|0
condition|)
do|;
if|if
condition|(
name|cmp
operator|==
literal|0
condition|)
block|{
if|if
condition|(
name|upto
operator|==
name|lastElement
condition|)
block|{
return|return
name|AcceptStatus
operator|.
name|YES
return|;
block|}
name|seekTerm
operator|=
name|terms
operator|.
name|get
argument_list|(
name|ords
index|[
operator|++
name|upto
index|]
argument_list|,
name|spare
argument_list|)
expr_stmt|;
return|return
name|AcceptStatus
operator|.
name|YES_AND_SEEK
return|;
block|}
else|else
block|{
return|return
name|AcceptStatus
operator|.
name|NO_AND_SEEK
return|;
block|}
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

