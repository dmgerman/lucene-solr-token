begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.queryParser.surround.query
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryParser
operator|.
name|surround
operator|.
name|query
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_class
DECL|class|SrndPrefixQuery
specifier|public
class|class
name|SrndPrefixQuery
extends|extends
name|SimpleTerm
block|{
DECL|method|SrndPrefixQuery
specifier|public
name|SrndPrefixQuery
parameter_list|(
name|String
name|prefix
parameter_list|,
name|boolean
name|quoted
parameter_list|,
name|char
name|truncator
parameter_list|)
block|{
name|super
argument_list|(
name|quoted
argument_list|)
expr_stmt|;
name|this
operator|.
name|prefix
operator|=
name|prefix
expr_stmt|;
name|this
operator|.
name|truncator
operator|=
name|truncator
expr_stmt|;
block|}
DECL|field|prefix
specifier|private
specifier|final
name|String
name|prefix
decl_stmt|;
DECL|method|getPrefix
specifier|public
name|String
name|getPrefix
parameter_list|()
block|{
return|return
name|prefix
return|;
block|}
DECL|field|truncator
specifier|private
specifier|final
name|char
name|truncator
decl_stmt|;
DECL|method|getSuffixOperator
specifier|public
name|char
name|getSuffixOperator
parameter_list|()
block|{
return|return
name|truncator
return|;
block|}
DECL|method|getLucenePrefixTerm
specifier|public
name|Term
name|getLucenePrefixTerm
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
return|return
operator|new
name|Term
argument_list|(
name|fieldName
argument_list|,
name|getPrefix
argument_list|()
argument_list|)
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
name|getPrefix
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|suffixToString
specifier|protected
name|void
name|suffixToString
parameter_list|(
name|StringBuilder
name|r
parameter_list|)
block|{
name|r
operator|.
name|append
argument_list|(
name|getSuffixOperator
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
comment|/* inspired by PrefixQuery.rewrite(): */
name|TermEnum
name|enumerator
init|=
name|reader
operator|.
name|terms
argument_list|(
name|getLucenePrefixTerm
argument_list|(
name|fieldName
argument_list|)
argument_list|)
decl_stmt|;
name|boolean
name|expanded
init|=
literal|false
decl_stmt|;
try|try
block|{
do|do
block|{
name|Term
name|term
init|=
name|enumerator
operator|.
name|term
argument_list|()
decl_stmt|;
if|if
condition|(
operator|(
name|term
operator|!=
literal|null
operator|)
operator|&&
name|term
operator|.
name|text
argument_list|()
operator|.
name|startsWith
argument_list|(
name|getPrefix
argument_list|()
argument_list|)
operator|&&
name|term
operator|.
name|field
argument_list|()
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|mtv
operator|.
name|visitMatchingTerm
argument_list|(
name|term
argument_list|)
expr_stmt|;
name|expanded
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
break|break;
block|}
block|}
do|while
condition|(
name|enumerator
operator|.
name|next
argument_list|()
condition|)
do|;
block|}
finally|finally
block|{
name|enumerator
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|expanded
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"No terms in "
operator|+
name|fieldName
operator|+
literal|" field for: "
operator|+
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

