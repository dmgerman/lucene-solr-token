begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.queryparser.surround.query
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryparser
operator|.
name|surround
operator|.
name|query
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|MultiFields
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
name|regex
operator|.
name|Pattern
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Matcher
import|;
end_import

begin_comment
comment|/**  * Query that matches wildcards  */
end_comment

begin_class
DECL|class|SrndTruncQuery
specifier|public
class|class
name|SrndTruncQuery
extends|extends
name|SimpleTerm
block|{
DECL|method|SrndTruncQuery
specifier|public
name|SrndTruncQuery
parameter_list|(
name|String
name|truncated
parameter_list|,
name|char
name|unlimited
parameter_list|,
name|char
name|mask
parameter_list|)
block|{
name|super
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|/* not quoted */
name|this
operator|.
name|truncated
operator|=
name|truncated
expr_stmt|;
name|this
operator|.
name|unlimited
operator|=
name|unlimited
expr_stmt|;
name|this
operator|.
name|mask
operator|=
name|mask
expr_stmt|;
name|truncatedToPrefixAndPattern
argument_list|()
expr_stmt|;
block|}
DECL|field|truncated
specifier|private
specifier|final
name|String
name|truncated
decl_stmt|;
DECL|field|unlimited
specifier|private
specifier|final
name|char
name|unlimited
decl_stmt|;
DECL|field|mask
specifier|private
specifier|final
name|char
name|mask
decl_stmt|;
DECL|field|prefix
specifier|private
name|String
name|prefix
decl_stmt|;
DECL|field|prefixRef
specifier|private
name|BytesRef
name|prefixRef
decl_stmt|;
DECL|field|pattern
specifier|private
name|Pattern
name|pattern
decl_stmt|;
DECL|method|getTruncated
specifier|public
name|String
name|getTruncated
parameter_list|()
block|{
return|return
name|truncated
return|;
block|}
annotation|@
name|Override
DECL|method|toStringUnquoted
specifier|public
name|String
name|toStringUnquoted
parameter_list|()
block|{
return|return
name|getTruncated
argument_list|()
return|;
block|}
DECL|method|matchingChar
specifier|protected
name|boolean
name|matchingChar
parameter_list|(
name|char
name|c
parameter_list|)
block|{
return|return
operator|(
name|c
operator|!=
name|unlimited
operator|)
operator|&&
operator|(
name|c
operator|!=
name|mask
operator|)
return|;
block|}
DECL|method|appendRegExpForChar
specifier|protected
name|void
name|appendRegExpForChar
parameter_list|(
name|char
name|c
parameter_list|,
name|StringBuilder
name|re
parameter_list|)
block|{
if|if
condition|(
name|c
operator|==
name|unlimited
condition|)
name|re
operator|.
name|append
argument_list|(
literal|".*"
argument_list|)
expr_stmt|;
elseif|else
if|if
condition|(
name|c
operator|==
name|mask
condition|)
name|re
operator|.
name|append
argument_list|(
literal|"."
argument_list|)
expr_stmt|;
else|else
name|re
operator|.
name|append
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
DECL|method|truncatedToPrefixAndPattern
specifier|protected
name|void
name|truncatedToPrefixAndPattern
parameter_list|()
block|{
name|int
name|i
init|=
literal|0
decl_stmt|;
while|while
condition|(
operator|(
name|i
operator|<
name|truncated
operator|.
name|length
argument_list|()
operator|)
operator|&&
name|matchingChar
argument_list|(
name|truncated
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
argument_list|)
condition|)
block|{
name|i
operator|++
expr_stmt|;
block|}
name|prefix
operator|=
name|truncated
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|i
argument_list|)
expr_stmt|;
name|prefixRef
operator|=
operator|new
name|BytesRef
argument_list|(
name|prefix
argument_list|)
expr_stmt|;
name|StringBuilder
name|re
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
while|while
condition|(
name|i
operator|<
name|truncated
operator|.
name|length
argument_list|()
condition|)
block|{
name|appendRegExpForChar
argument_list|(
name|truncated
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
argument_list|,
name|re
argument_list|)
expr_stmt|;
name|i
operator|++
expr_stmt|;
block|}
name|pattern
operator|=
name|Pattern
operator|.
name|compile
argument_list|(
name|re
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|visitMatchingTerms
specifier|public
name|void
name|visitMatchingTerms
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|String
name|fieldName
parameter_list|,
name|MatchingTermVisitor
name|mtv
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|prefixLength
init|=
name|prefix
operator|.
name|length
argument_list|()
decl_stmt|;
name|Terms
name|terms
init|=
name|MultiFields
operator|.
name|getTerms
argument_list|(
name|reader
argument_list|,
name|fieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|terms
operator|!=
literal|null
condition|)
block|{
name|Matcher
name|matcher
init|=
name|pattern
operator|.
name|matcher
argument_list|(
literal|""
argument_list|)
decl_stmt|;
try|try
block|{
name|TermsEnum
name|termsEnum
init|=
name|terms
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|TermsEnum
operator|.
name|SeekStatus
name|status
init|=
name|termsEnum
operator|.
name|seekCeil
argument_list|(
name|prefixRef
argument_list|)
decl_stmt|;
name|BytesRef
name|text
decl_stmt|;
if|if
condition|(
name|status
operator|==
name|TermsEnum
operator|.
name|SeekStatus
operator|.
name|FOUND
condition|)
block|{
name|text
operator|=
name|prefixRef
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|status
operator|==
name|TermsEnum
operator|.
name|SeekStatus
operator|.
name|NOT_FOUND
condition|)
block|{
name|text
operator|=
name|termsEnum
operator|.
name|term
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|text
operator|=
literal|null
expr_stmt|;
block|}
while|while
condition|(
name|text
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|text
operator|!=
literal|null
operator|&&
name|StringHelper
operator|.
name|startsWith
argument_list|(
name|text
argument_list|,
name|prefixRef
argument_list|)
condition|)
block|{
name|String
name|textString
init|=
name|text
operator|.
name|utf8ToString
argument_list|()
decl_stmt|;
name|matcher
operator|.
name|reset
argument_list|(
name|textString
operator|.
name|substring
argument_list|(
name|prefixLength
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|matcher
operator|.
name|matches
argument_list|()
condition|)
block|{
name|mtv
operator|.
name|visitMatchingTerm
argument_list|(
operator|new
name|Term
argument_list|(
name|fieldName
argument_list|,
name|textString
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
break|break;
block|}
name|text
operator|=
name|termsEnum
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|matcher
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

